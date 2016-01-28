package com.smarthome.client2.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;










import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;










import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.bean.FamilyClassBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.bean.UserInfo;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.model.MessageReply;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.util.BitmapUtil;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.UserInfoUtil;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.widget.CircleImageView;
import com.smarthome.client2.widget.SlidingDrawerGridView;

public class FamilyInfoActivity extends BaseActivity implements OnClickListener {
	
	private SlidingDrawerGridView mGridFamilyMem = null;

	private ImageView mImageAllFamily = null;
	private PopupWindow mPopupWindowDialog;
	
	private String takePicturePath;// 调用相机拍摄照片的名字

	private String filePath = "";// 裁剪后图片的路径

	private Bitmap bitmap;

	private FrameLayout mTitleBar = null;


	private CustomActionBar mActionBar = null;

	private ArrayList<BaseBean> mFamilyMemList = new ArrayList<BaseBean>();

	private FamilyClassBean familyData = null;

	private FamilyMemGridAdapter mMemGridAdapter = null;

	private String familyID;

	private String familyName;
	
	private String familyKeyPersonID;
	
	private String familyImagePath;

	private String newMem[] = new String[]{"创建新成员", "搜索成员"};
	
	private String removeMem[] = new String[]{"确定移除", "取消移除"};

	private String newMemPic[] = new String[]{"拍照", "从相册选择"};

	private MemHolder holder;

	private int mStatus;

	private TextView tvFamilyNickName = null;
	
	private ImageDownLoader mLoader;
	
	private String currentRemoveMemID = "";

	private String title= "";
	/**
	 * for load image
	 */
	private static final int LOAD_IMG_START = 1;

	private static final int LOAD_IMG_SUCCESS = 2;

	private static final int LOAD_IMG_FAIL = 3;

	/**
	 * for save image
	 */
	private static final int UP_LOAD_IMG_START = 4;

	private static final int UP_LOAD_IMG_SUCCESS = 5;

	private static final int UP_LOAD_IMG_FAIL = 6;

	/**
	 * for dialog id
	 */
	private static final int DIALOG_USERINFO_PROGRESS = 0;

	private static final String DIALOG_MSG = "dialog_msg";
	
