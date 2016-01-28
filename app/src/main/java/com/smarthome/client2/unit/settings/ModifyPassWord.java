package com.smarthome.client2.unit.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.MyExceptionDialog;
import com.smarthome.client2.util.StringUtils;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class ModifyPassWord extends Activity
{
    public static final String S_USER_BEAN = "S_USER_BEAN";

    private Context ctx;

    // topBar
    private FrameLayout fl_head_modify_pass_word;

    private CustomActionBar actionBar;

    private EditText et_orignal_pass_word;

    private EditText et_new_pass_word;

    private EditText et_new_pass_word_repeat;

    private Button btn_commit_modify_pass_word;

    private String s_orignal_pass_word;

    private String s_new_pass_word;

    private String s_new_pass_word_repeat;

    //  private UserBean userBean = new UserBean();

    //正在修改密码时的弹出框
    private ProgressDialog modifyPwdDialog;

    private MyExceptionDialog myExceptionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modify_pass_word);

        this.ctx = this;

        addTopActionBar();// 初始化页面顶部的topBar

        initView();// 初始化页面的控件

        iniViewClickListener();
        AppManager.getAppManager().addActivity(this);
    }

    private void addTopActionBar()
    {
        fl_head_modify_pass_word = (FrameLayout) findViewById(R.id.fl_head_modify_pass_word);

        actionBar = TopBarUtils.createCustomActionBar(ctx,
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                "修改密码",
                0,
                null);

        fl_head_modify_pass_word.addView(actionBar);
    }

    private void initView()
    {
        et_orignal_pass_word = (EditText) findViewById(R.id.et_orignal_pass_word);
        et_new_pass_word = (EditText) findViewById(R.id.et_new_pass_word);
        et_new_pass_word_repeat = (EditText) findViewById(R.id.et_new_pass_word_repeat);
        btn_commit_modify_pass_word = (Button) findViewById(R.id.btn_commit_modify_pass_word);

        modifyPwdDialog = new ProgressDialog(ModifyPassWord.this);
        modifyPwdDialog.setCanceledOnTouchOutside(false);

        myExceptionDialog = new MyExceptionDialog(ModifyPassWord.this);
        myExceptionDialog.setSubmitClick(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myExceptionDialog.dismissMyDialog();
            }
        });
    }

    private void iniViewClickListener()
    {
        btn_commit_modify_pass_word.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                s_orignal_pass_word = et_orignal_pass_word.getText().toString();
                s_new_pass_word = et_new_pass_word.getText().toString();
                s_new_pass_word_repeat = et_new_pass_word_repeat.getText()
                        .toString();

                if (TextUtils.isEmpty(s_orignal_pass_word))
                {
                    Toast.makeText(ctx, "原密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(s_new_pass_word))
                {
                    Toast.makeText(ctx, "新密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(s_new_pass_word_repeat))
                {
                    Toast.makeText(ctx, "请再次输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!s_new_pass_word.equals(s_new_pass_word_repeat))
                {
                    Toast.makeText(ctx, "两次输入的新密码不一样", Toast.LENGTH_SHORT)
                            .show();
                    et_new_pass_word.setText("");
                    et_new_pass_word_repeat.setText("");
                    return;
                }

                if (s_new_pass_word.length() > 16)
                {
                    Toast.makeText(ctx, "新密码长度不能超过16位", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (s_new_pass_word.length() < 6)
                {
                    Toast.makeText(ctx, "新密码长度不能小于6位", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (StringUtils.isCN(s_new_pass_word))
                {
                    Toast.makeText(ctx,
                            getString(R.string.pwd_no_chinese),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                doForModify();
            }
        });

    }

    //发起修改密码请求
    protected void doForModify()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //设置登录请求参数
                JSONObject obj = new JSONObject();
                try
                {
                    obj.put("newpwd", s_new_pass_word);
                    obj.put("pwd", s_orignal_pass_word);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                handler_modify.sendEmptyMessage(Constants.GET_DATA_START);
                HttpUtil.postRequest(obj,
                        Constants.MODIFY_PASSWORD,
                        handler_modify,
                        Constants.GET_DATA_SUCCESS,
                        Constants.GET_DATA_FAIL);
            }
        }).start();
    }

    //处理修改密码请求的handler
    private Handler handler_modify = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    modifyPwdDialog.setMessage("正在修改密码");
                    modifyPwdDialog.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    modifyPwdDialog.dismiss();
                    Toast.makeText(ModifyPassWord.this,
                            "修改密码成功",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case Constants.GET_DATA_FAIL:
                    modifyPwdDialog.dismiss();
                    et_orignal_pass_word.setText("");
                    et_new_pass_word.setText("");
                    et_new_pass_word_repeat.setText("");
                    if (msg.arg1 == 105)
                    {
                        Toast.makeText(ModifyPassWord.this,
                                "原密码输入错误",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Toast.makeText(ModifyPassWord.this,
                            "修改密码失败",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 201:
                    modifyPwdDialog.dismiss();
                    Toast.makeText(ctx,
                            getString(R.string.no_network),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    modifyPwdDialog.dismiss();
                    Toast.makeText(ModifyPassWord.this,
                            R.string.unknown_error,
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        if (myExceptionDialog != null)
        {
            myExceptionDialog.dismissMyDialog();
        }
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
