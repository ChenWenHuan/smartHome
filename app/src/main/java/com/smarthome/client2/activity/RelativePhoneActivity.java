package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.MyPhoneNumber;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HomeListener;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.HomeListener.OnHomePressedListener;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class RelativePhoneActivity extends Activity {

	private EditText family_name_1;

	private EditText family_name_2;

	private EditText family_name_3;

	private EditText family_name_4;

	private EditText family_phone_1;

	private EditText family_phone_2;

	private EditText family_phone_3;

	private EditText family_phone_4;

	private FrameLayout fl_header_relative_activity;

	private LinearLayout k210_layout;

	private CustomActionBar actionBar;

	private TextView main_relative;

	private List<MyPhoneNumber> relativeList = new ArrayList<MyPhoneNumber>();

	private HomeListener mHomeListener;
	
	private String  deviceID;
	
	private String deviceCode;

	private OnHomePressedListener mHomePressedListener = new OnHomePressedListener() {

		@Override
		public void onHomePressed() {
			if (mNetStatusListener != null) {
				mNetStatusListener.cancleToast();
			}
		}

		@Override
		public void onHomeLongPressed() {
			if (mNetStatusListener != null) {
				mNetStatusListener.cancleToast();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_relative_phone);
		initWidget();
	
		addTopBarToHead();
		deviceID = this.getIntent().getStringExtra("devId");
		deviceCode = this.getIntent().getStringExtra("deviceCode");
		initData();
		AppManager.getAppManager().addActivity(this);
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

	private void addTopBarToHead() {
		fl_header_relative_activity = (FrameLayout) findViewById(R.id.fl_header_relative_activity);
		actionBar = TopBarUtils.createCustomActionBar(this,
				R.drawable.btn_back_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				},
				getString(R.string.title_relative),
				getString(R.string.common_btn_save),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mNetStatusListener != null) {
							mNetStatusListener.cancleToast();
						}
						if (NetStatusListener.mClickflag) {
							//                            Toast.makeText(getApplicationContext(),
							//                                    getString(R.string.netlistener_already_set),
							//                                    Toast.LENGTH_SHORT)
							//                                    .show();
							return;
						}
						NetStatusListener.mClickflag = true;
						relativeList.clear();
						if (TextUtils.isEmpty(family_phone_1.getText()
								.toString()
								.trim())) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.relative_empty_phone1),
									Toast.LENGTH_SHORT).show();
							NetStatusListener.mClickflag = false;
							return;
						}
						if (family_phone_1.getText().toString().trim().length() > 12
								|| (family_phone_1.getText()
								.toString()
								.trim()
								.length() < 3 && family_phone_1.getText()
								.toString()
								.trim()
								.length() > 0)) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.relative_invalid_phone1),
									Toast.LENGTH_SHORT)
									.show();
							NetStatusListener.mClickflag = false;
							return;
						}

						if (family_phone_2.getText().toString().trim().length() > 12
								|| (family_phone_2.getText()
								.toString()
								.trim()
								.length() < 3 && family_phone_2.getText()
								.toString()
								.trim()
								.length() > 0)) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.relative_invalid_phone2),
									Toast.LENGTH_SHORT)
									.show();
							NetStatusListener.mClickflag = false;
							return;
						}

						if (family_phone_3.getText().toString().trim().length() > 12
								|| (family_phone_3.getText()
								.toString()
								.trim()
								.length() < 3 && family_phone_3.getText()
								.toString()
								.trim()
								.length() > 0)) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.relative_invalid_phone3),
									Toast.LENGTH_SHORT)
									.show();
							NetStatusListener.mClickflag = false;
							return;
						}

						if (family_phone_4.getText().toString().trim().length() > 12
								|| (family_phone_4.getText()
								.toString()
								.trim()
								.length() < 3 && family_phone_4.getText()
								.toString()
								.trim()
								.length() > 0)) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.relative_invalid_phone4),
									Toast.LENGTH_SHORT)
									.show();
							NetStatusListener.mClickflag = false;
							return;
						}

						if (family_name_1.getText().toString().trim().length() > 4
								|| family_name_2.getText()
								.toString()
								.trim()
								.length() > 4
								|| family_name_3.getText()
								.toString()
								.trim()
								.length() > 4
								|| family_name_4.getText()
								.toString()
								.trim()
								.length() > 4) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.relative_long_name),
									Toast.LENGTH_SHORT).show();
							NetStatusListener.mClickflag = false;
							return;
						}

						MyPhoneNumber myPhoneNumber1 = new MyPhoneNumber();
						myPhoneNumber1.nickName = family_name_1.getText()
								.toString()
								.trim();
						myPhoneNumber1.phoneNumber = family_phone_1.getText()
								.toString()
								.trim();
						relativeList.add(myPhoneNumber1);

						MyPhoneNumber myPhoneNumber2 = new MyPhoneNumber();
						myPhoneNumber2.nickName = family_name_2.getText()
								.toString()
								.trim();
						myPhoneNumber2.phoneNumber = family_phone_2.getText()
								.toString()
								.trim();
						relativeList.add(myPhoneNumber2);

						MyPhoneNumber myPhoneNumber3 = new MyPhoneNumber();
						myPhoneNumber3.nickName = family_name_3.getText()
								.toString()
								.trim();
						myPhoneNumber3.phoneNumber = family_phone_3.getText()
								.toString()
								.trim();
						relativeList.add(myPhoneNumber3);
