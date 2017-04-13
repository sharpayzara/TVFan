package tvfan.tv.ui.gdx.programDetail.item;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.ui.gdx.programDetail.adapter.DetailTagAdapter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

public class ScrollitemView extends Group implements IListItem {
	private Image image;
	private Label label;
	private CullGroup cullGroup;
	private String url;
	private Grid mGrid;
	private Label mTag;
	private Label mPageNum;
	private String pageNum= "%s/%s";
	
	public ScrollitemView(Page page) {
		super(page);
		setSize(1239, 450);
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(1239, 450);
		cullGroup.setCullingArea(new Rectangle(0, 0, 1239, 450));
		addActor(cullGroup);

		mTag = new Label(getPage(), false);
		mTag.setPosition(0, 375);
		mTag.setColor(Color.WHITE);
		mTag.setAlignment(Align.center);
		mTag.setText("标签“卡通动漫”");
		mTag.setAlpha(0.4f);
		cullGroup.addActor(mTag);
		
		mPageNum = new Label(getPage(), false);
		mPageNum.setPosition(1140, 375);
		mPageNum.setColor(Color.WHITE);
		mPageNum.setAlignment(Align.center);	
		mPageNum.setText(String.format(pageNum, "1", "5"));
		mPageNum.setAlpha(0.4f);
		cullGroup.addActor(mPageNum);
		
		mGrid = new Grid(page);
		mGrid.setSize(1650, 830f);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRowNum(5);
		mGrid.setGapLength(0);
		mGrid.setPosition((AppGlobalConsts.APP_WIDTH - 1650) * 0.5f, 70f);
		mGrid.setAdapter(new DetailTagAdapter(page));
		//mGrid.addHeader(actor)
		//mGrid.addHeader(actor)
		mGrid.setItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(Actor itemView, int position, AbsListView grid) {
				
			}
		});
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {
			
			@Override
			public void onSelectedChanged(int position, Actor actor) {
				int i = (int) position/10 +1;
				mPageNum.setText(String.format(pageNum, i,(int) (50/10)));
			}
		});
		cullGroup.addActor(mGrid);
		
	}


	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
		label.setAlignment(Align.center);
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);

	}

	@Override
	public void onSelected(boolean isSelected) {

	}


}
