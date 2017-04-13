package tvfan.tv.ui.gdx.programDetail.item;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

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

public class DetailGridItem extends Group implements IListItem, LoaderListener {
	private Page page;
	private Image image,imageHolder;
	private Label label;
	private CullGroup cullGroup;
	private PageImageLoader pageImageLoader;
	private Image focusImg;
	private Image scoreBack;
	private TVFANLabel score1, score2;
	private String url;
	private String text;

	public DetailGridItem(Page page) {
		super(page);
		this.page = page;
		setSize(180.0f, 285.0f);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setPosition(0.0f, 0.0f);
		cullGroup.setSize(180.0f, 285.0f);
		cullGroup.setCullingArea(new Rectangle(0.0f, 0.0f, 180.0f, 285.0f));
		addActor(cullGroup);
		
		
		imageHolder = new Image(page);
		imageHolder.setDrawableResource(R.drawable.placeholder);	
		imageHolder.setSize(180.0f, 240.0f);
		imageHolder.setPosition(0.0f, 45.0f);
		cullGroup.addActor(imageHolder);

		image = new Image(page);
		image.setSize(180.0f, 45.0f);
		image.setVisible(true);
		image.setDrawableResource(R.drawable.bottom_dark_bg);
		cullGroup.addActor(image);

		label = new Label(getPage(), false);
		label.setSize(180.0f, 45.0f);
		label.setTextSize(22);
		label.setMaxLine(1);
		label.setVisible(true);
		label.setColor(Color.valueOf("ffffff"));
		label.setAlignment(Align.center);
		cullGroup.addActor(label);

		//评分
		scoreBack = new Image(page);
		scoreBack.setPosition(135.0f, 255.0f);
		scoreBack.setSize(45.0f, 30.0f);
		scoreBack.setDrawableResource(R.mipmap.score_bg2);
		scoreBack.toFront();
		cullGroup.addActor(scoreBack);

		score1 = new TVFANLabel(page);
		score1.setPosition(135.0f, 258.0f);
		score1.setSize(21.0f, 28.0f);
		score1.setAlignment(Align.right);
		score1.setTextSize(22);
		score1.toFront();
		cullGroup.addActor(score1);

		score2 = new TVFANLabel(page);
		score2.setPosition(156.0f, 258.0f);
		score2.setSize(22.0f, 28.0f);
		score2.setAlignment(Align.left);
		score2.setTextSize(18);
		score2.toFront();
		cullGroup.addActor(score2);

		// 光标选中效果
		focusImg = NinePatchImage.make(page,findTexture(R.mipmap.detail_foucs), new int[]{45,45,45,45});
		focusImg.setSize(180, 285);
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
		} else {
			texture.getTexture().dispose();
		}
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		label.setMarquee(getFocus);
		focusImg.setVisible(getFocus);
		if(getFocus) {
			image.setDrawableResource(R.mipmap.detai_label_normal);
			scoreBack.setDrawableResource(R.mipmap.list_scores_icon_background_selected);
		}
		else {
			image.setDrawableResource(R.drawable.bottom_dark_bg);
			scoreBack.setDrawableResource(R.mipmap.score_bg2);
		}
		super.notifyFocusChanged(getFocus);
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
		image.setVisible(true);

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

	}

	public void setText(String text) {
		label.setText(text);
		this.text = text;
	}

	public void setScore1(String score) {
		String score_1 = score.substring(0,1);
		String score_2 = score.substring(1,score.length());

		this.score1.setText(score_1);
		this.score2.setText(score_2);
	}

	@Override
	public void onResume() {
		imageHolder.setDrawableResource(R.drawable.placeholder);
		image.setDrawableResource(R.mipmap.detai_label_normal);
		focusImg.setDrawable(new NinePatchDrawable(new NinePatch(findRegion(R.drawable.list_foucs), 45, 45, 45, 45)));
		label.setText(text);
		update(url);
		super.onResume();
	}

}
