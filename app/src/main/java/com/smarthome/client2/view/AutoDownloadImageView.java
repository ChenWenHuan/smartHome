package com.smarthome.client2.view;

import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.BitmapUtil;
import com.smarthome.client2.util.HttpUtil;

/**
 * @description 一个具备内部异步下载图片的 image控件 使用一个传入的线程池进行同步控制
 */
public class AutoDownloadImageView extends ImageView {

	private Context ctx;
	private ExecutorService pool = null;
	private String url = "";
	private String userId_now = "-1";
	private boolean isLoad = false;
	private Bitmap bmp = null;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AutoDownloadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ctx = context;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AutoDownloadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
	}

	/**
	 * @param context
	 */
	public AutoDownloadImageView(Context context) {
		super(context);
		ctx = context;
	}
	
	
	public void setImageUrl(String url) {
		if (!isLoad) {
			if (url != null) {
				if (!this.url.equals("")) {
					if (!this.url.equals(url)) {
						this.url = url;
						recylebmp();
					}
				} else {
					this.url = url;
					recylebmp();
				}
			}
			new CusThread().start();
		}
	}
	
	public void loadPhoto(final String userId) {
		if (!isLoad) {
			this.userId_now = userId;
			recylebmp();
			new Thread(new Runnable() {
				@Override
				public void run() {
					isLoad = true;
					
					String userId_net = userId;
					if (bmp == null||bmp.isRecycled()) {
						bmp = BitmapFactory.decodeFile(Constants.IMAGE_HEAD_FILE_PATH+Preferences.getInstance(ctx).getUserName()+".jpg");
						if(bmp != null && userId_now.equals(userId_net)){
							Message message = hander.obtainMessage();
							message.what = GET_IMG_SUCCESS;
							message.obj = bmp;
							message.sendToTarget();
						}
						byte[] data = null;
						if(!TextUtils.isEmpty(userId_net)){
//							data = HttpUtil.loadPhoto(Constants.GET_AVATAR,userId_net, ctx);
						}else{
//							data = HttpUtil.loadPhoto(Constants.GET_AVATAR);
						}
						if(null != data && data.length > 0){
							bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
						}else{
							bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_head);
						}
						if(bmp != null && userId_now.equals(userId_net)){
							Message message = hander.obtainMessage();
							message.what = GET_IMG_SUCCESS;
							message.obj = bmp;
							message.sendToTarget();
						}
					}else{
						TLog.Log("使用已有的bmp数据");
						Message message = hander.obtainMessage();
						message.what = GET_IMG_SUCCESS;
						message.obj = bmp;
						message.sendToTarget();
					}
					
					
					isLoad = false;
				}
			}).start();
		}
	}


	class CusThread extends Thread {
		@Override
		public void run() {
//			isLoad = true;
//			if (bmp == null||bmp.isRecycled()) {
//				bmp = BitmapFactory.decodeFile(Constants.IMAGE_HEAD_FILE_PATH+Preferences.getInstance(ctx).getUserName()+".jpg");
//				if(bmp != null){
//					Message message = hander.obtainMessage();
//					message.what = GET_IMG_SUCCESS;
//					message.obj = bmp;
//					message.sendToTarget();
//				}
//				byte[] data = HttpUtil.loadPhoto(Constants.GET_AVATAR);
//				if(null != data && data.length > 0){
//					bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//				}
//				if(bmp != null){
//					Message message = hander.obtainMessage();
//					message.what = GET_IMG_SUCCESS;
//					message.obj = bmp;
//					message.sendToTarget();
//				}
//			}else{
//				TLog.Log("使用已有的bmp数据");
//				Message message = hander.obtainMessage();
//				message.what = GET_IMG_SUCCESS;
//				message.obj = bmp;
//				message.sendToTarget();
//			}
//			
//			
//			isLoad = false;
			super.run();
		}
	}
	
	private static final int GET_IMG_SUCCESS = 1;
	
	private Handler hander = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case GET_IMG_SUCCESS:
				bmp = (Bitmap) msg.obj;
				if (bmp != null && !bmp.isRecycled()){
					bmp = BitmapUtil.getRoundedCornerBitmap(bmp);
					if (bmp != null) {
						AutoDownloadImageView.this.setScaleType(ScaleType.FIT_XY);
						AutoDownloadImageView.this.setImageBitmap(bmp);
					}
				}
				break;
			}
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		try {
			super.onDraw(canvas);
		} catch (RuntimeException ex) {
		}
	}

	private void recylebmp() {
		if (bmp != null) {
			if (!bmp.isRecycled()) {
				bmp.recycle();
				TLog.Log("zxl---AutoDownloadImageView 回收");
			}
			bmp = null;
		}
	}

	/** 销毁方法 */
	public void Destory() {
		recylebmp();
		ctx = null;
		hander = null;
		isLoad = false;
	}

}
