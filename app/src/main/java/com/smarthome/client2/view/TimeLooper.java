package com.smarthome.client2.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.MyGoalListBean;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.unit.domain.SleepTime;
import com.smarthome.client2.util.FormatParameters;
import com.smarthome.client2.util.ScreenUtils;

public class TimeLooper extends View
{

    private float mRadius = 0.0f;

    private RectF mRadialScoreRect;

    private Paint mRadialWidgetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint lightBluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint deepBluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint orangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mBorderStrokeThickness = 5.0f;

    private int mBorderColor = Color.LTGRAY;

    private int index = 0;

    private MyGoalListBean goalListBean = new MyGoalListBean();

    public int week = 0;

    public int index_line_view_from_my_sleep = 0;

    public int sleeptime;

    public int heavy_sleep;

    public int light_sleep;

    public int weak_up;

    private int awakeTimes = 0;

    private int sleepGoal = 0;

    private Handler mHandler;

    private String startTime = "";

    private String endTime = "";

    private int color_light_blue = Color.parseColor("#7dbeff");

    private int color_deep_blue = Color.parseColor("#3c7ec0");

    private int color_orange = Color.parseColor("#ff5f3d");

    private int color_background = Color.parseColor("#f8f8f8");

    // 圆环宽度
    private float mStrokeWidth;

    private int mXCenter = 0;

    private int mYCenter = 0;

    private int my_circle_radius = 0;

    private int my_v_tv_middle = ScreenUtils.dip2px(getContext(), 5);

    public int index_today_sleep = -1;

    public boolean isOntouch = false;

    public float mRingRadius = 0;

    private int sleepQuality = 0;

