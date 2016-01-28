package com.smarthome.client2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.smarthome.client2.common.TLog;
import com.smarthome.client2.util.ScreenUtils;

public class MyBindDialogView extends SurfaceView implements Callback, Runnable
{
    // 用于控制SurfaceView
    public SurfaceHolder sfh;

    // 声明一个画笔
    private Paint paint1, paint2;

    // 声明一条线程
    public Thread th;

    // 线程消亡的标识位
    private boolean flag;

    // 声明一个画布
    public Canvas canvas;

    // 设置画布绘图无锯齿
    private PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0,
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private Context ctx;

    public int w = 0;

    public int h = 0;

    public boolean isStop = true;

    int count_last = 0;

    int mStrokeWidth = ScreenUtils.dip2px(getContext(), 20);

    int mRadioCircle = ScreenUtils.dip2px(getContext(), 100);

    private int color_1 = Color.parseColor("#ffffffff");

    private int color_2 = Color.parseColor("#ffe7e7e7");

    private int color_3 = Color.parseColor("#b3ededed");

    private int color_4 = Color.parseColor("#80f5f5f5");

    private String msg = "正在搜索手环";

    //  private String msg = ctx.getString(R.string.bind_searching);
    private String msg1 = "请将手环靠近手机";

    /**
     * SurfaceView初始化函数
     */
    public MyBindDialogView(Context context)
    {
        super(context);
        this.ctx = context;
        init();
    }

