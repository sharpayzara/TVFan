package tvfan.tv.ui.gdx.programDetail.dialog;

import org.json.JSONArray;

import tvfan.tv.AppGlobalConsts;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class StagePhotoDialog extends Dialog {

	private Page page;
	private Image bg, leftIcon, rightIcon;
	private Grid grid;
	private Adapter adapter;
	private JSONArray images;
	

	public StagePhotoDialog(Page page,JSONArray images,int position) {
		super(page);
		
		this.page = page;
		this.images = images;
		bg = new Image(page);
		bg.setDrawableResource(R.drawable.default_background);
		bg.setPosition(0, 0);
		bg.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		bg.setFocusAble(false);
		addActor(bg);

		leftIcon = new Image(page);
		leftIcon.setDrawableResource(R.drawable.icon_left);
		leftIcon.setPosition(100, 502.5f);
		leftIcon.setSize(75, 75);
		leftIcon.setFocusAble(false);
		leftIcon.setVisible(false);
		addActor(leftIcon);

		rightIcon = new Image(page);
		rightIcon.setDrawableResource(R.drawable.icon_right);
		rightIcon.setPosition(1745, 502.5f);
		rightIcon.setSize(75, 75);
		rightIcon.setFocusAble(false);
		rightIcon.setVisible(false);
		addActor(rightIcon);

		grid = new Grid(page);
		grid.setRowNum(1);
		grid.setPosition(175, 0);
		grid.setSize(1570, AppGlobalConsts.APP_HEIGHT);
		grid.setGapLength(0);
		grid.setOrientation(Grid.ORIENTATION_LANDSPACE);
		adapter = new Adapter(page);
		adapter.setData(images);
		grid.setAdapter(adapter);
		grid.setOnItemSelectedChangeListener(onItemSelectedChangeListener);
		addActor(grid);
		
		
		grid.setSelection(position);
		if (position == 0) {
			leftIcon.setVisible(false);
		} else if (position > 0) {
			leftIcon.setVisible(true);
		}

		if (position < grid.getAdapterCount() - 1) {
			rightIcon.setVisible(true);
		} else if (position == grid.getAdapterCount() - 1) {
			rightIcon.setVisible(false);
		}

	}

	OnItemSelectedChangeListener onItemSelectedChangeListener = new OnItemSelectedChangeListener() {

		@Override
		public void onSelectedChanged(int position, Actor actor) {
			if (position == 0) {
				leftIcon.setVisible(false);
			} else if (position > 0) {
				leftIcon.setVisible(true);
			}

			if (position < grid.getAdapterCount() - 1) {
				rightIcon.setVisible(true);
			} else if (position == grid.getAdapterCount() - 1) {
				rightIcon.setVisible(false);
			}
		}
	};

	class Adapter extends GridAdapter {
		private Page page;
		private JSONArray items;

		public Adapter(Page page) {
			this.page = page;
		}

		public void setData(JSONArray items) {
			this.items = items;
		}

		@Override
		public Actor getActor(int position, Actor convertActor) {
			GridItem item = null;

			if (convertActor == null) {
				item = new GridItem(page);
			} else {
				item = (GridItem) convertActor;
			}
			item.setScale(1f);
			try {
				item.update(items.getString(position).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return item;
		}

		@Override
		public int getCount() {
			 return items.length();
//			return 10;
		}

	}

	class GridItem extends Group implements LoaderListener {
		private Image image;
		private CullGroup cullGroup;
		private PageImageLoader pageImageLoader;

		public GridItem(Page page) {
			super(page);
			setSize(1570, AppGlobalConsts.APP_HEIGHT);
			setFocusAble(true);
			cullGroup = new CullGroup(getPage());
			cullGroup.setSize(1570, AppGlobalConsts.APP_HEIGHT);
			cullGroup.setCullingArea(new Rectangle(0, 0,1570, AppGlobalConsts.APP_HEIGHT));
			addActor(cullGroup);

			image = new Image(getPage());
			image.setAlign(Align.center);
			image.setVisible(true);
			cullGroup.addActor(image);
		}

		@Override
		public void onLoadComplete(String url, TextureRegion region, Object tag) {
			int w = region.getRegionWidth();
			int h = region.getRegionHeight();

			image.setPosition((1570 - w) / 2, (AppGlobalConsts.APP_HEIGHT - h) / 2);
			image.setSize(w, h);
			image.setDrawable(region);
		}

		public void update(String url) {
			if (pageImageLoader != null) {
				pageImageLoader.cancelLoad();
			}
			pageImageLoader = new PageImageLoader(getPage());
			pageImageLoader.startLoadBitmap(url, "list", this, url);
		}

	}

}
