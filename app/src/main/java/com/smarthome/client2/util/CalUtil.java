package com.smarthome.client2.util;

import java.util.Calendar;

public class CalUtil
{
    public static int genCalAim(int gender, float weight, int height, int age)
    {
        int cal = 0;
        Calendar c = Calendar.getInstance();
        //female
        if (gender == 0)
        {
            cal = (int) (65 + 9.6 * weight + 1.7 * height - 4.7 * age);
        }
        else if (gender == 1)
        {
            cal = (int) (66 + 13.7 * weight + 5 * height - 6.8 * age);
        }

        return cal;
    }

    public static int genCalAim_v2(int gender, float weight, int height, int age)
    {
        int cal = 0;
        Calendar c = Calendar.getInstance();
        //female
        if (gender == 0)
        {
            cal = (int) (65 + 4.8 * weight + 1.7 * height - 4.7 * age);
        }
        else if (gender == 1)
        {
            cal = (int) (66 + 6.9 * weight + 2.3 * height - 6.8 * age);
        }

        return cal;
    }
}
