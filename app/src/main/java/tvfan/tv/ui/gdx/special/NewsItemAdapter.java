package tvfan.tv.ui.gdx.special;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.luxtone.lib.widget.Grid.GridAdapter;

/**
 * 專題匯總界面
 * 
 * @author 孫文龍
 * 
 */
public class NewsItemAdapter extends GridAdapter {
	private Page page;
	List<ProgramListItem> programList = new ArrayList<ProgramListItem>();

	public NewsItemAdapter(Page page) {
		this.page = page;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		SpecialGridItem item = null;

		if (convertActor == null) {
			item = new SpecialGridItem(page);
		} else {
			item = (SpecialGridItem) convertActor;
		}
		item.setScale(1f);
		item.update(programList.get(position).getPostImg());
		item.setText(programList.get(position).getPostName());
		return item;
	}

	public void setData(List<ProgramListItem> list) {
		this.programList = list;
	}

	class SpecialGridItem extends Group implements IListItem, LoaderListener {
		private Image image;
		private PageImageLoader pageImageLoader;
		private String url;
		private String text;
		private Image focusImg, shadowImg;
		private Label label;
		private CullGroup cullGroup;

		public SpecialGridItem(Page page) {
			super(page);
			setSize(400, 300);
			setFocusAble(true);
			setFocusScale(AppGlobalConsts.FOCUSSCALE);
			image = new Image(getPage());
			image.setSize(400, 300);
			image.setPosition(0, 0);
			image.setDrawableResource(R.drawable.list_mr);
			addActor(image);
			// 海报阴影
			shadowImg = NinePatchImage.make(page,
					findTexture(R.drawable.bannerbj), new int[] { 10, 10, 10,
							10 });
			shadowImg.setSize(400, 300);
			shadowImg.setPosition(0, 0);
			shadowImg.setDrawableResource(R.drawable.postshadow);
			addActor(shadowImg);

			// 外框
			cullGroup = new CullGroup(getPage());
			cullGroup.setSize(380, 60);
			cullGroup.setPosition(10, 10);
			cullGroup.setCullingArea(new Rectangle(0, 0, 380, 60));
			addActor(cullGroup);

			label = new Label(getPage(), false);
			label.setSize(400, 40);
			label.setTextSize(40);
			label.setPosition(0, 10);
			label.setMarquee(false);
			label.setColor(Color.WHITE);
			label.setAlpha(0.8f);
			label.setAlignment(Align.center);
			cullGroup.addActor(label);

			// 光标选中效果
			focusImg = NinePatchImage.make(page,
					findTexture(R.drawable.list_foucs), new int[] { 45, 45, 45,
							45 });
			focusImg.setSize(400 + 88, 300 + 88);
			focusImg.setPosition(-44, -45);
			focusImg.setVisible(false);
			addActor(focusImg);
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
		public void onRecycle() {
			label.setText("");
			label.setMarquee(false);
			image.clearActions();
		}

		@Override
		public void onSelected(boolean isSelected) {

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
				pageImageLoader.cancelLoad();
			}
			pageImageLoader = new PageImageLoader(getPage());
			pageImageLoader.startLoadBitmap(this.url, "list", this, "postimg");
		}

		public void setText(String text) {
			if (text == null || text.equals("")) {
				shadowImg.setVisible(false);
			} else {
				shadowImg.setVisible(true);
				label.setText(text);
			}

			this.text = text;
		}

		@Override
		public void onLoadComplete(String imageUrl,
				TextureRegion textureRegion, Object imageTag) {
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
		public void onResume() {
			Utils.resetImageSource(image, R.drawable.list_mr);
			shadowImg.setDrawable(new NinePatchDrawable(new NinePatch(
					findRegion(R.drawable.bannerbj), 10, 10, 10, 10)));
			Utils.resetImageSource(focusImg, R.drawable.list_foucs);
			if (text == null || text.equals("")) {
				shadowImg.setVisible(false);
			} else {
				shadowImg.setVisible(true);
				label.setText(text);
			}
			update(url);
			super.onResume();
		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return programList.size();
	}

}
