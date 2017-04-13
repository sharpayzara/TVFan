package tvfan.tv.ui.gdx.ranking;

/**
 * 個人中心-左側用戶界面
 * 
 * @author 孫文龍
 * 
 */
import java.util.List;

import tvfan.tv.dal.models.ProgramMenus;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

/**
 * 按钮控件 主要适用与海报页左侧栏目下的栏目分类显示
 * 
 * @author sadshine
 * 
 */
public class HotMenuListGroup extends Group {
	Grid mGrid;
	// private int iPageWidth = 480;
	// private int iPageHeight = 640;
	HotMenuListAdapter menulstAdapter;
	// private int ipos;
	private Actor mactor;

	public HotMenuListGroup(Page page, List<ProgramMenus> menulst) {
		super(page);
		CullGroup cullGroup = new CullGroup(getPage());
		cullGroup.setSize(480, 680);
		cullGroup.setPosition(0, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, 480, 680));
		mGrid = new Grid(getPage());
		mGrid.setSize(330, 660);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRememberSelected(true);
		mGrid.setAdjustiveScrollLengthForBackward(0.2f);
		mGrid.setScrollType(Grid.SCROLL_TYPE_NORMAL);
		mGrid.setRowNum(1);
		mGrid.setCull(false);
		mGrid.setPosition(80, 0);
		// 焦点变动监听 用于页数计算
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int position, Actor actor) {
				// ipos = position;
				mactor = actor;
			}
		});
		if (menulst != null) {
			menulstAdapter = new HotMenuListAdapter(getPage());
			menulstAdapter.setMenulist(menulst);
			mGrid.setAdapter(menulstAdapter);
		}
		// addActor(mGrid);
		cullGroup.addActor(mGrid);
		addActor(cullGroup);
	}

	public void setOnItemSelectedChangeListenen(
			OnItemSelectedChangeListener onitemselectorChangelisntener) {
		mGrid.setOnItemSelectedChangeListener(onitemselectorChangelisntener);
	}

	public void setItemOnClickListener(OnItemClickListener onitemclickListener) {
		if (mGrid != null) {
			mGrid.setItemClickListener(onitemclickListener);
		}
	}

	public void setGridSelection(int position) {
		mGrid.setSelection(position,true);
	}

	public Actor getMactor() {
		return mactor;
	}
}
