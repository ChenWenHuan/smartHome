package com.smarthome.client2.friendgroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;

public class PhotoLayout extends LinearLayout {

	private Activity act;
	private TextView mTextViewDialogTakePicture, mTextViewDialogAlbum,
			mTextViewDialogCancel;
	private PopupWindow mPopupWindowDialog;
	private String takePicturePath;

	private Context ctx;
	private View view;
	private Bitmap smallBitmap, bigBitmap;
	public static boolean addPhotoIsOpen = false;

	private static List<Map<String, Bitmap>> imgList = new ArrayList<Map<String, Bitmap>>();

	public PhotoLayout(Context context, AttributeSet attrs, FriendGroup fg) {
		super(context, attrs);
		init(context, fg);
	}

	public PhotoLayout(Context context, Activity fg) {
		super(context);
		init(context, fg);
	}

	private void init(Context ctx, Activity act) {
		this.ctx = ctx;
		if (act instanceof FriendGroup) {
			this.act = act;
		} else if (act instanceof FriendAddPhoto) {
			this.act = act;
		} else {
			this.act = act;
		}
		initView();
	}

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater
				.inflate(R.layout.post_daily_picture_choose_dialog, null);
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

		mTextViewDialogTakePicture.setOnClickListener(item_add_click);
		mTextViewDialogAlbum.setOnClickListener(item_add_click);
		mTextViewDialogCancel.setOnClickListener(item_add_click);
		// addPhotoIsOpen = false;
	}

	private OnClickListener item_add_click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.textview_dialog_take_picture:// 拍照
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(ctx, "未检测到内存卡", Toast.LENGTH_SHORT).show();
					return;
				}
				// 下面这句指定调用相机拍照后的照片存储的路径

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
				String picName = sdf.format(new Date());
				takePicturePath = Constants.IMAGE_HEAD_FILE_PATH + picName
						+ ".jpg";
				File folder = new File(Constants.IMAGE_HEAD_FILE_PATH);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				File image = new File(takePicturePath);
				if (!image.exists()) {
					try {
						image.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Uri uri2 = Uri.fromFile(image);

				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = takePicturePath;
				handler.sendMessage(msg);
				// 开启拍照功能
				Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri2);
				act.startActivityForResult(intent2,
						Constants.REQUEST_CODE_FOR_TAKE_CAMERA);

				if (mPopupWindowDialog != null
						&& mPopupWindowDialog.isShowing()) {
					mPopupWindowDialog.dismiss();
				}
				break;
			case R.id.textview_dialog_album:// 相册

				// 进入相册
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				act.startActivityForResult(intent,
						Constants.REQUEST_CODE_FOR_PHOTO_ALBUM);

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

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		// 获取大图
		getBigImage(uri);
		// 剪裁小图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		act.startActivityForResult(intent,
				Constants.REQUEST_CODE_START_PHOTO_ZOOM);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	public void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			smallBitmap = extras.getParcelable("data");
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

			// if(!addPhotoIsOpen){
			// Intent intent = new Intent(act,FriendAddPhoto.class);
			// addPhotoIsOpen = true;
			// act.startActivity(intent);
			// act.finish();
			// }else{
			if (smallBitmap != null) {
				Map<String, Bitmap> map = new HashMap<String, Bitmap>();
				map.put("small", smallBitmap);
				map.put("big", bigBitmap);
				imgList.add(map);
				bitmapListener
						.onBitmapListener(smallBitmap, bigBitmap);
			}
			// }
		}
	}

	public void initPhotoFrame(int layout, int widget) {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		if (mPopupWindowDialog != null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(layout, null);
			mPopupWindowDialog.showAtLocation(v.findViewById(widget),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		}
	}

	private Handler handler;

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	private BitmapListener bitmapListener;

	public void setBitmapListener(BitmapListener bitmapListener) {
		this.bitmapListener = bitmapListener;
		if (smallBitmap != null) {
			Map<String, Bitmap> map = new HashMap<String, Bitmap>();
			map.put("small", smallBitmap);
			map.put("big", bigBitmap);
			imgList.add(map);
			bitmapListener.onBitmapListener(smallBitmap, bigBitmap);
		}
	}

//	public void getSmallImage(Uri uri, Activity activity) {
//		// 剪裁小图
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setDataAndType(uri, "image/*");
////		intent.setType("image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 1);// 裁剪框比例
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", 150);// 输出图片大小
//		intent.putExtra("outputY", 150);
//		intent.putExtra("return-data", true);
//		
//		intent.getParcelableExtra("data");
//		activity.startActivityForResult(intent,
//				Constants.REQUEST_CODE_START_PHOTO_ZOOM);
//	}

	public Bitmap getBigImage(Uri uri) {
		if (uri != null) {
			try {
				bigBitmap = BitmapFactory.decodeStream(act.getContentResolver()
						.openInputStream(uri));

				 bigBitmap = comp(bigBitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return bigBitmap;
	}

	private Bitmap revitionImageSize(String path, int size) throws IOException {
		// 取得图片
		InputStream temp = act.getAssets().open(path);
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 这个参数代表，不为bitmap分配内存空间，只记录一些该图片的信息（例如图片大小），说白了就是为了内存优化
		options.inJustDecodeBounds = true;
		// 通过创建图片的方式，取得options的内容（这里就是利用了java的地址传递来赋值）
		BitmapFactory.decodeStream(temp, null, options);
		// 关闭流
		temp.close();

		// 生成压缩的图片
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			// 这一步是根据要设置的大小，使宽和高都能满足
			if ((options.outWidth >> i <= size)
					&& (options.outHeight >> i <= size)) {
				// 重新取得流，注意：这里一定要再次加载，不能二次使用之前的流！
				temp = act.getAssets().open(path);
				// 这个参数表示 新生成的图片为原始图片的几分之一。
				options.inSampleSize = (int) Math.pow(2.0D, i);
				// 这里之前设置为了true，所以要改为false，否则就创建不出图片
				options.inJustDecodeBounds = false;

				bitmap = BitmapFactory.decodeStream(temp, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	private Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
			Log.d("", "daitm----compress--running-----" + options);
		}
		Log.d("",
				"daitm----compress--finish--baos.toByteArray()---"
						+ baos.toByteArray().length);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public void showPhotoPopWindow() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		if (mPopupWindowDialog != null) {
			mPopupWindowDialog.showAtLocation(act.getWindow().getDecorView(),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		}
	}
	
	public interface BitmapListener {
		void onBitmapListener(Bitmap smallMap, Bitmap bigMap);
	}
	
	// private BitmapListListener bitmapListListener;
	// public void setBitmapListListener(BitmapListListener bitmapListListener){
	// this.bitmapListListener = bitmapListListener;
	// }
	// private TriggleImgList triggleImgList = new TriggleImgList() {
	// @Override
	// public void triggleImgList() {
	// bitmapListListener.BitmapListListener(imgList);
	// }
	// };
	// public interface TriggleImgList{
	// public void triggleImgList();
	// }
}
