package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.familySchool.utils.LogUtil;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.UserInfoUtil;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * [添加老人页面]<BR>
 * @author n003913
 * @version [ODP Client R001C01LAI141, 2015年2月3日]
 */
public class CreateMemberActivity_sm extends Activity
{
    private static final int COUNT_GET_INDENTIFY_CODE = 120;

    private static final int CHANGE_TIMER_MESSAGE = 100;

    private static final int TYPE_VALIDE_OLD = 0;

    private static final int TYPE_SAVE_OLD = 1;

    private FrameLayout mTitleBar;

    private CustomActionBar mActionBar;

    // private ImageView iv_back;// 返回

    // private TextView tv_right;// 右侧的保存和下一步按钮

    // private ImageView iv_head;// 头像

    // 输入昵称
    private EditText mEditTextName;

    // 输入电话
    private EditText mEditTextTel;

    // 输入设备标识
    private EditText mEditTextDeviceId;

    // 输入设备标识
    private EditText mEditTextValidecode;

    private Boolean clickNect = false;

    private String validCode = "";

    private LinearLayout mLayoutValidecode;

    private LinearLayout mRetryValide;

    private Button mBtnRetryValide;

    private String mOldPhone;

    private String mOldDeviceId;

    private String mOldAlias;

    private String mValidecode;

    private Timer mTimer;

    private TimerTask mTimerTask;

    private Toast mToast;

    private int mTimerRecord = COUNT_GET_INDENTIFY_CODE;

