package tvfan.tv.lib;

import tvfan.tv.App;
import android.util.Log;

public class Lg {
	private static boolean debug = App.debug;
	private final static String TAG = "TVFANLOG";

	public static void i(String tag, String msg) {
		if (debug) {
			if (tag == null) {
				tag = TAG;
			}
			if (msg == null) {
				msg = "";
			}
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (debug) {
			if (tag == null) {
				tag = TAG;
			}
			if (msg == null) {
				msg = "";
			}
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (debug) {
			if (tag == null) {
				tag = TAG;
			}
			if (msg == null) {
				msg = "";
			}
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (debug) {
			if (tag == null) {
				tag = TAG;
			}
			if (msg == null) {
				msg = "";
			}
			Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (debug) {
			if (tag == null) {
				tag = TAG;
			}
			if (msg == null) {
				msg = "";
			}
			Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (debug) {
			if (tag == null) {
				tag = TAG;
			}
			if (msg == null) {
				msg = "";
			}
			Log.w(tag, msg, tr);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (debug) {
			if (tag == null) {
				tag = TAG;
			}
			if (msg == null) {
				msg = "";
			}
			Log.e(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (debug) {
			if (tag == null) {
				tag = TAG;
			}
			if (msg == null) {
				msg = "";
			}
			Log.d(tag, msg, tr);
		}
	}
}
