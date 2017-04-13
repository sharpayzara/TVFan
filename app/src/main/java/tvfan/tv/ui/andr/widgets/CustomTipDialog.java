package tvfan.tv.ui.andr.widgets;
import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.lib.DisplayUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomTipDialog extends Dialog {
	private static int _styleID = -1;

	public CustomTipDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomTipDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private String title;
		private String message1;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;

		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message1 = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message1 = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setStyle(int style) {
			_styleID = style;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		private RelativeLayout mrelayout, reclayout;
		private TextView txtinfo, txtContent, qrlabel1, qrlabel2;
		private Button btnok, btncancel;
		private String message;
		private ImageView qrcode;

		public CustomTipDialog create() {
			final CustomTipDialog dialog = new CustomTipDialog(context,
					R.style.PlayDialogStyle);
			mrelayout = new RelativeLayout(context);
			setPostion(1280, 720, mrelayout, 0, 0, 0, 0);
			reclayout = new RelativeLayout(context);
			reclayout.setBackgroundResource(R.drawable.shape_dialog);
			setPostion(660, 440, reclayout, 140, 306, 0, 0);
			txtinfo = new TextView(context);
			txtinfo.setText("提示");
			txtinfo.setTextColor(Color.WHITE);
			txtinfo.setGravity(Gravity.CENTER);
			txtinfo.setAlpha(0.6f);
			txtinfo.setTextSize(App.adjustFontSize(22));
			setPostion(250, 66, txtinfo, 35, 50, 0, 0);

			txtContent = new TextView(context);
			txtContent.setText(message);
			txtContent.setAlpha(0.6f);
			txtContent.setTextColor(Color.WHITE);
			txtContent.setTextSize(App.adjustFontSize(22));
			setPostion(250, 140, txtContent, 100, 50, 0, 0);

			btnok = new Button(context);
			btnok.setText("重试");
			btnok.setTextColor(Color.WHITE);
			btnok.setGravity(Gravity.CENTER);
			setPostion(250, 50, btnok, 200, 40, 0, 0);
			btnok.setBackgroundResource(R.drawable.update_btnselector);
			btnok.setTextSize(App.adjustFontSize(22));
			btnok.requestFocus();

			btncancel = new Button(context);
			btncancel.setText("退出");
			btncancel.setTextColor(Color.WHITE);
			btncancel.setGravity(Gravity.CENTER);
			btncancel.setTextSize(App.adjustFontSize(22));
			btncancel.setBackgroundResource(R.drawable.update_btnselector);
			setPostion(250, 50, btncancel, 280, 40, 0, 0);

			qrcode = new ImageView(context);
			qrcode.setBackgroundResource(R.drawable.qrcode);
			setPostion(200, 200, qrcode, 100, 380, 0, 0);

			qrlabel1 = new TextView(context);
			qrlabel1.setText(message);
			qrlabel1.setTextColor(Color.WHITE);
			qrlabel1.setText("关注电视粉微信公众号");
			qrlabel1.setAlpha(0.6f);
			qrlabel1.setGravity(Gravity.CENTER);
			qrlabel1.setTextSize(App.adjustFontSize(18));
			setPostion(200, LayoutParams.WRAP_CONTENT, qrlabel1, 310, 380, 0, 0);

			qrlabel2 = new TextView(context);
			qrlabel2.setText(message);
			qrlabel2.setTextColor(Color.WHITE);
			qrlabel2.setText("第一时间获得更多精彩");
			qrlabel2.setAlpha(0.6f);
			qrlabel2.setGravity(Gravity.CENTER);
			qrlabel2.setTextSize(App.adjustFontSize(18));
			setPostion(200, LayoutParams.WRAP_CONTENT, qrlabel2, 340, 380, 0, 0);

			reclayout.addView(txtinfo);
			reclayout.addView(txtContent);
			reclayout.addView(btnok);
			reclayout.addView(btncancel);
			reclayout.addView(qrcode);
			reclayout.addView(qrlabel1);
			reclayout.addView(qrlabel2);
			mrelayout.addView(reclayout);

			txtinfo.setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				btnok.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					btnok.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							positiveButtonClickListener.onClick(dialog,
									DialogInterface.BUTTON_POSITIVE);
						}
					});
				}

			} else {
				// if no confirm button just set the visibility to GONE
				btnok.setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {

				btncancel.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					btncancel.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							negativeButtonClickListener.onClick(dialog,
									DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				btncancel.setVisibility(View.GONE);
			}

			if (message1 != null) {
				txtContent.setText(message1);
			}
			dialog.setContentView(mrelayout);
			return dialog;
		}

		private int getFitValue(int value) {
			return DisplayUtil.getDisplayValue(value, (Activity) context);
		}

		private void setPostion(int width, int height, View v, int top,
				int left, int right, int bottom) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					getFitValue(width), getFitValue(height));
			lp.setMargins(getFitValue(left), getFitValue(top),
					getFitValue(right), getFitValue(bottom));
			v.setLayoutParams(lp);
		}
	}
}
