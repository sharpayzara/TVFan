package tvfan.tv.ui.andr.play.baseplay.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.EntryPoint;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.ProgramList;
import tvfan.tv.lib.DisplayUtil;


public class ExitDialogView extends Dialog {
	// 父容器,收藏容器,分辨率容器,视频比例容器,剧集容器
	private RelativeLayout mrelayout, favlayout, distinlayout, ratiolayout,
			dramalayout;
	private GridView favgidlist;
	private Button lastbtn,nextbtn,exitbtn;
	private TextView favTitle;
	private Context mcontext;
	private int iprepos;
	private Handler timerhd;
	private String movename = "";
	private String programid = "";
	private ArrayList<ProgramList> prolist;
	private int h;
	public ExitDialogView(Context context, int theme,String id,String mname) {
		super(context, theme);
		mcontext = context;
		timerhd = new Handler();
		movename = mname;
		programid = id;
		if(context.getResources().getDisplayMetrics().densityDpi >=250){
			h = 30;
		}else{
			h = 25;
		}
		Log.d("index","当前设备的密度值为"+context.getResources().getDisplayMetrics().densityDpi);
		initLayout();
		setContentView(mrelayout);
		init();
	}

	private void initLayout() {
		mrelayout = new RelativeLayout(mcontext);
		setPostion(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT, mrelayout, 0, 0, 0, 0);
//		mrelayout.setBackgroundResource(R.drawable.background_shadow);
		mrelayout.setBackgroundColor(Color.parseColor("#aa000000"));
	}

	public void init(){
		addButton();
		addLine();
		requestRankListDate();
		/*final ArrayList<ProgramList> prolist = new ArrayList<ProgramList>();
		ProgramList programlist = new ProgramList();
		programlist.setId("007");
		programlist.setPostImg("http://images.ott.cibntv.net/2015/04/27/yugaopianhunjian0427.jpg#520*300");
		prolist.add(programlist);
		prolist.add(programlist);
		prolist.add(programlist);
		prolist.add(programlist);
		prolist.add(programlist);

		addFavList(prolist);*/
	}

	//add 功能按钮
	public void addButton(){
		lastbtn = new Button(mcontext);
		lastbtn.setBackgroundResource(R.drawable.exitdialog_backbtn_selector);
		lastbtn.setTextColor(Color.WHITE);
		lastbtn.setGravity(Gravity.CENTER);
		lastbtn.setTextSize(App.adjustFontSize(18));

		nextbtn = new Button(mcontext);
		nextbtn.setBackgroundResource(R.drawable.exitdialog_nextbtn_selector);
		nextbtn.setTextColor(Color.WHITE);
		nextbtn.setGravity(Gravity.CENTER);
		nextbtn.setTextSize(App.adjustFontSize(18));

		exitbtn = new Button(mcontext);
		exitbtn.setBackgroundResource(R.drawable.exitdialog_exitbtn_selector);
		exitbtn.setTextColor(Color.WHITE);
		exitbtn.setGravity(Gravity.CENTER);
		exitbtn.setTextSize(App.adjustFontSize(18));
		exitbtn.setFocusable(true);
		exitbtn.requestFocus();
		mrelayout.addView(lastbtn);
		mrelayout.addView(nextbtn);
		mrelayout.addView(exitbtn);
	}

	// 划线
	public void addLine(){
		ImageView imgline1 = new ImageView(mcontext);
		imgline1.setBackgroundResource(R.drawable.juji_line);
		setPostion(800, 1, imgline1, 278, 320, 0, 0);
		//mrelayout.addView(imgline1);
		favgidlist = new GridView(mcontext);
	}

