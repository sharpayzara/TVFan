package tvfan.tv.ui.andr.play.baseplay.widgets;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.dal.models.LiveItemBean;
import tvfan.tv.lib.DisplayUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import tvfan.tv.App;
import tvfan.tv.R;

public class LiveListAdapter extends BaseAdapter{
	private int iSelector = 0;
	private int iPreSelector = 0;
	private Context mcontext;
	private List<LiveItemBean> msourcelst;
	private String mtype = "";	
	private String programId = "";
	public LiveListAdapter(Context context,ArrayList<LiveItemBean>lst,String type){
		mcontext = context;
		msourcelst = lst;
		mtype = type;
	}

	
	public void setProgramId(String programId) {
		this.programId = programId;
	}


	public void setSelector(int i) {
		iSelector = i;

	}

	public int getSelector() {
		return iSelector;
	}

	public int getPreSelector() {
		return iPreSelector;
	}

	@Override
	public int getCount() {
		return msourcelst.size();
	}

	@Override
	public Object getItem(int arg0) {
		return msourcelst.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		View view = null;
		ViewHolder holder;
		if (mcontext == null)
			return null;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			if(mtype.equals("line")){
				view = View.inflate(mcontext, R.layout.live_seriesline_item,
						null);
			}else{
				view = View.inflate(mcontext, R.layout.live_series_item,
						null);
			}
			holder.rootlayout = (LinearLayout)view.findViewById(R.id.rootLayout);
			holder.textView = (TextView) view.findViewById(R.id.textView);			
			//初始化布局			
			if(mtype.equals("three")){//三级
				
				holder.textChannel = (TextView) view.findViewById(R.id.textcurchanel);
				holder.textView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.getDisplayValue(350, (Activity) mcontext)
						, LinearLayout.LayoutParams.WRAP_CONTENT));
				holder.textView.setGravity(Gravity.LEFT);
				holder.textChannel.setVisibility(View.VISIBLE);
				
				holder.textChannel.setTextColor(Color.WHITE);
				holder.textChannel.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.getDisplayValue(350, (Activity) mcontext)
						, LinearLayout.LayoutParams.WRAP_CONTENT));
				holder.textChannel.setGravity(Gravity.LEFT);
				holder.rootlayout.setPadding(DisplayUtil.getDisplayValue(24, (Activity) mcontext), 0, 0, 0);
				holder.rootlayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				
				
			}else if(mtype.equals("line")){//二级
				holder.textNum = (TextView) view.findViewById(R.id.textnum);
				holder.textChannel = (TextView) view.findViewById(R.id.textcurchanel);
				holder.textNum.setTextSize(App.adjustFontSize(25));				
//				holder.imgView = (ImageView)view.findViewById(R.id.lineimg);
//				holder.imgView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.getDisplayValue(2, (Activity) mcontext)
//						, DisplayUtil.getDisplayValue(45, (Activity) mcontext)));
				holder.textNum.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.getDisplayValue(45, (Activity) mcontext)
						, DisplayUtil.getDisplayValue(45, (Activity) mcontext)));
				LinearLayout.LayoutParams txtlp = new LinearLayout.LayoutParams(DisplayUtil.getDisplayValue(160, (Activity) mcontext)
						, LinearLayout.LayoutParams.WRAP_CONTENT);
				txtlp.leftMargin = DisplayUtil.getDisplayValue(2, (Activity) mcontext);
				LinearLayout.LayoutParams txtlp2 = new LinearLayout.LayoutParams(DisplayUtil.getDisplayValue(160, (Activity) mcontext)
						, LinearLayout.LayoutParams.WRAP_CONTENT);
				txtlp2.leftMargin = DisplayUtil.getDisplayValue(30, (Activity) mcontext);
				holder.textView.setLayoutParams(txtlp);
				holder.textChannel.setLayoutParams(txtlp2);
				holder.textChannel.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);				
				holder.rootlayout.setPadding(DisplayUtil.getDisplayValue(15, (Activity) mcontext), 0
						, DisplayUtil.getDisplayValue(12, (Activity) mcontext), 0);
				holder.rootlayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
			}else{//一级
				holder.textView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.getDisplayValue(200, (Activity) mcontext)
						, LinearLayout.LayoutParams.WRAP_CONTENT));
				holder.textView.setGravity(Gravity.CENTER);

			}
						
			view.setTag(holder);
			final View view2 = view;
			view.post(new Runnable() {

				@Override
				public void run() {
					if (mcontext == null)
						return;
					float height = DisplayUtil.getDisplayValue(85, (Activity) mcontext);
					AbsListView.LayoutParams params = new AbsListView.LayoutParams(
							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							(int) height);// xml中设置高度不起作用，这里强制设置一下
					view2.setLayoutParams(params);
				}
			});
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		String str = "";
		String substr = "";
		str =  msourcelst.get(arg0).getTitle();
		if(mtype.equals("line")){
			//设置二级菜单内容显示
			substr =  msourcelst.get(arg0).getSubtitle();
			if(msourcelst!=null
					&&msourcelst.size()>0
					&&msourcelst.size()>arg0
					&&msourcelst.get(arg0).getNo()!=null
					&&!msourcelst.get(arg0).getNo().equals("")
					&&Integer.parseInt(msourcelst.get(arg0).getNo())>0){
			
				holder.textNum.setText(String.valueOf(msourcelst.get(arg0).getNo()));
				
			}else{
				if(arg0<9){
					holder.textNum.setText("0"+String.valueOf(arg0+1));
				}else{
					holder.textNum.setText(String.valueOf(arg0+1));
				}
			}
		}else if(mtype.equals("three")){
			
			
			substr =  msourcelst.get(arg0).getStartTime();
			String canid = msourcelst.get(arg0).getChannealid();
			if(msourcelst.get(arg0).getChannealid().equals(programId)){
				
				//holder.textView.setTextColor(Color.parseColor("#04bbff"));
				holder.textView.setTextColor(Color.RED);
				holder.textChannel.setTextColor(Color.RED);
			}else{
				holder.textView.setTextColor(Color.WHITE);
				holder.textChannel.setTextColor(Color.WHITE);
			}
		}
		
		if (iSelector != arg0) {
			holder.textView.setAlpha(0.7f);
			holder.rootlayout.setBackgroundColor(Color.TRANSPARENT);
			holder.textView.setTextSize(App.adjustFontSize(25));
		} else {
			holder.textView.setAlpha(1f);
			holder.textView.setTextSize(App.adjustFontSize(25));
			if(!(mtype.equals("none")||mtype.equals("three"))){
				holder.rootlayout.setBackgroundResource(R.drawable.lb_left2foucs2);
			}
			if(mtype.equals("three")){
				holder.textView.setTextColor(Color.WHITE);
				holder.textChannel.setTextColor(Color.WHITE);
			}
			
			iPreSelector = iSelector;
		}
		holder.textView.setText(str);
		if(holder.textChannel!=null){
			
			holder.textChannel.setText(substr);
			holder.textChannel.setTextSize(App.adjustFontSize(25));
			if(mtype.equals("three")){
				if(msourcelst.get(arg0).getChannealid().equals(programId)){
					holder.textView.setText("正在播："+str);
				}				
			}
		}

		return view;
	}
	private class ViewHolder {
		TextView textNum;
		TextView textView;
		TextView textChannel;
		ImageView imgView;
		LinearLayout rootlayout;
	}
	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}
	
	
}
