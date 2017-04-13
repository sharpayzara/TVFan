package tvfan.tv.ui.gdx.programHotList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;
/**
 * 左侧栏列表item
 * @author sadshine
 *
 */
public class MenuListItem extends Group implements IListItem {
	//成员变量
	private Image image;
	private Label label;
	private Image focusimgbg;
	private CullGroup cullGroup;
	private int CULLWIDTH = 294;
	private int CULLHEIGHT = 140;
	private int LABELWIDTH = 249;
	private int LABELHEIGHT = 91;
	private int LABELTXTSIZE = 50;
	private int MenuListWidth = 249;
	private int MenuListHeight = 91;	
	private int FOCUSBGWIDTH = 250;
	private int FOCUSBGHEIGHT = 90;
	
	public MenuListItem(Page page) {
		super(page);
		//设置item大小
		setSize(MenuListWidth, MenuListHeight);
		setFocusAble(true);
		//设置item内的画布
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(CULLWIDTH, CULLHEIGHT);
		cullGroup.setCullingArea(new Rectangle(-40, -40, CULLWIDTH+41, CULLHEIGHT+41));
		//选中背景图
		focusimgbg = new Image(getPage());
		focusimgbg.setDrawableResource(R.drawable.listbj);
		focusimgbg.setSize(FOCUSBGWIDTH, FOCUSBGHEIGHT);
		focusimgbg.setPosition(0, 0);
		focusimgbg.setVisible(false);
		//设置item内的文字		
		label = new Label(getPage(), false);
		label.setPosition(0, 0);
		label.setSize(LABELWIDTH, LABELHEIGHT);
		label.setTextSize(LABELTXTSIZE);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.center);
		label.setAlpha(0.8f);
		label.setText("Test");
		label.setMarquee(false);
		//设置item内的图标5
		/*image = Widgets.image(getPage(), (R.drawable.menu_foucs));
		image.setSize(FOCUSBGWIDTH+41, FOCUSBGHEIGHT+44);
		image.setPosition(-21, -12);
		image.setVisible(false);*/
		
		image = NinePatchImage.make(getPage(),
				this.findTexture(R.drawable.new_foucs),
				new int[]{40,40,40,40});
		image.setSize(310, 155);
		image.setPosition(-31, -31);
		image.setVisible(false);
		
		//加载场景
		cullGroup.addActor(focusimgbg);
		cullGroup.addActor(label);
		cullGroup.addActor(image);
		addActor(cullGroup);
	}

	public void setText(String text) {
		label.setText(text);
	}
	
	public void setIcon(int rs){
		image.setDrawableResource(rs);
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
		label.setMarquee(getFocus);
		image.setVisible(getFocus);		
		if(getFocus){
			label.setAlpha(1);
		}else{
			label.setAlpha(0.8f);
		}

	}

	@Override
	public void onSelected(boolean isSelected) {
		//focusimgbg.setVisible(isSelected);
		//focusimgbg.setVisible(isSelected);
	}
	
	
	public void setFocusImgBg(boolean display){
		focusimgbg.setVisible(display);
	}

}
