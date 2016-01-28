package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
//import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.UserInfo;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.ui.AnnouncementActivity;
import com.smarthome.client2.familySchool.ui.ClassCircleActivity;
import com.smarthome.client2.familySchool.ui.HomeworkActivity;
import com.smarthome.client2.familySchool.ui.LeaveMsgActivityTeacher;
import com.smarthome.client2.familySchool.ui.ScoreActivityFamily;
import com.smarthome.client2.familySchool.ui.SyllabusActivityFamily;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.ToastUtil;
import com.smarthome.client2.util.UserInfoUtil;
import com.smarthome.client2.view.CustomActionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassesListDetailActivity_sm extends BaseActivity {


	private CustomActionBar actionBar;

	private TextView mClassesTtile;

	private ImageView mImageback;

	private Button mBtnOneKeyPress;

	private GridView classes_detail_grid;


	//  private int userId = -1;
	/**
	 * 用户id
	 */
	private String mMemberId = "";

	private String mMemberUserName = "";

	private String mClassName = "";

	/**
	 * 学生id
	 */
	private String mStudentId = "";

	private String mClassId = "";

	private String memberName = "";

	private String mHeadPicPath = "";

	private Bitmap mBm;


	/**
	 * 家庭组id
	 */
	private String mGroupId = "";

	private boolean mIsCurrentUser = false;

	//  private String mPassedStudentId = "";

	private boolean isStudent = false;

	/**
	 * 是否是家长
	 */
	private boolean isParent = false;

	private boolean isOld = false;

	private String mStrIMEI = "";

	private String mStrDeviceId = "";

	private GridItemAdapter adapter;

	private ProgressDialog getUserInfDialog;

	private ProgressDialog mSyncDialog;

	private ProgressDialog mDialog;

	private boolean isClick = false;

	private int mToastCount = 0;

	private ToastUtil mToastUtil;

	private NetStatusListener mNetStatusListener;

	// 图片的第一行文字
	private String[] titles = new String[]{"通知公告", "家庭作业", "留言",
			"课程表", "成绩信息", "班级圈"};

	// 图片ID数组
	private int[] images = {R.drawable.message_notice, R.drawable.homework,
			R.drawable.message_board, R.drawable.class_schedule,
			R.drawable.scoreinfo, R.drawable.classlist};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.e_class_family);

		//        initBLEDevice();

		mClassesTtile = (TextView) findViewById(R.id.tv_classes_title);
		mImageback = (ImageView) findViewById(R.id.img_back);
		mBtnOneKeyPress = (Button) findViewById(R.id.btn_one_key_press);

		//mImageBackClickListener = new ImageBackClickListener(this);
		mImageback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ClassesListDetailActivity_sm.this.finish();
			}
		});

		getUserInfDialog = new ProgressDialog(this);
		getUserInfDialog.setCanceledOnTouchOutside(false);

		getUserInfDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				isLoading = false;
			}
		});
		Preferences preferences = Preferences.getInstance(this.getApplicationContext());
		Bundle bundle = getIntent().getExtras();
		mMemberId = preferences.getUserID();
		mClassId = bundle.getString("classId");
		mClassName = bundle.getString("className");
		mClassesTtile.setText(mClassName);  //set grade name: grade_name
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK
				&& requestCode == UserInfoUtil.INTENT_NICKNAME_RESULT) {
			mMemberUserName = data.getStringExtra(UserInfoUtil.INTENT_KEY_NICKNAME);
			actionBar.setTvTitleMsg(mMemberUserName);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private final static int GETUSERINFO_START = 1;

	private final static int GETUSERINFO_SUCCESS = 2;

	private final static int GETUSERINFO_FAIL = 3;

	private boolean isLoading = false;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getFlagFromServer();

		if (adapter != null) {
			adapter.setSeclection(-1);
			adapter.notifyDataSetChanged();
		}
	}

	private Handler mGetUserInfoHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinishing()) {
				return;
			}
			log("zxl---login---mhandler---" + msg.what);
			switch (msg.what) {
				case GETUSERINFO_START:
					getUserInfDialog.setMessage("正在获取用户数据");
					getUserInfDialog.show();
					break;
				case GETUSERINFO_SUCCESS:
					isLoading = false;
					getUserInfDialog.setMessage("获取用户数据成功");
					getUserInfDialog.dismiss();
					break;
				case GETUSERINFO_FAIL:
					//                if (myAsyncTask != null) {
					//                    myAsyncTask.cancel(true);
					//                }
					//                myAsyncTask = null;
					isLoading = false;

					getUserInfDialog.dismiss();
					Toast.makeText(ClassesListDetailActivity_sm.this,
							"获取用户数据失败",
							Toast.LENGTH_SHORT).show();
					//                HttpUtil.responseHandler(getApplicationContext(), msg.arg1);
					break;
			}
		}
	};


	private void initGridLayout() {
//        mTipTv = (TextView) findViewById(R.id.classes_detail_tv);
//        if (TextUtils.isEmpty(Preferences.getInstance(getApplicationContext())
//                .getUserTelPhone())
//                && !TextUtils.isEmpty(Preferences.getInstance(getApplicationContext())
//                        .getDeviceType()))
//        {
//            mTipTv.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            mTipTv.setVisibility(View.GONE);
//        }
		classes_detail_grid = (GridView) findViewById(R.id.classes_list_detail_grid);

		adapter = new GridItemAdapter(titles, images, this);
		classes_detail_grid.setAdapter(adapter);

		adapter.setSeclection(-1);
		adapter.notifyDataSetChanged();

		classes_detail_grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
			                        int position, long id) {

				if (mNetStatusListener != null) {
					mNetStatusListener.cancleToast();
				}
				if (mToastUtil != null) {
					mToastUtil.cancleToast();
				}
				adapter.setSeclection(position);
				adapter.notifyDataSetChanged();

				Intent intent;
				switch (relationList.get(position)) {
					case 0:
						intent = new Intent(ClassesListDetailActivity_sm.this,
								AnnouncementActivity.class);
						intent.putExtra("class_id", mClassId);
						intent.putExtra("fromTeacher", "fromJZ");
						intent.putExtra("stu_user_id", mMemberId);
						if (mClassId.equals("")) {
							Toast.makeText(ClassesListDetailActivity_sm.this,
									"没有与您相关的通知公告信息",
									Toast.LENGTH_SHORT).show();
							break;
						}
						startActivity(intent);
						break;
					case 1:
						intent = new Intent(ClassesListDetailActivity_sm.this,
								HomeworkActivity.class);
						intent.putExtra("stu_user_id", mMemberId);
						if (mClassId.equals("")) {
							Toast.makeText(ClassesListDetailActivity_sm.this,
									"没有与您相关的家庭作业信息",
									Toast.LENGTH_SHORT).show();
							break;
						}
						intent.putExtra("class_id", mClassId);
						startActivity(intent);
						break;

					case 2:
						intent = new Intent(ClassesListDetailActivity_sm.this,
								LeaveMsgActivityTeacher.class);
						intent.putExtra("studentId", mStudentId);
						intent.putExtra("id", mMemberId);
						intent.putExtra("name", memberName);
						Log.e("info", mMemberId + "#" + memberName);
						startActivity(intent);
						break;
					case 3:
						intent = new Intent(ClassesListDetailActivity_sm.this,
								SyllabusActivityFamily.class);
						intent.putExtra("classId", mClassId);
						intent.putExtra("className", mClassName);
						intent.putExtra("userId", mMemberId);
						startActivity(intent);
						break;
					case 4:
						intent = new Intent(ClassesListDetailActivity_sm.this,
								ScoreActivityFamily.class);
						intent.putExtra("studentId", mStudentId);
						intent.putExtra("userId", mMemberId);
						intent.putExtra("memeber_userId", mMemberId);
						intent.putExtra("fromTeacher", "fromJZ");
						intent.putExtra("class_id", mClassId);
						if (mClassId.equals("")) {
							Toast.makeText(ClassesListDetailActivity_sm.this,
									"没有与您相关的成绩信息",
									Toast.LENGTH_SHORT).show();
							break;
						}
						startActivity(intent);
						break;
					case 5:
						intent = new Intent(ClassesListDetailActivity_sm.this,
								ClassCircleActivity.class);
						intent.putExtra("classId", mClassId);
						startActivity(intent);
						break;
					default:
						break;
				}
			}
		});
	}

	class GridItem {
		private String title;

		private int imageId;

		public GridItem() {
			super();
		}

		public GridItem(String title, int imageId) {
			super();
			this.title = title;
			this.imageId = imageId;
		}

		public String getTitle() {
			return title;
		}

		public int getImageId() {
			return imageId;
		}
	}

	private Map<Integer, Integer> relationList = new HashMap<Integer, Integer>();

	private class GridItemAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private List<GridItem> gridItemList;

		public GridItemAdapter(String[] titles, int[] images, Context context) {
			super();
			relationList.clear();
			gridItemList = new ArrayList<GridItem>();
			inflater = LayoutInflater.from(context);
			int pos = 0;
			for (int i = 0; i < images.length; i++) {
				//if (mIndexMap.get(i))
				//{
				GridItem picture = new GridItem(titles[i], images[i]);
				gridItemList.add(picture);
				relationList.put(pos, i);
				pos++;
				// }
			}

			setSeclection(-1);
		}

		@Override
		public int getCount() {
			if (null != gridItemList) {
				return gridItemList.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return gridItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.e_class_detail_item,
						null);
				viewHolder = new ViewHolder();
				viewHolder.classes_detail_title = (TextView) convertView.findViewById(R.id.classes_detail_title);
				viewHolder.classes_detail_image = (ImageView) convertView.findViewById(R.id.classes_detail_image);
				viewHolder.classes_detail_new = (ImageView) convertView.findViewById(R.id.classes_detail_new);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

//            if (position < 6 && mArrayFlag[position]
//                    && relationList.containsValue(position))
//            {
//                viewHolder.classes_detail_new.setVisibility(View.VISIBLE);
//            }
//            else
//            {
//                viewHolder.classes_detail_new.setVisibility(View.INVISIBLE);
//            }

			viewHolder.classes_detail_title.setText(gridItemList.get(position)
					.getTitle());

			viewHolder.classes_detail_image.setImageResource(gridItemList.get(position)
					.getImageId());
			if (clickTemp == position) {
				viewHolder.classes_detail_image.setAlpha(178);
			} else {
				viewHolder.classes_detail_image.setAlpha(255);
			}

			viewHolder.classes_detail_new.setVisibility(View.INVISIBLE);
			return convertView;
		}

		private class ViewHolder {
			public ImageView classes_detail_image;

			public TextView classes_detail_title;

			public ImageView classes_detail_new;
		}

		private int clickTemp = -1;

		//标识选择的Item
		public void setSeclection(int position) {
			clickTemp = position;
		}

	}

	/**
	 * 监听
	 */
	private boolean isWatch = false;


	// private Map<Integer, Boolean> mIndexMap = new HashMap<Integer, Boolean>();

	private final static int INDEX_NOTICE = 0;

	private final static int INDEX_HOMEWORK = 1;

	private final static int INDEX_ATTENDAMCE = 2;

	private final static int INDEX_LEAVEMESSAGE = 3;

	private final static int INDEX_SCHEDULE = 4;

	private final static int INDEX_DEGREE = 5;

	private final static int INDEX_CLASS = 6;

	private final static int INDEX_LOCATION = 7;

	private final static int INDEX_MONITOR = 8;

	private final static int INDEX_CALL = 9;

	private final static int INDEX_ALRAMCLOCK = 10;

	private final static int INDEX_HEALTHY = 11;

	private final static int INDEX_DISTURB = 12;

	private final static int INDEX_RELATIVE = 13;

	private final static int INDEX_WHITE = 14;

	private final static int INDEX_SOS = 15;

	private final static int INDEX_DEVICE = 16;

	private final static int INDEX_TARGET = 17;

	private final static int INDEX_POWER = 18;

	private final static int INDEX_ALARM = 19;

	private final static int INDEX_GPRS = 20;

	private final static int INDEX_SYNC = 21;

	private boolean mInterval = false;

	private boolean mWhiteNum = false;

	private boolean mFamilyNum = false;

	private boolean mSosNum = false;

	private boolean mOCAlarm = false;

	private boolean mElectricity = false;

	private final static int GET_USER_INFO = 1;

	private final static int DEVICE_STATUS = 2;

	private final static int MONITOR = 3;

	private final static int CALL = 4;

	private final static int SYNC_PHONE = 5;

	private final static int SYNC_GK309_GS300_START = 6;

	private final static int SYNC_GK309_GS300_SUCCESS = 7;

	private int deviceId = -1;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case Constants.GET_DATA_SUCCESS:
					switch (server_type.get(0)) {
						case GET_USER_INFO:
							updateUserInfo(msg.obj.toString());
							break;
						case DEVICE_STATUS:
							try {
								JSONObject json = new JSONObject(
										msg.obj.toString());
								if (json.has("data")) {
									JSONObject data = json.getJSONObject("data");
									///////////////////nongpsInterval/////////////////////
									if (!TextUtils.isEmpty(data.getString("nongpsInterval"))) {
										int nongpsInterval = data.getInt("nongpsInterval");
										Preferences.getInstance(getApplicationContext())
												.setNongpsIntervalStatus(nongpsInterval);
									}
									///////////////////nongpsInterval/////////////////////

									///////////////////silence/////////////////////
									if (!TextUtils.isEmpty(data.getString("silenceStatus"))) {
										int silenceStatus = data.getInt("silenceStatus");
										Preferences.getInstance(getApplicationContext())
												.setSilenceStatus(silenceStatus);
									}
									///////////////////silence/////////////////////

									///////////////////whiteSpace/////////////////////
									if (!TextUtils.isEmpty(data.getString("whiteSpace"))) {
										int whiteSpaceType = data.getInt("whiteSpace");
										Preferences.getInstance(getApplicationContext())
												.setWhiteType(whiteSpaceType);
									}
									///////////////////whiteSpace/////////////////////

									///////////////////powerAlarm/////////////////////
									if (!TextUtils.isEmpty(data.getString("powerAlarm"))) {
										int powerAlarm = data.getInt("powerAlarm");
										Preferences.getInstance(getApplicationContext())
												.setLowPower(powerAlarm == 1 ? true
														: false);
										int currPower = data.getInt("currPower");
										Preferences.getInstance(getApplicationContext())
												.setCurrPower(currPower);
										getPowerImage(currPower);
									}
									///////////////////powerAlarm/////////////////////

									///////////////////clock/////////////////////
									if (!TextUtils.isEmpty(data.getString("clockStatus"))) {
										int clockStatus = data.getInt("clockStatus");
										Preferences.getInstance(getApplicationContext())
												.setAlarmClockStatus(clockStatus);
									}
									///////////////////clock/////////////////////

									///////////////////ocAlarm/////////////////////
									if (!TextUtils.isEmpty(data.getString("ocAlarm"))) {
										int ocAlarm = data.getInt("ocAlarm");
										Preferences.getInstance(getApplicationContext())
												.setOCAlarm(ocAlarm);
									}
									///////////////////ocAlarm/////////////////////

									///////////////////gpsStatus/////////////////////
									if (!TextUtils.isEmpty(data.getString("gpsStatus"))) {
										int gpsStatus = data.getInt("gpsStatus");
										Preferences.getInstance(getApplicationContext())
												.setGpsStatus(gpsStatus);
									}
									///////////////////gpsStatus/////////////////////

									///////////////////gpsInterval/////////////////////
									if (!TextUtils.isEmpty(data.getString("gpsInterval"))) {
										int gpsInterval = data.getInt("gpsInterval");
										Preferences.getInstance(getApplicationContext())
												.setGpsInterval(gpsInterval);
									}
									///////////////////gpsStatus/////////////////////

									///////////////////lbsInterval/////////////////////
									if (!TextUtils.isEmpty(data.getString("lbsInterval"))) {
										int lbsInterval = data.getInt("lbsInterval");
										Preferences.getInstance(getApplicationContext())
												.setLbsInterval(lbsInterval);
									}
									///////////////////lbsInterval/////////////////////
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							break;
						case MONITOR:
							Log.d("", "监听成功");
							//                            if (mDialog != null && mDialog.isShowing())
							//                            {
							//                                mDialog.dismiss();
							//                            }
							mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
									ClassesListDetailActivity_sm.this,
									mDialog);
							break;
						case CALL:
							Log.d("", "打电话成功");
							//                            if (mDialog != null && mDialog.isShowing())
							//                            {
							//                                mDialog.dismiss();
							//                            }
							mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
									ClassesListDetailActivity_sm.this,
									mDialog);
							break;
						case SYNC_PHONE:
							String type = Preferences.getInstance(getApplicationContext())
									.getDeviceModel();
							if (type.equalsIgnoreCase("gk309")
									|| type.equalsIgnoreCase("gs300")) {
								if (mSyncDialog != null
										&& mSyncDialog.isShowing()) {
									Log.d("",
											"daitm----gk309,gs300 dialog is showing");
									mSyncDialog.dismiss();
									NetStatusListener.mClickflag = false;

									try {
										JSONObject json = new JSONObject(
												msg.obj.toString());
										if (json.has("data")) {
											JSONObject data = json.getJSONObject("data");
											if (TextUtils.isEmpty(data.getString("data"))
													&& !TextUtils.isEmpty(data.getString("errmsg"))) {
												Toast.makeText(getApplicationContext(),
														data.getString("errmsg"),
														Toast.LENGTH_SHORT)
														.show();
											} else {
												mToastUtil = new ToastUtil(
														ClassesListDetailActivity_sm.this);
												mToastUtil.initToast(mHandler,
														SYNC_GK309_GS300_SUCCESS);
												//                                                Toast.makeText(getApplicationContext(),
												//                                                        getString(R.string.netlistener_set_success),
												//                                                        Toast.LENGTH_SHORT)
												//                                                        .show();
											}
										} else {
											Toast.makeText(getApplicationContext(),
													getString(R.string.netlistener_server_off),
													Toast.LENGTH_SHORT)
													.show();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								} else {
									Log.d("",
											"daitm----gk309,gs300 dialog is dismissed");
								}
							} else {
								Log.d("",
										"daitm----k210,l300 is connecting netstatusListener");
								mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
										ClassesListDetailActivity_sm.this,
										mDialog);
							}
							break;
					}
					server_type.remove(0);
					break;
				case Constants.GET_USERINFO_DATA_FAIL:
					mGetUserInfoHandler.sendEmptyMessage(GETUSERINFO_FAIL);
					break;
				case Constants.GET_DATA_FAIL:
					break;
				case Constants.GET_DEVICE_DATA_FAIL:
					Toast.makeText(ClassesListDetailActivity_sm.this,
							"获取设备信息失败",
							Toast.LENGTH_SHORT).show();
					break;
				case SYNC_GK309_GS300_START:
					mSyncDialog = new ProgressDialog(ClassesListDetailActivity_sm.this);
					mSyncDialog.setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface arg0, int keyCode,
						                     KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								if (mToastCount % 2 == 0) {
									mHandler.removeCallbacksAndMessages(null);
									mSyncDialog.dismiss();
									NetStatusListener.mClickflag = false;
									Toast.makeText(getApplicationContext(),
											getString(R.string.netlistener_interupt),
											Toast.LENGTH_SHORT)
											.show();
								}
								mToastCount++;
							}
							return false;
						}
					});
					mSyncDialog.setMessage(getString(R.string.netlistener_waiting));
					mSyncDialog.setCanceledOnTouchOutside(false);
					mSyncDialog.show();
					ExceptionReciver.setDialog(mSyncDialog);
					break;
				case Constants.SET_NETLISENER_DATA_START:
					mNetStatusListener = new NetStatusListener();
					mDialog = new ProgressDialog(ClassesListDetailActivity_sm.this);
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
				case SYNC_GK309_GS300_SUCCESS:
					break;
			}

		}
	};

	private Handler mTmpHandler = new Handler();

	// 构建Runnable对象，在runnable中更新界面


	public static Bitmap getBitmap(String path) throws IOException {
		if (!TextUtils.isEmpty(path)) {

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
		return null;
	}

	/**
	 * 转换图片成圆形
	 *
	 * @param bitmap 传入Bitmap对象
	 * @return
	 */
	public Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	private List<Integer> server_type = new ArrayList<Integer>();

	/**
	 * 获取用户信息
	 */
	private void getUserInfoFromServer() {
		if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(),
					HttpUtil.responseHandler(getApplicationContext(),
							Constants.NO_NETWORK),
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			new GetUserInfoTask().execute();
		}
		server_type.add(GET_USER_INFO);
	}

	/**
	 * 获取设备所有信息
	 */
	private void deviceStautsFromServer(int deviceId) {
		server_type.add(DEVICE_STATUS);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.DEVICE_STATUS,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DEVICE_DATA_FAIL);
	}

	/**
	 * 远程监听
	 */
	private void monitorFromServer(int deviceId, String phone) {
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
	private void callPhoneFromServer(int deviceId, String phone) {
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

	private Handler mFlagHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case Constants.GET_DATA_SUCCESS:
					try {
						JSONObject json = new JSONObject(msg.obj.toString());
						if (json.has("data")) {
							JSONObject data = json.getJSONObject("data");
//                            if (data.getString("examscore").equals("1"))
//                            {
//                                //考试成绩有新内容
//                                mArrayFlag[INDEX_DEGREE] = true;
//                            }
//                            else
//                            {
//                                //无新内容
//                                mArrayFlag[INDEX_DEGREE] = false;
//                            }
//
//                            if (data.getString("homework").equals("1"))
//                            {
//                                //有新内容
//                                mArrayFlag[INDEX_HOMEWORK] = true;
//                            }
//                            else
//                            {
//                                //无新内容
//                                mArrayFlag[INDEX_HOMEWORK] = false;
//                            }
//
//                            if (data.getString("syllabus").equals("1"))
//                            {
//                                //课程表有新内容
//                                mArrayFlag[INDEX_SCHEDULE] = true;
//                            }
//                            else
//                            {
//                                //无新内容
//                                mArrayFlag[INDEX_SCHEDULE] = false;
//                            }
//
//                            if (data.getString("notice").equals("1"))
//                            {
//                                //通知公告有新内容
//                                mArrayFlag[INDEX_NOTICE] = true;
//                            }
//                            else
//                            {
//                                //无新内容
//                                mArrayFlag[INDEX_NOTICE] = false;
//                            }
//
//                            if (data.getString("timeclock").equals("1"))
//                            {
//                                //有新内容
//                                mArrayFlag[INDEX_ATTENDAMCE] = true;
//                            }
//                            else
//                            {
//                                //无新内容
//                                mArrayFlag[INDEX_ATTENDAMCE] = false;
//                            }
//
//                            if (data.getString("leavemsg").equals("1"))
//                            {
//                                //有新内容
//                                mArrayFlag[INDEX_LEAVEMESSAGE] = true;
//                            }
//                            else
//                            {
//                                //无新内容
//                                mArrayFlag[INDEX_LEAVEMESSAGE] = false;
//                            }
//                            if (adapter != null)
//                            {
//                                adapter = new GridItemAdapter(titles, images,
//                                        ClassesListDetailActivity_sm.this);
//                                classes_detail_grid.setAdapter(adapter);
//                                adapter.notifyDataSetChanged();
//                            }
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 获取用户信息
	 */
	private void getFlagFromServer() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("userId", mMemberId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.GET_USERFLAG,
				mFlagHandler,
				Constants.GET_DATA_SUCCESS,
				3);
	}

	/**
	 * 展示电量信息
	 */

	private void getPowerImage(int power) {
		int status = 0;
		if (power >= 90 && power <= 100) {
			status = 5;
		} else if (power >= 70 && power < 90) {
			status = 4;
		} else if (power >= 40 && power < 70) {
			status = 3;
		} else if (power >= 10 && power < 40) {
			status = 2;
		} else {
			status = 1;
		}

		switch (status) {
			case 1:
				images[18] = R.drawable.ic_launcher;
				break;
			case 2:
				images[18] = R.drawable.ic_launcher;
				break;
			case 3:
				images[18] = R.drawable.ic_launcher;
				break;
			case 4:
				images[18] = R.drawable.ic_launcher;
				break;
			case 5:
				images[18] = R.drawable.ic_launcher;
				break;
		}
	}

//    private void hideDeviceIcon()
//    {
//        if (!mIsCurrentUser)
//        {
//            mIndexMap.put(INDEX_LOCATION, false);
//        }
//        mIndexMap.put(INDEX_MONITOR, false);
//        mIndexMap.put(INDEX_CALL, false);
//        mIndexMap.put(INDEX_ALRAMCLOCK, false);
//        mIndexMap.put(INDEX_DISTURB, false);
//        mIndexMap.put(INDEX_RELATIVE, false);
//        mIndexMap.put(INDEX_SOS, false);
//        mIndexMap.put(INDEX_WHITE, false);
//        mIndexMap.put(INDEX_ALARM, false);
//        mIndexMap.put(INDEX_SYNC, false);
//    }

	@Override
	protected void onPause() {
		super.onPause();

		if (getUserInfDialog != null) {
			getUserInfDialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
		//        if (Preferences.getInstance(getApplicationContext())
		//                .getRefreshclassesDetail())
		//        {
		isClick = false;
		HttpUtil.initUrl(HttpUtil.BASE_URL_SMART_TYPE);
		getUserInfoFromServer();
		//            Preferences.getInstance(getApplicationContext())
		//                    .setRefreshclassesDetail(false);
		//        }
		super.onResume();
	}

	private void syncPhone() {
		if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(),
					HttpUtil.responseHandler(getApplicationContext(),
							Constants.NO_NETWORK),
					Toast.LENGTH_SHORT).show();
			NetStatusListener.mClickflag = false;
			return;
		}
		String type = Preferences.getInstance(getApplicationContext())
				.getDeviceModel();
		if (type.equalsIgnoreCase("gk309") || type.equalsIgnoreCase("gs300")) {
			mHandler.sendEmptyMessage(SYNC_GK309_GS300_START);
		} else {
			mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
		}
		server_type.add(SYNC_PHONE);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.SYN,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}

	private void updateUserInfo(String result) {
		try {
			JSONObject json = new JSONObject(result);
			UserInfo userInfo = new UserInfo();
			JSONObject data = json.getJSONObject("data");
			if (!data.has("function")) {
				return;
			}
			userInfo = UserInfoUtil.parseToUserInfoDetail(result, userInfo);
			if (userInfo == null) {
				return;
			}

			//check gender to set default value in sharedPreference
			if (userInfo.getGender().equals("0")) {
				if (TextUtils.isEmpty(userInfo.getHeight())
						|| userInfo.getHeight().equals("0")) {
					Preferences.getInstance(getApplicationContext())
							.setHeight(160);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setHeight(Integer.parseInt(userInfo.getHeight()));
				}
				if (TextUtils.isEmpty(userInfo.getWeight())
						|| userInfo.getWeight().equals("0")) {
					Preferences.getInstance(getApplicationContext())
							.setWeight(50f);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setWeight(Float.parseFloat(userInfo.getWeight()));
				}
				if (TextUtils.isEmpty(userInfo.getBirthday())
						|| userInfo.getBirthday().equals("0")) {
					Preferences.getInstance(getApplicationContext())
							.setYear(1987);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setYear(Integer.parseInt(userInfo.getBirthday()));
				}
			} else {
				if (TextUtils.isEmpty(userInfo.getHeight())
						|| userInfo.getHeight().equals("0")) {
					Preferences.getInstance(getApplicationContext())
							.setHeight(170);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setHeight(Integer.parseInt(userInfo.getHeight()));
				}
				if (TextUtils.isEmpty(userInfo.getWeight())
						|| userInfo.getWeight().equals("0")) {
					Preferences.getInstance(getApplicationContext())
							.setWeight(70f);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setWeight(Float.parseFloat(userInfo.getWeight()));
				}
				if (TextUtils.isEmpty(userInfo.getBirthday())
						|| userInfo.getBirthday().equals("0")) {
					Preferences.getInstance(getApplicationContext())
							.setYear(1987);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setYear(Integer.parseInt(userInfo.getBirthday()));
				}
			}

			Preferences.getInstance(getApplicationContext())
					.setGender(Integer.parseInt(userInfo.getGender()));
			Preferences.getInstance(getApplicationContext())
					.setUserTelPhone(userInfo.getTelnum());

//            mIndexMap.clear();
//            mIndexMap.put(INDEX_DEVICE, true);
			if (!data.isNull("kuqi")) {
				Calendar fallSleepDate = Calendar.getInstance();
				String tmpFallSleepTime = Preferences.getInstance(getApplicationContext())
						.getFallSleepTime();
				fallSleepDate.set(Calendar.HOUR_OF_DAY,
						Integer.parseInt(tmpFallSleepTime.split(":")[0]));
				fallSleepDate.set(Calendar.MINUTE,
						Integer.parseInt(tmpFallSleepTime.split(":")[1]));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Preferences.getInstance(getApplicationContext())
						.setFallSleepDateTime(sdf.format(fallSleepDate.getTime()));

				JSONArray kuqi = data.getJSONArray("kuqi");
				String addr = "";
				for (int i = 0; i < kuqi.length(); i++) {
					JSONObject item = kuqi.getJSONObject(i);
					addr = item.getString("devicecode") + "," + addr;
				}
				if (!TextUtils.isEmpty(addr)) {
					Preferences.getInstance(getApplicationContext())
							.setWristAddress(addr.substring(0,
									addr.length() - 1));
				} else {
					Preferences.getInstance(getApplicationContext())
							.setWristAddress("");
				}

//                if (data.getJSONArray("kuqi").length() > 0)
//                {
//                    mIndexMap.put(INDEX_TARGET, true);
//                    mIndexMap.put(INDEX_HEALTHY, true);
//                }
//                else
//                {
//                    mIndexMap.put(INDEX_TARGET, false);
//                    mIndexMap.put(INDEX_HEALTHY, false);
//                }
			} else {
				Preferences.getInstance(getApplicationContext())
						.setWristAddress("");
			}

			JSONObject function = data.getJSONObject("function");

			if (!function.isNull("sosNum")) {
				Preferences.getInstance(getApplicationContext())
						.setSosNum(function.getInt("sosNum"));
			}

			mFamilyNum = function.getBoolean("setFamilyNum");
			mSosNum = function.getBoolean("setSosNum");
			mWhiteNum = function.getBoolean("setWhiteNum");
			mInterval = function.getBoolean("setInterval");

			mOCAlarm = function.getBoolean("alarm");
			mElectricity = function.getBoolean("electricity");
//
//
//                mIndexMap.put(INDEX_NOTICE, true);
//                mIndexMap.put(INDEX_HOMEWORK, true);
//                mIndexMap.put(INDEX_ATTENDAMCE, true);
//                mIndexMap.put(INDEX_LEAVEMESSAGE, true);
//                mIndexMap.put(INDEX_SCHEDULE, true);
//                mIndexMap.put(INDEX_DEGREE, true);
//                mIndexMap.put(INDEX_CLASS, true);
//
//
//            mIndexMap.put(INDEX_LOCATION, true);
//            mIndexMap.put(INDEX_MONITOR, function.getBoolean("listener"));
//            mIndexMap.put(INDEX_CALL, function.getBoolean("call"));
//            mIndexMap.put(INDEX_ALRAMCLOCK, function.getBoolean("setClock"));
//            mIndexMap.put(INDEX_DISTURB, function.getBoolean("setNotDisturb"));
//
//            mIndexMap.put(INDEX_RELATIVE, function.getBoolean("setFamilyNum"));
//            mIndexMap.put(INDEX_SOS, function.getBoolean("setSosNum"));
//            mIndexMap.put(INDEX_WHITE, function.getBoolean("setWhiteNum"));
//            mIndexMap.put(INDEX_GPRS, false);//转移到定位中
//            mIndexMap.put(INDEX_SYNC, false);

			Preferences.getInstance(getApplicationContext())
					.setHasFence(function.getBoolean("fence"));
			if (function.has("gpsTimeOpen")) {
				Preferences.getInstance(getApplicationContext())
						.setSetUpGpsTimeOpen(function.getBoolean("gpsTimeOpen"));
			}
			if (function.has("lbsInterval")) {
				Preferences.getInstance(getApplicationContext())
						.setSetUpLbsInterval(function.getBoolean("lbsInterval"));
			}
//
//            if (!function.getBoolean("alarm")
//                    && !function.getBoolean("electricity"))
//            {
//                mIndexMap.put(INDEX_ALARM, false);
//            }
//            else
//            {
//                mIndexMap.put(INDEX_ALARM, true);
//            }
//
//            mIndexMap.put(INDEX_POWER, false);

//            if (Integer.valueOf(android.os.Build.VERSION.SDK) < 18)
//            {
//                //                                    Toast.makeText(FamilyDetailActivity.this,
//                //                                            "由于您的系统版本低于4.3，所以不支持健康和设备",
//                //                                            Toast.LENGTH_LONG).show();
//                //                                    mIndexMap.put(INDEX_HEALTHY, false);
//                mIndexMap.put(INDEX_TARGET, false);
//            }
//            mIndexMap.put(INDEX_TARGET, false);//将目标页面转移到健康数据页面

			if (!data.isNull("timecard")) {
				Preferences.getInstance(getApplicationContext())
						.setHasTimeCard(true);
				JSONObject timeCard = data.getJSONObject("timecard");
				Preferences.getInstance(getApplicationContext())
						.setTimeCardCode(timeCard.getString("devicecode"));
			} else {
				Preferences.getInstance(getApplicationContext())
						.setHasTimeCard(false);
				Preferences.getInstance(getApplicationContext())
						.setTimeCardCode("");
			}

			isParent = data.getBoolean("familySchool");
			isStudent = data.getBoolean("student");
			isOld = data.getBoolean("older");
			memberName = data.getString("userrealname");
			if (isStudent && !isOld) {//student
				//student card
				if (!data.isNull("studentphone")) {
					Preferences.getInstance(getApplicationContext())
							.setHasStudentPhone(true);
					JSONObject studentphone = data.getJSONObject("studentphone");
					if (!studentphone.isNull("id")) {
						deviceId = studentphone.getInt("id");
						mStrDeviceId = studentphone.getString("id");
						mStrIMEI = studentphone.getString("devicecode");
						Preferences.getInstance(getApplicationContext())
								.setStudentCode(mStrIMEI);
						Preferences.getInstance(getApplicationContext())
								.setDeviceModel(studentphone.getString("devicemodel"));
					} else {
						deviceId = -1;
						Preferences.getInstance(getApplicationContext())
								.setStudentCode("");
					}
					Preferences.getInstance(getApplicationContext())
							.setDeviceType("student");
					// mIndexMap.put(INDEX_SYNC, true);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setDeviceType("");
					Preferences.getInstance(getApplicationContext())
							.setHasStudentPhone(false);
				}
				Preferences.getInstance(getApplicationContext())
						.setRoleType(Constants.TYPE_IS_STUDENT);
				JSONObject studentInfo = data.getJSONObject("studentInfo");
				mStudentId = studentInfo.getString("id");
				mClassId = studentInfo.getString("class_id");
				mClassName = studentInfo.getString("class_name");
				//                          mStudentId = StudentInfoData.getString("id");
				mGetUserInfoHandler.sendEmptyMessage(GETUSERINFO_SUCCESS);
			} else if (isOld && !isStudent) {//old
				if (!data.isNull("oldiephone")) {
					JSONObject oldiephone = data.getJSONObject("oldiephone");
					if (!oldiephone.isNull("id")) {
						deviceId = oldiephone.getInt("id");
						mStrDeviceId = oldiephone.getString("id");
						mStrIMEI = oldiephone.getString("devicecode");
						Preferences.getInstance(getApplicationContext())
								.setOldCode(mStrIMEI);
						Preferences.getInstance(getApplicationContext())
								.setDeviceModel(oldiephone.getString("devicemodel"));
					} else {
						deviceId = -1;
						Preferences.getInstance(getApplicationContext())
								.setOldCode("");
					}
					Preferences.getInstance(getApplicationContext())
							.setDeviceType("old");
					// mIndexMap.put(INDEX_SYNC, true);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setDeviceType("");
				}
				Preferences.getInstance(getApplicationContext())
						.setRoleType(Constants.TYPE_IS_OLD);
			} else if (!isOld && !isStudent) {//parent
				// mIndexMap.put(INDEX_DEVICE, true);
				Preferences.getInstance(getApplicationContext())
						.setDeviceType("");
				Preferences.getInstance(getApplicationContext())
						.setTimeCardCode("");
				Preferences.getInstance(getApplicationContext())
						.setStudentCode("");
				Preferences.getInstance(getApplicationContext()).setOldCode("");
				Preferences.getInstance(getApplicationContext())
						.setDeviceModel("");
				Preferences.getInstance(getApplicationContext())
						.setRoleType(Constants.TYPE_IS_PARENT);
			}
			//                      initGridLayout();

			mHeadPicPath = data.getString("headpicpath")
					+ data.getString("headpicname");


			if (deviceId == -1) {
				//hide all device icon
				// hideDeviceIcon();
				mGetUserInfoHandler.sendEmptyMessage(GETUSERINFO_SUCCESS);
				initGridLayout();
				return;
			}
			initGridLayout();
			Preferences.getInstance(getApplicationContext())
					.setDeviceId(deviceId);
			deviceStautsFromServer(deviceId);
			mGetUserInfoHandler.sendEmptyMessage(GETUSERINFO_SUCCESS);
		} catch (Exception e) {
			mGetUserInfoHandler.sendEmptyMessage(GETUSERINFO_FAIL);
			e.printStackTrace();
			log("----" + e.toString());
		}
	}

	/**
	 * [Get User Info Task]<BR>
	 * 查询用户信息
	 *
	 * @author archermind
	 * @version [ODP Client R001C01LAI141, 2014年12月26日]
	 */
	class GetUserInfoTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String result = UserInfoUtil.getUserInfoByMemberId(ClassesListDetailActivity_sm.this,
					mMemberId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (null == result || TextUtils.isEmpty(result)) {
				log("GetUserInfoTask----error----else");
				updateState(GETUSERINFO_FAIL, Constants.JSON_ERROR);
				return;
			}
			boolean isCode = false;
			int resultcode = Constants.UNKNOW_RESULT;
			try {
				resultcode = Integer.parseInt(result);
				isCode = true;
				log("GetUserInfoTask----error----code:" + result);
			} catch (NumberFormatException e) {
				isCode = false;
			}
			if (isCode) {
				log("GetUserInfoTask----error----" + result);
				updateState(GETUSERINFO_FAIL, resultcode);
			} else {
				UserInfo mUserInfo = new UserInfo();
				UserInfo userInfo = UserInfoUtil.parseToUserInfoDetail(result,
						mUserInfo);
				if (userInfo == null) {
					log("login----fail----" + result);
					updateState(GETUSERINFO_FAIL, Constants.JSON_ERROR);
				} else {
					log("login----success----" + result);
					updateState(GETUSERINFO_SUCCESS, Constants.SC_OK);
					updateUserInfo(result);
					server_type.remove(0);
					//                    updateView();
					//                    mUserInfo = userInfo;
					//                    loadUserImage();
				}
			}
		}

	}

	/**
	 * [update state]<BR>
	 * 更新界面状态
	 *
	 * @param state 状态
	 * @param code  状态码
	 */
	private void updateState(int state, int code) {
		String errormsg = HttpUtil.responseHandler(this, code);
		switch (state) {
			case GETUSERINFO_START:
				break;
			case GETUSERINFO_SUCCESS:
				break;
			case GETUSERINFO_FAIL:
				switch (code) {
					case Constants.NO_NETWORK:
						showToast(R.string.no_network);
						break;
					case Constants.SERVER_OFFLINE:
						showToast(R.string.info_server_offline);
						break;
					case Constants.UNKNOW_RESULT:
						showToast(R.string.info_getuserinfo_fail);
						break;
					default:
						showToast(errormsg);
						break;
				}
				break;
			default:
				log("------updateState----else---" + state + "  " + errormsg);
				showToast(R.string.unknown_error);
				break;

		}
	}

	@Override
	public void finish() {
		if (mNetStatusListener != null) {
			mNetStatusListener.setActivityFinish();
			NetStatusListener.mClickflag = false;
			mNetStatusListener.setRunning(false);
			mNetStatusListener.cancleToast();
		}
		if (mToastUtil != null) {
			mToastUtil.cancleToast();
		}
		super.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//        if (keyCode == event.KEYCODE_BACK)
		//        {
		if (mNetStatusListener != null
				&& mNetStatusListener.getCustomToast() != null) {
			return mNetStatusListener.cancleToast();
		} else if (mToastUtil != null && mToastUtil.getCustomToast() != null) {
			return mToastUtil.cancleToast();
		}
		//        }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
