package com.smarthome.client2.view;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.UserBean;
import com.smarthome.client2.common.SysParamers;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.BitmapUtil;

public class SetNameView extends LinearLayout {

	public final int REQUEST_CODE_FOR_PHOTO_ALBUM = 1;//调用相册
	public final int REQUEST_CODE_FOR_TAKE_CAMERA = 2;//拍照
	public final int REQUEST_CODE_START_PHOTO_ZOOM = 3;//裁剪
	
	
	private byte[] datas;
	private Bitmap bitmap,rectf_bitmap;
	private ProgressDialog dialog;
	private EditText username;
	private Button btn_next_set_name_view;
	private PopupWindow mPopupWindowDialog;
	private TextView mTextViewDialogTakePicture, mTextViewDialogAlbum,
			mTextViewDialogCancel;
	private ImageView userImg;
	// 用来标识请求照相功能的activity
	public static final int CAMERA_WITH_DATA = 3023;
	
	private String takePicturePath = "";
	
	private UserBean userBean = new UserBean();
	

	// //////////////////////////////////////////////////////////////////////////
	private Context ctx;
	private View content_view;

	public SetNameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SetNameView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context ctx) {
		this.ctx = ctx;
		initView();
	}

	private void initView() {
		content_view = LayoutInflater.from(ctx).inflate(R.layout.set_name_view,
				this);

		btn_next_set_name_view = (Button) content_view
				.findViewById(R.id.btn_next_set_name_view);
		username = (EditText) content_view.findViewById(R.id.username);
		userImg = (ImageView) content_view.findViewById(R.id.userImg);
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.post_daily_picture_choose_dialog,
				null);
		mTextViewDialogTakePicture = (TextView) view
				.findViewById(R.id.textview_dialog_take_picture);
		mTextViewDialogAlbum = (TextView) view
				.findViewById(R.id.textview_dialog_album);
		mTextViewDialogCancel = (TextView) view
				.findViewById(R.id.textview_dialog_cancel);
		mPopupWindowDialog = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		mPopupWindowDialog.setFocusable(true);
		mPopupWindowDialog.update();
		mPopupWindowDialog.setBackgroundDrawable(new BitmapDrawable(
				getResources(), (Bitmap) null));
		mPopupWindowDialog.setOutsideTouchable(true);
		
		btn_next_set_name_view.setOnClickListener(set_name_view_click);
		mTextViewDialogTakePicture.setOnClickListener(set_name_view_click);
		mTextViewDialogAlbum.setOnClickListener(set_name_view_click);
		mTextViewDialogCancel.setOnClickListener(set_name_view_click);
		userImg.setOnClickListener(set_name_view_click);

		username.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					btn_next_set_name_view.setEnabled(false);
				} else {
					btn_next_set_name_view.setEnabled(true);
				}
			}
		});
	}
	
	public void showUserName(){
	}
	
	public void showUserImg(){
	}

	public String getInputNickName(){
		return username.getText().toString();
	}
	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode == Activity.RESULT_OK) {
				switch (requestCode) {
				// 如果是直接从相册获取
				case REQUEST_CODE_FOR_PHOTO_ALBUM:
					startPhotoZoom(data.getData());
					break;
				// 如果是调用相机拍照时
				case REQUEST_CODE_FOR_TAKE_CAMERA:
					File temp = new File(takePicturePath);
					startPhotoZoom(Uri.fromFile(temp));
					break;
				// 取得裁剪后的图片
				case REQUEST_CODE_START_PHOTO_ZOOM:
					if (data != null) {
						setPicToView(data);
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		if(doForPhotoZoomListener != null){
			doForPhotoZoomListener.doForTake(uri);
		}
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			bitmap = extras.getParcelable("data");
			if (bitmap != null) {
				rectf_bitmap = bitmap;
				bitmap = BitmapUtil.getRoundedCornerBitmap(bitmap);
				userImg.setImageBitmap(bitmap);
			}
		}
	}

	private OnClickListener set_name_view_click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_next_set_name_view:
				break;
			case R.id.userImg:
				InputMethodManager imm = (InputMethodManager) ctx
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				if (mPopupWindowDialog != null) {
					mPopupWindowDialog.showAtLocation(
							findViewById(R.id.userImg), Gravity.BOTTOM
									| Gravity.CENTER_HORIZONTAL, 0, 0);
				}
				break;
			case R.id.textview_dialog_take_picture:// 拍照
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(ctx, "未检测到内存卡", Toast.LENGTH_SHORT);
					return;
				}
				// 下面这句指定调用相机拍照后的照片存储的路径

				takePicturePath = SysParamers.IMAGE_HEAD_FILE_PATH + userBean.id +"head.jpg";
				File image = new File(takePicturePath);
				if (!image.exists())
					try {
						TLog.Log("" + image.createNewFile());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				Uri uri2 = Uri.fromFile(image);
				
				if(doForTakeCameraListener != null){
					doForTakeCameraListener.doForTake(uri2);
				}

				if (mPopupWindowDialog != null
						&& mPopupWindowDialog.isShowing()) {
					mPopupWindowDialog.dismiss();
				}
				break;
			case R.id.textview_dialog_album:// 相册
//				takePicturePath = getIntent().getStringExtra("data");
				
				if(doForPhotoAlbumListener != null){
					doForPhotoAlbumListener.doForTake();
				}

				if (mPopupWindowDialog != null
						&& mPopupWindowDialog.isShowing()) {
					mPopupWindowDialog.dismiss();
				}
				break;
			case R.id.textview_dialog_cancel: // 取消
				if (mPopupWindowDialog != null
						&& mPopupWindowDialog.isShowing()) {
					mPopupWindowDialog.dismiss();
				}
				break;
			}
		}
	};

	private IsFinishListener isFinishListener;

	public void setIsFinishListener(IsFinishListener isFinishListener) {
		this.isFinishListener = isFinishListener;
	}

	public interface IsFinishListener {
		boolean isActivityFinish();
	}

//	REQUEST_CODE_FOR_PHOTO_ALBUM//调用相册
//	REQUEST_CODE_FOR_TAKE_CAMERA//拍照
//	REQUEST_CODE_START_PHOTO_ZOOM//裁剪
	
	private DoForPhotoAlbumListener doForPhotoAlbumListener;
	private DoForTakeCameraListener doForTakeCameraListener;
	private DoForPhotoZoomListener doForPhotoZoomListener;
	
	public void setDoForPhotoAlbumListener(DoForPhotoAlbumListener doForPhotoAlbumListener){
		this.doForPhotoAlbumListener = doForPhotoAlbumListener;
	}
	
	public void setDoForTakeCameraListener(DoForTakeCameraListener doForTakeCameraListener){
		this.doForTakeCameraListener = doForTakeCameraListener;
	}
	
	public void setDoForPhotoZoomListener(DoForPhotoZoomListener doForPhotoZoomListener){
		this.doForPhotoZoomListener = doForPhotoZoomListener;
	}
	
	public interface DoForPhotoAlbumListener{
		void doForTake();
	}
	
	public interface DoForTakeCameraListener{
		void doForTake(Uri uri2);
	}
	
	public interface DoForPhotoZoomListener{
		void doForTake(Uri uri);
	}

}
