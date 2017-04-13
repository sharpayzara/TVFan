package tvfan.tv.ui.andr.widgets;

import tvfan.tv.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog {
		private static int _styleID = -1;

		private static ProgressBar progressBar;
		private static RelativeLayout progressBarLayout;

	 	public CustomDialog(Context context, int theme) {
	        super(context, theme);
	    }
	 
	    public CustomDialog(Context context) {
	        super(context);
	    }
	 
	    /**
	     * Helper class for creating a custom dialog
	     */
	    public static class Builder {
	 
	        private Context context;
	        private String title;
	        private String message1;
	        private String message2;
	        private String positiveButtonText;
	        private String negativeButtonText;

			private View contentView;
			private OnClickListener positiveButtonClickListener, negativeButtonClickListener;

			private boolean showProgress; // 是否显示进度条
			private String updateMessage;

			public Builder(Context context) {
	            this.context = context;
	        }

	        /**
	         * Set the Dialog message from String
	         * @param title
	         * @return
	         */
	        public Builder setMessage1(String message) {
	            this.message1 = message;
	            return this;
	        }
	 
	        public Builder setMessage2(String message) {
	            this.message2 = message;
	            return this;
	        }
	        
	        /**
	         * Set the Dialog message from resource
	         * @param title
	         * @return
	         */
	        public Builder setMessage1(int message) {
	            this.message1 = (String) context.getText(message);
	            return this;
	        }
	 
	        /**
	         * Set the Dialog title from resource
	         * @param title
	         * @return
	         */
	        public Builder setTitle(int title) {
	            this.title = (String) context.getText(title);
	            return this;
	        }
	 
	        /**
	         * Set the Dialog title from String
	         * @param title
	         * @return
	         */
	        public Builder setTitle(String title) {
	            this.title = title;
	            return this;
	        }
	 
	        /**
	         * Set a custom content view for the Dialog.
	         * If a message is set, the contentView is not
	         * added to the Dialog...
	         * @param v
	         * @return
	         */
	        public Builder setContentView(View v) {
	            this.contentView = v;
	            return this;
	        }
	 
	        /**
	         * Set the positive button resource and it's listener
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

	        public Builder setStyle(int style) {
	        	_styleID = style;
	        	return this;
	        }


			public Builder setUpdateMessage(String updateMessage) {
				this.updateMessage = updateMessage;
				return this;
			}

	        /**
	         * Create the custom dialog
	         */
	        public CustomDialog create() {
	            LayoutInflater inflater = (LayoutInflater) context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            // instantiate the dialog with the custom Theme
	            final CustomDialog dialog = new CustomDialog(context, 
	            		R.style.UpdateDialog);
	            final View layout = inflater.inflate(R.layout.dialog, null);

				progressBar = (android.widget.ProgressBar) layout.findViewById(R.id.progressBar);
				progressBarLayout = (RelativeLayout) layout.findViewById(R.id.progressBarLayout);
	            // set the dialog title
	            ((TextView) layout.findViewById(R.id.title)).setText(title);
	            // set the confirm button
	            if (positiveButtonText != null) {
	                ((android.widget.Button) layout.findViewById(R.id.positiveButton))
	                        .setText(positiveButtonText);
	                if (positiveButtonClickListener != null) {
	                    ((android.widget.Button) layout.findViewById(R.id.positiveButton))
	                            .setOnClickListener(new View.OnClickListener() {
	                                public void onClick(View v) {
										if (showProgress) {
											((android.widget.Button) layout.findViewById(R.id.positiveButton)).setVisibility(View.GONE);
											((android.widget.Button) layout.findViewById(R.id.negativeButton)).setVisibility(View.GONE);
											progressBarLayout.setVisibility(View.VISIBLE);
											progressBar.setVisibility(View.VISIBLE);
										}
										positiveButtonClickListener.onClick(
												dialog,
												DialogInterface.BUTTON_POSITIVE);
									}
	                            });
	                }
	                
	                
	            } else {
	                // if no confirm button just set the visibility to GONE
	                layout.findViewById(R.id.positiveButton).setVisibility(
	                        View.GONE);
	            }
	            // set the cancel button
	            if (negativeButtonText != null) {
	            	
	            	
	                ((android.widget.Button) layout.findViewById(R.id.negativeButton))
	                        .setText(negativeButtonText);
	                if (negativeButtonClickListener != null) {
	                    ((android.widget.Button) layout.findViewById(R.id.negativeButton))
	                            .setOnClickListener(new View.OnClickListener() {
	                                public void onClick(View v) {
	                                    negativeButtonClickListener.onClick(
	                                    		dialog, 
	                                            DialogInterface.BUTTON_NEGATIVE);
	                                }
	                            });
	                }
	            } else {
	                // if no confirm button just set the visibility to GONE
	                layout.findViewById(R.id.negativeButton).setVisibility(
	                        View.GONE);
	            }

	            // set the content message
//	            if (message2 != null) {
//	            	((TextView) layout.findViewById(
//	                		R.id.message2)).setText(message2);
//				}
	            
	            if (message1 != null) {
	                ((TextView) layout.findViewById(
	                		R.id.message1)).setText(message1);

	                
	            }

				if (updateMessage != null) {
					((TextView) layout.findViewById(
							R.id.updateMessage)).setText(updateMessage);
				}

				else if (contentView != null) {
	                // if no message set
	                // add the contentView to the dialog body
//	                ((LinearLayout) layout.findViewById(R.id.content))
//	                        .removeAllViews();
//	                ((LinearLayout) layout.findViewById(R.id.content))
//	                        .addView(contentView, 
//	                                new LayoutParams(
//	                                        LayoutParams.WRAP_CONTENT, 
//	                                        LayoutParams.WRAP_CONTENT));
	            }
	            _setStyle(layout);
	            dialog.setContentView(layout);
	            return dialog;
	        }
	        
	        private void _setStyle(View layout) {
	        	if(_styleID == 0) { //for bootloader information dialog
	                ((TextView) layout.findViewById(
	                		R.id.message1)).setGravity(Gravity.CENTER_HORIZONTAL);
	                ((TextView) layout.findViewById(
	                		R.id.title)).setText("提 示");
//	                ((TextView) layout.findViewById(
//	                		R.id.message2)).setVisibility(View.INVISIBLE);
	                ((TextView) layout.findViewById(
	                		R.id.textView1)).setVisibility(View.INVISIBLE);
	                ((TextView) layout.findViewById(
	                		R.id.textView2)).setVisibility(View.INVISIBLE);
	        	}
	        }

			public Builder setShowProgress(boolean showProgress) {
				this.showProgress = showProgress;
				return this;
			}
	 
	    }

		public ProgressBar getProgressBar() {
			return progressBar;
		}
}
