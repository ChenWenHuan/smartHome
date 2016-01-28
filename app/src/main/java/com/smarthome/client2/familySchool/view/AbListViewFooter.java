package com.smarthome.client2.familySchool.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smarthome.client2.R;

public class AbListViewFooter extends LinearLayout
{

    public final static int STATE_READY = 1;

    public final static int STATE_LOADING = 2;

    public final static int STATE_ALL = 3;

    public final static int STATE_EMPTY = 4;

    public final static int STATE_ERROR = 5;

    public final static int STATE_PULLING = 6;

    public final static int STATE_FINISH = 7;

    private Context mContext;

    private LinearLayout footerView;

    private ProgressBar footerProgressBar;

    private TextView footerTextView;

    /**
     * 初始默认高度
     */
    private int footerHeight;

    public AbListViewFooter(Context context)
    {
        super(context);
        initView(context);
    }

    public AbListViewFooter(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
        setState(STATE_EMPTY);
    }

    public void setState(int state)
    {
        footerView.setVisibility(View.VISIBLE);
        if (state == STATE_READY)
        {
            setVisiableHeight(0);
            footerTextView.setVisibility(View.GONE);
            footerProgressBar.setVisibility(View.GONE);
            footerTextView.setText(mContext.getString(R.string.pulllist_load_more));
        }
        else if (state == STATE_LOADING)
        {
            setVisiableHeight(footerHeight);
            footerTextView.setVisibility(View.VISIBLE);
            footerProgressBar.setVisibility(View.VISIBLE);
            footerTextView.setText(mContext.getString(R.string.is_loading));
        }
        else if (state == STATE_ALL)
        {
            setVisiableHeight(footerHeight);
            footerTextView.setVisibility(View.VISIBLE);
            footerProgressBar.setVisibility(View.GONE);
            footerTextView.setText(mContext.getString(R.string.pulllist_load_all));
        }
        else if (state == STATE_EMPTY)
        {
            setVisiableHeight(footerHeight);
            footerTextView.setVisibility(View.VISIBLE);
            footerProgressBar.setVisibility(View.GONE);
            footerTextView.setText(mContext.getString(R.string.pulllist_load_empty));
        }
        else if (state == STATE_PULLING)
        {
            footerTextView.setVisibility(View.VISIBLE);
            footerProgressBar.setVisibility(View.GONE);
            footerTextView.setText(mContext.getString(R.string.pulllist_load_more));
        }
        else if (state == STATE_FINISH)
        {
            setVisiableHeight(footerHeight);
            footerTextView.setVisibility(View.VISIBLE);
            footerProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 添加具体的加载信息
     * @param text
     */
    public void setState(String text)
    {
        footerView.setVisibility(View.VISIBLE);
        setVisiableHeight(footerHeight);
        footerTextView.setVisibility(View.VISIBLE);
        footerProgressBar.setVisibility(View.GONE);
        footerTextView.setText(text);
    }

    /**
     * 获取实际高度
     * @return the visiable height
     */
    public int getVisiableHeight()
    {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) footerView.getLayoutParams();
        return lp.height;
    }

    /**
     * 隐藏footerView.
     */
    public void hide()
    {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) footerView.getLayoutParams();
        lp.height = 0;
        footerView.setLayoutParams(lp);
        footerView.setVisibility(View.GONE);
    }

    /**
     * 显示footerView.
     */
    public void show()
    {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) footerView.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        footerView.setLayoutParams(lp);
        footerView.setVisibility(View.INVISIBLE);
    }

    private void initView(Context context)
    {
        mContext = context;
        // 底部刷新
        footerView = new LinearLayout(context);
        // 设置布局 水平方向
        footerView.setOrientation(LinearLayout.HORIZONTAL);
        footerView.setGravity(Gravity.CENTER);
        setBackgroundColor(getResources().getColor(R.color.list_foot_bg));

        footerTextView = new TextView(context);
        footerTextView.setGravity(Gravity.CENTER_VERTICAL);
        setTextColor(getResources().getColor(R.color.list_foot_text));
        footerTextView.setTextSize(15);
        footerTextView.setMinimumHeight(70);
        footerView.setPadding(0, 10, 0, 10);

        footerProgressBar = new ProgressBar(context, null,
                android.R.attr.progressBarStyle);
        footerProgressBar.setVisibility(View.GONE);

        LinearLayout.LayoutParams layoutParamsWW = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsWW.gravity = Gravity.CENTER;
        layoutParamsWW.width = 50;
        layoutParamsWW.height = 50;
        layoutParamsWW.rightMargin = 10;
        footerView.addView(footerProgressBar, layoutParamsWW);

        LinearLayout.LayoutParams layoutParamsWW1 = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        footerView.addView(footerTextView, layoutParamsWW1);

        LinearLayout.LayoutParams layoutParamsFW = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(footerView, layoutParamsFW);

        // 获取View的高度
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        this.measure(w, h);
        footerHeight = this.getMeasuredHeight();

    }

    /**
     * 描述：设置字体颜色
     * @param color
     * @throws
     */
    public void setTextColor(int color)
    {
        footerTextView.setTextColor(color);
    }

    /**
     * 描述：设置背景颜色
     * @param color
     * @throws
     */
    public void setBackgroundColor(int color)
    {
        footerView.setBackgroundColor(color);
    }

    /**
     * 描述：获取Footer ProgressBar，用于设置自定义样式
     * @return
     * @throws
     */
    public ProgressBar getFooterProgressBar()
    {
        return footerProgressBar;
    }

    /**
     * 描述：设置Footer ProgressBar样式
     * @return
     * @throws
     */
    public void setFooterProgressBarDrawable(Drawable indeterminateDrawable)
    {
        footerProgressBar.setIndeterminateDrawable(indeterminateDrawable);
    }

    /**
     * 描述：获取高度
     * @return
     * @throws
     */
    public int getFooterHeight()
    {
        return footerHeight;
    }

    /**
     * 设置高度.
     * @param height
     *            新的高度
     */
    public void setVisiableHeight(int height)
    {
        if (height < 0)
            height = 0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) footerView.getLayoutParams();
        lp.height = height;
        footerView.setLayoutParams(lp);
    }

}
