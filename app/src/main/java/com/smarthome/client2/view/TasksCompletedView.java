package com.smarthome.client2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.smarthome.client2.R;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.util.FormatParameters;
import com.smarthome.client2.util.ScreenUtils;

public class TasksCompletedView extends View
{

    // 渐变色
    private static int[] colors = new int[] { Color.RED, Color.YELLOW,
            Color.YELLOW, Color.RED, Color.RED };

    private static float[] positions = new float[] { 0.0f, 0.5f, 0.65f, 0.8f,
            1.0f };

    // 画实心圆的画笔
    private Paint mCirclePaint;

    // 画圆环的画笔
    private Paint mRingPaint;

    // 画字体的画笔
    private Paint mTextPaint;

    // 圆形颜色
    private int mCircleColor;

    // 圆环颜色
    private int mRingColor;

    // 半径
    private float mRadius;

    // 圆环半径
    private float mRingRadius;

    // 圆环宽度
    private float mStrokeWidth;

    // 圆心x坐标
    private int mXCenter;

    // 圆心y坐标
    private int mYCenter;

    // 字的长度
    private float mTxtWidth;

    // 字的高度
    private float mTxtHeight;

    private FontMetrics fm;

    // 总进度
    private int mTotalProgress = 100;

    // 当前进度
    private int mProgress;

    private int mPersent;

    private float mCal;

    private Paint mCalPaint;

    private Paint mCalUnitPaint;

    private BlurMaskFilter mBlur = null;

