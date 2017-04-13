package tvfan.tv.ui.andr.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import tvfan.tv.R;
import tvfan.tv.lib.DisplayUtil;

public class EnterLivePlayDisplayDialog extends Dialog {

	Context mcontext;
	private RelativeLayout mrelayout;

	public EnterLivePlayDisplayDialog(Context context,int theme) {
		super(context,theme);
		mcontext = context;
		// setContentView(R.layout.liveplayprompt_layout);
		initLayout();
		setContentView(mrelayout);
		setCancelable(false);
	}

	private void initLayout() {
		mrelayout = new RelativeLayout(mcontext);
		mrelayout.setBackgroundResource(R.drawable.liveplayprompt);
		setPostion(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT, mrelayout, 0, 0, 0, 0);
	}

	private void setPostion(int width, int height, View v, int top, int left,
			int right, int bottom) {
		LayoutParams lp = new LayoutParams(getFitValue(width),
				getFitValue(height));
		lp.setMargins(getFitValue(left), getFitValue(top), getFitValue(right),
				getFitValue(bottom));
		v.setLayoutParams(lp);
	}

	private int getFitValue(int value) {
		return DisplayUtil.getDisplayValue(value, (Activity) mcontext);
	}

	@Override
	public void show() {
			super.show();
	}
}
