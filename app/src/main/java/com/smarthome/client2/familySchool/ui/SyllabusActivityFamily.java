package com.smarthome.client2.familySchool.ui;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.Syllabus;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;
import com.smarthome.client2.familySchool.view.MyGridLayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 
 * @author n003913
 * 课程表（家长）
 */
public class SyllabusActivityFamily extends BaseActivity
{

    private ImageView iv_back;// 返回

    private TextView tv_class;// 显示班级

    private TextView tv_date;// 显示课表更新日期

    private MyGridLayout layout_syllabus;// 显示动态课表

    private ScrollView sv;// 动态课表的父布局

    private TextView tv_failhint;

    private Syllabus syllabus;

    private String classId;

    private String className;

    private String userId;

    private AbPullListView lv;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    // showProgressDialog("正在加载……");
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
                        syllabus = ResultParsers.parserSyllabus(result);
                        if (syllabus == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (syllabus.getId() == -1)
                        {
                            lv.getFooterView().setState("没有课程表");
                        }
                        else
                        {
                            // tv_failhint.setVisibility(View.GONE);
                            tv_date.setText(syllabus.getDate());
                            addSyllabusViews();
                            lv.setVisibility(View.INVISIBLE);
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
                    // removeProgressDialog();
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
        setContentView(R.layout.fs_activity_syllabus_family);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_class = (TextView) findViewById(R.id.tv_class);
        tv_date = (TextView) findViewById(R.id.tv_time);
        sv = (ScrollView) findViewById(R.id.sv);
        tv_failhint = (TextView) findViewById(R.id.tv_fail);

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

        iv_back.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                iv_back.setImageResource(R.drawable.back_in);
                finish();
            }
        });

        tv_failhint.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getSyllabus();
            }
        });

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        userId = getIntent().getStringExtra("userId");
        tv_class.setText("课程表" + "（" + className + "）");

        lv.performRefresh();
    }

    /**
     * 向GridLayout中动态添加布局
     */
    private void addSyllabusViews()
    {
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
        params.put("loadsize", 1);
        params.put("userid", userId);
        MyHttpUtil.post("/homeandschool/seeSubjectTable.action",
                params,
                mHandler);
    }

}
