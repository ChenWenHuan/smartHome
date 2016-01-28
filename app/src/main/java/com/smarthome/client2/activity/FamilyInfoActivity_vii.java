package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.bean.FamilyClassBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.util.BitmapUtil;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.UserInfoUtil;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.widget.CircleImageView;
import com.smarthome.client2.widget.SlidingDrawerGridView;
import com.squareup.picasso.Picasso;

import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FamilyInfoActivity_vii extends BaseActivity implements OnClickListener {
	
	private SlidingDrawerGridView mGridFamilyMem = null;

	@Bind(R.id.edit_pic)
	ImageView mImageAllFamily;

	@Bind(R.id.tv_family_nickname)
	TextView tvFamilyNickName;

	private PopupWindow mPopupWindowDialog;
	
	private String takePicturePath;// 调用相机拍摄照片的名字

	private String filePath = "";// 裁剪后图片的路径

	private Bitmap bitmap;

	private FrameLayout mTitleBar = null;


	private CustomActionBar mActionBar = null;

	private ArrayList<BaseBean> mFamilyMemList = new ArrayList<BaseBean>();

	private FamilyClassBean familyData = null;

	private String familyID;

	private String familyName;
	
	private String familyKeyPersonID;

	private String familyImagePath;

	private String newMem[] = new String[]{"创建新成员", "搜索成员"};
	
	private String removeMem[] = new String[]{"确定移除", "取消移除"};

	private String newMemPic[] = new String[]{"拍照", "从相册选择"};

	private MemHolder holder;

	private int mStatus;

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
	SmartHomeApplication app = SmartHomeApplication.getInstance();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.e_edit_family_vii);
		ButterKnife.bind(this);
		initView();
		initData();
	}


	private void initData() {

		Intent intent = this.getIntent();

		familyID = intent.getStringExtra("familyId");

		familyData = SmartHomeApplication.getInstance().getFamilyDataByFamilyID(familyID);
		if (familyData != null) {
			if (!familyData.getImgUrl().isEmpty()) {
				familyImagePath = familyData.getImgUrl();
			}

			if(familyImagePath.length() > 5) {
				Picasso.with(this).load(familyImagePath).into(mImageAllFamily);
			}
			tvFamilyNickName.setText(familyData.getName());
		}
		else {
			Picasso.with(this).load(R.drawable.ico_home_page_bg2).into(mImageAllFamily);
			tvFamilyNickName.setText("我的家庭");
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

		if (checkIsKeyPerson(familyKeyPersonID)) {
			mImageAllFamily.setOnClickListener(this);
		}

		if (checkIsKeyPerson(familyKeyPersonID)) {
			tvFamilyNickName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(FamilyInfoActivity_vii.this,
							UiEditFamilyName.class);
					intent.setAction(UiEditFamilyName.EDIT_FAMILY_NAME);
					intent.putExtra("familyid", familyID);
					startActivityForResult(intent, 0);
				}
			});
		}

	}
	
	private boolean checkIsKeyPerson(String keyPersonID) {
		return true;
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
				"我的家庭",
				null,
				null);
		mTitleBar.addView(mActionBar);
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
						BitmapUtil.startFamilyPhotoZoom(FamilyInfoActivity_vii.this,
								data.getData(),
								3);
						break;
					case CALLCAMERA:// 如果是调用相机拍照时
						File temp = new File(takePicturePath);
						BitmapUtil.startFamilyPhotoZoom(FamilyInfoActivity_vii.this,
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
						familyData.setName(familyName);
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

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.edit_pic:
				closeInputMethod();
				new AlertDialog.Builder(FamilyInfoActivity_vii.this).setTitle("家庭封面")
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



	private class MemHolder {
		CircleImageView headImg;
		TextView tvName;
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
			int resultcode;
			if (!TextUtils.isEmpty(result)) {
				try {
					resultcode = Integer.parseInt(result);
					if (resultcode == 200) {
						familyData.setImgUrl(filePath);
						updateState(UP_LOAD_IMG_SUCCESS, resultcode);
					}
					else {
						updateState(UP_LOAD_IMG_FAIL, resultcode);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}

