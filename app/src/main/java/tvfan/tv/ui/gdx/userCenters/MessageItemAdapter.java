package tvfan.tv.ui.gdx.userCenters;

import android.graphics.Color;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.dal.models.MsgList;
import tvfan.tv.lib.Utils;

/**
 * 專題匯總界面
 * 
 * @author 孫文龍
 * 
 */
public class MessageItemAdapter extends GridAdapter {
	private Page page;
	private List<MsgList> listdata = new ArrayList<MsgList>();

	public MessageItemAdapter(Page page) {
		this.page = page;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		MessageGridItem item = null;

		if (convertActor == null) {
			item = new MessageGridItem(page);
		} else {
			item = (MessageGridItem) convertActor;
		}
		item.setScale(1f);
		item.update(listdata.get(position).getType(), listdata.get(position)
				.getTitle(), listdata.get(position).getCreateTime(), listdata
				.get(position).getMark());
		return item;
	}

	class MessageGridItem extends Group implements IListItem {
		private Image image;
		private Image focusImg;
		private Label label, label1, label2;
		private String type, title, time;

		public MessageGridItem(Page page) {
			super(page);
			setSize(1290, 80);
			setFocusAble(true);
			image = new Image(getPage());
			image.setSize(20, 20);
			image.setPosition(0, 40);
			image.setVisible(true);
			image.setDrawableResource(R.drawable.reddot);
			addActor(image);

			label1 = new Label(getPage(), false);
			label1.setSize(1290 - 415, 100);
			label1.setTextSize(35);
			label1.setPosition(155, 0);
			label1.setMarquee(false);
			label1.setTextColor(Color.parseColor("#FFFFFF"));
			label1.setAlpha(0.9f);
			addActor(label1);

			label = new Label(getPage(), false);
			label.setSize(120, 100);
			label.setTextSize(40);
			label.setPosition(25, 0);
			label.setMarquee(false);
			label.setTextColor(Color.parseColor("#2E94E6"));
			label.setAlpha(0.9f);

			addActor(label);

			label2 = new Label(getPage(), false);
			label2.setSize(200, 100);
			label2.setTextSize(30);
			label2.setPosition(1020, 0);
			label2.setMarquee(false);
			label2.setTextColor(Color.parseColor("#FFFFFF"));
			label2.setAlpha(0.9f);
			label2.setAlignment(Align.center);
			addActor(label2);

			// 光标选中效果
			focusImg = new Image(getPage());
			focusImg.setSize(1280, 60);
			focusImg.setDrawableResource(R.mipmap.detai_variety_title_icon_selected);
			focusImg.setPosition(0, 20);
			focusImg.setVisible(false);
			addActor(focusImg);
		}

		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
			label.setMarquee(getFocus);
			focusImg.setVisible(getFocus);
			if (getFocus) {
				image.setVisible(false);
				label.setAlpha(1.0f);
				label1.setAlpha(1.0f);
				label.setTextColor(Color.parseColor("#FFFFFF"));
			} else {
				label.setAlpha(0.9f);
				label1.setAlpha(0.9f);
				label.setAlignment(Align.left | Align.center_Vertical);
				label.setTextColor(Color.parseColor("#2E94E6"));
			}
			focusImg.toBack();

		}

		@Override
		public void onRecycle() {
			label.setText("");
			label1.setText("");
			label2.setText("");
			label1.setMarquee(false);
		}

		@Override
		public void onSelected(boolean isSelected) {

		}

		@Override
		public void onResume() {
			Utils.resetImageSource(image, R.drawable.reddot);
			Utils.resetImageSource(focusImg, R.drawable.new_foucs);
			label.setText("[" + type + "]");
			label.setAlignment(Align.left | Align.center_Vertical);
			label1.setText(title);
			label1.setAlignment(Align.left | Align.center_Vertical);
			label2.setText(time);
			super.onResume();
		}

		public void update(String type, String title, String time, int mark) {
			this.type = type.toUpperCase();
			this.title = title;
			this.time = time;
			label.setText("[" + type + "]");
			label.setAlignment(Align.left | Align.center_Vertical);
			label1.setText(title);
			label1.setAlignment(Align.left | Align.center_Vertical);
			label2.setText(time);
			if (mark == 1) {
				image.setVisible(false);
			} else {
				image.setVisible(true);
			}

		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listdata.size();
	}

	public void setData(ArrayList<MsgList> items) {
		this.listdata = items;
	}

}
