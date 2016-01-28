package com.smarthome.client2.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.bean.SysMessageBean;
import com.smarthome.client2.bean.UserMessageBean;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.fragment.UserMessageFragment;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.BitmapUtil;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.MyExceptionDialog;
import com.smarthome.client2.util.RequestResult;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class UserMessageDetailActivity extends Activity
{
    public static final String USER_MESSAGE_BEAN = "USER_MESSAGE_BEAN";

    public static final String HAND_CODE_USER_MESSAGE_DETAIL = "HAND_CODE_USER_MESSAGE_DETAIL";

    public static final int DELETE_USER_MESSAGE_DETAIL = 1;

    public static final int ACCEPTE_USER_MESSAGE_DETAIL = 2;

    public static final int REJECT_USER_MESSAGE_DETAIL = 3;

    private Context ctx;

    // ///////////////topbar//////////////////////////
    private FrameLayout fl_head_user_message_detail;

    private CustomActionBar actionBar;

    private TextView tv_name_user_message_detail,
            tv_gender_user_message_detail, tv_in_time_user_message_detail;

    private TextView tv_tittle_user_message_detail,
            tv_result_user_message_detail;

    private ImageView img_user_message_detail;

    private LinearLayout ll_hand_message_detail_data;

    private Button btn_accept_user_message_detail,
            btn_reject_user_message_detail;

    private boolean isDeleteing = false;

    private ProgressDialog dialog;

    private SysMessageBean message_bean = new SysMessageBean();

    private boolean isHandMessageIng = false;

    private Bitmap headPhoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_message_detail_view);

        this.ctx = this;

        String s_user_message_bean = getIntent().getStringExtra(USER_MESSAGE_BEAN);
        message_bean.stringToBean(s_user_message_bean);

        addTopBarToHead();

        img_user_message_detail = (ImageView) findViewById(R.id.img_user_message_detail);
        tv_name_user_message_detail = (TextView) findViewById(R.id.tv_name_user_message_detail);
        tv_gender_user_message_detail = (TextView) findViewById(R.id.tv_gender_user_message_detail);
        tv_in_time_user_message_detail = (TextView) findViewById(R.id.tv_in_time_user_message_detail);

        tv_tittle_user_message_detail = (TextView) findViewById(R.id.tv_tittle_user_message_detail);
        tv_result_user_message_detail = (TextView) findViewById(R.id.tv_result_user_message_detail);

        ll_hand_message_detail_data = (LinearLayout) findViewById(R.id.ll_hand_message_detail_data);
        btn_accept_user_message_detail = (Button) findViewById(R.id.btn_accept_user_message_detail);
        btn_reject_user_message_detail = (Button) findViewById(R.id.btn_reject_user_message_detail);

        tv_name_user_message_detail.setText(!TextUtils.isEmpty(message_bean.sendername) ? message_bean.sendername
                : "陌生人");
        // tv_gender_user_message_detail.setText(((message_bean.sex ==
        // 1)?"男":"女"));
        tv_tittle_user_message_detail.setText(message_bean.title);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        tv_in_time_user_message_detail.setText(sf.format(new Date(
                message_bean.sendtime)));

        switch (message_bean.invitationResult)
        {
            case SysMessageBean.INVITE_RESULT_UNHANDLE:
                tv_result_user_message_detail.setVisibility(View.GONE);
                break;
            case SysMessageBean.INVITE_RESULT_ACCEPT:
                ll_hand_message_detail_data.setVisibility(View.GONE);
                break;
            case SysMessageBean.INVITE_RESULT_REFUSE:
                ll_hand_message_detail_data.setVisibility(View.GONE);
                break;
        }

        if (SysMessageBean.INVITE_RESULT_ACCEPT == message_bean.invitationResult)
        {
            switch (message_bean.friendType)
            {
                case SysMessageBean.INVITE_TYPE_FRIEND:
                    tv_result_user_message_detail.setText("已同意");
                    break;
                case SysMessageBean.INVITE_TYPE_FAMILY:
                    tv_result_user_message_detail.setText("已同意");
                    break;
            }
        }
        else if (SysMessageBean.INVITE_RESULT_REFUSE == message_bean.invitationResult)
        {
            tv_result_user_message_detail.setText("已拒绝");
        }

        btn_accept_user_message_detail.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                accept_friend(message_bean);
            }
        });

        btn_reject_user_message_detail.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reject_friend(message_bean);
            }
        });

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    headPhoto = getBitmap(message_bean.headpicpath
                            + message_bean.headpicname);
                    if (headPhoto != null)
                    {
                        mHandler.post(runnableUi);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        AppManager.getAppManager().addActivity(this);
    }

    private Runnable runnableUi = new Runnable()
    {
        @Override
        public void run()
        {
            //更新界面
            headPhoto = BitmapUtil.getRoundedCornerBitmap(headPhoto);
            img_user_message_detail.setImageBitmap(headPhoto);
        }

    };

    public static Bitmap getBitmap(String path) throws IOException
    {

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200)
        {
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }

    private void deleteMessage()
    {
        if (isDeleteing)
        {
            return;
        }
        isDeleteing = true;

        if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mHandler.sendEmptyMessage(DELETE_USER_MESSAGE_START);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                JSONObject obj = new JSONObject();

                try
                {
                    obj.put("id", message_bean.id);
                    RequestResult result = new RequestResult();

                    HttpUtil.postRequest(obj,
                            Constants.DELETE_USER_MESSAGE_ACTION,
                            result,
                            ctx);
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

    private void accept_friend(final SysMessageBean bean)
    {

        if (isHandMessageIng)
        {
            return;
        }
        isHandMessageIng = true;

        mHandler.sendEmptyMessage(ACCEPT_USER_MESSAGE_START);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // id
                // fromUser
                // toUser
                // type
                // result 1同意，2拒绝
                JSONObject obj = new JSONObject();
                try
                {
                    obj.put("invitationId", bean.invitationId);
                    obj.put("result", "1");
                    obj.put("msgId", message_bean.id);
                    
                    

                    RequestResult result = new RequestResult();

                    HttpUtil.postRequest(obj,
                            Constants.AUDIT_USER_MESSAGE_V11,
                            result,
                            UserMessageDetailActivity.this);
                    TLog.Log("zxl---usermessagefragment---accept--->"
                            + result.getCode());

                    Message message = mHandler.obtainMessage();
                    if (Constants.SC_OK == result.getCode())
                    {
                        message.what = ACCEPT_USER_MESSAGE_SUCCESS;
                        bean.status = UserMessageBean.STATUS_ACCEPT_MESSAGE;
                    }
                    else
                    {
                        message.what = ACCEPT_USER_MESSAGE_FAILED;
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

    private void reject_friend(final SysMessageBean bean)
    {
        if (isHandMessageIng)
        {
            return;
        }
        isHandMessageIng = true;

        mHandler.sendEmptyMessage(REJECT_USER_MESSAGE_START);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // id
                // fromUser
                // toUser
                // type
                // result 1同意，2拒绝
                JSONObject obj = new JSONObject();
                try
                {
                    obj.put("invitationId", bean.invitationId);
                    obj.put("result", "2");
                    obj.put("msgId", message_bean.id);

                    RequestResult result = new RequestResult();

                    HttpUtil.postRequest(obj,
                            Constants.AUDIT_USER_MESSAGE_V11,
                            result,
                            UserMessageDetailActivity.this);
                    TLog.Log("zxl---usermessagefragment---accept--->"
                            + result.getCode());

                    Message message = mHandler.obtainMessage();
                    if (Constants.SC_OK == result.getCode())
                    {
                        message.what = REJECT_USER_MESSAGE_SUCCESS;
                        bean.status = UserMessageBean.STATUS_REJECT_MESSAGE;
                    }
                    else
                    {
                        message.what = REJECT_USER_MESSAGE_FAILED;
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

    private static final int DELETE_USER_MESSAGE_START = 0;

    private static final int DELETE_USER_MESSAGE_SUCCESS = 1;

    private static final int DELETE_USER_MESSAGE_FAIL = 2;

    private static final int ACCEPT_USER_MESSAGE_START = 3;

    private static final int ACCEPT_USER_MESSAGE_SUCCESS = 4;

    private static final int ACCEPT_USER_MESSAGE_FAILED = 5;

    private static final int REJECT_USER_MESSAGE_START = 6;

    private static final int REJECT_USER_MESSAGE_SUCCESS = 7;

    private static final int REJECT_USER_MESSAGE_FAILED = 8;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case DELETE_USER_MESSAGE_START:
                    dialog = new ProgressDialog(ctx);
                    dialog.setMessage("正在删除...");
                    dialog.show();
                    break;
                case DELETE_USER_MESSAGE_SUCCESS:
                    isDeleteing = false;
                    dialog.setMessage("删除完成");
                    dialog.dismiss();

                    Toast.makeText(ctx, "删除完成", Toast.LENGTH_SHORT).show();

                    Intent data_delte = new Intent();
                    data_delte.putExtra(HAND_CODE_USER_MESSAGE_DETAIL,
                            DELETE_USER_MESSAGE_DETAIL);
                    setResult(UserMessageFragment.RESULT_CODE_USER_MESSAGE_DETAIL,
                            data_delte);
                    finish();

                    break;
                case DELETE_USER_MESSAGE_FAIL:
                    isDeleteing = false;
                    dialog.setMessage("删除失败");
                    dialog.dismiss();

                    Toast.makeText(ctx,
                            HttpUtil.responseHandler(ctx, msg.arg1),
                            Toast.LENGTH_SHORT).show();
                    break;
                case ACCEPT_USER_MESSAGE_START:
                    dialog = new ProgressDialog(ctx);
                    dialog.setMessage("正在请求...");
                    dialog.show();
                    break;
                case ACCEPT_USER_MESSAGE_SUCCESS:
                    isHandMessageIng = false;
                    dialog.setMessage("请求完成");
                    dialog.dismiss();
                    SmartHomeApplication.getInstance().getMainActivity().setRefreshHomeFragment(true);
                    ll_hand_message_detail_data.setVisibility(View.GONE);
                    tv_result_user_message_detail.setVisibility(View.VISIBLE);

                    switch (message_bean.invitationResult)
                    {
                        case SysMessageBean.INVITE_TYPE_FRIEND:
                            tv_result_user_message_detail.setText("已同意");
                            break;
                        case SysMessageBean.INVITE_TYPE_FAMILY:
                            tv_result_user_message_detail.setText("已同意");
                            break;
                    }

                    Toast.makeText(ctx, "请求完成", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra(HAND_CODE_USER_MESSAGE_DETAIL,
                            ACCEPTE_USER_MESSAGE_DETAIL);
                    setResult(UserMessageFragment.RESULT_CODE_USER_MESSAGE_DETAIL,
                            data);

                    break;
                case ACCEPT_USER_MESSAGE_FAILED:

                    isHandMessageIng = false;
                    dialog.setMessage("请求失败");
                    dialog.dismiss();

                    Toast.makeText(ctx,
                            HttpUtil.responseHandler(ctx, msg.arg1),
                            Toast.LENGTH_SHORT).show();

                    break;

                case REJECT_USER_MESSAGE_START:
                    dialog = new ProgressDialog(ctx);
                    dialog.setMessage("正在请求...");
                    dialog.show();
                    break;
                case REJECT_USER_MESSAGE_SUCCESS:
                    isHandMessageIng = false;
                    dialog.setMessage("请求完成");
                    dialog.dismiss();

                    ll_hand_message_detail_data.setVisibility(View.GONE);
                    tv_result_user_message_detail.setVisibility(View.VISIBLE);
                    tv_result_user_message_detail.setText("已拒绝");

                    Toast.makeText(ctx, "请求完成", Toast.LENGTH_SHORT).show();

                    Intent data2 = new Intent();
                    data2.putExtra(HAND_CODE_USER_MESSAGE_DETAIL,
                            REJECT_USER_MESSAGE_DETAIL);
                    setResult(UserMessageFragment.RESULT_CODE_USER_MESSAGE_DETAIL,
                            data2);

                    break;
                case REJECT_USER_MESSAGE_FAILED:

                    isHandMessageIng = false;
                    dialog.setMessage("请求失败");
                    dialog.dismiss();

                    Toast.makeText(ctx,
                            HttpUtil.responseHandler(ctx, msg.arg1),
                            Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    private void addTopBarToHead()
    {
        fl_head_user_message_detail = (FrameLayout) findViewById(R.id.fl_head_user_message_detail);
        actionBar = TopBarUtils.createCustomActionBar(this,
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                "消息详情",
                R.drawable.btn_delete_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        initDialog();
                    }
                });
        fl_head_user_message_detail.addView(actionBar);
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    private void initDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除该消息吗?");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                deleteMessage();
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

    /* (non-Javadoc)
    * @see android.app.Activity#onResume()
    */
    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
