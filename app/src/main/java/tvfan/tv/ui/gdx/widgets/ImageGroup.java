package tvfan.tv.ui.gdx.widgets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
/**
 * 图片加载组件
 * @author sadshine
 */
public class ImageGroup extends Group implements LoaderListener  {
	PageImageLoader pageImageLoader;
	Image img;
	/**
	 * 图片初始化
	 * @param page
	 * @param w 宽
	 * @param h 高
	 * @param x 坐标x
	 * @param y 坐标y
	 */
	public ImageGroup(Page page,int w,int h,int x, int y) {
		super(page);
		setSize(w, h);
		img = new Image(getPage());
		img.setSize(w+100, h+100);
		img.setPosition(x-50, y-50);
		addActor(img);
	}
	
	public Image getImage(){
		return img;
	}
	 
    public void load(String url,String name,Image image) {
		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		//pageImageLoader.startLoadBitmapWithSizeByCut(url, "img", true,6,image.getWidth(),image.getHeight(),this,"imggroup");
		pageImageLoader.startLoadBitmap(url, "img", this, "imggroup");
    }
    
    public void loadByFilter(String url,String name,Image image) {
		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmapByFilter(url,"",false,0,this,"imggroup");
	}
	@Override
	public void onLoadComplete(String imageUrl, TextureRegion textureRegion, Object imageTag) {
		img.setDrawable(new TextureRegionDrawable(textureRegion));
		
	}

}
