package tvfan.tv.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Locale;

import tvfan.tv.AppGlobalConsts;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;

public class Utils {
	public static final int HIGHT1080 = AppGlobalConsts.APP_HEIGHT;
	public static final int WIDTH1080 = AppGlobalConsts.APP_WIDTH;

	public static int getY(int toTop, int high) {
		return (int) ((HIGHT1080 - toTop - high / 1.5) / 1.5);
	}

	public static int getY(int toTop) {
		return (int) ((HIGHT1080 - toTop));
	}

	public static int getX(int toLeft) {
		return (int) (toLeft / 1.5);
	}

	/**
	 * Convert time to a string
	 * 
	 * @param millis
	 *            e.g.time/length from file
	 * @return formated string (hh:)mm:ss
	 */
	public static String millisToString(long millis) {
		boolean negative = millis < 0;
		millis = java.lang.Math.abs(millis);

		millis /= 1000;
		int sec = (int) (millis % 60);
		millis /= 60;
		int min = (int) (millis % 60);
		millis /= 60;
		int hours = (int) millis;

		String time;
		DecimalFormat format = (DecimalFormat) NumberFormat
				.getInstance(Locale.US);
		format.applyPattern("00");
		time = format.format(hours) + ":" + format.format(min) + ":"
				+ format.format(sec);
		time = negative ? "-" : "" + time;
		return time;
	}

	public static String getEthernetMac() {
		// return "C8:0E:77:30:77:62";
		String macAddr = null;
		macAddr = _getLocalEthernetMacAddress();
		if (macAddr == null)
			macAddr = getMac();
		if (TextUtils.isEmpty(macAddr)) {
			macAddr = _getEthMacAddress2();
			if (macAddr != null && macAddr.startsWith("0:")) {
				macAddr = "0" + macAddr;
			}
		}
		// return "C8:0E:77:30:77:62";
		return macAddr;
		// return "00:00:77:30:77:62";
	}

