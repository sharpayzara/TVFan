package tvfan.tv.ui.andr.play.liveplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.R;
import tvfan.tv.crack.UpdateCrackDialog;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.models.LiveExtraBean;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.andr.play.baseplay.dateparser.DataParser;
import tvfan.tv.ui.andr.play.baseplay.interfaces.ILiveChannel;
import tvfan.tv.ui.andr.play.baseplay.interfaces.IPlayerListener;
import tvfan.tv.ui.andr.play.baseplay.ui.BasePlayerActivity;
import tvfan.tv.ui.andr.play.baseplay.utils.DateUtils;
import tvfan.tv.ui.andr.play.baseplay.utils.LogUtils;
import tvfan.tv.ui.andr.play.baseplay.widgets.BaseVedioView;
import tvfan.tv.ui.andr.play.baseplay.widgets.LiveDialogView;
import tvfan.tv.ui.andr.play.baseplay.widgets.LivePlayControlLayout;
import tvfan.tv.ui.andr.widgets.EnterLivePlayDisplayDialog;
import tvfan.tv.ui.andr.widgets.LivePlaySettingDialog;
import tvfan.tv.ui.andr.widgets.LiveplayLoadingDialog;
import tvfan.tv.ui.andr.widgets.PlayererrorDialog;

//import tvfan.tv.ui.andr.cibnplay.p2p.P2PAsyncTask;

/**
 * 安卓轮播播放器
 * 
 * @author sadshine
 * 
 */
public class Page extends BasePlayerActivity implements ILiveChannel,
		IPlayerListener, OnClickListener {
	/*
	 * new Thread(new Runnable() {
	 * 
	 * @Override public void run() { Looper.prepare(); p2pasynctask = new
	 * P2PAsyncTask(Page.this, null, "", "", "", "", 0); //23.27.125.146:9906
	 * 54d2274800091117000a908a308fb195 "54acb8f5000c587a3cfe290770046b3d",
	 * "123.103.61.206:9906"
	 * p2pasynctask.sendHttpRequest("54acb8f5000c587a3cfe290770046b3d",
	 * "123.103.61.206:9906", "", "switch_chan", "");
	 * 
	 * } }).start(); url =
	 * "http://127.0.0.1:9906/54acb8f5000c587a3cfe290770046b3d.ts";
	 */
	// initPlayer("http://hls01.ott.disp.cibntv.net/2014/05/29/zuiyuandezhongfeng_hd1500k_140134109910636/zuiyuandezhongfeng_hd1500k_140134109910636.m3u8?k=01cff39100bd2e5f9f39fca7cd438e29&channel=cibn&t=1429256263&ttl=86400");
	// initseekBar();
	/*
	 * Bundle bd = getIntent().getExtras(); String strjson =
	 * bd.getString("moviedetail"); 123.103.61.206:9906
	 * 54acb8f5000c587a3cfe290770046b3d //23.27.125.146:9906
	 * 54d2274800091117000a908a308fb195 "54acb8f5000c587a3cfe290770046b3d",
	 * "123.103.61.206:9906"
	 */
	String lrid,tbid;//记录的菜单选择框3.4项的状态
	private int iRatioIndex = 0;// 当前筛选分辨率索引
	protected static final int UPDATE_CRACK_DIALOG = 1001;
	public static final int CRACK_COMPLETE = 1002;
	public static final int SWITCH_CHANNEL = 10;
//	private final int PLAY_OVERTIME = 1003;
//	private final int PLAY_OVERTIME_DELAY = 2 * 60 * 1000;
	private LocalData mLocalData;
	// 播放控件
	private EnterLivePlayDisplayDialog enterDialog;
	private PlayererrorDialog playererrorDialog;
	private BaseVedioView surfaceView;
	private int UPDATE_PROGRESS = 1000;
	// 布局控件
	private RelativeLayout rootlayout;
	LivePlayControlLayout playlayout;
	private Handler timerhd, mHandler;
	private LiveplayLoadingDialog liveplayLoadingDialog;
	// private ExitDialogView exitdialogview;
	private LiveDialogView livedialogview;
	private LivePlaySettingDialog livePlaySettingDialog;//设置界面
	// private P2PAsyncTask p2pasynctask;
	private PlayRecordHelpler playrecordhelper;// 操作保存在本地的播放数据的帮助类
	private LiveExtraBean liveextbean;
	private LinearLayout llChannel;
	// MyLiveService liveService;
	private ArrayList<String> sourceList;// 直播来源地址的集合
	private HomeKeyEventBroadCastReceiver receiver;//接收Home键的广播接收器
	private int i = 0,j=0;
	private int windowHeight;
	private Handler posthd = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_CRACK_DIALOG:
				updateCrackDialog = new UpdateCrackDialog(Page.this,
						R.style.UpdateDialog);
				updateCrackDialog.show();
				updateCrackDialog.setCancelable(false);
				break;
			case CRACK_COMPLETE:
				channelTurn((String) msg.obj);
				break;  
			case SWITCH_CHANNEL:// 进行节目来源的自动切换
				if (msg.what == 10 && sourceList.size() > 1) {
					changeSource(1);
					i++;
					j++;
					
				}
				if (msg.what == 10 && sourceList.size() == 1) {
					App.mToast(Page.this, "当前频道出现故障,请您换个台先!!!");
					livedialogview.changeChannel(-2);
				}

				if (i > sourceList.size()) {
					// showErrorDialog(101, false, 0, 10000);
					i = 0;
						liveplayLoadingDialog.show();
				}
				//当自动切源操作超过两遍时,进行自动换台
				if(j>sourceList.size()*2){
					livedialogview.changeChannel(-2);
					j=0;
				}
				break;
//			case PLAY_OVERTIME:
//				LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, livedialogview.getCurchannelId(), sourceList.get(ipos), "", "live", "13", Page.this);
//				Log.d("logger", "当前返回的logger值为13");
//				break;

			default:
				break;
			}
			/*
			 * if(i>sourceList.size()-1){ // showErrorDialog(101, false, 0,
			 * 10000); showLiveplayErrorDialog(); i=0;
			 * loaddialogview.displayprogress(View.INVISIBLE);
			 * loaddialogview.clear(); posthd.removeMessages(10); }
			 */
		}
	};

	private final static String TAG = "liveplay.Page";
	private int ipos;
	private String currentCHannelid;
