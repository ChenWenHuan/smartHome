package com.smarthome.client2.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.utils.LogUtil;
import com.smarthome.client2.manager.AppManager;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.fb.model.UserInfo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FbCustomActivity extends Activity
{

    private ListView mListView;

    private Conversation mComversation;

    private Context mContext;

    private ReplyAdapter adapter;

    private Button sendBtn;

    private EditText inputEdit;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final int VIEW_TYPE_COUNT = 2;

    private final int VIEW_TYPE_USER = 0;

    private final int VIEW_TYPE_DEV = 1;

    private ImageView ivBack;

    private RelativeLayout rlContact;

    private TextView tvContactHint;

    private TextView tvContactInfo;

    private Spinner spContactType;

    // 分别对应邮箱、QQ、手机、其他
    private String[] types = new String[] { "", "", "", "" };

    private final String[] keys = new String[] { "email", "qq", "phone",
            "plain" };

    private final String[] keys2 = new String[] { "邮箱：", "QQ：", "手机：", "其他：" };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.umeng_fb_activity_custom);
        mContext = this;
        initView();
        mComversation = new FeedbackAgent(this).getDefaultConversation();
        adapter = new ReplyAdapter();
        mListView.setAdapter(adapter);
        sync();
        AppManager.getAppManager().addActivity(this);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    private void initView()
    {
        mListView = (ListView) findViewById(R.id.fb_reply_list);
        sendBtn = (Button) findViewById(R.id.fb_send_btn);
        inputEdit = (EditText) findViewById(R.id.fb_send_content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fb_reply_refresh);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        rlContact = (RelativeLayout) findViewById(R.id.rl_contact);
        tvContactHint = (TextView) findViewById(R.id.tv_contact_hint);
        tvContactInfo = (TextView) findViewById(R.id.tv_contact_info);
        spContactType = (Spinner) findViewById(R.id.sp_contact_type);

        getContactInfo();

        OnClickListener listener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.fb_send_btn:
                        String content = inputEdit.getText().toString();
                        if (!TextUtils.isEmpty(content))
                        {
                            if (spContactType.getVisibility() == View.VISIBLE)
                            {
                                setContactInfo(content);
                            }
                            else
                            {
                                inputEdit.getEditableText().clear();
                                // 将内容添加到会话列表
                                mComversation.addUserReply(content);
                                //刷新ListView
                                adapter.notifyDataSetChanged();
                                scrollToBottom();
                                // 数据同步
                                sync();
                            }
                        }
                        else
                        {
                            Toast.makeText(mContext,
                                    R.string.content_can_not_empty,
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.iv_back:
                        ivBack.setImageResource(R.drawable.back_in);
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(inputEdit.getWindowToken(),
                                0);
                        finish();
                        break;
                    case R.id.rl_contact:
                        if (spContactType.getVisibility() == View.GONE)
                        {
                            sendBtn.setText(R.string.umeng_fb_contact_save);
                            spContactType.setVisibility(View.VISIBLE);
                            int position = spContactType.getSelectedItemPosition();
                            inputEdit.setHint(R.string.umeng_fb_contact_info);
                            if (types[position] != null
                                    && !types[position].isEmpty())
                            {
                                inputEdit.setText(types[position]);
                            }
                        }
                        else
                        {
                            spContactType.setVisibility(View.GONE);
                            sendBtn.setText(R.string.umeng_fb_send);
                            inputEdit.setText(null);
                            inputEdit.setHint(R.string.umeng_fb_feedback);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        sendBtn.setOnClickListener(listener);
        ivBack.setOnClickListener(listener);
        rlContact.setOnClickListener(listener);

        spContactType.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                if (types[position] == null || types[position].isEmpty())
                {
                    inputEdit.setText(null);
                    inputEdit.setHint(R.string.umeng_fb_contact_info);
                }
                else
                {
                    inputEdit.setText(types[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        // 下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                sync();
            }
        });
    }

    // 数据同步
    private void sync()
    {
        mComversation.sync(new SyncListener()
        {
            @Override
            public void onSendUserReply(List<Reply> replyList)
            {
            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList)
            {
                // SwipeRefreshLayout停止刷新
                mSwipeRefreshLayout.setRefreshing(false);
                // 刷新ListView
                adapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom()
    {
        if (adapter.getCount() > 0)
        {
            mListView.smoothScrollToPosition(adapter.getCount());
        }
    }

    private void getContactInfo()
    {
        UserInfo userInfo = new FeedbackAgent(mContext).getUserInfo();
        if (userInfo == null)
        {
            LogUtil.i("contact", "user_null");
            tvContactHint.setText(R.string.add_contact_info);
            tvContactInfo.setText(R.string.leave_contact_info_hint);
            userInfo = new UserInfo();
        }
        else
        {
            Map<String, String> contact = userInfo.getContact();
            LogUtil.i("contact", contact.toString());
            if (contact == null || contact.isEmpty())
            {
                LogUtil.i("contact", "contact_null");
                tvContactHint.setText(R.string.add_contact_info);
                tvContactInfo.setText(R.string.leave_contact_info_hint);
                contact = new HashMap<String, String>();
            }
            else
            {
                for (int i = 0; i <= 3; i++)
                {
                    types[i] = contact.get(keys[i]);
                }
                showContactInfo();
            }
        }
    }

    private void showContactInfo()
    {
        String contactInfo = "";
        for (int i = 0; i <= 3; i++)
        {
            if (types[i] != null && !types[i].isEmpty())
            {
                contactInfo += (keys2[i] + types[i] + " ");
            }
        }
        if (contactInfo.isEmpty())
        {
            tvContactHint.setText(R.string.add_contact_info);
            tvContactInfo.setText(R.string.leave_contact_info_hint);
        }
        else
        {
            tvContactHint.setText(R.string.change_contact_info);
            tvContactInfo.setText(contactInfo);
        }
    }

    private void setContactInfo(String content)
    {
        final FeedbackAgent fb = new FeedbackAgent(mContext);
        UserInfo userInfo = fb.getUserInfo();
        if (userInfo == null)
        {
            userInfo = new UserInfo();
        }
        Map<String, String> contact = userInfo.getContact();
        if (contact == null || contact.isEmpty())
        {
            contact = new HashMap<String, String>();
        }
        int position = spContactType.getSelectedItemPosition();
        contact.put(keys[position], content);
        userInfo.setContact(contact);
        fb.setUserInfo(userInfo);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                fb.updateUserInfo();
            }
        }).start();
        types[position] = content;
        showContactInfo();
    }

    // adapter
    class ReplyAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return mComversation.getReplyList().size();
        }

        @Override
        public Object getItem(int arg0)
        {
            return mComversation.getReplyList().get(arg0);
        }

        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        @Override
        public int getViewTypeCount()
        {
            // 两种不同的Item布局
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position)
        {
            // 获取单条回复
            Reply reply = mComversation.getReplyList().get(position);
            if (Reply.TYPE_DEV_REPLY.equals(reply.type))
            {
                // 开发者回复Item布局
                return VIEW_TYPE_DEV;
            }
            else
            {
                // 用户反馈、回复Item布局
                return VIEW_TYPE_USER;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            // 获取单条回复
            Reply reply = mComversation.getReplyList().get(position);
            if (convertView == null)
            {
                // 根据Type的类型来加载不同的Item布局
                if (Reply.TYPE_DEV_REPLY.equals(reply.type))
                {
                    // 开发者的回复
                    convertView = LayoutInflater.from(mContext)
                            .inflate(R.layout.umeng_fb_custom_dev_reply, null);
                }
                else
                {
                    // 用户的反馈、回复
                    convertView = LayoutInflater.from(mContext)
                            .inflate(R.layout.umeng_fb_custom_user_reply, null);
                }
                // 创建ViewHolder并获取各种View
                holder = new ViewHolder();
                holder.replyContent = (TextView) convertView.findViewById(R.id.fb_reply_content);
                holder.replyProgressBar = (ProgressBar) convertView.findViewById(R.id.fb_reply_progressBar);
                holder.replyStateFailed = (ImageView) convertView.findViewById(R.id.fb_reply_state_failed);
                holder.replyData = (TextView) convertView.findViewById(R.id.fb_reply_date);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            // 以下是填充数据
            // 设置Reply的内容
            holder.replyContent.setText(reply.content);
            // 在App应用界面，对于开发者的Reply来讲status没有意义
            if (!Reply.TYPE_DEV_REPLY.equals(reply.type))
            {
                // 根据Reply的状态来设置replyStateFailed的状态
                if (Reply.STATUS_NOT_SENT.equals(reply.status))
                {
                    holder.replyStateFailed.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.replyStateFailed.setVisibility(View.GONE);
                }

                // 根据Reply的状态来设置replyProgressBar的状态
                if (Reply.STATUS_SENDING.equals(reply.status))
                {
                    holder.replyProgressBar.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.replyProgressBar.setVisibility(View.GONE);
                }
            }

            Date replyTime = new Date(reply.created_at);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holder.replyData.setText(sdf.format(replyTime));

            return convertView;
        }

        class ViewHolder
        {
            TextView replyContent;

            ProgressBar replyProgressBar;

            ImageView replyStateFailed;

            TextView replyData;
        }
    }

}
