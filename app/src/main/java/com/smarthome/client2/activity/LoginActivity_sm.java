package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.model.retrofitServices.ApiService;
import com.smarthome.client2.model.retrofitServices.MyCookeiManager;
import com.smarthome.client2.model.retrofitServices.ServiceGenerator;
import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.message.MessageUtil;
import com.smarthome.client2.model.login.LoginModel;
import com.smarthome.client2.model.login.LoginServiceParameter;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LoginUtil;
import com.smarthome.client2.util.LoginUtil_vii;
import com.smarthome.client2.util.MySoftInputUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity_sm extends Activity implements TextWatcher {
    /**
     * 日志开关
     */
    private final static boolean DEBUG = true;

    public static final String KEY_REGIST_SUCCESS = "register_success";

    public static final String KEY_SHOW_USERNAME = "isShowUserName";

    public static final String KEY_LOGIN_FAILED = "isLoginFailed";

    public static final String KEY_ERROR_CODE = "error_code";

    public static final String KEY_SHOW_PSW = "isShowPassWord";

    public static final String KEY_SET_NEWPSW = "fromSetNewPwd";

    public static final int MY_RESULT_FINISH = 1;

    public static final int MY_ERROR_PASSWORD = 2;

    public static final int MY_UNEXIST_ID = 3;

    private  ServiceGenerator serviceGenerator = null;

    @Bind(R.id.e_person_img_person_pic)
    ImageView ePersonImgPersonPic;

    @Bind(R.id.editTextUsername)
    EditText editTextUsername;

    @Bind(R.id.Username)
    RelativeLayout Username;

    @Bind(R.id.editTextPassword)
    EditText editTextPassword;

    @Bind(R.id.forgetPwdTextView)
    TextView forgetPwdTextView;

    @Bind(R.id.Password)
    RelativeLayout Password;

    @Bind(R.id.loginButton)
    Button loginButton;

    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;

    @Bind(R.id.registerTextView)
    TextView registerTextView;

    private String username, password, originUserName;

    private ProgressDialog loginDialog;

    private boolean isLoading = false;

    private MyAsyncTask myAsyncTask = null;

    private GetNewServerListAsyncTask getServerListTask = null;

    private GetNewServerAddrAsyncTask getAddrTask = null;

    private int login_state = Constants.LOGIN_LOCAL_STATE;

    private final static int LOGIN_START = 1;

    private final static int LOGIN_SUCCESS = 2;

    private final static int LOGIN_FAIL = 3;

    private final static int GET_SERVER_LIST_SUCCESS = 9;

    private final static int GET_SERVER_LIST_FAIL = 10;

    private long main_key_down_start_time = 0;

    private boolean isFirstMainKeyDown = true;

    private Toast mToast;

    private String mStrServer[] = null;

    private Map<String, String> mServerList = new HashMap<String, String>();


    private long serverId = 0;

    private void updateState(int state) {
        updateState(state, Constants.SC_OK);
    }

    private void updateState(int state, String errorMsg) {
        if (isFinishing()) {
            return;
        }
        isLoading = false;
        loginDialog.dismiss();
        showToast(errorMsg);
    }

    private void updateState(int state, int code) {
        log("---updataDialog---" + state);
        switch (state) {
            case LOGIN_START:
                loginDialog.setMessage(getResources().getString(R.string.logining));
                loginDialog.show();
                break;
            case LOGIN_SUCCESS:
                isLoading = false;
                loginDialog.setMessage("登录成功");
                loginDialog.dismiss();
                setParameterAfterLoginSuccess();
                if (!MessageUtil.checkService(this)) {
                    log("----start service----");
                    MessageUtil.startMessageService(this);
                }
                // widget刷新视图
                Intent intentWidget = new Intent(
                        FsConstants.WIDGET_ACCOUNT_CHANGGE);
                sendBroadcast(intentWidget);
                Intent intent = new Intent(LoginActivity_sm.this,
                        MainActivity.class);
                startActivity(intent);
                finish();

                break;
            case LOGIN_FAIL:
            case GET_SERVER_LIST_FAIL:
                isLoading = false;

                loginDialog.dismiss();
                String resultString = HttpUtil.responseHandler(getApplicationContext(),
                        code);

                if (code == Constants.ERROR_PASSWORD
                        || code == Constants.UNEXIST_ID) {
                    editTextPassword.setText("");
                }
                showToast(resultString);
                break;
            case GET_SERVER_LIST_SUCCESS:
                if (!mServerList.isEmpty()) {
                    new AlertDialog.Builder(LoginActivity_sm.this).setTitle("请选择服务器")
                            .setItems(mStrServer, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    HttpUtil.BASE_URL_SMART = mServerList.get(mStrServer[which]);
                                    HttpUtil.g_bIsGetNewAddrDone = true;
                                    HttpUtil.initUrl(2);
                                    Intent intent = new Intent(LoginActivity_sm.this, RegisterActivity.class);
                                    startActivityForResult(intent, MY_RESULT_FINISH);
                                }
                            }).show();
                }
                break;
        }
    }

    private Handler serverListHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_SERVER_LIST_SUCCESS:
                    if (!mServerList.isEmpty()) {
                        new AlertDialog.Builder(LoginActivity_sm.this).setTitle("请选择服务器")
                                .setItems(mStrServer, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        HttpUtil.BASE_URL_SMART = mServerList.get(mStrServer[which]);
                                        HttpUtil.g_bIsGetNewAddrDone = true;
                                        HttpUtil.initUrl(2);
                                        Intent intent = new Intent(LoginActivity_sm.this, RegisterActivity.class);
                                        startActivityForResult(intent, MY_RESULT_FINISH);
                                    }
                                }).show();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void setParameterAfterLoginSuccess() {
        Preferences.getInstance(LoginActivity_sm.this).setAutoLogin(false);
        Preferences.getInstance(LoginActivity_sm.this).setUserName(username);
        Preferences.getInstance(LoginActivity_sm.this).setPassWord(password);
        Preferences.getInstance(LoginActivity_sm.this).setIsLogout(false);
        Preferences.getInstance(LoginActivity_sm.this).setLoginState(login_state);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        serviceGenerator = ServiceGenerator.getInstance(this);
        setContentView(R.layout.ac_login_sm);
        ButterKnife.bind(this);

        loginDialog = new ProgressDialog(this);
        loginDialog.setCanceledOnTouchOutside(false);


        loginDialog.setOnCancelListener(new OnCancelListener
                () {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (myAsyncTask != null) {
                    myAsyncTask.cancel(true);
                }
                myAsyncTask = null;
                isLoading = false;
            }
        });

        initData();
    }

    @OnClick(R.id.registerTextView)
    public void onRegisterTextView(View arg0) {
        Click_register(arg0);
    }

    @OnClick (R.id.forgetPwdTextView)
    public void onForgetPwdTextView() {
        Intent intent = new Intent(LoginActivity_sm.this,
                FindPassWord.class);
        startActivity(intent);
    }

    private void initData() {
        String username = Preferences.getInstance(this).getUserName();
        String password = Preferences.getInstance(this).getPassWord();
        originUserName = username;

        if (Preferences.getInstance(this).getIsLogout()
                && login_state == Constants.LOGIN_LOCAL_STATE) {
            editTextUsername.setText(username);
            editTextUsername.setSelection(username.length());
        }

        editTextUsername.setText(username);
        editTextPassword.setText(password);

        Intent intent = getIntent();

        if (intent.getBooleanExtra(KEY_SHOW_USERNAME, false)) {
            editTextUsername.setText(username);
            editTextUsername.setSelection(username.length());
        }

        if (intent.getBooleanExtra(KEY_REGIST_SUCCESS, false)) {
            showToast(getString(R.string.login_register_success));
        }

        if (intent.getBooleanExtra(KEY_LOGIN_FAILED, false)) {
            String error_msg = HttpUtil.responseHandler(this,
                    intent.getIntExtra(KEY_ERROR_CODE, Constants.UNKNOW_RESULT));
            if (!TextUtils.isEmpty(error_msg)) {
                showToast(error_msg);
            }
        }

        if (intent.getBooleanExtra(KEY_SHOW_PSW, false)) {
            editTextPassword.setText(password);
            editTextPassword.setSelection(password.length());
        }

        if (intent.getBooleanExtra(KEY_SET_NEWPSW, false)) {
            editTextPassword.setText("");
        }

    }

    public void back(View view) {
        MySoftInputUtil.hideInputMethod(this, view);
        this.finish();
    }

    public void login(View view) {
        MySoftInputUtil.hideInputMethod(this, view);
        username = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            updateState(LOGIN_FAIL, Constants.NAME_PASSWORD_EMPTY);
            return;
        } else if (username.length() != 11) {
            updateState(LOGIN_FAIL, Constants.NAME_PASSWORD_EMPTY); //PASSWORD_NOT_ENLVEN
            return;
        } else if (editTextPassword.length() > 16) {
            updateState(LOGIN_FAIL, Constants.NAME_PASSWORD_EMPTY); //PASSWORD_NOT_ENLVEN
            return;
        } else {
            login_state = Constants.LOGIN_LOCAL_STATE;
            doLogin();
        }
    }

    private void doLogin() {
        if (isLoading) {
            return;
        }

        isLoading = true;
        if (HttpUtil.isNetworkAvailable(this)) {
            updateState(LOGIN_START);
            if (HttpUtil.g_bIsGetNewAddrDone == true) {
                loginProcess();

            } else {
                getAddrTask = new GetNewServerAddrAsyncTask();
                getAddrTask.execute();
            }
        } else {
            updateState(LOGIN_FAIL, Constants.NO_NETWORK);
        }
    }

    public void loginProcess() {
        LoginServiceParameter loginParam = new LoginServiceParameter("v1.1",password,username);

        serviceGenerator.setBaseUrlSmart(HttpUtil.BASE_URL_SMART);
        ApiService loginService = serviceGenerator.getApiService();
        MyCookeiManager myCookieManager = new MyCookeiManager();
        CookieHandler.setDefault(myCookieManager);
        Call call = loginService.login(loginParam);
        call.enqueue( new Callback<LoginModel>() {
            @Override
            public void onResponse(Response<LoginModel> response, Retrofit retrofit) {
                int statusCode = response.code();

                Log.e("login", "statuscode=" + statusCode);
                if (statusCode == 200) {
                    LoginModel user = response.body();
                    if (user.getRetcode() == 200){
                        LoginUtil_vii.parseLoginInfoToOldInterface(LoginActivity_sm.this, user);
                        updateState(LOGIN_SUCCESS);
                    }else{
                        updateState(LOGIN_FAIL, user.getRetmsg());
                    }
                }
                else {
                    updateState(LOGIN_FAIL, Constants.NO_NETWORK);
                }
            }
            @Override
            public void onFailure(Throwable throwable) {
                updateState(LOGIN_FAIL, Constants.NO_NETWORK);
            }
        });
    }

    //    业务服务器id
    //    1:测试环境
    //    2：全国
    //    3：四川
    //    4：江苏
    public void Click_register(View view) {
        getServerListTask = new GetNewServerListAsyncTask();
        getServerListTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MY_RESULT_FINISH) {
            setResult(RunTypeActivity.MY_RESULT_FINISH);
            finish();
        }

        if (resultCode == MY_ERROR_PASSWORD) {
            editTextPassword.setText("");
        }

        if (resultCode == MY_UNEXIST_ID) {
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MySoftInputUtil.hideInputMethod(this, editTextUsername);
        MySoftInputUtil.hideInputMethod(this, editTextPassword);
        ButterKnife.unbind(this);

        if (loginDialog != null) {
            loginDialog.dismiss();
        }
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime()
                .getTime()) / (1000 * 60 * 60 * 24));
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            loginButton.setEnabled(true);
            loginButton.setBackgroundResource(R.drawable.border_red_round_login_sm);
        } else {
            loginButton.setEnabled(false);
            loginButton.setBackgroundResource(R.drawable.border_grey_round);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        if (!originUserName.equals(editTextUsername.getText().toString())
                && editTextUsername.isFocused()) {
            if (!TextUtils.isEmpty(editTextPassword.getText().toString())) {
                editTextPassword.setText("");
            }
        }
    }

    public void click_ForgetPsw(View view) {
    }

    class GetNewServerListAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = LoginUtil.requestNewServerAddr_v11(LoginActivity_sm.this);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result || TextUtils.isEmpty(result)) {
                updateState(LOGIN_FAIL, Constants.JSON_ERROR);
                return;
            }
            boolean isCode = false;
            String resultMsg = "";
            int resultcode = Constants.UNKNOW_RESULT;
            try {
                JSONObject jobj = new JSONObject(result);
                resultcode = jobj.getInt("retcode");
                if (jobj.has("retmsg")) {
                    resultMsg = jobj.getString("retmsg");
                }
                if (jobj.has("data")) {
                    JSONArray data = jobj.getJSONArray("data");
                    mStrServer = new String[data.length()];
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jobjServer = data.getJSONObject(i);
                        String serverName = jobjServer.getString("serverDesc");
                        mStrServer[i] = new String(serverName);
                        String serverAddr = jobjServer.getString("serverAddr");
                        if ((serverName != "") || (serverAddr != "")) {
                            mServerList.put(serverName, serverAddr);
                        }
                    }
                } else {
                    HttpUtil.g_bIsGetNewAddrDone = false;
                }
                isCode = resultcode == 200;

            } catch (Exception e) {
                isCode = false;
            }
            if (!isCode) {
                if (resultMsg.isEmpty()) {
                    updateState(GET_SERVER_LIST_FAIL, resultcode);
                } else {
                    updateState(GET_SERVER_LIST_FAIL, resultMsg);
                }
            } else {
                Message msg = new Message();
                msg.what = GET_SERVER_LIST_SUCCESS;
                serverListHandler.sendMessage(msg);
            }
        }
    }

    class GetNewServerAddrRegisterAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            log("---loginactivity---GetNewServerAddrRegisterAsyncTask---doInBackground--->");
            String result = LoginUtil.requestNewServerAddr_v11(LoginActivity_sm.this,
                    serverId);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result || TextUtils.isEmpty(result)) {
                log("login----error----else");
                updateState(LOGIN_FAIL, Constants.JSON_ERROR);
                return;
            }
            boolean isCode = false;
            String resultMsg = "";
            int resultcode = Constants.UNKNOW_RESULT;
            try {
                JSONObject jobj = new JSONObject(result);
                resultcode = jobj.getInt("retcode");
                if (jobj.has("retmsg")) {
                    resultMsg = jobj.getString("retmsg");
                }
                if (jobj.has("data")) {
                    JSONObject data = jobj.getJSONObject("data");
                    HttpUtil.BASE_URL_SMART = data.getString("appServerIntfsAddr");

                    HttpUtil.g_bIsGetNewAddrDone = true;
                    HttpUtil.initUrl(2);
                } else {
                    HttpUtil.g_bIsGetNewAddrDone = false;
                }
                isCode = resultcode == 200;

            } catch (Exception e) {
                isCode = false;
            }
            if (!isCode) {
                if (resultMsg.isEmpty()) {
                    updateState(LOGIN_FAIL, resultcode);
                } else {
                    updateState(LOGIN_FAIL, resultMsg);
                }
            } else {
                try {
                    Intent intent = new Intent(LoginActivity_sm.this, RegisterActivity.class);
                    startActivityForResult(intent, MY_RESULT_FINISH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GetNewServerAddrAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            log("---loginactivity---GetNewServerAddrAsyncTask---doInBackground--->");
            String result = LoginUtil.requestNewServerAddr_v11(LoginActivity_sm.this,
                    username);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result || TextUtils.isEmpty(result)) {
                log("login----error----else");
                updateState(LOGIN_FAIL, Constants.JSON_ERROR);
                return;
            }
            boolean isCode = false;
            String resultMsg = "";
            int resultcode = Constants.UNKNOW_RESULT;
            try {
                JSONObject jobj = new JSONObject(result);
                resultcode = jobj.getInt("retcode");
                if (jobj.has("retmsg")) {
                    resultMsg = jobj.getString("retmsg");
                }
                if (jobj.has("data")) {
                    JSONObject data = jobj.getJSONObject("data");
                    HttpUtil.BASE_URL_SMART = data.getString("appServerIntfsAddr");
                    serviceGenerator.setBaseUrlSmart(HttpUtil.BASE_URL_SMART);
                    Preferences.getInstance(LoginActivity_sm.this).setUserNameBaseUrl(username, HttpUtil.BASE_URL_SMART);
                    HttpUtil.g_bIsGetNewAddrDone = true;
                    HttpUtil.initUrl(2);
                } else {
                    HttpUtil.g_bIsGetNewAddrDone = false;
                }

                isCode = resultcode == 200;

            } catch (Exception e) {
                isCode = false;
            }
            if (!isCode) {
                if (resultMsg.isEmpty()) {
                    updateState(LOGIN_FAIL, resultcode);
                } else {
                    updateState(LOGIN_FAIL, resultMsg);
                }
            } else {
                try {
                    loginProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String result = LoginUtil.requestLogin_V11(LoginActivity_sm.this,
                    username,
                    password);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result || TextUtils.isEmpty(result)) {
                updateState(LOGIN_FAIL, Constants.JSON_ERROR);
                return;
            }
            boolean isCode = false;
            String resultMsg = "";
            int resultcode = Constants.UNKNOW_RESULT;
            try {
                JSONObject jobj = new JSONObject(result);
                resultcode = jobj.getInt("retcode");
                if (jobj.has("retmsg")) {
                    resultMsg = jobj.getString("retmsg");
                }
                isCode = resultcode == 200;

            } catch (Exception e) {
                isCode = false;
            }
            if (!isCode) {
                if (resultMsg.isEmpty()) {
                    updateState(LOGIN_FAIL, resultcode);
                } else {
                    updateState(LOGIN_FAIL, resultMsg);
                }
            } else {
                Boolean isLogin = LoginUtil.doLogin(LoginActivity_sm.this, result);
                if (isLogin) {
                    updateState(LOGIN_SUCCESS);
                } else {
                    updateState(LOGIN_FAIL, Constants.JSON_ERROR);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFirstMainKeyDown) {
                main_key_down_start_time = System.currentTimeMillis();
                showToast(getString(R.string.alert_exit));
                isFirstMainKeyDown = false;
                return false;
            } else {
                if (System.currentTimeMillis() - main_key_down_start_time < 3000) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    main_key_down_start_time = System.currentTimeMillis();
                    showToast(getString(R.string.alert_exit));
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * show toast<BR>
     *
     * @param msg alert info
     */
    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(),
                    msg,
                    Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    private void log(String msg) {
        if (DEBUG) {
            TLog.Log(msg);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        log("--onNewIntent--");
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
