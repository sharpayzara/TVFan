package tvfan.tv.daemon;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.models.PortalFreshEvent;
import tvfan.tv.dal.models.PortalMsgUpdateEvent;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.andr.TestActivity;
import tvfan.tv.ui.andr.widgets.CustomDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;

import com.itv.android.cpush.CrystalMessage;
import com.itv.android.cpush.CrystalPushBaseReceiver;
import com.itv.android.cpush.CrystalPushManager;

import de.greenrobot.event.EventBus;

public class PushService extends Service implements CrystalPushBaseReceiver {
	private static final String TAG = "TVFAN.EPG.PushService";
	private static final String USERNAME = null;
	private static final String PASSWORD = null;
	private static String _subject;
	private static String _clientID;
	private boolean isRegister = false;

	private static final int HOST_ERR = -1;
	private static final int PORT_ERR = -2;
	private static final int SUBJECT_ERR = -3;
	private static final int CLIENTID_ERR = -4;
	private static final int CONN_ERR = -999;
	private static final int REGISTER_ERR = 0;
	private static final int REGISTER_SUCCESS = 1;
	private static final int UNREGISTER = 999;
	private static Context mContext;

	Handler mHandler;

	// Static method to start the service
	public static void actionStart(Context ctx, String clientID, String subject) {
		Lg.i(TAG, "actionStart");
		_clientID = clientID;
		_subject = subject;
		mContext = ctx;
		Intent i = new Intent(ctx, PushService.class);
		i.setAction(_clientID + ".START");
		ctx.startService(i);
	}

	// Static method to stop the service
	public static void actionStop(Context ctx) {
		Lg.i(TAG, "actionStop");
		Intent i = new Intent(ctx, PushService.class);
		i.setAction(_clientID + ".STOP");
		ctx.startService(i);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Lg.i(TAG, "onStartCommand");
		if (intent.getAction().equals(_clientID + ".STOP") == true) {
			unregister();
			stopSelf();
		} else if (intent.getAction().equals(_clientID + ".START") == true) {
			register();
		}
		return 0;
	}

	private void setRegister(int reason) {
		Lg.i(TAG, "setRegister");
		switch (reason) {
		case HOST_ERR:
		case PORT_ERR:
		case SUBJECT_ERR:
		case CLIENTID_ERR:
			isRegister = false;
			break;
		case CONN_ERR:
			isRegister = false;
			// retry
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					crystalPushManager();
				}
			}, 1000 * 60);
			break;
		case REGISTER_ERR:
			isRegister = false;
			break;
		case UNREGISTER:
			isRegister = false;
			break;
		case REGISTER_SUCCESS:
			isRegister = true;
			break;
		default:
			break;
		}
	}

	private void register() {
		Lg.i(TAG, "register");
		if (isRegister)
			return;

		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					crystalPushManager();
				} catch (Exception e) {
					Lg.i(TAG, "register:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

	protected void crystalPushManager() {
		final String url = AppGlobalVars.getIns().SERVER_URL.get("IMS");
		Lg.i(TAG, _subject + "|" + _clientID);
		int si = url.lastIndexOf(":");
		int result = CrystalPushManager.registerPush(url.substring(0, si),
				Integer.parseInt(url.substring(++si)), _subject, _clientID,
				USERNAME, PASSWORD, true, PushService.this);
		setRegister(result);
	}

	private void unregister() {
		Lg.i(TAG, "unregister");
		if (!isRegister)
			return;

		try {
			CrystalPushManager.unregisterPush();
			setRegister(UNREGISTER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		Lg.i(TAG, "connectionLost:" + cause.getMessage());
	}

	@Override
	public void messageArrived(String topicName, final CrystalMessage message) {
		Lg.i(TAG, "messageArrived:" + message);
		_procMessage(message.toString());

		// wanqi,test
//		Intent i = new Intent(mContext, TestActivity.class);
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		this.startActivity(i);

	}

	@Override
	public IBinder onBind(Intent intent) {
		Lg.i(TAG, "onBind");
		return null;
	}

	private enum _MsgType {
		/**
		 * 升级消息
		 **/
		upgrade,
		/**
		 * 图文消息
		 **/
		txt,
		/**
		 * 应用消息
		 **/
		app,
		/**
		 * 首页滚动消息
		 **/
		marquee,
		/**
		 * 用户个人消息
		 **/
		user,
		/**
		 * 广告类消息
		 **/
		ad,
		/**
		 * 微信绑定
		 **/
		weixinregister,
		/**
		 * 购买成功
		 **/
		paysuccess,
		/**
		 * 播放
		 **/
		play,
		/**
		 * 内容更新
		 **/
		update,
		/**
		 * 新消息送达
		 **/
		newmsg
	};

	private void _procMessage(String message) {
		/*
		 * upgrade：升级消息；txt：图文消息；app：应用消息；marquee：跑马灯；user：用户个人消息；ad：广告类消息
		 * weixinregister:微信绑定；paysuccess: 购买成功 ；play：直接播放；update：内容更新；
		 */
		String msgtype = null;
		String decmsg = StringEscapeUtils.unescapeHtml(message);
		try {
			JSONObject msgObj = new JSONObject(decmsg);
			msgtype = msgObj.getJSONObject("head").getString("type");
		} catch (JSONException e) {
			e.printStackTrace();
			msgtype = null;
			Lg.e(TAG, e.getMessage());
		}

		if (msgtype == null)
			return;

		_MsgType mt = _MsgType.valueOf(_MsgType.class, msgtype);
		switch (mt) {
		case ad:
			break;
		case app:
			break;
		case marquee:
			_sendLocalMsg(AppGlobalConsts.LOCAL_MSG_FILTER.NOTICE_DISPLAY,
					decmsg, true);
			break;
		case paysuccess:
			_sendLocalMsg(AppGlobalConsts.LOCAL_MSG_FILTER.PAY_RESULT, decmsg,
					false);
			break;
		case play:
			break;
		case txt:
			break;
		case update:
			break;
		case upgrade:
			break;
		case user:
			break;
		case weixinregister:
			_sendLocalMsg(AppGlobalConsts.LOCAL_MSG_FILTER.USER_BIND, decmsg,
					false);
			break;
		case newmsg:
			_sendLocalMsg(AppGlobalConsts.LOCAL_MSG_FILTER.NEW_MSG_ARRIVED,
					decmsg, true);
			break;
		default:
			break;
		}
	}

	private void _sendLocalMsg(AppGlobalConsts.LOCAL_MSG_FILTER msgFilter,
			String text, boolean isSticky) {
		Lg.i(TAG, "_sendLocalMsg::msgFilter=" + msgFilter.name() + " | text::"
				+ text);
		Intent intent = new Intent();
		intent.setAction(msgFilter.toString());
		intent.putExtra(AppGlobalConsts.INTENT_MSG_PARAM, text);
		if (isSticky)
			this.sendStickyBroadcast(intent);
		else
			this.sendBroadcast(intent);
	}
}
