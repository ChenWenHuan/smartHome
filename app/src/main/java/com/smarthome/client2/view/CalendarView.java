package com.smarthome.client2.view;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 日历控件 功能：获得点选的日期区间
 */
public class CalendarView extends View implements View.OnTouchListener
{

    public Date curDate; // 切换月份时展现的临时日期

    public Date clickedDate;// 之前点击选中的有效日期

    public Date todayDate; // 今天

    private Date downDate; // 手指按下状态时临时日期

    private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期

    private int downIndex; // 按下的格子索引

    private Calendar calendar;

    private Surface surface;

    private int[] date = new int[42];

    private int curStartIndex, curEndIndex; // 当前选中月起始日期的索引、下个月起始日期的索引

    private OnItemClickListener onItemClickListener;//给控件设置监听事件

    public CalendarView(Context context)
    {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        clickedDate = curDate = todayDate = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        surface = new Surface();
        surface.density = getResources().getDisplayMetrics().density;
        setBackgroundColor(surface.bgColor);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Log.e("date", "onMeasure");
        surface.width = getResources().getDisplayMetrics().widthPixels;
        surface.height = getResources().getDisplayMetrics().heightPixels * 2 / 5;
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width,
                View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height,
                View.MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom)
    {
        Log.e("date", "onLayout");
        if (changed)
        {
            surface.init();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Log.e("date", "onDraw");
        // 画框
        // canvas.drawPath(surface.boxPath, surface.borderPaint);
        // 星期
        float weekTextY = surface.weekHeight * 3 / 4f;
        for (int i = 0; i < 7; i++)
        {
            float weekTextX = i
                    * surface.cellWidth
                    + (surface.cellWidth - surface.weekPaint.measureText(surface.weekText[i]))
                    / 2f;
            canvas.drawText(surface.weekText[i],
                    weekTextX,
                    weekTextY,
                    surface.weekPaint);
        }
        // 计算日期
        calculateDate();
        // 按下状态，选择状态背景色
        drawDownOrSelectedBg(canvas);
        // 今天日期飘红
        int todayIndex = -1;
        if (todayDate.after(showFirstDate) && todayDate.before(showLastDate))
        {
            calendar.setTime(todayDate);
            int todayMonth = calendar.get(Calendar.MONTH);
            int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
            if(curDate != null) {
                calendar.setTime(curDate);
            }
            int curMonth = calendar.get(Calendar.MONTH);
            if (todayMonth == curMonth)
            {
                todayIndex = findSelectedIndex(curStartIndex,
                        curEndIndex - 1,
                        todayDay);
            }
            else if (todayDate.before(curDate))
            {
                todayIndex = findSelectedIndex(0, curStartIndex - 1, todayDay);
            }
            else
            {
                todayIndex = findSelectedIndex(curEndIndex, 41, todayDay);
            }
        }

        for (int i = 0; i < 42; i++)
        {
            int color = surface.textColor;
            if (isLastMonth(i))
            {
                color = surface.borderColor;
            }
            else if (isNextMonth(i))
            {
                color = surface.borderColor;
            }
            if (todayIndex != -1 && i == todayIndex)
            {
                color = surface.todayNumberColor;
            }
            drawCellText(canvas, i, date[i] + "", color);
        }
        super.onDraw(canvas);
    }

    private void calculateDate()
    {
        if(curDate != null) {
            calendar.setTime(curDate);
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int monthStart = dayInWeek;
        if (monthStart == 1)
        {
            monthStart = 8;
        }
        monthStart -= 1; //以日为开头-1，以星期一为开头-2
        curStartIndex = monthStart;
        date[monthStart] = 1;
        // last month
        if (monthStart > 0)
        {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--)
            {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        showFirstDate = calendar.getTime();
        // this month
        if(curDate != null) {
            calendar.setTime(curDate);
        }
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < monthDay; i++)
        {
            date[monthStart + i] = i + 1;
        }
        curEndIndex = monthStart + monthDay;
        // next month
        for (int i = monthStart + monthDay; i < 42; i++)
        {
            date[i] = i - (monthStart + monthDay) + 1;
        }
        if (curEndIndex < 42)
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, date[41]);
        showLastDate = calendar.getTime();
    }

    /**
     * 
     * @param canvas
     * @param index
     * @param text
     */
    private void drawCellText(Canvas canvas, int index, String text, int color)
    {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.datePaint.setColor(color);
        float cellY = surface.weekHeight + (y - 1) * surface.cellHeight
                + surface.cellHeight * 3 / 4f;
        float cellX = (surface.cellWidth * (x - 1))
                + (surface.cellWidth - surface.datePaint.measureText(text))
                / 2f;
        canvas.drawText(text, cellX, cellY, surface.datePaint);
    }

    /**
     * 
     * @param canvas
     * @param index
     * @param color
     */
    private void drawCellBg(Canvas canvas, int index, int color)
    {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.cellBgPaint.setColor(color);
        float left = surface.cellWidth * (x - 1) + surface.borderWidth;
        float top = surface.weekHeight + (y - 1) * surface.cellHeight
                + surface.borderWidth;
        canvas.drawRect(left, top, left + surface.cellWidth
                - surface.borderWidth, top + surface.cellHeight
                - surface.borderWidth, surface.cellBgPaint);
    }

    private void drawDownOrSelectedBg(Canvas canvas)
    {
        // down and not up
        if (downDate != null)
        {
            drawCellBg(canvas, downIndex, surface.cellDownColor);
        }
        // selected bg color
        int selectedIndex = -1;
        if (clickedDate == null) {
            clickedDate = new Date();
        }
        if (clickedDate.after(showFirstDate)
                && clickedDate.before(showLastDate))
        {
            calendar.setTime(clickedDate);
            int preMonth = calendar.get(Calendar.MONTH);
            int preDay = calendar.get(Calendar.DAY_OF_MONTH);
            if(curDate != null) {
                calendar.setTime(curDate);
            }
            int curMonth = calendar.get(Calendar.MONTH);
            if (preMonth == curMonth)
            {
                selectedIndex = findSelectedIndex(curStartIndex,
                        curEndIndex - 1,
                        preDay);
            }
            else if (todayDate.before(curDate))
            {
                selectedIndex = findSelectedIndex(0, curStartIndex - 1, preDay);
            }
            else
            {
                selectedIndex = findSelectedIndex(curEndIndex, 41, preDay);
            }
        }
        if (selectedIndex != -1)
        {
            drawCellBg(canvas, selectedIndex, surface.cellSelectedColor);
        }
    }

    private int findSelectedIndex(int start, int end, int day)
    {
        for (int i = start; i <= end; i++)
        {
            if (date[i] == day)
            {
                return i;
            }
        }
        return -1;
    }

    private boolean isLastMonth(int i)
    {
        return i < curStartIndex;
    }

    private boolean isNextMonth(int i)
    {
        return i >= curEndIndex;
    }

    private int getXByIndex(int i)
    {
        return i % 7 + 1; // 1 2 3 4 5 6 7
    }

    private int getYByIndex(int i)
    {
        return i / 7 + 1; // 1 2 3 4 5 6
    }

    // 获得当前应该显示的年月
    public String getYearAndmonth()
    {
        if(curDate != null) {
            calendar.setTime(curDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        return year + "-" + surface.monthText[month];
    }

    public String getFormatDate()
    {
        if (curDate.after(todayDate))
        {
            return null;
        }
        if(curDate != null) {
            calendar.setTime(curDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayStr;
        if (day < 10)
        {
            dayStr = "0" + day;
        }
        else
        {
            dayStr = "" + day;
        }
        return year + "-" + surface.monthText[month] + "-" + dayStr;
    }

    public String getFormatLocDate()
    {
        if (curDate != null) {
            calendar.setTime(curDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayStr;
        if (day < 10)
        {
            dayStr = "0" + day;
        }
        else
        {
            dayStr = "" + day;
        }
        return year + "-" + surface.monthText[month] + "-" + dayStr;
    }

    public void backRightDate()
    {
        curDate = clickedDate;
    }

    public void backClickedDate()
    {
        clickedDate = curDate;
    }

    //上一月
    public String clickLeftMonth()
    {
        if (curDate != null) {
            calendar.setTime(curDate);
        }
        calendar.add(Calendar.MONTH, -1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    //下一月
    public String clickRightMonth()
    {
        if (curDate != null) {
            calendar.setTime(curDate);
        }
        calendar.add(Calendar.MONTH, 1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    private void setSelectedDateByCoor(float x, float y)
    {
        if (y > surface.weekHeight)
        {
            int m = (int) (Math.floor(x / surface.cellWidth) + 1);
            int n = (int) (Math.floor((y - surface.weekHeight)
                    / Float.valueOf(surface.cellHeight)) + 1);
            downIndex = (n - 1) * 7 + m - 1;
            if (curDate != null) {
                calendar.setTime(curDate);
            }
            if (isLastMonth(downIndex))
            {
                calendar.add(Calendar.MONTH, -1);
            }
            else if (isNextMonth(downIndex))
            {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = calendar.getTime();
            curDate = calendar.getTime();
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (downDate != null)
                {
                    downDate = null;
                    if (!curDate.after(todayDate))
                    {
                        clickedDate = curDate;
                    }
                    invalidate();
                    // 响应监听事件
                    onItemClickListener.OnItemClick(clickedDate);
                }
                break;
        }
        return true;
    }

    //给控件设置监听事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    //监听接口
    public interface OnItemClickListener
    {
        void OnItemClick(Date date);
    }

    private class Surface
    {
        public float density;

        public int width; // 整个控件的宽度

        public int height; // 整个控件的高度

        public float weekHeight; // 星期方框的高度

        public float cellWidth; // 日方框宽度

        public float cellHeight; // 日方框高度	

        public float borderWidth;// 方框边界线宽度

        public int bgColor = Color.parseColor("#4c9ada");

        private int textColor = Color.WHITE;

        private int borderColor = Color.parseColor("#257bc4");

        public int todayNumberColor = Color.RED;

        public int cellDownColor = Color.parseColor("#CCFFFF");

        public int cellSelectedColor = Color.parseColor("#4682b4");

        public Paint borderPaint;

        public Paint weekPaint;// 周文字画笔

        public Paint datePaint;// 日文字画笔 

        public Paint cellBgPaint;// 日背景画笔

        public Path boxPath; // 边框路径

        public String[] weekText = { "日", "一", "二", "三", "四", "五", "六" };

        public String[] monthText = { "01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12" };

        public void init()
        {
            weekHeight = height / 7f;
            cellHeight = weekHeight;
            cellWidth = width / 7f;
            borderPaint = new Paint();
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderWidth = (float) (0.5 * density);
            borderWidth = borderWidth < 1 ? 1 : borderWidth;
            borderPaint.setStrokeWidth(borderWidth);
            weekPaint = new Paint();
            weekPaint.setColor(textColor);
            weekPaint.setAntiAlias(true);
            float weekTextSize = weekHeight * 0.6f;
            weekPaint.setTextSize(weekTextSize);
            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
            datePaint = new Paint();
            datePaint.setColor(textColor);
            datePaint.setAntiAlias(true);
            float cellTextSize = cellHeight * 0.5f;
            datePaint.setTextSize(cellTextSize);
            datePaint.setTypeface(Typeface.DEFAULT_BOLD);
            boxPath = new Path();
            boxPath.rLineTo(width, 0);
            boxPath.moveTo(0, weekHeight);
            boxPath.rLineTo(width, 0);
            for (int i = 1; i < 6; i++)
            {
                boxPath.moveTo(0, weekHeight + i * cellHeight);
                boxPath.rLineTo(width, 0);
                boxPath.moveTo(i * cellWidth, 0);
                boxPath.rLineTo(0, height);
            }
            boxPath.moveTo(6 * cellWidth, 0);
            boxPath.rLineTo(0, height - 0);
            cellBgPaint = new Paint();
            cellBgPaint.setAntiAlias(true);
            cellBgPaint.setStyle(Paint.Style.FILL);
            cellBgPaint.setColor(cellSelectedColor);
        }
    }
}