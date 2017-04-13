package tvfan.tv.ui.gdx.setting;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import tvfan.tv.App;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.lib.QRCodeLoader;
import tvfan.tv.lib.QRCodeLoader.QRCodeLoaderListener;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.ConfirmLogDialog;

public class Page extends BasePage implements QRCodeLoaderListener {
	private Label titleLabel, productNameLabel, productTypeLabel;
	private Image backImage, tvfanImage, lineImage;
	private Label producersLabel, hotLine;
	private Label codeLabel,codeLabel2;
	private float Alpha8 = 0.8f;
	private QRCodeLoader qrCodeLoder;
	private Image qrCodeImg;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initBackground();// 添加背景图
		initTitle();// 初始化标题
		initLine();// 加载分割线
		initContent();// 初始化内容
	    initcodeloder();//初始化二维码
	}

	private void initcodeloder() {
		qrCodeLoder = new QRCodeLoader(this);
		if (qrCodeLoder != null) {
			qrCodeLoder.cancelLoad();
		}
		qrCodeImg = new Image(this);

		qrCodeImg = new Image(this);
		qrCodeImg.setSize(370, 370);
		qrCodeImg.setPosition(1150,420);
		qrCodeImg.setDrawable(new TextureRegionDrawable(
				findTextureRegion(R.drawable.qrcode)));
		
		addActor(qrCodeImg);
		
		codeLabel = new Label(this);
		codeLabel.setSize(370, 40);
		codeLabel.setTextSize(App.adjustFontSize(37));
		codeLabel.setColor(Color.WHITE);
		codeLabel.setAlpha(Alpha8);
//		codeLabel.setAlignment(Align.center);
		codeLabel.setPosition(1150, 420 - 50);
		codeLabel.setText("关注电视粉微信公众号");
		addActor(codeLabel);
		
		codeLabel2 = new Label(this);
		codeLabel2.setSize(370, 40);
		codeLabel2.setTextSize(App.adjustFontSize(37));
		codeLabel2.setColor(Color.WHITE);
		codeLabel2.setAlpha(Alpha8);
//		codeLabel2.setAlignment(Align.center);
		codeLabel2.setPosition(1150, 420 - 50-50);
		codeLabel2.setText("第一时间获得更多精彩");
		addActor(codeLabel2);
		
//		int webport = InteropService.getPort();
//		String webip = Utils.getLocalIpAddress();
//		qrCodeLoder.startLoad("http://"+webip+":"+webport, 370, 370, this);

	}

	private void initLine() {
		lineImage = new Image(this);
		lineImage.setDrawableResource(R.drawable.lineh);
		lineImage.setSize(1920 - 300, 2);
		lineImage.setPosition(150, 870);
		addActor(lineImage);
	}

	private void initContent() {

		tvfanImage = new Image(this);
		tvfanImage.setDrawableResource(R.drawable.wordstyle);
		tvfanImage.setPosition(150, 100);
		tvfanImage.setSize(900, 700);

		producersLabel = new Label(this);
		producersLabel.setPosition(1150, 100);
		producersLabel.setText("北京小蚁互动网络科技有限公司");
		producersLabel.setTextSize(App.adjustFontSize(36));
//		producersLabel.setTextColor(0xffffffff);
		producersLabel.setColor(Color.WHITE);
//		producersLabel.setAlpha(Alpha8);

		/*hotLine = new Label(this);
		hotLine.setPosition(150, 450);
		hotLine.setText(AppGlobalVars.getIns().PHONE_NUMBER);
		System.out.println(AppGlobalVars.getIns().PHONE_NUMBER);
		hotLine.setTextSize(40);
		hotLine.setColor(Color.WHITE);
//		hotLine.setAlpha(Alpha8);
*/
		addActor(tvfanImage);
		addActor(producersLabel);
//		addActor(hotLine);

		// 软件版本
		productNameLabel = new Label(this);
		productNameLabel.setText("软件版本:" + getVersionName());
		productNameLabel.setPosition(1150, 200);
		productNameLabel.setTextSize(App.adjustFontSize(33));
		productNameLabel.setColor(Color.WHITE);
//		productNameLabel.setAlpha(Alpha8);
		// mac地址
		/*productTypeLabel = new Label(this);
		productTypeLabel.setText("MAC 地址:" + AppGlobalVars.getIns().LAN_MAC);
		productTypeLabel.setPosition(650, 150);
		productTypeLabel.setTextSize(40);
		productTypeLabel.setColor(Color.WHITE);
//		productTypeLabel.setAlpha(Alpha8);
*/		addActor(productNameLabel);
//		addActor(productTypeLabel);
	}

	private void initBackground() {
		backImage = new Image(this);
		backImage.setPosition(0, 0);
		backImage.setSize(1920, 1080);
//		backImage.setDrawableResource(R.drawable.bj);
		backImage.setDrawableResource(R.drawable.new_foucs);
		addActor(backImage);
	}

	private void initTitle() {
		titleLabel = new Label(this);
		titleLabel.setText("关于");
		titleLabel.setPosition(150, 920);
		titleLabel.setTextSize(60);
		titleLabel.setTextColor(0xffffffff);
		titleLabel.setAlpha(Alpha8);
		addActor(titleLabel);

	}

	private String getVersionName() {

		try {
			// 获取packagemanager的实例
			PackageManager packageManager = this.getActivity()
					.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(this
					.getActivity().getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (Exception e) {
			return "";
		}

	}

	final static private int[] LOG_DB = { KeyEvent.KEYCODE_DPAD_UP,
			KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT,
			KeyEvent.KEYCODE_DPAD_RIGHT, };

	private int mKeycodeStackIndex = 0;

	@Override
	public boolean onKeyDown(int keycode) {
		// 20150522tao

		super.onKeyDown(keycode);
		if (true) {
			if (keycode == LOG_DB[mKeycodeStackIndex]) {
				mKeycodeStackIndex++;
			} else {
				mKeycodeStackIndex = 0;
			}
			if (mKeycodeStackIndex >= LOG_DB.length) {
				/*
				 * if (findViewById(R.id.debuglayout).getVisibility() !=
				 * View.VISIBLE) {
				 * findViewById(R.id.debuglayout).setVisibility(View.VISIBLE);
				 * ((
				 * TextView)findViewById(R.id.debugtext)).setText(App.debugtext
				 * ); }
				 */

				StringBuffer sb = new StringBuffer();

				for (String key : AppGlobalVars.getIns().HTTP_STACK.keySet()) {
					sb.append(AppGlobalVars.getIns().HTTP_STACK.get(key)
							+ "\r\n");
				}

				String log = sb.toString();
				new ConfirmLogDialog(this, log).show();
				mKeycodeStackIndex = 0;
			}
		}

		if (keycode == KeyEvent.KEYCODE_BACK) {

		}
		// 20150522tao

		return true;
	}

	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		Utils.resetImageSource(backImage, R.drawable.bj);
		Utils.resetImageSource(lineImage, R.drawable.lineh);
		Utils.resetImageSource(tvfanImage, R.drawable.logo_about);
		
		productTypeLabel.setText("MAC 地址:" + AppGlobalVars.getIns().LAN_MAC);
		productNameLabel.setText("软件版本:" + getVersionName());
		hotLine.setText(AppGlobalVars.getIns().PHONE_NUMBER);
		producersLabel.setText("北京小蚁互动网络科技有限公司");
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
	}

	@Override
	public void onLoadQRCodeComplete(String contents, int imageWidth,
			int imageHeight, TextureRegion textureRegion) {
		try {
					qrCodeImg.setDrawable(textureRegion);				
					return;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	};

}
