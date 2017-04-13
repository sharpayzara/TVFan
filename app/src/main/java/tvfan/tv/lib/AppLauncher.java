package tvfan.tv.lib;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.daemon.DownloadService;
import tvfan.tv.dal.LocalData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class AppLauncher {
	private final String TAG = "TVFAN.EPG.AppLauncher";
	private final String _prefixPos = "DLPOS";
	private final String _prefixStat = "DLSTAT";

	private Context _context = null;

	public AppLauncher(Context context) {
		_context = context;
	}

	public boolean appIsExist(String packageName) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager packageManager = _context.getPackageManager();

		List<ResolveInfo> apps = packageManager.queryIntentActivities(
				mainIntent, 0);
		for (ResolveInfo res : apps) {
			String pkg = res.activityInfo.packageName;
			if (pkg.equalsIgnoreCase(packageName)) {
				return true;
			}
		}
		return false;
	}

	public boolean startAppByPackageName(String packageName, Bundle params) {
		try {
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			PackageManager packageManager = _context.getPackageManager();

			List<ResolveInfo> apps = packageManager.queryIntentActivities(
					mainIntent, 0);

			String[] pks = packageName.split("/");
			for (ResolveInfo res : apps) {
				String pkg = res.activityInfo.packageName;
				if (pkg.equalsIgnoreCase(pks[0])) {
					String cls = (pks.length == 1) ? res.activityInfo.name
							: pks[1];
					// wanqi,后台传classname时，就利用此类名来启动
					if (params != null && params.containsKey("classname")) {
						cls = params.getString("classname");
					}
					ComponentName componet = new ComponentName(pkg, cls);
					Intent intent = new Intent();
					if (params == null) {
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setAction(pks[0]);
					} else {
						intent.putExtras(params);
						intent.setComponent(componet);
					}
					LocalData ld = new LocalData(_context);
					ld.removeKV(_prefixStat + packageName);
					Lg.i(TAG, "启动第三方应用 : " + componet.toString());
					_context.startActivity(intent);
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void startProccess(JSONObject actionParam) {
		String pkName = "";
		String appName = "";
		String url = "";
		String md5 = "";
		try {
			pkName = actionParam.getString("packageName");
			appName = actionParam.getString("name");
			url = actionParam.getString("downloadUrl").toString();
			if (actionParam.has("params")) {
				JSONArray ja = actionParam.getJSONArray("params");
				for (int i = 0; i < ja.length(); i++) {
					if (ja.getJSONObject(i).getString("name").toString()
							.equalsIgnoreCase("md5")) {
						md5 = ja.getJSONObject(i).getString("value").toString();
						break;
					}

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		final String appName1 = appName;
		LocalData ld = new LocalData(_context);
		String stat = ld.getKV(_prefixStat + pkName);
		String path = ld.getKV(_prefixPos + pkName);
		File f = null;
		if (path != null) {
			f = new File(path);
		}
		// 只有当当前下载栈中不存在此包的下载任务并且下载状态不是完成的状态，就默认前一个下载任务失败.比如：下载过程中，突然断电就会导致此现象
		if (stat != null
				&& !stat.equalsIgnoreCase(AppGlobalConsts.DOWNLOAD_SUCCESS)
				&& (DownloadService.downloadStack == null || !DownloadService.downloadStack
						.containsKey(pkName))) {
			stat = AppGlobalConsts.DOWNLOAD_FAILED;
		}
		if (stat == null) {
			// ld.setKV(_prefixStat+pkName, "0");
			_startDownload(appName, pkName, url, md5);
			return;
		}
		if (stat.equalsIgnoreCase(AppGlobalConsts.DOWNLOAD_START)
				|| stat.equalsIgnoreCase(AppGlobalConsts.DOWNLOAD_LOADING)) {
			Handler aHandler = new Handler(_context.getMainLooper()) {
				@Override
				public void handleMessage(android.os.Message msg) {
					super.handleMessage(msg);
				};
			};
			aHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(_context, "[" + appName1 + "] 正在下载，请稍候",
							Toast.LENGTH_LONG).show();
				}
			});
			// try{
			// Toast.makeText(_context, "[" + appName + "] 正在下载，请稍候",
			// Toast.LENGTH_LONG).show();
			// }catch(Exception err){
			// Lg.e(TAG, err.getMessage());
			// }
		} else if (stat.equalsIgnoreCase(AppGlobalConsts.DOWNLOAD_SUCCESS)) {
			if (f != null && f.exists()) {
				String localMD5;
				try {
					localMD5 = MD5FileUtil.getFileMD5String(f);
					//wanqi,md5值为空时，不进行校验，直接安装
					if (TextUtils.isEmpty(md5)) {
						Lg.i(TAG, "md5值为空，不进行md5值校验");
						startInstall(url);
						return;
					} else {
						if (localMD5.equalsIgnoreCase(md5)) {
							Lg.i(TAG, "md5值校验通过");
							startInstall(url);
							return;
						} else {
							Lg.i(TAG, "md5值校验失败，将重新下载.");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ld.removeKV(_prefixStat + pkName);
			ld.removeKV(_prefixPos + pkName);
			_startDownload(appName, pkName, url, md5);

		} else if (stat.equalsIgnoreCase(AppGlobalConsts.DOWNLOAD_FAILED)) {
			ld.removeKV(_prefixStat + pkName);
			ld.removeKV(_prefixPos + pkName);
			_startDownload(appName, pkName, url, md5);
		}
	}

	private void _startDownload(String appName, String pkName, String url,
			String md5) {
		Intent intent = new Intent(
				AppGlobalConsts.LOCAL_MSG_FILTER.DOWNLOAD_SERVICE.toString());
		intent.putExtra("name", appName);
		intent.putExtra("url", url);
		intent.putExtra("pkname", pkName);
		intent.putExtra("md5", md5);
		_context.sendBroadcast(intent);
	}

	public void startInstall(String apkuri) {
		Uri local = Uri.parse("file://" + App.DOWNLOADPATH + "/"
				+ Uri.parse(apkuri).getLastPathSegment().trim());
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(local,
				"application/vnd.android.package-archive");
		installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		_context.startActivity(installIntent);
	}
}
