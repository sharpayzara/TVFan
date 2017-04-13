package tvfan.tv.ui.andr.play.baseplay.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.lib.DisplayUtil;
import tvfan.tv.ui.andr.play.baseplay.utils.DateUtils;
import tvfan.tv.ui.andr.widgets.AlwaysMarqueeTextView;

/**
 * @since 2015 04/19
 * @author sadshine
 *
 */
public class LivePlayControlLayout {
	private Context mcontext;
	private RelativeLayout mrelayout, bottomlayout, centerlayout,
	// toplayout,
			rightlayout, smallBottomlayout;
	private TextView txtnum,
	// title,time,
			txtcurt1, txtcurt2, txtnext1, txtnext2, txtCP, txtCPNum;
	private AlwaysMarqueeTextView txtcurtitle, txtnexttitle, txttitle;
	private ImageView pause, forward, backward/* , topbg */;

	public LivePlayControlLayout(Context context, RelativeLayout relayout) {
		mcontext = context;
		mrelayout = relayout;

	}

	/**
	 * 添加播放器暂停播放资源布局
	 */
	public void addPlayCenterLayout() {
		/*centerlayout = new RelativeLayout(mcontext);
		centerlayout.setFocusable(false);
		backward = new ImageView(mcontext);
//		backward.setBackgroundResource(R.drawable.backword);
		setPostion(80, 80, backward, 300, 360, 0, 0);

		pause = new ImageView(mcontext);
		pause.setBackgroundResource(R.drawable.pause);
		setPostion(200, 200, pause, 248, 540, 0, 0);

		forward = new ImageView(mcontext);
//		forward.setBackgroundResource(R.drawable.forward);
		setPostion(80, 80, forward, 300, 840, 0, 0);

		// centerlayout.addView(backward);
		// centerlayout.addView(pause);
		// centerlayout.addView(forward);
		mrelayout.addView(centerlayout);
*/
	}

	/**
	 * 添加播放器时间标题资源布局
	 */
	public void addPlayTopLayout() {

		// toplayout = new RelativeLayout(mcontext);
		// toplayout.setFocusable(false);
		// topbg = new ImageView(mcontext);
		// topbg.setBackgroundColor(Color.BLACK);
		// setPostion(1280, 110, topbg, 0, 0, 0, 0);

		// title = new TextView(mcontext);
		// title.setTextColor(Color.WHITE);
		// title.setText("神雕侠侣第21集 高清");
		// setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		// title,
		// 40, 100, 0, 0);
		// title.setTextSize(getFitValue(24));
		//
		// time = new TextView(mcontext);
		// time.setTextColor(Color.WHITE);
		// time.setText("12:08");
		// setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		// time,
		// 40, 1090, 0, 0);
		// time.setTextSize(getFitValue(24));

		/*
		 * toplayout.addView(topbg); toplayout.addView(title);
		 */
		// toplayout.addView(time);
		// mrelayout.addView(toplayout);
	}

	/**
	 * 加载直播右下角显示的dialogview
	 */
	public void addLivePlayLayout() {
		bottomlayout = new RelativeLayout(mcontext);
		bottomlayout.setBackgroundResource(R.drawable.lb_dbbj);
		setPostion(450, 130, bottomlayout, 550, 600, 0, 0);

		txtnum = new TextView(mcontext);
		txtnum.setTextColor(Color.WHITE);
		txtnum.setText("09");
		setPostion(140, LayoutParams.WRAP_CONTENT, txtnum, 32, 28, 0, 0);
		txtnum.setTextSize(App.adjustFontSize(32));

		txttitle = new AlwaysMarqueeTextView(mcontext);
		txttitle.setTextColor(Color.WHITE);
		txttitle.setSingleLine(true);
		txttitle.setEllipsize(TruncateAt.MARQUEE);
		txttitle.alwaysRun = true;
		setPostion(180, 77, txttitle, 80, 15, 0, 0);
		txttitle.setTextSize(App.adjustFontSize(20));

		// 栏目当前节目正在播放
		txtcurt1 = new TextView(mcontext);
		txtcurt1.setTextColor(Color.parseColor("#2875c3"));
		txtcurt1.setTextSize(App.adjustFontSize(22));
		txtcurt1.setText("当前播放:");
		setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				txtcurt1, 25, 150, 0, 0);

