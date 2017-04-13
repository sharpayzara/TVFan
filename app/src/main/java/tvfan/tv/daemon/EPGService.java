package tvfan.tv.daemon;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.lib.Lg;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class EPGService extends Service {
	private static final String TAG = "TVFAN.EPG.EPGService";
	@Override
    public IBinder onBind(Intent intent) {
        Lg.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
    	super.onCreate();
        Lg.i(TAG, "onCreate");

        /* 启动网络检测 */
        Intent intent = new Intent("tvfan.tv.daemon.netservice");
        intent.setPackage(getPackageName());
		this.startService(intent);

		/* 启动间隔报时 */
		intent = new Intent("tvfan.tv.daemon.datetimeService");
		intent.setPackage(getPackageName());
		this.startService(intent);

		/* 启动日志服务 */
		intent = new Intent("tvfan.tv.daemon.logService");
		intent.setPackage(getPackageName());
		this.startService(intent);

		/* 启动消息推送监听 */
		PushService.actionStart(getApplicationContext(), AppGlobalVars.getIns().DEVICE_ID, AppGlobalConsts.CHANNELS_ID);

		/* 启动下载服务 */
		intent = new Intent("tvfan.tv.daemon.downloadService");
		intent.setPackage(getPackageName());
		this.startService(intent);

		/* 启动EPG升级和引导图片下载服务 */
		intent = new Intent("tvfan.tv.daemon.EPGUpdateService");
		intent.setPackage(getPackageName());
		this.startService(intent);

		/* tvfan.tv.daemon.interopservice */
		intent = new Intent("tvfan.tv.daemon.interopservice");
		intent.setPackage(getPackageName());
		this.startService(intent);
    }

    @Override
    public void onDestroy() {
        Lg.i(TAG, "onDestroy");
        super.onDestroy();
        PushService.actionStop(getApplicationContext());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Lg.i(TAG, "onStart");
    }
}
