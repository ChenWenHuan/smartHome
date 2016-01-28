package com.smarthome.client2.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.MainActivity;
import com.smarthome.client2.activity.UserMessageDetailActivity;
import com.smarthome.client2.bean.InviteMessageListBean;
import com.smarthome.client2.bean.SysMessageBean;
import com.smarthome.client2.bean.SysMessageListBean;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.parser.SysMessageJsonParser;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.RequestResult;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.view.RefreshableListView;
import com.smarthome.client2.view.RefreshableListView.CustomOnScrollListener;
import com.smarthome.client2.view.RefreshableListView.OnCustomItemClickListenner;
import com.smarthome.client2.view.RefreshableListView.OnRefreshListener;
import com.smarthome.client2.view.RefreshableListView.RemoveDirection;
import com.smarthome.client2.view.RefreshableListView.RemoveListener;

public class UserMessageFragment extends CommonFragment
{
    public static final int REQUEST_CODE_USER_MESSAGE_DETAIL = 1;

    public static final int RESULT_CODE_USER_MESSAGE_DETAIL = 2;

    private View containerView;

    private ProgressDialog dialog;

    private RefreshableListView list_view_user_message;

    private LinearLayout message_no_data;

    private View footView;

    private SysMessageListBean sysListBean = new SysMessageListBean();

    private InviteMessageListBean invListBean = new InviteMessageListBean();

    private MyUserMessageAdapter myUserMessageAdapter = null;

    /////////////////topbar//////////////////////////
    private FrameLayout fl_head_user_message;

    private CustomActionBar actionBar;

    private boolean isLoading = false;

    private int position_click = -1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        containerView = inflater.inflate(R.layout.user_message_fragment_view,
                null);
        Preferences.getInstance(getActivity()).setHasNewMessage(false);
        getActivity().sendBroadcast(new Intent(HomeFragement_V11.NEW_MESSAGE));
        addTopBarToHead();

        init();

