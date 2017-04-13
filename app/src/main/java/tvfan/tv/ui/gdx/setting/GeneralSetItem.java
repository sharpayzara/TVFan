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
import tvfan.tv.lib.Utils;

public class GeneralSetItem extends Group implements IListItem, LoaderListener{
	private Image image, mRightImage,mCheckBtn;
	private Label label, mSwitchLabel;
	private CullGroup cullGroup;
	private String text;
	private String mSwitchtext;

	public GeneralSetItem(Page page) {
		super(page);
		setSize(1520, 130);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setCullingArea(new Rectangle(0, 0, 1520 - 90, 130));
		addActor(cullGroup);
		label = new Label(getPage(), false);
		label.setSize(196, 30);
		label.setAlignment(Align.left);
		label.setPosition(45, 20);
		label.setTextSize(40);
		label.setColor(Color.WHITE);
		label.setMarquee(false);
		cullGroup.addActor(label);

		image = new Image(getPage());
		image.setDrawable(findRegion(R.mipmap.message_center_normal));
		image.setSize(1520 - 90, 90);
		image.setPosition(0, 0);
		cullGroup.addActor(image);
/*

		mLeftImage = new Image(getPage());
		mLeftImage.setDrawable(findRegion(R.mipmap.play_setting_left));
		mLeftImage.setSize(20, 37);
		mLeftImage.setPosition(556, 20);
		mLeftImage.setVisible(false);
		cullGroup.addActor(mLeftImage);
*/

		mRightImage = new Image(getPage());
		mRightImage.setDrawable(findRegion(R.mipmap.play_setting_right));
		mRightImage.setSize(20, 37);
		mRightImage.setPosition(1335, 20);
		cullGroup.addActor(mRightImage);

		mCheckBtn = new Image(getPage());
		mCheckBtn.setPosition(1240, 10);
		mCheckBtn.setSize(120, 60);
		mCheckBtn.setVisible(false);
		cullGroup.addActor(mCheckBtn);

		mSwitchLabel = new Label(getPage(), false);
		mSwitchLabel.setTextSize(40);
		mSwitchLabel.setSize(140, 40);
		mSwitchLabel.setAlignment(Align.center);
		mSwitchLabel.setColor(Color.WHITE);
		mSwitchLabel.setMarquee(false);
		mSwitchLabel.setPosition(1200, 20);
		cullGroup.addActor(mSwitchLabel);
		label.toFront();

	}

	@Override
	public void onLoadComplete(String url, TextureRegion texture, Object tag) {

	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
	}

	@Override
	public void onRecycle() {
		label.setText("");
		image.clearActions();
		//mLeftImage.clearActions();
		mSwitchLabel.clearActions();
	}

	@Override
	public void onSelected(boolean isSelected) {
		if(isSelected){
			image.setDrawableResource(R.mipmap.message_center_foucs);
		}else{
			image.setDrawable(findRegion(R.mipmap.message_center_normal));
		}
		label.toFront();
	}

	public void setText(String text) {
		label.setText(text);
		this.text = text;
	}

	public void setmCheckBtn(String flag){
		mSwitchLabel.setVisible(false);
		mRightImage.setVisible(false);
		mRightImage.remove();
		mCheckBtn.setVisible(true);
		if(flag.equals("是")){
			mCheckBtn.setDrawableResource(R.mipmap.setting_checkbox_on);
		}else{
			mCheckBtn.setDrawableResource(R.mipmap.setting_checkbox_off);
		}
	}


	public void setSwitchLabelText(String mSwitchtext) {
		mSwitchLabel.setText(mSwitchtext);
		this.mSwitchtext = mSwitchtext;
	}
	@Override
	public void onResume() {
		Utils.resetImageSource(image, R.drawable.new_foucs);
		Utils.resetImageSource(mRightImage, R.drawable.rt_right);
		label.setText(text);
		mSwitchLabel.setText(mSwitchtext);
		super.onResume();
	}
}
