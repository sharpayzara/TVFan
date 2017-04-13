package tvfan.tv.lib;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * User: archko Date: 12-8-21 Time: 下午4:14
 * @description: 日期格式化类。
 */
public class DateUtils {
    public static final String FULL_DATE_STRING="yyyy-MM-dd HH:mm:ss";
    public static final String SHORT_DATE_STRING="MM-dd HH:mm:ss";
    public static final String TIME_STRING="HH:mm";
    public static final String PLAY_TIME_STRING="HH:mm:ss";
    public static final String HUAWEI_DATE_STAMP = "yyyyMMddHHmmss";

    ///////////////////////////////////
    public static Date longTimeToDate(long time) {
        return new Date(time);
    }

    public static String formatDate(Date date, String pattern) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat(pattern);
            return sdf.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    public static String getFullDateString(Date date) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat(FULL_DATE_STRING);
            return sdf.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    public static String getDateString(Date date) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat(SHORT_DATE_STRING);
            return sdf.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    public static String getShortDateString(long time) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat(SHORT_DATE_STRING);
            return sdf.format(new Date(time));
        } catch (Exception e) {
        }
        return "";
    }

    //格式化GMT时间.
    public static String gmtToString(Date date) {
        String pattern="EEE MMM dd HH:mm:ss z yyyy";
        return formatDate(date, pattern);
    }

    public static String fullDateTimeString(Date date) {
        String pattern="yyyy-mm-dd hh:MM:ss";
        return formatDate(date, pattern);
    }

    public static String shortDateTimeString(Date date) {
        String pattern="mm-dd hh:MM:ss";
        return formatDate(date, pattern);
    }

    public static String fullDateString(Date date) {
        String pattern="yyyy-mm-dd";
        return formatDate(date, pattern);
    }

    public static String longToDateString(long time) {
        String pattern="yyyy-mm-dd";
        return formatDate(new Date(time), pattern);
    }
    
    public static String longToDateTimeString(long time) {
        //String pattern="yyyy-mm-dd hh:MM:ss";
        return formatDate(new Date(time), SHORT_DATE_STRING);
    }

    public static Date parseDateString(String date, String pattern) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat(pattern);
            return sdf.parse(date);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Convert time to a string。精确到秒
     * @param millis e.g.time/length from file
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
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");
//        if (millis > 0) {
//            time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec);
//        } else {
//            time = (negative ? "-" : "") + min + ":" + format.format(sec);
//        }
        time = format.format(hours) + ":" + format.format(min) + ":" + format.format(sec);
        time = negative ? "-" : "" + time;
    	return time;
    	//return formatDate(new Date(millis), PLAY_TIME_STRING);
        
    }
    
    /**
     * Convert time to a string。精确到分钟
     * @param millis e.g.time/length from file
     * @return formated string (hh:)mm
     */
    public static String millisToStringMin(long millis) {
        boolean negative = millis < 0;
        millis = java.lang.Math.abs(millis);

//        millis /= 1000;
//        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");
        if (millis > 0) {
            time = (negative ? "-" : "") + hours + ":" + format.format(min);
        } else {
            time = (negative ? "-" : "") + min;
        }
        return time;
    }
}
