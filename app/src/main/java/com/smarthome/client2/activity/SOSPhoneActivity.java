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
import com.smarthome.client2.util.MyExceptionDialog;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.HomeListener.OnHomePressedListener;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class SOSPhoneActivity extends Activity {

	private EditText sos_name_1;

	private EditText sos_name_2;

	private EditText sos_name_3;

	private EditText sos_phone_1;

	private EditText sos_phone_2;

	private EditText sos_phone_3;

	private TextView main_sos;

	private LinearLayout sos_phone_list_layout1;

	private LinearLayout sos_phone_list_layout2;

	private LinearLayout sos_phone_list_layout3;

	private FrameLayout fl_header_sos_activity;

	private CustomActionBar actionBar;

	private List<MyPhoneNumber> sosList = new ArrayList<MyPhoneNumber>();

	private MyExceptionDialog myExceptionDialog;

	private HomeListener mHomeListener;

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
		setContentView(R.layout.activity_sos_phone);
		initWidget();
		initData();
		addTopBarToHead();
		AppManager.getAppManager().addActivity(this);
	}

	private void addTopBarToHead() {
		fl_header_sos_activity = (FrameLayout) findViewById(R.id.fl_header_sos_activity);
		actionBar = TopBarUtils.createCustomActionBar(this,
				R.drawable.btn_back_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				},
				getString(R.string.title_sos),
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
						sosList.clear();
						if (sos_phone_1.getText().toString().trim().length() > 11
								|| (sos_phone_1.getText()
								.toString()
								.trim()
								.length() < 3 && sos_phone_1.getText()
								.toString()
								.trim()
								.length() > 0)) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.sos_invalid_phone1),
									Toast.LENGTH_SHORT).show();
							NetStatusListener.mClickflag = false;
							return;
						}

						if (sos_phone_2.getText().toString().trim().length() > 11
								|| (sos_phone_2.getText()
								.toString()
								.trim()
								.length() < 3 && sos_phone_2.getText()
								.toString()
								.trim()
								.length() > 0)) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.sos_invalid_phone2),
									Toast.LENGTH_SHORT).show();
							NetStatusListener.mClickflag = false;
							return;
						}

						if (sos_phone_3.getText().toString().trim().length() > 11
								|| (sos_phone_3.getText()
								.toString()
								.trim()
								.length() < 3 && sos_phone_3.getText()
								.toString()
								.trim()
								.length() > 0)) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.sos_invalid_phone3),
									Toast.LENGTH_SHORT).show();
							NetStatusListener.mClickflag = false;
							return;
						}

						MyPhoneNumber myPhoneNumber1 = new MyPhoneNumber();
						myPhoneNumber1.nickName = sos_name_1.getText()
								.toString()
								.trim();
						myPhoneNumber1.phoneNumber = sos_phone_1.getText()
								.toString()
								.trim();
						sosList.add(myPhoneNumber1);

						//支持1个或3个sos号码
						if (Preferences.getInstance(getApplicationContext())
								.getSosNum() == 3) {
							MyPhoneNumber myPhoneNumber2 = new MyPhoneNumber();
							myPhoneNumber2.nickName = sos_name_2.getText()
									.toString()
									.trim();
							myPhoneNumber2.phoneNumber = sos_phone_2.getText()
									.toString()
									.trim();
							sosList.add(myPhoneNumber2);

							MyPhoneNumber myPhoneNumber3 = new MyPhoneNumber();
							myPhoneNumber3.nickName = sos_name_3.getText()
									.toString()
									.trim();
							myPhoneNumber3.phoneNumber = sos_phone_3.getText()
									.toString()
									.trim();
							sosList.add(myPhoneNumber3);
						}

						AlertDialog alertDialog = new AlertDialog.Builder(
								SOSPhoneActivity.this).setOnKeyListener(new OnKeyListener() {
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
												setSOSPhoneFromServer(Preferences.getInstance(getApplicationContext())
																.getDeviceId(),
														sosList);
												//dialog.dismiss();
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
		fl_header_sos_activity.addView(actionBar);
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

	private void initWidget() {
		mHomeListener = new HomeListener(getApplicationContext());
		mHomeListener.setOnHomePressedListener(mHomePressedListener);
		mHomeListener.startWatch();
		main_sos = (TextView) findViewById(R.id.main_sos);
		main_sos.setOnTouchListener(mOnTouchListener);
		myExceptionDialog = new MyExceptionDialog(SOSPhoneActivity.this);
		myExceptionDialog.setSubmitClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myExceptionDialog.dismissMyDialog();
			}
		});
		sos_name_1 = (EditText) findViewById(R.id.sos_name_1);
		sos_name_2 = (EditText) findViewById(R.id.sos_name_2);
		sos_name_3 = (EditText) findViewById(R.id.sos_name_3);
		sos_phone_1 = (EditText) findViewById(R.id.sos_phone_1);
		sos_phone_2 = (EditText) findViewById(R.id.sos_phone_2);
		sos_phone_3 = (EditText) findViewById(R.id.sos_phone_3);

		sos_phone_list_layout1 = (LinearLayout) findViewById(R.id.sos_phone_list_layout1);
		sos_phone_list_layout2 = (LinearLayout) findViewById(R.id.sos_phone_list_layout2);
		sos_phone_list_layout3 = (LinearLayout) findViewById(R.id.sos_phone_list_layout3);

		//支持1个或3个sos号码
		if (Preferences.getInstance(getApplicationContext()).getSosNum() == 1) {
			sos_phone_list_layout2.setVisibility(View.GONE);
			sos_phone_list_layout3.setVisibility(View.GONE);
		}

		sos_name_1.setOnTouchListener(mOnTouchListener);
		sos_name_2.setOnTouchListener(mOnTouchListener);
		sos_name_3.setOnTouchListener(mOnTouchListener);
		sos_phone_1.setOnTouchListener(mOnTouchListener);
		sos_phone_2.setOnTouchListener(mOnTouchListener);
		sos_phone_3.setOnTouchListener(mOnTouchListener);
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
		if (myExceptionDialog != null) {
			myExceptionDialog.dismissMyDialog();
		}
		if (mHomeListener != null) {
			mHomeListener.stopWatch();
		}
	}

	private final static int SET_SOS_PHONE = 3;

	private final static int GET_SOS_PHONE = 4;

	private List<Integer> server_type = new ArrayList<Integer>();

	private ProgressDialog dialog = null;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case Constants.GET_DATA_START:
					dialog = new ProgressDialog(SOSPhoneActivity.this);
					dialog.setMessage(getString(R.string.sos_ready_to_get_info));
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
						case SET_SOS_PHONE:

							mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
									SOSPhoneActivity.this,
									dialog);
