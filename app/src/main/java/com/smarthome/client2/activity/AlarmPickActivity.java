package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.ImageView;
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

public class AlarmPickActivity extends Activity implements OnTouchListener
{

    private Intent broadcastIntent = new Intent(
            LocationAlarmClockActivity.ALARM_FILTER);

    private FrameLayout fl_header_alarmpick;

    private CustomActionBar actionBar;

    private WheelView alarm_picker_hour, alarm_picker_min;

    private WheelTextAdapter hourAdapter, minAdapter;

    private RelativeLayout alarm_picker_layout;

    private RelativeLayout alarm_pick_index_1_layout,
            alarm_pick_index_2_layout, alarm_pick_index_3_layout,
            alarm_pick_index_4_layout, alarm_pick_index_5_layout,
            alarm_pick_index_6_layout, alarm_pick_index_7_layout;

    private Button alarm_picker_delete_btn;

    private TextView alarm_pick_index_1_tv, alarm_pick_index_2_tv,
            alarm_pick_index_3_tv, alarm_pick_index_4_tv,
            alarm_pick_index_5_tv, alarm_pick_index_6_tv,
            alarm_pick_index_7_tv;

    private ImageView alarm_pick_overlay_1, alarm_pick_overlay_2;

    private int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0;

    private int index__h = 0, index__m = 0;

    private boolean isEdit = false;

    private int editPos = -1;

    private String editId = "";

    private ArrayList<TextInfo> mHour = new ArrayList<TextInfo>();

    private ArrayList<TextInfo> mMin = new ArrayList<TextInfo>();

    private String hour, min, date;

    private List<String> resultList = new ArrayList<String>();

    private List<Integer> hours = new ArrayList<Integer>();

    private List<Integer> mins = new ArrayList<Integer>();

    private boolean isClick = false;

    private boolean isFirstShowHour = true;

    private boolean isFirstShowMin = true;

    private int selectedColor = Color.parseColor("#4c9ada");

    private int sideColor = Color.parseColor("#9d9d9d");

    //  private int normalColor = Color.parseColor("#d1d1d1");
    //  private int slideColor = Color.parseColor("#f5f5f5");

    private GestureDetector gestureDetector;

