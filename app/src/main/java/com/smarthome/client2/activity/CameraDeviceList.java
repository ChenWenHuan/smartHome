package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.CameraInfoItem;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.ScreenUtils;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class CameraDeviceList extends Activity{
	
	public static String CAMERA_SHOW_NAME_KEY = "cameraShowName";
	public static String CAMERA_DEV_ID = "devid";
	public static String CAMERA_CID_KEY = "cid";
	public static String CAMERA_USER_NAME_KEY = "camerausername";
	public static String CAMERA_USER_PASS_KEY= "camerapass";
	public static final int DEL_FAMILY_DEVICE_MSG_ID = 0x000079;
	public static final int GET_FAMILY_DEVICES_MSG_ID = 0x000080;
	public static final int ERROR_MSG_ID = 0x000081;
	public static final int DEL_ERROR_MSG_ID = 0x000082;
	private FrameLayout mTitleBar;
	private CustomActionBar mActionBar;
	private LinearLayout llNoCamera;
	private List<CameraInfoItem> cameraList = new ArrayList<CameraInfoItem>();
	private CameraListAdapter listAdapter;
	private PopupWindow popupWindow;
	private ProgressDialog loadingDialog;
	private ListView  lvCamera;
	private String familyID;
	private Button btnAddByID;
	private Button btnAddByQrcode;
	private Button btnAddBySearch;
	private int delPosition = -1;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camera_list_main);
		llNoCamera = (LinearLayout)this.findViewById(R.id.ll_bind_nothing);

		lvCamera = (ListView)this.findViewById(R.id.lv_camera_list);
		listAdapter = new CameraListAdapter(this);
		lvCamera.setAdapter(listAdapter);
		familyID = this.getIntent().getStringExtra("familyid");
		addTopBarToHead();
        registerReceiver(addSucessRevcer, new IntentFilter(CameraAddByIdActivity.ADD_CAMERA_SUCCESS));
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage("设备信息加载中...");
		loadingDialog.show();
		getCameraListFromServer(familyID);
	}

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent){

            Bundle bundleData = intent.getExtras();
            if(intent.getAction().equals(CameraAddByIdActivity.ADD_CAMERA_SUCCESS)) {
                CameraInfoItem newItem = new CameraInfoItem();
                newItem.deviceid = bundleData.getString(CameraDeviceList.CAMERA_DEV_ID);
                newItem.cameraShowName = bundleData.getString(CameraDeviceList.CAMERA_SHOW_NAME_KEY);
                newItem.cameraID = bundleData.getString(CameraDeviceList.CAMERA_CID_KEY);
                newItem.cameraUserName = bundleData.getString(CameraDeviceList.CAMERA_USER_NAME_KEY);
                newItem.cameraPasswd = bundleData.getString(CameraDeviceList.CAMERA_USER_PASS_KEY);
                cameraList.add(0, newItem);
                listAdapter.notifyDataSetChanged();
                llNoCamera.setVisibility(View.GONE);
            }else{
                updateCameraDataByDevId(intent);
            }
        }
    };
	

	private void addTopBarToHead() {
		mTitleBar = (FrameLayout) findViewById(R.id.fl_header_bind);
		if (mActionBar != null) {
			mTitleBar.removeView(mActionBar);
		}
		mActionBar = TopBarUtils.createCustomActionBar(this,
				R.drawable.btn_back_selector,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						CameraDeviceList.this.finish();
					}
				},
				"家庭摄像头列表",
				R.drawable.popup_add,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
                        Intent  intent = new Intent(CameraDeviceList.this, CameraAddStepOne.class);
                        intent.putExtra("familyId", familyID);
                        CameraDeviceList.this.startActivity(intent);
					}
				});
		mTitleBar.addView(mActionBar);
		
		lvCamera.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CameraDeviceList.this, CameraMainActivity.class);
                Bundle data = new Bundle();
                data.putString(CameraDeviceList.CAMERA_SHOW_NAME_KEY, cameraList.get(position).cameraShowName);
                data.putString(CameraDeviceList.CAMERA_CID_KEY,cameraList.get(position).cameraID);
                data.putString(CameraDeviceList.CAMERA_USER_NAME_KEY,cameraList.get(position).cameraUserName);
                data.putString(CameraDeviceList.CAMERA_USER_PASS_KEY,cameraList.get(position).cameraPasswd);
                intent.putExtras(data);
                CameraDeviceList.this.startActivity(intent);
			}
		});
	}
	
	private void getCameraListFromServer(String familyID){
		
		JSONObject pJson = new JSONObject();
        try {
			pJson.put("famId", familyID);
			HttpUtil.postRequest(pJson,Constants.GET_FAMILY_DEVICES, mHandler,GET_FAMILY_DEVICES_MSG_ID,ERROR_MSG_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean handleCameraListResult(String result){
		
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(result);
			JSONArray cameraArray = jsonObj.getJSONArray("data");
			if(cameraArray == null){
				return false;
			}else{
				for(int i=0; i<cameraArray.length(); i++){
					
					JSONObject obj = cameraArray.getJSONObject(i);
					String devType = obj.getString("devType");
					if(devType.equals("01")){
						CameraInfoItem cameraItem = new CameraInfoItem();
						cameraItem.deviceid = obj.getString("id");
						cameraItem.cameraID = obj.getString("devCode");
						cameraItem.cameraShowName = obj.getString("ext1");
						cameraItem.cameraUserName = obj.getString("ext2").split(":")[0];
						cameraItem.cameraPasswd = obj.getString("ext2").split(":")[1];
						cameraList.add(cameraItem);
					}
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return cameraList.size() > 0;
        
	}
	
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
            case GET_FAMILY_DEVICES_MSG_ID:
            	loadingDialog.dismiss();
            	if (handleCameraListResult(msg.obj.toString())){
            		llNoCamera.setVisibility(View.GONE);
            		listAdapter.notifyDataSetChanged();
            	}else{
            		llNoCamera.setVisibility(View.VISIBLE);
            	}
            	break;
            case DEL_FAMILY_DEVICE_MSG_ID:
            	cameraList.remove(delPosition);
            	listAdapter.notifyDataSetChanged();if(cameraList.size() > 0){
        			llNoCamera.setVisibility(View.GONE);
        		}else{
        			llNoCamera.setVisibility(View.VISIBLE);
        		}
            	break;
            case ERROR_MSG_ID:
            	loadingDialog.dismiss();
            	break;
            case DEL_ERROR_MSG_ID:
            	Toast.makeText(CameraDeviceList.this,"删除失败，请重试！",Toast.LENGTH_SHORT).show();
            	delPosition = -1;
            	break;
            default:
                break;
            }
        }
    };
    
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public class CameraListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public CameraListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return cameraList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.camera_list_item, null);
				holder.tvCameraShowName = (TextView) convertView.findViewById(R.id.tv_camera_name);
				holder.tvCameraConnectedStatus = (TextView) convertView.findViewById(R.id.tv_camera_status);
				holder.imgMoreFunction = (ImageView) convertView.findViewById(R.id.img_fun_more);
				holder.btnCameraEdit = (Button) convertView.findViewById(R.id.btn_camera_edit);
				holder.btnCameraDel = (Button) convertView.findViewById(R.id.btn_camera_del);
				holder.llCameraFunDetail = (LinearLayout) convertView.findViewById(R.id.ll_camera_edit);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tvCameraShowName.setText(cameraList.get(position).cameraShowName);
			if(cameraList.get(position).isCameraConnected){
				holder.tvCameraConnectedStatus.setText("状态：已连接");
			}else{
				holder.tvCameraConnectedStatus.setText("状态：已连接");
			}
			CameraFunClick cameraFunClick = new CameraFunClick(holder.imgMoreFunction,holder.llCameraFunDetail,position);
			holder.imgMoreFunction.setOnClickListener(cameraFunClick);
			holder.btnCameraEdit.setOnClickListener(cameraFunClick);
			holder.btnCameraDel.setOnClickListener(cameraFunClick);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
	}
	
	public class ViewHolder{
		public TextView  tvCameraShowName;
		public TextView  tvCameraConnectedStatus;
		public ImageView imgMoreFunction;
		public LinearLayout  llCameraFunDetail;
		public Button btnCameraEdit;
		public Button btnCameraDel;
	}
	
	private class CameraFunClick   implements OnClickListener{
        public ImageView imgFunMore;
        public LinearLayout llFunsDetail;
        public int positon;
        
        CameraFunClick(ImageView imgView, LinearLayout llDetail, int postion){
        	this.imgFunMore = imgView;
        	this.llFunsDetail = llDetail;
        	this.positon = postion;
        }
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			switch(v.getId()){
			case R.id.img_fun_more:
				if (View.VISIBLE == llFunsDetail.getVisibility()){
					llFunsDetail.setVisibility(View.GONE);
					imgFunMore.setImageResource(R.drawable.icon_blue);
				}else{
					llFunsDetail.setVisibility(View.VISIBLE);
					imgFunMore.setImageResource(R.drawable.umeng_update_close_bg_normal);
				}
				break;
			case R.id.btn_camera_edit:
				intent = new Intent(CameraDeviceList.this,CameraAddByIdActivity.class);
				intent.setAction(CameraAddByIdActivity.CAMERA_UPDATE_ACTION);
				intent.putExtra(CameraDeviceList.CAMERA_DEV_ID, cameraList.get(positon).deviceid);
				intent.putExtra(CameraDeviceList.CAMERA_SHOW_NAME_KEY, cameraList.get(positon).cameraShowName);
				intent.putExtra(CameraDeviceList.CAMERA_CID_KEY,cameraList.get(positon).cameraID);
				intent.putExtra(CameraDeviceList.CAMERA_USER_NAME_KEY,cameraList.get(positon).cameraUserName);
				intent.putExtra(CameraDeviceList.CAMERA_USER_PASS_KEY,cameraList.get(positon).cameraPasswd);
				startActivityForResult(intent, 1);
				if (View.VISIBLE == llFunsDetail.getVisibility()){
					llFunsDetail.setVisibility(View.GONE);
					imgFunMore.setImageResource(R.drawable.icon_blue);
				}
				break;
			case R.id.btn_camera_del:
				delCameraDevice(cameraList.get(positon).deviceid);
				delPosition = positon;
				if (View.VISIBLE == llFunsDetail.getVisibility()){
					llFunsDetail.setVisibility(View.GONE);
					imgFunMore.setImageResource(R.drawable.icon_blue);
				}
				break;
			default:
				break;
			}
			
		}
	}
	
	private void delCameraDevice(String deviceId){
		
		 try {
	         	JSONObject pJson = new JSONObject();
	 			pJson.put("devId", deviceId);
	 			HttpUtil.postRequest(pJson,Constants.DEL_FAMILY_DEVICE, mHandler,DEL_FAMILY_DEVICE_MSG_ID,DEL_ERROR_MSG_ID);
	 		} catch (JSONException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if ( resultCode!= RESULT_CANCELED) {
            if(requestCode == 0){
            	Bundle bundleData = intent.getExtras();
            	CameraInfoItem newItem = new CameraInfoItem();
            	newItem.deviceid = bundleData.getString(CameraDeviceList.CAMERA_DEV_ID);
            	newItem.cameraShowName = bundleData.getString(CameraDeviceList.CAMERA_SHOW_NAME_KEY);
            	newItem.cameraID = bundleData.getString(CameraDeviceList.CAMERA_CID_KEY);
            	newItem.cameraUserName = bundleData.getString(CameraDeviceList.CAMERA_USER_NAME_KEY);
            	newItem.cameraPasswd = bundleData.getString(CameraDeviceList.CAMERA_USER_PASS_KEY);
        		cameraList.add(0,newItem);
        		listAdapter.notifyDataSetChanged();		
        		llNoCamera.setVisibility(View.GONE);
            }else if (requestCode == 1){
            	updateCameraDataByDevId(intent);
            }
        }
    }
	 
	private void updateCameraDataByDevId(Intent intent){
		
		Bundle bundleData = intent.getExtras();
		String devId = bundleData.getString(CameraDeviceList.CAMERA_DEV_ID);
		for (int i=0; i<cameraList.size(); i++){
			if(cameraList.get(i).deviceid.equals(devId)){				
				cameraList.get(i).cameraShowName = bundleData.getString(CameraDeviceList.CAMERA_SHOW_NAME_KEY);
				cameraList.get(i).cameraID = bundleData.getString(CameraDeviceList.CAMERA_CID_KEY);
				cameraList.get(i).cameraUserName = bundleData.getString(CameraDeviceList.CAMERA_USER_NAME_KEY);
				cameraList.get(i).cameraPasswd = bundleData.getString(CameraDeviceList.CAMERA_USER_PASS_KEY);
				listAdapter.notifyDataSetChanged();		
			}
		}
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(addSucessRevcer);
    }
}
