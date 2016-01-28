package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.adapter.LeaveMsgAdapterTeacher;
import com.smarthome.client2.familySchool.adapter.MessageReplyAdapter.TeacherReplyListener;
import com.smarthome.client2.familySchool.model.LeaveMessage;
import com.smarthome.client2.familySchool.model.MessageReply;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.LogUtil;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author n003913 留言界面（教师）
 * 现有内容可能有变化（新的回复），所以该页面下拉刷新是重新加载，而非根据topId差量更新
 */
public class LeaveMsgActivityTeacher extends BaseActivity
{

    private ImageView iv_back;

    private AbPullListView lv;

    private TextView tv_add;

    private ArrayList<LeaveMessage> list;

    private LeaveMsgAdapterTeacher mAdapter;

    private long bottomid = -1;

    private String teacherId;

    private String classId;

    private boolean isAll;

    private LinearLayout llReply;

    /**
     * 回复输入框
     */
    private EditText etReply;

    /**
     * 回复发送按钮
     */
    private TextView tvReply;

    private TeacherReplyListener replyListener;

    private String repliedContent;

    private MessageReply clickedReply;

    private LeaveMessage clickedmsg;

    /**
     * 记录llReply在最底部，即从隐藏到显示时在父布局中的bottom位置
     */
    private int replyBottom;

    private float downY;

