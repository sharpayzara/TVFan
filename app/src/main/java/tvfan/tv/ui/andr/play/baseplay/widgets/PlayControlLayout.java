package tvfan.tv.ui.andr.play.baseplay.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.lib.DateUtils;
import tvfan.tv.lib.DisplayUtil;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.andr.widgets.NofocusMarqueeTextView;
import tvfan.tv.ui.andr.widgets.NumberSeekBar;

/**
 * @since 2015 04/19
 * @author sadshine
 * 
 */
public class PlayControlLayout {
	private Context mcontext;
	// 顺序依次:1.父容器 2.顶部菜单容器 3.底部菜单容器 4中间快进快退容器,5暂停布局
	private RelativeLayout mrelayout, toplayout, bottomlayout, centerlayout,suspendlayout;
	private ImageView topbg, pause,  bottombg, imgbg, bottombg2,suspendbg, suspendLogo;
//	private Button forward,backward,exitbutton;
	private TextView time, cutime, totaltime,title2;
	//private NofocusMarqueeTextView title;
	private TextView title;
	private NumberSeekBar seekbar;
	public static boolean centerArea_state = false;
	public static boolean toplayout_state = false;
	public static boolean bottomlayout_state = false;
	public static boolean suspendlayout_state = false;
	public PlayControlLayout(Context context, RelativeLayout relayout) {
		mcontext = context;
		mrelayout = relayout;
	}
	
	/**
	 * 添加播放器时间标题资源布局
	 */
	public void addPlayTopLayout() {

		toplayout = new RelativeLayout(mcontext);
		toplayout.setFocusable(false);
		topbg = new ImageView(mcontext);
		imgbg = new ImageView(mcontext);
		imgbg.setBackgroundColor(Color.BLACK);
		imgbg.setAlpha(0.7f);
		/*setPostion(1280, 135, imgbg, 0, 0, 0, 0);
		setPostion(1280, 135, topbg, 0, 0, 0, 0);*/
		setPostion(1280, 80, imgbg, 0, 0, 0, 0);
		setPostion(1280, 80, topbg, 0, 0, 0, 0);
//     顶部布局主标题
		title = new NofocusMarqueeTextView(mcontext);
		title.setTextColor(Color.WHITE);
		title.setSingleLine(true);
		title.setSelected(true);
		setPostion(900, 50, title,
				20, 80, 0, 0);
		title.setTextSize(App.adjustFontSize(30));
		/*//顶部布局副标题
		title2 = new AlwaysMarqueeTextView(mcontext);
		title2.setTextColor(Color.GRAY);
		title2.setSingleLine(true);
		title2.setEllipsize(TruncateAt.MARQUEE);
		setPostion(280, LayoutParams.WRAP_CONTENT, title2,
				70, 80, 0, 0);
		title2.setTextSize(getFitValue(20));
		title2.setText("测试用副标题");*/
		//顶部布局时间
		time = new TextView(mcontext);
		time.setTextColor(ColorStateList.valueOf(0xfff3f2f2));
		time.setText("");
		setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, time,
				20, 1120, 0, 0);
		time.setTextSize(App.adjustFontSize(30));

