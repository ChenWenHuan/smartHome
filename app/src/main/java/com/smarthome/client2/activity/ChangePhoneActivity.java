package com.smarthome.client2.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.GloableData;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.manager.VersionManager;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.RequestResult;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.widget.MemberDao;
import com.umeng.analytics.MobclickAgent;

public class ChangePhoneActivity extends Activity implements OnClickListener
{
    private static final int CHANGE_BUTTON_SEND_MESSAGE = 100;

    public static final int COUNT_GET_INDENTIFY_CODE = 120;

    private boolean isInterrupt = false;

    private int count_get_identify_code = COUNT_GET_INDENTIFY_CODE;

    private FrameLayout fl_header_title; //actionbar布局

    private CustomActionBar actionBar; //上方菜单栏

    private LinearLayout mInput_validecode_LinearLayout; //输入验证码并验证的布局

    private Button mBtn_Next; //验证按钮

    private Button mGetValidCode; //获取验证码按钮

    private EditText mEditText_userId; //手机号输入框

    private EditText mEditValidcode; //验证码输入框

    private String mStrValidcode; //获取验证码接口返回的验证码

    private Resources mResource;

    private String mPhone = "";

    private ProgressDialog mGetValidcodeDlg;

    private int userType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget_password);

        initView();

        addTopBarToHead();
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    private Handler mGetValidcode_handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    mGetValidcodeDlg.setMessage(mResource.getString(R.string.validecode_getting));
                    mGetValidcodeDlg.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    mGetValidcodeDlg.dismiss();
                    try
                    {
                        JSONObject json = new JSONObject(msg.obj.toString());
                        mStrValidcode = json.getString("data");
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    if (userType != Constants.TYPE_IS_STUDENT)
                    {
                        Message message = mGetValidcode_handler.obtainMessage();
                        message.what = CHANGE_BUTTON_SEND_MESSAGE;
                        message.arg1 = count_get_identify_code;
                        mGetValidcode_handler.sendMessage(message);

                        mEditText_userId.setEnabled(false);
                        mInput_validecode_LinearLayout.setVisibility(View.VISIBLE);
                        mBtn_Next.setText(getString(R.string.edit_confirm));

                        mEditValidcode.setFocusable(true);
                        mEditValidcode.setFocusableInTouchMode(true);
                        mEditValidcode.requestFocus();
                    }
                    break;
                case Constants.GET_DATA_FAIL:
                    mGetValidcodeDlg.dismiss();
                    Toast.makeText(ChangePhoneActivity.this,
                            msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    mGetValidCode.setEnabled(true);
                    break;
                case 201:
                    mGetValidcodeDlg.dismiss();
                    Toast.makeText(ChangePhoneActivity.this,
                            mResource.getString(R.string.no_network),
                            Toast.LENGTH_SHORT).show();
                    mGetValidcodeDlg.dismiss();
                    mGetValidCode.setEnabled(true);
                    break;
                case CHANGE_BUTTON_SEND_MESSAGE:
                    if (!isInterrupt)
                    {
                        mGetValidCode.setText("" + msg.arg1);

                        mGetValidcodeDlg.dismiss();

                        count_get_identify_code--;
                        if (count_get_identify_code > 0)
                        {
                            Message message = this.obtainMessage();
                            message.what = CHANGE_BUTTON_SEND_MESSAGE;
                            message.arg1 = count_get_identify_code;
                            this.sendMessageDelayed(message, 1000);
                        }
                        else
                        {
                            mGetValidCode.setEnabled(true);
                            mGetValidCode.setText(R.string.validecode_get);
                            mStrValidcode = "";
                        }
                    }
                    else
                    {
                        this.removeMessages(CHANGE_BUTTON_SEND_MESSAGE);
                        isInterrupt = false;
                    }
                    break;
                default:
                    Toast.makeText(ChangePhoneActivity.this,
                            getResources().getString(R.string.unknown_error),
                            Toast.LENGTH_SHORT).show();
                    mGetValidcodeDlg.dismiss();
            }
        }

    };

    private void initView()
    {
        mGetValidcodeDlg = new ProgressDialog(ChangePhoneActivity.this);
        mGetValidcodeDlg.setCanceledOnTouchOutside(false);
        mGetValidcodeDlg.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    mGetValidCode.setEnabled(true);
                    mGetValidCode.setText(R.string.validecode_get);
                    count_get_identify_code = COUNT_GET_INDENTIFY_CODE;
                    isInterrupt = true;
                    mStrValidcode = "";
                    mGetValidcode_handler.removeMessages(CHANGE_BUTTON_SEND_MESSAGE);
                }
                return false;
            }
        });
        mEditText_userId = (EditText) findViewById(R.id.user_name);
        mGetValidCode = (Button) findViewById(R.id.next_valide_code);
        mGetValidCode.setOnClickListener(ChangePhoneActivity.this);

        mInput_validecode_LinearLayout = (LinearLayout) findViewById(R.id.ll_input_valide);
        mBtn_Next = (Button) findViewById(R.id.btn_valid);
        mBtn_Next.setOnClickListener(ChangePhoneActivity.this);

        mEditValidcode = (EditText) findViewById(R.id.et_input_validcode);

        mResource = getResources();
        userType = Preferences.getInstance(getApplicationContext())
                .getRoleType();
        if (userType == Constants.TYPE_IS_STUDENT)
        {
            mGetValidCode.setText(getString(R.string.common_btn_save));
        }

        mEditText_userId.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3)
            {
                Log.d("", "daitm---onTextChanged");
                if (!mGetValidCode.isEnabled())
                {
                    mGetValidCode.setEnabled(true);
                    mGetValidCode.setText(R.string.validecode_get);
                    count_get_identify_code = COUNT_GET_INDENTIFY_CODE;
                    isInterrupt = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3)
            {
            }

            @Override
            public void afterTextChanged(Editable arg0)
            {
            }
        });
    }

    //添加上方的actionbar
    private void addTopBarToHead()
    {
        fl_header_title = (FrameLayout) findViewById(R.id.fl_head_modify_pass_word);
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
                mResource.getString(R.string.change_phone),
                null,
                null);
        fl_header_title.addView(actionBar);
    }

    protected void getIdentifyCode()
    {
        count_get_identify_code = COUNT_GET_INDENTIFY_CODE;//click button to set 120s back
        try
        {
            JSONObject json = new JSONObject();
            json.put("phone", mEditText_userId.getText().toString());
            mGetValidcode_handler.sendEmptyMessage(Constants.GET_DATA_START);
            HttpUtil.postRequest(json,
                    Constants.VALIDCODE,
                    mGetValidcode_handler,
                    Constants.GET_DATA_SUCCESS,
                    Constants.GET_DATA_FAIL);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        mGetValidCode.setEnabled(false);
    }

    @Override
    public void onClick(View arg0)
    {
        switch (arg0.getId())
        {
            case R.id.next_valide_code:
                if ("".equals(mEditText_userId.getText().toString()))
                {
                    Toast.makeText(ChangePhoneActivity.this,
                            mResource.getString(R.string.user_name_not_null),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mEditText_userId.getText().toString().length() != 11)
                {
                    Toast.makeText(ChangePhoneActivity.this,
                            R.string.info_phonenumber_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mPhone = mEditText_userId.getText().toString().trim();
                if (userType != Constants.TYPE_IS_STUDENT)
                {
                    getIdentifyCode();
                }
                else
                {
                    changePhoneFromServer();
                }

                break;
            case R.id.btn_valid:
                if ("".equals(mEditText_userId.getText().toString()))
                {
                    Toast.makeText(ChangePhoneActivity.this,
                            mResource.getString(R.string.user_name_not_null),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mPhone.equals(mEditText_userId.getText().toString()))
                {
                    Toast.makeText(ChangePhoneActivity.this,
                            mResource.getString(R.string.phone_num_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(mEditText_userId.getText().toString()))
                {
                    Toast.makeText(ChangePhoneActivity.this,
                            mResource.getString(R.string.validecode_null),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(mEditValidcode.getText().toString()))
                {
                    Toast.makeText(ChangePhoneActivity.this,
                            R.string.validecode_null,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mStrValidcode.equals(mEditValidcode.getText().toString()))
                {
                    Toast.makeText(ChangePhoneActivity.this,
                            mResource.getString(R.string.validecode_error),
                            Toast.LENGTH_SHORT).show();
                    mEditValidcode.setText("");
                    return;
                }

                changePhoneFromServer();

            default:
                break;
        }
    }

    private ProgressDialog mDialog;

    private Handler mSaveHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (isFinishing())
            {
                return;
            }
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    mDialog = new ProgressDialog(ChangePhoneActivity.this);
                    mDialog.setMessage(getString(R.string.ready_to_get));
                    mDialog.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    //                    Toast.makeText(getApplicationContext(),
                    //                            getString(R.string.change_phone_success),
                    //                            Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    if (userType == Constants.TYPE_IS_PARENT)
                    {
                        doLogout();//强制重新登录
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.change_phone_success),
                                Toast.LENGTH_SHORT).show();

                        Intent data = new Intent();
                        data.putExtra("phoneNum", mEditText_userId.getText()
                                .toString()
                                .trim());
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                    break;
                case Constants.GET_DATA_FAIL:
                    if (msg.arg1 == 100)
                    {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.change_exsit_phone),
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.change_phone_fail),
                                Toast.LENGTH_SHORT).show();
                    }
                    mDialog.dismiss();
                    break;
            }
        }
    };

    private void changePhoneFromServer()
    {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mSaveHandler.sendEmptyMessage(Constants.GET_DATA_START);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("userId", Preferences.getInstance(getApplicationContext())
                    .getFamilyUserId());
            obj.put("phone", mPhone);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.CHANGE_PHONE,
                mSaveHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private void doLogout()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                JSONObject requestObject = new JSONObject();
                final RequestResult requestResult = new RequestResult();
                HttpUtil.postRequest(requestObject,
                        Constants.LOGOUT_ACTION,
                        requestResult,
                        getApplicationContext());
                Message msg = mLoginout.obtainMessage();
                msg.what = requestResult.getCode();
                msg.obj = requestResult.getResult();
                mLoginout.sendMessage(msg);
            }
        }).start();

    }

    private Handler mLoginout = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Constants.SC_OK:
                    executeLogout();
                    break;
                default:
                    String resultString = HttpUtil.responseHandler(getApplicationContext(),
                            msg.what);
                    Toast.makeText(ChangePhoneActivity.this,
                            resultString,
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void executeLogout()
    {
        Preferences.getInstance(ChangePhoneActivity.this).clearData();
        Preferences.getInstance(ChangePhoneActivity.this)
                .setLastVersionCode(VersionManager.getSofewareVersionCode(ChangePhoneActivity.this));
        //初始化屏幕参数
        GloableData.initScreen(ChangePhoneActivity.this);
        //初始化iemi和imsi
        GloableData.initIMSIIMEI(ChangePhoneActivity.this);

        //确认注销时清除该帐号的本地数据
        //AM_database.getInstance(getApplicationContext()).deleteAllData();
        MemberDao dao = new MemberDao(getApplicationContext());
        dao.deleteAll();
        dao.close();
        Intent intentWidget = new Intent(FsConstants.WIDGET_ACCOUNT_CHANGGE);
        getApplicationContext().sendBroadcast(intentWidget);

        AppManager.getAppManager().finishAllActivity();
        Intent intent = new Intent(getApplicationContext(), LoginActivity_sm.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),
                getString(R.string.info_new_phonenumber),
                Toast.LENGTH_LONG).show();
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
