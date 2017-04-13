package tvfan.tv.daemon;

import java.io.File;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.lib.FileUtils;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.MD5FileUtil;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class EPGUpdateService extends Service {
	private static final String TAG = "TVFAN.EPG.EPGUpdateService";

	private static Context mCtx;

	String UpdatePath = "";
	String UpdateMd5 = null;
	String UpdateVersionName;
	String versionId;
	String upgradePattern;
	String desc;

	String ImgPath = "";
	String ImgMd5 = null;
	String ImgTimespan = null;
	String ImgResUrl;
	
	
	private static final String BootImgMD5Key = "BootImgAPKmd5";
	private static final String ScreenProtectImgMD5Key = "ScreenProtectImgMD5Key";
	private static final String BootImgAddress = AppGlobalConsts.PERSIST_NAMES.BOOT_IMAGE.name();
	private static final String BootImgTimespan = AppGlobalConsts.PERSIST_NAMES.BOOT_TIMESPAN.name();
	
	public static void Start(Context ctx, String clientID, String subject) {
		Lg.i(TAG, "call Start");
		Intent intent = new Intent(ctx, EPGUpdateService.class);
		ctx.startService(intent);
		mCtx = ctx;
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	public static void Stop(Context ctx) {
		Lg.i(TAG, "call Stop");
		Intent intent = new Intent(ctx, EPGUpdateService.class);
		ctx.stopService(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Lg.i(TAG, "onStartCommand");
		
		EPGBootImg(intent, flags, startId);
		EPGUpdate(intent, flags, startId);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				stopSelf();
				Lg.i(TAG, "stopSelf");
			}
		}, 600000);

		return 0;
	}

	
	public void EPGUpdate(Intent intent, int flags, int startId){
		//本地存储apk路径
				UpdatePath = getApplicationContext().getExternalFilesDir(null)+ "/updateapk/";
				//创建本地存储路径
				if (!FileUtils.isFileExist(UpdatePath)) {
					FileUtils.creatDir(UpdatePath);
				}
				//final String url = "http://192.168.1.114/testupdate.json";
				RemoteData rd = new RemoteData(getApplicationContext());
				String vn = "";
				try {
					vn = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(),
							PackageManager.GET_CONFIGURATIONS).versionCode+"";
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				
				
				rd.getUpdateVersion(new Listener4JSONObject() {
					@Override
					public void onResponse(JSONObject response) {
						if (response == null) {
							// JSON 请求出现错误
							Lg.e(TAG, "ERROR");
							return;
						}

						//json 解析
						JSONObject object = null;
						String packageLocation = null;
//						String desc;
						try {
							JSONArray ml = response.getJSONArray("data");
							for (int i = 0; i < ml.length(); i++) {
								object = ml.getJSONObject(i);
								versionId = object.getString("versionId");
								UpdateVersionName = object.getString("versionName");
								packageLocation = object.getString("packageLocation");
								UpdateMd5 = object.getString("md5");
								upgradePattern = object.getString("upgradePattern");
								desc = object.getString("desc");
							}
						} catch (JSONException e) {
							Lg.e(TAG, e.getMessage());
							e.printStackTrace();
							//Utils.showToast("EPGUpdateService response Exception");
							Lg.e(TAG, "获取升级接口异常");
						}
						//json 解析 END

						if (upgradePattern != null && upgradePattern.equals("1")) {
							 // 本服务处理静默升级，因此强制升级时不下载
							return;
						}

						//判断本地是否存在已下载apk
						LocalData ld = new LocalData(getApplicationContext());
						String localMd5 = ld.getKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MD5.name());
						final String fileName = UpdatePath + "/" + UpdateVersionName;
//				    	if (FileUtils.isFileDateExist(fileName)) {
						if (!TextUtils.isEmpty(localMd5) && localMd5.equalsIgnoreCase(UpdateMd5)
								&& FileUtils.isFileDateExist(fileName)) {
							//发信息通知首页弹升级确认框
							//......

							Lg.e(TAG, "");
							//......
						}
						//判断本地是否存在已下载apk END


						//开始下载
						else if (null != object && !TextUtils.isEmpty(packageLocation)) {

							//先清空本地apk
							FileUtils.delAllDateFile(UpdatePath);
							//下载apk
							FinalHttp fh = new FinalHttp();
							//wanqi,去除url地址首尾空格
							fh.download(
									packageLocation.trim(),
									fileName,
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

										}

										@Override
										public void onSuccess(File t) {
											super.onSuccess(t);
											Lg.i(TAG, "apk下载成功");
											// 下载完毕
											// md5校验
											try {
												String localMD5 = MD5FileUtil.getFileMD5String(new File(fileName));
//											if (!UpdateMd5.equalsIgnoreCase(localMD5)) {
//												Lg.i(TAG, "md5校验未通过，删除已下载数据");
//												FileUtils.delAllDateFile(UpdatePath);
//												LocalData ld = new LocalData(getApplicationContext());
//												ld.setKV(MD5Key, "");
//												ld.setKV(UpdateAdress, "");
//												ld.setKV(VersionName, "");
//												ld.setKV(AppGlobalConsts.PERSIST_NAMES.READY_FOR_UPDATE.name(), "0");
//												ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_CODE.name(), "0");
//											} else {
//												Lg.i(TAG, "md5通过，下载成功");
												saveUpdateData(localMD5, fileName);
												//Utils.showToast("下载成功");
//											}
											} catch (Exception e) {
												Lg.i(TAG, "校验md5异常");
												FileUtils.delAllDateFile(UpdatePath);
												clearUpdateData();
											}


										}

										@Override
										public void onFailure(Throwable t, String strMsg) {
											super.onFailure(t, strMsg);
											Lg.i(TAG, "apk下载失败");
											FileUtils.delAllDateFile(UpdatePath);
											clearUpdateData();
										}
									});//AjaxCallBack<File>() END

						} //开始下载 END

					}

					@Override
					public void onError(String errorMessage) {
					}

				}, vn);//getBootImg END
	}

	private void saveUpdateData(String localMD5, String fileName) {
		LocalData ld = new LocalData(getApplicationContext());
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.READY_FOR_UPDATE.name(), "1");
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPGRADE_PATTERN.name(), upgradePattern);
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_CODE.name(), versionId);
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_NAME.name(), UpdateVersionName);
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MESSAGE.name(), desc);
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_ADRESS.name(), fileName);
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MD5.name(), localMD5);
	}

	private void clearUpdateData() {
		LocalData ld = new LocalData(getApplicationContext());
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.READY_FOR_UPDATE.name(), "0");
		ld.removeKV(AppGlobalConsts.PERSIST_NAMES.UPGRADE_PATTERN.name());
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_CODE.name(), "0");
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_VERSION_NAME.name(), "");
		ld.removeKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MESSAGE.name());
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_ADRESS.name(), "");
		ld.setKV(AppGlobalConsts.PERSIST_NAMES.UPDATE_MD5.name(), "");
	}

	public void EPGBootImg(Intent intent, int flags, int startId){
		//下载EPG开机图片
				ImgPath = getApplicationContext().getExternalFilesDir(null)+ "/welpic";
				//创建本地存储路径
				if (!FileUtils.isFileExist(ImgPath)) {
					FileUtils.creatDir(ImgPath);
				}
				
				
				RemoteData rd = new RemoteData(getApplicationContext());
				
				rd.getBootImg(new Listener4JSONObject() {
					@Override
					public void onResponse(JSONObject response) {
						if (response == null) {
							// JSON 请求出现错误
							Lg.e(TAG, "ERROR");
							return;
						}

						//json 解析
						JSONObject object = null;
						String name, nodeType = null;
						try {
							JSONArray ml = response.getJSONArray("data");
							int screenIndex = 0;
							for (int i = 0; i < ml.length(); i++) {
								if (ml.getJSONObject(i).getString("nodeType").equals("epgstartpic")) {
									object = ml.getJSONObject(i);
									name = object.getString("name");
									ImgResUrl = object.getString("resUrl");
									nodeType = object.getString("nodeType");
									ImgMd5 = object.getString("md5");
									ImgTimespan = object.getJSONObject("param").getString("timespan");
								} else if (ml.getJSONObject(i).getString("nodeType").equals("aboutus")) {
									object = ml.getJSONObject(i);
									AppGlobalVars.getIns().PHONE_NUMBER = object.getString("md5");
								} else if (ml.getJSONObject(i).getString("nodeType").equals("screen")) {
									screenIndex++;
									object = ml.getJSONObject(i);
									handleScreenProtectImage(screenIndex, object);
								}

							}
						} catch (JSONException e) {
							Lg.e(TAG, e.getMessage());
							e.printStackTrace();
							//Utils.showToast("EPGBootImgService response Exception");
							Lg.e(TAG, "获取升级接口异常");
						}
						//json 解析 END

						//判断本地是否存在已下载pic
						LocalData ld = new LocalData(getApplicationContext());
						String localMd5 = ld.getKV(BootImgMD5Key);
						final String fileName;
						if (TextUtils.isEmpty(ImgMd5)) {
							fileName = ImgPath + "/" + localMd5;
						} else fileName = ImgPath + "/" + ImgMd5;
						if (!TextUtils.isEmpty(localMd5) && localMd5.equalsIgnoreCase(ImgMd5)
								&& FileUtils.isFileDateExist(fileName)) {
							//发信息通知首页弹升级确认框
							//......
							ld.setKV(BootImgTimespan, ImgTimespan);

							//......
						}
						//判断本地是否存在已下载pic END


						//开始下载
						else if (null != object && !TextUtils.isEmpty(ImgResUrl)) {

							//先清空本地pic
							FileUtils.delAllDateFile(ImgPath);

							//下载pic
							FinalHttp fh = new FinalHttp();
							fh.download(
									ImgResUrl,
									fileName,
									new AjaxCallBack<File>() {
										@Override
										public void onStart() {
											super.onStart();
											Lg.i(TAG, "img开始下载");
										}

										@SuppressLint("DefaultLocale")
										@Override
										public void onLoading(long count, long current) {
											super.onLoading(count, current);

										}

										@Override
										public void onSuccess(File t) {
											super.onSuccess(t);
											Lg.i(TAG, "img下载成功");
											// 下载完毕
											// md5校验
											try {
												String localMD5 = MD5FileUtil.getFileMD5String(new File(fileName));
												LocalData ld = new LocalData(getApplicationContext());

												ld.setKV(BootImgMD5Key, localMD5);
												ld.setKV(BootImgAddress, fileName);
												ld.setKV(BootImgTimespan, ImgTimespan);
											/*if (!ImgMd5.equalsIgnoreCase(localMD5)) {
												Lg.i(TAG, "md5校验未通过，删除已下载数据");
												FileUtils.delAllDateFile(ImgPath);
												LocalData ld = new LocalData(getApplicationContext());
												ld.setKV(BootImgMD5Key, "");
												ld.setKV(BootImgAddress, "");
												ld.setKV(BootImgTimespan, "");
											} else {
												Lg.i(TAG, "md5通过，下载成功");
												LocalData ld = new LocalData(getApplicationContext());
												
												ld.setKV(BootImgMD5Key, localMD5);
												ld.setKV(BootImgAddress, fileName);
												ld.setKV(BootImgTimespan, ImgTimespan);
												//Utils.showToast("welpic下载成功");
											}*/
											} catch (Exception e) {
												Lg.i(TAG, "校验md5异常");
												FileUtils.delAllDateFile(ImgPath);
												LocalData ld = new LocalData(getApplicationContext());
												ld.setKV(BootImgMD5Key, "");
												ld.setKV(BootImgAddress, "");
												ld.setKV(BootImgTimespan, "");
											}


										}

										@Override
										public void onFailure(Throwable t, String strMsg) {
											super.onFailure(t, strMsg);
											Lg.i(TAG, "img下载失败");
											FileUtils.delAllDateFile(ImgPath);
											LocalData ld = new LocalData(getApplicationContext());
											ld.setKV(BootImgMD5Key, "");
											ld.setKV(BootImgAddress, "");
											ld.setKV(BootImgTimespan, "");
										}
									});//AjaxCallBack<File>() END

						} //开始下载 END
						else {
							FileUtils.delAllDateFile(ImgPath);
							ld.setKV(BootImgMD5Key, "");
							ld.setKV(BootImgAddress, "");
							ld.setKV(BootImgTimespan, "");
						}

					}

					@Override
					public void onError(String errorMessage) {

					}


				});//getBootImg END
	}

	private void handleScreenProtectImage(final int screenIndex, JSONObject object) {
		try {
			String url = object.getString("resUrl");
			String md5 = object.getString("md5");
			final File file = new File(getExternalFilesDir(null).getPath() + File.separator + screenIndex);
			//判断本地是否存在已下载pic
			LocalData ld = new LocalData(getApplicationContext());
			String localMd5 = ld.getKV(ScreenProtectImgMD5Key + screenIndex);
			if (!TextUtils.isEmpty(localMd5) && localMd5.equalsIgnoreCase(md5)
					&& file.exists()) {
				Log.i(TAG, "屏保图片" + screenIndex + "不用更新");
			} else if (null != object && !TextUtils.isEmpty(url)) {

				//先清空本地pic
				FileUtils.delAllDateFile(file.getPath());

				//下载pic
				FinalHttp fh = new FinalHttp();
				fh.download(
						url,
						file.getPath(),
						new AjaxCallBack<File>() {
							@Override
							public void onStart() {
								super.onStart();
								Lg.i(TAG, "屏保img" + screenIndex + "开始下载");
							}

							@SuppressLint("DefaultLocale")
							@Override
							public void onLoading(long count, long current) {
								super.onLoading(count, current);

							}

							@Override
							public void onSuccess(File t) {
								super.onSuccess(t);
								Lg.i(TAG, "屏保img" + screenIndex + "下载完成");
								// 下载完毕
								// md5校验
								try {
									String localMD5 = MD5FileUtil.getFileMD5String(file);
									LocalData ld = new LocalData(getApplicationContext());

									ld.setKV(ScreenProtectImgMD5Key + screenIndex, localMD5);
								} catch (Exception e) {
									Lg.i(TAG, "校验md5异常");
									FileUtils.delAllDateFile(file.getPath());
									LocalData ld = new LocalData(getApplicationContext());
									ld.setKV(ScreenProtectImgMD5Key + screenIndex, "");
								}


							}

							@Override
							public void onFailure(Throwable t, String strMsg) {
								super.onFailure(t, strMsg);
								Lg.i(TAG, "屏保img" + screenIndex + "下载失败");
								FileUtils.delAllDateFile(file.getPath());
								LocalData ld = new LocalData(getApplicationContext());
								ld.setKV(ScreenProtectImgMD5Key + screenIndex, "");
							}
						});

			} else {
				FileUtils.delAllDateFile(file.getPath());
				ld.setKV(ScreenProtectImgMD5Key + screenIndex, "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Lg.i(TAG, "onBind");
		return null;
	}
	
	@Override
	public void onDestroy() {
		Lg.i(TAG, "onDestroy");
		super.onDestroy();
	}
}
