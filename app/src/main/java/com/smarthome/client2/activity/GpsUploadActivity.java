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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.DeviceStatusListener;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HomeListener;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.HomeListener.OnHomePressedListener;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class GpsUploadActivity extends Activity
{

    private RelativeLayout location_interval_1;

    private RelativeLayout location_interval_2;

    private RelativeLayout location_interval_3;

    private RelativeLayout location_interval_4;

    private RelativeLayout location_interval_5;

    private RelativeLayout location_interval_6;

    private RelativeLayout location_interval_7;

    private ImageView location_interval_img_1;

    private ImageView location_interval_img_2;

    private ImageView location_interval_img_3;

    private ImageView location_interval_img_4;

    private ImageView location_interval_img_5;

    private ImageView location_interval_img_6;

    private ImageView location_interval_img_7;

    private FrameLayout fl_header_location_interval_head;

    private LinearLayout main_gps_setup;

    private CustomActionBar actionBar;

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
        setContentView(R.layout.interval_pick_layout);
        addTopBarToHead();
        AppManager.getAppManager().addActivity(this);
        DeviceStatusListener listener = new DeviceStatusListener(
                getApplicationContext(), Constants.TYPE_GPS_ACTION_3);
        listener.setGPSUPDeviceStatusListener(deviceStatusListener);
        listener.deviceStautsFromServer(Preferences.getInstance(getApplicationContext())
                .getDeviceId());
    }

    @Override
    public void finish()
    {
        if (mNetStatusListener != null)
        {
            mNetStatusListener.setActivityFinish();
            NetStatusListener.mClickflag = false;
            mNetStatusListener.setRunning(false);
            mNetStatusListener.cancleToast();
        }
        super.finish();
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
        fl_header_location_interval_head = (FrameLayout) findViewById(R.id.fl_header_location_interval_head);
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
                getString(R.string.title_gps_upload),
                getString(R.string.common_btn_save),
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mNetStatusListener != null)
                        {
                            mNetStatusListener.cancleToast();
                        }
                        if (NetStatusListener.mClickflag)
                        {
                            //                            Toast.makeText(getApplicationContext(),
                            //                                    getString(R.string.netlistener_already_set),
                            //                                    Toast.LENGTH_SHORT)
                            //                                    .show();
                            return;
                        }
                        NetStatusListener.mClickflag = true;
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                GpsUploadActivity.this).setOnKeyListener(new OnKeyListener()
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
                                .setNegativeButton(getString(R.string.common_btn_yes),
                                        new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which)
                                            {
                                                setLocationIntervalFromServer();
                                                dialog.dismiss();
                                            }
                                        })
                                .setPositiveButton(getString(R.string.common_btn_no),
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
                });
        fl_header_location_interval_head.addView(actionBar);
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
        main_gps_setup = (LinearLayout) findViewById(R.id.main_gps_setup);
        main_gps_setup.setOnTouchListener(mOnTouchListener);
        location_interval_1 = (RelativeLayout) findViewById(R.id.location_interval_1);
        location_interval_2 = (RelativeLayout) findViewById(R.id.location_interval_2);
        location_interval_3 = (RelativeLayout) findViewById(R.id.location_interval_3);
        location_interval_4 = (RelativeLayout) findViewById(R.id.location_interval_4);
        location_interval_5 = (RelativeLayout) findViewById(R.id.location_interval_5);
        location_interval_6 = (RelativeLayout) findViewById(R.id.location_interval_6);
        location_interval_7 = (RelativeLayout) findViewById(R.id.location_interval_7);
        location_interval_img_1 = (ImageView) findViewById(R.id.location_interval_img_1);
        location_interval_img_2 = (ImageView) findViewById(R.id.location_interval_img_2);
        location_interval_img_3 = (ImageView) findViewById(R.id.location_interval_img_3);
        location_interval_img_4 = (ImageView) findViewById(R.id.location_interval_img_4);
        location_interval_img_5 = (ImageView) findViewById(R.id.location_interval_img_5);
        location_interval_img_6 = (ImageView) findViewById(R.id.location_interval_img_6);
        location_interval_img_7 = (ImageView) findViewById(R.id.location_interval_img_7);
        location_interval_1.setOnClickListener(intervalListener);
        location_interval_2.setOnClickListener(intervalListener);
        location_interval_3.setOnClickListener(intervalListener);
        location_interval_4.setOnClickListener(intervalListener);
        location_interval_5.setOnClickListener(intervalListener);
        location_interval_6.setOnClickListener(intervalListener);
        location_interval_7.setOnClickListener(intervalListener);

        locationInterval = Preferences.getInstance(getApplicationContext())
                .getGpsInterval();
        location_interval_img_1.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        location_interval_img_2.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        location_interval_img_3.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        location_interval_img_4.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        location_interval_img_5.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        location_interval_img_6.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        location_interval_img_7.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        switch (locationInterval)
        {
            case 0:
                location_interval_img_1.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case 3:
                location_interval_img_2.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case 5:
                location_interval_img_3.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case 10:
                location_interval_img_4.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case 30:
                location_interval_img_5.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case 60:
                location_interval_img_6.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
            case 120:
                location_interval_img_7.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                break;
        }

    }

    private int locationInterval = 0;// 上传间隔时间

    private OnClickListener intervalListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mNetStatusListener != null)
            {
                mNetStatusListener.cancleToast();
            }
            locationInterval = 0;
            location_interval_img_1.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            location_interval_img_2.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            location_interval_img_3.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            location_interval_img_4.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            location_interval_img_5.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            location_interval_img_6.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            location_interval_img_7.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
            switch (v.getId())
            {
                case R.id.location_interval_1:
                    location_interval_img_1.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    locationInterval = 0;
                    break;
                case R.id.location_interval_2:
                    location_interval_img_2.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    locationInterval = 3;
                    break;
                case R.id.location_interval_3:
                    location_interval_img_3.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    locationInterval = 5;
                    break;
                case R.id.location_interval_4:
                    location_interval_img_4.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    locationInterval = 10;
                    break;
                case R.id.location_interval_5:
                    location_interval_img_5.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    locationInterval = 30;
                    break;
                case R.id.location_interval_6:
                    location_interval_img_6.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    locationInterval = 60;
                    break;
                case R.id.location_interval_7:
                    location_interval_img_7.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
                    locationInterval = 120;
                    break;
            }
        }
    };

    private final static int SET_LOCATION_INTERVAL = 1;

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
                        case SET_LOCATION_INTERVAL:
                            Log.d("", "设置间隔时间成功");
                            //                            if (mDialog != null && mDialog.isShowing())
                            //                            {
                            //                                mDialog.dismiss();
                            //                            }
                            mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
                                    GpsUploadActivity.this,
                                    mDialog);
                            break;
                    }
                    server_type.remove(0);
                    break;
                case Constants.GET_DATA_FAIL:
                    break;
                case Constants.SET_NETLISENER_DATA_START:
                    mNetStatusListener = new NetStatusListener();
                    mDialog = new ProgressDialog(GpsUploadActivity.this);
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
     * 定位间隔
     */
    private void setLocationIntervalFromServer()
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
        server_type.add(SET_LOCATION_INTERVAL);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("deviceId",
                    Preferences.getInstance(getApplicationContext())
                            .getDeviceId());
            obj.put("interval", locationInterval);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.SET_GPS_UP_INTERVAL,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private GPSUPDeviceStatusListener deviceStatusListener = new GPSUPDeviceStatusListener()
    {

        @Override
        public void getListener()
        {
            initWidget();
        }
    };

    public interface GPSUPDeviceStatusListener
    {
        void getListener();
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