    public TimeLooper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TasksCompletedView,
                0,
                0);
        mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius,
                80);
        mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth,
                10);
        TLog.Log("zxl---TimeLooper---initattrs--->" + getWidth() + "--->"
                + getHeight() + "--->" + mRadius + "--->" + mStrokeWidth);
        mRingRadius = mRadius + mStrokeWidth / 2;
    }

    public void setHandler(Handler handler)
    {
        this.mHandler = handler;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        heavy_sleep = 0;
        light_sleep = 0;

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() * 2 / 5;
        //      my_circle_radius = (int) ((getHeight() - mStrokeWidth*2)/2);
        my_circle_radius = (int) mRingRadius;
        //        int tmp_circle_radius = (int) ((getHeight() - mStrokeWidth*19)/2);
        RectF oval = new RectF();
        oval.left = (mXCenter - my_circle_radius);
        oval.top = (mYCenter - my_circle_radius);
        oval.right = my_circle_radius * 2 + (mXCenter - my_circle_radius);
        oval.bottom = my_circle_radius * 2 + (mYCenter - my_circle_radius);

        mRadialWidgetPaint.setStyle(Style.STROKE);
        mRadialWidgetPaint.setStrokeWidth(mStrokeWidth);

        mRadialScoreRect = new RectF(oval);
        //      canvas.drawArc(mRadialScoreRect, 120, 300, false, mRadialWidgetPaint);

        lightBluePaint.setStyle(Style.STROKE);
        deepBluePaint.setStyle(Style.STROKE);
        orangePaint.setStyle(Style.STROKE);
        lightBluePaint.setStrokeWidth(mStrokeWidth);
        deepBluePaint.setStrokeWidth(mStrokeWidth);
        orangePaint.setStrokeWidth(mStrokeWidth);
        lightBluePaint.setColor(color_light_blue);
        deepBluePaint.setColor(color_deep_blue);
        orangePaint.setColor(color_orange);

        //      mRadialWidgetPaint.setColor(getResources().getColor(R.color.default_blue));

        sleeptime = 0;
        heavy_sleep = 0;
        light_sleep = 0;
        weak_up = 0;
        //      startTime = "";
        //      endTime = "";
        boolean hasSleepData = false;
        if (kuqi_datalist_sleep != null && !kuqi_datalist_sleep.isEmpty())
        {

            //          if (index_today_sleep < 0) {
            //              index_today_sleep = 0;
            //          }
            //          if (index_today_sleep >= sleepDataList.size()) {
            //              index_today_sleep = sleepDataList.size() - 1;
            //          }
            //
            //          for(Sleeping s : sleepDataList){
            //              TLog.Log("zxl---timelooper---sleepDataList--->"+s.start.getTime()+"--->"+s.end.getTime()+"--->"+sleepDataList.size()+"--->"+index+"--->"+index_today_sleep);
            //          }
            //
            //          if(index < 0)
            //          {
            //              index = sleepDataList.size()-1;
            //          }else if( index > (sleepDataList.size()-1))
            //          {
            //              index = 0;
            //          }

            //          Sleeping sleeping = sleepDataList.get(index_today_sleep);
            //          startTime = formatTime(sleeping.start);
            //          endTime = formatTime(sleeping.end);

            //          List<AM_dataPerMinute> dataPerMinutes = sleeping.data;
            if (kuqi_datalist_sleep != null && !kuqi_datalist_sleep.isEmpty())
            {
                //              mRadialWidgetPaint.setStrokeCap(Paint.Cap.ROUND);
                //              mRadialWidgetPaint.setColor(color_orange);
                //              canvas.drawArc(oval, 120, 300,false, mRadialWidgetPaint);
                hasSleepData = true;

                //              TLog.Log("zxl---timelooper---AM_HS_SIZE--->"+dataPerMinutes.size());

                long t_1 = System.currentTimeMillis();

                float unitAngle = 300 * 1.0F / kuqi_datalist_sleep.size();
                for (int i = 0; i < kuqi_datalist_sleep.size(); i++)
                {
                    //                  AM_dataPerMinute dataPerMinute = dataPerMinutes.get(i);
                    int status = kuqi_datalist_sleep.get(i);
                    switch (status)
                    {
                        case Constants.KUQI_STATUS_HEAVY_SLEEP:

                            //                      TLog.Log("zxl---timelooper---AM_HS_HEAVY_SLEEP--->");

                            heavy_sleep += 5;
                            if (0 == i)
                            {
                                drawMyRound(canvas, color_deep_blue, true);
                            }
                            else if (i == kuqi_datalist_sleep.size() - 1)
                            {
                                drawMyRound(canvas, color_deep_blue, false);
                            }
                            canvas.drawArc(mRadialScoreRect, 120 + unitAngle
                                    * i, unitAngle, false, deepBluePaint);

                            break;

                        case Constants.KUQI_STATUS_LIGHT_SLEEP:

                            //                      TLog.Log("zxl---timelooper---AM_HS_LIGHT_SLEEP--->");

                            light_sleep += 5;
                            if (0 == i)
                            {
                                drawMyRound(canvas, color_light_blue, true);
                            }
                            else if (i == kuqi_datalist_sleep.size() - 1)
                            {
                                drawMyRound(canvas, color_light_blue, false);
                            }
                            canvas.drawArc(mRadialScoreRect, 120 + unitAngle
                                    * i, unitAngle, false, lightBluePaint);

                            break;
                        case Constants.KUQI_STATUS_AWAKE:

                            //                      TLog.Log("zxl---timelooper---AM_HS_AWAKENING--->");
                            if (0 == i)
                            {
                                drawMyRound(canvas, color_orange, true);
                            }
                            else if (i == kuqi_datalist_sleep.size() - 1)
                            {
                                drawMyRound(canvas, color_orange, false);
                            }
                            weak_up += 5;
                            canvas.drawArc(mRadialScoreRect, 120 + unitAngle
                                    * i, unitAngle, false, orangePaint);

                            break;
                        default:
                            break;
                    }
                }
                long t_2 = System.currentTimeMillis();
                TLog.Log("zxl---timelooper---draw time 1--->" + (t_2 - t_1));
            }
            Message message = new Message();
            message.what = 3;
            SleepTime sleepTime = new SleepTime();
            sleepTime.setStartTime(startTime);
            sleepTime.setEndTime(endTime);
            message.obj = sleepTime;
            mHandler.sendMessage(message);
        }
        else
        {
            //          Message message = new Message();
            //          message.what=111;
            //          mHandler.sendMessage(message);
        }

        if (!hasSleepData)
        {
            //          mRadialWidgetPaint.setColor(Color.GRAY);
            mRadialWidgetPaint.setColor(Color.parseColor("#4c000000"));
            mRadialWidgetPaint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawArc(mRadialScoreRect,
                    120,
                    300,
                    false,
                    mRadialWidgetPaint);
        }

        long t_3 = System.currentTimeMillis();
        initAllSleepCircle(canvas);
        long t_4 = System.currentTimeMillis();
        TLog.Log("zxl---timelooper---draw time 2--->" + (t_4 - t_3));
    }

    private void drawMyRound(Canvas canvas, int color, boolean isStart)
    {
        Paint paint = new Paint();
        paint.setColor(color);
        RectF oval = new RectF();

        float startAngle = 0;
        float sweepAngle = 0;
        if (isStart)
        {
            int x_center = (int) (mXCenter - my_circle_radius
                    * Math.sin(Math.toRadians(30)));
            int y_center = (int) (mYCenter + my_circle_radius
                    * Math.cos(Math.toRadians(30)));

            oval.left = x_center - mStrokeWidth / 2;
            oval.right = x_center + mStrokeWidth / 2;
            oval.top = y_center - mStrokeWidth / 2;
            oval.bottom = y_center + mStrokeWidth / 2;

            startAngle = 300;
            sweepAngle = 180;
        }
        else
        {
            int x_center = (int) (mXCenter + my_circle_radius
                    * Math.sin(Math.toRadians(30)));
            int y_center = (int) (mYCenter + my_circle_radius
                    * Math.cos(Math.toRadians(30)));

            oval.left = x_center - mStrokeWidth / 2;
            oval.right = x_center + mStrokeWidth / 2;
            oval.top = y_center - mStrokeWidth / 2;
            oval.bottom = y_center + mStrokeWidth / 2;

            startAngle = 60;
            sweepAngle = 180;
        }
        canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
    }

    private void initAllSleepCircle(Canvas canvas)
    {

        /////////////////////////////有效睡眠/////////////////////////////////

        //        canvas.drawLine(0, mYCenter-my_circle_radius/2, getWidth(), mYCenter-my_circle_radius/2, lightBluePaint);

        Paint text_paint_sleep = new Paint();
        text_paint_sleep.setAntiAlias(true);
        text_paint_sleep.setStyle(Paint.Style.FILL);
        text_paint_sleep.setTextSize(mRadius / 5);
        text_paint_sleep.setColor(Color.parseColor("#b0aeab"));
        //
        //        String s_all_sleep_time = "有效睡眠";
        //        Rect rect_s_all_sleep_time = new Rect();
        //        text_paint_sleep.getTextBounds(s_all_sleep_time, 0, s_all_sleep_time.length(), rect_s_all_sleep_time);
        //        
        //      int h_up = (int) (my_circle_radius/2+mStrokeWidth);
        //      int w_min_up = (int) (Math.sqrt(my_circle_radius*my_circle_radius - h_up*h_up)*2);
        //      int size_up = (int) (mRadius / 2);
        //      int y_position_up = (int) (mYCenter-my_circle_radius/2);
        //      int len_up = s_all_sleep_time.length();
        //      while(w_min_up < (rect_s_all_sleep_time.right-rect_s_all_sleep_time.left)){
        //          TLog.Log("zxl---timelooper---有效睡眠--->"+size_up+"--->"+w_min_up+"--->"+(rect_s_all_sleep_time.right-rect_s_all_sleep_time.left));
        //          text_paint_sleep.setTextSize(--size_up);
        //          text_paint_sleep.getTextBounds(s_all_sleep_time, 0, len_up, rect_s_all_sleep_time);
        //      }
        //        
        //        int x_all_sleep_time = mXCenter-(rect_s_all_sleep_time.right-rect_s_all_sleep_time.left)/2;
        //        int y_all_sleep_time = y_position_up+(rect_s_all_sleep_time.bottom-rect_s_all_sleep_time.top)/2;
        //        canvas.drawText(s_all_sleep_time, x_all_sleep_time, y_all_sleep_time, text_paint_sleep);

        //      text_paint_sleep.setAntiAlias(true);
        //      text_paint_sleep.setStyle(Paint.Style.FILL);
        //      text_paint_sleep.setARGB(255, 88, 89, 91);
        //      text_paint_sleep.setTextSize(mRadius / 5);
        //      String txt = "有效睡眠";
        //      Rect rect_up = new Rect();
        //      text_paint_sleep.getTextBounds(txt, 0, txt.length(), rect_up);
        //      int h_up = (int) (my_circle_radius/2+mStrokeWidth);
        //      int w_min_up = (int) (Math.sqrt(my_circle_radius*my_circle_radius - h_up*h_up)*2);
        //      int size_up = (int) (mRadius / 5);
        //      int y_position_up = (int) (mYCenter-my_circle_radius/2);
        //      while(w_min_up < (rect_up.right-rect_up.left)){
        //          text_paint_sleep.setTextSize(--size_up);
        //          text_paint_sleep.getTextBounds(txt, 0, txt.length(), rect_up);
        //
        //          TLog.Log("zxl---taskcompleted---w_mini_1 2--->"+w_min_up +"--->"+ (rect_up.right-rect_up.left));
        //      }
        //      float mTxtWidth = text_paint_sleep.measureText(txt, 0, txt.length());
        //      canvas.drawText(txt, mXCenter - mTxtWidth / 2, y_position_up+(rect_up.bottom-rect_up.top)/2, text_paint_sleep);
        /////////////////////////////有效睡眠/////////////////////////////////

        /////////////////////////////18小时25分钟////////////////////////////////////////

        //        canvas.drawLine(0, mYCenter+my_circle_radius/2, getWidth(), mYCenter+my_circle_radius/2, text_paint_sleep);

        //        int size_down_v = (int) (mRadius / 2);
        //        int size_down = size_down_v/2;
        //        
        //        Paint v_sleep_paint = new Paint();
        //      v_sleep_paint.setAntiAlias(true);
        //      v_sleep_paint.setStyle(Paint.Style.FILL);
        //      v_sleep_paint.setColor(Color.parseColor(LineView.blue));;
        //      v_sleep_paint.setTextSize(size_down_v);
        //
        //      sleeptime = heavy_sleep + light_sleep;
        //      int v_h = sleeptime/60;
        //      int v_m = sleeptime%60;
        //      String s_v_h = ""+v_h;
        //      String s_v_m = ""+v_m;
        //      String s_h = "小时";
        //      String s_m = "分钟";
        //      Rect rect_v_h = new Rect();
        //      Rect rect_v_m = new Rect();
        //      Rect rect_s_h = new Rect();
        //      Rect rect_s_m = new Rect();
        //
        //      if(v_h > 0){
        //          v_sleep_paint.getTextBounds(s_v_h, 0, s_v_h.length(), rect_v_h);
        //          text_paint_sleep.getTextBounds(s_h, 0, s_h.length(), rect_s_h);
        //      }
        //      int h_down = (int) mStrokeWidth;
        //      int r_down = (int) (my_circle_radius-mStrokeWidth/2);
        ////        int w_min_down = (int) (Math.sqrt(r_down*r_down - h_down*h_down)*2);
        //      int w_min_down = r_down*2;
        //      int w_down = 0;
        //
        ////        canvas.drawLine(0, mYCenter, getWidth(), mYCenter, text_paint_sleep);
        //
        //      v_sleep_paint.getTextBounds(s_v_m, 0, s_v_m.length(), rect_v_m);
        //      text_paint_sleep.getTextBounds(s_m, 0, s_m.length(), rect_s_m);
        //
        //      if(0 == v_h){
        //          w_down = (rect_s_m.right-rect_s_m.left)+(rect_v_m.right-rect_v_m.left)+my_v_tv_middle;
        //      }else{
        //          w_down = my_v_tv_middle*3+(rect_s_h.right-rect_s_h.left)+(rect_s_m.right-rect_s_m.left)+(rect_v_h.right-rect_v_h.left)+(rect_v_m.right-rect_v_m.left);
        //      }
        //
        //      while(w_min_down < w_down){
        //          --size_down_v;
        //          size_down = size_down_v/2;
        //          text_paint_sleep.setTextSize(size_down);
        //          v_sleep_paint.setTextSize(size_down_v);
        //          if(v_h > 0){
        //              v_sleep_paint.getTextBounds(s_v_h, 0, s_v_h.length(), rect_v_h);
        //              text_paint_sleep.getTextBounds(s_h, 0, s_h.length(), rect_s_h);
        //          }
        //          v_sleep_paint.getTextBounds(s_v_m, 0, s_v_m.length(), rect_v_m);
        //          text_paint_sleep.getTextBounds(s_m, 0, s_m.length(), rect_s_m);
        //          
        //          if(0 == v_h){
        //              w_down = (rect_s_m.right-rect_s_m.left)+(rect_v_m.right-rect_v_m.left)+my_v_tv_middle;
        //          }else{
        //              w_down = my_v_tv_middle*3+(rect_s_h.right-rect_s_h.left)+(rect_s_m.right-rect_s_m.left)+(rect_v_h.right-rect_v_h.left)+(rect_v_m.right-rect_v_m.left);
        //          }
        //          TLog.Log("zxl---timelooper---18小时25分钟--->"+w_min_down+"--->"+w_down);
        //      }
        //
        //
        //      int x_sleep_time = 0;
        //      int y_sleep_time = mYCenter+(rect_v_m.bottom-rect_v_m.top)/2;
        //      x_sleep_time = mXCenter-w_down/2;
        //
        //      if(0 == v_h){
        //          canvas.drawText(s_v_m, x_sleep_time, y_sleep_time, v_sleep_paint);
        //          canvas.drawText(s_m, my_v_tv_middle+x_sleep_time+(rect_v_m.right-rect_v_m.left), mYCenter+(rect_s_m.bottom-rect_s_m.top)/2, text_paint_sleep);
        //      }else{
        //          canvas.drawText(s_v_h, x_sleep_time, y_sleep_time, v_sleep_paint);
        //          canvas.drawText(s_h, my_v_tv_middle+x_sleep_time+(rect_v_h.right-rect_v_h.left), mYCenter+(rect_s_h.bottom-rect_s_h.top)/2, text_paint_sleep);
        //          canvas.drawText(s_v_m, my_v_tv_middle*2+x_sleep_time+(rect_v_h.right-rect_v_h.left)+(rect_s_h.right-rect_s_h.left), y_sleep_time, v_sleep_paint);
        //          canvas.drawText(s_m, my_v_tv_middle*3+x_sleep_time+(rect_v_h.right-rect_v_h.left)+(rect_s_h.right-rect_s_h.left)+(rect_v_m.right-rect_v_m.left), mYCenter+(rect_s_m.bottom-rect_s_m.top)/2, text_paint_sleep);
        //      }
        /////////////////////////////18小时25分钟////////////////////////////////////////

        //      if(sleepDataList != null && sleepDataList.size() > 1){
        //
        //          int count = sleepDataList.size();
        //
        //          Paint my_introduce_circle_paint = new Paint();
        //          my_introduce_circle_paint.setAntiAlias(true);
        //          my_introduce_circle_paint.setStyle(Paint.Style.FILL);
        //
        //          BitmapFactory.Options options_no_pressed = new BitmapFactory.Options();
        //          BitmapFactory.Options options_pressed = new BitmapFactory.Options();
        //          options_no_pressed.inJustDecodeBounds = true;
        //          options_pressed.inJustDecodeBounds = true;
        //          Bitmap bitmap_no_pressed = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sleep_introduce_circle_no_pressed, options_no_pressed);
        //          Bitmap bitmap_pressed = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sleep_introduce_circle_pressed, options_pressed);
        //
        //          int w_no_pressed = options_no_pressed.outWidth;
        //          int h_no_pressed = options_no_pressed.outHeight;
        //          int w_pressed = options_pressed.outWidth;
        //          int h_pressed = options_pressed.outHeight;

        //          int x_my_introduce_circle = mXCenter-(w_no_pressed*count+w_pressed)/2;
        //          int y_my_introduce_circle = (int) (getHeight()-h_no_pressed);
        //
        //          for(int i = 0;i<count;i++){
        //              if(i < index_today_sleep){
        //                  canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_sleep_introduce_circle_no_pressed), x_my_introduce_circle+(i+1)*w_no_pressed, y_my_introduce_circle, my_introduce_circle_paint);
        //              }else if(i == index_today_sleep){
        //                  canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_sleep_introduce_circle_pressed), x_my_introduce_circle+(i+1)*w_no_pressed, y_my_introduce_circle, my_introduce_circle_paint);
        //              }else {
        //                  canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_sleep_introduce_circle_no_pressed), x_my_introduce_circle+i*w_no_pressed+w_pressed, y_my_introduce_circle, my_introduce_circle_paint);
        //              }
        //          }
        //      }

        ///////////////////////05:23  13:56///////////////////////////////////////////////
        Paint text_paint = new Paint();
        text_paint.setAntiAlias(true);
        text_paint.setStyle(Paint.Style.FILL);
        text_paint.setTextSize(25);
        text_paint.setColor(Color.parseColor("#b0aeab"));

        int x_start_time = 0;
        int y_start_time = 0;
        int x_end_time = 0;
        int y_end_time = 0;

        BitmapFactory.Options options_no_pressed = new BitmapFactory.Options();
        options_no_pressed.inJustDecodeBounds = true;
        Bitmap bitmap_no_pressed = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_sleep_introduce_circle_no_pressed,
                options_no_pressed);
        int h_no_pressed = options_no_pressed.outHeight;

        String my_start_time = startTime;
        Rect rect_s_start_time = new Rect();
        text_paint.getTextBounds(my_start_time,
                0,
                my_start_time.length(),
                rect_s_start_time);

        int h_down_time = rect_s_start_time.bottom - rect_s_start_time.top;
        //      int y_position_down_time = (int) (mYCenter + (my_circle_radius+mStrokeWidth/2)*Math.cos(Math.toRadians(30)));
        int y_position_down_time = (int) (mYCenter + (my_circle_radius + mStrokeWidth / 2)
                * Math.cos(Math.toRadians(70)));
        int h_min_down_time = getHeight() - y_position_down_time - h_no_pressed;
        int size_down_time = 25;

        //      canvas.drawLine(0, y_position_down_time, getWidth(), y_position_down_time, text_paint);
        //      canvas.drawLine(0, getHeight()-h_no_pressed, getWidth(), getHeight()-h_no_pressed, text_paint);
        //      canvas.drawLine(mXCenter, mYCenter, (int)(mXCenter-my_circle_radius*Math.sin(Math.toRadians(30))),(int)(mYCenter+my_circle_radius*Math.cos(Math.toRadians(30))), text_paint);

        while (h_min_down_time < h_down_time)
        {
            text_paint.setTextSize(--size_down_time);
            text_paint.getTextBounds(my_start_time,
                    0,
                    my_start_time.length(),
                    rect_s_start_time);
            h_down_time = rect_s_start_time.bottom - rect_s_start_time.top;
        }

        x_start_time = (int) (mXCenter
                - (rect_s_start_time.right - rect_s_start_time.left) / 2 - my_circle_radius
                * Math.sin(Math.toRadians(30)));
        y_start_time = y_position_down_time + ((rect_s_start_time.bottom - rect_s_start_time.top) + h_min_down_time) / 2;

        canvas.drawText(my_start_time, x_start_time, y_start_time, text_paint);
        TLog.Log("zxl---timelooper---starttime--->" + x_start_time + "--->"
                + y_start_time + "--->" + my_start_time);

        String my_end_time = endTime;
        Rect rect_s_end_time = new Rect();
        text_paint.getTextBounds(my_end_time,
                0,
                my_end_time.length(),
                rect_s_end_time);
        x_end_time = (int) (mXCenter
                - (rect_s_end_time.right - rect_s_end_time.left) / 2 + my_circle_radius
                * Math.sin(Math.toRadians(30)));
        y_end_time = y_start_time;

        canvas.drawText(my_end_time, x_end_time, y_end_time, text_paint);
        TLog.Log("zxl---timelooper---endtime--->" + x_end_time + "--->"
                + y_end_time + "--->" + my_end_time);
        ///////////////////////05:23  13:56///////////////////////////////////////////////

        //      canvas.drawLine(0, mYCenter+my_circle_radius/2, getWidth(), mYCenter+my_circle_radius/2, text_paint);

        //      String strPersent = "已完成" + 100 + "%";
        //      Rect rect_down = new Rect();
        //      text_paint_sleep.getTextBounds(strPersent, 0, strPersent.length(), rect_down);
        //      int h_down_text = (int) (my_circle_radius/2+mStrokeWidth);
        //      int w_min_down_text = (int) (Math.sqrt(my_circle_radius*my_circle_radius - h_down_text*h_down_text)*2);
        //      int size_down_text = (int) (mRadius / 5);
        //      int y_position_down = (int) (mYCenter+my_circle_radius/2);
        //      while(w_min_down_text < (rect_down.right-rect_down.left)){
        //          text_paint_sleep.setTextSize(--size_down_text);
        //          text_paint_sleep.getTextBounds(strPersent, 0, strPersent.length(), rect_down);
        //      }

        //      int sleep_goal_by_week = 0;
        //      int count_all_cal_goal = goalListBean.list.size();
        //      if(count_all_cal_goal < 1 || count_all_cal_goal - 1 < week-1-index_line_view_from_my_sleep){
        //          sleep_goal_by_week = Preferences.getInstance(getContext()).getSleeptime();
        //      }else{
        //          sleep_goal_by_week = goalListBean.list.get(week-1-index_line_view_from_my_sleep).sleepTime;
        //      }

        //      int mPersent = (int) ((sleeptime*1.0/sleep_goal_by_week)*100);
        //      strPersent = "已完成" + mPersent + "%";

        //      TLog.Log("zxl---timelooper---有效睡眠--->"+sleeptime+"--->"+Preferences.getInstance(getContext()).getSleeptime()+"--->"+mPersent);

        //      mTxtWidth = text_paint_sleep.measureText(strPersent, 0, strPersent.length());
        //      canvas.drawText(strPersent, mXCenter - mTxtWidth / 2, y_position_down+(rect_down.bottom-rect_down.top)/2, text_paint_sleep);
        //////////////////////////////已完成100%/////////////////////////////////////

        // ///////////////////////////睡眠质量/////////////////////////////////

        // canvas.drawLine(0, mYCenter-my_circle_radius/2, getWidth(),
        // mYCenter-my_circle_radius/2, lightBluePaint);

        String strPersent = "已完成" + 100 + "%";
        float mTxtWidth = text_paint_sleep.measureText(strPersent,
                0,
                strPersent.length());
        String txt = "睡眠质量";
        Rect rect_up = new Rect();
        text_paint_sleep.getTextBounds(txt, 0, txt.length(), rect_up);
        int h_up = (int) ((my_circle_radius - mStrokeWidth / 2) / 3);
        int w_min_up = (int) (Math.sqrt((my_circle_radius - mStrokeWidth / 2)
                * (my_circle_radius - mStrokeWidth / 2) - h_up * h_up) * 2);
        int size_up = (int) (mRadius / 5);
        int y_position_up = mYCenter - my_circle_radius / 3;
        while (w_min_up < (rect_up.right - rect_up.left))
        {
            text_paint_sleep.setTextSize(--size_up);
            text_paint_sleep.getTextBounds(txt, 0, txt.length(), rect_up);

            TLog.Log("zxl---taskcompleted---w_mini_1 2--->" + w_min_up + "--->"
                    + (rect_up.right - rect_up.left));
        }
        mTxtWidth = text_paint_sleep.measureText(txt, 0, txt.length());
        canvas.drawText(txt, mXCenter - mTxtWidth / 2, y_position_up
                + (rect_up.bottom - rect_up.top) / 2, text_paint_sleep);
        // ///////////////////////////睡眠质量/////////////////////////////////

        // //////////////////////////////睡眠质量 评星等级///////////////////////
        int x_position_start = mXCenter - 10;
        int y_position_start = mYCenter + my_circle_radius / 2 - 10;

        Paint my_star_bitmap_paint = new Paint();
        my_star_bitmap_paint.setAntiAlias(true);
        my_star_bitmap_paint.setStyle(Paint.Style.FILL);

        BitmapFactory.Options options_star_red = new BitmapFactory.Options();
        BitmapFactory.Options options_star_black = new BitmapFactory.Options();
        options_no_pressed.inJustDecodeBounds = true;
        options_star_black.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),
                R.drawable.star,
                options_star_red);
        BitmapFactory.decodeResource(getResources(),
                R.drawable.star_2,
                options_star_black);

        int w_star_red_last = options_star_red.outWidth;
        int h_star_red_last = options_star_red.outHeight;
        int w_star_red = w_star_red_last;
        int h_star_red = h_star_red_last;
        int w_star_black = options_star_black.outWidth;
        int h_star_black = options_star_black.outHeight;

        int h_down_text = (int) ((my_circle_radius - mStrokeWidth / 2) / 3);
        int w_min_down_text = (int) Math.round((Math.sqrt((my_circle_radius - mStrokeWidth / 2)
                * (my_circle_radius - mStrokeWidth / 2)
                - h_down_text
                * h_down_text) * 2));
        int padding_star = ScreenUtils.dip2px(getContext(), 5);
        int w_every_star = (int) (Math.round((w_min_down_text - padding_star * 6) * 1.0 / 5));

        int inSampleSize = 1;
        while (w_every_star < w_star_red && w_every_star > 0)
        {
            inSampleSize++;
            w_star_red = w_star_red_last / inSampleSize;
            h_star_red = h_star_red_last / inSampleSize;
        }
        int padding_now = (int) (Math.round((w_min_down_text - w_star_red * 5) * 1.0 / 6));

        options_star_red.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options_star_red.inJustDecodeBounds = false;
        Bitmap bitmap_star_red = BitmapFactory.decodeResource(getResources(),
                R.drawable.star,
                options_star_red);

        options_star_black.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options_star_black.inJustDecodeBounds = false;
        Bitmap bitmap_star_black = BitmapFactory.decodeResource(getResources(),
                R.drawable.star_2,
                options_star_red);

        int x_star_position = mXCenter - w_min_down_text / 2 + padding_now;
        int y_star_position = mYCenter + h_star_red / 2;

        sleeptime = heavy_sleep + light_sleep + weak_up;

        //        int sleep_quality = (int) (Math.round(((heavy_sleep + light_sleep) * 1.0 / (sleeptime + weak_up)) * 5));
        //        int sleep_quality = 0;
        //        if (sleeptime != 0)
        //        {
        //            sleep_quality = Math.round((Float.parseFloat(FormatParameters.OneDecimalFormat((float) (100
        //                    - awakeTimes
        //                    * 5
        //                    - Math.round(1.0 * weak_up / (sleeptime) * 100) - Math.round((1.0 * (sleepGoal
        //                    - sleeptime < 0 ? 0 : (sleepGoal - sleeptime))) / 60 * 5)) / 100)) * 5));
        //
        //        }
        for (int i = 0; i < 5; i++)
        {

            if (i < sleepQuality)
            {
                canvas.drawBitmap(bitmap_star_red,
                        x_star_position + i * padding_now + i * w_star_red,
                        y_star_position,
                        my_star_bitmap_paint);
            }
            else
            {
                canvas.drawBitmap(bitmap_star_black,
                        x_star_position + i * padding_now + i * w_star_red,
                        y_star_position,
                        my_star_bitmap_paint);
            }
        }
        // //////////////////////////////睡眠质量 评星等级///////////////////////
    }

    public void setGoalListBean(MyGoalListBean goalListBean)
    {
        this.goalListBean.list.clear();
        this.goalListBean.list.addAll(goalListBean.list);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                TLog.Log("zxl---timelooper---ontouch--->down");
                isOntouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                TLog.Log("zxl---timelooper---ontouch--->move");
                isOntouch = true;
                break;
            case MotionEvent.ACTION_UP:
                TLog.Log("zxl---timelooper---ontouch--->up");
                isOntouch = false;

                int x = (int) event.getX();
                int y = (int) event.getY();
                int delta = (y - mYCenter) * (y - mYCenter) + (x - mXCenter)
                        * (x - mXCenter);
                if (delta <= my_circle_radius * my_circle_radius)
                {
                    index_today_sleep++;
//                    if (sleepDataList != null)
//                    {
//                        if (index_today_sleep < 0)
//                        {
//                            index_today_sleep = 0;
//                        }
//                        if (index_today_sleep >= sleepDataList.size())
//                        {
//                            index_today_sleep = 0;
//                        }
//                        if (changShowDetailSleepTimeListener != null)
//                        {
//                            changShowDetailSleepTimeListener.OnChangShowDetailSleepTimeListener(index_today_sleep);
//                            postInvalidate();
//                        }
//                    }
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                TLog.Log("zxl---timelooper---ontouch--->cancle");
                isOntouch = false;
                break;
        }
        return true;
    }

    private List<Integer> kuqi_datalist_sleep;

    public void setKuqiDataSet(List<Integer> kuqi_datalist_sleep)
    {
        this.kuqi_datalist_sleep = kuqi_datalist_sleep;
    }

    public void setIndicator(int index)
    {
        this.index = index;
        this.postInvalidate();
    }

    public void invalidate()
    {
        //      postInvalidate();
        requestLayout();
    }

    public void next()
    {
        this.index++;
        postInvalidate();
    }

    public void pre()
    {
        this.index--;
        postInvalidate();
    }

    private String formatTime(Calendar calendar)
    {
        //      StringBuilder sb = new StringBuilder();
        //      sb.append(calendar.get(Calendar.HOUR_OF_DAY)+"时");
        //      sb.append(calendar.get(Calendar.MINUTE)+"分");
        //      return sb.toString();

        Date date = calendar.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        return sf.format(date);

    }

    private ChangShowDetailSleepTimeListener changShowDetailSleepTimeListener;

    public void setOnChangShowDetailSleepTimeListener(
            ChangShowDetailSleepTimeListener listener)
    {
        this.changShowDetailSleepTimeListener = listener;
    }

    public interface ChangShowDetailSleepTimeListener
    {
        int OnChangShowDetailSleepTimeListener(int index);
    }

    public void setStartEndTime(String startTime, String endTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setAwakeTimes(int times)
    {
        this.awakeTimes = times;
    }

    public void setSleepGoal(int sleepGoal)
    {
        this.sleepGoal = sleepGoal;
    }

    public void setSleepQuality(int sleepQuality)
    {
        this.sleepQuality = sleepQuality;
    }
}