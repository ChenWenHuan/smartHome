package com.smarthome.client2.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
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
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class FindPassWord extends Activity implements OnClickListener
{
    private static final int CHANGE_BUTTON_SEND_MESSAGE = 100;

    public static final int COUNT_GET_INDENTIFY_CODE = 120;

    private boolean isInterrupt = false;

    private int count_get_identify_code = COUNT_GET_INDENTIFY_CODE;

    private FrameLayout fl_header_title; //actionbar布局

    private CustomActionBar actionBar; //上方菜单栏

    private LinearLayout mInput_validecode_LinearLayout; //输入验证码并验证的布局
    private LinearLayout  ll_register_phone_number;  // 获取验证码布局

    private Button mBtn_Next; //验证按钮

    private Button mGetValidCode; //获取验证码按钮

    private EditText mEditText_userId; //手机号输入框

    private EditText mEditValidcode; //验证码输入框

    private String mStrValidcode; //获取验证码接口返回的验证码

    private Resources mResource;

    private String mPhone = "";

    private ProgressDialog mGetValidcodeDlg;

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
                    Intent intent = new Intent(FindPassWord.this,
                            SetNewPwdActivity.class);
                    intent.putExtra("account", mEditText_userId.getText()
                            .toString());
                    startActivity(intent);
                    finish();
//                    mGetValidcodeDlg.dismiss();
//                    try
//                    {
//                        JSONObject json = new JSONObject(msg.obj.toString());
//                        mStrValidcode = json.getString("data");
//                    }
//                    catch (JSONException e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                    Message message1 = mGetValidcode_handler.obtainMessage();
//                    message1.what = CHANGE_BUTTON_SEND_MESSAGE;
//                    message1.arg1 = count_get_identify_code;
//                    mGetValidcode_handler.sendMessage(message1);
//
//                    mEditText_userId.setEnabled(false);
//                    ll_register_phone_number.setVisibility(View.GONE);
//                    mInput_validecode_LinearLayout.setVisibility(View.VISIBLE);
//
//                    mEditValidcode.setFocusable(true);
//                    mEditValidcode.setFocusableInTouchMode(true);
//                    mEditValidcode.requestFocus();

                    break;
                case Constants.GET_DATA_FAIL:
                    mGetValidcodeDlg.dismiss();
                    if(msg.obj != null) {
                        Toast.makeText(FindPassWord.this,
                                msg.obj.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                    mGetValidCode.setEnabled(true);
                    break;
                case Constants.GET_NEW_SERVER_ADDR_SUCESS:
                    try
                    {
                        JSONObject json = new JSONObject(msg.obj.toString());
                        if(json.has("data")) {
                            JSONObject data = json.getJSONObject("data");
                             HttpUtil.BASE_URL = data.getString("appServerIntfsAddr");
                             HttpUtil.g_bIsGetNewAddrDone = true;
                        }
                        else {
                            HttpUtil.g_bIsGetNewAddrDone = false;
                        }

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    getIdentifyCode();
                    break;

                case Constants.GET_NEW_SERVER_ADDR_FAIL:
                    HttpUtil.g_bIsGetNewAddrDone = false;
                    break;
                case 201:
                    mGetValidCode.setEnabled(true);
                    mGetValidCode.setText(R.string.validecode_get);
                    count_get_identify_code = COUNT_GET_INDENTIFY_CODE;
                    isInterrupt = true;
                    mGetValidcodeDlg.dismiss();
                    Toast.makeText(FindPassWord.this,
                            mResource.getString(R.string.no_network),
                            Toast.LENGTH_SHORT).show();
                    mGetValidcodeDlg.dismiss();
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
                    Toast.makeText(FindPassWord.this,
                            getResources().getString(R.string.unknown_error),
                            Toast.LENGTH_SHORT).show();
                    mGetValidcodeDlg.dismiss();
            }
        }

    };

    private void initView()
    {
        mGetValidcodeDlg = new ProgressDialog(FindPassWord.this);
        mGetValidcodeDlg.setCanceledOnTouchOutside(false);
        mGetValidcodeDlg.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
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
        mGetValidCode.setOnClickListener(FindPassWord.this);

        ll_register_phone_number = (LinearLayout) findViewById(R.id.ll_register_phone_number);
        mInput_validecode_LinearLayout = (LinearLayout) findViewById(R.id.ll_input_valide);
        ll_register_phone_number.setVisibility(View.VISIBLE);
        mInput_validecode_LinearLayout.setVisibility(View.INVISIBLE);

        mBtn_Next = (Button) findViewById(R.id.btn_valid);
        mBtn_Next.setOnClickListener(FindPassWord.this);

        mEditValidcode = (EditText) findViewById(R.id.et_input_validcode);

        mResource = getResources();

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
                mResource.getString(R.string.forget_pwd),
                null,
                null);
        fl_header_title.addView(actionBar);
    }

    protected void getNewServerAddr() {
        try
        {
            JSONObject json = new JSONObject();

            json.put("getType", "2");
            json.put("multiAcctCode", mEditText_userId.getText().toString());
            // json.put("forgetpassword", 1);
            mGetValidcode_handler.sendEmptyMessage(Constants.GET_DATA_START);
            HttpUtil.BASE_URL = HttpUtil.FIRST_ENTRY_ADDR;

            HttpUtil.postRequest(json,
                    Constants.GET_NEW_SERVER_ADDR,
                    mGetValidcode_handler,
                    Constants.GET_NEW_SERVER_ADDR_SUCESS,
                    Constants.GET_NEW_SERVER_ADDR_FAIL);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    protected void getIdentifyCode()
    {
        count_get_identify_code = COUNT_GET_INDENTIFY_CODE;//click button to set 120s back
        try
        {
            JSONObject json = new JSONObject();
            json.put("telNum", mEditText_userId.getText().toString());
           // json.put("forgetpassword", 1);
            mGetValidcode_handler.sendEmptyMessage(Constants.GET_DATA_START);
            HttpUtil.postRequest(json,
                    Constants.GETVALIDCODE,
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
                    Toast.makeText(FindPassWord.this,
                            mResource.getString(R.string.user_name_not_null),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mEditText_userId.getText().toString().length() != 11)
                {
                    Toast.makeText(FindPassWord.this,
                            "手机号码不正确",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mPhone = mEditText_userId.getText().toString().trim();
                if (HttpUtil.g_bIsGetNewAddrDone == true) {
                    getIdentifyCode();
                } else {
                    getNewServerAddr();
                }
                break;
            case R.id.btn_valid:
//                if ("".equals(mEditText_userId.getText().toString()))
//                {
//                    Toast.makeText(FindPassWord.this,
//                            mResource.getString(R.string.user_name_not_null),
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!mPhone.equals(mEditText_userId.getText().toString()))
//                {
//                    Toast.makeText(FindPassWord.this,
//                            mResource.getString(R.string.phone_num_error),
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if ("".equals(mEditText_userId.getText().toString()))
//                {
//                    Toast.makeText(FindPassWord.this,
//                            mResource.getString(R.string.validecode_null),
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if ("".equals(mEditValidcode.getText().toString()))
//                {
//                    Toast.makeText(FindPassWord.this,
//                            getResources().getString(R.string.validecode_null),
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!mStrValidcode.equals(mEditValidcode.getText().toString()))
//                {
//                    Toast.makeText(FindPassWord.this,
//                            mResource.getString(R.string.validecode_error),
//                            Toast.LENGTH_SHORT).show();
//                    mEditValidcode.setText("");
//                    return;
//                }

                Intent intent = new Intent(FindPassWord.this,
                        SetNewPwdActivity.class);
                intent.putExtra("account", mEditText_userId.getText()
                        .toString());
                startActivity(intent);
                finish();

            default:
                break;
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