//
//						if (Preferences.getInstance(getApplicationContext())
//								.getDeviceModel()
//								.indexOf("K210") >= 0) {
							MyPhoneNumber myPhoneNumber4 = new MyPhoneNumber();
							myPhoneNumber4.nickName = family_name_4.getText()
									.toString()
									.trim();
							myPhoneNumber4.phoneNumber = family_phone_4.getText()
									.toString()
									.trim();
							relativeList.add(myPhoneNumber4);
						//}

						AlertDialog alertDialog = new AlertDialog.Builder(
								RelativePhoneActivity.this).setOnKeyListener(new OnKeyListener() {
							@Override
							public boolean onKey(DialogInterface arg0,
							                     int keyCode, KeyEvent event) {
								if (keyCode == KeyEvent.KEYCODE_BACK) {
									NetStatusListener.mClickflag = false;
								}
								return false;
							}
						})
								.setMessage(getString(R.string.netlistener_ask))
								.setNegativeButton(getString(R.string.common_btn_yes),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												setFamilyPhoneFromServer(deviceID,
														relativeList);
												dialog.dismiss();
											}
										})
								.setPositiveButton(getString(R.string.common_btn_no),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												NetStatusListener.mClickflag = false;
											}
										})
								.create();
						alertDialog.setCanceledOnTouchOutside(false);
						alertDialog.show();
					}
				});
		fl_header_relative_activity.addView(actionBar);
	}

	private void initWidget() {
		mHomeListener = new HomeListener(getApplicationContext());
		mHomeListener.setOnHomePressedListener(mHomePressedListener);
		mHomeListener.startWatch();
		main_relative = (TextView) findViewById(R.id.main_relative);
		main_relative.setOnTouchListener(mOnTouchListener);
		family_name_1 = (EditText) findViewById(R.id.family_name_1);
		family_name_2 = (EditText) findViewById(R.id.family_name_2);
		family_name_3 = (EditText) findViewById(R.id.family_name_3);
		family_name_4 = (EditText) findViewById(R.id.family_name_4);
		family_phone_1 = (EditText) findViewById(R.id.family_phone_1);
		family_phone_2 = (EditText) findViewById(R.id.family_phone_2);
		family_phone_3 = (EditText) findViewById(R.id.family_phone_3);
		family_phone_4 = (EditText) findViewById(R.id.family_phone_4);
		k210_layout = (LinearLayout) findViewById(R.id.k210_layout);
//        if (Preferences.getInstance(getApplicationContext())
//                .getDeviceModel()
//                .indexOf("K210") >= 0)
//        {
//            k210_layout.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            k210_layout.setVisibility(View.GONE);
//        }

		family_name_1.setOnTouchListener(mOnTouchListener);
		family_name_2.setOnTouchListener(mOnTouchListener);
		family_name_3.setOnTouchListener(mOnTouchListener);
		family_name_4.setOnTouchListener(mOnTouchListener);
		family_phone_1.setOnTouchListener(mOnTouchListener);
		family_phone_2.setOnTouchListener(mOnTouchListener);
		family_phone_3.setOnTouchListener(mOnTouchListener);
		family_phone_4.setOnTouchListener(mOnTouchListener);
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mNetStatusListener != null) {
				mNetStatusListener.cancleToast();
			}
			return false;
		}
	};

	@Override
	protected void onDestroy() {
		AppManager.getAppManager().removeActivity(this);
		super.onDestroy();
		if (mHomeListener != null) {
			mHomeListener.stopWatch();
		}
	}

	private final static int SET_FAMILY_PHONE = 1;

	private final static int GET_FAMILY_PHONE = 2;

	private List<Integer> server_type = new ArrayList<Integer>();

	private ProgressDialog dialog = null;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case Constants.GET_DATA_START:
					dialog = new ProgressDialog(RelativePhoneActivity.this);
					dialog.setMessage(getString(R.string.relative_ready_to_get_info));
					dialog.show();

					if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
							Toast.makeText(getApplicationContext(),
									HttpUtil.responseHandler(getApplicationContext(),
											Constants.NO_NETWORK),
									Toast.LENGTH_SHORT)
									.show();
						}
						return;
					}
					break;
				case Constants.GET_DATA_SUCCESS:
					switch (server_type.get(0)) {
						case SET_FAMILY_PHONE:
//							if (dialog != null && dialog.isShowing()) {
//								dialog.dismiss();
//							}
							mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
									RelativePhoneActivity.this,
									dialog);
							break;
						case GET_FAMILY_PHONE:
							try {
								relativeList.clear();
								JSONObject data = new JSONObject(
										msg.obj.toString());
								JSONArray array = data.getJSONArray("data");
								List<MyPhoneNumber> list = new ArrayList<MyPhoneNumber>();
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									int id = obj.getInt("id");
									String nickName = obj.getString("nickName");
									String phone = obj.getString("phone");

									MyPhoneNumber relativeNumber = new MyPhoneNumber();
									relativeNumber.nickName = nickName;
									relativeNumber.phoneNumber = phone;
									relativeNumber.id = id;
									relativeList.add(relativeNumber);

									switch (i) {
										case 0:
											family_name_1.setText(relativeNumber.nickName);
											family_phone_1.setText(relativeNumber.phoneNumber);
											break;
										case 1:
											family_name_2.setText(relativeNumber.nickName);
											family_phone_2.setText(relativeNumber.phoneNumber);
											break;
										case 2:
											family_name_3.setText(relativeNumber.nickName);
											family_phone_3.setText(relativeNumber.phoneNumber);
											break;
										case 3:
											family_name_4.setText(relativeNumber.nickName);
											family_phone_4.setText(relativeNumber.phoneNumber);
											break;
									}
								}
								dialog.setMessage(getString(R.string.relative_receive_info_success));
								dialog.dismiss();
							} catch (JSONException e) {
								e.printStackTrace();
							}
							break;
					}
					server_type.remove(0);
					break;
				case Constants.GET_DATA_FAIL:
					dialog.setMessage(getString(R.string.relative_receive_info_fail));
					dialog.dismiss();
					break;
				case Constants.SET_NETLISENER_DATA_START:
					mNetStatusListener = new NetStatusListener();
					dialog = new ProgressDialog(RelativePhoneActivity.this);
					dialog.setMessage(getString(R.string.netlistener_set_data));
					dialog.setCanceledOnTouchOutside(false);
					dialog.setOnKeyListener(new OnKeyListener() {
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
					dialog.show();
					ExceptionReciver.setNetLisenerDialog(dialog);
					break;
			}
		}
	};

	/**
	 * 亲情号码（setFamilyPhone）
	 */
	private void setFamilyPhoneFromServer(String deviceId, List<MyPhoneNumber> list) {
		if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(),
					HttpUtil.responseHandler(getApplicationContext(),
							Constants.NO_NETWORK),
					Toast.LENGTH_SHORT).show();
			NetStatusListener.mClickflag = false;
			return;
		}
		mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
		server_type.add(SET_FAMILY_PHONE);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
			JSONArray array = new JSONArray();
			for (MyPhoneNumber relativeNumber : list) {
				JSONObject phoneObj = new JSONObject();
				phoneObj.put("nickName", relativeNumber.nickName);
				phoneObj.put("phone", relativeNumber.phoneNumber);
				array.put(phoneObj);
			}
			obj.put("data", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.SET_FAMILY_PHONE,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}

	/**
	 * 亲情号码（getFamilyPhone）
	 */
	private void getFamilyPhoneFromServer(String deviceId) {
		server_type.add(GET_FAMILY_PHONE);
		Log.e("-getFamilyPhoneFromServer--", "--deviceId=" + deviceId);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.GET_FAMILY_PHONE,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}

	private void initData() {
		mHandler.sendEmptyMessage(Constants.GET_DATA_START);
		getFamilyPhoneFromServer(deviceID);
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

	private NetStatusListener mNetStatusListener;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//        if (keyCode == event.KEYCODE_BACK)
		//        {
		if (mNetStatusListener != null
				&& mNetStatusListener.getCustomToast() != null) {
			return mNetStatusListener.cancleToast();
		}
		//        }
		return super.onKeyDown(keyCode, event);
	}

}
