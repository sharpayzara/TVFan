package tvfan.tv.ui.gdx.userCenters;

/**
 * 個人中心-入口
 * 
 * @author 孫文龍
 * 
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.FileUtils;
import tvfan.tv.ui.gdx.widgets.Button;

/**
 * 海报页主入口
 * 
 * @author sadshine
 */
public class MessageDetailPage extends BasePage implements LoaderListener {

	private Image bgImg;
	MsgVeritcalViewPager mVeritcalViewPager;
	Group dg;
	private Image lineImg, mesImg;
	private Label titleLabel, timeLabel, mesLabel1;
	int j = 0;
	private Button sure;
	private RemoteData rd;
	private int mesLabel1Hight = 0;
	private int mesLabellan = 1;
	private int mesLabelypos = 0;
	HashMap<String, String> _params = new HashMap<String, String>();
	private PageImageLoader pageImageLoader;
	LocalData _ld = null;
	String url;
	String msgId = "";
	String title = "";
	String packageName = "";
	String appName = "";
	String downloadUrl = "";
	String content;
	String time = "2015-02-09 12:30";
	String msgID = "";
	String msgAction = "";
	String img = "";
	String sureText = "";

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		msgID = bundle.getString("msgID"); // 获取msgid
		msgAction = bundle.getString("msgType"); // 获取msgType
		rd = new RemoteData(getActivity());
		_ld = new LocalData(getActivity());
		bgImg = new Image(this);
		bgImg.setPosition(0, 0);
		bgImg.setSize(1920, 1080);
		bgImg.setFocusAble(false);
		bgImg.setDrawableResource(R.drawable.bj);
		addActor(bgImg);

