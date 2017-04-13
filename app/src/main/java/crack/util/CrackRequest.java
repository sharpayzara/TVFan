package crack.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.util.Log;

public class CrackRequest {
	/**
	 * 构造请求串
	 * 
	 * @param url
	 * @param type
	 * @return
	 */
	public static String getRequestString(String url, int type) {
		String tmp = "http://crack.tvfan.cn:8380/CrackVideo?url=";
		try {
			tmp += URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		long time = System.currentTimeMillis() / 1000;
		tmp += "&time=" + time;
		String key = MD5
				.GetMD5Code(url + "_tvfan_" + time + "ijklrejiosjfdlir");
		tmp += "&key=" + key;
		tmp += "&type=" + type;
		Log.i("CrackRequest", "request url:" + tmp);
		return tmp;
	};
}