    public TasksCompletedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // 获取自定义的属性
        initAttrs(context, attrs);
        initVariable();
    }

    private void initAttrs(Context context, AttributeSet attrs)
    {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TasksCompletedView,
                0,
                0);
        mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius,
                80);
        mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth,
                10);
        mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor,
                0xFFFFFFFF);
        mRingRadius = mRadius + mStrokeWidth / 2;
        TLog.Log("zxl---taskcomplete---initattrs--->" + getWidth() + "--->"
                + getHeight() + "--->" + mRadius + "--->" + mStrokeWidth);
    }

    private void initVariable()
    {

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.parseColor("#4c000000"));
        // mCirclePaint.setColor(getResources().getColor(R.color.default_gray));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mStrokeWidth);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        // mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mStrokeWidth);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setARGB(255, 88, 89, 91);
        mTextPaint.setTextSize(mRadius / 5);

        mCalPaint = new Paint();
        mCalPaint.setAntiAlias(true);
        mCalPaint.setStyle(Paint.Style.FILL);
        mCalPaint.setARGB(255, 255, 67, 67);
        mCalPaint.setTextSize(mRadius / 2);

        mCalUnitPaint = new Paint();
        mCalUnitPaint.setAntiAlias(true);
        mCalUnitPaint.setStyle(Paint.Style.FILL);
        mCalUnitPaint.setColor(Color.parseColor("#b0aeab"));
        //        mCalUnitPaint.setARGB(255, 88, 89, 91);
        mCalUnitPaint.setTextSize(mRadius / 5);

        fm = mTextPaint.getFontMetrics();
        mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() * 2 / 5;
        //      int my_circle_radius = (int) ((getHeight() - mStrokeWidth*2)/2);
        int my_circle_radius = (int) mRingRadius;
        TLog.Log("zxl---onDraw--->" + getWidth() + "--->" + getHeight()
                + "--->" + mRadius + "--->" + mStrokeWidth + "--->" + getTop());
        canvas.drawCircle(mXCenter, mYCenter, my_circle_radius, mCirclePaint);
        mBlur = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);

        // 渐变色

        SweepGradient sweepGradient = new SweepGradient(mXCenter, mYCenter,
                colors, positions);
        mRingPaint.setShader(sweepGradient);
        mRingPaint.setMaskFilter(mBlur);
        mRingPaint.setStrokeCap(Paint.Cap.ROUND);

        mRingPaint.setShader(sweepGradient);

        RectF oval = new RectF();
        oval.left = (mXCenter - my_circle_radius);
        oval.top = (mYCenter - my_circle_radius);
        oval.right = my_circle_radius * 2 + (mXCenter - my_circle_radius);
        oval.bottom = my_circle_radius * 2 + (mYCenter - my_circle_radius);
        canvas.drawArc(oval,
                -90,
                ((float) mProgress / mTotalProgress) * 360,
                false,
                mRingPaint); //

        //////////////////////////////步数/////////////////////////////////////

        //canvas.drawLine(0, mYCenter-my_circle_radius/2, getWidth(), mYCenter-my_circle_radius/2, mCalPaint);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.parseColor("#b0aeab"));
        //        mTextPaint.setARGB(255, 88, 89, 91);
        mTextPaint.setTextSize(mRadius / 5);
        String txt = "步数";
        Rect rect_up = new Rect();
        mTextPaint.getTextBounds(txt, 0, txt.length(), rect_up);
        fm = mTextPaint.getFontMetrics();
        mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
        int h_up = (int) (my_circle_radius / 2 + mStrokeWidth);
        int w_min_up = (int) (Math.sqrt(my_circle_radius * my_circle_radius
                - h_up * h_up) * 2);
        int size_up = (int) (mRadius / 5);
        int y_position_up = mYCenter - my_circle_radius / 2;
        while (w_min_up < (rect_up.right - rect_up.left))
        {
            mTextPaint.setTextSize(--size_up);
            mTextPaint.getTextBounds(txt, 0, txt.length(), rect_up);

            TLog.Log("zxl---taskcompleted---w_mini_1 2--->" + w_min_up + "--->"
                    + (rect_up.right - rect_up.left));
        }
        mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
        canvas.drawText(txt, mXCenter - mTxtWidth / 2, y_position_up
                + (rect_up.bottom - rect_up.top) / 2, mTextPaint);
        //////////////////////////////步数/////////////////////////////////////

        //////////////////////////////1,234步/////////////////////////////////////

        //canvas.drawLine(mXCenter-my_circle_radius, mYCenter, mXCenter+my_circle_radius, mYCenter, mCalPaint);

        mCalPaint.setAntiAlias(true);
        mCalPaint.setStyle(Paint.Style.FILL);
        //        mCalPaint.setARGB(255, 255, 67, 67);
        mCalPaint.setColor(Color.parseColor("#b0aeab"));
        mCalPaint.setTextSize(mRadius / 4);
        String strCal = FormatParameters.NumberFirmwareFormat(this.mCal);
        int w_min_middle = my_circle_radius * 2;
        int size_middle = (int) (mRadius / 2);
        int y_position_middle = mYCenter;
        mTxtWidth = mCalPaint.measureText(strCal + "步", 0, strCal.length() + 1);
        while (w_min_middle < mTxtWidth)
        {
            mCalPaint.setTextSize(--size_middle);
            mTxtWidth = mCalPaint.measureText(strCal + "步",
                    0,
                    strCal.length() + 1);

            TLog.Log("zxl---taskcompleted---w_mini_1 2--->" + w_min_up + "--->"
                    + (rect_up.right - rect_up.left));
        }
        Rect rect_middle = new Rect();
        mCalPaint.getTextBounds(strCal, 0, strCal.length(), rect_middle);
        Rect rect_middle_k = new Rect();
        mCalUnitPaint.getTextBounds("步", 0, 1, rect_middle_k);
        int w_middle = rect_middle.right - rect_middle.left
                + rect_middle_k.right - rect_middle_k.left;
        canvas.drawText(strCal, mXCenter - (w_middle) / 2, y_position_middle
                + (rect_middle.bottom - rect_middle.top) / 2, mCalPaint);
        canvas.drawText("步",
                mXCenter + (w_middle) / 2
                        - (rect_middle_k.right - rect_middle_k.left) / 3 * 2,
                y_position_middle + (rect_middle_k.bottom - rect_middle_k.top)
                        / 2,
                mCalUnitPaint);
        //////////////////////////////1,234步/////////////////////////////////////

        //////////////////////////////已完成100%/////////////////////////////////////

        //canvas.drawLine(0, mYCenter+my_circle_radius/2, getWidth(), mYCenter+my_circle_radius/2, mCalPaint);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.parseColor("#b0aeab"));
        //        mTextPaint.setARGB(255, 88, 89, 91);
        mTextPaint.setTextSize(mRadius / 5);

        String strPersent = "已完成" + 100 + "%";

        Rect rect_down = new Rect();
        mTextPaint.getTextBounds(strPersent, 0, strPersent.length(), rect_down);
        fm = mTextPaint.getFontMetrics();
        mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
        int h_down = (int) (my_circle_radius / 2 + mStrokeWidth);
        int w_min_down = (int) (Math.sqrt(my_circle_radius * my_circle_radius
                - h_down * h_down) * 2);
        int size_down = (int) (mRadius / 5);
        int y_position_down = mYCenter + my_circle_radius / 2;
        while (w_min_down < (rect_down.right - rect_down.left))
        {
            mTextPaint.setTextSize(--size_down);
            mTextPaint.getTextBounds(strPersent,
                    0,
                    strPersent.length(),
                    rect_down);
        }

        strPersent = "已完成" + mPersent + "%";
        mTxtWidth = mTextPaint.measureText(strPersent, 0, strPersent.length());
        canvas.drawText(strPersent, mXCenter - mTxtWidth / 2, y_position_down
                + (rect_down.bottom - rect_down.top) / 2, mTextPaint);
        //////////////////////////////已完成100%/////////////////////////////////////

    }

    public void setProgress(int progress)
    {
        mProgress = progress;
        // invalidate();
        postInvalidate();
    }

    public void setPersent(int persent)
    {
        this.mPersent = persent;
        postInvalidate();
    }

    public void setCal(float cal)
    {
        this.mCal = cal;
        postInvalidate();
    }

}