	private String 	linkDeviceID = "";
	private String  linkDeviceAccout = "";
	private String  watchTelNun = "";
	private Preferences tmpPreferences;
	private String userTelNum = "";
	private LinkTopSDKUtil instance = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        tmpPreferences = Preferences.getInstance(this.getApplicationContext());
        userTelNum = tmpPreferences.getUserTelNum();
		setContentView(R.layout.e_edit_family);
		initData();
		initView();
	}

	private void initData() {
		Intent intent = this.getIntent();

		familyID = intent.getStringExtra("familyid");
		familyName = intent.getStringExtra("famillyname");
		familyKeyPersonID = intent.getStringExtra("familyKeyPersonID");
		familyImagePath = intent.getStringExtra("familyImagePath");
		familyData = SmartHomeApplication.getInstance().getFamilyDataByFamilyID(familyID);
		mLoader = ImageDownLoader.getInstance();
		if (!familyImagePath.equals("")) {
			mLoader.downloadImage(familyImagePath, FsConstants.HOME_IMAGE, mHandler);
		}
		
		mLoader = ImageDownLoader.getInstance();
		if (familyData != null) {
			mFamilyMemList.addAll(familyData.getList());
			if (!hasAddMemFlag()) {
				showAddPerson(mFamilyMemList);
			}
		}
	}
	
	private boolean hasAddMemFlag() {
		
		boolean ret = false;
		int memSize = mFamilyMemList.size();
		if(memSize == 0){
			return false;
		}
		if (((MemBean) mFamilyMemList.get(memSize - 1)).memType.equals("5")) {
			ret = true;
		}
		return ret;
	}

	private void initView() {

		addTopBarToHead();
		//initPicDialog();

		mImageAllFamily = (ImageView) this.findViewById(R.id.edit_pic);
		if (checkIsKeyPerson(familyKeyPersonID)) {
			mImageAllFamily.setOnClickListener(this);
		}
		
		tvFamilyNickName = (TextView) this.findViewById(R.id.tv_family_nickname);
		tvFamilyNickName.setText(familyName);
		if (checkIsKeyPerson(familyKeyPersonID)) {
			tvFamilyNickName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(FamilyInfoActivity.this,
							UiEditFamilyName.class);
					intent.setAction(UiEditFamilyName.EDIT_FAMILY_NAME);
					intent.putExtra("familyid", familyID);
					startActivityForResult(intent, 0);
				}
			});
		}
		mGridFamilyMem = (SlidingDrawerGridView) this.findViewById(R.id.grid_family_mem);
		mGridFamilyMem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			                        long arg3) {
				// TODO Auto-generated method stub
				final String memID = ((MemBean) mFamilyMemList.get(position)).memID;
				if (!((MemBean) mFamilyMemList.get(position)).memType.equals("5")) {
					// TODO Auto-generated method stub
					if (checkIsKeyPerson(familyKeyPersonID)) {
						//获取用户信息，如有手表在删除账号之前，先将手表进行解绑操作
						getUserInfoByID(memID);
						new AlertDialog.Builder(FamilyInfoActivity.this).setTitle("移除家庭成员")
								.setItems(removeMem, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
									                    int which) {
										if (which == 0) {
											currentRemoveMemID = "";
											removeFamilyMem(familyID, memID);
											currentRemoveMemID = memID;
										} else if (which == 1) {

										}
									}
								}).show();
					} else {
						Toast.makeText(FamilyInfoActivity.this, "您不是户主，无法移除家庭成员！", Toast.LENGTH_SHORT).show();
					}
					
				} else {
					if (checkIsKeyPerson(familyKeyPersonID)) {
						//进入添加家庭成员的页面
						new AlertDialog.Builder(FamilyInfoActivity.this).setTitle("添加家庭成员")
								.setItems(newMem, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
									                    int which) {
										Intent intent = null;
										if (which == 0) {
											//创建新成员
											intent = new Intent(FamilyInfoActivity.this, CreateNewMember.class);
											intent.putExtra("groupid", familyID);
											intent.putExtra("memtype", "");
											FamilyInfoActivity.this.startActivityForResult(intent, ADDMEMBER);
										} else if (which == 1) {
											//搜索成员
											intent = new Intent(FamilyInfoActivity.this, SearchFamilyMember_sm.class);
											intent.putExtra("groupid", familyID);
											FamilyInfoActivity.this.startActivity(intent);
										}
									}
								}).show();
					} else {
						Toast.makeText(FamilyInfoActivity.this, "您不是户主，无法添加家庭成员！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		mMemGridAdapter = new FamilyMemGridAdapter(this, mFamilyMemList, mHandler);
		mGridFamilyMem.setAdapter(mMemGridAdapter);
		mMemGridAdapter.notifyDataSetChanged();
	}
	
	private boolean checkIsKeyPerson(String keyPersonID) {
		
		String userID = "";
		
		Preferences preferences = Preferences.getInstance(FamilyInfoActivity.this.getApplicationContext());
		userID = preferences.getUserID();

		return keyPersonID.equals(userID);
	}


	private void addTopBarToHead() {
		mTitleBar = (FrameLayout) findViewById(R.id.fl_header_home);
		if (mActionBar != null) {
			mTitleBar.removeView(mActionBar);
		}
		mActionBar = TopBarUtils.createCustomActionBar(SmartHomeApplication.getInstance(),
				R.drawable.btn_back_selector,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				},
				"家庭信息",
				null,
				null);
		mTitleBar.addView(mActionBar);
	}

	private void handleImageLoader(Message msg) {

		Bitmap bm;
		String imgUrl;

		bm = (Bitmap) msg.obj;
		imgUrl = msg.getData().getString("imgurl");

		CircleImageView headView = (CircleImageView) mGridFamilyMem.findViewWithTag(imgUrl);
		if (bm != null) {
			headView.setImageBitmap(bm);
		}
	}

	private void showAddPerson(ArrayList<BaseBean> memList) {

		MemBean bean = new MemBean();
		bean.memType = "5";
		memList.add(bean);

	}

	//以下代码来自 UserInfoReadOrEditActivity_sm.java
	private final int GETFROMPIC = 1;      // 从相册获取
	private final int CALLCAMERA = 2;      // 从相机获取
	private final int CRAPPIC = 3;         //裁减相片
	private final int ADDMEMBER = 4;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == Activity.RESULT_OK) {
				switch (requestCode) {
					case GETFROMPIC:// 如果是直接从相册获取
						BitmapUtil.startFamilyPhotoZoom(FamilyInfoActivity.this,
								data.getData(),
								3);
						break;
					case CALLCAMERA:// 如果是调用相机拍照时
						File temp = new File(takePicturePath);
						BitmapUtil.startFamilyPhotoZoom(FamilyInfoActivity.this,
								Uri.fromFile(temp),
								3);
						break;
					case CRAPPIC:// 取得裁剪后的图片
						if (data != null && data.getExtras() != null) {
							Bundle extras = data.getExtras();
							Bitmap image = extras.getParcelable(UserInfoUtil.KEY_JASON_DATA);
							filePath = Constants.IMAGE_FILE_PATH
									+ UserInfoUtil.USER_FAMILY_PICNAME_DEFAULT;
							BitmapUtil.setPicToView(image,
									Constants.IMAGE_FILE_PATH,
									UserInfoUtil.USER_FAMILY_PICNAME_DEFAULT);
						}
						break;
					case 0:
						Bundle bundleData = data.getExtras();
						familyName = bundleData.getString("familyname");
						tvFamilyNickName.setText(familyName);
						break;
					case ADDMEMBER:
//						FamilyClassBean baseBean = new FamilyClassBean();
//						Bundle bData = data.getExtras();
//						title = bData.getString("title");
//						baseBean.setName(title);
//						mFamilyMemList.add(baseBean);
//						mMemGridAdapter.notifyDataSetChanged();
						FamilyInfoActivity.this.finish();
						break;

					default:
						break;
				}
				if (!TextUtils.isEmpty(filePath)) {
					Bitmap bm = BitmapFactory.decodeFile(filePath);
					// bm = toRoundBitmap(bm);
					if (bm != null) {
						mImageAllFamily.setImageBitmap(bm);
						new SaveIFamilyPortaitTask().execute();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * initial Picture Dialog<BR>
	 * 定义图片摄取弹窗
	 */
//	private void initPicDialog() {
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.post_daily_picture_choose_dialog,
//				null);
//		TextView dialogTakePicture = (TextView) view.findViewById(R.id.textview_dialog_take_picture);
//		TextView dialogAlbum = (TextView) view.findViewById(R.id.textview_dialog_album);
//		TextView dialogCancel = (TextView) view.findViewById(R.id.textview_dialog_cancel);
//		mPopupWindowDialog = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
//				ViewGroup.LayoutParams.WRAP_CONTENT);
//		mPopupWindowDialog.setFocusable(true);
//		mPopupWindowDialog.update();
//		mPopupWindowDialog.setBackgroundDrawable(new BitmapDrawable(
//				getResources(), (Bitmap) null));
//		mPopupWindowDialog.setOutsideTouchable(true);
//		dialogTakePicture.setOnClickListener(this);
//		dialogAlbum.setOnClickListener(this);
//		dialogCancel.setOnClickListener(this);
//
//	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.edit_pic:
				closeInputMethod();
//				mPopupWindowDialog.showAtLocation(mImageAllFamily, Gravity.CENTER
//						| Gravity.CENTER_HORIZONTAL, 0, 0);
				new AlertDialog.Builder(FamilyInfoActivity.this).setTitle("家庭封面")
						.setItems(newMemPic, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
							                    int which) {
								Intent intent = new Intent();
								if (which == 0) {
									//拍照
									String sdStatus = Environment.getExternalStorageState();
									if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
										showToast(R.string.info_alert_hasnosdcard);
										return;
									}
									intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
									// 下面这句指定调用相机拍照后的照片存储的路径

									takePicturePath = Constants.IMAGE_FILE_PATH
											+ UserInfoUtil.USER_PICNAME_DEFAULT;
									log("----------" + takePicturePath);
									showToast(takePicturePath);
									File image = new File(takePicturePath);
									if (!image.exists())
										try {
											log("" + image.createNewFile());
										} catch (IOException e) {
											e.printStackTrace();
										}
									intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
									startActivityForResult(intent, CALLCAMERA);
								} else if (which == 1) {
									//相册选择
									intent.setAction(Intent.ACTION_PICK);
									intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
											"image/*");
									takePicturePath = getIntent().getStringExtra("data");
									startActivityForResult(intent, GETFROMPIC);
								}

							}
						}).show();
				break;
