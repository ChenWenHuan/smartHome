package com.smarthome.client2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.util.ScreenUtils;

public class LocationHead extends ImageView{

	private Bitmap mBitmap;
	private int mXCenter;
	private int mYCenter;
	private Paint headPaint;
	public LocationHead(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mXCenter = ScreenUtils.dip2px(getContext(), 100) / 2;
		mYCenter = ScreenUtils.dip2px(getContext(), 100) / 2;
		
		headPaint = new Paint();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
		if(mBitmap!=null){
			canvas.drawBitmap(mBitmap, bitmap.getWidth()/6+5, bitmap.getHeight()/6, headPaint);
		}
	}
	
	public void setLocBitmap(Bitmap bitmap){
		if(bitmap==null){
//			Toast.makeText(getContext(), getContext().getString(R.string.location_head_error), Toast.LENGTH_SHORT).show();
			return;
		}
		this.mBitmap = bitmap;
		
		// 计算缩放比例
//		float scaleWidth = ((float) ScreenUtils.dip2px(getContext(), 100)) / mBitmap.getWidth();
//		float scaleHeight = ((float) ScreenUtils.dip2px(getContext(), 100)) / mBitmap.getHeight();
		float scale =((float) ScreenUtils.dip2px(getContext(), 30))/
				(mBitmap.getWidth()>mBitmap.getHeight()?mBitmap.getHeight():mBitmap.getWidth());
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		// 得到新的图片
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,mBitmap.getWidth(),mBitmap.getHeight(), matrix, true);
		postInvalidate();
	}
}
