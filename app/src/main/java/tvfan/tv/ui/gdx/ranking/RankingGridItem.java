package tvfan.tv.ui.gdx.ranking;

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

public class RankingGridItem extends Group implements IListItem, LoaderListener {
	private Image image, rankimage1, rankimage3;
	private Image focusImg;
	private Image shadowImg, buyImg;
	private Label label, labelNumber1, labelNumber3;
	private PageImageLoader pageImageLoader;
	private CullGroup cullGroup;
	private String url;
	private String text;
	private int leftTr;
	private Page page;

	// private Image preImg;

	public RankingGridItem(Page page) {
		super(page);
		this.page = page;
		setSize(300, 400);
		setFocusAble(true);
		setFocusScale(AppGlobalConsts.FOCUSSCALE);

		// 海报图片
		image = new Image(getPage());
		image.setSize(300, 400);
		image.setPosition(0, 0);
		image.setDrawableResource(R.drawable.list_mr);

		// 海报阴影
		shadowImg = NinePatchImage.make(page, findTexture(R.drawable.bannerbj),
				new int[] { 10, 10, 10, 10 });
		shadowImg.setSize(300, 400);
		shadowImg.setPosition(0, 0);
		shadowImg.setDrawableResource(R.drawable.postshadow);
		// 海报字幕
		label = new Label(getPage(), false);
		label.setPosition(0, 10);
		label.setSize(280, 40);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.center);
		label.setText("");
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
		focusImg = NinePatchImage.make(page,findTexture(R.drawable.list_foucs), new int[]{45,45,45,45});
		focusImg.setSize(300+88, 400+88);
		focusImg.setPosition(-44, -45);
		focusImg.setVisible(false);

		// 载入场景
		addActor(image);
		addActor(shadowImg);

		rankimage1 = new Image(getPage());
		rankimage1.setSize(80, 110);
		rankimage1.setDrawableResource(R.drawable.jiaob_rank1);
		rankimage1.setPosition(200, 300);
		rankimage1.setVisible(false);
		addActor(rankimage1);
		// 排行
		labelNumber1 = new Label(getPage(), false);
		labelNumber1.setPosition(240, 350);
		labelNumber1.setColor(Color.WHITE);
		labelNumber1.setTextSize(40);
		labelNumber1.setAlpha(0.9f);
		labelNumber1.setVisible(false);
		labelNumber1.setMarquee(false);
		addActor(labelNumber1);
		// 排行图片
		rankimage3 = new Image(getPage());
		rankimage3.setSize(80, 110);
		rankimage3.setDrawableResource(R.drawable.jiaob_rank3);
		rankimage3.setPosition(200, 300);
		rankimage3.setVisible(false);
		addActor(rankimage3);
		// 排行
		labelNumber3 = new Label(getPage(), false);
		labelNumber3.setPosition(240, 350);
		labelNumber3.setColor(Color.WHITE);
		labelNumber3.setTextSize(40);
		labelNumber3.setAlpha(0.9f);
		labelNumber3.setVisible(false);
		labelNumber3.setMarquee(false);
		addActor(labelNumber3);

		buyImg = new Image(page);
		buyImg.setSize(100, 100);
		buyImg.setPosition(0, 300);
		buyImg.setFocusAble(false);
		buyImg.setVisible(false);
		addActor(buyImg);
		addActor(cullGroup);
		addActor(focusImg);
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

	public void setText(String text, int pos) {
		this.text = text;
		label.setText(text);
		if (pos < 3) {
			labelNumber1.setText(String.valueOf(pos + 1));
			labelNumber1.setVisible(true);
			rankimage1.setVisible(true);
		} else if (pos < 5) {
			labelNumber3.setText(String.valueOf(pos + 1));
			labelNumber3.setVisible(true);
			rankimage3.setVisible(true);
		} else {
			labelNumber1.setText("");
			labelNumber1.setVisible(false);
			rankimage1.setVisible(false);
			labelNumber3.setText("");
			labelNumber3.setVisible(false);
			rankimage3.setVisible(false);
		}
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

	@Override
	public void onSelected(boolean isfocus) {

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
		}
	}
/*
	public void setRightCornerImage(String tr) {
		if(pos > 4){
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
		}else{
			cornerImg.setVisible(false);
		}
	}
*/
	@Override
	public void onResume() {
		Utils.resetImageSource(image, R.drawable.list_mr);
		shadowImg.setDrawable(new NinePatchDrawable(new NinePatch(
				findRegion(R.drawable.bannerbj), 10, 10, 10, 10)));
		Utils.resetImageSource(focusImg, R.drawable.list_foucs);
		Utils.resetImageSource(rankimage1, R.drawable.jiaob_rank1);
		Utils.resetImageSource(rankimage3, R.drawable.jiaob_rank3);
		label.setText(text);
		update(url);
		if (leftTr != -1 && Utils.isResourceRecycled(buyImg))
			buyImg.setDrawableResource(leftTr);
		super.onResume();
	}
}