//                            if (dialog != null && dialog.isShowing()) {
//                                dialog.dismiss();
//                            }
							break;
						case GET_SOS_PHONE:
							try {
								sosList.clear();
								JSONObject data = new JSONObject(
										msg.obj.toString());
								JSONArray array = data.getJSONArray("data");
								List<MyPhoneNumber> list = new ArrayList<MyPhoneNumber>();
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									int id = obj.getInt("id");
									String nickName = obj.getString("nickName");
									String phone = obj.getString("phone");

									MyPhoneNumber sosNumber = new MyPhoneNumber();
									sosNumber.nickName = nickName;
									sosNumber.phoneNumber = phone;
									sosNumber.id = id;
									sosList.add(sosNumber);

									switch (i) {
										case 0:
											sos_name_1.setText(sosNumber.nickName);
											sos_phone_1.setText(sosNumber.phoneNumber);
											break;
										case 1:
											sos_name_2.setText(sosNumber.nickName);
											sos_phone_2.setText(sosNumber.phoneNumber);
											break;
										case 2:
											sos_name_3.setText(sosNumber.nickName);
											sos_phone_3.setText(sosNumber.phoneNumber);
											break;
									}
								}
								dialog.setMessage(getString(R.string.sos_receive_info_success));
								dialog.dismiss();
							} catch (JSONException e) {
								e.printStackTrace();
							}
							break;
					}
					server_type.remove(0);
					break;
				case Constants.GET_DATA_FAIL:
					dialog.setMessage(getString(R.string.sos_receive_info_fail));
					dialog.dismiss();
					break;
				case Constants.SET_NETLISENER_DATA_START:
					mNetStatusListener = new NetStatusListener();
					dialog = new ProgressDialog(SOSPhoneActivity.this);
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
	 * SOS号码（setSOSPhone）
	 */
	private void setSOSPhoneFromServer(int deviceId, List<MyPhoneNumber> list) {
		if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(),
					HttpUtil.responseHandler(getApplicationContext(),
							Constants.NO_NETWORK),
					Toast.LENGTH_SHORT).show();
			NetStatusListener.mClickflag = false;
			return;
		}
		mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
		server_type.add(SET_SOS_PHONE);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
			JSONArray array = new JSONArray();
			for (MyPhoneNumber sosNumber : list) {
				JSONObject phoneObj = new JSONObject();
				phoneObj.put("nickName", sosNumber.nickName);
				phoneObj.put("phone", sosNumber.phoneNumber);
				array.put(phoneObj);
			}
			obj.put("data", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.SET_SOS_PHONE,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}

	/**
	 * SOS号码（getSOSPhone）
	 */
	private void getSOSPhoneFromServer(int deviceId) {
		server_type.add(GET_SOS_PHONE);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.GET_SOS_PHONE,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}

	private void initData() {
		mHandler.sendEmptyMessage(Constants.GET_DATA_START);
		getSOSPhoneFromServer(Preferences.getInstance(getApplicationContext())
				.getDeviceId());
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
