package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.adapter.NoticeAdapter;
import com.smarthome.client2.familySchool.model.Notice;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author n003913
 * 通知公告页面（老师、家长）
 */
public class AnnouncementActivity extends BaseActivity
{

    private ImageView ivBack;

    private AbPullListView lv;

    private TextView tvPublish;

    /**
     * 以何种身份查看该页面
     */
    private boolean isTeacher = false;

    private String userId;

    private String className;

    private String classId;

    private long topid = -1;

    private long bottomid = -1;

    private boolean isGetNew = false;

    private ArrayList<Notice> mList;

    private NoticeAdapter mAdapter;

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
                        ArrayList<Notice> tmpList = ResultParsers.parserNotice(result);
                        if (tmpList == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (tmpList.isEmpty())
                        {
                            if (mList.isEmpty())
                            {
                                lv.getFooterView().setState("没有通知公告");
                            }
                            else
                            {
                                if (isGetNew)
                                {
                                    showToast("暂时没有新的通知公告");
                                }
                                else
                                {
                                    lv.getFooterView()
                                            .setState(getString(R.string.already_all));
                                    isAll = true;
                                }
                            }
                        }
                        else
                        {
                            if (isGetNew)
                            {
                                mList.addAll(0, tmpList);
                                lv.getFooterView()
                                        .setState(getString(R.string.pulllist_load_more));
                            }
                            else
                            {
                                mList.addAll(tmpList);
                                if (tmpList.size() < 10)
                                {
                                    lv.getFooterView()
                                            .setState(getString(R.string.already_all));
                                    isAll = true;
                                }
                                else
                                {
                                    lv.getFooterView()
                                            .setState(getString(R.string.pulllist_load_more));
                                }
                            }
                            topid = mList.get(0).getId();
                            bottomid = mList.get(mList.size() - 1).getId();
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

    /* (non-Javadoc)
     * @see com.smarthome.client2.familySchool.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_activity_announcement);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        lv = (AbPullListView) findViewById(R.id.lv);
        tvPublish = (TextView) findViewById(R.id.tv_publish);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userId = bundle.getString("stu_user_id");
        classId = bundle.getString("class_id");
        className = bundle.getString("className");
        if (userId == null)
        {
            isTeacher = true;
            tvPublish.setVisibility(View.VISIBLE);
        }
        else
        {
            isTeacher = false;
            tvPublish.setVisibility(View.INVISIBLE);
        }

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));


        mList = new ArrayList<Notice>();
        mAdapter = new NoticeAdapter(this, mList, isTeacher);
        lv.setAdapter(mAdapter);

        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                getNewNotice();
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
                loadMoreNotice();
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
                    Intent intent = new Intent(AnnouncementActivity.this,
                            WriteMsgActivity.class);
                    intent.putExtra("classId", classId);
                    intent.putExtra("className", className);
                    intent.putExtra(FsConstants.TYPE_ADD_FLAG,
                            FsConstants.TYPE_ADD_NOTICE);
                    startActivity(intent);
                }
            }
        };

        ivBack.setOnClickListener(listener);
        tvPublish.setOnClickListener(listener);

        lv.performRefresh();
    }

    /**
     * 获取新的通知
     */
    private void getNewNotice()
    {
        isGetNew = true;
        HttpJson pJson = new HttpJson();
        pJson.put("loadsize", 10);
        if (!isTeacher)
        {
            pJson.put("stu_user_id", userId);
        }
        else
        {
            pJson.put("classid", classId);
        }
        if (topid != -1)
        {
            pJson.put("topid", topid);
        }
        MyHttpUtil.post("/homeandschool/getNotices.action", pJson, mHandler);
    }

    /**
     * 获取历史通知
     */
    private void loadMoreNotice()
    {
        isGetNew = false;
        HttpJson pJson = new HttpJson();
        pJson.put("loadsize", 10);
        if (!isTeacher)
        {
            pJson.put("stu_user_id", userId);
        }
        else
        {
            pJson.put("classid", classId);
        }
        if (bottomid != -1)
        {
            pJson.put("bottomid", bottomid);
        }
        MyHttpUtil.post("/homeandschool/getNotices.action", pJson, mHandler);
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
