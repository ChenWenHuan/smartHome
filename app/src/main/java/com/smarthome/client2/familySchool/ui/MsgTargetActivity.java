package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.adapter.MsgTargetAdapter;
import com.smarthome.client2.familySchool.model.MsgTarget;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;

/**
 * @author n003913 留言对象选择
 */
public class MsgTargetActivity extends BaseActivity
{

    private ImageView iv_back;// 返回按钮

    private TextView tv_all;// 全选按钮

    private AbPullListView lv;

    private TextView tv_input;// 开始输入按钮

    private MsgTargetAdapter adapter;

    private ArrayList<MsgTarget> list;

    private boolean isAll = false;

    private String classId;

    private String ids;

    private String names;

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
                        ArrayList<MsgTarget> temp = ResultParsers.parserTargets(result);
                        if (temp == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (temp.isEmpty())
                        {
                            lv.getFooterView().setState("没有留言对象");
                        }
                        else
                        {
                            tv_input.setClickable(true);
                            tv_all.setClickable(true);
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
        setContentView(R.layout.fs_activity_msg_target);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_all = (TextView) findViewById(R.id.tv_all);
        lv = (AbPullListView) findViewById(R.id.lv);
        tv_input = (TextView) findViewById(R.id.tv_input);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));

        list = new ArrayList<MsgTarget>();
        adapter = new MsgTargetAdapter(list, this);
        lv.setAdapter(adapter);
        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                list.clear();
                getTargets();
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
                    lv.getFooterView().setState("已是全部");
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
                        getSlecetedIds();
                        if (!ids.isEmpty())
                        {
                            showCancelDialog();
                        }
                        else
                        {
                            finish();
                        }
                        break;
                    case R.id.tv_all:
                        isAll = !isAll;
                        if (isAll)
                        {
                            tv_all.setText("全不选");
                        }
                        else
                        {
                            tv_all.setText("全选");
                        }
                        for (MsgTarget target : list)
                        {
                            target.setChecked(isAll);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.tv_input:
                        getSlecetedIds();
                        if (ids.isEmpty())
                        {
                            showToast("请至少选择一名留言对象");
                        }
                        else
                        {
                            Intent mIntent = new Intent(MsgTargetActivity.this,
                                    WriteMsgActivity.class);
                            mIntent.putExtra("ids", ids);
                            mIntent.putExtra("names", names);
                            mIntent.putExtra("classId", classId);
                            mIntent.putExtra(FsConstants.TYPE_ADD_FLAG,
                                    FsConstants.TYPE_ADD_MSG);
                            startActivity(mIntent);
                            finish();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        iv_back.setOnClickListener(listener);
        tv_all.setOnClickListener(listener);
        tv_input.setOnClickListener(listener);
        tv_input.setClickable(false);
        tv_all.setClickable(false);

        classId = getIntent().getStringExtra("classId");

        lv.performRefresh();
    }

    private void getSlecetedIds()
    {
        ids = "";
        names = "";
        ArrayList<MsgTarget> selectedTargets = adapter.getSelectedTargets();
        if (selectedTargets.size() == 0)
        {
            return;
        }
        JSONArray jArray = new JSONArray();
        JSONObject jObject = null;
        for (MsgTarget msgTarget : selectedTargets)
        {
            ids += "," + msgTarget.getId();
            jObject = new JSONObject();
            try
            {
                jObject.put("name", msgTarget.getName());
                jObject.put("userId", msgTarget.getUserId());
                jArray.put(jObject);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        ids = ids.substring(1);
        names = jArray.toString();
    }

    private void getTargets()
    {
        HttpJson params = new HttpJson();
        params.put("classid", classId);
        MyHttpUtil.post("/homeandschool/getStudentInfoByClass.action",
                params,
                mHandler);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        getSlecetedIds();
        if (!ids.isEmpty())
        {
            showCancelDialog();
        }
        else
        {
            finish();
        }
    }
}
