package tvfan.tv.ui.gdx.userCenter;

/**
 * 個人中心-播放歷史-grid元素
 * 
 * @author 孫文龍
 * 
 */
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

public class PlayHistoryGridItem extends Group implements IListItem,
		LoaderListener {
	private Page page;
	private Image image, logoImage, buyImg, cornerImg;
	private Image focusImg;
	private Image shadowImg;
	private Label label, labelTime;
	private PageImageLoader pageImageLoader;
	private CullGroup titlecullGroup;
	private String url;
	private String time;
	private String name;
	private int rightTr;
	private int leftTr;

	public PlayHistoryGridItem(Page page, int cullx, int cully) {
		super(page);
		this.page = page;
		setSize(300, 400);
		setFocusAble(true);
		setOrigin(150, 200);
		// 海报图片
		image = new Image(getPage());
		image.setSize(300, 400);
		image.setPosition(0, 0);
		image.setDrawableResource(R.drawable.list_mr);
		addActor(image);

		//电视粉logo图片
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

		/*
		 * // 外框 gkzcullGroup = new CullGroup(getPage());
		 * gkzcullGroup.setSize(260, 53); gkzcullGroup.setPosition(10, 80);
		 * gkzcullGroup.setCullingArea(new Rectangle(0, 0, 260, 53));
		 * addActor(gkzcullGroup);
		 */

		/*
		 * // 观看至 labelTime = new Label(getPage(), false);
		 * labelTime.setPosition(20, 0); labelTime.setSize(260, 33);
		 * labelTime.setColor(Color.WHITE); labelTime.setText("观看至");
		 * labelTime.setTextSize(35); labelTime.setAlpha(0.9f);
		 * labelTime.setMarquee(false); labelTime.setAlignment(Align.left);
		 * gkzcullGroup.addActor(labelTime);
		 */

		// 时间
		labelTime = new Label(getPage(), false);
		labelTime.setPosition(20, 80);
		labelTime.setSize(260, 33);
		labelTime.setColor(Color.YELLOW);
		labelTime.setTextSize(35);
		labelTime.setAlpha(0.8f);
		labelTime.setMarquee(false);
		labelTime.setAlignment(Align.center);
		addActor(labelTime);

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
		titlecullGroup = new CullGroup(getPage());
		titlecullGroup.setSize(280, 53);
		titlecullGroup.setPosition(10, 20);
		titlecullGroup.setCullingArea(new Rectangle(0, 0, 280, 53));
		addActor(titlecullGroup);

		// 海报字幕
		label = new Label(getPage(), false);
		label.setPosition(0, 0);
		label.setSize(280, 53);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.center);
		label.setSize(280, 53);
		label.setTextSize(40);
		label.setAlpha(0.8f);
		label.setMarquee(false);
		titlecullGroup.addActor(label);

		// 光标选中效果
		focusImg = NinePatchImage.make(page,
				findTexture(R.drawable.list_foucs),
				new int[] { 45, 45, 45, 45 });
		focusImg.setSize(300 + 80, 400 + 80);
		focusImg.setPosition(-42, -42);
		focusImg.setVisible(false);
		addActor(focusImg);
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
		image.setDrawableResource(R.drawable.placeholder);
		logoImage.setVisible(true);
		if (pageImageLoader != null) {
			pageImageLoader.reuse();
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmap(this.url, "list", this, "postimg");
	}

	public void setText(String name, String time) {
		this.time = time.contains(":") ? time : "第" + time + "集";
		label.setText(name);
		labelTime.setText("观看至" + this.time);
		this.name = name;
		/*
		 * if (time.contains(":")) { this.time = time; gkzcullGroup.setSize(260,
		 * 53); gkzcullGroup.setPosition(20, 80); labelTime.setSize(260, 33);
		 * labelTime1.setSize(260, 33); } else { this.time = "第12" + time + "集";
		 * gkzcullGroup.setSize(210, 53); gkzcullGroup.setPosition(45, 80);
		 * labelTime.setSize(210, 33); labelTime1.setSize(210, 33); }
		 */
	}

	public void setLeftCornerImage(String tr) {
		buyImg.setVisible(false);
		if (tr == null || tr.equals(""))
			return;
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
		cornerImg.setVisible(false);
		if (tr == null || tr.equals(""))
			return;
		int id = Utils.getSuperScriptImageResourceId(page, "right", tr);
		rightTr = id;
		if (id != -1) {
			TextureRegion cp = findRegion(id);
			cornerImg.setSize(cp.getRegionWidth(), cp.getRegionWidth());
			cornerImg.setPosition(300 - cp.getRegionWidth(),
					400 - cp.getRegionHeight());
			cornerImg.setVisible(true);
			cornerImg.setDrawable(cp);
		}
	}

	@Override
	public void onRecycle() {
		labelTime.setText("");
		label.setText("");
		label.setMarquee(false);
		image.clearActions();
		logoImage.clearActions();
		this.clearActions();
		this.setScale(1, 1);
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		label.setMarquee(getFocus);
		focusImg.setVisible(getFocus);
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

	@Override
	public void onResume() {
		Utils.resetImageSource(image, R.drawable.list_mr);
		Utils.resetImageSource(logoImage, R.drawable.placeholder_logo);
		shadowImg.setDrawable(new NinePatchDrawable(new NinePatch(
				findRegion(R.drawable.bannerbj), 10, 10, 10, 10)));
		Utils.resetImageSource(focusImg, R.drawable.list_foucs);
		labelTime.setText("观看至" + time);
		label.setText(name);
		update(url);
		if (leftTr != -1 && Utils.isResourceRecycled(buyImg))
			buyImg.setDrawableResource(leftTr);
		if (rightTr != -1 && Utils.isResourceRecycled(cornerImg))
			cornerImg.setDrawableResource(rightTr);
		super.onResume();
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
}
