package tvfan.tv.ui.gdx.brand.detail;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.lib.Utils;

public class BrandMenuItemAdapter extends GridAdapter {
	private Page page;
	private ArrayList<String> items = new ArrayList<String>();

	public BrandMenuItemAdapter(Page page) {
		this.page = page;
	}

	public void setData(ArrayList<String> items) {
		this.items = items;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		BrandMenuItem item = null;

		if (convertActor == null) {
			item = new BrandMenuItem(page);
		} else {
			item = (BrandMenuItem) convertActor;
		}
		item.setScale(1f);
		item.setText(items.get(position).toString());
		return item;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	class BrandMenuItem extends Group implements IListItem, LoaderListener {
		private Image image;
		private Label label;
		private CullGroup cullGroup;

		public BrandMenuItem(Page page) {
			super(page);
			setSize(336, 95);
			setFocusAble(true);
			setFocusScale(AppGlobalConsts.FOCUSSCALE);
			cullGroup = new CullGroup(getPage());
			cullGroup.setPosition(15, 0);
			cullGroup.setSize(306, 136);
			cullGroup.setCullingArea(new Rectangle(32, 0, 250, 136));
			addActor(cullGroup);

			label = new Label(getPage(), false);
			label.setPosition(35, 0);
			label.setSize(230, 95);
			label.setColor(Color.WHITE);
			label.setTextSize(45);
			label.setAlpha(0.6f);
			label.setAlignment(Align.center);
			label.setMarquee(false);
			cullGroup.addActor(label);

			image = new Image(getPage());
			image.setDrawable(findRegion(R.drawable.new_foucs));
			image.setSize(306, 136);
			image.setPosition(15, -20);
			image.setVisible(false);
			addActor(image);
		}
		
		@Override
		public void onResume() {
			super.onResume();
			Utils.resetImageSource(image,R.drawable.new_foucs);
		}

		@Override
		public void onLoadComplete(String url, TextureRegion texture, Object tag) {

		}

		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
			image.setVisible(getFocus);
			label.setMarquee(getFocus);
			if (getFocus) {
				label.setAlpha(1f);
			} else {
				label.setAlpha(0.6f);
			}
		}

		@Override
		public void onRecycle() {
			label.setText("");
			image.clearActions();
		}

		@Override
		public void onSelected(boolean isSelected) {

		}
		
		public String getText(){
			return label.getText().toString();
		}

		public void setText(String text) {
			label.setText(text);
		}

	}

}
