package tvfan.tv.daemon;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.LocalData;
import tvfan.tv.lib.AppLauncher;
import tvfan.tv.lib.FileUtils;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.MD5FileUtil;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

public class DownloadService extends Service {

	private static final String TAG = "TVFAN.EPG.DownloadService";
	private BroadcastReceiver _br = null;
	private BroadcastReceiver _lbr = null;
	private LocalData _ld = null;
	private final String _prefixPos = "DLPOS";
	private final String _prefixStat = "DLSTAT";
	public static HashMap<String, String> downloadStack = new HashMap<String, String>();

	@Override
	public IBinder onBind(Intent arg0) {
		Lg.i(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Lg.i(TAG, "onCreate");
		super.onCreate();
		_ld = new LocalData(getApplicationContext());
		_regLocalReceiver();
		// _regDLReceiver();
	}

	@Override
	public void onDestroy() {
		Lg.i(TAG, "onDestroy");
		if (_br != null)
			unregisterReceiver(_br);
		_br = null;

		if (_lbr != null)
			unregisterReceiver(_lbr);
		_lbr = null;

		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Lg.i(TAG, "onStart");
	}

	// private void _regDLReceiver() {
	// if (_br != null)
	// unregisterReceiver(_br);
	//
	// _br = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context arg0, Intent arg1) {
	// _chkDownloadTask();
	// }
	// };
	// registerReceiver(_br, new IntentFilter(
	// DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	// }

	private void _regLocalReceiver() {
		_lbr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				_addTask(intent);
			}
		};
		registerReceiver(_lbr, new IntentFilter(
				AppGlobalConsts.LOCAL_MSG_FILTER.DOWNLOAD_SERVICE.toString()));
	}

	// @SuppressLint("NewApi")
	// private void _chkDownloadTask() {
	// ContentValues tasks = _ld.getKVbyPrefix(_prefixTask);
	// for (Entry<String, Object> item : tasks.valueSet()) {
	// DownloadManager.Query query = new DownloadManager.Query();
	// long i = Long.parseLong((String) item.getValue());
	// query.setFilterById(i);
	// Cursor cur = _dlmgr.query(query);
	// if (cur.moveToFirst()) {
	// int status = cur.getInt(cur
	// .getColumnIndex(DownloadManager.COLUMN_STATUS));
	// String title = cur.getString(cur
	// .getColumnIndex(DownloadManager.COLUMN_TITLE));
	// Lg.i(TAG,
	// "DownloadManager.COLUMN_STATUS:"
	// + String.valueOf(status));
	// if (status == DownloadManager.STATUS_SUCCESSFUL) {
	// Lg.i(TAG, "DownloadManager.STATUS_SUCCESSFUL");
	// Toast.makeText(getApplicationContext(),
	// "[" + title + "]，下载完毕", Toast.LENGTH_LONG).show();
	//
	// AppLauncher appl = new AppLauncher(getApplicationContext());
	// appl.startInstall(cur.getString(cur
	// .getColumnIndex(DownloadManager.COLUMN_URI)));
	// _ld.setKV(item.getKey().replace(_prefixTask, _prefixStat),
	// "1");
	// } else if (status == DownloadManager.STATUS_RUNNING) {
	// Lg.i(TAG, "DownloadManager.STATUS_RUNNING");
	// } else {
	// _dlmgr.remove(i);
	// _ld.removeKV(item.getKey());
	// _ld.removeKV(item.getKey()
	// .replace(_prefixTask, _prefixStat));
	// Toast.makeText(getApplicationContext(),
	// "[" + title + "]，下载中断，请与我们联系", Toast.LENGTH_LONG)
	// .show();
	// }
	// }
	// }
	// }

	@SuppressLint("NewApi")
	private void _addTask(Intent intent) {
		if (!FileUtils.isFileExist(App.DOWNLOADPATH)) {
			FileUtils.creatDir(App.DOWNLOADPATH);
		}
		final String md5 = intent.getStringExtra("md5");
		final String pkname = intent.getStringExtra("pkname");
		String stat = _queryTaskStat(pkname);
		final String appName = intent.getStringExtra("name");
		if (stat != null) {
			Toast.makeText(getApplicationContext(),
					"[" + appName + "] 正在下载,请稍候", Toast.LENGTH_LONG).show();
			return;
		}

		final String url = intent.getStringExtra("url").trim();
		Uri uri = Uri.parse(url);
		String fn = uri.getLastPathSegment();

		// wanqi,使用新的下载方式，不再使用downloadmanager
		FinalHttp fh = new FinalHttp();
		final String fileName = App.DOWNLOADPATH + "/" + fn;
		// wanqi,url.trim()，去掉收尾空格，后台传过来的地址有可能不规范
		fh.download(url.trim(), fileName, new DownloadCallBack() {
			@Override
			public void onStart() {
				super.onStart();
				Lg.i(TAG, fileName + "----- 开始下载.");
				Toast.makeText(getApplicationContext(),
						"[" + appName + "] 已开始下载", Toast.LENGTH_LONG).show();
				_ld.setKV(_prefixStat + pkname, AppGlobalConsts.DOWNLOAD_START); // 下载的状态
				downloadStack.put(pkname, "downloading");
			}

			@Override
			public void onSuccess(File t) {
				super.onSuccess(t);
				Lg.i(TAG, fileName + "----- 下载完成.");
				Toast.makeText(getApplicationContext(),
						"[" + appName + "]，下载完毕", Toast.LENGTH_LONG).show();
				_ld.setKV(_prefixStat + pkname,
						AppGlobalConsts.DOWNLOAD_SUCCESS);
				_ld.setKV(_prefixPos + pkname, fileName);
				AppLauncher appl = new AppLauncher(getApplicationContext());

				try {
					String localMD5 = MD5FileUtil.getFileMD5String(new File(
							fileName));
					// wanqi,md5值为空时，不进行校验，直接安装
					if (TextUtils.isEmpty(md5)) {
						Lg.i(TAG, fileName + "----- md5值 为空，不进行md5值检验.");
						appl.startInstall(fileName);
					} else {
						if (localMD5.equalsIgnoreCase(md5)) {
							Lg.i(TAG, fileName + "----- md5值校验通过.");
							appl.startInstall(fileName);
						} else {
							Lg.i(TAG, fileName + "----- md5值校验失败，无法安装.");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				downloadStack.remove(pkname);
			}

			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
				_ld.setKV(_prefixStat + pkname,
						AppGlobalConsts.DOWNLOAD_LOADING);
				this.count = count;
				this.current = current;
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				Lg.e(TAG, fileName + "----- 下载失败. errorMsg : " + strMsg);
				Toast.makeText(getApplicationContext(),
						"[" + appName + "] 下载失败,请联系客服", Toast.LENGTH_LONG)
						.show();
				_ld.setKV(_prefixStat + pkname, AppGlobalConsts.DOWNLOAD_FAILED);
				downloadStack.remove(pkname);
			}

		});

		// try {
		// DownloadManager.Request request = new DownloadManager.Request(uri);
		// MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		// String mimeString = mimeTypeMap
		// .getMimeTypeFromExtension(MimeTypeMap
		// .getFileExtensionFromUrl(uri.toString()));
		// request.setMimeType(mimeString);
		// request.setVisibleInDownloadsUi(false);
		// request.setTitle(appName);
		// request.setDestinationInExternalPublicDir("/cibn/", fn);
		// long downloadID = _dlmgr.enqueue(request);
		// _ld.setKV(_prefixStat + pkname, "0");
		// _ld.setKV(_prefixTask + pkname, String.valueOf(downloadID));
		// Toast.makeText(getApplicationContext(), "[" + appName + "] 已开始下载",
		// Toast.LENGTH_LONG).show();
		// } catch (IllegalArgumentException e) {
		// // TODO: handle exception
		// Lg.i(TAG, "onStart+只能下载http||https链接");
		// }

	}

	private String _queryTaskStat(String pkname) {
		return _ld.getKV(_prefixStat + pkname);
	}

	// private void _sendLocalMsg(long now) {
	// Lg.i(TAG, "_sendLocalMsg::now=" + String.valueOf(now));
	// Intent intent = new Intent(
	// AppGlobalConsts.LOCAL_MSG_FILTER.DATETIME_UPDATE.toString());
	// intent.putExtra("now", now);
	// this.sendStickyBroadcast(intent);
	// }

	class DownloadCallBack extends AjaxCallBack<File> {
		public long count;
		public long current;
	}
}
