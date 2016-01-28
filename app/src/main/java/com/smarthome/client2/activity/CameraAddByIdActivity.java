package com.smarthome.client2.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

public class CameraAddByIdActivity extends Activity implements OnClickListener{
	
	public static final int UPDATE_FAMILY_DEVICES_MSG_ID = 0x79;
	public static final int ADD_FAMILY_DEVICES_MSG_ID = 0x80;
	public static final int ERROR_MSG_ID = 0x81;
    public static final String  ADD_CAMERA_SUCCESS = "com.smarthome.client2.activity.CameraAddConfirm.sucess";
	public static final String CAMERA_ADD_ACTION = "camera add";
	public static final String CAMERA_UPDATE_ACTION = "camera update";
	private FrameLayout fl_camera_add_head;
    private CustomActionBar actionBar;
    private Button btnConfirm;
    private EditText edtCameraName;
    private EditText edtCameraId;
    private EditText edtCameraUser;
    private EditText edtCameraPaswd;
    private String familyId;
    private String cameraName;
    private String cameraId;
    private String cameraUserName;
    private String cameraCameraPass;
    private String devId;
    private String title;
    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera_add_id_main);
        initView();
        
        if(this.getIntent().getAction().equals(CAMERA_UPDATE_ACTION)){
        	isUpdate = true;
        	devId = this.getIntent().getStringExtra(CameraDeviceList.CAMERA_DEV_ID);
        	cameraId = this.getIntent().getStringExtra(CameraDeviceList.CAMERA_CID_KEY);
        	cameraName = this.getIntent().getStringExtra(CameraDeviceList.CAMERA_SHOW_NAME_KEY);
        	cameraUserName = this.getIntent().getStringExtra(CameraDeviceList.CAMERA_USER_NAME_KEY);
        	cameraCameraPass = this.getIntent().getStringExtra(CameraDeviceList.CAMERA_USER_PASS_KEY);
        	title = "修改信息";
        }else{
        	familyId= this.getIntent().getStringExtra("familyid");
        	title = "添加摄像头";
        }
        initView();
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();	
	}

	private void initView(){
    	addTopBarToHead();
    	btnConfirm = (Button)this.findViewById(R.id.btn_camera_idadd_confirm);
    	btnConfirm.setOnClickListener(this);
    	edtCameraId = (EditText)this.findViewById(R.id.et_camera_id);
    	edtCameraName = (EditText)this.findViewById(R.id.et_camera_name);
    	edtCameraUser = (EditText)this.findViewById(R.id.et_camera_username);
    	edtCameraPaswd = (EditText)this.findViewById(R.id.et_camera_pass);
    	if(isUpdate){
    		edtCameraName.setText(cameraName);
    		edtCameraId.setText(cameraId);
    		edtCameraUser.setText(cameraUserName);
    		edtCameraPaswd.setText(cameraCameraPass);
    	}
    }

    
    private void addTopBarToHead()
    {
        fl_camera_add_head = (FrameLayout) findViewById(R.id.fl_header_home);
        if (actionBar != null)
        {
            fl_camera_add_head.removeView(actionBar);
        }
        actionBar = TopBarUtils.createCustomActionBar(SmartHomeApplication.getInstance(),
						R.drawable.btn_back_selector,
						new OnClickListener()
						{
							@Override
							public void onClick(View v){
								finish();
							}
						},
						title,
						"",
						null);

        fl_camera_add_head.addView(actionBar);
    }
    
    private boolean addCameraByID(){
    	
    	cameraName = edtCameraName.getEditableText().toString().trim();
    	cameraId = edtCameraId.getEditableText().toString().trim();
    	cameraUserName = edtCameraUser.getEditableText().toString().trim();
    	cameraCameraPass = edtCameraPaswd.getEditableText().toString().trim();
    	
    	if (cameraName.equals("") || cameraId.equals("") 
    			|| cameraUserName.equals("") || cameraCameraPass.equals("")){
    		Toast.makeText(CameraAddByIdActivity.this,
                    "上述信息不能为空，请确认！",
                    Toast.LENGTH_SHORT).show();
    		return false;
    		
    	}
    	return true;
    }
    
    private void addFamilyDeviceOfCamera(){
    	
        try {
        	JSONObject pJson = new JSONObject();
			pJson.put("famId", familyId);
			pJson.put("deviceType", "01");
			pJson.put("deviceCode", cameraId);
			pJson.put("telNum", "");
			pJson.put("ext1", cameraName);
			pJson.put("ext2", cameraUserName + ":" + cameraCameraPass);
			HttpUtil.postRequest(pJson,Constants.ADD_FAMILY_DEVICE, mHandler,ADD_FAMILY_DEVICES_MSG_ID,ERROR_MSG_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void updateFamilyDevideOfCamera(String devId){
    	
    	 try {
         	JSONObject pJson = new JSONObject();
 			pJson.put("devId", devId);
 			pJson.put("deviceType", "01");
 			pJson.put("deviceCode", cameraId);
 			pJson.put("telNum", "");
 			pJson.put("ext1", cameraName);
 			pJson.put("ext2", cameraUserName + ":" + cameraCameraPass);
 			HttpUtil.postRequest(pJson,Constants.UPDATE_FAMILY_DEVICE, mHandler,UPDATE_FAMILY_DEVICES_MSG_ID,ERROR_MSG_ID);
 		} catch (JSONException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    	
    }
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
            case ADD_FAMILY_DEVICES_MSG_ID:
            case UPDATE_FAMILY_DEVICES_MSG_ID:
            	handleCameraAddResult(msg.obj.toString());
            	break;
            case ERROR_MSG_ID:
            	Toast.makeText(CameraAddByIdActivity.this,"提交失败，请重试！",Toast.LENGTH_SHORT).show();
            	break;
            
            default:
                break;
            }
        }
    };
    
    private void handleCameraAddResult(String result){
    	
    	JSONObject jsonObj = null;
    	
    	try {
			jsonObj = new JSONObject(result);
			if(!isUpdate){
				devId = jsonObj.getString("data");
			}
            sendAddCameraSucessBroadcastMsg(isUpdate);
            CameraAddByIdActivity.this.finish();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    private void sendAddCameraSucessBroadcastMsg(boolean isUpdate){

        //通知其他界面，创建摄像头成功
        Intent intent = new Intent(CameraAddByIdActivity.ADD_CAMERA_SUCCESS);
        if(isUpdate){
            intent.setAction(CAMERA_UPDATE_ACTION);
        }else{
            intent.setAction(CAMERA_ADD_ACTION);
        }
        Bundle data = new Bundle();
        data.putString(CameraDeviceList.CAMERA_DEV_ID, devId);
        data.putString(CameraDeviceList.CAMERA_SHOW_NAME_KEY, cameraName);
        data.putString(CameraDeviceList.CAMERA_CID_KEY,cameraId);
        data.putString(CameraDeviceList.CAMERA_USER_NAME_KEY,cameraUserName);
        data.putString(CameraDeviceList.CAMERA_USER_PASS_KEY,cameraCameraPass);
        intent.putExtras(data);
        CameraAddByIdActivity.this.sendBroadcast(intent);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_camera_idadd_confirm:
			if(addCameraByID()){
				if(isUpdate){
					updateFamilyDevideOfCamera(devId);
				}else{
					addFamilyDeviceOfCamera();
				}
                return;
			}
			break;
		default:
			break;
		}
	}
}
