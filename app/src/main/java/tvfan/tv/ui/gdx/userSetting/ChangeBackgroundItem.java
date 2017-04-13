package tvfan.tv.ui.gdx.userSetting;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.widget.AbsListView.IListItem;

import tvfan.tv.R;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

public class ChangeBackgroundItem extends Group implements IListItem,
		LoaderListener, OnClickListener {

	private Image mImageBackground;
	public Image mImageTrue,frameImage;
	public int imgid;

	public ChangeBackgroundItem(Page page) {
		super(page);
		setSize(1119,634);
		setFocusAble(true);

		frameImage = new Image(getPage());
		frameImage.setDrawableResource(R.mipmap.setting_frame_line_un);
		frameImage.setSize(1119,634);
		frameImage.setPosition(0,0);
		addActor(frameImage);

		mImageBackground = new Image(page);
		mImageBackground.setSize(1100, 616);
		mImageBackground.setPosition(10,10);
		addActor(mImageBackground);

		mImageTrue = new Image(page);
		mImageTrue.setPosition(1000, 524);
		mImageTrue.setDrawableResource(R.drawable.ok);
		mImageTrue.setVisible(false);
		addActor(mImageTrue);

	}

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
	}

	@Override
	public void onRecycle() {

	}

	@Override
	public void onSelected(boolean arg0) {
		if(arg0){
			frameImage.setDrawableResource(R.mipmap.setting_frame_line);
		}else{
			frameImage.setDrawableResource(R.mipmap.setting_frame_line_un);
		}
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
	}

	public void setImageBackground(int image) {
		this.imgid = image;
		mImageBackground.setDrawableResource(image);
	}

	@Override
	public void onResume() {
		Utils.resetImageSource(mImageBackground, imgid);
		Utils.resetImageSource(mImageTrue, R.drawable.ok);
		super.onResume();
	}

	@Override
	public void onClick(Actor arg0) {
		// TODO Auto-generated method stub

	}

}