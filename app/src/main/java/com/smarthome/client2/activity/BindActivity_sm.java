package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.CommonLog;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BindActivity_sm extends Activity {

	private static final CommonLog log = LogFactory.createLog();
	private List<ItemAdapter> deviceList = new ArrayList<ItemAdapter>();

	private final static int LIST_DEVICE_SUCCESS = 801;
	private final static int LIST_DEVICE_FAIL = 802;
	private final static int DEL_DEVICE_FAIL = 803;
	private final static int DEL_DEVICE_SUCCESS = 804;
	private final static int ADD_DEVICE_SUCCESS = 805;
	private final static int ADD_DEVICE_FAIL = 806;

	//设备类型同SelectFamilyRelative.java 中的类型
	private final static String STUDENT_PHONE_TYPE = "1";
	private final static String OLD_PHONE_TYPE = "2";
	private final static String SMART_PHONE_TYPE = "4";
	private final static String CHILD_WATHCH_TYPE = "6";
	
	private ProgressDialog mProgressBar;

	JSONObject jObject;
	JSONArray jsonArray;

	private String mUserId;
	private String mFamilyKeyPersonID;
	private String mLoginUserId;
    private LinkTopSDKUtil instance = null;
	private String mStrWatchID;
	private String mStrWatchAccount;

	private int mNReadOrModify; // 0: read 1: modify
	private Boolean bMultiDevice = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case 1:
					break;
				case 2:
					ll_dialog_body.setVisibility(View.GONE);
					ll_bind_body.setVisibility(View.VISIBLE);
					ll_bind_nothing.setVisibility(View.GONE);
					menuListView.setVisibility(View.VISIBLE);

					initDeviceList();

					addTopBarToHead(false);
					break;
				case 3:
					ll_dialog_body.setVisibility(View.VISIBLE);
					ll_bind_body.setVisibility(View.GONE);
					addTopBarToHead(true);
					break;
				case Constants.SESSION_TIME_OUT:
					Intent intent = new Intent(BindActivity_sm.this,
							LoginActivity_sm.class);
					startActivity(intent);
					break;

				case LIST_DEVICE_SUCCESS:
					String Obj = msg.obj.toString();
					try {
						jObject = new JSONObject(Obj);
						jsonArray = jObject.getJSONArray("data");
						JSONObject childObject;
						for (int i = 0; i < jsonArray.length(); i++) {
							bMultiDevice = true;
							ItemAdapter deviceItem = new ItemAdapter();
							childObject = jsonArray.getJSONObject(i);
							deviceItem.title_deviceCode = childObject.getString("devicecode");
							deviceItem.deviceId = childObject.getInt("id");
							Log.e("--LIST_DEVICE_SUCCESS-", "--deviceid=" + deviceItem.deviceId);
							deviceItem.deviceTypeCode = childObject.getString("devicetype");
							deviceItem.phoneNum = childObject.getString("mobilecode");
							deviceList.add(deviceItem);
						}
						if (deviceList.size() != 0) {
							btn_add_device.setVisibility(View.GONE);
							ll_bind_nothing.setVisibility(View.GONE);
							menuListView.setVisibility(View.VISIBLE);
						} else {
							if (mNReadOrModify == 1) {
								btn_add_device.setVisibility(View.VISIBLE);
							} else {
								btn_add_device.setVisibility(View.INVISIBLE);
							}

							ll_bind_nothing.setVisibility(View.VISIBLE);
							menuListView.setVisibility(View.GONE);
						}

						la.notifyDataSetChanged();

					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case ADD_DEVICE_SUCCESS:
					TLog.Log("result--------add ok");
					break;
				case ADD_DEVICE_FAIL:
					TLog.Log("result------- add fails");
					break;
				case DEL_DEVICE_SUCCESS:
					mProgressBar.dismiss();
					deviceList.remove(curPos);
					if (deviceList.size() != 0) {
						btn_add_device.setVisibility(View.GONE);
						ll_bind_nothing.setVisibility(View.GONE);
						menuListView.setVisibility(View.VISIBLE);
					} else {
						if (mNReadOrModify == 1) {
							btn_add_device.setVisibility(View.VISIBLE);
						} else {
							btn_add_device.setVisibility(View.INVISIBLE);
						}

						ll_bind_nothing.setVisibility(View.VISIBLE);
						menuListView.setVisibility(View.GONE);
					}
					la.notifyDataSetChanged();
					break;
				case DEL_DEVICE_FAIL:
					mProgressBar.dismiss();
					Toast.makeText(BindActivity_sm.this,
    	                    "删除设备失败，请重试！",
    	                    Toast.LENGTH_SHORT).show();
					break;
				case LIST_DEVICE_FAIL:
					break;
				case LinkTopSDKUtil.LINK_SDK_LOGIN_TOKEN:
					if (msg.arg1 == 200){
	            		instance.unBindDevice(mStrWatchID);
	            	}else{
	            		mProgressBar.dismiss();
	            		Toast.makeText(BindActivity_sm.this,
	    	                    "删除设备失败，请重试！",
	    	                    Toast.LENGTH_SHORT).show();
	            	}
					break;
				case LinkTopSDKUtil.LINK_SDK_UNBIND_DEVICE_ACTION:
					delDevice(curPos);
//					if (msg.arg1 == 200){
//	            		delDevice(curPos);
//	            	}else{
//	            		
//	            		Toast.makeText(BindActivity_sm.this,
//	    	                    "删除设备失败，请重试！",
//	    	                    Toast.LENGTH_SHORT).show();
//	            	}
					break;
				default:
					break;

			}
			super.handleMessage(msg);
		}
	};

	/**
	 * blue tooth operate
	 */

	private MenuAdapter la = null;

	protected ListView menuListView = null;

	private LinearLayout ll_bind_nothing;


	private FrameLayout fl_header_bind;

	private CustomActionBar actionBar;

	/**
	 * button
	 */
	private Button btn_add_device, btn_cancle_search_bind;

	private LinearLayout ll_bind_body, ll_dialog_body;

	//private MyBindDialogView myBindDialogView;


	void getInfoFromIntent() {
		Preferences preferences = Preferences.getInstance(this.getApplicationContext());
		mLoginUserId = preferences.getUserID();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mUserId = bundle.getString("userId");
			if (mUserId.compareToIgnoreCase(mLoginUserId) != 0) {
				mFamilyKeyPersonID = bundle.getString("familyKeyPersonID");
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_device_manager_sm);
		getInfoFromIntent();
		initView();
		addTopBarToHead(false);
		initGetDevice();
		AppManager.getAppManager().addActivity(this);
	}

	private void addTopBarToHead(boolean isSearching) {
		fl_header_bind = (FrameLayout) findViewById(R.id.fl_header_bind);
		if (isSearching) {
			actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
					R.drawable.btn_back_selector,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							ll_dialog_body.setVisibility(View.GONE);
							ll_bind_body.setVisibility(View.VISIBLE);
							addTopBarToHead(false);
						}
					},
					getString(R.string.bind_add_device),
					null,
					null);
		} else {
			actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
					R.drawable.btn_back_selector,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					},
					getString(R.string.bind_my_device),
					null,
					null);
		}
		fl_header_bind.addView(actionBar);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private final int ADDDEVICESUCCESS = 1;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == Activity.RESULT_OK) {
				switch (requestCode) {
					case ADDDEVICESUCCESS:
						ItemAdapter deviceItem = new ItemAdapter();
						Bundle bundle = data.getExtras();

						deviceItem.deviceId = bundle.getLong("deviceId");
						deviceItem.title_deviceCode = bundle.getString("deviceCode");
						deviceItem.deviceType = bundle.getString("deviceType");
						deviceItem.deviceTypeCode = bundle.getString("deviceTypeCode");
						deviceItem.phoneNum = bundle.getString("devicePhone");
						deviceList.add(deviceItem);
						if (deviceList.size() != 0) {
							btn_add_device.setVisibility(View.GONE);
							ll_bind_nothing.setVisibility(View.GONE);
							menuListView.setVisibility(View.VISIBLE);
						} else {
							btn_add_device.setVisibility(View.VISIBLE);
							ll_bind_nothing.setVisibility(View.VISIBLE);
							menuListView.setVisibility(View.GONE);
						}
						la.notifyDataSetChanged();
						break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initView() {
		if ((mLoginUserId.compareToIgnoreCase(mUserId) == 0) ||
				(mLoginUserId.compareToIgnoreCase(mFamilyKeyPersonID) == 0)) {
			mNReadOrModify = 1;
		} else {
			mNReadOrModify = 0;
		}
		mProgressBar = new ProgressDialog(BindActivity_sm.this);
	    mProgressBar.setCanceledOnTouchOutside(false);
		ll_bind_nothing = (LinearLayout) findViewById(R.id.ll_bind_nothing);
		ll_bind_body = (LinearLayout) findViewById(R.id.ll_bind_body);
		btn_add_device = (Button) findViewById(R.id.btn_add_device);
		menuListView = (ListView) findViewById(R.id.menuListView);

		menuListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		la = new MenuAdapter(menuListView.getContext(), deviceList);
		menuListView.setAdapter(la);
		gestureDetector = new GestureDetector(getApplicationContext(),
				gestureListener);
		if (mNReadOrModify == 1) {
			menuListView.setOnTouchListener(touchListener);
		}
		btn_add_device.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(BindActivity_sm.this, DeviceManagerment_sm.class);
				intent.putExtra("userId", mUserId);
				startActivityForResult(intent, ADDDEVICESUCCESS);
			}
		});
		btn_add_device.setVisibility(View.INVISIBLE);  // 默认不可见

	}

	private void initGetDevice() {
		initDeviceList();
	}

	protected void initDeviceList() {
		deviceList.clear();
		listDevices();
	}


	public class MenuAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		@SuppressWarnings("unchecked")
		public MenuAdapter(Context context, Object ItemList) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return deviceList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
		                    ViewGroup parent) {
			ItemAdapter holder = null;
			if (convertView == null) {
				holder = new ItemAdapter();
				convertView = mInflater.inflate(R.layout.menu_item, null);
				holder.imgId = (ImageView) convertView.findViewById(R.id.menu_img);
				holder.imgMore = (ImageView) convertView.findViewById(R.id.menu_more);
				holder.titleId = (TextView) convertView.findViewById(R.id.menu_title);
				holder.tvPhone = (TextView) convertView.findViewById(R.id.menu_phone);
				holder.imgDelete = (ImageView) convertView.findViewById(R.id.menu_delete);
				//holder.del = (Button) convertView.findViewById(R.id.menu_del);

				holder.menuListLinearlayout = (RelativeLayout) convertView.findViewById(R.id.menuListLinearlayout);
				if (mNReadOrModify == 1) {
					holder.imgDelete .setVisibility(View.VISIBLE);
				} else {
					holder.imgDelete .setVisibility(View.INVISIBLE);
				}
				holder.imgDelete.setVisibility(View.GONE);  //不用删除按钮删除

				convertView.setTag(holder);
			} else {
				holder = (ItemAdapter) convertView.getTag();
			}

			String strDeviceTypeTmp = deviceList.get(position).deviceType;
			String srtDeviceTypeCodeTmp = deviceList.get(position).deviceTypeCode;

			if ((strDeviceTypeTmp != null && strDeviceTypeTmp.compareToIgnoreCase("老人机") == 0) ||
					(srtDeviceTypeCodeTmp != null && srtDeviceTypeCodeTmp.compareToIgnoreCase(OLD_PHONE_TYPE) == 0)) {
				holder.imgId.setImageResource(R.drawable.icon_older_sm_sc70110);
			} else if ((strDeviceTypeTmp != null && strDeviceTypeTmp.compareToIgnoreCase("学生机") == 0) ||
					(srtDeviceTypeCodeTmp != null && srtDeviceTypeCodeTmp.compareToIgnoreCase(STUDENT_PHONE_TYPE) == 0)) {
				holder.imgId.setImageResource(R.drawable.device_student_v2);
			} else if ((strDeviceTypeTmp != null && strDeviceTypeTmp.compareToIgnoreCase("手表") == 0) ||
					(srtDeviceTypeCodeTmp != null && srtDeviceTypeCodeTmp.compareToIgnoreCase(CHILD_WATHCH_TYPE) == 0)) {
				holder.imgId.setImageResource(R.drawable.icon_watch_sm_sc80110);
			} else if ((strDeviceTypeTmp != null && strDeviceTypeTmp.compareToIgnoreCase("智能机") == 0) ||
					(srtDeviceTypeCodeTmp != null && srtDeviceTypeCodeTmp.compareToIgnoreCase(SMART_PHONE_TYPE) == 0)) {
				holder.imgId.setImageResource(R.drawable.device_old);
			}
//			holder.imgId.setImageResource(deviceList.get(position).img == -1 ? R.drawable.device_kuqi
//					: deviceList.get(position).img);

			if(deviceList.get(position).title_deviceCode != null) {
				holder.titleId.setText(deviceList.get(position).title_deviceCode.trim());
			}

			if(deviceList.get(position).phoneNum != null) {
				holder.tvPhone.setText(deviceList.get(position).phoneNum.trim());
			}

			holder.clazz = deviceList.get(position).clazz;

			//holder.menuListLinearlayout.setOnTouchListener(gestureListener);

			holder.menuListLinearlayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					curPos = position;
					return false;
				}
			});
			holder.imgMore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					curPos = position;
					String clickItem = deviceList.get(curPos).title_deviceCode;
					Intent intent = new Intent(BindActivity_sm.this,
							ListDeviceFunctionsActivity_sm.class);
					intent.putExtra("userId", mUserId);
					intent.putExtra("deviceCode", deviceList.get(curPos).title_deviceCode);
					intent.putExtra("devId", deviceList.get(curPos).deviceId);
					startActivity(intent);
				}
			});
			return convertView;
		}
	}
	
	
	private void unBindWatch(String deviceID, String watchAccount){
		
		instance = LinkTopSDKUtil.getInstance();
		instance.initSDK(BindActivity_sm.this, handler);
		instance.setupAccount(mStrWatchAccount, "888888");
		instance.loginToken();
	}


	private int curPos = -1;

	private int delPos = -1;

	private GestureDetector gestureDetector;

	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
				case R.id.menuListView:
					return gestureDetector.onTouchEvent(event);

				default:
					break;
			}
			return false;
		}
	};

	private OnGestureListener gestureListener = new OnGestureListener() {

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {

			if (curPos != -1) {
				if (deviceList.size() == 0) {
					return false;
				} else {					
					Intent intent = new Intent(BindActivity_sm.this,
													ListDeviceFunctionsActivity_sm.class);
					intent.putExtra("userId", mUserId);
					intent.putExtra("deviceCode", deviceList.get(curPos).title_deviceCode);
					intent.putExtra("devId",deviceList.get(curPos).deviceId);
					startActivity(intent);
					curPos = -1;
				}
			}
			return curPos >= 0;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
		                        float arg3) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			if (mNReadOrModify == 1) {
				Dialog dialog_clear = new AlertDialog.Builder(
						BindActivity_sm.this).setMessage("确定删除吗？")
						.setNegativeButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (deviceList.get(curPos).deviceTypeCode.equals("6")) {
									String[] deviceinfo = deviceList.get(curPos).title_deviceCode.split(":");
									mStrWatchID = deviceinfo[0];
									mStrWatchAccount = deviceinfo[1];
									mProgressBar.setMessage("删除过程中，请等待...");
									mProgressBar.show();
									unBindWatch(mStrWatchID, mStrWatchAccount);
								} else {
									delDevice(curPos);
								}
							}
						})
						.setPositiveButton("取消", null)
						.create();
				dialog_clear.show();
			}
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
		                       float arg3) {
			String flingItem = "";
			if (curPos != -1) {
				flingItem = deviceList.get(curPos).title_deviceCode;
			}
			//从右向左
			if (arg0.getX() - arg1.getX() > 0 && curPos != -1) {
				if (curPos >= 0) {
					if (flingItem.indexOf(getString(R.string.bind_old)) >= 0
							|| flingItem.indexOf(getString(R.string.bind_student)) >= 0) {

						delPos = -1;
						curPos = -1;
						return false;
					} else if (flingItem.indexOf(getString(R.string.bind_timecard)) >= 0) {

						delPos = -1;
						curPos = -1;
						return false;
					} else if (flingItem.indexOf(getString(R.string.bind_status_unbond)) >= 0) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.bind_no_delete_unbonded_device),
								Toast.LENGTH_SHORT)
								.show();
						delPos = -1;
						curPos = -1;
						return false;
					}
					delPos = curPos;
					la.notifyDataSetChanged();
					return true;
				}
			} else if (arg0.getX() - arg1.getX() < 0 && curPos != -1) {
				if (delPos == curPos) {
					delPos = -1;
					curPos = -1;
					la.notifyDataSetChanged();
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent arg0) {
			log.d("daitm------onDown");
			return false;
		}
	};

	public final class ItemAdapter {
		public ImageView imgId;
		public ImageView imgMore;
		public ImageView imgDelete;

		public TextView titleId;
		public TextView tvPhone;

		public Button del;

		public RelativeLayout menuListLinearlayout;

		public int img;

		public long deviceId;           //服务器给的设备唯一识别码
		public String title_deviceCode;

		public String phoneNum;

		public String deviceType;

		public String deviceTypeCode;

		public Class<Object> clazz;

		public int status = 0;
	}


	private void listDevices() {
		JSONObject obj = new JSONObject();
		try {
			//Preferences.getInstance(getApplicationContext()).getUserID()
			obj.put("userId", mUserId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.LISTDEVICES,
				handler,
				LIST_DEVICE_SUCCESS,
				LIST_DEVICE_FAIL);
	}


	private void delDevice(int pos) {
		ItemAdapter delDevice = deviceList.get(pos);
		JSONObject obj = new JSONObject();
		try {
			//Preferences.getInstance(getApplicationContext()).getUserID()
			obj.put("deviceId", delDevice.deviceId);
			Log.e("---deldevice---", "----deviceid=" + delDevice.deviceId + " pos=" + pos);
			// obj.put("devicecode", address);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.DELDEVICES,
				handler,
				DEL_DEVICE_SUCCESS,
				DEL_DEVICE_FAIL);

		
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart(getClass().getSimpleName());
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd(getClass().getSimpleName());
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().removeActivity(this);
	}

	@Override
	public void finish() {
		HttpUtil.initUrl(HttpUtil.BASE_URL_SMART_TYPE);
		super.finish();
	}

}
