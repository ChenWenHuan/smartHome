package com.smarthome.client2.fragment;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class LocationFragment extends CommonFragment implements
		OnGestureListener {

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

//	private String data1 = "{\"data\":[{\"jd\":\"32.033333\",\"wd\":\"118.733333\",\"address\":\"place111\"},"
//			+ "{\"jd\":\"32.025634\",\"wd\":\"118.733333\",\"address\":\"place222\"},"
//			+ "{\"jd\":\"32.036533\",\"wd\":\"118.733333\",\"address\":\"place333\"}]}";
//
//	private String data2 = "{\"data\":[{\"jd\":\"32.036533\",\"wd\":\"118.733333\",\"address\":\"江苏省南京市雨花台区软件大道11号\",\"times\":10},"
//			+ "{\"jd\":\"32.046533\",\"wd\":\"118.733333\",\"address\":\"江苏省南京市雨花台区软件大道12号\",\"times\":9},"
//			+ "{\"jd\":\"32.056533\",\"wd\":\"118.733333\",\"address\":\"江苏省南京市雨花台区软件大道13号\",\"times\":8},"
//			+ "{\"jd\":\"32.066533\",\"wd\":\"118.733333\",\"address\":\"江苏省南京市雨花台区软件大道14号\",\"times\":7},"
//			+ "{\"jd\":\"32.076533\",\"wd\":\"118.733333\",\"address\":\"江苏省南京市雨花台区软件大道15号\",\"times\":6}]}";
//
//	// private String data3 =
//	// "{\"data\":{\"id\":\"\",\"type\":\"0\",\"name\":\"\",\"list\"：[{\"jd\":\"32.036533\",\"wd\":\"118.733333\","
//	// +
//	// "\"radius\":\"1000\",\"height\":\"1000\",\"width\":\"10000\"}]}}";
//	private String data3 = "{\"data\":{\"list\"：[{\"jd\":\"32.036533\",\"wd\":\"118.733333\","
//			+ "\"radius\":\"1000\",\"height\":\"1000\",\"width\":\"10000\"}]}}";
//
//	private final static int GET_DATA_READY = 0;
//	private final static int GET_DATA_SUCCESS = 1;
//	private final static int GET_DATA_FAIL = 2;
//
//	private final static int LOC_FREQUENT = 1;
//	private final static int LOC_HISTORY = 2;
//	private final static int LOC_GEOFENCE = 3;
//	private final static int ADD_GEO_FENCE = 4;
//	private final static int EDIT_GEO_FENCE = 5;
//	private final static int DELETE_GEO_FENCE = 6;
//	private final static int LOC_UPLOAD = 7;
//	private final static int GET_NOW_POS = 8;
//	private final static int MONITOR = 9;
//
//	private int loc_type = -1;
//
//	private ProgressDialog locDialog;
//
//	private MainActivity ma;
//	private View containerView;
//	private MainActivity.MyOntouchListener listener;
//
//	// /////////////////////////home/////////////////////////////
//	private ViewFlipper viewFlipper;
//	private GestureDetector gestureDetector;
//
//	private TextView home_listening, home_calling, home_alarm_clock,
//			home_setup;
//	private TextView home_affairs_time, home_affairs_detail;
//	private TextView home_date_healthy;
//	private TextView home_calorie, home_steps, home_target;
//	private TextView home_sleep_h, home_sleep_m, home_awake_times,
//			home_sleep_quality;
//	private LinearLayout home_layout_function, home_affairs_layout,
//			home_healthy_sport_layout, home_healthy_sleep_layout,
//			home_layout_baidumap, home_jiaxiao_layout;
//
//	private int[] imageID = { R.drawable.t1, R.drawable.t2, R.drawable.t3 };
//	private String[] names = { "son", "daughter", "grandpa" };
//	private String[] places = { "place1", "place2", "place3" };
//	private int[] progresses = { 10, 60, 90 };
//	private LatLng[] latlngs = { new LatLng(32.033333, 118.733333),
//			new LatLng(32.025634, 118.733333),
//			new LatLng(32.036533, 118.733333) };
//	private BitmapUtil mBitmapUtil;
//
//	private AlertDialog dialog;
//	private boolean isLastLoc = false;
//	private boolean isMapView = false;
//
//	// /////////////////////////home/////////////////////////////
//
//	// /////////////////////////location start/////////////////////////
//
//	private List<LatLng> frequentList = new ArrayList<LatLng>();
//	private List<LatLng> historyLatLng = new ArrayList<LatLng>();
//	private List<LatLng> fenceList = new ArrayList<LatLng>();
//	private List<Integer> radiusList = new ArrayList<Integer>();
//	private List<Map<String, Object>> geoList = new ArrayList<Map<String, Object>>();
//	private Map<String, Object> geoMap = new HashMap<String, Object>();
//
//	private List<String> historySpecificLoc = new ArrayList<String>();
//	private List<String> frequentSpecificLoc = new ArrayList<String>();
//
//	private LatLng[] test1 = new LatLng[] { new LatLng(32.033333, 118.733333),
//			new LatLng(32.025634, 118.733333),
//			new LatLng(32.036533, 118.733333),
//			new LatLng(32.033333, 118.733333),
//			new LatLng(32.025634, 118.733333),
//			new LatLng(32.036533, 118.833333),
//			new LatLng(32.037563, 118.72633),
//			new LatLng(32.0456833, 118.734533),
//			new LatLng(32.056533, 118.733533),
//			new LatLng(32.066533, 118.963333) };
//
//	private String[] test2 = { "place1", "place2", "place3", "place4",
//			"place5", "place6", "place7", "place8", "place9", "place10" };
//
//	private final static double LATITUDE_NANJING = 32.047906;
//	private final static double LONGITUDE_NANJING = 118.794247;
//
//	private final static int FREQUENT_TYPE = 1;
//	private final static int HISTORY_TYPE = 2;
//	private final static int GEOFENCE_TYPE = 3;
//
//	private Context currentContext = null;
//	private static final String TAG = "LocationActivity";
//	private boolean isFirstLoc = true;
//	private LatLng theVeryFirstLocation = null, currentLocation = null;
//	private MapView mMapView = null;
//	private BaiduMap mMap = null;
//	private LocationClientOption option = null;
//
//	private int historicalPlacesNum = 0;
//	private LatLng[] historicalPlaces = null;
//	private Timer drawHistoricalPathTime = new Timer();
//	private TimerTask drawHistoricalPathTask = null;
//
//	private BitmapDescriptor mLocationMarker = null;
//	private List<LatLng> pathPoints = new ArrayList<LatLng>();
//	private LocationClient mLocationClient = null;
//	private GeoCoder mCoder = null; // 经纬度与地址转换
//	private String currentAddress = null;
//	private Bitmap bmp = null;
//	private LocationServiceImpl uploadLocation = null;// 上传地理位置经纬度\
//	private Location location = null; // 地理位置经纬度
//	private Marker userMarker,fenceMarker;
//
//	// ///////////////////////////location default page///////////////
//	private ImageView geoFence = null, historyLoc = null,
//			frequentLocations = null;
//	private RelativeLayout activity_location_mapView_default;
//	// ///////////////////////////location default page///////////////
//
//	// ///////////////////////////location frequent page///////////////
//	private RelativeLayout activity_location_mapView_frequent_place;
//	private TextView activity_location_frequentLocation_tv;
//	private ImageView activity_location_frequentLocation_btn;
//	private LinearLayout activity_location_mapView_frequent_place_list_layout;
//	private ListView activity_location_mapView_frequent_place_listview;
//	private FrequentPlaceAdapter frequentPlaceAdapter;
//	// ///////////////////////////location frequent page///////////////
//
//	// ///////////////////////////location geoFence page///////////////
//	private EditText function_meter;
//	private Button function_ok, function_cancle;
//	private RelativeLayout function_layout;
//	private SeekBar function_SeekBar;
//	private TextView activity_location_geofence_specific_tv;
//	private LatLng currentFenceLoc;
//	private int currentFenceRadius;
//	private Map<String, Object> editFenceLoc,delFenceLoc;
//
//	private final static int MAX_DISTANCE = 2;
//	private boolean isEdit = false;
//
//	private BitmapDescriptor draw_bd,draw_bd_head,draw_popup;
//	private boolean isOpenGeoFence = false;
//	private boolean isCreateGeoFence = false;
//	// ///////////////////////////location geoFence page///////////////
//
//	// ///////////////////////////location listener page///////////////
//	private LinearLayout location_function_listener;
//	private TextView location_function_listener_time;
//	private Button location_function_listener_btn;
//	// ///////////////////////////location listener page///////////////
//
//	private FrameLayout fl_header_location_activity;
//	private CustomActionBar actionBar;
//
//	// ////////////////////CalendarView///////////////////////////////
//	private RelativeLayout location_calendar;
//	private boolean calendarIsClick = false;
//	private CalendarView calendar;
//	private ImageButton calendarLeft, calendarRight;
//	private TextView calendarCenter;
//	private int index_on_line_touch_calendar = 100;
//	private Date selectDate;
//	private String selectDateString;
//	// ////////////////////CalendarView//////////////////////////////
//
//	// /////////////////////////location end//////////////////////////
//
//	private Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case GET_DATA_READY:
//				locDialog.setMessage("正在获取用户信息");
//				locDialog.show();
//				break;
//			case GET_DATA_SUCCESS:
//				locDialog.setMessage("获取完成");
//				locDialog.dismiss();
//				switch (loc_type) {
//				case LOC_FREQUENT:
//					try {
//						JSONObject data = new JSONObject(data2);
//						JSONArray array = data.getJSONArray("data");
//						for (int i = 0; i < array.length(); i++) {
//							JSONObject obj = array.getJSONObject(i);
//							LatLng latLng = new LatLng(obj.getDouble("jd"),
//									obj.getDouble("wd"));
//							frequentList.add(latLng);
//							frequentSpecificLoc.add(obj.getString("address"));
//						}
//
//						for (int i = 0; i < frequentList.size(); i++) {
//							setPosition(frequentList.get(i), i + 1);
//						}
//
//						if (frequentPlaceAdapter == null) {
//							frequentPlaceAdapter = new FrequentPlaceAdapter(
//									frequentSpecificLoc);
//							activity_location_mapView_frequent_place_listview
//									.setAdapter(frequentPlaceAdapter);
//						} else {
//							frequentPlaceAdapter.notifyDataSetChanged();
//						}
//						alterTopBarToHead(FREQUENT_TYPE);
//
//					} catch (JSONException e1) {
//						e1.printStackTrace();
//					}
//					break;
//				case LOC_HISTORY:
//					try {
//						JSONObject data = new JSONObject(data1);
//						// JSONObject data = new JSONObject(msg.obj.toString());
//						JSONArray array = data.getJSONArray("data");
//						for (int i = 0; i < array.length(); i++) {
//							JSONObject obj = array.getJSONObject(i);
//							LatLng latLng = new LatLng(obj.getDouble("jd"),
//									obj.getDouble("wd"));
//							historyLatLng.add(latLng);
//							historySpecificLoc.add(obj.getString("address"));
//						}
//
//						if (isLastLoc) {// home页滑动的历史轨迹最新地点，取最后一个位置
//							setUserCenterFocus(historyLatLng.get(historyLatLng
//									.size() - 1));
//							activity_location_geofence_specific_tv
//									.setText(historySpecificLoc
//											.get(historySpecificLoc.size() - 1));
//						} else {// 历史轨迹，取全部
//							drawHistroy();
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					break;
//				case LOC_GEOFENCE:
//					try {
//						JSONObject data = new JSONObject(msg.obj.toString());
//						JSONArray array = data.getJSONArray("data");
//						for (int i = 0; i < array.length(); i++) {
//							JSONArray arr = array.getJSONObject(i)
//									.getJSONArray("point");
//							for (int j = 0; j < arr.length(); j++) {
//								JSONObject obj = arr.getJSONObject(j);
//								LatLng latLng = new LatLng(obj.getDouble("jd"),
//										obj.getDouble("wd"));
//								int radius = obj.getInt("radius");
//								int fenceId = obj.getInt("fenceId");
//								drawGeoFence(latLng, radius);
//
//								geoMap = new HashMap<String, Object>();
//								geoMap.put("latLng", latLng);
//								geoMap.put("radius", radius);
//								geoMap.put("fenceId", fenceId);
//								geoList.add(geoMap);
//							}
//						}
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					break;
//				case ADD_GEO_FENCE:
//					Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT)
//							.show();
//					break;
//				case EDIT_GEO_FENCE:
//					Toast.makeText(getActivity(), "编辑成功", Toast.LENGTH_SHORT)
//							.show();
//					break;
//				case DELETE_GEO_FENCE:
//					Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT)
//							.show();
//					break;
//				case LOC_UPLOAD:
//					log.d("daitm--------uploadLocation------success");
//					break;
//				case GET_NOW_POS:
//					log.d("daitm--------getNowPos------success");
//					try {
//						JSONObject data = new JSONObject(msg.obj.toString());
//						JSONArray array = data.getJSONArray("data");
//						for (int i = 0; i < array.length(); i++) {
//							JSONObject obj = array.getJSONObject(i);
//							setUserCenterFocus(new LatLng(obj.getDouble("jd"), obj.getDouble("wd")));
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					break;
//				case MONITOR:
//					break;
//				}
//				break;
//			case GET_DATA_FAIL:
//				locDialog.setMessage("获取失败");
//				locDialog.dismiss();
//				break;
//			}
//		}
//
//	};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//		if (option != null && option.isOpenGps()) {
//			option.setOpenGps(false);
//			mLocationClient.setLocOption(option);
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		if (option != null && option.isOpenGps()) {
//			option.setOpenGps(false);
//			mLocationClient.setLocOption(option);
//		}
//
//		mLocationClient.stop();
//		mLocationClient = null;
//		uploadLocation.stop();
//
//		mMapView.onDestroy();
//		((MainActivity) getActivity()).unRegisterListener(listener);
//	}
//
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		ma = (MainActivity) activity;
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		containerView = inflater.inflate(R.layout.activity_location, null);
//		BluetoothManager4.getInstance(getActivity()).startBLE();
//		return containerView;
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//
//		initHomeWidget();
//		initLocWidget();
//		initialMap();
//		hideMapDefaultWidget();
//		addTopBarToHead(true);
////		ma.updateTitle("智慧家庭", "", true);
//
//		// setUserCenterFocus(latlngs[0]);
//		getHistoryLocationFromServer("", selectDateString);
//		isLastLoc = true;
//
//		uploadLocation = (LocationServiceImpl) ServiceFactory
//				.getLocationService(getActivity());
//		uploadLocation.start();
//
//		// 点击历史轨迹
//		historyLoc.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				alterTopBarToHead(HISTORY_TYPE);
//				drawHistroy();
//			}
//		});
//
//		// 点击电子围栏
//		geoFence.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				alterTopBarToHead(GEOFENCE_TYPE);
//				isOpenGeoFence = true;
//				activity_location_mapView_default.setVisibility(View.GONE);
//
//				// 连接服务器
//				getGeoFenceFromServer("");
//			}
//		});
//
//		// 点击常去地点
//		frequentLocations.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				activity_location_mapView_default.setVisibility(View.GONE);
//				activity_location_mapView_frequent_place
//						.setVisibility(View.VISIBLE);
//
//				isOpenGeoFence = false;
//				function_layout.setVisibility(View.GONE);
//
//				mLocationClient.stop();
//
//				// 连接服务器
//				getFrequentLocationFromServer("");
//
//				// for (int i = 0; i < test1.length; i++) {
//				// setPosition(test1[i], i + 1);
//				// }
//				//
//				// if (frequentPlaceAdapter == null) {
//				// frequentPlaceAdapter = new
//				// FrequentPlaceAdapter(frequentSpecificLoc);
//				// activity_location_mapView_frequent_place_listview
//				// .setAdapter(frequentPlaceAdapter);
//				// } else {
//				// frequentPlaceAdapter.notifyDataSetChanged();
//				// }
//				//
//				// alterTopBarToHead(FREQUENT_TYPE);
//			}
//		});
//
//		/**
//		 * 设置标注点击事件
//		 */
//		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
//
//			@Override
//			public boolean onMarkerClick(Marker marker) {
//				if(marker == userMarker){
//					getNowPosFromServer("");
//				}else if(marker == fenceMarker){
//					function_SeekBar.setProgress(0);
//					String radius = function_meter.getText().toString();
//					function_layout.setVisibility(View.VISIBLE);
//					drawGeoFence(currentFenceLoc, 300);
//				}
//				return true;
//			}
//		});
//	}
//
//	private void addTopBarToHead(boolean isHome) {
//		fl_header_location_activity = (FrameLayout) containerView
//				.findViewById(R.id.fl_header_location_activity);
//		location_calendar = (RelativeLayout) containerView
//				.findViewById(R.id.location_calendar);
//		if (actionBar != null) {
//			fl_header_location_activity.removeView(actionBar);
//		}
//		if (isHome) {
//			actionBar = TopBarUtils.createCustomActionBar(getActivity(),
//					R.drawable.s_action_bar_menu_ic, new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							ma.toggle();
//						}
//					}, "智慧家庭", null, null);
//		} else {
//			actionBar = TopBarUtils.createCustomActionBar(getActivity(),
//					R.drawable.btn_back_selector, new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							hideHomeWideget(false);
//							addTopBarToHead(true);
//							// drawHistroy();
//							clearMap();
//						}
//					}, "实时轨迹", null, null);
//		}
//		fl_header_location_activity.addView(actionBar);
//
//		actionBar.getTvTitle().setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				selectCalendar();
//			}
//		});
//	}
//
//	private void alterTopBarToHead(int type) {
//		String title = "";
//		switch (type) {
//		case FREQUENT_TYPE:
//			title = "常去地点";
//			break;
//		case HISTORY_TYPE:
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			title = sdf.format(new Date());
//			break;
//		case GEOFENCE_TYPE:
//			title = "设定围栏";
//			break;
//		default:
//			break;
//		}
//
//		fl_header_location_activity.removeView(actionBar);
//		actionBar = TopBarUtils.createCustomActionBar(getActivity(),
//				R.drawable.btn_back_selector, new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						recoverDefault();
//					}
//				}, title, "", null);
//		fl_header_location_activity.addView(actionBar);
//
//		if (type == HISTORY_TYPE) {
//			actionBar.getTvTitle().setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					selectCalendar();
//				}
//			});
//		}
//	}
//
//	private void selectCalendar() {
//		location_calendar.setVisibility(View.VISIBLE);
//
//		// 获取日历控件对象
//		calendar = (CalendarView) containerView.findViewById(R.id.calendar);
//		calendarLeft = (ImageButton) containerView
//				.findViewById(R.id.calendarLeft);
//		calendarRight = (ImageButton) containerView
//				.findViewById(R.id.calendarRight);
//		calendarCenter = (TextView) containerView
//				.findViewById(R.id.calendarCenter);
//		calendarCenter.setText(calendar.getYearAndmonth());
//		// 获取日历中年月 ya[0]为年，ya[1]为月（格式大家可以自行在日历控件中改）
//		String[] ya = calendar.getYearAndmonth().split("-");
//		calendar.setOnItemClickListener(new CalendarItemClickListener());
//		calendarLeft.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				calendarCenter.setText(calendar.clickLeftMonth());
//			}
//		});
//		calendarRight.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				calendarCenter.setText(calendar.clickRightMonth());
//			}
//		});
//	}
//
//	class CalendarItemClickListener implements OnItemClickListener {
//		@Override
//		public void OnItemClick(Date date) {
//
//			selectDate = date;
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			String s = sdf.format(date);
//			location_calendar.setVisibility(View.GONE);
//
//			fl_header_location_activity.removeView(actionBar);
//			fl_header_location_activity.addView(actionBar);
//
//			Calendar calendar = Calendar.getInstance();
//			int cur = calendar.get(Calendar.DAY_OF_YEAR);
//			calendar.setTime(date);
//			int tar = calendar.get(Calendar.DAY_OF_YEAR);
//
//			if (cur < tar) {
//				Toast.makeText(getActivity(), "当前时间尚无数据.", Toast.LENGTH_SHORT)
//						.show();
//				return;
//			} else {
//				Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
//
//	public class GetLocationListener implements BDLocationListener {
//		LatLng formerLocation = null;
//		boolean isRepeat = false;
//
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//
//			Log.i(TAG,
//					"" + location.getLatitude() + " , "
//							+ location.getLongitude());
//
//			/**
//			 * 无效经纬度
//			 */
//			if (String.valueOf((location.getLatitude())).equals("4.9E-324")
//					&& (String.valueOf(location.getLongitude()))
//							.equals("4.9E-324")) {
//				Log.i(TAG, "Net Exception");
//				return;
//			}
//
//			/**
//			 * 初次定位设置
//			 */
//			if (isFirstLoc) {
//				theVeryFirstLocation = new LatLng(location.getLatitude(),
//						location.getLongitude());
//				pathPoints.add(theVeryFirstLocation);
//				formerLocation = theVeryFirstLocation;
//				isFirstLoc = false;
//				updateMap(location);
//				return;
//			}
//
//			/**
//			 * 获取当前经纬度
//			 */
//			currentLocation = new LatLng(location.getLatitude(),
//					location.getLongitude());
//			pathPoints.add(currentLocation);
//
//			// handler.sendEmptyMessage(0);
//
//			/**
//			 * 根据实时经纬度画路径，过滤重复点
//			 */
//			for (LatLng loc : pathPoints) {
//				if ((formerLocation.latitude == loc.latitude)
//						&& (formerLocation.longitude == loc.longitude)) {
//					isRepeat = true;
//				} else
//					isRepeat = false;
//			}
//			double realDistance = DistanceUtil.getDistance(formerLocation,
//					currentLocation);
//
//			if ((realDistance > 5) && (!isRepeat)) {
//
//				List<LatLng> locations = new ArrayList<LatLng>();
//				locations.add(formerLocation);
//				locations.add(currentLocation);
//				OverlayOptions ooPolyline = new PolylineOptions().width(10)
//						.color(0xAAFF0000).points(locations);
//				mMap.addOverlay(ooPolyline);
//				formerLocation = currentLocation;
//
//				// locations = null;
//				// ooPolyline = null;
//				// currentLocation = null;
//			}
//
//			updateMap(location);
//			Log.i(TAG, location.getTime());
//
//		}
//
//		private void updateMap(BDLocation location) {
//			MyLocationData locData = new MyLocationData.Builder().accuracy(50)
//					.direction(100).latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//			mMap.setMyLocationData(locData);
//			if (!TextUtils.isEmpty(location.getAddrStr())) {
//				currentAddress = location.getAddrStr();
//			}
//			mLocationClient.stop();
//			currentLocation = new LatLng(location.getLatitude(),
//					location.getLongitude());
//			// setUserPosition(currentLocation);
//			uploadLocationFromServer("");
//		}
//	}
//
//	private void initHomeWidget() {
//		mBitmapUtil = new BitmapUtil();
//		locDialog = new ProgressDialog(getActivity());
//		selectDate = new Date();
//		selectDateString = new SimpleDateFormat("yyyy-MM-dd")
//				.format(selectDate);
//
//		viewFlipper = (ViewFlipper) containerView
//				.findViewById(R.id.home_flipper);
//
//		gestureDetector = new GestureDetector(getActivity(), this);
//		listener = new MainActivity.MyOntouchListener() {
//			@Override
//			public void onTouchEvent(MotionEvent event) {
//				gestureDetector.onTouchEvent(event);
//			}
//		};
//		((MainActivity) getActivity()).registerListener(listener);
//
//		HomeItemAdapter adapter = new HomeItemAdapter();
//		for (int i = 0; i < imageID.length; i++) {
//
//			// 添加到viewFlipper中
//			viewFlipper.addView(adapter.getView(i, null, null));
//		}
//
//		home_listening = (TextView) containerView
//				.findViewById(R.id.home_listening);
//		home_calling = (TextView) containerView.findViewById(R.id.home_calling);
//		home_alarm_clock = (TextView) containerView
//				.findViewById(R.id.home_alarm_clock);
//		home_setup = (TextView) containerView.findViewById(R.id.home_setup);
//		home_affairs_time = (TextView) containerView
//				.findViewById(R.id.home_affairs_time);
//		home_affairs_detail = (TextView) containerView
//				.findViewById(R.id.home_affairs_detail);
//		home_date_healthy = (TextView) containerView
//				.findViewById(R.id.home_date_healthy);
//		home_calorie = (TextView) containerView.findViewById(R.id.home_calorie);
//		home_steps = (TextView) containerView.findViewById(R.id.home_steps);
//		home_target = (TextView) containerView.findViewById(R.id.home_target);
//		home_sleep_h = (TextView) containerView.findViewById(R.id.home_sleep_h);
//		home_sleep_m = (TextView) containerView.findViewById(R.id.home_sleep_m);
//		home_awake_times = (TextView) containerView
//				.findViewById(R.id.home_awake_times);
//		home_sleep_quality = (TextView) containerView
//				.findViewById(R.id.home_sleep_quality);
//		home_layout_function = (LinearLayout) containerView
//				.findViewById(R.id.home_layout_function);
//		home_affairs_layout = (LinearLayout) containerView
//				.findViewById(R.id.home_affairs_layout);
//		home_healthy_sport_layout = (LinearLayout) containerView
//				.findViewById(R.id.home_healthy_sport_layout);
//		home_healthy_sleep_layout = (LinearLayout) containerView
//				.findViewById(R.id.home_healthy_sleep_layout);
//		home_layout_baidumap = (LinearLayout) containerView
//				.findViewById(R.id.home_layout_baidumap);
//		home_jiaxiao_layout = (LinearLayout) containerView
//				.findViewById(R.id.home_jiaxiao_layout);
//
//		home_listening.setOnClickListener(homeClickListener);
//		home_calling.setOnClickListener(homeClickListener);
//		home_alarm_clock.setOnClickListener(homeClickListener);
//		home_setup.setOnClickListener(homeClickListener);
//		home_healthy_sport_layout.setOnClickListener(homeClickListener);
//		home_healthy_sleep_layout.setOnClickListener(homeClickListener);
//		home_jiaxiao_layout.setOnClickListener(homeClickListener);
//
//		// viewFlipper.setLayoutParams(new
//		// android.widget.LinearLayout.LayoutParams(
//		// android.widget.LinearLayout.LayoutParams.FILL_PARENT,
//		// ScreenUtils.getScreenHeight(getActivity())/4));
//		// home_jiaxiao_layout.setLayoutParams(new
//		// android.widget.LinearLayout.LayoutParams(
//		// android.widget.LinearLayout.LayoutParams.FILL_PARENT,
//		// ScreenUtils.getScreenHeight(getActivity())/6));
//
//	}
//
//	private OnClickListener homeClickListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.home_listening:
//				openWatcherDialog();
//				break;
//			case R.id.home_calling:
//				callPhone();
//				break;
//			case R.id.home_alarm_clock:
//				startActivity(new Intent(getActivity(),
//						LocationAlarmClockActivity.class));
//				break;
//			case R.id.home_setup:
//				startActivity(new Intent(getActivity(),
//						LocationSettingActivity.class));
//				break;
//			case R.id.home_healthy_sport_layout:
//				// goToMainActivity(true);
//				ma.isSport = true;
//				ma.isSleep = false;
//				ma.switchContent(FragmentControlCenter.getInstance(
//						getActivity()).getCurrentFragmentModel());
//				break;
//			case R.id.home_healthy_sleep_layout:
//				// goToMainActivity(false);
//				ma.isSport = false;
//				ma.isSleep = true;
//				ma.switchContent(FragmentControlCenter.getInstance(
//						getActivity()).getCurrentFragmentModel());
//				break;
//			case R.id.home_jiaxiao_layout:
//
//				break;
//			}
//		}
//	};
//
//	private void goToMainActivity(boolean isSportView) {
//		Bundle b = new Bundle();
//		b.putBoolean("isSportView", isSportView);
//		Intent intent = new Intent(getActivity(), MainActivity.class);
//		intent.putExtras(b);
//		startActivity(intent);
//	}
//
//	private void openWatcherDialog() {
//		Builder bleBuilder = new Builder(getActivity());
//		View v = LayoutInflater.from(getActivity()).inflate(
//				R.layout.home_listening_dialog, null);
//		dialog = bleBuilder
//				.setView(v)
//				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				})
//				.setNegativeButton("发送监听请求",
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Toast.makeText(getActivity(), "toast",
//										Toast.LENGTH_SHORT).show();
//								
//								dialog.dismiss();
//							}
//						}).setCancelable(false).create();
//		dialog.show();
//	}
//
//	/**
//	 * 初始化变量
//	 */
//	private void initLocWidget() {
//		mMapView = (MapView) containerView
//				.findViewById(R.id.activity_location_mapView);
//		mMapView.setLayoutParams(new LinearLayout.LayoutParams(
//				LayoutParams.FILL_PARENT, 200));
//		mMap = mMapView.getMap();
//		LatLng cenpt = new LatLng(LATITUDE_NANJING, LONGITUDE_NANJING);
//		setCenterPoint(cenpt);
//
//		mLocationClient = new LocationClient(getActivity());
//		mCoder = GeoCoder.newInstance();
//		currentContext = getActivity();
//		activity_location_mapView_default = (RelativeLayout) containerView
//				.findViewById(R.id.activity_location_mapView_default);
//		geoFence = (ImageView) containerView
//				.findViewById(R.id.activity_location_geofence_iv);
//		historyLoc = (ImageView) containerView
//				.findViewById(R.id.activity_location_history);
//		frequentLocations = (ImageView) containerView
//				.findViewById(R.id.activity_location_freqentPlaces_iv);
//		activity_location_geofence_specific_tv = (TextView) containerView
//				.findViewById(R.id.activity_location_geofence_specific_tv);
//		draw_bd_head = BitmapDescriptorFactory.fromResource(R.drawable.locate);
//		draw_bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
//		draw_popup = BitmapDescriptorFactory.fromResource(R.drawable.popup);
//		mAudioManager = (AudioManager) getActivity().getSystemService(
//				Context.AUDIO_SERVICE);
//		vibrator = (Vibrator) getActivity().getSystemService(
//				Context.VIBRATOR_SERVICE);
//		
//		//长按删除
//		mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
//			
//			@Override
//			public void onMapLongClick(LatLng latLng) {
//				if (isOpenGeoFence) {
//					delFenceLoc = null;
//					for (Map<String, Object> map : geoList) {
//						if (calculateDistance(latLng,
//								(LatLng) map.get("latLng"),
//								(Integer) map.get("radius"))) {
//							delFenceLoc = map;
//						}
//					}
//					if (delFenceLoc != null) {
//						function_layout.setVisibility(View.VISIBLE);
//						geoList.remove(delFenceLoc);
//						mMap.clear();
//						for (Map<String, Object> map : geoList) {
//							drawGeoFence(((LatLng) map.get("latLng")),
//									(Integer) map.get("radius"));
//						}
//						deleteGeoFenceByServer((Integer)delFenceLoc.get("fenceId"));
//					}
//				}
//			}
//		});
//
//		mMap.setOnMapClickListener(new OnMapClickListener() {
//
//			@Override
//			public boolean onMapPoiClick(MapPoi arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public void onMapClick(LatLng latLng) {
//				if (isOpenGeoFence) {
//					editFenceLoc = null;
//					for (Map<String, Object> map : geoList) {
//						if (calculateDistance(latLng,
//								(LatLng) map.get("latLng"),
//								(Integer) map.get("radius"))) {
//							editFenceLoc = map;
//						}
//					}
//					if (editFenceLoc != null) {
//						function_layout.setVisibility(View.VISIBLE);
//						currentFenceLoc = (LatLng) editFenceLoc.get("latLng");
//						currentFenceRadius = (Integer) editFenceLoc.get("radius");
//						geoList.remove(editFenceLoc);
//						mMap.clear();
//						drawGeoFence(currentFenceLoc, currentFenceRadius);
//						for (Map<String, Object> map : geoList) {
//							drawGeoFence(((LatLng) map.get("latLng")),
//									(Integer) map.get("radius"));
//						}
//						return;
//					}
//
//					if (!isCreateGeoFence) {
//
//						setGeoFenceCenter(latLng);
//						currentFenceLoc = latLng;
//						isCreateGeoFence = true;
//					} else {
//						Toast.makeText(getActivity(), "请先确认当前电子围栏",
//								Toast.LENGTH_SHORT).show();
//					}
//				} else {
//					hideHomeWideget(true);
//					addTopBarToHead(false);
//					mMap.clear();
//					isMapView = true;
//					if(historyLatLng.size()>0){
//						setUserPosition(historyLatLng.get(historyLatLng
//								.size() - 1));
//					}
//				}
//			}
//		});
//
//		function_ok = (Button) containerView.findViewById(R.id.function_ok);
//		function_cancle = (Button) containerView
//				.findViewById(R.id.function_cancle);
//		function_meter = (EditText) containerView
//				.findViewById(R.id.function_meter);
//		function_meter.setVisibility(View.GONE);
//		function_meter.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//				// if(isEdit){
//				// String distance = "";
//				// if(function_meter.getText().toString().indexOf(".")>=0){
//				// distance = function_meter.getText().toString().split(".")[0];
//				// }else{
//				// distance = function_meter.getText().toString();
//				// }
//				// int progress = (int)
//				// (Integer.parseInt(distance)*1.0/5000*100);
//				// function_SeekBar.setProgress(progress);
//				// clearMap();
//				// drawGeoFence(fenceList.get(0),
//				// (int)(MAX_DISTANCE*1000*progress*1.0/100));
//				// }
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1,
//					int arg2, int arg3) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable arg0) {
//
//			}
//		});
//		function_meter.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View arg0, boolean arg1) {
//				isEdit = arg1;
//			}
//		});
//
//		function_layout = (RelativeLayout) containerView
//				.findViewById(R.id.function_layout);
//		function_SeekBar = (SeekBar) containerView
//				.findViewById(R.id.function_SeekBar);
//		function_SeekBar.setMax(100);
//		function_SeekBar.setProgress(0);
//		function_SeekBar
//				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//					@Override
//					public void onStopTrackingTouch(SeekBar seekBar) {
//					}
//
//					@Override
//					public void onStartTrackingTouch(SeekBar seekBar) {
//					}
//
//					@Override
//					public void onProgressChanged(SeekBar seekBar,
//							int progress, boolean fromUser) {
//						mMap.clear();
//						function_meter.setText(MAX_DISTANCE * 1000 * progress
//								* 1.0 / 100 + "");
//						currentFenceRadius = (int) (MAX_DISTANCE
//								* 1000 * progress * 1.0 / 100);
//						drawGeoFence(currentFenceLoc, (int) (MAX_DISTANCE
//								* 1000 * progress * 1.0 / 100));
//						for (Map<String, Object> map : geoList) {
//							drawGeoFence(((LatLng) map.get("latLng")),
//									(Integer) map.get("radius"));
//						}
//					}
//				});
//
//		function_ok.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				geoMap = new HashMap<String, Object>();
//				geoMap.put("latLng", currentFenceLoc);
//				geoMap.put("radius", currentFenceRadius);
//				geoList.add(geoMap);
//				// radiusList.add(currentRadius);
//				isCreateGeoFence = false;
//				function_layout.setVisibility(View.GONE);
//				
//				int fenceId = -1;
//				if(editFenceLoc!=null){
//					fenceId = (Integer)editFenceLoc.get("fenceId");
//				}
//				addGeoFenceByServer(fenceId);
//				// recoverDefault();
//			}
//		});
//		function_cancle.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				AlertDialog.Builder builder = new AlertDialog.Builder(
//						getActivity());
//				builder.setMessage("确定删除该电子围栏吗?");
//				builder.setNegativeButton("确定",
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// recoverDefault();
//								dialog.dismiss();
//								isCreateGeoFence = false;
//								for (Map<String, Object> map : geoList) {
//									drawGeoFence((LatLng) map.get("latLng"),
//											(Integer) map.get("radius"));
//								}
//							}
//						});
//				builder.setPositiveButton("取消",
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.dismiss();
//							}
//						});
//				builder.create().show();
//			}
//		});
//
//		activity_location_mapView_frequent_place = (RelativeLayout) containerView
//				.findViewById(R.id.activity_location_mapView_frequent_place);
//		activity_location_frequentLocation_tv = (TextView) containerView
//				.findViewById(R.id.activity_location_frequentLocation_tv);
//		activity_location_frequentLocation_btn = (ImageView) containerView
//				.findViewById(R.id.activity_location_frequentLocation_btn);
//		activity_location_mapView_frequent_place_list_layout = (LinearLayout) containerView
//				.findViewById(R.id.activity_location_mapView_frequent_place_list_layout);
//		activity_location_mapView_frequent_place_listview = (ListView) containerView
//				.findViewById(R.id.activity_location_mapView_frequent_place_listview);
//
//		activity_location_frequentLocation_btn
//				.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						activity_location_mapView_frequent_place
//								.setVisibility(View.GONE);
//						activity_location_mapView_frequent_place_list_layout
//								.setVisibility(View.VISIBLE);
//					}
//				});
//
//		location_function_listener = (LinearLayout) containerView
//				.findViewById(R.id.location_function_listener);
//		location_function_listener_time = (TextView) containerView
//				.findViewById(R.id.location_function_listener_time);
//		location_function_listener_btn = (Button) containerView
//				.findViewById(R.id.location_function_listener_btn);
//
//		location_function_listener_btn
//				.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						location_function_listener.setVisibility(View.GONE);
//						// TODO结束监听功能
//					}
//				});
//	}
//
//	/**
//	 * 显示扩展菜单
//	 */
//	private void showEnlargeMenu() {
//		ScaleAnimation anim0 = new ScaleAnimation(0.0f, 1.0f, 1.0f, 0.0f);
//		ScaleAnimation anim1 = new ScaleAnimation(0.0f, 1.0f, 1.0f, 0.0f);
//		ScaleAnimation anim2 = new ScaleAnimation(0.0f, 1.0f, 1.0f, 0.0f);
//		ScaleAnimation anim3 = new ScaleAnimation(0.0f, 1.0f, 1.0f, 0.0f);
//
//		anim0.setDuration(100);
//		anim1.setDuration(300);
//		anim2.setDuration(500);
//		anim3.setDuration(700);
//		geoFence.setVisibility(View.VISIBLE);
//		frequentLocations.setVisibility(View.VISIBLE);
//		historyLoc.setVisibility(View.VISIBLE);
//		geoFence.setAnimation(anim1);
//		historyLoc.setAnimation(anim2);
//		frequentLocations.setAnimation(anim3);
//	}
//
//	/**
//	 * 消除扩展菜单
//	 */
//	private void dismissEnlargeMenu() {
//
//		historyLoc.setVisibility(View.INVISIBLE);
//		geoFence.setVisibility(View.INVISIBLE);
//		frequentLocations.setVisibility(View.INVISIBLE);
//	}
//
//	/**
//	 * 初始化地图参数
//	 */
//	private void initialMap() {
//		isFirstLoc = true;
//		mMap.setMyLocationEnabled(true);
//		// mMap.setMyLocationConfigeration(new MyLocationConfigeration(
//		// com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.FOLLOWING,
//		// true, mLocationMarker));
//		mLocationClient.registerLocationListener(new GetLocationListener());
//		option = new LocationClientOption();
//		option.setOpenGps(true);
//		option.setCoorType("bd09ll");
//		option.setScanSpan(1000 * 10);
//		option.setIsNeedAddress(true);
//		mLocationClient.setLocOption(option);
//		mLocationClient.start();
//		option = null;
//
//		/**
//		 * 将经纬度坐标转换成地址
//		 */
//		OnGetGeoCoderResultListener geoCoderListener = new OnGetGeoCoderResultListener() {
//
//			@Override
//			public void onGetGeoCodeResult(GeoCodeResult result) {
//
//			}
//
//			@Override
//			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
//				currentAddress = result.getAddress();
//			}
//
//		};
//		mCoder.setOnGetGeoCodeResultListener(geoCoderListener);
//
//	}
//
//	/**
//	 * 显示活动地点
//	 * 
//	 * @param places
//	 */
//	private void showActivePlaces(LatLng[] places) {
//		for (int i = 0; i < places.length; i++) {
//			BitmapDescriptor bitmap = BitmapDescriptorFactory
//					.fromResource(R.drawable.locate);
//			MarkerOptions option = new MarkerOptions().position(places[i])
//					.icon(bitmap).title("place" + String.valueOf(i));
//			mMap.addOverlay(option);
//		}
//
//	}
//
//	private AudioManager mAudioManager;
//	private Vibrator vibrator;
//	private final static int DEFAULT_BIND_PHONE_NUMBER = -1;
//
//	/**
//	 * 绘制覆盖层
//	 */
//	private void initBorder() {
//		mMap.clear();
//		OverlayOptions ooPolygon = new PolygonOptions().points(fenceList)
//				.stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
//		mMap.addOverlay(ooPolygon);
//		isCreateGeoFence = true;
//	}
//
//	private void clearMap() {
//		mMap.clear();
//		fenceList.clear();
//		radiusList.clear();
//		isCreateGeoFence = false;
//	}
//
//	private void setPosition(LatLng latLng, int pos) {
//		View view = LayoutInflater.from(getActivity()).inflate(
//				R.layout.location_detail_item, null);
//		TextView location_detail_item_tv = (TextView) view
//				.findViewById(R.id.location_detail_item_tv);
//		location_detail_item_tv.setVisibility(View.VISIBLE);
//		location_detail_item_tv.setText(pos + "");
//		BitmapDescriptor item = BitmapDescriptorFactory.fromView(view);
//		OverlayOptions oop = new MarkerOptions().position(latLng).icon(item);
//		mMap.addOverlay(oop);
//	}
//
//	/**
//	 * 设置电子围栏中心点
//	 * @param latLng
//	 */
//	private void setGeoFenceCenter(LatLng latLng) {
//		View view = LayoutInflater.from(getActivity()).inflate(
//				R.layout.location_detail_item, null);
//		Button location_geo_fence_btn = (Button) view
//				.findViewById(R.id.location_geo_fence_btn);
//		location_geo_fence_btn.setVisibility(View.VISIBLE);
//		BitmapDescriptor item = BitmapDescriptorFactory.fromView(view);
//		OverlayOptions oop = new MarkerOptions().position(latLng).icon(item);
//		fenceMarker = (Marker) mMap.addOverlay(oop);
//	}
//
//	private void setUserPosition(LatLng latLng) {
//		mMap.clear();
//		OverlayOptions oop = null;
//		if(isMapView){
//			oop = new MarkerOptions().position(latLng).icon(draw_bd_head)
//					.zIndex(9);
//		}else{
//			oop = new MarkerOptions().position(latLng).icon(draw_bd)
//					.zIndex(9);
//		}
//		userMarker = (Marker) (mMap.addOverlay(oop));
//	}
//
//	/**
//	 * 设置用户为中心点
//	 * 
//	 * @param latLng
//	 */
//	private void setUserCenterFocus(LatLng latLng) {
//		setCenterPoint(latLng);
//		setUserPosition(latLng);
//	}
//
//	/**
//	 * 免打扰模式
//	 */
//	private void setSilenceMode() {
//		mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
//		vibrator.cancel();
//	}
//
//	/**
//	 * 取得绑定亲情号码 keycode-phonenumber
//	 * 
//	 * @param keyCode
//	 * @return
//	 */
//	private String getBindRelNumber(int keyCode) {
//		int constants_keyCode = Integer.parseInt(Preferences
//				.getInstance(currentContext).getRelPhoneNumberBinder()
//				.split("-")[0]);
//		String constants_phone_number = Preferences.getInstance(currentContext)
//				.getRelPhoneNumberBinder().split("-")[1];
//		if (keyCode == constants_keyCode) {
//			return constants_phone_number;
//		} else {
//			return "";
//		}
//	}
//
//	/**
//	 * 取得绑定SOS号码 keycode-phonenumber
//	 * 
//	 * @param keyCode
//	 * @return
//	 */
//	private String getBindSOSNumber(int keyCode) {
//		int constants_keyCode = Integer.parseInt(Preferences
//				.getInstance(currentContext).getSOSPhoneNumberBinder()
//				.split("-")[0]);
//		String constants_phone_number = Preferences.getInstance(currentContext)
//				.getSOSPhoneNumberBinder().split("-")[1];
//		if (keyCode == constants_keyCode) {
//			return constants_phone_number;
//		} else {
//			return "";
//		}
//	}
//
//	/**
//	 * 设置绑定SOS或亲情号码
//	 * 
//	 * @param keyCode
//	 * @param phoneNumber
//	 * @param type
//	 */
//	private void setBindNumber(int keyCode, String phoneNumber, int type) {
//		String sos = Preferences.getInstance(currentContext)
//				.getSOSPhoneNumberBinder();
//		String rel = Preferences.getInstance(currentContext)
//				.getRelPhoneNumberBinder();
//
//		switch (type) {
//		case 1:
//			if (sos.indexOf("-") < 0) {
//				Preferences.getInstance(currentContext)
//						.setSOSPhoneNumberBinder(keyCode + "-" + phoneNumber);
//			} else {
//				if (rel.split("-")[0].equals("" + keyCode)) {
//					Toast.makeText(currentContext, "当前号码已被绑定为亲情号码",
//							Toast.LENGTH_SHORT).show();
//				} else if (sos.split("-")[0].equals("" + keyCode)) {
//					Toast.makeText(currentContext, "当前号码已被绑定为SOS号码",
//							Toast.LENGTH_SHORT).show();
//				} else {
//					Preferences.getInstance(currentContext)
//							.setSOSPhoneNumberBinder(
//									keyCode + "-" + phoneNumber);
//				}
//			}
//			break;
//		case 2:
//			if (rel.indexOf("-") < 0) {
//				Preferences.getInstance(currentContext)
//						.setRelPhoneNumberBinder(keyCode + "-" + phoneNumber);
//			} else {
//				if (rel.split("-")[0].equals("" + keyCode)) {
//					Toast.makeText(currentContext, "当前号码已被绑定为亲情号码",
//							Toast.LENGTH_SHORT).show();
//				} else if (sos.split("-")[0].equals("" + keyCode)) {
//					Toast.makeText(currentContext, "当前号码已被绑定为SOS号码",
//							Toast.LENGTH_SHORT).show();
//				} else {
//					Preferences.getInstance(currentContext)
//							.setRelPhoneNumberBinder(
//									keyCode + "-" + phoneNumber);
//				}
//			}
//			break;
//		}
//	}
//
//	/**
//	 * 立刻定位
//	 */
//	private void getLocationImm() {
//		if (mLocationClient != null) {
//			mLocationClient.requestLocation();
//		}
//	}
//
//	class FrequentPlaceAdapter extends BaseAdapter {
//
//		private List<String> places;
//
//		public FrequentPlaceAdapter(List<String> places) {
//			this.places = places;
//		}
//
//		@Override
//		public int getCount() {
//			return places.size();
//		}
//
//		@Override
//		public Object getItem(int pos) {
//			return places.get(pos);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public View getView(final int pos, View view, ViewGroup viewgroup) {
//
//			view = LayoutInflater.from(getActivity()).inflate(
//					R.layout.location_frequent_item, null);
//			Getter getter;
//			if (view != null) {
//				getter = new Getter();
//				getter.frequent_location_item_name = (TextView) view
//						.findViewById(R.id.frequent_location_item_name);
//				getter.frequent_location_item_seri = (TextView) view
//						.findViewById(R.id.frequent_location_item_seri);
//				getter.frequent_location_item_layout = (LinearLayout) view
//						.findViewById(R.id.frequent_location_item_layout);
//				view.setTag(getter);
//			} else
//				getter = (Getter) view.getTag();
//
//			getter.frequent_location_item_name.setText(places.get(pos));
//			getter.frequent_location_item_seri.setText("" + (pos + 1));
//			getter.frequent_location_item_layout
//					.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							setCenterPoint(test1[pos]);
//							activity_location_mapView_frequent_place
//									.setVisibility(View.VISIBLE);
//							activity_location_mapView_frequent_place_list_layout
//									.setVisibility(View.GONE);
//							activity_location_frequentLocation_tv
//									.setText(places.get(pos));
//						}
//					});
//
//			return view;
//		}
//
//		public class Getter {
//			private TextView frequent_location_item_name,
//					frequent_location_item_seri;
//			private LinearLayout frequent_location_item_layout;
//		}
//	}
//
//	/**
//	 * 设置中心点
//	 */
//	private void setCenterPoint(LatLng cenpt) {
//		// 定义地图状态
//		MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).build();
//		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
//				.newMapStatus(mMapStatus);
//		// 改变地图状态
//		mMap.setMapStatus(mMapStatusUpdate);
//	}
//
//	private void recoverDefault() {
//		activity_location_mapView_frequent_place.setVisibility(View.GONE);
//		function_layout.setVisibility(View.GONE);
//		activity_location_mapView_default.setVisibility(View.VISIBLE);
//		geoFence.setVisibility(View.VISIBLE);
//		historyLoc.setVisibility(View.VISIBLE);
//		frequentLocations.setVisibility(View.VISIBLE);
//
//		addTopBarToHead(false);
//		clearMap();
//		mLocationClient.start();
//	}
//
//	private void drawHistroy() {
//		List<LatLng> points = new ArrayList<LatLng>();
//		for (LatLng point : historyLatLng) {
//			points.add(point);
//		}
//		OverlayOptions ooPolyline = new PolylineOptions().width(10)
//				.color(0xAA0000FF).points(points);
//		mMap.addOverlay(ooPolyline);
//	}
//
//	private void drawGeoFence(LatLng llCircle, int radius) {
//		// 添加圆
//		OverlayOptions ooCircle = new CircleOptions().fillColor(0xAAf1efe9)
//				.center(llCircle).stroke(new Stroke(5, 0xAAc9db30))
//				.radius(radius);
//		// MarkerOptions
//		mMap.addOverlay(ooCircle);
//	}
//
//	private void callPhone() {
//		if (!TextUtils.isEmpty(Preferences.getInstance(currentContext)
//				.getRelPhoneNumberBinder())) {
//			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
//					+ Preferences.getInstance(currentContext)
//							.getRelPhoneNumberBinder()));
//			startActivity(intent);
//		} else {
//			Toast.makeText(currentContext, "尚未绑定号码", Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	@Override
//	public boolean onDown(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
//			float arg3) {
//		// 对手指滑动的距离进行了计算，如果滑动距离大于120像素，就做切换动作，否则不做任何切换动作。
//		// 从左向右滑动
//		if (arg0.getX() - arg1.getX() > 120) {
//			// 添加动画
//			viewFlipper.setInAnimation(AnimationUtils.loadAnimation(
//					getActivity(), R.anim.push_left_in));
//			viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
//					getActivity(), R.anim.push_left_out));
//			viewFlipper.showNext();
//			// setUserCenterFocus(latlngs[viewFlipper.getDisplayedChild()]);
//			// 连接服务器
//			getHistoryLocationFromServer("", selectDateString);
//			isLastLoc = true;
//			return true;
//		}// 从右向左滑动
//		else if (arg0.getX() - arg1.getX() < -120) {
//			viewFlipper.setInAnimation(AnimationUtils.loadAnimation(
//					getActivity(), R.anim.push_right_in));
//			viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
//					getActivity(), R.anim.push_right_out));
//			viewFlipper.showPrevious();
//			// setUserCenterFocus(latlngs[viewFlipper.getDisplayedChild()]);
//			// 连接服务器
//			getHistoryLocationFromServer("", selectDateString);
//			isLastLoc = true;
//			return true;
//		}
//		return true;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
//			float arg3) {
//		// TODO Auto-generated method stub
//		Log.d("", "daitm-----onscrolling");
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean onTouchEvent(MotionEvent event) {
//		return gestureDetector.onTouchEvent(event);
//	}
//
//	class HomeItemAdapter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return imageID.length;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return imageID[position];
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return imageID[position];
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			convertView = LayoutInflater.from(getActivity()).inflate(
//					R.layout.activity_home_item, null);
//			Holder holder;
//			if (convertView != null) {
//				holder = new Holder();
//				holder.home_user_name = (TextView) convertView
//						.findViewById(R.id.home_user_name);
//				holder.home_head = (BatteryHead) convertView
//						.findViewById(R.id.home_head);
//				holder.home_head_left = (ImageView) convertView
//						.findViewById(R.id.home_head_left);
//				holder.home_head_right = (ImageView) convertView
//						.findViewById(R.id.home_head_right);
//				convertView.setTag(holder);
//			} else
//				holder = (Holder) convertView.getTag();
//
//			int pos_left = -1;
//			int pos_right = -1;
//			if (position == 0) {
//				pos_left = imageID.length - 1;
//				pos_right = position + 1;
//			} else if (position == imageID.length - 1) {
//				pos_left = imageID.length - 2;
//				pos_right = 0;
//			} else {
//				pos_left = position - 1;
//				pos_right = position + 1;
//			}
//			holder.home_user_name.setText(names[position]);
//			holder.home_head.setBitmap(mBitmapUtil
//					.getRoundedCornerBitmap(BitmapFactory.decodeResource(
//							getResources(), imageID[position])),
//					progresses[position]);
//
//			holder.home_head_left.setImageBitmap(mBitmapUtil
//					.getRoundedCornerBitmap(BitmapFactory.decodeResource(
//							getResources(), imageID[pos_left])));
//
//			holder.home_head_right.setImageBitmap(mBitmapUtil
//					.getRoundedCornerBitmap(BitmapFactory.decodeResource(
//							getResources(), imageID[pos_right])));
//			return convertView;
//		}
//
//		class Holder {
//			ImageView home_head_left, home_head_right;
//			TextView home_user_name;
//			BatteryHead home_head;
//		}
//
//	}
//
//	// 隐藏缩放控件
//	private void hideMapDefaultWidget() {
//		int childCount = mMapView.getChildCount();
//		for (int i = 0; i < childCount; i++) {
//			if (i > 0) {// 0为百度地图
//				View child = mMapView.getChildAt(i);
//				child.setVisibility(View.GONE);
//			}
//		}
//	}
//
//	private void hideHomeWideget(boolean isLoc) {
//		if (isLoc) {
//			viewFlipper.setVisibility(View.GONE);
//			home_layout_function.setVisibility(View.GONE);
//			home_affairs_layout.setVisibility(View.GONE);
//			home_healthy_sport_layout.setVisibility(View.GONE);
//			home_healthy_sleep_layout.setVisibility(View.GONE);
//
//			activity_location_mapView_default.setVisibility(View.VISIBLE);
//			geoFence.setVisibility(View.VISIBLE);
//			historyLoc.setVisibility(View.VISIBLE);
//			frequentLocations.setVisibility(View.VISIBLE);
//
//			mMapView.setLayoutParams(new LinearLayout.LayoutParams(
//					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//		} else {
//			viewFlipper.setVisibility(View.VISIBLE);
//			home_layout_function.setVisibility(View.VISIBLE);
//			home_affairs_layout.setVisibility(View.VISIBLE);
//			home_healthy_sport_layout.setVisibility(View.VISIBLE);
//			home_healthy_sleep_layout.setVisibility(View.VISIBLE);
//
//			activity_location_mapView_default.setVisibility(View.GONE);
//			geoFence.setVisibility(View.GONE);
//			historyLoc.setVisibility(View.GONE);
//			frequentLocations.setVisibility(View.GONE);
//
//			mMapView.setLayoutParams(new LinearLayout.LayoutParams(
//					LayoutParams.FILL_PARENT, 200));
//		}
//	}
//
//	// ////////////////////////////server//////////////////////////////////////
//	/**
//	 * 获取常去地点
//	 */
//	private void getFrequentLocationFromServer(String userId) {
//		loc_type = LOC_FREQUENT;
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("acctid", userId);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.GET_OFTEN_POS, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//
//	/**
//	 * 获取电子围栏
//	 */
//	private void getGeoFenceFromServer(String userId) {
//		loc_type = LOC_GEOFENCE;
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("acctid", userId);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.GET_FENCE, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//
//	private void addGeoFenceByServer(int fenceId) {
//		loc_type = ADD_GEO_FENCE;
//		JSONObject obj = new JSONObject();
//		JSONArray list = new JSONArray();
//		try {
//			// for(Map<String,Object> map: geoList){
//			// JSONObject geoItem = new JSONObject();
//			// geoItem.put("jd", ((LatLng)map.get("latLng")).latitude);//经度
//			// geoItem.put("wd", ((LatLng)map.get("latLng")).longitude);//纬度
//			// geoItem.put("radius", (Integer)map.get("radius"));//半径
//			// geoItem.put("height", 0);//高度
//			// geoItem.put("width", 0);//宽度
//			// list.put(geoItem);
//			// }
//			JSONObject geoItem = new JSONObject();
//			geoItem.put("jd", ((LatLng) geoMap.get("latLng")).latitude);// 经度
//			geoItem.put("wd", ((LatLng) geoMap.get("latLng")).longitude);// 纬度
//			geoItem.put("radius", (Integer) geoMap.get("radius"));// 半径
//			geoItem.put("height", 0);// 高度
//			geoItem.put("width", 0);// 宽度
//			list.put(geoItem);
//			if(editFenceLoc!=null){
//				obj.put("id", fenceId);
//			}
//			obj.put("type", 0);
//			obj.put("name", "");
//			obj.put("list", list);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.ADD_GEO_FENCE, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//
//	private void editGeoFenceByServer(int fencId) {
//		loc_type = EDIT_GEO_FENCE;
//		JSONObject obj = new JSONObject();
//		JSONArray list = new JSONArray();
//		try {
//			JSONObject geoItem = new JSONObject();
//			geoItem.put("jd", ((LatLng) geoMap.get("latLng")).latitude);// 经度
//			geoItem.put("wd", ((LatLng) geoMap.get("latLng")).longitude);// 纬度
//			geoItem.put("radius", (Integer) geoMap.get("radius"));// 半径
//			geoItem.put("height", 0);// 高度
//			geoItem.put("width", 0);// 宽度
//			list.put(geoItem);
//			obj.put("id", fencId);
//			obj.put("type", 0);
//			obj.put("name", "");
//			obj.put("list", list);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.EDIT_GEO_FENCE, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//
//	private void deleteGeoFenceByServer(int fenceId) {
//		loc_type = DELETE_GEO_FENCE;
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("fenceId", fenceId);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.DELETE_GEO_FENCE, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//
//	/**
//	 * 获取历史轨迹
//	 */
//	private void getHistoryLocationFromServer(String userId, String historyDate) {
//		loc_type = LOC_HISTORY;
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("acctid", userId);
//			obj.put("date", historyDate);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.GET_TRACK, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//	
//	/**
//	 * 上传位置
//	 */
//	private void uploadLocationFromServer(String userId){
//		loc_type = LOC_UPLOAD;
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("acctid", userId);
//			obj.put("jd", currentLocation.latitude);
//			obj.put("wd", currentLocation.longitude);
//			obj.put("address", currentAddress);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.UPLOAD, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//	
//	/**
//	 * 立刻请求地理位置
//	 */
//	private void getNowPosFromServer(String userId){
//		loc_type = LOC_UPLOAD;
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("acctid", userId);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.GET_NOW_POS, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//	
//	/**
//	 * 远程监听
//	 */
//	private void monitorFromServer(String deviceId,String phone){
//		loc_type = MONITOR;
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("deviceId", deviceId);
//			obj.put("phone", phone);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		HttpUtil.postRequest(obj, Constants.MONITOR, handler,
//				GET_DATA_SUCCESS, GET_DATA_FAIL);
//	}
//
//	/**
//	 * 计算距离
//	 */
//
//	private boolean calculateDistance(LatLng currentLoc, LatLng fenceLoc,
//			int radius) {
//		if (DistanceUtil.getDistance(currentLoc, fenceLoc) > radius) {
//			log.d("daitm-----------calculateDistance-------已超出范围");
//			// Toast.makeText(getActivity(), "已超出范围",
//			// Toast.LENGTH_SHORT).show();
//			return false;
//		} else {
//			log.d("daitm-----------calculateDistance-------未超出范围");
//			// Toast.makeText(getActivity(), "未超出范围",
//			// Toast.LENGTH_SHORT).show();
//			return true;
//		}
//	}
}
