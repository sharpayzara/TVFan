package tvfan.tv.dal;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

	private static final String TAG = "JSONParser";

	private JSONObject jsonObj;
	private HashMap<String, Object> cacheMap = new HashMap<String, Object>();

	public JSONParser(JSONObject obj) {
		jsonObj = obj;
		parserJson();
	}

	public JSONParser(String jsonString) {
		try {
			jsonObj = new JSONObject(jsonString);
			parserJson();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// path是要调用的字段值在json中的“位置”，如sources_0_playlist_1_videoid :
	// 就是第一集第二个播放地址的videoid
	public String getString(String path) {
		if (cacheMap.containsKey(path))
			return (String) cacheMap.get(path);
		else
			return "";
	}

	public int getInt(String path) {
		if (cacheMap.containsKey(path))
			return Integer.valueOf(cacheMap.get(path).toString());
		else
			return -1;
	}

	public boolean getBooelan(String path) {
		if (cacheMap.containsKey(path))
			return Boolean.valueOf(cacheMap.get(path).toString());
		else
			return false;
	}

	public int getArrayListLength(String path) {
		if (cacheMap.containsKey(path)) {
			JSONArray array = (JSONArray) cacheMap.get(path);
			return array.length();
		} else
			return -1;
	}

	public void clear() {
		cacheMap.clear();
	}

	private void parserJson() {
		parserJson("", jsonObj);
	}

	private void parserJson(String path, JSONObject obj) {
		Iterator<String> iterator = obj.keys();
		String p = path;
		while (iterator.hasNext()) {
			String str = (String) iterator.next();
			Object o = obj.opt(str);
			if (p.equals("")) {
				p = str;
			} else {
				p = p + "_" + str;
			}
			//Lg.i(TAG, "[K,V] = [" + p + "," + o.toString() + "]");
			cacheMap.put(p, o);

			if (o instanceof JSONObject) {
				parserJson(p, (JSONObject) o);
			} else if (o instanceof JSONArray) {
				parserJsonArray(p, (JSONArray) o);
			}

			p = path;

		}
	}

	private void parserJsonArray(String path, JSONArray array) {
		try {
			String p = path;
			for (int i = 0; i < array.length(); i++) {
				Object obj = array.get(i);
				p = p + "_" + i;
				//Lg.i(TAG, "[K,V] = [" + p + "," + obj.toString() + "]");
				cacheMap.put(p, obj);

				if (obj instanceof JSONObject) {
					parserJson(p, (JSONObject) obj);
				} else if (obj instanceof JSONArray) {
					parserJsonArray(p, (JSONArray) obj);
				}
				p = path;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
