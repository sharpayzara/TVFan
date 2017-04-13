package tvfan.tv.ui.gdx.liveShow.special;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalConsts.LOCAL_MSG_FILTER;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.lib.QRCodeLoader;
import tvfan.tv.lib.QRCodeLoader.QRCodeLoaderListener;
import tvfan.tv.ui.gdx.widgets.Button;
import tvfan.tv.ui.gdx.widgets.ImageGroup;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

public class Page extends BasePage implements QRCodeLoaderListener {

	private Image specialBg;
	private RemoteData rd;
	private String parentCatgId, playTime, liveUrl, bigPic, price;
	private boolean is_authorize_finished = false; // 是否完成鉴权
	private boolean is_through_authorize = false; // 是否通过鉴权
	private JSONObject authorizeResult;
	private Handler mHandler;// 全局handler
	private Label timeCount, payContent, payContent2, payContent3;
	private Button playBtn;

	int time = 0;// 时间差
	private String date;

	private ImageGroup imgbg;

	private Image lineImg;

	private JSONObject orderResult;

	private int fx;
	private TVFANLabel[] taobaoLabel;
	private Image[] qrCodeImg;
	private QRCodeLoader[] qrCodeLoder;
	private boolean is_user_exist = true; // 默认用户存在

	private Group codeGroup;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		parentCatgId = bundle.getString("parentCatgId");
		playTime = bundle.getString("playTime");
		liveUrl = bundle.getString("liveUrl");
		bigPic = bundle.getString("bigPic");
		price = bundle.getString("price");

