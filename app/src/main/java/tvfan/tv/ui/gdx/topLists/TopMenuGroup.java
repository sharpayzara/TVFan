package tvfan.tv.ui.gdx.topLists;
import tvfan.tv.App;
import tvfan.tv.R;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.Grid;

import java.util.List;

import tvfan.tv.dal.RemoteData;
import tvfan.tv.ui.gdx.topLists.TopMenuListAdapter.*;

/**
 *  desc  排行榜类别布局
 *  @author  yangjh
 *  created at  2016/4/8 20:46
 */
public class TopMenuGroup extends Group {
	private RemoteData rd;
	private Label titleLabel;
	private Image topIcon,leftImg;
	public Grid mGrid;
	private Actor mactor;
	TopMenuListAdapter menulstAdapter;

	public TopMenuGroup(Page page,List menuArray) {
		super(page);

		leftImg = new Image(getPage());
		leftImg.setPosition(0,0);
		leftImg.setSize(360,1080);
		leftImg.setDrawableResource(R.mipmap.left_bg);
		addActor(leftImg);

		topIcon = new Image(page);
		topIcon.setDrawableResource(R.drawable.top_icon);
		topIcon.setSize(75,60);
		topIcon.setPosition(85,1080-235);
		addActor(topIcon);

		titleLabel = new Label(page);
		titleLabel.setTextSize(44);
		titleLabel.setText("榜单");
		titleLabel.setPosition(180, 1080-235+2);
		titleLabel.setTextColor(android.graphics.Color.parseColor("#A7A7A7"));
		addActor(titleLabel);
		rd = new RemoteData(page.getActivity());
		addGrid(menuArray);
	}

	public void addGrid(List menuArray){
		mGrid = new Grid(getPage());
		mGrid.setSize(240, 1080-300+10);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRememberSelected(true);
		mGrid.setAdjustiveScrollLengthForBackward(0.2f);
		mGrid.setScrollType(Grid.SCROLL_TYPE_NORMAL);
		mGrid.setRowNum(1);
		mGrid.setCull(false);
		mGrid.setPosition(40, 20);
		menulstAdapter = new TopMenuListAdapter(getPage());
		menulstAdapter.setMenulist(menuArray);
		mGrid.setSelection(0);
		mGrid.setAdapter(menulstAdapter);
		// 焦点变动监听 用于页数计算
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int position, Actor actor) {
				mactor = actor;
			}
		});
		addActor(mGrid);
	}

	public void setOnItemSelectedChangeListener(
			OnItemSelectedChangeListener onitemselectorChangelisntener) {
		mGrid.setOnItemSelectedChangeListener(onitemselectorChangelisntener);
	}

	public Actor getMactor() {
		return mactor;
	}

	public Actor _setFavView() {
		TopMenuListItem item = (TopMenuListItem) mGrid.getActorAtPosition(2);
		return mactor;
	}

	public void setSelection(int pos) {
		mGrid.setSelection(pos, true);
	}

}
