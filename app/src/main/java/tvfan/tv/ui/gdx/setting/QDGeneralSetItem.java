package tvfan.tv.ui.gdx.setting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.R;

public class QDGeneralSetItem extends Group implements IListItem, LoaderListener {
	private Image image, mLeftImage,mRightImage;
	private Label label,midLable;
	private CullGroup cullGroup;

	public QDGeneralSetItem(Page page) {
		super(page);
		setSize(1520, 105);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setCullingArea(new Rectangle(0, 0, 1520-90, 105));
		addActor(cullGroup);
		label = new Label(getPage(), false);
		label.setSize(196, 30);
		label.setAlignment(Align.left);
		label.setPosition(45, 18);
		label.setTextSize(40);
		label.setColor(Color.WHITE);
		label.setMarquee(false);
		cullGroup.addActor(label);


		midLable = new Label(getPage(), false);
		midLable.setSize(200, 30);
		midLable.setAlignment(Align.center);
		midLable.setPosition(540, 18);
		midLable.setTextSize(40);
		midLable.setColor(Color.WHITE);
		midLable.setMarquee(false);
		midLable.setVisible(false);
		cullGroup.addActor(midLable);

		image = new Image(getPage());
		image.setDrawable(findRegion(R.mipmap.message_center_normal));
		image.setSize(1520-90, 85);
		image.setPosition(0, 0);
		cullGroup.addActor(image);


		mLeftImage = new Image(getPage());
		mLeftImage.setDrawable(findRegion(R.mipmap.play_setting_left));
		mLeftImage.setSize(20, 37);
		mLeftImage.setPosition(90, 18);
		mLeftImage.setVisible(false);
		cullGroup.addActor(mLeftImage);


		mRightImage = new Image(getPage());
//		mRightImage.setDrawable(findRegion(R.mipmap.set_check_on));
		mRightImage.setSize(50, 36);
		mRightImage.setPosition(1335, 20);
		mRightImage.setVisible(false);
		cullGroup.addActor(mRightImage);
		label.toFront();
		midLable.toFront();
	}

	@Override
	public void onLoadComplete(String url, TextureRegion texture, Object tag) {

	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		mRightImage.setVisible(getFocus);
	}

	@Override
	public void onRecycle() {
		//label.setText("");
		image.clearActions();
		mRightImage.clearActions();
	}

	@Override
	public void onSelected(boolean isSelected) {
		if(isSelected){
			image.setDrawableResource(R.mipmap.message_center_foucs);
		}else{
			image.setDrawable(findRegion(R.mipmap.message_center_normal));
		}
		label.toFront();
		midLable.toFront();
	}

	public void setText(String text) {
		label.setText(text);
	}

	public void setMidText(String text){
		mRightImage.setVisible(false);
		mRightImage.remove();
		mLeftImage.setVisible(true);
		midLable.setVisible(true);
		midLable.setText(text);
	}
	@Override
	public void onResume() {
		super.onResume();
	}

}
