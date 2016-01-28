package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.Syllabus;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;
import com.smarthome.client2.familySchool.view.MyGridLayout;

/**
 * @author n003913
 * 课程表（教师）
 */
public class SyllabusActivityTeacher extends BaseActivity
{

    private ImageView iv_back;// 返回

    private TextView tv_class;// 显示班级

    private TextView tv_date;// 显示课表更新日期

    private MyGridLayout layout_syllabus;// 显示动态课表

    private ScrollView sv;// 动态课表的父布局

    private ImageView iv_pre;// 点击加载上一张课表

    private ImageView iv_next;// 点击加载下一张课表

    /*
     * 首次加载课表（最新）时无网络或者无数据时的提示信息，无网络时提示检查网络后点击刷新。
     * 如果在加载上一张课表时，出现上述情况，toast引导用户点击iv_pre重新尝试。
     */
    private TextView tv_failhint;

    private Syllabus syllabus;// 当前显示的课表

    private String classId;

    private String className;

    private ArrayList<Syllabus> list = new ArrayList<Syllabus>();// 保存加载过的课表，从最新到最老

    private int syllabusIndex = 0;// 当前显示的课表在list中的索引

    private boolean isAll = false;// 历史课表是否已经完全加载

    private AbPullListView lv;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    if (syllabus != null)
                    {
                        showProgressDialog(R.string.is_loading);
                    }
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
                        Syllabus temp = ResultParsers.parserSyllabus(result);
                        if (temp == null)
                        {
                            showToast(R.string.data_parser_error);
                            if (syllabus == null)
                            {
                                // tv_failhint.setVisibility(View.VISIBLE);
                                // tv_failhint.setText("数据错误");
                                // tv_failhint.setClickable(false);
                            }
                            else
                            {
                                syllabusIndex--;
                            }
                        }
                        else
                        {
                            if (temp.getId() == -1)
                            {
                                if (syllabus == null)
                                {
                                    lv.getFooterView().setState("没有课程表");
                                }
                                else
                                {
                                    showToast("历史课表已经完全加载");
                                    iv_pre.setVisibility(View.INVISIBLE);
                                    isAll = true;
                                    syllabusIndex--;
                                }
                                return;
                            }
                            if (syllabus == null)
                            {
                                // tv_failhint.setVisibility(View.GONE);
                                iv_pre.setVisibility(View.VISIBLE);
                                lv.setVisibility(View.INVISIBLE);
                            }
                            syllabus = temp;
                            list.add(syllabus);
                            if (syllabusIndex == 1)
                            {
                                iv_next.setVisibility(View.VISIBLE);
                            }
                            addSyllabusViews();
                        }
                    }
                    else
                    {
                        showToast(R.string.server_offline);
                        if (syllabus == null)
                        {
                            // tv_failhint.setVisibility(View.VISIBLE);
                            // tv_failhint.setText("没有课程表");
                            // tv_failhint.setClickable(false);
                        }
                        else
                        {
                            syllabusIndex--;
                        }
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
                    if (syllabus == null)
                    {
                        // tv_failhint.setVisibility(View.VISIBLE);
                        // tv_failhint.setText(R.string.no_network);
                        // tv_failhint.setClickable(true);
                        // lv.getFooterView()
                        //        .setState(getString(R.string.no_network));
                    }
                    else
                    {
                        syllabusIndex--;
                    }
                    showToast(R.string.no_network);
                    break;
                case FsConstants.HTTP_FINISH:
                    removeProgressDialog();
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
        setContentView(R.layout.fs_activity_syllabus_teacher);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_class = (TextView) findViewById(R.id.tv_class);
        tv_date = (TextView) findViewById(R.id.tv_time);
        sv = (ScrollView) findViewById(R.id.sv);
        iv_pre = (ImageView) findViewById(R.id.iv_pre);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        tv_failhint = (TextView) findViewById(R.id.tv_failhint);

        lv = (AbPullListView) findViewById(R.id.lv);
        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1));
        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                getSyllabus();
            }

            @Override
            public void onLoadMore()
            {
                getSyllabus();
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
                        finish();
                        break;
                    case R.id.iv_next:
                        if (syllabusIndex == list.size() - 1)
                        {
                            iv_pre.setVisibility(View.VISIBLE);
                        }
                        syllabusIndex--;
                        if (syllabusIndex == 0)
                        {
                            iv_next.setVisibility(View.INVISIBLE);
                        }
                        syllabus = list.get(syllabusIndex);
                        addSyllabusViews();
                        break;
                    case R.id.iv_pre:
                        syllabusIndex++;
                        if (syllabusIndex >= list.size() && !isAll)
                        {
                            getSyllabus();
                        }
                        else
                        {
                            if (syllabusIndex == 1)
                            {
                                iv_next.setVisibility(View.VISIBLE);
                            }
                            if (isAll && (syllabusIndex == list.size() - 1))
                            {
                                iv_pre.setVisibility(View.INVISIBLE);
                            }
                            syllabus = list.get(syllabusIndex);
                            addSyllabusViews();
                        }
                        break;
                    case R.id.tv_failhint:
                        getSyllabus();
                        break;
                    default:
                        break;
                }

            }
        };

        iv_back.setOnClickListener(listener);
        iv_next.setOnClickListener(listener);
        iv_pre.setOnClickListener(listener);
        tv_failhint.setOnClickListener(listener);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        tv_class.setText(className);

        lv.performRefresh();
    }

    /**
     * 添加GridLayout动态布局
     */
    private void addSyllabusViews()
    {
        tv_date.setText(syllabus.getDate());
        if (layout_syllabus != null)
        {
            sv.removeView(layout_syllabus);
        }
        layout_syllabus = new MyGridLayout(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        sv.addView(layout_syllabus, params);
        layout_syllabus.addSyllabus(syllabus);
    }

    /**
     * 获取课程表
     */
    private void getSyllabus()
    {
        HttpJson params = new HttpJson();
        params.put("classid", classId);
        if (syllabus != null)
        {
            params.put("bottomid", syllabus.getId());
        }
        params.put("loadsize", 1);
        MyHttpUtil.post("/homeandschool/seeSubjectTable.action",
                params,
                mHandler);
    }

}
