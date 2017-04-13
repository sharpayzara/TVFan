package tvfan.tv.ui.gdx.search;

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

public class SearchGridItem extends Group implements IListItem, LoaderListener {
	private Image image;
	private Image focusImg;
	private Image shadowImg;
	private Label label;
	private PageImageLoader pageImageLoader;
	private CullGroup cullGroup;
	private String url;
	private String text;
	private Image cornerImg, buyImg;
	private int rightTr;
	private int leftTr;
	private Page page;

	public SearchGridItem(Page page, int cullx, int cully) {
		super(page);
		setSize(300, 400);
		setFocusAble(true);
		setFocusScale(AppGlobalConsts.FOCUSSCALE);
		this.page = page;
		// 海报图片
		image = new Image(getPage());
		image.setSize(300, 400);
		image.setPosition(0, 0);
		image.setDrawableResource(R.drawable.list_mr);
		// 海报阴影
		shadowImg = NinePatchImage.make(getPage(),
				findTexture(R.drawable.bannerbj), new int[] { 10, 10, 10, 10 });
		shadowImg.setSize(300, 400);
		shadowImg.setPosition(0, 0);
		shadowImg.setDrawableResource(R.drawable.postshadow);
		// 海报字幕
		label = new Label(getPage(), false);
		label.setPosition(0, 10);
		label.setSize(280, 40);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.center);
		label.setTextSize(40);
		label.setAlpha(0.9f);
		label.setMarquee(false);

		// 外框
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(280, 60);
		cullGroup.setPosition(10, 10);
		cullGroup.setCullingArea(new Rectangle(0, 0, 280, 60));
		cullGroup.addActor(label);

		// 光标选中效果
		focusImg = NinePatchImage.make(getPage(),
				findTexture(R.drawable.list_foucs),
				new int[] { 45, 45, 45, 45 });
		focusImg.setSize(300 + 88, 400 + 88);
		focusImg.setPosition(-44, -45);
		focusImg.setVisible(false);

		buyImg = new Image(getPage());
		buyImg.setSize(100, 100);
		buyImg.setPosition(0, 300);
		buyImg.setFocusAble(false);
		buyImg.setVisible(false);

		cornerImg = new Image(getPage());
		cornerImg.setSize(100, 100);
		cornerImg.setPosition(200, 200);
		cornerImg.setFocusAble(false);
		cornerImg.setVisible(false);

		// 载入场景
		addActor(image);
		addActor(shadowImg);
		addActor(cullGroup);
		addActor(focusImg);
		addActor(cornerImg);
		addActor(buyImg);
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
		image.setDrawableResource(R.drawable.list_mr);

		if (pageImageLoader != null) {
			pageImageLoader.reuse();
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
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
		image.clearActions();
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		label.setMarquee(getFocus);
		focusImg.setVisible(getFocus);
		if (getFocus) {
			focusImg.addAction(Actions.fadeIn(0.1f));
		} else {
			focusImg.addAction(Actions.fadeOut(0.1f));
		}
	}

	@Override
	public void onLoadComplete(String imageUrl, TextureRegion textureRegion,
			Object imageTag) {
		// TODO Auto-generated method stub
		if (this.url.equals(url)) {
			image.addAction(Actions.fadeOut(0f));
			image.setDrawable(new TextureRegionDrawable(textureRegion));
			image.addAction(Actions.fadeIn(0.6f));
		} else {
			textureRegion.getTexture().dispose();
		}
	}

	public void setLeftCornerImage(String tr) {
		buyImg.setVisible(false);
		int id = Utils.getSuperScriptImageResourceId(page, "left", tr);
		leftTr = id;
		if (id != -1) {
			TextureRegion ct = findRegion(id);
			buyImg.setSize(ct.getRegionWidth(), ct.getRegionWidth());
			buyImg.setPosition(0, 400 - ct.getRegionHeight());
			buyImg.setVisible(true);
			buyImg.setDrawable(ct);
		}else{
			buyImg.setVisible(false);
		}
	}

	public void setRightCornerImage(String tr) {
		cornerImg.setVisible(false);
		int id = Utils.getSuperScriptImageResourceId(page, "right", tr);
		rightTr = id;
		if (id != -1) {
			TextureRegion cp = findRegion(id);
			cornerImg.setSize(cp.getRegionWidth(), cp.getRegionWidth());
			cornerImg.setPosition(300 - cp.getRegionWidth(),
					400 - cp.getRegionHeight());
			cornerImg.setVisible(true);
			cornerImg.setDrawable(cp);
		}else{
			cornerImg.setVisible(false);
		}
	}

	@Override
	public void onResume() {
		Utils.resetImageSource(image, R.drawable.list_mr);
		shadowImg.setDrawable(new NinePatchDrawable(new NinePatch(
				findRegion(R.drawable.bannerbj), 10, 10, 10, 10)));
		Utils.resetImageSource(focusImg, R.drawable.list_foucs);
		label.setText(text);
		update(url);
		if (leftTr != -1 && Utils.isResourceRecycled(buyImg))
			buyImg.setDrawableResource(leftTr);
		if (rightTr != -1 && Utils.isResourceRecycled(cornerImg))
			cornerImg.setDrawableResource(rightTr);
		super.onResume();
	}

	@Override
	public void onSelected(boolean isfocus) {

	}
}
