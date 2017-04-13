package tvfan.tv.ui.gdx.userCenters;

import android.content.Context;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.R;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.UserHelper;
import tvfan.tv.lib.Utils;

public class DeleteUserDialog extends Dialog {

	private String uname;
	private Image bgImg;
	private Image sureBg, cancalBg;
	private Image sure, cancal;
	private Label titleLabel, surelabel,cancallabel;
	private UserHelper userHelper;
	private LocalData localData;
	private tvfan.tv.ui.gdx.userCenters.Page myPage;

	public DeleteUserDialog(final Page page, final String uid, String uname,
			Context context) {
		super(page);
		setSize(1000, 660);
		setPosition(460, 270);
		this.myPage = (tvfan.tv.ui.gdx.userCenters.Page) page;
		this.uname = uname;
		userHelper = new UserHelper(context);
		localData = new LocalData(context);
		bgImg = new Image(getPage());
		bgImg.setPosition(0, 0);
		bgImg.setSize(1000, 660);
		bgImg.setDrawableResource(R.drawable.playercenter_layout_background);
		addActor(bgImg);

		titleLabel = new Label(getPage());
		titleLabel.setSize(1000, 45);
		titleLabel.setTextSize(45);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlpha(0.9f);
		titleLabel.setText("点击确认即可删除" + uname);
		titleLabel.setPosition(0, 660 - 120 - 45);
		titleLabel.setAlignment(Align.center);
		addActor(titleLabel);

		sureBg = new Image(getPage());
		sureBg.setSize(386, 156);
		sureBg.setPosition(125 - 28 + 10, 80 - 28);
		sureBg.setDrawableResource(R.drawable.new_foucs);
		sureBg.setVisible(false);
		addActor(sureBg);

		sure = new Image(getPage());
		sure.setSize(350, 100);
		sure.setPosition(125, 80);
		sure.setName("sure");
		sure.setNextFocusRight("cancal");
		sure.setDrawableResource(R.drawable.listbj);
		sure.setFocusAble(true);

		surelabel = new Label(getPage());
		surelabel.setSize(350, 100);
		surelabel.setPosition(125, 80);
		surelabel.setAlignment(Align.center);
		surelabel.setTextSize(45);
		surelabel.setColor(Color.WHITE);
		surelabel.setText("确认");
		surelabel.setAlpha(1.0f);
		addActor(surelabel);

		cancalBg = new Image(getPage());
		cancalBg.setSize(386, 156);
		cancalBg.setPosition(525 - 28 + 10, 80 - 28);
		cancalBg.setDrawableResource(R.drawable.new_foucs);
		cancalBg.setVisible(false);
		addActor(cancalBg);

		cancal = new Image(getPage());
		cancal.setSize(350, 100);
		cancal.setPosition(525, 80);
		cancal.setName("cancal");
		cancal.setNextFocusLeft("sure");
		cancal.setDrawableResource(R.drawable.listbj);
		cancal.setFocusAble(true);

		cancallabel = new Label(getPage());
		cancallabel.setSize(350, 100);
		cancallabel.setPosition(525, 80);
		cancallabel.setAlignment(Align.center);
		cancallabel.setTextSize(45);
		cancallabel.setColor(Color.WHITE);
		cancallabel.setText("取消");
		cancallabel.setAlpha(1.0f);
		addActor(cancallabel);

		sure.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					cancalBg.setVisible(false);
					sureBg.setVisible(true);
				} else {
					sureBg.setVisible(false);
				}

			}
		});
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Actor arg0) {
				// TODO Auto-generated method stub

				userHelper.removeUser(uid);
				//删除当前账号
				if (uid.equals(AppGlobalVars.getIns().USER_ID)) {
					localData
							.removeKV(AppGlobalConsts.PERSIST_NAMES.CURRENT_USER
									.name());
					AppGlobalVars.getIns().USER_ID = localData
							.getKV(AppGlobalConsts.PERSIST_NAMES.DEFAULT_USER
									.name());
					AppGlobalVars.getIns().USER_PIC = "";
					AppGlobalVars.getIns().USER_TOKEN = "";
					AppGlobalVars.getIns().USER_NICK_NAME = "个人中心";
					myPage.sendLocalMsg();
				}
				DeleteUserDialog.this.dismiss();
				((tvfan.tv.ui.gdx.userCenters.Page) page)._deleteUser();
				
			}
		});
		addActor(sure);
		cancal.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				if (arg1) {
					sureBg.setVisible(false);
					cancalBg.setVisible(true);
				} else {
					cancalBg.setVisible(false);
				}

			}
		});
		cancal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Actor arg0) {
				// TODO Auto-generated method stub
				DeleteUserDialog.this.dismiss();
			}
		});
		addActor(cancal);

	}
	@Override
	public void onResume() {
		Utils.resetImageSource(bgImg, R.drawable.playercenter_layout_background);
		Utils.resetImageSource(cancalBg, R.drawable.new_foucs);
		Utils.resetImageSource(sureBg, R.drawable.new_foucs);
		Utils.resetImageSource(sure, R.drawable.listbj);
		Utils.resetImageSource(cancal, R.drawable.listbj);
		
		surelabel.setText("确认");
		cancallabel.setText("取消");
		titleLabel.setText("点击确认即可删除" + uname);
		super.onResume();
	}
}
