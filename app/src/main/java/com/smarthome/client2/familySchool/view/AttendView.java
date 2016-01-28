package com.smarthome.client2.familySchool.view;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.CardAnalysis;
import com.smarthome.client2.familySchool.model.CardOne;
import com.smarthome.client2.util.ScreenUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author n003913 教师用的考勤View
 */
public class AttendView extends View
{

    private float width;

    private float height;

    private CardAnalysis model;

    private Paint paint;

    private Rect rect;

    private final String LATESTR = "迟到";

    private final String EARLYSTR = "早退";

    private final String ABSENTSTR = "缺卡";

    private final String NO_ATTENDANCE = "没有出勤记录";

    private int color_bar;

    private int color_name;

    private int color_time;

    private int color_early;

    private int color_late;

    private int color_absent;

    private int color_normal;

    private int color_future;

    public AttendView(Context context)
    {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        rect = new Rect();
        Resources resources = getResources();
        color_bar = resources.getColor(R.color.attendance_bar);
        color_name = resources.getColor(R.color.class_circle_text_deep);
        color_time = resources.getColor(R.color.class_circle_text_light);
        color_early = resources.getColor(R.color.class_circle_yellow);
        color_late = resources.getColor(R.color.attendance_late);
        color_absent = resources.getColor(R.color.attendance_absent);
        color_normal = resources.getColor(R.color.springgreen);
        color_future = resources.getColor(R.color.syllabus_frame);
    }

