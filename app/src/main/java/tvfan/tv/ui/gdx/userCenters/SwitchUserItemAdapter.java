package tvfan.tv.ui.gdx.userCenters;

/**
 * 個人中心-切換賬戶
 * 
 * @author 孫文龍
 * 
 */

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;

import tvfan.tv.AppGlobalVars;
import tvfan.tv.R;
import tvfan.tv.dal.models.UserInfo;
import tvfan.tv.lib.Utils;

public class SwitchUserItemAdapter extends GridAdapter {
	private Page page;
	private ArrayList<UserInfo> items = new ArrayList<UserInfo>();

	public SwitchUserItemAdapter(Page page) {
		this.page = page;
	}

	public void setData(ArrayList<UserInfo> items) {
		this.items = items;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		SwitchUserItem item = null;
		if (convertActor == null) {
			item = new SwitchUserItem(page);
		} else {
			item = (SwitchUserItem) convertActor;
		}
		item.setScale(1f);
		item.setText(items.get(position).getWxname());
		if (items.get(position).getUserid()
				.equals(AppGlobalVars.getIns().USER_ID))
			item.setFocus(true);
		return item;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	public class SwitchUserItem extends Group implements IListItem,
			LoaderListener {
		private Image image, image1;
		private Label label;
		private String text;
		private CullGroup cullGroup;

		public SwitchUserItem(Page page) {
			super(page);
			setSize(1920, 100);
			setFocusAble(true);
			cullGroup = new CullGroup(getPage());
			cullGroup.setCullingArea(new Rectangle(0, -20, 1920, 140));
			addActor(cullGroup);

			label = new Label(getPage(), false);
			label.setPosition(300, 30);
			label.setTextSize(40);
			label.setColor(Color.WHITE);
			label.setMarquee(false);
			label.setAlpha(0.8f);
			cullGroup.addActor(label);

			image = new Image(getPage());
			image.setDrawableResource(R.drawable.new_foucs);
			image.setSize(1380, 120);
			image.setPosition(250, -10);
			image.setVisible(false);
			cullGroup.addActor(image);

			image1 = new Image(getPage());
			image1.setDrawableResource(R.drawable.icon_11);
			image1.setSize(60, 60);
			image1.setPosition(1525, 22);
			image1.setVisible(false);
			cullGroup.addActor(image1);
		}

		@Override
		public void onLoadComplete(String url, TextureRegion texture, Object tag) {
		}

		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
			image.setVisible(getFocus);
			if (getFocus) {
				image.addAction(Actions.fadeIn(0.1f));
			} else {
				image.addAction(Actions.fadeOut(0.1f));
			}
		}

		@Override
		public void onRecycle() {
			label.setText("");
			label.setMarquee(false);
		}

		@Override
		public void onSelected(boolean isSelected) {
		}

		public void setText(String text) {
			label.setText(text);
			this.text = text;
		}

		public void setFocus(boolean getFocus) {
			image1.setVisible(getFocus);
		}

		@Override
		public void onResume() {
			Utils.resetImageSource(image1, R.drawable.icon_11);
			Utils.resetImageSource(image, R.drawable.new_foucs);
			label.setText(text);
			super.onResume();
		}
	}
}