    private float upY;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
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
                        ArrayList<LeaveMessage> temp = ResultParsers.parserMessage(result,
                                null);
                        if (temp == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (temp.isEmpty())
                        {
                            if (list.isEmpty())
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.no_msg));
                            }
                            else
                            {
                                lv.getFooterView()
                                        .setState(getString(R.string.already_all));
                                isAll = true;
                            }
                        }
                        else
                        {
                            if (bottomid == -1)
                            {
                                list.clear();
                                isAll = false;
                            }
                            list.addAll(temp);
                            bottomid = Long.parseLong(list.get(list.size() - 1)
                                    .getId());
                            if (temp.size() < 10)
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

    private Handler replyHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    tvReply.setEnabled(false);
                    showProgressDialog(LeaveMsgActivityTeacher.this.getString(R.string.is_submitting));
                    break;
                case FsConstants.HTTP_SUCCESS:
                    String result = (String) msg.obj;
                    String code = ResultParsers.getCode(result);
                    if (code.equals("200"))
                    {
                        showToast("回复成功");
                        // 隐藏软键盘
                        InputMethodManager imm = (InputMethodManager) LeaveMsgActivityTeacher.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etReply.getWindowToken(), 0);
                        clickedmsg.getReplies().add(new MessageReply(null,
                                clickedReply.getMessage_id(),
                                clickedReply.getRev_user_id(),
                                clickedReply.getReply_user_id(),
                                repliedContent, null,
                                clickedReply.getRev_username(),
                                clickedReply.getReply_username()));
                        mAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        showToast(R.string.server_offline);
                    }
                    break;
                case FsConstants.HTTP_FAILURE:
                    showToast(R.string.no_network);
                    break;
                case FsConstants.HTTP_FINISH:
                    tvReply.setEnabled(true);
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
        setContentView(R.layout.fs_activity_leave_message_teacher);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv = (AbPullListView) findViewById(R.id.lv_leave_message);
        tv_add = (TextView) findViewById(R.id.tv_addMsg);
        llReply = (LinearLayout) findViewById(R.id.ll_reply);
        etReply = (EditText) findViewById(R.id.et_leave_message);
        tvReply = (TextView) findViewById(R.id.tv_submit);

        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));
        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
                .getDrawable(R.drawable.fs_pull_progress));

        initReplyListener();

        list = new ArrayList<LeaveMessage>();
        mAdapter = new LeaveMsgAdapterTeacher(this, list, replyListener);
        lv.setAdapter(mAdapter);
        lv.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                bottomid = -1;
                refreshMessage();
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
                refreshMessage();
            }
        });

        lv.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (llReply.getVisibility() == View.VISIBLE)
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        downY = event.getY();
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        upY = event.getY();
                    }
                    // 判定用户未滚动lv，隐藏回复视图
                    if (Math.abs(upY - downY) <= 10)
                    {
                        InputMethodManager imm = (InputMethodManager) LeaveMsgActivityTeacher.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etReply.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        // 按下back实体键后，软键盘消失，希望回复视图一同消失，所以添加此监听
        llReply.addOnLayoutChangeListener(new OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                    int bottom, int oldLeft, int oldTop, int oldRight,
                    int oldBottom)
            {
                LogUtil.i("bottom", "old=" + oldBottom + "new=" + bottom);
                if (llReply.getVisibility() == View.VISIBLE
                        && bottom == replyBottom && replyBottom != 0)
                {
                    mHandler.post(new Runnable()
                    {
                        public void run()
                        {
                            llReply.setVisibility(View.GONE);
                            replyBottom = 0;
                        }
                    });
                }
                if (replyBottom == 0)
                {
                    replyBottom = bottom;
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
                        if (llReply.getVisibility() == View.VISIBLE)
                        {
                            InputMethodManager imm = (InputMethodManager) LeaveMsgActivityTeacher.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(etReply.getWindowToken(),
                                    0);
                        }
                        else
                        {
                            iv_back.setImageResource(R.drawable.back_in);
                            finish();
                        }
                        break;
                    case R.id.tv_submit:
                        replyJudge();
                        break;
                    case R.id.tv_addMsg:
                        Intent mIntent = new Intent(
                                LeaveMsgActivityTeacher.this,
                                MsgTargetActivity.class);
                        mIntent.putExtra("classId", classId);
                        startActivity(mIntent);
                        break;
                    default:
                        break;
                }

            }
        };
        iv_back.setOnClickListener(listener);
        tvReply.setOnClickListener(listener);
        tv_add.setOnClickListener(listener);

        classId = getIntent().getStringExtra("classId");
        teacherId = Preferences.getInstance(this).getTeacherId();
        lv.performRefresh();
    }

    /**
     *  这可能是在发布留言Activity销毁后,因为可能发布了新留言，所以需要刷新。
     */
    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (Preferences.getInstance(this).getPublishNew())
        {
            Preferences.getInstance(this).setPublishNew(false);
            bottomid = -1;
            lv.performRefresh();
        }
    }

    /**
     * 刷新留言
     */
    private void refreshMessage()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("teacherId", teacherId);
        if (bottomid != -1)
        {
            pJson.put("bottomid", bottomid);
        }
        pJson.put("classId", classId);
        pJson.put("loadsize", 10);
        MyHttpUtil.post("/homeandschool/seeLeaveMsg.action", pJson, mHandler);
    }

    private void initReplyListener()
    {
        replyListener = new TeacherReplyListener()
        {
            @Override
            public void onReply(LeaveMessage msg, MessageReply reply)
            {
                clickedmsg = msg;
                clickedReply = reply;
                llReply.setVisibility(View.VISIBLE);
                etReply.setText(null);
                etReply.setHint("回复" + clickedReply.getReply_username()
                        + "（不能超过500字）");
                etReply.requestFocus();
                InputMethodManager imm = (InputMethodManager) LeaveMsgActivityTeacher.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        };
    }

    private void replyJudge()
    {
        repliedContent = etReply.getText().toString();
        if (repliedContent != null && !repliedContent.isEmpty())
        {
            HttpJson params = new HttpJson();
            params.put("msgid", clickedReply.getMessage_id());
            params.put("revUserId", clickedReply.getReply_user_id());
            params.put("replyUserId", clickedReply.getRev_user_id());
            params.put("revUserName", clickedReply.getReply_username());
            params.put("replyUserName", clickedReply.getRev_username());
            params.put("content", repliedContent);
            MyHttpUtil.post("/homeandschool/replyLeaveMsg.action",
                    params,
                    replyHandler);
        }
        else
        {
            showToast(R.string.content_can_not_empty);
        }
    }

}
