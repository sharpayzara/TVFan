package tvfan.tv.ui.gdx.widgets;

import java.util.List;

import tvfan.tv.dal.models.ProgramMenus;
import tvfan.tv.ui.gdx.programList.MenuListAdapter;

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
public class MenuListGroup extends Group {
	Grid mGrid;
	private int iPageWidth = 344;
	private int iPageHeight = 1025;  //564
	MenuListAdapter menulstAdapter;

	public MenuListGroup(Page page,List<ProgramMenus> menulst, String mHeadTitle) {
		super(page);
		CullGroup cullGroup = new CullGroup(getPage());
		cullGroup.setSize(iPageWidth, iPageHeight);
		cullGroup.setPosition(0, 0);
		cullGroup.setCullingArea(new Rectangle(0.0f, 0.0f, iPageWidth, iPageHeight));

		mGrid = new Grid(getPage());
		mGrid.setPosition(0.0f, 0.0f);
		mGrid.setSize(iPageWidth, iPageHeight);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRememberSelected(true);
		mGrid.setAdjustiveScrollLengthForBackward(0.2f);
		mGrid.setScrollType(Grid.SCROLL_TYPE_NORMAL);
		mGrid.setRowNum(1);
		mGrid.setCull(false);

		if(menulst!=null){
			menulstAdapter = new MenuListAdapter(getPage());
			menulstAdapter.setMenulist(menulst, mHeadTitle);
			mGrid.setAdapter(menulstAdapter);			
		}
		
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

	public void setGridSelection(int position){
		mGrid.setSelection(position, true);
	}
	
}
