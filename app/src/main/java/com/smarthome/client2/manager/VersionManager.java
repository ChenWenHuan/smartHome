package com.smarthome.client2.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.smarthome.client2.config.Preferences;

public class VersionManager
{
    public static String getSofewareVersion(Context context)
    {
        try
        {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        }
        catch (NameNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "N/A";
    }

    public static int getSofewareVersionCode(Context context)
    {
        try
        {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        }
        catch (NameNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    public static String getFirmwareVersion(Context context)
    {
        long v = Preferences.getInstance(context).getDeviceInfo();
        if (0 == v)
        {
            return "N/A";
        }
        else
        {
            return "" + v;
        }
    }
}
