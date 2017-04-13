package tvfan.tv.ui.gdx.userSetting;
/**
 *  desc  个人中心 用户左侧界面适配器
 *  @author  yangjh
 *  created at  16-4-19 上午11:08
 */

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
		item.setText(menuArray[position]);
		item.setScale(1f);
		return item;
	}

	class UserMenuListItem extends Group implements IListItem {
		// 成员变量
		private Image imagenew;
		private Label label;
		private Image focusimgbg;
		private String text;
		private CullGroup cullGroup;
		private int MenuListHeight = 90;


		public UserMenuListItem(Page page, int pos) {
			super(page);
			// 设置item大小
			setSize(265, MenuListHeight);
			setFocusAble(true);
			// 设置item内的画布
			cullGroup = new CullGroup(getPage());
			cullGroup.setSize(265, 90);
			// 选中背景图
			focusimgbg = new Image(getPage());
			focusimgbg.setDrawableResource(R.mipmap.leaderboard_light);
			focusimgbg.setSize(295, 120);
			focusimgbg.setPosition(-30, -30);
			focusimgbg.setVisible(false);
			// 设置item内的文字
			label = new Label(getPage(), false);
			label.setPosition(30, 0);
			label.setSize(165, 45);
			label.setTextSize(42);
			label.setAlignment(Align.center);
			label.setTextColor(android.graphics.Color.parseColor("#2D94E8"));
			label.setMarquee(false);
			// 加载场景
			cullGroup.addActor(focusimgbg);
			cullGroup.addActor(label);
			addActor(cullGroup);
		}

		public void setText(String text) {
			label.setText(text);
			this.text = text;
		}

		public String getText() {
			return label.getText().toString();
		}

		@Override
		public void onRecycle() {
			label.setText("");
			label.setMarquee(false);
		}

		@Override
		public void onResume() {
			label.setText(text);
			super.onResume();
		}

		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
		}

		@Override
		public void onSelected(boolean isSelected) {
				 focusimgbg.setVisible(isSelected);
		}

		public void setFocusImgBg(boolean display) {
			focusimgbg.setVisible(display);
			if(display){
				label.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
			}else{
				label.setTextColor(android.graphics.Color.parseColor("#2D94E8"));
			}

		}

	}
}
