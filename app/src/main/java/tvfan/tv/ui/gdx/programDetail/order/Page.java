package tvfan.tv.ui.gdx.programDetail.order;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.AppGlobalConsts.LOCAL_MSG_FILTER;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.QRCodeLoader;
import tvfan.tv.lib.QRCodeLoader.QRCodeLoaderListener;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.utils.Utils;
//import com.umeng.analytics.MobclickAgent;

public class Page extends BasePage implements QRCodeLoaderListener {

	private Image mBackImage;
	private TVFANLabel titleLabel;
	private Image topLine;
	private TVFANLabel movieCreator;

	private TVFANLabel orderInfo;
	private TVFANLabel orderTime;

	private TVFANLabel[] taobaoLabel;
	private Image[] verticalLine;
	private Image[] qrCodeImg;
	private QRCodeLoader[] qrCodeLoder;

	private JSONObject productInfo;

	private int fx;

	private String programSeriesId;
	private RemoteData rd;

	private JSONObject orderResult;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Lg.i(TAG, "show order page");
		rd = new RemoteData(getActivity());
		try {
			programSeriesId = bundle.getString("programSeriesId");
			productInfo = new JSONObject(bundle.getString("authorize_result",
					""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// bundle.getString("authorize_result", "");

		registerLocalMsgReceiver(new LocalMsgListener() {

			@Override
			public void onReceive(Context context, Intent intent) {
				try {
					String msg = intent.getStringExtra(AppGlobalConsts.INTENT_MSG_PARAM);
					System.out
							.println("order page receive paysuccess broadcast , msg = "
									+ msg);
					JSONObject obj = new JSONObject(msg);
					if (obj.optJSONObject("head").optString("type")
							.equals("paysuccess")) {
						
						HashMap<String,String> map = new HashMap<String,String>();
						map.put("UID", AppGlobalVars.getIns().USER_ID);
						map.put("PROGRAM_SERIES_ID", programSeriesId); 
						map.put("U_P", AppGlobalVars.getIns().USER_ID + "|" + programSeriesId);
//						MobclickAgent.onEvent(getActivity().getApplicationContext(), "event_buy", map);
						
						Utils.showToast("您已付费成功!");
						finish();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, LOCAL_MSG_FILTER.PAY_RESULT);

		initUI();

	}

	@SuppressLint("SimpleDateFormat") private void initUI() {
		mBackImage = new Image(this);
		mBackImage.setSize(AppGlobalConsts.APP_WIDTH,
				AppGlobalConsts.APP_HEIGHT);
		mBackImage.setPosition(0, 0);
		mBackImage.setDrawable(new TextureRegionDrawable(
				findTextureRegion(R.drawable.bj)));
		addActor(mBackImage);

		titleLabel = new TVFANLabel(this);
		titleLabel.setPosition(150, 930);
		titleLabel.setSize(150, 50);
		titleLabel.setText("支付方式");
		titleLabel.setColor(Color.valueOf("ffffff"));
		titleLabel.setAlpha(0.5f);
		titleLabel.setTextSize(60);
		addActor(titleLabel);

		topLine = new Image(this);
		topLine.setPosition(150, 880);
		topLine.setSize(1620, 1.3f);
		topLine.setDrawableResource(R.drawable.order_line);
		topLine.setAlpha(0.5f);
		addActor(topLine);

		JSONObject obj = null;
		try {
			if (productInfo.opt("products") instanceof JSONArray) {
				obj = (JSONObject) productInfo.optJSONArray("products").get(0);
			} else if (productInfo.opt("products") instanceof JSONObject) {
				obj = productInfo.optJSONObject("products");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		movieCreator = new TVFANLabel(this);
		movieCreator.setPosition(0, 780);
		movieCreator.setSize(AppGlobalConsts.APP_WIDTH, 50);
		movieCreator.setAlignment(Align.center);
		movieCreator.setText("华视影院");
		movieCreator.setColor(Color.valueOf("ffffff"));
		movieCreator.setAlpha(0.5f);
		movieCreator.setTextSize(50);

		if (obj != null) {
			movieCreator.setText(obj.optString("productName"));
		}
		addActor(movieCreator);

		orderInfo = new TVFANLabel(this);
		orderInfo.setPosition(880, 730);
		orderInfo.setSize(150, 20);
		orderInfo.setAlignment(Align.right);
		orderInfo.setText("支付成功后 有效期");
		orderInfo.setColor(Color.valueOf("ffffff"));
		orderInfo.setAlpha(0.5f);
		orderInfo.setTextSize(35);
		addActor(orderInfo);

		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		
		orderTime = new TVFANLabel(this);
		orderTime.setPosition(1030, 730);
		orderTime.setSize(150, 20);
		orderTime.setAlignment(Align.left);
//		try {
//		//	orderTime.setText(sdf.format(sdf.parse(obj.optString("period"))));
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
		orderTime.setColor(Color.valueOf("ffea00"));
		orderTime.setAlpha(0.5f);
		orderTime.setTextSize(35);
		addActor(orderTime);

		//UN2015033100001020
		// rd.getDetailOrder("UN2015033100001023", programSeriesId, "",
		rd.getDetailOrder(AppGlobalVars.getIns().USER_ID, programSeriesId, "",
				"", obj.optString("productCode"), AppGlobalVars.getIns().DEVICE_ID,
				new Listener4JSONObject() {

					@Override
					public void onResponse(JSONObject response) {
						if(isDisposed()){
							return ;
						}
						if (response == null) {
							Lg.i(TAG, "detail order page get response is null");
							return;
						}
						try {
							orderResult = response;
							System.out.println("[CIBN_ODDER_RESULT] = "
									+ response.toString());
							Gdx.app.postRunnable(new Runnable() {

								@Override
								public void run() {
									String code = orderResult
											.optString("resultCode");
									// 调用订购接口成功，成功生成订单.
									if (code.equals("1")) {
										orderTime.setText(orderResult.optString("period",""));
										showQrCodes();
										return;
									}
									// 已经购买，勿重复购买
									else if (code.equals("22001")
											|| code.equals("22002")
											|| code.equals("22003")
											|| code.equals("21003")
											|| code.equals("21005")) {
										Utils.showToast(orderResult
												.optString("resultDesc"));
										finish();
									} else {
										Utils.showToast(orderResult
												.optString("resultDesc"));
										finish();
									}
								}
							});

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(String errorMessage) {

					}
				});

	}

	private void showQrCodes() {

		try {
			JSONArray data = orderResult.optJSONArray("data");
			int total = data.length();
			fx = (1920 - (total - 1) * 200 - 370 * total) / 2;

			taobaoLabel = new TVFANLabel[total];
			qrCodeImg = new Image[total];
			qrCodeLoder = new QRCodeLoader[total];

			if (total > 1)
				verticalLine = new Image[total - 1];

			for (int i = 0; i < total; i++) {
				JSONObject obj = (JSONObject) data.get(i);
				if (i > 0) {
					verticalLine[i - 1] = new Image(this);
					verticalLine[i - 1].setSize(1.3f, 370);
					verticalLine[i - 1].setPosition(fx + (370 + 200) * i - 100,
							310);
					verticalLine[i - 1].setDrawable(new TextureRegionDrawable(
							findTextureRegion(R.drawable.wxzf_line)));
					verticalLine[i - 1].setAlpha(0.5f);
					addActor(verticalLine[i - 1]);
				}

				qrCodeLoder[0] = new QRCodeLoader(this);
				if (qrCodeLoder[0] != null) {
					qrCodeLoder[0].cancelLoad();
				}
				qrCodeLoder[0]
						.startLoad(obj.optString("image"), 370, 370, this);

				qrCodeImg[i] = new Image(this);
				qrCodeImg[i].setSize(370, 370);
				qrCodeImg[i].setPosition(fx + 200 * i + 370 * i, 310);
				qrCodeImg[i].setDrawable(new TextureRegionDrawable(
						findTextureRegion(R.drawable.qrcode)));

				addActor(qrCodeImg[i]);

				taobaoLabel[i] = new TVFANLabel(this);
				taobaoLabel[i].setPosition(fx + 200 * i + 370 * i, 250);
				taobaoLabel[i].setSize(370, 30);
				taobaoLabel[i].setAlignment(Align.center);
				taobaoLabel[i].setText(obj.optString("name"));
				taobaoLabel[i].setColor(Color.valueOf("ffffff"));
				taobaoLabel[i].setAlpha(0.5f);
				taobaoLabel[i].setTextSize(38);
				addActor(taobaoLabel[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDispose() {
		super.onDispose();
		unregisterLocalMsgReceiver(LOCAL_MSG_FILTER.PAY_RESULT);
	}

	@Override
	public void onLoadQRCodeComplete(String contents, int imageWidth,
			int imageHeight, TextureRegion textureRegion) {
		try {
			JSONArray data = orderResult.optJSONArray("data");
			int total = data.length();
			for (int i = 0; i < total; i++) {
				JSONObject obj = (JSONObject) data.get(i);
				if (obj.optString("image").equals(contents)) {
					qrCodeImg[i].setDrawable(textureRegion);
					return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
