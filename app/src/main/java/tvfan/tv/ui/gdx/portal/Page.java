package tvfan.tv.ui.gdx.portal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.Timer;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.FocusFinder;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;
import com.tvata.p2p.P2PManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.MsgHelper;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.MsgList;
import tvfan.tv.dal.models.PortalFreshEvent;
import tvfan.tv.dal.models.PortalMsgUpdateEvent;
import tvfan.tv.lib.ACache;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.PortalLayoutParser.LayoutElement;
import tvfan.tv.lib.StringUtil;
import tvfan.tv.ui.gdx.widgets.ArrowMarker;
import tvfan.tv.ui.gdx.widgets.ArrowMarker.Arrow;
import tvfan.tv.ui.gdx.widgets.HorizontalIconList;
import tvfan.tv.ui.gdx.widgets.HorizontalIconList.IconBuilder;
import tvfan.tv.ui.gdx.widgets.HorizontalIconListItem;
import tvfan.tv.ui.gdx.widgets.InternetErrorDialog;
import tvfan.tv.ui.gdx.widgets.InternetErrorDialog.ConfirmDialogListener;
import tvfan.tv.ui.gdx.widgets.NavBar;
import tvfan.tv.ui.gdx.widgets.NavBarItem;
import tvfan.tv.ui.gdx.widgets.PortalPageTopBar;
import tvfan.tv.ui.gdx.widgets.PortalVeritcalViewPager;
import tvfan.tv.ui.gdx.widgets.PortalVeritcalViewPager.VeritcalViewPagerListener;
import tvfan.tv.ui.gdx.widgets.StatusBar;
import tvfan.tv.ui.gdx.widgets.TipsDialog;

//import com.umeng.analytics.MobclickAgent;

