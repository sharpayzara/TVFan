package tvfan.tv.ui.andr.play.baseplay.ui;

import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar.OnSeekBarChangeListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.dal.models.PlayListBean;
import tvfan.tv.dal.models.PlayerBean;
import tvfan.tv.dal.models.SourcesBean;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.andr.play.baseplay.dateparser.DataParser;
import tvfan.tv.ui.andr.play.baseplay.interfaces.IPlayerListener;
import tvfan.tv.ui.andr.play.baseplay.utils.ConnectionChangeReceiver;
import tvfan.tv.ui.andr.play.baseplay.widgets.BaseVedioView;
import tvfan.tv.ui.andr.play.baseplay.widgets.ExitDialogView;
import tvfan.tv.ui.andr.play.baseplay.widgets.PlayControlLayout;
import tvfan.tv.ui.andr.play.baseplay.widgets.PlayMenuDialogView;
import tvfan.tv.ui.andr.widgets.BufferLoadingDialog;
import tvfan.tv.ui.andr.widgets.LoadingDialog;

/**
 * 点播父类播放器UI
 * 
 * @author sadshine
 */
public abstract class BasePlayUI extends BasePlayerActivity implements
		OnClickListener, OnItemClickListener, OnSeekBarChangeListener,
		IPlayerListener {
	// 播放控件
	protected BaseVedioView surfaceView;
	protected ExitDialogView exitDialogView;
	// 布局控件
	protected RelativeLayout rootlayout;
	protected PlayControlLayout playlayout;
	protected PlayMenuDialogView playMenuDialogView;
	protected LoadingDialog loadingDialog;
	protected BufferLoadingDialog bufferLoadingDialog;
//	protected RecDialogView recdialogview;11.23
	// 数据对象
	protected PlayerBean playerbean = null;
	protected PlayerBean tempPlayBean =  null;
	protected ArrayList<String> definitionlist = null;
	protected ArrayList<String> screenlist = null;
	protected ArrayList<ArrayList<String>> sourcelist = null;
	protected ArrayList<String> pagingList;//电视剧、动漫的分页列表
	protected int totalEpisodes;
	// 播放时间
	protected int totaltime = 0;
	protected int seekcurtime = 0;
	protected int currentime = 0;
	protected int UPDATE_PROGRESS = 1000;
	protected final int PAGESIZE = 20;
	protected final int OVER_PLAY_TIME = 2 * 60 * 1000;
	
	protected Handler mHandler;
	protected Runnable mSeekRunnable = null;
	protected Runnable mProgressRunnable = null;
	protected Runnable mPlayOverTimeRunnable = null;//播放超时
	/**
	 * 当前集数
	 */
	protected int ipos = 0;
	protected Handler timerhd, clockhd, playhd, bufferhd;
	protected int state = 0;// 播放器状态
	protected boolean isplaying = false;// 播放状态
	protected PlayRecordHelpler playrecord;// 播放记录
	protected int ibreaktime = 0;// 断点时间
	protected boolean iscomplete = false;// 是否播放完成
	protected boolean isplaycomplete = false;// 判断是否已触发播放完成函数方法 避免二次触发
	protected int iRatioIndex = 0;// 当前筛选分辨率索引
	protected int iDefinitIndex = -1;// 当前筛选适配比例索引
	protected int freePlayTime = 0;// 免费观看时长
	protected int extraCurPos = -1;// 外界播放集数参值
	protected int lack;
	protected String type;
	protected boolean isFirstEntry = false;
	protected int pagingNum = 0;// 当前播放的节目属于第几页
	protected String cpId;
	protected int yearNum;//有年份的对应的年份在相应集合的位置,,,,电影、电视剧、动漫表示当前的第几个分页
	protected String year;//有年份的当前播放的是哪个年份的剧集
	protected RemoteData rd;
	/**
	 * 初始化筛选器数据
	 */
	abstract protected void initDataList();

	protected int dinNum = 0;//设置中选则的清晰度的编号
	/**
	 * 初始化对话框
	 */
	protected void initDialogView() {
		loadingDialog = new LoadingDialog(this,R.style.Dialog);
		loadingDialog.getWindow().setLayout(
				getWindowManager().getDefaultDisplay().getWidth(),
				getWindowManager().getDefaultDisplay().getHeight());
		loadingDialog.show();

		loadingDialog.showLodingDialogText();

		bufferLoadingDialog = new BufferLoadingDialog(this,R.style.dialog,exitDialogView);
		bufferLoadingDialog.getWindow().setLayout(
				getWindowManager().getDefaultDisplay().getWidth(),
				getWindowManager().getDefaultDisplay().getHeight());
		bufferLoadingDialog.setOnBackKeyListener(new BufferLoadingDialog.OnBackKeyListener() {
			@Override
			public void onBackKey() {
				saveRecordPoint();
			}
		});

	}

	protected void initMenuDialog(){
		playMenuDialogView = new PlayMenuDialogView(this, R.style.livePlaySettingDialog);
		if (playerbean != null) {
			playMenuDialogView.setProgramSeriesId(playerbean.getId());
		}
		playMenuDialogView.addDistinList(definitionlist, dinNum, this);
		playMenuDialogView.addRatioList(screenlist, this);
		playMenuDialogView.setOnDinChangeListener(new PlayMenuDialogView.OnDinChangeListener() {
			@Override
			public void onDinChange(int dinNum) {
				iDefinitIndex = dinNum;
				ibreaktime = surfaceView.getCurrentTime();
				stopUpdateProgress();
				DefinitChange();
			}
		});
		playMenuDialogView.setOnRatioChangeListener(new PlayMenuDialogView.OnRatioChangeListener() {
			@Override
			public void onRatioChange(int ratioNum) {
				iRatioIndex = ratioNum;
				RatioChange();
			}
		});
		if(!isFinishing())
			displayFilterAction(true);
		playMenuDialogView.dismiss();
	}


	/**
	 * 播放器界面初始化
	 */
	protected void initPlaylayout() {
		playlayout = new PlayControlLayout(this, rootlayout);
		playlayout.addPlayTopLayout();

		playlayout.addPlaySuspendLayout();
		playlayout.displaySuspendlayout(View.INVISIBLE);


		playlayout.addPlaySeekLayout();
		postDelayDisplaySeek();
		playlayout.setSeekOnlistener(this);
		playlayout.setPauseImgListener(this);
		//初始化exitdialogview
		exitDialogView = new ExitDialogView(this, R.style.PlayDialogStyle,
				playerbean.getId(), playerbean.getName());
		exitDialogView.setOnItemClick(this);
		if (playerbean != null) {
			playlayout.setTitle(playerbean.getName());
		}
		if (playerbean != null && playerbean.getSourcelist() != null
				&& playerbean.getSourcelist().size() < 2) {
			exitDialogView.setDisplayLastAndForwardBtn(View.INVISIBLE);
			
		} else {
			exitDialogView.setLastBtnOnclick(this);
			exitDialogView.setNextBtnOnclick(this);
			
		}
		exitDialogView.setExitBtnOnclick(this);
		displayNextPlayer();
	}

	protected void next(boolean isNext) {
		if (isNext) {
			ipos++;
			if (playerbean != null) {
				if(ipos == playerbean.getSourcelist().size()
						&& playerbean.getSourcelist().size() < totalEpisodes) {
					if(type.equals("电视剧") || type.equals("电影") || type.equals("动漫")) {
						yearNum ++;
						String pagingStr = pagingList.get(yearNum);
						String start = pagingStr.substring(0, pagingStr.indexOf("-"));
						String end = pagingStr.substring(pagingStr.indexOf("-") + 1, pagingStr.length());
						getMoreEpisodeData(start, end, "asc");
					}
					else {
						pagingNum ++;
						getMoreEpisodeData(pagingNum+"", "", "desc");
					}
				}
				else {
					nextPlayer(true);
				}
			}
		} else {
			ipos--;

			if (ipos < 0) {
				ipos = 0;
			}
			nextPlayer(false);
		}
	}

	protected void getMoreEpisodeData (String start, String end, String sortType) {
		//String programSeriesId, String cpId, String sortType, String start, String end, String type, String year,
		String programSeriesId = playerbean.getId();
		if(rd == null) {
			rd = new RemoteData();
		}

		rd.getMovieEpisodeData(programSeriesId, cpId, sortType, start, end, type, year, new HttpResponse.Listener4JSONObject() {
			@Override
			public void onResponse(final JSONObject response) {
				if(response!=null && !"{}".equals(response.toString())) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							initMoreEpisodeData(response);
							nextPlayer(true);
						}
					});

				}
				else {

				}
			}

			@Override
			public void onError(String httpStatusCode) {

			}
		});
	}

	protected void initMoreEpisodeData(JSONObject response) {
		JSONArray epArray = response.optJSONArray("sources");
		if(epArray!=null && !"[]".equals(epArray.toString())) {
			int count = epArray.length();
			for (int i = 0; i < count; i++) {
				SourcesBean sourcebean = new SourcesBean();
				JSONObject obj = (JSONObject) epArray.opt(i);
				sourcebean.setId(obj.optString("id"));
				sourcebean.setVideoid(obj.optString("videoid"));
				sourcebean.setVolumncount(obj.optString("volumncount"));
				JSONArray subjsonarr = obj.optJSONArray("playlist");
				ArrayList<PlayListBean> playlist = new ArrayList<PlayListBean>();
				for (int j = 0; j < subjsonarr.length(); j++) {
					PlayListBean playlistbean = new PlayListBean();
					JSONObject subjsonobj = (JSONObject) subjsonarr.opt(j);
					playlistbean.setType(subjsonobj.optString("type"));
					playlistbean.setUrl(subjsonobj.optString("playurl"));
					playlist.add(playlistbean);
				}
				sourcebean.setPlaylist(playlist);
				playerbean.getSourcelist().add(sourcebean);
			}
		}
	}

	/**
	 * 判断是执行播放下一集还是上一集的方法
	 * 
	 * @param isnext
	 *            true,下一集;false,上一集
	 */
	protected void nextPlayer(boolean isnext) {
		surfaceView.stop();
		isplaycomplete = true;
		playlayout.displaySuspendlayout(View.INVISIBLE);
		exitDialogView.setDisplayLastAndForwardBtn(View.VISIBLE);
		/*if (isnext) {
			ipos++;
			if (playerbean != null && ipos >= playerbean.getSourcelist().size()) {
				ipos = playerbean.getSourcelist().size() - 1;
			}

		} else {
			ipos--;
			if (ipos < 0) {
				ipos = 0;
			}
		}*/
		displayNextPlayer();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				playlayout.centerArea_state = false;
				exitDialogView.dismiss();
				if (playMenuDialogView != null)
					playMenuDialogView.hide();
			}
		}, 50l);
	}

	protected void choosePlayer() {
		isplaycomplete = true;
		displayNextPlayer();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (playMenuDialogView != null) {
					playMenuDialogView.hide();
				}
				exitDialogView.dismiss();
			}
		}, 2000l);

	}

	/**
	 * 这是控制点播界面中间布局和顶部布局显示数据的方法
	 */
	protected void displayNextPlayer() {
		if (playlayout == null)
			return;
		exitDialogView.setChangeBtnPosition(1);
		if (ipos == 0) {
			exitDialogView.setDisplayLastBtn(View.INVISIBLE);
			exitDialogView.setChangeBtnPosition(0);
		} else if (playerbean != null
				&& ipos >= totalEpisodes-1 && ipos > 0) {
			exitDialogView.setDisplayForwardBtn(View.INVISIBLE);
			exitDialogView.setChangeBtnPosition(1);
		} else {
			exitDialogView.setDisplayLastAndForwardBtn(View.VISIBLE);
		}
			if(playerbean != null && playerbean.getSourcelist() != null
					&& playerbean.getSourcelist().size() > 1 && !type.endsWith("电影")) {
				playlayout.setTitle(playerbean.getName() + " " + playerbean.getSourcelist().get(ipos).getVolumncount());
			}else {
				playlayout.setTitle(playerbean.getName());
			}

		exitDialogView.getExitbtn().requestFocus();
	}

	/** ---------------播放器界面显示隐藏------------------- **/
	protected void displayLoading(int what) {
		if (state == 2) {
			return;
		}
		/*if (bufferhd == null) {
			bufferhd = new Handler();
		}*/
	if(!exitDialogView.isShowing()){
	if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
			/*bufferhd.removeCallbacks(bufferunnable);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
				}
			}, 1000l);*/
		if(playMenuDialogView!=null&&playererrorDialog!=null&&bufferLoadingDialog!=null){
			if(!loadingDialog.isShowing()&&!playMenuDialogView.isShowing()){
				bufferLoadingDialog.show();
			}
		}
		Log.d("play.Page", "缓冲开始了......");
	} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
//			bufferhd.postDelayed(bufferunnable, 3000l);
		if(bufferLoadingDialog!=null){
			bufferLoadingDialog.dismiss();
		}
		Log.d("play.Page", "缓冲结束了......");
	}
}
		/*else if (isplaying) {
//			bufferhd.postDelayed(bufferunnable, 3000l);
			if (playererrDialog != null) {
				playererrDialog.hide();
			}
		} else {
//			bufferhd.removeCallbacks(bufferunnable);
		}*/
	}

	/*private Runnable bufferunnable = new Runnable() {

		@Override
		public void run() {
			loadingDialog.dismiss();
		}
	};*/

	protected void displayInfoArea(boolean display) {
		if (playlayout == null)
			return;
		playlayout.displayTopArea(display);
		playlayout.displayBottomArea(display);
		
	}

	protected void displaySeekArea(boolean display) {
		if (playlayout == null)
			return;
		playlayout.displayBottomArea(display);

	}


	protected void showhideUI(boolean display) {
		if (playlayout == null)
			return;
		if (display) {
			playlayout.centerArea_state = true;
			playlayout.displaySeekGroup(View.VISIBLE);
			playlayout.displayTopGroup(View.VISIBLE);
		} else {
			playlayout.centerArea_state = false;
			playlayout.displaySeekGroup(View.INVISIBLE);
			playlayout.displayTopGroup(View.INVISIBLE);
		}

	}
	
	

	/** -----------onkeydown----------------- **/
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		hideTimerAction();
		return super.dispatchKeyEvent(event);
	}
	
	private boolean menu = false;//记录播放时是否有menu按键活动
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Lg.v("player keydown:", "keycode:" + keyCode);
		if (this.isFinishing() || event == null) {
			// isFinishing():If the activity is finishing, returns true; else
			// returns false.
			return true;
		}
		hideTimerAction();
		switch (keyCode) {

		case 178:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (state == 2) {
				return true;
			}
			
			break;
		case KeyEvent.KEYCODE_MENU:
			if(playlayout.getSuspendLayoutState()==false){

				if(playMenuDialogView == null)
					if(!bufferLoadingDialog.isShowing()){
						playMenuDialogView.show();
					}
				int idefin = iDefinitIndex;
				if(idefin == -1)
					idefin = dinNum;
				displayFilterAction(true);
				if (timerhd != null) {
					timerhd.removeCallbacks(timerrb);
				}
			}
			return true;
		case 122:
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			
			// 当中间布局显示的时候,确认键执行布局中的按钮点击事件
			if (playlayout != null&&playlayout.centerArea_state == true) {
				if (playlayout.getCenterLayout().findFocus() != null) {
					playlayout.getCenterLayout().findFocus()
					.setOnClickListener(this);
				}
			} else if(loadingDialog != null && loadingDialog.isShowing()==false){
				// 当中间布局隐藏的时候,确认键执行
				pauseResumeAction();
			}
			break;
		// case KeyEvent.KEYCODE_VOLUME_UP:
		// volumnAction(true);
		//
		// break;
		// case KeyEvent.KEYCODE_VOLUME_DOWN:
		// volumnAction(false);
		// break;
		
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (playlayout != null&&playlayout.centerArea_state == false &&loadingDialog.isShowing()==false) {
				seekAction();
			}
			
			break;
		case KeyEvent.KEYCODE_BACK:
			// menu界面状态
			if(playlayout != null){
				if (playMenuDialogView != null && PlayMenuDialogView.FILTER_STATE == true) {
					displayFilterAction(false);
				} else if (playlayout.isBottomDisplay()==View.VISIBLE) {
					playlayout.displayInfo(false);
				} else if (playlayout.suspendlayout_state==true) {
					playlayout.displayInfo(false);
					playlayout.displaySuspendlayout(View.INVISIBLE);
					pauseResumeAction();
				}else {
					//点击返回键时,弹出exitviewdialog
					if(exitDialogView!=null){
						exitDialogView.show();
						/*if (freePlayTime == 0) {
							saveRecordPoint();
						}*/
					}
				}
			}
			saveRecordPoint();
			return true;
		}

		// 继续执行父类的其他按键事件
		return super.onKeyDown(keyCode, event);
	}
	
	boolean isShow = false;

	/**
	 * 暂停/继续播放的动作
	 */
	protected void pauseResumeAction() {
		if (playlayout != null) {
			// 如果正在播放.这时要进行暂停
			if (isplaying) {
				if(!(playlayout.toplayout_state&&playlayout.bottomlayout_state)){
					// 弹出上下布局
					displayInfoArea(true);
				}
				playlayout.displaySuspendlayout(View.VISIBLE);
				// 改变暂停按钮的图片
				playlayout.getPause().setBackgroundResource(R.drawable.lb_play);
				// 停止更新进度
				stopUpdateProgress();
				// 延时隐藏上下布局
				hideTimerAction();
			} else if(!isplaying) {
				// 如果本身就是暂停状态,这是要进行播放
				// 开始跟新进度
				startUpdateProgress();
				if(!(playlayout.toplayout_state&&playlayout.bottomlayout_state)){
					// 显示上下布局
					displayInfoArea(true);
				}
				playlayout.displaySuspendlayout(View.INVISIBLE);

				// 改变暂停按钮的图片
				playlayout.getPause().setBackgroundResource(R.drawable.pause);
			}
		}

	}

	/**
	 * 显示上下布局并且隔段时间上下布局自动隐藏
	 */
	protected void displaySeekAction() {
		displayInfoArea(true);
		hideTimerAction();
	}

	protected void displayTopAndBottomGroup(int display) {
		if (playlayout == null)
			return;
		playlayout.displayTopGroup(display);
		playlayout.displaySeekGroup(display);
	}


	protected void seekAction() {

		displayInfoArea(true);

	}

	protected void displayFilterAction(boolean display) {
		if (display) {
			if(playMenuDialogView != null)
				playMenuDialogView.showDialog();
			PlayMenuDialogView.FILTER_STATE = true;
		} else {
			if(playMenuDialogView != null)
				playMenuDialogView.hide();
			PlayMenuDialogView.FILTER_STATE = false;
		}
	}
	
	//延时1.5秒显示底部布局
	private void postDelayDisplaySeek() {
		playlayout.displaySeekGroup(View.INVISIBLE);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				playlayout.displaySeekGroup(View.VISIBLE);
				
			}
		}, 2500);
	}

	/**
	 * 执行音量改变的操作
	 */
	abstract protected void volumnAction(boolean up);

	protected void hideTimerAction() {
		if (timerhd != null) {
			timerhd.removeCallbacks(timerrb);
			timerhd.postDelayed(timerrb, 3000l);
		}
	}

	protected void startClock() {
		if (clockhd != null) {
			clockhd.removeCallbacks(clockrb);
			clockhd.postDelayed(clockrb, 1000l);
		}

	}

	protected void stopClock() {
		if (clockhd != null) {
			clockhd.removeCallbacks(clockrb);
		}
	}

	protected Runnable clockrb = new Runnable() {

		@Override
		public void run() {
			playlayout.getData();
			clockhd.postDelayed(clockrb, 1000l);
		}
	};

	protected Runnable timerrb = new Runnable() {

		@Override
		public void run() {
			// displayFilterAction(false);
			displayTopAndBottomGroup(View.INVISIBLE);
		}
	};

	/** ------------播放器时间、进度条等渲染操作----------- **/

	protected abstract void drawCurrentTime();

	protected abstract void drawTotalTime();

	protected abstract void drawSeekTime(int progress);

	//设置断点播放对话框
	/*protected void popRecordDialog() {
		recdialogview.setBreakTime(ibreaktime);
		recdialogview.recDialogShow();
	}*/
	
	/**
	 * 设置进度条进度相关
	 * @param totaltime
	 */
	protected void setPlayProgress(int totaltime) {
		if (playlayout != null) {
			try {
				int seekBarMax = playlayout.getSeekBarMax();
				int progress = playlayout.getProgress();
				int duration = totaltime;
				if (duration != 0) {
					float currentPositionF = (float) progress
							* (float) duration / seekBarMax;
					int currentPosition = (int) Math.floor(currentPositionF);
					currentPosition = currentPosition <= totaltime ? currentPosition
							: totaltime;
					seekcurtime = currentPosition;
					playlayout.setSeekTime(currentPosition);
					if (progress >= seekBarMax) {
						Lg.v("complete", "the player has seek completed");
						PlayerCompleteListener();
						playlayout.setSeekTime(totaltime, progress);

					} else {
						seekto(600);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	protected void seekto(long delay) {
		mHandler.removeCallbacks(mProgressRunnable);
		mHandler.removeCallbacks(mSeekRunnable);
		mHandler.postDelayed(mSeekRunnable, delay);
	}

	protected void updateProgressbarTime(int currentime, int totaltime) {
		if (playlayout != null) {
			int seekBarMax = playlayout.getSeekBarMax();
			float percent = (float) currentime / (float) totaltime;
			int progress = (int) Math.ceil(percent * seekBarMax);
			if (progress > seekBarMax) {
				progress = seekBarMax;
			}
			drawCurrentTime();
			drawTotalTime();
			drawSeekTime(progress);
		}

	}

	protected void startUpdateProgress() {
		// add by wanqi,解决友盟上报的nullpointer问题
		if (mHandler != null) {
			mHandler.removeCallbacks(mProgressRunnable);
			mHandler.postDelayed(mProgressRunnable, 0);
		}
	}

	protected void stopUpdateProgress() {
		// add by wanqi,解决友盟上报的nullpointer问题
		if (freePlayTime == 0 && mHandler != null) {
			mHandler.removeCallbacks(mProgressRunnable);
		}
	}

	/**
	 *退出播放器进行资源释放
	 */
	protected void clear() {
		surfaceView.clear();
		playlayout.clearAllImages();
		exitDialogView.removeAllHandler();
		rootlayout.removeAllViews();
		clearHandler(clockaHandler);
		clearHandler(mHandler);
		clearHandler(bufferhd);
		clearHandler(clockhd);
		clearHandler(playhd);
		clearHandler(timerhd);
	}

	public void removehandler() {
		try {
			playlayout.clearAllImages();
			exitDialogView.removeAllHandler();
			stopUpdateProgress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 断点记录
	 */
	public void saveRecordPoint() {
		if (playerbean == null || playrecord == null)
			return;
		MPlayRecordInfo minfo = new MPlayRecordInfo();
		minfo.setEpgId(playerbean.getId());
		minfo.setDetailsId(playerbean.getSourcelist().get(ipos).getId());
		minfo.setType(playerbean.getType());
		minfo.setPlayerName(playerbean.getName());
		if (totaltime > 0) {
			minfo.setTotalTime(totaltime);
		}
		minfo.setPonitime(currentime);
		if (playerbean.getSourcelist().size() > 1) {
			minfo.setPlayerpos((ipos+lack)%20);
		} else {
			minfo.setPlayerpos(0);
		}

		minfo.setPicUrl(playerbean.getPicurl());
		minfo.setDateTime(System.currentTimeMillis());
		minfo.setIsSingle(-1);// 在全屏中不更新单集多集状态
		minfo.setActionUrl("");
		minfo.setCpId(cpId);
		minfo.setPageNum(pagingNum);
		minfo.setYearNum(yearNum);
		minfo.setHistoryInfo(playerbean.getSourcelist().get(ipos).getVolumncount());
		playrecord.savePlayRecord(minfo);
	}

	protected Runnable clockalertrb = new Runnable() {

		@Override
		public void run() {
			displayInfoArea(true);
			hideTimerAction();
		}
	};

	protected Handler clockaHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			this.post(clockalertrb);
		}
	};

	/**
	 * 节目播放完成
	 */
	protected void saveCompleteRecPoint() {
		if (playerbean == null || playrecord == null)
			return;
		playrecord.updPlayComplete(1, playerbean.getId(),
				System.currentTimeMillis());
	}

	/**
	 * 获取本地数据
	 * 
	 * @return
	 */
	protected int getBreakPoint() {
		int playPoint = 0;
		if (playerbean == null || playrecord == null)
			return 0;
		MPlayRecordInfo minfo = new MPlayRecordInfo();
		minfo = playrecord.getPlayRcInfo(playerbean.getId());
		if (minfo != null) {

			try {
				playPoint = minfo.getPonitime();
				ipos = (minfo.getPlayerpos() < 0 ? 0 : minfo.getPlayerpos())%20;
				int total = minfo.getTotalTime();
				if (playPoint == total || freePlayTime > 0) {
					playPoint = 0;
				}
				Lg.v("break:", "extraCurPos:" + extraCurPos + " pos:" + ipos);
				if (extraCurPos != ipos && extraCurPos >= 0) {
					playPoint = 0;
					ipos = extraCurPos;
				}
			} catch (Exception e) {
				playPoint = 0;
			}
		} else {
			if (freePlayTime > 0) {
				playPoint = 0;
			}
			if (extraCurPos != ipos && extraCurPos >= 0) {
				playPoint = 0;
				ipos = extraCurPos;
			}
		}
		return playPoint;
	}

	/**
	 * 切换源的时候获取断点播放的时间
	 * @return
	 */
	protected int getBreakTiame(){
		int playPoint = 0;
		if (playerbean == null || playrecord == null)
			return 0;
		MPlayRecordInfo minfo = new MPlayRecordInfo();
		minfo = playrecord.getPlayRcInfo(playerbean.getId());
		if (minfo != null) {

			try {
				playPoint = minfo.getPonitime();
				int total = minfo.getTotalTime();
				if (playPoint == total) {
					playPoint = 0;
				}
			} catch (Exception e) {
				playPoint = 0;
			}
		} 
		
		return playPoint;
	}
	public void refreshDistin() {
		/*if(bufferLoadingDialog.isShowing()){
			initMenuDialog();
		}*/
		playMenuDialogView.show();
		displayFilterAction(false);
	}
 
	protected void restartPlay() {
	}

	/**
	 * 切换视频分辨率
	 */
	protected void RatioChange() {
	}

	/**
	 * 切换适配比例
	 */
	protected void DefinitChange() {
		 if(playlayout.getSuspendLayoutState()){
			 playlayout.displaySuspendlayout(View.INVISIBLE);
		 }

	}

	/**
	 * 设置点播播放器按钮的点击事件
	 */
	@Override
	public void onClick(View v) {

		if (v.equals(exitDialogView.getExitbtn())) {
			clear();
		}else if (v.equals(playlayout.getPause())) {
			pauseResumeAction();// 暂停
		} else if (v.equals(exitDialogView.getLastbtn())) {
			next(false);
			exitDialogView.dismiss();
		} else if (v.equals(exitDialogView.getNextbtn())) {
			next(true);
		} /*else if (v.equals(playMenuDialogView.getDistinlayout())) {//清晰度
			iDefinitIndex = playMenuDialogView.getIdisindex();
			ibreaktime = surfaceView.getCurrentTime();
			stopUpdateProgress();
			DefinitChange();
			playMenuDialogView.hide();

		} else if (v.equals(playMenuDialogView.getRatioLayut())) {//屏幕大小
			iRatioIndex = playMenuDialogView.getRatioIndex();
			RatioChange();
			playMenuDialogView.hide();

		}*//* else if (v.equals(filterdialogview.getDramalayout())) {//选集
			rightPosition = filterdialogview.getIPagingIndexed();
			ipos = filterdialogview.getIdramaindexe();
			choosePlayer();
			filterdialogview.hide();
		} else if (v.equals(filterdialogview.getPagingLayout())) {//切换选集的分页
			 int pageNum = filterdialogview.getIPagingIndexed() + 1;
			 refreshDramaData(pageNum);
		}*/
	}

	/**
	 * 点击菜单节目集分页列表请求该页的节目集列表数据
	 * @param pageNum
	 */
	protected void refreshDramaData(int pageNum){
		String year = "";
		String sortType = "asc";
		if(type.equalsIgnoreCase("综艺")||type.equalsIgnoreCase("纪录片")
				||type.equalsIgnoreCase("短视频")||type.equalsIgnoreCase("音乐")||type.equalsIgnoreCase("游戏"))
			sortType = "desc";
		new RemoteData(this).getMovieEpisodeData(playerbean.getId(), cpId, sortType, pageNum+"", "20", type, year,
				new HttpResponse.Listener4JSONObject() {
					
					@Override
					public void onResponse(JSONObject response) {
						if(response != null && !"{}".equals(response.toString())){
							final JSONArray dramaArr = response.optJSONArray("sources");
							if(dramaArr != null && dramaArr.length() > 0){
								ArrayList<String> dramaList = new ArrayList<String>();
								dramaList.addAll(DataParser.initEpisodeArrayList(dramaArr, type));
								//filterdialogview.refreshDramaListView(dramaList);
							}
						} else {
							
						}
					}
					
					@Override
					public void onError(String httpStatusCode) {
						
					}
				}); 
	}
	

	@Override
	protected void onResume() {
		super.onResume();

		try {
			if (mConnectionChangeReceiver == null) {// 如果播放途中，网络断掉，需要进行相应提示
				mConnectionChangeReceiver = new ConnectionChangeReceiver();
				mConnectionChangeReceiver
						.setOnNetworkChangedListener(new ConnectionChangeReceiver.onNetworkChangedListener() {

							@Override
							public void onNetworkChanged(boolean isConnected) {
								if (!isConnected) {
									showErrorDialog(-1, false, 0, 3000);

								} else {
									if (playererrorDialog != null) {
										if (state > 0) {
											restartPlay();
										}
										loadingDialog.dismiss();
										playererrorDialog.dismiss();
									}
								}
							}
						});
			}

			IntentFilter broadcastFilter = new IntentFilter(
					"android.net.conn.CONNECTIVITY_CHANGE");
			if (mConnectionChangeReceiver != null) {
				registerReceiver(mConnectionChangeReceiver, broadcastFilter);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(surfaceView!=null){
			surfaceView.clear();
		}
		if(loadingDialog!=null){
			
			loadingDialog.dismiss();
		}
		if(exitDialogView!=null){

			exitDialogView.dismiss();
		}
	}
	public void changeFilterDialogVideoList(int p) {
		//filterdialogview.setIMove3(p);
	}

	
}
