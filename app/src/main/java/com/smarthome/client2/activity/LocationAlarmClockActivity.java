package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HomeListener;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.ScrollViewUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.HomeListener.OnHomePressedListener;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class LocationAlarmClockActivity extends Activity {

    public final static String ALARM_FILTER = "com.smarthome.client2.alarmtime";

    private CheckBox location_alarm_checkbox;

    private ListView location_alarm_listview;

    private LinearLayout location_alarm_tv;

    private ScrollView main_alarm;

    private AlarmClockAdapter alarmClockAdapter;

    private FrameLayout fl_header_alarm_clock;

    private CustomActionBar actionBar;

    private List<String> alarmList = new ArrayList<String>();

    private int status = 0;

    private HomeListener mHomeListener;
    private long mNDevId;


    private OnHomePressedListener mHomePressedListener = new OnHomePressedListener() {

        @Override
        public void onHomePressed() {
            if (mNetStatusListener != null) {
                mNetStatusListener.cancleToast();
            }
        }

        @Override
        public void onHomeLongPressed() {
            if (mNetStatusListener != null) {
                mNetStatusListener.cancleToast();
            }
        }
    };

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(ALARM_FILTER)) {
                initAlarmList();
            }
        }
    };

    private void addTopBarToHead() {
        fl_header_alarm_clock = (FrameLayout) findViewById(R.id.fl_header_alarm_clock);
        actionBar = TopBarUtils.createCustomActionBar(this,
                R.drawable.btn_back_selector,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                getString(R.string.title_alarm),
                getString(R.string.common_btn_save),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mNetStatusListener != null) {
                            mNetStatusListener.cancleToast();
                        }
                        if (NetStatusListener.mClickflag) {
                            //                            Toast.makeText(getApplicationContext(),
                            //                                    getString(R.string.netlistener_already_set),
                            //                                    Toast.LENGTH_SHORT)
                            //                                    .show();
                            return;
                        }
                        NetStatusListener.mClickflag = true;
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                LocationAlarmClockActivity.this).setOnKeyListener(new OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface arg0,
                                                 int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    NetStatusListener.mClickflag = false;
                                }
                                return false;
                            }
                        })
                                .setMessage("是否保存闹钟？")
                                .setNegativeButton(getString(R.string.common_btn_yes),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                if (mNetStatusListener != null) {
                                                    mNetStatusListener.cancleToast();
                                                }
                                                if (location_alarm_checkbox.isChecked()) {
                                                    setClockStatusFromServer(mNDevId,
                                                            1);
                                                    status = 1;
                                                } else {
                                                    setClockStatusFromServer(mNDevId,
                                                            0);
                                                    status = 0;
                                                }
                                                dialog.dismiss();
                                            }
                                        })
                                .setPositiveButton(getString(R.string.common_btn_no),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                                NetStatusListener.mClickflag = false;
                                            }
                                        })
                                .show();
                        //alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                });
        fl_header_alarm_clock.addView(actionBar);
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mNetStatusListener != null) {
                mNetStatusListener.cancleToast();
            }
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alarm_clock);
        Bundle bundle = getIntent().getExtras();
        mNDevId = bundle.getLong("devId");


        addTopBarToHead();
        getClockFromServer(mNDevId);

        mHomeListener = new HomeListener(getApplicationContext());
        mHomeListener.setOnHomePressedListener(mHomePressedListener);
        mHomeListener.startWatch();
        main_alarm = (ScrollView) findViewById(R.id.main_alarm);
        //main_alarm.setOnTouchListener(mOnTouchListener);
        location_alarm_checkbox = (CheckBox) findViewById(R.id.location_alarm_checkbox);
        location_alarm_listview = (ListView) findViewById(R.id.location_alarm_listview);
        location_alarm_tv = (LinearLayout) findViewById(R.id.location_alarm_tv);
        alarmClockAdapter = new AlarmClockAdapter();
        location_alarm_listview.setAdapter(alarmClockAdapter);

        location_alarm_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetStatusListener != null) {
                    mNetStatusListener.cancleToast();
                }
                if (alarmList.size() >= 5) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.alarm_5_alarm),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getApplicationContext(),
                        AlarmPickActivity.class));
            }
        });

        location_alarm_checkbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetStatusListener != null) {
                    mNetStatusListener.cancleToast();
                }
                if (location_alarm_checkbox.isChecked()) {
                    location_alarm_listview.setVisibility(View.VISIBLE);
                    location_alarm_tv.setVisibility(View.VISIBLE);
                } else {
                    location_alarm_listview.setVisibility(View.GONE);
                    location_alarm_tv.setVisibility(View.GONE);
                }
            }
        });

        registerReceiver(alarmReceiver, new IntentFilter(ALARM_FILTER));

        if (Preferences.getInstance(getApplicationContext())
                .getAlarmClockStatus() == 1) {
            location_alarm_checkbox.setChecked(true);
            location_alarm_listview.setVisibility(View.VISIBLE);
            location_alarm_tv.setVisibility(View.VISIBLE);
        } else {
            location_alarm_checkbox.setChecked(false);
            location_alarm_listview.setVisibility(View.GONE);
            location_alarm_tv.setVisibility(View.GONE);
        }

