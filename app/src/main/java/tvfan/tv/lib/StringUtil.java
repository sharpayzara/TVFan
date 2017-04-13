package tvfan.tv.lib;

import android.text.TextUtils;

public class StringUtil {

	/**
	 * 获取字符串中字母，数字的个数
	 * 
	 * @param str
	 * @return
	 */
	public static int countCharNumber(String str) {
		int countDigit = 0, countLetter = 0, countOthers = 0;

		// 将字符串变量转化为字符数组
		char[] charArray = str.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			// ASIIC码
			if (charArray[i] <= 'z' && charArray[i] >= 'a'
					|| charArray[i] <= 'Z' && charArray[i] >= 'A')
				countLetter++;
			else if (charArray[i] <= '9' && charArray[i] >= '0')
				countDigit++;
			else
				countOthers++;
		}

		return countLetter + countDigit;
	}

	/**
	 * 判断字符串是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (TextUtils.isEmpty(str))
			return false;
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
