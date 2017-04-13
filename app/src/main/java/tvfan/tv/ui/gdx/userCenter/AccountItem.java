package tvfan.tv.ui.gdx.userCenter;

/**
 * 個人中心-莊戶界面
 * 
 * @author 孫文龍
 * 
 */
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.R;
import tvfan.tv.dal.LocalData;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.Button;

public class AccountItem extends Group {

	private Button regBtn; // 注册按钮
	private Button switchBtn;
	private LocalData localData;
	Context context;
	private tvfan.tv.ui.gdx.userCenter.Page page;
	private String wxname = "";
	private String userid = "";

	public AccountItem(Page page, Context context) {
		super(page);
		this.context = context;
		this.page = (tvfan.tv.ui.gdx.userCenter.Page) page;
		setSize(1540, 1080);
		localData = new LocalData(context);
		_initView();

	}

	public void _setAccountMsg(ContentValues switchCV) {
		wxname = switchCV.get("wxname").toString();
		userid = switchCV.get("userid").toString();
		wNameLabel.setText("微信名:" + wxname);
		accountLabel.setText("会员账号:" + userid);
	}

	private void _initView() {
		titleLabel = new Label(getPage());
		titleLabel.setPosition(100, 930);
		titleLabel.setTextSize(50);
		titleLabel.setTextColor(Color.parseColor("#ffffff"));
		titleLabel.setText("我的账号");
		titleLabel.setAlpha(0.9f);
		addActor(titleLabel);

		wNameLabel = new Label(getPage());
		wNameLabel.setPosition(100, 820);
		wNameLabel.setTextSize(40);
		wNameLabel.setTextColor(Color.parseColor("#ffffff"));
		wNameLabel.setText("微信名:" + wxname);
		wNameLabel.setAlpha(0.9f);
		addActor(wNameLabel);

		macLabel = new Label(getPage());
		macLabel.setPosition(100, 740);
		macLabel.setTextSize(40);
		macLabel.setTextColor(Color.parseColor("#ffffff"));
		macLabel.setText("MAC信息:" + AppGlobalVars.getIns().LAN_MAC);
		macLabel.setAlpha(0.9f);
		addActor(macLabel);

		accountLabel = new Label(getPage());
		accountLabel.setPosition(100, 660);
		accountLabel.setTextSize(40);
		accountLabel.setTextColor(Color.parseColor("#ffffff"));
		accountLabel.setText("会员账号:" + userid);
		accountLabel.setAlpha(0.9f);
		addActor(accountLabel);

		regBtnBg = new Image(getPage());
		regBtnBg.setSize(556, 156);
		regBtnBg.setPosition(100 - 28, 520 - 28);
		regBtnBg.setDrawableResource(R.drawable.new_foucs);
		regBtnBg.setVisible(false);
		addActor(regBtnBg);

		regBtn = new Button(getPage(), 500, 100);
		regBtn.setPosition(100, 520);
		regBtn.setName("regBtn");
		regBtn.setNextFocusDown("switchBtn");
		regBtn.getImage().setDrawableResource(R.drawable.userbj);
		regBtn.setFocusBackGround(R.drawable.userbj);
		regBtn.setUnFocusBackGround(R.drawable.userbj);
		regBtn.setFocusAble(true);
		regBtn.getLabel().setText("注销");
		regBtn.getLabel().setTextColor(Color.WHITE);
		regBtn.getLabel().setTextSize(40);
		regBtn.getLabel().setAlpha(0.9f);
		regBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Actor arg0) {
				// TODO Auto-generated method stub
				localData.removeKV(AppGlobalConsts.PERSIST_NAMES.CURRENT_USER
						.name());
				AppGlobalVars.getIns().USER_ID = localData
						.getKV(AppGlobalConsts.PERSIST_NAMES.DEFAULT_USER
								.name());
				AppGlobalVars.getIns().USER_TOKEN = "";
				AppGlobalVars.getIns().USER_PIC = "";
				AppGlobalVars.getIns().USER_NICK_NAME = "个人中心";
				page.sendLocalMsg();
				page._switchUser();
			}
		});
		regBtn.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					switchBtnBg.setVisible(false);
					regBtnBg.setVisible(true);
				} else {
					regBtnBg.setVisible(false);
				}
			}
		});
		addActor(regBtn);

		switchBtnBg = new Image(getPage());
		switchBtnBg.setSize(556, 156);
		switchBtnBg.setPosition(100 - 28, 390 - 28);
		switchBtnBg.setDrawableResource(R.drawable.new_foucs);
		switchBtnBg.setVisible(false);
		addActor(switchBtnBg);

		switchBtn = new Button(getPage(), 500, 100);
		switchBtn.setPosition(100, 390);
		switchBtn.setName("switchBtn");
		switchBtn.setNextFocusUp("regBtn");
		switchBtn.getImage().setDrawableResource(R.drawable.userbj);
		switchBtn.setFocusBackGround(R.drawable.userbj);
		switchBtn.setUnFocusBackGround(R.drawable.userbj);
		switchBtn.setFocusAble(true);
		switchBtn.getLabel().setText("切换账号");
		switchBtn.getLabel().setTextColor(Color.WHITE);
		switchBtn.getLabel().setTextSize(40);
		switchBtn.getLabel().setAlpha(0.9f);
		switchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Actor arg0) {
				// TODO Auto-generated method stub
				page.showSwltchDialog();
			}
		});
		switchBtn.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					switchBtnBg.setVisible(true);
					regBtnBg.setVisible(false);
				} else {
					switchBtnBg.setVisible(false);
				}

			}
		});
		addActor(switchBtn);
	}

	@Override
	public void onResume() {
		Utils.resetImageSource(regBtnBg, R.drawable.new_foucs);
		regBtn.getImage().setDrawableResource(R.drawable.userbj);
		regBtn.setFocusBackGround(R.drawable.userbj);
		regBtn.setUnFocusBackGround(R.drawable.userbj);
		Utils.resetImageSource(switchBtnBg, R.drawable.new_foucs);
		switchBtn.getImage().setDrawableResource(R.drawable.userbj);
		switchBtn.setFocusBackGround(R.drawable.userbj);
		switchBtn.setUnFocusBackGround(R.drawable.userbj);
		wNameLabel.setText("微信名:" + wxname);
		accountLabel.setText("会员账号:" + userid);
		titleLabel.setText("我的账号");
		macLabel.setText("MAC信息:" + AppGlobalVars.getIns().LAN_MAC);
		super.onResume();
	}

	private Label wNameLabel, titleLabel, macLabel, accountLabel;
	private Image regBtnBg, switchBtnBg;
}
