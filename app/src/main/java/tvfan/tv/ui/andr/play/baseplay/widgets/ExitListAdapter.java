package tvfan.tv.ui.andr.play.baseplay.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.dal.models.ProgramList;
import tvfan.tv.lib.AnrImageUtils;
import tvfan.tv.lib.DisplayUtil;

public class ExitListAdapter extends BaseAdapter{
	private int iSelector = 0;
	private int iPreSelector = 0;
	private Context mcontext;
	private List<ProgramList> msourcelst;
	private String mtype = "";
	private ExitDialogView exitDialogView;
	public ExitListAdapter(Context context,ArrayList<ProgramList>lst,String type,ExitDialogView exitDialogView){
		mcontext = context;
		msourcelst = lst;
		mtype = type;
		this.exitDialogView = exitDialogView;
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
			view = View.inflate(mcontext, R.layout.player_grid_item,
					null);
			holder = new ViewHolder();
			holder.imgView = (ImageView)view.findViewById(R.id.postimg);
			holder.textView = (TextView) view.findViewById(R.id.textView);
			holder.tv_score = (TextView) view.findViewById(R.id.tv_score);
			holder.rl_scorebj = (RelativeLayout)view.findViewById(R.id.rl_scorebj);
			holder.rl_detailbj = (RelativeLayout)view.findViewById(R.id.rl_detailbj);
			holder.rootlayout = (RelativeLayout)view.findViewById(R.id.rootLayout);
			holder.textView.setTextSize(App.adjustFontSize(12));
			/*LayoutParams lp = new LayoutParams(DisplayUtil.getDisplayValue(200, (Activity) mcontext)
					, DisplayUtil.getDisplayValue(60, (Activity) mcontext));
			lp.setMargins(0, DisplayUtil.getDisplayValue(200, (Activity) mcontext), 0, 0);
			holder.textView.setLayoutParams(lp);*/
/*			LayoutParams lp2 = new LayoutParams(DisplayUtil.getDisplayValue(200, (Activity) mcontext)
					, DisplayUtil.getDisplayValue(266, (Activity) mcontext));
			lp2.setMargins(0, DisplayUtil.getDisplayValue(240, (Activity) mcontext), 0, 0);
			holder.imgView.setLayoutParams(lp2);*/
			view.setTag(holder);

			final View view2 = view;
			view.post(new Runnable() {

				@Override
				public void run() {
					if (mcontext == null)
						return;
					float height = DisplayUtil.getDisplayValue(224, (Activity) mcontext);
					float width = DisplayUtil.getDisplayValue(150, (Activity) mcontext);
					AbsListView.LayoutParams params = new AbsListView.LayoutParams(
							(int)width,
							(int) height);// xml中设置高度不起作用，这里强制设置一下
					view2.setLayoutParams(params);
				}
			});
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		String str = "";
		str =  msourcelst.get(arg0).getPostName();
		String url = "";
		url = msourcelst.get(arg0).getPostImg();
		String score = "";
		score = msourcelst.get(arg0).getScore();
		holder.tv_score.setText(score);
		if (iSelector == arg0&&exitDialogView.getGridFocus()==true) {
			iPreSelector = iSelector;
			holder.rl_scorebj.setBackgroundColor(Color.parseColor("#2e94e8"));
			holder.rl_detailbj.setBackgroundColor(Color.parseColor("#2e94e8"));
		} else{
			holder.rl_scorebj.setBackgroundColor(Color.parseColor("#AA222222"));
			holder.rl_detailbj.setBackgroundColor(Color.parseColor("#222222"));
		}

		str = str == null ? "" : str;
		holder.textView.setText(str);
		AnrImageUtils.drawPostImage(holder.imgView, url,mcontext);
		return view;
	}
	private class ViewHolder {
		TextView textView,tv_score;
		ImageView imgView;
		ImageView shadwowimg;
		RelativeLayout rootlayout,rl_scorebj,rl_detailbj;
	}
}
