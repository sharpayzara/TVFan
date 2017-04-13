package tvfan.tv.lib;

import tvfan.tv.App;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

//git.coding.net/cibndevteam/EPG40.git

/**
 * 转换手机分辨率的类
 * 
 * @author
 * 
 */
public class DisplayUtil {
	/**
	 * 根据屏幕的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据屏幕的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static DisplayMetrics getDisplayMetrics(WindowManager windowsmanagers) {
		DisplayMetrics metric = new DisplayMetrics();
		windowsmanagers.getDefaultDisplay().getMetrics(metric);
		return metric;
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 获取当前适应的分辨率值
	 * 
	 * @return
	 */
	public static int getDisplayValue(int value, Activity mactivty) {
		int mvalue = value;
		float f = 0;
		try {
			// mvalue =
			// (int)(value*DisplayUtil.getDisplayMetrics(mactivty.getWindowManager()).density);

			f = App.ScreenWidth * ((float) value / 1280) + 0.5f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (int) f;
	}

	public static int getTextSize(int size, Activity mactivty) {
		DisplayMetrics dm = mactivty.getResources().getDisplayMetrics();
		return (int) (size * dm.density);
	}

	/**
	 * 获取当前适应的分辨率值
	 * 
	 * @return
	 */
	public static int getDisplayHeight(int value, Activity mactivty) {
		int mvalue = value;
		float f = 0;
		try {
			// mvalue =
			// (int)(value*DisplayUtil.getDisplayMetrics(mactivty.getWindowManager()).density);
			f = App.ScreenWidth * ((float) value / 720) + 0.5f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (int) f;
	}

}
