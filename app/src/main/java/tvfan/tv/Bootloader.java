package tvfan.tv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.ACache;
import tvfan.tv.lib.FileUtils;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.MD5FileUtil;
import tvfan.tv.lib.StringUtil;
import tvfan.tv.lib.UpdateUtils;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.andr.widgets.CustomTipDialog;
import tvfan.tv.ui.andr.widgets.UpdateDialog;

public class Bootloader extends Activity implements UpdateUtils.CheckUpdateOverListener{

	private final String TAG = "TVFAN.EPG.Bootloader";

	LocalData _ld = null;
	ImageView _biv = null;

	UpdateUtils updateUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bootloader);
		_biv = (ImageView) this.findViewById(R.id.bootimgview);
		_ld = new LocalData(null);
		_loadBootImg();
		handleUpdate();
		_deviceLogin();
	}

	private void handleUpdate() {
		updateUtils = new UpdateUtils(this, this);
		updateUtils.checkFirstLoginAfterUpdate();
		updateUtils.checkUpd();
	}

	@Override
	public void checkUpdateOver() {
		_deviceLogin();
	}

	@Override
	public void updateDialogOnCancel() {
		this.finish();
	}

	@Override
	protected void onDestroy() {
		if (_biv != null)
			_biv.setImageResource(0);
		System.gc();
		super.onDestroy();
	}

	private void _loadBootImg() {
		String imgurl = _ld.getKV(AppGlobalConsts.PERSIST_NAMES.BOOT_IMAGE
				.name());

		 if (TextUtils.isEmpty(imgurl)){
			 
			 _biv.setImageBitmap(Utils.readBitMap(this, R.drawable.bootimg));
		 }
		 else {
		 File f = new File(imgurl);
		 if (f.exists())
		 _biv.setImageBitmap(BitmapFactory.decodeFile(imgurl));
		 else{
			 
			 _biv.setImageBitmap(Utils.readBitMap(this, R.drawable.bootimg));
		 }
		 }
	}

	private void _deviceLogin() {
		if (_isRelogin()) {
			RemoteData rd = new RemoteData(this);
			rd.startDeviceLogin(tvfan.tv.lib.Utils.getEthernetMac(),
					new HttpResponse.Listener4JSONObject() {
						@Override
						public void onResponse(JSONObject response) {
							if (response == null) {
								_showToast("");
								return;
							}

							try {
								if (response.get("resultCode").equals("0")) {
									if (response.getString("state").equals(
											"1111")) {
										_ld.setKV(AppGlobalConsts.DEV_LOGIN_INFO,
												response.toString());
										_ld.setKV(AppGlobalConsts.DEV_LOGIN_TIME, String
												.valueOf(System
														.currentTimeMillis()));
										_gotoPortal();
										return;
									}
									else {
										_showToast(response.getString("state"));
								} 
									}else {
									_showToast("服务端接口异常");
								}
							} catch (JSONException e) {
								e.printStackTrace();
								Lg.e(TAG, e.getMessage());
							}
							_ld.removeKV(AppGlobalConsts.DEV_LOGIN_TIME);
						}

						@Override
						public void onError(String errorMessage) {
							_ld.removeKV(AppGlobalConsts.DEV_LOGIN_TIME);
							_showToast(errorMessage);

						}
					});
		} else {
			Lg.i(TAG, "_deviceLogin from local");
			_gotoPortal();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private boolean _isRelogin() {
		String dt = _ld.getKV(AppGlobalConsts.DEV_LOGIN_TIME);
		if (TextUtils.isEmpty(dt))
			return true;

		Date d1 = new Date(Long.parseLong(dt));
		Date d2 = new Date(System.currentTimeMillis());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return !sdf.format(d1).equals(sdf.format(d2));
	}

	private void _showToast(String exText) {
		CustomTipDialog dialog = null;
		CustomTipDialog.Builder customBuilder = new CustomTipDialog.Builder(
				this);
		customBuilder
				.setTitle("提示")
				.setMessage(exText)
				.setStyle(0)
				.setNegativeButton("退出", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						System.exit(0);
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				})
				.setPositiveButton("重试", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
//						martchOn();
						updateUtils.checkUpd();
					}

				});
		dialog = customBuilder.create();
		dialog.setCancelable(false);
		dialog.show();
	}

	private void _gotoPortal() {
		final Activity _self = this;
		final Intent inte = _self.getIntent();
		final Intent intent = new Intent(_self, EntryPoint.class);
		intent.putExtras(inte);
		// long timeout = 3000l;
		long timeout = 0l;
		if (inte.getStringExtra("action") == null) {
			timeout = 3000l;
			// 获取后台配置的开机画面时间
			String timespan = _ld
					.getKV(AppGlobalConsts.PERSIST_NAMES.BOOT_TIMESPAN.name());
			if (!TextUtils.isEmpty(timespan) && StringUtil.isNumeric(timespan)) {
				timeout = Integer.valueOf(timespan);
				if (timeout < 2)
					timeout = 2;
				else if (timeout > 30)
					timeout = 30;
				timeout *= 1000l;
			}

		}
		(new Handler()).postDelayed(new Runnable() {
			@Override
			public void run() {
				_self.startActivity(intent);
				_self.finish();
			}
		}, timeout);
	}
}
