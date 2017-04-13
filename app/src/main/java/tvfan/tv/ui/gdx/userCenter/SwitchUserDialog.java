package tvfan.tv.ui.gdx.userCenter;

import java.util.ArrayList;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.UserHelper;
import tvfan.tv.dal.models.UserInfo;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.userCenter.SwitchUserItemAdapter.SwitchUserItem;
import android.content.Context;
import android.util.Log;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

public class SwitchUserDialog extends Dialog {

	Image bgImg, image, lineImg;
	Actor baseActor;
	Label titleLabel, titleLabel1, bootLabel, bootLabel2, bootLabel3;
	Grid menuList;
	CullGroup cullGroup;
	boolean clicktip = false;
	int selectPos = 0;
	ArrayList<UserInfo> arrUserInfo = new ArrayList<UserInfo>();
	private SwitchUserItemAdapter menuAdapter;
	SwitchUserItem switchUserItem;
	private UserHelper userHelper;
	Context context;
	private LocalData localData;
	tvfan.tv.ui.gdx.userCenter.Page page;

	public SwitchUserDialog(Page page) {
		super(page);
	}

	public SwitchUserDialog(Page page, Context context) {
		super(page);
		setSize(1920, 1080);
		setPosition(0, 0);
		this.context = context;
		this.page = (tvfan.tv.ui.gdx.userCenter.Page) page;
		userHelper = new UserHelper(context);
		localData = new LocalData(context);
		_initView();
	}

	private void _initView() {
		bgImg = new Image(getPage());
		bgImg.setPosition(0, 0);
		bgImg.setSize(1920, 1080);
		bgImg.setDrawableResource(R.drawable.bj);
		addActor(bgImg);

		titleLabel = new Label(getPage());
		titleLabel.setPosition(150, 920);
		titleLabel.setTextSize(60);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlpha(0.9f);
		titleLabel.setText("切换账号");
		addActor(titleLabel);

		titleLabel1 = new Label(getPage());
		titleLabel1.setPosition(300, 770);
		titleLabel1.setColor(Color.WHITE);
		titleLabel1.setTextSize(40);
		titleLabel1.setText("最近登录过的账号:");
		titleLabel1.setAlpha(0.9f);

		addActor(titleLabel1);
		lineImg = new Image(getPage());
		lineImg.setPosition(150, 880);
		lineImg.setSize(1620, 2);
		lineImg.setDrawableResource(R.drawable.line);
		lineImg.setFocusAble(false);
		addActor(lineImg);

		bootLabel = new Label(getPage());
		bootLabel.setPosition(1920 - 150 - 180 - 120 - 10, 110);
		bootLabel.setColor(Color.WHITE);
		bootLabel.setTextSize(30);
		bootLabel.setText("按");
		bootLabel.setAlpha(0.9f);
		addActor(bootLabel);

		bootLabel2 = new Label(getPage());
		bootLabel2.setPosition(1920 - 150 - 180 - 80, 108);
		bootLabel2.setColor(Color.WHITE);
		bootLabel2.setTextSize(30);
		bootLabel2.setAlpha(0.9f);
		bootLabel2.setText("菜单");
		addActor(bootLabel2);

		image = new Image(getPage());
		image.setSize(90, 60);
		image.setPosition(1496, 95);
		image.setDrawableResource(R.drawable.btn);
		addActor(image);

		bootLabel3 = new Label(getPage());
		bootLabel3.setPosition(1920 - 150 - 180, 110);
		bootLabel3.setColor(Color.WHITE);
		bootLabel3.setTextSize(30);
		bootLabel3.setAlpha(0.9f);
		bootLabel3.setText("键可删除账号");
		addActor(bootLabel3);

		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(1920, 450);
		cullGroup.setPosition(0, 320);
		cullGroup.setCullingArea(new Rectangle(0, 0, 1920, 450));
		addActor(cullGroup);

	}

	public void _initData() {
		arrUserInfo = userHelper.queryAllUser();
		if (!arrUserInfo.isEmpty())
			initmenuList();
	}

	private void initmenuList() {
		cullGroup.clearChildren();
		menuList = new Grid(getPage());
		menuList.setPosition(0, 100);
		menuList.setSize(1920, 300);
		menuList.setGapLength(0);
		menuList.setOrientation(Grid.ORIENTATION_VERTICAL);
		menuList.setCullingArea(new Rectangle(0, -20, 1920, 340));
		menuList.setRowNum(1);
		menuAdapter = new SwitchUserItemAdapter(getPage());
		menuAdapter.setData(arrUserInfo);
		menuList.setAdapter(menuAdapter);
		menuList.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int arg0, Actor arg1) {
				// TODO Auto-generated method stub
				selectPos = arg0;
				if (!arrUserInfo.get(arg0).getUserid()
						.equals(AppGlobalVars.getIns().USER_ID)) {
					switchUserItem = (SwitchUserItem) arg1;
					switchUserItem.setFocus(false);
				}
			}
		});
		menuList.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor arg0, int arg1, AbsListView arg2) {
				// TODO Auto-generated method stub
				if (switchUserItem != null) {
					switchUserItem.setFocus(false);
				}
				switchUserItem = (SwitchUserItem) arg0;
				switchUserItem.setFocus(true);
				if (!arrUserInfo.get(arg1).getUserid()
						.equals(AppGlobalVars.getIns().USER_ID)) {
					AppGlobalVars.getIns().USER_ID = arrUserInfo.get(arg1)
							.getUserid();
					AppGlobalVars.getIns().USER_TOKEN = arrUserInfo.get(arg1)
							.getToken();
					AppGlobalVars.getIns().USER_PIC = arrUserInfo.get(arg1)
							.getWxheadimgurl();
					AppGlobalVars.getIns().USER_NICK_NAME = arrUserInfo.get(
							arg1).getWxname();

					localData.setKV(
							AppGlobalConsts.PERSIST_NAMES.CURRENT_USER.name(),
							arrUserInfo.get(arg1).getUserid());
					page._switchUser();
					page.sendLocalMsg();
					SwitchUserDialog.this.dismiss();
				}

			}
		});
		cullGroup.addActor(menuList);
		menuList.setSelection(0, true);

	}

	public void removeUser() {

		arrUserInfo.remove(selectPos);
		int pos = selectPos < arrUserInfo.size() - 1 ? selectPos : arrUserInfo
				.size() - 1;
		if (!arrUserInfo.isEmpty()) {
			menuList.notifyDataChanged();
			menuList.setSelection(pos, true);
		} else {
			SwitchUserDialog.this.dismiss();
		}

	}

	public void showDelDialog() {
		new DeleteUserDialog(getPage(), arrUserInfo.get(selectPos).getUserid(),
				arrUserInfo.get(selectPos).getWxname(), context).show();
	}

	@Override
	public void onResume() {
		Utils.resetImageSource(bgImg, R.drawable.bj);
		Utils.resetImageSource(image, R.drawable.btn);
		Utils.resetImageSource(lineImg, R.drawable.line);
		titleLabel1.setText("最近登录过的账号:");
		titleLabel.setText("切换账号");
		bootLabel3.setText("键可删除账号");
		bootLabel.setText("按");
		bootLabel2.setText("菜单");

		super.onResume();
	}
}
