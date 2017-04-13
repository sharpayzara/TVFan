
package tvfan.tv.ui.andr.widgets;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.lib.DisplayUtil;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.widget.SeekBar;


public class NumberSeekBar extends SeekBar {
    
    private int oldPaddingTop;
    
    private int oldPaddingLeft;
    
    private int oldPaddingRight;
    
    private int oldPaddingBottom;
    
    private boolean isMysetPadding = true;
    
    private String mText;
     
    private float mTextWidth;
    
    private float mImgWidth;
    
    private float mImgHei;
    
    private Paint mPaint;
    
    private Resources res;
    
    private Bitmap bm;
    
    private int textsize = 26;
    
    private int textpaddingleft;
    
    private int textpaddingtop;
    
    private int imagepaddingleft;
    
    private int imagepaddingtop;
    
    private String currtime = "00:00:00";
    
    private Context mcontext;
    public NumberSeekBar(Context context) {
        super(context);
        mcontext = context;
        init();
    }
    
    public NumberSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        init();
        
    }
    
    public NumberSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mcontext = context;
        init();
       
    }
    
    // @Override
    // public boolean onTouchEvent(MotionEvent event) {
    // return false;
    // }
    
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (isMysetPadding) {
            super.setPadding(left, top, right, bottom);
        }
    }

    private void init() {    	
        res = getResources();
        initBitmap();
        initDraw();
        setPadding();
    }
    
    private void initDraw() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置为默认字体
        mPaint.setTypeface(Typeface.DEFAULT);
//        mPaint.setTextSize(DisplayUtil.getDisplayValue(textsize,(Activity) mcontext));
        mPaint.setTextSize(App.adjustFontSize(textsize));
        mPaint.setColor(0xff666666);
        mPaint.measureText("0");
    }
    
   private void initBitmap() {
	   //自定义seekbar上方滑块的样式
//		bm = BitmapFactory.decodeResource(res, R.drawable.jindupop);
	   bm = decodeResource(res, R.drawable.jindupop);
        if (bm != null) {
            mImgWidth = bm.getWidth();
            mImgHei =  bm.getHeight()-DisplayUtil.getDisplayValue(15, (Activity) mcontext);
        } else {
            mImgWidth = 0;
            mImgHei = 0;
        }
    }
   /**
    * 保持density和targetDensity的一致，就可以避免缩放了
    * @param resources
    * @param id
    * @return
    */
   private Bitmap decodeResource(Resources resources, int id)
   {
      TypedValue value = new TypedValue();
      resources.openRawResource(id,value);
      BitmapFactory.Options opts = new BitmapFactory.Options();
      opts.inTargetDensity = value.density;
      return BitmapFactory.decodeResource(resources, id, opts);
  }
    protected synchronized void onDraw(Canvas canvas) {
        try { 
            super.onDraw(canvas);
            mText = this.currtime;/*(getProgress() * 100 / getMax()) + "%";*/
            mTextWidth = mPaint.measureText(mText);
            mPaint.setColor(Color.BLACK);
            Rect bounds = this.getProgressDrawable().getBounds();
            float xImg =
                bounds.width() * getProgress() / getMax() + imagepaddingleft
                    + oldPaddingLeft;
            float yImg = imagepaddingtop + oldPaddingTop;
            float xText =
                bounds.width() * getProgress() / getMax() + mImgWidth / 2
                    - mTextWidth / 2 + textpaddingleft + oldPaddingLeft;
            float yText =
                yImg + textpaddingtop + mImgHei / 2 + getTextHei() / 4;
            //
            canvas.drawBitmap(bm, xImg-DisplayUtil.getDisplayValue(1, (Activity) mcontext), yImg, mPaint);
            canvas.drawText(mText, xText, yText-DisplayUtil.getDisplayValue(2, (Activity) mcontext), mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private void setPadding() {
        int top = getBitmapHeigh() + oldPaddingTop;
        int left = getBitmapWidth() / 2 + oldPaddingLeft;
        int right = getBitmapWidth() / 2 + oldPaddingRight;
        int bottom = oldPaddingBottom;
        isMysetPadding = true;
        setPadding(left, top, right, bottom);
        isMysetPadding = false;
    }
    
    /**
     * 
     * @param resid
     */
    public void setBitmap(int resid) {
//        bm = BitmapFactory.decodeResource(res, resid);
        bm = decodeResource(res, resid);
        if (bm != null) {
            mImgWidth = bm.getWidth();
            mImgHei = bm.getHeight();
        } else {
            mImgWidth = 0;
            mImgHei = 0;
        }
        setPadding();
    }
   
    /**etpadding
     * 
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setMyPadding(int left, int top, int right, int bottom) {
        oldPaddingTop = top;
        oldPaddingLeft = left;
        oldPaddingRight = right;
        oldPaddingBottom = bottom;
        isMysetPadding = true;
        setPadding(left + getBitmapWidth() / 2, top + getBitmapHeigh(), right
            + getBitmapWidth() / 2, bottom);
        isMysetPadding = false;
    }
    
    /**
     * 
     * @param textsize
     */
    public void setTextSize(int textsize) {
//        this.textsize = DisplayUtil.getDisplayValue(textsize,(Activity) mcontext);
    	 this.textsize = App.adjustFontSize(textsize);
        mPaint.setTextSize(textsize);
    }
    
    /**
     * 
     * @param color
     */
    public void setTextColor(int color) {
        mPaint.setColor(color);
    }
    
    /**
     * 
     * @param top
     * @param left
     */
    public void setTextPadding(int top, int left) {
        this.textpaddingleft = left;
        this.textpaddingtop = top;
    }
    
    /**
     * 
     * @param top
     * @param left
     */
    public void setImagePadding(int top, int left) {
        this.imagepaddingleft = left;
        this.imagepaddingtop = top;
    }
    
    private int getBitmapWidth() {
        return (int)Math.ceil(mImgWidth);
    }
    
    private int getBitmapHeigh() {
        return (int)Math.ceil(mImgHei);
    }
    
    private float getTextHei() {
        FontMetrics fm = mPaint.getFontMetrics();
        return (float)Math.ceil(fm.descent - fm.top) + 2;
    }
    
    public int getTextpaddingleft() {
        return textpaddingleft;
    }
    
    public int getTextpaddingtop() {
        return textpaddingtop;
    }
    
    public int getImagepaddingleft() {
        return imagepaddingleft;
    }
    
    public int getImagepaddingtop() {
        return imagepaddingtop;
    }
    
    public int getTextsize() {
        return textsize;
    }

	public void setCurrtime(String currtime) {
		this.currtime = currtime;
	}

    
}
