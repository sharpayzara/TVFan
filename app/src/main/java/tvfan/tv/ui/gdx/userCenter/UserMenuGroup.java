package tvfan.tv.ui.gdx.userCenter;

import tvfan.tv.AppGlobalVars;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.userCenter.UserMenuListAdapter.UserMenuListItem;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
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
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

/**
 * 個人中心-左側用戶界面
 * 
 * @author 孫文龍
 * 
 */
public class UserMenuGroup extends Group implements LoaderListener {

	private PageImageLoader pageImageLoader;
	private Label titleLabel, userNameLabel;
	private Image lineImg, hImage,borderImage;
	private String imgUrl;
	private CullGroup userNamecullGroup;
	Grid mGrid;
	private Actor mactor;
	UserMenuListAdapter menulstAdapter;

	public UserMenuGroup(Page page, String[] menuArray) {
		super(page);
		// 初始化头标签
		titleLabel = new Label(getPage());
		titleLabel.setSize(480, 60);
		titleLabel.setTextSize(60);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlignment(Align.center);
		titleLabel.setAlpha(0.9f);
		titleLabel.setText("个人中心");
		titleLabel.setPosition(20, 920);
		addActor(titleLabel);

		// 画线
		lineImg = new Image(getPage());
		lineImg.setDrawableResource(R.drawable.juji_line);
		lineImg.setSize(300, 1.5f);
		lineImg.setAlign(Align.center_Horizontal);
		lineImg.setPosition(110, 900);
		addActor(lineImg);

		// 头像
		hImage = new Image(getPage());
		hImage.setPosition(185, 740);
		hImage.setSize(140, 140);
		addActor(hImage);

		// 头像外圈
		borderImage = new Image(getPage());
		borderImage.setPosition(167, 722);
		borderImage.setSize(175, 175);
		borderImage.setDrawableResource(R.drawable.border);
		borderImage.setVisible(false);
		addActor(borderImage);

		userNamecullGroup = new CullGroup(getPage());
		userNamecullGroup.setSize(360, 34);
		userNamecullGroup.setPosition(80, 690);
		userNamecullGroup.setCullingArea(new Rectangle(0, 0, 360, 34));
		addActor(userNamecullGroup);

		userNameLabel = new Label(getPage());
		userNameLabel.setPosition(0, 0);
		userNameLabel.setTextSize(35);
		userNameLabel.setSize(360, 30);
		userNameLabel.setColor(Color.WHITE);
		userNameLabel.setAlpha(0.9f);
		userNameLabel.setMarquee(true);
		userNameLabel.setAlignment(Align.center);
//		userNamecullGroup.addActor(userNameLabel);

		mGrid = new Grid(getPage());
		mGrid.setSize(330, 640);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRememberSelected(true);
		mGrid.setAdjustiveScrollLengthForBackward(0.2f);
		mGrid.setScrollType(Grid.SCROLL_TYPE_NORMAL);
		mGrid.setRowNum(1);
		mGrid.setCull(false);
		mGrid.setPosition(80, 20);
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
			borderImage.setVisible(true);
		} else {
			borderImage.setVisible(false);
			hImage.setDrawableResource(R.drawable.uer1);
		}
	}

	public void initHeadImg(String mimgurl) {
		imgUrl = mimgurl;
		userNameLabel.setText(AppGlobalVars.getIns().USER_NICK_NAME);
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
		Utils.resetImageSource(lineImg, R.drawable.juji_line);
		userNameLabel.setText(AppGlobalVars.getIns().USER_NICK_NAME);
		loadHeadImg(imgUrl);
		super.onResume();
	}
	public void refreshMenu(String[] menuArray){
		menulstAdapter.setMenulist(menuArray);
		mGrid.notifyDataChanged();
		setSelection(0);
	}
}
