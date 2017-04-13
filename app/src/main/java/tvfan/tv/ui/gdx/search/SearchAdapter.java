package tvfan.tv.ui.gdx.search;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class SearchAdapter extends GridAdapter {

	private List<String> mNumerKey = new ArrayList<String>();
	private Page searchPage;

	public SearchAdapter(Page searchPage, List<String> mNumerKey) {
		this.searchPage = searchPage;
		this.mNumerKey = mNumerKey;
	}

	@Override
	public Actor getActor(int arg0, Actor arg1) {
		SearchAdapterItem mAdapterItem;
		if (arg1 == null) {
			mAdapterItem = new SearchAdapterItem(searchPage);
		} else {
			mAdapterItem = (SearchAdapterItem) arg1;
		}
		mAdapterItem.setScale(1f);
		mAdapterItem.setmNumer(mNumerKey.get(arg0));
		return mAdapterItem;
	}

	@Override
	public int getCount() {

		return mNumerKey.size();
	}

}
