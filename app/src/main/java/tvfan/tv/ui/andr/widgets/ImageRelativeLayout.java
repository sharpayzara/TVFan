package tvfan.tv.ui.andr.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tvfan.tv.App;
import tvfan.tv.R;

/**
 * a RelativeLayout containa ImageView and TextView 用于首页的图片控件，选中时，显示文本。
 * 
 * @author archko
 */
public class ImageRelativeLayout extends RelativeLayout {

	private Context mContext;
	private ImageView mImageView;
	private TextView mTextView;
	private ImageView mImageViewCover;

	private static final ScaleType[] sScaleTypeArray = { ScaleType.MATRIX,
			ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER,
			ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP,
			ScaleType.CENTER_INSIDE };
	// ================= imageview attrs =================
	private ScaleType mScaleType = ScaleType.FIT_XY;

	private Drawable mDrawable;
	// ================= textview attrs =================
	private ColorStateList mTextColor;
	private int mTextSize = 14;
	private int mTxtLeftPadding = 0;
	private int mTxtTopPadding = 0;
	private int mTxtRightPadding = 0;
	private int mTxtBottomPadding = 0;
	private CharSequence mText = "";

	private int mAlign = 0;
	/**
	 * 总是显示文本
	 */
	private boolean alwaysShowTxt = false;
	public int mPos = 0;

	public ImageRelativeLayout(Context context) {
		super(context);
		init(context);
	}

