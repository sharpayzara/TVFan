package tvfan.tv.ui.andr.widgets;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.LocalData;
import tvfan.tv.lib.ACache;
import tvfan.tv.lib.FileUtils;
import tvfan.tv.lib.MD5FileUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;

public class UpdateDialog {
	private final boolean isForce;
	private Context mctx;
	private CustomDialog dialog;

	public interface UpdateDialogListener {
        public void onDismiss();
		public void onOk();
    }

	public UpdateDialog(Context mctx, boolean isForce) {
		super();
		this.mctx = mctx;
		this.isForce = isForce;
	}


	public void UpdateConfirm(final UpdateDialogListener updateDialogListener) {
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(mctx);
		
		String vn = new LocalData(mctx.getApplicationContext()).getKV("VersionName");
		String updateMessage = new LocalData(mctx.getApplicationContext()).getKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MESSAGE.name());
		if (TextUtils.isEmpty(updateMessage)) {
			updateMessage = "修复了若干bug";
		}
		try {
			customBuilder
					.setShowProgress(isForce)
					.setTitle("发现新版本")
					.setMessage2(mctx.getApplicationContext().getPackageManager().getPackageInfo(mctx.getApplicationContext().getPackageName(),
							PackageManager.GET_CONFIGURATIONS).versionName + "")
					.setMessage1(vn)
					.setUpdateMessage(updateMessage)
					.setNegativeButton("下次再说",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int which) {
									updateDialogListener.onDismiss();
									dialog.dismiss();
								}
							})
					.setPositiveButton("立即升级",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
//									if (!isForce) {
//										//安装apk
//										LocalData ld = new LocalData(mctx.getApplicationContext());
//										String uri = "file://"+ld.getKV("UpdateAdress");
//										installApk(uri);
//										ACache.get(mctx).remove("epg_portal_navBar");
//										updateDialogListener.onDismiss();
//										dialog.dismiss();
//									} else {
										updateDialogListener.onOk();
//										dialog.dismiss();
//									}
								}

							});
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialog = customBuilder.create();
		dialog.getWindow().setLayout(900, 500);
//		dialog.getWindow().setLayout(
//				((WindowManager)mctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 2,
//				((WindowManager)mctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() / 2);
		dialog.setCancelable(false);
		dialog.show();
	}
//
//	private void installApk(String uri) {
//		Intent installIntent = new Intent(
//				Intent.ACTION_VIEW);
//		installIntent.setDataAndType(Uri.parse(uri),
//				"application/vnd.android.package-archive");
//		installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//				| Intent.FLAG_ACTIVITY_NEW_TASK);
//		mctx.startActivity(installIntent);
//	}

	public void initProgressBar() {
//		dialog.getProgressBar().setIndeterminate(false);
		dialog.getProgressBar().setMax(100);
	}

	public void setProgress(int progress) {
		dialog.getProgressBar().setProgress(progress);
	}

	public void dismiss() {
		dialog.dismiss();
	}
}
