package tvfan.tv.dal;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.PortalFreshEvent;
import tvfan.tv.lib.Lg;

public class HttpRequestWrapper {
	static final private String TAG = "TVFAN.EPG.HttpRequestWrapper";

	private final static int TIMEOUT_MS = 6000;
	private final static int MAX_RETRIES = 2;
	private final static float BACKOFF_MULT = 1.0f;
	private static Context _context;

	private static RequestQueue _reqQueue = null;

	public static void init(Context context) {
		_context = context;
		_reqQueue = Volley.newRequestQueue(_context);
	}

	public static void cancelAll() {
		_reqQueue.stop();
		_reqQueue = Volley.newRequestQueue(_context);
	}

	public static void epgXMLRequestWithCache(String fullurl,
			Response.Listener<XmlPullParser> responseProc,
			Response.ErrorListener errorListener) {
		epgXMLRequest(fullurl, responseProc, errorListener, true);
	}

	public static void epgXMLRequest(String fullurl,
			Response.Listener<XmlPullParser> responseProc,
			Response.ErrorListener errorListener) {
		epgXMLRequest(fullurl, responseProc, errorListener, false);
	}

	private static void epgXMLRequest(String fullurl,
			Response.Listener<XmlPullParser> responseProc,
			final Response.ErrorListener errorListener, boolean isCache) {
		Lg.i(TAG, "Got a epgXMLRequest: " + fullurl);
		XMLRequest xmlRequest = new XMLRequest(fullurl, responseProc,
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Lg.e(TAG, error.getMessage(), error);
						errorListener.onErrorResponse(error);
					}
				});
		xmlRequest.setShouldCache(isCache);
		if (!isCache) {
			_reqQueue.getCache().clear();
		}
		_reqQueue.add(xmlRequest);
		// _reqQueue.start();
		Lg.i(TAG, "Start...");
	}

	public static void epgJSONArrayRequestWithCache(String fullurl,
			Response.Listener<JSONArray> responseProc,
			Response.ErrorListener errorListener) {
		epgJSONArrayRequest(fullurl, responseProc, errorListener, true);
	}

	public static void epgJSONArrayRequest(String fullurl,
			Response.Listener<JSONArray> responseProc,
			Response.ErrorListener errorListener) {
		epgJSONArrayRequest(fullurl, responseProc, errorListener, false);

	}

	private static void epgJSONArrayRequest(String fullurl,
			Response.Listener<JSONArray> responseProc,
			final Response.ErrorListener errorListener, boolean isCache) {
		Lg.i(TAG, "Got a epgJSONArrayRequest: " + fullurl);
		JsonArrayRequest jr = new JsonArrayRequest(fullurl, responseProc,
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Lg.e(TAG, error.getMessage(), error);
						errorListener.onErrorResponse(error);
					}
				});
		jr.setShouldCache(isCache);
		if (!isCache) {
			_reqQueue.getCache().clear();
		}
		_reqQueue.add(jr);
		// _reqQueue.start();
		Lg.i(TAG, "Start...");
	}

	public static void epgJSONRequestWithCache(String fullurl,
			Response.Listener<JSONObject> responseProc,
			Response.ErrorListener errorListener,
			HashMap<String, String> headers) {
		epgJSONRequest(fullurl, responseProc, errorListener, true, headers);
	}

	public static void epgJSONRequest(String fullurl,
			Response.Listener<JSONObject> responseProc,
			Response.ErrorListener errorListener) {
		epgJSONRequest(fullurl, responseProc, errorListener, false, null);
	}

	public static void epgJSONRequest(String fullurl,
			Response.Listener<JSONObject> responseProc,
			Response.ErrorListener errorListener,
			HashMap<String, String> headers) {
		epgJSONRequest(fullurl, responseProc, errorListener, false, headers);
	}

	private static void epgJSONRequest(String fullurl,
			Response.Listener<JSONObject> responseProc,
			final Response.ErrorListener errorListener, boolean isCache,
			final HashMap<String, String> headers) {
		Lg.i(TAG, "Got a epgJSONRequest: " + fullurl);
		JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET,
				fullurl, null, responseProc, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Lg.e(TAG, error.getMessage(), error);
						errorListener.onErrorResponse(error);
					}
				}) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> h = new HashMap<String, String>();
				if (headers != null)
					h.putAll(headers);
				// headers.put("token", AppGlobalVars.getIns().USER_TOKEN);
				// Lg.i(TAG, "headers=" + headers);

				return h;
			}

			@Override
			protected Response<JSONObject> parseNetworkResponse(
					NetworkResponse response) {
				PortalFreshEvent p;
				// 解析后台的header参数用于刷新首页
				HashMap<String, String> headers = (HashMap<String, String>) response.headers;
				String tamp = headers.get("X-Cibn-Received-Millis");
				if (tamp != null) {
					 p = new PortalFreshEvent(tamp);
					EventBus.getDefault().post(p); // 用于首页刷新的时间戳
				}
				return super.parseNetworkResponse(response);
			}
		};
		jr.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES,
				BACKOFF_MULT));
		jr.setShouldCache(isCache);
		// RequestQueue requestQueue = Volley.newRequestQueue(_context);
		// requestQueue.add(jr);
		if (!isCache) {
			// _reqQueue.getCache().remove(fullurl);
			_reqQueue.getCache().clear();
		}
		_reqQueue.add(jr);
		// _reqQueue.start();
		Lg.i(TAG, "Start...");
	}

	/*
	 * Http POST Request
	 */
	public static void epgJSONRequestByPost(String postUrl,
			Response.Listener<JSONObject> responseProc,
			Response.ErrorListener errorListener,
			HashMap<String, String> postData, HashMap<String, String> headers) {
		_epgJSONRequestByPost(postUrl, responseProc, errorListener, postData,
				headers);
	}

	private static void _epgJSONRequestByPost(String postUrl,
			final Response.Listener<JSONObject> responseProc,
			final Response.ErrorListener errorListener,
			final HashMap<String, String> postData,
			final HashMap<String, String> headers) {
		Lg.i(TAG, "Got a epgJSONRequestByPost: " + postUrl);
		StringRequest sr = new StringRequest(Request.Method.POST, postUrl,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							responseProc.onResponse(new JSONObject(response));
						} catch (JSONException e) {
							e.printStackTrace();
							responseProc.onResponse(null);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Lg.e(TAG, error.getMessage(), error);
						errorListener.onErrorResponse(error);
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> map = new HashMap<String, String>();
				map.putAll(postData);
				return map;
			}

			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> _headers = new HashMap<String, String>();
				_headers.put("Accept", "application/json");
				// _headers.put("Content-Type",
				// "application/json; charset=UTF-8");
				_headers.put("Content-Type",
						"application/x-www-form-urlencoded");
				_headers.putAll(headers);
				return _headers;
			}
		};
		sr.setShouldCache(false);
		sr.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES,
				BACKOFF_MULT));
		_reqQueue.getCache().clear();
		_reqQueue.add(sr);
		// _reqQueue.start();
		Lg.i(TAG, "Start...");
	}

	public static void epgJsonObjectRequestByPost(String url,
			JSONObject jsonRequest, final Listener4JSONObject requestListener,
			ErrorListener errorListener) {
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
				url, jsonRequest, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject arg0) {
						requestListener.onResponse(arg0);
					}
				}, errorListener);
		request.setShouldCache(false);
		request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES,
				BACKOFF_MULT));
		_reqQueue.getCache().clear();
		_reqQueue.add(request);
	}

	// private void _epgJSONRequestByPost(String postUrl,
	// Response.Listener<JSONObject> responseProc, final Response.ErrorListener
	// errorListener,
	// HashMap<String, String> postData, final HashMap<String, String> headers)
	// {
	// Lg.i(TAG, "Got a epgJSONRequestByPost: " + postUrl);
	// JSONObject jo = new JSONObject(postData);
	// JsonObjectRequest jr = new JsonObjectRequest(
	// Request.Method.POST,
	// postUrl, jo,
	// responseProc,
	// new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// Lg.e(TAG, error.getMessage(), error);
	// errorListener.onErrorResponse(error);
	// }
	// }){
	// @Override
	// public Map<String, String> getHeaders() {
	// HashMap<String, String> _headers = new HashMap<String, String>();
	// _headers.put("Accept", "application/json");
	// // _headers.put("Content-Type", "application/json; charset=UTF-8");
	// _headers.put("Content-Type", "application/x-www-form-urlencoded");
	// _headers.putAll(headers);
	// return _headers;
	// }
	// };
	// jr.setShouldCache(false);
	// RequestQueue requestQueue = Volley.newRequestQueue(_context);
	// requestQueue.add(jr);
	// requestQueue.getCache().clear();
	// requestQueue.getCache().remove(postUrl);
	// requestQueue.start();
	// Lg.i(TAG, "Start...");
	// }
}