//			case R.id.textview_dialog_take_picture:// 拍照
//				String sdStatus = Environment.getExternalStorageState();
//				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
//					showToast(R.string.info_alert_hasnosdcard);
//					return;
//				}
//				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//				// 下面这句指定调用相机拍照后的照片存储的路径
//
//				takePicturePath = Constants.IMAGE_FILE_PATH
//						+ UserInfoUtil.USER_PICNAME_DEFAULT;
//				log("----------" + takePicturePath);
//				showToast(takePicturePath);
//				File image = new File(takePicturePath);
//				if (!image.exists())
//					try {
//						log("" + image.createNewFile());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
//				startActivityForResult(intent, CALLCAMERA);
//				if (mPopupWindowDialog != null
//						&& mPopupWindowDialog.isShowing()) {
//					mPopupWindowDialog.dismiss();
//				}
//				break;
//			case R.id.textview_dialog_album:// 相册
//				intent.setAction(Intent.ACTION_PICK);
//				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//						"image/*");
//				takePicturePath = getIntent().getStringExtra("data");
//				startActivityForResult(intent, GETFROMPIC);
//				if (mPopupWindowDialog != null
//						&& mPopupWindowDialog.isShowing()) {
//					mPopupWindowDialog.dismiss();
//				}
//				break;
//			case R.id.textview_dialog_cancel: // 取消
//				if (mPopupWindowDialog != null
//						&& mPopupWindowDialog.isShowing()) {
//					mPopupWindowDialog.dismiss();
//				}
//				break;

			default:
				break;
		}
	}


	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			if (imm != null) {
				if (this.getCurrentFocus() == null)
					return;
				if (this.getCurrentFocus().getWindowToken() == null)
					return;
				imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}


	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FsConstants.HANDLE_IMAGE:
					handleImageLoader(msg);
					break;
				case FsConstants.HOME_IMAGE:
					if (msg.obj != null) {
						mImageAllFamily.setImageBitmap((Bitmap) msg.obj);
					}
					break;
				default:
					break;
			}
		}
	};

	private class MemHolder {
		CircleImageView headImg;
		TextView tvName;
	}

	private class FamilyMemGridAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater mInflater;

		private ArrayList<BaseBean> mMemList;

		private ImageDownLoader mLoader;

		private Handler imgHandler;


		public FamilyMemGridAdapter(Context context, ArrayList<BaseBean> list, Handler imgHandle) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mMemList = list;
			mLoader = ImageDownLoader.getInstance();
			imgHandler = imgHandle;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mMemList == null) {
				return 0;
			}
			return mMemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mMemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {


			MemBean item = (MemBean) mMemList.get(position);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.home_fragment_teacher_item_members, null);
				holder = new MemHolder();
				holder.headImg = (CircleImageView) convertView.findViewById(R.id.img_mem_head);
				holder.headImg.setTag(item.memHeadImgUrl);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_mem_name);
				convertView.setTag(holder);
			} else {
				holder = (MemHolder) convertView.getTag();
			}
			if (!item.memType.equals("5")) {
				if (item.memHeadImgUrl.equals("")) {
					holder.headImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ico_head_blue));

				} else {
					mLoader.downloadImage(item.memHeadImgUrl, FsConstants.HANDLE_IMAGE, imgHandler);
				}
				holder.tvName.setText(item.memName);
			} else {
				holder.headImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ico_add_person));
				holder.tvName.setText("添加成员");
			}
			return convertView;
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
		if (isFinishing()) {
			return;
		}
		mStatus = state;
		Bundle bundle = new Bundle();
		String errormsg = HttpUtil.responseHandler(this, code);
		switch (state) {
			case LOAD_IMG_START:
				break;
			case LOAD_IMG_SUCCESS:
				try {
					bitmap = BitmapUtil.getBitmap(filePath);
				} catch (Exception e) {
					log(e.toString());
				}
				if (bitmap != null) {
					holder.headImg.setImageBitmap(bitmap);
				}
				break;
			case LOAD_IMG_FAIL:
				showToast(errormsg);
				break;
			case UP_LOAD_IMG_SUCCESS:
				break;
			case UP_LOAD_IMG_FAIL:
				switch (code) {
					case Constants.NO_NETWORK:
						showToast(R.string.no_network);
						break;
					case Constants.SERVER_OFFLINE:
						showToast(R.string.info_server_offline);
						break;
					case Constants.UNKNOW_RESULT:
						showToast(R.string.info_upload_family_portrait_fail);
						break;
					default:
						showToast(errormsg);
						break;
				}
				break;
			case UP_LOAD_IMG_START:
				bundle.putString(DIALOG_MSG, getString(R.string.info_saving));
				showDialog(DIALOG_USERINFO_PROGRESS, bundle);
				break;

			default:
				log("------updateState----else---" + state + "  " + errormsg);
				showToast(R.string.unknown_error);
				break;

		}
	}


	/**
	 * [Save  family portrait Task]
	 * 保存全家福
	 *
	 * @author
	 */

	class SaveIFamilyPortaitTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String result = String.valueOf(Constants.UNKNOW_RESULT);
			try {
				result = HttpUtil.upLoadFile(familyID, new File(
						filePath), HttpUtil.BASE_URL
						+ "/family/upFamilyPic.action", "familyId");
			} catch (ClientProtocolException e) {
				log(e.toString());
			} catch (IOException e) {
				log(e.toString());
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			int resultcode = Constants.UNKNOW_RESULT;
			if (!TextUtils.isEmpty(result)) {
				boolean isCode = false;
				try {
					resultcode = Integer.parseInt(result);
					isCode = true;
				} catch (NumberFormatException e) {
					isCode = false;
				}
				if (isCode) {
					updateState(UP_LOAD_IMG_FAIL, resultcode);
				}
			}
		}
	}
	
	private void removeFamilyMem(String familyID, String memID) {

//		HttpJson params = new HttpJson();
//		params.put("famId", familyID);
//		params.put("userId", memID);
//		MyHttpUtil.post("/family/delFamMember.action", params, removeMemHandler);
		
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("famId", familyID);
            obj.put("userId", memID);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.DEL_FAMILY_MEM_WITH_MAIN_GUARDER,
                removeMemHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
	}
	
	
	private void getUserInfoByID(String memID) {

//		HttpJson params = new HttpJson();
//		params.put("famId", familyID);
//		params.put("userId", memID);
//		MyHttpUtil.post("/family/delFamMember.action", params, removeMemHandler);
		
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("acctid", memID);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
        		Constants.GET_USER_INFO,
                gerUserInfoHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
	}
	
	private Handler gerUserInfoHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FsConstants.HTTP_START:
					break;
				case Constants.GET_DATA_SUCCESS:
					String[] linkDeviceInfo = null;
					linkDeviceInfo = UserInfoUtil.parserUserLinkTopDeviceInfo(msg.obj.toString());
					if (linkDeviceInfo != null){
						linkDeviceID = linkDeviceInfo[0];
						linkDeviceAccout = linkDeviceInfo[1];
						watchTelNun = linkDeviceInfo[2];
						if(linkDeviceAccout.equals(userTelNum)){
							instance = LinkTopSDKUtil.getInstance();
							instance.initSDK(FamilyInfoActivity.this, handlerWatchData);
							instance.setupAccount(userTelNum, "888888");				
							instance.loginToken();
						}
					}
					break;
				case Constants.GET_DATA_FAIL:
					break;
				case FsConstants.HTTP_FINISH:
					break;
				default:
					break;
			}
		}
	};
	
	private Handler handlerWatchData = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
            case LinkTopSDKUtil.LINK_SDK_LOGIN_TOKEN:
            	break;
				default:
					break;
			}
		}
	};

	private Handler removeMemHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FsConstants.HTTP_START:
					break;
				case Constants.GET_DATA_SUCCESS:
//				case FsConstants.HTTP_SUCCESS:
					if(instance != null){
						instance.unBindDevice(linkDeviceID);
					}
					removeFamilyMemRefresh(currentRemoveMemID);
					showToast("已成功移出此家庭！");
					break;
				case Constants.GET_DATA_FAIL:
//				case FsConstants.HTTP_FAILURE:
					showToast(msg.obj.toString());
					break;
				case FsConstants.HTTP_FINISH:
					break;
				default:
					break;
			}
		}
	};

	private void removeFamilyMemRefresh(String userid) {

		for (int i = 0; i < mFamilyMemList.size(); i++) {
			if (((MemBean) mFamilyMemList.get(i)).memID.equals(userid)) {
				mFamilyMemList.remove(i);
			}
		}
		mMemGridAdapter.notifyDataSetChanged();
	}
}

