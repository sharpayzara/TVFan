package tvfan.tv.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.luxtone.lib.gdx.GdxContext;
import com.luxtone.lib.image.ImageCacheRequest;
import com.luxtone.lib.thread.ThreadPool.JobContext;

public class QRCodeRequest extends ImageCacheRequest {

	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	private String mContents;
	private int mImageWidth;
	private int mImageHeight;

	public QRCodeRequest(GdxContext context,String contents, int imageWidth, int imageHeight) {
		// example: test zxing!481x661
		super(context,contents + "!" + imageWidth + "x" + imageHeight,false,-1,0,0,false, false);
		this.mContents = contents;
		this.mImageWidth = imageWidth;
		this.mImageHeight = imageHeight;
	}

	@Override
	public byte[] onDecodeOriginal(JobContext jc, String url) {
		Bitmap b = null;
		byte[] data = null;
		try {
			if (jc.isCancelled())
				return null;
			b = encodeAsBitmap(mContents, mImageWidth, mImageHeight);
			if (jc.isCancelled())
				return null;
			data = bitmapToByte(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (b != null) {
				b.recycle();
			}
		}
		return data;
	}

	public static byte[] bitmapToByte(Bitmap bitmap) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

	private Bitmap encodeAsBitmap(String contents, int imageWidth, int imageHeight) throws WriterException {
		String contentsToEncode = contents;
		if (contentsToEncode == null) {
			return null;
		}
		Map<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(contentsToEncode);
		if (encoding != null) {
			hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		BitMatrix result;
		try {
			result = new QRCodeWriter().encode(contentsToEncode, BarcodeFormat.QR_CODE, imageWidth, imageHeight, hints);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			return null;
		}
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

}
