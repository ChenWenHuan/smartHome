package com.smarthome.client2.model.retrofitServices;

import android.content.Context;

import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.config.Preferences;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;

import android.util.Log;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.util.HttpUtil;

/**
 * Created by wenhuanchen on 11/23/15.
 */
public class MyCookeiManager extends CookieManager {

    @Override
    public void put(URI uri, Map<String, List<String>> stringListMap) throws IOException {
        super.put(uri, stringListMap);
        //String strSessionID =
        if (stringListMap != null && stringListMap.get("Set-Cookie") != null) {
//            int len = stringListMap.size();
//            for (int i =0 ;i < len;i++) {
//                String str = stringListMap.
//            }
            log("MyCookeiManager SessionID stringListMap is " + stringListMap);
            for (String string : stringListMap.get("Set-Cookie")) {
                if (string.contains("JSESSIONID")) {
                    string = string.substring(string.indexOf("=") + 1, string.indexOf(";"));
                    log("MyCookeiManager SessionID is " + string);
                    SmartHomeApplication smartHomeApp = SmartHomeApplication.getInstance();
                    Context mContext = smartHomeApp.getApplicationContext();
                    Preferences.getInstance(mContext.getApplicationContext())
                            .setJessionId(string);
                    //以下代码是为了兼容老的网络接口
                    HttpUtil.setJessionId(string);
                    //Preference.getInstance().setSessionId(string);
                }
            }
        }
    }

    private static final boolean DEBUG = true;
    private static void log(String msg) {
        if (DEBUG) {
            TLog.Log(msg);
        }
    }
}