//        getClockFromServer(Preferences.getInstance(getApplicationContext())
//                .getDeviceId());
       // getClockFromServer(mNDevId);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        if (alarmReceiver != null) {
            unregisterReceiver(alarmReceiver);
        }
        if (mHomeListener != null) {
            mHomeListener.stopWatch();
        }
    }

    class AlarmClockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alarmList.size();
        }

        @Override
        public Object getItem(int pos) {
            return alarmList.get(pos);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup viewgroup) {

            view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.alarm_clock_item, null);
            Getter getter;
            if (view != null) {
                getter = new Getter();
                getter.location_alarm_item_1 = (TextView) view.findViewById(R.id.location_alarm_item_1);
                getter.location_alarm_item_2 = (TextView) view.findViewById(R.id.location_alarm_item_2);
                getter.alarm_clock_item_layout = (LinearLayout) view.findViewById(R.id.alarm_clock_item_layout);
                view.setTag(getter);
            } else
                getter = (Getter) view.getTag();

            getter.location_alarm_item_1.setText(alarmList.get(pos).split(" ")[0]);
            getter.location_alarm_item_2.setText(translateIntoWeekday(alarmList.get(pos)
                    .split(" ")[1]));
            getter.alarm_clock_item_layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNetStatusListener != null) {
                        mNetStatusListener.cancleToast();
                    }
                    Bundle b = new Bundle();
                    b.putString("editAlarmTime", alarmList.get(pos));
                    b.putInt("editAlarmTime_pos", pos);
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    intent.setClass(getApplicationContext(),
                            AlarmPickActivity.class);
                    startActivity(intent);
                }
            });

            return view;
        }

        public class Getter {
            private TextView location_alarm_item_1, location_alarm_item_2;

            private LinearLayout alarm_clock_item_layout;
        }
    }

    private void initAlarmList() {
        //      getClockFromServer(Preferences.getInstance(getApplicationContext()).getDeviceId());
        alarmList.clear();
        String alarmTime = "";
        String array = Preferences.getInstance(getApplicationContext())
                .getAlarmTime();
        if (!TextUtils.isEmpty(array)) {
            String[] part = array.split("--");
            for (int i = 0; i < part.length; i++) {
                String clocktime = part[i].split(" ")[0];
                String[] weekdays = part[i].split(" ")[1].split("-");
                String weekday = "";
                for (int j = 0; j < weekdays.length; j++) {
                    weekday = weekdays[j] + "-" + weekday;
                }
                if (!TextUtils.isEmpty(weekday)) {
                    alarmList.add(clocktime + " "
                            + weekday.substring(0, weekday.length() - 1));
                    alarmTime = alarmTime + "," + clocktime + " "
                            + weekday.substring(0, weekday.length() - 1);
                }
            }
        }
        alarmClockAdapter.notifyDataSetChanged();
        ScrollViewUtil.setListViewHeightBasedOnChildren(location_alarm_listview);
    }

    private List<Integer> sortList = new ArrayList<Integer>();

    private List<String> resultList = new ArrayList<String>();

    private List<String> sortWeekDay(String[] array) {
        sortList.clear();
        resultList.clear();
        for (String s : array) {
            sortList.add(Integer.parseInt(s));
        }
        Collections.sort(sortList, new Comparator<Integer>() {

            @Override
            public int compare(Integer arg0, Integer arg1) {
                return arg1.compareTo(arg0);
            }
        });
        for (int i : sortList) {
            resultList.add(i + "");
        }
        return resultList;
    }

    private String translateIntoWeekday(String indexList) {
        String result = "";
        List<String> ss = sortWeekDay(indexList.split("-"));
        for (String s : ss) {
            switch (Integer.parseInt(s)) {
                case 1:
                    result = getString(R.string.common_day1) + "-" + result;
                    break;
                case 2:
                    result = getString(R.string.common_day2) + "-" + result;
                    break;
                case 3:
                    result = getString(R.string.common_day3) + "-" + result;
                    break;
                case 4:
                    result = getString(R.string.common_day4) + "-" + result;
                    break;
                case 5:
                    result = getString(R.string.common_day5) + "-" + result;
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

    private final static int SET_CLOCK_STATUS = 1;

    private final static int ADD_CLOCK = 2;

    private final static int GET_CLOCK = 3;

    private int server_type = 0;

    private ProgressDialog dialog = null;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
                case Constants.GET_DATA_START:
                    dialog = new ProgressDialog(LocationAlarmClockActivity.this);
                    dialog.setMessage(getString(R.string.alarm_ready_to_get_info));
                    dialog.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    switch (server_type) {
                        case SET_CLOCK_STATUS:
                            //                            if (dialog != null && dialog.isShowing())
                            //                            {
                            //                                dialog.dismiss();
                            //                            }
                            if (status == 1) {
                                location_alarm_checkbox.setChecked(true);
                                location_alarm_listview.setVisibility(View.VISIBLE);
                                location_alarm_tv.setVisibility(View.VISIBLE);
                            }
                            else {
                                location_alarm_checkbox.setChecked(false);
                                location_alarm_listview.setVisibility(View.GONE);
                                location_alarm_tv.setVisibility(View.GONE);
                            }
                            mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
                                    LocationAlarmClockActivity.this,
                                    dialog);
                            break;
                        case ADD_CLOCK:
                            break;
                        case GET_CLOCK:
                            try {
                                alarmList.clear();
                                JSONObject json = new JSONObject(
                                        msg.obj.toString());

                                JSONObject data = json.getJSONObject("data");

                                if(data.has("status")) {
                                    int status = data.getInt("status");
                                    if (status == 1) {
                                        location_alarm_checkbox.setChecked(true);
                                        location_alarm_listview.setVisibility(View.VISIBLE);
                                        location_alarm_tv.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        location_alarm_checkbox.setChecked(false);
                                        location_alarm_listview.setVisibility(View.GONE);
                                        location_alarm_tv.setVisibility(View.GONE);
                                    }
                                }

                                String alarmTime = "";
                                if(data.has("arr")) {
                                    JSONArray array = data.getJSONArray("arr");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        //                          int id = obj.getInt("id");
                                        String clocktime = obj.getString("clocktime");
                                        JSONArray weekdays = obj.getJSONArray("week_day");
                                        String weekday = "";
                                        for (int j = 0; j < weekdays.length(); j++) {
                                            weekday = weekdays.get(j)
                                                    + "-" + weekday;
                                        }
                                        if (!TextUtils.isEmpty(weekday)) {
                                            alarmList.add(clocktime
                                                    + " "
                                                    + weekday.substring(0,
                                                    weekday.length() - 1));
                                            alarmTime = alarmTime
                                                    + "--"
                                                    + clocktime
                                                    + " "
                                                    + weekday.substring(0,
                                                    weekday.length() - 1);
                                        }

                                        Preferences.getInstance(getApplicationContext()).setAlarmClockStatus(1);
                                        location_alarm_checkbox.setChecked(true);
                                        location_alarm_listview.setVisibility(View.VISIBLE);
                                        location_alarm_tv.setVisibility(View.VISIBLE);

                                    }
                                }

                                alarmClockAdapter.notifyDataSetChanged();

                                if (!TextUtils.isEmpty(alarmTime)) {
                                    Preferences.getInstance(getApplicationContext())
                                            .setAlarmTime(alarmTime.substring(2,
                                                    alarmTime.length()));
                                } else {
                                    Preferences.getInstance(getApplicationContext())
                                            .setAlarmTime("");
                                }
                                dialog.setMessage(getString(R.string.alarm_receive_info_success));
                                dialog.dismiss();
                                ScrollViewUtil.setListViewHeightBasedOnChildren(location_alarm_listview);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
                case Constants.GET_DATA_FAIL:
                    dialog.setMessage(getString(R.string.alarm_receive_info_fail));
                    dialog.dismiss();
                    break;
                case Constants.SET_NETLISENER_DATA_START:
                    mNetStatusListener = new NetStatusListener();
                    dialog = new ProgressDialog(LocationAlarmClockActivity.this);
                    dialog.setMessage(getString(R.string.netlistener_set_data));
                    //dialog.setCanceledOnTouchOutside(false);
                    dialog.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface arg0, int keyCode,
                                             KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                NetStatusListener.mClickflag = false;
                                if (mNetStatusListener != null
                                        && mNetStatusListener.isRunning()) {
                                    mNetStatusListener.setRunning(false);
                                }

                            }
                            return false;
                        }
                    });
                    dialog.show();
                    ExceptionReciver.setNetLisenerDialog(dialog);
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 设置闹钟状态
     */
    private void setClockStatusFromServer(long deviceId, int status) {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            NetStatusListener.mClickflag = false;
            return;
        }
        mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
        server_type = SET_CLOCK_STATUS;
        JSONObject obj = new JSONObject();
        try {
            JSONArray clock = new JSONArray();
            obj.put("deviceId", deviceId);
            obj.put("status", status);
            if (!TextUtils.isEmpty(Preferences.getInstance(getApplicationContext())
                    .getAlarmTime())) {
                String[] array = Preferences.getInstance(getApplicationContext())
                        .getAlarmTime()
                        .split("--");
                for (String item : array) {
                    JSONObject addItem = new JSONObject();
                    addItem.put("clocktime", item.split(" ")[0]);
                    JSONArray weeks = new JSONArray();
                    String[] week_day = item.split(" ")[1].split("-");
                    for (String alarm : week_day) {
                        weeks.put(alarm);
                    }
                    addItem.put("week_day", weeks);
                    clock.put(addItem);
                }
            }
            obj.put("clock", clock);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.SET_CLOCK,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    @Override
    public void finish() {
        if (mNetStatusListener != null) {
            mNetStatusListener.setActivityFinish();
            NetStatusListener.mClickflag = false;
            mNetStatusListener.setRunning(false);
            mNetStatusListener.cancleToast();
        }

        Preferences.getInstance(getApplicationContext()).setAlarmTime("");
        super.finish();
    }

    /**
     * 获取闹钟
     */
    private void getClockFromServer(long deviceId) {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler.sendEmptyMessage(Constants.GET_DATA_START);
        server_type = GET_CLOCK;
        JSONObject obj = new JSONObject();
        try {
            obj.put("deviceId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.GET_CLOCK,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        super.onPause();
    }

    private NetStatusListener mNetStatusListener;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //        if (keyCode == event.KEYCODE_BACK)
        //        {
        if (mNetStatusListener != null
                && mNetStatusListener.getCustomToast() != null) {
            return mNetStatusListener.cancleToast();
        }
        //        }
        return super.onKeyDown(keyCode, event);
    }
}
