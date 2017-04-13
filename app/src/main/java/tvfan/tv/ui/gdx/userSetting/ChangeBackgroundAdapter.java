package tvfan.tv.ui.gdx.userSetting;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;

public class ChangeBackgroundAdapter extends GridAdapter {
	private ArrayList<Integer> mImageList;
	private Page Page;
	private int mTrue;
	private ChangeBackgroundItem mChangeBackgroundItem;

	@Override
	public Actor getActor(int arg0, Actor arg1) {
		ChangeBackgroundItem mChangeBackgroundItem = null;
		if (arg1 == null) {
			mChangeBackgroundItem = new ChangeBackgroundItem(Page);

		} else {
			mChangeBackgroundItem = (ChangeBackgroundItem) arg1;
		}
		mChangeBackgroundItem.setImageBackground(mImageList.get(arg0));
		if (getmTrue() == arg0) {
			mChangeBackgroundItem.mImageTrue.setVisible(true);
			setmChangeBackgroundItem(mChangeBackgroundItem);
		} else {
			mChangeBackgroundItem.mImageTrue.setVisible(false);
		}
		return mChangeBackgroundItem;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImageList.size();
	}

	public ChangeBackgroundAdapter(Page Page) {
		this.Page = Page;
	}

	public void setData(ArrayList<Integer> mImageList) {
		this.mImageList = mImageList;
	}

	public int getmTrue() {
		return mTrue;
	}

	public void setmTrue(int mTrue) {
		this.mTrue = mTrue;
	}

	public ChangeBackgroundItem getmChangeBackgroundItem() {
		return mChangeBackgroundItem;
	}

	public void setmChangeBackgroundItem(
			ChangeBackgroundItem mChangeBackgroundItem) {
		this.mChangeBackgroundItem = mChangeBackgroundItem;
	}

}
