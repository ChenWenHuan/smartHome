package com.smarthome.client2.familySchool.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.adapter.AttendanceAdapterFamily;
import com.smarthome.client2.familySchool.model.CardLog;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.LogUtil;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;
import com.smarthome.client2.view.CalendarView;
import com.smarthome.client2.view.CalendarView.OnItemClickListener;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author n003913 出勤查看（家长）
 */
public class AttendanceActivityFamily extends BaseActivity
{

    private ImageView iv_back;

    private TextView tv_date;

    private AbPullListView lv;

    private ArrayList<CardLog> list;// 当前显示的出勤记录

    // TODO 是否添加大小限制
    private HashMap<String, ArrayList<CardLog>> map;// 保存成功加载过的不同日期的考勤记录

    private AttendanceAdapterFamily adapter;

    private String date;

    private String studentId;

    private String mUserId;

    private RelativeLayout rl_calendar;

    private TextView tv_calendar;

    private ImageView iv_left;

    private ImageView iv_right;

    private CalendarView calendarView;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    tv_date.setClickable(false);
                    break;
                case FsConstants.HTTP_SUCCESS:
                    if (lv.isRefreshing())
                    {
                        lv.stopRefresh();
                    }
                    if (lv.isLoading())
                    {
                        lv.stopLoadMore();
                    }
                    String result = (String) msg.obj;
                    String code = ResultParsers.getCode(result);
                    if (code.equals("200"))
                    {
                        ArrayList<CardLog> temp = ResultParsers.parserAttendanceFamily(result);
                        if (temp == null)
                        {
                            map.remove(date);
                            list = new ArrayList<CardLog>();
                            showToast(R.string.data_parser_error);
                        }
                        else if (temp.isEmpty())
                        {
                            map.remove(date);
                            list = temp;
                            lv.getFooterView().setState("没有出勤记录");
                        }
                        else
                        {
                            list = temp;
                            map.put(date, list);
                            lv.getFooterView()
                                    .setState(getString(R.string.already_all));
                        }
                    }
                    else
                    {
                        list = new ArrayList<CardLog>();
                        showToast(R.string.server_offline);
                    }
                    break;
                case FsConstants.HTTP_FAILURE:
                    if (lv.isRefreshing())
                    {
                        lv.stopRefresh();
                    }
                    if (lv.isLoading())
                    {
                        lv.stopLoadMore();
                    }
                    list = new ArrayList<CardLog>();
                    showToast(R.string.no_network);
                    break;
                case FsConstants.HTTP_FINISH:
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    tv_date.setClickable(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_activity_attendance_family);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_date = (TextView) findViewById(R.id.tv_date);
        lv = (AbPullListView) findViewById(R.id.lv_attendance);
        rl_calendar = (RelativeLayout) findViewById(R.id.rl_date);
        tv_calendar = (TextView) findViewById(R.id.tv_calendar);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        calendarView = (CalendarView) findViewById(R.id.calendar);

        calendarView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void OnItemClick(Date date)
            {
                rl_calendar.setVisibility(View.GONE);
                LogUtil.e("date", calendarView.getFormatDate() + "");
                String tmpDate = calendarView.getFormatDate();
                if (tmpDate == null)
                {
                    showToast("未来日期,没有数据");
                }
                else
                {
                    AttendanceActivityFamily.this.date = tmpDate;
                    lv.performRefresh();
                    tv_date.setText(tmpDate);
                }
            }
        });

        OnClickListener listener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.iv_back:
                        iv_back.setImageResource(R.drawable.back_in);
                        if (rl_calendar.getVisibility() == View.VISIBLE)
                        {
                            rl_calendar.setVisibility(View.GONE);
                        }
                        else
                        {
                            finish();
                        }
                        break;
                    case R.id.iv_left:
                        calendarView.clickLeftMonth();
                        tv_calendar.setText(calendarView.getYearAndmonth());
                        break;
                    case R.id.iv_right:
                        calendarView.clickRightMonth();
                        tv_calendar.setText(calendarView.getYearAndmonth());
                        break;
                    case R.id.tv_date:
                        if (rl_calendar.getVisibility() == View.GONE)
                        {
                            calendarView.backRightDate();
                            tv_calendar.setText(calendarView.getYearAndmonth());
                            rl_calendar.setVisibility(View.VISIBLE);
                            calendarView.invalidate();
                        }
                        else
                        {
                            rl_calendar.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        iv_back.setOnClickListener(listener);
        iv_left.setOnClickListener(listener);
        iv_right.setOnClickListener(listener);
        tv_date.setOnClickListener(listener);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        map = new HashMap<String, ArrayList<CardLog>>();
        list = new ArrayList<CardLog>();
        adapter = new AttendanceAdapterFamily(this, list);
        lv.setAdapter(adapter);

        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                getAttendance();
            }

            @Override
            public void onLoadMore()
            {
                if (list.isEmpty())
                {
                    getAttendance();
                }
                else
                {
                    lv.stopLoadMore();
                    lv.getFooterView()
                            .setState(getString(R.string.already_all));
                }
            }
        });

        lv.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (rl_calendar.getVisibility() == View.VISIBLE)
                {
                    rl_calendar.setVisibility(View.GONE);
                }
                return false;
            }
        });

        Date nowDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        date = format.format(nowDate);
        tv_date.setText("今天");

        studentId = getIntent().getStringExtra("studentId");
        mUserId = getIntent().getStringExtra("userId");

        lv.performRefresh();
    }

    private void getAttendance()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("studentid", studentId);
        pJson.put("userid", mUserId);
        pJson.put("date", date);
        MyHttpUtil.post("/homeandschool/getCardRecord.action", pJson, mHandler);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        if (rl_calendar.getVisibility() == View.VISIBLE)
        {
            rl_calendar.setVisibility(View.GONE);
        }
        else
        {
            finish();
        }
    }

}