    private ProgressDialog mAddOldPersonDialog;

    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case CHANGE_TIMER_MESSAGE:
                    if (mTimerRecord <= 0)
                    {
                        clearTimer();
                        mBtnRetryValide.setText(getString(R.string.validecode_get));
                        mBtnRetryValide.setEnabled(true);
                    }
                    else
                    {
                        mTimerRecord--;
                        mBtnRetryValide.setText(String.valueOf(mTimerRecord));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param result
     * @param errorcode
     * @param type
     */
    private void updateState(int result, int errorcode, int type)
    {
        switch (result)
        {
            case Constants.GET_DATA_START:
                if (type == TYPE_VALIDE_OLD)
                {
                    mAddOldPersonDialog.setMessage(getString(R.string.validecode_getting));
                }
                else
                {
                    mAddOldPersonDialog.setMessage(getString(R.string.search_add_oldman_wait));
                }
                mAddOldPersonDialog.show();
                break;
            case Constants.GET_DATA_SUCCESS:
                mAddOldPersonDialog.dismiss();
                if (type == TYPE_SAVE_OLD)
                {
                    finish();
                }
                break;
            case Constants.GET_DATA_FAIL:
                mAddOldPersonDialog.dismiss();
                if (type == TYPE_VALIDE_OLD)
                {
                    showToast(HttpUtil.responseHandler(getApplicationContext(),
                            errorcode));
                    mBtnRetryValide.setText(getString(R.string.validecode_get));
                    mBtnRetryValide.setEnabled(true);
                }
                else
                {
                    if (errorcode == 104)
                    {
                        showToast(R.string.search_add_oldman_exist);
                    }
                    else
                    {
                        StringBuffer errBuffer = new StringBuffer(
                                getString(R.string.search_add_oldman_fail));
                        showToast(errBuffer.append("，")
                                .append(HttpUtil.responseHandler(getApplicationContext(),
                                        errorcode))
                                .toString());
                    }
                }
                break;
            default:
                TLog.Log("----add old orther--" + result + "  " + errorcode);
                showToast(HttpUtil.responseHandler(getApplicationContext(),
                        errorcode));
                mBtnRetryValide.setText(getString(R.string.validecode_get));
                mBtnRetryValide.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.fs_activity_add_older);
        setContentView(R.layout.e_create_new_person);

        addTopBarToHead();
        initViews();

        mAddOldPersonDialog = new ProgressDialog(CreateMemberActivity_sm.this);
        mAddOldPersonDialog.setCanceledOnTouchOutside(false);
        AppManager.getAppManager().addActivity(this);
    }

    /**
     * 添加actonbar<BR>
     */
    private void addTopBarToHead()
    {
        mTitleBar = (FrameLayout) findViewById(R.id.title_header);
        if (mActionBar != null)
        {
            mTitleBar.removeView(mActionBar);
        }

        mActionBar = TopBarUtils.createCustomActionBar(CreateMemberActivity_sm.this,
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                getString(R.string.search_add_oldman),
                getString(R.string.search_next),
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (!clickNect)
                        {
                            getIdentifyCode();
                        }
                        else
                        {
                            doSaveOld();
                        }
                    }
                });
        mTitleBar.addView(mActionBar);
    }

    /**
     * [保存老人卡信息]<BR>
     */
    private void doSaveOld()
    {
        mValidecode = mEditTextValidecode.getText().toString().trim();
        if (TextUtils.isEmpty(mValidecode))
        {
            showToast(getString(R.string.validecode_null));
            return;
        }
        else if (!mValidecode.equals(validCode))
        {
            showToast(getString(R.string.validecode_error));
            return;
        }
        if (mBtnRetryValide.isEnabled())
        {
            showToast(getString(R.string.validecode_timeout));
            return;
        }

        updateState(Constants.GET_DATA_START, Constants.SC_OK, TYPE_SAVE_OLD);
        if (HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            new AddOldTask().execute(Constants.Add_OLD_PERSON);
        }
        else
        {
            updateState(Constants.GET_DATA_FAIL,
                    Constants.NO_NETWORK,
                    TYPE_SAVE_OLD);
        }
    }

    /**
     * 初始化界面
     */
    private void initViews()
    {
        mEditTextName = (EditText) findViewById(R.id.et_nick_name);
        mEditTextTel = (EditText) findViewById(R.id.et_telphone_number);
//        mEditTextDeviceId = (EditText) findViewById(R.id.et_phone_id);
//        mLayoutValidecode = (LinearLayout) findViewById(R.id.valid_code);
//        mEditTextValidecode = (EditText) findViewById(R.id.et_valide_code);
//        mRetryValide = (LinearLayout) findViewById(R.id.retry_valid);
//        mBtnRetryValide = (Button) findViewById(R.id.btn_retry_identify);
//        mBtnRetryValide.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                getIdentifyCode();
//            }
//        });
//        mBtnRetryValide.setText(R.string.validecode_getting);

    }

    @Override
    protected void onDestroy()
    {
        clearTimer();
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("resultCode", resultCode + "#");
    }

    /**
     * [onResume]<BR>
     * @see Activity#onResume()
     */
    protected void onResume()
    {
        MobclickAgent.onPageStart("AddOlderActivity");
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        MobclickAgent.onPageEnd("AddOlderActivity");
        MobclickAgent.onPause(this);
        super.onPause();
    }

    private void getIdentifyCode()
    {
        TLog.Log("--getIdentifyCode--");
        mBtnRetryValide.setEnabled(false);
        mBtnRetryValide.setText(R.string.validecode_getting);

        mOldAlias = mEditTextName.getText().toString().trim();
        if (mOldAlias.equals(""))
        {
            showToast(R.string.info_nickname_null);
            return;
        }

        mOldPhone = mEditTextTel.getText().toString().trim();
        if (mOldPhone.equals(""))
        {
            showToast(R.string.user_name_not_null);
            return;
        }

        if (mOldPhone.length() != 11)
        {
            showToast(R.string.info_phonenumber_error);
            return;
        }

        mOldDeviceId = mEditTextDeviceId.getText().toString().trim();
        if (mOldDeviceId.equals(""))
        {
            showToast(R.string.device_code_null);
            return;
        }
        updateState(Constants.GET_DATA_START, Constants.SC_OK, TYPE_VALIDE_OLD);
        if (HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            new AddOldTask().execute(Constants.VALID_OLD_PERSON);
        }
        else
        {
            updateState(Constants.GET_DATA_FAIL,
                    Constants.NO_NETWORK,
                    TYPE_VALIDE_OLD);
        }
    }

    /**
     * [添加老人处理类]<BR>
     * 添加老人处理类
     * @author archermind
     * @version [ODP Client R001C01LAI141, 2015年2月9日]
     */
    class AddOldTask extends AsyncTask<String, Void, String>
    {
        private int mTaskType = -1;

        @Override
        protected String doInBackground(String... params)
        {
            String action = params[0];
            if (action.equals(Constants.VALID_OLD_PERSON))
            {
                mTaskType = TYPE_VALIDE_OLD;
            }
            else
            {
                mTaskType = TYPE_SAVE_OLD;
            }
            String result = UserInfoUtil.doAddOld(CreateMemberActivity_sm.this,
                    action,
                    mOldDeviceId,
                    mOldPhone,
                    mOldAlias);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if (null == result || TextUtils.isEmpty(result))
            {
                TLog.Log("AddOldTask----error----else");
                updateState(Constants.GET_DATA_FAIL,
                        Constants.JSON_ERROR,
                        mTaskType);
                return;
            }
            boolean isCode = false;
            int resultcode = Constants.UNKNOW_RESULT;
            try
            {
                resultcode = Integer.parseInt(result);
                isCode = true;
                TLog.Log("AddOldTask----error----code:" + result);
            }
            catch (NumberFormatException e)
            {
                isCode = false;
            }
            if (isCode)
            {
                TLog.Log("AddOldTask----error----" + result);
                updateState(Constants.GET_DATA_FAIL, resultcode, mTaskType);
            }
            else
            {
                if (mTaskType == TYPE_VALIDE_OLD)
                {
                    if (updateOldInfo(result))
                    {
                        TLog.Log("AddOldTask----success----" + result);
                        updateState(Constants.GET_DATA_SUCCESS,
                                Constants.SC_OK,
                                mTaskType);
                    }
                    else
                    {
                        TLog.Log("AddOldTask----fail----" + result);
                        updateState(Constants.GET_DATA_FAIL,
                                Constants.JSON_ERROR,
                                mTaskType);
                    }
                }
                else
                {
                    TLog.Log("saveOldTask----success----" + result);
                    updateState(Constants.GET_DATA_SUCCESS,
                            Constants.SC_OK,
                            mTaskType);
                }
            }
        }
    }

    private void initTimer()
    {
        mTimerTask = new TimerTask()
        {

            @Override
            public void run()
            {
                Message msg = new Message();
                msg.what = CHANGE_TIMER_MESSAGE;
                mHandler.sendMessage(msg);
            }
        };
        mTimer = new Timer();
    }

    public boolean updateOldInfo(String result)
    {
        try
        {
            JSONObject json = new JSONObject(result);
            JSONObject data = json.getJSONObject("data");
            String flag = data.getString("flag");
            if (flag.equals("success"))
            {
                mActionBar.setTvRightMsg(getString(R.string.common_btn_save));
                clickNect = true;
                mLayoutValidecode.setVisibility(View.VISIBLE);
                mEditTextValidecode.requestFocus();
                mEditTextName.setEnabled(false);
                mEditTextTel.setEnabled(false);
                mEditTextDeviceId.setEnabled(false);
                validCode = data.getString("code");
                mRetryValide.setVisibility(View.VISIBLE);
                //click button to set 120s back
                mTimerRecord = COUNT_GET_INDENTIFY_CODE;
                if (mTimer == null || mTimerTask == null)
                {
                    initTimer();
                    mTimer.schedule(mTimerTask, 0, 1000);
                }
                return true;
            }
            else if (flag.equals("notexist"))
            {
                showToast(R.string.device_unsupport);
            }
            else if (flag.equals("used"))
            {
                showToast(R.string.device_exist);
            }
            else if (flag.equals("codeerror"))
            {
                showToast(R.string.validecode_get_fail);
            }
            else
            {
                showToast(R.string.unknown_error);
            }
            mBtnRetryValide.setText(getString(R.string.validecode_get));
            mBtnRetryValide.setEnabled(true);
            return true;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            TLog.Log("--updateOldInfo error--" + e.toString());
            return false;
        }
    }

    /**
     * show toast<BR>
     * @param msg alert info
     */
    private void showToast(String msg)
    {
        if (mToast == null)
        {
            mToast = Toast.makeText(getApplicationContext(),
                    msg,
                    Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     show toast<BR>
     * @param msgid alert string id
     */
    private void showToast(int msgid)
    {
        showToast(getString(msgid));
    }

    private void clearTimer()
    {
        if (mTimerTask != null)
        {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
        }
    }

}
