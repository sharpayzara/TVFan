package tvfan.tv.ui.gdx.programDetail.item;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

import android.text.TextUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.CullGroup;

public class VideoSourceGridItem extends Group {
	private Image iconImage;
	private Image focusImg;
	private TVFANLabel label;
	private CullGroup cullGroup;
	
	public VideoSourceGridItem(Page page) {
		super(page);
		setSize(290.0f, 65.0f);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(290.0f, 65.0f);
		cullGroup.setCullingArea(new Rectangle(0, 0, 290.0f, 65.0f));
		addActor(cullGroup);

		focusImg = new Image(page);
		focusImg.setPosition(0.0f, 0.0f);
		focusImg.setSize(290.0f, 65.0f);
		focusImg.setVisible(false);
		focusImg.setFocusAble(false);
		focusImg.setDrawableResource(R.mipmap.detail_play_source_frame);
		cullGroup.addActor(focusImg);

		iconImage = new Image(getPage());
		iconImage.setSize(95.0f, 55.0f);
		iconImage.setPosition(25.0f, 5.0f);
		iconImage.setVisible(true);
		cullGroup.addActor(iconImage);

		label = new TVFANLabel(getPage());
		label.setPosition(125.0f, 0.0f);
		label.setSize(175.0f, 65.0f);
		label.setMaxLine(1);
		label.setTextSize(30);
		label.setColor(Color.GRAY);
		label.setAlignment(Align.center);
		cullGroup.addActor(label);
		
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		if (getFocus) {
			focusImg.setVisible(true);
		} else {
			focusImg.setVisible(false);
		}
	}

	public Label getLabel() {
		return label;
	}

	public Image getIconImage() {
		return iconImage;
	}

	public void setText(String text) {
		label.setText(text);
	}

}
