package com.smarthome.client2.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;

public class LowPowerActivity extends Activity
{

    private CheckBox low_power_ischeck;

    private ProgressBar low_power_progress;

    private FrameLayout fl_header_lowpower_activity;

    private CustomActionBar actionBar;

    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lowpower);
        addTopBarToHead();

        low_power_ischeck = (CheckBox) findViewById(R.id.low_power_ischeck);
        low_power_progress = (ProgressBar) findViewById(R.id.low_power_progress);
        low_power_progress.setMax(100);
        low_power_progress.setProgress(Preferences.getInstance(getApplicationContext())
                .getCurrPower());
        low_power_ischeck.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean isClick)
            {
                if (isClick)
                {
                    status = 1;
                    //					startMonitorBatteryState();
                }
                else
                {
                    status = 0;
                    //					closeMonitorBatteryState();
                }
            }
        });

        if (Preferences.getInstance(getApplicationContext()).getLowPower())
        {
            status = 1;
            low_power_ischeck.setChecked(true);
        }
        AppManager.getAppManager().addActivity(this);
    }

    private void addTopBarToHead()
    {
        fl_header_lowpower_activity = (FrameLayout) findViewById(R.id.fl_header_lowpower_activity);
        actionBar = TopBarUtils.createCustomActionBar(this,
                R.drawable.btn_back_selector,
                new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        setPowerAlarmFromServer(Preferences.getInstance(getApplicationContext())
                                .getDeviceId(),
                                status);
                        finish();
                    }
                },
                "电量剩余",
                null,
                null);
        fl_header_lowpower_activity.addView(actionBar);
    }

    private BroadcastReceiver batteryLevelRcvr;

    private IntentFilter batteryLevelFilter;

    /**
     * 监听电池状态
     */
    private void startMonitorBatteryState()
    {
        batteryLevelRcvr = new BroadcastReceiver()
        {

            public void onReceive(Context context, Intent intent)
            {
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0)
                {
                    level = (rawlevel * 100) / scale;
                }
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health)
                {
                }
                else
                {
                    switch (status)
                    {
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            break;
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            Log.d("", "daitm-------battery is charging");
                            // Toast.makeText(getApplicationContext(),
                            // "battery level:"+level, Toast.LENGTH_SHORT).show();
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                            Log.d("", "daitm-------battery is discharging");
                            break;
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            if (level == 0)
                            {
                                Log.d("", "daitm-------battery is empty");
                            }
                            else if (level > 0 && level <= 33)
                            {
                                Log.d("",
                                        "daitm-------battery need to be charged");
                            }
                            else
                            {
                                Log.d("", "daitm-------battery is full");
                            }
                            // Toast.makeText(getApplicationContext(),
                            // "battery level:"+level, Toast.LENGTH_SHORT).show();
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:
                            Log.d("", "daitm-------battery is full");
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //		registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        //		closeMonitorBatteryState();
    }

    /**
     * 关闭监听电池状态
     */
    private void closeMonitorBatteryState()
    {
        if (batteryLevelRcvr != null)
        {
            unregisterReceiver(batteryLevelRcvr);
        }
    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_SUCCESS:
                    Log.d("", "daitm--------设置低电量成功");
                    if (status == 1)
                    {
                        Preferences.getInstance(getApplicationContext())
                                .setLowPower(true);
                    }
                    else
                    {
                        Preferences.getInstance(getApplicationContext())
                                .setLowPower(false);
                    }
                    break;
                case Constants.GET_DATA_FAIL:
                    break;
            }
        }
    };

    /**
     * 低电量警告 status:0 关闭告警 1开启告警
     */
    private void setPowerAlarmFromServer(int deviceId, int status)
    {
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("deviceId", deviceId);
            obj.put("status", status);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.SET_POWER_ALARM,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
