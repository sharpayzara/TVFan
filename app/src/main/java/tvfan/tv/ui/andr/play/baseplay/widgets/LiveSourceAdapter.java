package tvfan.tv.ui.andr.play.baseplay.widgets;

import java.util.ArrayList;

import tvfan.tv.R;
import tvfan.tv.dal.models.PlayUrlBean;
import tvfan.tv.lib.DisplayUtil;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LiveSourceAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<PlayUrlBean> list;

	public LiveSourceAdapter(Context context, ArrayList<PlayUrlBean> lst) {
		this.context = context;
		this.list = lst;
	}

	@Override
	public int getCount() {
		if (list == null)
			return 0;
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder;
		if (context == null)
			return null;
		if (convertView == null || convertView.getTag() == null) {
			view = View.inflate(context, R.layout.player_series_item, null);
			holder = new ViewHolder();
			holder.textView = (TextView) view.findViewById(R.id.textView);
			holder.rootlayout = (LinearLayout) view
					.findViewById(R.id.rootLayout);
			holder.textView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, DisplayUtil
							.getDisplayValue(80, (Activity) context)));
			view.setTag(holder);

			final View view2 = view;
			view.post(new Runnable() {

				@Override
				public void run() {
					if (context == null)
						return;
					float height = DisplayUtil.getDisplayValue(80,
							(Activity) context);
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
		// str = list.get(position);

		holder.textView.setTextSize(DisplayUtil.getDisplayValue(10,
				(Activity) context));

		str = str == null ? "" : str;
		if (!TextUtils.isEmpty(list.get(position).videoPath)) {
			str = list.get(position).videoPath;
		} else if (!TextUtils.isEmpty(list.get(position).url)) {
			str = list.get(position).url;
		} else {
			str = list.get(position).channelIdStr;
		}
		holder.textView.setText("源" + (position + 1) + ":" + str);
		holder.textView.setAlpha(0.6f);
		return view;
	}

	private class ViewHolder {
		TextView textView;
		LinearLayout rootlayout;
	}

}
