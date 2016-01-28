package com.smarthome.client2.familySchool.view;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.model.Syllabus;

import android.content.Context;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * @author n003913 显示课程表
 *
 */
public class MyGridLayout extends GridLayout
{

    private final int columnW;// 内容单元格宽度

    private final int rowH;// 内容单元格高度

    private final int titleH;// 表头单元格(第一行)高度

    private final int titleW;// 表头单元格(第一列)宽度

    private final int dividerH;// 分割线高度

    private final int dividerW;// 分割线宽度

    private final int paddingL;// 左边距

    private final int paddingR;// 右边距

    public MyGridLayout(Context context)
    {
        super(context);
        setBackgroundColor(getResources().getColor(R.color.white));
        int screenW = Preferences.getInstance(context).getScreenW();
        if (screenW < 720)
        {
            // columnW = 84;
            // dividerW = 672;
            titleH = 90;
            titleW = 50;
            rowH = 70;
            paddingL = 2;
            paddingR = 2;
            dividerH = 10;
            columnW = (int) ((screenW - 30 - titleW) / 7.0);
        }
        else
        {
            titleH = 132;
            titleW = 84;
            rowH = 94;
            paddingL = 15;
            paddingR = 15;
            dividerH = 20;
            // columnW = (int) ((screenW - 30 - 84)/7.0);
            // dividerW = screenW - paddingL - paddingR;
            columnW = (int) ((screenW - paddingL - paddingR - titleW) / 7.0);
        }
        dividerW = screenW - paddingL - paddingR;
        setPadding(paddingL, 0, paddingR, 0);
    }

    /**
     * 添加课程表布局
     * 
     * @param syllabus
     */
    public void addSyllabus(Syllabus syllabus)
    {
        Context context = getContext();
        int beforeAm = syllabus.getBeforeAm();
        int amNum = syllabus.getAmNum();
        int pmNum = syllabus.getPmNum();
        int afterPm = syllabus.getAfterPm();
        int rowNow = 0;
        TextView textView = null;
        GridLayout.LayoutParams params = null;

        /*
         * 第一行，显示周几
         */
        // 表头
        textView = new TextView(context);
        textView.setWidth(titleW);
        textView.setHeight(titleH);
        textView.setText("");
        textView.setGravity(Gravity.CENTER);
        // textView.setBackgroundResource(R.drawable.shape_square_deep);
        params = new LayoutParams();
        params.rowSpec = GridLayout.spec(rowNow, TOP);
        params.columnSpec = GridLayout.spec(0);
        addView(textView, params);

        for (int i = 1; i <= 7; i++)
        {
            textView = new TextView(context);
            textView.setWidth(columnW);
            textView.setHeight(titleH);
            textView.setText(getWeekday(i));
            textView.setTextColor(getResources().getColor(R.color.syllabus_text_light));
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER);
            // textView.setBackgroundResource(R.drawable.shape_square_deep);
            params = new LayoutParams();
            params.rowSpec = GridLayout.spec(rowNow, TOP);
            params.columnSpec = GridLayout.spec(i);
            addView(textView, params);
        }
        rowNow++;

        /*
         * 有早自习
         */
        if (beforeAm != 0)
        {
            textView = new TextView(context);
            textView.setWidth(titleW);
            textView.setHeight(rowH * beforeAm);
            textView.setText("早");
            textView.setTextSize(15);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setGravity(Gravity.CENTER);
            // textView.setBackgroundResource(R.drawable.shape_square_deep);
            textView.setBackgroundResource(R.color.chocolate);
            params = new LayoutParams();
            params.rowSpec = GridLayout.spec(rowNow, beforeAm, FILL);
            params.columnSpec = GridLayout.spec(0);
            addView(textView, params);

            for (int i = 1; i <= beforeAm; i++)
            {
                for (int j = 1; j <= 7; j++)
                {
                    textView = new TextView(context);
                    textView.setWidth(columnW);
                    textView.setHeight(rowH);
                    textView.setText(syllabus.getPositionSubject(0, i, j));
                    textView.setTextColor(getResources().getColor(R.color.class_circle_text_deep));
                    textView.setTextSize(15);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundResource(BackgroundResource(j,
                            i,
                            beforeAm));
                    params = new LayoutParams();
                    params.rowSpec = GridLayout.spec(rowNow, TOP);
                    params.columnSpec = GridLayout.spec(j);
                    addView(textView, params);
                }
                rowNow++;
            }

        }

        /*
         * 有上午
         */
        if (amNum != 0)
        {
            // 判断是否加分割线
            if (beforeAm != 0)
            {
                textView = new TextView(context);
                textView.setWidth(dividerW);
                textView.setHeight(dividerH);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.color.white);
                params = new LayoutParams();
                params.rowSpec = GridLayout.spec(rowNow, TOP);
                params.columnSpec = GridLayout.spec(0, 8);
                addView(textView, params);
                rowNow++;
            }

            textView = new TextView(context);
            textView.setWidth(titleW);
            textView.setHeight(rowH * amNum);
            textView.setText("上\n\n午");
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(15);
            textView.setTextColor(getResources().getColor(R.color.white));
            // textView.setBackgroundResource(R.drawable.shape_square_deep);
            textView.setBackgroundResource(R.color.syllabus_yellow);
            params = new LayoutParams();
            params.rowSpec = GridLayout.spec(rowNow, amNum, FILL);
            params.columnSpec = GridLayout.spec(0);
            addView(textView, params);

