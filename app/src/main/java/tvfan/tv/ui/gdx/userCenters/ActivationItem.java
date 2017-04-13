package tvfan.tv.ui.gdx.userCenters;

/**
 * 個人中心-微信註冊界面
 * 
 * @author 孫文龍
 * 
 */

import android.content.Context;
import android.graphics.Color;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.UserHelper;
import tvfan.tv.dal.models.UserInfo;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.widgets.Button;

public class ActivationItem extends Group implements LoaderListener {

	public ActivationItem(final Page page, Context context) {
		super(page);
		this.context = context;
		this.myPage = (tvfan.tv.ui.gdx.userCenters.Page) page;
		rd = new RemoteData(context);
		setSize(1440, 1080);
		userHelper = new UserHelper(context);
		_initView();
	}

	public void _initView() {
		label = new Label(getPage());
		label.setPosition(100, 930);
		label.setTextSize(50);
		label.setTextColor(Color.parseColor("#ffffff"));
		label.setText("用户激活");
		label.setAlpha(0.9f);
		addActor(label);

		// 设置二维码图
		dimensionscodeImg = new Image(getPage());
		dimensionscodeImg.setSize(400, 400);
		dimensionscodeImg.setPosition(190, 480);
		addActor(dimensionscodeImg);

		label1 = new Label(getPage());
		label1.setPosition(250, 400);
		label1.setTextSize(40);
		label1.setTextColor(Color.parseColor("#ffffff"));
		label1.setText("微信扫一扫激活");
		label1.setAlpha(0.9f);
		addActor(label1);

		switchBtnBg = new Image(getPage());
		switchBtnBg.setSize(556, 156);
		switchBtnBg.setPosition(112, 250 - 28);
		switchBtnBg.setDrawableResource(R.drawable.new_foucs);
		switchBtnBg.setVisible(false);
		addActor(switchBtnBg);

		switchBtn = new Button(getPage(), 500, 100);
		switchBtn.setPosition(140, 250);
		switchBtn.getImage().setDrawableResource(R.drawable.userbj);
		switchBtn.setFocusBackGround(R.drawable.userbj);
		switchBtn.setUnFocusBackGround(R.drawable.userbj);
		switchBtn.setFocusAble(true);
		switchBtn.getLabel().setText("切换账号");
		switchBtn.getLabel().setTextColor(Color.WHITE);
		switchBtn.getLabel().setTextSize(40);
		switchBtn.getLabel().setAlpha(0.9f);
		switchBtn.setVisible(false);
		switchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Actor arg0) {
				// TODO Auto-generated method stub
				((tvfan.tv.ui.gdx.userCenters.Page) myPage)
						.showSwltchDialog();
			}
		});
		switchBtn.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				switchBtnBg.setVisible(arg1);
			}
		});
		addActor(switchBtn);

	}

	public void _initBtnView() {
		arrUserInfo = userHelper.queryAllUser();
		switchBtn.setVisible(!arrUserInfo.isEmpty());
	}

	public void _requestTicketData() {
		rd.getWeixinTicket(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				String qrTicket = response.optString("qrTicket", "");
				mqrTicket = qrTicket;
				initImageCode(qrTicket);
			}

			@Override
			public void onError(String errorMessage) {
			}
		});
	}

	/**
	 * 初始化二维码
	 */
	public void initImageCode(String qrTicket) {
		Lg.i("cibn-log", "qrTicket : " + qrTicket);
		String fullUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="
				+ qrTicket;
		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader
				.startLoadBitmap(fullUrl, "list", true, 2, this, fullUrl);
	}

	private Image dimensionscodeImg;
	private Label label, label1;
	private RemoteData rd;
	Context context;
	private PageImageLoader pageImageLoader;
	private String mqrTicket = "";
	private Button switchBtn;
	private Image switchBtnBg;
	private UserHelper userHelper;
	private ArrayList<UserInfo> arrUserInfo = new ArrayList<UserInfo>();
	private tvfan.tv.ui.gdx.userCenters.Page myPage;

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
		// TODO Auto-generated method stub
		Lg.i("cibn-log", "二维码图片下载完成 : " + arg1.getRegionWidth());
		dimensionscodeImg.setDrawable(new TextureRegionDrawable(arg1));
	}

	@Override
	public void onResume() {
		if (!mqrTicket.equals("")) {
			initImageCode(mqrTicket);
		} else {
			_requestTicketData();
		}
		label.setText("用户激活");
		label1.setText("微信扫一扫激活");
		switchBtn.getImage().setDrawableResource(R.drawable.userbj);
		switchBtn.setFocusBackGround(R.drawable.userbj);
		switchBtn.setUnFocusBackGround(R.drawable.userbj);
		switchBtn.getLabel().setText("切换账号");
		switchBtnBg.setDrawableResource(R.drawable.new_foucs);
		super.onResume();
	}
}
