package crack.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import crack.listener.DownLoadJarCompleteListener;

@SuppressLint("NewApi")
public class DownloadJarTask extends AsyncTask<Object, Integer, Integer> {

	private String liburl;
	private String fileName;
	private Context ct;
	private DownLoadJarCompleteListener ccb;

	public DownloadJarTask(Context ct, DownLoadJarCompleteListener ccl,
			String liburl, String fileName) {
		super();
		this.liburl = liburl;
		this.fileName = fileName;
		this.ccb = ccl;
		this.ct = ct;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(Object... params) {
		HttpDownloader httpDownloader = new HttpDownloader();
		String path = ct.getFilesDir().getPath() + "/";
		Log.i("downfile start", path + fileName);
		return httpDownloader.downFile(liburl, path, fileName);
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		ccb.onDownLoadJarComplete(result);

	}

}
