package tvfan.tv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.luxtone.lib.gdx.FocusFinder;
import com.luxtone.lib.gdx.IMemoryManager;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import tvfan.tv.lib.AppLauncher;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.programDetail.dialog.PersonPage;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

//import com.umeng.analytics.MobclickAgent;

public class BasePage extends Page {
	private IntentFilter _intentFilter = null;
	private HashMap<AppGlobalConsts.LOCAL_MSG_FILTER, BroadcastReceiver> _broadcastReceivers = new HashMap<AppGlobalConsts.LOCAL_MSG_FILTER, BroadcastReceiver>();
	private String _pageName = "首页";
	private String _pageAction = null;
	public static ArrayList<String> _path = new ArrayList<String>();
	private boolean _isReleased = false;
	public boolean isRight = false;

	/**
	 * 本地消息监听
	 **/
	public interface LocalMsgListener {
		/**
		 * 收到消息
		 **/
		public void onReceive(Context context, Intent intent);
	}

	// private Label mFps;

	public enum ACTION_NAME {
		/**
		 * @param OPEN_PORTAL
		 *            打开首页
		 * @param OPTIONS
		 *            (String)catalogId
		 **/
		OPEN_PORTAL,
		/**
		 * @param OPEN_PROGRAM_LIST
		 *            打开节目列表
		 * @param OPTIONS
		 *            (String)catalogId
		 **/
		OPEN_PROGRAM_LIST,
		/**
		 * @param OPEN_DETAIL
		 *            打开节目详情
		 * @param OPTIONS
		 *            (String)catalogId, (String)contentId
		 **/
		OPEN_DETAIL,
		/**
		 * @param OPEN_PLAYER
		 *            打开优酷播放页
		 **/

		OPEN_YKEWPLAYER,
		/**
		 * @param OPEN_PLAYER
		 *            打开轮播播放页
		 **/
		OPEN_LIVEPLAYER,
		/**
		 * @param OPEN_PLAYER
		 *            打开LIVESHOW播放页
		 **/

		OPEN_LIVESHOWPLAYER,
		/**
		 * @param OPEN_PLAYER
		 *            打开播放页
		 **/

		OPEN_PLAYER,
		/**
		 * @param OPEN_USER_CENTER
		 *            打开用户中心
		 **/
		OPEN_USER_CENTER,
		/**
		 * @param OPEN_HISTORY
		 *            打开播放记录
		 **/
		OPEN_HISTORY,
		/**
		 * @param OPEN_FAVOR
		 *            打开收藏
		 **/
		OPEN_FAVOR,
		/**
		 * @param OPEN_SEARCH
		 *            打开搜索
		 **/
		OPEN_SEARCH,
		/**
		 * @param OPEN_YOUR_LIKE
		 *            打开猜你喜欢
		 **/
		OPEN_YOUR_LIKE,
		/**
		 * @param OPEN_HOT_WORDS
		 *            打开热词推荐
		 **/
		OPEN_HOT_WORDS,
		/**
		 * @param OPEN_LIVE_SHOW
		 *            打开LiveShow
		 **/
		OPEN_LIVE_SHOW,
		/**
		 * @param OPEN_LIVE_SHOW_LIST
		 *            打开LiveShowList
		 **/
		OPEN_LIVE_SHOW_LIST,
		/**
		 * @param OPEN_LIVE_SHOW_DETAIL
		 *            打开LiveShowDetail
		 **/
		OPEN_LIVE_SHOW_DETAIL,
		/**
		 * @param OPEN_LIVE_SHOW_PAY
		 *            打开LiveShowPay
		 **/
		OPEN_LIVE_SHOW_PAY,
		/**
		 * @param OPEN_HOT_PROGRAM_LIST
		 *            打开特殊节目列表
		 **/
		OPEN_HOT_PROGRAM_LIST,
		/**
		 * @param OPEN_NEWS_INDEX
		 *            打开新闻首页
		 **/
		OPEN_NEWS_INDEX,
		/**
		 * @param OPEN_NEWS_DETAIL
		 *            打开新闻详情
		 **/
		OPEN_NEWS_DETAIL,
		/**
		 * @param OPEN_NEWS_PROGRAM_LIST
		 *            打开新闻隐藏二级栏目
		 **/
		OPEN_NEWS_PROGRAM_LIST,
		/**
		 * @param OPEN_BRAND
		 *            打开品牌专区页面
		 **/
		OPEN_BRAND_INDEX,
		/**
		 * @param OPEN_BRAND_DETAIL
		 *            打开单个品牌
		 **/
		OPEN_BRAND_DETAIL,