	// add gridview
	TextView txtview;
	RelativeLayout rl_scorebj,rl_detailbj;
	public void addFavList(final ArrayList<ProgramList> lst) {
		favTitle = new TextView(mcontext);
		favTitle.setText("相关推荐");
		favTitle.setTextSize(App.adjustFontSize(21));
		favTitle.setTextColor(Color.WHITE);
		setPostion(166, 60, favTitle, 188, 120, 0, 0);
		final ExitListAdapter distinadpater = new ExitListAdapter(mcontext,
				lst, "",this);
		distinadpater.notifyDataSetChanged();
		favgidlist.setSelector(R.drawable.grid_item_selector);
		favgidlist.setAdapter(distinadpater);
		favgidlist.setHorizontalSpacing(DisplayUtil.getDisplayValue(12, (Activity) mcontext));
		favgidlist.setDrawSelectorOnTop(true);
		favgidlist.setVerticalScrollBarEnabled(false);
		favgidlist.setGravity(Gravity.CENTER);
		favgidlist.setNumColumns(7);
		favgidlist.setFocusable(false);
		favgidlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(mcontext, EntryPoint.class);
				intent.putExtra(AppGlobalConsts.INTENT_ACTION_NAME, "OPEN_DETAIL");
				intent.putExtra("programSeriesId", lst.get(position).getId());
				mcontext.startActivity(intent);
				((Activity) mcontext).finish();

			}
		});
		favgidlist.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> v, View view,
									   int arg2, long arg3) {
				txtview = (TextView) view.findViewById(R.id.textView);
				rl_scorebj = (RelativeLayout) view.findViewById(R.id.rl_scorebj);
				rl_detailbj = (RelativeLayout) view.findViewById(R.id.rl_detailbj);
				txtview.setEllipsize(TruncateAt.MARQUEE);
				if (distinadpater != null) {
					distinadpater.setSelector(arg2);
					distinadpater.notifyDataSetChanged();
				}
				startZoomAni(view);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				if (txtview != null)
					txtview.setEllipsize(TruncateAt.END);
				/*if (rl_scorebj != null) {

					rl_scorebj.setBackgroundColor(Color.parseColor("#222222"));
				}
				if (rl_detailbj != null) {

					rl_detailbj.setBackgroundColor(Color.parseColor("#222222"));
				}*/
//				startShrinkAni(arg0);
			}
		});
