package tvfan.tv.ui.andr.play.play;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.R;
import tvfan.tv.crack.CrackCompleteListener;
import tvfan.tv.crack.CrackResult;
import tvfan.tv.crack.ParserUtil;
import tvfan.tv.crack.UpdateCrackDialog;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.models.PlayListBean;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.andr.play.baseplay.dateparser.DataParser;
import tvfan.tv.ui.andr.play.baseplay.ui.BasePlayUI;
import tvfan.tv.ui.andr.play.baseplay.utils.DateUtils;
import tvfan.tv.ui.andr.play.baseplay.utils.LogUtils;
import tvfan.tv.ui.andr.play.baseplay.widgets.BaseVedioView;

/**
 * 安卓原生播放器 基础播放器
 * 
 * @author sadshine
 * 
 */
public class Page extends BasePlayUI implements CrackCompleteListener {
	private final static String TAG = "play.Page";
	private static final int HANDLE_CRACK_RESULT = 1000;
	private String videoPath = "";
	// 播放时间
	private int inetspeed = 0;
	private int SPEEDCOUNT = 3;
	// 操作本地数据库的类的对象
	private LocalData mLocalData;
	private HomeKeyEventBroadCastReceiver receiver;
	Handler handleCrackResultHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CRACK_RESULT:
				handlerCrackResult((CrackResult) msg.obj);
				initMenuDialog();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		playType = VOD_PLAY;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		initDate();
		try {
			setPlayerListener(this);
			init();
			DateUtils.startClockAlert(clockaHandler);

		} catch (Exception ex) {
			this.finish();
		}

		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
	     /*DisplayMetrics metrics = new DisplayMetrics();
	     getWindowManager().getDefaultDisplay().getMetrics(metrics);
	     //获取屏幕宽度
	     int width = metrics.widthPixels;//1920
	     //获取屏幕好毒
	     int height = metrics.heightPixels;//1080
	     //获取屏幕密度
	     float density = metrics.density;//1.5
	     //获取屏幕密度Dpi
	     int densityDpi= metrics.densityDpi;//240
	     android.util.Log.e(TAG, "width============"+width);
	     android.util.Log.e(TAG, "height============"+height);
	     android.util.Log.e(TAG, "density============"+density);
	     android.util.Log.e(TAG, "densityDpi============"+densityDpi);*/

	}

	/**
	 * 获取传递过来的数据
	 */
	private void initDate() {
		isFirstEntry = true;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		totalEpisodes = bundle.getInt("totalEpisodes");
		type = bundle.getString("type");
		pagingNum = bundle.getInt("pagingNum");
		cpId = bundle.getString("cpId");
		yearNum = bundle.getInt("yearNum");
		year = bundle.getString("year");
		if(type.equals("电视剧") || type.equals("电影") || type.equals("动漫"))
			totalEpisodes = totalEpisodes - yearNum*20;
		else
			totalEpisodes = totalEpisodes - (pagingNum-1)*20;
		getYearOrList(bundle.getString("yearJSONArray"));
	}

	private void getYearOrList (String yearStr) {
		if(!TextUtils.isEmpty(yearStr) && !"[]".equals(yearStr)) {
			try {
				JSONArray yearJSONArray = new JSONArray(yearStr);
				if(type.equals("电视剧") || type.equals("电影") || type.equals("动漫")) {
					int start = Integer.parseInt(yearJSONArray.optString(0));
					int end = Integer.parseInt(yearJSONArray.optString(1));
					int totalEpisodes = end - start +1;
					pagingList = new ArrayList<String>();
					int right = totalEpisodes % 20 == 0 ? totalEpisodes / 20 : totalEpisodes / 20 + 1;
					for (int i = 0; i < right; i++) {
						if (20 * (i + 1) <= totalEpisodes) {
							pagingList.add(20 * i + start + "-"
									+ (20 * (i + 1)+start-1));
						} else {
							pagingList.add(20 * i + start + "-"
									+ end );
							break;
						}
					}
				}
				else {
					year = yearJSONArray.optString(yearNum);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		hideTimerAction();
		if(surfaceView != null){
			isplaying = surfaceView.isPlaying();
			state = surfaceView.getState();
		}
		return super.dispatchKeyEvent(event);
	}

	/** ---------------初始化------------------- **/
	private void init() {

		iscomplete = false;
		isplaycomplete = false;
		playrecord = new PlayRecordHelpler(this);
		initDataList();
		if (playerbean != null && playerbean.getSourcelist() != null) {
			ibreaktime = getBreakPoint();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					initsurfaceview();
					initPlaylayout();
					initUI();
					ArrayList<PlayListBean> playList = playerbean
							.getSourcelist().get(ipos).getPlaylist();
					int count = playList.size() - 1;
					pu=new ParserUtil(Page.this, Page.this, playList.get(count)
							.getUrl(), 1,0);
				}
			}, 0l);
		}
	}

	@Override
	protected void initDataList() {
		try {
			freePlayTime = DataParser.getFreePlayTime(this);
			extraCurPos = DataParser.getExtraPos(this);
			playerbean = DataParser.getPlayerBean(
					DataParser.DATA_KEY.moviedetail, this);
			if (playerbean == null) {
				Toast.makeText(this, "数据异常，请联系客服!", Toast.LENGTH_LONG).show();
				this.finish();
			}
			if (extraCurPos >= playerbean.getSourcelist().size()) {
				extraCurPos = 0;
			}

			super.definitionlist = new ArrayList<String>();
			for (int i = 0; i < playerbean.getSourcelist().get(ipos)
					.getPlaylist().size(); i++) {
				if (playerbean.getSourcelist().get(ipos).getPlaylist().get(i)
						.getType().equals("流畅")) {
					definitionlist.add   ("标     清");
				} else {
					definitionlist.add(playerbean.getSourcelist().get(ipos)
							.getPlaylist().get(i).getType());
				}
			}
			super.screenlist = new ArrayList<String>();
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					screenlist.add("全屏");
				} else {
					screenlist.add("原始");
				}
			}
			/*
			 * super.sourcelist = new ArrayList<String>(); Lg.i("source", "集数："
			 * + playerbean.getSourcelist().size()); for (int i = 0; i <
			 * playerbean.getSourcelist().size(); i++) { sourcelist.add("第" + (i
			 * + 1) + "集"); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			this.finish();
		}
	}

	private void refreshDistinguish() {
		super.definitionlist = new ArrayList<String>();
		for (int i = 0; i < playerbean.getSourcelist().get(ipos).getPlaylist()
				.size(); i++) {
			if (playerbean.getSourcelist().get(ipos).getPlaylist().get(i)
					.getType().equals("流畅")) {
				definitionlist.add("标     清");
			} else {
				definitionlist.add(playerbean.getSourcelist().get(ipos)
						.getPlaylist().get(i).getType());
			}
		}

		super.refreshDistin();
	}

	// 根据破解结果刷新
	private void refreshDistinguish(CrackResult result) {
		super.definitionlist = new ArrayList<String>();
		ArrayList<PlayListBean> playList = playerbean.getSourcelist().get(ipos)
				.getPlaylist();
		int count = playList.size() - 1;
		PlayListBean playListBean = playList.get(count);
		playerbean.getSourcelist().get(ipos).getPlaylist().clear();
		PlayListBean tmp = null;
		if (!TextUtils.isEmpty(result.standUrl)) {
			definitionlist.add("标     清");
			tmp = new PlayListBean();
			tmp.setType("标     清");
			tmp.setUrl(result.standUrl);
			playerbean.getSourcelist().get(ipos).getPlaylist().add(tmp);
		}
		if (!TextUtils.isEmpty(result.highUrl)) {
			definitionlist.add("高     清");
			tmp = new PlayListBean();
			tmp.setType("高     清");
			tmp.setUrl(result.highUrl);
			playerbean.getSourcelist().get(ipos).getPlaylist().add(tmp);
		}
		if (!TextUtils.isEmpty(result.superUrl)) {
			definitionlist.add("超     清");
			tmp = new PlayListBean();
			tmp.setType("超     清");
			tmp.setUrl(result.superUrl);
			playerbean.getSourcelist().get(ipos).getPlaylist().add(tmp);

		}
		/*if (playerbean.getSourcelist().get(ipos).getPlaylist().size() == 0) {
			showErrorDialog(001, false, 0, 2000);
		}*/
		playerbean.getSourcelist().get(ipos).getPlaylist().add(playListBean);
		//super.refreshDistin();
	}

	/**
	 * 成员变量初始化
	 */
	protected void initUI() {
		super.initDialogView();
		mProgressRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					updateProgressbarTime();

					if (surfaceView.isPlaying()) {
						removeError();
//						loadingDialog.dismiss();
						if (playererrorDialog != null) {
							playererrorDialog.dismiss();
						}
					}

					mHandler.postDelayed(mProgressRunnable, UPDATE_PROGRESS);

				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		};

		mSeekRunnable = new Runnable() {
			@Override
			public void run() {
				surfaceView.seek(seekcurtime);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						startUpdateProgress();
					}
				}, 0l);
			}
		};
	}

	private void initsurfaceview() {
		mHandler = new Handler();
		timerhd = new Handler();
		clockhd = new Handler();
		playhd = new Handler();
		rootlayout = (RelativeLayout) findViewById(R.id.content);
		surfaceView = new BaseVedioView(this, null);
		surfaceView.setSurfaceView(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, 0, 0);
		rootlayout.addView(surfaceView);
	}

	/**
	 * 播放器界面初始化
	 */
	protected void initPlaylayout() {
		super.initPlaylayout();
	}

	@Override
	protected void restartPlay() {
		super.restartPlay();
		surfaceView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				if (surfaceView != null) {
					surfaceView.start();
					if(loadingDialog!=null){
						loadingDialog.dismiss();
					}
				}
			}
		});

	}

	protected void nextPlayer(boolean isnext) {
		super.nextPlayer(isnext);
		// refreshDistinguish();

		if (playerbean != null) {
			state = 0;
			// surfaceView.startPlayer(playerbean.getSourcelist().get(ipos)
			// .getPlaylist().get(0).getUrl());
			ArrayList<PlayListBean> playList = playerbean.getSourcelist()
					.get(ipos).getPlaylist();
			int count = playList.size() - 1;
			pu = new ParserUtil(this, this, playList.get(count).getUrl(), 1,0);
		}

	}

	protected void choosePlayer() {
		super.choosePlayer();
		if (playerbean != null) {
			state = 0;
			ArrayList<PlayListBean> playList = playerbean.getSourcelist()
					.get(ipos).getPlaylist();
			int count = playList.size() - 1;
			pu = new ParserUtil(this, this, playList.get(count).getUrl(), 1,0);
		}

	}

	/**
	 * 在播放的时候改变视频清晰度的方法
	 */
	protected void DefinitChange() {
		super.DefinitChange();
		loadingDialog.show();
		state = 0;
		ArrayList<PlayListBean> playlist = playerbean.getSourcelist().get(ipos)
				.getPlaylist();
		for (PlayListBean playListBean : playlist) {
			if (definitionlist.get(iDefinitIndex).equals("高     清")
					&& playListBean.getType().equals("高     清")) {
				surfaceView.startPlayer(playListBean.getUrl());
				break;
			} else if (definitionlist.get(iDefinitIndex).equals("标     清")
					&& playListBean.getType().equals("标     清")) {
				surfaceView.startPlayer(playListBean.getUrl());
				break;
			} else if (definitionlist.get(iDefinitIndex).equals("流畅")
					&& playListBean.getType().equals("流畅")) {
				surfaceView.startPlayer(playListBean.getUrl());
				break;
			} else if (definitionlist.get(iDefinitIndex).equals("超     清")
					&& playListBean.getType().equals("超     清")) {
				surfaceView.startPlayer(playListBean.getUrl());
				break;
			}
		}
		mHandler.postDelayed(mPlayOverTimeRunnable, OVER_PLAY_TIME);
	}

	protected void RatioChange() {
		super.RatioChange();
		if (iRatioIndex == 1) {
			int ileft = (App.ScreenWidth - surfaceView.getVideoWidth()) / 2;
			int itop = (App.ScreenHeight - surfaceView.getVideoHeight()) / 2;
			surfaceView.setVideoView(surfaceView.getVideoWidth(),
					surfaceView.getVideoHeight(), itop, ileft);
		} else {
			surfaceView.setSurfaceView(App.ScreenWidth, App.ScreenHeight, 0, 0);

		}

	}

	/**
	 * 播放器入口
	 * 
	 * @param url
	 */
	private void initPlayer(String url) {
		try {
			videoPath = url;
			initPlayerListener();
			surfaceView.createPlayer(url);

		} catch (Exception ex) {
		}
	}

	/** ----------按键响应操作-----播放器功能逻辑+界面显示------------------ **/
	boolean isShow = false;
	private boolean firstPlay = true;

	@Override
	protected void pauseResumeAction() {
		isplaying = surfaceView.isPlaying();
		if (isplaying) {
			surfaceView.pause();
			isShow = false;
			stopUpdateProgress();

		} else {
					surfaceView.start();

			isShow = true;
			startUpdateProgress();
		}
		super.pauseResumeAction();
	}

	protected void volumnAction(boolean up) {
		surfaceView.volumn(up);
	}

	/** ---------------播放器界面显示逻辑------------------- **/
	/**
	 * 呼出上下界面区域
	 * 
	 * @param display
	 */
	protected void displayInfoArea(boolean display) {
		super.displayInfoArea(display);
		if (display) {
			startUpdateProgress();
			startClock();
		} else {
			stopClock();
		}
	}

	private void setPlayProgress() {
		try {
			int duration = freePlayTime > 0 ? freePlayTime : surfaceView
					.getTotalTime();
			super.setPlayProgress(duration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateProgressbarTime() {
		int seekBarMax = playlayout.getSeekBarMax();
		float percent = (float) surfaceView.getCurrentTime()
				/ ((float) freePlayTime > 0 ? freePlayTime : surfaceView
						.getTotalTime());
		int progress = (int) Math.ceil(percent * seekBarMax);
		if (progress > seekBarMax) {
			progress = seekBarMax;
		}
		drawCurrentTime();
		drawTotalTime();
		drawSeekTime(progress);
		if (freePlayTime > 0 && progress == seekBarMax) {
			PlayerCompleteListener();
		}

	}

	/** ---------------播放器初始化------------------- **/

	private void initPlayerListener() {
		try {
			surfaceView.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					/*loadingDialog.show();
					loadingDialog.showLodingDialogText();*/
					loadingDialog.dismiss();
					surfaceView.start();
//					mHandler.removeCallbacksAndMessages(mPlayOverTimeRunnable);
					mHandler.removeCallbacks(mPlayOverTimeRunnable);
					drawCurrentTime();
					drawTotalTime();
					startUpdateProgress();
					// displaySeekAction();
					// displayInfoArea(true);
				
					if (ibreaktime > 0) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								surfaceView.seek(ibreaktime);
								// popRecordDialog();11.23
								ibreaktime = 0;
							}
						}, 200L);

					}
					surfaceView.invalidate();
					surfaceView.requestLayout();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {

							isplaycomplete = false;
						}
					}, 1000l);
					hideTimerAction();
				}
			});

			surfaceView
					.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

						@Override
						public void onBufferingUpdate(MediaPlayer mediaplaer,
								int precent) {
							/*if (!surfaceView.isPlaying()
									&& surfaceView.getState() != 2
									) {
								loadingDialog.show();
								loadingDialog.showLodingDialogText();
								Log.d(TAG, "弹出loading");
							}
							if(loadingDialog.isShowing()){
								loadingDialog.dismiss();
								Log.d(TAG, "隐藏loading");
							}*/
							/*Log.d(TAG, "surfaceView.getState()当前状态"+surfaceView.getState());
							Log.d(TAG,"surfaceView.isPlaying()当前状态"+surfaceView.isPlaying());*/
						}
					});

			surfaceView.setOnInfoListener(new OnInfoListener() {

				@Override
				public boolean onInfo(MediaPlayer meiaplayer, int what, int info) {
					/*if(what==702&&surfaceView.getState() != 2){
						loadingDialog.show();
					}*/
//					loadingDialog.dismiss();
//					loadingDialog.show();
					displayLoading(what);
					/*isplaying = surfaceView.isPlaying();
					state = surfaceView.getState();*/
					return true;
				}
			});

			surfaceView.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mediaplayer, int what,
						int extra) {
					Log.d("logger", "Error: " + what + "," + extra);
					switch (what) {
						case MediaPlayer.MEDIA_ERROR_UNKNOWN:
						case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
							PlayerErrorListener(what);
							return true;
						default:
							Log.d("logger", "Error: " + what + "," + extra);
							break;
					}
					return true;
				}
			});

			surfaceView.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Lg.v("complete", "the player has auto completed");
					PlayerCompleteListener();
					loadingDialog.show();
					loadingDialog.showLodingDialogText();
				}
			});

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Lg.v("player:", e.toString());
		} catch (SecurityException e) {

			e.printStackTrace();
			Lg.v("player:", e.toString());
		} catch (IllegalStateException e) {

			e.printStackTrace();
			Lg.v("player:", e.toString());
		}

	}

	@Override
	protected void onStop() {
		Lg.v("onStop1:", "play onStop");
		super.onStop();
		Lg.v("onStop2:", "play onStop");
		DateUtils.closeClockAlert();
		if (freePlayTime == 0) {
			saveRecordPoint();
		}
	}

	@Override
	protected void onDestroy() {
		Lg.v("onDestroy1:", "play onDestroy");
		unregisterReceiver(receiver);
		super.onDestroy();
		Lg.v("onDestroy2:", "play onDestroy");
		if(pu != null)
			pu.stop();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/*
		 * if (view.equals(exitdialogview)) {// 前往详情页 try { Intent intent = new
		 * Intent(this, tvfan.tv.EntryPoint.class); Bundle op = new Bundle();
		 * op.putString("programSeriesId",
		 * exitdialogview.getProlist().get(position).getId());
		 * intent.putExtra(AppGlobalConsts.INTENT_ACTION_NAME,
		 * BasePage.ACTION_NAME.OPEN_DETAIL.name()); intent.putExtras(op);
		 * startActivity(intent); } catch (Exception e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } clear();
		 * 
		 * }
		 */
	}

	/**
	 * 进度条进度变化的回调
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (!fromUser)
			return;
		stopUpdateProgress();
		hideTimerAction();
		setPlayProgress();

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void PlayerCompleteListener() {
		if (isplaycomplete)
			return;
		
		stopUpdateProgress();
		// isplaycomplete = true;
		if (playerbean != null && playerbean.getSourcelist() != null
				&& playerbean.getSourcelist().size() > 0
				&& ipos < totalEpisodes-1
				&& freePlayTime == 0) {
			next(true);
		} else {
			iscomplete = true;
			clear();
		}
	}

	@Override
	public void PlayerErrorListener(int what) {
			//破解失效的情况会走这里
			showErrorDialog(-1004, false, 0, 0);
			ArrayList<PlayListBean> playList = playerbean.getSourcelist().get(ipos)
					.getPlaylist();
			int count = playList.size() - 1;
			LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, playerbean.getId(), playList.get(count).getUrl(), cpId, "vod", "12", Page.this);
			Log.d("logger", "当前返回的logger值为12");
	}

	@Override
	protected void drawCurrentTime() {

		playlayout.setPlayCurrentTime(surfaceView.getCurrentTime());
	}

	@Override
	protected void drawTotalTime() {
		totaltime = freePlayTime > 0 ? freePlayTime : surfaceView
				.getTotalTime();
		playlayout.setPlayTotalTime(totaltime);
	}

	@Override
	protected void drawSeekTime(int progress) {
		playlayout.setSeekTime(surfaceView.getCurrentTime(), progress);
	}

	/**
	 * 这是停止播放的方法
	 */
	@Override
	public void clear() {
		Lg.v("clear1:", "play clear");
		super.clear();

		if (surfaceView != null) {
			if (iscomplete) {
				currentime = 0;
				totaltime = 0;
				ipos = 0;
			} else {
				currentime = surfaceView.getCurrentTime();
				totaltime = surfaceView.getTotalTime();
			}
		}
		extraCurPos = -1;
		freePlayTime = 0;
		stopUpdateProgress();

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (surfaceView != null) {
					LogUtils.exitLogger(playerbean.getParentid(),
							playerbean.getId(), AppGlobalVars.getIns().USER_ID,
							"", Page.this);
					surfaceView.clear();
				}

			}
		}).start();

		Lg.v("clear2:", "play clear");
		this.finish();
		// android.os.Process.killProcess(android.os.Process.myPid());
		Lg.v("clear3:", "play clear");
	}

	/**
	 * 实现IPlayerListener中的的方法，在BasePlayerActivity中进行调用，来判断是开始播放还是暂停
	 */
	@Override
	public void PlayerPause(boolean ispause) {
		if (surfaceView != null) {
			if (ispause) {
				surfaceView.pause();
			} else {
						surfaceView.start();

			}
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();

		try {
			PlayerPause(false);
			// playlayout.getCenterLayout().clearFocus();
			DateUtils.startClockAlert(clockaHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		try {
			PlayerPause(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	UpdateCrackDialog updateCrackDialog;
	private ParserUtil pu;

	@Override
	public void onCrackComplete(final CrackResult result) {
		Message msg = handleCrackResultHandler.obtainMessage();
		msg.what = HANDLE_CRACK_RESULT;
		msg.obj = result;
		handleCrackResultHandler.sendMessage(msg);

	}

	/**
	 * 破解失败时返回原始地址
	 * @param arg0
	 */
	@Override
	public void onCrackFailed(HashMap<String, String> arg0) {
		if (arg0 == null || (TextUtils.isEmpty(arg0.get("standardDef"))
		&& TextUtils.isEmpty(arg0.get("hightDef"))
		&& TextUtils.isEmpty(arg0.get("superDef")))) {
			LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, playerbean.getId(), arg0.get("orignUrl"), cpId, "vod", "11", this);
			Log.d("logger", "当前返回的logger值为11");
			showErrorDialog(-1004, false, 0, 0);
		}
	}

	private void handlerCrackResult(final CrackResult result) {
		ArrayList<PlayListBean> playList = playerbean.getSourcelist()
				.get(ipos).getPlaylist();
		int count = playList.size() - 1;
		if(result.type!=null){
			if (result.type.equals("updatePlugins")) {
				updateCrackDialog = new UpdateCrackDialog(this, R.style.UpdateDialog);
				updateCrackDialog.show();
				updateCrackDialog.setCancelable(false);
			} else if (result.type.equals("complete")) {
				if (updateCrackDialog != null) {
					updateCrackDialog.dismiss();
				}

				pu = new ParserUtil(Page.this, Page.this, playList.get(count).getUrl(),
						1,1);
			} else {
				refreshDistinguish(result);
				String path = getPlayUrl(result);
				if (TextUtils.isEmpty(path)){
					path = result.path;
				}
				if(TextUtils.isEmpty(path)){
//					Toast.makeText(this,"对不起，破解失败",Toast.LENGTH_SHORT).show();
//					LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, playerbean.getId(), playList.get(count).getUrl(), cpId, "vod", "11", this);
					showErrorDialog(-1004, false, 0, 0);
//					Log.d("logger", "当前返回的logger值为11");
//					finish();
				}
				if (firstPlay) {
					firstPlay = false;
					initPlayer(path);
					startClock();
					LogUtils.enterLogger(playerbean.getParentid(),
							playerbean.getId(), AppGlobalVars.getIns().USER_ID, "",
							Page.this);
					hideTimerAction();
				} else {
					surfaceView.startPlayer(path);
				}

				mPlayOverTimeRunnable = new Runnable() {
					@Override
					public void run() {
						ArrayList<PlayListBean> playList = playerbean.getSourcelist().get(ipos)
								.getPlaylist();
						int count = playList.size() - 1;
						showErrorDialog(-1004, false, 0, 0);
						LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, playerbean.getId(), playList.get(count).getUrl(), cpId, "vod", "13", Page.this);
						Log.d("logger", "当前返回的logger值为13");
						}
				};
				mHandler.postDelayed(mPlayOverTimeRunnable ,OVER_PLAY_TIME);
			}
		}
	}

	/**
	 * 获取保存在本地的设置的清晰度对应的播放地址
	 * 
	 * @param result
	 * @return
	 */
	private String getPlayUrl(CrackResult result) {
		try {
			if (mLocalData == null)
				mLocalData = new LocalData(this);
			String dinID = mLocalData
					.getKV(AppGlobalConsts.PERSIST_NAMES.VIDEO_DEFAULT_DEFINITION
							.name());
			String dinName = "";
			if (!TextUtils.isEmpty(dinID)) {
				if (dinID.equals("2")) {
					dinName = "超     清";
					getDum(dinName);
					return result.superUrl;
				} else if (dinID.equals("1")) {
					dinName = "高     清";
					getDum(dinName);
					return result.highUrl;
				} else if (dinID.equals("0")) {
					dinName = "标     清";
					getDum(dinName);
					return result.standUrl;
				}
			}

		} catch (Exception e) {
			return result.path;
		}
		return result.path;
	}

	private void getDum (String dinName) {
		for(int i=0; i<definitionlist.size(); i++) {
			if(dinName.equals(definitionlist.get(i)))
				dinNum = i;
		}
	}
	/**
	 * 监听Home键事件的广播接收器
	 * 进行退出
	 * @author ddd
	 *
	 */
	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						// home key处理点
						clear();
						App.removeAllPages();
						System.exit(0);
						android.os.Process.killProcess(android.os.Process.myPid());
					} 
				}
			}
		}
	}
}
