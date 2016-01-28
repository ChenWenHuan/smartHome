package com.smarthome.client2.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.MySoftInputUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DeviceFunctionWatchingCall_sm extends BaseActivity {
	
	private FrameLayout fl_header_device_function_watching;
	private CustomActionBar actionBar;
	
	/**
	 * 用户id
	 */
	private String mUserId = "";
	private String mStrDeviceCode = "";
	private String mStringPhone = "";
	
	private long mNDevId;
	private int mNWatchOrCall;
	
	private String mStrDeviceId = "";
	private String mStrTitle;
	
	/**
	 * 监听
	 */
	private boolean isWatch = false;
	
	private LinearLayout family_detail_edit_dialog;
	
	private EditText family_detail_dialog_edit;
	
	private TextView family_detail_edit_title;
	
	private Button family_detail_edit_layout_btn_1,
			family_detail_edit_layout_btn_2;
	private final static int MONITOR = 3;
	private final static int CALL = 4;
	
	private NetStatusListener mNetStatusListener;
	private ProgressDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.e_device_function_waching_call);

		Bundle bundle = getIntent().getExtras();
		mUserId = bundle.getString("userId");
		mStrDeviceCode = bundle.getString("deviceCode");
		mNDevId = bundle.getLong("devId");
		mNWatchOrCall = bundle.getInt("wachingOrCall"); // 1: watching : 0:calling

		family_detail_edit_dialog = (LinearLayout) findViewById(R.id.family_detail_edit_dialog);
		family_detail_dialog_edit = (EditText) findViewById(R.id.family_detail_dialog_edit);
		family_detail_edit_title = (TextView) findViewById(R.id.family_detail_edit_title);
		family_detail_edit_layout_btn_1 = (Button) findViewById(R.id.family_detail_edit_layout_btn_1);
		family_detail_edit_layout_btn_2 = (Button) findViewById(R.id.family_detail_edit_layout_btn_2);
		family_detail_edit_layout_btn_1.setOnClickListener(watcherListener);
		family_detail_edit_layout_btn_2.setOnClickListener(watcherListener);
		if (mNWatchOrCall == 1) {
			mStrTitle = "监听设备";
			openWatcherDialog();
		} else {
			callPhone();
			mStrTitle = "呼叫设备";
		}
		addTopBarToHead();
		
	}
	
	private void addTopBarToHead() {
		fl_header_device_function_watching = (FrameLayout) findViewById(R.id.fl_header_device_fuction_watching_call);

		actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
				R.drawable.btn_back_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				},
				mStrTitle,
				null,
				null);
		
		fl_header_device_function_watching.addView(actionBar);
	}
	
	/**
	 * 监听
	 */
	
	private void openWatcherDialog() {
		family_detail_edit_title.setText("请输入需要监听设备的号码");
		family_detail_dialog_edit.setText("");
		family_detail_dialog_edit.setHint("输入需要监听的号码");
		family_detail_edit_dialog.setVisibility(View.VISIBLE);
		family_detail_edit_layout_btn_1.setText("发起监听请求");
		isWatch = true;
	}
	
	/**
	 * 打电话
	 */
	private void callPhone() {
		family_detail_edit_title.setText("请输入设备需要呼叫的号码");
		family_detail_dialog_edit.setText("");
		family_detail_dialog_edit.setHint("输入需要拨打的号码");
		family_detail_edit_dialog.setVisibility(View.VISIBLE);
		family_detail_edit_layout_btn_1.setText("拨打电话");
		isWatch = false;
	}
	
	
	private OnClickListener watcherListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.family_detail_edit_layout_btn_1:
					if (TextUtils.isEmpty(family_detail_dialog_edit.getText()
							.toString()
							.trim())) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.common_phone_isnull),
								Toast.LENGTH_SHORT).show();
						NetStatusListener.mClickflag = false;
						return;
					}
					
					if (family_detail_dialog_edit.getText()
							.toString()
							.trim()
							.length() < 3
							|| family_detail_dialog_edit.getText()
							.toString()
							.length() > 11) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.common_phone_valid),
								Toast.LENGTH_SHORT).show();
						NetStatusListener.mClickflag = false;
						return;
					}
					if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
						Toast.makeText(getApplicationContext(),
								HttpUtil.responseHandler(getApplicationContext(),
										Constants.NO_NETWORK),
								Toast.LENGTH_SHORT)
								.show();
						NetStatusListener.mClickflag = false;
						return;
					}
					if (isWatch) {
						monitorFromServer(mNDevId,
								family_detail_dialog_edit.getText().toString());
					} else {
						callPhoneFromServer(mNDevId,
								family_detail_dialog_edit.getText()
										.toString()
										.trim());
					}
					//family_detail_edit_dialog.setVisibility(View.GONE);
					MySoftInputUtil.hideInputMethod(getApplicationContext(),
							family_detail_edit_dialog);
					break;
				case R.id.family_detail_edit_layout_btn_2:
					//family_detail_edit_dialog.setVisibility(View.GONE);
					MySoftInputUtil.hideInputMethod(getApplicationContext(),
							family_detail_edit_dialog);
					NetStatusListener.mClickflag = false;
					break;
			}
		}
	};
	
	/**
	 * 远程监听
	 */
	private void monitorFromServer(long deviceId, String phone) {
		mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
		server_type.add(MONITOR);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
			obj.put("phone", phone);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.MONITOR,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}
	
	/**
	 * 打电话
	 */
	private void callPhoneFromServer(long deviceId, String phone) {
		mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
		server_type.add(CALL);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
			obj.put("phone", phone);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.CALL,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	
	private List<Integer> server_type = new ArrayList<Integer>();
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case Constants.GET_DATA_SUCCESS:
					switch (server_type.get(0)) {
						
						case MONITOR:
							Log.d("", "监听成功");
							mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
									DeviceFunctionWatchingCall_sm.this,
									mDialog);
							//解决按返回按钮，输入的号码会重置
							//openWatcherDialog();
							break;
						case CALL:
							Log.d("", "打电话成功");

							mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
									DeviceFunctionWatchingCall_sm.this,
									mDialog);
							 //callPhone();
							break;
					}
					server_type.remove(0);
					break;
				case Constants.GET_DATA_FAIL:
					break;
				case Constants.SET_NETLISENER_DATA_START:
					mNetStatusListener = new NetStatusListener();
					mDialog = new ProgressDialog(DeviceFunctionWatchingCall_sm.this);
					mDialog.setMessage(getString(R.string.netlistener_set_data));
					mDialog.setCanceledOnTouchOutside(false);
					mDialog.setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface arg0, int keyCode,
						                     KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								NetStatusListener.mClickflag = false;
								if (mNetStatusListener != null
										&& mNetStatusListener.isRunning()) {
									mNetStatusListener.setRunning(false);
								}

							}
							return false;
						}
					});
					mDialog.show();
					break;
				
				
			}
			
		}
	};
	
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		HttpUtil.initUrl(HttpUtil.BASE_URL_SMART_TYPE);
		super.onResume();
	}
	
	@Override
	public void finish() {
		if (mNetStatusListener != null) {
			mNetStatusListener.setActivityFinish();
			NetStatusListener.mClickflag = false;
			mNetStatusListener.setRunning(false);
			mNetStatusListener.cancleToast();
		}
		super.finish();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
