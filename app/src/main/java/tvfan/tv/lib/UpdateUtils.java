package tvfan.tv.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.ui.andr.widgets.UpdateDialog;

/**
 * Created by zhangyisu on 2016/5/15.
 * <p/>
 * 刚进入应用时使用的升级相关工具类
 */

public class UpdateUtils {

    private final String TAG = "UpdateUtils";
    private final String _is_first_login = "isFirstLogin"; // 是否是升级之后第一次登录

    private String upgradePattern = null;
    private String curVersionCode = null;
    private String updateVersionCode = null;
    private String packageLocation = null;
    private String updateMd5 = null;
    private String UpdateVersionName = null;
    private String updateMessage = null;

    private Context context;
    private CheckUpdateOverListener listener;
    private LocalData ld;

    public UpdateUtils(Context context, CheckUpdateOverListener listener) {
        this.context = context;
        this.listener = listener;
        ld = new LocalData(context);
    }

    public interface CheckUpdateOverListener {
        void checkUpdateOver();

        void updateDialogOnCancel();
    }

    /*
     * 检查是否有已经下载好的待更新包
	 */
    public void checkUpd() {
        String rfu = ld.getKV(AppGlobalConsts.PERSIST_NAMES.READY_FOR_UPDATE
                .name());
        // 有
        if (!TextUtils.isEmpty(rfu) && rfu.equals("1")) {
            String vc = ld
                    .getKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_CODE
                            .name());

            try {
                curVersionCode = context.getPackageManager()
                        .getPackageInfo(
                                context.getPackageName(),
                                PackageManager.GET_CONFIGURATIONS).versionCode
                        + "";
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                curVersionCode = null;
            }

            if (!TextUtils.isEmpty(vc) && curVersionCode != null
                    && Integer.parseInt(vc) <= Integer.parseInt(curVersionCode)) {
                clearUpdateData();
                listener.checkUpdateOver();
                Log.i(TAG, "无需升级");
                return;
            }

            final UpdateDialog ud = new UpdateDialog(context, false);
            if (isValid()) {
                final String upgradePattern = ld.getKV(AppGlobalConsts.PERSIST_NAMES.UPGRADE_PATTERN
                        .name());

                ud.UpdateConfirm(new UpdateDialog.UpdateDialogListener() {
                    @Override
                    public void onDismiss() {
                        if (!TextUtils.isEmpty(upgradePattern) && upgradePattern.equals("1")) {
                            listener.updateDialogOnCancel();
                        } else {
                            listener.checkUpdateOver();
                        }
                    }

                    @Override
                    public void onOk() {
                        ld.removeKV(_is_first_login);
                        String uri = ld.getKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_ADRESS.name());
                        installApk(uri);
                        ud.dismiss();
//                        Bootloader.this.finish();
                        listener.updateDialogOnCancel();
                        System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
//				return true;
            } else {
                // 清空数据
                clearUpdateData();
                listener.checkUpdateOver();
            }
        } else { // 没有，进入强制升级的判断逻辑
            checkForceUpdate();
        }

    }

    /**
     * 检测是否需要强制升级
     */
    private void checkForceUpdate() {

        try {
            if (curVersionCode == null)
                curVersionCode = context.getApplicationContext().getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(),
                        PackageManager.GET_CONFIGURATIONS).versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            new RemoteData(context.getApplicationContext()).getUpdateVersion(new HttpResponse.Listener4JSONObject() {

                @Override
                public void onResponse(JSONObject response) {
                    if (response == null) {
                        Lg.e(TAG, "ERROR");
                        listener.checkUpdateOver();
                        return;
                    }

                    JSONObject object = null;
                    try {
                        JSONArray ml = response.getJSONArray("data");
                        for (int i = 0; i < ml.length(); i++) {
                            object = ml.getJSONObject(i);
                            upgradePattern = object.getString("upgradePattern");
                            updateVersionCode = object.getString("versionId");
                            UpdateVersionName = object.getString("versionName");
                            updateMd5 = object.getString("md5");
                            packageLocation = object.getString("packageLocation");
                            updateMessage = object.getString("desc");
                        }
                    } catch (JSONException e) {
                        Lg.e(TAG, e.getMessage());
                        e.printStackTrace();
                        Lg.e(TAG, "获取升级接口异常");
                    }

                    // 测试用
//					updateVersionCode = "6";
//					UpdateVersionName = "1.0.6";
//					packageLocation = "http://172.16.16.181/test.apk";
//					upgradePattern = "1";
//					updateMessage = "1.update\n2.update";

                    if (updateVersionCode == null || upgradePattern == null || UpdateVersionName == null) {
                        listener.checkUpdateOver();
                        return;
                    }

                    ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_NAME.name(), UpdateVersionName);
                    ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MESSAGE.name(), updateMessage);

                    if (Integer.parseInt(curVersionCode) < Integer.parseInt(updateVersionCode)
                            && upgradePattern.equals("1")) { // 需要强制升级
                        UpdateDialog ud = showForceUpdateDialog();
                        ud.initProgressBar();
                    } else {
                        listener.checkUpdateOver();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    listener.checkUpdateOver();
                }

            }, curVersionCode);
        } catch (Exception e) {
            e.printStackTrace();
            listener.checkUpdateOver();
        }
        ;

    }

    /**
     * 强制升级的dialog
     * @return
     */
    private UpdateDialog showForceUpdateDialog() {
        final UpdateDialog ud = new UpdateDialog(context, true);
        ud.UpdateConfirm(new UpdateDialog.UpdateDialogListener() {
            @Override
            public void onDismiss() {
                listener.updateDialogOnCancel();
            }

            @Override
            public void onOk() {
                final String updatePath = context.getApplicationContext().getExternalFilesDir(null) + "/updateapk";
                //创建本地存储路径
                if (!FileUtils.isFileExist(updatePath)) {
                    FileUtils.creatDir(updatePath);
                }
                final String apkpath = updatePath + File.separator + UpdateVersionName;
                FileUtils.delAllDateFile(apkpath);
                clearUpdateData();
                FinalHttp fh = new FinalHttp();
                fh.download(
                        packageLocation.trim(),
                        apkpath,
                        new AjaxCallBack<File>() {
                            @Override
                            public void onStart() {
                                super.onStart();
                                Lg.i(TAG, "apk开始下载");
                            }

                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                                Log.i(TAG, "count:" + count + ", current:" + current);
                                int tmp = (int) (current * 100 / count);
                                ud.setProgress(tmp);
                            }

                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                Lg.i(TAG, "apk下载成功");
                                String localMD5 = null;
                                try {
                                    localMD5 = MD5FileUtil.getFileMD5String(new File(apkpath));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (localMD5 != null && localMD5.equals(updateMd5)) {
                                    saveUpdateData(apkpath);
//                                    clearData();
                                    ld.removeKV(_is_first_login);
                                    installApk(apkpath);
                                    ud.dismiss();
                                    listener.updateDialogOnCancel();
                                    System.exit(0);
                                    android.os.Process.killProcess(android.os.Process
                                            .myPid());
                                } else {
//                                    Toast.makeText(context, "由于网络原因下载失败，请退出重试", Toast.LENGTH_SHORT);
                                    clearUpdateData();
                                    FileUtils.delAllDateFile(apkpath);
                                    listener.checkUpdateOver();
                                }

                            }

                            @Override
                            public void onFailure(Throwable t, String strMsg) {
                                super.onFailure(t, strMsg);
                                Lg.i(TAG, "apk下载失败");
                                Toast.makeText(context, "由于网络原因下载失败，请退出重试", Toast.LENGTH_SHORT);
                                clearUpdateData();
                                FileUtils.delAllDateFile(apkpath);
                                listener.checkUpdateOver();
                            }
                        });
            }
        });
        return ud;
    }

    private void clearUpdateData() {
        ld.removeKV(AppGlobalConsts.PERSIST_NAMES.READY_FOR_UPDATE.name());
        ld.removeKV(AppGlobalConsts.PERSIST_NAMES.UPGRADE_PATTERN.name());
        ld.removeKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_ADRESS.name());
        ld.removeKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MD5.name());
        ld.removeKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_CODE.name());
        ld.removeKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_NAME.name());
        ld.removeKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MESSAGE.name());
    }

    private void saveUpdateData(String apkpath) {
        ld.setKV(AppGlobalConsts.PERSIST_NAMES.READY_FOR_UPDATE.name(), "1");
        ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPGRADE_PATTERN.name(), upgradePattern);
        ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_ADRESS.name(), apkpath);
        ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MD5.name(), updateMd5);
        ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_CODE.name(), updateVersionCode);
        ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_NAME.name(), UpdateVersionName);
        ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MESSAGE.name(), updateMessage);
    }

    /**
     * 双清数据：开机数据，及首页数据
     */
    private void clearData() {
        ACache.get(context).remove("epg_portal_navBar");
        ld.removeKV(AppGlobalConsts.DEV_LOGIN_INFO);
        ld.removeKV(AppGlobalConsts.DEV_LOGIN_TIME);
        ld.removeKV(_is_first_login);
    }

    private void installApk(String uri) {
        Intent installIntent = new Intent(
                Intent.ACTION_VIEW);
        installIntent.setDataAndType(Uri.fromFile(new File(uri)),
                "application/vnd.android.package-archive");
        installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(installIntent);
    }

    /**
     * 检查是否为升级后的第一次登录
     */
    public void checkFirstLoginAfterUpdate() {
        // 第一次登录，清一下首页数据和开机数据
        LocalData ld = new LocalData(context);
        String isFirstLogin = ld.getKV(_is_first_login);
        if (isFirstLogin == null) {
            clearData();
            ld.setKV(_is_first_login, "1");
        }
    }

    public boolean isValid() {
        String uri = ld.getKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_ADRESS.name());
        String localMd5 = ld.getKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MD5.name());
        try {
            if (FileUtils.isFileExist(uri) && localMd5 != null && localMd5.equals(MD5FileUtil.getFileMD5String(new File(uri)))) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