            for (int i = 1; i <= amNum; i++)
            {
                for (int j = 1; j <= 7; j++)
                {
                    textView = new TextView(context);
                    textView.setWidth(columnW);
                    textView.setHeight(rowH);
                    textView.setText(syllabus.getPositionSubject(1, i, j));
                    textView.setTextColor(getResources().getColor(R.color.class_circle_text_deep));
                    textView.setTextSize(15);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundResource(BackgroundResource(j,
                            i,
                            amNum));
                    params = new LayoutParams();
                    params.rowSpec = GridLayout.spec(rowNow, TOP);
                    params.columnSpec = GridLayout.spec(j);
                    addView(textView, params);
                }
                rowNow++;
            }
        }

        /*
         * 有下午
         */
        if (pmNum != 0)
        {
            // 判断是否加分割线
            if (beforeAm + amNum != 0)
            {
                textView = new TextView(context);
                textView.setWidth(dividerW);
                textView.setHeight(dividerH);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.color.white);
                params = new LayoutParams();
                params.rowSpec = GridLayout.spec(rowNow, TOP);
                params.columnSpec = GridLayout.spec(0, 8);
                addView(textView, params);
                rowNow++;
            }

            textView = new TextView(context);
            textView.setWidth(titleW);
            textView.setHeight(rowH * pmNum);
            textView.setText("下\n\n午");
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(15);
            textView.setTextColor(getResources().getColor(R.color.white));
            // textView.setBackgroundResource(R.drawable.shape_square_deep);
            textView.setBackgroundResource(R.color.syllabus_green);
            params = new LayoutParams();
            params.rowSpec = GridLayout.spec(rowNow, pmNum, FILL);
            params.columnSpec = GridLayout.spec(0);
            addView(textView, params);

            for (int i = 1; i <= pmNum; i++)
            {
                for (int j = 1; j <= 7; j++)
                {
                    textView = new TextView(context);
                    textView.setWidth(columnW);
                    textView.setHeight(rowH);
                    textView.setText(syllabus.getPositionSubject(2, i, j));
                    textView.setTextColor(getResources().getColor(R.color.class_circle_text_deep));
                    textView.setTextSize(15);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundResource(BackgroundResource(j,
                            i,
                            pmNum));
                    params = new LayoutParams();
                    params.rowSpec = GridLayout.spec(rowNow, TOP);
                    params.columnSpec = GridLayout.spec(j);
                    addView(textView, params);
                }
                rowNow++;
            }
        }

        /*
         * 有晚自习
         */
        if (afterPm != 0)
        {
            // 判断是否加分割线
            if (beforeAm + amNum + pmNum != 0)
            {
                textView = new TextView(context);
                textView.setWidth(dividerW);
                textView.setHeight(dividerH);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.color.white);
                params = new LayoutParams();
                params.rowSpec = GridLayout.spec(rowNow, TOP);
                params.columnSpec = GridLayout.spec(0, 8);
                addView(textView, params);
                rowNow++;
            }

            textView = new TextView(context);
            textView.setWidth(titleW);
            textView.setHeight(rowH * afterPm);
            textView.setText("晚");
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(15);
            textView.setTextColor(getResources().getColor(R.color.white));
            // textView.setBackgroundResource(R.drawable.shape_square_deep);
            textView.setBackgroundResource(R.color.deepskyblue);
            params = new LayoutParams();
            params.rowSpec = GridLayout.spec(rowNow, afterPm, FILL);
            params.columnSpec = GridLayout.spec(0);
            addView(textView, params);

            for (int i = 1; i <= afterPm; i++)
            {
                for (int j = 1; j <= 7; j++)
                {
                    textView = new TextView(context);
                    textView.setWidth(columnW);
                    textView.setHeight(rowH);
                    textView.setText(syllabus.getPositionSubject(3, i, j));
                    textView.setTextColor(getResources().getColor(R.color.class_circle_text_deep));
                    textView.setTextSize(15);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundResource(BackgroundResource(j,
                            i,
                            afterPm));
                    params = new LayoutParams();
                    params.rowSpec = GridLayout.spec(rowNow, TOP);
                    params.columnSpec = GridLayout.spec(j);
                    addView(textView, params);
                }
                rowNow++;
            }
        }
    }

    private String getWeekday(int weekday)
    {
        switch (weekday)
        {
            case 1:
                return "周\n一";
            case 2:
                return "周\n二";
            case 3:
                return "周\n三";
            case 4:
                return "周\n四";
            case 5:
                return "周\n五";
            case 6:
                return "周\n六";
            case 7:
                return "周\n日";
        }
        return "";
    }

    /**
     * 获取背景资源id
     * 
     * @param weekday 周几
     * @param sort 某个时间段的第几节，如上午第2节
     * @param total 某个时间段的总节数，如上午总共4节
     * @return
     */
    private int BackgroundResource(int weekday, int sort, int total)
    {
        if (sort < total)
        {
            if (weekday < 7)
            {
                return R.drawable.syllabus_lt;
            }
            else
            {
                return R.drawable.syllabus_lrt;
            }
        }
        else
        {
            if (weekday < 7)
            {
                return R.drawable.syllabus_ltb;
            }
            else
            {
                return R.drawable.syllabus_lrtb;
            }
        }
    }

}
