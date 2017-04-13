package tvfan.tv.ui.gdx.liveShow.detail;

import android.content.ContentValues;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.utils.Utils;
import com.luxtone.lib.widget.CullGroup;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.JSONParser;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.StringUtil;
import tvfan.tv.ui.gdx.widgets.Button;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

//import com.umeng.analytics.MobclickAgent;

public class DetailGroup extends Group implements OnFocusChangeListener,
		OnClickListener, LoaderListener {

	private TVFANLabel mTitle; // 电影名称
	private Image mPoster; // 海报
	private Image mTextBg; // 剧情介绍后面背景




	private TVFANLabel introduce;

	private Button buyBtn; // 购买按钮
	private Button playBtn; // 播放按钮
	private Button collectBtn; // 收藏按钮

	private Image buyImg;
	private Image buyBgImg;

	private PageImageLoader posterLoader;

	private boolean is_authorize_finished = false; // 是否完成鉴权
	private boolean is_through_authorize = false; // 是否通过鉴权

	private CullGroup cullGroup;
	private tvfan.tv.ui.gdx.liveShow.detail.Page page;

	private static final String TAG = "DetailGroup";
	private RemoteData rd;
	private JSONObject detailJsonObj;

	private String errorMsg = "正在验证播放权限，请稍候";
	private JSONObject authorizeResult;

	private boolean isFavorite = false; // 收藏状态
	private String programSeriesId;
	private String updateNum;// 更新的集数

	private JSONParser movieDetailParser;

	private LocalData localData;

	private final static String FAVORITE_TABLE = "FAVORITE_TAB"; // 我的收藏记录本地数据库

	private String timeTamp;

	public enum VIDEOSOURCETYPE {
		/**
		 * cinb视频源
		 */
		CIBN,
		/**
		 * youku视频源
		 */
		YOUKU
	}
	
	
	private boolean is_user_exist = true; // 默认用户存在
	public DetailGroup(Page page, String programSeriesId) {
		super(page);
		setSize(1630, 700);	
		this.page = (tvfan.tv.ui.gdx.liveShow.detail.Page) page;

		//测试数据
		/*programSeriesId = "00010000000000000000000000000391";
		AppGlobalVars.getIns().SERVER_URL.put("EPG", "http://114.247.94.15:8080/epg/web/v40");
		AppGlobalVars.getIns().TEMPLATE_ID = "00080000000000000000000000000050";*/
		//测试数据
		
		this.programSeriesId = programSeriesId;

		rd = new RemoteData(page.getActivity());
		localData = new LocalData(page.getActivity());
		timeTamp = System.currentTimeMillis() + "";
		initView();
	}

	@Override
	public void onResume() {
		// onresume要重新鉴权
		startAuthorize();
		isFavorite();
		
		tvfan.tv.lib.Utils.resetImageSource(buyImg, R.drawable.buy_img);

		//tvfan.tv.lib.Utils.resetImageSource(mTextBg, R.drawable.detail_bg);
		//tvfan.tv.lib.Utils.resetImageSource(mPoster, R.drawable.list_mr);
		super.onResume();
	}

	// 视频鉴权
	private void startAuthorize() {
		//AppGlobalVars.getIns().USER_ID    programSeriesId  00010868174044134498
		rd.startAuthorize(AppGlobalVars.getIns().USER_ID, this.programSeriesId,  
				new Listener4JSONObject() {

					@Override
					public void onResponse(JSONObject response) {
						if (page.isDisposed()) {
							return;
						}
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
							System.out.println("[CIBN_AUTHORIZE_RESULT] = "+ response.toString());
							// code为0，代表鉴权完成，影片没有购买，免费播放10分钟，需要购买
							if (authorizeResult.optString("resultCode").equals(
									"23002")) {
								is_user_exist = true;
								is_authorize_finished = true; // 鉴权完成
								is_through_authorize = false; // 鉴权未通过
								// //通知播放器免费播放10分钟
								setBuyLayoutVisible(true);
							}
							// code为22000，代表鉴权通过，免费影片，不需要购买，直接播放
							else if (authorizeResult.optString("resultCode")
									.equals("22000")) {
								is_user_exist = true;
								is_authorize_finished = true; // 鉴权完成
								is_through_authorize = true; // 鉴权通过
								setBuyLayoutVisible(false);
							}
							// code为23006，代表用户在bms不存在，跳往微信绑定
							else if (authorizeResult.optString("resultCode")
									.equals("23006")) {
								is_authorize_finished = true; // 鉴权完成
								is_through_authorize = false; // 鉴权未通过
								setBuyLayoutVisible(true);
								is_user_exist = false; // 用户不存在
							} else {
								is_user_exist = true;
								is_authorize_finished = true; // 鉴权完成
								is_through_authorize = true; // 鉴权通过
								setBuyLayoutVisible(false);
							}
						} catch (Exception e) {
							Lg.e(TAG, e.getMessage());
							e.printStackTrace();
						}
					}

					@Override
					public void onError(String errorMessage) {

					}
				});

	}

	private void setBuyLayoutVisible(boolean visible) {
		
		
		
		buyBtn.setVisible(visible);
		buyImg.setVisible(visible);
		//buyBgImg.setVisible(visible);
		playBtn.setVisible(!visible);
		
		if (visible) {
			collectBtn.setNextFocusLeft("buyBtn");
			buyImg.toFront();
			buyBgImg.toFront();
		} else {
			collectBtn.setNextFocusLeft("playBtn");
		}
		
		Gdx.app.postRunnable(new Runnable(){
			@Override
			public void run() {
				if (is_through_authorize) {
					playBtn.requestFocus();
				} else buyBtn.requestFocus();
			}
			
		});
	}

	private void initView() {
		cullGroup = new CullGroup(page);
		cullGroup.setPosition(0, 0);
		cullGroup.setSize(1630, 500);
		//cullGroup.setCullingArea(new Rectangle(0, 0, 1630, 500));
		addActor(cullGroup);

		createButtons();
		
		callLogInterface();
		
		startAuthorize();

		// 获取详情信息
		rd.getMovieDetail(programSeriesId,"", new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response == null) {
					Lg.e(TAG, "detail page get detail  response is null");
					return;
				}
				try {
					detailJsonObj = response;
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							movieDetailParser = new JSONParser(detailJsonObj);
							createDetailUI(movieDetailParser);
						}
					});
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {

			}
		});

		// 获取收藏状态
		rd.isFavorite(programSeriesId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response == null) {
					Lg.e(TAG,
							"detail page get favorite state response is null");
					return;
				}
				try {
					isFavorite = response.optBoolean("msg");
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							if (isFavorite) {
								collectBtn.getLabel().setText("已收藏");
							} else {
								collectBtn.getLabel().setText("收藏");
							}
						}
					});
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
			}
		});

		
	}
	
	/**
	 * 调用详情页日志接口
	 */
	private void callLogInterface(){
//		String s = AppGlobalVars.getIns().DETAIL_PAGE_STATISTICS_PATH;
//		int b = s.lastIndexOf("首页");
//		s = s.substring(b, s.length());
//		AppGlobalVars.getIns().DETAIL_PAGE_STATISTICS_PATH = s;
		
		//send
	}

	private void createDetailUI(JSONParser movieDetailParser) {
		try {
			page.setBackGroud(movieDetailParser.getString("picurl"));

			String title = movieDetailParser.getString("name");

			// 详情页的title的cullgroup，用来控制label的滑动区域
			CullGroup cullTitle = new CullGroup(page);
			cullTitle.setPosition(0, 550);
			mTitle = new TVFANLabel(page);
			// mTitle.setPosition(0, 550f);

			// 字母和数字的长度要减半
			int l = (int) ((float) StringUtil.countCharNumber(title) / 2
					+ title.length() - StringUtil.countCharNumber(title));

			if (l <= 15) { // 标题最多15个字
				mTitle.setSize(60 * l, 75);
				cullTitle.setSize(60 * l, 75);
				cullTitle.setCullingArea(new Rectangle(0, 0, 60 * l, 75));
			} else {
				cullTitle.setSize(60 * title.length(), 75);
				cullTitle.setCullingArea(new Rectangle(0, 0, 60 * 15, 75));
				mTitle.setSize(60 * 15, 75);
				mTitle.setMarquee(true);
			}
			cullGroup.addActor(cullTitle);
			mTitle.setTextSize(60);
			mTitle.setAlpha(0.7f);
			mTitle.setMaxLine(1);
			// mTitle.setMarquee(true);
			mTitle.setText(title);
			mTitle.setFocusAble(false);
			mTitle.setAlignment(Align.center_Vertical);
			cullTitle.addActor(mTitle);

			collectBtn.setPosition(915, 30);
			cullGroup.addActor(collectBtn);


			updateFavoriteTable();

			mPoster = new Image(page);
			mPoster.setPosition(0, 30);
			mPoster.setSize(665, 500);
			mPoster.setDrawable(findRegion(R.drawable.list_mr));
			mPoster.setFocusAble(false);
			cullGroup.addActor(mPoster);

			if (posterLoader != null) {
				posterLoader.cancelLoad();
			}
			posterLoader = new PageImageLoader(page);
			posterLoader.startLoadBitmapWithSizeByCut(
					detailJsonObj.optString("picurl"), "poster", true,
					AppGlobalConsts.CUTLENGTH, 665, 500, this, "poster");

			
			introduce = new TVFANLabel(page);
			introduce.setPosition(700, 200);
			introduce.setAlignment(Align.top);
			introduce.setSize(900, 300);
			introduce.setMaxLine(5);
			introduce.setFocusAble(false);
			introduce.setTextSize(40);
			introduce.setText(detailJsonObj.optString("information"));
			introduce.setAlpha(0.8f);
			introduce.setSpacingadd(20);
			cullGroup.addActor(introduce);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 更新本地数据库剧集
	 */
	private void updateFavoriteTable() {
		ContentValues values = new ContentValues();
		values.put("latestEpisode", updateNum);
		int r = localData.execUpdate(FAVORITE_TABLE, values,
				"programSeriesId=?", new String[] { programSeriesId });
		System.out.println("FAVORITE_TABLE Update programSeriesId = "
				+ programSeriesId + ",latestEpisode = " + updateNum
				+ ", result = " + r);
	}

	@Override
	public void onLoadComplete(String url, TextureRegion textureRegion,
			Object tag) {
		mPoster.setDrawable(textureRegion);
		if (is_authorize_finished && !is_through_authorize) {
			setBuyLayoutVisible(true);
		}
		
		/*buyBgImg.toFront();
		buyImg.toFront();*/
	}

	private void createButtons() {

		buyImg = new Image(page);
		buyImg.setDrawableResource(R.drawable.buy_img);
		buyImg.setPosition(0, 430);
		//buyImg.setVisible(false);
		buyImg.setSize(100, 100);
		buyImg.setFocusAble(false);
		cullGroup.addActor(buyImg);

		mTextBg = new Image(page);
		mTextBg.setPosition(0, 30);
		mTextBg.setSize(1630, 500);
		mTextBg.setFocusAble(false);
		mTextBg.setAlpha(0.6f);
		//mTextBg.setDrawable(findRegion(R.drawable.detail_bg));
		cullGroup.addActor(mTextBg);

		playBtn = new Button(page, 256, 136);
		playBtn.setPosition(695, 30);
		playBtn.setName("playBtn");
		playBtn.getImage().setDrawableResource(R.drawable.small_btn_unfocus);
		playBtn.setUnFocusBackGround(R.drawable.small_btn_unfocus);
		playBtn.setFocusBackGround(R.drawable.new_foucs);
		playBtn.setFocusAble(true);
		playBtn.getLabel().setText("播放");
		playBtn.getLabel().setTextSize(45);
		playBtn.setButtonFocusScale(AppGlobalConsts.FOCUSSCALE);
		playBtn.setNextFocusRight("collectBtn");
		playBtn.setOnClickListener(this);
		cullGroup.addActor(playBtn);

		collectBtn = new Button(page, 256, 136);
		collectBtn.setPosition(915, 30);
		collectBtn.setName("collectBtn");
		collectBtn.getImage().setDrawableResource(R.drawable.small_btn_unfocus);
		collectBtn.setFocusAble(true);
		collectBtn.getLabel().setText("收藏");
		collectBtn.getLabel().setTextSize(45);
		collectBtn.setUnFocusBackGround(R.drawable.small_btn_unfocus);
		collectBtn.setFocusBackGround(R.drawable.new_foucs);
		collectBtn.setButtonFocusScale(AppGlobalConsts.FOCUSSCALE);
		collectBtn.setOnClickListener(this);
		collectBtn.setNextFocusLeft("playBtn");
		cullGroup.addActor(collectBtn);


		buyBgImg = NinePatchImage.make(page, findTexture(R.drawable.bannerbj),
				new int[] { 10, 10, 10, 10 });
		buyBgImg.setPosition(0, 30);
		buyBgImg.setSize(665, 500);
		//buyBgImg.setVisible(false);
		buyBgImg.setFocusAble(false);
		cullGroup.addActor(buyBgImg);

		buyBtn = new Button(page, 256, 136);
		buyBtn.setPosition(695, 30);
		buyBtn.setName("buyBtn");
		buyBtn.setFocusAble(true);
		buyBtn.setVisible(false);
		buyBtn.getLabel().setText("订购");
		buyBtn.getLabel().setTextSize(45);
		buyBtn.setButtonFocusScale(AppGlobalConsts.FOCUSSCALE);
		buyBtn.getImage().setDrawableResource(R.drawable.buy_btn_unfocus);
		buyBtn.setUnFocusBackGround(R.drawable.buy_btn_unfocus);
		buyBtn.setFocusBackGround(R.drawable.buy_btn_focus);
		buyBtn.setOnClickListener(this);
		buyBtn.setNextFocusRight("collectBtn");
		cullGroup.addActor(buyBtn);
	}

	public Button getPlayBtn() {
		return playBtn;
	}

	public Button getBuyBtn() {
		return buyBtn;
	}
	
	public void setTitle(String text) {
		mTitle.setText(text);
	}


	public CullGroup getCullGroup() {
		return cullGroup;
	}

	@Override
	public void onFocusChanged(Actor actor, boolean hasFocus) {
	}

	private void addFavorite() {
		rd.addFavorite(programSeriesId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (page.isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG, "detail page addFavorite  response is null");
					Utils.showToast("添加收藏失败");
					return;
				}
				try {
					boolean msg = response.optBoolean("msg");
					if (msg) {
						isFavorite = true;
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								Utils.showToast("添加收藏成功");
								collectBtn.getLabel().setText("已收藏");
							}
						});

						// 更新本地数据库,增加添加收藏的记录
						ContentValues values = new ContentValues();
						values.put("programSeriesId", programSeriesId);
						values.put("latestEpisode", updateNum);
						values.put("ts", timeTamp);
						int r = localData.execInsert(FAVORITE_TABLE, null,
								values);
						System.out
								.println("FAVORITE_TABLE Insert programSeriesId = "
										+ programSeriesId + ",result = " + r);
					}
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Utils.showToast("添加收藏失败");
			}
		});
	}

	private void cancelFavorite() {
		rd.cancelFavorite(programSeriesId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (page.isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG, "detail page cancelFavorite  response is null");
					Utils.showToast("添加收藏失败");
					return;
				}
				try {
					boolean msg = response.optBoolean("msg");
					if (msg) {
						isFavorite = false;
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								Utils.showToast("取消收藏成功");
								collectBtn.getLabel().setText("收藏");
							}
						});

						// 更新本地数据库,删除取消收藏的记录
						int r = localData.execDelete(FAVORITE_TABLE,
								"programSeriesId=?",
								new String[] { programSeriesId });
						System.out
								.println("FAVORITE_TABLE delete programSeriesId = "
										+ programSeriesId + ",result = " + r);
					}
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Utils.showToast("添加收藏失败");
			}
		});
	}


	@Override
	public void onClick(Actor actor) {
		if (actor.equals(collectBtn)) {
			if (isFavorite) {
				cancelFavorite();
			} else {
				addFavorite();
			}
		} else if (actor.equals(playBtn)) {
			if (is_authorize_finished) {
				if (movieDetailParser.getArrayListLength("sources") == 0) {
					Utils.showToast("视频源为空，请联系客服!");
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putString("moviedetail", detailJsonObj.toString());
				bundle.putInt("freePlayTime", 0);
				bundle.putInt("currentPlayPosition", -1);
				
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("UID", AppGlobalVars.getIns().USER_ID);
				map.put("PLAYNAME", movieDetailParser.getString("type")+"-"+movieDetailParser.getString("name")); 
				map.put("U_P", AppGlobalVars.getIns().USER_ID + "|" + movieDetailParser.getString("name"));
				MobclickAgent.onEvent(page.getActivity().getApplicationContext(), "event_play", map);
				
				VIDEOSOURCETYPE videoSourceType;
				videoSourceType = getVideoSourceType(movieDetailParser
						.getString("showid"));
				if (videoSourceType == VIDEOSOURCETYPE.CIBN) {
					page.doAction(ACTION_NAME.OPEN_PLAYER, bundle);
				} else if (videoSourceType == VIDEOSOURCETYPE.YOUKU) {
					page.doAction(ACTION_NAME.OPEN_YKEWPLAYER, bundle);
				}
				
				//detail player 
			} else {
				Utils.showToast(errorMsg);
			}
		} else if (actor.equals(buyBtn)) {
			if (is_user_exist) {
				Bundle bundle = new Bundle();
				bundle.putString("programSeriesId", programSeriesId);
				bundle.putString("authorize_result", authorizeResult.toString());
				page.doAction(ACTION_NAME.OPEN_DETAIL_ORDER, bundle);
			} else {
				Utils.showToast("您尚未绑定微信账号，请绑定后订购");
				page.doAction(ACTION_NAME.OPEN_USER_CENTER, new Bundle());
			}
		}
	}
	
	private void isFavorite() {
		rd.isFavorite(programSeriesId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (page.isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG,
							"detail page get favorite state response is null");
					return;
				}
				try {
					isFavorite = response.optBoolean("msg");
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							if (isFavorite) {
								collectBtn.getLabel().setText("已收藏");
							} else {
								collectBtn.getLabel().setText("收藏");
							}
						}
					});
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
			}
		});

	}
	
	
	/**
	 * 获取当前视频的视频源类型，目前判断依据是：若showid为空，则是cibn片源，否则为youku片源
	 * 
	 * @param str
	 * @return
	 */

	private VIDEOSOURCETYPE getVideoSourceType(String str) {
		if (str.isEmpty() || str.equalsIgnoreCase("cibn")) {
			return VIDEOSOURCETYPE.CIBN;
		} else {
			return VIDEOSOURCETYPE.YOUKU;
		}
	}
	
}
