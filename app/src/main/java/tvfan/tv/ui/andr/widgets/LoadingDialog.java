package tvfan.tv.ui.andr.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.ui.andr.play.play.Page;

public class LoadingDialog extends Dialog {
	private Context mctx;
	private ProgressBar play_progressbar;
	private TextView loadinginfo0;
	private TextView loadinginfo1,tv_netspeed;
	private Handler mHandler,timerhd;
	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	public LoadingDialog(Context mctx,int theme) {
		super(mctx,theme);
		this.mctx = mctx;
		mHandler = new Handler();
		timerhd = new Handler();
		init();
		setCancelable(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode==KeyEvent.KEYCODE_BACK){
		exitBy2Click();
		return true;
	}
	return super.onKeyDown(keyCode, event);
	}
	private static Boolean isExit = false;
	/**
	 * 这是设置双击退出播放器的方法
	 */
		private void exitBy2Click() {
			 Timer tExit = null;
			    if (isExit == false) {  
			        isExit = true; // 准备退出  
			        App.mToast(mctx, "再按一次返回退出当前播放");
			        tExit = new Timer();  
			        tExit.schedule(new TimerTask() {
			            @Override  
			            public void run() {  
			                isExit = false; // 取消退出  
			            }  
			        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
			  
			    } else {  
			    	((Page)mctx).clear();
					((Activity)mctx).finish();
			    }  
		}
	private void init() {
		setContentView(R.layout.play_loadingdialogview_lauout);
		play_progressbar = (ProgressBar) findViewById(R.id.play_progressbar);
		loadinginfo0 = (TextView) findViewById(R.id.loadinginfo0);
		loadinginfo1 = (TextView) findViewById(R.id.loadinginfo1);
		loadinginfo0.setVisibility(View.INVISIBLE);
		loadinginfo1.setVisibility(View.INVISIBLE);
		tv_netspeed= (TextView) findViewById(R.id.tv_netspeed);
		showNetSpeedControl();
	}

	private void showNetSpeedControl() {
		timerhd.removeCallbacks(timerb);
		lastTotalRxBytes = getTotalRxBytes();
		lastTimeStamp = System.currentTimeMillis();
		timerhd.postDelayed(timerb, 500l);
		tv_netspeed.requestLayout();
		tv_netspeed.invalidate();
	}

	public void showText0(){
		setTextAnim();
		loadinginfo0.setVisibility(View.VISIBLE);
		loadinginfo0.setTextSize(App.adjustFontSize(13));
	}
	public void showText1(){
		setTextAnim();
		loadinginfo1.setVisibility(View.VISIBLE);
		loadinginfo1.setTextSize(App.adjustFontSize(13));
	}
	private long preSpeed = 0l;
	private Runnable timerb = new Runnable() {

		@Override
		public void run() {
			showNetSpeed();
			timerhd.postDelayed(timerb, 2000l);
		}
	};
	/**
	 * 显示网速的方法
	 */
	private void showNetSpeed() {
	    long nowTotalRxBytes = getTotalRxBytes();
	    long nowTimeStamp = System.currentTimeMillis();
	    long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
	 /*  if(speed==0){
//	    dismiss();
		   if(BasePlayerActivity.playType!=2){
			   Utils.showToast(R.string.no_internet_error);
		   }
		
	}*/
	    lastTimeStamp = nowTimeStamp;
	    lastTotalRxBytes = nowTotalRxBytes;
		Message msg = pmHandler.obtainMessage();
		msg.what = 100;
		msg.obj = String.valueOf(speed) + " kbp/s";
		pmHandler.sendMessage(msg);//更新界面
	}
	private Handler pmHandler=new Handler(){
		public void handleMessage(Message msg) {
			String speed=(String)msg.obj;
			tv_netspeed.setText(speed);
		};
	};
	private long getTotalRxBytes() {
	    return TrafficStats.getUidRxBytes(mctx.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
	}
	/**
	 * 设置lodingdialog的显示动画
	 */
	private void setTextAnim() {
		Animation animation = AnimationUtils.loadAnimation(mctx, R.anim.lodingdialog_textview_anim);//创建动画对象 
		animation.setDuration(500);
	}
	
	public void showLodingDialogText(){
		mHandler.removeCallbacksAndMessages(poster);
		showText0();
		mHandler.postDelayed(poster, 2000);
	}
		private Runnable poster = new Runnable() {
			
			@Override
			public void run() {
				showText1();
			}
		}; 
}