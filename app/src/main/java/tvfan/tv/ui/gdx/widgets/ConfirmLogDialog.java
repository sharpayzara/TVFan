package tvfan.tv.ui.gdx.widgets;

import tvfan.tv.R;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.Page;

public class ConfirmLogDialog extends Dialog {

	private Image bgImg;
	private Label titleLabel;

	public ConfirmLogDialog(Page page,String msg) {
		super(page);
		setSize(1920, 1080);
		setPosition(0, 0);

		
		bgImg = new Image(getPage());
		bgImg.setPosition(0, 0);
		bgImg.setSize(1920, 1080);
		bgImg.setDrawableResource(R.drawable.tkbj);
		addActor(bgImg);
		
		
		titleLabel = new Label(getPage());
		titleLabel.setSize(1920-100, 1080-100);
		titleLabel.setTextSize(45);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setText(msg);
		titleLabel.setPosition(0+50, 0+50);
		titleLabel.setAlignment(Align.center);
		titleLabel.setMaxLine(24);
		addActor(titleLabel);

	}
}
