package tvfan.tv.ui.gdx.widgets;

import tvfan.tv.R;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
/**
 * 工具按钮控件(左图标右文字)
 * @author sadshine
 */
public class ToolBarGroup extends Group implements LoaderListener {
	//控件大小
	private int iWidth = 260;
	private int iHeight = 120;
	//icon图标
	private Image iconImg;
	private Image focusImg;
	private int iIconWidth = 60;
	private int iIconHeight = 60;
	private int iIconX = 40;
	private int iIconY = 0;
	
	//文字
	private Label txtLabel;
	private int iTxtWidth = 165;
	private int iTxtHeight = 60;
	private int iTxtX = 103;
	private int iTxtY = 0;
	
	/**
	 * 初始化本地icon
	 * @param page
	 * @param resId 图标资源ID
	 */
	public ToolBarGroup(Page page,int resId,String txtContext) {
		super(page);
		setFocusAble(true);
		//setFocusScale(0.09f);
		setSize(iWidth, iHeight);
		//初始化图标和文字
		getLocalImage(resId);
		//初始化焦点图片
		getFocuseImage();
		//文字
		getTextLabel(txtContext);				
		//添加
		addActor(iconImg);
		addActor(txtLabel);	
		addActor(focusImg);
	}
	
	/**
	 * 请求网络icon
	 * @param page
	 * @param iconurl
	 * @param txtcontext
	 */
	public ToolBarGroup(Page page,String iconurl,String txtcontext){
		super(page);
		
		//初始化图标和文字
		getHttpImage(iconurl);
		getTextLabel(txtcontext);	
		getFocuseImage();
		//添加
		addActor(iconImg);
		addActor(txtLabel);		
		addActor(focusImg);
	}
	
	/**
	 * 获取本地图片图标
	 * @param page
	 * @param resId
	 */
	private void getLocalImage(int resId){
		//初始化图标
		iconImg = new Image(getPage());
		iconImg.setSize(iIconWidth, iIconHeight);
		iconImg.setDrawableResource(resId);
		iconImg.setAlpha(0.6f);
		iconImg.setPosition(iIconX, iIconY);
	}
	
	private void getHttpImage(String url){
		//初始化图标
		iconImg = new Image(getPage());
		iconImg.setSize(iIconWidth, iIconHeight);
		PageImageLoader pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmap(url, "list", this, url);;
		iconImg.setAlpha(0.6f);
		iconImg.setPosition(iIconX, iIconY);
	}
	/**
	 * 获取焦点图片
	 * @return
	 */
	private void getFocuseImage(){
		//初始化图标
		/*focusImg = Widgets.image(getPage(), (R.drawable.menu_foucs));
		focusImg.setSize(290, 126);
		focusImg.setVisible(false);
		focusImg.setPosition(-18, -35);*/
		//focusImg.setPosition(getWidth() / 2 - focusImg.getWidth() / 2, getHeight() / 2 - focusImg.getHeight() / 2);
		
		focusImg = NinePatchImage.make(getPage(),
				this.findTexture(R.drawable.new_foucs),
				new int[]{40,40,40,40});
		focusImg.setSize(310, 155);
		focusImg.setPosition(-30, -50);
		focusImg.setVisible(false);
	}
	
	private void getTextLabel(String Text){
		//初始化文字
		txtLabel = new Label(getPage());
		txtLabel.setSize(iTxtWidth, iTxtHeight);
		txtLabel.setPosition(iTxtX, iTxtY);
		txtLabel.setAlignment(Align.center_Vertical);
		txtLabel.setTextSize(50);
		txtLabel.setColor(Color.WHITE);
		txtLabel.setAlpha(0.8f);
		txtLabel.setText(Text);
	}

	@Override
	public void onLoadComplete(String imageUrl, TextureRegion textureRegion, Object imageTag) {
		// TODO 加载请求返回图片
		iconImg.setDrawable(new TextureRegionDrawable(textureRegion));
		
	}
	
	@Override
	public void notifyFocusChanged(boolean getFocus) {
		focusImg.setVisible(getFocus);
		if(getFocus){
			txtLabel.setAlpha(1f);
			iconImg.setAlpha(1f);
		}else{
			txtLabel.setAlpha(0.8f);
			iconImg.setAlpha(0.8f);
		}
		super.notifyFocusChanged(getFocus);
	}


}