	/**
	 * 获取当前系统连接网络的网卡的mac地址
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getMac() {
		byte[] mac = null;
		StringBuffer sb = new StringBuffer();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();

				while (address.hasMoreElements()) {
					InetAddress ip = address.nextElement();
					if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address)
							|| ip.isLoopbackAddress())
						continue;
					if (ip.isSiteLocalAddress())
						mac = ni.getHardwareAddress();
					else if (!ip.isLinkLocalAddress()) {
						mac = ni.getHardwareAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		if (mac != null) {
			for (int i = 0; i < mac.length; i++) {
				sb.append(parseByte(mac[i]));
			}
			return sb.substring(0, sb.length() - 1);
		}

		return null;
	}

	// 获取当前连接网络的网卡的mac地址
	private static String parseByte(byte b) {
		String s = "00" + Integer.toHexString(b) + ":";
		return s.substring(s.length() - 3);
	}

	/*
	 * PRIVATE METHODS
	 */
	@SuppressLint("NewApi")
	private static String _getLocalEthernetMacAddress() {
		String mac = null;
		try {
			Enumeration localEnumeration = NetworkInterface
					.getNetworkInterfaces();

			while (localEnumeration.hasMoreElements()) {
				NetworkInterface localNetworkInterface = (NetworkInterface) localEnumeration
						.nextElement();
				String interfaceName = localNetworkInterface.getDisplayName();

				if (interfaceName == null) {
					continue;
				}

				if (interfaceName.equals("eth0")) {
					mac = _convertToMac(localNetworkInterface
							.getHardwareAddress());
					if (mac != null && mac.startsWith("0:")) {
						mac = "0" + mac;
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return mac;
	}

	private static String _convertToMac(byte[] mac) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			byte b = mac[i];
			int value = 0;
			if (b >= 0 && b < 16) {// Jerry(2013-11-6): if (b>=0&&b<=16) => if
									// (b>=0&&b<16)
				value = b;
				sb.append("0" + Integer.toHexString(value));
			} else if (b >= 16) {// Jerry(2013-11-6): else if (b>16) => else if
									// (b>=16)
				value = b;
				sb.append(Integer.toHexString(value));
			} else {
				value = 256 + b;
				sb.append(Integer.toHexString(value));
			}
			if (i != mac.length - 1) {
				sb.append(":");
			}
		}
		return sb.toString();
	}

	private static String _getEthMacAddress2() {
		String mac = _loadFileAsString("/sys/class/net/eth0/address");
		if (mac == null) {
			mac = "";
		} else {
			mac = mac.toUpperCase();
			if (mac.length() > 17) {
				mac = mac.substring(0, 17);
			}
		}

		return mac;
	}

	private static String _loadFileAsString(String filePath) {
		try {
			if (new File(filePath).exists()) {
				StringBuffer fileData = new StringBuffer(1000);
				BufferedReader reader = new BufferedReader(new FileReader(
						filePath));
				char[] buf = new char[1024];
				int numRead = 0;
				while ((numRead = reader.read(buf)) != -1) {
					String readData = String.valueOf(buf, 0, numRead);
					fileData.append(readData);
				}
				reader.close();
				return fileData.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据CornerType和角标的位置来获取角标图片资源
	 * 
	 * @param leftOrRight
	 *            左上角或右上角， 参数： left|LEFT , right|RIGHT
	 * @param cornerType
	 *            0,1,2,3,4
	 * @return 图片资源,返回结果可能为null
	 */
	public static TextureRegion getSuperScriptImage(Page page,
			String leftOrRight, String cornerType) {
		// 测试使用
		// cornerType = new Random().nextInt(5)+"";
		// cornerType = "1";
		// 若cornerType为0，则返回null
		if (cornerType.equals("0")) {
			return null;
		}
		// 左上角付费角标
		else if (leftOrRight.equalsIgnoreCase("left") && cornerType.equals("1")) {
			return page.findTextureRegion(R.drawable.buy_img);
		}
		// 右上角高清
		else if (leftOrRight.equalsIgnoreCase("right")
				&& cornerType.equals("2")) {
			return page.findTextureRegion(R.drawable.jiaob_85);
		}
		// 右上角超清
		else if (leftOrRight.equalsIgnoreCase("right")
				&& cornerType.equals("3")) {
			return page.findTextureRegion(R.drawable.jiaob_84);
		}
		// 右上角高清
		else if (leftOrRight.equalsIgnoreCase("right")
				&& cornerType.equals("4")) {
			return page.findTextureRegion(R.drawable.jiaob_82);
		} else {
			return null;
		}

	}

	public static int getSuperScriptImageResourceId(Page page,
			String leftOrRight, String cornerType) {
		// 测试使用
		// cornerType = new Random().nextInt(5)+"";
		// cornerType = "1";
		// 若cornerType为0，则返回null
		if (TextUtils.isEmpty(cornerType) && cornerType.equals("0")) {
			return -1;
		}
		// 左上角付费角标
		else if (leftOrRight.equalsIgnoreCase("left") && cornerType.equals("1")) {
			return R.drawable.buy_img;
		}
		// 右上角高清
		else if (leftOrRight.equalsIgnoreCase("right")
				&& cornerType.equals("2")) {
			return R.drawable.jiaob_85;
		}
		// 右上角超清
		else if (leftOrRight.equalsIgnoreCase("right")
				&& cornerType.equals("3")) {
			return R.drawable.jiaob_84;
		}
		// 右上角高清
		else if (leftOrRight.equalsIgnoreCase("right")
				&& cornerType.equals("4")) {
			return R.drawable.jiaob_82;
		} else {
			return -1;
		}
	}

	/**
	 * 判断Image的纹理是否已被回收
	 * 
	 * @param img
	 * @return
	 */
	public static boolean isResourceRecycled(Image img) {
		Drawable d = img.getDrawable();
		if (d != null) {
			Texture texture = null;
			if (d instanceof TextureRegionDrawable) {
				TextureRegionDrawable trd = (TextureRegionDrawable) d;
				texture = trd.getRegion().getTexture();
			} else if (d instanceof SpriteDrawable) {
				SpriteDrawable sd = (SpriteDrawable) d;
				texture = sd.getSprite().getTexture();
			} else if (d instanceof NinePatchDrawable) {
				NinePatchDrawable npd = (NinePatchDrawable) d;
				texture = npd.getPatch().getTexture();
			}
			if (texture != null) {
				return texture.isDispose();
			}
		}
		return false;
	}

	public static void resetImageSource(Image img, int resId) {
		// if (Utils.isResourceRecycled(img))
		if(img != null)
			img.setDrawableResource(resId);
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 获取内网IP,有可能为null
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (inetAddress instanceof Inet4Address
							&& !(inetAddress.isLoopbackAddress())) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	/**
	 * wanqi，将log日志输出到本地公用方法
	 */
	public static void logcatToLocal() {
		try {
			Process process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			StringBuilder log = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				log.append(line).append("\n");
			}
			FileWriter fw = new FileWriter(
					Environment.getExternalStorageDirectory() + "/logcat_"
							+ System.currentTimeMillis() + ".txt");
			fw.flush();
			fw.write(log.toString());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
