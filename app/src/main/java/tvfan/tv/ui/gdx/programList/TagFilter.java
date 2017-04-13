package tvfan.tv.ui.gdx.programList;

import java.util.ArrayList;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.models.FilmClassItem;
import android.content.Intent;

import tvfan.tv.R;
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
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.AbsListView.ScrollStatusListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class TagFilter extends Group implements OnHasFocusChangeListener {

	private ArrayList<ArrayList<FilmClassItem>> data = new ArrayList<ArrayList<FilmClassItem>>();

	private ArrayList<Grid> grids = new ArrayList<Grid>();

	private float fy;

	private Page page;
	private CullGroup cullGroup;
	Adapter adapter;

	private static final float WIDTH = 280.0f;
	private static final float HEIGHT = 1050.0f;
	private static final float ITEM_HEGIHT = 150.0f;
	private static final float SPACE = 100.0f;

	private ArrayList<Image> focuss;
	private Image topBg;
	private Image bottomBg;
				   //默认 最新 = 1
	private String classType = "1", typeIndex = "-1", zoneIndex = "-1", timeIndex = "-1";
	private String filterTitle = "", classTitle = "最新", typeTitle = "", zoneTitle = "", timeTitle = "";
	private boolean is_scrolling = false;

	public TagFilter(Page page) {
		super(page);
		this.page = page;
		setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);

		cullGroup = new CullGroup(page);
		cullGroup.setPosition(440.0f, 0);
		cullGroup.setSize(AppGlobalConsts.APP_WIDTH-440.0f, AppGlobalConsts.APP_HEIGHT);
		addActor(cullGroup);

	}

	private void initGrids() {
		if (data.size() > 0) {
			fy = 11;
			focuss = new ArrayList<Image>();

			for (int i = 0; i < data.size(); i++) {
				Image focus = new Image(page);
				if (i == 0)
					focus.setDrawableResource(R.mipmap.list_filter_icon_bj_selected);
				else
					focus.setDrawableResource(R.mipmap.list_filter_icon_bj_normal);
				focus.setSize(WIDTH, 70.0f);
				focus.setPosition((WIDTH + SPACE) * i, fy + 494.5f);
				focus.setFocusAble(false);
				focus.toBack();
				cullGroup.addActor(focus);
				focuss.add(focus);

				grids.add(new Grid(page));
				grids.get(i).setPosition((WIDTH+SPACE)*i, fy);
				grids.get(i).setSize(WIDTH, HEIGHT);
				grids.get(i).setOrientation(Grid.ORIENTATION_VERTICAL);
				grids.get(i).setScrollType(Grid.SCROLL_TYPE_REFER_CENTER_LINE);
				grids.get(i).setRowNum(1);
				grids.get(i).setHeadPadding(450);
				grids.get(i).setFootPadding(450);
				adapter = new Adapter(page);
				adapter.setData(data.get(i),i);
				
				grids.get(i).setAdapter(adapter);
				cullGroup.addActor(grids.get(i));
				grids.get(i).setSelection(0);
				grids.get(i).setOnHasFocusChangeListener(this);
				grids.get(i).setGapLength(30.0f);
				grids.get(i).setScrollStatusListener(new MScrollStatusListener());
				grids.get(i).setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {
					@Override
					public void onSelectedChanged(int paramInt, Actor paramActor) {
						if (((Item)paramActor).getIndex() == 0) {
							classType = ((Item)paramActor).getTag();
							classTitle = ((Item)paramActor).getLabel().getText().toString();
						} else if (((Item)paramActor).getIndex() == 1) {
							typeIndex = ((Item)paramActor).getTag();
							if (typeIndex != "-1") {
								typeTitle = ((Item)paramActor).getLabel().getText().toString();
							} else typeTitle = "";
						} else if (((Item)paramActor).getIndex() == 2) {
							zoneIndex = ((Item)paramActor).getTag();
							if (zoneIndex != "-1") {
								zoneTitle = ((Item)paramActor).getLabel().getText().toString();
							} else zoneTitle = "";
						} else if (((Item)paramActor).getIndex() == 3) {
							timeIndex = ((Item)paramActor).getTag();
							if (timeIndex != "-1") {
								timeTitle = ((Item)paramActor).getLabel().getText().toString();
							} else timeTitle = "";
						} 
						
					}
				});
				
				
				grids.get(i).setItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(Actor paramActor, int paramInt,
							AbsListView paramAbsListView) {
						Intent intent = new Intent(AppGlobalConsts.LOCAL_MSG_FILTER.LIST_FILTER.toString());
						intent.putExtra("classType", classType);
						intent.putExtra("typeIndex", typeIndex);
						intent.putExtra("zoneIndex", zoneIndex);
						intent.putExtra("timeIndex", timeIndex);
						filterTitle = classTitle + " " + typeTitle + " " +  zoneTitle + " " +  timeTitle;
						intent.putExtra("filterTitle", filterTitle);
						page.getActivity().sendBroadcast(intent);
					}
				});
			}

			topBg = new Image(page);
			topBg.setPosition(0, 862.5f);
			topBg.setSize(AppGlobalConsts.APP_WIDTH, 217.5f);
			topBg.setDrawableResource(R.drawable.ys_top);
			addActor(topBg);

			bottomBg = new Image(page);
			bottomBg.setPosition(0, 0);
			bottomBg.setSize(AppGlobalConsts.APP_WIDTH, 217.5f + fy);
			bottomBg.setDrawableResource(R.drawable.ys_bottom);
			addActor(bottomBg);

		}
	}

	@Override
	public boolean onKey(int keycode) {
		
		for (int i = 0; i < data.size(); i++) {
			if (keycode == Keys.UP && grids.get(i).findSelectedPosition() == 0) {
				return true;
			}
			if (keycode == Keys.DOWN
					&& grids.get(i).findSelectedPosition() == grids.get(i)
							.getAdapterCount() - 1) {
				return true;
			}
		}
		
		if(keycode==Keys.LEFT || keycode==Keys.RIGHT){
			if(is_scrolling){
				return true;
			}
		}
		
		return super.onKey(keycode);
	}

	public void setData(ArrayList<ArrayList<FilmClassItem>> data) {
		this.data = data;
		initGrids();
	}

	@Override
	public void onHasFocusChanged(Group grid, boolean hasFocus) {
		if (hasFocus) {
//			focus1.addAction(Actions.moveTo(grid.getX(), grid.getY() + 454.5f,
//					0.2f));
			for(int i = 0; i<data.size(); i++) {
				if(hasFocus && grid == grids.get(i)) {
					focuss.get(i).setDrawableResource(R.mipmap.list_filter_icon_bj_selected);
				} else {
					focuss.get(i).setDrawableResource(R.mipmap.list_filter_icon_bj_normal);
				}
			}

		}
	}

	class Adapter extends GridAdapter {

		private ArrayList<FilmClassItem> strs;
		private Page page;
		private int index;
		public Adapter(Page page) {
			this.page = page;
		}

		public void setData(ArrayList<FilmClassItem> array,int i) {
			this.strs = array;
			this.index = i;
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
			item.setText(strs.get(position).getTitle(),strs.get(position).getValue(),this.index);
			return item;
		}

	}

	class Item extends Group implements IListItem {

		private CullGroup cull;
		private Label label;
		private float h = ITEM_HEGIHT;
		private String tag;
		private int index;

		public Label getLabel() {
			return label;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

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
			label.setPosition(0, 4);
			label.setSize(WIDTH, h);
			label.setTextSize(45);
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

		public void setText(String text, String tag, int index) {
			label.setText(text);
			setTag(tag);
			setIndex(index);
		}




		@Override
		public void onRecycle() {

		}

		@Override
		public void onSelected(boolean arg0) {
			// TODO Auto-generated method stub
		}

	}


	class MScrollStatusListener implements ScrollStatusListener {

		@Override
		public void onScrollStart(float arg0, float arg1) {
			is_scrolling = true;
		}

		@Override
		public void onScrollStop(float arg0, float arg1) {
			is_scrolling = false;
		}

		@Override
		public void onScrolling(float arg0, float arg1) {

		}

	}

}
