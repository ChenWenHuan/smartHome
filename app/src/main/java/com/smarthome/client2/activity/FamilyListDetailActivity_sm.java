package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
//import com.smarthome.client2.view.FamilyPopMenuLayout_sm;
//import com.smarthome.client2.view.MyFamilyLayout;
import com.smarthome.client2.bean.UserInfo;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.ui.AnnouncementActivity;
import com.smarthome.client2.familySchool.ui.AttendanceActivityFamily;
import com.smarthome.client2.familySchool.ui.HomeworkActivity;
import com.smarthome.client2.familySchool.ui.ScoreActivityFamily;
import com.smarthome.client2.familySchool.ui.SyllabusActivityFamily;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HomeListener;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.MySoftInputUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.ToastUtil;
import com.smarthome.client2.util.UserInfoUtil;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.widget.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyListDetailActivity_sm extends BaseActivity {


	private CustomActionBar actionBar;

	private GridView family_detail_grid;


	//  private int userId = -1;
	/**
	 * 用户id
	 */
	private String mMemberId = "";

	private String mMemberUserName = "";

	private String mClassName = "";
	
	private String familyID = "";

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


	private int mToastCount = 0;

	private ToastUtil mToastUtil;

	private NetStatusListener mNetStatusListener;

	private String[] titles = new String[]{ "通知公告", "课程表",
			"家庭作业", "出勤记录",
			"成绩信息", "微聊",
			"位置",    "班级圈",
			"设备管理", "拨打电话",
			"健康数据",};

	private int[] images = {
			R.drawable.message_notice,  R.drawable.class_schedule,
			R.drawable.homework,        R.drawable.ic_attendance,
			R.drawable.scoreinfo,       R.drawable.message_board,
			R.drawable.ic_position,     R.drawable.classlist,
			R.drawable.supervisory_unit,R.drawable.phone_icon,
			R.drawable.health_data_sm11,};



	private Boolean[] mArrayFlag = {false, false,
									false, false,
									false, true,
									true, false,
									false, true,
									true};
	private final static int INDEX_NOTICE = 0;
	private final static int INDEX_SCHEDULE = 1;
	private final static int INDEX_HOMEWORK = 2;
	private final static int INDEX_ATTENDAMCE = 3;
	private final static int INDEX_SCOREINFO = 4;
	private final static int INDEX_LEAVEMESSAGE = 5;
	private final static int INDEX_LOCATION = 6;
	private final static int INDEX_CLASSLIST = 7;

	private final static int INDEX_DEVICEMANAGE = 8;  //设备管理
	private final static int INDEX_DIAL_PERSON = 9;  //打电话
	private final static int INDEX_HEALTHY = 10;

	public final static int INDEX_MONITOR = 11;  //XIAM 都加一
	public final static int INDEX_CALL = 12;
	public final static int INDEX_ALRAMCLOCK = 13;
	public final static int INDEX_DISTURB = 14;
	public final static int INDEX_RELATIVE = 15;
	public final static int INDEX_WHITE = 16;
	public final static int INDEX_SOS = 17;
	public final static int INDEX_TARGET = 18;
	public final static int INDEX_POWER = 19;
	public final static int INDEX_ALARM = 20;
	public final static int INDEX_GPS = 21;
	public final static int INDEX_SYNC = 22;


	private ImageView ePersonImageBack;
	private ImageView ePersonImageOptionMenu;
	private CircleImageView imgPersonHead;
	private TextView ePersonTextTitle;
	private TextView ePersonTextNickName;

	private HomeListener mHomeListener;
	
	private ImageDownLoader mImageLoader;
	private FamilyPopMenuLayout_sm1 familyLayoutPopMenu;


	private LinearLayout family_detail_edit_dialog;

	private EditText family_detail_dialog_edit;

	private TextView family_detail_edit_title;

	private Button family_detail_edit_layout_btn_1,
			family_detail_edit_layout_btn_2;
	
	private String linkDeviceID = "";
	private String linkDeviceAccout = "";
	private String watchTelNun = "";
	
	private int mBindedDeviceType = 0;

	/**
	 * 监听
	 */
	private boolean isWatch = false;

	private Map<Integer, Boolean> mIndexMap = new HashMap<Integer, Boolean>();




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
	
	private String familyKeyPersonID = "";
	
	private String headImgUrl = "";
	private String mStrTelNum = "";
	private final int USER_INFO = 2;      // 回传用户信息
	private String picPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.e_personal_family);

		ePersonImageBack = (ImageView) findViewById(R.id.e_person_back);
		ePersonImageOptionMenu = (ImageView) findViewById(R.id.e_person_options_menu);
		ePersonTextTitle = (TextView) findViewById(R.id.e_person_tv_title);
		ePersonTextTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FamilyListDetailActivity_sm.this,
						UiEditFamilyName.class);
				intent.setAction(UiEditFamilyName.EDIT_FAMILY_MEM_MARK);
				intent.putExtra("userid", mMemberId);
				intent.putExtra("familyid", familyID);
                startActivityForResult(intent, 0);
			}
		});
		ePersonTextNickName = (TextView) findViewById(R.id.e_person_tv_nickname);
		imgPersonHead = (CircleImageView) findViewById(R.id.e_person_img_person_pic);
		ePersonImageOptionMenu.setVisibility(View.INVISIBLE);
		ePersonImageOptionMenu.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Rect rect = new Rect();
						getWindow()
								.getDecorView()
								.getWindowVisibleDisplayFrame(rect);
						int statusBarHeight = rect.top; //状态栏高度
						int px = ePersonImageOptionMenu.getHeight();
						familyLayoutPopMenu = new FamilyPopMenuLayout_sm1(FamilyListDetailActivity_sm.this, arg0, mIndexMap, statusBarHeight
								+ px);
						familyLayoutPopMenu.initFamilyFrame(arg0);
					}
				});

		ePersonImageBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FamilyListDetailActivity_sm.this.finish();
			}
		});


		imgPersonHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		getUserInfDialog = new ProgressDialog(this);
		getUserInfDialog.setCanceledOnTouchOutside(false);

		getUserInfDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//                myAsyncTask = null;
				isLoading = false;
			}
		});

		Bundle bundle = getIntent().getExtras();
		//userId = Integer.parseInt(bundle.getString("userId"));
		mMemberId = bundle.getString("memeber_userId");
		familyID = bundle.getString("familyid");
		familyKeyPersonID = bundle.getString("familykeypersonid");
		String name = bundle.getString("memeber_alias");
		if (TextUtils.isEmpty(name)) {
			name = bundle.getString("memeber_userName");
			ePersonTextNickName.setVisibility(View.GONE);
		} else {
			ePersonTextNickName.setText(bundle.getString("memeber_userName"));
		}

		ePersonTextTitle.setText(name);
		mMemberUserName = name;
		mIsCurrentUser = Preferences.getInstance(this).getUserID().equals(mMemberId);
		
		mImageLoader = ImageDownLoader.getInstance();
		headImgUrl = bundle.getString("imgurl");
		mImageLoader.downloadImage(headImgUrl, FsConstants.HANDLE_IMAGE, mGetUserInfoHandler);

		Preferences.getInstance(getApplicationContext())
				.setFamilyUserId(Integer.parseInt(mMemberId));

		family_detail_edit_dialog = (LinearLayout) findViewById(R.id.family_detail_edit_dialog);
		family_detail_dialog_edit = (EditText) findViewById(R.id.family_detail_dialog_edit);
		family_detail_edit_title = (TextView) findViewById(R.id.family_detail_edit_title);
		family_detail_edit_layout_btn_1 = (Button) findViewById(R.id.family_detail_edit_layout_btn_1);
		family_detail_edit_layout_btn_2 = (Button) findViewById(R.id.family_detail_edit_layout_btn_2);
		family_detail_edit_layout_btn_1.setOnClickListener(watcherListener);
		family_detail_edit_layout_btn_2.setOnClickListener(watcherListener);

		hideDeviceIcon();
		initGridLayout();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK
				&& requestCode == UserInfoUtil.INTENT_NICKNAME_RESULT) {
			mMemberUserName = data.getStringExtra(UserInfoUtil.INTENT_KEY_NICKNAME);
			actionBar.setTvTitleMsg(mMemberUserName);
		}else if (resultCode == RESULT_OK && requestCode == 0){
			  Bundle bundleData = data.getExtras();
			  ePersonTextTitle.setText(bundleData.getString("familyname"));
		} else if (resultCode == RESULT_OK && requestCode == USER_INFO) {
			if (data != null && data.getExtras() != null) {
				Bundle bundle = data.getExtras();
				picPath = bundle.getString("headPicPath");
				String nickName = bundle.getString("nickName");
				if (TextUtils.isEmpty(nickName)) {
					ePersonTextNickName.setText(nickName);
				} else {
					ePersonTextNickName.setText(nickName);
				}

				if (!TextUtils.isEmpty(picPath))
					mImageLoader.downloadImage(picPath,
							FsConstants.HEAD_IMAGE, mHandler);
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
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
						monitorFromServer(Preferences.getInstance(getApplicationContext())
										.getDeviceId(),
								family_detail_dialog_edit.getText().toString());
					} else {
						callPhoneFromServer(Preferences.getInstance(getApplicationContext())
										.getDeviceId(),
								family_detail_dialog_edit.getText()
										.toString()
										.trim());
					}
					family_detail_edit_dialog.setVisibility(View.GONE);
					family_detail_grid.setEnabled(true);
					MySoftInputUtil.hideInputMethod(getApplicationContext(),
							family_detail_edit_dialog);
					break;
				case R.id.family_detail_edit_layout_btn_2:
					family_detail_edit_dialog.setVisibility(View.GONE);
					family_detail_grid.setEnabled(true);
					MySoftInputUtil.hideInputMethod(getApplicationContext(),
							family_detail_edit_dialog);
					NetStatusListener.mClickflag = false;
					break;
			}
		}
	};

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
				case FsConstants.HANDLE_IMAGE:
					if (msg.obj != null) {
						imgPersonHead.setImageBitmap((Bitmap) msg.obj);
					}
					break;
				case GETUSERINFO_SUCCESS:
					isLoading = false;
					getUserInfDialog.setMessage("获取用户数据成功");
					getUserInfDialog.dismiss();
					break;
				case GETUSERINFO_FAIL:
					isLoading = false;
					getUserInfDialog.dismiss();
					Toast.makeText(FamilyListDetailActivity_sm.this,
							"获取用户数据失败",
							Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};


	private void initGridLayout() {
		family_detail_grid = (GridView) findViewById(R.id.e_person_family_detail_grid);

		adapter = new GridItemAdapter(titles, images, this);
		family_detail_grid.setAdapter(adapter);

		adapter.setSeclection(-1);
		adapter.notifyDataSetChanged();

		family_detail_grid.setOnItemClickListener(new OnItemClickListener() {
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
					case INDEX_NOTICE:
						intent = new Intent(FamilyListDetailActivity_sm.this,
								AnnouncementActivity.class);
						intent.putExtra("class_id", mClassId);
						intent.putExtra("fromTeacher", "fromJZ");
						intent.putExtra("stu_user_id", mMemberId);
						if (mClassId.equals("")) {
							Toast.makeText(FamilyListDetailActivity_sm.this,
									"没有与您相关的通知公告信息",
									Toast.LENGTH_SHORT).show();
							break;
						}
						startActivity(intent);
						break;
					case INDEX_SCHEDULE:
						intent = new Intent(FamilyListDetailActivity_sm.this,
								SyllabusActivityFamily.class);
						intent.putExtra("classId", mClassId);
						intent.putExtra("className", mClassName);
						intent.putExtra("userId", mMemberId);
						startActivity(intent);
						break;

					case INDEX_HOMEWORK:
						intent = new Intent(FamilyListDetailActivity_sm.this,
								HomeworkActivity.class);
						intent.putExtra("stu_user_id", mMemberId);
						if (mClassId.equals("")) {
							Toast.makeText(FamilyListDetailActivity_sm.this,
									"没有与您相关的家庭作业信息",
									Toast.LENGTH_SHORT).show();
							break;
						}
						intent.putExtra("class_id", mClassId);
						startActivity(intent);
						break;
					case INDEX_ATTENDAMCE:
						intent = new Intent(FamilyListDetailActivity_sm.this,
								AttendanceActivityFamily.class);
						intent.putExtra("studentId", mStudentId);
						intent.putExtra("userId", mMemberId);
						startActivity(intent);
						break;
					case INDEX_SCOREINFO:
						intent = new Intent(FamilyListDetailActivity_sm.this,
								ScoreActivityFamily.class);
						intent.putExtra("studentId", mStudentId);
						intent.putExtra("userId", mMemberId);
						intent.putExtra("memeber_userId", mMemberId);
						intent.putExtra("fromTeacher", "fromJZ");
						intent.putExtra("class_id", mClassId);
						if (mClassId.equals("")) {
							Toast.makeText(FamilyListDetailActivity_sm.this,
									"没有与您相关的成绩信息",
									Toast.LENGTH_SHORT).show();
							break;
						}
						startActivity(intent);
						break;

					case INDEX_LEAVEMESSAGE:
						
						if (mBindedDeviceType == 1){
							//学生机 
							Toast.makeText(FamilyListDetailActivity_sm.this,
									"学生机暂不支持留言功能！",
									Toast.LENGTH_SHORT).show();
							return;
						}else if (mBindedDeviceType == 2){
							//老人机
							Toast.makeText(FamilyListDetailActivity_sm.this,
									"老人机暂不支持留言功能！",
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (!linkDeviceAccout.equals("") && !linkDeviceID.equals("")){
							intent = new Intent(FamilyListDetailActivity_sm.this, WatchChatActivity_SM.class);
							intent.putExtra("deviceid", linkDeviceID);
							intent.putExtra("deviceaccount", linkDeviceAccout);
							intent.putExtra("title", mMemberUserName);
							intent.putExtra("headimg", headImgUrl);
							intent.putExtra("deviceNum", watchTelNun);
							intent.putExtra(FsConstants.TYPE_ADD_FLAG, FsConstants.TYPE_SEND_PERSONAL_MSG);
						}else{
//							intent = new Intent(FamilyListDetailActivity_sm.this, WriteMsgActivity.class);
							intent = new Intent(FamilyListDetailActivity_sm.this, WatchChatActivity_SM.class);
							intent.putExtra("ids", familyID);
							intent.putExtra("title", mMemberUserName);
							intent.putExtra(FsConstants.TYPE_ADD_FLAG, FsConstants.TYPE_ADD_FAMILY_MSG);
						}
						intent.setAction("");
						FamilyListDetailActivity_sm.this.startActivity(intent);
						break;

					case INDEX_LOCATION:
						if (mBindedDeviceType == 4){
							
							Toast.makeText(FamilyListDetailActivity_sm.this,
									"智能机用户暂不支持位置功能！",
									Toast.LENGTH_SHORT).show();
							return;
						}
							
						intent = new Intent(FamilyListDetailActivity_sm.this,
								LocationActivity.class);
						intent.putExtra("isCurrentUser", mIsCurrentUser);
						intent.putExtra("interval", mInterval);
						intent.putExtra("id", deviceId);
						intent.putExtra("deviceId", mStrDeviceId);
						intent.putExtra("userId", mMemberId);
						intent.putExtra("headimg", headImgUrl);
						intent.putExtra("linkBindNum", "");
						if (!linkDeviceAccout.equals("") && !linkDeviceID.equals("")){
							intent.putExtra("deviceId",linkDeviceID);
							intent.putExtra("linkBindNum", linkDeviceAccout);
							intent.putExtra("watchTelNum", watchTelNun);
						}
						startActivity(intent);
						break;

//					case INDEX_CLASSLIST:
//						intent = new Intent(FamilyListDetailActivity_sm.this,
//								ClassCircleActivity_sm.class);
//						intent.putExtra("classId", mClassId);
//						startActivity(intent);
//						break;

					case INDEX_HEALTHY:
						intent = new Intent(FamilyListDetailActivity_sm.this,
								WatchHealthActivity.class);
						if (!linkDeviceAccout.equals("") && !linkDeviceID.equals("")){
							intent.putExtra("deviceId",linkDeviceID);
							intent.putExtra("linkBindNum", linkDeviceAccout);
							intent.putExtra("watchTelNum", watchTelNun);
							intent.putExtra("title", mMemberUserName);
						}
						startActivity(intent);
						break;

					case INDEX_DEVICEMANAGE:
						intent = new Intent(FamilyListDetailActivity_sm.this,
								BindActivity_sm.class);
						intent.putExtra("userId", mMemberId);
						intent.putExtra("familyKeyPersonID", familyKeyPersonID);  //户主的id
						startActivity(intent);
						break;

					case INDEX_DIAL_PERSON:
						intent = new Intent(Intent.ACTION_CALL);
						String call = "tel:" + mStrTelNum;
						
						if (!linkDeviceID.equals("")){
							call = "tel:" + watchTelNun;
						}
						intent.setData(Uri.parse(call));
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
		family_detail_grid.setEnabled(false);
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
		family_detail_grid.setEnabled(false);
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
				if (mIndexMap.get(i)) {
					GridItem picture = new GridItem(titles[i], images[i]);
					gridItemList.add(picture);
					relationList.put(pos, i);
					pos++;
				}
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
				convertView = inflater.inflate(R.layout.e_family_detail_item,
						null);
				viewHolder = new ViewHolder();
				viewHolder.family_detail_title = (TextView) convertView.findViewById(R.id.e_family_detail_title);
				viewHolder.family_detail_image = (ImageView) convertView.findViewById(R.id.e_family_detail_image);
//				viewHolder.family_detail_new = (ImageView) convertView.findViewById(R.id.e_family_detail_new);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

//			if (position < 6 && mArrayFlag[position]
//					&& relationList.containsValue(position)) {
//				viewHolder.family_detail_new.setVisibility(View.VISIBLE);
//			} else {
//				viewHolder.family_detail_new.setVisibility(View.INVISIBLE);
//			}

			viewHolder.family_detail_title.setText(gridItemList.get(position)
					.getTitle());

			viewHolder.family_detail_image.setImageResource(gridItemList.get(position)
					.getImageId());
			if (clickTemp == position) {
				viewHolder.family_detail_image.setAlpha(178);
			} else {
				viewHolder.family_detail_image.setAlpha(255);
			}

			//          viewHolder.family_detail_new.setVisibility(View.VISIBLE);
			return convertView;
		}

		private class ViewHolder {
			public ImageView family_detail_image;

			public TextView family_detail_title;

			public ImageView family_detail_new;
		}

		private int clickTemp = -1;

		//标识选择的Item
		public void setSeclection(int position) {
			clickTemp = position;
		}

	}


	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case FsConstants.HEAD_IMAGE:
					if (msg.obj != null) {
						imgPersonHead.setImageBitmap((Bitmap) msg.obj);
//						if(picPath != null) {
//							Preferences preferences = Preferences.getInstance(ma.getApplicationContext());
//							preferences.setHeadPath(picPath);
//							ma.sendBroadcast(new Intent(HomeFragement_V11.NEW_HEAD_IMG));
//						}
					}
					break;
				case Constants.SESSION_TIME_OUT:
					Intent intent = new Intent(FamilyListDetailActivity_sm.this,
							LoginActivity_sm.class);
					startActivity(intent);
					break;

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
									FamilyListDetailActivity_sm.this,
									mDialog);
							break;
						case CALL:
							Log.d("", "打电话成功");
							//                            if (mDialog != null && mDialog.isShowing())
							//                            {
							//                                mDialog.dismiss();
							//                            }
							mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
									FamilyListDetailActivity_sm.this,
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
														FamilyListDetailActivity_sm.this);
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
										FamilyListDetailActivity_sm.this,
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
					Toast.makeText(FamilyListDetailActivity_sm.this,
							"获取设备信息失败",
							Toast.LENGTH_SHORT).show();
					break;
				case SYNC_GK309_GS300_START:
					mSyncDialog = new ProgressDialog(FamilyListDetailActivity_sm.this);
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
					mDialog = new ProgressDialog(FamilyListDetailActivity_sm.this);
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
							//考试成绩有新内容
//无新内容
							mArrayFlag[INDEX_SCOREINFO] = data.getString("examscore").equals("1");

							//有新内容
//无新内容
							mArrayFlag[INDEX_HOMEWORK] = data.getString("homework").equals("1");

							//课程表有新内容
//无新内容
							mArrayFlag[INDEX_SCHEDULE] = data.getString("syllabus").equals("1");

							//通知公告有新内容
//无新内容
							mArrayFlag[INDEX_NOTICE] = data.getString("notice").equals("1");

							//有新内容
//无新内容
							mArrayFlag[INDEX_ATTENDAMCE] = data.getString("timeclock").equals("1");

							//有新内容
//无新内容
							mArrayFlag[INDEX_LEAVEMESSAGE] = data.getString("leavemsg").equals("1");
							if (adapter != null) {
								adapter = new GridItemAdapter(titles, images,
										FamilyListDetailActivity_sm.this);
								family_detail_grid.setAdapter(adapter);
								adapter.notifyDataSetChanged();
							}
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


	}

	private void hideDeviceIcon() {
//		if (!mIsCurrentUser) {
//			mIndexMap.put(INDEX_LOCATION, false);
//		}
		mIndexMap.put(INDEX_NOTICE, false);
		mIndexMap.put(INDEX_SCHEDULE, false);
		mIndexMap.put(INDEX_HOMEWORK, false);
		mIndexMap.put(INDEX_ATTENDAMCE, false);
		mIndexMap.put(INDEX_SCOREINFO, false);
		mIndexMap.put(INDEX_LEAVEMESSAGE, true);
		mIndexMap.put(INDEX_LOCATION, true);
		mIndexMap.put(INDEX_CLASSLIST, false);
		mIndexMap.put(INDEX_MONITOR, false);
		mIndexMap.put(INDEX_DEVICEMANAGE, true);
		mIndexMap.put(INDEX_DIAL_PERSON, true);
		mIndexMap.put(INDEX_HEALTHY, false);



		mIndexMap.put(INDEX_MONITOR, false);
		mIndexMap.put(INDEX_CALL, false);
		mIndexMap.put(INDEX_ALRAMCLOCK, false);
		mIndexMap.put(INDEX_DISTURB, false);
		mIndexMap.put(INDEX_RELATIVE, false);
		mIndexMap.put(INDEX_SOS, false);
		mIndexMap.put(INDEX_WHITE, false);
		mIndexMap.put(INDEX_ALARM, false);
		mIndexMap.put(INDEX_SYNC, false);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (getUserInfDialog != null) {
			getUserInfDialog.dismiss();
		}
	}

	@Override
	protected void onResume() {

		HttpUtil.initUrl(HttpUtil.BASE_URL_SMART_TYPE);
		getUserInfoFromServer();

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
	
	private void parserUserInfo(String result){
		String[] linkDeviceInfo = null;
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
			
			mBindedDeviceType = UserInfoUtil.parserUserDeviceType(result);
			
			linkDeviceInfo = UserInfoUtil.parserUserLinkTopDeviceInfo(result);
			if (linkDeviceInfo != null){
				linkDeviceID = linkDeviceInfo[0];
				linkDeviceAccout = linkDeviceInfo[1];
				watchTelNun = linkDeviceInfo[2];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void updateUserInfo(String result) {
		String[] linkDeviceInfo = null;
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
			
			mBindedDeviceType = UserInfoUtil.parserUserDeviceType(result);
			
			linkDeviceInfo = UserInfoUtil.parserUserLinkTopDeviceInfo(result);
			if (linkDeviceInfo != null){
				linkDeviceID = linkDeviceInfo[0];
				linkDeviceAccout = linkDeviceInfo[1];
				watchTelNun = linkDeviceInfo[2];
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

			if (!userInfo.getGender().equals("")){
				Preferences.getInstance(getApplicationContext())
						.setGender(Integer.parseInt(userInfo.getGender()));
			}
			Preferences.getInstance(getApplicationContext())
					.setUserTelPhone(userInfo.getTelnum());

			mIndexMap.clear();
			mIndexMap.put(INDEX_DEVICEMANAGE, true);
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

				if (data.getJSONArray("kuqi").length() > 0) {
					mIndexMap.put(INDEX_TARGET, true);
					if (data.getBoolean("student")) {
						mIndexMap.put(INDEX_HEALTHY, false);
					} else {
						// disable health data
						mIndexMap.put(INDEX_HEALTHY, false);
						//mIndexMap.put(INDEX_HEALTHY, true);
					}

				} else {
					mIndexMap.put(INDEX_TARGET, false);
					mIndexMap.put(INDEX_HEALTHY, false);
				}
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

			mStrTelNum = data.getString("telnum");

			if (data.getBoolean("student")) {
				mIndexMap.put(INDEX_NOTICE, true);
				mIndexMap.put(INDEX_SCHEDULE, true);
				mIndexMap.put(INDEX_HOMEWORK, true);
				mIndexMap.put(INDEX_ATTENDAMCE, true);
				mIndexMap.put(INDEX_SCOREINFO, true);
				mIndexMap.put(INDEX_LEAVEMESSAGE, true);
				mIndexMap.put(INDEX_LOCATION, true);
				mIndexMap.put(INDEX_CLASSLIST, true);
			} else {
				mIndexMap.put(INDEX_NOTICE, false);
				mIndexMap.put(INDEX_SCHEDULE, false);
				mIndexMap.put(INDEX_HOMEWORK, false);
				mIndexMap.put(INDEX_ATTENDAMCE, false);
				mIndexMap.put(INDEX_SCOREINFO, false);
				mIndexMap.put(INDEX_LEAVEMESSAGE, true);
				mIndexMap.put(INDEX_LOCATION, true);
				mIndexMap.put(INDEX_CLASSLIST, false);
				// disable health data
				mIndexMap.put(INDEX_HEALTHY, false);
				//mIndexMap.put(INDEX_HEALTHY, true);
			}

			if (!linkDeviceAccout.equals("") && !linkDeviceID.equals("")){
				mIndexMap.put(INDEX_HEALTHY, true);
			}
			mIndexMap.put(INDEX_DIAL_PERSON, true);

			if (data.getBoolean("student")) {
				//外部菜单挪到了设备管理
				ePersonImageOptionMenu.setVisibility(View.INVISIBLE);

			} else {
				//外部菜单挪到了设备管理
				ePersonImageOptionMenu.setVisibility(View.INVISIBLE);

			}


			mIndexMap.put(INDEX_MONITOR, function.getBoolean("listener"));
			mIndexMap.put(INDEX_CALL, function.getBoolean("call"));
			mIndexMap.put(INDEX_ALRAMCLOCK, function.getBoolean("setClock"));
			mIndexMap.put(INDEX_DISTURB, function.getBoolean("setNotDisturb"));
			mIndexMap.put(INDEX_RELATIVE, function.getBoolean("setFamilyNum"));
			mIndexMap.put(INDEX_WHITE, function.getBoolean("setWhiteNum"));
			mIndexMap.put(INDEX_SOS, function.getBoolean("setSosNum"));

			mIndexMap.put(INDEX_TARGET, false);
			mIndexMap.put(INDEX_POWER, false);
			mIndexMap.put(INDEX_ALARM, false);
			mIndexMap.put(INDEX_GPS, false);//转移到定位中
			mIndexMap.put(INDEX_SYNC, false);

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

			if (!function.getBoolean("alarm")
					&& !function.getBoolean("electricity")) {
				mIndexMap.put(INDEX_ALARM, false);
			} else {
				mIndexMap.put(INDEX_ALARM, true);
			}


			if (Integer.valueOf(android.os.Build.VERSION.SDK) < 18) {
				//                                    Toast.makeText(FamilyDetailActivity.this,
				//                                            "由于您的系统版本低于4.3，所以不支持健康和设备",
				//                                            Toast.LENGTH_LONG).show();
				//                                    mIndexMap.put(INDEX_HEALTHY, false);
				mIndexMap.put(INDEX_TARGET, false);
			}
			mIndexMap.put(INDEX_TARGET, false);//将目标页面转移到健康数据页面

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
					mIndexMap.put(INDEX_SYNC, true);
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
					mIndexMap.put(INDEX_SYNC, true);
				} else {
					Preferences.getInstance(getApplicationContext())
							.setDeviceType("");
				}
				Preferences.getInstance(getApplicationContext())
						.setRoleType(Constants.TYPE_IS_OLD);
			} else if (!isOld && !isStudent) {//parent
				mIndexMap.put(INDEX_DEVICEMANAGE, true);
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

			if (!data.isNull("studentphone")) {
				JSONObject studentphone = data.getJSONObject("studentphone");
				if (!studentphone.isNull("id")) {
					deviceId = studentphone.getInt("id");
					mStrDeviceId = studentphone.getString("id");
				}
			}
			if (!data.isNull("oldiephone")) {
				JSONObject oldiephone = data.getJSONObject("oldiephone");
				if (!oldiephone.isNull("id")) {
					deviceId = oldiephone.getInt("id");
					mStrDeviceId = oldiephone.getString("id");
				}
			}

			if (IsExternMenuShow()) {
				//外部菜单挪到了设备管理
				ePersonImageOptionMenu.setVisibility(View.INVISIBLE);
			} else {
				ePersonImageOptionMenu.setVisibility(View.INVISIBLE);
				//ePersonImageOptionMenu.setVisibility(View.VISIBLE);
			}

			mHeadPicPath = data.getString("headpicpath")
					+ data.getString("headpicname");


			if (deviceId == -1) {
				//hide all device icon
				//  hideDeviceIcon();
				mGetUserInfoHandler.sendEmptyMessage(GETUSERINFO_SUCCESS);
				initGridLayout();
				if (data.getBoolean("student")) {
					family_detail_grid.setNumColumns(4);
					adapter.notifyDataSetChanged();
				}
				return;
			}
			initGridLayout();
			if (data.getBoolean("student")) {
				family_detail_grid.setNumColumns(4);
				adapter.notifyDataSetChanged();
			}

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
			String result = UserInfoUtil.getUserInfoByMemberId(FamilyListDetailActivity_sm.this,
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
				JSONObject jobj = new JSONObject(result);
				resultcode = jobj.getInt("retcode");
				isCode = true;
			} catch (Exception e) {
				isCode = false;
			}

			if (!isCode) {
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
		if (mHomeListener != null) {
			mHomeListener.stopWatch();
		}
	}

	// true ,has extern menu
	// false, no extern menu
	private boolean IsExternMenuShow() {
		for (int i = INDEX_MONITOR; i <= INDEX_SYNC; i++) {
			if (mIndexMap.get(i)) {
				return true;
			}
		}
		return false;
	}

	class FamilyPopMenuLayout_sm1 extends LinearLayout {

		private LinearLayout family_choose_ll_listening;
		private LinearLayout family_choose_ll_calling;
		private LinearLayout family_choose_ll_alarming;
		private LinearLayout family_choose_ll_sleeping;
		private LinearLayout family_choose_ll_relative_number;
		private LinearLayout family_choose_ll_white_list;
		private LinearLayout family_choose_ll_sos;
		private LinearLayout family_choose_ll_sport_target;
		private LinearLayout family_choose_ll_power_left;
		private LinearLayout family_choose_ll_warning;
		private LinearLayout family_choose_ll_gps_setting;
		private LinearLayout family_choose_ll_sync;

		private PopupWindow mPopupWindowDialog;

		private Context ctx;

		private View view;

		private View parentView;

		private int height = 0;

		private Map<Integer, Boolean> mIndexMap;

		public FamilyPopMenuLayout_sm1(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context);
		}

		public FamilyPopMenuLayout_sm1(Context context, int height) {
			super(context);
			this.height = height;
			init(context);
		}

		public FamilyPopMenuLayout_sm1(Context context, View _parentView, int height) {
			super(context);
			this.height = height;
			this.parentView = _parentView;
			init(context);
		}

		public FamilyPopMenuLayout_sm1(Context context, Map<Integer, Boolean> indexMap, int height) {
			super(context);
			this.height = height;
			this.mIndexMap = indexMap;
			init(context);
		}

		public FamilyPopMenuLayout_sm1(Context context, View _parent, Map<Integer, Boolean> indexMap, int height) {
			super(context);
			this.parentView = _parent;
			this.height = height;
			this.mIndexMap = indexMap;
			init(context);
		}

		private void init(Context ctx) {
			this.ctx = ctx;
		}
		public void initFamilyFrame(View parent) {
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
			if (mPopupWindowDialog != null) {
				mPopupWindowDialog.showAsDropDown(parentView, -70, -70);
			}
		}
	}
}
