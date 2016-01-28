package com.smarthome.client2.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;



import com.smarthome.client2.R;
import com.smarthome.client2.bean.UserBean;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.MyExceptionDialog;
import com.smarthome.client2.util.StringUtils;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 注册页面
 * 
 * @author xiaolong.zhang
 * 
 */
public class RegisterActivity extends Activity
{

    public static final int REQUEST_CODE = 0;

    public static final int RESULT_CODE = 1;

    public static final int MY_RESULT_FINISH = 1;

    public static final int COUNT_GET_INDENTIFY_CODE = 120;

    private Context ctx;

    private EditText et_user_name_register;

    private EditText et_pass_word_register;

    private EditText et_pass_word_repeat_register;

    private EditText et_identify_code_register;

    private Button btn_commit_register;

    private TextView textview_identify_code;
    private TextView textView_unshow_passwd;

    private TextView textView_show_passwd;

    //topBar
    private FrameLayout fl_head_register;

    private CustomActionBar actionBar;

    private boolean isGetIdentifyCodeing = false;//是否正在获取验证码

    private String s_user_name = "";

    private String s_pass_word = "";

    //private String s_pass_word_repeat = "";

    private String s_identify_code_register = "";

    private String mstrJsonData = "";

    private UserBean userBean = new UserBean();

    private ProgressDialog mProgressBar;

    private MyExceptionDialog myExceptionDialog;

    private boolean isInterrupt = false;

    private String mStrServerId = "";

