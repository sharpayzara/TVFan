package crack.cracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.sumavison.crack.interfaces.CrackListener;
import com.sumavison.crack.interfaces.ILoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import crack.listener.DownLoadJarCompleteListener;
import crack.listener.JarCrackCompleteListener;
import crack.util.DownloadJarTask;
import crack.util.HttpDownloader;
import dalvik.system.DexClassLoader;
import tvfan.tv.lib.ACache;

public class JarCracker implements CrackListener, DownLoadJarCompleteListener {

    private static final String TAG = "JarCracker";
//    private static String jarJsonUrl = "http://172.16.16.181/crack_edition_info.json";
    private static String jarJsonUrl = "http://tvfan.cn/resource/lua/crack_edition_info.json";

    /**
     * 破解jar包中要用到的标志位，用于标记so文件是否被更新过
     */
    private static String isInitSo = "isInitSo";

    /**
     * 用于标记jar包是否下载好
     */
    private static boolean isInitJar = false;

    private static Context ct;
    private static ILoader tl;

    private String currentEdition;
    private String jarName = "Dynamic_DefaultParse.jar";
    private JarCrackCompleteListener jarCrackCompleteListener;

    /**
     * 如果进入播放器后，需要重新触发jar包下载流程，则置为true，在jar包下载完成后调用一次破解
     */
    private boolean playerInvoke;
    private int type;
    private String url;
    private String originUrl;

    private JarCracker() {
    }

    private static JarCracker instance;

    public static JarCracker getInstance() {
        if (instance == null) {
            instance = new JarCracker();
        }
        return instance;
    }

    public void init(Context context) {
        this.ct = context;
        new GetVersion(this).execute();
        ACache.get(context).put(isInitSo, "0");
    }

