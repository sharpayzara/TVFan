package tvfan.tv.ui.gdx.setting;

import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.widget.AbsListView.IListItem;

public class ChangeBackgroundItem extends Group implements IListItem,
		LoaderListener, OnClickListener {

	private Image mImageBackground, mFocusImage;
	public Image mImageTrue;
	public int imgid;

	public ChangeBackgroundItem(Page page) {
		super(page);
		setSize(318, 179);
		setFocusAble(true);

		mImageBackground = new Image(page);
		mImageBackground.setSize(318, 179);
		addActor(mImageBackground);

		mImageTrue = new Image(page);
		mImageTrue.setPosition(244, 111);
		mImageTrue.setDrawableResource(R.drawable.ok);
		mImageTrue.setVisible(false);
		addActor(mImageTrue);

		// 光标选中效果
		mFocusImage = NinePatchImage.make(page,
				findTexture(R.drawable.list_foucs),
				new int[] { 45, 45, 45, 45 });
		mFocusImage.setSize(318 + 88, 179 + 88);
		mFocusImage.setPosition(-44, -45);
		mFocusImage.setVisible(false);
		addActor(mFocusImage);

	}

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
	}

	@Override
	public void onRecycle() {

	}

	@Override
	public void onSelected(boolean arg0) {

	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		mFocusImage.setVisible(getFocus);
		setFocusScale(0.1f);

	}

	public void setImageBackground(int image) {
		this.imgid = image;
		mImageBackground.setDrawableResource(image);

	}

	@Override
	public void onResume() {
		Utils.resetImageSource(mImageBackground, imgid);
		Utils.resetImageSource(mFocusImage, R.drawable.list_foucs);
		Utils.resetImageSource(mImageTrue, R.drawable.ok);
		super.onResume();
	}

	@Override
	public void onClick(Actor arg0) {
		// TODO Auto-generated method stub

	}

}