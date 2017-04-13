package tvfan.tv.ui.andr.play.baseplay.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Handler;

public class DateUtils {
	
	public static Date getLivePositionTime(Date dt, int millsecond) {
		Date curDate = dt;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		calendar.set(Calendar.MILLISECOND, Calendar.MILLISECOND + millsecond);
		return calendar.getTime();
	}

	public static String getDateString(Date dtime, String parten) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(parten);
		Date dt = dtime;
		String date = sDateFormat.format(dt);
		return date;
	}
	
	public static String getTime(String millsecond, String parten) {
		String sd = "";
		try {
			String beginDate = millsecond;  		  
			SimpleDateFormat sdf=new SimpleDateFormat(parten);  		  
			sd = sdf.format(new Date(Long.parseLong(beginDate)));  		  
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sd;
	}

	//private static BasePage bs;
	private static Handler alerthd;
	private static Handler sendhd;
	public static void startClockAlert(final Handler hd){
		if(alerthd == null){
			alerthd = new Handler();
		}
		alerthd.postDelayed(alertrb, 60000l);
		sendhd = hd;
		/*bs = new BasePage();
		bs.registerLocalMsgReceiver(new LocalMsgListener(){
			@Override
			public void onReceive(Context context, Intent intent) {
				long time = intent.getLongExtra("now", 0);
				Date nowTime = new Date(time);
				if(nowTime.getMinutes()%30 == 0){
					hd.sendEmptyMessage(1);
				}
				
			}},
			AppGlobalConsts.LOCAL_MSG_FILTER.DATETIME_UPDATE
		);*/
	}
	public static Runnable alertrb = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long time = System.currentTimeMillis();
			Date nowTime = new Date(time);
			if(nowTime.getMinutes()%30 == 0){
				sendhd.sendEmptyMessage(1);
			}
			alerthd.postDelayed(alertrb, 60000l);
		}
	};
	public static void closeClockAlert(){
		/*if(bs!=null){
			bs.unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.DATETIME_UPDATE);
		}*/
		if(alerthd!=null){
			alerthd.removeCallbacks(alertrb);
			alerthd = null;
		}
	}
	
	/**
	 * 这是截取字符串的方法
	 * @param s
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public static String bSubstring(String s, int length) throws Exception
    {

        byte[] bytes = s.getBytes("Unicode");
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++)
        {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1)
            {
                n++; // 在UCS2第二个字节时n加1
            }
            else
            {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0)
                {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1)

        {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
            // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }

        return new String(bytes, 0, i, "Unicode");
    }

}