		toplayout.addView(imgbg);
		toplayout.addView(topbg);
		toplayout.addView(title);
		toplayout.addView(time);
		mrelayout.addView(toplayout);
	}

	
	


	/**
	 * 添加播放器暂停布局
	 */
	public void addPlaySuspendLayout() {
		suspendlayout = new RelativeLayout(mcontext);
		suspendlayout.setFocusable(false);
		suspendlayout.setBackgroundColor(Color.BLACK);
		suspendlayout.setAlpha(0.8f);
		setPostion(1920, 1080, suspendlayout, 0, 0, 0, 0);
		suspendbg = new ImageView(mcontext);
		suspendbg.setBackgroundResource(R.drawable.play_suspend);
		setPostion(500, 282, suspendbg, 155, 390, 0, 0);
		suspendlayout.addView(suspendbg);

		suspendLogo =  new ImageView(mcontext);
		suspendLogo.setBackgroundResource(R.mipmap.suspended);
		//setPostion(148, 125, suspendLogo, 425, 571, 0, 0);
		setPostion(111, 94, suspendLogo, 465, 589, 0, 0);
		suspendlayout.addView(suspendLogo);

		mrelayout.addView(suspendlayout);
	}
	

	/**
	 * 添加播放器进度条资源
	 */
	public void addPlaySeekLayout() {
		AlphaAnimation alnimation = new AlphaAnimation(0.8f, 0.8f);
		bottomlayout = new RelativeLayout(mcontext);
		bottombg2 = new ImageView(mcontext);
		bottombg = new ImageView(mcontext);
		bottombg.setBackgroundColor(Color.BLACK);
		bottombg.setAlpha(0.7f);
		setPostion(1280, 220, bottombg, 570, 0, 0, 0);
		setPostion(1280, 220, bottombg2, 570, 0, 0, 0);
		bottomlayout.addView(bottombg2);
		
		seekbar = new NumberSeekBar(mcontext);

		Drawable progressdrw = mcontext.getResources().getDrawable(
				R.drawable.numberseekbar_background);
		seekbar.setProgressDrawable(progressdrw);
		Drawable thumndrw = mcontext.getResources().getDrawable(
				R.drawable.thumb_bar);
		seekbar.setThumb(thumndrw);
//		seekbar.setMinimumHeight(App.adjustFontSize(1));
		seekbar.setMax(100);
		MPlayRecordInfo recordInfo = new MPlayRecordInfo();
		/*if(recordInfo != null){
			DateFormat df = new SimpleDateFormat("HH:mm:ss");
			seekbar.setCurrtime(df.format(recordInfo.getPonitime()));
			seekbar.setProgress(45);
		}else{
			
		}12.02*/
		seekbar.setProgress(0);
		//设置seekbar文本的pading
		seekbar.setTextPadding(1,1);
		
		//设置自定义nuberseekbar的显示效果
		seekbar.setMyPadding(getFitValue(1), getFitValue(50), getFitValue(5),
				getFitValue(60));
		seekbar.setFocusable(true);
		setPostion(850, 220, seekbar, 550,250, 0, 0);
		//// 当前时间
		cutime = new TextView(mcontext);
		cutime.setFocusable(false);
		cutime.setTextColor(Color.WHITE);
		cutime.setText("00:00:00");
		alnimation.setFillAfter(true);
		cutime.startAnimation(alnimation);
		setPostion(150, 40,
				cutime, 625,170, 0, 0);
		cutime.setTextSize(App.adjustFontSize(30));
		// 总时间
		totaltime = new TextView(mcontext);
		totaltime.setFocusable(false);
		totaltime.setTextColor(ColorStateList.valueOf(0xfff3f2f2));
		totaltime.setText("00:00:00");
		alnimation.setFillAfter(true);
		totaltime.startAnimation(alnimation);
		setPostion(150, 40,
				totaltime, 625, 1080, 0, 0);
		totaltime.setTextSize(App.adjustFontSize(30));
		//播放暂停控件
		pause = new ImageView(mcontext);
		pause.setBackgroundResource(R.drawable.pause);
		pause.setFocusable(false);
		setPostion(70, 70, pause, 605, 60, 0, 0);
		// 添加控件
		bottomlayout.addView(bottombg);
		bottomlayout.addView(seekbar);
		bottomlayout.addView(cutime);
		bottomlayout.addView(totaltime);
		bottomlayout.addView(pause);
		mrelayout.addView(bottomlayout);
	}

	/** ----------action--------------- **/
	public void displayTopGroup(int display) {
		if (toplayout == null)
			return;
		toplayout.setVisibility(display);
	}
	
	public void displayTopBg(int display) {
		if (topbg == null)
			return;
		topbg.setVisibility(display);
	}

	public void displaySuspendlayout(int display) {
		if (suspendlayout == null)
			return;
		suspendlayout.setVisibility(display);
		if(display == View.VISIBLE){
			suspendlayout_state = true;
		}else{
			suspendlayout_state = false;
		}
	}

	public void displaySeekGroup(int display) {
		if (bottomlayout == null)
			return;
		bottomlayout.setVisibility(display);
		if(display == View.VISIBLE){
			bottomlayout_state = true;
		}else{
			bottomlayout_state = false;
		}
	}

	public void displayTopArea(boolean display) {
		if (toplayout == null)
			return;
		if (display) {
			//设置顶部布局和底部布局弹出动画 -110->0
			TranslateAnimation tranimation = new TranslateAnimation(0, 0,
					getFitValue(-110), 0);
			tranimation.setDuration(500l);
			toplayout.setVisibility(View.VISIBLE);
			tranimation.setAnimationListener(showanimation);
			toplayout.startAnimation(tranimation);
			//改变view的z轴，使其处在他父view的顶端;
//			将这个toplayout从父view中移除，然后再加入父view的顶端
			//常用来处理动画中的view动作
			toplayout.bringToFront();
			toplayout_state=true;
		} else {
			//执行缩回动画
			TranslateAnimation tranimation = new TranslateAnimation(0, 0, 0,
					getFitValue(-110));
			tranimation.setDuration(500l);
			tranimation.setAnimationListener(hideanimation);
			toplayout.startAnimation(tranimation);
			toplayout_state=false;
		}
	}

	public void displayInfo(boolean b){
		if (b){
			toplayout.setVisibility(View.VISIBLE);
			bottomlayout.setVisibility(View.VISIBLE);
			toplayout_state=true;
			bottomlayout_state = true;
		}else{
			toplayout.setVisibility(View.INVISIBLE);
			bottomlayout.setVisibility(View.INVISIBLE);
			toplayout_state=false;
			bottomlayout_state = false;
		}
	}
	
	/**
	 * 设置显示底部区域的方法
	 * @param display
	 */
	public void displayBottomArea(boolean display) {
		if (bottomlayout == null)
			return;
		if (display) {

			TranslateAnimation tranimation = new TranslateAnimation(0, 0,
					getFitValue(720), getFitValue(0));
			tranimation.setDuration(500l);
			bottomlayout.setVisibility(View.VISIBLE);
			tranimation.setAnimationListener(showanimation);
			bottomlayout.startAnimation(tranimation);
			bottomlayout.bringToFront();
			//当底部区域弹出时,seekbar自动获取焦点
			seekbar.requestFocus();
			bottomlayout_state = true;
		} else {

			TranslateAnimation tranimation = new TranslateAnimation(0, 0,
					getFitValue(0), getFitValue(720));
			tranimation.setDuration(500l);
			tranimation.setAnimationListener(hideanimation);
			bottomlayout.startAnimation(tranimation);
			bottomlayout.setVisibility(View.INVISIBLE);
			bottomlayout_state = false;
		}

	}


	/**
	 * 设置点播界面隐藏动画的监听
	 */
	AnimationListener hideanimation = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation arg0) {
			bottomlayout.setVisibility(View.VISIBLE);
			toplayout.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			bottomlayout.setVisibility(View.INVISIBLE);
			toplayout.setVisibility(View.INVISIBLE);
		}
	};
	AnimationListener showanimation = new AnimationListener() {
//对于showanimation这个动画监听而言,当动画开始和结束的时候,把底部控件和顶部控件做显示操作
		@Override
		public void onAnimationStart(Animation arg0) {
			if (bottomlayout != null) {
				bottomlayout.setVisibility(View.VISIBLE);
			}
			if (toplayout != null) {
				toplayout.setVisibility(View.VISIBLE);
			}
		}
		//当动画重复执行时调用
		@Override
		public void onAnimationRepeat(Animation arg0) {
		}
		//动画结束时调用
		@Override
		public void onAnimationEnd(Animation arg0) {
			if (bottomlayout != null) {
				bottomlayout.setVisibility(View.VISIBLE);
			}
			if (toplayout != null) {
				toplayout.setVisibility(View.VISIBLE);
			}
		}
	};

	/** -----------set---------- **/

	/**
	 * 设置暂停按钮的点击事件监听
	 * @param onclick
	 */
	public void setPauseImgListener(OnClickListener onclick) {
		pause.setOnClickListener(onclick);
	/*	//定义上下搜索时获得焦点的id
		pause.setNextFocusDownId(seekbar.getId());
		pause.setNextFocusUpId(seekbar.getId());*/
	}

	/**
	 * 设置底部布局按钮焦点转移
	 */
	public void setSingelPauseBtnNextFocus() {
		pause.setNextFocusDownId(seekbar.getId());
		pause.setNextFocusUpId(seekbar.getId());
	}

	/**
	 * 设置当前时间的显示
	 * @param currentime
	 */
	public void setPlayCurrentTime(int currentime) {
		this.cutime.setText(Utils.millisToString(currentime));
		this.cutime.invalidate();
		//有时我们在改变一个view 的内容之后 可能会造成显示出现错误，
//		比如写ListView的时候 重用convertview中的某个TextView 可能因为前后填入的text长度不同而造成显示出错，
//		此时我们可以在改变内容之后调用requestLayout方法加以解决。
		this.cutime.requestLayout();
	}

	/**
	 * 设置顶部布局的主标题
	 * @param strTitle
	 */
	public void setTitle(String strTitle) {
		title.setText(strTitle);
		title.invalidate();
		title.requestLayout();
	}

	/**
	 * 设置总时间的显示
	 * @param totaltime
	 */
	public void setPlayTotalTime(long totaltime) {
		// wanqi，大于10分钟的就30sseek
		if (totaltime > 10 * 60 * 1000l) {
			seekbar.setMax((int) (totaltime / 30000));
			//设置方向键改变后的进度值,参数:当用户按下方向键后减少或增加之后的进度值。这个进度值是正数
			seekbar.setKeyProgressIncrement(1);
		} else {
			seekbar.setMax(100);
		}
		this.totaltime.setText(Utils.millisToString(totaltime));
		this.totaltime.invalidate();
		this.totaltime.requestLayout();
	}

	/**
	 * 设置seekbar的焦点
	 * @param isable ,true,seekbar获取焦点;false,seekbar失去焦点
	 */
	public void setSeekFocusAble(boolean isable) {
		this.seekbar.setFocusable(isable);
		this.seekbar.requestFocus();
		
	}

	/**
	 * 设置自定义seekbar的时间显示
	 * @param currentime
	 * @param progress
	 */
	public void setSeekTime(int currentime, int progress) {
		this.seekbar.setCurrtime(Utils.millisToString(currentime));
		this.seekbar.setProgress(progress);
		this.seekbar.invalidate();
		
	}

	public void setSeekTime(int currentime) {
		this.seekbar.setCurrtime(Utils.millisToString(currentime));
		this.seekbar.invalidate();
		this.seekbar.requestLayout();
	}

	/**
	 * 设置seekbar的监听
	 * @param onseekbarChanged
	 */
	public void setSeekOnlistener(OnSeekBarChangeListener onseekbarChanged) {
		this.seekbar.setOnSeekBarChangeListener(onseekbarChanged);
	}

	/** -----------get---------- **/
	/**
	 * 获取进度条的当前进度
	 * @return
	 */
	public int getProgress() {
		return this.seekbar.getProgress();
	}

	/**
	 * 获取当前底部布局的显示状态
	 * @return final int VISIBLE = 0x00000000
	 * final int INVISIBLE = 0x00000004
	 * final int GONE = 0x00000008
	 */
	public int isBottomDisplay() {
		return bottomlayout.getVisibility();
	}
	
	public View getSeekbar(){
		return seekbar;
	}
	/**
	 * 获取时间数据的方法
	 */
	public void getData() {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		time.setText(DateUtils.formatDate(date, DateUtils.TIME_STRING));
	}

	/** ----------private--------------- **/
	/**
	 * 根据分辨率适值配的方法
	 * @param value
	 * @return
	 */
	private int getFitValue(int value) {
		return DisplayUtil.getDisplayValue(value, (Activity) mcontext);
	}
	/**
	 * 经过适配处理的设置控件位置的方法
	 * @param width   控件大小
	 * @param height   控件高度
	 * @param v       目标控件
	 * @param top		top值
	 * @param left      left值
	 * @param right		right值
	 * @param bottom	bottom值
	 */
	private void setPostion(int width, int height, View v, int top, int left,
			int right, int bottom) {
		LayoutParams lp = new LayoutParams(getFitValue(width),
				getFitValue(height));
		lp.setMargins(getFitValue(left), getFitValue(top), getFitValue(right),
				getFitValue(bottom));
		v.setLayoutParams(lp);
	}

	/**
	 * 获取暂停按钮的方法
	 * @return
	 */
	public ImageView getPause() {
		return pause;
	}
	/**
	 * 清空所有的图片资源
	 */
	public void clearAllImages() {
		topbg = null;
		pause = null;
		bottombg = null;
		System.gc();
	}
	
	/**
	 * 获取进度条最大值的方法
	 * @return
	 */
	public int getSeekBarMax() {
		if (seekbar == null) {
			return 100;
		}
		return seekbar.getMax();
	}
	public Boolean getTopLayoutState(){
		
		return toplayout_state;
	}
	public Boolean getBottomLayoutState(){
		return bottomlayout_state;
	}
	
	/**
	 * 获取中间布局的方法
	 * @return
	 */
	public View getCenterLayout(){
		return centerlayout;
	}
	
	/**
	 * 获取暂停布局的方法
	 * @return
	 */
	public Boolean getSuspendLayoutState(){
		return suspendlayout_state;
	}

}
