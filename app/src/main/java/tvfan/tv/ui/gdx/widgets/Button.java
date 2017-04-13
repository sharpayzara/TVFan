package tvfan.tv.ui.gdx.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.R;

public class Button extends Group {
	private Image image;
	private Image iconImage;
	private TVFANLabel label;
	private CullGroup cullGroup;

	private int unfocusId = -1;
	private int focusId = -1;

	public Button(Page page) {
		super(page);
		setSize(160, 60);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(160, 60);
		cullGroup.setCullingArea(new Rectangle(0, 0, 160, 60));
		addActor(cullGroup);

		image = new Image(getPage());
		image.setSize(150, 60);
		image.setPosition(0, 0);
		image.setVisible(true);
		cullGroup.addActor(image);

		label = new TVFANLabel(getPage());
		label.setPosition(0, 10);
		label.setSize(160, 50);
		label.setMaxLine(1);
		label.setColor(Color.valueOf("f0f0f0"));
		label.setAlignment(Align.center);
		cullGroup.addActor(label);
	}

	public Button(Page page, float width, float height) {
		super(page);
		setSize(width, height);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(width, height);
		cullGroup.setPosition(0, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, width, height));
		addActor(cullGroup);

		image = new Image(getPage());
		image.setSize(width, height);
		image.setPosition(0, 0);
		image.setVisible(true);
		image.setDrawableResource(R.drawable.but_info_1);
		cullGroup.addActor(image);

		label = new TVFANLabel(getPage());
		label.setPosition(0, 0);
		label.setSize(width, height - 1);
		label.setMaxLine(1);
		label.setColor(Color.valueOf("f0f0f0"));
		label.setAlignment(Align.center);
		cullGroup.addActor(label);
	}

	public Button(Page page, float width, float height, boolean isMarquee,
			float marginleft) {
		super(page);
		setSize(width, height);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(width, height);
		cullGroup.setPosition(0, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, width, height));
		addActor(cullGroup);

		image = new Image(getPage());
		image.setSize(width, height);
		image.setPosition(0, 0);
		image.setVisible(true);
		image.setDrawableResource(R.drawable.but_info_1);
		cullGroup.addActor(image);

		CullGroup cg = new CullGroup(getPage());
		cg.setSize(width, height);
		cg.setPosition(0, 0);
		cg.setCullingArea(new Rectangle(marginleft, 0, width - 2 * marginleft,
				height));
		cullGroup.addActor(cg);
		label = new TVFANLabel(getPage());
		label.setPosition(0, 0);
		label.setSize(width, height - 1);
		label.setMarquee(isMarquee);
		label.setMaxLine(1);
		label.setColor(Color.valueOf("f0f0f0"));
		label.setAlignment(Align.center);
		cg.addActor(label);
	}

	public void setViewPosition(){
		String sourceName = label.getText().toString();
		int length = sourceName.length();
		if(sourceName.getBytes().length == length){
			if(length == 4){
				iconImage.setPosition(80, 45.5f);
				label.setPosition(60, 0);
			}else if(length == 8){
				iconImage.setPosition(50, 45.5f);
				label.setPosition(50, 0);
			}
		}else {
			if(length == 2){
				iconImage.setPosition(80, 45.5f);
				label.setPosition(60, 0);
			}else if(length == 3) {
				iconImage.setPosition(60, 45.5f);
				label.setPosition(60, 0);
			}else if(length == 4) {
				iconImage.setPosition(50, 45.5f);
				label.setPosition(45, 0);
			}
		}
	}
	public Button(Page page, float width, float height, boolean hasIcon) {
		super(page);
		setSize(width, height);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(width, height);
		cullGroup.setPosition(0, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, width, height));
		addActor(cullGroup);

		image = new Image(getPage());
		image.setSize(width, height);
		image.setPosition(0, 0);
		image.setVisible(true);
		image.setDrawableResource(R.drawable.but_info_1);
		cullGroup.addActor(image);

		iconImage = new Image(getPage());
		iconImage.setSize(50, 50);
		iconImage.setPosition(60, 45.5f);
		iconImage.setVisible(true);
		cullGroup.addActor(iconImage);

		label = new TVFANLabel(getPage());
		label.setPosition(60, 0);
		label.setSize(width - 30, height - 1);
		label.setMaxLine(1);
		label.setColor(Color.valueOf("f0f0f0"));
		label.setAlignment(Align.center);
		cullGroup.addActor(label);
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		if (getFocus) {
			if (focusId != -1) {
				image.setDrawableResource(focusId);
			} else {
				image.setDrawableResource(R.drawable.new_foucs);
			}
		} else {
			if (unfocusId != -1) {
				image.setDrawableResource(unfocusId);
			} else {
				image.setDrawableResource(R.drawable.but_info_1);
			}
		}
	}

	public void setUnFocusBackGround(int id) {
		unfocusId = id;
	}

	public void setFocusBackGround(int id) {
		focusId = id;
	}

	public Label getLabel() {
		return label;
	}

	public Image getImage() {
		return image;
	}

	public void setButtonFocusScale(float scale) {
		setFocusScale(scale);
	}

	public Image getIconImage(){
		return iconImage;
	}
}
