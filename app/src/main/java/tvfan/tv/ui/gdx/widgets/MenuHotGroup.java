package tvfan.tv.ui.gdx.widgets;

import java.util.List;

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
public class MenuHotGroup extends Group{

	//左侧栏目布局按钮坐标宽高
	private int DISTANCEHEIGHT = 0;
	private int iTextWidth = 480;
	private int iTextHeight = 60;
	private int iTextX = 20;
	private int iTextY = 920;
	//划线坐标宽高
	private int iLineWidth = 260;
	private int iLineHeight = 2;
	private int iLineX = 120;
	private int iLineY = 895;
	//工具栏坐标宽高
	private int iToolbarWidth = 345;
	private int iToolbarHeight = 99;
	private int iToolbarX = 127;
	private int iToolbarY = 807;
	//头标题
	private Label titleLabel;
	//分割线
	private Image lineImg;
	//工具按钮
	private Group search_toolbarGroup;
	private Group choose_toolbarGroup;
	//标签按钮组
	private MenuListHotGroup menuListGroup;
	private int iMenuListX = 123;
	private int iMenuListY = 117;
	private int iMenuWidth = 249;
	private int iMenuHeight = 564; 

	public MenuHotGroup(Page page,List<ProgramMenus> menulst) {
		super(page);
		//初始化头标签
		titleLabel = new Label(getPage());
		titleLabel.setSize(iTextWidth, iTextHeight);
		titleLabel.setTextSize(60);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlignment(Align.center);
		titleLabel.setAlpha(0.8f);
		titleLabel.setText("动漫");
		titleLabel.setPosition(iTextX, iTextY);
		//画线
		lineImg = new Image(getPage());
		lineImg.setDrawableResource(R.drawable.juji_line);
		lineImg.setSize(iLineWidth, iLineHeight);
		lineImg.setAlign(Align.center_Horizontal);
		lineImg.setPosition(iLineX, iLineY);
		//初始化搜索工具按钮
		search_toolbarGroup = new ToolBarGroup(getPage(),R.drawable.movie_search_f,"搜索");
		search_toolbarGroup.setSize(iToolbarWidth, iToolbarHeight);
		search_toolbarGroup.setPosition(iToolbarX, iToolbarY);
		//初始化筛选工具按钮
		choose_toolbarGroup = new ToolBarGroup(getPage(),R.drawable.movie_filter_f,"筛选");
		choose_toolbarGroup.setSize(iToolbarWidth, iToolbarHeight);
		choose_toolbarGroup.setPosition(iToolbarX, iToolbarY-iToolbarHeight);
		
		//标签按钮组
		menuListGroup = new MenuListHotGroup(page,menulst);
		menuListGroup.setSize(iMenuWidth, iMenuHeight);
		menuListGroup.setPosition(iMenuListX, iMenuListY);
				
		//初始化按钮组	
		addActor(titleLabel);
		addActor(lineImg);
		addActor(search_toolbarGroup);
		addActor(choose_toolbarGroup);
		addActor(menuListGroup);
	}
	
	public void refreshMenuList(Page page,List<ProgramMenus> menulst){
		if(menuListGroup!=null){
			menuListGroup.remove();
		}
		//标签按钮组
		menuListGroup = new MenuListHotGroup(page,menulst);
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
		if(titleLabel!=null){
			titleLabel.setText(title);
		}	
	}

	public Actor getMactor(){
		return menuListGroup.getMactor();
	}
	
	public void setSearchOnkeyListener(OnkeyListener onkeylistener){
		search_toolbarGroup.setOnkeyListener(onkeylistener);
	}
	public void setFilterOnkeyListener(OnkeyListener onkeylistener){
		choose_toolbarGroup.setOnkeyListener(onkeylistener);
	}


}
