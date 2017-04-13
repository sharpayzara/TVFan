package tvfan.tv.ui.gdx.widgets;

import java.util.List;

import tvfan.tv.dal.models.ProgramMenus;
import tvfan.tv.ui.gdx.programHotList.MenuListHotAdapter;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;
/**
 * 按钮控件
 * 主要适用与海报页左侧栏目下的栏目分类显示
 * @author sadshine
 *
 */
public class MenuListHotGroup extends Group {
	Grid mGrid;
	private int iPageWidth = 250;
	private int iPageHeight = 564;
	MenuListHotAdapter menulstAdapter;
	private int ipos;
	private Actor mactor;
	public MenuListHotGroup(Page page,List<ProgramMenus> menulst) {
		super(page);
		CullGroup cullGroup = new CullGroup(getPage());
		cullGroup.setSize(iPageWidth+80, iPageHeight+20);
		cullGroup.setPosition(0, 0);
		cullGroup.setCullingArea(new Rectangle(-30, 0, iPageWidth+80, iPageHeight+20));
		mGrid = new Grid(getPage());
		mGrid.setSize(iPageWidth, iPageHeight);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRememberSelected(true);
		mGrid.setAdjustiveScrollLengthForBackward(0.2f);		
		mGrid.setScrollType(Grid.SCROLL_TYPE_NORMAL);
		mGrid.setRowNum(1);
		mGrid.setCull(false);
		mGrid.setPosition(0, 0);
		//焦点变动监听 用于页数计算
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {
			
			@Override
			public void onSelectedChanged(int position, Actor actor) {
				ipos = position;
				mactor = actor;			
			}
		});
		if(menulst!=null){
			menulstAdapter = new MenuListHotAdapter(getPage());
			menulstAdapter.setMenulist(menulst);
			mGrid.setAdapter(menulstAdapter);			
		}
		//addActor(mGrid);
		cullGroup.addActor(mGrid);
		addActor(cullGroup);
	}
	
	public void setOnItemSelectedChangeListenen(OnItemSelectedChangeListener onitemselectorChangelisntener){
		mGrid.setOnItemSelectedChangeListener(onitemselectorChangelisntener);
	}
	
	public void setItemOnClickListener(OnItemClickListener onitemclickListener){
		if(mGrid!=null){
			mGrid.setItemClickListener(onitemclickListener);
		}		
	}

	public Actor getMactor() {
		return mactor;
	}	
}
