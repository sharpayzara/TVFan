package tvfan.tv.ui.gdx.programList;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

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

public class PostGridItem extends Group implements IListItem, LoaderListener {
	private Image image, logoImage;
	private Image focusImg;
	private Image shadowImg;
	private Image scoreBack;
	private TVFANLabel score1;
	private TVFANLabel score2;
	private Label label,label7;
	private PageImageLoader pageImageLoader;
	private CullGroup cullGroup;
	private String url;
	//长宽坐标
	private int iCullGpWidth = 261;
	private int iCullGpHeight= 398;
	private int icullgpX = 0;
	private int icullgpY = 0;
	private int iPostWidth = 261;
	private int iPostHeight = 398;
	private int iLabelWidth = 261;
	private int iLabelHeight = 50;
	private int iShadowWidth = 261;

	public PostGridItem(Page page,int cullx,int cully) {
		super(page);
		setSize(iPostWidth, iPostHeight);
		setFocusAble(true);
		setFocusScale(AppGlobalConsts.FOCUSSCALE);
		
		//外框
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(iCullGpWidth, iCullGpHeight);
		cullGroup.setPosition(icullgpX, icullgpY);
		cullGroup.setCullingArea(new Rectangle(0, 0, iCullGpWidth, iCullGpHeight));
		
		//海报图片
		image = new Image(getPage());
		image.setSize(iCullGpWidth, 348.0f);
		image.setPosition(0.0f, 50.0f);
		image.setDrawableResource(R.drawable.placeholder);

		//默认海报图片中间的logo
		logoImage = new Image(getPage());
		logoImage.setSize(137.6f, 48f);
		logoImage.setPosition((float) ((iCullGpWidth - 137.6) / 2), (iCullGpHeight - 48) / 2);
		logoImage.setDrawableResource(R.drawable.placeholder_logo);
		logoImage.setVisible(true);
		//海报阴影
		shadowImg = new Image(getPage());
		shadowImg.setSize(iShadowWidth, iLabelHeight);
		shadowImg.setPosition(0, 0);
		shadowImg.setDrawableResource(R.drawable.bottom_dark_bg);
	
		//海报字幕
		label = new Label(getPage(), false);
		label.setPosition(0.0f, 0.0f);
		label.setSize(iLabelWidth, iLabelHeight);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.center);
		label.setText("");
		label.setTextSize(30);
		label.setAlpha(0.8f);
		label.setMarquee(false);
		
		label7 = new Label(getPage(), false);
		label7.setPosition(0.0f, 0.0f);
		label7.setSize(iLabelWidth, iLabelHeight);
		label7.setColor(Color.WHITE);
		label7.setAlignment(Align.center);
		label7.setText("");
		label7.setTextSize(30);
		label7.setAlpha(0.8f);
		label7.setMarquee(false);
		label7.setVisible(false);
		
		focusImg = NinePatchImage.make(page,findTexture(R.mipmap.detail_foucs), new int[]{45,45,45,45});
		focusImg.setSize(iCullGpWidth, iCullGpHeight);
		focusImg.setPosition(0.0f, 0.0f);
		focusImg.setVisible(false);

		//载入场景
		cullGroup.addActor(image);
		cullGroup.addActor(logoImage);
		cullGroup.addActor(shadowImg);
		cullGroup.addActor(label);	
		
		cullGroup.addActor(label7);


		addActor(cullGroup);
		addActor(focusImg);

		//评分
		scoreBack = new Image(page);
		scoreBack.setPosition(201.0f, 358.0f);
		scoreBack.setSize(60.0f, 40.0f);
		scoreBack.setDrawableResource(R.mipmap.score_bg2);
		scoreBack.toFront();
		cullGroup.addActor(scoreBack);

		score1 = new TVFANLabel(page);
		score1.setPosition(201.0f, 361.0f);
		score1.setSize(28.0f, 40.0f);
		score1.setAlignment(Align.right);
		score1.setTextSize(30);
		score1.toFront();
		cullGroup.addActor(score1);

		score2 = new TVFANLabel(page);
		score2.setPosition(229.0f, 361.0f);
		score2.setSize(30.0f, 40.0f);
		score2.setAlignment(Align.left);
		score2.setTextSize(25);
		score2.toFront();
		cullGroup.addActor(score2);
	}

	public void update(String url,String name, String score) {
		this.url = url;
		if (pageImageLoader != null){
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmap(this.url, "list", this, "postimg");

		if(Float.parseFloat(score) >= 10.0f) {
			score = "10";
		}
		String score_1 = score.substring(0, score.indexOf("."));
		String score_2 = score.substring(score.indexOf("."),score.length());

		this.score1.setText(score_1);
		this.score2.setText(score_2);
		if (getWordCount(name) >= 14) {
			label7.setText(name);
			label.setVisible(false);
			label7.setVisible(true);
		} else
			label7.setText(name);
		
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
			label7.setVisible(false);
			label.setVisible(true);
			shadowImg.setDrawableResource(R.mipmap.detai_label_normal);
			scoreBack.setDrawableResource(R.mipmap.score_bg);
		}else{
			focusImg.addAction(Actions.fadeOut(0.1f));	
			label7.setVisible(true);
			label.setVisible(false);
			shadowImg.setDrawableResource(R.drawable.bottom_dark_bg);
			scoreBack.setDrawableResource(R.mipmap.score_bg2);
		}
	}

	@Override
	public void onLoadComplete(String imageUrl, TextureRegion textureRegion, Object imageTag) {
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);
		image.addAction(Actions.fadeOut(0));
		image.setDrawable(textureRegionDrawable);
		image.addAction(Actions.fadeIn(0.6f));
		logoImage.setVisible(false);
	}

	@Override
	public void onSelected(boolean isfocus) {
	
	}
	
	public void setImage(int resId){
		image.setDrawableResource(resId);
	}

	public void setLogoImage(int resId){
		logoImage.setDrawableResource(resId);
		logoImage.setVisible(true);
	}
	public Image getFocusImg() {
		return focusImg;
	}
	

	public int getWordCount(String s) {
		int length = 0;
		for (int i = 0; i < s.length(); i++) {
			int ascii = Character.codePointAt(s, i);
			if (ascii >= 0 && ascii <= 255)
				length++;
			else
				length += 2;
		}
		return length;
	}

}