    private OnGestureListener gestureListener = new OnGestureListener()
    {
        @Override
        public boolean onDown(MotionEvent arg0)
        {

            if (touchedView == alarm_picker_hour)
            {
                if (isFirstShowHour)
                {
                    alarm_picker_hour.setAdapter(hourAdapter);
                    alarm_picker_hour.setSelection(index__h);
                    isFirstShowHour = false;
                }
            }
            else if (touchedView == alarm_picker_min)
            {
                if (isFirstShowMin)
                {
                    alarm_picker_min.setAdapter(minAdapter);
                    alarm_picker_min.setSelection(index__m);
                    isFirstShowMin = false;
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

    private int mHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alarmpick_location_v2);
        initWidget();
        getEditDisturbTime();
        addTopBarToHead();
        generateTimeWidget();
        prepareData();
        AppManager.getAppManager().addActivity(this);
    }

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

    private void getEditDisturbTime()
    {
        // 非编辑状态
        editPos = -1;
        editId = "";
        isEdit = false;
        alarm_picker_delete_btn.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent.getExtras() != null)
        {
            alarm_picker_delete_btn.setVisibility(View.VISIBLE);
            isEdit = true;// 编辑状态
            String editTime = intent.getExtras().getString("editAlarmTime");
            editPos = intent.getExtras().getInt("editAlarmTime_pos");
            hour = editTime.split(" ")[0].split(":")[0];
            min = editTime.split(" ")[0].split(":")[1];
            date = editTime.split(" ")[1];
            //          editId = editTime.split(" ")[2];

            String[] dates = date.split("-");
            for (String s : dates)
            {
                switch (Integer.parseInt(s))
                {
                    case 1:
                        resultList.add("1");
                        c2 = 1;
                        alarm_pick_index_2_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_2_tv.setTextColor(Color.WHITE);
                        break;
                    case 2:
                        resultList.add("2");
                        c3 = 1;
                        alarm_pick_index_3_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_3_tv.setTextColor(Color.WHITE);
                        break;
                    case 3:
                        resultList.add("3");
                        c4 = 1;
                        alarm_pick_index_4_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_4_tv.setTextColor(Color.WHITE);
                        break;
                    case 4:
                        resultList.add("4");
                        c5 = 1;
                        alarm_pick_index_5_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_5_tv.setTextColor(Color.WHITE);
                        break;
                    case 5:
                        resultList.add("5");
                        c6 = 1;
                        alarm_pick_index_6_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_6_tv.setTextColor(Color.WHITE);
                        break;
                    case 6:
                        resultList.add("6");
                        c7 = 1;
                        alarm_pick_index_7_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_7_tv.setTextColor(Color.WHITE);
                        break;
                    case 7:
                        resultList.add("7");
                        c1 = 1;
                        alarm_pick_index_1_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_1_tv.setTextColor(Color.WHITE);
                        break;
                }
            }
            index__h = Integer.parseInt(hour);
            index__m = Integer.parseInt(min);

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

    private void initWidget()
    {
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

        myExceptionDialog = new MyExceptionDialog(AlarmPickActivity.this);
        myExceptionDialog.setSubmitClick(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myExceptionDialog.dismissMyDialog();
            }
        });
        alarm_pick_index_1_layout = (RelativeLayout) findViewById(R.id.alarm_pick_index_1_layout);
        alarm_pick_index_2_layout = (RelativeLayout) findViewById(R.id.alarm_pick_index_2_layout);
        alarm_pick_index_3_layout = (RelativeLayout) findViewById(R.id.alarm_pick_index_3_layout);
        alarm_pick_index_4_layout = (RelativeLayout) findViewById(R.id.alarm_pick_index_4_layout);
        alarm_pick_index_5_layout = (RelativeLayout) findViewById(R.id.alarm_pick_index_5_layout);
        alarm_pick_index_6_layout = (RelativeLayout) findViewById(R.id.alarm_pick_index_6_layout);
        alarm_pick_index_7_layout = (RelativeLayout) findViewById(R.id.alarm_pick_index_7_layout);

        alarm_pick_index_1_tv = (TextView) findViewById(R.id.alarm_pick_index_1_tv);
        alarm_pick_index_2_tv = (TextView) findViewById(R.id.alarm_pick_index_2_tv);
        alarm_pick_index_3_tv = (TextView) findViewById(R.id.alarm_pick_index_3_tv);
        alarm_pick_index_4_tv = (TextView) findViewById(R.id.alarm_pick_index_4_tv);
        alarm_pick_index_5_tv = (TextView) findViewById(R.id.alarm_pick_index_5_tv);
        alarm_pick_index_6_tv = (TextView) findViewById(R.id.alarm_pick_index_6_tv);
        alarm_pick_index_7_tv = (TextView) findViewById(R.id.alarm_pick_index_7_tv);

        alarm_picker_delete_btn = (Button) findViewById(R.id.alarm_picker_delete_btn);

        alarm_picker_layout = (RelativeLayout) findViewById(R.id.alarm_picker_layout);
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) alarm_picker_layout.getLayoutParams();
        lp.height = ScreenUtils.getScreenHeight(getApplicationContext()) / 2;
        alarm_picker_layout.setLayoutParams(lp);

        alarm_pick_overlay_1 = (ImageView) findViewById(R.id.alarm_pick_overlay_1);
        alarm_pick_overlay_2 = (ImageView) findViewById(R.id.alarm_pick_overlay_2);

        //      RelativeLayout.LayoutParams lp_overlay1 = (android.widget.RelativeLayout.LayoutParams) alarm_pick_overlay_1.getLayoutParams();
        //      lp_overlay1.height = mHeight;
        //      lp_overlay1.width = ScreenUtils.getScreenWidth(getApplicationContext());
        //      lp_overlay1.alignWithParent = true;
        //      alarm_pick_overlay_1.setLayoutParams(lp_overlay1);
        //
        //      RelativeLayout.LayoutParams lp_overlay2 = (android.widget.RelativeLayout.LayoutParams) alarm_pick_overlay_2.getLayoutParams();
        //      lp_overlay2.height = mHeight;
        //      lp_overlay2.width = ScreenUtils.getScreenWidth(getApplicationContext());
        //      lp_overlay1.alignWithParent = true;
        //      alarm_pick_overlay_2.setLayoutParams(lp_overlay2);

        alarm_pick_index_1_layout.setOnClickListener(onClickListener);
        alarm_pick_index_2_layout.setOnClickListener(onClickListener);
        alarm_pick_index_3_layout.setOnClickListener(onClickListener);
        alarm_pick_index_4_layout.setOnClickListener(onClickListener);
        alarm_pick_index_5_layout.setOnClickListener(onClickListener);
        alarm_pick_index_6_layout.setOnClickListener(onClickListener);
        alarm_pick_index_7_layout.setOnClickListener(onClickListener);
        alarm_picker_delete_btn.setOnClickListener(onClickListener);

        alarm_picker_hour = (WheelView) findViewById(R.id.alarm_picker_hour);
        alarm_picker_min = (WheelView) findViewById(R.id.alarm_picker_min);

        hourAdapter = new WheelTextAdapter(this);
        minAdapter = new WheelTextAdapter(this);

        alarm_picker_hour.setOnEndFlingListener(mListener);
        alarm_picker_hour.setSoundEffectsEnabled(true);
        alarm_picker_hour.setAdapter(hourAdapter);

        alarm_picker_min.setOnEndFlingListener(mListener);
        alarm_picker_min.setSoundEffectsEnabled(true);
        alarm_picker_min.setAdapter(minAdapter);

        gestureDetector = new GestureDetector(getApplicationContext(),
                gestureListener);
        alarm_picker_hour.setOnTouchListener(this);
        alarm_picker_min.setOnTouchListener(this);

    }

