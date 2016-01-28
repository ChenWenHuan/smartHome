package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.model.retrofitServices.ApiService;
import com.smarthome.client2.model.retrofitServices.ServiceGenerator;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import com.squareup.okhttp.ResponseBody;

import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 2015/12/7.
 */
public class SmartPhoneAddStepOne extends Activity implements View.OnClickListener {

    private final static String TAG = "SmartPhoneAddStepOne";
    private final static int VALICODE_WAITING_COUNT = 120;
    private final static int MSG_ID_GET_VALICODE = 0x800001;
    private FrameLayout fl_header;
    private CustomActionBar actionBar;

    private ImageView imgContacts;

    private EditText edtUserTelNum;
    private TextView tvGetValiCode;
    private EditText edtValiCode;
    private Button btAddConfirm;
    private String familyId;
    private int mCurrentWaitingCount = VALICODE_WAITING_COUNT;
    private String addTelNum;
    private String validateCode;
    private ServiceGenerator serviceGenerator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.smart_phoe_add_step_one_main);
        familyId = getIntent().getStringExtra("groupid");
        initView();
    }

    private void initView() {

        edtUserTelNum = (EditText) findViewById(R.id.et_account_tel_num);
        tvGetValiCode = (TextView) findViewById(R.id.tv_send_validate_code);
        tvGetValiCode.setOnClickListener(this);
        edtValiCode = (EditText) findViewById(R.id.et_vali_code);
        btAddConfirm = (Button) findViewById(R.id.bt_confirm_next);
        btAddConfirm.setOnClickListener(this);
        imgContacts = (ImageView) findViewById(R.id.img_contact);
        imgContacts.setOnClickListener(this);
        addTopBarToHead();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_send_validate_code:
                getValidcode();
                break;
            case R.id.bt_confirm_next:
                addConfirmClick();
                break;
            case R.id.img_contact:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }

    }

    private void addConfirmClick() {

        addTelNum = edtUserTelNum.getEditableText().toString().trim();
        validateCode = edtValiCode.getEditableText().toString().trim();
        if (TextUtils.isEmpty(addTelNum) || TextUtils.isEmpty(validateCode)) {
            Toast.makeText(SmartPhoneAddStepOne.this,
                    ("对方账号或验证码不能为空,请输入！"),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        addSmartPhoneAccount();
    }

    private void getValidcode() {

        if (mCurrentWaitingCount != VALICODE_WAITING_COUNT) {
            return;
        }
        addTelNum = edtUserTelNum.getEditableText().toString().trim();
        if(TextUtils.isEmpty(addTelNum)){
            Toast.makeText(SmartPhoneAddStepOne.this,("对方手机号不能为空,请输入！"),Toast.LENGTH_SHORT).show();
            return;
        }
        //发送网络获取验证码的命令
        handler.sendEmptyMessage(MSG_ID_GET_VALICODE);
        sendValidcode(addTelNum);

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case MSG_ID_GET_VALICODE:
                    mCurrentWaitingCount--;
                    if (mCurrentWaitingCount > 0) {
                        handler.sendEmptyMessageDelayed(MSG_ID_GET_VALICODE, 1000);
                        tvGetValiCode.setText(Integer.toString(mCurrentWaitingCount));
                    } else {
                        mCurrentWaitingCount = VALICODE_WAITING_COUNT;
                        tvGetValiCode.setText("发送验证码");
                    }
                    break;

                case Constants.GET_DATA_SUCCESS:
                    break;

                case Constants.GET_DATA_FAIL:
                    if(msg.arg1 == 7){
                        showErrorMsgDialog("发送失败","此为非智能机账号，无法接收验证码, 请到首页添加关注！", true);
                        handler.removeCallbacksAndMessages(null);
                        mCurrentWaitingCount = VALICODE_WAITING_COUNT;
                        tvGetValiCode.setText("发送验证码");
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private Handler handler = new MyHandler();

    private void addTopBarToHead() {
        fl_header = (FrameLayout) findViewById(R.id.fl_header);

        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                "添加智能机",
                null,
                null);

        fl_header.addView(actionBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                String phoneNum = getContactPhoneNum(data);
                edtUserTelNum.setText(phoneNum);
                break;
            default:
                break;
        }
    }

    private String getContactPhoneNum(Intent data) {
        String telnum = "";
        //ContentProvider展示数据类似一个单个数据库表
        //ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
        ContentResolver reContentResolverol = getContentResolver();
        //URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
        Uri contactData = data.getData();
        if (contactData == null) {
            return "请在此输入";
        }
        //查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
        Cursor cursor = SmartPhoneAddStepOne.this.getContentResolver().query(contactData, null, null, null, null);
        cursor.moveToFirst();
        //条件为联系人ID
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
        Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                null,
                null);
        while (phone.moveToNext()) {
            telnum = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        return telnum;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void addSmartPhoneAccount() {

        serviceGenerator = ServiceGenerator.getInstance(this);
        ApiService.AddSmartPhoneAccountParameters parameters = new ApiService.AddSmartPhoneAccountParameters();
        parameters.setTelNum(addTelNum);
        parameters.setMaintotarAlias(addTelNum);
        parameters.setValidCode(validateCode);
        parameters.setGroupId(familyId);
        Call call = serviceGenerator.getApiService().addSmartPhoneAccount(parameters);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    String result = response.body().string();
                    Log.e(TAG, "add result=" + result);
                    try{
                        JSONObject json = new JSONObject(result);
                        String retMsg = "";
                        int resultcode = json.getInt("retcode");
                        if(resultcode == 200){
                            String newOrOld = json.getString("data");
                            if(newOrOld.equals("01")){
                                // 搜索添加成功
                                Toast.makeText(SmartPhoneAddStepOne.this,("添加成功！"),Toast.LENGTH_SHORT).show();
                                SmartPhoneAddStepOne.this.finish();
                            }else if (newOrOld.equals("02")){
                                // 创建添加成功
                                showErrorMsgDialog("添加成功","账号和密码已通过短信发给新成员！", true);
                            }
                        }else if (resultcode == 6){
                            // 存在在其他家庭，可以通过关注，添加
                            showErrorMsgDialog("添加失败","此成员已加入其他家庭，您可以将其加入关注列表！", false);
                        }else{
                            retMsg = json.getString("retmsg");
                            showErrorMsgDialog("添加失败", "retMsg", false);
                        }
                    }catch (JSONException e){

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void showErrorMsgDialog(String title, String errMsg, boolean isClose){

        AlertDialog.Builder builder = new AlertDialog.Builder(SmartPhoneAddStepOne.this);
        builder.setMessage(errMsg);
        builder.setTitle(title);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(isClose){
                    SmartPhoneAddStepOne.this.finish();
                }
            }
        });

        builder.create().show();

    }

    private void sendValidcode(String telNum) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("telNum", telNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,Constants.GET_VALID_CODE_FOR_SM,handler,Constants.GET_DATA_SUCCESS,Constants.GET_DATA_FAIL);
    }
}