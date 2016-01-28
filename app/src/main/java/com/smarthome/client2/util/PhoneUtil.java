package com.smarthome.client2.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

public class PhoneUtil
{
    private Context ctx;

    public PhoneUtil(Context ctx)
    {
        this.ctx = ctx;
    }

    public static boolean isPhoneNumberValid(String phoneNumber)
    {
        boolean isValid = false;
        /*
         * 可接受的电话格式有：
         */
        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
        /*
         * 可接受的电话格式有：
         */
        String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        /*
         * 可接受的电话格式有：
         */
        String expression3 = "\\d{4}-\\d{8}|\\d{4}-\\d{7}|\\d(3)-\\d(8)";
        /*
         * 可接受的电话格式有：
         */
        String expression4 = "(?<!//d)((//+86-)?((0//d{2,3}//-)?//d{7,8}))(?!//d)";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);

        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(inputStr);

        Pattern pattern3 = Pattern.compile(expression3);
        Matcher matcher3 = pattern3.matcher(inputStr);

        Pattern pattern4 = Pattern.compile(expression4);
        Matcher matcher4 = pattern4.matcher(inputStr);
        if (matcher.matches() || matcher2.matches() || matcher3.matches()
                || matcher4.matches() || TextUtils.isEmpty(phoneNumber))
        {
            isValid = true;
        }
        return isValid;
    }

    private final static String SENT = "sms_sent";//发送成功接收到的回复

    private final static String DELIVERED = "sms_delivered";//短信接受者接受到短信的回复

    private BroadcastReceiver msgStatus = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Log.i("====>", "Activity.RESULT_OK");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.i("====>", "RESULT_ERROR_GENERIC_FAILURE");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Log.i("====>", "RESULT_ERROR_NO_SERVICE");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Log.i("====>", "RESULT_ERROR_NULL_PDU");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Log.i("====>", "RESULT_ERROR_RADIO_OFF");
                    break;
            }
        }
    };

    private BroadcastReceiver msgStatus2 = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Log.i("====>", "RESULT_OK");
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i("=====>", "RESULT_CANCELED");
                    break;
            }
        }
    };

    public void sendMSG(String number, String message)
    {

        PendingIntent sentPI = PendingIntent.getActivity(ctx, 0, new Intent(
                SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getActivity(ctx,
                0,
                new Intent(DELIVERED),
                0);
        ctx.registerReceiver(msgStatus, new IntentFilter(SENT));
        ctx.registerReceiver(msgStatus2, new IntentFilter(DELIVERED));
        SmsManager smsm = SmsManager.getDefault();
        smsm.sendTextMessage(number, null, message, sentPI, deliveredPI);
    }
    
    public void sendMSGNoReg(String number, String message)
    {

        PendingIntent sentPI = PendingIntent.getActivity(ctx, 0, new Intent(
                SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getActivity(ctx,
                0,
                new Intent(DELIVERED),
                0);
       
        SmsManager smsm = SmsManager.getDefault();
        smsm.sendTextMessage(number, null, message, sentPI, deliveredPI);
    }

    public void unRegisterBC()
    {
        ctx.unregisterReceiver(msgStatus);
        ctx.unregisterReceiver(msgStatus2);
    }
}
