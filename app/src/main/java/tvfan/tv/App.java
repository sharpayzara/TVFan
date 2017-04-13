package tvfan.tv;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.luxtone.lib.gdx.Page;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import crack.cracker.JarCracker;
import tvfan.tv.dal.HttpRequestWrapper;
import tvfan.tv.dal.LocalData;
import tvfan.tv.lib.ACache;
import tvfan.tv.lib.DisplayUtil;
import tvfan.tv.lib.FileUtils;
import tvfan.tv.lib.ImeiUtil;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.StringUtil;

public class App extends com.luxtone.lib.gdx.App {

	private static final String TAG = "App";
	public static int ScreenWidth = 1920;
	public static int ScreenHeight = 1080;
	public static int ScreenDpi = 160;
	public static String DOWNLOADPATH; // 下载文件的目录

	public static String cachePath = "";
	public static long CIBNTAMPMILLS = 0;
	private ACache mCache;

	public static int MESSAGENUMBER = 0; // 用户还没有看过的消息的数量

	public static boolean debug = true;// 是否是debug版本
	
	private static ArrayList<Page>  pages = new ArrayList<Page>();

	@Override
	public void onCreate() {
		super.onCreate();
		final Context mContext = this;
		mCache = ACache.get(this);
		initDownloadPath();
		cachePath = getDiskCacheDir(getApplicationContext());
		initData();
		HttpRequestWrapper.init(getApplicationContext());
		LocalData.initLocalData(getApplicationContext());
		if (getCurProcessName(mContext).equals(getPackageName())) {
			Log.d("App","进入应用了");
			// 崩溃日志
			TalkTvExcepiton.getInstance().init(getApplicationContext());
			Log.d("App", "崩溃日志输出");
			// p2p初始化
			ImeiUtil.getInstance(getApplicationContext()).initRecord();
			String imei = ImeiUtil.getInstance(getApplicationContext())
					.getUniqueId();
			Log.d("App", "ImeiUtil");
			JarCracker.getInstance().init(this);
			Log.d("App","破解初始化");
		}
//		if (getCurProcessName(mContext).contains("liveplay")) {
			loadChannel();
//		}
	}





