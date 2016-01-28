package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.adapter.ScoreAdapterTeacher;
import com.smarthome.client2.familySchool.model.ScoreTeacher;
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
 * 成绩信息页面（教师）
 */
public class ScoreActivityTeacher extends BaseActivity
{

    private ImageView ivBack;

    private AbPullListView lv;

    private TextView tvExamName;

    private TextView tvExamDate;

    private TextView tvScoreTitle;

    private TextView tvRankTitle;

    private ArrayList<ScoreTeacher> mList;

    private ScoreAdapterTeacher mAdapter;

    private String examId;

    /**
     * 成绩形式（分数or等级）
     */
    private boolean isScore;

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
                        ArrayList<ScoreTeacher> tmpList = ResultParsers.parserScoreTeacher(result,
                                isScore);
                        if (tmpList == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (tmpList.isEmpty())
                        {
                            lv.getFooterView().setState("没有成绩信息");
                        }
                        else
                        {
                            if (isScore)
                            {
                                ScoreTeacher.orderAsc(tmpList);
                            }
                            mList.addAll(tmpList);
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
        setContentView(R.layout.fs_activity_score_teacher);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        lv = (AbPullListView) findViewById(R.id.lv);
        tvExamDate = (TextView) findViewById(R.id.tv_exam_date);
        tvExamName = (TextView) findViewById(R.id.tv_exam_name);
        tvScoreTitle = (TextView) findViewById(R.id.tv_score_title);
        tvRankTitle = (TextView) findViewById(R.id.tv_rank_title);

        Intent tmpIntent = getIntent();
        examId = tmpIntent.getStringExtra("examId");
        String examName = tmpIntent.getStringExtra("examName");
        String subject = tmpIntent.getStringExtra("subject");
        String examDate = tmpIntent.getStringExtra("examDate");
        String type = tmpIntent.getStringExtra("type");
        if (type.equals("0"))
        {
            isScore = true;
            tvRankTitle.setVisibility(View.VISIBLE);
            tvScoreTitle.setText("分数");
        }
        else
        {
            isScore = false;
            tvRankTitle.setVisibility(View.GONE);
            tvScoreTitle.setText("等级");
        }
        tvExamName.setText(examName + "-" + subject);
        tvExamDate.setText(examDate);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));

        mList = new ArrayList<ScoreTeacher>();
        mAdapter = new ScoreAdapterTeacher(this, mList, isScore);
        lv.setAdapter(mAdapter);

        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                if (mList.isEmpty())
                {
                    getScore();
                }
                else
                {
                    lv.stopRefresh();
                    lv.getFooterView()
                            .setState(getString(R.string.already_all));
                }
            }

            @Override
            public void onLoadMore()
            {
                if (mList.isEmpty())
                {
                    getScore();
                }
                else
                {
                    lv.stopLoadMore();
                    lv.getFooterView()
                            .setState(getString(R.string.already_all));
                }
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

        lv.performRefresh();
    }

    /**
     * 获取成绩信息（全部加载）
     */
    private void getScore()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("examid", examId);
        MyHttpUtil.post("/homeandschool/detailScore.action", pJson, mHandler);
    }
}
