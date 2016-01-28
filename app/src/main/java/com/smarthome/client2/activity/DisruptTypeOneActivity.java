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

public class DisruptTypeOneActivity extends Activity {

    public final static String DISRUPT_TYPE_1_FILTER = "com.smarthome.client2.disrupttime_1";

    public static int seriNum = 1;

    private CheckBox disrupt_ischeck;

    private LinearLayout disrupt_layout;

    private ListView disrupt_list_listview;

    private LinearLayout disrupt_list_tv;

    private ScrollView main_disrupt_1;

    private DisruptAdapter disruptAdapter;

    private FrameLayout fl_header_disrupt;

    private CustomActionBar actionBar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Bundle bundle = getIntent().getExtras();
        mNDevId = bundle.getLong("devId");
        setContentView(R.layout.activity_disrupt_type_1);
        initWidget();
        addTopBarToHead();
        getSilenceFromServer();
        AppManager.getAppManager().addActivity(this);
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

    private void initWidget() {
        mHomeListener = new HomeListener(getApplicationContext());
        mHomeListener.setOnHomePressedListener(mHomePressedListener);
        mHomeListener.startWatch();
        main_disrupt_1 = (ScrollView) findViewById(R.id.main_disrupt_1);
        main_disrupt_1.setOnTouchListener(mOnTouchListener);
        disrupt_layout = (LinearLayout) findViewById(R.id.disrupt_layout);
        disrupt_list_tv = (LinearLayout) findViewById(R.id.disrupt_list_tv);
        disrupt_list_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetStatusListener != null) {
                    mNetStatusListener.cancleToast();
                }
                if (disruptTime.size() == 5) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.disrupt_5_disrupt),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(DisruptTypeOneActivity.this,
                        DisruptTypeOneTimePickActivity.class));
            }
        });

        disrupt_list_listview = (ListView) findViewById(R.id.disrupt_list_listview);
        disruptAdapter = new DisruptAdapter();
        disrupt_list_listview.setAdapter(disruptAdapter);

        disrupt_list_listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> viewgroup, View v, int pos,
                                    long arg3) {
                //				String[] times = Preferences
                //						.getInstance(getApplicationContext())
                //						.getDisturbTime().split(",");
                if (mNetStatusListener != null) {
                    mNetStatusListener.cancleToast();
                }
                Bundle b = new Bundle();
                if (disruptTime.get(pos).split(" ").length == 3) {
                    //                    b.putString("editId", disruptTime.get(pos).split(" ")[2]);
                }
                b.putString("editDisruptWeekday",
                        disruptTime.get(pos).split(" ")[1]);
                b.putString("editDisruptTime",
                        disruptTime.get(pos).split(" ")[0]);
                b.putInt("editDisruptTime_pos", pos);
                Intent intent = new Intent();
                intent.putExtras(b);
                intent.setClass(getApplicationContext(),
                        DisruptTypeOneTimePickActivity.class);
                startActivity(intent);
            }
        });

        disrupt_ischeck = (CheckBox) findViewById(R.id.disrupt_ischeck);
        //		disrupt_ischeck
        //				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        //					@Override
        //					public void onCheckedChanged(CompoundButton cb,
        //							boolean isClick) {
        //						if (isClick) {
        //							disrupt_layout.setVisibility(View.VISIBLE);
        //							setSilenceFromServer(Preferences.getInstance(getApplicationContext()).getDeviceId(), 1);
        //						} else {
        //							disrupt_layout.setVisibility(View.GONE);
        //							setSilenceFromServer(Preferences.getInstance(getApplicationContext()).getDeviceId(), 0);
        //						}
        //					}
        //				});
        disrupt_ischeck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetStatusListener != null) {
                    mNetStatusListener.cancleToast();
                }
                if (disrupt_ischeck.isChecked()) {
                    disrupt_layout.setVisibility(View.VISIBLE);
                } else {
                    disrupt_layout.setVisibility(View.GONE);
                }
            }
        });

        registerReceiver(disruptReceiver, new IntentFilter(
                DISRUPT_TYPE_1_FILTER));

        if (Preferences.getInstance(getApplicationContext()).getSilenceStatus() == 1) {
            disrupt_ischeck.setChecked(true);
            disrupt_layout.setVisibility(View.VISIBLE);
        } else {
            disrupt_ischeck.setChecked(false);
        }

    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        if (disruptReceiver != null) {
            unregisterReceiver(disruptReceiver);
        }
        if (mHomeListener != null) {
            mHomeListener.stopWatch();
        }
    }

    private void addTopBarToHead() {
        fl_header_disrupt = (FrameLayout) findViewById(R.id.fl_header_disrupt);
        actionBar = TopBarUtils.createCustomActionBar(this,
                R.drawable.btn_back_selector,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                getString(R.string.title_disrupt),
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
                                DisruptTypeOneActivity.this).setOnKeyListener(new OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface arg0,
                                                 int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    NetStatusListener.mClickflag = false;
                                }
                                return false;
                            }
                        })
                                .setMessage(getString(R.string.netlistener_ask))
                                .setNegativeButton(getString(R.string.common_btn_yes),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                if (disrupt_ischeck.isChecked()) {
                                                    setSilenceFromServer(mNDevId,
                                                            1);
                                                    status = 1;
                                                } else {
                                                    setSilenceFromServer(mNDevId,
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
                                .create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                });
        fl_header_disrupt.addView(actionBar);
    }

    private List<String> disruptTime = new ArrayList<String>();

    class DisruptAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return disruptTime.size();
        }

        @Override
        public Object getItem(int pos) {
            return disruptTime.get(pos);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup viewgroup) {

            view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.location_setting_disrupt_listview_item,
                            null);
            Getter getter;
            if (view != null) {
                getter = new Getter();
                getter.location_setting_starttime = (TextView) view.findViewById(R.id.location_setting_starttime);
                getter.location_setting_endtime = (TextView) view.findViewById(R.id.location_setting_endtime);
                getter.location_setting_weekday = (TextView) view.findViewById(R.id.location_setting_weekday);
                view.setTag(getter);
            } else
                getter = (Getter) view.getTag();

            String starttime = disruptTime.get(pos).split(" ")[0].split("-")[0];
            String endtime = disruptTime.get(pos).split(" ")[0].split("-")[1];
            getter.location_setting_starttime.setText(starttime);
            getter.location_setting_endtime.setText(endtime);
            getter.location_setting_weekday.setText(translateIntoWeekday(sortWeekDay(disruptTime.get(pos)
                    .split(" ")[1])));

            return view;
        }

        public class Getter {
            private TextView location_setting_starttime,
                    location_setting_endtime, location_setting_weekday;
        }
    }

    private String sortWeekDay(String weekday) {
        String result = "";
        String[] ss = weekday.split(",");
        List<Integer> tmp = new ArrayList<Integer>();
        for (String s : ss) {
            tmp.add(Integer.parseInt(s));
        }
        Collections.sort(tmp);
        for (int sort : tmp) {
            result = sort + "-" + result;
        }
        return result.substring(0, result.length() - 1);
    }

    private String translateIntoWeekday(String indexList) {
        String result = "";
        String[] ss = indexList.split("-");
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

    private final static int SET_SILENCE = 1;

    private final static int GET_SILENCE = 2;

    private final static int ADD_SILENCE = 3;

    private ProgressDialog dialog = null;

    private List<Integer> serverType = new ArrayList<Integer>();

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
                case Constants.GET_DATA_START:
                    dialog = new ProgressDialog(DisruptTypeOneActivity.this);
                    dialog.setMessage(getString(R.string.disrupt_ready_to_get_info));
                    dialog.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    switch (serverType.get(0)) {
                        case SET_SILENCE:
                            //                            if (dialog != null && dialog.isShowing())
                            //                            {
                            //                                dialog.dismiss();
                            //                            }
                            mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
                                    DisruptTypeOneActivity.this,
                                    dialog);
                            break;
                        case GET_SILENCE:
                            try {
                                Preferences.getInstance(getApplicationContext())
                                        .setDisturbTime("");
                                String disturbTimes = "";
                                disruptTime.clear();
                                JSONObject json = new JSONObject(
                                        msg.obj.toString());
                                JSONObject data = json.getJSONObject("data");
                                if(data.has("status")) {
                                    int status = data.getInt("status");
                                    if (status == 1) {
                                        disrupt_ischeck.setChecked(true);
                                        disrupt_layout.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        disrupt_ischeck.setChecked(false);
                                        disrupt_layout.setVisibility(View.INVISIBLE);
                                    }
                                }

                                if(data.has("arr")) {
                                    JSONArray array = data.getJSONArray("arr");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        //                                    int id = obj.getInt("id");
                                        String begin_time = obj.getString("begin_time");
                                        String end_time = obj.getString("end_time");
                                        JSONArray weekdays = obj.getJSONArray("week_day");
                                        String week_day = "";
                                        for (int j = 0; j < weekdays.length(); j++) {
                                            week_day = weekdays.get(j) + ","
                                                    + week_day;
                                        }
                                        week_day = sortWeekDay(week_day.substring(0,
                                                week_day.length() - 1)).replace("-",
                                                ",");
                                        // 6:30-7:30 周一
                                        if (!TextUtils.isEmpty(week_day)) {
                                            String item = begin_time + "-"
                                                    + end_time + " " + week_day;
                                            disruptTime.add(item);
                                            disturbTimes = disturbTimes + "--"
                                                    + item;
                                        }
                                    }
                                }

                                if (!TextUtils.isEmpty(disturbTimes)) {
                                    Preferences.getInstance(getApplicationContext())
                                            .setDisturbTime(disturbTimes.substring(2,
                                                    disturbTimes.length()));
                                } else {
                                    Preferences.getInstance(getApplicationContext())
                                            .setDisturbTime("");
                                }
                                Preferences.getInstance(getApplicationContext()).setSilenceStatus(1);



                                disruptAdapter.notifyDataSetChanged();
                                if (dialog != null) {
                                    dialog.setMessage(getString(R.string.disrupt_receive_info_success));
                                    dialog.dismiss();
                                }
                                ScrollViewUtil.setListViewHeightBasedOnChildren(disrupt_list_listview);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case ADD_SILENCE:
                            break;
                    }
                    serverType.remove(0);
                    break;
                case Constants.GET_DATA_FAIL:
                    if (dialog != null) {
                        dialog.setMessage(getString(R.string.disrupt_receive_info_fail));
                        dialog.dismiss();
                    }
                    break;
                case Constants.SET_NETLISENER_DATA_START:
                    mNetStatusListener = new NetStatusListener();
                    dialog = new ProgressDialog(DisruptTypeOneActivity.this);
                    dialog.setMessage(getString(R.string.netlistener_set_data));
                    dialog.setCanceledOnTouchOutside(false);
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
            }
        }

    };

    /**
     * 免打扰（setSilence） status:0:关闭、1:开启
     */
    private void setSilenceFromServer(long deviceId, int status) {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            NetStatusListener.mClickflag = false;
            return;
        }
        mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
        serverType.add(SET_SILENCE);
        JSONObject obj = new JSONObject();
        try {
            obj.put("deviceId", deviceId);
            obj.put("status", status);
            JSONArray silence = new JSONArray();
            if (!TextUtils.isEmpty(Preferences.getInstance(getApplicationContext())
                    .getDisturbTime())) {
                String[] array = Preferences.getInstance(getApplicationContext())
                        .getDisturbTime()
                        .split("--");
                for (String item : array) {
                    JSONObject addItem = new JSONObject();
                    addItem.put("begin_time", item.split(" ")[0].split("-")[0]);
                    addItem.put("end_time", item.split(" ")[0].split("-")[1]);
                    JSONArray weeks = new JSONArray();
                    String[] week_day = item.split(" ")[1].split(",");
                    for (String disrupt : week_day) {
                        weeks.put(disrupt);
                    }
                    addItem.put("week_day", weeks);
                    silence.put(addItem);
                }
            }
            obj.put("silence", silence);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.SET_SILENCE,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    /**
     * 免打扰（getSilence）
     */
    private void getSilenceFromServer() {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler.sendEmptyMessage(Constants.GET_DATA_START);
        serverType.add(GET_SILENCE);
        JSONObject obj = new JSONObject();
        try {
            obj.put("deviceId",
                    mNDevId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.GET_SILENCE,
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
        Preferences.getInstance(getApplicationContext()).setDisturbTime("");
        super.finish();
    }

    private BroadcastReceiver disruptReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(DISRUPT_TYPE_1_FILTER)) {
                //                getSilenceFromServer();

                disruptTime.clear();
                String array = Preferences.getInstance(getApplicationContext())
                        .getDisturbTime();
                if (!TextUtils.isEmpty(array)) {
                    String[] part = array.trim().split("--");
                    for (int i = 0; i < part.length; i++) {
                        String[] time = part[i].split(" ");
                        String begin_time = time[0].split("-")[0];
                        String end_time = time[0].split("-")[1];
                        String[] weekdays = time[1].split(",");
                        String week_day = "";
                        for (int j = 0; j < weekdays.length; j++) {
                            week_day = weekdays[j] + "," + week_day;
                        }
                        // 6:30-7:30 周一
                        if (!TextUtils.isEmpty(week_day)) {
                            String item = begin_time
                                    + "-"
                                    + end_time
                                    + " "
                                    + week_day.substring(0,
                                    week_day.length() - 1);
                            disruptTime.add(item);
                        }
                    }
                }
                disruptAdapter.notifyDataSetChanged();
                ScrollViewUtil.setListViewHeightBasedOnChildren(disrupt_list_listview);
            }
        }
    };

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
