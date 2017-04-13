package tvfan.tv.ui.gdx.userCenter;

import tvfan.tv.lib.GdxPageImageBatchLoader;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;
import tvfan.tv.R;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.AbsListView.IListItem;

public class FavoritesGridItem extends Group implements IListItem,
		LoaderListener {
	private Image image,logoImage, cornerImg, buyImg, imagenew;
	private PageImageLoader pageImageLoader;
	private GdxPageImageBatchLoader gpibl;
	private int rightTr;
	private int leftTr;
	private Image focusImg, shadowImg;
	private Label label;
	private CullGroup cullGroup;
	private String url;
	private String text;
	private Page page;

	public FavoritesGridItem(Page page) {
		super(page);
		this.page = page;
		setSize(300, 400);
		setFocusAble(true);
		setOrigin(150, 200);
		image = new Image(getPage());
		image.setSize(300, 400);
		image.setPosition(0, 0);
		image.setDrawableResource(R.drawable.placeholder);
		addActor(image);
		
		logoImage = new Image(getPage());
		logoImage.setSize(137.6f, 48f);
		logoImage.setPosition((float) ((300-137.6)/2), (400-48)/2);
		logoImage.setDrawableResource(R.drawable.placeholder_logo);
		logoImage.setVisible(true);
		addActor(logoImage);
		// 海报阴影
		shadowImg = NinePatchImage.make(page, findTexture(R.drawable.bannerbj),
				new int[] { 10, 10, 10, 10 });
		shadowImg.setSize(300, 400);
		shadowImg.setPosition(0, 0);
		shadowImg.setDrawableResource(R.drawable.postshadow);
		addActor(shadowImg);

		// 最近更新脚标图片
		imagenew = new Image(getPage());
		imagenew.setSize(100, 100);
		imagenew.setPosition(200, 300);
		imagenew.setDrawableResource(R.drawable.jiaob_1);
		imagenew.setVisible(false);
		addActor(imagenew);

		buyImg = new Image(page);
		buyImg.setSize(100, 100);
		buyImg.setPosition(0, 300);
		buyImg.setFocusAble(false);
		buyImg.setVisible(false);
		addActor(buyImg);

		cornerImg = new Image(page);
		cornerImg.setSize(100, 100);
		cornerImg.setPosition(200, 200);
		cornerImg.setFocusAble(false);
		cornerImg.setVisible(false);
		addActor(cornerImg);

		// 外框
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(280, 60);
		cullGroup.setPosition(10, 10);
		cullGroup.setCullingArea(new Rectangle(0, 0, 280, 60));
		addActor(cullGroup);

		label = new Label(getPage(), false);
		label.setSize(300, 40);
		label.setTextSize(40);
		label.setPosition(0, 10);
		label.setMarquee(false);
		label.setColor(Color.WHITE);
		label.setAlpha(0.8f);
		label.setAlignment(Align.center);
		cullGroup.addActor(label);

		// 光标选中效果
		focusImg = NinePatchImage.make(page,
				findTexture(R.drawable.list_foucs),
				new int[] { 45, 45, 45, 45 });
		focusImg.setSize(300 + 80, 400 + 80);
		focusImg.setPosition(-42, -42);
		focusImg.setVisible(false);
		addActor(focusImg);
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		label.setMarquee(getFocus);
		focusImg.setVisible(getFocus);
	}

	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
		image.clearActions();
		logoImage.clearActions();
		this.clearActions();
		this.setScale(1, 1);
	}

	@Override
	public void onSelected(boolean isSelected) {
		if (isSelected) {
			addAction(Actions.sequence(Actions.scaleTo(1.0f, 1.0f),
					Actions.scaleTo(1.1f, 1.1f, 0.3f)));
		} else {
			addAction(Actions.sequence(Actions.scaleTo(1.1f, 1.1f),
					Actions.scaleTo(1.0f, 1.0f, 0.3f)));
		}
	}
	
	public void update(String url) {
		if (url == null) {
			image.setDrawableResource(R.drawable.placeholder);
			logoImage.setVisible(true);
			return;
		}
		if (url.isEmpty()) {
			image.setDrawableResource(R.drawable.placeholder);
			logoImage.setVisible(true);
			return;
		}
		this.url = url;

		// 在grid滑动时，恢复默认的背景
		//image.setDrawableResource(R.drawable.list_mr);
		image.setDrawableResource(R.drawable.placeholder);
		logoImage.setVisible(true);
		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmap(this.url, "list", this, "postimg");
	}

	public void setText(String text) {
		label.setText(text);
		this.text = text;
	}

	@Override
	public void onLoadComplete(String imageUrl, TextureRegion textureRegion,
			Object imageTag) {
		// TODO Auto-generated method stub
		if (this.url.equals(url)) {
			image.addAction(Actions.fadeOut(0f));
			image.setDrawable(new TextureRegionDrawable(textureRegion));
			image.addAction(Actions.fadeIn(0.6f));
			logoImage.setVisible(false);
		} else {
			textureRegion.getTexture().dispose();
		}
	}

	public void setLeftCornerImage(String tr) {
		// wanqi,20150822,gridview的item内在设置各项值之前要全部恢复到默认值，否则会因为getActor的机制导致混乱.
		// 解决测试发现我的收藏内的付费角标出现混乱现象。
		buyImg.setVisible(false);  //很重要
		
		int id = Utils.getSuperScriptImageResourceId(page, "left", tr);
		leftTr = id;
		if (id != -1) {
			TextureRegion ct = findRegion(id);
			buyImg.setSize(ct.getRegionWidth(), ct.getRegionWidth());
			buyImg.setPosition(0, 400 - ct.getRegionHeight());
			buyImg.setVisible(true);
			buyImg.setDrawable(ct);
		}
	}

	public void setRightCornerImage(String tr) {
		// wanqi,20150822,gridview的item内在设置各项值之前要全部恢复到默认值，否则会因为getActor的机制导致混乱.
		// 解决测试发现我的收藏内的付费角标出现混乱现象。
		cornerImg.setVisible(false); //很重要
		
		int id = Utils.getSuperScriptImageResourceId(page, "right", tr);
		rightTr = id;
		if (id != -1) {
			TextureRegion cp = findRegion(id);
			cornerImg.setSize(cp.getRegionWidth(), cp.getRegionWidth());
			cornerImg.setPosition(300 - cp.getRegionWidth(),
					400 - cp.getRegionHeight());
			cornerImg.setVisible(true);
			cornerImg.setDrawable(cp);
			imagenew.setVisible(false);
		}
	}

	public void setNewImageGone(boolean isfocus) {
		imagenew.setVisible(isfocus);
	}

	@Override
	public void onResume() {
		//Utils.resetImageSource(image, R.drawable.list_mr);
		Utils.resetImageSource(image, R.drawable.placeholder);
		Utils.resetImageSource(logoImage, R.drawable.placeholder_logo);
		shadowImg.setDrawable(new NinePatchDrawable(new NinePatch(
				findRegion(R.drawable.bannerbj), 10, 10, 10, 10)));
		Utils.resetImageSource(focusImg, R.drawable.list_foucs);
		Utils.resetImageSource(imagenew, R.drawable.jiaob_1);
		label.setText(text);
		update(url);
		if (leftTr != -1 && Utils.isResourceRecycled(buyImg))
			buyImg.setDrawableResource(leftTr);
		if (rightTr != -1 && Utils.isResourceRecycled(cornerImg))
			cornerImg.setDrawableResource(rightTr);
		super.onResume();
	}
	
}
