package com.smarthome.client2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.smarthome.client2.R;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.util.ScreenUtils;

public class BatteryHead extends ImageView{

	private Bitmap mBitmap;
	private int mProgress = 0;
	private int mXCenter;
	private int mYCenter;
	private float mStrokeWidth;
	private float mRadius;
	private Paint mCirclePaint,mBatteryPaint,headPaint;
	public BatteryHead(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius,
				80);
		mStrokeWidth = typeArray.getDimension(
				R.styleable.TasksCompletedView_strokeWidth, 10);
		TLog.Log("zxl---TimeLooper---initattrs--->"+getWidth()+"--->"+getHeight()+"--->"+mRadius+"--->"+mStrokeWidth);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mXCenter = ScreenUtils.dip2px(getContext(), 100) / 2;
		mYCenter = ScreenUtils.dip2px(getContext(), 100) / 2;
		
		headPaint = new Paint();
		if(mBitmap!=null){
			canvas.drawBitmap(mBitmap, 0, 0, headPaint);
			
			int my_circle_radius = (int) (mXCenter-mStrokeWidth/2);
			
			mCirclePaint = new Paint();
			mCirclePaint.setAntiAlias(true);
			mCirclePaint.setColor(Color.parseColor("#4c000000"));
			mCirclePaint.setStyle(Paint.Style.STROKE);
			mCirclePaint.setStrokeWidth(mStrokeWidth);
			
			mBatteryPaint = new Paint();
			mBatteryPaint.setAntiAlias(true);
			mBatteryPaint.setColor(Color.parseColor("#38b87d"));
			mBatteryPaint.setStyle(Paint.Style.STROKE);
			mBatteryPaint.setStrokeWidth(mStrokeWidth);
			
			canvas.drawCircle(mXCenter, mYCenter, my_circle_radius, mCirclePaint);
			
			RectF oval = new RectF();
			oval.left = (mXCenter - my_circle_radius);
			oval.top = (mYCenter - my_circle_radius);
			oval.right = my_circle_radius * 2 + (mXCenter - my_circle_radius);
			oval.bottom = my_circle_radius * 2 + (mYCenter - my_circle_radius);
			canvas.drawArc(oval, -90, ((float) mProgress / 100) * 360,
					false, mBatteryPaint);
		}
	}
	
	public void setBitmap(Bitmap bitmap,int progress){
		this.mBitmap = bitmap;
		this.mProgress = progress;
		
		// 计算缩放比例
//		float scaleWidth = ((float) ScreenUtils.dip2px(getContext(), 100)) / mBitmap.getWidth();
//		float scaleHeight = ((float) ScreenUtils.dip2px(getContext(), 100)) / mBitmap.getHeight();
		float scale =((float) ScreenUtils.dip2px(getContext(), 100))/
				(mBitmap.getWidth()>mBitmap.getHeight()?mBitmap.getHeight():mBitmap.getWidth());
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		// 得到新的图片
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,mBitmap.getWidth(),mBitmap.getHeight(), matrix, true);
		postInvalidate();
	}
}