		/**
		 * @param OPEN_DETAIL_ORDER
		 *            打开购买页面
		 **/
		OPEN_DETAIL_ORDER,
		/**
		 * @param OPEN_SPECIAL_INDEX
		 *            打开专题汇总页面
		 **/
		OPEN_SPECIAL_INDEX,
		/**
		 * @param OPEN_SPECIAL_TEMPLATE
		 *            打开专题模板
		 **/
		OPEN_SPECIAL_TEMPLATE,
		/**
		 * @param OPEN_NEWS_SPECIAL_INDEX
		 *            打开新闻专题汇总页面
		 **/
		OPEN_NEWS_SPECIAL_INDEX,
		/**
		 * @param OPEN_NEWS_VERCITAL_SPECIAL_INDEX
		 *            打开竖版新闻专题汇总页面
		 **/
		OPEN_NEWS_VERCITAL_SPECIAL_INDEX,

		/**
		 * @param OPEN_TOP_PROGRAM
		 *            打开排行榜
		 **/
		OPEN_TOP_PROGRAM,
		/**
		 * @param LAUNCH_APP
		 *            启动APP
		 **/
		LAUNCH_APP,
		/**
		 * @param OPEN_MSG_DETAIL
		 *            启动消息详情
		 **/
		OPEN_MSG_DETAIL,
		/**
		 * @param OPEN_ABOUT_US
		 *            关于我们
		 **/
		OPEN_ABOUT_US,
		/**
		 * @param OPEN_BG_IMG_MGR
		 *            更换背景
		 **/
		OPEN_BG_IMG_MGR,
		/**
		 * @param OPEN_SETTINGS
		 *            常规设置
		 **/
		OPEN_SETTINGS,
		/**
		 * @param OPEN_ORDERS_LIST
		 *            我的订购
		 **/
		OPEN_ORDERS_LIST,
		/**
		 * @param OPEN_MESSAGE_LIST
		 *            消息中心
		 **/
		OPEN_MESSAGE_LIST,
		/**
		 * @param OPEN_PERFORMER
		 *            打开相关人物
		 **/
		OPEN_PERFORMER,
		/**
		 * @param OPEN_PERFORMER
		 *            打开追剧
		 **/
		OPEN_BINGE_WATCHING,
		/**
		 * @param OPEN_NEWS_TEMPLATE
		 *            打开新闻专题模板(4.1新需求)
		 **/
		OPEN_NEWS_TEMPLATE,
		/**
		 * @param OPEN_BRAND_PORTAL
		 *            打开聚合专区首页
		 **/
		OPEN_GROUP_PORTAL,
		/**
		 * @param OPEN_LIVE_DETAIL
		 *            打开直播详情
		 **/
		OPEN_LIVE_DETAIL,
		/**
		 * @param OPEN_TEST
		 *            打开测试页
		 **/
		OPEN_TEST,
		/**
		 * @param OPEN_USER_SETTING
		 * 			  打开用户设置
		 */
		OPEN_USER_SETTING
	};

