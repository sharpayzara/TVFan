package tvfan.tv.ui.andr.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.dal.LocalData;
import tvfan.tv.ui.andr.play.baseplay.widgets.LiveDialogView;

/**
 * 这是直播加载框
 * 
 * @author zhangyisu
 *
 */
public class LiveplayLoadingDialog extends Dialog {
	private LocalData mLocalData;
	private Context mctx;
	private TextView liveplay_textprogressbar,liveplay_info1;
	private Handler timerhd;
	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	private LiveDialogView livedialogview;
	private String tbid ;
	public LiveplayLoadingDialog(Context mctx,int theme,LiveDialogView livedialogview) {
		super(mctx,theme);
		this.mctx = mctx;
		this.livedialogview= livedialogview;
		timerhd = new Handler();
		setContentView(R.layout.liveplay_loadingdialogview_lauout);
		liveplay_textprogressbar = (TextView) findViewById(R.id.liveplay_textprogressbar);
		 liveplay_info1 = (TextView) findViewById(R.id.liveplay_info1);
		 liveplay_info1.setTextSize(App.adjustFontSize(13));
		timerhd.removeCallbacks(timerb);
		lastTotalRxBytes = getTotalRxBytes();
		lastTimeStamp = System.currentTimeMillis();
		timerhd.postDelayed(timerb, 500l);
		liveplay_textprogressbar.requestLayout();
		liveplay_textprogressbar.invalidate();
		mLocalData = new LocalData(mctx);
		setCancelable(false);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode==KeyEvent.KEYCODE_BACK){
		
		exitBy2Click();
		return true;
	}
	switch (keyCode){
	
	case KeyEvent.KEYCODE_BACK:
		exitBy2Click();
		break;
	case KeyEvent.KEYCODE_DPAD_UP:
	case KeyEvent.KEYCODE_CHANNEL_UP:

		tbid=mLocalData.getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_TOPBOTTOM_DEFAILT.name());
		if(TextUtils.isEmpty(tbid)){
			tbid="0";
		}
		if(tbid.contains("0")){

			show();
			livedialogview.changeChannel(-1);
			if(!((tvfan.tv.ui.andr.play.liveplay.Page)mctx).issamlllayoutShow()){

				((tvfan.tv.ui.andr.play.liveplay.Page)mctx).displayInfoArea(true);
			}
		}else{
			show();
			livedialogview.changeChannel(-2);
			if(!((tvfan.tv.ui.andr.play.liveplay.Page)mctx).issamlllayoutShow()){

				((tvfan.tv.ui.andr.play.liveplay.Page)mctx).displayInfoArea(true);
			}
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

			show();
			livedialogview.changeChannel(-2);
			if(!((tvfan.tv.ui.andr.play.liveplay.Page)mctx).issamlllayoutShow()){

				((tvfan.tv.ui.andr.play.liveplay.Page)mctx).displayInfoArea(true);
			}
		}else{
			show();
			livedialogview.changeChannel(-1);
			if(!((tvfan.tv.ui.andr.play.liveplay.Page)mctx).issamlllayoutShow()){

				((tvfan.tv.ui.andr.play.liveplay.Page)mctx).displayInfoArea(true);
			}
		}
		return true;
	case KeyEvent.KEYCODE_DPAD_RIGHT:
		((tvfan.tv.ui.andr.play.liveplay.Page)mctx).changeSource(1);
		break;
	case KeyEvent.KEYCODE_DPAD_LEFT:
		((tvfan.tv.ui.andr.play.liveplay.Page)mctx).changeSource(0);
		break;
	/*case KeyEvent.KEYCODE_ENTER:
		((tvfan.tv.ui.andr.cibnplay.liveplay.Page)mctx).removehidetimer();
		((tvfan.tv.ui.andr.cibnplay.liveplay.Page)mctx).setSmallBottomDisPlay();
		livedialogview.setDisplay(View.VISIBLE);
		livedialogview.showLiveDialog();
		((tvfan.tv.ui.andr.cibnplay.liveplay.Page)mctx).hideTimerAction();
		return true;*/
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
			    	((tvfan.tv.ui.andr.play.liveplay.Page)mctx).clear();
					((Activity)mctx).finish();
			    }  
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
	     
	    Message msg = mHandler.obtainMessage();
	    msg.what = 100;
	    msg.obj = String.valueOf(speed) + " kbp/s";
	    mHandler.sendMessage(msg);//更新界面
	}
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			String speed=(String)msg.obj;
			liveplay_textprogressbar.setText(speed);
		};
	};
	private long getTotalRxBytes() {
	    return TrafficStats.getUidRxBytes(mctx.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
	}
}
