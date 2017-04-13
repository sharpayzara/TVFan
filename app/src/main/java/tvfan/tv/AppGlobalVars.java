package tvfan.tv;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.UserHelper;
import tvfan.tv.lib.Lg;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.text.TextUtils;

import tvfan.tv.R;

public class AppGlobalVars {
	private final static String TAG = "TVFAN.EPG.AppGlobalVars";
	private static AppGlobalVars _appGlobalVars = null;

	public static AppGlobalVars getIns() {
		if (_appGlobalVars == null || SERVER_URL == null
				|| SERVER_URL.size() <= 0) {
			try {
				_initEnvVars();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return _appGlobalVars;
	}

	@SuppressLint("NewApi")
	private static void _initEnvVars() throws JSONException {
		LocalData _ld = new LocalData(null);
		String loginInfo = _ld.getKV("_DEV_LOGIN_INFO_");
		if (TextUtils.isEmpty(loginInfo))
			return;

		_appGlobalVars = new AppGlobalVars();
		_appGlobalVars.SERVER_URL = new HashMap<String, String>();
		_appGlobalVars.TMP_VARS = new HashMap<String, Object>();
		_appGlobalVars.HTTP_STACK = new HashMap<String, String>();
		JSONObject response = new JSONObject(loginInfo);
		String user = _ld.getKV(AppGlobalConsts.PERSIST_NAMES.CURRENT_USER
				.name());
		if (user == null || user.isEmpty()) {
			_appGlobalVars.USER_ID = response.getString("userId");
			_ld.setKV(AppGlobalConsts.PERSIST_NAMES.DEFAULT_USER.name(),
					_appGlobalVars.USER_ID);
			Lg.i(TAG, "set DEFAULT_USER");
		} else {
			UserHelper uh = new UserHelper(null);
			ContentValues cvs = uh.getUser(user);
			if (cvs.containsKey("token")) {
				_appGlobalVars.USER_PIC = cvs.getAsString("wxheadimgurl");
				_appGlobalVars.USER_NICK_NAME = cvs.getAsString("wxname");
				_appGlobalVars.USER_TOKEN = cvs.getAsString("token");
			}
			_appGlobalVars.USER_ID = user;
			Lg.i(TAG, "get CURRENT_USER");
		}
		if (TextUtils.isEmpty(_appGlobalVars.USER_PIC)) {
			_appGlobalVars.USER_PIC = "@"
					+ String.valueOf(R.drawable.usercentericon);
			_appGlobalVars.USER_NICK_NAME = "个人中心";
		}
		_appGlobalVars.TEMPLATE_ID = response.getString("templateId");
		_appGlobalVars.DEVICE_ID = response.getString("deviceId");
		_appGlobalVars.LAN_MAC = tvfan.tv.lib.Utils.getEthernetMac();
		JSONObject ul = response.getJSONObject("addressList");

		_appGlobalVars.SERVER_URL.clear();
		_appGlobalVars.SERVER_URL.put("EPG",
				(ul.has("epg")) ? ul.getString("epg") : "");
		_appGlobalVars.SERVER_URL.put("LOG",
				(ul.has("log")) ? ul.getString("log") : "");
		_appGlobalVars.SERVER_URL.put("SEARCH",
				(ul.has("search")) ? ul.getString("search") : "");
		_appGlobalVars.SERVER_URL.put("UPDATE",
				(ul.has("update")) ? ul.getString("update") : "");
		_appGlobalVars.SERVER_URL.put("IMS",
				(ul.has("ims")) ? ul.getString("ims") : "");
		_appGlobalVars.SERVER_URL.put("BMSORDER",
				(ul.has("bmsOrder") ? ul.getString("bmsOrder") : ""));
		_appGlobalVars.SERVER_URL.put("BMSWXBIND",
				(ul.has("bmswxBind") ? ul.getString("bmswxBind") : ""));
		_appGlobalVars.SERVER_URL.put("BMSPAYLIST",
				(ul.has("bmsPayList") ? ul.getString("bmsPayList") : ""));
		_appGlobalVars.SERVER_URL.put("LIVESHOW",
				(ul.has("liveshow") ? ul.getString("liveshow") : ""));
		_appGlobalVars.SERVER_URL.put("LIVEPLAY",
				(ul.has("liveplay") ? ul.getString("liveplay") : ""));
		_appGlobalVars.SERVER_URL.put("LIVESHOW_DB",
				(ul.has("liveshow_db") ? ul.getString("liveshow_db") : ""));
		_appGlobalVars.SERVER_URL.put("LIVESHOW_ZB",
				(ul.has("liveshow_zb") ? ul.getString("liveshow_zb") : ""));
		_appGlobalVars.SERVER_URL.put("LIVEAPI_CHANNEL",
				(ul.has("liveapi_channel") ? ul.getString("liveapi_channel") : ""));
		_appGlobalVars.SERVER_URL.put("LIVEAPI_PROGRAM",
				(ul.has("liveapi_program") ? ul.getString("liveapi_program") : ""));

		Lg.i(TAG, "_initEnvVars done");
	}

	/**
	 * 模板ID
	 **/
	public String TEMPLATE_ID = "";

	/**
	 * 设备串号
	 **/
	public String DEVICE_ID = "";

	/**
	 * 有线网卡MAC
	 **/
	public String LAN_MAC = "";

	/**
	 * 当前用户ID
	 **/
	public String USER_ID = "";

	/**
	 * 当前用户token
	 */
	public static String USER_TOKEN = "";

	/**
	 * 当前用户头像
	 */
	public String USER_PIC = "";

	/**
	 * 当前用户名(微信昵称)
	 */
	public String USER_NICK_NAME = "";

	/**
	 * 客服电话
	 */
	public String PHONE_NUMBER = "";

	/**
	 * 服务URL列表，来自设备登录JSON数据 addressList <br />
	 * Key: EPG, LOG, SEARCH, UPDATE, IMS, BMS, LIVESHOW, LIVEPLAY
	 */

	public static HashMap<String, String> SERVER_URL = null;

	/**
	 * 暂存变量池，请及时TMP_VARS.remove(key)
	 **/
	public HashMap<String, Object> TMP_VARS = null;

	/**
	 * HTTP错误栈，来自RemoteData.java记录 Key: Time Value: http status code | url
	 **/
	public HashMap<String, String> HTTP_STACK = null;
}