	@Override
	public void onCreate(Bundle bundle) {
		String bv = bundle.getString("__pageName");
		if (bv != null) {
			this._pageName = bv;
			_path.add(bv);
		}

		bv = bundle.getString("action");
		if (bv != null) {
			this._pageAction = bv;
		}
		// Gdx.graphics.setContinuousRendering(true);
		// mFps = Widgets.lable(this, "FPS", Color.WHITE);
		// mFps.setVisible(true);
		// mFps.setSize(200, 50);
		// mFps.setAlignment(Align.right);
		// mFps.setPosition(ScreenAdapterUtil.getWidth() - 200,
		// ScreenAdapterUtil.getHeight() - 50);
		// mFps.toFront();
		// this.addActor(mFps);
		// ACTION_NAME i = ACTION_NAME.OPEN_PORTAL;
		
		mHomeKeyEventBroadCastReceiver = new HomeKeyEventBroadCastReceiver();
		getActivity().registerReceiver(mHomeKeyEventBroadCastReceiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}
	private HomeKeyEventBroadCastReceiver mHomeKeyEventBroadCastReceiver;
	/**
	 * 监听Home键事件的广播接收器
	 * 进行退出
	 * @author ddd
	 *
	 */
	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						// home key处理点
						App.removeAllPages();
						System.exit(0);
						android.os.Process.killProcess(android.os.Process.myPid());
					} 
				}
			}
		}
	}

	@Override
	public void recyclePage() {
		if (!(this instanceof tvfan.tv.ui.gdx.portal.Page))
			releaseMemory();
		super.recyclePage();
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onDispose() {
		super.onDispose();
// getActivity().unregisterReceiver(mHomeKeyEventBroadCastReceiver);
	}

	@Override
	public boolean onKeyDown(int keycode) {
		return super.onKeyDown(keycode);
	}

	@Override
	public boolean onBackKeyDown() {
		int pi = _path.size() - 1;
		if (pi > -1 && this._pageName.equals((String) _path.get(pi)))
			_path.remove(pi);

		if (this instanceof tvfan.tv.ui.gdx.programDetail.Page) {
			removeAll("详情页");
		}
		if (this instanceof tvfan.tv.ui.gdx.programDetail.dialog.PersonPage) {
			removeAll("相关人物");
		}

		/*
		 * if(this._pageAction != null) _logWrite(this._pageAction,
		 * AppGlobalConsts.LOG_CMD.UMENG_PAGE_END);
		 */

		return super.onBackKeyDown();
	}

	// 删除_path中的所有str ，比如 : 详情页,相关人物
	private void removeAll(String str) {
		HashSet<String> h = new HashSet<String>(_path);
		_path.clear();
		_path.addAll(h);
		_path.remove(str);
	}

	@Override
	public void onNewCreate(Bundle bundle) {
		super.onNewCreate(bundle);
	}

	@Override
	public void onAndoridNewIntent(final Intent intent) {
		super.onAndoridNewIntent(intent);
		Gdx.app.postRunnable(intentRunnable = new Runnable() {
			@Override
			public void run() {
				String actName = intent
						.getStringExtra(AppGlobalConsts.INTENT_ACTION_NAME);
				if (actName != null) {
					Bundle b = new Bundle();
					if (intent.getExtras() != null)
						b.putAll(intent.getExtras());
					doAction(ACTION_NAME.valueOf(ACTION_NAME.class, actName), b);
				} else {
					gotoPage(intent);
				}
			}
		});
	}

	private void gotoPage(Intent intent) {
		final String action = intent.getStringExtra("action");
		String actionParam = intent.getStringExtra("actionParam");
		JSONObject actParamObj;
		String id = "";
		try {
			actParamObj = new JSONObject(actionParam);
			id = actParamObj.getString("id");
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		Bundle options = new Bundle();
		options.putString("id", id);
		options.putString("action", action);
		options.putString("actionParam", actionParam);
		options.putBoolean("isHub", true);
		doAction(ACTION_NAME.valueOf(ACTION_NAME.class, action), options);
	}

	Runnable intentRunnable;

	@Override
	public void onResume() {
		_logWrite(_pageAction, AppGlobalConsts.LOG_CMD.UMENG_PAGE_START);
		if (_isReleased) {
			rebuildAll();
			onResumeTextures();
		}
		_isReleased = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		Lg.i("BasePage", _pageAction + " onpause");
		if (!isPause())
			_logWrite(_pageAction, AppGlobalConsts.LOG_CMD.UMENG_PAGE_END);
		if (intentRunnable != null)
			Gdx.app.removeRunnable(intentRunnable);
		super.onPause();
	}

	/**
	 * 当页面回收后，此方法在onResume()时被调用，所有派生类需要重载
	 * **/
	protected void onResumeTextures() {
	}

	@Override
	public Action onFinishPageInScreenAnimation() {
		Action act1 = Actions.alpha(1f, .3f);
		Action act2 = Actions.scaleTo(1f, 1f, .3f);
		return Actions.parallel(act1, act2);
	}

	@Override
	public Action onFinishPageOutScreenAnimation() {
		// Action act1 = Actions.scaleTo(2.5f, 2.5f, .2f);
		Action act1 = Actions.scaleTo(.1f, .1f, .2f);
		Action act2 = Actions.alpha(.2f, .2f);
		return Actions.parallel(act1, act2);
	}

	// private Class<? extends Page> _getPage(String className) {
	// doAction(ACTION_NAME.OPEN_DETAIL, null);
	// String pageName = intent.getStringExtra("page");
	// pageName = "tvfan.tv.ui.gdx.programDetail.Page";
	// Class<? extends Page> p = _getPage(pageName);
	// if(p != null) {
	// Bundle b = new Bundle();
	// if(intent.getExtras() != null)
	// b.putAll(intent.getExtras());
	// startPage(p, b);
	// }
	// Class<? extends Page> page = null;
	// try{
	// page = (Class<? extends Page>)Class.forName(className);
	// }catch(Exception e){
	// page = null;
	// }
	// return page;
	// }
	/**
	 * Start Page in EPG 根据actionName打开匹配页面，并传入options
	 * 
	 * @param actionName
	 *            ACTION_NAME 行为名称
	 * @param options
	 *            Bundle 传入参数
	 * @return void
	 **/
	public void doAction(ACTION_NAME actionName, Bundle options) {
		if (options == null)
			options = new Bundle();
		options.putString("action", actionName.name());
		
		try {
			if (_pageAction != null)
				_logWrite(_pageAction, AppGlobalConsts.LOG_CMD.UMENG_PAGE_END);
				_logWrite(actionName.name(),
					AppGlobalConsts.LOG_CMD.UMENG_PAGE_START);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		switch (actionName) {
		case OPEN_DETAIL:
			if (options.containsKey("id"))
				options.putString("programSeriesId", options.getString("id"));
			options.putString("__pageName", "详情页");
			// if(tvfan.tv.ui.gdx.programDetail.Page.getInstance()!=null){
			// tvfan.tv.ui.gdx.programDetail.Page.getInstance().finish();
			// }
			if (_path != null && _path.size() > 0) {
				String path = _path.get(_path.size() - 1);
				if (!"用户中心".equals(path) && !"播放记录".equals(path)
						&& !"我的收藏".equals(path) && !"详情页".equals(path)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("UID", AppGlobalVars.getIns().USER_ID);
					map.put("PROGRAM_ID", options.getString("programSeriesId"));
					map.put("WAY_NAME", path + "-" + options.getString("name"));
					map.put("U_I_N", AppGlobalVars.getIns().USER_ID + "|"
							+ options.getString("programSeriesId") + "|"
							+ options.getString("name"));
					MobclickAgent.onEvent(
							getActivity().getApplicationContext(),
							"event_detail", map);
					Lg.i("TAG", path + ":" + options.getString("name") + ":"
							+ options.getString("programSeriesId"));
				}
			}
			startGdxPage(tvfan.tv.ui.gdx.programDetail.Page.class, options);
			break;
		case OPEN_FAVOR:
			options.putString("__pageName", "我的收藏");
			options.putString("actionParam", "{\"select\":\"favorite\"}");
			startGdxPage(tvfan.tv.ui.gdx.userCenters.Page.class, options);
//			startGdxPage(tvfan.tv.ui.gdx.topLists.Page.class, options);
			break;
		case OPEN_HISTORY:
			options.putString("__pageName", "播放记录");
			options.putString("actionParam", "{\"select\":\"history\"}");
			startGdxPage(tvfan.tv.ui.gdx.userCenters.Page.class, options);
//			startGdxPage(tvfan.tv.ui.gdx.searchs.Page.class, options);
			break;
		case OPEN_HOT_WORDS:
			options.putString("__pageName", "热词");
			startGdxPage(tvfan.tv.ui.gdx.ranking.Page.class, options);
			break;
		
		case OPEN_LIVEPLAYER:
			startAndrPage(tvfan.tv.ui.andr.play.liveplay.Page.class,
					options, getActivity());
			break;
		/*case OPEN_LIVESHOWPLAYER:
			startAndrPage(
					tvfan.tv.ui.andr.cibnplay.liveshowplay.Page.class,
					options, getActivity());
			break;*/
		case OPEN_PLAYER:
			startAndrPage(tvfan.tv.ui.andr.play.play.Page.class,
					options, getActivity());
			break;
		case OPEN_PORTAL:
			options.putString("__pageName", "首页");
			startGdxPage(tvfan.tv.ui.gdx.portal.Page.class, options);
			break;
		case OPEN_PROGRAM_LIST:
			if (options.containsKey("id"))
				options.putString("parentCatgId", options.getString("id"));
			options.putString("__pageName", "节目列表");
			startGdxPage(tvfan.tv.ui.gdx.programList.Page.class, options);
			break;
		case OPEN_SEARCH:
			options.putString("__pageName", "搜索");
			startGdxPage(tvfan.tv.ui.gdx.searchs.Page.class, options);
			break;
		case OPEN_USER_CENTER:
			options.putString("__pageName", "用户中心");
			startGdxPage(tvfan.tv.ui.gdx.userCenters.Page.class, options);
			break;
		case OPEN_MSG_DETAIL:
			options.putString("__pageName", "消息详请");
			startGdxPage(
					tvfan.tv.ui.gdx.userCenters.MessageDetailPage.class,
					options);
			break;
		case OPEN_YOUR_LIKE:
			options.putString("__pageName", "猜你喜欢");
			startGdxPage(tvfan.tv.ui.gdx.yourLike.Page.class, options);
			break;
		case OPEN_LIVE_SHOW:
			
				
		    	startGdxPage(tvfan.tv.ui.gdx.liveShow.Page.class, options);
			
			break;
		case OPEN_LIVE_SHOW_PAY:
			options.putString("__pageName", "liveshowpay");
			startGdxPage(tvfan.tv.ui.gdx.liveShow.special.Page.class,
					options);
			break;
		case OPEN_LIVE_SHOW_LIST:
			options.putString("__pageName", "liveshowlist");
			startGdxPage(tvfan.tv.ui.gdx.liveShow.programList.Page.class,
					options);
			break;
		case OPEN_LIVE_SHOW_DETAIL:
			options.putString("__pageName", "liveshowdetail");
			startGdxPage(tvfan.tv.ui.gdx.liveShow.detail.Page.class,
					options);
			break;

		case OPEN_HOT_PROGRAM_LIST:
			options.putString("__pageName", "liveshowdetail");
			startGdxPage(tvfan.tv.ui.gdx.programHotList.Page.class,
					options);
			break;
		case OPEN_SPECIAL_INDEX:
			options.putString("__pageName", "专题汇总");
			startGdxPage(tvfan.tv.ui.gdx.special.Page.class, options);
			break;
		case OPEN_NEWS_SPECIAL_INDEX:
			options.putString("__pageName", "新闻专题汇总");
			startGdxPage(tvfan.tv.ui.gdx.special.NewsPage.class, options);
			break;
		case OPEN_NEWS_VERCITAL_SPECIAL_INDEX:
			options.putString("__pageName", "新闻竖版专题汇总");
			startGdxPage(tvfan.tv.ui.gdx.special.VerticalNewsPage.class,
					options);
			break;
		case OPEN_SPECIAL_TEMPLATE:
			options.putString("__pageName", "专题模板");

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("UID", AppGlobalVars.getIns().USER_ID);
			map.put("SPECIAL_TEMPLATE_ID", options.getString("id"));
			map.put("U_S",
					AppGlobalVars.getIns().USER_ID + "|"
							+ options.getString("id"));
//			MobclickAgent.onEvent(getActivity().getApplicationContext(),
//					"event_special_template", map);

			startGdxPage(tvfan.tv.ui.gdx.special.TemplatePage.class,
					options);
			break;
		/*case OPEN_NEWS_INDEX:
			options.putString("__pageName", "新闻首页");
			startGdxPage(tvfan.tv.ui.andr.news.Page.class, options);
			break;
		case OPEN_NEWS_DETAIL:
			if (tvfan.tv.ui.andr.news.detail.Page.instance != null) {
				tvfan.tv.ui.andr.news.detail.Page.instance.finish();
			}
			startAndrPage(tvfan.tv.ui.andr.news.detail.Page.class,
					options, getActivity());
			break;
		case OPEN_NEWS_PROGRAM_LIST:
			options.putBoolean("is_hide", true);
			options.putString("hideCatgId", options.getString("id", ""));
			startAndrPage(tvfan.tv.ui.andr.news.detail.Page.class,
					options, getActivity());
			break;
		case OPEN_NEWS_TEMPLATE:
			options.putString("__pageName", "新闻专题模板");
			startAndrPage(tvfan.tv.ui.andr.news.special.Page.class,
					options, getActivity());
			break;*/
		case OPEN_DETAIL_ORDER:
			options.putString("__pageName", "支付页");
			startGdxPage(tvfan.tv.ui.gdx.programDetail.order.Page.class,
					options);
			break;
		case OPEN_BRAND_INDEX:
			options.putString("__pageName", "品牌专区");
			startGdxPage(tvfan.tv.ui.gdx.brand.Page.class, options);
			break;
		case OPEN_BRAND_DETAIL:
			options.putString("__pageName", "品牌详情");
			startGdxPage(tvfan.tv.ui.gdx.brand.detail.Page.class, options);
			break;
		case LAUNCH_APP:
			AppLauncher appLauncher = new AppLauncher(this.getActivity());
			try {
				JSONObject actionParam = new JSONObject(options.getString(
						"actionParam").toString());
				Bundle params = new Bundle();
				JSONArray ja = actionParam.getJSONArray("params");
				for (int i = 0; i < ja.length(); i++)
					params.putString(ja.getJSONObject(i).getString("name")
							.toString(), ja.getJSONObject(i).getString("value")
							.toString());

				if (!appLauncher.startAppByPackageName(
						actionParam.getString("packageName"), params)) {
					// (new ConfirmDialog(this, null)).show();
					appLauncher.startProccess(actionParam);
				}
			} catch (JSONException e) {
				Lg.e(TAG, e.getMessage());
				e.printStackTrace();
			}
			break;
		case OPEN_TOP_PROGRAM:
			options.putString("__pageName", "排行榜");
//			startGdxPage(tvfan.tv.ui.gdx.ranking.RankingPage.class,
//					options);
			options.putString("actionParam", "{\"select\":\"favorite\"}");
			startGdxPage(tvfan.tv.ui.gdx.topLists.Page.class,
					options);
			break;
		case OPEN_ABOUT_US:
			options.putString("__pageName", "关于我们");
			startGdxPage(tvfan.tv.ui.gdx.setting.Page.class, options);
			break;
		/*case OPEN_BG_IMG_MGR:
			options.putString("__pageName", "更换背景");
			startGdxPage(tvfan.tv.ui.gdx.setting.ChangeBgPage.class,
					options);
			break;*/
		case OPEN_SETTINGS:
			options.putString("__pageName", "常规设置");
			startGdxPage(tvfan.tv.ui.gdx.setting.SetPage.class, options);
			break;
		case OPEN_ORDERS_LIST:
			options.putString("__pageName", "我的订购");
			options.putString("actionParam", "{\"select\":\"orders\"}");
			startGdxPage(tvfan.tv.ui.gdx.userCenter.Page.class, options);
			break;
		case OPEN_MESSAGE_LIST:
			options.putString("__pageName", "消息中心");
			options.putString("actionParam", "{\"select\":\"message\"}");
			//startGdxPage(tvfan.tv.ui.gdx.userCenter.Page.class, options);
			startGdxPage(tvfan.tv.ui.gdx.userCenters.Page.class, options);
			break;
		case OPEN_PERFORMER:
			options.putString("__pageName", "相关人物");
			if (PersonPage.getInstance() != null) {
				PersonPage.getInstance().finish();
			}
			startGdxPage(
					tvfan.tv.ui.gdx.programDetail.dialog.PersonPage.class,
					options);
			break;
		case OPEN_BINGE_WATCHING:
			options.putString("__pageName", "热播追剧");
			startGdxPage(tvfan.tv.ui.gdx.bingewatching.Page.class, options);
			break;
		case OPEN_GROUP_PORTAL:
			options.putString("__pageName", "专区");
			startGdxPage(tvfan.tv.ui.gdx.brand.PortalPage.class, options);
			break;
		case OPEN_LIVE_DETAIL:
			options.putString("__pageName", "直播详情页");
			startGdxPage(tvfan.tv.ui.gdx.live.detail.Page.class, options);
			break;
			
		case OPEN_TEST:
			// Intent intent = new Intent(this.getActivity(),
			// TestAndrPage.class);
			// getActivity().startActivity(intent);
			break;
			case OPEN_USER_SETTING:
				options.putString("__pageName", "直播详情页");
				startGdxPage(tvfan.tv.ui.gdx.userSetting.Page.class, options);
				break;
		default:
			break;
		}
	}

	public boolean startSingleGdxPage(Class<? extends Page> page, Bundle bundle) {
		startPage(page, bundle, MODEL_SINGLE_INSTANCE);
		return true;
	}

	public boolean startGdxPage(Class<? extends Page> page, Bundle bundle) {
		startPage(page, bundle);
		return true;
	}

	public boolean startGdxPage(Class<? extends Page> page) {
		startPage(page);
		return true;
	}

	public boolean startAndrPage(Class<? extends Activity> page, Bundle bundle,
			Context mcontext) {
		Intent intent = new Intent(mcontext, page);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		getActivity().startActivity(intent);
		return true;
	}

	/**
	 * 获得当前导航路径
	 **/
	public String getCurrentPath() {
		String path = "首页/";
		for (int i = 0; i < _path.size(); i++) {
			path += _path.get(i) + "/";
		}
		return path;
	}

	/**
	 * 注册本地广播
	 **/
	public void registerLocalMsgReceiver(
			final LocalMsgListener localMsgListener,
			AppGlobalConsts.LOCAL_MSG_FILTER localMsgFilter) {
		unregisterLocalMsgReceiver(localMsgFilter);

		_intentFilter = new IntentFilter(localMsgFilter.toString());

		_broadcastReceivers.put(localMsgFilter, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				localMsgListener.onReceive(context, intent);
				// context.removeStickyBroadcast(intent);
				// this.getActivity().removeStickyBroadcast(intent);
			}
		});
		this.getActivity().registerReceiver(
				_broadcastReceivers.get(localMsgFilter), _intentFilter);
	}

	/**
	 * 注销本地广播
	 **/
	public void unregisterLocalMsgReceiver(
			AppGlobalConsts.LOCAL_MSG_FILTER localMsgFilter) {
		if (_broadcastReceivers.containsKey(localMsgFilter)) {
			this.getActivity().unregisterReceiver(
					_broadcastReceivers.get(localMsgFilter));
			_broadcastReceivers.remove(localMsgFilter);
		}
	}

	/**
	 * 发送本地消息
	 **/
	public void sendLocalMsg(AppGlobalConsts.LOCAL_MSG_FILTER localMsgFilter,
			Intent intent) {
		intent.setAction(localMsgFilter.toString());
		this.getActivity().sendBroadcast(intent);
	}

	/**
	 * 发送本地滞留消息
	 **/
	public void sendLocalStickyMsg(
			AppGlobalConsts.LOCAL_MSG_FILTER localMsgFilter, Intent intent) {
		intent.setAction(localMsgFilter.toString());
		this.getActivity().sendStickyBroadcast(intent);
	}

	/**
	 * 写日志
	 **/
	private void _logWrite(String logParamStr, AppGlobalConsts.LOG_CMD logCmd) {
		Intent intent = new Intent();
		intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME, logCmd.name());
		intent.putExtra(AppGlobalConsts.INTENT_LOG_PARAM, logParamStr);
		sendLocalStickyMsg(AppGlobalConsts.LOCAL_MSG_FILTER.LOG_WRITE, intent);
	}

	private void releaseMemory() {
		List<Actor> list = FocusFinder.findAllActorsInGroup(getStage()
				.getRoot());
		if (list != null) {
			for (Actor a : list) {
				if (a instanceof IMemoryManager) {
					((IMemoryManager) a).free();
				}
				if (a instanceof Image) {
					Image image = (Image) a;
					Utils.disposeImage(image, Texture.Type.NETWORK);
				}
			}
			_isReleased = true;
		}

		if (getTextureCache() != null) {
			for (Entry<Integer, Texture> entry : getTextureCache().entries()) {
				if (entry.value != null) {
					this.freeTexture(entry.value);
				}
			}
			getTextureCache().clear();
			_isReleased = true;
		}
	}

	private void rebuildAll() {
		List<Actor> list = FocusFinder.findAllActorsInGroup(getStage()
				.getRoot());
		if (list != null) {
			for (Actor a : list) {
				if (a instanceof TVFANLabel || a instanceof Label) {
					((Label) a).reload();
				} else {
					if (a != null)
						a.onResume();
				}
			}
		}
	}
	
}