        return containerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        myUserMessageAdapter = null;
        page = 0;
        sysListBean.list.clear();
        invListBean.list.clear();
        getDataFromNet();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        log.d("daitm----onActivityResult---" + data);
        getDataFromNet();
    }

    private void init()
    {
        message_no_data = (LinearLayout) containerView.findViewById(R.id.message_no_data);
        list_view_user_message = (RefreshableListView) containerView.findViewById(R.id.list_view_user_message);
        list_view_user_message.initialize();
        footView = LayoutInflater.from(getActivity())
                .inflate(R.layout.footview, null);

        list_view_user_message.setIsShowUpdateTime(true);

        list_view_user_message.setRemoveListener(new RemoveListener()
        {
            @Override
            public void removeItem(RemoveDirection direction, int position)
            {
                sysListBean.list.remove(position);
                if (null == myUserMessageAdapter)
                {
                    myUserMessageAdapter = new MyUserMessageAdapter();
                    list_view_user_message.setAdapter(myUserMessageAdapter);
                }
                else
                {
                    myUserMessageAdapter.notifyDataSetChanged();
                }
            }
        });

        list_view_user_message.setonRefreshListener(new OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                myUserMessageAdapter = null;
                page = 0;
                sysListBean.list.clear();
                getDataFromNet();
            }
        });

        list_view_user_message.SetOnCustomScrollListener(new CustomOnScrollListener()
        {
            @Override
            public void CustomonScrollStateChanged(AbsListView view,
                    int scrollState)
            {
                //              TLog.Log("zxl---friend fragment---onScroll 1--->"+friendListBean.getCurrent()+"--->"+friendListBean.getTotal()+"--->"+scrollState);
                if (sysListBean.getCurrent() < sysListBean.getTotal())
                {
                    if (scrollState == OnScrollListener.SCROLL_STATE_IDLE)
                    { // 判断滚动到底部
                      //                        TLog.Log("zxl---friend fragment---onScroll 2--->"+view.getLastVisiblePosition()+"--->"+view.getCount());
                        if (view.getLastVisiblePosition() >= (view.getCount() - 1))
                        {
                            page++;
                            getDataFromNet();
                        }
                    }
                }
            }

            @Override
            public void CustomonScroll(AbsListView arg0, int firstVisiableItem,
                    int arg2, int arg3)
            {

            }
        });

        list_view_user_message.SetOnCustomItemClickListenner(new OnCustomItemClickListenner()
        {
            @Override
            public void onCustomItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                position_click = position;
                SysMessageBean bean = sysListBean.list.get(position);
                if (bean.msgtype.equals("08") || bean.msgtype.equals("06"))
                {
                    Intent intent = new Intent(getActivity(),
                            UserMessageDetailActivity.class);
                    intent.putExtra(UserMessageDetailActivity.USER_MESSAGE_BEAN,
                            bean.beanToString());
                    startActivityForResult(intent,
                            REQUEST_CODE_USER_MESSAGE_DETAIL);
                }
            }
        });

        list_view_user_message.setOnItemLongClickListener(new OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                    int pos, long id)
            {
                initDialog(sysListBean.list.get((int) id).id);
                return true;
            }
        });
    }

    private void initDialog(final long id)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定删除该消息吗?");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                deleteMessage(id);
                dialog.cancel();
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private boolean isDeleteing = false;

    private void deleteMessage(final long id)
    {
        if (isDeleteing)
        {
            return;
        }
        isDeleteing = true;

        mHandler.sendEmptyMessage(DELETE_USER_MESSAGE_START);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                JSONObject obj = new JSONObject();

                try
                {
                    obj.put("id", id);
                    RequestResult result = new RequestResult();

                    HttpUtil.postRequest(obj,
                            Constants.DELETE_USER_MESSAGE_ACTION,
                            result,
                            getActivity());
                    TLog.Log("zxl---FriendDetailActivity---net--->"
                            + result.getCode() + "--->" + result.getResult());

                    Message message = mHandler.obtainMessage();
                    if (result.getCode() == Constants.SC_OK)
                    {
                        message.what = DELETE_USER_MESSAGE_SUCCESS;
                    }
                    else
                    {
                        message.what = DELETE_USER_MESSAGE_FAIL;
                        message.arg1 = result.getCode();
                    }
                    message.sendToTarget();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private int page = 0;

    private void getDataFromNet()
    {
        if (isLoading)
        {
            return;
        }
        isLoading = false;

        if (!HttpUtil.isNetworkAvailable(getActivity()))
        {
            Toast.makeText(getActivity(),
                    HttpUtil.responseHandler(getActivity(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mHandler.sendEmptyMessage(GET_USER_MESSAGE_START);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                JSONObject obj = new JSONObject();
                try
                {
                    obj.put("userId", Preferences.getInstance(getActivity())
                            .getUserID());
                    RequestResult result = new RequestResult();

                    HttpUtil.postRequest(obj,
                            Constants.GET_MESSAGE,
                            result,
                            getActivity());
                    TLog.Log("zxl---UserMessageFragment---net--->"
                            + result.getCode() + "--->" + result.getResult());

                    Message message = mHandler.obtainMessage();
                    if (Constants.SC_OK == result.getCode())
                    {
                        message.what = GET_USER_MESSAGE_SUCCESS;
                        message.obj = result.getResult();
                    }
                    else
                    {
                        message.what = GET_USER_MESSAGE_FAILED;
                        message.arg1 = result.getCode();
                    }
                    message.sendToTarget();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private static final int GET_USER_MESSAGE_START = 0;

    private static final int GET_USER_MESSAGE_SUCCESS = 1;

    private static final int GET_USER_MESSAGE_FAILED = 2;

    private static final int ACCEPT_USER_MESSAGE_START = 3;

    private static final int ACCEPT_USER_MESSAGE_SUCCESS = 4;

    private static final int ACCEPT_USER_MESSAGE_FAILED = 5;

    private static final int REJECT_USER_MESSAGE_START = 6;

    private static final int REJECT_USER_MESSAGE_SUCCESS = 7;

    private static final int REJECT_USER_MESSAGE_FAILED = 8;

    private static final int DELETE_USER_MESSAGE_START = 9;

    private static final int DELETE_USER_MESSAGE_SUCCESS = 10;

    private static final int DELETE_USER_MESSAGE_FAIL = 11;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case GET_USER_MESSAGE_START:
                    dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("正在查找消息...");
                    dialog.show();
                    break;
                case GET_USER_MESSAGE_SUCCESS:
                    list_view_user_message.onRefreshComplete();
                    isLoading = false;
                    dialog.setMessage("查找完成");
                    dialog.dismiss();

                    String s_result = (String) msg.obj;
                    SysMessageJsonParser sysParser = new SysMessageJsonParser();
                    sysListBean = sysParser.getResult(s_result);

                    if (sysListBean.list.size() > 0)
                    {
                        message_no_data.setVisibility(View.GONE);
                        list_view_user_message.setVisibility(View.VISIBLE);
                        if (null == myUserMessageAdapter)
                        {
                            myUserMessageAdapter = new MyUserMessageAdapter();
                            list_view_user_message.setAdapter(myUserMessageAdapter);
                        }
                        else
                        {
                            myUserMessageAdapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        message_no_data.setVisibility(View.VISIBLE);
                        list_view_user_message.setVisibility(View.GONE);
                        //                  myExceptionDialog.setMsg("没有消息");
                        //                  myExceptionDialog.showMyDialog();
                    }

                    break;
                case GET_USER_MESSAGE_FAILED:
                    list_view_user_message.onRefreshComplete();
                    isLoading = false;
                    dialog.setMessage("查找失败");
                    dialog.dismiss();
                    Toast.makeText(getActivity(),
                            HttpUtil.responseHandler(getActivity(), msg.arg1),
                            Toast.LENGTH_SHORT).show();
                    break;

                case ACCEPT_USER_MESSAGE_START:
                    dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("正在请求...");
                    dialog.show();
                    break;
                case ACCEPT_USER_MESSAGE_SUCCESS:
                    isLoading = false;
                    dialog.setMessage("请求完成");
                    dialog.dismiss();

                    //              userMessageListBean.list.get(msg.arg1).status = 1;

                    Toast.makeText(getActivity(), "请求完成", Toast.LENGTH_SHORT)
                            .show();
                    myUserMessageAdapter.notifyDataSetChanged();
                    break;
                case ACCEPT_USER_MESSAGE_FAILED:

                    isLoading = false;
                    dialog.setMessage("请求失败");
                    dialog.dismiss();
                    Toast.makeText(getActivity(),
                            HttpUtil.responseHandler(getActivity(), msg.arg1),
                            Toast.LENGTH_SHORT).show();
                    break;

                case REJECT_USER_MESSAGE_START:
                    dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("正在请求...");
                    dialog.show();
                    break;
                case REJECT_USER_MESSAGE_SUCCESS:
                    isLoading = false;
                    dialog.setMessage("请求完成");
                    dialog.dismiss();

                    //              userMessageListBean.list.get(msg.arg1).status = 1;

                    Toast.makeText(getActivity(), "请求完成", Toast.LENGTH_SHORT)
                            .show();
                    myUserMessageAdapter.notifyDataSetChanged();
                    break;
                case REJECT_USER_MESSAGE_FAILED:

                    isLoading = false;
                    dialog.setMessage("请求失败");
                    dialog.dismiss();
                    Toast.makeText(getActivity(),
                            HttpUtil.responseHandler(getActivity(), msg.arg1),
                            Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_USER_MESSAGE_START:
                    dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("正在删除...");
                    dialog.show();
                    break;
                case DELETE_USER_MESSAGE_SUCCESS:
                    isDeleteing = false;
                    dialog.setMessage("删除完成");
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "删除完成", Toast.LENGTH_SHORT)
                            .show();
                    getDataFromNet();
                    break;
                case DELETE_USER_MESSAGE_FAIL:
                    isDeleteing = false;
                    dialog.setMessage("查找失败");
                    dialog.dismiss();
                    Toast.makeText(getActivity(),
                            HttpUtil.responseHandler(getActivity(), msg.arg1),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void showSingleRow()
    {
        if (list_view_user_message.getFooterViewsCount() == 0
                && sysListBean.getCurrent() < sysListBean.getTotal())
        {
            list_view_user_message.addFooterView(footView);
        }

        if (myUserMessageAdapter == null)
        {
            myUserMessageAdapter = new MyUserMessageAdapter();
            list_view_user_message.setAdapter(myUserMessageAdapter);

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    list_view_user_message.requestFocusFromTouch();
                    list_view_user_message.setSelection(0);
                }
            }, 100);

        }
        else
        {
            myUserMessageAdapter.notifyDataSetChanged();
        }

        if (sysListBean.getCurrent() >= sysListBean.getTotal()
                && list_view_user_message.getFooterViewsCount() > 0)
        {
            list_view_user_message.removeFooterView(footView);
        }
    }

    private void addTopBarToHead()
    {
        fl_head_user_message = (FrameLayout) containerView.findViewById(R.id.fl_head_user_message);
        actionBar = TopBarUtils.createCustomActionBar(getActivity(),
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        final MainActivity ma = (MainActivity) getActivity();
                    }
                },
                "消息中心",
                0,
                null);
        fl_head_user_message.addView(actionBar);
    }


    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }

    class MyUserMessageAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return sysListBean.list.size();
        }

        @Override
        public SysMessageBean getItem(int position)
        {
            return sysListBean.list.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent)
        {

            Holder holder = null;

            if (null == convertView)
            {
                convertView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_list_view_user_message, null);
                holder = new Holder();
                holder.msg_item_img = (ImageView) convertView.findViewById(R.id.msg_item_img);
                holder.msg_item_title = (TextView) convertView.findViewById(R.id.msg_item_title);
                holder.tv_item_list_view_user_message = (TextView) convertView.findViewById(R.id.tv_item_list_view_user_message);
                holder.tv_item_list_view_user_message_time = (TextView) convertView.findViewById(R.id.tv_item_list_view_user_message_time);

                convertView.setTag(holder);
            }
            else
            {
                holder = (Holder) convertView.getTag();
            }

            //          消息类型
            //          1:好友请求
            //          2:系统消息
            //          3:挑战赛请求
            //          4：家庭成员请求
            //          1:未处理
            //          2:同意
            //          3:拒绝
            final SysMessageBean bean = sysListBean.list.get(position);

            String s_status = "";
            switch (bean.invitationResult)
            {
                case SysMessageBean.INVITE_RESULT_UNHANDLE:
                    s_status = "未处理";
                    break;
                case SysMessageBean.INVITE_RESULT_ACCEPT:
                    s_status = "已同意";
                    break;
                case SysMessageBean.INVITE_RESULT_REFUSE:
                    s_status = "已拒绝";
                    break;
            }

            String s_title = bean.title;
            String s_content = "";
            int s_img = R.drawable.msg_friend;
            switch (Integer.parseInt(bean.msgtype))
            {
                case SysMessageBean.TYPE_01:
                    s_content = bean.content;
                    break;
                case SysMessageBean.TYPE_02:
                    s_content = bean.content;
                    break;
                case SysMessageBean.TYPE_03:
                    s_content = bean.content;
                    break;
                case SysMessageBean.TYPE_04:
                    s_content = bean.content;
                    break;
                case SysMessageBean.TYPE_05:
                    s_content = bean.content;
                    s_img = R.drawable.msg_enter;
                    break;
                case SysMessageBean.TYPE_06:
                    if (bean.friendType == SysMessageBean.INVITE_TYPE_FAMILY)
                    {
                        s_content = bean.content + "\n结果：" + s_status;
                    }
                    else
                    {
                        s_content = bean.content + "\n结果：" + s_status;
                    }
                    s_img = R.drawable.msg_friend;
                    break;
                case SysMessageBean.TYPE_07:
                    s_content = bean.content;
                    break;
                case SysMessageBean.TYPE_08:
                    if (bean.friendType == SysMessageBean.INVITE_TYPE_FAMILY)
                    {
                        s_content = bean.content + "\n结果：" + s_status;
                    }
                    else
                    {
                        s_content = bean.content + "\n结果：" + s_status;
                    }
                    s_img = R.drawable.msg_friend;
                    break;
                case SysMessageBean.TYPE_09:
                    s_content = bean.content;
                    break;
                case SysMessageBean.TYPE_10:
                    s_content = bean.content;
                    s_img = R.drawable.msg_sos;
                    break;
                case SysMessageBean.TYPE_11:
                    s_content = bean.content;
                    s_img = R.drawable.msg_power;
                    break;
                case SysMessageBean.TYPE_12:
                    s_content = bean.content;
                    s_img = R.drawable.msg_reach;
                    break;
                case SysMessageBean.TYPE_13:
                    s_content = bean.content;
                    s_img = R.drawable.msg_schedule;
                    break;
                case SysMessageBean.TYPE_14:
                    s_content = bean.content;
                    s_img = R.drawable.msg_result;
                    break;
                case SysMessageBean.TYPE_15:
                    s_content = bean.content;
                    s_img = R.drawable.msg_attendance;
                    break;
            }

            holder.tv_item_list_view_user_message.setText(s_content);
            holder.msg_item_img.setImageResource(s_img);
            holder.msg_item_title.setText(s_title);

            Date d1 = new Date();
            long diff = d1.getTime() - bean.sendtime;
            long days = checktime(bean.sendtime);
            if (days == 0)
            {
                if (diff / 1000 / 60 <= 1)
                {
                    holder.tv_item_list_view_user_message_time.setText("刚刚");
                }
                else
                {
                    holder.tv_item_list_view_user_message_time.setText("今天 "
                            + timeShow(bean.sendtime));
                }
            }
            else if (days == 1)
            {
                holder.tv_item_list_view_user_message_time.setText("昨天 "
                        + timeShow(bean.sendtime));
            }
            else if (days <= 30)
            {
                holder.tv_item_list_view_user_message_time.setText("" + days
                        + "天前");
            }
            else if (days > 30 && days < 365)
            {
                holder.tv_item_list_view_user_message_time.setText(monthShow(days));
            }
            else
            {
                holder.tv_item_list_view_user_message_time.setText(yearShow(days));
            }

            return convertView;
        }
    }

    class Holder
    {
        public TextView tv_item_list_view_user_message,
                tv_item_list_view_user_message_time;

        public TextView msg_item_title;

        public ImageView msg_item_img;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    private String timeShow(long createTime)
    {
        Date d = new Date();
        d.setTime(createTime);
        return sdf.format(d);
    }

    private String monthShow(long days)
    {
        return days / 30 + "个月前";
    }

    private String yearShow(long days)
    {
        return days / 365 + "年前";
    }

    private long checktime(long time)
    {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        Calendar sendtime = Calendar.getInstance();
        sendtime.setTimeInMillis(time);
        sendtime.set(Calendar.HOUR_OF_DAY, 0);
        sendtime.set(Calendar.MINUTE, 0);
        sendtime.set(Calendar.SECOND, 0);
        long diff = now.getTimeInMillis() - sendtime.getTimeInMillis();
        long days = (diff / (1000 * 60 * 60 * 24));
        log.d("daitm---checktime---" + days);
        return days;
    }
}
