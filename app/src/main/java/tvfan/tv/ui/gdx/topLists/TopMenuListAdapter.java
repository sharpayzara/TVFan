package tvfan.tv.ui.gdx.topLists;

/**
 *  desc  排行榜类别列表适配器
 *  @author  yangjh
 *  created at  2016/4/8 20:56
 */
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
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

import java.util.List;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.lib.Utils;

public class TopMenuListAdapter extends GridAdapter {

	private Page page;
	private List<String> menuArray;
	private TopMenuListItem item = null;

	public TopMenuListAdapter(Page page) {
		this.page = page;
	}

	public void setMenulist(List menuArray) {
		this.menuArray = menuArray;
	}

	@Override
	public int getCount() {
		// 获取总长度
		int count = 0;
		if (menuArray != null) {
			count = menuArray.size();
		}
		return count;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {

		if (convertActor == null) {
			item = new TopMenuListItem(page, position);
		} else {
			item = (TopMenuListItem) convertActor;
		}
		if(menuArray.size() > position){
			item.setText(menuArray.get(position));
		}
		item.setScale(1f);
		return item;
	}

	public void setFocusImgbg(boolean display) {
		((TopMenuListItem) item).setFocusImgBg(display);
	}

	class TopMenuListItem extends Group implements IListItem {
		// 成员变量
		private Label label;
		private Image focusimgbg;
		private String text;
		private CullGroup cullGroup;
		private int LABELTXTSIZE = 44;

		public TopMenuListItem(Page page, int pos) {
			super(page);
			// 设置item大小
			setSize(240, 90);
			setFocusAble(true);
			// 设置item内的画布
			cullGroup = new CullGroup(getPage());
			cullGroup.setSize(240, 90);
			// 选中背景图
			focusimgbg = new Image(getPage());
			focusimgbg.setDrawableResource(R.mipmap.leaderboard_light);
			focusimgbg.setSize(240+30, 90+40);
			focusimgbg.setPosition(-30, -40);
			focusimgbg.setVisible(false);
			// 设置item内的文字
			label = new Label(getPage(), false);
			label.setPosition(50, 0);
			label.setTextSize(LABELTXTSIZE);
			label.setColor(Color.WHITE);
			label.setAlignment(Align.left);
			label.setTextColor(android.graphics.Color.parseColor("#2D94E8"));
			label.setMarquee(false);

			// 加载场景
			cullGroup.addActor(focusimgbg);
			cullGroup.addActor(label);
			//cullGroup.addActor(image);
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
		//	Utils.resetImageSource(image, R.drawable.menu_foucs);
			Utils.resetImageSource(focusimgbg, R.drawable.listbj);
			label.setText(text);
			super.onResume();
		}

		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
			label.setMarquee(getFocus);
		//	image.setVisible(getFocus);
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
