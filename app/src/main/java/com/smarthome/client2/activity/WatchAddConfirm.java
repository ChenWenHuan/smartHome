package com.smarthome.client2.activity;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.zxing.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class WatchAddConfirm extends Activity implements OnClickListener{
	
	public static final String  QRCODE_ACTION = "add by qrcode";
    public static final String  ID_ACTION = "add by id";
    public static final String  ADD_WATCH_SUCCESS = "com.smarthome.client2.activity.WatchAddConfirm.sucess";

    private FrameLayout fl_header;
    private CustomActionBar actionBar;
    private ProgressDialog mProgressBar;
    private Button btnConfirm;
    private EditText edtWatchTelNum;
    private LinearLayout llWatchQrcode;
    private LinearLayout llWatchId;
    private LinearLayout llWatchKey;
    private ImageView imgQrIcon;
    private TextView tvWatchQrcode;
    private EditText edtWatchId;
    private EditText edtWatchKey;
    private String watchTelNum;
    private String watchQrcode;
    private String watchId;
    private String watchKey;
    private String familyId;
    private String actionCommand;
    private String userTelNum;
    private boolean isLinkAccount = false;
    private boolean isWatchBinded = false;
    private int mTryBindCount = 2;
    private String returnWatchDeviceID = "";
    private LinkTopSDKUtil instance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.add_watch_confirm_main);
        initData();
	    initView();
        initLinkTopUserAccount();

	}

    private void initLinkTopUserAccount(){
        instance = LinkTopSDKUtil.getInstance();
        instance.initSDK(WatchAddConfirm.this, handlerWatchData);
        if (SmartHomeApplication.getInstance().isLinkTopAccout){
            instance.setupAccount(userTelNum, "888888");
            instance.loginToken();
            isLinkAccount = true;
        }
    }

    private void initData(){

        actionCommand = this.getIntent().getAction();
        watchTelNum = this.getIntent().getStringExtra("watchTelNum");
        familyId = this.getIntent().getStringExtra("familyId");
        userTelNum = Preferences.getInstance(this.getApplicationContext()).getUserTelNum();
        btnConfirm = (Button)this.findViewById(R.id.bt_watch_next);
        btnConfirm.setOnClickListener(this);
        imgQrIcon = (ImageView)this.findViewById(R.id.img_qrcode);
        btnConfirm.setOnClickListener(this);
        if(actionCommand.equals(QRCODE_ACTION)){
            watchQrcode = this.getIntent().getStringExtra("watchQrcode");
        }else if (actionCommand.equals(ID_ACTION)){
            watchId = this.getIntent().getStringExtra("watchid");
            watchKey = this.getIntent().getStringExtra("watchkey");
        }

    }

    private void initView(){

        btnConfirm = (Button)this.findViewById(R.id.bt_watch_next);
        edtWatchTelNum = (EditText)this.findViewById(R.id.et_watch_tel_num);
        edtWatchTelNum.setText(watchTelNum);
        llWatchQrcode = (LinearLayout)this.findViewById(R.id.ll_watch_qrcode);
        llWatchId = (LinearLayout)this.findViewById(R.id.ll_watch_id);
        llWatchKey = (LinearLayout)this.findViewById(R.id.ll_watch_key);
        tvWatchQrcode = (TextView)this.findViewById(R.id.tv_qr_value);
        imgQrIcon = (ImageView)this.findViewById(R.id.img_qrcode);
        edtWatchId = (EditText)this.findViewById(R.id.et_watch_id);
        edtWatchKey = (EditText)this.findViewById(R.id.et_watch_key);
        if(actionCommand.equals(QRCODE_ACTION)){
            tvWatchQrcode.setText(watchQrcode);
            llWatchId.setVisibility(View.GONE);
            llWatchKey.setVisibility(View.GONE);
        }else if (actionCommand.equals(ID_ACTION)){
            llWatchQrcode.setVisibility(View.GONE);
            edtWatchId.setText(watchId);
            edtWatchKey.setText(watchKey);
        }
        addTopBarToHead();
        mProgressBar = new ProgressDialog(WatchAddConfirm.this);
        mProgressBar.setCanceledOnTouchOutside(false);
    }
	
    private void addTopBarToHead() {
    	fl_header = (FrameLayout) findViewById(R.id.fl_header);

        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                "手表绑定-确认",
                null,
                null);

        fl_header.addView(actionBar);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bt_watch_next:
                btnWatchNextClick();
                break;
            case R.id.img_qrcode:
                imgWatchQrcode();
                break;
            default:
                break;
        }

    }

    private void btnWatchNextClick(){

        watchId = edtWatchId.getEditableText().toString().trim();
        watchKey = edtWatchKey.getEditableText().toString().trim();
        watchTelNum = edtWatchTelNum.getEditableText().toString().trim();
        watchQrcode = tvWatchQrcode.getText().toString().trim();

        if (isLinkAccount){
            //进行绑定
            if (actionCommand.equals(QRCODE_ACTION)){
                instance.bindDevice(watchQrcode, watchTelNum);
            }else if (actionCommand.equals(ID_ACTION)){
                if (!watchId.equals("") && !watchKey.equals("")){
                    instance.bindDeviceByID(watchId, watchTelNum, watchKey);
                }else{
                    Toast.makeText(WatchAddConfirm.this,
                            "手表ID和AKey不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }else{
            //激活账号
            instance.registerAccount(userTelNum, "888888");
        }
        handlerWatchData.sendEmptyMessage(Constants.GET_DATA_START);
    }


    private void sendAddWatchSucessBroadcastMsg(){

        //通知其他界面，创建手表成功
        Intent intent = new Intent(WatchAddConfirm.ADD_WATCH_SUCCESS);
        WatchAddConfirm.this.sendBroadcast(intent);
    }

    private Handler handlerWatchData = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    mProgressBar.setMessage("正在创建成员并绑定手表...");
                    mProgressBar.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    mProgressBar.dismiss();
                    isWatchBinded = true;
                    Toast.makeText(WatchAddConfirm.this,"手表绑定成功！",Toast.LENGTH_SHORT).show();
                    SmartHomeApplication.getInstance().getMainActivity().setRefreshHomeFragment(true);
                    sendAddWatchSucessBroadcastMsg();
                    WatchAddConfirm.this.finish();

                    break;

                case Constants.GET_LINK_ACCOUNT:
                    mProgressBar.setMessage(getResources().getString(R.string.validecode_getting));
                    mProgressBar.show();
                    break;
                case Constants.GET_DATA_FAIL:
                    if (mProgressBar != null && mProgressBar.isShowing()){
                        mProgressBar.dismiss();
                    }
                    mTryBindCount = 2;
                    isWatchBinded = false;
                    String errMsg = msg.obj.toString() + "请确认上述步骤是否输入正确，然后重试！";
                    showErrorMsgDialog(errMsg);
                    break;
                case LinkTopSDKUtil.LINK_SDK_LOGIN_TOKEN:
                    if (msg.arg1 != 200){
                        Log.e("watch bind", "link top account login error !!!");
                        sendErrorMsg("手表服务器账号错误，");
                    }
                    break;

                case LinkTopSDKUtil.LINK_SDK_REGISTER_ACCOUNT_ACTION:
                    if (msg.arg1 == 0){
                        SmartHomeApplication.getInstance().isLinkTopAccout = true;
                        isLinkAccount = true;
                        if(!SmartHomeApplication.getInstance().jiTuiRegisterID.equals("")){
                            instance.registerPushParam(SmartHomeApplication.getInstance().jiTuiRegisterID);
                        }
                        if (actionCommand.equals(QRCODE_ACTION)){
                            instance.bindDevice(watchQrcode, watchTelNum);
                        }else if (actionCommand.equals(ID_ACTION)){
                            instance.bindDeviceByID(watchId, watchTelNum, watchKey);
                        }
                    }else{
                        if (mProgressBar != null && mProgressBar.isShowing()){
                            mProgressBar.dismiss();
                        }
                        sendErrorMsg("手表服务器账号错误");
                    }
                    break;
                case Constants.WATCH_DEVICE_BINDED:
                    sendErrorMsg("此设备已被绑定，");
                    break;
                case Constants.WATCH_DEVICES_LIST:
                    mTryBindCount--;
                    instance.getDevices();
                    break;
                case LinkTopSDKUtil.LINK_SDK_LIST_DEVICES:
                    if(msg.arg1 == 200){
                        if(msg.obj.toString().contains(returnWatchDeviceID)){
                            doAddWatchMem();
                            return;
                        }
                    }
                    if (mTryBindCount > 0){
                        handlerWatchData.sendEmptyMessageDelayed(Constants.WATCH_DEVICES_LIST, 40000);
                    }else {
                        mTryBindCount = 2;
                        sendErrorMsg("绑定超时,请确认手表号是否正确，2分钟后");
                    }
                    break;

                case Constants.WATCH_DEVICE_BINDED_AGAIN:
                    mTryBindCount--;
                    if (actionCommand.equals(QRCODE_ACTION)){
                        instance.bindDevice(watchQrcode, watchTelNum);
                    }else if (actionCommand.equals(ID_ACTION)){
                        instance.bindDeviceByID(watchId, watchTelNum, watchKey);
                    }
                    break;

                case LinkTopSDKUtil.LINK_SDK_BIND_DEVICE_ACTION:
                    if (msg.arg1 != 200){
                        handlerWatchData.sendEmptyMessage(Constants.GET_DATA_FAIL);
                    }else{
                        if (msg.obj != null){
                            try {
                                JSONObject jsonObject = new JSONObject(
                                        (String)msg.obj);
                                String state = jsonObject.optString("state");
                                returnWatchDeviceID = jsonObject.optString("id");

                                if (state.equals("") || state.equals("0")){
                                    doAddWatchMem();
                                }else if (state.equals("1")){//
                                    handlerWatchData.sendEmptyMessageDelayed(Constants.WATCH_DEVICES_LIST, 40000);
                                }
                                else if (state.equals("3")){
                                    handlerWatchData.sendEmptyMessage(Constants.WATCH_DEVICE_BINDED);
                                }else if (state.equals("4")) {
                                    mTryBindCount = 2;
                                    sendErrorMsg("二维码格式错误");
                                }else {
                                    mTryBindCount = 2;
                                    sendErrorMsg("请确认输入ID和KEY是否正确，");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void sendErrorMsg(String errMsg){

        Message failMsg = new Message();
        failMsg.what = Constants.GET_DATA_FAIL;
        failMsg.obj = errMsg;
        handlerWatchData.sendMessage(failMsg);
    }

    private void showErrorMsgDialog(String errMsg){

        AlertDialog.Builder builder = new AlertDialog.Builder(WatchAddConfirm.this);
        builder.setMessage(errMsg);
        builder.setTitle("添加失败");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
              }
            });

        builder.create().show();

    }

    private void doAddWatchMem(){

        if(isWatchBinded){
            return;
        }

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("telNum", watchTelNum);

            obj.put("acctType", "02");
            obj.put("groupId", familyId);
            obj.put("appellationCode", "000000");
            obj.put("deviceType", "6");
            obj.put("deviceCode", returnWatchDeviceID + ":" + userTelNum);
            obj.put("maintotarAlias", watchTelNum);
            obj.put("acctCode", "");
            obj.put("email", "");
            obj.put("devTelNum", watchTelNum);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.ADD_NEM_FAMILY_MEM_V11,
                handlerWatchData,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }


    private void imgWatchQrcode(){

        Intent intent = new Intent(WatchAddConfirm.this, CaptureActivity.class);
        WatchAddConfirm.this.startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == 0 && resultCode == RESULT_OK){
            watchQrcode = data.getStringExtra("qrcode");
            tvWatchQrcode.setText(watchQrcode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
