package tvfan.tv.ui.gdx.brand.detail;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.lib.Utils;

public class SearchItem extends Group {

	public SearchItem(Page page) {
		super(page);
		setSize(326, 136);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(306, 136);
		cullGroup.setPosition(10, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, 306, 136));
		addActor(cullGroup);

		label = new Label(getPage(), false);
		label.setPosition(80, 0);
		label.setSize(226, 136);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.left);
		label.setTextSize(45);
		label.setAlpha(0.6f);
		label.setText("搜索");
		label.setMarquee(false);
		cullGroup.addActor(label);

		iconImg = new Image(getPage());
		iconImg.setDrawable(findRegion(R.drawable.search_icon));
		iconImg.setSize(60, 60);
		iconImg.setPosition(75, 38);
		iconImg.setVisible(true);
		cullGroup.addActor(iconImg);
		
		image = new Image(getPage());
		image.setDrawable(findRegion(R.drawable.new_foucs));
		image.setSize(306, 136);
		image.setPosition(0, 0);
		image.setVisible(false);
		cullGroup.addActor(image);
		
		
	}

	private Image image;
	private Image iconImg;
	private Label label;
	private CullGroup cullGroup;

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		image.setVisible(getFocus);
		setFocusScale(AppGlobalConsts.FOCUSSCALE);
		if(getFocus){
			label.setAlpha(1f);
		}else{
			label.setAlpha(0.6f);
			image.clearActions();
		}

	}
	
	@Override
	public void onResume() {
		super.onResume();
		label.setText("搜索");
		Utils.resetImageSource(image, R.drawable.new_foucs);
		Utils.resetImageSource(iconImg, R.drawable.search_icon);
	}
	
	@Override
	protected void onHasFocusChanged(boolean hasFocus) {
		super.onHasFocusChanged(hasFocus);
	}
}
