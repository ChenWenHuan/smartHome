package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.smarthome.client2.util.ScrollViewUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.HomeListener.OnHomePressedListener;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class GPSTypeOneActivity extends Activity
{

    public final static String GPS_TYPE_1_FILTER = "com.smarthome.client2.gps_type_1_time";

    public static int seriNum = 1;

    private CheckBox gprs_ischeck;

    private LinearLayout gprs_layout;

    private ListView gprs_list_listview;

    private LinearLayout gprs_list_tv;

    private GPRSAdapter gprsAdapter;

    private FrameLayout fl_header_gprs;

    private CustomActionBar actionBar;

    private ScrollView gprs_scroll;

    private int sund = 0;

    private int satd = 0;

    private int workd = 0;

    private int status = 0;

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
        setContentView(R.layout.activity_gps_type_1);
        AppManager.getAppManager().addActivity(this);
        initWidget();
        addTopBarToHead();
        DeviceStatusListener listener = new DeviceStatusListener(
                getApplicationContext(), Constants.TYPE_GPS_ACTION_5);
        listener.setGPSType1DeviceStatusListener(deviceStatusListener);
        listener.deviceStautsFromServer(Preferences.getInstance(getApplicationContext())
                .getDeviceId());
    }

    private void initSP()
    {
        if (Preferences.getInstance(getApplicationContext()).getGpsStatus() == 1)
        {
            gprs_ischeck.setChecked(true);
            gprs_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            gprs_ischeck.setChecked(false);
        }

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
        gprs_layout = (LinearLayout) findViewById(R.id.gprs_layout);
        gprs_list_tv = (LinearLayout) findViewById(R.id.gprs_list_tv);
        gprs_list_tv.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mNetStatusListener != null)
                {
                    mNetStatusListener.cancleToast();
                }
                if (workd >= 8 && satd >= 8 && sund >= 8)
                {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.gps_24_gps),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(GPSTypeOneActivity.this,
                        GPSTypeOneTimePickActivity.class));
            }
        });

        gprs_scroll = (ScrollView) findViewById(R.id.gprs_scroll);
        gprs_scroll.setOnTouchListener(mOnTouchListener);
        gprs_list_listview = (ListView) findViewById(R.id.gprs_list_listview);
        gprsAdapter = new GPRSAdapter();
        gprs_list_listview.setAdapter(gprsAdapter);

        gprs_list_listview.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> viewgroup, View v, int pos,
                    long arg3)
            {
                // String[] times = Preferences
                // .getInstance(getApplicationContext())
                // .getDisturbTime().split(",");
                if (mNetStatusListener != null)
                {
                    mNetStatusListener.cancleToast();
                }
                Bundle b = new Bundle();
                if (gprsTime.get(pos).split(" ").length == 3)
                {
                    b.putString("editId", gprsTime.get(pos).split(" ")[2]);
                }
                b.putString("editGPRSWeekday", gprsTime.get(pos).split(" ")[1]);
                b.putString("editGPRSTime", gprsTime.get(pos).split(" ")[0]);
                b.putInt("editGPRSTime_pos", pos);
                Intent intent = new Intent();
                intent.putExtras(b);
                intent.setClass(getApplicationContext(),
                        GPSTypeOneTimePickActivity.class);
                startActivity(intent);
            }
        });

        gprs_ischeck = (CheckBox) findViewById(R.id.gprs_ischeck);
        gprs_ischeck.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mNetStatusListener != null)
                {
                    mNetStatusListener.cancleToast();
                }
                if (gprs_ischeck.isChecked())
                {
                    gprs_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    gprs_layout.setVisibility(View.GONE);
                }
            }
        });

        registerReceiver(gprsReceiver, new IntentFilter(GPS_TYPE_1_FILTER));

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
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        if (gprsReceiver != null)
        {
            unregisterReceiver(gprsReceiver);
        }
        if (mHomeListener != null)
        {
            mHomeListener.stopWatch();
        }
    }

    private void addTopBarToHead()
    {
        fl_header_gprs = (FrameLayout) findViewById(R.id.fl_header_gprs);
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
                getString(R.string.title_gps),
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
                                GPSTypeOneActivity.this).setOnKeyListener(new OnKeyListener()
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
                                                if (gprs_ischeck.isChecked())
                                                {
                                                    setGPSFromServer(Preferences.getInstance(getApplicationContext())
                                                            .getDeviceId(),
                                                            1);
                                                    status = 1;
                                                }
                                                else
                                                {
                                                    setGPSFromServer(Preferences.getInstance(getApplicationContext())
                                                            .getDeviceId(),
                                                            0);
                                                    status = 0;
                                                }
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
        fl_header_gprs.addView(actionBar);
    }

    private List<String> gprsTime = new ArrayList<String>();

    class GPRSAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return gprsTime.size();
        }

        @Override
        public Object getItem(int pos)
        {
            return gprsTime.get(pos);
        }

        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup viewgroup)
        {

            view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.location_setting_disrupt_listview_item,
                            null);
            Getter getter;
            if (view != null)
            {
                getter = new Getter();
                getter.location_setting_starttime = (TextView) view.findViewById(R.id.location_setting_starttime);
                getter.location_setting_endtime = (TextView) view.findViewById(R.id.location_setting_endtime);
                getter.location_setting_weekday = (TextView) view.findViewById(R.id.location_setting_weekday);
                view.setTag(getter);
            }
            else
                getter = (Getter) view.getTag();

            String starttime = gprsTime.get(pos).split(" ")[0].split("-")[0];
            String endtime = gprsTime.get(pos).split(" ")[0].split("-")[1];
            getter.location_setting_starttime.setText(starttime);
            getter.location_setting_endtime.setText(endtime);
            getter.location_setting_weekday.setText(translateIntoWeekday(sortWeekDay(gprsTime.get(pos)
                    .split(" ")[1])));

            return view;
        }

        public class Getter
        {
            private TextView location_setting_starttime,
                    location_setting_endtime, location_setting_weekday;
        }
    }

    private String sortWeekDay(String weekday)
    {
        String result = "";
        String[] ss = weekday.split(",");
        List<Integer> tmp = new ArrayList<Integer>();
        for (String s : ss)
        {
            tmp.add(Integer.parseInt(s));
        }
        Collections.sort(tmp);
        for (int sort : tmp)
        {
            result = sort + "-" + result;
        }
        return result.substring(0, result.length() - 1);
    }

    private String translateIntoWeekday(String indexList)
    {
        String result = "";
        String[] ss = indexList.split("-");
        for (String s : ss)
        {
            switch (Integer.parseInt(s))
            {
                case 1:
                    result = getString(R.string.common_day8) + "-" + result;
                    break;
                case 6:
                    result = getString(R.string.common_day6) + "-" + result;
                    break;
                case 7:
                    result = getString(R.string.common_day7) + "-" + result;
                    break;
            }
        }
        return result.substring(0, result.length() - 1);
    }

    private final static int SET_GPRS = 1;

    private final static int GET_GPRS = 2;

    private final static int ADD_GPRS = 3;

    private ProgressDialog dialog = null;

    private List<Integer> serverType = new ArrayList<Integer>();

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
                case Constants.GET_DATA_START:
                    dialog = new ProgressDialog(GPSTypeOneActivity.this);
                    dialog.setMessage(getString(R.string.gps_ready_to_get_info));
                    dialog.show();

                    if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
                    {
                        if (dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    HttpUtil.responseHandler(getApplicationContext(),
                                            Constants.NO_NETWORK),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                        return;
                    }
                    break;
                case Constants.GET_DATA_SUCCESS:
                    switch (serverType.get(0))
                    {
                        case SET_GPRS:
                            //                            if (dialog != null && dialog.isShowing())
                            //                            {
                            //                                dialog.dismiss();
                            //                            }
                            mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
                                    GPSTypeOneActivity.this,
                                    dialog);
                            break;
                        case GET_GPRS:
                            try
                            {
                                gprsTime.clear();
                                workd = 0;
                                satd = 0;
                                sund = 0;
                                String gpsTimes = "";
                                JSONObject json = new JSONObject(
                                        msg.obj.toString());
                                JSONArray array = json.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++)
                                {
                                    JSONObject obj = array.getJSONObject(i);
                                    //                                    int id = obj.getInt("id");
                                    String begin_time = obj.getString("begin_time");
                                    String end_time = obj.getString("end_time");
                                    JSONArray weekdays = obj.getJSONArray("week_day");
                                    String week_day = "";
                                    for (int j = 0; j < weekdays.length(); j++)
                                    {
                                        if (weekdays.getInt(j) == 1)
                                        {
                                            workd++;
                                        }
                                        else if (weekdays.getInt(j) == 6)
                                        {
                                            satd++;
                                        }
                                        else
                                        {
                                            sund++;
                                        }
                                        week_day = weekdays.get(j) + ","
                                                + week_day;
                                    }
                                    // 6:30-7:30 周一
                                    String item = begin_time
                                            + "-"
                                            + end_time
                                            + " "
                                            + week_day.substring(0,
                                                    week_day.length() - 1);
                                    gprsTime.add(item);
                                    gpsTimes = gpsTimes + "--" + item;
                                }
                                if (array.length() != 0)
                                {
                                    Preferences.getInstance(getApplicationContext())
                                            .setGPRSNum(workd + "-" + satd
                                                    + "-" + sund);
                                    Preferences.getInstance(getApplicationContext())
                                            .setGPSTime(gpsTimes.substring(2));
                                }
                                else
                                {
                                    Preferences.getInstance(getApplicationContext())
                                            .setGPRSNum("");
                                    Preferences.getInstance(getApplicationContext())
                                            .setGPSTime("");
                                }
                                if (gprsTime.size() > 0)
                                {
                                    // gprs_ischeck.setChecked(true);
                                    // gprs_layout.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    // gprs_ischeck.setChecked(false);
                                    // gprs_layout.setVisibility(View.GONE);
                                }
                                gprsAdapter.notifyDataSetChanged();
                                if (dialog != null)
                                {
                                    dialog.setMessage(getString(R.string.gps_receive_info_success));
                                    dialog.dismiss();
                                }
                                ScrollViewUtil.setListViewHeightBasedOnChildren(gprs_list_listview);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        case ADD_GPRS:
                            break;
                    }
                    serverType.remove(0);
                    break;
                case Constants.GET_DATA_FAIL:
                    if (dialog != null)
                    {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.gps_receive_info_fail),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.SET_NETLISENER_DATA_START:
                    mNetStatusListener = new NetStatusListener();
                    dialog = new ProgressDialog(GPSTypeOneActivity.this);
                    dialog.setMessage(getString(R.string.netlistener_set_data));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setOnKeyListener(new OnKeyListener()
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
                    dialog.show();
                    ExceptionReciver.setNetLisenerDialog(dialog);
                    break;
            }
        }
    };

    private void setGPSFromServer(int deviceId, int status)
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
        serverType.add(SET_GPRS);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("deviceId", deviceId);
            obj.put("status", status);
            //            obj.put("interval", locationInterval);
            JSONArray gps = new JSONArray();
            if (!TextUtils.isEmpty(Preferences.getInstance(getApplicationContext())
                    .getGPSTime()))
            {
                String[] array = Preferences.getInstance(getApplicationContext())
                        .getGPSTime()
                        .split("--");
                for (String item : array)
                {
                    JSONObject addItem = new JSONObject();
                    addItem.put("begin_time", item.split(" ")[0].split("-")[0]);
                    addItem.put("end_time", item.split(" ")[0].split("-")[1]);
                    JSONArray weeks = new JSONArray();
                    String[] week_day = item.split(" ")[1].split(",");
                    for (String disrupt : week_day)
                    {
                        weeks.put(disrupt);
                    }
                    addItem.put("week_day", weeks);
                    gps.put(addItem);
                }
            }
            obj.put("gpstime", gps);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.SET_GPRS,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private void getGPRSFromServer()
    {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler.sendEmptyMessage(Constants.GET_DATA_START);
        serverType.add(GET_GPRS);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("deviceId",
                    Preferences.getInstance(getApplicationContext())
                            .getDeviceId());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.GET_GPRS,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private BroadcastReceiver gprsReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context ctx, Intent intent)
        {
            if (intent.getAction().equals(GPS_TYPE_1_FILTER))
            {
                //                getGPRSFromServer();
                gprsTime.clear();
                workd = 0;
                satd = 0;
                sund = 0;
                String array = Preferences.getInstance(getApplicationContext())
                        .getGPSTime();
                if (!TextUtils.isEmpty(array))
                {
                    String[] part = array.split("--");
                    for (int i = 0; i < part.length; i++)
                    {
                        String begin_time = part[i].split(" ")[0].split("-")[0];
                        String end_time = part[i].split(" ")[0].split("-")[1];
                        String[] weekdays = part[i].split(" ")[1].split(",");
                        String week_day = "";
                        for (int j = 0; j < weekdays.length; j++)
                        {
                            if (Integer.parseInt(weekdays[j]) == 1)
                            {
                                workd++;
                            }
                            else if (Integer.parseInt(weekdays[j]) == 6)
                            {
                                satd++;
                            }
                            else
                            {
                                sund++;
                            }
                            week_day = weekdays[j] + "," + week_day;
                        }
                        // 6:30-7:30 周一
                        String item = begin_time + "-" + end_time + " "
                                + week_day.substring(0, week_day.length() - 1);
                        gprsTime.add(item);
                    }
                }
                Preferences.getInstance(getApplicationContext())
                        .setGPRSNum(workd + "-" + satd + "-" + sund);
                gprsAdapter.notifyDataSetChanged();
                ScrollViewUtil.setListViewHeightBasedOnChildren(gprs_list_listview);
            }
        }
    };

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
        Preferences.getInstance(getApplicationContext()).setGPSTime("");
        Preferences.getInstance(getApplicationContext()).setGPRSNum("");
        super.finish();
    }

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

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    private int locationInterval = 0;// 上传间隔时间

    private OnClickListener intervalListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
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

    private GPSType1DeviceStatusListener deviceStatusListener = new GPSType1DeviceStatusListener()
    {

        @Override
        public void getListener()
        {
            initSP();
            getGPRSFromServer();
        }
    };

    public interface GPSType1DeviceStatusListener
    {
        void getListener();
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