    private void prepareData()
    {
        //设置为当前时间
        if (!isEdit)
        {
            Date d = new Date();
            hour = d.getHours() + "";
            min = d.getMinutes() + "";
            index__h = d.getHours();
            index__m = d.getMinutes();
        }

        mHour.clear();
        mMin.clear();
        for (int i = 0; i < hours.size(); ++i)
        {
            mHour.add(new TextInfo(i, hours.get(i) + "", (i == index__h)));
        }
        for (int i = 0; i < mins.size(); ++i)
        {
            mMin.add(new TextInfo(i, mins.get(i) + "", (i == index__m)));
        }

        ((WheelTextAdapter) alarm_picker_hour.getAdapter()).setData(mHour);
        ((WheelTextAdapter) alarm_picker_min.getAdapter()).setData(mMin);

        alarm_picker_hour.setSelection(index__h);
        alarm_picker_min.setSelection(index__m);
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

            if (v == alarm_picker_hour)
            {
                TextInfo info = mHour.get(pos);
                hour = info.mText.length() == 2 ? info.mText : "0" + info.mText;
                TextView hourV = (TextView) view;
                if (hourV != null)
                {
                    hourV.setTextColor(selectedColor);
                }
            }
            else if (v == alarm_picker_min)
            {
                TextInfo info = mMin.get(pos);
                min = info.mText.length() == 2 ? info.mText : "0" + info.mText;
                TextView minV = (TextView) view;
                if (minV != null)
                {
                    minV.setTextColor(selectedColor);
                }
            }
        }
    };

    protected class TextInfo
    {
        public TextInfo(int index, String text, boolean isSelected)
        {
            mIndex = index;
            mText = text;
            mIsSelected = isSelected;

            if (isSelected)
            {
                //              mColor = selectedColor;
            }
        }

        public int mIndex;

        public String mText;

        public boolean mIsSelected = false;

        public int mColor = sideColor;
    }

    private OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.alarm_pick_index_1_layout:
                    if (c1 % 2 == 0)
                    {
                        resultList.add("7");
                        alarm_pick_index_1_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_1_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("7");
                        alarm_pick_index_1_layout.setBackgroundResource(R.drawable.border_light_grey);
                        alarm_pick_index_1_tv.setTextColor(Color.BLACK);
                    }
                    c1++;
                    break;
                case R.id.alarm_pick_index_2_layout:
                    if (c2 % 2 == 0)
                    {
                        resultList.add("1");
                        alarm_pick_index_2_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_2_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("1");
                        alarm_pick_index_2_layout.setBackgroundResource(R.drawable.border_light_grey);
                        alarm_pick_index_2_tv.setTextColor(Color.BLACK);
                    }
                    c2++;
                    break;
                case R.id.alarm_pick_index_3_layout:
                    if (c3 % 2 == 0)
                    {
                        resultList.add("2");
                        alarm_pick_index_3_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_3_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("2");
                        alarm_pick_index_3_layout.setBackgroundResource(R.drawable.border_light_grey);
                        alarm_pick_index_3_tv.setTextColor(Color.BLACK);
                    }
                    c3++;
                    break;
                case R.id.alarm_pick_index_4_layout:
                    if (c4 % 2 == 0)
                    {
                        resultList.add("3");
                        alarm_pick_index_4_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_4_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("3");
                        alarm_pick_index_4_layout.setBackgroundResource(R.drawable.border_light_grey);
                        alarm_pick_index_4_tv.setTextColor(Color.BLACK);
                    }
                    c4++;
                    break;
                case R.id.alarm_pick_index_5_layout:
                    if (c5 % 2 == 0)
                    {
                        resultList.add("4");
                        alarm_pick_index_5_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_5_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("4");
                        alarm_pick_index_5_layout.setBackgroundResource(R.drawable.border_light_grey);
                        alarm_pick_index_5_tv.setTextColor(Color.BLACK);
                    }
                    c5++;
                    break;
                case R.id.alarm_pick_index_6_layout:
                    if (c6 % 2 == 0)
                    {
                        resultList.add("5");
                        alarm_pick_index_6_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_6_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("5");
                        alarm_pick_index_6_layout.setBackgroundResource(R.drawable.border_light_grey);
                        alarm_pick_index_6_tv.setTextColor(Color.BLACK);
                    }
                    c6++;
                    break;
                case R.id.alarm_pick_index_7_layout:
                    if (c7 % 2 == 0)
                    {
                        resultList.add("6");
                        alarm_pick_index_7_layout.setBackgroundResource(R.drawable.border_green);
                        alarm_pick_index_7_tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        resultList.remove("6");
                        alarm_pick_index_7_layout.setBackgroundResource(R.drawable.border_light_grey);
                        alarm_pick_index_7_tv.setTextColor(Color.BLACK);
                    }
                    c7++;
                    break;
                case R.id.alarm_picker_delete_btn:
                    //              deleteClockFromServer(editId);
                    deleteClock();
                    break;
            }
        }
    };

    private void addTopBarToHead()
    {
        fl_header_alarmpick = (FrameLayout) findViewById(R.id.fl_header_alarmpick);
        String title = isEdit ? getString(R.string.title_edit_alarm)
                : getString(R.string.title_add_alarm);
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
                            String alarmTime = "";
                            if (resultList.size() == 0)
                            {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.alarm_choose_date),
                                        Toast.LENGTH_SHORT).show();
                                isClick = false;
                                return;
                            }

                            String resultDate = "";
                            for (String s : resultList)
                            {
                                resultDate = s + "-" + resultDate;
                            }
                            resultDate = sortWeekDay(resultDate);

                            String origin = Preferences.getInstance(getApplicationContext())
                                    .getAlarmTime();
                            if (isEdit)
                            {
                                if (!TextUtils.isEmpty(origin))
                                {
                                    if (origin.indexOf("--") >= 0)
                                    {
                                        String[] tmp = origin.split("--");
                                        for (int i = 0, l = tmp.length; i < l; i++)
                                        {
                                            if (i == editPos)
                                            {
                                                alarmTime = alarmTime + "--"
                                                        + hour + ":" + min
                                                        + " " + resultDate;
                                            }
                                            else
                                            {
                                                alarmTime = alarmTime + "--"
                                                        + tmp[i];
                                            }

                                        }
                                        alarmTime = alarmTime.substring(2);
                                    }
                                    else
                                    {
                                        alarmTime = origin.replace(origin, hour
                                                + ":" + min + " " + resultDate);
                                    }
                                }
                            }
                            else
                            {
                                if (!TextUtils.isEmpty(origin))
                                {
                                    alarmTime = origin + "--" + hour + ":"
                                            + min + " " + resultDate;
                                }
                                else
                                {
                                    alarmTime = hour + ":"
                                            + min + " " + resultDate;
                                }
                            }
                            Preferences.getInstance(getApplicationContext())
                                    .setAlarmTime(alarmTime);
                            sendBroadcast(broadcastIntent);
                            finish();
                            //                          addClockFromServer(editId, Preferences.getInstance(getApplicationContext())
                            //                                  .getDeviceId(), hour + ":"
                            //                                          + min + ":" + "00", resultList);
                        }
                    }
                });
        fl_header_alarmpick.addView(actionBar);
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
                //              textView.setTextColor(selectedColor);
                convertView.setTag(textView);
            }
            else
            {
                textView = (TextView) convertView.getTag();
            }

            //          if (null == textView) {
            //              textView = (TextView) convertView;
            //          }

            TextInfo info = mData.get(position);
            textView.setText(info.mText);
            textView.setTextColor(info.mColor);

            if (alarm_picker_hour == parent)
            {
                if (position == index__h)
                {
                    if (isFirstShowHour)
                    {
                        textView.setTextColor(selectedColor);
                    }
                    else
                    {
                        textView.setTextColor(sideColor);
                    }
                    return convertView;
                }
                if ((position == index__h - 1 && index__h != 0)
                        || (position == index__h + 1) && index__h != 0)
                {
                    textView.setTextColor(sideColor);
                    return convertView;
                }
            }
            else if (alarm_picker_min == parent)
            {
                if (position == index__m)
                {
                    if (isFirstShowMin)
                    {
                        textView.setTextColor(selectedColor);
                    }
                    else
                    {
                        textView.setTextColor(sideColor);
                    }
                    return convertView;
                }
                if ((position == index__m - 1 && index__m != 0)
                        || (position == index__m + 1) && index__m != 0)
                {
                    textView.setTextColor(sideColor);
                    return convertView;
                }
            }

            return convertView;
        }
    }

    private String sortWeekDay(String weekday)
    {
        String result = "";
        String[] ss = weekday.split("-");
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

    private final static int ADD_CLOCK = 1;

    private final static int DEL_CLOCK = 2;

    private final static int START_ADD_CLOCK = 3;

    private final static int START_DEL_CLOCK = 4;

    private ProgressDialog dialog;

    private MyExceptionDialog myExceptionDialog;

    private List<Integer> server_type = new ArrayList<Integer>();

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case START_ADD_CLOCK:
                    dialog = new ProgressDialog(AlarmPickActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage(getString(R.string.alarm_ready_to_save));
                    dialog.show();
                    break;
                case START_DEL_CLOCK:
                    dialog = new ProgressDialog(AlarmPickActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage(getString(R.string.alarm_ready_to_delete));
                    dialog.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    switch (server_type.get(0))
                    {
                        case ADD_CLOCK:
                            Log.d("", "daitm--------添加闹钟成功");
                            dialog.setMessage(getString(R.string.alarm_add_alarm_success));
                            dialog.dismiss();
                            sendBroadcast(broadcastIntent);
                            finish();
                            break;
                        case DEL_CLOCK:
                            Log.d("", "daitm--------删除闹钟成功");
                            dialog.setMessage(getString(R.string.alarm_delete_alarm_success));
                            dialog.dismiss();
                            sendBroadcast(broadcastIntent);
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
     * 添加/编辑闹钟
     * @param id 如果新增，此为空
     * @param deviceId
     * @param clocktime
     * @param week_day
     */
    private void addClockFromServer(String id, int deviceId, String clocktime,
            List<String> week_day)
    {
        server_type.add(ADD_CLOCK);
        if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            isClick = false;
            return;
        }
        mHandler.sendEmptyMessage(START_ADD_CLOCK);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("id", id);
            obj.put("deviceId", deviceId);
            obj.put("clocktime", clocktime);
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
                Constants.ADD_CLOCK,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private void deleteClockFromServer(String id)
    {
        server_type.add(DEL_CLOCK);
        if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler.sendEmptyMessage(START_DEL_CLOCK);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("id", Integer.parseInt(id));
            obj.put("deviceId",
                    Preferences.getInstance(getApplicationContext())
                            .getDeviceId());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.DEL_CLOCK,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private View touchedView = null;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        touchedView = v;
        return gestureDetector.onTouchEvent(event);
    }

    private void deleteClock()
    {
        String[] items = Preferences.getInstance(getApplicationContext())
                .getAlarmTime()
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
                    .setAlarmTime(tmp.substring(2, tmp.length()));
        }
        else
        {
            Preferences.getInstance(getApplicationContext()).setAlarmTime("");
        }
        sendBroadcast(broadcastIntent);
        finish();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart("AlarmPickActivity");
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        MobclickAgent.onPageEnd("AlarmPickActivity");
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