    public MyBindDialogView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.ctx = context;
        init();
    }

    private void init()
    {

        w = ScreenUtils.getScreenWidth(ctx);
        h = ScreenUtils.getScreenHeight(ctx);

        // 实例SurfaceHolder
        sfh = this.getHolder();
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // 为SurfaceView添加状态监听
        sfh.addCallback(this);
        // 实例一个画笔
        paint1 = new Paint();
        paint2 = new Paint();
        paint1.setColor(Color.RED);
        paint2.setColor(Color.BLUE);
        // 设置画笔颜色为白色
        // 设置焦点
        setFocusable(true);
    }

    /**
     * 第一个时间点，只需要绘制一个点，不需要绘制线
     */
    public boolean myDrawOneCircle()
    {
        try
        {
            canvas = sfh.lockCanvas();
            if (canvas != null)
            {
                // ----设置画布绘图无锯齿
                canvas.setDrawFilter(pfd);
                // ----利用填充画布，刷屏
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

                if (isStop)
                {
                    return isStop;
                }

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Style.STROKE);

                paint.setColor(color_1);
                paint.setStrokeWidth(mRadioCircle);
                RectF oval0 = new RectF();
                oval0.left = w / 2 - mRadioCircle / 2;
                oval0.right = w / 2 + mRadioCircle / 2;
                oval0.top = h / 2 - mRadioCircle / 2;
                oval0.bottom = h / 2 + mRadioCircle / 2;
                canvas.drawArc(oval0, 0, 360, true, paint);

                switch (count_last % 4)
                {
                    case 0:
                        paint.setColor(color_1);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        //                  canvas.drawCircle(w/2, h/2, 100, paint);
                        RectF oval = new RectF();
                        oval.left = w / 2 - mRadioCircle;
                        oval.right = w / 2 + mRadioCircle;
                        oval.top = h / 2 - mRadioCircle;
                        oval.bottom = h / 2 + mRadioCircle;
                        canvas.drawArc(oval, 0, 360, false, paint);
                        break;
                    case 1:

                        paint.setColor(color_1);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval1 = new RectF();
                        oval1.left = w / 2 - mRadioCircle;
                        oval1.right = w / 2 + mRadioCircle;
                        oval1.top = h / 2 - mRadioCircle;
                        oval1.bottom = h / 2 + mRadioCircle;
                        canvas.drawArc(oval1, 0, 360, false, paint);

                        paint.setColor(color_2);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval12 = new RectF();
                        oval12.left = w / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth;
                        oval12.right = w / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth;
                        oval12.top = h / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth;
                        oval12.bottom = h / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth;
                        canvas.drawArc(oval12, 0, 360, false, paint);
                        break;
                    case 2:

                        paint.setColor(color_1);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval2 = new RectF();
                        oval2.left = w / 2 - mRadioCircle;
                        oval2.right = w / 2 + mRadioCircle;
                        oval2.top = h / 2 - mRadioCircle;
                        oval2.bottom = h / 2 + mRadioCircle;
                        canvas.drawArc(oval2, 0, 360, false, paint);

                        paint.setColor(color_2);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval22 = new RectF();
                        oval22.left = w / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth;
                        oval22.right = w / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth;
                        oval22.top = h / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth;
                        oval22.bottom = h / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth;
                        canvas.drawArc(oval22, 0, 360, false, paint);

                        paint.setColor(color_3);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval23 = new RectF();
                        oval23.left = w / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth / 4 - mStrokeWidth * 2;
                        oval23.right = w / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth / 4 + mStrokeWidth * 2;
                        oval23.top = h / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth / 4 - mStrokeWidth * 2;
                        oval23.bottom = h / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth / 4 + mStrokeWidth * 2;
                        canvas.drawArc(oval23, 0, 360, false, paint);
                        break;
                    case 3:
                        paint.setColor(color_1);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval3 = new RectF();
                        oval3.left = w / 2 - mRadioCircle;
                        oval3.right = w / 2 + mRadioCircle;
                        oval3.top = h / 2 - mRadioCircle;
                        oval3.bottom = h / 2 + mRadioCircle;
                        canvas.drawArc(oval3, 0, 360, false, paint);

                        paint.setColor(color_2);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval32 = new RectF();
                        oval32.left = w / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth;
                        oval32.right = w / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth;
                        oval32.top = h / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth;
                        oval32.bottom = h / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth;
                        canvas.drawArc(oval32, 0, 360, false, paint);

                        paint.setColor(color_3);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval33 = new RectF();
                        oval33.left = w / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth / 4 - mStrokeWidth * 2;
                        oval33.right = w / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth / 4 + mStrokeWidth * 2;
                        oval33.top = h / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth / 4 - mStrokeWidth * 2;
                        oval33.bottom = h / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth / 4 + mStrokeWidth * 2;
                        canvas.drawArc(oval33, 0, 360, false, paint);

                        paint.setColor(color_4);
                        paint.setStrokeWidth(mStrokeWidth / 4);
                        RectF oval34 = new RectF();
                        oval34.left = w / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth / 4 - mStrokeWidth / 4
                                - mStrokeWidth * 3;
                        oval34.right = w / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth / 4 + mStrokeWidth / 4
                                + mStrokeWidth * 3;
                        oval34.top = h / 2 - mRadioCircle - mStrokeWidth / 4
                                - mStrokeWidth / 4 - mStrokeWidth / 4
                                - mStrokeWidth * 3;
                        oval34.bottom = h / 2 + mRadioCircle + mStrokeWidth / 4
                                + mStrokeWidth / 4 + mStrokeWidth / 4
                                + mStrokeWidth * 3;
                        canvas.drawArc(oval34, 0, 360, false, paint);
                        break;
                }
                //              String msg = ctx.getString(R.string.bind_searching);
                int test_size = 25;
                Paint paint_text = new Paint();
                paint_text.setAntiAlias(true);
                paint_text.setTextSize(test_size);

                Rect r = new Rect();
                paint_text.getTextBounds(msg, 0, msg.length(), r);

                int w_msg = r.right - r.left;
                while (w_msg > mRadioCircle * 2)
                {
                    paint_text.setTextSize(--test_size);
                    paint_text.getTextBounds(msg, 0, msg.length(), r);
                    w_msg = r.right - r.left;
                }

                canvas.drawText(msg,
                        w / 2 - (r.right - r.left) / 2,
                        h / 2 + (r.bottom - r.top) / 2
                                - ScreenUtils.dip2px(ctx, 5),
                        paint_text);

                int test_size1 = 25;
                Paint paint_text1 = new Paint();
                paint_text1.setAntiAlias(true);
                paint_text1.setTextSize(test_size1);

                Rect r1 = new Rect();
                paint_text1.getTextBounds(msg1, 0, msg1.length(), r1);

                int w_msg1 = r1.right - r1.left;
                while (w_msg1 > mRadioCircle * 2)
                {
                    paint_text1.setTextSize(--test_size1);
                    paint_text1.getTextBounds(msg1, 0, msg1.length(), r1);
                    w_msg1 = r1.right - r1.left;
                }

                float txtSize = -paint_text1.ascent() + paint_text1.descent();
                float lineSpace = txtSize * 0.7f;
                canvas.drawText(msg1, w / 2 - (r1.right - r1.left) / 2, h / 2
                        + (r1.bottom - r1.top) / 2 + lineSpace, paint_text1);

            }

        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        finally
        {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
        return false;
    }

    @Override
    public void run()
    {

        while (true)
        {
            myDrawOneCircle();
            count_last++;
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (isStop)
            {
                break;
            }
        }
    }

    private static final int CONTINUE_DRAW_1 = 1;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(final android.os.Message msg)
        {
            switch (msg.what)
            {
                case CONTINUE_DRAW_1:
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                        }
                    }).start();

                    break;
            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // int x_every = 1;
        // int ra_every = 0;
        // int ra_every_last = 0;
        // int x_last = 0;
        int count_last = 0;
        // isFirstDraw = true;
        // 实例线程
        th = new Thread(this);
        // 启动线程
        th.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height)
    {
        TLog.Log("zxl---surfaceChanged---ondraw--->" + width + "--->" + height);
        w = width;
        h = height;
        mRadioCircle = w / 2 - mStrokeWidth * 6;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

}
