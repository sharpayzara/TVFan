package tvfan.tv.dal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

public class HttpResponse {
	public interface Listener4JSONObject {
        public void onResponse(JSONObject response);
        public void onError(String httpStatusCode);
    }

	public interface Listener4XmlPullParser {
        public void onResponse(XmlPullParser response);
        public void onError(String errorMessage);
    }

	public interface Listener4JSONArray {
        public void onResponse(JSONArray response) throws JSONException;
        public void onError(String errorMessage);
    }
}
