package tvfan.tv.ui.gdx.widgets;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.OnHasFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;

public class TagFilter extends Group implements OnHasFocusChangeListener {

	private ArrayList<String[]> data = new ArrayList<String[]>();

	private ArrayList<Grid> grids = new ArrayList<Grid>();

	private float fx, fy;

	private Page page;
	private CullGroup cullGroup;
	Adapter adapter;

	private static final float WIDTH = 375f;
	private static final float HEIGHT = 1050f;
	private static final float ITEM_HEGIHT = 150f;

	private Image focus;
	private Image centerLine;
	private Image topBg;
	private Image bottomBg;
	private ArrayList<String> firstGridData = new ArrayList<String>();
	private ArrayList<String> subGridData = new ArrayList<String>();

	public TagFilter(Page page) {
		super(page);
		this.page = page;
		setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);

		cullGroup = new CullGroup(page);
		cullGroup.setPosition(0, 0);
		cullGroup.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		addActor(cullGroup);

	}

	private void initGrids() {
		if (data.size() > 0) {
			fx = (AppGlobalConsts.APP_WIDTH - WIDTH * data.size()) / 2;
			fy = 11;

			centerLine = new Image(page);
			centerLine.setDrawableResource(R.drawable.sxline);
			centerLine.setPosition(-100, (AppGlobalConsts.APP_HEIGHT - 110) * 0.5f);
			centerLine.setSize(2180, 110);
			centerLine.setFocusAble(false);
			cullGroup.addActor(centerLine);

			focus = new Image(page);
			focus.setDrawable(page
					.findTextureRegion(R.drawable.new_foucs));
			focus.setSize(WIDTH, ITEM_HEGIHT);
			focus.setPosition(fx, fy + 454.5f);
			focus.setFocusAble(false);
			cullGroup.addActor(focus);

			for (int i = 0; i < data.size(); i++) {

				grids.add(new Grid(page));
				grids.get(i).setPosition(fx + WIDTH * i, fy);
				grids.get(i).setSize(WIDTH, HEIGHT);
				grids.get(i).setOrientation(Grid.ORIENTATION_VERTICAL);
				grids.get(i).setScrollType(Grid.SCROLL_TYPE_REFER_CENTER_LINE);
				grids.get(i).setGapLength(0);
				grids.get(i).setRowNum(1);
				grids.get(i).setHeadPadding(450);
				grids.get(i).setFootPadding(450);
				// grids.get(i).addInterceptKey(Keys.UP);
				// grids.get(i).addInterceptKey(Keys.DOWN);
				adapter = new Adapter(page);
				adapter.setData(new ArrayList<String>(
						Arrays.asList(data.get(i))));
				grids.get(i).setAdapter(adapter);
				cullGroup.addActor(grids.get(i));
				grids.get(i).setSelection(0);
				grids.get(i).setOnHasFocusChangeListener(this);
			}
			// grids.get(0).requestFocus();
			// grids.get(0).setSelection(0);
			// curGrid = grids.get(0);

			topBg = new Image(page);
			topBg.setPosition(0, 862.5f);
			topBg.setSize(AppGlobalConsts.APP_WIDTH, 217.5f);
			topBg.setDrawableResource(R.drawable.ys_top);
			cullGroup.addActor(topBg);

			bottomBg = new Image(page);
			bottomBg.setPosition(0, 0);
			bottomBg.setSize(AppGlobalConsts.APP_WIDTH, 217.5f + fy);
			bottomBg.setDrawableResource(R.drawable.ys_bottom);
			cullGroup.addActor(bottomBg);

		}
	}

	@Override
	public boolean onKey(int keycode) {
		if (keycode == Keys.UP && grids.get(0).findSelectedPosition() == 0) {
			grids.get(0).setSelection(grids.get(0).getAdapterCount() - 1);
			return true;
		}
		if (keycode == Keys.DOWN
				&& grids.get(0).findSelectedPosition() == grids.get(0)
						.getAdapterCount() - 1) {
			grids.get(0).setSelection(0);
			return true;
		}
		return super.onKey(keycode);
	}

	public void setData(ArrayList<String[]> data) {
		this.data = data;
		initGrids();
	}

	@Override
	public void onHasFocusChanged(Group grid, boolean hasFocus) {
		if (hasFocus) {
			focus.addAction(Actions.moveTo(grid.getX(), grid.getY() + 454.5f,
					0.2f));
			// curGrid = (Grid) grid;
		}
	}

	class Adapter extends GridAdapter {

		private ArrayList<String> strs;
		private Page page;

		public Adapter(Page page) {
			this.page = page;
		}

		public void setData(ArrayList<String> array) {
			this.strs = array;
		}

		@Override
		public int getCount() {
			return strs.size();
		}

		@Override
		public Actor getActor(int position, Actor convertActor) {
			Item item = null;

			if (convertActor == null) {
				item = new Item(page);
			} else {
				item = (Item) convertActor;
			}
			item.setScale(1f);
			item.setText(strs.get(position));
			return item;
		}

	}

	class Item extends Group implements IListItem {

		private CullGroup cull;
		private Label label;
		private float h = ITEM_HEGIHT;

		public Item(Page page) {
			super(page);
			setSize(WIDTH, h);
			setFocusAble(true);
			cull = new CullGroup(page);
			cull.setPosition(0, 0);
			cull.setSize(WIDTH, h);
			cull.setCullingArea(new Rectangle(0, 0, WIDTH, h));

			addActor(cull);
			label = new Label(page);
			label.setPosition(0, 0);
			label.setSize(WIDTH, h);
			label.setTextSize(40);
			label.setAlignment(Align.center);
			label.setAlpha(0.6f);
			cull.addActor(label);
		}

		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
			if (getFocus) {
				label.setAlpha(1f);
			} else {
				label.setAlpha(0.6f);
			}
		}

		public void setText(String text) {
			label.setText(text);
		}

		@Override
		public void onRecycle() {

		}

		@Override
		public void onSelected(boolean arg0) {
			// TODO Auto-generated method stub

		}

	}

}
