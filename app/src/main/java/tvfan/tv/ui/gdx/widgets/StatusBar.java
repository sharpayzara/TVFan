package tvfan.tv.ui.gdx.widgets;

import java.text.SimpleDateFormat;
import java.util.Date;

import tvfan.tv.lib.Lg;
import android.annotation.SuppressLint;
import android.net.ConnectivityManager;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Page;

public class StatusBar extends Group {

	private final String TAG = "TVFAN.EPG.StatusBar";

	private Label _time;
//	private Label _date;
//	private Label _week;
	private Image _wifi;

	private int _x = 0, _y = 0;

	public StatusBar(final Page page) {
		super(page);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				_wifi = new Image(page);
				_wifi.setDrawableResource(R.drawable.wifi);
				_wifi.setPosition(_x, _y + 10);
				_wifi.setSize(49, 40);
				addActor(_wifi);

				_x += 55;
				_time = new Label(page);
				_time.setPosition(_x, _y + 12);
				_time.setSize(80, 36);
				_time.setTextSize(36);
				_time.setColor(Color.WHITE);
				_time.setText(":");
//				_time.setAlpha(0.6f);
				addActor(_time);

//				_x += 108;
//				_date = new Label(page);
//				_date.setPosition(_x, _y + 25);
//				_date.setSize(40, 17);
//				_date.setTextSize(17);
//				_date.setColor(Color.WHITE);
//				_date.setText("-");
//				_date.setAlpha(0.6f);
//				addActor(_date);

//				_week = new Label(page);
//				_week.setPosition(_x, _y + 8);
//				_week.setSize(40, 17);
//				_week.setTextSize(17);
//				_week.setColor(Color.WHITE);
//				_week.setText("-");
//				_week.setAlpha(0.6f);
//				addActor(_week);
			}
		});
	}

	public void setWifi(final boolean isConnected, final int netType) {
		Lg.i(TAG, "setWifi::isConnected=" + String.valueOf(isConnected));
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (netType == ConnectivityManager.TYPE_ETHERNET) {// 有线
					if (isConnected)
						_wifi.setDrawableResource(R.drawable.lan_conn_icon);
					else
						_wifi.setDrawableResource(R.drawable.lan_disconn_icon);
				} else {// 无线或者其他
					if (isConnected)
						_wifi.setDrawableResource(R.drawable.wifi);
					else
						_wifi.setDrawableResource(R.drawable.wifi_err);
				}
			}
		});
	}

	/*
	 * 设置wifi信号等级
	 */
	public void setWifiLevel(final int level) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (level <= 0 && level >= -50) {
					_wifi.setDrawableResource(R.drawable.wifi);
				} else if (level < -50 && level >= -70) {
					_wifi.setDrawableResource(R.drawable.wifi);
				} else if (level < -70 && level >= -80) {
					_wifi.setDrawableResource(R.drawable.wifi);
				} else if (level < -80 && level >= -100) {
					_wifi.setDrawableResource(R.drawable.wifi);
				} else {
					_wifi.setDrawableResource(R.drawable.wifi_err);
				}
			}
		});

	}

	public void setDatetime(final long now) {
		Gdx.app.postRunnable(new Runnable() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				Date d = new Date(now);
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				_time.setText(sdf.format(d));

				sdf = new SimpleDateFormat("EEEE");
//				_week.setText(sdf.format(d));

				sdf = new SimpleDateFormat("M月d日");
//				_date.setText(sdf.format(d));
			}
		});
	}
}