//	private P2PModule p2p;

//	public void initP2P() {
//		p2p = P2PModule.getInstance("vbyte-v7a");
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		playType = LIVE_PLAY;
		setContentView(R.layout.activity_player);
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		// 使窗口支持透明度
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		mLocalData = new LocalData(this);
		Lg.i(TAG, "onCreate");
		try {
			setPlayerListener(this);
			Lg.i(TAG, "init.");
			init();
			Lg.i(TAG, "initPlayer.");
			initPlayer("");

			// Lg.i(TAG, "initForceClient.");
			// tvfan.tv.ui.andr.cibnplay.p2p.ForceTV.initForceClient();
			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// Looper.prepare();
			// p2pasynctask = new P2PAsyncTask(Page.this, null, "", "",
			// "", "", 0);
			// // 23.27.125.146:9906 54d2274800091117000a908a308fb195
			// // "54acb8f5000c587a3cfe290770046b3d", "123.103.61.206:9906"
			// Lg.i(TAG, "p2pasynctask-->sendHttpRequest.");
			// p2pasynctask.sendHttpRequest("", "", "", "switch_chan", "");
			//
			// }
			// }).start();
			// http://127.0.0.1:9906/54acb8f5000c587a3cfe290770046b3d.ts

			// DateUtils.startClockAlert(clockaHandler);
		} catch (Exception e) {
			e.printStackTrace();
			this.finish();
			try {
				android.os.Process.killProcess(android.os.Process.myPid());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	//

	private void playChannel(String murl) {

		try {
			Lg.i(TAG, "playChannel-->murl = " + murl);
			// p2p播放
			if (murl.contains("tvfan.cn")&& AppGlobalConsts.P2PLOCKED==1) {
				murl = getMobilePlayUrl(murl);

				Lg.i(TAG, "playChannel-->murl2 = " + murl);
			}
			surfaceView.startPlayer(murl.trim());
//			posthd.sendEmptyMessageDelayed(PLAY_OVERTIME, PLAY_OVERTIME_DELAY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/** ---------------初始化------------------- **/
	/*
	 * protected Runnable clockalertrb = new Runnable() {
	 * 
	 * @Override public void run() { // setSmallBottomDisPlay(); } };
	 * 
	 * protected Handler clockaHandler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) {
	 * this.post(clockalertrb); } };
	 */

	/**
	 * 成员变量初始化
	 */
	private void init() {
		// 进行传递数据的解析
		liveextbean = DataParser.getLiveExtraBean(this, null);
		playrecordhelper = new PlayRecordHelpler(this);
		mHandler = new Handler();
		timerhd = new Handler();
		rootlayout = (RelativeLayout) findViewById(R.id.content);
		llChannel = new LinearLayout(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		// tvChannel.setGravity(Gravity.CENTER);//居中
		llChannel.setLayoutParams(layoutParams);
		llChannel.setOrientation(LinearLayout.HORIZONTAL);
		llChannel.setVisibility(View.GONE);

		surfaceView = new BaseVedioView(this, null);

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		surfaceView.setLayoutParams(lp);
		rootlayout.addView(surfaceView);
		rootlayout.addView(llChannel);
		playlayout = new LivePlayControlLayout(this, rootlayout);
		playlayout.addLivePlayLayout();
		// playlayout.addPlayTopLayout();
		playlayout.addPlayCenterLayout();
		playlayout.addSmallBottomlayout();
		displayInfoArea(false);
		livedialogview = new LiveDialogView(this, R.style.LiveDialogStyle,
				liveextbean, posthd);
		livedialogview.setILiveChannel(this);
		livedialogview.setDisplay(View.INVISIBLE);
		currentCHannelid = livedialogview.getCurchannelId();
		liveplayLoadingDialog = new LiveplayLoadingDialog(Page.this, R.style.enterdialog, livedialogview);
		liveplayLoadingDialog.getWindow().setLayout(
				getWindowManager().getDefaultDisplay().getWidth(),
				getWindowManager().getDefaultDisplay().getHeight());
		liveplayLoadingDialog.show();

		// startClock();
		LogUtils.enterLogger("", "", "", "", this);
		String entershow = mLocalData.getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_ENTERDIALOG_SHOW
				.name());
		if(TextUtils.isEmpty(entershow)){
			entershow ="0";
		}
		int tag = Integer.parseInt(entershow);
		if(tag<3){
			enterDialog = new EnterLivePlayDisplayDialog(Page.this,R.style.enterdialog);
			enterDialog.show();
			//设置dialog填充屏幕显示
			enterDialog.getWindow().setLayout(
					getWindowManager().getDefaultDisplay().getWidth(),
					getWindowManager().getDefaultDisplay().getHeight());
		}else{
				liveplayLoadingDialog.show();
		}
		Log.d("ENTERDIALOG_SHOW","当前记录的App.ENTERDIALOG_SHOW值是...."+tag);
		tag++;
		mLocalData.setKV(
				AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_ENTERDIALOG_SHOW.name(), tag+"");
		Log.d("ENTERDIALOG_SHOW", "当前记录的App.ENTERDIALOG_SHOW值是...." + tag);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (enterDialog != null) {
					enterDialog.dismiss();
				}
				livedialogview.cancel();
			}
		}, 5000);
		playererrorDialog = new PlayererrorDialog(this,R.style.errordialog);

	}
	private void initLivePlaySettingdialog(){
		livePlaySettingDialog = new LivePlaySettingDialog(this,R.style.livePlaySettingDialog,playrecordhelper);
		livePlaySettingDialog.setMPlayeRecordInfo(livedialogview
				.getCurrentChannel());
		livePlaySettingDialog.setCurchannelId(livedialogview.getCurchannelId());
	}

	private Runnable mProgressRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				if (surfaceView.isPlaying()) {
					playererrorDialog.dismiss();
				}
				mHandler.postDelayed(mProgressRunnable, UPDATE_PROGRESS);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	protected void startUpdateProgress() {
		mHandler.removeCallbacks(mProgressRunnable);
		mHandler.postDelayed(mProgressRunnable, UPDATE_PROGRESS);
	}

	protected void stopUpdateProgress() {

		mHandler.removeCallbacks(mProgressRunnable);

	}

	/**
	 * 播放器入口
	 * 
	 * @param url
	 */
	private void initPlayer(String url) {
		try {
			initPlayerListener();
			surfaceView.createPlayer(url);
//			posthd.sendEmptyMessageDelayed(PLAY_OVERTIME, PLAY_OVERTIME_DELAY);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** ----------按键响应操作-----播放器功能逻辑+界面显示------------------ **/
	boolean isShow = false;

	private void CenterAction() {

		setSmallBottomDisPlay();
	}

	private void volumnAction(boolean up) {
		//清空缓存
		surfaceView.destroyDrawingCache();
		surfaceView.volumn(up);

	}

	public void hideTimerAction() {
		if (timerhd != null) {
			timerhd.removeCallbacks(timerrb);
		}

		timerhd.postDelayed(timerrb, 3000l);

	}

	public void removehidetimer() {
		if (timerhd != null) {
			timerhd.removeCallbacks(timerrb);
		}
	}

	// private void startClock() {
	// if (clockhd != null) {
	// clockhd.removeCallbacks(clockrb);
	// }
	//
	// clockhd.postDelayed(clockrb, 1000l);
	//
	// }
	//
	// private void stopClock() {
	// if (clockhd != null) {
	// clockhd.removeCallbacks(clockrb);
	// }
	// }

	// Runnable clockrb = new Runnable() {
	//
	// @Override
	// public void run() {
	// playlayout.getData();
	// clockhd.postDelayed(clockrb, 1000l);
	// }
	// };
	Runnable timerrb = new Runnable() {

		@Override
		public void run() {
			displayInfoArea(false);
			// livediaologview.hide();
		}
	};

	/**
	 * 结束播放进行资源释放
	 */
	public void clear() {
		try {
			if(liveplayLoadingDialog!=null){
				liveplayLoadingDialog.cancel();
			}
			if(livedialogview!=null){
				livedialogview.clear();
			}
			/*if(livefilterview!=null){
				
				livefilterview.clear();
			}*/
			if(livePlaySettingDialog!=null){
				livePlaySettingDialog.cancel();
			}
			if(surfaceView!=null){
				surfaceView.stop();
			}
			if (receiver != null) {
				unregisterReceiver(receiver);
			}
//			surfaceView.clearAvoidStuke();
			stopUpdateProgress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (surfaceView != null) {
					surfaceView.clear();
				}

			}
		}).start();
		// p2pasynctask.removeRequest(this);
		DateUtils.closeClockAlert();
		//timerhd, mHandler;
		clearHandler(timerhd);
		clearHandler(mHandler);
		clearHandler(posthd);
	}

	/** ---------------播放器界面显示逻辑------------------- **/
	/**
	 * 呼出上下界面区域
	 * 
	 * @param display
	 */
	public void displayInfoArea(boolean display) {

		if (playlayout != null) {
			playlayout.displayBottomArea(display);
			playlayout.displaySmallBottomArea(display);
		}
		// if (display) {
		// getFav();
		// startClock();
		// } else {
		// stopClock();
		// }
	}

	/** ---------------播放器界面事件------------------- **/

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (surfaceView.getState() == 2
				&& (event.getKeyCode() != KeyEvent.KEYCODE_ENTER
						&& event.getKeyCode() != KeyEvent.KEYCODE_DPAD_CENTER && event
						.getKeyCode() != KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	};

	/**
	 * 遥控器按键的监听事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Lg.v("player keydown:", "keycode:" + keyCode);


		if (this.isFinishing()) {
			return true;
		}
		if(liveplayLoadingDialog.isShowing()==false){
			switch (keyCode) {

				case KeyEvent.KEYCODE_0:
				case KeyEvent.KEYCODE_1:
				case KeyEvent.KEYCODE_2:
				case KeyEvent.KEYCODE_3:
				case KeyEvent.KEYCODE_4:
				case KeyEvent.KEYCODE_5:
				case KeyEvent.KEYCODE_6:
				case KeyEvent.KEYCODE_7:
				case KeyEvent.KEYCODE_8:
				case KeyEvent.KEYCODE_9:
				onNumKeyDown((keyCode - 7) + "");
				break;
				case 178:
				break;
				case KeyEvent.KEYCODE_DPAD_UP:
				case KeyEvent.KEYCODE_CHANNEL_UP:
					tbid=mLocalData.getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_TOPBOTTOM_DEFAILT.name());
				if(TextUtils.isEmpty(tbid)){
					tbid="0";
				}
				if(tbid.contains("0")){

				liveplayLoadingDialog.show();
					livedialogview.changeChannel(-1);
					displayInfoArea(true);
				}else{
						liveplayLoadingDialog.show();
					livedialogview.changeChannel(-2);
					displayInfoArea(true);
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_CHANNEL_DOWN:
			case 2012:
			case 2006:
				tbid=mLocalData.getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_TOPBOTTOM_DEFAILT.name());
				if(TextUtils.isEmpty(tbid)){
					tbid="0";
				}
				if(tbid.contains("0")){

						liveplayLoadingDialog.show();
					livedialogview.changeChannel(-2);
					if(!issamlllayoutShow()){

						displayInfoArea(true);
					}
				}else{
						liveplayLoadingDialog.show();
					livedialogview.changeChannel(-1);
					if(!issamlllayoutShow()){
						displayInfoArea(true);
					}
				}
				return true;
			case KeyEvent.KEYCODE_MENU:
				Log.i(TAG, "KEYCODE_MENU");
				if(livePlaySettingDialog==null){
					initLivePlaySettingdialog();
				}else{
					livePlaySettingDialog.setMPlayeRecordInfo(livedialogview
							.getCurrentChannel());
					livePlaySettingDialog.setCurchannelId(livedialogview.getCurchannelId());
					livePlaySettingDialog.fav();
				}
				livePlaySettingDialog.show();
				return true;

			case 122:
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				removehidetimer();
				setSmallBottomDisPlay();
				livedialogview.setDisplay(View.VISIBLE);
				livedialogview.showLiveDialog();
				hideTimerAction();
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
					volumnAction(true);
				break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				volumnAction(false);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				lrid=mLocalData.getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_LEFTRIGHRT_DEFAILT.name());
				Log.d("MyTAG","6当前的lrid值为...."+ lrid);
				if(TextUtils.isEmpty(lrid)){
					lrid="0";
				}
				if (lrid.contains("0")){
					if (sourceList.size() > 1) {
							liveplayLoadingDialog.show();
						changeSource(1);
					}
				}else{

					volumnAction(true);
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				lrid=mLocalData.getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_LEFTRIGHRT_DEFAILT.name());
				Log.d("MyTAG","5当前的lrid值为...."+ lrid);
				if(TextUtils.isEmpty(lrid)){
					lrid="0";
				}
				if (lrid.contains("0")){
					if (sourceList.size() > 1) {
							liveplayLoadingDialog.show();
						changeSource(0);
					}
				}else{
					volumnAction(false);
				}
				break;
			case KeyEvent.KEYCODE_BACK:
				if (playlayout.isBottomDisplay() == View.VISIBLE
						|| playlayout.isSmallBottomDisplay() == View.VISIBLE
						|| livedialogview.isShowing()) {
					displayInfoArea(false);
					livedialogview.dismiss();

				} else if (llChannel.getVisibility() == View.VISIBLE) {
					llChannel.setVisibility(View.GONE);
				} else {
					break;
				}
				return true;
				// break;

			}
			hideTimerAction();
		}
		return super.onKeyDown(keyCode, event);
		}
		

	/*
	private static Boolean isExit = false;

	 * 这是设置双击退出播放器的方法
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			clear();
			this.finish();
//			try {
//				android.os.Process.killProcess(android.os.Process.myPid());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

		}
	}
*/
	/**
	 * 按左右键切源
	 * 
	 * @param arg
	 *            0代表左键，1代表右键
	 */
	public void changeSource(int arg) {
		if (liveplayLoadingDialog.isShowing()){
			surfaceView.clearAvoidStuke();
		}
		surfaceView.destroyDrawingCache();
		playlayout.displayBottomArea(true);
		playlayout.displaySmallBottomArea(true);
		if (sourceList != null && sourceList.size() > 1) {

			switch (arg) {
			case 0:
				ipos--;
				if (ipos < 0) {
					ipos = sourceList.size() - 1;
				}
				break;
			case 1:
				ipos++;
				if (ipos > sourceList.size() - 1) {
					ipos = 0;
				}
				break;
			}
			playlayout.setTxtCPNumDisplay((ipos+1) + "/" + sourceList.size());
			Log.d("liveplaychannal","当前的ipos值为"+(ipos+1) + "/" + sourceList.size());
			livedialogview.play(ipos);
			/*livefilterview.setIMove3(ipos);
			livefilterview.dismiss();*/
		} else {
			playlayout.displaySmallBottomArea(false);
		}
	}

	private int digit = 3;
	private String channelNum;
	Runnable task = new Runnable() {
		public void run() {
			changeChannel(channelNum);
			llChannel.removeAllViews();
			llChannel.setVisibility(View.GONE);
			digit = 3;
			channelNum = null;
		}
	};
	Handler handler = new Handler();

	/**
	 * 数字键进行的操作
	 * 
	 * @param i
	 */
	private void onNumKeyDown(String i) {
		if (digit > 0) {
			channelNum = channelNum == null ? i : channelNum + i;
			digit--;
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(150,
					170);
			lp.setMargins(3, 2, 3, 2);
			TextView textView = new TextView(this);
			textView.setBackgroundResource(R.drawable.shape_channel);
			textView.setGravity(Gravity.CENTER);
			textView.setText(i);
			textView.setTextSize(App.adjustFontSize(60));
			textView.setTextColor(Color.WHITE);
			textView.setLayoutParams(lp);
			llChannel.addView(textView);
		}
		llChannel.setVisibility(View.VISIBLE);
		handler.removeCallbacks(task);
		handler.postDelayed(task, 1500);
	}
	
	/**
	 * 按遥控器数字键时调用
	 * 
	 * @param channelNum
	 */
	private void changeChannel(String channelNum) {
		if (channelNum != null && channelNum.length() == 1
				&& !channelNum.equals("0")) {
			channelNum = 0 + channelNum;
		}
		int channelPos = livedialogview.getChannelPos(channelNum);
		if (channelPos == -1) {
			// mToast(this, "没有" + channelNum + "频道", Toast.LENGTH_SHORT);
			App.mToast(this, "暂无此节目");
		} else {
			livedialogview.changeChannel(channelPos);
		}
	}

	/** ---------------播放器初始化渲染-------------------- **/

	private void initPlayerListener() {
		try {
			surfaceView.setOnPreparedListener(new OnPreparedListener() {// 准备播放的监听
				@Override
						public void onPrepared(MediaPlayer mp) {
							Log.i(TAG, "onPrepared");
//							posthd.removeCallbacksAndMessages(PLAY_OVERTIME);
							if (updateCrackDialog != null && updateCrackDialog.isShowing()) {
								updateCrackDialog.dismiss();
							}
							surfaceView.start();
							posthd.removeMessages(SWITCH_CHANNEL);
							playlayout.setCenterText(livedialogview.getCurNo(),
									livedialogview.getCurchannel(),
									livedialogview.getCurTime(),
									livedialogview.getNextTime(),
									livedialogview.getCurprogram(),
									livedialogview.getNextprogram());
							hideTimerAction();
							startUpdateProgress();
					if(liveplayLoadingDialog!=null){

						closeLivePlayLoadingDialog();
					}


						}
					});

			surfaceView
					.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {// 播放时进行缓冲的监听
						
						@Override
						public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
							if (arg1==0) {
//								liveplayLoadingDialog.show();
							}
//							liveplayLoadingDialog.dismiss();
						}
					});
/*
			surfaceView.setOnInfoListener(new OnInfoListener() {// 信息传递的监听（warning
																// or info）

						@Override
						public boolean onInfo(MediaPlayer meiaplayer, int what,
								int info) {
							if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {// 开始缓冲
//								liveplayLoadingDialog.show();
							} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {// 缓冲结束
								liveplayLoadingDialog.dismiss();
							} else {
								if (surfaceView.isPlaying()) {
									// loaddialogview.displayprogress(View.GONE);
									liveplayLoadingDialog.dismiss();
									if (playererrDialog != null) {
										playererrDialog.hide();
									}
								}
//								liveplayLoadingDialog.show();
							}

							return true;
						}
					});*/

			surfaceView.setOnErrorListener(new OnErrorListener() {// 播放出错的监听

						@Override
						public boolean onError(MediaPlayer mediaplayer,
								int what, int extra) {
							Log.e(TAG, "onError() what:" + what + ", extra:" + extra);
							switch (what) {
								case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
									PlayerErrorListener(what);
									return true;
								default:
									Log.d("logger", "Error: " + what + "," + extra);
									LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, livedialogview.getCurchannelId(), sourceList.get(ipos), "", "live", "16", Page.this);
									Log.d("logger", "此时发送的log是16");
									Log.d("logger","sourceList.get(ipos)...."+sourceList.get(ipos));
									break;
							}
							return false;
						}
					});
			surfaceView.setOnCompletionListener(new OnCompletionListener() {// 播放结束的监听

						@Override
						public void onCompletion(MediaPlayer mp) {
							Lg.v("complete", "the player has auto completed");
							// MediaPlayerCompletionCallback();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// super.onActivityResult(requestCode, resultCode, data);
	}

	private long exitTime = 0;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - exitTime > 3000) {
			// Toast.makeText(getApplicationContext(), "再按一次返回退出当前播放",
			// Toast.LENGTH_SHORT).show();
			App.mToast(this, "再按一次返回退出当前播放");
			exitTime = System.currentTimeMillis();

			return;
		}
		clear();
		this.finish();
//		try {
//			android.os.Process.killProcess(android.os.Process.myPid());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		super.onBackPressed();
	}
	MPlayRecordInfo mplayrcinfo;
	public int getFav() {
		int fav=0 ;
		try {
//			PlayRecordHelpler ph = new PlayRecordHelpler(this);
			mplayrcinfo= playrecordhelper.getLivePlayRcInfo(livedialogview.getCurchannelId());
			if(mplayrcinfo!= null){
				fav = mplayrcinfo.getPlayerFav();
				Log.d("index","数据库中的index值为...."+fav);
			}

			return fav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("index","get方法返回的index值为...."+fav);
		return fav;
	}

	/**
	 * 加载dialog进行消失
	 */
	/*Runnable mRunnable_loading = new Runnable() {

		@Override
		public void run() {
			liveplayLoadingDialog.dismiss();
		}
	};*/
	/**
	 * 用于LiveDialogView调用播放
	 */
	@Override
	public void channelTurn(String url) {
//		liveplayLoadingDialog.show();
		playChannel(url);
		int index = getFav();
		Log.d("index","当前收藏列表的index为.."+index);
		//根据获取到的index值刷新settingdialog的显示
		livePlaySettingDialog.refreshshow(index);
//		mHandler.postDelayed(mRunnable_loading, 3000l);
	}

	@Override
	public void freshChannel(String no, String title, String curt, String next,
			String curinfo, String nextinfo) {
		playlayout.setCenterText(no, title, curt, next, curinfo, nextinfo);
		// getFav();

	}

	@Override
	public void PlayerCompleteListener() {
	}

	/**
	 * 显示播放失败的dialog
	 */
	@Override
	public void PlayerErrorListener(int what) {
		if (sourceList.size()<=ipos)
			LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID,  livedialogview.getCurchannelId(), "", "", "live", "15",Page.this);
		else
			LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID,  livedialogview.getCurchannelId(), sourceList.get(ipos), "", "live","12", Page.this);
		Log.d("logger","当前返回的logger值为12");
		/*
		 * while(!surfaceView.isPlaying()&&ipos>sourceList.size()-1){
		 * mHandler.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { ipos++; changeSource(ipos); }
		 * },10000L);
		 * 
		 * }
		 */
		/*
		 * if(i>=sourceList.size()){
		 * 
		 * // showErrorDialog(what, false, 0, 5000);
		 * while(!surfaceView.isPlaying()&&ipos>sourceList.size()-1){
		 * mHandler.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { ipos++; changeSource(ipos); }
		 * },10000L);
		 * 
		 * }
		 */
	}

	@Override
	protected void onStop() {
		// wanqi,解决悟空二级跳转多次进入轮播不刷新的问题,0723
		//clear();
		//this.finish();
		super.onStop();
	}

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
	protected void onPause() {
		PlayerPause(true);
		if (surfaceView != null)
			surfaceView.clearAvoidStuke();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*if(livefilterview != null){
			livefilterview.dismiss();
			livefilterview = null;
		}*/
		if(livePlaySettingDialog != null){
			livePlaySettingDialog.cancel();
			livePlaySettingDialog = null;
		}
		clear();
		livedialogview.stopCrack();
		if (surfaceView != null)
			surfaceView.clearAvoidStuke();
	}

	@Override
	protected void onResume() {
		super.onResume();
		surfaceView.avoidStukeTimer();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		PlayerPause(false);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// 悟空遥控器切台
		PlayerPause(false);
		liveextbean = DataParser.getLiveExtraBean(this, intent);
		livedialogview.setLivextbean(liveextbean);
		livedialogview.changeChannel(livedialogview.getChannelPos());
	}

	// ServiceConnection conn = new ServiceConnection() {
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// liveService = null;
	// }
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// liveService = MyLiveService.Stub.asInterface(service);
	// livediaologview.setLiveService(liveService);
	// // Log.e("msg_bind_complete",System.currentTimeMillis()+"");
	// // EventBus.getDefault().post(new
	// // EventMessage("Player_Letv_Ready"));
	// }
	// };

	// public void stopLeTvService() {
	// if (liveService != null) {
	// try {
	// liveService.stopServer();
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	@Override
	public void updateSource(ArrayList<String> sourceList) {
		this.sourceList = sourceList;
		ipos = 0;
		playlayout.setTxtCPNumDisplay((ipos + 1)+ "/" + sourceList.size());
		Log.d("liveplaychannal", "2当前的ipos值为" + (ipos + 1) + "/" + sourceList.size());
		if (livePlaySettingDialog == null) {
			initLivePlaySettingdialog();
		}else{
			livePlaySettingDialog.setMPlayeRecordInfo(livedialogview
					.getCurrentChannel());
			livePlaySettingDialog.setCurchannelId(livedialogview.getCurchannelId());
			livePlaySettingDialog.fav();
		}
		/*if (livefilterview == null) {
			initLiveFilterView();
		} else {
			livefilterview.updateSourceList(sourceList);
			livefilterview.setCurchannelId(livedialogview.getCurchannelId());
			livefilterview.setMPlayeRecordInfo(livedialogview
					.getCurrentChannel());
			livefilterview.fav();
		}
		ipos = livefilterview.getSourceindex();*/
		if (sourceList.size() > 1) {

			playlayout.setTxtCPNumDisplay((ipos+1) + "/" + sourceList.size());
			Log.d("liveplaychannal", "3当前的ipos值为" + (ipos + 1) + "/" + sourceList.size());
		} else {
			playlayout.displaySmallBottomArea(false);
		}
	}

	/**
	 * 点击事件的监听
	 */
	@Override
	public void onClick(View v) {
		/*if (livefilterview != null
				&& v.equals(livefilterview.getSourcelayout())) {// 点击的是节目来源列表时
			ipos = livefilterview.getSourceindex();
			livedialogview.play(ipos);
			playlayout.setTxtCPNumDisplay((ipos + 1) + "/" + sourceList.size());
			livefilterview.hide();
		} else if (livefilterview != null
				&& v.equals(livefilterview.getRatiolayout())) {// 点击的是视频比例列表时
			livefilterview.hide();
		}*/
	}


	/**
	 * 显示或者隐藏当前节目信息提示框
	 */
	@Override
	public void showCurrentProgramInfo(boolean show) {

		displayInfoArea(show);
	}

	/**
	 * 根据当前台的源的个数来设定右下角的节目信息提示框的源状态栏的是否显示
	 */
	public void setSmallBottomDisPlay() {
		if (sourceList.size() > 1) {
			displayInfoArea(true);
		} else {
			playlayout.displaySmallBottomArea(false);
			playlayout.displayBottomArea(true);
		}
	}

	UpdateCrackDialog updateCrackDialog;

	@Override
	public void showUpdateDialog(boolean show) {
		if (show) {
			posthd.sendEmptyMessage(UPDATE_CRACK_DIALOG);
		} else if (updateCrackDialog != null) {
			updateCrackDialog.dismiss();
		}
	}

	@Override
	public void stopSurfaceview() {
		// TODO Auto-generated method stub
		surfaceView.stop();
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
						clear();
						Page.this.finish();
						try {
							android.os.Process.killProcess(android.os.Process.myPid());
						} catch (Exception e) {
							e.printStackTrace();
						}

					} 
				}
			}
		}
	}
	/**
	 * 这是改变显示比例的方法
	 * @param ratioid
	 */
	public void ratioChange(String ratioid) {
		if (ratioid.equals("1")) {
			int ileft = (App.ScreenWidth - surfaceView.getVideoWidth()) / 2;
			int itop = (App.ScreenHeight - surfaceView.getVideoHeight()) / 2;
			surfaceView.setVideoView(surfaceView.getVideoWidth(),
					surfaceView.getVideoHeight(), itop, ileft);
		} else {
			surfaceView.setSurfaceView(App.ScreenWidth, App.ScreenHeight, 0, 0);

		}

	}

	/**
	 * 这是获取直播信息布局显示状态的方法
	 * @return
	 */
	public boolean issamlllayoutShow(){
		 if(playlayout.isBottomDisplay()==View.VISIBLE){
			 return true;
		 }
		return false;
	}

	public void closeLivePlayLoadingDialog(){
		surfaceView.avoidStukeTimer();
		liveplayLoadingDialog.dismiss();
	}

}
