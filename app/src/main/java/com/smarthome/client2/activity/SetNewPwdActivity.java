package com.smarthome.client2.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.smarthome.client2.util.StringUtils;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class SetNewPwdActivity extends Activity implements OnClickListener
{
    String mStrAccount = ""; //手机号码

    Button mBtn_submit; //提交按钮

    EditText mEditTextPwd; //第一次输入的密码

    EditText mEditTextRepeatPwd;//第二次输入的密码
    EditText mEditValidcode;

    ProgressDialog mProgressDlg;//网络提示

    private FrameLayout fl_header_title; //actionbar布局

    private CustomActionBar actionBar; //上方菜单栏

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ac_set_new_pwd);

        Intent intent = getIntent();
        mStrAccount = intent.getExtras().getString("account");

        mBtn_submit = (Button) findViewById(R.id.submit_ok);
        mBtn_submit.setOnClickListener(this);

        mEditTextPwd = (EditText) findViewById(R.id.new_pwd);
        mEditTextRepeatPwd = (EditText) findViewById(R.id.repeat_pwd);
        mEditValidcode = (EditText) findViewById(R.id.et_input_validcode);

        mProgressDlg = new ProgressDialog(SetNewPwdActivity.this);
        mProgressDlg.setCanceledOnTouchOutside(false);

        addTopBarToHead();
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View arg0)
    {
        switch (arg0.getId())
        {
            case R.id.submit_ok:
                modify_password();
                break;
            default:
                break;
        }

    }

    //处理设置新密码请求
    Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    mProgressDlg.setMessage(getResources().getString(R.string.modifying_pwd));
                    mProgressDlg.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    mProgressDlg.dismiss();
                    Intent intent = new Intent(SetNewPwdActivity.this,
                            LoginActivity_sm.class);
                    intent.putExtra(LoginActivity_sm.KEY_SET_NEWPSW, true);
                    startActivity(intent);
                    Toast.makeText(SetNewPwdActivity.this,
                            R.string.modify_pwd_success,
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case Constants.GET_DATA_FAIL:
                    mProgressDlg.dismiss();
                    Toast.makeText(SetNewPwdActivity.this,
                            (String)msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    mProgressDlg.dismiss();
                    Toast.makeText(SetNewPwdActivity.this,
                            R.string.unknown_error,
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    //发送新密码请求
    private void modify_password()
    {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mEditTextPwd.getText().toString()))
        {
            Toast.makeText(SetNewPwdActivity.this,
                    getResources().getString(R.string.empty_pwd),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditTextPwd.getText().toString().length() < 6
                || mEditTextPwd.getText().toString().length() > 16)
        {
            Toast.makeText(SetNewPwdActivity.this,
                    getResources().getString(R.string.wrong_pwd),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mEditTextPwd.getText()
                .toString()
                .equals(mEditTextRepeatPwd.getText().toString()))
        {
            Toast.makeText(SetNewPwdActivity.this,
                    getResources().getString(R.string.two_pwd_diff),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtils.isCN(mEditTextPwd.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.pwd_no_chinese),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            JSONObject obj = new JSONObject();
            obj.put("telNum", mStrAccount);
            obj.put("pwd", mEditTextPwd.getText().toString());
            obj.put("validCode",mEditValidcode.getText().toString());
            HttpUtil.postRequest(obj,
                    Constants.FIND_PASSWORD,
                    mHandler,
                    Constants.GET_DATA_SUCCESS,
                    Constants.GET_DATA_FAIL);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //添加上方的actionbar
    private void addTopBarToHead()
    {
        fl_header_title = (FrameLayout) findViewById(R.id.fl_head_set_new_pass_word);
        if (actionBar != null)
        {
            fl_header_title.removeView(actionBar);
        }

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
                getResources().getString(R.string.set_new_pwd),
                null,
                null);
        fl_header_title.addView(actionBar);
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
