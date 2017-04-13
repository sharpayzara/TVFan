package tvfan.tv.lib;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.widget.ImageView;

public class AnrImageUtils {

	/**
	 * 获取海报页图片
	 * 
	 * @param url
	 */
	private static FinalBitmap fb = null;

	public static void drawPostImage(ImageView imgView, String url,Context mcontext) {

		if (fb == null) {
			fb = FinalBitmap.create(mcontext, mcontext.getCacheDir().toString());
		//	fb.configLoadingImage(R.drawable.listfilm_grid_item_background);
			//fb.configLoadfailImage(R.drawable.listfilm_grid_item_background);
			fb.configTransitionDuration(100);
		}
		fb.configCompressFormat(CompressFormat.JPEG);
		fb.display(imgView, url);
		
	}
}
