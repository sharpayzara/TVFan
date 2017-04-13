package tvfan.tv.ui.gdx.userCenters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.Grid;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.ui.gdx.userCenters.UserMenuListAdapter.UserMenuListItem;

/**
 *  desc  個人中心-左側用戶界面
 *  @author  yangjh
 *  created at  16-4-19 上午11:10
 */
public class UserMenuGroup extends Group implements LoaderListener {

	private PageImageLoader pageImageLoader;
	private Label titleLabel;
	private Image hImage,leftImg;
	private String imgUrl;
	//private CullGroup userNamecullGroup;
	Grid mGrid;
	private Actor mactor;
	UserMenuListAdapter menulstAdapter;

	public UserMenuGroup(Page page, String[] menuArray) {
		super(page);

		leftImg = new Image(getPage());
		leftImg.setPosition(0,0);
		leftImg.setSize(400,1080);
		leftImg.setDrawableResource(R.mipmap.left_bg);
		addActor(leftImg);

		// 初始化头标签
		titleLabel = new Label(getPage());
		titleLabel.setSize(70, 36);
		titleLabel.setTextSize(40);
		titleLabel.setAlignment(Align.center);
		titleLabel.setText("我的");
		titleLabel.setTextColor(android.graphics.Color.parseColor("#A7A7A7"));
		titleLabel.setPosition(105+80, 850);
		addActor(titleLabel);

		// 头像
		hImage = new Image(getPage());
		hImage.setPosition(105, 1080-240);
		hImage.setSize(65, 65);
		hImage.setDrawableResource(R.mipmap.user_icons);
		addActor(hImage);

		/*userNamecullGroup = new CullGroup(getPage());
		userNamecullGroup.setSize(360, 34);
		userNamecullGroup.setPosition(80, 690);
		userNamecullGroup.setCullingArea(new Rectangle(0, 0, 360, 34));
		addActor(userNamecullGroup);*/

		/*userNameLabel = new Label(getPage());
		userNameLabel.setPosition(0, 0);
		userNameLabel.setTextSize(35);
		userNameLabel.setSize(360, 30);
		userNameLabel.setColor(Color.WHITE);
		userNameLabel.setAlpha(0.9f);
		userNameLabel.setMarquee(true);
		userNameLabel.setAlignment(Align.center);*/
//		userNamecullGroup.addActor(userNameLabel);

		mGrid = new Grid(getPage());
		mGrid.setSize(280, 1080-250);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRememberSelected(true);
		mGrid.setAdjustiveScrollLengthForBackward(0.2f);
		mGrid.setScrollType(Grid.SCROLL_TYPE_NORMAL);
		mGrid.setRowNum(1);
		mGrid.setCull(false);
		mGrid.setPosition(60, 0);
		menulstAdapter = new UserMenuListAdapter(getPage());
		menulstAdapter.setMenulist(menuArray);
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
		UserMenuListItem item = (UserMenuListItem) mGrid.getActorAtPosition(2);
		item.setIsNew();
		return mactor;
	}

	public void setSelection(int pos) {
		mGrid.setSelection(pos, true);
	}
	
	public void hideMessageDot(){
		//根据position获取相关item（我的消息）来进行隐藏新消息的红点
		UserMenuListItem item = (UserMenuListItem) mGrid.getActorAtPosition(2);
		item.setDotVisible(false);
	}

	public void loadHeadImg(String mimgurl) {
		if (imgUrl != null && !imgUrl.equals("")) {
			if (pageImageLoader != null) {
				pageImageLoader.cancelLoad();
			}
			pageImageLoader = new PageImageLoader(getPage());
			pageImageLoader.startLoadBitmap(imgUrl, "list", true, -1, this,
					imgUrl);
		} else {
			hImage.setDrawableResource(R.mipmap.user_icons);
		}
	}

	public void initHeadImg(String mimgurl) {
		imgUrl = mimgurl;
		//userNameLabel.setText(AppGlobalVars.getIns().USER_NICK_NAME);
		loadHeadImg(imgUrl);
	}

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
		// TODO Auto-generated method stub
		hImage.setDrawable(new TextureRegionDrawable(arg1));

	}

	@Override
	public void onResume() {
		titleLabel.setText("个人中心");
		//userNameLabel.setText(AppGlobalVars.getIns().USER_NICK_NAME);
		loadHeadImg(imgUrl);
		super.onResume();
	}
	public void refreshMenu(String[] menuArray){
		menulstAdapter.setMenulist(menuArray);
		mGrid.notifyDataChanged();
		setSelection(0);
	}
}
