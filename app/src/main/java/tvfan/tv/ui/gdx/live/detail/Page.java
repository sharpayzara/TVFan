package tvfan.tv.ui.gdx.live.detail;

import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.UserHelper;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.gdx.widgets.Button;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.utils.Utils;

public class Page extends BasePage implements LoaderListener {
	private Image bg, qrcode;
	private TVFANLabel title, weixinTip;
	private RemoteData rd;
	private String mqrTicket;
	private PageImageLoader bgLoader, qrcodeLoader;

	private Button liveBtn;
	private UserHelper userHelper;
	private LocalData localData;
	private String liveId; // 直播id
	private CIBNLoadingView loadingview;
	private boolean a, b; // 用来标识背景图片和二维码图片加载是否完成
	private boolean isFirstIn = true;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		userHelper = new UserHelper(this.getActivity());
		localData = new LocalData(this.getActivity());
		rd = new RemoteData();
		initActors();
		requestTicketData();
		getLiveInfo(bundle);
	}

	@Override
	public void onResume() {
		super.onResume();
		regMSGFilter();
		if (!isFirstIn) {
			// 如果用户此时没有登录或已经注销登录，则需要重新刷新二维码.
			if (!checkUserLoginState()) {
				qrcode.setVisible(true);
				weixinTip.setVisible(true);
				liveBtn.setVisible(false);
				if (!TextUtils.isEmpty(mqrTicket))
					initImageCode(mqrTicket);
			} else {
				qrcode.setVisible(false);
				weixinTip.setVisible(false);
				liveBtn.setVisible(true);
			}
		}
		isFirstIn = false;
	}

	@Override
	public void onPause() {
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.USER_BIND);
		super.onPause();
	}

	private void initActors() {
		bg = new Image(this);
		bg.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		bg.setDrawableResource(R.drawable.default_background);
		addActor(bg);

		title = new TVFANLabel(this);
		title.setSize(1120, 100);
		title.setPosition(800, 800);
		title.setColor(Color.WHITE);
		title.setTextSize(40);
		title.setText("中国 VS 马尔代夫(尚未开始)");
		title.setAlignment(Align.center);
		title.setVisible(false);
		addActor(title);

		qrcode = new Image(this);
		qrcode.setSize(370, 370);
		qrcode.setPosition(1100, 200);
		qrcode.setDrawableResource(R.drawable.default_background);
		qrcode.setVisible(false);
		addActor(qrcode);

		weixinTip = new TVFANLabel(this);
		weixinTip.setSize(1000, 100);
		weixinTip.setPosition(800, 100);
		weixinTip.setColor(Color.WHITE);
		weixinTip.setTextSize(30);
		weixinTip.setText("微信扫一扫,登录即可观看");
		weixinTip.setAlignment(Align.center);
		weixinTip.setVisible(false);
		addActor(weixinTip);

		liveBtn = new Button(this, 300, 150);
		liveBtn.setPosition(300, 300);
		liveBtn.getLabel().setText("观看直播");
		liveBtn.getLabel().setTextSize(35);
		liveBtn.setButtonFocusScale(AppGlobalConsts.FOCUSSCALE);
		liveBtn.setOnClickListener(clickListener);
		liveBtn.setVisible(false);
		addActor(liveBtn);

		loadingview = new CIBNLoadingView(this);
		loadingview.setSize(AppGlobalConsts.APP_WIDTH,
				AppGlobalConsts.APP_HEIGHT);
		loadingview.setVisible(true);
		addActor(loadingview);
	}

	private void getLiveInfo(Bundle bundle) {
		liveId = bundle.getString(liveId);
		if (TextUtils.isEmpty(liveId)) {
			Utils.showToast("参数异常，请联系客服");
			a = true;
			b = true;
			hideLoadingView();
			return;
		}
		rd.getLiveDetail(liveId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response == null) {
					Lg.e(TAG, "getLiveDetail response is null.");
					a = true;
					b = true;
					hideLoadingView();
					return;
				}
				updateViewData(response);
			}

			@Override
			public void onError(String httpStatusCode) {
				Lg.e(TAG, "getLiveDetail onError :" + httpStatusCode);
				NetWorkUtils.handlerError(httpStatusCode, Page.this);
			}
		});
	}

	/**
	 * 刷新view数据
	 * 
	 * @param jsonObj
	 *            从网络获取的json数据
	 */
	private void updateViewData(JSONObject jsonObj) {
		title.setText(jsonObj.optString("name", ""));

		if (bgLoader != null) {
			bgLoader.cancelLoad();
		}
		bgLoader = new PageImageLoader(this);
		bgLoader.startLoadBitmap(jsonObj.optString("picUrl", ""), "list", this,
				"bg");
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(Actor actor) {
			Bundle bundle = new Bundle();
			bundle.putString("liveUrl",
					"p2p://111.206.172.108:9906/559f2502000ebe2a277e44b99a5f0dfa.ts");
			doAction(ACTION_NAME.OPEN_LIVESHOWPLAYER, bundle);
		}
	};

	// 若用户存在，返回true，否则返回false
	private boolean checkUserLoginState() {
		return !TextUtils.isEmpty(AppGlobalVars.USER_TOKEN)
				&& !TextUtils.isEmpty(AppGlobalVars.getIns().USER_PIC);
	}

	private void requestTicketData() {
		rd.getWeixinTicket(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				String qrTicket = response.optString("qrTicket", "");
				mqrTicket = qrTicket;
				initImageCode(qrTicket);
			}

			@Override
			public void onError(String errorMessage) {
				Lg.e(TAG, "getWeixinTicket onError :" + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
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
		if (qrcodeLoader != null) {
			qrcodeLoader.cancelLoad();
		}
		qrcodeLoader = new PageImageLoader(this);
		qrcodeLoader.startLoadBitmap(fullUrl, "list", true, 2, this, "qrcode");
	}

	public void regMSGFilter() {
		// 筛选注册微信绑定注册消息监听
		registerLocalMsgReceiver(new LocalMsgListener() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String userID = "", userName = "", wxID = "", userPicUrl = "", userToken = "";
				String userJson = intent.getExtras().getString(
						AppGlobalConsts.INTENT_MSG_PARAM);
				try {
					JSONObject a = new JSONObject(userJson);
					JSONObject wxmsg = a.getJSONObject("body");
					userID = wxmsg.optString("userID", "");
					wxID = wxmsg.optString("wxID", "");
					userName = wxmsg.optString("userName", "");
					userPicUrl = wxmsg.optString("userPicUrl", "");
					userToken = wxmsg.optString("userToken", "");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				AppGlobalVars.getIns().USER_ID = userID;
				AppGlobalVars.USER_TOKEN = userToken;
				AppGlobalVars.getIns().USER_PIC = userPicUrl;
				AppGlobalVars.getIns().USER_NICK_NAME = userName;
				localData.setKV(
						AppGlobalConsts.PERSIST_NAMES.CURRENT_USER.name(),
						userID);
				ContentValues userData = new ContentValues();
				userData.put("userid", userID);
				userData.put("wxid", wxID);
				userData.put("wxname", userName);
				userData.put("wxheadimgurl", userPicUrl);
				userData.put("token", userToken);
				userHelper.addUser(userData);

				Utils.showToast("您已成功登录!");

				sendLocalMsg();
				qrcode.setVisible(false);
				weixinTip.setVisible(false);
				liveBtn.setVisible(true);
			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.USER_BIND);
	}

	public void sendLocalMsg() {
		Intent inte = new Intent();
		sendLocalStickyMsg(AppGlobalConsts.LOCAL_MSG_FILTER.USER_IMAGE_CHANGE,
				inte);
	}

	private void hideLoadingView() {
		if (a && b) {
			loadingview.setVisible(false);
			title.setVisible(true);
			// 如果用户已经登录
			if (checkUserLoginState()) {
				qrcode.setVisible(false);
				weixinTip.setVisible(false);
				liveBtn.setVisible(true);
			} else {
				qrcode.setVisible(true);
				weixinTip.setVisible(true);
				liveBtn.setVisible(false);
			}
		}
	}

	@Override
	public void onLoadComplete(String url, TextureRegion textureRegion,
			Object tag) {
		if (tag != null && !TextUtils.isEmpty(tag.toString())) {
			if (tag.toString().equalsIgnoreCase("qrcode")) {
				qrcode.setDrawable(textureRegion);
				a = true;
			} else if (tag.toString().equalsIgnoreCase("bg")) {
				bg.setDrawable(textureRegion);
				b = true;
			}
		}
		hideLoadingView();
	}
}
