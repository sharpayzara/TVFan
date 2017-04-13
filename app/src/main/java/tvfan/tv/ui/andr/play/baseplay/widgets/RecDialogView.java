package tvfan.tv.ui.andr.play.baseplay.widgets;

import tvfan.tv.lib.DisplayUtil;
import tvfan.tv.lib.Utils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import tvfan.tv.R;

/**
 * 播放记录对话框
 * @author sadshine
 *
 */
public class RecDialogView extends Dialog{

	// 父容器,收藏容器,分辨率容器,视频比例容器,剧集容器
	private RelativeLayout mrelayout;
	private Context mcontext;
	private RelativeLayout reclayout;
	private TextView txtinfo,txttime,txttimer;
	/**
	 * btnok 重新播放按钮   ,btncancel.继续播放按钮
	 */
	private Button btnok,btncancel;
	private String strBreakTime;
	private Handler hd;
	private int itimer = 5;
	public RecDialogView(Context context, int theme, String breaktime) {
		super(context, theme);
		mcontext = context;
		strBreakTime = breaktime;
		initLayout();
		setContentView(mrelayout);
	}

	private void initLayout() {
		mrelayout = new RelativeLayout(mcontext);
		setPostion(1280,
				720, mrelayout, 0, 0, 0, 0);
	}

	public void addRecView(){
		
		reclayout = new RelativeLayout(mcontext);
		reclayout.setBackgroundResource(R.drawable.shape_dialog);
		//reclayout.setBackgroundColor(Color.WHITE);
		setPostion(660, 440,reclayout, 140, 306, 0, 0);
		
		txtinfo = new TextView(mcontext);
		txtinfo.setText("上次播放到:");
		txtinfo.setTextColor(Color.WHITE);
		txtinfo.setTextSize(DisplayUtil.getDisplayValue(28, (Activity)mcontext));
		setPostion(240, 66, txtinfo, 73, 140, 0, 0);
		
		txttime = new TextView(mcontext);
		txttime.setText("00:23:45");
		txttime.setTextColor(Color.YELLOW);
		txttime.setTextSize(DisplayUtil.getDisplayValue(26, (Activity)mcontext));
		setPostion(280, 66, txttime, 73, 370, 0, 0);
		
		txttimer = new TextView(mcontext);
		txttimer.setText("5");
		txttimer.setTextColor(Color.WHITE);
		txttimer.setTextSize(DisplayUtil.getDisplayValue(45, (Activity)mcontext));
		setPostion(100, 100, txttimer, 160, 310, 0, 0);
		
		btnok = new Button(mcontext);		
		btnok.setText("重新播放");	
		btnok.setTextColor(Color.WHITE);
		btnok.setGravity(Gravity.CENTER);
		//setPostion(233, 266, btnok, 310, 83, 0, 0);
		setPostion(233, 266, btnok, 310, 83, 0, 0);
		btnok.setBackgroundResource(R.drawable.update_btnselector);
		btnok.setTextSize(DisplayUtil.getDisplayValue(18, (Activity)mcontext));		
		btnok.requestFocus();
		
		
		btncancel = new Button(mcontext);
		
		btncancel.setText("继续播放");
		btncancel.setTextColor(Color.WHITE);
		btncancel.setGravity(Gravity.CENTER);
		btncancel.setTextSize(DisplayUtil.getDisplayValue(18, (Activity)mcontext));	
		btncancel.setBackgroundResource(R.drawable.update_btnselector);
		setPostion(233, 266, btncancel, 310,350, 0, 0);	
		
		
		reclayout.addView(txtinfo);
		reclayout.addView(txttime);
		reclayout.addView(txttimer);
		reclayout.addView(btnok);
		reclayout.addView(btncancel);		
		mrelayout.addView(reclayout);
		
		
		txttime.setText(strBreakTime);
		hd = new Handler();
		
	}
	
	public void setOkButtonOnclick(android.view.View.OnClickListener onclicklstener){
		btnok.setOnClickListener(onclicklstener);
	}
	public void setCancelButtonOnclick(android.view.View.OnClickListener onclicklstener){
		btncancel.setOnClickListener(onclicklstener);
	}

	/** ----------private--------------- **/
	private int getFitValue(int value) {
		return DisplayUtil.getDisplayValue(value, (Activity) mcontext);
	}

	private void setPostion(int width, int height, View v, int top, int left,
			int right, int bottom) {
		LayoutParams lp = new LayoutParams(getFitValue(width),
				getFitValue(height));
		lp.setMargins(getFitValue(left), getFitValue(top), getFitValue(right),
				getFitValue(bottom));
		v.setLayoutParams(lp);
	}
	/**----on event-----**/
	Runnable rb = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(((Activity)mcontext).isFinishing())
				return;
			txttimer.setText(Integer.toString(itimer--));
			hd.postDelayed(rb, 1000l);
			if(itimer<0){
				itimer = 5;
				hd.removeCallbacks(rb);
				try {
					RecDialogView.this.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	protected void onStop() {
		if(hd!=null){
			hd.removeCallbacks(rb);
			itimer = 5;
		}
	}

	public Button getBtnok() {
		return btnok;
	}

	public Button getBtncancel() {
		return btncancel;
	}
	
	public void recDialogShow(){
		if(btncancel!=null){
			btncancel.requestFocus();
		}
		itimer = 5;
		if(hd!=null){
			hd.removeCallbacks(rb);
		}
		hd.postDelayed(rb, 0l);
		try {
			this.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void recDialogHide(){
		if(hd!=null){
			hd.removeCallbacks(rb);
			itimer = 5;
		}
		this.hide();
	}
	
	public void setBreakTime(int itime){
		String strtime = Utils.millisToString(itime);
		txttime.setText(strtime);
		txttime.requestLayout();
	}

	
}
