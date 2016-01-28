package com.smarthome.client2.familySchool.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.adapter.AttendanceAdapterTeacher;
import com.smarthome.client2.familySchool.model.CardAnalysis;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.LogUtil;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;
import com.smarthome.client2.view.CalendarView;
import com.smarthome.client2.view.CalendarView.OnItemClickListener;

/**
 * @author n003913 出勤查看（教师）
 */
public class AttendanceActivityTeacher extends BaseActivity
{

    private ImageView iv_back;

    private TextView tv_date;

    private AbPullListView lv;

    private TextView tv_analyse;

    private ArrayList<CardAnalysis> list;

    private AttendanceAdapterTeacher adapter;

    private String date;

    private long bottomid = -1;

    private String classId;

    private RelativeLayout rl_calendar;

    private TextView tv_calendar;

    private ImageView iv_left;

    private ImageView iv_right;

    private CalendarView calendarView;

    private int[] colors;

    private boolean isAll = false;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
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
                        ArrayList<CardAnalysis> temp = ResultParsers.parserAttendanceTeacher(result);
                        if (temp == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (temp.isEmpty())
                        {
                            if (list.isEmpty())
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.no_attendance_record));
                            }
                            else
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.already_all));
                                isAll = true;
                            }
                        }
                        else
                        {
                            if (bottomid == -1)
                            {
                                list.clear();
                                isAll = false;
                            }
                            list.addAll(temp);
                            bottomid = list.get(list.size() - 1).getId();
                            if (temp.size() < 10)
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.already_all));
                            }
                            else
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.pulllist_load_more));
                            }
                        }
                    }
                    else
                    {
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
                    showToast(R.string.no_network);
                    break;
                case FsConstants.HTTP_FINISH:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private Handler nHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_SUCCESS:
                    String result = (String) msg.obj;
                    String code = ResultParsers.getCode(result);
                    if (code.equals("200"))
                    {
                        int[] a = ResultParsers.parserAttendanceAnalyse(result);
                        if (a == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else
                        {
                            tv_analyse.setText(formatAnalyse(a));
                        }
                    }
                    else
                    {
                        showToast(R.string.server_offline);
                    }
                    break;
                case FsConstants.HTTP_FAILURE:
                    showToast(R.string.no_network);
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
        setContentView(R.layout.fs_activity_attendance_teacher);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_date = (TextView) findViewById(R.id.tv_date);
        lv = (AbPullListView) findViewById(R.id.lv_attendance);
        tv_analyse = (TextView) findViewById(R.id.tv_summary);

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
                    AttendanceActivityTeacher.this.date = tmpDate;
                    bottomid = -1;
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
        list = new ArrayList<CardAnalysis>();
        adapter = new AttendanceAdapterTeacher(this, list);
        lv.setAdapter(adapter);

        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                bottomid = -1;
                getAttendance();
                getAnalyse();
            }

            @Override
            public void onLoadMore()
            {
                if (isAll)
                {
                    lv.stopLoadMore();
                    lv.getFooterView()
                            .setState(getString(R.string.already_all));
                    return;
                }
                getAttendance();
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

        classId = getIntent().getStringExtra("classId");

        lv.performRefresh();
    }

    private void getAttendance()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("classid", classId);
        pJson.put("date", date);
        pJson.put("loadsize", 10);
        if (bottomid != -1)
        {
            pJson.put("bottomid", bottomid);
        }
        MyHttpUtil.post("/homeandschool/getCardRecord.action", pJson, mHandler);
    }

    private void getAnalyse()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("classid", classId);
        pJson.put("date", date);
        MyHttpUtil.post("/homeandschool/getAttendanceAnalyse.action",
                pJson,
                nHandler);
    }

    private Spannable formatAnalyse(int[] a)
    {
        if (colors == null)
        {
            Resources resources = getResources();
            colors = new int[] { resources.getColor(R.color.attendance_late2),
                    resources.getColor(R.color.attendance_absent),
                    resources.getColor(R.color.attendance_late) };
        }
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("迟到")
                .append(a[0])
                .append("人")
                .append(" 缺卡")
                .append(a[1])
                .append("人")
                .append(" 早退")
                .append(a[2])
                .append("人");
        Spannable spannable = new SpannableString(sBuilder);
        int[] len = new int[] { String.valueOf(a[0]).length(),
                String.valueOf(a[1]).length(), String.valueOf(a[2]).length() };
        spannable.setSpan(new ForegroundColorSpan(colors[0]),
                0,
                3 + len[0],
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(colors[1]), 4 + len[0], 7
                + len[0] + len[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(colors[2]),
                8 + len[0] + len[1],
                spannable.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
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
