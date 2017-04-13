package tvfan.tv.ui.gdx.topLists;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

public class ToplGridItem extends Group implements IListItem, LoaderListener {
	private Image imageHolder,imageHolderLogo,bottomShadowImage;
	private TVFANLabel label,topNum,scoreNum1,scoreNum2;
	private CullGroup cullGroup;
	private PageImageLoader pageImageLoader;
	private Image focusImg,topTip,scoreBg;
	private String url;
	private String text;
	private UpdateProgramDetail detailListener;
	private String currentTitle,currentDetail;

	public ToplGridItem(Page page,UpdateProgramDetail detailListener) {
		super(page);
		setSize(250, 400);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setPosition(0, 20);
		cullGroup.setSize(250, 400);
		cullGroup.setCullingArea(new Rectangle(0, 0, 250, 400));
		addActor(cullGroup);

		imageHolder = new Image(page);
		imageHolder.setDrawableResource(R.drawable.placeholder);
		imageHolder.setSize(250, 350);
		imageHolder.setPosition(0, 50);
		cullGroup.addActor(imageHolder);

		imageHolderLogo = new Image(page);
		imageHolderLogo.setDrawableResource(R.drawable.placeholder_logo);
		imageHolderLogo.setSize(172,53);
		imageHolderLogo.setPosition((250-172)/2, (400-50)/2);
		cullGroup.addActor(imageHolderLogo);

		bottomShadowImage = new Image(getPage());
		bottomShadowImage.setPosition(0,0);
		bottomShadowImage.setSize(250,50);
		bottomShadowImage.setDrawableResource(R.drawable.bottom_dark_bg);
		cullGroup.addActor(bottomShadowImage);

		label = new TVFANLabel(getPage());
		label.setPosition(10, 10);
		label.setSize(250, 30);
		label.setTextSize(30);
		label.setMaxLine(1);
		label.setVisible(true);
		label.setColor(Color.valueOf("f0f0f0"));
		label.setAlignment(Align.center);
		cullGroup.addActor(label);


		topTip = new Image(getPage());
		//topTip.setPosition(202,330);
		topTip.setPosition(0,330);
		topTip.setSize(50,70);
		topTip.setDrawableResource(R.mipmap.red_bg);
		topTip.setVisible(false);
		cullGroup.addActor(topTip);

		scoreBg = new Image(getPage());
		scoreBg.setSize(50,35);
		scoreBg.setDrawableResource(R.mipmap.score_bg2);
		scoreBg.setPosition(202,330+35);
		cullGroup.addActor(scoreBg);

		scoreNum1 = new TVFANLabel(getPage());
		scoreNum1.setPosition(208,366);
		scoreNum1.setTextSize(28);
		cullGroup.addActor(scoreNum1);

		scoreNum2 = new TVFANLabel(getPage());
		scoreNum2.setPosition(232,369);
		scoreNum2.setTextSize(22);
		cullGroup.addActor(scoreNum2);

		topNum = new TVFANLabel(getPage());
		topNum.setPosition(18,346);
		topNum.setColor(Color.valueOf("ffffff"));
		topNum.setTextSize(40);
		topNum.setText("1");
		topNum.setVisible(false);
		cullGroup.addActor(topNum);

		focusImg = NinePatchImage.make(page,findTexture(R.mipmap.detail_foucs), new int[]{45,45,45,45});
		focusImg.setSize(254, 405);
		focusImg.setPosition(-2,17);
		focusImg.setVisible(false);
		addActor(focusImg);

		setFocusScale(AppGlobalConsts.FOCUSSCALE);
		this.detailListener = detailListener;
	}

	public void setCurrentNum(String currentTitle ){
		this.currentTitle = currentTitle ;
	}

	public void setCurrentDetail(String currentDetail){
		this.currentDetail= currentDetail ;
	}


	@Override
	public void onLoadComplete(String url, TextureRegion texture, Object tag) {
		if (this.url.equals(url)) {
			imageHolder.addAction(Actions.fadeOut(0f));
			imageHolder.setDrawable(new TextureRegionDrawable(texture));
			imageHolder.addAction(Actions.fadeIn(0.6f));
			imageHolderLogo.setVisible(false);
		} else {
			texture.getTexture().dispose();
		}
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		label.setMarquee(getFocus);
		focusImg.setVisible(getFocus);
		if(getFocus){
			bottomShadowImage.setDrawableResource(R.drawable.bottom_blue_bg);
			scoreBg.setDrawableResource(R.mipmap.score_bg);
		}else{
			bottomShadowImage.setDrawableResource(R.drawable.bottom_dark_bg);
			scoreBg.setDrawableResource(R.mipmap.score_bg2);
		}
		focusImg.toFront();
		super.notifyFocusChanged(getFocus);
	}

	public void setScoreVisiable(boolean bool){
		scoreNum1.setVisible(bool);
		scoreNum2.setVisible(bool);
		scoreBg.setVisible(bool);
	}

	public void setScoreNum2Visiable(boolean bool){
		scoreNum2.setVisible(bool);
	}

	public void setScoreNum1(String num){
		scoreNum1.setText(num + ".");
	}


	public void setScoreNum2(String num){
		scoreNum2.setText(num);
	}


	public void setUpdateListener(UpdateProgramDetail detailListener){
		this.detailListener = detailListener;
	}

	public void update(String url) {
		if (url == null) {
			return;
		}
		if (url.isEmpty()) {
			return;
		}
		this.url = url;

		// 在grid滑动时，恢复默认的背景
		imageHolder.setDrawableResource(R.drawable.placeholder);

		if (pageImageLoader != null) {
			pageImageLoader.reuse();
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmap(url, "list", this, url);
	}

	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
	}

	@Override
	public void onSelected(boolean isSelected) {
		if(isSelected){
			detailListener.updateDetail(currentTitle,currentDetail);
		}
	}

	public void setText(String text,int num) {
		label.setText(text);
		this.text = text;
		if(num == 0){
			topNum.setText(1+"");
			topTip.setVisible(true);
			topNum.setVisible(true);
		}else if(num == 1){
			topNum.setText(2+"");
			topTip.setVisible(true);
			topNum.setVisible(true);
		}else if(num == 2){
			topNum.setText(3+"");
			topTip.setVisible(true);
			topNum.setVisible(true);
		}else{
			topTip.setVisible(false);
			topNum.setVisible(false);
		}
	}

	@Override
	public void onResume() {
		imageHolder.setDrawableResource(R.drawable.placeholder);
		focusImg.setDrawable(new NinePatchDrawable(new NinePatch(findRegion(R.drawable.list_foucs), 45, 45, 45, 45)));
		label.setText(text);
		update(url);
		super.onResume();
	}

}
