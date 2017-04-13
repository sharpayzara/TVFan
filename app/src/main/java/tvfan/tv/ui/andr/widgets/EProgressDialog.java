package tvfan.tv.ui.andr.widgets;

import tvfan.tv.App;
import tvfan.tv.lib.DisplayUtil;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import tvfan.tv.R;

public class EProgressDialog extends Dialog {

	private Context mContext;
	private TextView messageView;
	private ProgressBar progressBarView;
	private LinearLayout loadingLayout;

	public EProgressDialog(Context context) {
		super(context, R.style.dialog);
		setContentView(R.layout.new_progress_dialog);
		this.mContext = context;
		
		loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
		FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) loadingLayout.getLayoutParams();
		lp.width = App.getAdjustWidthSize(1920);
		lp.height = App.getAdjustHeightSize(1080);
		lp.topMargin = App.getAdjustHeightSize(-50);
		loadingLayout.setLayoutParams(lp);
		loadingLayout.setGravity(Gravity.CENTER);

		progressBarView = (ProgressBar) findViewById(R.id.loading_dialog_progressBar);
		LayoutParams progressBarViewParams = new LayoutParams(
				App.getAdjustWidthSize(310), App.getAdjustHeightSize(95));
		progressBarView.setLayoutParams(progressBarViewParams);
		progressBarView.setIndeterminate(false);
		progressBarView.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.loading));
		
		
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(dm);
		int displayWidth = dm.widthPixels;
		int displayHeight = dm.heightPixels;
		messageView = (TextView) findViewById(R.id.loading_message);
		messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
		messageView.setTextSize(adjustFontSize(messageView, displayWidth, displayHeight));
		LayoutParams messageViewParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		messageViewParams.topMargin = App.getAdjustHeightSize(10);
		messageView.setLayoutParams(messageViewParams);
	}

	public void setMessage(String message) {
		if (messageView != null && !TextUtils.isEmpty(message)) {
			messageView.setText(message);
			messageView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (progressBarView != null) {
			progressBarView.setVisibility(View.GONE);
		}
	}

	@Override
	public void show() {
		if (progressBarView != null) {
			progressBarView.setVisibility(View.VISIBLE);
		}

		try {
			if (EProgressDialog.this != null) {
				super.show();
			}
		} catch (Exception ex) {
		}
	}
	
	// 获取字体大小
	public int adjustFontSize(View v, int screenWidth, int screenHeight) {
		float textSize = 15;
		if (v instanceof Button) {
			textSize = ((Button) v).getTextSize();
		}
		if (v instanceof TextView) {
			textSize = ((TextView) v).getTextSize();
		}
		textSize = DisplayUtil.px2sp(mContext, textSize);
		screenWidth = screenWidth > screenHeight ? screenWidth : screenHeight;
		int rate = (int) (textSize * (float) screenWidth / 1280); // 我自己测试这个倍数比较适合，当然你可以测试后再修改
		return rate < 15 ? 15 : rate; // 字体太小也不好看的
	}
}
