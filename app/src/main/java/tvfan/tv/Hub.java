package tvfan.tv;

import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.lib.Lg;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.luxtone.lib.gdx.Page;
//import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent;

public class Hub extends BasePage {
	private final String TAG = "TVFAN.EPG.Hub";
	private int i = 0;
	public static boolean FRESH_PAGE = false;

	public static Page instance = null;

	@Override
	public void onResume() {

		Lg.i(TAG, " Hub onresume");
		i++;
		if (i == 2) {
			MobclickAgent.onKillProcess(this.getActivity());
			this.finish();
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		super.onResume();
	}

	public void onNewHubCreate() {
		if (FRESH_PAGE) {
			gotoPage();
			FRESH_PAGE = false;
		}

	}

	@Override
	public void onCreate(Bundle bundle) {
		// am start -n "tvfan.tv/.Bootloader" --es action "OPEN_DETAIL"
		// --es actionParam "{id:\"CIBN_a39195c8-d168-4eca-9214-7aa339861695\"}"
		// am start -n "tvfan.tv/.Bootloader" --es action
		// "OPEN_NEWS_DETAIL" --es actionParam
		// "{id:\"000108612135991920796\",parentCatgId:\"00050000000000000000000000019649\"}"
		// am start -n "tvfan.tv/.Bootloader" --es action
		// "OPEN_PROGRAM_LIST" --es actionParam
		// "{id:\"00050000000000000000000000019596\",
		// actionParam:{\"image\":\"http:\/\/images.ott.cibntv.net\/pdimage\/vod\/channel\/0620\/3.jpg\",\"name\":\"\"}}"

		super.onCreate(bundle);
		instance = Hub.this;
		Lg.i(TAG, " Hub onCreate");
		gotoPage();

		// this.finish();
	}

	@Override
	public void onDispose() {
		instance = null;
		super.onDispose();
	}

	private void gotoPage() {
		Intent hubIntent = (Intent) AppGlobalVars.getIns().TMP_VARS
				.get("GdxHubIntent");
		final String action = hubIntent.getStringExtra("action");
		String actionParam = "";
		if (hubIntent.hasExtra("actionParam"))
			actionParam = hubIntent.getStringExtra("actionParam");
		Lg.i(TAG, "action: " + action);
		Lg.i(TAG, "actParam: " + actionParam);
		JSONObject actParamObj;
		String id = "";
		try {
			if (!TextUtils.isEmpty(actionParam)) {
				actParamObj = new JSONObject(actionParam);
				if (actParamObj.has("id"))
					id = actParamObj.getString("id");
			}
		} catch (JSONException e) {
			Lg.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		Bundle options = new Bundle();
		options.putString("id", id);
		options.putString("action", action);
		options.putString("actionParam", actionParam);
		doAction(ACTION_NAME.valueOf(ACTION_NAME.class, action), options);
		if (ACTION_NAME.valueOf(ACTION_NAME.class, action).equals(
				ACTION_NAME.OPEN_LIVEPLAYER)) {
			this.finish();
		}
	}
}
