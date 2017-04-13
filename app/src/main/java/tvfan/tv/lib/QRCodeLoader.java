package tvfan.tv.lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.luxtone.lib.gdx.App;
import com.luxtone.lib.gdx.GdxContext;
import com.luxtone.lib.image.AbsImageLoader;
import com.luxtone.lib.thread.Future;
import com.luxtone.lib.thread.FutureListener;

/**
 * 生成二维码
 */
public class QRCodeLoader extends AbsImageLoader {

	public QRCodeLoader(GdxContext context) {
		super(context);
	}

	private String mContents;
	private int mImageWidth;
	private int mImageHeight;

	private QRCodeLoaderListener mListener;

	public void startLoad(String contents, int imageWidth, int imageHeight, QRCodeLoaderListener listener) {
		this.mContents = contents;
		this.mImageWidth = imageWidth;
		this.mImageHeight = imageHeight;
		this.mListener = listener;
		startLoad();
	}

	@Override
	protected Future<Pixmap> submitImageTask(FutureListener<Pixmap> l) {
		return App.getThreadPool("image").submit(new QRCodeRequest(getGdxContext(),mContents, mImageWidth, mImageHeight), l);
	}

	@Override
	protected void onLoadComplete(final Pixmap data) {
		if (data != null) {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					Texture texture = new Texture(data);
					texture.setType(Texture.Type.NETWORK);
					recyclePixmap(data);
					texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					TextureRegion region = new TextureRegion(texture);
					if (mListener != null) {
						mListener.onLoadQRCodeComplete(mContents, mImageWidth, mImageHeight, region);
					} else {
						texture.dispose();
					}
				}
			});
		}
	}

	public interface QRCodeLoaderListener {
		public void onLoadQRCodeComplete(String contents, int imageWidth, int imageHeight, TextureRegion textureRegion);
	}
}
