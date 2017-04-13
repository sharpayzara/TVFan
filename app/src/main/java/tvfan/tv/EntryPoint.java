package tvfan.tv;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.badlogic.gdx.Gdx;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.gdx.PageActivity;
import com.luxtone.lib.utils.ScreenAdapterUtil;

import tvfan.tv.dal.LocalData;
import tvfan.tv.lib.Lg;

public class EntryPoint extends PageActivity {
	private static EntryPoint instance = null;
	private static final int SHOW_PROTECT_SCREEN = 1001;
	public static long TIME_TO_SHOW_PROTECT_SCREEN = 120000;
	private final String TAG = "TVFAN.EPG.EntryPoint";

	@Override
	public Class<? extends Page> configStartPage() {
		_startSrv();
		_writeLog();
		ScreenAdapterUtil.configDevScreenSize(AppGlobalConsts.APP_WIDTH,
				AppGlobalConsts.APP_HEIGHT);
		// 0
		Intent intent = this.getIntent();
		String act = intent.getStringExtra(AppGlobalConsts.INTENT_ACTION_NAME);
		if (act != null) {
			return _getPage((act == null) ? BasePage.ACTION_NAME.OPEN_PORTAL
					: BasePage.ACTION_NAME.valueOf(BasePage.ACTION_NAME.class,
							act));
		}

		act = intent.getStringExtra("action");
		if (act == null)
			return tvfan.tv.ui.gdx.portal.Page.class;

		AppGlobalVars.getIns().TMP_VARS.put("GdxHubIntent", intent);
		return tvfan.tv.Hub.class;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			showProtectScreen();
		}
	};

	private void showProtectScreen() {
		//展示屏保界面
		Intent intent = new Intent(EntryPoint.this, ScreenProtectActivity.class);
		startActivity(intent);
	}

	public static EntryPoint getInstance(){
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 1
		super.onCreate(savedInstanceState);
//		createVideo();
		instance = this;

		String timeArrIndex = new LocalData(this).getKV(AppGlobalConsts.PERSIST_NAMES.SCREEN_PROTECT_TIME_INDEX.name());
		if (!TextUtils.isEmpty(timeArrIndex)) {
			try {
				TIME_TO_SHOW_PROTECT_SCREEN = AppGlobalConsts.timeArr[Integer.parseInt(timeArrIndex)] * 60 * 1000;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	WindowManager mWindowManager;
	WindowManager.LayoutParams wmParams;
	LinearLayout mFloatLayout;
	VideoView mFloatView;
	MediaController mediaco;

	public void createVideo(){
		//获取LayoutParams对象
		wmParams = new WindowManager.LayoutParams();
		//获取的是LocalWindowManager对象
		mWindowManager =getWindowManager();
		Log.i(TAG, "mWindowManager3--->" + mWindowManager);
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 57;
		wmParams.y = 141;
		wmParams.width = 390;
		wmParams.height = 349;
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		AppGlobalVars.getIns().TMP_VARS.put("GdxHubIntent", getIntent());
		
		Hub.FRESH_PAGE = true;
	}

	@Override
	public void onResume() {
		// 2
		Lg.i(TAG, "onResume");
		super.onResume();

		if (Hub.FRESH_PAGE) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {

							if (Hub.instance != null) {
								((Hub) Hub.instance).onNewHubCreate();
							}
						}
					});

				}
			}, 100l);
		}
		if(TIME_TO_SHOW_PROTECT_SCREEN != 0)
			mHandler.sendEmptyMessageDelayed(SHOW_PROTECT_SCREEN, TIME_TO_SHOW_PROTECT_SCREEN);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeMessages(SHOW_PROTECT_SCREEN);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		mHandler.removeMessages(SHOW_PROTECT_SCREEN);
		if(TIME_TO_SHOW_PROTECT_SCREEN != 0)
			mHandler.sendEmptyMessageDelayed(SHOW_PROTECT_SCREEN, TIME_TO_SHOW_PROTECT_SCREEN);
		return super.onKeyDown(keyCode, event);
	}

	private Class<? extends Page> _getPage(BasePage.ACTION_NAME actName) {
		Class<? extends Page> p = MainStage.class;
		switch (actName) {
		case OPEN_DETAIL:
			p = tvfan.tv.ui.gdx.programDetail.Page.class;
			break;
		case OPEN_FAVOR:
			break;
		case OPEN_HISTORY:
			break;
		case OPEN_HOT_PROGRAM_LIST:
			break;
		case OPEN_HOT_WORDS:
			break;
		case OPEN_LIVE_SHOW:
			break;
		case OPEN_NEWS_DETAIL:
			break;
		/*case OPEN_NEWS_INDEX:
			p = tvfan.tv.ui.andr.news.Page.class;
			break;*/
		case OPEN_NEWS_SPECIAL_INDEX:
			p = tvfan.tv.ui.gdx.special.NewsPage.class;
			break;
		case OPEN_SPECIAL_TEMPLATE:
			p = tvfan.tv.ui.gdx.special.TemplatePage.class;
			break;
		case OPEN_PLAYER:
			break;
		case OPEN_PORTAL:
			p = tvfan.tv.ui.gdx.portal.Page.class;
			break;
		case OPEN_PROGRAM_LIST:
			break;
		case OPEN_SEARCH:
			p = tvfan.tv.ui.gdx.search.Page.class;
			break;
		case OPEN_TEST:
			break;
		case OPEN_USER_CENTER:
			break;
		case OPEN_YKEWPLAYER:
			break;
		case OPEN_YOUR_LIKE:
			break;
		default:
			break;
		}
		return p;
	}

	private void _startSrv() {
		Intent intent = new Intent("tvfan.tv.daemon.epgservice");
		intent.setPackage(getPackageName());
		this.startService(intent);
	}

	private void _writeLog() {
		Intent intent = new Intent();
		intent.setAction(AppGlobalConsts.LOCAL_MSG_FILTER.LOG_WRITE.toString());
		intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME,
				AppGlobalConsts.LOG_CMD.EPG_LAUNCH.name());
		intent.putExtra(AppGlobalConsts.INTENT_LOG_PARAM,
				AppGlobalVars.getIns().DEVICE_ID);
		this.sendStickyBroadcast(intent);
	}
}
