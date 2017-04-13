package tvfan.tv;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.text.format.Time;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 
 * @author yanzhidan
 * @time 2013-10-9 11:50:06
 * 
 */
public class TalkTvExcepiton implements UncaughtExceptionHandler {

	private Thread.UncaughtExceptionHandler defaultExceptionHandler;
	// 单例声明CustomException;
	private static TalkTvExcepiton appException;
	private Context context;

	private TalkTvExcepiton() {

	}

	public static TalkTvExcepiton getInstance() {

		if (appException == null) {
			appException = new TalkTvExcepiton();
		}
		return appException;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		StringBuffer sb = new StringBuffer();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			String path = null;
			if (defaultExceptionHandler != null) {
				String state = Environment.getExternalStorageState();
				// 判断SdCard是否存在并且是可用的
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					path = Environment.getExternalStorageDirectory().toString();
				}
				// 创建一个logcat目录
				path = path + "/"
						+ context.getResources().getString(R.string.app_name)
						+ "/Exception";
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
				// 删除过期文件
				deleteOldFile(path);
				String time = getCurrentTime();
				String fileName = time.substring(0, 9);
				File myFile = new File(path + "/" + fileName + ".log");
				String str = "\n" + time + "-->" + "\n";
				FileOutputStream fos = new FileOutputStream(myFile, true);
				fos.write(str.getBytes());
				fos.write(sb.toString().getBytes());
				String exception = "equipName:" + Build.MODEL;
				fos.write(exception.getBytes());
				fos.flush();
				fos.close();
//				restartApp();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			defaultExceptionHandler.uncaughtException(thread, ex);
		}

	}

	/**
	 * 获得当前时间
	 * 
	 * @return
	 */
	public String getCurrentTime() {

		Time t = new Time();
		t.setToNow();
		int year = t.year;
		int month = t.month + 1;
		int day = t.monthDay;
		int hour = t.hour;
		int minute = t.minute;
		int second = t.second;
		String time = year + "-" + month + "-" + day + " " + hour + ":"
				+ minute + ":" + second;
		return time;

	}

	public void init(Context context) {
		this.context = context;
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public void deleteOldFile(final String path) {

		File file = new File(path);
		file.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {

				File file = new File(path + "/" + filename);
				Long ago = file.lastModified();
				Long now = System.currentTimeMillis();
				// 如果最后一次修改时间超过一年：3153600秒
				if ((now - ago) > 31536000) {
					file.delete();
				}
				return false;
			}
		});

	}

	/**
	 * 重启应用
	 */
	public void restartApp(){
		Intent intent = new Intent(context, Bootloader.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		MobclickAgent.onKillProcess(context);
		android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
	}

}
