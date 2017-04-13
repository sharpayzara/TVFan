package tvfan.tv.ui.gdx.programHotList;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

public class PostGridHotItem extends Group implements IListItem, LoaderListener {
	private Image image;
	private Image focusImg;
	private Image shadowImg;
	private Label label;
	private PageImageLoader pageImageLoader;
	private CullGroup cullGroup;
	private String url;
	//长宽坐标
	private int icullgpX = 0;
	private int icullgpY = 0;
	private int iLabelHeight = 60;
	private Image preImg;
	
	public PostGridHotItem(Page page,int iPostWidth,int iPostHeight) {
		super(page);
		setSize(iPostWidth, iPostHeight);
		setFocusAble(true);
		setFocusScale(AppGlobalConsts.FOCUSSCALE);
		
		//外框
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(iPostWidth, iPostHeight);
		cullGroup.setPosition(icullgpX, icullgpY);
		cullGroup.setCullingArea(new Rectangle(0, 0, iPostWidth, iPostHeight));
		
		//海报图片
		image = new Image(getPage());
		image.setSize(iPostWidth, iPostHeight);
		image.setPosition(0, 0);
		//海报阴影
		shadowImg = new Image(getPage());
		shadowImg.setSize(iPostWidth, iPostHeight);
		shadowImg.setPosition(0, 0);
		shadowImg.setDrawableResource(R.drawable.postshadow);
		//海报字幕
		label = new Label(getPage(), false);
		label.setPosition(0, 0);
		label.setSize(iPostWidth, iLabelHeight);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.center);
		label.setText("");
		label.setTextSize(40);
		label.setAlpha(0.8f);
		label.setMarquee(false);
		
		//光标选中效果 
		focusImg = NinePatchImage.make(getPage(),
				this.findTexture(R.drawable.list_foucs),
				new int[]{65,65,65,65});
		focusImg.setSize(iPostWidth +105, iPostHeight +105);
		focusImg.setPosition(-53, -53);
		focusImg.setVisible(false);
		
		
		//载入场景
		cullGroup.addActor(image);
		cullGroup.addActor(shadowImg);
		cullGroup.addActor(label);	
		
		addActor(cullGroup);
		addActor(focusImg);	
	}

	public void update(String url,String name) {
		this.url = url;
		if (pageImageLoader != null){
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmapWithSizeByCut(this.url, "list", true,AppGlobalConsts.CUTLENGTH,image.getWidth(),image.getHeight(),this,"postimg");		
		label.setText(name);
	}

	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
		label.setAlignment(Align.center);
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		label.setMarquee(getFocus);
		if(getFocus){
			focusImg.setVisible(true);
			focusImg.addAction(Actions.fadeIn(0.1f));			
			
		}else{
			focusImg.addAction(Actions.fadeOut(0.1f));		
		}
		
	}

	@Override
	public void onLoadComplete(String imageUrl, TextureRegion textureRegion, Object imageTag) {
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);
		image.addAction(Actions.fadeOut(0));
		image.setDrawable(textureRegionDrawable);
		image.addAction(Actions.fadeIn(0.6f));
	}

	public int getIcullgpX() {
		return icullgpX;
	}

	public void setIcullgpX(int icullgpX) {
		this.icullgpX = icullgpX;
	}

	public int getIcullgpY() {
		return icullgpY;
	}

	public void setIcullgpY(int icullgpY) {
		this.icullgpY = icullgpY;
	}

	@Override
	public void onSelected(boolean isfocus) {
	
	}
	
	public void setImage(int rid){
		image.setDrawableResource(rid);
	}

	public Image getFocusImg() {
		return focusImg;
	}
	
	

}