		mVeritcalViewPager = new MsgVeritcalViewPager(this);
		mVeritcalViewPager.setSize(1920, 1080);
		mVeritcalViewPager.setPosition(0, 0);
		requestDate();

	}

	public void requestDate() {

		rd.getDeMsgList(msgID, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				msgId = response.optString("msgId", "");
				title = response.optString("title", "");
				content = response.optString("content", "");
				time = response.optString("createTime", "");
				img = response.optString("img", "");
				packageName = response.optString("packageName", "");
				appName = response.optString("appName", "");
				downloadUrl = response.optString("downloadUrl", "");
				if (content != null && content.length() > 0) {
					content = Replace(content, "\r\n", "\n" + "  ");
				}

				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						getView();
						addActor(mVeritcalViewPager);
						if (img != null && !img.equals("")&&!img.equals("无")) {
							initHeadImg(img);
						}
					}
				});
			}

			@Override
			public void onError(String errorMessage) {
			}
		});

	}

	public static String Replace(String strReplaced, String oldStr,
			String newStr) {
		int pos = 0;
		int findPos;
		while ((findPos = strReplaced.indexOf(oldStr, pos)) != -1) {
			strReplaced = strReplaced.substring(0, findPos) + newStr
					+ strReplaced.substring(findPos + oldStr.length());
			findPos += newStr.length();
		}
		return strReplaced;
	}

	public void getView() {
		initTitle();
		if (content != null && content.length() != 0) {
			mesLabellan = content.length() / 35;
			mesLabel1Hight = mesLabellan * 50;
			mesLabel1 = new Label(this);
			mesLabel1.setSize(1920 - 300, mesLabel1Hight);
			mesLabel1.setPosition(150, mesLabelypos - mesLabel1Hight);
			mesLabel1.setTextSize(40);
			mesLabel1.setAlignment(Align.center);
			mesLabel1.setSpacingadd(10.0f);
			mesLabel1.setTextColor(Color.WHITE);
			mesLabel1.setFocusAble(false);
			mesLabel1.setAlpha(0.9f);
			mesLabel1.setMaxLine(mesLabellan);
			mesLabel1.setText(content);
			mVeritcalViewPager.addLabelSection(mesLabel1, mesLabelypos
					- mesLabel1Hight);
		}

		if (msgAction != null) {
			if (msgAction.equals("OPENTXT")) {

			} else {
				if (msgAction.equals("UPGRADE")) {
					sureText = "升级";
				} else if (msgAction.equals("DOWNLOAD")) {
					sureText = "下载";
				}
				sure = new Button(this, 212, 70);
				sure.setPosition(855, 100);
				sure.getImage().setDrawableResource(R.mipmap.upgrade_normal);
				sure.setFocusBackGround(R.mipmap.upgrade_selected);
				sure.setUnFocusBackGround(R.mipmap.upgrade_normal);
				sure.setFocusAble(true);
				sure.getLabel().setText(sureText);
				sure.getLabel().setTextSize(45);
				sure.getLabel().setTextColor(Color.WHITE);
				sure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(Actor arg0) {
						// TODO Auto-generated method stub
						if (msgAction.equals("UPGRADE")) {
							_checkUpd();
						} else if (msgAction.equals("DOWNLOAD")) {
							if (!packageName.equals("")
									&& !downloadUrl.equals("")) {
								Bundle options = new Bundle();
								options.putString(
										"actionParam",
										"{\"name\":\""
												+ appName
												+ "\",\"action\":\"LAUNCH_APP\",\"packageName\":\""
												+ packageName
												+ "\",\"downloadUrl\":\""
												+ downloadUrl
												+ "\",\"params\":[]}");
								doAction(ACTION_NAME.valueOf(ACTION_NAME.class,
										"LAUNCH_APP"), options);
							}else{
								Utils.showToast("链接无效，下载失败");
							}

						}

					}
				});
				mVeritcalViewPager.addButtonSection(sure, mesLabelypos
						- mesLabel1Hight - 100);
			}
		}

	}

	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		bgImg.setDrawableResource(R.drawable.bj);
		lineImg.setDrawableResource(R.drawable.juji_line);
		titleLabel.setText(title);
		mesLabel1.setText(content);
		timeLabel.setText(time);
		if (sure != null)
			sure.getImage().setDrawableResource(R.mipmap.upgrade_normal);
		initHeadImg(url);
	}

	@Override
	public void recyclePage() {
		super.recyclePage();
	}

	@Override
	public void onDispose() {
		super.onDispose();
	}

	@Override
	public void onPause() {
		super.onPause();
	};

	private void initTitle() {

		lineImg = new Image(this);
		lineImg.setDrawableResource(R.drawable.juji_line);
		lineImg.setFocusAble(false);
		lineImg.setSize(1764, 1.5f);
		lineImg.setPosition(80, 820);
		mVeritcalViewPager.addImageSection(lineImg);

		// 初始化头标签
		titleLabel = new Label(this);
		titleLabel.setSize(1140, 60);
		titleLabel.setTextSize(52);
		titleLabel.setTextColor(Color.WHITE);
		titleLabel.setFocusAble(false);
		titleLabel.setAlignment(Align.center);
		titleLabel.setAlpha(0.5f);
		titleLabel.setPosition(400, 1080 - 230);
		titleLabel.setText(title);
		mVeritcalViewPager.addTitleLabelSection(titleLabel);

		timeLabel = new Label(this);
		timeLabel.setSize(400, 30);
		timeLabel.setTextSize(30);
		timeLabel.setFocusAble(false);
		timeLabel.setTextColor(Color.WHITE);
		timeLabel.setAlignment(Align.right);
		timeLabel.setAlpha(0.5f);
		timeLabel.setText(time);
		timeLabel.setPosition(1408, 1080-111);
		mVeritcalViewPager.addTimeLabelSection(timeLabel);
		String a=url;
		if (img != null && !img.equals("")&&!img.equals("无")) {
			mesLabelypos = 380;
			// 海报
			mesImg = new Image(this);
			mesImg.setFocusAble(false);
			mesImg.setSize(600, 360);
			mesImg.setPosition(660, 400);
			mVeritcalViewPager.addMesImgSection(mesImg);
		} else {
			mesLabelypos = 780;
		}
	}

	/**
	 * 初始化图片
	 */
	public void initHeadImg(String imgurl) {

		if (imgurl == null) {
			return;
		}
		if (imgurl.isEmpty()) {
			return;
		}
		this.url = imgurl;
		if (pageImageLoader != null) {
			pageImageLoader.reuse();
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(this);
		pageImageLoader.startLoadBitmap(imgurl, "list", true, 5, this, imgurl);
	}

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
		// TODO Auto-generated method stub
		if (mesImg != null)
			mesImg.setDrawable(new TextureRegionDrawable(arg1));

	}

	public boolean onKeyDown(int keycode) {
		switch (keycode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mVeritcalViewPager.smoothScollTo(100, 0.8f, 1);
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			mVeritcalViewPager.smoothScollTo(-100, 0.8f, 0);
		}
		return super.onKeyDown(keycode);

	};

	/*
	 * PRIVATE METHODS
	 */
	private boolean _checkUpd() {
		String rfu = _ld.getKV(AppGlobalConsts.PERSIST_NAMES.READY_FOR_UPDATE
				.name());
		if (!TextUtils.isEmpty(rfu) && rfu.equals("1")) {

			String vc = _ld
					.getKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_CODE
							.name());

			String svc = null;
			try {
				svc = this
						.getActivity()
						.getPackageManager()
						.getPackageInfo(this.getActivity().getPackageName(),
								PackageManager.GET_CONFIGURATIONS).versionCode
						+ "";
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				svc = null;
			}

			if (!TextUtils.isEmpty(vc) && svc != null
					&& Integer.parseInt(vc) <= Integer.parseInt(svc)) {
				_ld.setKV(
						AppGlobalConsts.PERSIST_NAMES.READY_FOR_UPDATE.name(),
						"0");
				_ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_CODE
						.name(), "0");
				return false;
			}

			if (isValid()) {
				//安装apk
				String uri = "file://"+_ld.getKV("UpdateAdress");
				installApk(uri);
				return true;
			}
		} else {
			Utils.showToast("开始下载 ，下次启动app安装");
			/* 启动EPG升级和引导图片下载服务 */
			Intent intent = new Intent("tvfan.tv.daemon.EPGUpdateService");
			//intent.setPackage(getActivity().getPackageName());
			this.getActivity().startService(intent);
		}

		return false;
	}
	private void installApk(String uri) {
		Intent installIntent = new Intent(
				Intent.ACTION_VIEW);
		installIntent.setDataAndType(Uri.parse(uri),
				"application/vnd.android.package-archive");
		installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		this.getActivity().startActivity(installIntent);
	}
	public boolean isValid() {
		String uri = _ld.getKV("UpdateAdress");
		if (FileUtils.isFileExist(uri)) {
			return true;
		} else return false;
	}
}
