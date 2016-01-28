package com.smarthome.client2.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.view.View;
import android.widget.TextView;

public class FormatParameters
{
    public static String NumberFirmwareFormat(float value)
    {
        DecimalFormat df = new DecimalFormat("#,###");
        String s = df.format(value);
        return s;
    }

    public static String TimeFirmwareFormat(int min)
    {
        int h = min / 60;
        int m = min % 60;
        if (h > 0)
        {
            return h + "小时" + m + "分钟";
        }
        else
        {
            return m + "分钟";
        }
    }

    public static String TimeFirmwareFormat(long milli)
    {
        Date date = new Date(milli);
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        return sf.format(date);
    }

    /**
     * @param 待验证的字符串
     * @return 如果是符合邮箱格式的字符串,返回<b>true</b>,否则为<b>false</b>
     */
    public static boolean isEmail(String str)
    {
        String regex = "[a-zA-Z0-9]+@([a-zA-Z0-9]+\\.)+[a-z]{2,3}";
        return match(regex, str);
    }

    /**
     * 判断是否是年份<BR>
     * @param str 输入字符串
     * @return 是否是年份
     */
    public static boolean isYear(String str)
    {
        String regex = "[1-9][0-9]{3}";
        return match(regex, str);
    }

    /**
     * 判断是否是11位手机号码<BR>
     * @param str 输入字符串
     * @return 是否是11位手机号码
     */
    public static boolean isMobileNumeric(String str)
    {
        String regex = "[1][3]\\d{9}";
        return match(regex, str);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static void showHourAndMinute(int time, TextView v_h,
            TextView v_h_text, TextView v_m)
    {
        int h_time = time / 60;
        int m_time = time % 60;
        if (h_time > 0)
        {
            v_h.setVisibility(View.VISIBLE);
            v_h_text.setVisibility(View.VISIBLE);
            v_h.setText("" + h_time);
            v_m.setText("" + m_time);
        }
        else
        {
            v_h.setVisibility(View.GONE);
            v_h_text.setVisibility(View.GONE);
            v_m.setText("" + m_time);
        }
    }

    public static void showDistance(int distance, TextView v_value,
            TextView v_unit)
    {
        if (distance > 999)
        {
            v_value.setText(""
                    + OneDecimalFormat((float) (distance * 1.0 / 1000)));
            v_unit.setText("公里");
        }
        else
        {
            v_value.setText("" + distance);
            v_unit.setText("米");
        }
    }

    public static String TwoDecimalFormat(float v)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(v);
    }

    public static String OneDecimalFormat(float v)
    {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(v);
    }
}
