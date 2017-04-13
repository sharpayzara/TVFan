package tvfan.tv.ui.gdx.widgets;

import java.util.ArrayList;
import java.util.Arrays;

import tvfan.tv.AppGlobalConsts;

import tvfan.tv.R;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.AbsListView.ScrollStatusListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;
import com.luxtone.lib.widget.Grid.GridAdapter;

/**
 * 综艺剧集选择
 * @author cibn
 *
 */
public class VarietyTagFilter extends Group {

	private ArrayList<String[]> data = new ArrayList<String[]>();

	private ArrayList<Grid> grids = new ArrayList<Grid>();

	private float fx, fy;

	private Page page;
	private CullGroup cullGroup;
	Adapter adapter;
	VarietyAdapter gridAdapter;

	private static final float WIDTH = 375f;
	private static final float HEIGHT = 1050f;
	private static final float ITEM_HEGIHT = 150f;

	private boolean is_scrolling = false;

	public VarietyTagFilter(Page page) {
		super(page);
		this.page = page;
		setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);

		cullGroup = new CullGroup(page);
		cullGroup.setPosition(0, 0);
		cullGroup
				.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		addActor(cullGroup);

	}

	private void initGrids() {
		if (data.size() > 0) {
			fx = (AppGlobalConsts.APP_WIDTH - WIDTH * data.size()) / 2;
			fy = 11;


			for (int i = 0; i < data.size(); i++) {
				if (i == 0) {
					grids.add(new Grid(page));
					if (data.size() > 1) {
						grids.get(i).setPosition(506, fy);
					} else {
						grids.get(i).setPosition(610, fy);
					}
					grids.get(i).setSize(600, HEIGHT);
					grids.get(i).setOrientation(Grid.ORIENTATION_VERTICAL);
					grids.get(i).setScrollType(
							Grid.SCROLL_TYPE_REFER_CENTER_LINE);
					grids.get(i).setGapLength(0);
					grids.get(i).setRowNum(1);
					grids.get(i).setHeadPadding(450);
					grids.get(i).setFootPadding(450);
					gridAdapter = new VarietyAdapter(page);
					gridAdapter.setData(new ArrayList<String>(Arrays
							.asList(data.get(i))));
					grids.get(i).setAdapter(gridAdapter);
					cullGroup.addActor(grids.get(i));
				} else {
					grids.add(new Grid(page));
					grids.get(i).setPosition(600+606, fy);
					grids.get(i).setSize(WIDTH, HEIGHT);
					grids.get(i).setOrientation(Grid.ORIENTATION_VERTICAL);
					grids.get(i).setScrollType(
							Grid.SCROLL_TYPE_REFER_CENTER_LINE);
					grids.get(i).setGapLength(0);
					grids.get(i).setRowNum(1);
					grids.get(i).addInterceptKey(Keys.UP);
					grids.get(i).addInterceptKey(Keys.DPAD_DOWN);
					grids.get(i).setHeadPadding(450);
					grids.get(i).setFootPadding(450);
					adapter = new Adapter(page);
					adapter.setData(new ArrayList<String>(Arrays.asList(data
							.get(i))));
					grids.get(i).setAdapter(adapter);
					cullGroup.addActor(grids.get(i));
					grids.get(i).setSelection(0);
				}

			}

		}
	}

	@Override
	public boolean onKey(int keycode) {
		if (keycode == Keys.UP && grids.get(0).findSelectedPosition() == 0) {
			grids.get(0).setSelection(grids.get(0).getAdapterCount() - 1,true);
			return true;
		}
		if (keycode == Keys.DOWN
				&& grids.get(0).findSelectedPosition() == grids.get(0)
						.getAdapterCount() - 1) {
			grids.get(0).setSelection(0,true);
			return true;
		}
		
		if(keycode==Keys.RIGHT && keycode==Keys.LEFT){
			if(is_scrolling){
				return true;
			}
		}

		return super.onKey(keycode);
	}

	public void setData(ArrayList<String[]> data) {
		this.data = data;
		initGrids();
	}

	public ArrayList<Grid> getGrids() {
		return grids;
	}

	class VarietyAdapter extends GridAdapter {
		private ArrayList<String> strs;
		private Page page;

		public VarietyAdapter(Page page) {
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
			VarietyItem item = null;

			if (convertActor == null) {
				item = new VarietyItem(page);
			} else {
				item = (VarietyItem) convertActor;
			}
			item.setScale(1f);
			item.setText(strs.get(position));
			item.setLabelAlign();
			return item;
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

	class VarietyItem extends Group implements IListItem {

		private CullGroup cull;
		private Label label;
		private float h = ITEM_HEGIHT;

		public VarietyItem(Page page) {
			super(page);
			setSize(600, h);
			setFocusAble(true);
			cull = new CullGroup(page);
			cull.setPosition(0, 0);
			cull.setSize(600, h);
			cull.setCullingArea(new Rectangle(0, 0, 600, h));

			addActor(cull);
			label = new Label(page);
			label.setPosition(0, 0+4);
			label.setSize(600, h);
			label.setTextSize(40);
			label.setAlignment(Align.center);
			label.setAlpha(0.2f);
			cull.addActor(label);
			setFocusScale(AppGlobalConsts.FOCUSSCALE);
		}

		public void setLabelAlign(){
			label.setAlignment(Align.center);
		}
		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
			if (getFocus) {
				label.setAlpha(1f);
				label.setMarquee(true);
			} else {
				label.setAlpha(0.2f);
				label.setMarquee(false);
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
			label.setAlpha(0.2f);
			cull.addActor(label);
			setFocusScale(AppGlobalConsts.FOCUSSCALE);
		}

		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
			if (getFocus) {
				label.setAlpha(1f);
			} else {
				label.setAlpha(0.2f);
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

		}

	}

	/**
	 * 右侧列表点击后进行左侧列表的数据更新
	 * @param episodeArray
	 */
	public void resetEpisodeData(ArrayList<String> episodeArray){
		if(gridAdapter != null) {
			gridAdapter = null;
			grids.get(0).clear();
		}
		gridAdapter = new VarietyAdapter(page);
		gridAdapter.setData(episodeArray);
		grids.get(0).setAdapter(gridAdapter);
		grids.get(0).setSelection(0);
	}

}