    HashMap<String, String> tel_Validecode = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_register_sm);

        this.ctx = this;

        addTopActionBar();//初始化页面顶部的topBar

        initView();//初始化页面的控件

        iniViewClickListener();
        if(this.getIntent().hasExtra("serverid")) {
            mStrServerId = this.getIntent().getStringExtra("serverid");
        }

    }

    private void addTopActionBar()
    {
        fl_head_register = (FrameLayout) findViewById(R.id.fl_head_register);

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
                "注册",
                0,
                null);

        fl_head_register.addView(actionBar);
    }

    private void initView()
    {
        et_user_name_register = (EditText) findViewById(R.id.et_user_name_register);
        et_pass_word_register = (EditText) findViewById(R.id.et_pass_word_register);
//        et_pass_word_repeat_register = (EditText) findViewById(R.id.et_pass_word_repeat_register);
        et_identify_code_register = (EditText) findViewById(R.id.et_identify_code_register);
        btn_commit_register = (Button) findViewById(R.id.btn_commit_register);
        textview_identify_code = (TextView) findViewById(R.id.textview_identify_code);
        textView_show_passwd = (TextView) findViewById(R.id.textView_show_passwd);
        textView_unshow_passwd = (TextView) findViewById(R.id.textView_unshow_passwd);

        mProgressBar = new ProgressDialog(RegisterActivity.this);
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    textview_identify_code.setEnabled(true);
                    textview_identify_code.setText(R.string.validecode_get);
                    count_get_identify_code = COUNT_GET_INDENTIFY_CODE;
                    isInterrupt = true;
                    mstrJsonData = "";
                    handler_get_identify_code.removeMessages(CHANGE_BUTTON_SEND_MESSAGE);
                    isGetIdentifyCodeing = false;
                }
                return false;
            }
        });

        myExceptionDialog = new MyExceptionDialog(RegisterActivity.this);
        myExceptionDialog.setSubmitClick(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myExceptionDialog.dismissMyDialog();
            }
        });

        et_user_name_register.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3)
            {
                Log.d("", "daitm---onTextChanged");
                if (!textview_identify_code.isEnabled())
                {
                    textview_identify_code.setEnabled(true);
                    textview_identify_code.setText(R.string.validecode_get);
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

    private void iniViewClickListener()
    {
        btn_commit_register.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                s_user_name = et_user_name_register.getText().toString();
                s_pass_word = et_pass_word_register.getText().toString();
//                s_pass_word_repeat = et_pass_word_repeat_register.getText()
//                        .toString();
                s_identify_code_register = et_identify_code_register.getText()
                        .toString();

                if (TextUtils.isEmpty(s_user_name))
                {
                    Toast.makeText(ctx,
                            R.string.user_name_not_null,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (s_user_name.length() != 11)
                {
                    Toast.makeText(ctx,
                            R.string.phone_num_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(s_identify_code_register))
                {
                    Toast.makeText(ctx,
                            R.string.validecode_null,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(s_pass_word))
                {
                    Toast.makeText(ctx, R.string.empty_pwd, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if ((s_pass_word.length() > 16) && (s_pass_word.length() < 6))
                {
                    Toast.makeText(ctx, R.string.wrong_pwd_size, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (!mstrJsonData.equals(et_identify_code_register.getText()
                        .toString()))
                {
                    Toast.makeText(ctx,
                            R.string.validecode_error,
                            Toast.LENGTH_SHORT).show();
                    et_identify_code_register.setText("");
                    return;
                }



                if (s_pass_word.length() < 6 || s_pass_word.length() > 16)
                {
                    Toast.makeText(ctx,
                            R.string.wrong_pwd_size,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtils.isCN(s_pass_word))
                {
                    Toast.makeText(ctx,
                            getString(R.string.pwd_no_chinese),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                userBean.acctno = s_user_name;
                userBean.pwd = s_pass_word;

                doForRegister(userBean);
            }
        });

        textview_identify_code.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ExceptionReciver.setDialog(mProgressBar);
                ExceptionReciver.setRegisterExceptionListener(mServerExceptionLisenter);
                count_get_identify_code = COUNT_GET_INDENTIFY_CODE;
                s_user_name = et_user_name_register.getText().toString();
                if (s_user_name.equals(""))
                {
                    Toast.makeText(RegisterActivity.this,
                            R.string.user_name_not_null,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (s_user_name.length() != 11)
                {
                    Toast.makeText(ctx,
                            R.string.phone_num_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                et_identify_code_register.setFocusable(true);
                et_identify_code_register.setFocusableInTouchMode(true);
                et_identify_code_register.requestFocus();

                tel_Validecode.clear();

                getIdentifyCode();
            }
        });


        textView_show_passwd.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                et_pass_word_register.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                textView_unshow_passwd.setVisibility(View.VISIBLE);
                textView_show_passwd.setVisibility(View.INVISIBLE);
            }
        });

        textView_unshow_passwd.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                et_pass_word_register.setTransformationMethod(PasswordTransformationMethod.getInstance());
                textView_show_passwd.setVisibility(View.VISIBLE);
                textView_unshow_passwd.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void doForRegister(final UserBean userBean)
    {

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                try
                {
                    if (et_identify_code_register.getText()
                            .toString()
                            .equals(mstrJsonData))
                    {
                        if (!et_pass_word_register.getText()
                                .toString().isEmpty()
                                )
                        {
                            JSONObject obj = new JSONObject();
                            obj.put("account", et_user_name_register.getText()
                                    .toString());
                            obj.put("pwd", et_pass_word_register.getText()
                                    .toString());
                            HttpUtil.postRequest(obj,
                                    Constants.REGISTER_ACTION,
                                    handler_register,
                                    Constants.GET_DATA_SUCCESS,
                                    Constants.GET_DATA_FAIL);
                        }
                        else
                        {
                            mExceptionHandler.sendEmptyMessage(REGISTER_PASSWORD_EMPTY);
                        }
                    }
                    else
                    {
                        mExceptionHandler.sendEmptyMessage(REGISTER_VALIDECODE_ERROR);
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int count_get_identify_code = COUNT_GET_INDENTIFY_CODE;//COUNT_GET_INDENTIFY_CODE秒内获取一次验证码

    protected void getIdentifyCode()
    {
        if (isGetIdentifyCodeing)
        {
            return;
        }
        isGetIdentifyCodeing = true;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //设置登录请求参数
                JSONObject obj = new JSONObject();
                try
                {
                    obj.put("phone", et_user_name_register.getText().toString());
                    obj.put("reg", true);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                handler_get_identify_code.sendEmptyMessage(Constants.GET_DATA_START);
                HttpUtil.postRequest(obj,
                        Constants.VALIDCODE,
                        handler_get_identify_code,
                        Constants.GET_DATA_SUCCESS,
                        Constants.GET_DATA_FAIL);
            }
        }).start();

        textview_identify_code.setEnabled(true);
    }

    private final static int REGISTER_VALIDECODE_ERROR = 1;

    private final static int REGISTER_CONFIG_SECRET_ERROR = 2;
    private final static int REGISTER_PASSWORD_EMPTY = 3;

    private final static int LOGIN_FAIL = 4;

    private Handler mExceptionHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case REGISTER_VALIDECODE_ERROR:
                    Toast.makeText(ctx,
                            R.string.validecode_error,
                            Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_CONFIG_SECRET_ERROR:
                    Toast.makeText(ctx,
                            R.string.two_pwd_diff,
                            Toast.LENGTH_SHORT).show();
                    et_pass_word_register.setText("");
                    et_pass_word_repeat_register.setText("");
                    break;
                case REGISTER_PASSWORD_EMPTY:
                    Toast.makeText(ctx,
                            "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    et_pass_word_register.setText("");
                    et_pass_word_repeat_register.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    private Handler handler_register = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    mProgressBar.setMessage(getResources().getString(R.string.register_now));
                    mProgressBar.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    mProgressBar.dismiss();
                    Intent intent = new Intent(RegisterActivity.this,
                            LoginActivity_sm.class);
                    intent.putExtra(LoginActivity_sm.KEY_REGIST_SUCCESS, true);
                    startActivity(intent);
                    break;
                case Constants.GET_DATA_FAIL:
                    mProgressBar.dismiss();
                    if (msg.arg1 == 104)
                    {
                        Toast.makeText(RegisterActivity.this,
                                getResources().getString(R.string.register_repeat),
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    else if (msg.arg1 == 500){
                        Toast.makeText(RegisterActivity.this,
                                msg.obj.toString(),
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }else  {
                        Toast.makeText(RegisterActivity.this,
                                getResources().getString(R.string.register_fail),
                                Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 201:
                    mProgressBar.dismiss();
                    Toast.makeText(RegisterActivity.this,
                            getResources().getString(R.string.no_network),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    mProgressBar.dismiss();
                    Toast.makeText(RegisterActivity.this,
                            getResources().getString(R.string.unknown_error),
                            Toast.LENGTH_SHORT).show();

                    //          case HttpService.SC_OK:
                    //              isRegistering = false;
                    //              TLog.Log("zxl---RegisterActivity---sc_ok--->"+msg.arg1+"--->"+msg.obj);
                    //
                    //              UserBean tempBean = (UserBean) msg.obj;
                    //              userBean.id = tempBean.id;
                    ////                Intent intent = new Intent(RegisterActivity.this, SetInfoActivity.class);
                    ////                intent.putExtra(SetInfoActivity.S_USER_BEAN, userBean.beanToString());
                    ////                startActivityForResult(intent,0);
                    ////
                    ////                DBManager.open(ctx).insertToUserTable(userBean);
                    //              Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    //              intent.putExtra("register_success", "register_success");
                    //              startActivity(intent);
                    //
                    //              finish();
                    //
                    //              break;
                    //          default:
                    //              isRegistering = false;
                    //              TLog.Log("zxl---RegisterActivity---fail--->"+msg.arg1+"--->"+msg.obj);
                    //              break;
            }
        }
    };

    private static final int CHANGE_BUTTON_SEND_MESSAGE = 100;

    private Handler handler_get_identify_code = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    mProgressBar.setMessage(getResources().getString(R.string.validecode_getting));
                    mProgressBar.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    mProgressBar.dismiss();
                    isGetIdentifyCodeing = false;
                    TLog.Log("zxl---RegisterActivity get_identify_code---sc_ok--->"
                            + msg.arg1 + "--->" + msg.obj);
                    JSONObject JsonObj;
                    try
                    {
                        JsonObj = new JSONObject(msg.obj.toString());
                        mstrJsonData = JsonObj.getString("data");
                        tel_Validecode.put(et_user_name_register.getText()
                                .toString(), mstrJsonData);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    Message msg1 = handler_get_identify_code.obtainMessage();
                    msg1.what = CHANGE_BUTTON_SEND_MESSAGE;
                    msg1.arg1 = count_get_identify_code;
                    handler_get_identify_code.sendMessage(msg1);

                    break;
                case 201:
                    isGetIdentifyCodeing = false;
                    textview_identify_code.setEnabled(true);
                    textview_identify_code.setText(R.string.validecode_get);
                    count_get_identify_code = COUNT_GET_INDENTIFY_CODE;
                    isInterrupt = true;
                    if (mProgressBar != null && mProgressBar.isShowing())
                    {
                        mProgressBar.dismiss();
                    }
                    Toast.makeText(ctx,
                            getString(R.string.no_network),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.GET_DATA_FAIL:
                    isGetIdentifyCodeing = false;
                    mProgressBar.dismiss();
                    Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    textview_identify_code.setEnabled(true);
                    break;
                case CHANGE_BUTTON_SEND_MESSAGE:
                    if (!isInterrupt)
                    {
                        textview_identify_code.setText("" + msg.arg1);

                        mProgressBar.dismiss();

                        count_get_identify_code--;
                        if (count_get_identify_code > 0)
                        {
                            textview_identify_code.setEnabled(false);
                            Message message = this.obtainMessage();
                            message.what = CHANGE_BUTTON_SEND_MESSAGE;
                            message.arg1 = count_get_identify_code;
                            this.sendMessageDelayed(message, 1000);
                        }
                        else
                        {
                            textview_identify_code.setEnabled(true);
                            textview_identify_code.setText(R.string.validecode_get);
                            mstrJsonData = "";
                        }
                    }
                    else
                    {
                        this.removeMessages(CHANGE_BUTTON_SEND_MESSAGE);
                        isInterrupt = false;
                    }
                    break;
                default:
                    mProgressBar.setMessage(getResources().getString(R.string.unknown_error));
                    mProgressBar.show();
                    isGetIdentifyCodeing = false;
                    TLog.Log("zxl---RegisterActivity get_identify_code---fail--->"
                            + msg.arg1 + "--->" + msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy()
    {
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

    private ServerExceptionListener mServerExceptionLisenter = new ServerExceptionListener()
    {
        @Override
        public void getConnectionFail()
        {
            textview_identify_code.setEnabled(true);
            textview_identify_code.setText(R.string.validecode_get);
            count_get_identify_code = COUNT_GET_INDENTIFY_CODE;
            isInterrupt = true;
            isGetIdentifyCodeing = false;
        }
    };

    public interface ServerExceptionListener
    {
        void getConnectionFail();
    }

}
