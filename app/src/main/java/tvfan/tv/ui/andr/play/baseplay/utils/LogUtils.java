package tvfan.tv.ui.andr.play.baseplay.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.AppGlobalConsts.LOCAL_MSG_FILTER;
import tvfan.tv.BasePage.LocalMsgListener;
import tvfan.tv.dal.HttpResponse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class LogUtils {
	public static String PLAYLOGURL = AppGlobalConsts.INTENT_LOG_PARAM;
	private static IntentFilter _intentFilter = null;
	private static BasePage basepage ;
	private static HashMap<AppGlobalConsts.LOCAL_MSG_FILTER, BroadcastReceiver> _broadcastReceivers = new HashMap<AppGlobalConsts.LOCAL_MSG_FILTER, BroadcastReceiver>();
	public static void enterLogger(String programserialid,String programid,String customerid,String path,Context context){
		//开始日志
		String loggertimestamp = System.currentTimeMillis()+java.util.UUID.randomUUID().toString().substring(0,8);
		String paramulr = "";
		try {
			paramulr = String.format("programSerialId=%s&programId=%s&uId=%s&deviceId=%s&path=%s&timestamp=%s"
					,URLEncoder.encode(programserialid, "UTF-8")
					,URLEncoder.encode(programid, "UTF-8")
					,URLEncoder.encode(AppGlobalVars.getIns().USER_ID, "UTF-8")
					,URLEncoder.encode(AppGlobalVars.getIns().DEVICE_ID, "UTF-8")
					,URLEncoder.encode(BasePage._path.toString(), "UTF-8")
					,loggertimestamp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		Intent intent = new Intent();
		intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME, AppGlobalConsts.LOG_CMD.PLAY_START.name());		
		intent.putExtra(PLAYLOGURL, paramulr);
		sendLocalMsg(LOCAL_MSG_FILTER.LOG_WRITE, intent,context);
	}

	public static void livePlayLogger(String channelId, Context context) {
		//直播开始播放日志
		String paramulr = "";
		try {
			paramulr = String.format("device=%s&client=%s&channel=%s"
					,URLEncoder.encode(AppGlobalVars.getIns().DEVICE_ID, "UTF-8")
					,URLEncoder.encode("41", "UTF-8")
					,URLEncoder.encode(channelId, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent = new Intent();
		intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME, AppGlobalConsts.LOG_CMD.LIVEPLAY_START.name());		
		intent.putExtra(PLAYLOGURL, paramulr);
		sendLocalMsg(LOCAL_MSG_FILTER.LOG_WRITE, intent, context);
	}
	
	public static void exitLogger(String programserialid,String programid,String customerid,String path,Context context){
		//开始日志
		String loggertimestamp = System.currentTimeMillis()+java.util.UUID.randomUUID().toString().substring(0,8);
		String paramulr = "";
		try {
			paramulr = String.format("programSerialId=%s&programId=%s&uId=%s&deviceId=%s&path=%s&timestamp=%s"
					,URLEncoder.encode(programserialid, "UTF-8")
					,URLEncoder.encode(programid, "UTF-8")
					,URLEncoder.encode(AppGlobalVars.getIns().USER_ID, "UTF-8")
					,URLEncoder.encode(AppGlobalVars.getIns().DEVICE_ID, "UTF-8")
					,URLEncoder.encode(BasePage._path.toString(), "UTF-8")
					,loggertimestamp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent();
		intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME, AppGlobalConsts.LOG_CMD.PLAY_END.name());		
		intent.putExtra(PLAYLOGURL, paramulr);
		sendLocalMsg(LOCAL_MSG_FILTER.LOG_WRITE, intent,context);
	}

	/**
	 * 播放失败日志输出
	 */
	public static void playErrorLogger(String uid, String programSeriesId,
									   String src, String cpId, String type, String errorType,
									   Context context){
		Intent intent = new Intent();
		intent.putExtra("uid", uid);
		intent.putExtra("programSeriesId", programSeriesId);
		intent.putExtra("src", src);
		intent.putExtra("cpId", cpId);
		intent.putExtra("type", type);
		intent.putExtra("err", errorType);
		intent.putExtra("logType", "post");
		intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME, AppGlobalConsts.LOG_CMD.PLAY_ERROR.name());
		sendLocalMsg(LOCAL_MSG_FILTER.LOG_WRITE, intent, context);
	}

	/**
	 * 发送本地消息
	 **/
	public static void sendLocalMsg(AppGlobalConsts.LOCAL_MSG_FILTER localMsgFilter,
			Intent intent,Context mcontext) {
		intent.setAction(localMsgFilter.toString());
		mcontext.sendBroadcast(intent);
	}
	
	/**
	 * 注册本地广播
	 **/
	public static void registerLocalMsgReceiver(
			final LocalMsgListener localMsgListener,
			AppGlobalConsts.LOCAL_MSG_FILTER localMsgFilter,Context context) {
		
		try {
			unregisterLocalMsgReceiver(localMsgFilter,context);

			_intentFilter = new IntentFilter(localMsgFilter.toString());

			_broadcastReceivers.put(localMsgFilter, new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					//Toast.makeText(context, "网络断了", 3000).show();
					localMsgListener.onReceive(context, intent);
				}
			});
			unregisterLocalMsgReceiver(localMsgFilter,context);
			context.registerReceiver(
					_broadcastReceivers.get(localMsgFilter), _intentFilter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 注销本地广播
	 **/
	public static void unregisterLocalMsgReceiver(
			AppGlobalConsts.LOCAL_MSG_FILTER localMsgFilter,Context context) {
		try {
			if (_broadcastReceivers!=null&&_broadcastReceivers.containsKey(localMsgFilter)) {
				context.unregisterReceiver(
						_broadcastReceivers.get(localMsgFilter));
				_broadcastReceivers.remove(localMsgFilter);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
