package tvfan.tv.daemon;

import java.util.Calendar;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.lib.Lg;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class DateTimeService extends Service {

	private static final String TAG = "TVFAN.EPG.DateTimeService";

	@Override
	public IBinder onBind(Intent arg0) {
		Lg.i(TAG, "onBind");
		return null;
	}


    @Override
    public void onCreate() {
        Lg.i(TAG, "onCreate");
        _updDatetime();
		super.onCreate();
    }

    @Override
    public void onDestroy() {
        Lg.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Lg.i(TAG, "onStart");
    }

    private void _startDateTimeFetcher() {
    	Handler _pd = new Handler();
    	_pd.postDelayed(new Runnable(){
			@Override
			public void run() {
				_updDatetime();
			}}, 60000);
	}

    private void _updDatetime() {
    	_sendLocalMsg(Calendar.getInstance().getTime().getTime());
    	_startDateTimeFetcher();
    }

	private void _sendLocalMsg(long now) {
		Lg.i(TAG, "_sendLocalMsg::now="+String.valueOf(now));
		Intent intent = new Intent(AppGlobalConsts.LOCAL_MSG_FILTER.DATETIME_UPDATE.toString());
		intent.putExtra("now", now);
		this.sendStickyBroadcast(intent);
	}
}
