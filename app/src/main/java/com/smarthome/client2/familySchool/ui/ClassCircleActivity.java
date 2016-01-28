package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.adapter.ContactsListAdapter;
import com.smarthome.client2.familySchool.model.ClassContacts;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;

/**
 * @author n003913
 * 班级圈（教师、家长）
 */
public class ClassCircleActivity extends BaseActivity
{

    private ImageView iv_back;// 返回按钮

    private TextView tv_teacher;// 切换到老师通讯录按钮

    private TextView tv_family;// 切换到家长通讯录按钮

    private AbPullListView lv;

    private ArrayList<ClassContacts> list_teacher;

    private ArrayList<ClassContacts> list_family;

    private ContactsListAdapter adapter;

    private int type = 2;// 2教师，1家长

    private String classId;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    tv_family.setClickable(false);
                    tv_teacher.setClickable(false);
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
                    Log.d("XXX", "circle Fragments:" + result);
                    if (code.equals("200"))
                    {
                        ArrayList<ClassContacts> temp = ResultParsers.parserContacts(result);
                        if (temp == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (temp.isEmpty())
                        {
                            if (type == 1)
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.no_family_contacts));
                            }
                            else
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.no_teacher_contacts));
                            }
                        }
                        else
                        {
                            if (type == 1)
                            {
                                list_family.addAll(temp);
                            }
                            else
                            {
                                list_teacher.addAll(temp);
                            }
                            lv.getFooterView()
                                    .setState(getString(R.string.already_all));
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
                    tv_family.setClickable(true);
                    tv_teacher.setClickable(true);
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
        setContentView(R.layout.fs_activity_class_circle);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv = (AbPullListView) findViewById(R.id.lv_contacts);
        tv_teacher = (TextView) findViewById(R.id.tv_teacher);
        tv_family = (TextView) findViewById(R.id.tv_family);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        list_teacher = new ArrayList<ClassContacts>();
        list_family = new ArrayList<ClassContacts>();
        adapter = new ContactsListAdapter(list_teacher, this);
        lv.setAdapter(adapter);

        getResources().getColor(R.color.darkgray);

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
                    case R.id.tv_teacher:
                        tv_family.setBackgroundResource(R.drawable.shape_white_corner);
                        tv_teacher.setBackgroundResource(R.drawable.shape_yellow_corner);
                        tv_family.setTextColor(ClassCircleActivity.this.getResources()
                                .getColor(R.color.class_circle_text_deep));
                        tv_teacher.setTextColor(ClassCircleActivity.this.getResources()
                                .getColor(R.color.white));
                        adapter.setList(list_teacher);
                        adapter.notifyDataSetChanged();
                        type = 2;
                        if (list_teacher.isEmpty())
                        {
                            lv.performRefresh();

                        }
                        else
                        {
                            lv.getFooterView()
                                    .setState(getString(R.string.already_all));
                        }
                        break;
                    case R.id.tv_family:
                        tv_family.setBackgroundResource(R.drawable.shape_yellow_corner);
                        tv_teacher.setBackgroundResource(R.drawable.shape_white_corner);
                        tv_family.setTextColor(ClassCircleActivity.this.getResources()
                                .getColor(R.color.white));
                        tv_teacher.setTextColor(ClassCircleActivity.this.getResources()
                                .getColor(R.color.class_circle_text_deep));
                        adapter.setList(list_family);
                        adapter.notifyDataSetChanged();
                        type = 1;
                        if (list_family.isEmpty())
                        {
                            lv.performRefresh();
                        }
                        else
                        {
                            lv.getFooterView()
                                    .setState(getString(R.string.already_all));
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                if (type == 1)
                {
                    if (list_family.isEmpty())
                    {
                        getContacts();
                    }
                    else
                    {
                        lv.stopRefresh();
                        lv.getFooterView()
                                .setState(getString(R.string.already_all));
                    }
                }
                else
                {
                    if (list_teacher.isEmpty())
                    {
                        getContacts();
                    }
                    else
                    {
                        lv.stopRefresh();
                        lv.getFooterView()
                                .setState(getString(R.string.already_all));
                    }
                }
            }

            @Override
            public void onLoadMore()
            {
                if (type == 1 && !list_family.isEmpty())
                {
                    lv.stopLoadMore();
                    lv.getFooterView()
                            .setState(getString(R.string.already_all));
                    return;
                }
                if (type == 2 && !list_teacher.isEmpty())
                {
                    lv.stopLoadMore();
                    lv.getFooterView()
                            .setState(getString(R.string.already_all));
                    return;
                }
                getContacts();
            }
        });

        iv_back.setOnClickListener(listener);
        tv_teacher.setOnClickListener(listener);
        tv_family.setOnClickListener(listener);

        classId = getIntent().getStringExtra("classId");

        tv_teacher.performClick();
    }

    private void getContacts()
    {
        HttpJson params = new HttpJson();
        params.put("classid", classId);
        params.put("type", type);
//        Log.d("XXX", " ClassCircleActivity getContacts :" + classId);
//        Log.d("XXX", " ClassCircleActivity getContacts :" + type);
        MyHttpUtil.post("/homeandschool/getContactsList.action",
                params,
                mHandler);
    }

}
