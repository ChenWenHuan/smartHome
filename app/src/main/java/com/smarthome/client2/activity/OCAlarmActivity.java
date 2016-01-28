package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HomeListener;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.HomeListener.OnHomePressedListener;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class OCAlarmActivity extends Activity
{

    public final static int TYPE_ON = 1;

    public final static int TYPE_OFF = 2;

    public final static int TYPE_BOTH = 3;

    public final static int TYPE_FORBIDDEN = 4;

    private RelativeLayout ocAlarm_1;

    private RelativeLayout ocAlarm_2;

    private RelativeLayout ocAlarm_3;

    private RelativeLayout ocAlarm_4;

    private LinearLayout alarm_ele_layout, alarm_oc_layout;

    private ImageView ocalarm_img_1;

    private ImageView ocalarm_img_2;

    private ImageView ocalarm_img_3;

    private ImageView ocalarm_img_4;

    private ImageView low_power_ischeck;

    private LinearLayout main_ocalarm;

    private int status = 0;

    private FrameLayout fl_header_ocalarm_setup;

    private CustomActionBar actionBar;

    private int ocAlarmType = 0;

    private ProgressDialog mDialog;

    private HomeListener mHomeListener;

    private OnHomePressedListener mHomePressedListener = new OnHomePressedListener()
    {

        @Override
        public void onHomePressed()
        {
            if (mNetStatusListener != null)
            {
                mNetStatusListener.cancleToast();
            }
        }

        @Override
        public void onHomeLongPressed()
        {
            if (mNetStatusListener != null)
            {
                mNetStatusListener.cancleToast();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_oc_alarm);
        addTopBarToHead();
        initWidget();
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        if (mHomeListener != null)
        {
            mHomeListener.stopWatch();
        }
    }

    private void addTopBarToHead()
    {
        fl_header_ocalarm_setup = (FrameLayout) findViewById(R.id.fl_header_ocalarm_setup);
        actionBar = TopBarUtils.createCustomActionBar(this,
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                "告警设置",
                "保存",
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mNetStatusListener != null)
                        {
                            mNetStatusListener.cancleToast();
                        }
                        String type = Preferences.getInstance(getApplicationContext())
                                .getDeviceModel();
                        if (type.equalsIgnoreCase("gk309")
                                || type.equalsIgnoreCase("gs300"))
                        {
                            if (NetStatusListener.mClickflag)
                            {
                                //                                Toast.makeText(getApplicationContext(),
                                //                                        getString(R.string.netlistener_already_set),
                                //                                        Toast.LENGTH_SHORT)
                                //                                        .show();
                                return;
                            }
                            NetStatusListener.mClickflag = true;
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    OCAlarmActivity.this).setOnKeyListener(new OnKeyListener()
                            {
                                @Override
                                public boolean onKey(DialogInterface arg0,
                                        int keyCode, KeyEvent event)
                                {
                                    if (keyCode == KeyEvent.KEYCODE_BACK)
                                    {
                                        NetStatusListener.mClickflag = false;
                                    }
                                    return false;
                                }
                            })
                                    .setMessage(getString(R.string.netlistener_ask))
                                    .setNegativeButton("确定",
                                            new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which)
                                                {
                                                    setAlarmFromServer();
                                                    dialog.dismiss();
                                                }
                                            })
                                    .setPositiveButton("取消",
                                            new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which)
                                                {
                                                    dialog.dismiss();
                                                    NetStatusListener.mClickflag = false;
                                                }
                                            })
                                    .create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }
                        else
                        {
                            setAlarmFromServer();
                        }
                    }
                });
        fl_header_ocalarm_setup.addView(actionBar);
    }

    @Override
    public void finish()
    {
        String type = Preferences.getInstance(getApplicationContext())
                .getDeviceModel();
        if (type.equalsIgnoreCase("gk309") || type.equalsIgnoreCase("gs300"))
        {
            if (mNetStatusListener != null)
            {
                mNetStatusListener.setActivityFinish();
                NetStatusListener.mClickflag = false;
                mNetStatusListener.setRunning(false);
                mNetStatusListener.cancleToast();
            }
        }
        super.finish();
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener()
    {

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (mNetStatusListener != null)
            {
                mNetStatusListener.cancleToast();
            }
            return false;
        }
    };

    private void initWidget()
    {
        mHomeListener = new HomeListener(getApplicationContext());
        mHomeListener.setOnHomePressedListener(mHomePressedListener);
        mHomeListener.startWatch();
        main_ocalarm = (LinearLayout) findViewById(R.id.main_ocalarm);
        main_ocalarm.setOnTouchListener(mOnTouchListener);
        alarm_oc_layout = (LinearLayout) findViewById(R.id.alarm_oc_layout);
        alarm_ele_layout = (LinearLayout) findViewById(R.id.alarm_ele_layout);
        ocAlarm_1 = (RelativeLayout) findViewById(R.id.ocalarm_layout_1);
        ocAlarm_2 = (RelativeLayout) findViewById(R.id.ocalarm_layout_2);
        ocAlarm_3 = (RelativeLayout) findViewById(R.id.ocalarm_layout_3);
        ocAlarm_4 = (RelativeLayout) findViewById(R.id.ocalarm_layout_4);
        ocalarm_img_1 = (ImageView) findViewById(R.id.ocalarm_img_1);
        ocalarm_img_2 = (ImageView) findViewById(R.id.ocalarm_img_2);
        ocalarm_img_3 = (ImageView) findViewById(R.id.ocalarm_img_3);
        ocalarm_img_4 = (ImageView) findViewById(R.id.ocalarm_img_4);
        ocAlarm_1.setOnClickListener(ocAlarmListener);
        ocAlarm_2.setOnClickListener(ocAlarmListener);
        ocAlarm_3.setOnClickListener(ocAlarmListener);
        ocAlarm_4.setOnClickListener(ocAlarmListener);

        low_power_ischeck = (ImageView) findViewById(R.id.low_power_ischeck);
        low_power_ischeck.setOnClickListener(lowPowerListener);
        if (Preferences.getInstance(getApplicationContext()).getLowPower())
        {
            status = 1;
            low_power_ischeck.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
            //low_power_ischeck.setChecked(true);
        }
        else
        {
            status = 0;
            //low_power_ischeck.setChecked(false);
            low_power_ischeck.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        }

        ocAlarmType = Preferences.getInstance(getApplicationContext())
                .getOCAlarm();
        if (ocAlarmType != TYPE_ON && ocAlarmType != TYPE_OFF
                && ocAlarmType != TYPE_BOTH && ocAlarmType != TYPE_FORBIDDEN)
        {
            ocAlarmType = TYPE_FORBIDDEN;
        }
        switch (ocAlarmType)
        {
            case TYPE_ON:
                ocalarm_img_1.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case TYPE_OFF:
                ocalarm_img_2.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case TYPE_BOTH:
                ocalarm_img_3.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case TYPE_FORBIDDEN:
                ocalarm_img_4.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
        }

        Intent intent = getIntent();
        if (!intent.getBooleanExtra("ocAlarm", true))
        {
            alarm_oc_layout.setVisibility(View.GONE);
        }
        if (!intent.getBooleanExtra("electricity", true))
        {
            alarm_ele_layout.setVisibility(View.GONE);
        }
    }

    private OnClickListener lowPowerListener = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            if (mNetStatusListener != null)
            {
                mNetStatusListener.cancleToast();
            }

            if (status == 0)
            {
                low_power_ischeck.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                status = 1;
            }
            else
            {
                low_power_ischeck.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                status = 0;
            }
        }
    };

    private OnClickListener ocAlarmListener = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            if (mNetStatusListener != null)
            {
                mNetStatusListener.cancleToast();
            }
            ocalarm_img_1.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            ocalarm_img_2.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            ocalarm_img_3.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            ocalarm_img_4.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);

            switch (v.getId())
            {
                case R.id.ocalarm_layout_1:
                    ocalarm_img_1.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    ocAlarmType = TYPE_ON;
                    break;
                case R.id.ocalarm_layout_2:
                    ocalarm_img_2.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    ocAlarmType = TYPE_OFF;
                    break;
                case R.id.ocalarm_layout_3:
                    ocalarm_img_3.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    ocAlarmType = TYPE_BOTH;
                    break;
                case R.id.ocalarm_layout_4:
                    ocalarm_img_4.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    ocAlarmType = TYPE_FORBIDDEN;
                    break;
            }
        }
    };

    private final static int SET_ALARM = 1;

    private final static int SET_ALARM_SUCCESS = 200;

    private List<Integer> server_type = new ArrayList<Integer>();

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if (isFinishing())
            {
                return;
            }
            switch (msg.what)
            {
                case Constants.GET_DATA_SUCCESS:
                    switch (server_type.get(0))
                    {
                        case SET_ALARM:
                            Log.d("", "daitm----set alarm success");
                            String type = Preferences.getInstance(getApplicationContext())
                                    .getDeviceModel();
                            if (type.equalsIgnoreCase("gk309")
                                    || type.equalsIgnoreCase("gs300"))
                            {
                                mNetStatusListener.setGK309OCAlarmListener(mGK309OCAlarmListener);
                                mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
                                        OCAlarmActivity.this,
                                        mDialog);
                            }
                            else
                            {
                                if (mDialog != null && mDialog.isShowing())
                                {
                                    mDialog.dismiss();
                                }
                                showSuccessToast();
                            }
                            break;
                    }
                    server_type.remove(0);
                    break;
                case SET_ALARM_SUCCESS:
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(),
                            "设置成功,告警将会发送短信给SOS号码",
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.GET_DATA_FAIL:
                    break;
                case Constants.SET_NETLISENER_DATA_START:
                    mNetStatusListener = new NetStatusListener();
                    mDialog = new ProgressDialog(OCAlarmActivity.this);
                    mDialog.setMessage(getString(R.string.netlistener_set_data));
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setOnKeyListener(new OnKeyListener()
                    {
                        @Override
                        public boolean onKey(DialogInterface arg0, int keyCode,
                                KeyEvent event)
                        {
                            if (keyCode == KeyEvent.KEYCODE_BACK)
                            {
                                NetStatusListener.mClickflag = false;
                                if (mNetStatusListener != null
                                        && mNetStatusListener.isRunning())
                                {
                                    mNetStatusListener.setRunning(false);
                                }
                            }
                            return false;
                        }
                    });
                    mDialog.show();
                    ExceptionReciver.setNetLisenerDialog(mDialog);
                    break;
            }
        }
    };

    /**
     * 低电量警告 status:0 关闭告警 1开启告警
     */
    private void setAlarmFromServer()
    {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            NetStatusListener.mClickflag = false;
            return;
        }
        mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
        server_type.add(SET_ALARM);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("deviceId",
                    Preferences.getInstance(getApplicationContext())
                            .getDeviceId());
            obj.put("powerAlarm", status);
            obj.put("ocAlarm", ocAlarmType);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.SET_ALARM,
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

    private GK309OCAlarmListener mGK309OCAlarmListener = new GK309OCAlarmListener()
    {
        @Override
        public void getGK309OCAlarmSuccess()
        {
            showSuccessToast();
        }
    };

    public interface GK309OCAlarmListener
    {
        void getGK309OCAlarmSuccess();
    }

    private void showSuccessToast()
    {
        mHandler.sendEmptyMessage(SET_ALARM_SUCCESS);
        if (status == 1)
        {
            Preferences.getInstance(getApplicationContext()).setLowPower(true);
        }
        else
        {
            Preferences.getInstance(getApplicationContext()).setLowPower(false);
        }
        Preferences.getInstance(getApplicationContext())
                .setOCAlarm(ocAlarmType);
    }

    private NetStatusListener mNetStatusListener;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //        if (keyCode == event.KEYCODE_BACK)
        //        {
        if (mNetStatusListener != null
                && mNetStatusListener.getCustomToast() != null)
        {
            return mNetStatusListener.cancleToast();
        }
        //        }
        return super.onKeyDown(keyCode, event);
    }

}
