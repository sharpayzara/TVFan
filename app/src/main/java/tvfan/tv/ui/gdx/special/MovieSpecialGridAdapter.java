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
public class MovieSpecialGridAdapter extends GridAdapter {
	private Page page;
	private int mItemWidth=450;
	private int mItemHeight=600;
	List<ProgramListItem> programList = new ArrayList<ProgramListItem>();;

	public MovieSpecialGridAdapter(Page page) {
		this.page = page;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		MovieSpecialGridItem item = null;

		if (convertActor == null) {
			item = new MovieSpecialGridItem(page);
		} else {
			item = (MovieSpecialGridItem) convertActor;
		}
		item.setScale(1f);
		item.update(programList.get(position).getPostImg());
		item.setText(programList.get(position).getPostName());
		item.setLeftCornerImage(programList.get(position).getCornerPrice());
		item.setRightCornerImage(programList.get(position).getCornerType());
		return item;
	}

	public void setData(List<ProgramListItem> list) {
		this.programList = list;
	}

	class MovieSpecialGridItem extends Group implements IListItem, LoaderListener {
		private Image image, cornerImg, buyImg;
		private PageImageLoader pageImageLoader;
		private int rightTr;
		private int leftTr;
		private Image focusImg, shadowImg;
		private Label label;
		private CullGroup cullGroup;
		private String url;
		private String text;

		public MovieSpecialGridItem(Page page) {
			super(page);
			setSize(mItemWidth, mItemHeight);
			setFocusAble(true);
			setFocusScale(AppGlobalConsts.FOCUSSCALE);
			image = new Image(getPage());
			image.setSize(mItemWidth, mItemHeight);
			image.setPosition(0, 0);
			image.setDrawableResource(R.drawable.list_mr);
			addActor(image);
			// 海报阴影
			shadowImg = NinePatchImage.make(page,
					findTexture(R.drawable.bannerbj), new int[] { 10, 10, 10,
							10 });
			shadowImg.setSize(mItemWidth, 150);
			shadowImg.setPosition(0, 0);
			shadowImg.setDrawableResource(R.drawable.postshadow);
			addActor(shadowImg);

			// 外框
			cullGroup = new CullGroup(getPage());
			cullGroup.setSize(400, 60);
			cullGroup.setPosition(10, 10);
			cullGroup.setCullingArea(new Rectangle(0, 0, 400, 60));
			addActor(cullGroup);

			buyImg = new Image(page);
			buyImg.setSize(100, 100);
			buyImg.setPosition(0, mItemHeight-100);
			buyImg.setFocusAble(false);
			buyImg.setVisible(false);
			addActor(buyImg);

			cornerImg = new Image(page);
			cornerImg.setSize(100, 100);
			cornerImg.setPosition(200, 200);
			cornerImg.setFocusAble(false);
			cornerImg.setVisible(false);
			addActor(cornerImg);

			label = new Label(getPage(), false);
			label.setSize(mItemWidth-10, 40);
			label.setTextSize(38);
			label.setPosition(0, 20);
			label.setMarquee(false);
			label.setColor(Color.WHITE);
			label.setAlpha(0.8f);
			label.setAlignment(Align.center);
			cullGroup.addActor(label);

			// 光标选中效果
			focusImg = NinePatchImage.make(page,findTexture(R.drawable.list_foucs), new int[]{45,45,45,45});
			focusImg.setSize(mItemWidth+88, mItemHeight+88);
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

		public void setLeftCornerImage(String tr) {
			buyImg.setVisible(false);
			int id = Utils.getSuperScriptImageResourceId(page, "left", tr);
			leftTr = id;
			if (id != -1) {
				TextureRegion ct = findRegion(id);
				buyImg.setSize(ct.getRegionWidth(), ct.getRegionWidth());
				buyImg.setPosition(0, mItemHeight - ct.getRegionHeight());
				buyImg.setVisible(true);
				buyImg.setDrawable(ct);
			}
		}

		public void setRightCornerImage(String tr) {
			cornerImg.setVisible(false);
			int id = Utils.getSuperScriptImageResourceId(page, "right", tr);
			rightTr = id;
			if (id != -1) {
				TextureRegion cp = findRegion(id);
				cornerImg.setSize(cp.getRegionWidth(), cp.getRegionWidth());
				cornerImg.setPosition(mItemWidth - cp.getRegionWidth(),
						mItemHeight - cp.getRegionHeight());
				cornerImg.setVisible(true);
				cornerImg.setDrawable(cp);
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
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return programList.size();
	}

}
