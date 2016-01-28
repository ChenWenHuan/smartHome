package com.smarthome.client2.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.CommonLog;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.BitmapUtil;
import com.smarthome.client2.util.HomeListener;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.LocationUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.UserInfoUtil;
import com.smarthome.client2.view.CalendarView;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.view.LocationHead;
import com.smarthome.client2.view.CalendarView.OnItemClickListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.umeng.analytics.MobclickAgent;

public class LocationActivity extends Activity implements OnTouchListener,
		OnGetGeoCoderResultListener {
	
	private final static int GOT_BAIDU_XY_RESULT = 0x90001;	
	public static final CommonLog log = LogFactory.createLog();	
	
	private final static int LOC_FREQUENT = 1;	
	private final static int LOC_HISTORY = 2;	
	private final static int LOC_GEOFENCE = 3;	
	private final static int ADD_GEO_FENCE = 4;	
	private final static int EDIT_GEO_FENCE = 5;	
	private final static int DELETE_GEO_FENCE = 6;	
	private final static int LOC_UPLOAD = 7;	
	private final static int GET_NOW_POS = 8;	
	private final static int MONITOR = 9;	
	private final static int ONMAPLOADED = 10;	
	private final static int GET_LAST_POS = 11;	
	private final static int START_LAST_POS = 12;	
	private int loc_type = -1;
	
	private Dialog locDialog;
	
	private boolean isLocNullValue = false;
	
	private List<LatLng> frequentList = new ArrayList<LatLng>();
	
	private List<LatLng> historyLatLng = new ArrayList<LatLng>();
	
	private List<LatLng> fenceList = new ArrayList<LatLng>();
	
	private List<Integer> radiusList = new ArrayList<Integer>();
	
	private List<Map<String, Object>> geoList = new ArrayList<Map<String, Object>>();
	
	private Map<String, Object> geoMap = new HashMap<String, Object>();
	
	private List<String> historySpecificLoc = new ArrayList<String>();
	
	private List<String> frequentSpecificLoc = new ArrayList<String>();
	
	private List<LatLng> originHistoryLatLng = null;
	
	
	private boolean originIsEmpty = false;	

	
	private final static int FREQUENT_TYPE = 1;
	
	private final static int HISTORY_TYPE = 2;
	
	private final static int GEOFENCE_TYPE = 3;
	
	private Context currentContext = null;
	
	private static final String TAG = "LocationActivity";
	
	private LatLng currentLocation = null;
	
	private MapView mMapView = null;
	
	private BaiduMap mMap = null;
	
	private UiSettings mUiSettings;
	
	private LocationClientOption option = null;
	
	private LinearLayout home_layout_baidumap;
	
	
	private LocationClient mLocationClient = null;
	
	private String currentAddress = null;
	
	private Marker userMarker, fenceMarker;	

	
	private LinearLayout activity_location_mapView_default;
	
	// ///////////////////////////location default page///////////////	
	
	private String mLastPosAddr, mLastPosTime, mLastPosType;
	
	private int mLastPosPower;
	
	private LatLng mLastPosLatLng;
	
	private RelativeLayout activity_location_mapView_history_place;
	
	private ImageView activity_location_refresh_btn;
	
	private TextView activity_location_history_tv;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private ListView mHistoryListview;
	
	private HistoryAdapter mHistoryAdapter;
	
	private List<LatLng> points = new ArrayList<LatLng>();
	
	private RelativeLayout activity_location_mapView_frequent_place;
	
	private TextView activity_location_frequentLocation_img;
	
	private TextView activity_location_frequentLocation_tv;
	
	private ImageView activity_location_frequentLocation_btn;
	
	private LinearLayout activity_location_mapView_frequent_place_list_layout;
	
	private ListView activity_location_mapView_frequent_place_listview;
	
	private ImageView imgChat, imgPhone, imgMore;
	
	private FrequentPlaceAdapter frequentPlaceAdapter;
	

	private EditText function_meter, function_name;
	
	private Button function_ok, function_cancle;
	
	private RelativeLayout function_layout;
	
	private SeekBar function_SeekBar;
	
	private LatLng currentFenceLoc;
	
	private int currentFenceRadius;
	
	private Map<String, Object> editFenceLoc, delFenceLoc, tmpFenceLoc;
	
	private RelativeLayout activity_location_mapView_fence;
	
	private ImageView activity_location_fence_btn;
	
	private TextView activity_location_fence_img;
	
	private TextView activity_location_fence_tv;
	
	private LinearLayout activity_location_mapView_fence_list_layout;
	
	private ListView activity_location_mapView_fence_listview;
	
	private FenceAdapter fenceAdapter;
	
	private final static int MAX_DISTANCE = 1900;
	
	private final static int DEFAULT_DISTANCE = 400;
	
	private final static int MIN_DISTANCE = 100;
	
	private final static int DEFAULT_PROGRESS = (int) (DEFAULT_DISTANCE * 1.0
			/ MAX_DISTANCE * 100);
	
	private boolean isOpenGeoFence = false;
	
	private boolean isShowFirstGeoFence = true;
	
	private boolean isCreateGeoFence = false;
	
	private boolean isExit = true;
	
	private boolean isTexting = false;
	
	private boolean isCurrentUser = false;
	
	private String mStrMemberUserId = "";
	private long mLongMemberUserId = 0;
	private String linkTopBindAccount = "";
	private LinkTopSDKUtil linkInstance = null;
	
	private String headImage = "";
	private String latestToken = null;
	private String userName;
	private HashMap<String, String>  watchFenceMap = new HashMap<String, String>();
	private String deviceId;
	private int mBindedDeviceType = 0;
	private String mBindedDeviceCode = "";
	private String mBindedDeviceTelNum = "";
	private String mFamilyKeyPersonId;
	private String mFamilyId;
	
	// ///////////////////////////location geoFence page///////////////
	
	private FrameLayout fl_header_location_activity;
	
	private CustomActionBar actionBar;
	
	private RelativeLayout location_calendar;
	
	private CalendarView calendarView;
	
	private ImageButton calendarLeft, calendarRight;
	
	private TextView calendarCenter;	
	
	private Date selectDate;
	
	private String selectDateString;
	
	private LinearLayout mHomeLayout1;
	
	private LinearLayout mHomeLayout2;
	
	private LinearLayout mHomeLayout3;
	
	// /////////////////////////location end//////////////////////////
	
	private int mEditPosition = -100;
	
	private Bitmap headPhoto = null;
	
	private Map<String, Bitmap> photoMap = new HashMap<String, Bitmap>();
	
	private NetStatusListener mNetStatusListener;
	
	private HomeListener mHomeListener;
	
	private Runnable runnableUi = new Runnable() {
		@Override
		public void run() {
			// 更新界面
			refreshRunnableUI();
		}
		
	};
	
	private Runnable smartPhoneUi = new Runnable() {
		@Override
		public void run() {
			// 更新界面
			refreshSmartHomeUI();
		}
		
	};
	
	private Runnable accountPhoneUi = new Runnable() {
		@Override
		public void run() {
			// 更新界面
			refreshAccountUI();
		}
		
	};
	
	private void refreshRunnableUI() {
		if (historyLatLng != null && historyLatLng.size() > 0) {
			
			setUserCenterFocus(historyLatLng.get(0));
		}
	}
	
	private void refreshSmartHomeUI() {
		String loc = currentAddress + "\n";
		String date = "定位时间:" + sdf2.format(new Date()) + "\n";
		String type = "定位方式:GPS";
		setUserLastPosition(currentLocation, loc + date + type);
	}
	
	private void refreshAccountUI() {
		setUserLastPosition(mLastPosLatLng, mLastPosAddr + mLastPosTime
				+ mLastPosType + mLastPosPower);
	}
	
	public static Bitmap getBitmap(String path) throws IOException {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			return bitmap;
		}
		return null;
	}
	
	private boolean checkNetWork() {
		if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
			if (!isKeyBack) {
				Toast.makeText(currentContext,
						HttpUtil.responseHandler(currentContext,
								Constants.NO_NETWORK),
						Toast.LENGTH_SHORT).show();
				NetStatusListener.mClickflag = false;
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * @param context
	 * @return true 表示开启
	 */
	public static final boolean isOPenGPS(final Context context) {
		LocationManager locationManager
				= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。
		// 主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return gps || network;

	}

	/**
	 * 强制帮用户打开GPS
	 *
	 * @param context
	 */
	public  final void setGPSEnable(Context context) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		} catch (PendingIntent.CanceledException e) {
			e.printStackTrace();
		}
		Toast.makeText(LocationActivity.this,
				("GPS 开启中！请点击重新操作。"),
				Toast.LENGTH_SHORT)
				.show();
	}

	public  void gotoGpsSystemSetting(Activity activity) {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		activity.startActivityForResult(intent, 0);
	}

	private void queryOpenGPSDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LocationActivity.this);
		builder.setMessage("点击确定开启GPS？");

		builder.setNegativeButton(getString(R.string.common_btn_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setGPSEnable(LocationActivity.this);
					}
				});
		builder.setPositiveButton(getString(R.string.common_btn_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}


	class MyProgressDialog extends ProgressDialog {
		
		public MyProgressDialog(Context context, int theme) {
			super(context, theme);
		}
		
		@Override
		public void dismiss() {
			if (!isFinishing()) {
				super.dismiss();
			}
		}
	}
	
	private MyProgressDialog progressDialog;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case Constants.GET_DATA_START:
					// 首先得到整个View
					View viewProce = LayoutInflater.from(currentContext).inflate(
							R.layout.loading_dialog_view, null);
					// 获取整个布局
					LinearLayout layout = (LinearLayout) viewProce
							.findViewById(R.id.dialog_view);
					// 页面中的Img
					ImageView img = (ImageView) viewProce.findViewById(R.id.img);
					// 页面中显示文本
					// 加载动画，动画用户使img图片不停的旋转
					Animation animation = AnimationUtils.loadAnimation(currentContext,
							R.anim.dialog_load_animation);
					// 显示动画
					img.startAnimation(animation);
					
					// 显示动画
					img.startAnimation(animation);
					// 创建自定义样式的Dialog
					locDialog = new Dialog(LocationActivity.this, R.style.loading_dialog);
					// 设置返回键无效
					locDialog.setCancelable(true);
					locDialog.setContentView(layout, new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT));
					locDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
					locDialog.setCanceledOnTouchOutside(false);
					locDialog.setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface arg0, int keyCode,
						                     KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								if (loc_type == ADD_GEO_FENCE
										|| loc_type == EDIT_GEO_FENCE
										|| loc_type == DELETE_GEO_FENCE
										|| loc_type == GET_NOW_POS) {
									NetStatusListener.mClickflag = false;
									if (mNetStatusListener != null
											&& mNetStatusListener.isRunning()) {
										mNetStatusListener.setRunning(false);
									}
								}
							}
							return false;
						}
					});
					locDialog.show();
					break;
				case Constants.GET_DATA_SUCCESS:
					switch (loc_type) {
						case LOC_FREQUENT:
							try {
								frequentList.clear();
								frequentSpecificLoc.clear();
								JSONObject data = new JSONObject(
										msg.obj.toString());
								JSONArray array = data.getJSONArray("data");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									if (obj.has("wd")
											&& obj.has("jd")
											&& !TextUtils.isEmpty(obj.getString("wd"))
											|| !TextUtils.isEmpty(obj.getString("jd"))) {
										LatLng latLng = new LatLng(
												obj.getDouble("wd"),
												obj.getDouble("jd"));
										frequentList.add(latLng);
										frequentSpecificLoc.add(obj.getString("address"));
									}
								}
								
								for (int i = 0; i < frequentList.size(); i++) {
									setPosition(frequentList.get(i), i + 1);
								}
								
								if (frequentSpecificLoc.size() > 0) {
									activity_location_frequentLocation_tv.setText(frequentSpecificLoc.get(0));
									activity_location_frequentLocation_img.setVisibility(View.VISIBLE);
									activity_location_frequentLocation_btn.setEnabled(true);
								} else {
									activity_location_frequentLocation_tv.setText(getString(R.string.location_no_data));
									activity_location_frequentLocation_img.setVisibility(View.GONE);
									activity_location_frequentLocation_btn.setEnabled(false);
								}
								
								if (frequentPlaceAdapter == null) {
									frequentPlaceAdapter = new FrequentPlaceAdapter(
											frequentSpecificLoc);
									activity_location_mapView_frequent_place_listview.setAdapter(frequentPlaceAdapter);
								} else {
									frequentPlaceAdapter.notifyDataSetChanged();
								}
								
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							break;
						case LOC_HISTORY:
							NetStatusListener.mClickflag = false;
							HistoryPlaces.clear();
							try {
								mHistoryListview.setVisibility(View.GONE);
								historyLatLng.clear();
								historySpecificLoc.clear();
								activity_location_history_tv.setText(getString(R.string.location_history));
								JSONObject data = new JSONObject(
										msg.obj.toString());
								if (!data.isNull("data")) {
									JSONArray array = data.getJSONArray("data");
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = array.getJSONObject(i);
										if (obj.has("wd")
												&& obj.has("jd")
												&& !TextUtils.isEmpty(obj.getString("wd"))
												|| !TextUtils.isEmpty(obj.getString("jd"))) {
											LatLng latLng = new LatLng(
													obj.getDouble("wd"),
													obj.getDouble("jd"));
											String addr = obj.getString("address");
											String time = obj.getString("getdate");
											historyLatLng.add(latLng);
											historySpecificLoc.add(addr);
											HistoryItem item = new HistoryItem();
											item.itemLoc = addr;
											item.itemTime = time;
											HistoryPlaces.add(item);
										}
									}
									HistorySize = HistoryPlaces.size();
									
									points.clear();
									for (LatLng point : historyLatLng) {
										points.add(point);
									}
									
									if (mHistoryAdapter == null) {
										mHistoryAdapter = new HistoryAdapter();
										mHistoryListview.setAdapter(mHistoryAdapter);
									} else {
										mHistoryAdapter.notifyDataSetChanged();
									}
									
									if (!originIsEmpty) {
										originHistoryLatLng = historyLatLng;
										originIsEmpty = true;
										log.d("daitm---1---historyLatLng----"
												+ historyLatLng);
										log.d("daitm---2---originHistoryLatLng----"
												+ originHistoryLatLng);
									}
									
									if (headPhoto == null
											&& historyLatLng.size() > 0
											&& !photoMap.containsKey("account")) {
										new Thread(new Runnable() {
											@Override
											public void run() {
												try {

													if (!TextUtils.isEmpty(headImage)) {
														headPhoto = getBitmap(headImage);
														photoMap.put("account",
																headPhoto);
													}
													handler.post(runnableUi);
												} catch (IOException e) {
													e.printStackTrace();
												}
											}
										}).start();
									} else {
										refreshRunnableUI();
									}								

									if (historySpecificLoc.size() > 0) {
										LatLng lastPos = historyLatLng.get(0);
										setUserCenterFocus(lastPos);
										activity_location_history_tv.setText(historySpecificLoc.get(0));
										mHistoryListview.setVisibility(View.VISIBLE);
									} else {
										Toast.makeText(LocationActivity.this,
												getString(R.string.location_no_data),
												Toast.LENGTH_SHORT)
												.show();
										activity_location_history_tv.setText(getString(R.string.location_no_data));
									}
									handler.postDelayed(new Runnable() {
										
										@Override
										public void run() {
											drawHistroy(0);
										}
									}, 1000);
									//                          }
								}
							} catch (JSONException e) {
								if (progressDialog != null
										&& progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								Toast.makeText(currentContext,
										getString(R.string.location_receive_message_fail),
										Toast.LENGTH_SHORT)
										.show();
								e.printStackTrace();
							}
							break;
						case LOC_GEOFENCE:
							NetStatusListener.mClickflag = false;
							try {
								geoList.clear();
								mMap.clear();
								JSONObject data = new JSONObject(
										msg.obj.toString());
								JSONArray array = data.getJSONArray("data");
								String pre_geofence = "";
								for (int i = 0; i < array.length(); i++) {
									JSONObject fence = array.getJSONObject(i);
									Long fenceId = fence.getLong("id");
									String name = fence.getString("name");									
									int alarmType = fence.getInt("alarmType");
									String seq = fence.getString("seq");
									JSONArray arr = array.getJSONObject(i)
											.getJSONArray("point");
									for (int j = 0; j < arr.length(); j++) {
										JSONObject obj = arr.getJSONObject(j);
										if (obj.has("wd")
												&& obj.has("jd")
												&& !TextUtils.isEmpty(obj.getString("wd"))
												|| !TextUtils.isEmpty(obj.getString("jd"))) {
											LatLng latLng = new LatLng(
													obj.getDouble("wd"),
													obj.getDouble("jd"));
											int radius = obj.getInt("radius");
											drawGeoFence(latLng, radius);
											// latitude-longtitude-radius
											pre_geofence = obj.getDouble("jd")
													+ "-" + obj.getDouble("wd")
													+ radius + ","
													+ pre_geofence;
											geoMap = new HashMap<String, Object>();
											geoMap.put("latLng", latLng);
											geoMap.put("radius", radius);
											geoMap.put("fenceId", fenceId);
											geoMap.put("name", name);
											geoMap.put("alarmType", alarmType);
											geoMap.put("seq", seq);
											geoList.add(geoMap);
										}
									}
								}
								
								if (geoList.size() > 0) {
									activity_location_fence_img.setVisibility(View.VISIBLE);
									activity_location_fence_btn.setEnabled(true);
									if (isShowFirstGeoFence) {
										int ser = geoList.size();
										activity_location_fence_tv.setText((String) geoList.get(ser - 1)
												.get("name"));
										setCenterPoint((LatLng) geoList.get(ser - 1)
												.get("latLng"));
										activity_location_fence_img.setText(""
												+ ser);
										isShowFirstGeoFence = false;
									} else {

										int pos = (mEditPosition == -100 ? (geoList.size() - 1)
												: mEditPosition);//add pos ? edit pos
										if (pos == geoList.size()) {
											pos = geoList.size() - 1;//delete pos
										}
										activity_location_fence_tv.setText((String) geoList.get(pos)
												.get("name"));
										setCenterPoint((LatLng) geoList.get(pos)
												.get("latLng"));
										activity_location_fence_img.setText(""
												+ (pos + 1));
									}
								} else {
									activity_location_fence_tv.setText(getString(R.string.location_no_fence));
									activity_location_fence_img.setVisibility(View.GONE);
									activity_location_fence_btn.setEnabled(false);
								}
								activity_location_mapView_fence.setVisibility(View.VISIBLE);
								
								if (!TextUtils.isEmpty(pre_geofence)) {
									Preferences.getInstance(currentContext)
											.setGeofence(pre_geofence.substring(0,
													pre_geofence.length() - 1));
								}
								
								if (fenceAdapter == null) {
									fenceAdapter = new FenceAdapter(geoList);
									activity_location_mapView_fence_listview.setAdapter(fenceAdapter);
								} else {
									fenceAdapter.notifyDataSetChanged();
								}
								
							} catch (JSONException e) {
								if (progressDialog != null
										&& progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								Toast.makeText(currentContext,
										getString(R.string.location_receive_fence_fail),
										Toast.LENGTH_SHORT)
										.show();
								e.printStackTrace();
							}
							break;
						case ADD_GEO_FENCE:
							editFenceLoc = null;
							tmpFenceLoc = null;						

							if (!Preferences.getInstance(getApplicationContext())
									.getDeviceModel()
									.equalsIgnoreCase("gk309")) {
								Toast.makeText(LocationActivity.this,
										getString(R.string.common_save_success),
										Toast.LENGTH_SHORT)
										.show();
								getGeoFenceFromServer(mStrMemberUserId);
							} else {
								mNetStatusListener = new NetStatusListener();
								mNetStatusListener.setGK309FenceListener(mGK309FenceListener);
								mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
										LocationActivity.this,
										locDialog);
							}
							break;
						case EDIT_GEO_FENCE:
							if (!Preferences.getInstance(getApplicationContext())
									.getDeviceModel()
									.equalsIgnoreCase("gk309")) {
								Toast.makeText(LocationActivity.this,
										getString(R.string.common_edit_success),
										Toast.LENGTH_SHORT)
										.show();
								getGeoFenceFromServer(mStrMemberUserId);
							} else {
								mNetStatusListener = new NetStatusListener();
								mNetStatusListener.setGK309FenceListener(mGK309FenceListener);
								mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
										LocationActivity.this,
										locDialog);
							}
							break;
						case DELETE_GEO_FENCE:
							tmpFenceLoc = null;
							isCreateGeoFence = false;
							if (!Preferences.getInstance(getApplicationContext())
									.getDeviceModel()
									.equalsIgnoreCase("gk309")) {
								Toast.makeText(LocationActivity.this,
										getString(R.string.common_delete_success),
										Toast.LENGTH_SHORT)
										.show();
								getGeoFenceFromServer(mStrMemberUserId);
							} else {
								mNetStatusListener = new NetStatusListener();
								mNetStatusListener.setGK309FenceListener(mGK309FenceListener);
								mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
										LocationActivity.this,
										locDialog);
							}
							break;
						case LOC_UPLOAD:
							break;
						case GET_NOW_POS:
							mNetStatusListener = new NetStatusListener();
							mNetStatusListener.setLastPosListener(mLastPosListener);
							mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
									LocationActivity.this,
									locDialog);
							break;
						case GET_LAST_POS:
							NetStatusListener.mClickflag = false;
							try {
								isLocNullValue = false;
								JSONObject json = new JSONObject(
										msg.obj.toString());
								if (json.has("data") && !json.isNull("data")) {
									JSONObject data = json.getJSONObject("data");
									if (data.has("wd")
											&& data.has("jd")
											&& !TextUtils.isEmpty(data.getString("wd"))
											&& !TextUtils.isEmpty(data.getString("jd"))) {
										mLastPosLatLng = new LatLng(
												data.getDouble("wd"),
												data.getDouble("jd"));
									} else {
										Toast.makeText(currentContext,
												getString(R.string.location_no_user_position),
												Toast.LENGTH_SHORT)
												.show();
										isLocNullValue = true;
										mLocationClient.start();
										if (progressDialog != null
												&& progressDialog.isShowing()) {
											progressDialog.dismiss();
										}
										return;
									}
									if (data.has("address")) {
										mLastPosAddr = data.getString("address")
												+ "\n";
									}
									if (data.has("getdate")) {
										mLastPosTime = "定位时间:"
												+ data.getString("getdate")
												+ "\n";
									}
									if (data.has("type")) {
										mLastPosType = "定位方式:"
												+ (data.getInt("type") == 0 ? "GPS"
												: "LBS") + "\n";
									}
									if (data.has("currPower")) {
										mLastPosPower = data.getInt("currPower");
									}
									
									if (progressDialog != null
											&& progressDialog.isShowing()) {
										progressDialog.setMessage(getString(R.string.location_get_user_position));
										progressDialog.dismiss();
									}
									
									if (!photoMap.containsKey("user")) {
										new Thread(new Runnable() {
											@Override
											public void run() {
												try {
													if (!TextUtils.isEmpty(headImage)) {
														headPhoto = getBitmap(headImage);
														photoMap.put("user",headPhoto);
													}
													handler.post(accountPhoneUi);
												} catch (IOException e) {
													e.printStackTrace();
												}
											}
										}).start();
									} else {
										headPhoto = photoMap.get("user");
										refreshAccountUI();
										
									}
								} else {
									Toast.makeText(currentContext,
											getString(R.string.location_no_user_position),
											Toast.LENGTH_SHORT)
											.show();
									Toast.makeText(currentContext,
											getString(R.string.location_no_user_position),
											Toast.LENGTH_SHORT)
											.show();
									isLocNullValue = true;
									mLocationClient.start();
									if (progressDialog != null
											&& progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
									return;
								}
							} catch (JSONException e) {
								if (progressDialog != null
										&& progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								Toast.makeText(currentContext,
										getString(R.string.location_receive_message_fail),
										Toast.LENGTH_SHORT)
										.show();
								e.printStackTrace();
							}
							break;
						case MONITOR:
							break;
					}
					if (locDialog != null && locDialog.isShowing()) {
						if (loc_type != ADD_GEO_FENCE
								&& loc_type != EDIT_GEO_FENCE
								&& loc_type != DELETE_GEO_FENCE
								&& loc_type != GET_NOW_POS) {
							locDialog.dismiss();
						}
					}
					break;
				case START_LAST_POS:
					break;
				case Constants.GET_DATA_FAIL:
					if (locDialog != null && locDialog.isShowing()) {
						Toast.makeText(LocationActivity.this,
								getString(R.string.location_receive_message_fail),
								Toast.LENGTH_SHORT)
								.show();
						locDialog.dismiss();
					}
					if (progressDialog != null && progressDialog.isShowing()) {
						Toast.makeText(LocationActivity.this,
								getString(R.string.location_receive_message_fail),
								Toast.LENGTH_SHORT)
								.show();
						progressDialog.dismiss();
					}
					break;
				case ONMAPLOADED:
					MarkerOptions oopInfo = null;
					View view = LayoutInflater.from(getApplicationContext())
							.inflate(R.layout.location_detail_item, null);
					Button location_geo_fence_btn = (Button) view.findViewById(R.id.location_geo_fence_btn);
					location_geo_fence_btn.setVisibility(View.VISIBLE);
					location_geo_fence_btn.setText(historySpecificLoc.get(0));
					BitmapDescriptor item = BitmapDescriptorFactory.fromView(view);
					
					Point p = mMap.getProjection()
							.toScreenLocation(historyLatLng.get(historyLatLng.size() - 1));
					p.y -= 160;
					LatLng llInfo = mMap.getProjection().fromScreenLocation(p);
					oopInfo = new MarkerOptions().position(llInfo)
							.icon(item)
							.zIndex(9);
					mMap.addOverlay(oopInfo);
					break;
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_location);
		initData();
		addTopBarToHead();
		initLocWidget();
		initialMap();
		hideMapDefaultWidget();
		setTouchListener();
		
		getUserInfoFromServer(mStrMemberUserId);		
		mHomeLayout1.setOnClickListener(mLocationClickListener);
		mHomeLayout2.setOnClickListener(mLocationClickListener);
		mHomeLayout3.setOnClickListener(mLocationClickListener);

		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				if (marker == fenceMarker) {
					mMap.clear();
					function_SeekBar.setProgress(DEFAULT_PROGRESS);
					function_meter.setText(DEFAULT_DISTANCE + MIN_DISTANCE + "");
					function_name.setText("");
					function_layout.setVisibility(View.VISIBLE);
					drawGeoFence(currentFenceLoc, DEFAULT_DISTANCE
							+ MIN_DISTANCE);
					isCreateGeoFence = true;
				}
				return true;
			}
		});
		
		AppManager.getAppManager().addActivity(this);
	}
	
	private OnClickListener mLocationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {		
			headPhoto = null;
			switch (v.getId()) {
				case R.id.activity_location_me_iv://立刻定位本机				

					if(isOPenGPS(currentContext)) {
						isLocNullValue = false;
						mLocationClient.start();
					} else {
						queryOpenGPSDialog();
					}
					break;
				case R.id.location_home_layout1://立刻定位用户
					getNowPosFromServer(mLongMemberUserId);
					break;
				case R.id.location_home_layout2:// 点击历史轨迹
					mMap.clear();
					isExit = false;
					activity_location_mapView_default.setVisibility(View.GONE);
					
					activity_location_mapView_history_place.setVisibility(View.VISIBLE);

					isOpenGeoFence = false;
					alterTopBarToHead(HISTORY_TYPE);
					getHistoryLocationFromServer(mStrMemberUserId,
							selectDateString);
					break;
				case R.id.location_home_layout3:// 点击电子围栏
					mMap.clear();
					isExit = false;
					alterTopBarToHead(GEOFENCE_TYPE);
					isOpenGeoFence = true;
					activity_location_mapView_default.setVisibility(View.GONE);
					// 连接服务器
					getGeoFenceFromServer(mStrMemberUserId);
					break;
				case R.id.activity_location_chat_iv:
					Log.e("mLocationClickListener", "activity_location_chat_iv");
					launchWeiChat();
					break;
				case R.id.activity_location_phone_iv:
					launchPhoneCall();
					break;
				case R.id.activity_location_more_iv:
					launchDeviceFunctions();
					break;
				
				default:
					break;
			}
		}
	};
	
	private void launchDeviceFunctions(){
		
		Intent intent = new Intent(LocationActivity.this,
		ListDeviceFunctionsActivity_sm.class);
		intent.putExtra("userId", mStrMemberUserId);
		intent.putExtra("deviceCode", mBindedDeviceCode);
		intent.putExtra("devId",deviceId);
		intent.putExtra("deviceType", Integer.toString(mBindedDeviceType));
		intent.putExtra("deviceTelNum", mBindedDeviceTelNum);
		intent.putExtra("userName", userName);
		intent.putExtra("familyid", mFamilyId);
		intent.putExtra("familyKeyPersonId", mFamilyKeyPersonId);
		if(mBindedDeviceType == 6){
			intent.putExtra("watchAccout", linkTopBindAccount);
		}		
		startActivityForResult(intent, 0);
	}
	
	private void launchWeiChat(){
		
		Intent intent = null;
		
		if (mBindedDeviceType == 1){
			//学生机 
			Toast.makeText(LocationActivity.this,
					"学生机暂不支持微聊功能！",
					Toast.LENGTH_SHORT).show();
			return;
		}else if (mBindedDeviceType == 2){
			//老人机
			Toast.makeText(LocationActivity.this,
					"老人机暂不支持微聊功能！",
					Toast.LENGTH_SHORT).show();
			return;
		}else if (mBindedDeviceType == 6){
			intent = new Intent(LocationActivity.this, WatchChatActivity_SM.class);
			intent.setAction("");
			intent.putExtra("deviceid", mBindedDeviceCode);
			intent.putExtra("deviceaccount", linkTopBindAccount);
			intent.putExtra("title", userName);
			intent.putExtra("headimg", headImage);
			intent.putExtra("deviceNum", mBindedDeviceTelNum);
			intent.putExtra(FsConstants.TYPE_ADD_FLAG, FsConstants.TYPE_SEND_PERSONAL_MSG);
			startActivity(intent);
		}
	}
	
	private void launchPhoneCall(){		
		Intent intent = null;		
		intent = new Intent(Intent.ACTION_CALL);
		String call = "tel:" + mBindedDeviceTelNum;
		intent.setData(Uri.parse(call));
		startActivity(intent);
	}
	
	@Override
	public void onStop() {
		// 可能home键出去，查看widget了，所以在此处发广播立即通知widget刷新
		Intent intent = new Intent(FsConstants.WIDGET_REFRESH_SYNC);
		sendBroadcast(intent);
		super.onStop();
		if (option != null && option.isOpenGps()) {
			option.setOpenGps(false);
			mLocationClient.setLocOption(option);
		}
	}
	
	@Override
	public void onDestroy() {
		AppManager.getAppManager().removeActivity(this);
		super.onDestroy();
		if (option != null && option.isOpenGps()) {
			option.setOpenGps(false);
			mLocationClient.setLocOption(option);
		}
		
		if (headPhoto != null) {
			headPhoto.recycle();
		}
		mLocationClient.stop();
		mLocationClient = null;
	
		
		mMapView.onDestroy();
		if (mHomeListener != null) {
			mHomeListener.stopWatch();
		}
		
		handler.removeCallbacksAndMessages(null);
		mLinkTopHandler.removeCallbacksAndMessages(null);
	}
	
	private void addTopBarToHead() {
		fl_header_location_activity = (FrameLayout) findViewById(R.id.fl_header_location_activity);
		location_calendar = (RelativeLayout) findViewById(R.id.location_calendar);
		if (actionBar != null) {
			fl_header_location_activity.removeView(actionBar);
		}
		actionBar = TopBarUtils.createCustomActionBar(this,
				R.drawable.btn_back_selector,
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						finish();
					}
				},
				userName,
				R.drawable.btn_edit,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        modifyFamilyMemTitle();
                    }
                });
		fl_header_location_activity.addView(actionBar);

	}

    private void modifyFamilyMemTitle(){
        Intent intent = new Intent(LocationActivity.this,
                UiEditFamilyName.class);
        intent.setAction(UiEditFamilyName.EDIT_FAMILY_MEM_MARK);
        intent.putExtra("userid", mStrMemberUserId);
        intent.putExtra("familyid", mFamilyId);
        intent.putExtra("name", userName);
        startActivityForResult(intent, 1);
    }
	
	private String historyCalendar = "";
	
	private Date clickDate;
	
	private void alterTopBarToHead(int type) {
		String title = "";
		switch (type) {
			case FREQUENT_TYPE:
				title = getString(R.string.title_frequent);
				break;
			case HISTORY_TYPE:
				if (!TextUtils.isEmpty(historyCalendar)) {
					title = historyCalendar;
					historyCalendar = "";
				} else {
					title = sdf.format(new Date());
				}
				break;
			case GEOFENCE_TYPE:
				title = getString(R.string.title_geofence);
				break;
			default:
				break;
		}
		
		if (type == HISTORY_TYPE) {
			fl_header_location_activity.removeView(actionBar);
			actionBar = TopBarUtils.createCustomActionBarCancleCalendar(LocationActivity.this,
					R.drawable.btn_back_selector,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							recoverDefault();
						}
					},
					title,
					"",
					null);
			fl_header_location_activity.addView(actionBar);
			
			actionBar.getTvCalendarTitle()
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (location_calendar.getVisibility() == View.GONE) {
								selectCalendar();
							} else {
								location_calendar.setVisibility(View.GONE);
							}
						}
					});
			actionBar.getIvCalendarArrow()
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (location_calendar.getVisibility() == View.GONE) {
								selectCalendar();
							} else {
								location_calendar.setVisibility(View.GONE);
							}
						}
					});
			
		} else {
			fl_header_location_activity.removeView(actionBar);
			actionBar = TopBarUtils.createCustomActionBarCancle(LocationActivity.this,
					R.drawable.btn_back_selector,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							recoverDefault();
//							finish();
						}
					},
					title,
					"",
					null);
			fl_header_location_activity.addView(actionBar);
		}
	}
	
	private void selectCalendar() {
		location_calendar.setVisibility(View.VISIBLE);
		isExit = false;
		
		// 获取日历控件对象
		calendarView = (CalendarView) findViewById(R.id.calendar);
		calendarLeft = (ImageButton) findViewById(R.id.calendarLeft);
		calendarRight = (ImageButton) findViewById(R.id.calendarRight);
		calendarCenter = (TextView) findViewById(R.id.calendarCenter);
		calendarCenter.setTextColor(Color.WHITE);
		calendarCenter.setText(calendarView.getYearAndmonth());

		calendarView.setOnItemClickListener(new CalendarItemClickListener());
		calendarLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				calendarCenter.setText(calendarView.clickLeftMonth());
			}
		});
		calendarRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				calendarCenter.setText(calendarView.clickRightMonth());
			}
		});
	}
	
	class CalendarItemClickListener implements OnItemClickListener {
		@Override
		public void OnItemClick(Date date) {
			
			try {
				selectDate = sdf.parse(calendarView.getFormatLocDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String s = calendarView.getFormatLocDate();
			location_calendar.setVisibility(View.GONE);
			
			Calendar calendar = Calendar.getInstance();
			long cur = calendar.getTimeInMillis();
			calendar.setTime(selectDate);
			long tar = calendar.getTimeInMillis();
			
			calendarView.backClickedDate();
			mMap.clear();
			activity_location_mapView_default.setVisibility(View.GONE);
			if (cur < tar) {

				Toast.makeText(LocationActivity.this,
						getString(R.string.location_future_data),
						Toast.LENGTH_SHORT).show();
				activity_location_history_tv.setText(getString(R.string.location_future_data));
				mHistoryListview.setVisibility(View.GONE);
				if (calendarView != null) {

					calendarView.clickedDate = calendarView.curDate = clickDate;
					calendarView.invalidate();
				}
				return;
			} else {
				historyCalendar = s;
				alterTopBarToHead(HISTORY_TYPE);
				getHistoryLocationFromServer(mStrMemberUserId,
						s);
				clickDate = calendar.getTime();
			}
		}
	}
	
	public class GetLocationListener implements BDLocationListener {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			
			Log.i(TAG,
					"" + location.getLatitude() + " , "
							+ location.getLongitude());
			
			/**
			 * 无效经纬度
			 */
			if (String.valueOf((location.getLatitude())).equals("4.9E-324")
					&& (String.valueOf(location.getLongitude())).equals("4.9E-324")) {
				Log.i(TAG, "Net Exception");
				return;
			}
			
			updateMap(location);
			Log.i(TAG, location.getTime());
			
		}
		
		private void updateMap(BDLocation location) {
			home_layout_baidumap.setVisibility(View.VISIBLE);
			MyLocationData locData = new MyLocationData.Builder().accuracy(50)
					.direction(100)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude())
					.build();
			mMap.setMyLocationData(locData);
			//            setCenterPoint(new LatLng(location.getLatitude(),
			//                    location.getLongitude()));
			if (!TextUtils.isEmpty(location.getAddrStr())) {
				currentAddress = location.getAddrStr();
			}
			mLocationClient.stop();
			currentLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			// setUserPosition(currentLocation);
//			uploadLocationFromServer(Preferences.getInstance(getApplicationContext())
//					.getUserID());
			//uploadLocationFromServer(mStrWatchDeviceId);
			NetStatusListener.mClickflag = false;

			if (!isLocNullValue && !photoMap.containsKey("account")) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Preferences preferences = Preferences.getInstance(currentContext);
							String pic = preferences.getHeadPath();
							
							if (!TextUtils.isEmpty(pic)) {
								headPhoto = getBitmap(pic);
								photoMap.put("account", headPhoto);
							}
							handler.post(smartPhoneUi);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			} else if (!isLocNullValue && photoMap.containsKey("account")) {
				headPhoto = photoMap.get("account");
				refreshSmartHomeUI();
			} else {
				log.d("daitm-----photoMap contains 'user'");
				setCenterPoint(currentLocation);
				
			}
		}
	}	
	/**
	 * 初始化变量
	 */
	private void initLocWidget() {
		home_layout_baidumap = (LinearLayout) findViewById(R.id.home_layout_baidumap);
		home_layout_baidumap.setVisibility(View.GONE);
		mMapView = (MapView) findViewById(R.id.activity_location_mapView);
		mMap = mMapView.getMap();
		mUiSettings = mMap.getUiSettings();
		mUiSettings.setRotateGesturesEnabled(false);
		
		mLocationClient = new LocationClient(this);

		currentContext = getApplicationContext();
		activity_location_mapView_default = (LinearLayout) findViewById(R.id.activity_location_mapView_default_layout);
		activity_location_mapView_history_place = (RelativeLayout) findViewById(R.id.activity_location_mapView_history_place);
		activity_location_refresh_btn = (ImageView) findViewById(R.id.activity_location_refresh_btn);
		activity_location_history_tv = (TextView) findViewById(R.id.activity_location_history_tv);		
		
		mHistoryListview = (ListView) findViewById(R.id.activity_location_mapView_history_listview);
		
		activity_location_refresh_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getHistoryLocationFromServer(mStrMemberUserId,
						historyCalendar);
			}
		});
		
		imgChat = (ImageView)this.findViewById(R.id.activity_location_chat_iv);
		imgChat.setAlpha(0.8f);
		imgChat.setOnClickListener(mLocationClickListener);
		imgPhone = (ImageView)this.findViewById(R.id.activity_location_phone_iv);
		imgPhone.setAlpha(0.8f);
		imgPhone.setOnClickListener(mLocationClickListener);
		imgMore = (ImageView)this.findViewById(R.id.activity_location_more_iv);
		imgMore.setAlpha(0.8f);
		imgMore.setOnClickListener(mLocationClickListener);
		// 长按删除
		mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng latLng) {

			}
		});
		
		mMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onMapClick(LatLng latLng) {
				if (calendarView != null) {
					//            calendarView.backRightDate();
					calendarView.clickedDate = calendarView.curDate = calendarView.todayDate = new Date();
					calendarView.invalidate();
				}
				mEditPosition = -100;
				if (location_calendar.getVisibility() == View.VISIBLE) {
					location_calendar.setVisibility(View.GONE);
				}
				if (isOpenGeoFence) {
					mFenceType = TYPE_FORBIDDEN;
					initFenceSetupWidget();
					editFenceLoc = null;
					function_cancle.setText(getString(R.string.common_btn_no));
					for (int i = 0, l = geoList.size(); i < l; i++) {
						if (calculateDistance(latLng,
								(LatLng) geoList.get(i).get("latLng"),
								(Integer) geoList.get(i).get("radius"))) {
							editFenceLoc = geoList.get(i);
							tmpFenceLoc = geoList.get(i);
							mEditPosition = i;
						}
					}

					
					if (isCreateGeoFence && editFenceLoc == null
							&& tmpFenceLoc != null) {
						editFenceLoc = tmpFenceLoc;
						editFenceLoc.put("name", function_name.getText()
								.toString());
						Toast.makeText(LocationActivity.this,
								getString(R.string.location_current_geofence),
								Toast.LENGTH_SHORT).show();
					}
					
					if (editFenceLoc != null) {
						isCreateGeoFence = true;
						function_cancle.setText(getString(R.string.common_btn_delete));
						function_layout.setVisibility(View.VISIBLE);
						currentFenceLoc = (LatLng) editFenceLoc.get("latLng");
						currentFenceRadius = (Integer) editFenceLoc.get("radius");
						function_SeekBar.setProgress((int) ((currentFenceRadius)
								* 1.0 / (MAX_DISTANCE + MIN_DISTANCE) * 100));
						function_meter.setText(currentFenceRadius + "");
						function_name.setText(editFenceLoc.get("name") + "");
						mSeq = (String) editFenceLoc.get("seq");
						mFenceType = (Integer) editFenceLoc.get("alarmType");
						initFenceSetupWidget();
						// geoList.remove(editFenceLoc);
						mMap.clear();
						drawGeoFence(currentFenceLoc, currentFenceRadius);
						for (Map<String, Object> map : geoList) {
							if (map != editFenceLoc) {
								drawGeoFence(((LatLng) map.get("latLng")),
										(Integer) map.get("radius"));
							} else {
								log.d("daitm----draw fence except edit one");
								
							}
						}
						return;
					}
					
					if (!isCreateGeoFence) {
						if (geoList.size() >= 5) {
							Toast.makeText(currentContext,
									getString(R.string.location_5_geofence),
									Toast.LENGTH_SHORT).show();
							return;
						}
						mMap.clear();
						for (Map<String, Object> map : geoList) {
							drawGeoFence(((LatLng) map.get("latLng")),
									(Integer) map.get("radius"));
						}
						setGeoFenceCenter(latLng);
						currentFenceLoc = latLng;
						// isCreateGeoFence = true;
					} else {
						if (tmpFenceLoc != null) {
							return;
						}
						Toast.makeText(LocationActivity.this,
								getString(R.string.location_current_geofence),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		function_ok = (Button) findViewById(R.id.function_ok);
		function_cancle = (Button) findViewById(R.id.function_cancle);
		function_name = (EditText) findViewById(R.id.function_name);
		function_meter = (EditText) findViewById(R.id.function_meter);
		function_meter.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
			                          int arg3) {

				log.d("daitm----onTextChanged");
				isTexting = true;
				if (TextUtils.isEmpty(function_meter.getText().toString())) {
					currentFenceRadius = 0;
				} else {
					currentFenceRadius = Integer.parseInt(function_meter.getText()
							.toString());
				}
				mMap.clear();
				function_SeekBar.setProgress((int) ((currentFenceRadius) * 1.0
						/ (MAX_DISTANCE + MIN_DISTANCE) * 100));
				drawGeoFence(currentFenceLoc, currentFenceRadius);
				for (Map<String, Object> map : geoList) {
					if (editFenceLoc != null && map != editFenceLoc) {
						drawGeoFence(((LatLng) map.get("latLng")),
								(Integer) map.get("radius"));
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
			                              int arg2, int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				isTexting = false;
				log.d("daitm----afterTextChanged");
			}
		});
		
		function_layout = (RelativeLayout) findViewById(R.id.function_layout);
		function_SeekBar = (SeekBar) findViewById(R.id.function_SeekBar);
		function_SeekBar.setMax(100);
		function_SeekBar.setProgress(DEFAULT_PROGRESS);
		function_SeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
			                              boolean fromUser) {
				if (!isTexting) {
					mMap.clear();
					currentFenceRadius = (MAX_DISTANCE + MIN_DISTANCE)
							* progress / 100;
					if (currentFenceRadius < 100) {
						currentFenceRadius = 100;
					}
					function_meter.setText(currentFenceRadius + "");
					drawGeoFence(currentFenceLoc, currentFenceRadius);
					for (Map<String, Object> map : geoList) {
						if (editFenceLoc != null && map != editFenceLoc) {
							drawGeoFence(((LatLng) map.get("latLng")),
									(Integer) map.get("radius"));
						}
					}

				}
			}
		});
		
		function_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				geoMap = new HashMap<String, Object>();
				geoMap.put("latLng", currentFenceLoc);
				geoMap.put("radius", currentFenceRadius);
				if (TextUtils.isEmpty(function_name.getText().toString())) {
					Toast.makeText(currentContext,
							getString(R.string.location_geofence_empty_name),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (100 > currentFenceRadius || 2000 < currentFenceRadius) {
					Toast.makeText(currentContext,
							getString(R.string.location_geofence_scale),
							Toast.LENGTH_SHORT).show();
					return;
				}
				//              geoList.add(geoMap);
				// radiusList.add(currentRadius);
				isCreateGeoFence = false;
				function_layout.setVisibility(View.GONE);
				
				Long fenceId = null;
				if (editFenceLoc != null) {
					fenceId = (Long) editFenceLoc.get("fenceId");
				}
				addGeoFenceByServer(fenceId);
			}
		});
		function_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initCancelDialog();
			}
		});
		
		activity_location_mapView_frequent_place = (RelativeLayout) findViewById(R.id.activity_location_mapView_frequent_place);
		activity_location_frequentLocation_img = (TextView) findViewById(R.id.activity_location_frequentLocation_img);
		activity_location_frequentLocation_tv = (TextView) findViewById(R.id.activity_location_frequentLocation_tv);
		activity_location_frequentLocation_btn = (ImageView) findViewById(R.id.activity_location_frequentLocation_btn);
		activity_location_mapView_frequent_place_list_layout = (LinearLayout) findViewById(R.id.activity_location_mapView_frequent_place_list_layout);
		activity_location_mapView_frequent_place_listview = (ListView) findViewById(R.id.activity_location_mapView_frequent_place_listview);
		
		activity_location_frequentLocation_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity_location_mapView_frequent_place.setVisibility(View.GONE);
				activity_location_mapView_frequent_place_list_layout.setVisibility(View.VISIBLE);
			}
		});
		
		activity_location_mapView_fence_list_layout = (LinearLayout) findViewById(R.id.activity_location_mapView_fence_list_layout);
		activity_location_mapView_fence_listview = (ListView) findViewById(R.id.activity_location_mapView_fence_listview);
		activity_location_mapView_fence = (RelativeLayout) findViewById(R.id.activity_location_mapView_fence);
		activity_location_fence_img = (TextView) findViewById(R.id.activity_location_fence_img);
		activity_location_fence_tv = (TextView) findViewById(R.id.activity_location_fence_tv);
		activity_location_fence_btn = (ImageView) findViewById(R.id.activity_location_fence_btn);
		activity_location_fence_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity_location_mapView_fence.setVisibility(View.GONE);
				activity_location_mapView_fence_list_layout.setVisibility(View.VISIBLE);
			}
		});
		
		location_fence_1 = (RelativeLayout) findViewById(R.id.location_fence_1);
		location_fence_2 = (RelativeLayout) findViewById(R.id.location_fence_2);
		location_fence_3 = (RelativeLayout) findViewById(R.id.location_fence_3);
		location_fence_4 = (RelativeLayout) findViewById(R.id.location_fence_4);
		location_fence_img_1 = (ImageView) findViewById(R.id.location_fence_img_1);
		location_fence_img_2 = (ImageView) findViewById(R.id.location_fence_img_2);
		location_fence_img_3 = (ImageView) findViewById(R.id.location_fence_img_3);
		location_fence_img_4 = (ImageView) findViewById(R.id.location_fence_img_4);
		location_fence_1.setOnClickListener(fenceListener);
		location_fence_2.setOnClickListener(fenceListener);
		location_fence_3.setOnClickListener(fenceListener);
		location_fence_4.setOnClickListener(fenceListener);
		initFenceSetupWidget();
		
		mHomeLayout1 = (LinearLayout) findViewById(R.id.location_home_layout1);
		mHomeLayout2 = (LinearLayout) findViewById(R.id.location_home_layout2);
		mHomeLayout3 = (LinearLayout) findViewById(R.id.location_home_layout3);
		String type = Preferences.getInstance(getApplicationContext())
				.getDeviceModel();
		
		//if it is the current user
		isCurrentUser = getIntent().getExtras().getBoolean("isCurrentUser");
		if (isCurrentUser) {
			actionBar.getIvRightIcon().setVisibility(View.GONE);
			mHomeLayout2.setVisibility(View.GONE);			
		}

		if (type.equalsIgnoreCase("gk309") || type.equalsIgnoreCase("gs300")
				|| isCurrentUser) {
			mHomeLayout1.setVisibility(View.GONE);
		}
	}
	
	private void initCancelDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LocationActivity.this);
		if (editFenceLoc != null && !isKeyBack) {
			builder.setMessage(getString(R.string.location_choice_delete_geofence1));
		} else {
			builder.setMessage(getString(R.string.location_choice_delete_geofence2));
		}
		builder.setNegativeButton(getString(R.string.common_btn_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						function_layout.setVisibility(View.GONE);
						function_name.setText("");
						if (editFenceLoc != null && !isKeyBack) {
							if (isOpenGeoFence) {
								delFenceLoc = editFenceLoc;
								if (delFenceLoc != null) {
									deleteGeoFenceByServer((Long) delFenceLoc.get("fenceId"));
								}
							}
						} else {
							if (fenceMarker != null) {
								fenceMarker.remove();
							}
							dialog.dismiss();
							isCreateGeoFence = false;
							mMap.clear();
							for (Map<String, Object> map : geoList) {
								drawGeoFence((LatLng) map.get("latLng"),
										(Integer) map.get("radius"));
							}

							editFenceLoc = null;
							tmpFenceLoc = null;
							isKeyBack = false;
						}
					}
				});
		builder.setPositiveButton(getString(R.string.common_btn_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						isKeyBack = false;
					}
				});
		builder.create().show();
	}
	
	private void initData() {
		
		mStrMemberUserId = getIntent().getStringExtra("memeber_userId");
		mLongMemberUserId = Long.parseLong(mStrMemberUserId);
		headImage = getIntent().getStringExtra("imgurl");
		userName = getIntent().getStringExtra("memeber_alias");
		mFamilyKeyPersonId = getIntent().getStringExtra("familykeypersonid"); 
		selectDateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		mFamilyId = getIntent().getStringExtra("familyid");
	}
	
	/**
	 * 初始化地图参数
	 */
	private void initialMap() {
		
		mMap.setMyLocationEnabled(false);

		mLocationClient.registerLocationListener(new GetLocationListener());
		option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(Preferences.getInstance(currentContext)
				.getInterval() * 1000 * 60);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		//        mLocationClient.start();
		option = null;
	}

	
	private void clearMap() {
		mMap.clear();
		fenceList.clear();
		radiusList.clear();
		isCreateGeoFence = false;
		isOpenGeoFence = false;
		isShowFirstGeoFence = true;
	}
	
	private void setPosition(LatLng latLng, int pos) {
		View view = LayoutInflater.from(this)
				.inflate(R.layout.location_detail_item, null);
		TextView location_detail_item_tv = (TextView) view.findViewById(R.id.location_detail_item_tv);
		location_detail_item_tv.setVisibility(View.VISIBLE);
		location_detail_item_tv.setText(pos + "");
		BitmapDescriptor item = BitmapDescriptorFactory.fromView(view);
		OverlayOptions oop = new MarkerOptions().position(latLng).icon(item);
		mMap.addOverlay(oop);
	}
	
	/**
	 * 设置电子围栏中心点
	 *
	 * @param latLng
	 */
	private void setGeoFenceCenter(LatLng latLng) {
		View view = LayoutInflater.from(this)
				.inflate(R.layout.location_detail_item, null);
		Button location_geo_fence_btn = (Button) view.findViewById(R.id.location_geo_fence_btn);
		location_geo_fence_btn.setVisibility(View.VISIBLE);
		BitmapDescriptor item = BitmapDescriptorFactory.fromView(view);
		OverlayOptions oop = new MarkerOptions().position(latLng).icon(item);
		fenceMarker = (Marker) mMap.addOverlay(oop);
	}
	
	private void setUserPosition(LatLng latLng) {
		mMap.clear();
		OverlayOptions oop = null;
		
		View view = LayoutInflater.from(this)
				.inflate(R.layout.location_user_center, null);
		TextView location_user_center_tv = (TextView) view.findViewById(R.id.location_user_center_tv);
		LocationHead location_user_center_img = (LocationHead) view.findViewById(R.id.location_user_center_img);
		if (headPhoto != null) {
			headPhoto = BitmapUtil.getRoundedCornerBitmap(headPhoto);
		}
		location_user_center_img.setLocBitmap(headPhoto);
		location_user_center_tv.setText(historySpecificLoc.get(0));
		BitmapDescriptor item = BitmapDescriptorFactory.fromView(view);
		oop = new MarkerOptions().position(latLng).icon(item);
		
		userMarker = (Marker) (mMap.addOverlay(oop));
	}
	
	private void setUserLastPosition(LatLng lastLat, String loc) {
		home_layout_baidumap.setVisibility(View.VISIBLE);
		setCenterPoint(lastLat);
		mMap.clear();
		OverlayOptions oop = null;
		View view = LayoutInflater.from(this)
				.inflate(R.layout.location_last_pos, null);
		TextView location_last_title = (TextView) view.findViewById(R.id.location_last_title);
		TextView location_last_time = (TextView) view.findViewById(R.id.location_last_time);
		TextView location_last_power = (TextView) view.findViewById(R.id.location_last_power);
		TextView location_last_type = (TextView) view.findViewById(R.id.location_last_type);
		LocationHead location_user_center_img = (LocationHead) view.findViewById(R.id.location_user_center_img);
		if (headPhoto != null) {
			headPhoto = BitmapUtil.getRoundedCornerBitmap(headPhoto);
		}else{
            headPhoto = BitmapUtil.getRoundedCornerBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.nickname));
        }
		location_user_center_img.setLocBitmap(headPhoto);
		if (loc.split("\n").length == 3) {
			location_last_title.setText(loc.split("\n")[0]);
			location_last_time.setText(loc.split("\n")[1]);
			location_last_type.setText(loc.split("\n")[2]);
			location_last_power.setVisibility(View.GONE);
		} else if (loc.split("\n").length == 4) {
			location_last_title.setText(loc.split("\n")[0]);
			location_last_time.setText(loc.split("\n")[1]);
			location_last_type.setText(loc.split("\n")[2]);
			if (mLastPosPower > 0) {
				location_last_power.setText("电量:"
						+ (mLastPosPower == 255 ? "正在充电" : (mLastPosPower + "%")));
				changePowerColor(location_last_power);
			}
			else {
				location_last_power.setVisibility(View.GONE);
			}
		}
		String type = Preferences.getInstance(getApplicationContext())
				.getDeviceModel();
		if (type.equalsIgnoreCase("gk309") || type.equalsIgnoreCase("gs300")) {
			location_last_type.setVisibility(View.VISIBLE);
		} else {
			location_last_type.setVisibility(View.GONE);
		}
		
		BitmapDescriptor item = BitmapDescriptorFactory.fromView(view);
		if (item != null) {
			oop = new MarkerOptions().position(lastLat).icon(item);
			userMarker = (Marker) (mMap.addOverlay(oop));
		}
	}
	
	/**
	 * 设置用户为中心点
	 *
	 * @param latLng
	 */
	private void setUserCenterFocus(LatLng latLng) {
		setCenterPoint(latLng);
		setUserPosition(latLng);
	}
	
	
	class FrequentPlaceAdapter extends BaseAdapter {
		
		private List<String> places;
		
		public FrequentPlaceAdapter(List<String> places) {
			this.places = places;
		}
		
		@Override
		public int getCount() {
			return places.size();
		}
		
		@Override
		public Object getItem(int pos) {
			return places.get(pos);
		}
		
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		
		@Override
		public View getView(final int pos, View view, ViewGroup viewgroup) {
			
			view = LayoutInflater.from(LocationActivity.this)
					.inflate(R.layout.location_frequent_item, null);
			Getter getter;
			if (view != null) {
				getter = new Getter();
				getter.frequent_location_item_name = (TextView) view.findViewById(R.id.frequent_location_item_name);
				getter.frequent_location_item_seri = (TextView) view.findViewById(R.id.frequent_location_item_seri);
				getter.frequent_location_item_layout = (LinearLayout) view.findViewById(R.id.frequent_location_item_layout);
				view.setTag(getter);
			} else
				getter = (Getter) view.getTag();
			
			getter.frequent_location_item_name.setText(places.get(pos));
			getter.frequent_location_item_seri.setText("" + (pos + 1));
			getter.frequent_location_item_layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					setCenterPoint(frequentList.get(pos));
					activity_location_mapView_frequent_place.setVisibility(View.VISIBLE);
					activity_location_mapView_frequent_place_list_layout.setVisibility(View.GONE);
					activity_location_frequentLocation_tv.setText(places.get(pos));
					activity_location_frequentLocation_img.setText(""
							+ (pos + 1));
				}
			});
			
			return view;
		}
		
		public class Getter {
			private TextView frequent_location_item_name,
					frequent_location_item_seri;
			
			private LinearLayout frequent_location_item_layout;
		}
	}
	
	private List<HistoryItem> HistoryPlaces = new ArrayList<HistoryItem>();
	
	private int HistorySize = 0;
	
	class HistoryAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return HistoryPlaces.size();
		}
		
		@Override
		public Object getItem(int pos) {
			return HistoryPlaces.get(pos);
		}
		
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		
		@Override
		public View getView(final int pos, View view, ViewGroup viewgroup) {
			
			view = LayoutInflater.from(LocationActivity.this)
					.inflate(R.layout.history_item, null);
			Getter getter;
			if (view != null) {
				getter = new Getter();
				getter.history_item_seri = (TextView) view.findViewById(R.id.history_item_seri);
				getter.history_item_time = (TextView) view.findViewById(R.id.history_item_time);
				getter.history_item_loc = (TextView) view.findViewById(R.id.history_item_loc);
				getter.history_item_layout = (LinearLayout) view.findViewById(R.id.history_item_layout);
				view.setTag(getter);
			} else
				getter = (Getter) view.getTag();
			
			getter.history_item_loc.setText(HistoryPlaces.get(pos).itemLoc);
			getter.history_item_time.setText(HistoryPlaces.get(pos).itemTime);
			getter.history_item_seri.setText("" + (HistorySize - pos));
			getter.history_item_layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setCenterPoint(historyLatLng.get(pos));
					drawHistroy(pos);
				}
			});
			
			return view;
		}
		
		public class Getter {
			private TextView history_item_seri;
			
			private TextView history_item_time;
			
			private TextView history_item_loc;
			
			private LinearLayout history_item_layout;
		}
	}
	
	class FenceAdapter extends BaseAdapter {
		
		private List<Map<String, Object>> fences;
		
		public FenceAdapter(List<Map<String, Object>> fences) {
			this.fences = fences;
		}
		
		@Override
		public int getCount() {
			return fences.size();
		}
		
		@Override
		public Object getItem(int pos) {
			return fences.get(pos);
		}
		
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		
		@Override
		public View getView(final int pos, View view, ViewGroup viewgroup) {
			
			view = LayoutInflater.from(LocationActivity.this)
					.inflate(R.layout.location_frequent_item, null);
			Getter getter;
			if (view != null) {
				getter = new Getter();
				getter.fence_item_name = (TextView) view.findViewById(R.id.frequent_location_item_name);
				getter.fence_item_seri = (TextView) view.findViewById(R.id.frequent_location_item_seri);
				getter.fence_item_layout = (LinearLayout) view.findViewById(R.id.frequent_location_item_layout);
				view.setTag(getter);
			} else
				getter = (Getter) view.getTag();
			
			if (TextUtils.isEmpty(fences.get(pos).get("name") + "")) {
				getter.fence_item_name.setText("暂无名称");
			} else {
				getter.fence_item_name.setText(fences.get(pos).get("name") + "");
			}
			getter.fence_item_seri.setText("" + (pos + 1));
			getter.fence_item_layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setCenterPoint((LatLng) fences.get(pos).get("latLng"));
					activity_location_mapView_fence.setVisibility(View.VISIBLE);
					activity_location_mapView_fence_list_layout.setVisibility(View.GONE);
					activity_location_fence_tv.setText(fences.get(pos)
							.get("name") + "");
					activity_location_fence_img.setText("" + (pos + 1));
				}
			});
			
			return view;
		}
		
		public class Getter {
			private TextView fence_item_name, fence_item_seri;
			
			private LinearLayout fence_item_layout;
		}
	}
	
	/**
	 * 设置中心点
	 */
	private void setCenterPoint(LatLng cenpt) {
		MapStatus mMapStatus = null;
		MapStatusUpdate mMapStatusUpdate = null;
		// 定义地图状态
		if (cenpt != null) {
			mMapStatus = new MapStatus.Builder().zoom(15.0f)
					.target(cenpt)
					.build();
		}
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		if (mMapStatus != null) {
			mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		}
		// 改变地图状态
		if (mMapStatusUpdate != null && mMap != null)//bug#71989
		{
			mMap.setMapStatus(mMapStatusUpdate);
		} else {
			log.e("daitm----mMapStatusUpdate is null");
		}
	}
	
	private void recoverDefault() {
		if (calendarView != null) {
			//            calendarView.backRightDate();
			calendarView.clickedDate = calendarView.curDate = calendarView.todayDate = new Date();
			calendarView.invalidate();
		}
		mFenceType = TYPE_FORBIDDEN;
		isExit = true;
		frequentSpecificLoc.clear();
		if (frequentPlaceAdapter != null) {
			frequentPlaceAdapter.notifyDataSetChanged();
		}
		
		activity_location_mapView_history_place.setVisibility(View.GONE);
		location_calendar.setVisibility(View.GONE);
		activity_location_mapView_frequent_place_list_layout.setVisibility(View.GONE);
		activity_location_mapView_fence.setVisibility(View.GONE);
		activity_location_mapView_fence_list_layout.setVisibility(View.GONE);
		activity_location_mapView_frequent_place.setVisibility(View.GONE);
		function_layout.setVisibility(View.GONE);
		activity_location_mapView_default.setVisibility(View.VISIBLE);		
		addTopBarToHead();
		clearMap();
		getLastPosFromServer(mLongMemberUserId);
		isKeyBack = false;
		NetStatusListener.mClickflag = false;
	}
	
	private TextView location_user_center_tv;
	
	private LocationHead location_user_center_img;
	
	private View historyView;
	
	private View viewFir;
	
	private TextView location_detail_item_tv;
	
	private void drawHistroy(int pos) {
		long t1 = System.currentTimeMillis();
		if (historyLatLng == null) {
			Toast.makeText(currentContext,
					getString(R.string.location_no_data),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (mMap != null) {
			mMap.clear();
		}
		if (historyLatLng.size() > 0)//服务器已做倒序处理
		{
			if (historyLatLng.size() > 1) {
				viewFir = LayoutInflater.from(this)
						.inflate(R.layout.location_detail_item, null);
				location_detail_item_tv = (TextView) viewFir.findViewById(R.id.location_detail_item_tv);
				location_detail_item_tv.setVisibility(View.VISIBLE);
				location_detail_item_tv.setText("");
				location_detail_item_tv.setBackgroundResource(R.drawable.location_2);
				BitmapDescriptor itemFir = BitmapDescriptorFactory.fromView(viewFir);
				OverlayOptions oopFir = new MarkerOptions().position(historyLatLng.get(historyLatLng.size() - 1))
						.icon(itemFir);
				mMap.addOverlay(oopFir);
			}
			//画最后一个点，展示头像
			historyView = LayoutInflater.from(this)
					.inflate(R.layout.location_user_center, null);
			location_user_center_tv = (TextView) historyView.findViewById(R.id.location_user_center_tv);
			location_user_center_img = (LocationHead) historyView.findViewById(R.id.location_user_center_img);
			location_user_center_img.setLocBitmap(headPhoto);
			location_user_center_tv.setText(historySpecificLoc.get(pos));
			BitmapDescriptor item = BitmapDescriptorFactory.fromView(historyView);
			OverlayOptions oop = new MarkerOptions().position(historyLatLng.get(pos))
					.icon(item);
			mMap.addOverlay(oop);
			
			activity_location_history_tv.setText(historySpecificLoc.get(pos));
		}
		
		if (points.size() > 1) {
			OverlayOptions ooPolyline = new PolylineOptions().width(10)
					.color(0xAA4c9ada)
					.points(points);
			mMap.addOverlay(ooPolyline);
		}
		long t2 = System.currentTimeMillis();
		log.d("daitm----drawhistory takes " + (t2 - t1));
	}
	
	private void drawGeoFence(LatLng llCircle, int radius) {
		// 添加圆
		if (llCircle != null) {
			OverlayOptions ooCircle = new CircleOptions().fillColor(0xAAA5E9FB)
					.center(llCircle)
					.stroke(new Stroke(1, 0xAAA5E9FB))
					.radius(radius);
			// MarkerOptions
			mMap.addOverlay(ooCircle);
		} else {
			log.d("daitm---drawGeoFence error, latlng is null");
		}
	}
	
	// 隐藏缩放控件
	private void hideMapDefaultWidget() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int childCount = mMapView.getChildCount();
				for (int i = 0; i < childCount; i++) {
					if (i > 0) {// 0为百度地图
						View child = mMapView.getChildAt(i);
						child.setVisibility(View.GONE);
					}
				}
			}
		}).start();
	}
		
	/**
	 * 获取电子围栏
	 */
	private void getGeoFenceFromServer(String userId) {
		
		if(!checkNetWork()){
			activity_location_fence_tv.setText(getString(R.string.no_network));
			activity_location_fence_img.setVisibility(View.GONE);
			activity_location_fence_btn.setEnabled(false);
			return;
		}			
		if (mBindedDeviceType == 6){
			linkInstance.getSafeZone(mBindedDeviceCode);
		}else{
			handler.sendEmptyMessage(Constants.GET_DATA_START);
			loc_type = LOC_GEOFENCE;
			JSONObject obj = new JSONObject();
			try {
				obj.put("deviceId", deviceId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			HttpUtil.postRequest(obj,
					Constants.GET_FENCE,
					handler,
					Constants.GET_DATA_SUCCESS,
					Constants.GET_DATA_FAIL);
		}
	}
	
	private void addGeoFenceByServer(Long fenceId) {
		
		if(!checkNetWork()){
			return;
		}
		
		if (mBindedDeviceType == 6){
			handler.sendEmptyMessage(Constants.GET_DATA_START);
			if (editFenceLoc != null) {
				updateGeoFenceToLinkServer(Long.toString(fenceId));
			}else{
				addGeoFenceToLinkServer();
			}			
		}else{
			loc_type = ADD_GEO_FENCE;
			handler.sendEmptyMessage(Constants.GET_DATA_START);
			JSONObject obj = new JSONObject();
			JSONArray list = new JSONArray();
			try {
				JSONObject geoItem = new JSONObject();
				geoItem.put("jd", ((LatLng) geoMap.get("latLng")).longitude);// 经度
				geoItem.put("wd", ((LatLng) geoMap.get("latLng")).latitude);// 纬度
				geoItem.put("radius", geoMap.get("radius"));// 半径
				geoItem.put("height", 0);// 高度
				geoItem.put("width", 0);// 宽度
				list.put(geoItem);
				if (editFenceLoc != null) {
					obj.put("id", fenceId);
					obj.put("seq", mSeq);
				}
				obj.put("userId", mStrMemberUserId);
				obj.put("name", function_name.getText().toString());// 名称
				obj.put("point", list);
				
				obj.put("deviceId",deviceId);
				obj.put("alarmType", mFenceType);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			HttpUtil.postRequest(obj,
					Constants.SET_FENCE,
					handler,
					Constants.GET_DATA_SUCCESS,
					Constants.GET_DATA_FAIL);
		}
	}
	
	private void addGeoFenceToLinkServer(){
		
		watchFenceMap.clear();
		
		watchFenceMap.put("addr", "");
		watchFenceMap.put("alias", function_name.getText().toString());
		LatLng gdLatLng = LocationUtil.BaiduToGaode(((LatLng)geoMap.get("latLng")).longitude
				                                    ,((LatLng) geoMap.get("latLng")).latitude);
		watchFenceMap.put("latitude", Double.toString(gdLatLng.latitude));
		watchFenceMap.put("longitude", Double.toString(gdLatLng.longitude));
		watchFenceMap.put("in_ts", Integer.toString(1000));
		watchFenceMap.put("out_ts", Integer.toString(1000));
		watchFenceMap.put("days", "63");
		watchFenceMap.put("radius", Integer.toString((Integer) geoMap.get("radius")));
		watchFenceMap.put("safe_level", "2");
		linkInstance.addSafeZone(mBindedDeviceCode, watchFenceMap);
		
	}
	
	private void updateGeoFenceToLinkServer(String fenceid){
		
		watchFenceMap.clear();
		
		watchFenceMap.put("id", fenceid);
		watchFenceMap.put("addr", "test");
		watchFenceMap.put("alias", function_name.getText().toString());
		LatLng gdLatLng = LocationUtil.BaiduToGaode(((LatLng)geoMap.get("latLng")).longitude
				                                    ,((LatLng) geoMap.get("latLng")).latitude);
		watchFenceMap.put("latitude", Double.toString(gdLatLng.latitude));
		watchFenceMap.put("longitude", Double.toString(gdLatLng.longitude));
		watchFenceMap.put("in_ts", Integer.toString(1000));
		watchFenceMap.put("out_ts", Integer.toString(1000));
		watchFenceMap.put("days", "63");
		watchFenceMap.put("radius", Integer.toString((Integer) geoMap.get("radius")));
		watchFenceMap.put("safe_level", "2");
		linkInstance.updateSafeZone(mBindedDeviceCode, watchFenceMap);
		
	}

	

	private void deleteGeoFenceByServer(Long fenceId) {
		
		if (!checkNetWork()){
			return;
		}
		if(mBindedDeviceType == 6){
			handler.sendEmptyMessage(Constants.GET_DATA_START);
			linkInstance.delSafeZone(mBindedDeviceCode, Long.toString(fenceId));
		}else{
			loc_type = DELETE_GEO_FENCE;
			handler.sendEmptyMessage(Constants.GET_DATA_START);
			JSONObject obj = new JSONObject();
			try {
				obj.put("fenceId", fenceId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			HttpUtil.postRequest(obj,
					Constants.DELETE_GEO_FENCE,
					handler,
					Constants.GET_DATA_SUCCESS,
					Constants.GET_DATA_FAIL);
		}
	}
	
	/**
	 * 获取历史轨迹
	 */
	private void getHistoryLocationFromServer(String userId, String historyDate) {
		
		if(!checkNetWork()){
			activity_location_history_tv.setText(getString(R.string.no_network));
			return;
		}
		if (mBindedDeviceType == 6) {
			linkInstance.getHistoryLocation(mBindedDeviceCode, historyDate);
			handler.sendEmptyMessage(Constants.GET_DATA_START);
		} else {
				handler.sendEmptyMessage(Constants.GET_DATA_START);
				loc_type = LOC_HISTORY;
				JSONObject obj = new JSONObject();
				try {
					obj.put("userId", userId);
					obj.put("date", historyDate);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				HttpUtil.postRequest(obj,
						Constants.GET_TRACK,
						handler,
						Constants.GET_DATA_SUCCESS,
						Constants.GET_DATA_FAIL);
		}
	}
	
	/**
	 * 上传位置
	 */
	private void uploadLocationFromServer(String userId, double jd, double wd, String address, Handler handle) {
		if (checkNetWork()) {

			JSONObject obj = new JSONObject();
			try {
				obj.put("userId", userId);
				obj.put("jd", jd);
				obj.put("wd", wd);
				obj.put("address", address);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			HttpUtil.postRequest(obj,
					Constants.UPLOAD,
					handle,
					Constants.GET_DATA_SUCCESS,
					Constants.GET_DATA_FAIL);
		}
	}
	
	/**
	 * 立刻请求地理位置
	 */
	private void getNowPosFromServer(long _userId) {
		
		if (mBindedDeviceType == 6 && checkNetWork()) {
			linkInstance.sendNorLocationSMSToken(mBindedDeviceCode, mBindedDeviceTelNum);
			handler.sendEmptyMessage(Constants.GET_DATA_START);
		} else {
			if (checkNetWork()) {
				loc_type = GET_NOW_POS;
				handler.sendEmptyMessage(Constants.GET_DATA_START);
				JSONObject obj = new JSONObject();
				try {

					obj.put("userId", _userId);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				HttpUtil.postRequest(obj,
						Constants.GET_NOW_POS2,
						handler,
						Constants.GET_DATA_SUCCESS,
						Constants.GET_DATA_FAIL);
			}
		}
	}
	
	private void initLinkSDK(String linkaccount) {
		linkInstance = LinkTopSDKUtil.getInstance();
		linkInstance.initSDK(this, mLinkTopHandler);
		linkInstance.setupAccount(linkaccount, "888888");

	}
	
	private Runnable delayLocation = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			linkInstance.getLocationByToken(mBindedDeviceCode, latestToken);
//			linkInstance.getLatestPosition(mStrWatchDeviceId);
		}
	};
	
	private Handler mLinkTopHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case LinkTopSDKUtil.LINK_SDK_LATEST_POS_ACTION:
					Log.e("-LATEST POS ACTION-", "-arg1=" + msg.arg1 + "--obj=" + msg.obj);
					if (msg.arg1 == 200) {
						if (!TextUtils.isEmpty(msg.obj.toString())){
							NetStatusListener.mClickflag = false;
							parserPositionData(msg.obj.toString());
						}else{
							//在次发起获取数据的请求
							mLinkTopHandler.postDelayed(delayLocation, 20000);
						}
					}else{
						
						if (locDialog != null && locDialog.isShowing()) {
							locDialog.dismiss();
						}
						Toast.makeText(currentContext,
								"定位失败，请重试！",
								Toast.LENGTH_SHORT)
								.show();
						NetStatusListener.mClickflag = false;
					}
					break;
				
				case LinkTopSDKUtil.LINK_SDK_START_LOC_ACTION:
					Log.e("-LINK_SDK_START_LOC_ACTION-", "-arg1=" + msg.arg1 + "--obj=" + msg.obj);
					if (msg.arg1 == 200) {
						if (locDialog != null && locDialog.isShowing()) {
							locDialog.dismiss();
						}
						NetStatusListener.mClickflag = false;
						linkInstance.getLatestPosition(mBindedDeviceCode);
					}
					break;
				case LinkTopSDKUtil.LINK_SDK_NOR_LOCATION_TOKEN:
					latestToken = msg.obj.toString();
					mLinkTopHandler.postDelayed(delayLocation, 20000);
					break;
				
				case GOT_BAIDU_XY_RESULT:
					if (msg.arg1 == 200) {
						if (locDialog != null && locDialog.isShowing()) {
							locDialog.dismiss();
						}
						Bundle bundleData = msg.getData();
						Log.e("---wzl GOT_BAIDU_XY_RESULT--", "--address=" + bundleData.getString("address"));
						mLastPosLatLng = new LatLng(Double.parseDouble(bundleData.getString("latitude"))
								, Double.parseDouble(bundleData.getString("longitude")));
						String range = "(" + bundleData.getString("range") + "米范围)"; 
						mLastPosAddr = bundleData.getString("address") + range +"\n";
						mLastPosTime = "定位时间:" + timestamps2string(1000 * Long.parseLong(bundleData.getString("timestamp"))) + "\n";
						mLastPosType = "" + "\n";
						mLastPosPower = Integer.parseInt(bundleData.getString("batteryLevel"));
						uploadLocationFromServer(mStrMemberUserId,mLastPosLatLng.longitude
								                 ,mLastPosLatLng.latitude
								                 ,mLastPosAddr
								                 ,mLinkTopHandler);
						if (!photoMap.containsKey("user")) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {

										if (!TextUtils.isEmpty(headImage)) {
											headPhoto = getBitmap(headImage);
											photoMap.put("user",
													headPhoto);
										}
										handler.post(accountPhoneUi);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}).start();
						} else {
							headPhoto = photoMap.get("user");
							refreshAccountUI();
						}
					} else {
						Toast.makeText(currentContext,
								getString(R.string.location_no_user_position),
								Toast.LENGTH_SHORT)
								.show();
						isLocNullValue = true;
						mLocationClient.start();
					}
					break;
					
				case LinkTopSDKUtil.LINK_SDK_GET_HISTORY_LOCATION:
					if (locDialog != null && locDialog.isShowing()) {
						locDialog.dismiss();
					}
					if (msg.arg1 != 200){
						Toast.makeText(currentContext,
								"获取历史轨迹失败，请稍后重试！",
								Toast.LENGTH_SHORT)
								.show();
						return;
					}
					if(msg.obj != null){
						handleShowWatchHistoryLocation(msg.obj.toString());
					}					
					break;
					
				case LinkTopSDKUtil.LINK_SDK_ADD_SAFE_ZONE:
				case LinkTopSDKUtil.LINK_SDK_EDT_SAFE_ZONE:
					editFenceLoc = null;
					tmpFenceLoc = null;		
					// 从服务器获取电子围栏的列表

					if (msg.arg1 == 200){
						mLinkTopHandler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								linkInstance.getSafeZone(mBindedDeviceCode);
							}
						}, 4000);
						
					}else{
						if (locDialog != null && locDialog.isShowing()) {
							locDialog.dismiss();
						}
						Toast.makeText(currentContext,
								"电子围栏添加或更新失败，请重试!",
								Toast.LENGTH_SHORT)
								.show();
					}
					break;
				case LinkTopSDKUtil.LINK_SDK_GET_SAFE_ZONE:
					if (locDialog != null && locDialog.isShowing()) {
						locDialog.dismiss();
					}
					if (msg.arg1 == 200){
						handleGetSafeZone(msg.obj.toString());
					}else{
						Toast.makeText(currentContext,
								"获取电子围栏失败，请重试!",
								Toast.LENGTH_SHORT)
								.show();
					}
					break;
				case LinkTopSDKUtil.LINK_SDK_DEL_SAFE_ZONE:
					tmpFenceLoc = null;
					isCreateGeoFence = false;
					if (msg.arg1 == 200){
						mLinkTopHandler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								linkInstance.getSafeZone(mBindedDeviceCode);
							}
						}, 4000);
					}else{
						if (locDialog != null && locDialog.isShowing()) {
							locDialog.dismiss();
						}
						Toast.makeText(currentContext,
								"删除电子围栏失败，请重试!",
								Toast.LENGTH_SHORT)
								.show();
					}
					break;
				default:
					break;
			}
		}
	};
	
	private String timestamps2string(long timestamps) {

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamps));
	}
	
	
	private void parserPositionData(final String positionResult) {
		
		new Thread(new Runnable() {
			String address;
			String batteryLevel;
			double longitude;
			double latitude;
			String timestamps;
			String range;
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObject = new JSONObject(positionResult);
					address = jsonObject.getString("addr");
					batteryLevel = jsonObject.getString("battery");
					longitude = jsonObject.getDouble("longitude");
					latitude = jsonObject.getDouble("latitude");
					timestamps = jsonObject.getString("timestamp");
					range = jsonObject.getString("range");

					LatLng dbLat = LocationUtil.GaodeToBaidu(longitude, latitude);
					Message msg = mLinkTopHandler.obtainMessage();
					Bundle bundleData = new Bundle();
					bundleData.putString("address", address);
					bundleData.putString("batteryLevel", batteryLevel);
					bundleData.putString("longitude", Double.toString(dbLat.longitude));
					bundleData.putString("latitude", Double.toString(dbLat.latitude));
					bundleData.putString("timestamp", timestamps);
					bundleData.putString("range", range);
					msg.what = GOT_BAIDU_XY_RESULT;
					msg.arg1 = 200;
					msg.setData(bundleData);
					msg.sendToTarget();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	
	/**
	 * 在服务器取最新位置
	 */
	private void getLastPosFromServer(long tmpUserId) {
		if (mBindedDeviceType == 6 && checkNetWork()) {
			linkInstance.getLatestPosition(mBindedDeviceCode);
		} else {
			if (checkNetWork()) {
				handler.sendEmptyMessage(START_LAST_POS);
				loc_type = GET_LAST_POS;
				JSONObject obj = new JSONObject();
				try {
					obj.put("userId", tmpUserId);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				HttpUtil.postRequest(obj,
						Constants.GET_LAST_LOC,
						handler,
						Constants.GET_DATA_SUCCESS,
						Constants.GET_DATA_FAIL);
			}
		}
	}
	
	private boolean calculateDistance(LatLng currentLoc, LatLng fenceLoc,
	                                  int radius) {
		if (DistanceUtil.getDistance(currentLoc, fenceLoc) > radius) {
			return false;
		} else {
			return true;
		}
	}
	
	private void setTouchListener() {
		
		activity_location_mapView_frequent_place.setOnTouchListener(this);
		activity_location_mapView_frequent_place_list_layout.setOnTouchListener(this);
		function_layout.setOnTouchListener(this);
		home_layout_baidumap.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		location_calendar.setVisibility(View.GONE);
	
		return false;
	}
	
	private boolean isKeyBack = false;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		isKeyBack = true;
		if (mNetStatusListener != null
				&& mNetStatusListener.getCustomToast() != null) {
			return mNetStatusListener.cancleToast();
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isExit && !isCreateGeoFence) {
				recoverDefault();
			} else if (isCreateGeoFence) {
				initCancelDialog();
			} else {
				finish();
			}
		}
		return false;
	}
	
	private LastPosListener mLastPosListener = new LastPosListener() {
		
		@Override
		public void getLastPosListener() {
			getLastPosFromServer(mLongMemberUserId);
		}
	};
	
	public interface LastPosListener {
		void getLastPosListener();
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		home_layout_baidumap.setVisibility(View.VISIBLE);
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Log.d("", "daitm-------抱歉，未能找到结果");
		}
		if (mMap != null) {
			mMap.clear();
		}
		
		MapStatus mMapStatus = null;
		MapStatusUpdate mMapStatusUpdate = null;
		// 定义地图状态
		if (result.getLocation() != null) {
			mMapStatus = new MapStatus.Builder().zoom(12.0f)
					.target(result.getLocation())
					.build();
		}
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		if (mMapStatus != null) {
			mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		}
		// 改变地图状态
		if (mMapStatusUpdate != null)//bug#71989
		{
			mMap.setMapStatus(mMapStatusUpdate);
		} else {
			log.e("daitm----mMapStatusUpdate is null");
		}
	}
	
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
	}
	
	@Override
	public void finish() {
		HttpUtil.initUrl(HttpUtil.BASE_URL_SMART_TYPE);
		if (handler != null) {
			handler.removeCallbacksAndMessages(this);
		}
		if (mNetStatusListener != null) {
			mNetStatusListener.setActivityFinish();
			NetStatusListener.mClickflag = false;
			mNetStatusListener.setRunning(false);
			mNetStatusListener.cancleToast();
		}
		super.finish();
	}
	
	//////////////////////////////////////////////////////////////////
	public final static int TYPE_INSIDE = 1;
	
	public final static int TYPE_OUTSIDE = 2;
	
	public final static int TYPE_BOTHSIDE = 3;
	
	public final static int TYPE_FORBIDDEN = 4;
	
	private RelativeLayout location_fence_1;
	
	private RelativeLayout location_fence_2;
	
	private RelativeLayout location_fence_3;
	
	private RelativeLayout location_fence_4;
	
	private ImageView location_fence_img_1;
	
	private ImageView location_fence_img_2;
	
	private ImageView location_fence_img_3;
	
	private ImageView location_fence_img_4;
	
	private int mFenceType = 0;
	
	private String mSeq = "";
	
	@Override
	protected void onStart() {
		super.onStart();
		
	}
	
	private void initFenceSetupWidget() {
		if (mFenceType != TYPE_INSIDE && mFenceType != TYPE_OUTSIDE
				&& mFenceType != TYPE_BOTHSIDE && mFenceType != TYPE_FORBIDDEN) {
			mFenceType = TYPE_FORBIDDEN;
		}
		location_fence_img_1.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
		location_fence_img_2.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
		location_fence_img_3.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
		location_fence_img_4.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
		switch (mFenceType) {
			case TYPE_INSIDE:
				location_fence_img_1.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
				break;
			case TYPE_OUTSIDE:
				location_fence_img_2.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
				break;
			case TYPE_BOTHSIDE:
				location_fence_img_3.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
				break;
			case TYPE_FORBIDDEN:
				location_fence_img_4.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
				break;
		}
	}
	
	private OnClickListener fenceListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			location_fence_img_1.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
			location_fence_img_2.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
			location_fence_img_3.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
			location_fence_img_4.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
			switch (v.getId()) {
				case R.id.location_fence_1:
					location_fence_img_1.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
					mFenceType = TYPE_INSIDE;
					break;
				case R.id.location_fence_2:
					location_fence_img_2.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
					mFenceType = TYPE_OUTSIDE;
					break;
				case R.id.location_fence_3:
					location_fence_img_3.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
					mFenceType = TYPE_BOTHSIDE;
					break;
				case R.id.location_fence_4:
					location_fence_img_4.setImageResource(R.drawable.ic_action_bar_right_save_pressed);
					mFenceType = TYPE_FORBIDDEN;
					break;
			}
		}
	};
	
	class HistoryItem {
		public String itemTime = "";
		
		public String itemLoc = "";
	}
	
	private void changePowerColor(TextView powerColorView) {
		if (mLastPosPower == 255) {
			return;
		}
		if (mLastPosPower >= 70 && mLastPosPower <= 100) {
			powerColorView.setTextColor(Color.GREEN);
		} else if (mLastPosPower >= 30 && mLastPosPower < 70) {
			powerColorView.setTextColor(Color.YELLOW);
		} else if (mLastPosPower >= 0 && mLastPosPower < 30) {
			powerColorView.setTextColor(Color.RED);
		}
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
	
	private GK309FenceListener mGK309FenceListener = new GK309FenceListener() {
		@Override
		public void getGK309FenceSuccess() {
			getGeoFenceFromServer(mStrMemberUserId);
		}
	};
	
	public interface GK309FenceListener {
		void getGK309FenceSuccess();
	}
	
	private void handleShowWatchHistoryLocation(String historyData){
		
		NetStatusListener.mClickflag = false;
		HistoryPlaces.clear();
		try {
			mHistoryListview.setVisibility(View.GONE);
			historyLatLng.clear();
			historySpecificLoc.clear();
			activity_location_history_tv.setText(getString(R.string.location_history));
			JSONObject data = new JSONObject("{\"data\"" +":"+ historyData + "}");
			if (!data.isNull("data")) {
				JSONArray array = data.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					if ((i+1)%2 == 1){
						continue;
					}
					JSONObject obj = array.getJSONObject(i);

						String addr = obj.getString("addr");
						String time = timestamps2string(1000*obj.getLong("timestamp"));
						historyLatLng.add(LocationUtil.GaodeToBaidu(obj.getDouble("longitude")
								                                    , obj.getDouble("latitude")));
						historySpecificLoc.add(addr);
						HistoryItem item = new HistoryItem();
						item.itemLoc = addr;
						item.itemTime = time;
						HistoryPlaces.add(item);
				}
				HistorySize = HistoryPlaces.size();
				
				points.clear();
				for (LatLng point : historyLatLng) {
					points.add(point);
				}
				
				if (mHistoryAdapter == null) {
					mHistoryAdapter = new HistoryAdapter();
					mHistoryListview.setAdapter(mHistoryAdapter);
				} else {
					mHistoryAdapter.notifyDataSetChanged();
				}
				
				if (!originIsEmpty) {
					originHistoryLatLng = historyLatLng;
					originIsEmpty = true;
				}
				
				if (historyLatLng.size() > 0
						&& !photoMap.containsKey("user")) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								if (!TextUtils.isEmpty(headImage)) {
									headPhoto = getBitmap(headImage);
									photoMap.put("user",
											headPhoto);
								}
								handler.post(runnableUi);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				} else {
					headPhoto = photoMap.get("user");
					refreshRunnableUI();
				}

				if (historySpecificLoc.size() > 0) {
					LatLng lastPos = historyLatLng.get(0);
					setUserCenterFocus(lastPos);
					activity_location_history_tv.setText(historySpecificLoc.get(0));
					mHistoryListview.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(LocationActivity.this,
							getString(R.string.location_no_data),
							Toast.LENGTH_SHORT)
							.show();
					activity_location_history_tv.setText(getString(R.string.location_no_data));
				}
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						drawHistroy(0);
					}
				}, 1000);
				//                          }
			}
		} catch (JSONException e) {
			if (progressDialog != null
					&& progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			Toast.makeText(currentContext,
					getString(R.string.location_receive_message_fail),
					Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
	}
	
	private void handleGetSafeZone(String safeZoneData){
		
		NetStatusListener.mClickflag = false;
		try {
			geoList.clear();
			mMap.clear();
			JSONObject data = new JSONObject(safeZoneData);
			String pre_geofence = "";
			for (Iterator<String> keys = data.keys(); keys.hasNext();){
				String key1 = keys.next(); 
				Long fenceId = Long.parseLong(key1);
				JSONObject fenceDetail = data.getJSONObject(key1);
				JSONObject fencePdata = fenceDetail.getJSONObject("p");
				String name = fencePdata.getString("alias");
				int alarmType = 0;
				LatLng latLng = LocationUtil.GaodeToBaidu(fencePdata.getDouble("longitude")
						                                  , fencePdata.getDouble("latitude"));
				int radius = fencePdata.getInt("radius");
				drawGeoFence(latLng, radius);
				pre_geofence = latLng.longitude
						+ "-" + latLng.latitude
						+ radius + ","
						+ pre_geofence;
				geoMap = new HashMap<String, Object>();
				geoMap.put("latLng", latLng);
				geoMap.put("radius", radius);
				geoMap.put("fenceId", fenceId);
				geoMap.put("name", name);
				geoMap.put("alarmType", alarmType);				
				geoList.add(geoMap);
			}			
			if (geoList.size() > 0) {
				activity_location_fence_img.setVisibility(View.VISIBLE);
				activity_location_fence_btn.setEnabled(true);
				if (isShowFirstGeoFence) {
					int ser = geoList.size();
					activity_location_fence_tv.setText((String) geoList.get(ser - 1)
							.get("name"));
					setCenterPoint((LatLng) geoList.get(ser - 1)
							.get("latLng"));
					activity_location_fence_img.setText(""
							+ ser);
					isShowFirstGeoFence = false;
				} else {

					int pos = (mEditPosition == -100 ? (geoList.size() - 1)
							: mEditPosition);//add pos ? edit pos
					if (pos == geoList.size()) {
						pos = geoList.size() - 1;//delete pos
					}
					activity_location_fence_tv.setText((String) geoList.get(pos)
							.get("name"));
					setCenterPoint((LatLng) geoList.get(pos)
							.get("latLng"));
					activity_location_fence_img.setText(""
							+ (pos + 1));
				}
			} else {
				activity_location_fence_tv.setText(getString(R.string.location_no_fence));
				activity_location_fence_img.setVisibility(View.GONE);
				activity_location_fence_btn.setEnabled(false);
			}
			activity_location_mapView_fence.setVisibility(View.VISIBLE);
			
			if (!TextUtils.isEmpty(pre_geofence)) {
				Preferences.getInstance(currentContext)
						.setGeofence(pre_geofence.substring(0,
								pre_geofence.length() - 1));
			}
			
			if (fenceAdapter == null) {
				fenceAdapter = new FenceAdapter(geoList);
				activity_location_mapView_fence_listview.setAdapter(fenceAdapter);
			} else {
				fenceAdapter.notifyDataSetChanged();
			}
			
		} catch (JSONException e) {
			if (progressDialog != null
					&& progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			Toast.makeText(currentContext,
					getString(R.string.location_receive_fence_fail),
					Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
		
	}
	
	private boolean parserUserInfo(String result){
		String[] userDeviceInfo = null;
		try {
			userDeviceInfo = UserInfoUtil.parserUserDevice(result);
			if(userDeviceInfo != null){
				deviceId = userDeviceInfo[4];
				mBindedDeviceType = Integer.parseInt(userDeviceInfo[0]);
				mBindedDeviceCode = userDeviceInfo[1];
				mBindedDeviceTelNum = userDeviceInfo[2];
				if (mBindedDeviceType == 6){
					linkTopBindAccount = userDeviceInfo[3];
				}
			}else{
				mBindedDeviceType = 4;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;			
		}
		return true;
		
		// get device function list  T.B.D
		
	}
	
	private Handler mUserInfoHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UserInfoUtil.USER_INFO_MSG_ID:
				if(parserUserInfo(msg.obj.toString())){
					if(mBindedDeviceType == 6){
						initLinkSDK(linkTopBindAccount);
					}
					getLastPosFromServer(mLongMemberUserId);
				}
				break;
			case UserInfoUtil.USER_INFO_ERROR_MSG_ID:
				break;
			default:
				break;
			}
		}
	};
	
	
	private void getUserInfoFromServer(String userId){
		
		JSONObject requestObject = new JSONObject();
		
		try {
			requestObject.put(UserInfoUtil.KEY_MEMBER_ID, userId);
			HttpUtil.postRequest(requestObject, Constants.GET_USER_INFO, mUserInfoHandler, 
								UserInfoUtil.USER_INFO_MSG_ID, 
								UserInfoUtil.USER_INFO_ERROR_MSG_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode==0 && resultCode == RESULT_OK){
			LocationActivity.this.finish();
		}else if (requestCode==1 && resultCode == RESULT_OK){
            userName = data.getStringExtra("familyname");
            actionBar.setTvTitleMsg(userName);
        }

		super.onActivityResult(requestCode, resultCode, data);
	}

	
	
	
}