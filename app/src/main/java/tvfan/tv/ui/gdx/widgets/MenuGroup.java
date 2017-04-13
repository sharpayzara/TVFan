package tvfan.tv.ui.gdx.widgets;

import java.util.List;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.models.ProgramMenus;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.OnkeyListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
/**
 * 海报左侧栏组合控件 包含组件如下:
 * ToolBarGroup\MenuListGroup
 * @author sadshine
 */
public class MenuGroup extends Group{

	//背景色
	private Image  back;

	//标签按钮组
	private MenuListGroup menuListGroup;
	private int iMenuListX = 7;
	private int iMenuListY = 0;
	private int iMenuWidth = 344;
	private int iMenuHeight = 1025;

	private String mHeadTitle;//当前界面的具体标题（是什么分类“电视剧、电影、动漫”）
	public MenuGroup(Page page,List<ProgramMenus> menulst, String mHeadTitle) {
		super(page);

		back = new Image(page);
		back.setPosition(0.0f, 0.0f);
		back.setSize(360.0f, AppGlobalConsts.APP_HEIGHT);
		back.setDrawableResource(R.mipmap.search_left_bg);
		this.mHeadTitle = mHeadTitle;

		//标签按钮组
		menuListGroup = new MenuListGroup(page,menulst, mHeadTitle);
		menuListGroup.setSize(iMenuWidth, iMenuHeight);
		menuListGroup.setPosition(iMenuListX, 0);
				
		//初始化按钮组
		addActor(back);
		addActor(menuListGroup);
		
	}
	
	public void refreshMenuList(Page page,List<ProgramMenus> menulst){
		if(menuListGroup!=null){
			menuListGroup.remove();
		}
		//标签按钮组
		menuListGroup = new MenuListGroup(page,menulst, mHeadTitle);
		menuListGroup.setSize(iMenuWidth, iMenuHeight);
		menuListGroup.setPosition(iMenuListX, iMenuListY);
		addActor(menuListGroup);
	}
	
	public void setItemOnClickListener(OnItemClickListener onitemclicklistener){
		menuListGroup.setItemOnClickListener(onitemclicklistener);	

	}
	
	public void setOnItemSelectedChangeListenen(OnItemSelectedChangeListener onitemselectorChangelisntener){
		menuListGroup.setOnItemSelectedChangeListenen(onitemselectorChangelisntener);
	}
	
	public void setTitle(String title){
	}

	public void setFilterOnkeyListener(OnkeyListener onkeylistener){
		//choose_toolbarGroup.setOnkeyListener(onkeylistener);
	}

	public void setGridSelection(int position){
		menuListGroup.setGridSelection(position);
	}

}
