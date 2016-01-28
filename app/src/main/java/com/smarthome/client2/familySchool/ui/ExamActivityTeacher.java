package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.adapter.ExamAdapterTeacher;
import com.smarthome.client2.familySchool.model.Exam;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author n003913
 * 考试记录页面
 */
public class ExamActivityTeacher extends BaseActivity
{
    private ImageView ivBack;

    private AbPullListView lv;

    private TextView tvPublish;

    private ArrayList<Exam> examList;

    private ExamAdapterTeacher examAdapter;

    private long topid = -1;

    private long bottomid = -1;

    private boolean isGetNew = false;

    private ArrayList<Subject> subjectList;

    private String[] subArray;

    private String classId;

    private String className;

    private String teacherId;

    private Handler examHandler = new Handler()
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
                        ArrayList<Exam> tmpList = ResultParsers.parserExamTeacher(result);
                        if (tmpList == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (tmpList.isEmpty())
                        {
                            if (examList.isEmpty())
                            {
                                lv.getFooterView().setState("没有成绩信息");
                            }
                            else
                            {
                                if (isGetNew)
                                {
                                    showToast("暂时没有新的考试动态");
                                }
                                else
                                {
                                    lv.getFooterView()
                                            .setState(getString(R.string.already_all));
                                }
                            }
                        }
                        else
                        {
                            if (isGetNew)
                            {
                                examList.addAll(0, tmpList);
                                lv.getFooterView()
                                        .setState(getString(R.string.pulllist_load_more));
                            }
                            else
                            {
                                examList.addAll(tmpList);
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
                            topid = examList.get(0).getId();
                            bottomid = examList.get(examList.size() - 1)
                                    .getId();
                            examAdapter.notifyDataSetChanged();
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
                        else if (tmpList.isEmpty())
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
        setContentView(R.layout.fs_activity_exam_teacher);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        lv = (AbPullListView) findViewById(R.id.lv);
        tvPublish = (TextView) findViewById(R.id.tv_publish);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));

        examList = new ArrayList<Exam>();
        examAdapter = new ExamAdapterTeacher(this, examList);
        lv.setAdapter(examAdapter);

        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                getNewExam();
            }

            @Override
            public void onLoadMore()
            {
                loadMoreExam();
            }
        });

        lv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                position--;// 有一个headerView
                // 如果不在list范围内，返回
                if (position < 0 || position >= examList.size())
                {
                    return;
                }
                Exam item = examList.get(position);
                Intent intent = new Intent(ExamActivityTeacher.this,
                        ScoreActivityTeacher.class);
                intent.putExtra("examId", item.getId() + "");
                intent.putExtra("examName", item.getName());
                intent.putExtra("subject", item.getSubject());
                intent.putExtra("examDate", item.getDate());
                intent.putExtra("type", item.getType() + "");
                startActivity(intent);
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

        lv.performRefresh();
    }

    /**
     * 获取新的考试
     */
    private void getNewExam()
    {
        isGetNew = true;
        HttpJson pJson = new HttpJson();
        pJson.put("classid", classId);
        pJson.put("loadsize", 10);
        if (topid != -1)
        {
            pJson.put("topid", topid);
        }
        MyHttpUtil.post("/homeandschool/seeScore.action", pJson, examHandler);
    }

    /**
     * 加载历史考试记录
     */
    private void loadMoreExam()
    {
        isGetNew = false;
        HttpJson pJson = new HttpJson();
        pJson.put("classid", classId);
        pJson.put("loadsize", 10);
        if (bottomid != -1)
        {
            pJson.put("bottomid", bottomid);
        }
        MyHttpUtil.post("/homeandschool/seeScore.action", pJson, examHandler);
    }

    /**
     * 获取科目列表
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
     * 显示科目列表
     */
    private void showSubjects()
    {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.fs_dialog_select_subject, null);
        final Spinner sp_subject = (Spinner) view.findViewById(R.id.sp_subject);
        final Spinner sp_score = (Spinner) view.findViewById(R.id.sp_score);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1,
                subArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_subject.setAdapter(adapter);

        new AlertDialog.Builder(this).setTitle("请选择科程和成绩类型")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(ExamActivityTeacher.this,
                                ScoreInputActivity.class);
                        intent.putExtra("fromWhere", "fromScore");
                        intent.putExtra("className", className);
                        intent.putExtra("classId", classId);
                        intent.putExtra("subjectName",
                                subArray[sp_subject.getSelectedItemPosition()]);
                        intent.putExtra("subjectId",
                                subjectList.get(sp_subject.getSelectedItemPosition())
                                        .getId());
                        intent.putExtra("input_type",
                                sp_score.getSelectedItemPosition());
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRestart()
     */
    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (Preferences.getInstance(this).getPublishNew())
        {
            Preferences.getInstance(this).setPublishNew(false);
            lv.performRefresh();
        }
    }
}
