package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.adapter.HomeworkAdapter;
import com.smarthome.client2.familySchool.model.Homework;
import com.smarthome.client2.familySchool.model.Subject;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author n003913
 * 家庭作业页面（老师、家长）
 */
public class HomeworkActivity extends BaseActivity
{

    private ImageView ivBack;

    private AbPullListView lv;

    private TextView tvPublish;

    /**
     * 以何种身份查看该页面
     */
    private boolean isTeacher = false;

    private String classId;

    private String className;

    private String teacherId;

    private String userId;

    private ArrayList<Homework> homeworkList;

    private HomeworkAdapter mAdapter;

    private long topid = -1;

    private long bottomid = -1;

    private boolean isGetNew = false;

    private boolean isAll = false;

    private ArrayList<Subject> subjectList;

    private String[] subArray;

    private Handler homeworkHandler = new Handler()
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
                        ArrayList<Homework> tmpList = ResultParsers.parserHomework(result);
                        if (tmpList == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (tmpList.isEmpty())
                        {
                            if (homeworkList.isEmpty())
                            {
                                lv.getFooterView().setState("没有家庭作业");
                            }
                            else
                            {
                                if (isGetNew)
                                {
                                    showToast("暂时没有新的家庭作业");
                                }
                                else
                                {
                                    isAll = true;
                                    lv.getFooterView()
                                            .setState(getString(R.string.already_all));
                                }
                            }
                        }
                        else
                        {
                            if (isGetNew)
                            {
                                homeworkList.addAll(0, tmpList);
                                lv.getFooterView()
                                        .setState(getString(R.string.pulllist_load_more));
                            }
                            else
                            {
                                homeworkList.addAll(tmpList);
                                if (tmpList.size() < 10)
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
                            topid = homeworkList.get(0).getId();
                            bottomid = homeworkList.get(homeworkList.size() - 1)
                                    .getId();
                            mAdapter.notifyDataSetChanged();
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
                default:
                    break;
            }
        }
    };

    private Handler subjectHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    showProgressDialog(R.string.is_loading);
                    break;
                case FsConstants.HTTP_SUCCESS:
                    removeProgressDialog();
                    String result = (String) msg.obj;
                    String code = ResultParsers.getCode(result);
                    if (code.equals("200"))
                    {
                        ArrayList<Subject> tmpList = ResultParsers.parserSubjects(result);
                        if (tmpList == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else
                        {
                            if (tmpList.isEmpty())
                            {
                                showToast("没有对应的课目");
                            }
                            else
                            {
                                subjectList = tmpList;
                                int size = subjectList.size();
                                subArray = new String[subjectList.size()];
                                for (int i = 0; i < size; i++)
                                {
                                    subArray[i] = subjectList.get(i).getName();
                                }
                                showSubjects();
                            }
                        }
                    }
                    else
                    {
                        showToast(R.string.server_offline);
                    }
                    break;
                case FsConstants.HTTP_FAILURE:
                    removeProgressDialog();
                    showToast(R.string.no_network);
                    break;
                default:
                    break;
            }
        }
    };

    /* (non-Javadoc)
     * @see com.smarthome.client2.familySchool.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_activity_homework);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        lv = (AbPullListView) findViewById(R.id.lv);
        tvPublish = (TextView) findViewById(R.id.tv_publish);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));

        homeworkList = new ArrayList<Homework>();
        mAdapter = new HomeworkAdapter(this, homeworkList);
        lv.setAdapter(mAdapter);

        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                getNewHomework();
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
                loadMoreHomework();
            }
        });

        OnClickListener listener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v.getId() == R.id.iv_back)
                {
                    ivBack.setImageResource(R.drawable.back_in);
                    finish();
                }
                else if (v.getId() == R.id.tv_publish)
                {
                    if (subjectList == null)
                    {
                        subjectList = new ArrayList<Subject>();
                    }
                    if (subjectList.isEmpty())
                    {
                        getSubjects();
                    }
                    else
                    {
                        showSubjects();
                    }
                }
            }
        };

        ivBack.setOnClickListener(listener);
        tvPublish.setOnClickListener(listener);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        classId = bundle.getString("class_id");
        className = bundle.getString("className");
        teacherId = bundle.getString("teacherId");
        userId = bundle.getString("stu_user_id");
        if (teacherId != null)
        {
            isTeacher = true;
            tvPublish.setVisibility(View.VISIBLE);
        }
        else
        {
            isTeacher = false;
            tvPublish.setVisibility(View.INVISIBLE);
        }

        lv.performRefresh();
    }

    /**
     * 获取新作业
     */
    private void getNewHomework()
    {
        isGetNew = true;
        HttpJson pJson = new HttpJson();
        pJson.put("classid", classId);
        if (!isTeacher)
        {
            pJson.put("stu_user_id", userId);
        }
        pJson.put("loadsize", 10);
        if (topid != -1)
        {
            pJson.put("topid", topid);
        }
        MyHttpUtil.post("/homeandschool/seeHomeWork.action",
                pJson,
                homeworkHandler);
    }

    /**
     * 加载历史作业
     */
    private void loadMoreHomework()
    {
        isGetNew = false;
        HttpJson pJson = new HttpJson();
        pJson.put("classid", classId);
        if (!isTeacher)
        {
            pJson.put("stu_user_id", userId);
        }
        pJson.put("loadsize", 10);
        if (bottomid != -1)
        {
            pJson.put("bottomid", bottomid);
        }
        MyHttpUtil.post("/homeandschool/seeHomeWork.action",
                pJson,
                homeworkHandler);
    }

    /**
     * 获取课程列表
     */
    private void getSubjects()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("classId", classId);
        pJson.put("teacherId", teacherId);
        MyHttpUtil.post("/homeandschool/getSubjects.action",
                pJson,
                subjectHandler);
    }

    /**
     * 显示选择课程对话框
     */
    private void showSubjects()
    {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("请选择课程")
                .setSingleChoiceItems(subArray,
                        -1,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                Subject subject = subjectList.get(which);
                                Intent intent = new Intent(
                                        HomeworkActivity.this,
                                        WriteMsgActivity.class);
                                intent.putExtra(FsConstants.TYPE_ADD_FLAG,
                                        FsConstants.TYPE_ADD_HOMEWORK);
                                intent.putExtra("className", className);
                                intent.putExtra("classId", classId);
                                intent.putExtra("subjectId", subject.getId());
                                intent.putExtra("subjectName",
                                        subject.getName());
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                .create();
        dialog.show();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRestart()
     */
    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (isTeacher && Preferences.getInstance(this).getPublishNew())
        {
            Preferences.getInstance(this).setPublishNew(false);
            lv.performRefresh();
        }
    }

}