	// 获取当前进程名
	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}


	private void initDownloadPath() {
		if (getApplicationContext().getExternalFilesDir(null) != null)
			DOWNLOADPATH = getApplicationContext().getExternalFilesDir(null)
					+ "/download";
		else
			DOWNLOADPATH = getApplicationContext().getFilesDir() + "/download";
		if (!FileUtils.isFileExist(DOWNLOADPATH)) {
			FileUtils.creatDir(DOWNLOADPATH);
		}
	}

	private String getDiskCacheDir(Context context) {
		String cachePath = null;
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())
					|| !Environment.isExternalStorageRemovable()) {
				cachePath = context.getExternalCacheDir().getPath();
			} else {
				cachePath = context.getCacheDir().getPath();
			}

		} catch (Exception e) {
			e.printStackTrace();
			cachePath = context.getCacheDir().getPath();
		}
		return cachePath + "/cibn";
	}

	private void loadChannel() {

		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(
							AppGlobalConsts.CHANNELS_FILENAME));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;
			String[] split = Result.split("\"");
			AppGlobalConsts.CHANNELS_ID = split[split.length - 1];
			Lg.i("getAdjustWidthSize", AppGlobalConsts.CHANNELS_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Typeface getFontTypeface() {
		return Typeface.createFromAsset(context.getAssets(), "font/simhei.ttf");
		// return null;
	}

	private void initData() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(dm);
		// 获取当前屏幕的分辨率
		ScreenWidth = dm.widthPixels;
		ScreenHeight = dm.heightPixels;
		// 获取当前屏幕的dp值
		ScreenDpi = dm.densityDpi;
		if (ScreenHeight > 900 && ScreenHeight < 1260) {
			ScreenHeight = 1080;
		} else if (ScreenHeight > 540 && ScreenHeight <= 900) {
			ScreenHeight = 720;
		}

		String s = mCache.getAsString("XCibnReceivedMillis");
		CIBNTAMPMILLS = s != null && StringUtil.isNumeric(s) ? Long.valueOf(s)
				: 0l;

		// wanqi,初始化消息数量，从本地存储读取
		String msg;
		if ((msg = mCache.getAsString("MESSAGENUMBER")) != null) {
			if (TextUtils.isDigitsOnly(msg))
				MESSAGENUMBER = Integer.valueOf(msg);
		}
	}

	/**
	 * 获取转化后的宽度
	 * 
	 * @param width
	 * @return
	 */
	public static int getAdjustWidthSize(float width) {
		float f = ScreenWidth * (width / AppGlobalConsts.APP_WIDTH) + 0.5f;
		// return DisplayUtil.dip2px(context, f);
		// Lg.i("getAdjustWidthSize", (int)f+"");
		return (int) f;
	}

	public static int getAdjustHeightSize(float height) {
		// float f =ScreenHeight * (height / AppGlobalConsts.APP_HEIGHT) + 0.5f;
		float f = ScreenWidth * (height / AppGlobalConsts.APP_WIDTH) + 0.5f;
		// return DisplayUtil.dip2px(context, f);
		return (int) f;
	}

	public static int getAdjustHeightSize2(float height) {
		// float f =ScreenHeight * (height / AppGlobalConsts.APP_HEIGHT) + 0.5f;
		float f = ScreenHeight * (height / AppGlobalConsts.APP_HEIGHT) + 0.5f;
		// return DisplayUtil.dip2px(context, f);
		return (int) f;
	}

	final static Instrumentation instrumentation = new Instrumentation();

	public static void procRemoteCtrl(final String keyCode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				instrumentation.sendKeyDownUpSync(Integer.parseInt(keyCode));
			}
		}).start();
	}

	public static void playFromRemoteCtrl(String moviedetail) {
		// String s =
		// "{\"id\":\"CIBN_70bfdc82-2d23-4bb9-9cba-4947b1d8cfd8\",\"parent_root_item_id\":\"00050000000000000000000000023106\",\"name\":\"分歧者:异类觉醒\",\"showid\":\"7d8bd9f20ee111e3a705\",\"length\":\"139\",\"type\":\"电影\",\"class\":\"科幻\",\"zone\":\"美国\",\"director\":\"尼尔·博格\",\"actor\":\"谢琳·伍德蕾,凯特·温丝莱特,迈尔斯·特勒,提奥·詹姆斯,李美琪,雷·史蒂文森,杰·科特尼,托尼·戈德温,梅奇·费法,佐伊·克拉维兹,艾什莉·贾德,安塞尔·埃尔格特\",\"language\":\"\",\"releaseDate\":\"2014\",\"picurl\":\"http://images.ott.cibntv.net/image/film/android/360x480/11/118C05B377956E601434573.jpg\",\"information\":\"一百年前，席卷全球的战争让几乎所有的国家都从地球上消失，幸存的人类聚集在破败的芝加哥。新世界的创始者为了谋求永久的和平，将幸存者们分成了无私派、诚实派、无畏派、友好派和博学派。每个派系都有自己专门的社会职责，他们通力合作促进这个世界的进步。美丽女孩碧翠丝（谢琳·伍德蕾 饰）出生于无私派家庭，她和其他同龄人一样，将在16岁那年决定自己终身所属的派系。然而测试表明她是一个极为少见的分歧者，碧翠丝不顾测试官的劝阻，毅然选择了无畏派。在这个勇士的集团，她必须和伙伴们接受最为严苛的训练。 　　而她所坚守的秘密，也将成为影响其终身的重要因素。\",\"totalCount\":\"1\",\"price\":\"0\",\"sources\":[{\"id\":\"CIBN_9bc752f7-8303-4026-9246-fffec038d7e7\",\"name\":\"分歧者:异类觉醒\",\"length\":\"139\",\"playlist\":[{\"type\":\"流畅\",\"playurl\":\"http://v.youku.com/v_show/id_XODU1MDE0MjM2.html?k=7fd6ed59ffc6aa8a2bd6adbaabca360f&channel=cibn&t=1435655011&ttl=86400\"}],\"volumncount\":\"1\",\"videoid\":\"XODU1MDE0MjM2\"}]}";
		int playPosition = 0;
		int freePlayTime = 0;

		Bundle bundle = new Bundle();
		bundle.putString("moviedetail", moviedetail);
		try {
			JSONObject movie = new JSONObject(moviedetail);

			int num = movie.getJSONArray("sources").length();
			playPosition = movie.getInt("currentPlayPosition");
			if (playPosition < 0)
				playPosition = -1;
			if (playPosition >= num)
				playPosition = num - 1;

			freePlayTime = movie.getInt("freePlayTime");
			if (freePlayTime < 0)
				freePlayTime = 0;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		bundle.putInt("freePlayTime", freePlayTime);
		bundle.putInt("currentPlayPosition", playPosition);

		Intent intent = new Intent(context,
				tvfan.tv.ui.andr.play.play.Page.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	public static File getAssetsFile(String fileName) {
		File file = null;
		try {
			InputStream is = context.getResources().getAssets().open(fileName);

			file = File.createTempFile("abc", "d");
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1)
				os.write(buffer, 0, bytesRead);

			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static String AppgetControlFromAssets() {
		String result = "";
		try {
			InputStream in = context.getAssets().open("control.html");
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "UTF-8");
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result == "")
			return "";
		return result;
	}

	/*
	 * 自定义Toast
	 */
	public static void mToast(Context context, CharSequence text) {
		mToast(context, text, Toast.LENGTH_SHORT);

	}

	public static void mToast(Context context, CharSequence text, int duration) {
		Toast toast = new Toast(context);
		TextView textView = new TextView(context);
		textView.setBackgroundResource(R.drawable.shape_toast);
		textView.setTextColor(Color.WHITE);
		textView.setPadding(adjustFontSize(50), adjustFontSize(30),
				adjustFontSize(50), adjustFontSize(30));
		textView.setTextSize(adjustFontSize(25));
		textView.setText(text);
		textView.setSingleLine(true);
		toast.setDuration(duration);
		toast.setView(textView);
		toast.show();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

	/**
	 * 适配字体的方法
	 * 
	 * @param textSize
	 * @return
	 */
	public static int adjustFontSize(float textSize) {
		int screenWidth = ScreenWidth > ScreenHeight ? ScreenWidth
				: ScreenHeight;
		textSize = DisplayUtil.px2sp(context, textSize);
		int rate = (int) (textSize * (float) screenWidth / 1280); // 我自己测试这个倍数比较适合，当然你可以测试后再修改
		return rate < 15 ? 15 : rate; // 字体太小也不好看的
	}
	
	public static void addPage(Page page){
		pages.add(page);
	}
	
	public static void removePage(Page page){
		pages.remove(page);
	}
	
	public static void removeAllPages(){
		int count = pages.size();
		for(int i=0; i<count; i++){
			pages.get(i).finish();
		}
	}
	
}
