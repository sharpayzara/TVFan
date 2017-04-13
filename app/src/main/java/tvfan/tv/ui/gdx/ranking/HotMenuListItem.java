package tvfan.tv.ui.gdx.ranking;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Widgets;

import tvfan.tv.R;
import tvfan.tv.lib.Utils;

/**
 * 左侧栏列表item
 * 
 * @author 孫文龍
 * 
 */
public class HotMenuListItem extends Group implements IListItem {
	// 成员变量
	private Image image;
	private Label label;
	private Image focusimgbg;
	private CullGroup cullGroup;
	private String text;
	private int CULLHEIGHT = 160;
	private int LABELWIDTH = 200;
	private int LABELHEIGHT = 91;
	private int LABELTXTSIZE = 45;
	// private int MenuListWidth = 330;
	private int MenuListHeight = 90;
	private int FOCUSBGWIDTH = 250;
	private int FOCUSBGHEIGHT = 80;

	public HotMenuListItem(Page page, int pos) {
		super(page);
		// 设置item大小
		setSize(330, MenuListHeight);
		setFocusAble(true);
		// 设置item内的画布
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(330, CULLHEIGHT);
		cullGroup.setCullingArea(new Rectangle(0, -35, 330, CULLHEIGHT));
		// 选中背景图
		focusimgbg = new Image(getPage());
		focusimgbg.setDrawableResource(R.drawable.listbj);
		focusimgbg.setSize(FOCUSBGWIDTH, FOCUSBGHEIGHT);
		focusimgbg.setPosition(50, 8);
		focusimgbg.setVisible(false);
		// 设置item内的文字
		label = new Label(getPage(), false);
		label.setPosition(75, 0);
		label.setSize(LABELWIDTH, LABELHEIGHT);
		label.setTextSize(LABELTXTSIZE);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.center);
		label.setAlpha(0.8f);
		label.setText("Test");
		label.setMarquee(false);

		// 设置item内的图标5
		image = Widgets.image(getPage(), (R.drawable.new_foucs));
		image.setSize(FOCUSBGWIDTH + 45, FOCUSBGHEIGHT + 47);
		image.setPosition(27, -15);
		image.setVisible(false);
		// 加载场景
		cullGroup.addActor(focusimgbg);
		cullGroup.addActor(label);
		cullGroup.addActor(image);
		addActor(cullGroup);
	}

	public void setText(String text) {
		label.setText(text);
		this.text = text;
	}

	public void setIcon(int rs) {
		image.setDrawableResource(rs);
	}

	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		label.setMarquee(getFocus);
		image.setVisible(getFocus);
	}

	@Override
	public void onSelected(boolean isSelected) {
	}

	public void setFocusImgBg(boolean display) {
		focusimgbg.setVisible(display);
		if (display)
			label.setAlpha(1.0f);
		else
			label.setAlpha(0.8f);
	}
	@Override
	public void onResume() {
		Utils.resetImageSource(image, R.drawable.new_foucs);
		Utils.resetImageSource(focusimgbg, R.drawable.listbj);
		label.setText(text);
		super.onResume();
	}

}
