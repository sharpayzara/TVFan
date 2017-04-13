package tvfan.tv.ui.gdx.ranking;

import java.util.List;

import tvfan.tv.dal.models.ProgramMenus;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;

/**
 * 海报左侧栏组合控件 包含组件如下: ToolBarGroup\MenuListGroup
 * 
 * @author sunwenlong
 */
public class HotTagsMenuGroup extends Group {

	// 左侧栏目布局按钮坐标宽高

	private int iTextWidth = 480;
	private int iTextHeight = 60;
	private int iTextX = 20;
	private int iTextY = 920;
	// 划线坐标宽高
	private int iLineWidth = 260;
	private int iLineHeight = 2;
	private int iLineX = 130;
	private int iLineY = 895;
	// 头标题
	private Label titleLabel;
	// 分割线
	private Image lineImg;

	// 标签按钮组
	private HotMenuListGroup menuListGroup;


	public HotTagsMenuGroup(Page page, List<ProgramMenus> menulst) {
		super(page);
		// 初始化头标签
		titleLabel = new Label(getPage());
		titleLabel.setSize(iTextWidth, iTextHeight);
		titleLabel.setTextSize(60);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlignment(Align.center);
		titleLabel.setAlpha(0.9f);
		titleLabel.setText("热门标签");
		titleLabel.setPosition(iTextX, iTextY);
		// 画线
		lineImg = new Image(getPage());
		lineImg.setDrawableResource(R.drawable.juji_line);
		lineImg.setSize(iLineWidth, iLineHeight);
		lineImg.setAlign(Align.center_Horizontal);
		lineImg.setPosition(iLineX, iLineY);

		// 标签按钮组
		menuListGroup = new HotMenuListGroup(page, menulst);
		menuListGroup.setSize(480, 680);
		menuListGroup.setPosition(0, 180);

		// 初始化按钮组
		addActor(titleLabel);
		addActor(lineImg);
		addActor(menuListGroup);
	}

	public void setItemOnClickListener(OnItemClickListener onitemclicklistener) {
		menuListGroup.setItemOnClickListener(onitemclicklistener);

	}

	public void setOnItemSelectedChangeListenen(
			OnItemSelectedChangeListener onitemselectorChangelisntener) {
		menuListGroup
				.setOnItemSelectedChangeListenen(onitemselectorChangelisntener);
	}

	public void setGridSelection(int position){
		menuListGroup.setGridSelection(position);
	}
	public Actor getMactor() {
		return menuListGroup.getMactor();
	}

}
