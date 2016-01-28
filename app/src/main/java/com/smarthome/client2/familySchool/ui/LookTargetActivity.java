package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.adapter.LookTargetAdapter;
import com.smarthome.client2.familySchool.model.MsgTarget;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;

/**
 * @author n003913 老师查看一条留言的对象
 */
public class LookTargetActivity extends BaseActivity
{

    private ImageView iv_back;// 返回按钮

    private AbPullListView lv;

    private LookTargetAdapter adapter;

    private ArrayList<MsgTarget> list;

    private String msgId;

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
                        ArrayList<MsgTarget> temp = ResultParsers.parserLookTargets(result);
                        if (temp == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (temp.isEmpty())
                        {
                            lv.getFooterView()
                                    .setState(getString(R.string.no_msg_target));
                        }
                        else
                        {
                            list.addAll(temp);
                            adapter.notifyDataSetChanged();
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
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_activity_look_target);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv = (AbPullListView) findViewById(R.id.lv);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));

        list = new ArrayList<MsgTarget>();
        adapter = new LookTargetAdapter(list, this);
        lv.setAdapter(adapter);
        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                if (list.isEmpty())
                {
                    getTargets();
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
                if (list.isEmpty())
                {
                    getTargets();
                }
                else
                {
                    lv.stopLoadMore();
                    lv.getFooterView()
                            .setState(getString(R.string.already_all));
                }
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

        msgId = getIntent().getStringExtra("msgId");

        lv.performRefresh();
    }

    private void getTargets()
    {
        HttpJson params = new HttpJson();
        params.put("leave_msg_id", msgId);
        MyHttpUtil.post("/homeandschool/getLeaveTarget.action",
                params,
                mHandler);
    }
}
