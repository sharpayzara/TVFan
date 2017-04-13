package tvfan.tv.daemon;

import java.net.InetAddress;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.lib.Lg;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

public class NetService extends Service {

	private static final String TAG = "TVFAN.EPG.NetService";

	@Override
	public IBinder onBind(Intent arg0) {
		Lg.i(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Lg.i(TAG, "onCreate");
		_regConnectivityManager();
		_regWifiManager();
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

	private void _regConnectivityManager() {
		this.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Lg.i(TAG, "CONNECTIVITY_CHANGE::onReceive");
				_checkNet();
			}
		},
				new IntentFilter(
						android.net.ConnectivityManager.CONNECTIVITY_ACTION));
	}

	private void _regWifiManager() {
		this.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Lg.i(TAG, "CONNECTIVITY_CHANGE::onReceive");
				_checkWifi();
			}

		}, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
	}

	private void _checkWifi() {
		WifiInfo wifiInfo = ((WifiManager) getSystemService(WIFI_SERVICE))
				.getConnectionInfo();
		int level = -100;
		if (wifiInfo != null) {
			level = wifiInfo.getRssi();
		}
		_sendLocalMsg(level);
	}

	private void _checkNet() {
		ConnectivityManager connManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = connManager.getActiveNetworkInfo();
		int netType = -1;
		if (ni != null) {
			netType = ni.getType();
		}
		if (ni != null && ni.isConnected()) {
			Lg.i(TAG, "_checkNet::isConnected=true");
			_checkInternetAccess(netType);
		} else {
			Lg.i(TAG, "_checkNet::isConnected=false");
			_sendLocalMsg(false, netType);
		}
	}

	private void _checkInternetAccess(final int typeName) {

		Lg.i(TAG, "_checkInternetAccess::www.baidu.com");
		new Thread(new Runnable() {
			@Override
			public void run() {
				InetAddress address = null;
				try {
					address = InetAddress.getByName("www.baidu.com");
				} catch (Exception e) {
					Lg.e(TAG,
							"_checkInternetAccess::www.baidu.com=>"
									+ e.getMessage());
					e.printStackTrace();
					address = null;
				}
				if (address != null) {
					Lg.i(TAG,
							"_checkInternetAccess::www.baidu.com=>"
									+ address.getHostAddress());
					_sendLocalMsg(true, typeName);
				} else {
					Lg.i(TAG, "_checkInternetAccess::www.baidu.com=>unknown");
					_sendLocalMsg(false, typeName);
				}
			}
		}).start();
	}

	private void _sendLocalMsg(boolean isConnected, int netType) {
		Lg.i(TAG, "_sendLocalMsg::isConnected=" + String.valueOf(isConnected));
		Intent intent = new Intent(
				AppGlobalConsts.LOCAL_MSG_FILTER.NET_CHANGED.toString());
		intent.putExtra("isConnected", isConnected);
		intent.putExtra("netType", netType);
		this.sendStickyBroadcast(intent);
	}

	private void _sendLocalMsg(int level) {
		Intent intent = new Intent(
				AppGlobalConsts.LOCAL_MSG_FILTER.RSSI_CHANGED.toString());
		intent.putExtra("level", level);
		this.sendStickyBroadcast(intent);

	}
}
