package com.smarthome.client2.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
    public static boolean isCN(String str)
    {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find())
        {
            temp = true;
        }
        return temp;
    }
}
