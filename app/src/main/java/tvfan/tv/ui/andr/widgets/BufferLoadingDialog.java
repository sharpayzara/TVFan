package tvfan.tv.ui.andr.widgets;

import android.app.Dialog;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import tvfan.tv.R;
import tvfan.tv.ui.andr.play.baseplay.ui.BasePlayUI;
import tvfan.tv.ui.andr.play.baseplay.widgets.ExitDialogView;

public class BufferLoadingDialog extends Dialog {
	private Context mctx;
	private ProgressBar play_progressbar;
	private TextView tv_netspeed;
	private Handler mHandler,timerhd;

	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	private ExitDialogView exitDialogView;
	public BufferLoadingDialog(Context mctx, int theme,ExitDialogView exitDialogView) {
		super(mctx,theme);
		this.mctx = mctx;
		this.exitDialogView =exitDialogView;
		mHandler = new Handler();
		timerhd = new Handler();
		init();
		setCancelable(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode==KeyEvent.KEYCODE_BACK){
		//弹出退出dialog
		exitDialogView.show();
		dismiss();
		if(onBackKeyListener!=null)
			onBackKeyListener.onBackKey();
		return true;
	}
	return super.onKeyDown(keyCode, event);
	}
	private static Boolean isExit = false;
	private void init() {
		setContentView(R.layout.buffer_loadingdialogview_lauout);
		play_progressbar = (ProgressBar) findViewById(R.id.play_progressbar);
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

	public interface OnBackKeyListener{
		public void onBackKey();
	}

	private OnBackKeyListener onBackKeyListener;

	public void setOnBackKeyListener(OnBackKeyListener onBackKeyListener) {
		this.onBackKeyListener = onBackKeyListener;
	}
	
}