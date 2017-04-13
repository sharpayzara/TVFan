package tvfan.tv.ui.gdx.programList;

import tvfan.tv.BasePage;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;
/**
 * 左侧栏列表item
 * @author sadshine
 *
 */
public class MenuListItem extends Group implements IListItem {
	//成员变量
	private Label label;
	private Image focusimgbg;
	private CullGroup cullGroup;
	private int CULLWIDTH = 344;
	private int CULLHEIGHT = 98;
	private int LABELWIDTH = 100;
	private int LABELHEIGHT = 98;
	private int LABELTXTSIZE = 40;
	private int resId;
	private Image iconImg;
	private Page mPage;
	
	public MenuListItem(Page page) {
		super(page);
		mPage = page;
		//设置item大小
		setSize(CULLWIDTH, CULLHEIGHT);
		setFocusAble(true);
		//设置item内的画布
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(CULLWIDTH, CULLHEIGHT);
		cullGroup.setCullingArea(new Rectangle(0.0f, 0.0f, CULLWIDTH, CULLHEIGHT));
		
		//选中背景图
		focusimgbg = new Image(getPage());
		focusimgbg.setDrawableResource(R.drawable.leaderboard_light);
		focusimgbg.setSize(CULLWIDTH, CULLHEIGHT);
		focusimgbg.setPosition(0.0f, 0.0f);
		focusimgbg.setVisible(false);
		//设置item内的文字		
		label = new Label(getPage(), false);
		label.setPosition(83.0f, 0);
		label.setSize(LABELWIDTH, LABELHEIGHT);
		label.setTextSize(LABELTXTSIZE);
		label.setColor(Color.valueOf("2d96ea"));
		label.setAlignment(Align.center_Vertical | Align.left);
		label.setText("Test");
		label.setMarquee(false);

		//加载场景
		
		addActor(focusimgbg);
		cullGroup.addActor(label);
		addActor(cullGroup);
	}
	
	public void setSize(){
		cullGroup.setSize(0, 0);
		cullGroup.remove();
	}
	
	public MenuListItem(Page page, int resId) {
		super(page);
		this.resId = resId;
		mPage = page;
		//设置item大小
		setSize(CULLWIDTH, CULLHEIGHT);
		setFocusAble(true);
		//设置item内的画布
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(CULLWIDTH, CULLHEIGHT);
		cullGroup.setCullingArea(new Rectangle(0.0f, 0.0f, CULLWIDTH, CULLHEIGHT));
		//选中背景图
		focusimgbg = new Image(getPage());
		focusimgbg.setDrawableResource(R.drawable.leaderboard_light);
		focusimgbg.setSize(CULLWIDTH, CULLHEIGHT);
		focusimgbg.setPosition(0.0f, 0.0f);
		focusimgbg.setVisible(false);
		//设置item内的文字		
		label = new Label(getPage(), false);
		label.setSize(LABELWIDTH, CULLHEIGHT);
		label.setTextSize(LABELTXTSIZE);
		label.setColor(Color.valueOf("2d96ea"));
		label.setAlignment(Align.center_Vertical);
		label.setText("Test");
		label.setMarquee(false);

		iconImg = new Image(getPage());
		iconImg.setDrawableResource(resId);
		iconImg.toFront();

		if(resId == R.mipmap.list_search_selected) {
			iconImg.setSize(45.0f, 42.0f);
			iconImg.setPosition(83.0f,  26.5f);
			label.setPosition(144.0f, 0.0f);
		} else {
			iconImg.setSize(38.0f, 45.0f);
			iconImg.setPosition(83.0f, 27.5f);
			label.setPosition(141.0f, 0.0f);
		}

		//加载场景
		cullGroup.addActor(focusimgbg);
		cullGroup.addActor(iconImg);
		cullGroup.addActor(label);
		addActor(cullGroup);
	}
	
	

	public void setText(String text) {
		label.setText(text);
	}

	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
		label.setAlignment(Align.center_Vertical);
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		focusimgbg.setVisible(getFocus);
		if(getFocus){
			label.setColor(Color.WHITE);
			if(resId == R.mipmap.list_search_selected)
				iconImg.setDrawableResource(R.mipmap.list_search_normal);
			else if (resId == R.mipmap.list_filter_normal)
				iconImg.setDrawableResource(R.mipmap.list_filter_selected);
		}else{
			label.setColor(Color.valueOf("2d96ea"));
			if(resId == R.mipmap.list_search_selected)
				iconImg.setDrawableResource(R.mipmap.list_search_selected);
			else if (resId == R.mipmap.list_filter_normal)
				iconImg.setDrawableResource(R.mipmap.list_filter_normal);
		}

	}

	@Override
	public void onSelected(boolean isSelected) {
		if(resId == R.mipmap.list_search_selected)
			iconImg.setDrawableResource(R.mipmap.list_search_normal);
		else if (resId == R.mipmap.list_filter_normal)
			iconImg.setDrawableResource(R.mipmap.list_filter_selected);
		label.setColor(Color.WHITE);
	}
	
	
	public void setFocusImgBg(boolean display){
		//focusimgbg.setVisible(display);
	}
	

}
