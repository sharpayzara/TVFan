package tvfan.tv.ui.gdx.widgets;

import android.content.Context;
import android.view.View;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;

import tvfan.tv.R;

public class TipsDialog extends Dialog {

	static tvfan.tv.ui.gdx.portal.Page myPage;
	public TipsDialog(final Page page, final Context context) {
		super(page);
	}
	public static class Builder {

		private Context context;
		private String title;
		private String message1;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;

		private OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Page page, final Context context) {
			this.context=context;
			this.myPage = (tvfan.tv.ui.gdx.portal.Page) page;
		}

		/**
		 * Set the Dialog message from String
		 *
		 * @param
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message1 = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 *
		 * @param
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
										 OnClickListener listener) {
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
										 OnClickListener listener) {
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
										 OnClickListener listener) {
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
										 OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		private Image bgImg,qrcode,otherImg;
		private Label titleLabel,messageLabel,codeLabel,codeLabel2;
		static tvfan.tv.ui.gdx.portal.Page myPage;
		private Button sure, cancal;

		public TipsDialog create() {
			final TipsDialog dialog = new TipsDialog(myPage,context);
			dialog.setSize(1920, 1280);
			dialog.setPosition(0, 0);

			otherImg = new Image(myPage);
			otherImg.setPosition(0, 0);
			otherImg.setSize(1920, 1280);
			otherImg.setDrawableResource(R.drawable.bfy_bj);
			otherImg.setAlpha(0.9f);
			dialog.addActor(otherImg);

			bgImg = new Image(myPage);
			bgImg.setPosition(380, 250);
			bgImg.setSize(1120, 600);
			bgImg.setDrawableResource(R.drawable.exit);
			dialog.addActor(bgImg);

			sure = new Button(myPage, 170, 60);
			sure.setPosition(610, 270);
			sure.getImage().setDrawableResource(R.drawable.exit_cancel_normal);
			sure.setUnFocusBackGround(R.drawable.exit_cancel_normal);
			sure.setFocusBackGround(R.drawable.exit_affirm_selected);
			sure.setFocusAble(true);
			sure.setName("sure");
			sure.setNextFocusDown("cancal");
			sure.getLabel().setText("残忍离去");
			sure.getLabel().setTextSize(24);
			sure.getLabel().setColor(Color.GRAY);
			sure.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChanged(Actor actor, boolean b) {
					if(b){
						sure.getLabel().setColor(Color.WHITE);
					}else{
						sure.getLabel().setColor(Color.GRAY);
					}
				}
			});
			sure.requestFocus();

			cancal = new Button(myPage, 170, 60);
			cancal.setPosition(420, 270);
			cancal.getImage().setDrawableResource(R.drawable.exit_cancel_normal);
			cancal.setUnFocusBackGround(R.drawable.exit_cancel_normal);
			cancal.setFocusBackGround(R.drawable.exit_affirm_selected);
			cancal.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChanged(Actor actor, boolean b) {
					if(b){
						cancal.getLabel().setColor(Color.WHITE);
					}else{
						cancal.getLabel().setColor(Color.GRAY);
					}
				}
			});
			cancal.setFocusAble(true);
			cancal.setName("cancal");
			cancal.setNextFocusUp("sure");
			cancal.getLabel().setText("继续使用");
			cancal.getLabel().setTextSize(24);
			cancal.getLabel().setColor(Color.GRAY);
			dialog.addActor(sure);
			dialog.addActor(cancal);
			/*sure.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChanged(Actor arg0, boolean arg1) {
					if (arg1) {
						cancal.getLabel().setAlpha(0.5f);
						sure.getLabel().setAlpha(0.8f);
					}
				}
			});

			cancal.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChanged(Actor arg0, boolean arg1) {
					if (arg1) {
						sure.getLabel().setAlpha(0.5f);
						cancal.getLabel().setAlpha(0.8f);
					}

				}
			});*/
			// set the confirm button
			if (positiveButtonText != null) {
				sure.getLabel().setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					sure.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(Actor arg0) {
							positiveButtonClickListener.onClick(arg0);
						}
					});
				}

			} else {
				// if no confirm button just set the visibility to GONE
				sure.setVisible(false);
			}
			// set the cancel button
			if (negativeButtonText != null) {

				cancal.getLabel().setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					cancal.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(Actor arg0) {
							negativeButtonClickListener.onClick(arg0);
						}
					});
				}
			} else {
				cancal.setVisible(false);
			}

			return dialog;
		}

	}
}