    public void crack(Context context, String originUrl, String url, int type,
                      JarCrackCompleteListener jarCrackCompleteListener) {
        this.jarCrackCompleteListener = jarCrackCompleteListener;
        this.ct = context;
        this.url = url;
        this.type = type;
        this.originUrl = originUrl;
        if (tl != null) {
            Log.i(TAG, "TestLoader的实例不空，直接破解");
            tl.crack(url, type + "", this, context);
        } else {
            if (new File(context.getFilesDir().getPath() + "/" + jarName)
                    .exists() && isInitJar) {
                Log.i(TAG, "存在jar包且已初始化，首次加载TestLoader");
                File jarFile = new File(context.getFilesDir().getPath() + "/"
                        + jarName);
                DexClassLoader dcl = new DexClassLoader(jarFile.toString(),
                        context.getFilesDir().getPath(), null, this.getClass()
                        .getClassLoader());
                try {
                    Class<?> c = dcl
                            .loadClass("com.sumavision.crack.interfacesImp.TestLoader");
                    tl = (ILoader) c.newInstance();
                    tl.crack(url, type + "", this, context);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Log.w(TAG, "jar包不存在或未初始化，再次初始化");
                new GetVersion(this).execute();
                playerInvoke = true;
            }
        }
    }

    @Override
    public void start() {

    }

    @Override
    public HashMap<String, String> end(HashMap<String, String> arg0) {
    	 /**
    	  * 必须同时满足以下条件，才返回结果
    	  * 1.arg0不空
    	  * 2.videoType不空
    	  * 3.videoType等于updatePlugins或complete，或者orignUrl等于最后一次破解的url
          * 4.3中清晰度至少有一个不为空
    	  */
        Log.d("logger","arg0的值。。。"+arg0);
        if (arg0 != null 
        		&& !TextUtils.isEmpty(arg0.get("videoType")) 
        		&& ((arg0.get("videoType").equals("updatePlugins") || arg0.get("videoType").equals("complete"))
        				|| (!TextUtils.isEmpty(arg0.get("orignUrl")) && arg0.get("orignUrl").equals(originUrl))
                            && (!TextUtils.isEmpty(arg0.get("standardDef")) ||! TextUtils.isEmpty(arg0.get("hightDef")) ||! TextUtils.isEmpty(arg0.get("superDef"))))) {
            jarCrackCompleteListener.onJarCrackComplete(arg0);
        } else {
            Log.i(TAG, "不用从正常路径返回");

//            if (arg0 != null
//                    && !TextUtils.isEmpty(arg0.get("orignUrl")) // 有原始地址的才返回，否则没有统计的意义
//                    && arg0.get("orignUrl").equals(originUrl) // 多次破解的最后一次
//                    && TextUtils.isEmpty(arg0.get("standardDef"))
//                    && TextUtils.isEmpty(arg0.get("hightDef"))
//                    && TextUtils.isEmpty(arg0.get("superDef"))) {
            jarCrackCompleteListener.onCrackFailed(arg0);
//            }
        }
        return null;
    }

    /**
     * 破解jar包中要用到的标志位，用于标记so文件是否被更新过
     */
    @Override
    public void setIsInitJar(boolean init) {
        ACache.get(ct).put(isInitSo, "1");
    }

    @Override
    public boolean getIsInitJar() {
        if (ACache.get(ct).getAsString(isInitSo) == null) {
            ACache.get(ct).put(isInitSo, "0");
            return false;
        } else {
            return ACache.get(ct).getAsString(isInitSo).equals("1") ? true : false;
        }
    }

    private String readRef() {
        SharedPreferences sp = ct.getSharedPreferences("jarInfo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = sp.edit();
        return sp.getString("edition", "0.0.0");

    }

    private void writeRef() {
        SharedPreferences sp = ct.getSharedPreferences("jarInfo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putString("edition", currentEdition);
        spEdit.commit();
    }

    @Override
    public void onDownLoadJarComplete(int result) {
        if (result != -1) {
            writeRef();
            isInitJar = true;
            if (playerInvoke) {
                crack(ct, originUrl, url, type, jarCrackCompleteListener);
            }
        }
    }

    class GetVersion extends AsyncTask<Object, Integer, Object> {

        JarCracker cji;

        public GetVersion(JarCracker cji) {
            super();
            this.cji = cji;
        }

        @Override
        protected Object doInBackground(Object... params) {
            return new HttpDownloader().download(jarJsonUrl);

        }

        @Override
        protected void onPostExecute(Object result) {
            try {
                JSONObject json = new JSONObject((String) result);
                JSONObject defalultJar = json.optJSONObject("DefaultJar");
                currentEdition = defalultJar.optString("edition");
                String localEdition = readRef();
                Log.i("crack", "currentedition" + currentEdition
                        + "localedition" + localEdition);
                Log.e("View", "*** " + currentEdition.compareTo(localEdition));
                // if (currentEdition.compareTo(localEdition) > 0) {
                if (compareEdition(localEdition, currentEdition)) {
                    String name = defalultJar.optString("url");
                    Log.i("crack", "downloadnewversion");
                    new DownloadJarTask(ct, cji, name, jarName).execute();
                } else {
                    isInitJar = true;
                    if (playerInvoke) {
                        crack(ct, originUrl, url, type, jarCrackCompleteListener);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    /**
     * 判断是否需要更新jar包
     *
     * @param oldEdition 本地存储的旧版本
     * @param newEdition 服务器获取的新版本
     * @return true 为需要更新
     */
    private boolean compareEdition(String oldEdition, String newEdition) {
        String[] oldArr = oldEdition.split("\\.");
        String[] newArr = newEdition.split("\\.");
        for (int i = 0; i < newArr.length; i++) {
            if (Integer.parseInt(newArr[i]) > Integer.parseInt(oldArr[i])) {
                return true;
            } else if (Integer.parseInt(newArr[i]) < Integer
                    .parseInt(oldArr[i])) {
                return false;
            }
        }
        return false;
    }

}
