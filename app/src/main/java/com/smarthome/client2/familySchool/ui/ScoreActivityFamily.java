package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.adapter.ScoreAdapterFamily;
import com.smarthome.client2.familySchool.model.ScoreFamily;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;

/**
 * @author n003913
 * 成绩信息（家长）
 */
public class ScoreActivityFamily extends BaseActivity
{
    private ImageView ivBack;

    private AbPullListView lv;

    private ArrayList<ScoreFamily> mList;

    private ScoreAdapterFamily mAdapter;

    private long bottomId = -1;

    private long topId = -1;

    private String studentId;

    private String mUserId;

    private boolean isAll = false;

    private boolean getNew = true;

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
                        ArrayList<ScoreFamily> tmpList = ResultParsers.parserScoreFamily(result);
                        if (tmpList == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (tmpList.isEmpty())
                        {
                            if (mList.isEmpty())
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.no_score_info));
                            }
                            else
                            {
                                if (getNew)
                                {
                                    showToast(R.string.no_new_score_info);
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
                            if (getNew)
                            {
                                mList.addAll(0, tmpList);
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
                            }
                            topId = mList.get(0).getId();
                            bottomId = mList.get(mList.size() - 1).getId();
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
                    mAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.fs_activity_score_family);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        lv = (AbPullListView) findViewById(R.id.lv);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));

        mList = new ArrayList<ScoreFamily>();
        mAdapter = new ScoreAdapterFamily(this, mList);
        lv.setAdapter(mAdapter);

        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                getNew = true;
                getScore();
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
                getNew = false;
                getScore();
            }
        });

        ivBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ivBack.setImageResource(R.drawable.back_in);
                finish();
            }
        });

        studentId = getIntent().getStringExtra("studentId");
        mUserId = getIntent().getStringExtra("userId");

        lv.performRefresh();
    }

    /**
     * 获取成绩信息
     */
    private void getScore()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("studentid", studentId);
        pJson.put("userid", mUserId);
        pJson.put("loadsize", 10);
        if (getNew)
        {
            if (topId != -1)
            {
                pJson.put("topid", topId);
            }
        }
        else
        {
            if (bottomId != -1)
            {
                pJson.put("bottomid", bottomId);
            }
        }
        MyHttpUtil.post("/homeandschool/queryScore.action", pJson, mHandler);
    }
}
