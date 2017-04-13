package tvfan.tv.ui.andr.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * @description: 公告信息，保证跑马灯效果。
 * @author: root Date: 13-5-3 Time: 下午3:03
 */
public class AlwaysMarqueeTextView extends TextView
{

	/**
	 * 默认聚焦
	 */
	private boolean focused = true;

	// alwaysRun决定了该TextView是否是一直移动的
	public boolean alwaysRun;
	// 记录得到焦点的父控件
	public View parentFocusView;

	/**
	 * constructor
	 * 
	 * @param context
	 *            Context
	 */
	public AlwaysMarqueeTextView(Context context)
	{
		super(context);
		setMarqueeRepeatLimit(-1);
	}

	/**
	 * constructor
	 * 
	 * @param context
	 *            Context
	 * @param attrs
	 *            AttributeSet
	 */
	public AlwaysMarqueeTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	/**
	 * constructor
	 * 
	 * @param context
	 *            Context
	 * @param attrs
	 *            AttributeSet
	 * @param defStyle
	 *            int
	 */
	public AlwaysMarqueeTextView(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused()
	{
		if(this.alwaysRun)
		{
			return true;
		}
		else 
		{
			return (parentFocusView != null && parentFocusView.isFocused());
		}
	}
	
	public void setMarquee(boolean marquee)
	{
		if(marquee)
		{
			setEllipsize(TextUtils.TruncateAt.MARQUEE);
			setMarqueeRepeatLimit(-1);
		}
		else
		{
			setEllipsize(TextUtils.TruncateAt.END);
			setMarqueeRepeatLimit(0);
		}
	}

	public void setFocused(boolean focused)
	{
		this.focused = focused;

		if (this.focused)
		{
			setEllipsize(TextUtils.TruncateAt.MARQUEE);
			setMarqueeRepeatLimit(-1);
		}
		else
		{
			setEllipsize(TextUtils.TruncateAt.END);
			setMarqueeRepeatLimit(0);
		}
	}
}