package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.MyExceptionDialog;
import com.smarthome.client2.util.ScreenUtils;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.view.TosGallery;
import com.smarthome.client2.view.WheelView;
import com.umeng.analytics.MobclickAgent;

public class GPSTypeOneTimePickActivity extends Activity implements
        OnTouchListener
{

    private Intent broadcast = new Intent(GPSTypeOneActivity.GPS_TYPE_1_FILTER);

    private WheelView time_picker_start_hour, time_picker_start_min,
            time_picker_end_hour, time_picker_end_min;

    private WheelTextAdapter hourStartAdapter, minStartAdapter, hourEndAdapter,
            minEndAdapter;

    private List<Integer> hours = new ArrayList<Integer>();

    private List<Integer> mins = new ArrayList<Integer>();

    private FrameLayout fl_header_timepick;

    private CustomActionBar actionBar;

    private String start_h, end_h, start_m, end_m;

    private boolean isEdit = false;

    private String editId = "";

    private String editTime = "";

    private int editPos = -1;

    private int index_start_h = 0, index_start_m = 0, index_end_h = 0,
            index_end_m = 0;

    private Button time_picker_delete_btn;

    private RelativeLayout time_pick_index_1_layout, time_pick_index_2_layout,
            time_pick_index_3_layout;

    private RelativeLayout time_pick_gprs_layout1, time_pick_gprs_layout2;

    private LinearLayout time_pick_gprs_layout3;

    private TextView time_pick_index_1_tv, time_pick_index_2_tv,
            time_pick_index_3_tv;

    private int c1 = 0, c6 = 0, c7 = 0;

    private List<Integer> originEdit = new ArrayList<Integer>();

    private List<String> resultList = new ArrayList<String>();

    private ArrayList<TextInfo> mStart_h = new ArrayList<TextInfo>();

    private ArrayList<TextInfo> mStart_m = new ArrayList<TextInfo>();

    private ArrayList<TextInfo> mEnd_h = new ArrayList<TextInfo>();

    private ArrayList<TextInfo> mEnd_m = new ArrayList<TextInfo>();

    private boolean isFirstShowStartHour = true;

    private boolean isFirstShowStartMin = true;

    private boolean isFirstShowEndHour = true;

    private boolean isFirstShowEndMin = true;

    private int selectedColor = Color.parseColor("#4c9ada");

    private int sideColor = Color.parseColor("#9d9d9d");

    private String gpsTime = "";

    private boolean isClick = false;

    private int mHeight = 0;

    private View touchedView = null;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        touchedView = v;
        return gestureDetector.onTouchEvent(event);
    }

    private GestureDetector gestureDetector;

    private OnGestureListener gestureListener = new OnGestureListener()
    {
        @Override
        public boolean onDown(MotionEvent arg0)
        {

            if (touchedView == time_picker_start_hour)
            {
                if (isFirstShowStartHour)
                {
                    time_picker_start_hour.setAdapter(hourStartAdapter);
                    time_picker_start_hour.setSelection(index_start_h);
                    isFirstShowStartHour = false;
                }
            }
            else if (touchedView == time_picker_start_min)
            {
                if (isFirstShowStartMin)
                {
                    time_picker_start_min.setAdapter(minStartAdapter);
                    time_picker_start_min.setSelection(index_start_m);
                    isFirstShowStartMin = false;
                }
            }
            else if (touchedView == time_picker_end_hour)
            {
                if (isFirstShowEndHour)
                {
                    time_picker_end_hour.setAdapter(hourEndAdapter);
                    time_picker_end_hour.setSelection(index_end_h);
                    isFirstShowEndHour = false;
                }
            }
            else if (touchedView == time_picker_end_min)
            {
                if (isFirstShowEndMin)
                {
                    time_picker_end_min.setAdapter(minEndAdapter);
                    time_picker_end_min.setSelection(index_end_m);
                    isFirstShowEndMin = false;
                }
            }

            return false;
        }

        @Override
        public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
                float arg3)
        {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent arg0)
        {
        }

        @Override
        public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
                float arg3)
        {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent arg0)
        {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent arg0)
        {
            return false;
        }
    };

    private void getEditDisturbTime()
    {
        // 非编辑状态
        isEdit = false;
        editPos = -1;
        time_picker_delete_btn.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent.getExtras() != null)
        {
            // 编辑状态
            time_picker_delete_btn.setVisibility(View.VISIBLE);
            isEdit = true;
            //			editId = intent.getExtras().getString("editId");
            editTime = intent.getExtras().getString("editGPRSTime");
            editPos = intent.getExtras().getInt("editGPRSTime_pos");
            String weekdays = intent.getExtras().getString("editGPRSWeekday");
            String[] weekday = weekdays.split(",");
            originEdit.clear();
            for (String day : weekday)
            {
                switch (Integer.parseInt(day))
                {
                    case 1:
                        resultList.add("1");
                        originEdit.add(1);
                        c1 = 1;
                        time_pick_index_2_layout.setBackgroundResource(R.drawable.border_green);
                        time_pick_index_2_tv.setTextColor(Color.WHITE);
                        break;
                    case 6:
                        resultList.add("6");
                        originEdit.add(6);
                        c6 = 1;
                        time_pick_index_3_layout.setBackgroundResource(R.drawable.border_green);
                        time_pick_index_3_tv.setTextColor(Color.WHITE);
                        break;
                    case 7:
                        resultList.add("7");
                        originEdit.add(7);
                        c7 = 1;
                        time_pick_index_1_layout.setBackgroundResource(R.drawable.border_green);
                        time_pick_index_1_tv.setTextColor(Color.WHITE);
                        break;
                }
            }

            start_h = editTime.split("-")[0].split(":")[0];
            end_h = editTime.split("-")[1].split(":")[0];
            start_m = editTime.split("-")[0].split(":")[1];
            end_m = editTime.split("-")[1].split(":")[1];

            index_start_h = Integer.parseInt(start_h);
            index_start_m = Integer.parseInt(start_m);
            index_end_h = Integer.parseInt(end_h);
            index_end_m = Integer.parseInt(end_m);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timepicker_gprs);

        int myWidth = ScreenUtils.getScreenWidth(getApplicationContext());
        int myHeight = ScreenUtils.getScreenHeight(getApplicationContext());
        int tmpH = 150;
        int maxWH = Math.max(myWidth, myHeight);
        if (maxWH >= 800 && maxWH < 1200)
        {
            tmpH = 170;
        }
        else if (maxWH >= 1200 && maxWH < 1440)
        {
            tmpH = 180;
        }
        else if (maxWH >= 1440 && maxWH < 1800)
        {
            tmpH = 220;
        }
        else if (maxWH >= 1800)
        {
            tmpH = 360;
        }
        mHeight = ScreenUtils.px2dip(getApplicationContext(), tmpH);

        time_picker_start_hour = (WheelView) findViewById(R.id.time_picker_gprs_start_hour);
        time_picker_start_min = (WheelView) findViewById(R.id.time_picker_gprs_start_min);
        time_picker_end_hour = (WheelView) findViewById(R.id.time_picker_gprs_end_hour);
        time_picker_end_min = (WheelView) findViewById(R.id.time_picker_gprs_end_min);

        hourStartAdapter = new WheelTextAdapter(this);
        minStartAdapter = new WheelTextAdapter(this);
        hourEndAdapter = new WheelTextAdapter(this);
        minEndAdapter = new WheelTextAdapter(this);

        time_picker_start_hour.setOnEndFlingListener(mListener);
        time_picker_start_hour.setSoundEffectsEnabled(true);
        time_picker_start_hour.setAdapter(hourStartAdapter);

        time_picker_start_min.setOnEndFlingListener(mListener);
        time_picker_start_min.setSoundEffectsEnabled(true);
        time_picker_start_min.setAdapter(minStartAdapter);

        time_picker_end_hour.setOnEndFlingListener(mListener);
        time_picker_end_hour.setSoundEffectsEnabled(true);
        time_picker_end_hour.setAdapter(hourEndAdapter);

        time_picker_end_min.setOnEndFlingListener(mListener);
        time_picker_end_min.setSoundEffectsEnabled(true);
        time_picker_end_min.setAdapter(minEndAdapter);

        time_pick_index_1_layout = (RelativeLayout) findViewById(R.id.time_pick_gprs_index_1_layout);
        time_pick_index_2_layout = (RelativeLayout) findViewById(R.id.time_pick_gprs_index_2_layout);
        time_pick_index_3_layout = (RelativeLayout) findViewById(R.id.time_pick_gprs_index_3_layout);

        time_pick_index_1_tv = (TextView) findViewById(R.id.time_pick_gprs_index_1_tv);
        time_pick_index_2_tv = (TextView) findViewById(R.id.time_pick_gprs_index_2_tv);
        time_pick_index_3_tv = (TextView) findViewById(R.id.time_pick_gprs_index_3_tv);

        time_pick_gprs_layout1 = (RelativeLayout) findViewById(R.id.time_pick_gprs_layout1);
        time_pick_gprs_layout2 = (RelativeLayout) findViewById(R.id.time_pick_gprs_layout2);
        time_pick_gprs_layout3 = (LinearLayout) findViewById(R.id.time_pick_gprs_layout3);

        LinearLayout.LayoutParams lp1 = (android.widget.LinearLayout.LayoutParams) time_pick_gprs_layout1.getLayoutParams();
        lp1.height = ScreenUtils.getScreenHeight(getApplicationContext()) / 3;
        time_pick_gprs_layout1.setLayoutParams(lp1);

        LinearLayout.LayoutParams lp2 = (android.widget.LinearLayout.LayoutParams) time_pick_gprs_layout2.getLayoutParams();
        lp2.height = ScreenUtils.getScreenHeight(getApplicationContext()) / 3;
        time_pick_gprs_layout2.setLayoutParams(lp2);

        LinearLayout.LayoutParams lp3 = (android.widget.LinearLayout.LayoutParams) time_pick_gprs_layout3.getLayoutParams();
        lp3.height = ScreenUtils.getScreenHeight(getApplicationContext()) / 3;
        time_pick_gprs_layout3.setLayoutParams(lp3);

        time_pick_index_1_layout.setOnClickListener(onClickListener);
        time_pick_index_2_layout.setOnClickListener(onClickListener);
        time_pick_index_3_layout.setOnClickListener(onClickListener);

        time_picker_delete_btn = (Button) findViewById(R.id.time_picker_gprs_delete_btn);
        time_picker_delete_btn.setText(getString(R.string.gps_delete_gps));
        time_picker_delete_btn.setOnClickListener(onClickListener);

        gestureDetector = new GestureDetector(getApplicationContext(),
                gestureListener);
        time_picker_start_hour.setOnTouchListener(this);
        time_picker_start_min.setOnTouchListener(this);
        time_picker_end_hour.setOnTouchListener(this);
        time_picker_end_min.setOnTouchListener(this);

        myExceptionDialog = new MyExceptionDialog(
                GPSTypeOneTimePickActivity.this);
        myExceptionDialog.setSubmitClick(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myExceptionDialog.dismissMyDialog();
            }
        });

        getEditDisturbTime();
        addTopBarToHead();
        generateTimeWidget();
        prepareData();
        AppManager.getAppManager().addActivity(this);
    }

    private OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.time_pick_gprs_index_1_layout:
                    if (c7 % 2 == 0)
                    {
                        resultList.add("7");
                        time_pick_index_1_layout.setBackgroundResource(R.drawable.border_green);
                        time_pick_index_1_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("7");
                        time_pick_index_1_layout.setBackgroundResource(R.drawable.border_light_grey);
                        time_pick_index_1_tv.setTextColor(Color.BLACK);
                    }
                    c7++;
                    break;
                case R.id.time_pick_gprs_index_2_layout:
                    if (c1 % 2 == 0)
                    {
                        resultList.add("1");
                        time_pick_index_2_layout.setBackgroundResource(R.drawable.border_green);
                        time_pick_index_2_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("1");
                        time_pick_index_2_layout.setBackgroundResource(R.drawable.border_light_grey);
                        time_pick_index_2_tv.setTextColor(Color.BLACK);
                    }
                    c1++;
                    break;
                case R.id.time_pick_gprs_index_3_layout:
                    if (c6 % 2 == 0)
                    {
                        resultList.add("6");
                        time_pick_index_3_layout.setBackgroundResource(R.drawable.border_green);
                        time_pick_index_3_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("6");
                        time_pick_index_3_layout.setBackgroundResource(R.drawable.border_light_grey);
                        time_pick_index_3_tv.setTextColor(Color.BLACK);
                    }
                    c6++;
                    break;
                case R.id.time_picker_gprs_delete_btn:
                    //                    deleteGPRSFromServer(editId);
                    deleteGPS();
                    break;
            }
        }
    };

    private void prepareData()
    {
        // 设置为当前时间
        if (!isEdit)
        {
            Date d = new Date();
            start_h = d.getHours() + "";
            end_h = d.getHours() + "";
            start_m = d.getMinutes() + "";
            end_m = d.getMinutes() + "";

            index_start_h = d.getHours();
            index_end_h = d.getHours();
            index_start_m = d.getMinutes();
            index_end_m = d.getMinutes();
        }

        for (int i = 0; i < hours.size(); ++i)
        {
            mStart_h.add(new TextInfo(i, hours.get(i) + "",
                    (i == index_start_h)));
            mEnd_h.add(new TextInfo(i, hours.get(i) + "", (i == index_end_h)));
        }
        for (int i = 0; i < mins.size(); ++i)
        {
            mStart_m.add(new TextInfo(i, mins.get(i) + "", (i == index_start_m)));
            mEnd_m.add(new TextInfo(i, mins.get(i) + "", (i == index_end_m)));
        }

        ((WheelTextAdapter) time_picker_start_hour.getAdapter()).setData(mStart_h);
        ((WheelTextAdapter) time_picker_start_min.getAdapter()).setData(mStart_m);
        ((WheelTextAdapter) time_picker_end_hour.getAdapter()).setData(mEnd_h);
        ((WheelTextAdapter) time_picker_end_min.getAdapter()).setData(mEnd_m);

        time_picker_start_hour.setSelection(index_start_h);
        time_picker_start_min.setSelection(index_start_m);
        time_picker_end_hour.setSelection(index_end_h);
        time_picker_end_min.setSelection(index_end_m);
    }

    protected class TextInfo
    {
        public TextInfo(int index, String text, boolean isSelected)
        {
            mIndex = index;
            mText = text;
            mIsSelected = isSelected;

            if (isSelected)
            {
                //				mColor = Color.BLUE;
            }
        }

        public int mIndex;

        public String mText;

        public boolean mIsSelected = false;

        public int mColor = sideColor;
    }

    private TosGallery.OnEndFlingListener mListener = new TosGallery.OnEndFlingListener()
    {
        @Override
        public void onEndFling(TosGallery v)
        {
            int pos = v.getSelectedItemPosition();

            int visibleCount = v.getChildCount();
            for (int i = 0; i < visibleCount; ++i)
            {
                View view = v.getChildAt(i);
                TextView textView = (TextView) view;
                if (textView != null)
                {
                    if (i == 0 || i == visibleCount - 1)
                    {
                    }
                    else
                    {
                        textView.setTextColor(sideColor);
                    }
                }
            }

            View view = v.getSelectedView();

            if (v == time_picker_start_hour)
            {
                TextInfo info = mStart_h.get(pos);
                start_h = info.mText.length() == 2 ? info.mText : "0"
                        + info.mText;
                TextView hourV = (TextView) view;
                if (hourV != null)
                {
                    hourV.setTextColor(selectedColor);
                }
            }
            else if (v == time_picker_start_min)
            {
                TextInfo info = mStart_m.get(pos);
                start_m = info.mText.length() == 2 ? info.mText : "0"
                        + info.mText;
                TextView minV = (TextView) view;
                if (minV != null)
                {
                    minV.setTextColor(selectedColor);
                }

            }
            else if (v == time_picker_end_hour)
            {
                TextInfo info = mEnd_h.get(pos);
                end_h = info.mText.length() == 2 ? info.mText : "0"
                        + info.mText;
                TextView hourV = (TextView) view;
                if (hourV != null)
                {
                    hourV.setTextColor(selectedColor);
                }
            }
            else if (v == time_picker_end_min)
            {
                TextInfo info = mEnd_m.get(pos);
                end_m = info.mText.length() == 2 ? info.mText : "0"
                        + info.mText;
                TextView minV = (TextView) view;
                if (minV != null)
                {
                    minV.setTextColor(selectedColor);
                }

            }
        }
    };

    private void generateTimeWidget()
    {
        for (int i = 0; i <= 23; i++)
        {
            hours.add(i);
        }
        for (int i = 0; i <= 59; i++)
        {
            mins.add(i);
        }
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        if (myExceptionDialog != null)
        {
            myExceptionDialog.dismissMyDialog();
        }
    }

    private void addTopBarToHead()
    {
        fl_header_timepick = (FrameLayout) findViewById(R.id.fl_header_timepick_gprs);
        String title = isEdit ? getString(R.string.title_edit_gps)
                : getString(R.string.title_add_gps);
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
                title,
                getString(R.string.common_btn_yes),
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        if (!isClick)
                        {
                            isClick = true;
                            if (TextUtils.isEmpty(start_h)
                                    || TextUtils.isEmpty(start_m)
                                    || TextUtils.isEmpty(end_h)
                                    || TextUtils.isEmpty(end_m))
                            {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.gps_uncomplete),
                                        Toast.LENGTH_SHORT).show();
                                isClick = false;
                                return;
                            }

                            if (IsOverTime(start_h + ":" + start_m, end_h + ":"
                                    + end_m))
                            {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.common_error_time),
                                        Toast.LENGTH_SHORT).show();
                                isClick = false;
                                return;
                            }

                            if (resultList.size() == 0)
                            {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.gps_choose_date),
                                        Toast.LENGTH_SHORT).show();
                                isClick = false;
                                return;
                            }

                            String resultDate = "";
                            for (Object s : resultList.toArray())
                            {
                                resultDate = resultDate + s.toString() + ",";
                            }
                            resultDate = resultDate.substring(0,
                                    resultDate.length() - 1).trim();
                            String gprsNum = Preferences.getInstance(getApplicationContext())
                                    .getGPRSNum();
                            if (!TextUtils.isEmpty(gprsNum))
                            {
                                int workd = Integer.parseInt(gprsNum.split("-")[0]);
                                int satd = Integer.parseInt(gprsNum.split("-")[1]);
                                int sund = Integer.parseInt(gprsNum.split("-")[2]);

                                if (workd == 8 && isEdit && c1 % 2 == 1
                                        && originEdit.contains(1))
                                {
                                    workd--;
                                }
                                if (satd == 8 && isEdit && c6 % 2 == 1
                                        && originEdit.contains(6))
                                {
                                    satd--;
                                }
                                if (sund == 8 && isEdit && c7 % 2 == 1
                                        && originEdit.contains(7))
                                {
                                    sund--;
                                }

                                if (resultDate.indexOf("1") >= 0)
                                {
                                    workd++;
                                }
                                if (resultDate.indexOf("6") >= 0)
                                {
                                    satd++;
                                }
                                if (resultDate.indexOf("7") >= 0)
                                {
                                    sund++;
                                }

                                if (workd > 8)
                                {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.gps_8_workday),
                                            Toast.LENGTH_SHORT).show();
                                    isClick = false;
                                    return;
                                }
                                if (satd > 8)
                                {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.gps_8_saturday),
                                            Toast.LENGTH_SHORT).show();
                                    isClick = false;
                                    return;
                                }
                                if (sund > 8)
                                {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.gps_8_sunday),
                                            Toast.LENGTH_SHORT).show();
                                    isClick = false;
                                    return;
                                }
                            }

                            String origin = Preferences.getInstance(getApplicationContext())
                                    .getGPSTime();
                            if (!TextUtils.isEmpty(origin))
                            {
                                if (isEdit)
                                {
                                    if (origin.indexOf("--") >= 0)
                                    {
                                        String[] tmp = origin.split("--");
                                        for (int i = 0, l = tmp.length; i < l; i++)
                                        {
                                            if (i == editPos)
                                            {
                                                gpsTime = gpsTime + "--"
                                                        + start_h + ":"
                                                        + start_m + "-" + end_h
                                                        + ":" + end_m + " "
                                                        + resultDate;
                                            }
                                            else
                                            {
                                                gpsTime = gpsTime + "--"
                                                        + tmp[i];
                                            }

                                        }
                                        gpsTime = gpsTime.substring(2);
                                    }
                                    else
                                    {
                                        gpsTime = origin.replace(origin,
                                                start_h + ":" + start_m + "-"
                                                        + end_h + ":" + end_m
                                                        + " " + resultDate);
                                    }
                                }
                                else
                                {
                                    gpsTime = origin + "--" + start_h + ":"
                                            + start_m + "-" + end_h + ":"
                                            + end_m + " " + resultDate;
                                }
                            }
                            else
                            {
                                gpsTime = start_h + ":" + start_m + "-" + end_h
                                        + ":" + end_m + " " + resultDate;
                            }
                            //							addGPRSFromServer(editId,
                            //									Preferences
                            //									.getInstance(getApplicationContext())
                            //									.getDeviceId(), 1, start_h + ":"
                            //											+ start_m + ":" + "00", end_h + ":"
                            //													+ end_m + ":" + "00", resultList);

                            Preferences.getInstance(getApplicationContext())
                                    .setGPSTime(gpsTime);
                            sendBroadcast(broadcast);
                            finish();

                        }
                    }
                });
        fl_header_timepick.addView(actionBar);
    }

    private boolean IsOverTime(String start, String end)
    {
        long t1 = 0, t2 = 0;
        Date date = new Date();
        date.setHours(Integer.parseInt(start.split(":")[0]));
        date.setMinutes(Integer.parseInt(start.split(":")[1]));
        t1 = date.getTime();
        date.setHours(Integer.parseInt(end.split(":")[0]));
        date.setMinutes(Integer.parseInt(end.split(":")[1]));
        t2 = date.getTime();
        return t2 <= t1;
    }

    protected class WheelTextAdapter extends BaseAdapter
    {
        ArrayList<TextInfo> mData = null;

        int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;

        Context mContext = null;

        public WheelTextAdapter(Context context)
        {
            mContext = context;
        }

        public void setData(ArrayList<TextInfo> data)
        {
            mData = data;
            this.notifyDataSetChanged();
        }

        public void setItemSize(int width, int height)
        {
            mWidth = width;
            mHeight = ScreenUtils.px2dip(mContext, height);
        }

        @Override
        public int getCount()
        {
            return (null != mData) ? mData.size() : 0;
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            TextView textView = null;

            if (null == convertView)
            {
                convertView = new TextView(mContext);
                convertView.setLayoutParams(new TosGallery.LayoutParams(mWidth,
                        mHeight));
                textView = (TextView) convertView;
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                //				textView.setTextColor(selectedColor);
                convertView.setTag(textView);
            }
            else
            {
                textView = (TextView) convertView.getTag();
            }

            //			if (null == textView) {
            //				textView = (TextView) convertView;
            //			}

            TextInfo info = mData.get(position);
            textView.setText(info.mText);
            textView.setTextColor(info.mColor);

            if (time_picker_start_hour == parent)
            {
                if (position == index_start_h)
                {
                    if (isFirstShowStartHour)
                    {
                        textView.setTextColor(selectedColor);
                    }
                    else
                    {
                        textView.setTextColor(sideColor);
                    }
                    return convertView;
                }
                if ((position == index_start_h - 1 && index_start_h != 0)
                        || (position == index_start_h + 1)
                        && index_start_h != 0)
                {
                    textView.setTextColor(sideColor);
                    return convertView;
                }
            }
            else if (time_picker_start_min == parent)
            {
                if (position == index_start_m)
                {
                    if (isFirstShowStartMin)
                    {
                        textView.setTextColor(selectedColor);
                    }
                    else
                    {
                        textView.setTextColor(sideColor);
                    }
                    return convertView;
                }
                if ((position == index_start_m - 1 && index_start_m != 0)
                        || (position == index_start_m + 1)
                        && index_start_m != 0)
                {
                    textView.setTextColor(sideColor);
                    return convertView;
                }
            }
            else if (time_picker_end_hour == parent)
            {
                if (position == index_end_h)
                {
                    if (isFirstShowEndHour)
                    {
                        textView.setTextColor(selectedColor);
                    }
                    else
                    {
                        textView.setTextColor(sideColor);
                    }
                    return convertView;
                }
                if ((position == index_end_h - 1 && index_end_h != 0)
                        || (position == index_end_h + 1) && index_end_h != 0)
                {
                    textView.setTextColor(sideColor);
                    return convertView;
                }
            }
            else if (time_picker_end_min == parent)
            {
                if (position == index_end_m)
                {
                    if (isFirstShowEndMin)
                    {
                        textView.setTextColor(selectedColor);
                    }
                    else
                    {
                        textView.setTextColor(sideColor);
                    }
                    return convertView;
                }
                if ((position == index_end_m - 1 && index_end_m != 0)
                        || (position == index_end_m + 1) && index_end_m != 0)
                {
                    textView.setTextColor(sideColor);
                    return convertView;
                }
            }

            return convertView;
        }
    }

    private final static int ADD_GPRS = 1;

    private final static int DEL_GPRS = 2;

    private final static int START_ADD_GPRS = 3;

    private final static int START_DEL_GPRS = 4;

    private ProgressDialog dialog;

    private MyExceptionDialog myExceptionDialog;

    private List<Integer> server_type = new ArrayList<Integer>();

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case START_ADD_GPRS:
                    dialog = new ProgressDialog(GPSTypeOneTimePickActivity.this);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setMessage(getString(R.string.gps_ready_to_save));
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
                        isClick = false;
                        return;
                    }
                    break;
                case START_DEL_GPRS:
                    dialog = new ProgressDialog(GPSTypeOneTimePickActivity.this);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setMessage(getString(R.string.gps_ready_to_delete));
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
                    switch (server_type.get(0))
                    {
                        case ADD_GPRS:
                            Log.d("", "daitm--------添加GPRS成功");
                            dialog.dismiss();
                            sendBroadcast(broadcast);
                            finish();
                            try
                            {
                                Thread.sleep(50);
                                isClick = false;
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        case DEL_GPRS:
                            Log.d("", "daitm--------删除GPRS成功");
                            dialog.dismiss();
                            sendBroadcast(broadcast);
                            finish();
                            break;
                    }
                    server_type.remove(0);
                    break;
                case Constants.GET_DATA_FAIL:
                    break;
            }
        }
    };

    /**
     * 免打扰（addSilence） id:如果编辑，此为空
     */
    private void addGPRSFromServer(String id, int deviceId, int status,
            String begin_time, String end_time, List<String> week_day)
    {
        server_type.add(ADD_GPRS);
        mHandler.sendEmptyMessage(START_ADD_GPRS);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("id", id);
            obj.put("deviceId", deviceId);
            obj.put("status", status);
            obj.put("begin_time", begin_time);
            obj.put("end_time", end_time);
            JSONArray array = new JSONArray();
            for (String disrupt : week_day)
            {
                array.put(disrupt);
            }
            obj.put("week_day", array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.ADD_GPRS,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private void deleteGPS()
    {
        String[] items = Preferences.getInstance(getApplicationContext())
                .getGPSTime()
                .split("--");
        String tmp = "";
        for (int i = 0; i < items.length; i++)
        {
            if (i != editPos)
            {
                tmp = tmp + "--" + items[i];
            }
        }
        if (!TextUtils.isEmpty(tmp))
        {
            Preferences.getInstance(getApplicationContext())
                    .setGPSTime(tmp.substring(2, tmp.length()));
        }
        else
        {
            Preferences.getInstance(getApplicationContext()).setGPSTime("");
        }
        sendBroadcast(broadcast);
        finish();
    }

    private void deleteGPRSFromServer(String id)
    {
        server_type.add(DEL_GPRS);
        mHandler.sendEmptyMessage(START_DEL_GPRS);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("id", Integer.parseInt(id));
            obj.put("deviceId",
                    Preferences.getInstance(getApplicationContext())
                            .getDeviceId());
            JSONArray array = new JSONArray();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.DEL_GPRS,
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