		/*
		 * txtcurt2 = new TextView(mcontext);
		 * txtcurt2.setTextColor(Color.parseColor("#05bbff"));
		 * txtcurt2.setTextSize(getFitValue(10)); txtcurt2.setText("");
		 * setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		 * txtcurt2, 25, 200, 0, 0);
		 */

		txtcurtitle = new AlwaysMarqueeTextView(mcontext);
		txtcurtitle.setSingleLine(true);
		txtcurtitle.setEllipsize(TruncateAt.MARQUEE);
		txtcurtitle.setTextColor(Color.parseColor("#2875c3"));
		txtcurtitle.alwaysRun = true;
		setPostion(320, LayoutParams.WRAP_CONTENT, txtcurtitle, 25, 250, 0, 0);
		txtcurtitle.setTextSize(App.adjustFontSize(22));
		// 即将播放
		txtnext1 = new TextView(mcontext);
		txtnext1.setTextSize(App.adjustFontSize(22));
		txtnext1.setText("即将播放:");
		setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				txtnext1, 78, 150, 0, 0);
		/*
		 * txtnext2 = new TextView(mcontext);
		 * txtnext2.setTextSize(getFitValue(12)); txtnext2.setText("");
		 * setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		 * txtnext2, 88, 200, 0, 0);
		 */

		txtnexttitle = new AlwaysMarqueeTextView(mcontext);
		txtnexttitle.setTextColor(Color.WHITE);
		txtnexttitle.setEllipsize(TruncateAt.MARQUEE);
		txtnexttitle.setSingleLine(true);
		txtnexttitle.alwaysRun = true;

		setPostion(320, LayoutParams.WRAP_CONTENT, txtnexttitle, 78, 250, 0, 0);
		txtnexttitle.setTextSize(App.adjustFontSize(22));

		bottomlayout.addView(txtnum);
		bottomlayout.addView(txttitle);

		bottomlayout.addView(txtcurtitle);
		bottomlayout.addView(txtnexttitle);
		bottomlayout.addView(txtcurt1);
		bottomlayout.addView(txtnext1);
		// bottomlayout.addView(txtcurt2);
		// bottomlayout.addView(txtnext2);
		mrelayout.addView(bottomlayout);
	}

	/**
	 * 添加节目源资源布局
	 */
	public void addSmallBottomlayout() {
		smallBottomlayout = new RelativeLayout(mcontext);
		// smallBottomlayout.setBackgroundResource(R.drawable.play_cp_bj);
		smallBottomlayout.setBackgroundColor(Color.parseColor("#2e2e2e"));

		setPostion(120, 130, smallBottomlayout, 550, 1050, 0, 0);

		txtCP = new TextView(mcontext);
		txtCP.setTextColor(Color.WHITE);
		txtCP.setText("节目源");
		setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, txtCP,
				40, 20, 0, 0);
		txtCP.setTextSize(App.adjustFontSize(22));

		txtCPNum = new TextView(mcontext);
		txtCPNum.setTextColor(Color.WHITE);
		txtCPNum.setText("1/5");
		setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				txtCPNum, 65, 38, 0, 0);
		txtCPNum.setTextSize(App.adjustFontSize(22));

		smallBottomlayout.addView(txtCP);
		smallBottomlayout.addView(txtCPNum);
		mrelayout.addView(smallBottomlayout);

	}

	public void setTxtCPNumDisplay(String resid) {

		txtCPNum.setText(resid);
	}

	/** ----------action--------------- **/

	public void displaySeekGroup(int display) {
		bottomlayout.setVisibility(display);
	}

	public void displayBottomArea(boolean display) {
		if (display) {
			bottomlayout.setVisibility(View.VISIBLE);
		} else {

			bottomlayout.setVisibility(View.INVISIBLE);
		}

	}

	public void displaySmallBottomArea(boolean display) {
		if (display) {

			smallBottomlayout.setVisibility(View.VISIBLE);

		} else {

			smallBottomlayout.setVisibility(View.INVISIBLE);
		}

	}

	/** -----------set---------- **/
	public void getData() {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// time.setText(DateUtils.formatDate(date, DateUtils.TIME_STRING));
	}

	public void displayPauseGroup(int display) {
		centerlayout.setVisibility(display);
		if (View.VISIBLE != display) {
			txttitle.alwaysRun = false;
			txtcurtitle.alwaysRun = false;
			txtnexttitle.alwaysRun = false;
		} else {
			txttitle.alwaysRun = true;
			txtcurtitle.alwaysRun = true;
			txtnexttitle.alwaysRun = true;
		}
	}

	public void displayPauseArea(boolean display) {
		if (display) {
			AlphaAnimation alphanimation = new AlphaAnimation(0, 1);
			alphanimation.setDuration(500l);
			alphanimation.setFillAfter(true);
			centerlayout.startAnimation(alphanimation);

		} else {
			AlphaAnimation alphanimation = new AlphaAnimation(1, 0);
			alphanimation.setDuration(500l);
			alphanimation.setFillAfter(true);
			centerlayout.startAnimation(alphanimation);
		}
		if (!display) {
			txttitle.alwaysRun = false;
			txtcurtitle.alwaysRun = false;
			txtnexttitle.alwaysRun = false;
		} else {
			txttitle.alwaysRun = true;
			txtcurtitle.alwaysRun = true;
			txtnexttitle.alwaysRun = true;
		}
	}

	public int isBottomDisplay() {
		return bottomlayout.getVisibility();
	}

	public int isSmallBottomDisplay() {
		return smallBottomlayout.getVisibility();
	}

	public void setCenterText(String no, String channel, String curt,
			String next, String curprogram, String nextprgram) {
		if(no.length()>2){
			setPostion(140, LayoutParams.WRAP_CONTENT, txtnum, 32, 28, 0, 0);
		}else if(no.length() == 2){
			setPostion(140, LayoutParams.WRAP_CONTENT, txtnum, 32, 38, 0, 0);
		}else {
			setPostion(140, LayoutParams.WRAP_CONTENT, txtnum, 32, 48, 0, 0);
		}
		txtnum.setText(no);
		txtcurtitle.setText(curprogram);
		txtnexttitle.setText(nextprgram);
		try {
			if (channel.contains("黑龙江") || channel.contains("内蒙古")
					|| channel.contains("石家庄") || channel.contains("连云港")||channel.contains("央视纪录片")) {
				setPostion(180, 77, txttitle, 80, 5, 0, 0);
				channel = DateUtils.bSubstring(channel, 10);
			}else if(channel.contains("CCTV")){
				if(channel.contains("10")||channel.contains("11")||channel.contains("12")||channel.contains("13")||channel.contains("14")||channel.contains("15")){
					setPostion(180, 77, txttitle, 80, 10, 0, 0);
					channel = DateUtils.bSubstring(channel, 14);
				}else if(channel.contains("CCTV-1")||channel.contains("CCTV-2")||channel.contains("CCTV-3")||channel.contains("CCTV-4")||
						channel.contains("CCTV-5")||channel.contains("CCTV-6")||channel.contains("CCTV-7")||channel.contains("CCTV-8")||
						channel.contains("CCTV-9")){
					setPostion(180, 77, txttitle, 80, 18, 0, 0);
					channel = DateUtils.bSubstring(channel, 12);
				}else if(channel.contains("CCTV-5+")){
					setPostion(180, 77, txttitle, 80, 18, 0, 0);
					channel = DateUtils.bSubstring(channel, 14);
				}else{
					setPostion(180, 77, txttitle, 80, 5, 0, 0);
					channel = DateUtils.bSubstring(channel, 14);
				}
			}else if(channel.contains("CIBN")){
				setPostion(180, 77, txttitle, 80, 18, 0, 0);
				channel = DateUtils.bSubstring(channel, 12);
			}else{
			setPostion(180, 77, txttitle, 80, 18, 0, 0);
			channel = DateUtils.bSubstring(channel, 8);
			} 
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		txttitle.setText(channel);
	}

	/** ----------private--------------- **/
	private int getFitValue(int value) {
		return DisplayUtil.getDisplayValue(value, (Activity) mcontext);
	}

	private void setPostion(int width, int height, View v, int top, int left,
			int right, int bottom) {
		RelativeLayout.LayoutParams lp = new LayoutParams(getFitValue(width),
				getFitValue(height));
		lp.setMargins(getFitValue(left), getFitValue(top), getFitValue(right),
				getFitValue(bottom));
		v.setLayoutParams(lp);
	}

}
