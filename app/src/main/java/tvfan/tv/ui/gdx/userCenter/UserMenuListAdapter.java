package tvfan.tv.ui.gdx.userCenter;

/**
 * 個人中心-左側用戶界面
 * 
 * @author 孫文龍
 * 
 */
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid.GridAdapter;
import com.luxtone.lib.widget.Widgets;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.lib.Utils;

public class UserMenuListAdapter extends GridAdapter {

	private Page page;
	private String[] menuArray;
	private UserMenuListItem item = null;

	public UserMenuListAdapter(Page page) {
		this.page = page;
	}

	public void setMenulist(String[] menuArray) {
		this.menuArray = menuArray;
	}

	@Override
	public int getCount() {
		// 获取总长度
		int count = 0;
		if (menuArray != null) {
			count = menuArray.length;
		}
		return count;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {

		if (convertActor == null) {
			item = new UserMenuListItem(page, position);
		} else {
			item = (UserMenuListItem) convertActor;
		}
		item.setDotVisible(false);
		item.setText(menuArray[position]);
		if (item.getText().equalsIgnoreCase("我的消息") && App.MESSAGENUMBER > 0)
			item.setDotVisible(true);
		item.setScale(1f);
		return item;
	}

	public void setFocusImgbg(boolean display) {
		((UserMenuListItem) item).setFocusImgBg(display);
	}

	class UserMenuListItem extends Group implements IListItem {
		// 成员变量
		private Image image, imagenew;
		private Label label;
		private Image focusimgbg, dot;
		private String text;
		private CullGroup cullGroup;
		private int CULLHEIGHT = 160;
		private int LABELWIDTH = 200;
		private int LABELHEIGHT = 91;
		private int LABELTXTSIZE = 45;
		private int MenuListHeight = 90;
		private int FOCUSBGWIDTH = 250;
		private int FOCUSBGHEIGHT = 80;

		public UserMenuListItem(Page page, int pos) {
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
			focusimgbg.setPosition(52, 5);
			focusimgbg.setVisible(false);
			// 设置item内的文字
			label = new Label(getPage(), false);
			label.setPosition(75, 0);
			label.setSize(LABELWIDTH, LABELHEIGHT);
			label.setTextSize(LABELTXTSIZE);
			label.setColor(Color.WHITE);
			label.setAlignment(Align.center);
			label.setAlpha(0.8f);
			label.setMarquee(false);
			if (pos == 2) {
				imagenew = Widgets.image(getPage(), (R.drawable.info));
				imagenew.setSize(25, 25);
				imagenew.setPosition(83 + 45 * 4, 53);
				imagenew.setVisible(false);
				cullGroup.addActor(imagenew);
			}

			// 设置item内的图标5
			image = Widgets.image(getPage(), (R.drawable.new_foucs));
			image.setSize(FOCUSBGWIDTH + 45, FOCUSBGHEIGHT + 47);
			image.setPosition(27, -20);
			image.setVisible(false);

			dot = new Image(page);
			dot.setDrawableResource(R.drawable.reddot);
			dot.setSize(13, 13);
			dot.setPosition(265, 60);
			dot.setVisible(false);
			addActor(dot);

			// 加载场景
			cullGroup.addActor(focusimgbg);
			cullGroup.addActor(label);
			cullGroup.addActor(image);
			cullGroup.addActor(dot);
			addActor(cullGroup);
		}

		public void setText(String text) {
			label.setText(text);
			this.text = text;
		}

		public String getText() {
			return label.getText().toString();
		}

		public void setIcon(int rs) {
			image.setDrawableResource(rs);
		}

		public void setIsNew() {
			imagenew.setVisible(true);
		}

		public void setDotVisible(boolean visible) {
			dot.setVisible(visible);
		}

		@Override
		public void onRecycle() {
			label.setText("");
			label.setMarquee(false);
		}

		@Override
		public void onResume() {
			Utils.resetImageSource(image, R.drawable.new_foucs);
			Utils.resetImageSource(focusimgbg, R.drawable.listbj);
			Utils.resetImageSource(imagenew, R.drawable.info);
			label.setText(text);
			super.onResume();
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

		public void setNewImgBgGone() {
			if (imagenew != null && imagenew.isVisible())
				imagenew.setVisible(false);
		}

		public void setFocusImgBg(boolean display) {
			focusimgbg.setVisible(display);
			if (display)
				label.setAlpha(1.0f);
			else
				label.setAlpha(0.8f);
		}

	}
}