//这个监听是焦点在list和button之间转移时才被调用
		favgidlist.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (txtview != null) {
						txtview.setEllipsize(TruncateAt.END);
					}
					if (rl_scorebj != null) {

						rl_scorebj.setBackgroundColor(Color.parseColor("#2e94e8"));
					}
					if (rl_detailbj != null) {

						rl_detailbj.setBackgroundColor(Color.parseColor("#2e94e8"));
					}

				} else {
					if (rl_scorebj != null) {

						rl_scorebj.setBackgroundColor(Color.parseColor("#222222"));
					}
					if (rl_detailbj != null) {

						rl_detailbj.setBackgroundColor(Color.parseColor("#222222"));
					}

				}
			}
		});
		setPostion(1100, 400, favgidlist, 255, 120, 0, 0);

		mrelayout.addView(favgidlist);
		mrelayout.addView(favTitle);
	}

	public void setGridOnItemClickListener(OnItemClickListener sourceItemLinstener){
		favgidlist.setOnItemClickListener(sourceItemLinstener);
	}
	public void setGridonitemselectListener(OnItemSelectedListener onitemselectListener){
		favgidlist.setOnItemSelectedListener(onitemselectListener);
	}

	public void setDisplayLastAndForwardBtn(int display){
		lastbtn.setVisibility(display);
		nextbtn.setVisibility(display);
	}

	public void setDisplayLastBtn(int display){
		lastbtn.setVisibility(display);
	}

	public void setDisplayForwardBtn(int display){
		nextbtn.setVisibility(display);
	}

	public void setDisplay(int display) {
		mrelayout.setVisibility(display);
	}

	public int getDisplay() {
		return mrelayout.getVisibility();
	}

	public void setLastBtnOnclick(android.view.View.OnClickListener onclick){
		lastbtn.setOnClickListener(onclick);
	}

	public void setNextBtnOnclick(android.view.View.OnClickListener onclick){
		nextbtn.setOnClickListener(onclick);
	}

	public void setExitBtnOnclick(android.view.View.OnClickListener onclick){
		exitbtn.setOnClickListener(onclick);
	}

	/** ----------private--------------- **/
	private int getFitValue(int value) {
		return DisplayUtil.getDisplayValue(value, (Activity) mcontext);
	}

	private int getFitHeight(int value){
		return DisplayUtil.getDisplayHeight(value, (Activity) mcontext);
	}

	private void setPostion(int width, int height, View v, int top, int left,
							int right, int bottom) {
		LayoutParams lp = new LayoutParams(getFitValue(width),
				getFitHeight(height));
		lp.setMargins(getFitValue(left), getFitValue(top), getFitValue(right),
				getFitValue(bottom));
		v.setLayoutParams(lp);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case 178:
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				favgidlist.setFocusable(true);
				break;

		}
		return super.onKeyDown(keyCode, event);
	}
	public void setOnItemClick(OnItemClickListener onitemonclick){
		if(favgidlist!=null){
			favgidlist.setOnItemClickListener(onitemonclick);
		}

	}
	/**
	 * 切换btn的位子
	 * itype 0, 1
	 */
	public void setChangeBtnPosition(int itype){
		switch(itype){
			case 0:
				setPostion(150, h, exitbtn, 530, 120, 0, 0);
				setPostion(150, h, nextbtn, 530, 298, 0, 0);
				break;
			case 1:
				setPostion(150, h, lastbtn, 530, 120, 0, 0);
				setPostion(150, h, nextbtn, 530, 474, 0, 0);
				setPostion(150, h, exitbtn, 530, 298, 0, 0);
				break;
		}
	}

	private void hideTimerAction(){
		if(timerhd!=null){
			timerhd.removeCallbacks(timerrb);
		}

		timerhd.postDelayed(timerrb, 8000l);
	}

	Runnable timerrb = new Runnable() {

		@Override
		public void run() {
			try {
				ExitDialogView.this.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public void removeAllHandler(){
		timerhd.removeCallbacks(timerrb);
	}

	private void requestRankListDate() {
		RemoteData remoteData = new RemoteData(mcontext);
		remoteData.getDetailRelatedRecommend(programid, movename, new HttpResponse.Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response == null || !response.has("programList")) {

					return;
				}
				prolist = new ArrayList<ProgramList>();
				try {

					JSONArray jsonarr = response.getJSONArray("programList");
					int length = jsonarr.length();
					if (length > 7) {
						length = 7;
					}
					for (int i = 0; i < length; i++) {
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						ProgramList programlist = new ProgramList();
						programlist.setId(jsonobject.getString("id"));
						programlist.setPostName(jsonobject.getString("name"));
						programlist.setPostImg(jsonobject.getString("image"));
						programlist.setCurrentNum(jsonobject.getString("currentNum"));
						programlist.setScore(jsonobject.getString("score"));
						prolist.add(programlist);
					}

					if (prolist.size() > 0) {
						new Handler().post(new Runnable() {

							@Override
							public void run() {
								addFavList(prolist);

							}
						});
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onError(String errorMessage) {
				// TODO Auto-generated method stub

			}
		});

	}

	public Button getLastbtn() {
		return lastbtn;
	}

	public Button getNextbtn() {
		return nextbtn;
	}

	public Button getExitbtn() {
		return exitbtn;
	}

	public ArrayList<ProgramList> getProlist() {
		return prolist;
	}

	/*public void showDialog(){
		if(exitbtn!=null){

			exitbtn.requestFocus();
		}
		show();
	}*/

	@Override
	public void show() {
		if(exitbtn!=null){

			exitbtn.requestFocus();
		}
		super.show();
	}

	public boolean getGridFocus(){
		return favgidlist.hasFocus();
	}

	/**
	 * 执行放大动画
	 * @param view
	 */
	public void startZoomAni(final View view){
		//初始化
		Animation zoomAnimation = new ScaleAnimation(1.0f, 1.1f,1.0f,1.1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		//设置动画时间
		zoomAnimation.setDuration(500);
		//设置停留在动画结束位置
//		zoomAnimation.setFillAfter(true);
		view.startAnimation(zoomAnimation);
/*
		ObjectAnimator anim = ObjectAnimator//
				.ofFloat(view, "zhy", 1.0F,  1.1F)//
				.setDuration(500);//
		anim.start();
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				float cVal = (Float) animation.getAnimatedValue();
//				view.setAlpha(cVal);
				view.setScaleX(cVal);
				view.setScaleY(cVal);
			}
		});*/
	}

	/**
	 * 执行缩小动画
	 * @param view
	 */
	public void startShrinkAni(View view){
		//初始化
		Animation shrinkAnimation = new ScaleAnimation(1.1f, 1.0f,1.1f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		//设置动画时间
		shrinkAnimation.setDuration(500);
		shrinkAnimation.setFillAfter(true);
		view.startAnimation(shrinkAnimation);
	}
}
