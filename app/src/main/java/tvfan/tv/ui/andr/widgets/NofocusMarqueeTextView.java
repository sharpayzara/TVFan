package tvfan.tv.ui.andr.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

/**
 * 自定义无视焦点的跑马灯效果
 */
public class NofocusMarqueeTextView extends TextView
{

	/** 是否停止滚动 */
	private boolean mStopMarquee;
	private String mText;
	private float mCoordinateX;
	private float mTextWidth;

	public NofocusMarqueeTextView(Context context) {
		super(context);
	}

	public void setText(String text) {
		this.mText = text;
		mTextWidth = getPaint().measureText(mText);
		if (mHandler.hasMessages(0))
			mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 2000);
	}

	@Override
	protected void onAttachedToWindow() {
		mStopMarquee = false;
		if (!StringUtils.isEmpty(mText))
			mHandler.sendEmptyMessageDelayed(0, 2000);
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		mStopMarquee = true;
		if (mHandler.hasMessages(0))
			mHandler.removeMessages(0);
		super.onDetachedFromWindow();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!StringUtils.isEmpty(mText))
			canvas.drawText(mText, mCoordinateX, 45, getPaint());
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					if (Math.abs(mCoordinateX) > (mTextWidth + 100)) {
						mCoordinateX = 0;
						invalidate();
						if (!mStopMarquee) {
							sendEmptyMessageDelayed(0, 2000);
						}
					} else {
						mCoordinateX -= 1;
						invalidate();
						if (!mStopMarquee) {
							sendEmptyMessageDelayed(0, 30);
						}
					}

					break;
			}
			super.handleMessage(msg);
		}
	};

}