	public ImageRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ImageRelativeLayout, defStyle, 0);
		int index = a.getInt(R.styleable.ImageRelativeLayout_scaleType, -1);
		if (index >= 0) {
			setScaleType(sScaleTypeArray[index]);
		}
		Drawable d = a.getDrawable(R.styleable.ImageRelativeLayout_src);
		mDrawable = (d);
		mTextSize = a.getDimensionPixelSize(
				R.styleable.ImageRelativeLayout_textSize, 14);

		a.getInt(R.styleable.ImageRelativeLayout_txt_align, 0);
		mTxtLeftPadding = a.getDimensionPixelSize(
				R.styleable.ImageRelativeLayout_txtPaddingLeft, 0);
		mTxtRightPadding = a.getDimensionPixelSize(
				R.styleable.ImageRelativeLayout_txtPaddingRight, 0);
		mTxtTopPadding = a.getDimensionPixelSize(
				R.styleable.ImageRelativeLayout_txtPaddingTop, 0);
		mTxtBottomPadding = a.getDimensionPixelSize(
				R.styleable.ImageRelativeLayout_txtPaddingBottom, 0);

		mText = a.getText(R.styleable.ImageRelativeLayout_text);
		mTextColor = a
				.getColorStateList(R.styleable.ImageRelativeLayout_textColor);

		mAlign = a.getInt(R.styleable.ImageRelativeLayout_txt_align, 0);
		alwaysShowTxt = a.getBoolean(
				R.styleable.ImageRelativeLayout_alwaysShowTxt, false);
		a.recycle();

		if (getChildCount() == 1) {
			initWithXml(getChildAt(0));
		} else if (getChildCount() == 2) {
			initWithXml();
		} else {
			init(context);
		}
	}

	private void initWithXml() {
		setFocusable(true);
		setClickable(true);
		mImageView = (ImageView) getChildAt(0);
		mTextView = (NofocusMarqueeTextView) getChildAt(1);

		mImageView.setFocusable(false);
		mImageView.setClickable(false);

		mTextView.setFocusable(false);
		mTextView.setClickable(false);

		if (!alwaysShowTxt) {
			mTextView.setVisibility(GONE);
		} else {
			mTextView.setVisibility(VISIBLE);
		}
	}

	private void initWithXml(View view) {
		setFocusable(true);
		setClickable(true);
		mImageView = (ImageView) view;
		mImageView.setFocusable(false);
		mImageView.setClickable(false);
	}

	void init(Context ctx) {
		removeAllViews();

		mContext = ctx;
		setFocusable(true);
		setClickable(true);

		mImageViewCover = new ImageView(ctx);
		mImageViewCover.setFocusable(false);
		mImageViewCover.setClickable(false);
		mImageViewCover.setScaleType(mScaleType);
		/*
		 * if (null != mDrawable) { mImageViewCover.setImageDrawable(mDrawable);
		 * mDrawable = null; }
		 */
		// mImageViewCover.setBackgroundColor(Color.YELLOW);
		// mImageViewCover.setImageResource(R.drawable.sss);

		LayoutParams layoutParamsCover = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutParamsCover.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParamsCover.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(mImageViewCover, layoutParamsCover);

		mImageView = new ImageView(ctx);
		mImageView.setFocusable(false);
		mImageView.setClickable(false);
		mImageView.setScaleType(mScaleType);
		// mImageView.setPadding(2, 2, 2, 2);
		if (null != mDrawable) {
			mImageView.setImageDrawable(mDrawable);
			mDrawable = null;
		}
		LayoutParams layoutParams = new LayoutParams(
				App.getAdjustWidthSize(260), App.getAdjustHeightSize(100));
		layoutParams.addRule(ALIGN_PARENT_BOTTOM);
		addView(mImageView, layoutParams);

		mTextView = new NofocusMarqueeTextView(ctx);
		mTextView.setFocusable(false);
		mTextView.setClickable(false);
		mTextView.setTextSize(mTextSize);
		mTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		mTextView.setSingleLine();
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setPadding(mTxtLeftPadding, mTxtTopPadding, mTxtRightPadding,
				mTxtBottomPadding);

		mTextView.setTextColor(mTextColor != null ? mTextColor : ColorStateList
				.valueOf(0xFFFFFFFF));
		mTextView.setAlpha(0.8f);
		mTextView.setText(mText);

		// 对Marquee增加对父对象的引用，这个可以知道当前的父容器是否得到了焦点
		((AlwaysMarqueeTextView) mTextView).parentFocusView = this;
		layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		if (mAlign == 0) {
			layoutParams.addRule(ALIGN_PARENT_BOTTOM);
		} else {
			// layoutParams.addRule(ALIGN_PARENT_TOP);
			layoutParams.addRule(ALIGN_PARENT_BOTTOM);
		}
		addView(mTextView, layoutParams);
		if (!alwaysShowTxt) {
			mTextView.setVisibility(GONE);
		} else {
			mTextView.setVisibility(VISIBLE);
		}
	}

	public ImageView getImageView() {
		return mImageView;
	}

	public TextView getTextView() {
		return mTextView;
	}

	public ImageView getmImageViewCover() {
		return mImageViewCover;
	}

	public void setmImageViewCover(ImageView mImageViewCover) {
		this.mImageViewCover = mImageViewCover;
	}

	public void setImageView(ImageView imageView) {
		this.mImageView = imageView;
	}

	public void setScaleType(ScaleType scaleType) {
		if (mScaleType != scaleType) {
			mScaleType = scaleType;
		}
		if (null != mImageView) {
			mImageView.setScaleType(mScaleType);
		}
	}

	public void setTxtPadding(int txtLeftPadding, int txtBottomPadding,
			int txtTopPadding, int txtRightPadding) {
		this.mTxtLeftPadding = txtLeftPadding;
		this.mTxtTopPadding = txtTopPadding;
		this.mTxtRightPadding = txtRightPadding;
		this.mTxtBottomPadding = txtBottomPadding;
		mTextView.setPadding(txtLeftPadding, txtTopPadding, txtRightPadding,
				txtBottomPadding);
	}

	public void setTextSize(int textSize) {
		this.mTextSize = textSize;
		mTextView.setTextSize(textSize);
	}

	public void setTextSize(int type, int textSize) {
		mTextView.setTextSize(type, textSize);
	}

	public void setAlpha(float alpha) {
		mTextView.setAlpha(alpha);
	}

	public void setTextView(TextView mTextView) {
		this.mTextView = mTextView;
	}

	public void setText(CharSequence text) {
		this.mText = text;
		mTextView.setText(text);
	}

	/**
	 * 只支持上下位置的文本
	 * 
	 * @param align
	 *            0表示在底部,1表示在顶部
	 */
	public void setAlign(int align) {
		if (this.mAlign != align) {
			this.mAlign = align;

			removeViewAt(1);

			mTextView = new NofocusMarqueeTextView(mContext);
			mTextView.setFocusable(false);
			mTextView.setClickable(false);
			mTextView.setTextSize(mTextSize);
			mTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			mTextView.setSingleLine();
			mTextView.setPadding(mTxtLeftPadding, mTxtTopPadding,
					mTxtRightPadding, mTxtBottomPadding);
			mTextView.setTextColor(mTextColor != null ? mTextColor
					: ColorStateList.valueOf(0xFFFFFFFF));
			mTextView.setText(mText);
			mTextView.setAlpha(0.8f);
			mTextView.setGravity(Gravity.CENTER);
			LayoutParams layoutParams = new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			if (align == 0) {
				layoutParams.addRule(ALIGN_PARENT_BOTTOM);
			} else {
				layoutParams.addRule(ALIGN_PARENT_TOP);
			}
			addView(mTextView, layoutParams);
		}
	}

	AlwaysMarqueeTextView atv = null;

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		/*
		 * KeelLg.d(VIEW_LOG_TAG,
		 * "ImageRelativeLayout:"+gainFocus+" v:"+(mTextView
		 * .getVisibility()==GONE) +" show:"+alwaysShowTxt+" this:"+this);
		 */
		if (atv == null)
			atv = (AlwaysMarqueeTextView) mTextView;
		atv.setMarquee(gainFocus);

		if (!alwaysShowTxt) {
			if (gainFocus) {
				if (mTextView.getVisibility() != View.VISIBLE) {
					// mTextView.setFocused(true);
					mTextView.setVisibility(VISIBLE);
					mTextView.requestLayout();
				}
			} else {
				if (mTextView.getVisibility() == VISIBLE) {
					// mTextView.setFocused(false);
					mTextView.setVisibility(GONE);
				}
			}
		}
		// KeelLg.d(VIEW_LOG_TAG,
		// "txt:"+mTextView.getText().toString()+" focus:"+mTextView.isFocused()+" gainFocus:"+gainFocus);
	}

	public void setFocus() {
		if (mTextView.getVisibility() != View.VISIBLE) {
			mTextView.setVisibility(VISIBLE);
		}
		// mTextView.setFocused(true);
	}
}