public class Page extends BasePage implements OnClickListener,
		OnFocusChangeListener {

	private String TAG = "TVFAN.EPG.gdx.portal";
	private PortalVeritcalViewPager _pagesContainer;
	private Page _pageCtx;
	private ArrayList<String> _menuIdList = new ArrayList<String>();
	private ArrayList<String> _menuList = new ArrayList<String>();//封装首页侧边栏的
	private StatusBar _statBar;
	private NavBar _navBar;
	private ArrowMarker _arrowMarker;
	private int _navItemLength = 4;
	private final int _marginLeft = 0;
	private Group _seeker;
	private Image _bgimg = null;
	private int _bgresid = -1;
	private HorizontalIconListItem ucicon = null;
	private int _curPage = 0;
	private Label _mq = null;
	private CullGroup _noticeHolder = null;
	private boolean _guideIsShow = false;
	private Image _guideImg = null;
	private Image _guideImgBG = null;
	private int _defItem = 0;
	//_defItem 值为1时,定位在推荐;2时定位在片库;0/3时定位在用户;4时定位在直播,5是设置
	private int _autoScrollTo = -1;
	private Timer _timer = null;
	private LocalMsgListener wifiMsgListener;
	private int netType;
	private ACache mCache;
	private SnapshotArray<Actor> HIGroups;
	private Handler mHandler;

	private final static long FRESH_TIME = 30 * 60 * 1000l;// 半个小时向后台获取刷新的时间戳

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		App.addPage(this);
		mCache = ACache.get(getActivity());
		mHandler = new Handler(getActivity().getMainLooper());
		EventBus.getDefault().register(this);
		_pageCtx = this;
		String di = LocalData
				.sGetKV(AppGlobalConsts.PERSIST_NAMES.PORTAL_DEFAULT_ITEM
						.name());


		if(LocalData.sGetKV(AppGlobalConsts.PERSIST_NAMES.VIDEO_DEFAULT_DEFINITION
				.name()) == null)
			LocalData.sSetKV(AppGlobalConsts.PERSIST_NAMES.VIDEO_DEFAULT_DEFINITION
					.name(), "2");
		if (!TextUtils.isEmpty(di))
			_defItem = Integer.parseInt(di);
		_seeker = new Group(this);
		addActor(_seeker);
		_loadBG();
		//_loadArrow();
		_createNoticeLabel();

		_pagesContainer = new PortalVeritcalViewPager(this);
		_pagesContainer.setSize(AppGlobalConsts.APP_WIDTH,
				AppGlobalConsts.APP_HEIGHT);
		_pagesContainer.setPosition(0, 0);
		_pagesContainer.setZIndex(10);
		_pagesContainer.setOnPageChanged(new VeritcalViewPagerListener() {
			@Override
			public void focusChanged(Group actor) {
				_navBar.setSelected(Integer.parseInt(actor.getName()));
				_curPage = Integer.valueOf(actor.getName());
				//_setArrow();
			}
		});
		addActor(_pagesContainer);

		_loadLogo();

		_loadNavBar();

		_loadTopBar();
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		_regLocalMsg();
		if (msgItem != null) {
			msgItem.onResume();
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		_unregLocalMsg();
		wifiMsgListener = null;
		super.onPause();
	}

	@Override
	public void onFocusChanged(Actor arg0, boolean arg1) {
	}

	private Runnable freshRunnable = new Runnable() {

		@Override
		public void run() {
			RemoteData rd = new RemoteData(getActivity());
			rd.getLastUpdateTime(new Listener4JSONObject() {

				@Override
				public void onResponse(JSONObject response) {
					Lg.i(TAG, "轮询获取后台时间戳.");
				}

				@Override
				public void onError(String httpStatusCode) {
					Lg.i(TAG, "轮询获取后台时间戳.");
				}
			});

		}
	};

	public void onEventMainThread(PortalFreshEvent event) {
		String cibnTampMills = event.getCibnTampMills();
		mHandler.removeCallbacks(freshRunnable);
		if (cibnTampMills != null && StringUtil.isNumeric(cibnTampMills)) {
			long ct = Long.valueOf(cibnTampMills);
			if (ct > App.CIBNTAMPMILLS) {
				// Utils.showToast("cibnTampMills=" + cibnTampMills
				// + " , currentTampMills=" + App.CIBNTAMPMILLS);
				// mCache.put("XCibnReceivedMillis", cibnTampMills);
				if (App.CIBNTAMPMILLS != 0) {
					Lg.i(TAG, "后台通知刷新首页");
					refreshPortalData();
				}
				App.CIBNTAMPMILLS = ct;
			}
		}
		// 重启刷新定时器
		mHandler.postDelayed(freshRunnable, FRESH_TIME);
	}

	// add by wanqi,刷新首页消息中心的显示数量
	public void onEventMainThread(PortalMsgUpdateEvent event) {
		int msg = event.getMsg();
		if (msg < 0)
			msg = 0;
		mCache.put("MESSAGENUMBER", msg + "");
		final int m = msg;
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				if (msgItem != null) {
					msgItem.updateMsgTip(m);
				}
				if (mineButton != null) {
					mineButton.updateMsgTip(m);
				}
				if (m > 0)
					_navBar.showReddot(true);
				else
					_navBar.showReddot(false);
			}
		});

	}

	@Override
	public void onDispose() {
		super.onDispose();
		EventBus.getDefault().unregister(this);
		_unregLocalMsg();
		App.removePage(this);
	}

	@Override
	public void recyclePage() {
		super.recyclePage();
	}

	private boolean _backclked = false;

	@Override
	public boolean onBackKeyDown() {
		if (_guideIsShow) {
			return true;
		}
		if (dialog == null) {
			showDialog();
			return true;
		}
		if (dialog.isShown()) {
			dialog.dismiss();
			dialog = null;
		} else {
			showDialog();
			//dialog.show();
		}
		return true;

		// Lg.i(TAG, "EXIT EPG");
		// if (!_backclked) {
		// Utils.showToast("再按“返回”退出");
		// _backclked = true;
		// return true;
		// }
		// return super.onBackKeyDown();
	}

	TipsDialog dialog = null;

	private void showDialog() {
		TipsDialog.Builder builder = new TipsDialog.Builder(Page.this,
				getActivity());
		builder.setMessage("您打算退出电视粉么？")
				.setPositiveButton("再看一会", new OnClickListener() {

					@Override
					public void onClick(Actor arg0) {
						dialog.dismiss();
					}
				}).setNegativeButton("退出观看", new OnClickListener() {

			@Override
			public void onClick(Actor arg0) {
				if(P2PManager.get()!=null){
					P2PManager.get().stop();
				}
				Log.d("P2P","P2P has stopped");
				tvfan.tv.ui.gdx.portal.Page.this.finish();
				System.exit(0);
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onClick(Actor arg0) {

	}

	private void _setStatusBar(boolean isConnected, int netType) {
		if(_statBar != null)
			_statBar.setWifi(isConnected, netType);
	}

	private void _setStatusBar(long now) {
		if (_statBar != null) {
			_statBar.setDatetime(now);
		}
	}

	private void _setStatusBarLevel(int level) {
		if (_statBar != null) {
			_statBar.setWifiLevel(level);
		}

	}

	/**
	 * 加载背景图片
	 */
	private void _loadBG() {
		if (_bgimg != null) {
			_bgimg.dispose();
			_bgimg = null;
		}

		_bgimg = new Image(this);
		_bgimg.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		_bgimg.setPosition(0, 0);

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				addActor(_bgimg);
				_bgimg.toBack();
				_bgimg.addAction(Actions.fadeOut(0));

				LocalData ld = new LocalData(getActivity());
				String bg = ld
						.getKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE
								.name());

				if (bg == null)
//					_bgresid = R.drawable.bja;
					_bgresid = R.drawable.other_background;
				else
					_bgresid = Integer.parseInt(bg);

				_bgimg.setDrawableResource(_bgresid);
				_bgimg.addAction(Actions.fadeIn(0.6f));
			}
		});
	}

	PortalPageTopBar searchButton;
	PortalPageTopBar mineButton;
	PortalPageTopBar settingButton;

	/**
	 * 添加logo
	 */
	private void _loadLogo() {
		Image logo = new Image(_pageCtx);
		logo.setSize(155, 57);
		logo.setPosition(1710, 945);
//		logo.setDrawableResource(R.drawable.portal_logo);
		logo.setDrawableResource(R.drawable.logo);
		addActor(logo);
	}

	private void _loadTopBar() {
		_statBar = new StatusBar(this);
		_statBar.setPosition(1535, 945);
		_statBar.setSize(150, 32);
		addActor(_statBar);

		// 添加搜索，我的，设置等
		searchButton = new PortalPageTopBar(this, R.drawable.home_search_normal, R.drawable.home_search_selected, "搜索");
		searchButton.setPosition(75, 945);
		searchButton.setSize(180, 60);
		searchButton.setFocusAble(true);
		addActor(searchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor actor) {
				doAction(ACTION_NAME.valueOf(ACTION_NAME.class, "OPEN_SEARCH"), null);
			}
		});

		mineButton = new PortalPageTopBar(this, R.drawable.home_user_normal, R.drawable.home_user_selected, "我的");
		mineButton.setPosition(255, 945);
		mineButton.setSize(180, 60);
		mineButton.setFocusAble(true);
		mineButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor actor) {
				doAction(ACTION_NAME.valueOf(ACTION_NAME.class, "OPEN_USER_CENTER"), null);
			}
		});
		addActor(mineButton);
		requestMessageNumber();

		settingButton = new PortalPageTopBar(this, R.drawable.home_setting_normal, R.drawable.home_setting_selected, "设置");
		settingButton.setPosition(1365, 945);
		settingButton.setSize(180, 60);
		settingButton.setFocusAble(true);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor actor) {
				doAction(ACTION_NAME.valueOf(ACTION_NAME.class, "OPEN_USER_SETTING"), null);
			}
		});
		addActor(settingButton);
	}

	private void _loadGuide() {
		String st = LocalData
				.sGetKV(AppGlobalConsts.PERSIST_NAMES.GUIDE_SHOW_TIMES.name());
		int t = 1;
		if (!TextUtils.isEmpty(st)) {
			t = Integer.valueOf(st);
			if (t > 2)
				return;
			t++;
		}
		LocalData.sSetKV(AppGlobalConsts.PERSIST_NAMES.GUIDE_SHOW_TIMES.name(),
				String.valueOf(t));

		_guideImgBG = new Image(this);
		_guideImgBG.setSize(AppGlobalConsts.APP_WIDTH,
				AppGlobalConsts.APP_HEIGHT);
		_guideImgBG.setPosition(0.0f, 0.0f);
		_guideImgBG.setDrawableResource(R.drawable.background_shadow);
		_guideImgBG.setZIndex(999);
		//addActor(_guideImgBG);

		_guideImg = new Image(this);
		_guideImg
				.setSize(AppGlobalConsts.APP_WIDTH,
						AppGlobalConsts.APP_HEIGHT);
		_guideImg.setPosition(0.0f, 0.0f);
		_guideImg.setDrawableResource(R.mipmap.portalguide);
		_guideImg.setZIndex(1000);
		_guideImg.setAlpha(0.85f);
		addActor(_guideImg);
		_guideIsShow = true;
	}

	/**
	 * 加载下侧导航
	 */
	private void _loadNavBar() {
		_timerStart();

		String layoutfile = "电视粉";
		// wanqi，加载本地数据
		layoutfile = mCache.getAsString("epg_portal_navBar");
//		int curVersion = 0;
//		try {
//			curVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
//		} catch (Exception e) {
//
//		}

		if (layoutfile == null || checkPortalCache()) { // 若没有本地缓存，就访问网络获取数据
			Lg.i(TAG, "epg_portal_navBar , 首页栏目数据缓存不存在");
			loadInternetData();
		} else
			try {
				loadDefaultLayout(new JSONObject(layoutfile));
				refreshPortalData();
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 检测是否存在首页的缓存
	 * @return
	 */
	private boolean checkPortalCache() {
		Lg.i(TAG, "check portal cache .");
		String s = mCache.getAsString("epg_portal_ids");
		if (s == null)
			return true;
		s = s.replace(" ", "");
		String[] strs = s.split(",");
		for (String str : strs) {
			if (TextUtils.isEmpty(str))
				continue;
			s = mCache.getAsString("epg_index_" + str);
			if (TextUtils.isEmpty(s)) {
				Lg.i(TAG, "epg_index_" + str + "  ,  此项分类内容缓存不存在.");
				return true;
			}
		}
		return false;
	}

	/**
	 * 初始的时候获取首页的数据
	 */
	private void loadInternetData() {
		RemoteData rd = new RemoteData(getActivity());
		rd.getPortalNav(new HttpResponse.Listener4JSONObject() {
			@Override
			public void onResponse(final JSONObject response) {
				_timerStop();
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						try {
							JSONArray ml = response.getJSONArray("data");
							mCache.put("epg_portal_navBar", response.toString());
							if(ml != null)
								AppGlobalVars.getIns().TMP_VARS.put(
										"PORTAL_NAV_DATA", ml);

							_navItemLength = ml.length() - 1;
							for (int i = 0; i < ml.length(); i++) {
								String d = ml.getJSONObject(i).getString("id");
								String navname = ml.getJSONObject(i).getString(
										"name");
								_menuIdList.add(d);
								_menuList.add(navname);
								if (_defItem == Integer.parseInt(d))
									_autoScrollTo = i;
							}
							_navBar = new NavBar(_pageCtx, ml);
							_navBar.setPosition((AppGlobalConsts.APP_WIDTH - 240 * ml
									.length()) / 2, 45);
							_navBar.setSize(240 * ml.length(), 200);
							_navBar.setOrientation(Grid.ORIENTATION_VERTICAL);
							_navBar.setGapLength(0);
							_navBar.setZIndex(90);
							// _navBar.setFocus(0);
							_navBar.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

								@Override
								public void onSelectedChanged(int paramInt,
															  Actor paramActor) {
									_navBar.setSelected(paramInt);
									_pagesContainer.turnPage(paramInt);
									_curPage = paramInt;
									//_setArrow();
								}
							});
							_pageCtx.addActor(_navBar);
							// Gdx.app.postRunnable(new Runnable(){
							// @Override
							// public void run() {
							// _pageCtx.addActor(_navBar);
							// }});
							_loadData();
							_loadGuide();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onError(final String errorMessage) {
				// Utils.showToast("_loadNavBar: " + errorMessage);
				Lg.e(TAG, "_loadNavBar: " + errorMessage);
				handleInternetError();
			}
		});
	}

	private void loadDefaultLayout(JSONObject response) {
		try {
			JSONArray ml = response.getJSONArray("data");
			if(ml != null)
				AppGlobalVars.getIns().TMP_VARS.put("PORTAL_NAV_DATA", ml);
			_navItemLength = ml.length() - 1;
			for (int i = 0; i < ml.length(); i++) {
				String d = ml.getJSONObject(i).getString("id");
				String name = ml.getJSONObject(i).getString("name");
				_menuIdList.add(d);
				_menuList.add(name);
				if (_defItem == Integer.parseInt(d))
					_autoScrollTo = i;
			}
			_navBar = new NavBar(_pageCtx, ml);
			_navBar.setPosition((AppGlobalConsts.APP_WIDTH - 240 * ml
					.length()) / 2, 45);
			_navBar.setSize(240 * ml.length(), 200);
			_navBar.setOrientation(Grid.ORIENTATION_VERTICAL);
			_navBar.setGapLength(0);
			_navBar.setZIndex(90);
			// _navBar.setFocus(0);
			_navBar.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

				@Override
				public void onSelectedChanged(int paramInt, Actor paramActor) {
					_navBar.setSelected(paramInt);
					_pagesContainer.turnPage(paramInt);
					_curPage = paramInt;
					//_setArrow();
				}
			});
			_pageCtx.addActor(_navBar);
			// Gdx.app.postRunnable(new Runnable(){
			// @Override
			// public void run() {
			// _pageCtx.addActor(_navBar);
			// }});
			// _loadData();
			loadData();
			_loadGuide();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private String jsonMap = "";

	// wanqi，加载本地列表数据
	private void loadData() {
		String mid = _menuIdList.get(0);
		String navname = _menuList.get(0);
		try {
			String s = mCache.getAsString("epg_index_" + mid);
			JSONObject response = new JSONObject(s);
			JSONArray items;

			items = response.getJSONArray("itemList");
			if (items != null)
				createHIList(response.getString("layoutFile"), items, mid,
						navname);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleInternetError() {
		final String msg = Page.this.getActivity().getResources()
				.getString(R.string.portal_internet_error);
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				new InternetErrorDialog(Page.this, msg,
						new ConfirmDialogListener() {

							@Override
							public void onOK(Dialog dialog) {
								MobclickAgent.onKillProcess(getActivity());
								Page.this.finish();
								Page.this.getActivity().finish();
								System.exit(0);
							}

							@Override
							public void onCancel(Dialog dialog) {
								dialog.dismiss();
								_loadNavBar();

							}
						}).show();
			}
		});
	}

	private void refreshPortalData() {
		RemoteData rd = new RemoteData(getActivity());
		rd.getPortalNav(new HttpResponse.Listener4JSONObject() {
			@Override
			public void onResponse(final JSONObject response) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						try {
							Lg.i(TAG, "refresh Portal data");
							mCache.put("epg_portal_navBar", response.toString());
							JSONArray ml = response.getJSONArray("data");
							if(ml != null)
								AppGlobalVars.getIns().TMP_VARS.put(
										"PORTAL_NAV_DATA", ml);
							_navItemLength = ml.length() - 1;

							HIGroups = _pagesContainer.getChildren();
							for (int i = 0; i < ml.length(); i++) {
								String d = ml.getJSONObject(i).getString("id");
								refreshHIList(d);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onError(final String errorMessage) {
				Lg.e(TAG, "_loadNavBar: " + errorMessage);
			}
		});
	}

	private void refreshHIList(final String mid) {
		RemoteData rd = new RemoteData(getActivity());
		rd.getPortalItem(mid, new HttpResponse.Listener4JSONObject() {
			@Override
			public void onResponse(final JSONObject response) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						try {
							Lg.i(TAG, "refresh HIList");
							jsonMap = jsonMap + mid + ",";
							mCache.put("epg_portal_ids",
									jsonMap.substring(0, jsonMap.length() - 1));
							mCache.put("epg_index_" + mid, response.toString());
							String layouturl = response.optString("layoutFile");
							loadAndCachelayout(layouturl, mid);
							JSONArray items = response.getJSONArray("itemList");
							if (HIGroups != null)
								for (Actor a : HIGroups) {
									if (a.getTag().toString()
											.equalsIgnoreCase("HIL_" + mid)) {
										Group g = (Group) a;
										for (Actor ac : g.getChildren()) {
											if (ac != null
													&& ac instanceof HorizontalIconList) {
												refreshHIItems(
														(HorizontalIconList) ac,
														items, mid);
												break;
											}
										}
										break;
									}
								}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onError(String errorMessage) {
				Lg.e(TAG, "_loadData: " + errorMessage);
			}
		});
	}

	private void refreshHIItems(HorizontalIconList hil, JSONArray array,
								String mid) {
		Lg.i(TAG, "refresh HIItems");
		SnapshotArray<Actor> gs = hil.getChildren();
		for (int i = 0; i < gs.size; i++) {
			try {
				HorizontalIconListItem item = (HorizontalIconListItem) gs
						.get(i);
				int j = (Integer) item.getTag();
				if (j > array.length())
					continue;
				JSONObject dataListItem = array.getJSONObject(j);
				String id = dataListItem.getString("id").toString();
				String name = dataListItem.getString("name").toString();
				String image = dataListItem.getString("image").toString();
				String action = dataListItem.getString("action").toString();
				String descInfo = "";
				if (dataListItem.has("descInfo")) {
					descInfo = dataListItem.getString("descInfo").toString();
				}
				String title = dataListItem.getString("subName");
				JSONObject actionParam = dataListItem
						.getJSONObject("actionParam");
				String ap = actionParam.put("name", name).put("image", image)
						.toString();
				if (!item.getParam("id").equals(id)
						|| !item.getParam("action").equals(action)
						|| !item.getParam("actionParam").equals(ap)
						|| !item.hasDrawable()) {
					item.putParam("id", id);
					item.putParam("action", action);
					item.putParam("actionParam", ap);
					item.putParam("subName", title);
					item.putParam("descInfo", descInfo);

					if (title.isEmpty()) {
						title = dataListItem.getString("name");
					}
					if (j == 0
							&& dataListItem.getString("action").equals(
							ACTION_NAME.OPEN_USER_CENTER.name())) {
						item.addIcon(AppGlobalVars.getIns().USER_PIC);
						if (!AppGlobalVars.getIns().USER_PIC.startsWith("@")) {
							item.showMask(true);
							item.showHeadimg(AppGlobalVars.getIns().USER_PIC,
									true);
						}
						item.keepShowTitle();
						ucicon = item;
						title = AppGlobalVars.getIns().USER_NICK_NAME;
					} else {
						item.addIcon(image);
					}
					item.setTitle(title);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void loadAndCachelayout(String layoutFileUrl, final String mid) {
		RemoteData rd = new RemoteData(getActivity());
		rd.startJsonHttpGet(layoutFileUrl,
				new HttpResponse.Listener4JSONObject() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response == null) {
								Lg.e(TAG, "load layout File failed");
								return;
							}
							mCache.put("epg_layoutFile_" + mid,
									response.toString()); // 将布局文件放入到缓存

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(String errorMessage) {
					}
				});
	}

	/**
	 * 加载滑动时显示的箭头
	 */
	private void _loadArrow() {
		_arrowMarker = new ArrowMarker(this, new ArrowMarker.Arrow[] {
				Arrow.top, Arrow.down }, new int[] { 0, 880, 0, 80 });
		_arrowMarker.setPosition(930, 0);
		_arrowMarker.setSize(100, 1080);
		_arrowMarker.setVisible(Arrow.top, false);
		_arrowMarker.setZIndex(5);
		addActor(_arrowMarker);
	}

	/**
	 * 加载顶部显示推送的label
	 */
	private void _createNoticeLabel() {
		_noticeHolder = new CullGroup(this);
		_noticeHolder.setPosition(560, 930);
		_noticeHolder.setSize(900, 65);
		_noticeHolder.setCullingArea(new Rectangle(0, 0, 900, 65));
		_noticeHolder.setVisible(false);
		// wanqi,删除首页跑马灯背景，4.1需求
		// Image img = new Image(this);
		// img.setDrawableResource(R.drawable.noticebj);
		// img.setSize(1000, 65);
		_mq = new Label(this);
		_mq.setSize(880, 40);
		_mq.setPosition(10, 15f);
		_mq.setTextSize(30);
		_mq.setColor(Color.WHITE);
		_mq.setAlpha(.8f);
		_mq.setMarquee(true);
		// _noticeHolder.addActor(img);
		_noticeHolder.addActor(_mq);
		addActor(_noticeHolder);

		String text = mCache.getAsString("epg_notice_text");
		String endtime = mCache.getAsString("epg_notice_endtime");
		if (text != null && endtime != null) {
			_noticeHolder.setVisible(true);
			_mq.setText(text);
			if (StringUtil.isNumeric(endtime)) {
				long end = Long.valueOf(endtime) - System.currentTimeMillis();
				if (end <= 0) { // 消息过期就不要再显示了
					mCache.remove("epg_notice_text");
					mCache.remove("epg_notice_endtime");
				} else {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							_noticeHolder.setVisible(false);
							_mq.setText(null);
						}
					}, end);
				}
			}
		}

	}

	private long noticeDuration = 60;// 60分钟

	// wanqi,跑马灯4.1新需求
	@SuppressLint("SimpleDateFormat")
	private void _showNotice(String text, String starttime, long duration) {
		if (duration > 0)
			noticeDuration = duration;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long endtime = 0l;
		long ct = System.currentTimeMillis();
		try {
			Date date = format.parse(starttime);
			if (date != null)
				endtime = date.getTime() + noticeDuration * 60 * 1000;
			else
				endtime = ct + noticeDuration * 60 * 1000;
		} catch (ParseException e) {
			endtime = ct + noticeDuration * 60 * 1000;
			e.printStackTrace();
		}
		if (endtime > ct) { // 消息过期就不要再显示了
			_noticeHolder.setVisible(true);
			_mq.setText(text);
			mCache.put("epg_notice_text", text);
			mCache.put("epg_notice_endtime", endtime + "");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					_noticeHolder.setVisible(false);
					_mq.setText(null);
				}
			}, endtime - ct);
		}
	}

	/**
	 * 从网络获取首页列表数据，并加载
	 */
	private void _loadData() {
		final String mid = _menuIdList.get(0);
		final String mnavname = _menuList.get(0);
		_timerStart();

		RemoteData rd = new RemoteData(getActivity());
		rd.getPortalItem(mid, new HttpResponse.Listener4JSONObject() {
			@Override
			public void onResponse(final JSONObject response) {
				_timerStop();
				try {
					jsonMap = jsonMap + mid + ",";
					mCache.put("epg_portal_ids",
							jsonMap.substring(0, jsonMap.length()));
					mCache.put("epg_index_" + mid, response.toString());
					String layouturl = response.optString("layoutFile");
					loadAndCachelayout(layouturl, mid);
					JSONArray items = response.getJSONArray("itemList");
					if (items != null) {
						_createHIList(response.getString("layoutFile"), items,
								mid, mnavname);

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				// Gdx.app.postRunnable(new Runnable(){
				// @Override
				// public void run() {
				// try {
				// JSONArray items = response.getJSONArray("itemList");
				// if(items != null)
				// _createHIList(response.getString("layoutFile"), items);
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				// }});
			}

			@Override
			public void onError(String errorMessage) {
				Lg.e(TAG, "_loadData: " + errorMessage);
			}
		});
	}

	private void createHIList(String layoutFile, JSONArray items,
							  final String id, final String navname) {
		HorizontalIconList hil = new HorizontalIconList(this, layoutFile,
				items, new IconBuilder() {
			@Override
			public void Create(final LayoutElement layoutElement,
							   final JSONObject dataListItem,
							   final int layoutElementIndex,
							   final HorizontalIconList horizontalIconList) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						_createIcon(layoutElement, dataListItem,
								layoutElementIndex, horizontalIconList,
								id, navname);
					}
				});
			}
		}, id);
		hil.setPosition(_marginLeft, (AppGlobalConsts.APP_HEIGHT - 620) / 2);
		hil.setSize((AppGlobalConsts.APP_WIDTH - _marginLeft), 620);
		Group g = new Group(_pageCtx);
		g.setTag("HIL_" + id);
		g.addActor(hil);



		_pagesContainer.addSection(g);
		_menuIdList.remove(0);
		_menuList.remove(0);
		if (_menuIdList.isEmpty()) {
			_pagesContainer.commit();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							_curPage = _autoScrollTo;
							if (_curPage == -1)
								_curPage = 0;
							_pagesContainer.turnPage(_curPage);
							_pagesContainer.setFocus(_curPage);
						}
					});

				}
			}, 500);
		} else {
			loadData();
		}
	}

	/**
	 * 创建首页详情界面
	 * @param layoutFile
	 * @param items
	 * @param i
	 * @param navname
	 */
	private void _createHIList(String layoutFile, JSONArray items,
							   final String i, final String navname) {
		HorizontalIconList hil = new HorizontalIconList(this, layoutFile,
				items, new IconBuilder() {
			@Override
			public void Create(final LayoutElement layoutElement,
							   final JSONObject dataListItem,
							   final int layoutElementIndex,
							   final HorizontalIconList horizontalIconList) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						_createIcon(layoutElement, dataListItem,
								layoutElementIndex, horizontalIconList,
								i, navname);
					}
				});
			}
		});
		hil.setPosition(_marginLeft, (AppGlobalConsts.APP_HEIGHT - 620) / 2);
		hil.setSize((AppGlobalConsts.APP_WIDTH - _marginLeft), 620);
		Group g = new Group(_pageCtx);
		g.setTag("HIL_" + i);
		g.addActor(hil);

		_pagesContainer.addSection(g);
		_menuIdList.remove(0);
		_menuList.remove(0);
		if (_menuIdList.isEmpty()) {
			_pagesContainer.commit();
			(new Handler()).postDelayed(new Runnable() {
				@Override
				public void run() {
					_curPage = _autoScrollTo;
					if (_curPage == -1)
						_curPage = 0;
					_pagesContainer.turnPage(_curPage);
					_pagesContainer.setFocus(_curPage);
				}
			}, 500);
		} else {
			_loadData();
		}
	}

	private void _timerStart() {
		// _timerStop();
		// _timer = new Timer();
		// _timer.scheduleTask(new Task(){
		// @Override
		// public void run() {
		// HttpRequestWrapper.cancelAll();
		// ConfirmDialog cd = new ConfirmDialog(_pageCtx,
		// "页面加载异常，请重试",
		// new ConfirmDialogListener() {
		// @Override
		// public void onOK() {
		// _restart();
		// }
		//
		// @Override
		// public void onCancel() {
		// _restart();
		// }
		// });
		// cd.show();
		// Lg.i(TAG, "Timeout dialog show.");
		// }}, 9);
	}

	private void _timerStop() {
		if (_timer != null) {
			_timer.stop();
			_timer.clear();
			_timer = null;
		}
	}

	public boolean onKeyDown(int keycode) {
		// Lg.i(TAG, "onKeyDown:" + String.valueOf(keycode));

		if (_backclked && Keys.BACK != keycode)
			_backclked = false;

		if (_guideIsShow) {
			_guideIsShow = false;
			_guideImg.remove();
			_guideImg.dispose();
			_guideImg = null;

			_guideImgBG.remove();
			_guideImgBG.dispose();
			_guideImgBG = null;
			return true;
		}
		Actor c = _seeker.getFocusContainer().getFocusActor();
		if (c == null)
			return true;

		Group g = c.getParent();

		if (Keys.UP == keycode
				&& FocusFinder.findNextActorInGroup(c, FocusFinder.UP, g) == null) {
			if (c instanceof NavBarItem) {
				_navBar.setItemFocus(false);
//				_navBar.setFocusImg(R.drawable.focusline);
				_navBar.setCursorVisible(true);
				_pagesContainer.setFocus(_curPage);
			}
			if (c instanceof HorizontalIconListItem) {
				searchButton.requestFocus();
			}
			return true;
		} else if (Keys.DOWN == keycode
				&& FocusFinder.findNextActorInGroup(c, FocusFinder.DOWN, g) == null) {
			if (c instanceof HorizontalIconListItem) {
				_navBar.setItemFocus(true);
				// _navBar.setFocusImg(R.drawable.foucsline);
				_navBar.setCursorVisible(false);
				_navBar.setSelection(_curPage, true);
			}
			if (c instanceof PortalPageTopBar) {
				_pagesContainer.setFocus(_curPage);
			}
			return true;
		} else if (Keys.LEFT == keycode
				&& FocusFinder.findNextActorInGroup(c, FocusFinder.LEFT, g) == null) {
			if (c instanceof NavBarItem)
				return true;
		} else if (Keys.RIGHT == keycode
				&& FocusFinder.findNextActorInGroup(c, FocusFinder.RIGHT, g) == null) {
			if (c instanceof NavBarItem)
				return true;
		}

		if (Keys.DOWN == keycode && c instanceof PortalPageTopBar) {
			_pagesContainer.setFocus(_curPage);
			return true;
		}

		if (Keys.LEFT == keycode && c instanceof PortalPageTopBar && ((PortalPageTopBar)c).getName().equals("搜索")) {
			return true;
		}

		if (Keys.RIGHT == keycode && c instanceof PortalPageTopBar && ((PortalPageTopBar)c).getName().equals("设置")) {
			return true;
		}

		return super.onKeyDown(keycode);
	}



	@SuppressLint("NewApi")
	private void _createIcon(LayoutElement layoutElement,
							 JSONObject dataListItem, int layoutElementIndex,
							 HorizontalIconList horizontalIconList, String i,
							 final String navname) {
		HorizontalIconListItem item = new HorizontalIconListItem(this,
				new int[] { layoutElement.width, layoutElement.height },
				new HorizontalIconListItem.IconListItemListener() {
					@Override
					public void focusChanged(HorizontalIconListItem selectedItem) {
						// _setSelectedFocus(selectedItem);
					}
				});
		item.setPosition(layoutElement.x, layoutElement.y);
		item.setFocusAble(true);

//		item.setFocusScale(.1f);
		try {
			item.putParam("layoutElementIndex",
					String.valueOf(layoutElementIndex));
			item.putParam("id", dataListItem.getString("id").toString());
			item.putParam("action", dataListItem.getString("action").toString());
			item.putParam("subName", dataListItem.getString("subName")
					.toString());
			if (dataListItem.has("descInfo")) {
				item.putParam("descInfo", dataListItem.getString("descInfo")
						.toString());
			}
			item.putParam(
					"actionParam",
					dataListItem
							.getJSONObject("actionParam")
							.put("name",
									dataListItem.getString("name").toString())
							.put("image",
									dataListItem.getString("image").toString())
							.toString());

			item.putParam("LMindex", i);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		//首页item的点击事件
		item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor arg0) {
				// String[] ps =
				// arg0.getName().split(",");//layoutElementIndex,id,action,actionParam
				HorizontalIconListItem item = (HorizontalIconListItem) arg0;
				int pos = Integer.parseInt(item.getParam("layoutElementIndex")) +1;
				String program = navname + "-" + pos + "-"
						+ item.getParam("subName") + "-"
						+ item.getParam("descInfo");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("UID", AppGlobalVars.getIns().USER_ID);
				map.put("PROGRAM_SERIES_ID", program);
				map.put("ACTION_NAME", item.getParam("action"));
				map.put("U_P_A",
						AppGlobalVars.getIns().USER_ID + "|"
								+ item.getParam("id") + "|"
								+ item.getParam("action"));
				// wanqi,修复友盟上报的空指针bug
				if (item.getParam("LMindex") == null) {
					// 当为null不执行任何操作
				} else if ("1".equals(item.getParam("LMindex"))) {// 推荐
					MobclickAgent.onEvent(
							getActivity().getApplicationContext(),
							"event_index1_tuijian_click", map);

				} else if ("4".equals(item.getParam("LMindex"))) {// 轮播
					MobclickAgent.onEvent(
							getActivity().getApplicationContext(),
							"event_carousel_zhibo_click", map);

				} else if ("3".equals(item.getParam("LMindex"))) {// 游戏
//					MobclickAgent.onEvent(
//							getActivity().getApplicationContext(),
//							"event_game_click", map);
				}
				MobclickAgent.onEvent(getActivity().getApplicationContext(),
						"event_index_click", map);

				Bundle options = new Bundle();
				options.putString("id", item.getParam("id"));
				options.putString("action", item.getParam("action"));
				//记录具体的跳转详情
				String actionParam = item.getParam("actionParam");
				Log.e("actionParam", "actionParam===="+actionParam);
				options.putString("actionParam", item.getParam("actionParam"));

				// doAction(
				// ACTION_NAME.valueOf(ACTION_NAME.class,
				// item.getParam("action")), options);
				// wanqi,test,0831
				// if (!item.getParam("action").equalsIgnoreCase(
				// ACTION_NAME.OPEN_USER_CENTER.name()))
				// doAction(ACTION_NAME.OPEN_LIVE_DETAIL, options);
				// else

				doAction(
						ACTION_NAME.valueOf(ACTION_NAME.class,
								item.getParam("action")), options);

			}

		});
		try {
			String title = dataListItem.getString("subName");
			if (title.isEmpty()) {
				title = dataListItem.getString("name");
			}
			if (layoutElementIndex == 0
					&& dataListItem.getString("action").equals(
					ACTION_NAME.OPEN_USER_CENTER.name())) {
				item.addIcon(AppGlobalVars.getIns().USER_PIC);
				if (!AppGlobalVars.getIns().USER_PIC.startsWith("@")) {
					item.showMask(true);
					item.showHeadimg(AppGlobalVars.getIns().USER_PIC, true);
				}
				item.keepShowTitle();
				ucicon = item;
				title = AppGlobalVars.getIns().USER_NICK_NAME;
			} else {
				item.addIcon(dataListItem.getString("image"));
			}
			item.setTitle(title);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		item.setTag(layoutElementIndex);
		/*-------begin---------add by wanqi,首页消息中心添加消息条数提示----------------------*/
		if (item.getParam("action").equalsIgnoreCase(
				ACTION_NAME.OPEN_MESSAGE_LIST.name())) {
			msgItem = item;
			requestMessageNumber();
			if (App.MESSAGENUMBER > 0) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						msgItem.updateMsgTip(App.MESSAGENUMBER);
						_navBar.showReddot(true);
					}
				});
			}
		}
		/*-------end---------add by wanqi,首页消息中心添加消息条数提示----------------------*/
		horizontalIconList.addActor(item);
	}

	private HorizontalIconListItem msgItem = null;

	/**
	 * 设置箭头的显示情况
	 */
	private void _setArrow() {
		if (_curPage == 0) {
			_arrowMarker.setVisible(Arrow.top, false);
		} else if (_curPage == _navItemLength) {
			_arrowMarker.setVisible(Arrow.down, false);
		} else {
			_arrowMarker.setVisible(Arrow.top, true);
			_arrowMarker.setVisible(Arrow.down, true);
		}
	}

	private void _unregLocalMsg() {
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.NET_CHANGED);
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.DATETIME_UPDATE);
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.BACKGROUND_CHANGE);
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.USER_IMAGE_CHANGE);
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.NOTICE_DISPLAY);
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.NEW_MSG_ARRIVED);
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.RSSI_CHANGED);
	}

	private void postLocalIpToServer() {
		RemoteData rd = new RemoteData();
		rd.updateTerminalIP(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response == null) {
					Lg.d(TAG, "updateTerminalIP response is null");
					return;
				}

				if (response.optString("resultCode", "").endsWith("0")) {
					Lg.i(TAG, "post local ip success");
				} else {
					Lg.i(TAG, "post local ip failed");
				}
			}

			@Override
			public void onError(String httpStatusCode) {
				Lg.e(TAG, httpStatusCode);
			}
		});
	}

	private void _regLocalMsg() {
		registerLocalMsgReceiver(new LocalMsgListener() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean isconnected = intent.getBooleanExtra("isConnected",
						true);
				netType = intent.getIntExtra("netType",
						ConnectivityManager.TYPE_ETHERNET);
				_setStatusBar(isconnected, netType);

				if (netType == ConnectivityManager.TYPE_WIFI
						&& wifiMsgListener == null) {
					wifiMsgListener = new LocalMsgListener() {
						@Override
						public void onReceive(Context context, Intent intent) {
							context.removeStickyBroadcast(intent);
							// WiFi信号改变
							_setStatusBarLevel(intent.getIntExtra("level", -100));
						}
					};
					// WiFi信号强度监听
					registerLocalMsgReceiver(wifiMsgListener,
							AppGlobalConsts.LOCAL_MSG_FILTER.RSSI_CHANGED);
				}

				// add by wanqi,网络重新连接时都上报局域网ip，用于手机遥控器
				if (isconnected
						&& !TextUtils.isEmpty(AppGlobalVars.USER_TOKEN)
						&& !TextUtils.isEmpty(tvfan.tv.lib.Utils
						.getLocalIpAddress()))
					postLocalIpToServer();

			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.NET_CHANGED);

		registerLocalMsgReceiver(new LocalMsgListener() {
			@Override
			public void onReceive(Context context, Intent intent) {
				_setStatusBar(intent.getLongExtra("now", 0));
			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.DATETIME_UPDATE);

		registerLocalMsgReceiver(new LocalMsgListener() {
			@Override
			public void onReceive(Context context, Intent intent) {
				context.removeStickyBroadcast(intent);
				LocalData ld = new LocalData(getActivity());
				String bg = ld.getKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE
						.name());
				if (bg != null && Integer.valueOf(bg) == _bgresid)
					return;

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						_loadBG();
					}
				}, 500);
			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.BACKGROUND_CHANGE);

		registerLocalMsgReceiver(new LocalMsgListener() {
			@SuppressLint("NewApi")
			@Override
			public void onReceive(Context context, Intent intent) {
				context.removeStickyBroadcast(intent);
				if (TextUtils.isEmpty(AppGlobalVars.getIns().USER_PIC)) {
					AppGlobalVars.getIns().USER_PIC = "@"
							+ String.valueOf(R.drawable.usercentericon);
				}
				if (ucicon != null) {
					if (AppGlobalVars.getIns().USER_PIC.startsWith("@")) {
						ucicon.showMask(false);
						ucicon.showHeadimg(null, false);
						ucicon.addIcon(AppGlobalVars.getIns().USER_PIC);

					} else {
						ucicon.showMask(true);
						ucicon.showHeadimg(AppGlobalVars.getIns().USER_PIC,
								true);
					}
					ucicon.setTitle(AppGlobalVars.getIns().USER_NICK_NAME);
				}
			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.USER_IMAGE_CHANGE);

		registerLocalMsgReceiver(new LocalMsgListener() {
			@SuppressLint("NewApi")
			@Override
			public void onReceive(Context context, Intent intent) {
				context.removeStickyBroadcast(intent);
				try {
					JSONObject j = new JSONObject(
							intent.getStringExtra(AppGlobalConsts.INTENT_MSG_PARAM));
//					_showNotice(j.getJSONObject("body").getString("title"), j
//							.getJSONObject("head").getString("starttime"), Long
//							.valueOf(j.getJSONObject("head").getString(
//									"duration")));

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.NOTICE_DISPLAY);

		registerLocalMsgReceiver(new LocalMsgListener() {
			@SuppressLint("NewApi")
			@Override
			public void onReceive(Context context, Intent intent) {
				context.removeStickyBroadcast(intent);
				requestMessageNumber();
			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.NEW_MSG_ARRIVED);
	}

	private MsgHelper msgHelper;
	String[] arrMsgInfo = null;

	/**
	 * 获取消息数量,wanqi
	 */
	public void requestMessageNumber() {
		if (msgHelper == null)
			msgHelper = new MsgHelper(getActivity());
		arrMsgInfo = msgHelper.queryAllMsg();
		RemoteData rd = new RemoteData();
		rd.getMsgList(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				Lg.e("response", "getMsgList" + response);
				try {
					JSONArray jsonarr = response.getJSONArray("data");
					int total = 0;
					for (int i = jsonarr.length() - 1; i >= 0; i--) {
						MsgList msg = new MsgList();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						msg.setMsgId(jsonobject.optString("msgId", ""));
						if (arrMsgInfo != null && arrMsgInfo.length > 0) {
							for (int j = 0; j < arrMsgInfo.length; j++) {
								if (arrMsgInfo[j].equals(msg.getMsgId())) {
									msg.setMark(1);
								}
							}
						}
						if (msg.getMark() == 0) {
							total++;
						}
					}
					App.MESSAGENUMBER = total;
					mCache.put("MESSAGENUMBER", total + "");
					if (mineButton != null && total > 0) {
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
//								msgItem.updateMsgTip(App.MESSAGENUMBER);
//								if (mineButton != null) {
								mineButton.updateMsgTip(App.MESSAGENUMBER);
//								}
//								_navBar.showReddot(true);
							}
						});

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
			}
		});

	}
}
