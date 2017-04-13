package tvfan.tv.lib;

import android.content.Context;

import com.badlogic.gdx.Gdx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpRequestWrapper;
import tvfan.tv.ui.andr.play.baseplay.ui.BasePlayerActivity;

public class NetWorkUtils {

	private static int iresult = 0;

	// public static String getNetSpeed() {
	//
	// if (iresult != 0) {
	// return Integer.toString(iresult / 1024) + " kbps";
	// }
	// int icurspeed = (int) (syncFetchReceivedBytes());
	//
	// int ilastspeed = 0;
	//
	// try {
	// new Thread().sleep(1000l);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// ilastspeed = (int) (syncFetchReceivedBytes());
	//
	// iresult = (ilastspeed - icurspeed);
	//
	// new Handler().postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// iresult = 0;
	// }
	// }, 10000l);
	// return Integer.toString(iresult / 1024) + " kbps";
	// }

	public static long getNetSpeed() {

		return syncFetchReceivedBytes();

	}

	/**
	 * 计算当前网络码率
	 * 
	 * @author dw
	 * @return
	 */
	public static long syncFetchReceivedBytes() {
		// TODO Auto-generated method stub
		ProcessBuilder cmd;
		long readBytes = 0;
		BufferedReader rd = null;
		try {
			String[] args = { "/system/bin/cat", "/proc/net/dev" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			rd = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.contains("lan0") || line.contains("eth0")
						|| line.contains("wlan0")) {
					String[] delim = line.split(":");
					if (delim.length >= 2) {
						readBytes = parserNumber(delim[1].trim());
						if (readBytes == 0) {
							continue;
						}
						break;
					}
				}
			}
			rd.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return readBytes;
	}

	private static long parserNumber(String line) throws Exception {
		long ret = 0;
		String[] delim = line.split(" ");
		if (delim.length >= 1) {
			ret = Long.parseLong(delim[0]);
		}
		return ret;
	}

	/**
	 * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
	 * @return
	 */
	public static final boolean ping() {

		String result = null;
		try {
			String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
			// 读取ping的内容，可以不加
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer stringBuffer = new StringBuffer();
			String content = "";
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
			}
			Lg.d("------ping-----",
					"result content : " + stringBuffer.toString());
			// ping的状态
			int status = p.waitFor();
			if (status == 0) {
				result = "success";
				return true;
			} else {
				result = "failed";
			}
		} catch (IOException e) {
			result = "IOException";
		} catch (InterruptedException e) {
			result = "InterruptedException";
		} finally {
			Lg.d("----result---", "result = " + result);
		}
		return false;
	}

	// 处理页面跳转间的网络请求onError事件
	public static void handlerError(final String errorMessage,
			final BasePage page) {
		HttpRequestWrapper.cancelAll();
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				if (errorMessage != null && errorMessage.equals("000")) {
					com.luxtone.lib.utils.Utils
							.showToast(R.string.unknown_internet_error);// 未知网络错误
				} else if (errorMessage != null && errorMessage.equals("800")) {
					com.luxtone.lib.utils.Utils
							.showToast(R.string.timeout_internet_error);// 网络连接超时
				} else if (errorMessage != null && errorMessage.equals("802")&& BasePlayerActivity.playType!=2) {
					com.luxtone.lib.utils.Utils
							.showToast(R.string.no_internet_error);// 网络已断开连接

				} else {
					com.luxtone.lib.utils.Utils
							.showToast(R.string.server_down_error);// 服务器访问异常
				}
				page.finish();
			}
		});
	}

	public static String getErrorMessage(Context context,
			final String errorMessage) {
		if (errorMessage != null && errorMessage.equals("000")) {
			return context.getResources().getString(
					R.string.unknown_internet_error);// 未知网络错误
		} else if (errorMessage != null && errorMessage.equals("800")) {
			return context.getResources().getString(
					R.string.timeout_internet_error);// 网络连接超时
		} else if (errorMessage != null && errorMessage.equals("802")&& BasePlayerActivity.playType!=2) {
			return context.getResources().getString(R.string.no_internet_error);// 网络已断开连接
		} else {
			return context.getResources().getString(R.string.server_down_error);// 服务器访问异常
		}
	}
}
