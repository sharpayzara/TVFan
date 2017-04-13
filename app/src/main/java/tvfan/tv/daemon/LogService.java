package tvfan.tv.daemon;

import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.Lg;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

//import com.umeng.analytics.MobclickAgent;

public class LogService extends Service {

	private static final String TAG = "TVFAN.EPG.LogService";
	private final String _msgFilter = AppGlobalConsts.LOCAL_MSG_FILTER.LOG_WRITE.toString();
	private String _logServer = AppGlobalVars.getIns().SERVER_URL.get("LOG");
	private BroadcastReceiver _br = null;
	private RemoteData _remoteData = null;

	@Override
	public IBinder onBind(Intent arg0) {
		Lg.i(TAG, "onBind");
		return null;
	}

    @Override
    public void onCreate() {
        Lg.i(TAG, "onCreate");
		super.onCreate();
		//umeng
		MobclickAgent.openActivityDurationTrack(false);
		//umeng END
		_remoteData = new RemoteData(this);
		_registerLocalMsgReceiver();
    }

    @Override
    public void onDestroy() {
        Lg.i(TAG, "onDestroy");
        super.onDestroy();
        _unregisterLocalMsgReceiver();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Lg.i(TAG, "onStart");
    }

	@SuppressLint("NewApi")
	private void _procOnReceive(Context context, Intent intent) {
		String logType = intent.getStringExtra("logType");
		String logCmdStr = intent.getStringExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME);
		if(logCmdStr == null || logCmdStr.isEmpty())
			return;
		AppGlobalConsts.LOG_CMD logCmd= AppGlobalConsts.LOG_CMD.valueOf(AppGlobalConsts.LOG_CMD.class, logCmdStr);
		if("post".equals(logType)) {

			switch (logCmd) {

				case PLAY_ERROR:
					String uid = intent.getStringExtra("uid");
					String programSeriesId = intent.getStringExtra("programSeriesId");
					String src = intent.getStringExtra("src");
					String cpId = intent.getStringExtra("cpId");
					String type = intent.getStringExtra("type");
					String errorType = intent.getStringExtra("err");
					String postUrl = _logServer + "/v40/logger!playfailed.action";
					Log.d(TAG, "play_error_log:" + errorType + ", url:" + src);
					_remoteData.playErrorLog(uid, programSeriesId, src, cpId, type, errorType, postUrl,
							new HttpResponse.Listener4JSONObject() {
								@Override
								public void onResponse(JSONObject response) {
								}
								@Override
								public void onError(String httpStatusCode) {
								}
							});
					break;
			}
		}
		else {
			String logParamStr = intent.getStringExtra(AppGlobalConsts.INTENT_LOG_PARAM);

			switch(logCmd) {
				case DETAIL_PAGE_PATH:
					logParamStr = "/v40/logger!pvLog.action?" + logParamStr;
					Log.e("pvLog.action","pvLog.action");
				/*详情页统计接口
http://114.247.94.15:8082/logger/v40/logger!pvLg.action?programSerialId=000108611909580456558&uId=00850866457574428292&deviceId=060103001000277&path=myfav&timestamp=43243243243
*/
					break;
				case PLAY_END:
					logParamStr = "/v40/logger!exit.action?" + logParamStr;
				/*播放结束日志接口
http://114.247.94.15:8082/logger/v40/logger!exit.action?catgId=&programSerialId=000108611909580456558&programId=000208611909581422589&customerId=00850866457574428292&deviceId=060103001000277&accountId=00850866457574428292&path=%E7%94%B5%E5%BD%B1%7C%E5%85%A8%E9%83%A8%7C|%E3%80%8A%E5%AE%8C%E7%BE%8E%E7%BA%A6%E4%BC%9A%E3%80%8B%7C%E5%AE%8C%E7%BE%8E%E7%BA%A6%E4%BC%9A&timestamp=1427447175260181e0794
*/
					break;
				case PLAY_START:
					logParamStr = "/v40/logger!enter.action?" + logParamStr;
				/*播放开始日志接口
http://114.247.94.15:8082/logger/v40/logger!enter.action?programSerialId=000108611909580456558&programId=000208611909581422589&uId=00850866457574428292&deviceId=060103001000277&path=%E7%94%B5%E5%BD%B1%7C%E5%85%A8%E9%83%A8%7C|%E3%80%8A%E5%AE%8C%E7%BE%8E%E7%BA%A6%E4%BC%9A%E3%80%8B%7C%E5%AE%8C%E7%BE%8E%E7%BA%A6%E4%BC%9A&timestamp=1427447175260181e0794
*/
					break;
				case LIVEPLAY_START:
					logParamStr = "/v40/logger!liveapi.action?" + logParamStr;
					break;
				case EPG_LAUNCH:
					this.removeStickyBroadcast(intent);
					logParamStr = "/v40/logger!startup.action?deviceId=" + logParamStr;
//				logParamStr = "/startup/" + logParamStr;
				/*
				 * new: http://114.247.94.15:8082/logger/v40/logger!startup.action?deviceId=060404001000024
				 * old: http://114.247.94.15:8082/logger/v40/startup/060404001000024*/
					break;

				case UMENG_PAGE_START:
					Lg.i(TAG, logParamStr+"|UMENG_PAGE_START");
					this.removeStickyBroadcast(intent);
					MobclickAgent.onPageStart(logParamStr);
					MobclickAgent.onResume(getApplicationContext());
					logParamStr = null;
					break;

				case UMENG_PAGE_END:
					Lg.i(TAG, logParamStr+"|UMENG_PAGE_END");
					this.removeStickyBroadcast(intent);
					MobclickAgent.onPageEnd(logParamStr);
					MobclickAgent.onPause(getApplicationContext());
					logParamStr = null;
					break;

				default:
					logParamStr = null;
					break;
			}

			if(logParamStr==null)
				return;

			Lg.i(TAG, logParamStr);
			_remoteData.startJsonHttpGet(_logServer + logParamStr, new HttpResponse.Listener4JSONObject(){
						@Override
						public void onResponse(JSONObject response) {}
						@Override
						public void onError(String errorMessage) {}
					}
			);
		}
	}

    private void _registerLocalMsgReceiver() {
		_unregisterLocalMsgReceiver();
		_br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				_procOnReceive(context, intent);
			}
		};
		this.getApplicationContext().registerReceiver(_br, new IntentFilter(_msgFilter));
	}

	private void _unregisterLocalMsgReceiver() {
		if(_br != null)
			this.getApplicationContext().unregisterReceiver(_br);
		_br = null;
	}
}