    public AttendView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        paint = new Paint();
        rect = new Rect();
        Resources resources = getResources();
        color_bar = resources.getColor(R.color.attendance_bar);
        color_name = resources.getColor(R.color.class_circle_text_deep);
        color_time = resources.getColor(R.color.class_circle_text_light);
        color_early = resources.getColor(R.color.class_circle_yellow);
        color_late = resources.getColor(R.color.attendance_late);
        color_absent = resources.getColor(R.color.attendance_absent);
        color_normal = resources.getColor(R.color.springgreen);
        color_future = resources.getColor(R.color.syllabus_frame);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        Log.e("size", width + "##" + height);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (model == null)
        {
            return;
        }
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL);
        // 姓名
        String temp = model.getName();
        paint.setColor(color_name);
        paint.setStrokeWidth(ScreenUtils.dip2px(getContext(), 2));
        paint.setTextSize(ScreenUtils.sp2px(getContext(), 16));
        paint.getTextBounds(temp, 0, temp.length(), rect);
        canvas.drawText(temp,
                width / 2 - rect.width() / 2,
                ScreenUtils.dip2px(getContext(), 5) + rect.height(),
                paint);
        // 考勤点
        ArrayList<CardOne> list = model.getList();
        // 如果没有考勤点
        if (list == null || list.size() == 0)
        {
            paint.setColor(color_time);
            paint.setTextSize(ScreenUtils.sp2px(getContext(), 14));
            paint.getTextBounds(NO_ATTENDANCE, 0, NO_ATTENDANCE.length(), rect);
            canvas.drawText(NO_ATTENDANCE, width / 2 - rect.width() / 2, height
                    / 2 + rect.height() / 2, paint);
            return;
        }
        // 时间线
        paint.setColor(color_bar);
        paint.setStrokeWidth(ScreenUtils.dip2px(getContext(), 5));
        canvas.drawLine(ScreenUtils.dip2px(getContext(), 10), height / 2, width
                - ScreenUtils.dip2px(getContext(), 10), height / 2, paint);
        // 设定起点、终点圆心距离两侧40dp
        int count = list.size();
        float distance = 1;
        if (count - 1 > 0)
        {
            distance = (width - ScreenUtils.dip2px(getContext(), 80))
                    / (count - 1);
        }
        paint.setStrokeWidth(ScreenUtils.dip2px(getContext(), 2));
        CardOne cardOne = null;
        // 文字偏离时间线的距离(纵向上的),相邻点的文字一上一下
        float direct = 1;
        for (int i = 0; i < count; i++)
        {
            cardOne = list.get(i);
            // 时间
            temp = cardOne.getTime();
            paint.setColor(color_time);
            paint.getTextBounds(temp, 0, temp.length(), rect);
            // 时间的左下顶点相对于height/2的偏移
            if (i % 2 == 0)
            {
                direct = ScreenUtils.dip2px(getContext(), 10) + rect.height();
            }
            else
            {
                direct = -ScreenUtils.dip2px(getContext(), 10);
            }
            canvas.drawText(temp, ScreenUtils.dip2px(getContext(), 40) + i
                    * distance - rect.width() / 2, height / 2 + direct, paint);
            // 在时间线下方,迟到\早退\缺勤的左下顶点相对于height/2的偏移
            if (direct > 0)
            {
                direct = direct + ScreenUtils.dip2px(getContext(), 5);
            }
            // 在时间线上方,迟到\早退\缺勤的左下顶点相对于height/2的偏移
            else
            {
                direct = direct - rect.height()
                        - ScreenUtils.dip2px(getContext(), 5);
            }
            // 迟到
            if (cardOne.getState().equals("1"))
            {
                paint.setColor(color_late);
                paint.getTextBounds(LATESTR, 0, LATESTR.length(), rect);
                if (direct > 0)
                {
                    direct = direct + rect.height();
                }
                canvas.drawText(LATESTR,
                        ScreenUtils.dip2px(getContext(), 40) + i * distance
                                - rect.width() / 2,
                        height / 2 + direct,
                        paint);
                canvas.drawCircle(ScreenUtils.dip2px(getContext(), 40) + i
                        * distance,
                        height / 2,
                        ScreenUtils.dip2px(getContext(), 8),
                        paint);
            }
            // 早退
            else if (cardOne.getState().equals("2"))
            {
                paint.setColor(color_early);
                paint.getTextBounds(EARLYSTR, 0, EARLYSTR.length(), rect);
                if (direct > 0)
                {
                    direct = direct + rect.height();
                }
                canvas.drawText(EARLYSTR,
                        ScreenUtils.dip2px(getContext(), 40) + i * distance
                                - rect.width() / 2,
                        height / 2 + direct,
                        paint);
                canvas.drawCircle(ScreenUtils.dip2px(getContext(), 40) + i
                        * distance,
                        height / 2,
                        ScreenUtils.dip2px(getContext(), 8),
                        paint);
            }
            // 缺勤
            else if (cardOne.getState().equals("3"))
            {
                paint.setColor(color_absent);
                paint.getTextBounds(ABSENTSTR, 0, ABSENTSTR.length(), rect);
                if (direct > 0)
                {
                    direct = direct + rect.height();
                }
                canvas.drawText(ABSENTSTR,
                        ScreenUtils.dip2px(getContext(), 40) + i * distance
                                - rect.width() / 2,
                        height / 2 + direct,
                        paint);
                canvas.drawCircle(ScreenUtils.dip2px(getContext(), 40) + i
                        * distance,
                        height / 2,
                        ScreenUtils.dip2px(getContext(), 8),
                        paint);
            }
            // 正常
            else if (cardOne.getState().equals("0"))
            {
                paint.setColor(color_normal);
                canvas.drawCircle(ScreenUtils.dip2px(getContext(), 40) + i
                        * distance,
                        height / 2,
                        ScreenUtils.dip2px(getContext(), 8),
                        paint);
            }
            // 未来考勤点
            else
            {
                paint.setColor(color_future);
                canvas.drawCircle(ScreenUtils.dip2px(getContext(), 40) + i
                        * distance,
                        height / 2,
                        ScreenUtils.dip2px(getContext(), 8),
                        paint);
            }
        }
    }

    public void setData(CardAnalysis model)
    {
        this.model = model;
        invalidate();
    }

}