		if (TextUtils.isEmpty(parentCatgId) || TextUtils.isEmpty(playTime)
				|| TextUtils.isEmpty(liveUrl) || TextUtils.isEmpty(bigPic)
				|| TextUtils.isEmpty(price)) {
			if (bundle.containsKey("id"))
				parentCatgId = bundle.getString("id");
			initLiveShowParams(parentCatgId);
		} else {
			doNext();
		}
	}

	private void doNext() {
		Looper mainLooper = Looper.getMainLooper();
		mHandler = new Handler(mainLooper);
		initDate();

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
						Utils.showToast("您已付费成功!");
						// 刷新出播放按钮
						// ..
						codeGroup.setVisible(false);
						startAuthorize();
						// 刷新出播放按钮
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, LOCAL_MSG_FILTER.PAY_RESULT);
	}

	private void initLiveShowParams(final String id) {
		rd = new RemoteData(this.getActivity());
		rd.getLiveShowList(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response == null) {
					Lg.e(TAG, "getLiveShowList response is null: ");
					return;
				}
				if (response.has("ItemList")) {
					try {
						JSONArray items = response.getJSONArray("ItemList");
						for (int i = 0; i < items.length(); i++) {
							JSONObject obj = (JSONObject) items.get(i);
							if (obj.getString("id").equals(id)) {
								playTime = obj.getString("playTime");
								liveUrl = obj.getString("liveUrl");
								bigPic = obj.getString("bigPic");
								price = obj.getString("price");
								break;
							}
						}
						doNext();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}

			@Override
			public void onError(String httpStatusCode) {
				Lg.e(TAG, "getLiveShowList : " + httpStatusCode);
				NetWorkUtils.handlerError(httpStatusCode, Page.this);
			}
		});
	}

	private void initDate() {
		imgbg = new ImageGroup(this, AppGlobalConsts.APP_WIDTH,
				AppGlobalConsts.APP_HEIGHT, 0, 0);
		imgbg.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		imgbg.setFocusAble(false);
		addActor(imgbg);
		imgbg.load(bigPic, "", imgbg.getImage());

		lineImg = new Image(this);
		playBtn = new Button(this, 350, 150);
		payContent = new Label(this);
		payContent3 = new Label(this);
		timeCount = new Label(this);

		codeGroup = new Group(this);
		// startAuthorize();
	}

	@Override
	public void onResume() {
		startAuthorize();
		super.onResume();
	}

	// 视频鉴权
	private void startAuthorize() {
		rd = new RemoteData(this.getActivity());
		rd.startAuthorize(AppGlobalVars.getIns().USER_ID, parentCatgId,
				new Listener4JSONObject() {

					@Override
					public void onResponse(JSONObject response) {
						if (response == null) {
							Lg.e(TAG,
									"detail page get authorize  response is null");
							return;
						}

						try {
							authorizeResult = response;
							Lg.i(TAG,
									"[CIBN_AUTHORIZE_RESULT] = "
											+ response.toString());
							System.out.println("[CIBN_AUTHORIZE_RESULT] = "
									+ response.toString());
							// code为0，代表鉴权完成，影片没有购买，免费播放10分钟，需要购买
							if (authorizeResult.optString("resultCode").equals(
									"23002")) {
								is_user_exist = true;
								is_authorize_finished = true; // 鉴权完成
								is_through_authorize = false; // 鉴权未通过

							}
							// code为22000，代表鉴权通过，免费影片，不需要购买，直接播放
							else if (authorizeResult.optString("resultCode")
									.equals("22000")) {
								is_user_exist = true;
								is_authorize_finished = true; // 鉴权完成
								is_through_authorize = true; // 鉴权通过
							}

							else if (authorizeResult.optString("resultCode")
									.equals("23006")) {
								is_authorize_finished = true; // 鉴权完成
								is_through_authorize = false; // 鉴权未通过
								is_user_exist = false; // 用户不存在

								Utils.showToast("您尚未绑定微信账号，请绑定后订购");

								Gdx.app.postRunnable(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										doAction(ACTION_NAME.OPEN_USER_CENTER,
												new Bundle());
										finish();
									}
								});

							} else {
								is_user_exist = true;
								is_authorize_finished = true; // 鉴权完成
								is_through_authorize = true; // 鉴权通过
							}
							/*
							 * //暂时写死 if (price.equals("580")) { playTime =
							 * "2015-06-01 19:00:00"; } //暂时写死 END
							 */setLayout(is_through_authorize);

						} catch (Exception e) {
							Utils.showToast("鉴权异常");
							Lg.e(TAG, e.getMessage());
							e.printStackTrace();
						}
					}

					@Override
					public void onError(String errorMessage) {

					}
				});

	}

	private void setLayout(final boolean is_through_authorize) {

		date = playTime;
		time = getTimeInterval(date);// 获取时间差

		payContent.setVisible(false);
		payContent3.setVisible(false);
		timeCount.setVisible(false);
		playBtn.setVisible(false);

		if (is_through_authorize) {
			// 鉴权通过
			if (time <= 0) {
				// 到期显示正在直播，显示播放按钮 0
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						// 画线
						lineImg.setDrawableResource(R.drawable.juji_line);
						lineImg.setSize(900, 4);
						lineImg.setAlign(Align.center_Horizontal);
						lineImg.setPosition(880, 580);
						lineImg.setAlign(Align.center_Horizontal);
						addActor(lineImg);

						playBtn.setVisible(true);
						playBtn.setPosition(862, 250);
						playBtn.getImage().setDrawableResource(
								R.drawable.new_foucs);
						playBtn.setUnFocusBackGround(R.drawable.new_foucs);
						playBtn.setFocusBackGround(R.drawable.new_foucs);
						playBtn.setFocusAble(true);
						playBtn.getLabel().setText("播放");
						playBtn.getLabel().setTextSize(45);
						playBtn.getLabel().setColor(Color.WHITE);
						// playBtn.getLabel().setAlpha(0.5f);
						addActor(playBtn);
						playBtn.requestFocus();
						playBtn.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(Actor paramActor) {
								Bundle bundle = new Bundle();

								bundle.putString("liveUrl", liveUrl);
								doAction(ACTION_NAME.OPEN_LIVESHOWPLAYER,
										bundle);
							}
						});

						payContent.setVisible(true);
						payContent.setSize(500, 50);
						payContent.setPosition(880, 500);
						payContent.setColor(Color.valueOf("ffffffff"));
						payContent.setText("恭喜您，您已经成功订购");
						payContent.setTextSize(45);
						addActor(payContent);

						timeCount.setVisible(true);
						timeCount.setSize(800, 50);
						timeCount.setPosition(880, 420);
						timeCount.setColor(Color.valueOf("04E6FE"));
						timeCount.setText("正在直播中...");
						timeCount.setTextSize(45);
						addActor(timeCount);
					}
				});

			} else {
				// 未到期 显示 倒计时 ，播放按钮灰色 1
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						// 画线
						lineImg.setDrawableResource(R.drawable.juji_line);
						lineImg.setSize(900, 4);
						lineImg.setAlign(Align.center_Horizontal);
						lineImg.setPosition(880, 580);
						lineImg.setAlign(Align.center_Horizontal);
						addActor(lineImg);

						playBtn.setVisible(true);
						playBtn.setPosition(862, 250);
						playBtn.getImage().setDrawableResource(
								R.drawable.paybtn);
						playBtn.setUnFocusBackGround(R.drawable.paybtn);
						playBtn.setFocusBackGround(R.drawable.paybtn);
						playBtn.setFocusAble(false);
						playBtn.getLabel().setText("播放");
						playBtn.getLabel().setTextSize(45);
						playBtn.getLabel().setColor(Color.WHITE);
						playBtn.getLabel().setAlpha(0.5f);
						addActor(playBtn);

						payContent.setVisible(true);
						payContent.setSize(500, 50);
						payContent.setPosition(880, 500);
						payContent.setColor(Color.valueOf("ffffffff"));
						payContent.setText("恭喜您，您已经成功订购");
						payContent.setTextSize(45);
						addActor(payContent);

						timeCount.setVisible(true);
						timeCount.setSize(800, 50);
						timeCount.setPosition(880, 420);
						timeCount.setColor(Color.valueOf("ffffff"));
						timeCount.setTextSize(45);
						addActor(timeCount);
					}
				});

				new Thread(new TimeCount()).start();// 开启线程
			}

		} else {
			// 鉴权不通过
			if (time <= 0) {
				// 到期显示正在直播中，显示二维码 2
				payContent.setVisible(true);
				payContent.setSize(500, 50);
				payContent.setPosition(880, 540);
				payContent.setColor(Color.valueOf("DDDDDB"));
				payContent.setText("票价:");
				payContent.setTextSize(45);
				payContent.setAlignment(Align.left);
				addActor(payContent);

				payContent3.setVisible(true);
				payContent3.setSize(500, 50);
				payContent3.setPosition(1000, 540);
				payContent3.setColor(Color.valueOf("FFEB00"));
				payContent3.setText("￥" + price);
				payContent3.setTextSize(48);
				payContent3.setAlignment(Align.left);
				addActor(payContent3);

				timeCount.setVisible(true);
				timeCount.setSize(800, 50);
				timeCount.setPosition(880, 470);
				timeCount.setColor(Color.valueOf("04E6FE"));
				timeCount.setText("正在直播中...");
				timeCount.setTextSize(45);
				addActor(timeCount);
				// 给购买接口传两个值 authorizeResult,programSeriesId
				updateQrCodes(AppGlobalVars.getIns().USER_ID, parentCatgId);

			} else {
				// 未到期显示二维码 3
				payContent.setVisible(true);
				payContent.setSize(500, 50);
				payContent.setPosition(880, 540);
				payContent.setColor(Color.valueOf("DDDDDB"));
				payContent.setText("票价:");
				payContent.setTextSize(45);
				payContent.setAlignment(Align.left);
				addActor(payContent);

				payContent3.setVisible(true);
				payContent3.setSize(500, 50);
				payContent3.setPosition(1000, 540);
				payContent3.setColor(Color.valueOf("FFEB00"));
				payContent3.setText("￥" + price);
				payContent3.setTextSize(48);
				payContent3.setAlignment(Align.left);
				addActor(payContent3);
				// 给购买接口传两个值 authorizeResult,programSeriesId
				updateQrCodes(AppGlobalVars.getIns().USER_ID, parentCatgId);

			}
		}

	}

	private void updateQrCodes(String uid, String programSeriesId) {
		// 测试附值：
		/*
		 * uid = "UN2015033100001023"; programSeriesId =
		 * "0001086860652283614012"; AppGlobalVars.getIns().DEVICE_ID = "";
		 */
		// 测试附值：END

		JSONObject obj = null;
		try {
			if (authorizeResult.opt("products") instanceof JSONArray) {
				obj = (JSONObject) authorizeResult.optJSONArray("products")
						.get(0);
			} else if (authorizeResult.opt("products") instanceof JSONObject) {
				obj = authorizeResult.optJSONObject("products");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		rd.getDetailOrder(uid, programSeriesId, "", "",
				obj.optString("productCode"), AppGlobalVars.getIns().DEVICE_ID,
				new Listener4JSONObject() {

					@Override
					public void onResponse(JSONObject response) {
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
										showQrCodes();
										return;
									}
									// 已经购买，勿重复购买
									else {
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
			fx = (1920 - (total - 1) * 200 - 290 * total) / 2 + 310;

			taobaoLabel = new TVFANLabel[total];
			qrCodeImg = new Image[total];
			qrCodeLoder = new QRCodeLoader[total];

			for (int i = 0; i < total; i++) {
				JSONObject obj = (JSONObject) data.get(i);

				qrCodeLoder[0] = new QRCodeLoader(this);
				if (qrCodeLoder[0] != null) {
					qrCodeLoder[0].cancelLoad();
				}
				qrCodeLoder[0]
						.startLoad(obj.optString("image"), 320, 320, this);

				qrCodeImg[i] = new Image(this);
				qrCodeImg[i].setSize(290, 290);
				qrCodeImg[i].setPosition(fx + 50 * i + 290 * i, 310 - 160);
				qrCodeImg[i].setDrawable(new TextureRegionDrawable(
						findTextureRegion(R.drawable.qrcode)));

				codeGroup.addActor(qrCodeImg[i]);

				taobaoLabel[i] = new TVFANLabel(this);
				taobaoLabel[i].setPosition(fx + 50 * i + 290 * i, 260 - 160);
				taobaoLabel[i].setSize(290, 30);
				taobaoLabel[i].setAlignment(Align.center);
				taobaoLabel[i].setText(obj.optString("name"));
				taobaoLabel[i].setColor(Color.valueOf("DDDDDB"));
				// taobaoLabel[i].setAlpha(0.9f);
				taobaoLabel[i].setTextSize(37);
				codeGroup.addActor(taobaoLabel[i]);

				codeGroup.setPosition(0, 0);
				codeGroup.setSize(AppGlobalConsts.APP_WIDTH,
						AppGlobalConsts.APP_HEIGHT);
				codeGroup.setVisible(true);
				addActor(codeGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class TimeCount implements Runnable {
		@Override
		public void run() {
			while (time > 0)// 整个倒计时执行的循环
			{
				time--;
				mHandler.post(new Runnable() // 通过它在UI主线程中修改显示的剩余时间
				{
					public void run() {

						updateTime(time);

					}

				});
				try {
					Thread.sleep(1000);// 线程休眠一秒钟 这个就是倒计时的间隔时间
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// 下面是倒计时结束逻辑
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					startAuthorize();
				}
			});
		}
	}

	private void updateTime(final int time) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				timeCount.setText(getInterval(time));// 显示剩余时间
			}
		});

	}

	/**
	 * 获取两个日期的时间差
	 */
	public static int getTimeInterval(String data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		int interval = 0;
		try {
			Date currentTime = new Date();// 获取现在的时间
			Date beginTime = dateFormat.parse(data);
			interval = (int) ((beginTime.getTime() - currentTime.getTime()) / (1000));// 时间差
																						// 单位秒
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return interval;
	}

	/**
	 * 设定显示文字
	 */
	public static String getInterval(int time) {
		String txt = null;
		if (time >= 0) {
			long day = time / (24 * 3600);// 天
			// long hour = time % (24 * 3600) / 3600;// 小时
			long hour = time % (24 * 3600) / 3600 + day * 24;// 小时
			long minute = time % 3600 / 60;// 分钟
			long second = time % 60;// 秒
			String dayS = String.format("%2d", day);
			String hourS = String.format("%2d", hour);
			String minuteS = String.format("%2d", minute);
			String secondS = String.format("%2d", second);

			txt = "距离开始还有 " + hourS + " 小时 " + minuteS + " 分 " + secondS + " 秒";
		} else {
			txt = "已过期";
		}
		return txt;
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

	@Override
	public void onDispose() {
		super.onDispose();
		unregisterLocalMsgReceiver(LOCAL_MSG_FILTER.PAY_RESULT);
	}

}
