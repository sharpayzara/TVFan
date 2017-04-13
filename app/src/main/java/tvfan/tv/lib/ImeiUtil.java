package tvfan.tv.lib;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2015/7/15.
 */
public class ImeiUtil {
	private static ImeiUtil instance;
	Context context;
	SharedPreferences sp;

	private ImeiUtil(Context context) {
		this.context = context;
		sp = context.getSharedPreferences("UniqueInfo", 0);
	}

	public static ImeiUtil getInstance(Context context) {
		if (instance == null) {
			instance = new ImeiUtil(context);
		}
		return instance;
	}

	public String getUniqueId() {
		String code = sp.getString("code", "");
		if (TextUtils.isEmpty(code)) {
			initRecord();
			code = sp.getString("code", "");
		}
		return code;
	}

	public void initRecord() {
		String data;
		if (new File(getRecordPath()).exists()) {
			data = readFromFile();
		} else {
			data = getImei() + "," + getAndroidId() + "," + getMacAddress();
			writeToFile(data);
		}
		String[] results = data.split(",");
		int len = results.length;
		String code = "";
		if (len > 0 && !TextUtils.isEmpty(results[0])) {
			code = results[0];
		} else if (len > 1 && !TextUtils.isEmpty(results[1])) {
			code = results[1];
		} else if (len > 2 && !TextUtils.isEmpty(results[2])) {
			code = results[2];
		}
		if (TextUtils.isEmpty(code)) {
			code = getVirtualImei();
		}
		sp.edit().putString("code", code).commit();
	}

	private String getImei() {
		TelephonyManager telemamanger = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telemamanger.getDeviceId();
		if (TextUtils.isEmpty(imei) || imei.startsWith("00000")) {
			return "";
		}
		return imei;
	}

	private String getAndroidId() {
		return Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);
	}

	private String getMacAddress() {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String address = info.getMacAddress();
		return address;
	}

	private static String getVirtualImei() {
		StringBuilder result = new StringBuilder("1234567");
		for (int i = 0; i < 8; i++) {
			result.append((int) (Math.random() * 10));
		}
		return result.toString();
	}

	private String getRecordPath() {
		File temp = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
		if (!temp.exists()) {
			temp.mkdirs();
		}
		return temp.getPath() + "/.matedata";
	}

	private void writeToFile(String data) {
		try {
			File myFile = new File(getRecordPath());
			if (myFile.exists()) {
				myFile.delete();
			}
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(data);
			myOutWriter.close();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String readFromFile() {
		String result = "";
		File myFile = new File(getRecordPath());
		if (myFile.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(myFile);
				InputStreamReader reader = new InputStreamReader(inputStream);
				int length = inputStream.available();
				char[] buffer = new char[length];
				reader.read(buffer);
				result = new String(buffer);
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
