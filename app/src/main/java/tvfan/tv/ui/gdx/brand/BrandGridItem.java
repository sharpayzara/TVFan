package tvfan.tv.ui.gdx.brand;

import tvfan.tv.AppGlobalConsts;
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
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

public class BrandGridItem extends Group implements IListItem, LoaderListener {
	private Image image,imageHolder;
	private Label label;
	private CullGroup cullGroup;
	private PageImageLoader pageImageLoader;
	private Image shadowImg;

	private Image focusImg;
	private String url;
	private String text;

	public BrandGridItem(Page page) {
		super(page);
		setSize(300, 280);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setPosition(0, 0);
		cullGroup.setSize(300, 280);
		cullGroup.setCullingArea(new Rectangle(0, 0, 300, 280));
		addActor(cullGroup);
		
		
		imageHolder = new Image(page);
		imageHolder.setDrawableResource(R.drawable.placeholder);	
		imageHolder.setSize(300, 280);
		imageHolder.setPosition(0, 0);
		cullGroup.addActor(imageHolder);

		image = new Image(page);
		image.setSize(172, 60);
		image.setPosition((300-172)/2, (280-60)/2);
		image.setVisible(true);
		image.setDrawableResource(R.drawable.placeholder_logo);
		cullGroup.addActor(image);

		// shadowImg = NinePatchImage.make(page,
		// findTexture(R.drawable.bannerbj), new int[]{10,10,10,10});
		shadowImg = new Image(getPage());
		shadowImg.setSize(300, 280);
		shadowImg.setPosition(0, 0);
		shadowImg.setVisible(true);
		shadowImg.setDrawableResource(R.drawable.postshadow);
		cullGroup.addActor(shadowImg);

		label = new Label(getPage(), false);
		label.setPosition(10, 20);
		label.setSize(280, 30);
		label.setTextSize(40);
		label.setMaxLine(1);
//		label.setAlpha(0.6f);
		label.setVisible(true);
		label.setColor(Color.valueOf("f0f0f0"));
		label.setAlignment(Align.center);
		cullGroup.addActor(label);

		// 光标选中效果
		focusImg = NinePatchImage.make(page,
				findTexture(R.drawable.list_foucs),
				new int[] { 45, 45, 45, 45 });
		focusImg.setSize(300+88, 280+88);
		focusImg.setPosition(-44, -44);
		focusImg.setVisible(false);
		addActor(focusImg);

		setFocusScale(AppGlobalConsts.FOCUSSCALE);
	}

	@Override
	public void onLoadComplete(String url, TextureRegion texture, Object tag) {
		if (this.url.equals(url)) {
			imageHolder.addAction(Actions.fadeOut(0f));
			imageHolder.setDrawable(new TextureRegionDrawable(texture));
			imageHolder.addAction(Actions.fadeIn(0.6f));
			image.setVisible(false);
		} else {
			texture.getTexture().dispose();
		}
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		label.setMarquee(getFocus);
		focusImg.setVisible(getFocus);
		// label.setVisible(getFocus);
		super.notifyFocusChanged(getFocus);
//		if (getFocus) {
//			label.setAlpha(0.8f);
//		} else {
//			label.setAlpha(0.6f);
//		}
	}

	public void update(String url) {
		if (url == null) {
			return;
		}
		if (url.isEmpty()) {
			return;
		}
		this.url = url;
		
		imageHolder.setDrawableResource(R.drawable.placeholder);
		image.setVisible(true);
		

		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmap(url, "list", this, url);
		// pageImageLoader.startLoadBitmap(url, "list", true,
		// AppGlobalConsts.CUTLENGTH + 2, this, url);
		// label.setText(url);
	}

	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
		image.clearActions();
	}

	@Override
	public void onSelected(boolean isSelected) {

	}

	public void setText(String text) {
		this.text = text;
		label.setText(text);
	}

	@Override
	public void onResume() {
		super.onResume();
		label.setText(text);
		if (!url.isEmpty())
			update(url);
		imageHolder.setDrawableResource(R.drawable.placeholder);
		image.setDrawableResource(R.drawable.placeholder_logo);
		Utils.resetImageSource(shadowImg, R.drawable.postshadow);
		if (Utils.isResourceRecycled(focusImg))
			focusImg.setDrawable(new NinePatchDrawable(new NinePatch(
					findRegion(R.drawable.list_foucs),45, 45, 45, 45)));
	}

}
