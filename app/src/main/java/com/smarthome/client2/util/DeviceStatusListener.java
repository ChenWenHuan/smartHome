package com.smarthome.client2.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.smarthome.client2.activity.GPSTypeOneActivity.GPSType1DeviceStatusListener;
import com.smarthome.client2.activity.GPSTypeTwoActivity.GPSType2DeviceStatusListener;
import com.smarthome.client2.activity.GpsSetUpActivity.GPSDeviceStatusListener;
import com.smarthome.client2.activity.GpsUploadActivity.GPSUPDeviceStatusListener;
import com.smarthome.client2.activity.GpsUploadType2Activity.GPSUPType2DeviceStatusListener;
import com.smarthome.client2.activity.LBSSetUpActivity.LBSDeviceStatusListener;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;

public class DeviceStatusListener
{
    private Context ctx;

    private int type;

    public DeviceStatusListener(Context ctx, int type)
    {
        this.ctx = ctx;
        this.type = type;
    }

    private Handler mHandlerDevice = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_SUCCESS:
                    try
                    {
                        JSONObject json = new JSONObject(msg.obj.toString());
                        if (json.has("data"))
                        {
                            JSONObject data = json.getJSONObject("data");
                            ///////////////////nongpsInterval/////////////////////
                            if (!TextUtils.isEmpty(data.getString("nongpsInterval")))
                            {
                                int nongpsInterval = data.getInt("nongpsInterval");
                                Preferences.getInstance(ctx)
                                        .setNongpsIntervalStatus(nongpsInterval);
                            }
                            ///////////////////nongpsInterval/////////////////////

                            ///////////////////silence/////////////////////
                            if (!TextUtils.isEmpty(data.getString("silenceStatus")))
                            {
                                int silenceStatus = data.getInt("silenceStatus");
                                Preferences.getInstance(ctx)
                                        .setSilenceStatus(silenceStatus);
                            }
                            ///////////////////silence/////////////////////

                            ///////////////////whiteSpace/////////////////////
                            if (!TextUtils.isEmpty(data.getString("whiteSpace")))
                            {
                                int whiteSpaceType = data.getInt("whiteSpace");
                                Preferences.getInstance(ctx)
                                        .setWhiteType(whiteSpaceType);
                            }
                            ///////////////////whiteSpace/////////////////////

                            ///////////////////powerAlarm/////////////////////
                            if (!TextUtils.isEmpty(data.getString("powerAlarm")))
                            {
                                int powerAlarm = data.getInt("powerAlarm");
                                Preferences.getInstance(ctx)
                                        .setLowPower(powerAlarm == 1 ? true
                                                : false);
                                int currPower = data.getInt("currPower");
                                Preferences.getInstance(ctx)
                                        .setCurrPower(currPower);
                            }
                            ///////////////////powerAlarm/////////////////////

                            ///////////////////clock/////////////////////
                            if (!TextUtils.isEmpty(data.getString("clockStatus")))
                            {
                                int clockStatus = data.getInt("clockStatus");
                                Preferences.getInstance(ctx)
                                        .setAlarmClockStatus(clockStatus);
                            }
                            ///////////////////clock/////////////////////

                            ///////////////////ocAlarm/////////////////////
                            if (!TextUtils.isEmpty(data.getString("ocAlarm")))
                            {
                                int ocAlarm = data.getInt("ocAlarm");
                                Preferences.getInstance(ctx)
                                        .setOCAlarm(ocAlarm);
                            }
                            ///////////////////ocAlarm/////////////////////

                            ///////////////////gpsStatus/////////////////////
                            if (!TextUtils.isEmpty(data.getString("gpsStatus")))
                            {
                                int gpsStatus = data.getInt("gpsStatus");
                                Preferences.getInstance(ctx)
                                        .setGpsStatus(gpsStatus);
                            }
                            ///////////////////gpsStatus/////////////////////

                            ///////////////////gpsInterval/////////////////////
                            if (!TextUtils.isEmpty(data.getString("gpsInterval")))
                            {
                                int gpsInterval = data.getInt("gpsInterval");
                                Preferences.getInstance(ctx)
                                        .setGpsInterval(gpsInterval);
                            }
                            ///////////////////gpsStatus/////////////////////

                            ///////////////////lbsInterval/////////////////////
                            if (!TextUtils.isEmpty(data.getString("lbsInterval")))
                            {
                                int lbsInterval = data.getInt("lbsInterval");
                                Preferences.getInstance(ctx)
                                        .setLbsInterval(lbsInterval);
                            }
                            ///////////////////lbsInterval/////////////////////

                            switch (type)
                            {
                                case Constants.TYPE_GPS_ACTION_1:
                                    mGpsDeviceStatusListener.getListener();
                                    break;
                                case Constants.TYPE_GPS_ACTION_2:
                                    mLbsDeviceStatusListener.getListener();
                                    break;
                                case Constants.TYPE_GPS_ACTION_3:
                                    mGpsupDeviceStatusListener.getListener();
                                    break;
                                case Constants.TYPE_GPS_ACTION_4:
                                    mGpsupType2DeviceStatusListener.getListener();
                                    break;
                                case Constants.TYPE_GPS_ACTION_5:
                                    mGPSType1DeviceStatusListener.getListener();
                                    break;
                                case Constants.TYPE_GPS_ACTION_6:
                                    mGPSType2DeviceStatusListener.getListener();
                                    break;
                            }
                            ctx.sendBroadcast(new Intent());
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;

                case Constants.GET_DEVICE_DATA_FAIL:

                    break;
            }
        }
    };

    /**
     * 获取设备所有信息
     */
    public void deviceStautsFromServer(int deviceId)
    {
        if (!HttpUtil.isNetworkAvailable(ctx))
        {
            Toast.makeText(ctx,
                    HttpUtil.responseHandler(ctx, Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("deviceId", deviceId);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.DEVICE_STATUS,
                mHandlerDevice,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DEVICE_DATA_FAIL);
    }

    private GPSDeviceStatusListener mGpsDeviceStatusListener;

    public void setGPSDeviceStatusListener(GPSDeviceStatusListener listener)
    {
        this.mGpsDeviceStatusListener = listener;
    }

    private LBSDeviceStatusListener mLbsDeviceStatusListener;

    public void setLBSDeviceStatusListener(LBSDeviceStatusListener listener)
    {
        this.mLbsDeviceStatusListener = listener;
    }

    private GPSUPDeviceStatusListener mGpsupDeviceStatusListener;

    public void setGPSUPDeviceStatusListener(GPSUPDeviceStatusListener listener)
    {
        this.mGpsupDeviceStatusListener = listener;
    }

    private GPSUPType2DeviceStatusListener mGpsupType2DeviceStatusListener;

    public void setGPSUPType2DeviceStatusListener(
            GPSUPType2DeviceStatusListener listener)
    {
        this.mGpsupType2DeviceStatusListener = listener;
    }

    private GPSType1DeviceStatusListener mGPSType1DeviceStatusListener;

    public void setGPSType1DeviceStatusListener(
            GPSType1DeviceStatusListener listener)
    {
        this.mGPSType1DeviceStatusListener = listener;
    }

    private GPSType2DeviceStatusListener mGPSType2DeviceStatusListener;

    public void setGPSType2DeviceStatusListener(
            GPSType2DeviceStatusListener listener)
    {
        this.mGPSType2DeviceStatusListener = listener;
    }

}
