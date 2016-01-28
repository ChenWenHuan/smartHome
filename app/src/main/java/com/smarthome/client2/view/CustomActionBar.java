package com.smarthome.client2.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.util.MySoftInputUtil;

public class CustomActionBar extends FrameLayout
{
    private ImageView iv_left_icon, iv_left_cancle,iv_arrow_calendar;

    private AutoDownloadImageView iv_right_icon, iv_right_head;

    private TextView tv_title, tv_right, tv_calendar_title;

    private LinearLayout ll_title, ll_middle_content, ll_calendar_title;

    private Context ctx;

    public EditText editTextMiddle;

    private LinearLayout mHealthyContent;

    private ImageButton mHealthyCalendarLeft;

    private ImageButton mHealthyCalendarRight;

    private TextView mHealthyCalendarTv;

    private Calendar mCalendar;

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CustomActionBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.ctx = context;
        init();
    }

    public CustomActionBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.ctx = context;
        init();
    }

    public CustomActionBar(Context context)
    {
        super(context);
        this.ctx = context;
        init();
    }

    private void init()
    {
        FrameLayout head_view = (FrameLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.custom_action_bar, this);
        ll_title = (LinearLayout) head_view.findViewById(R.id.ll_title);
        ll_middle_content = (LinearLayout) head_view.findViewById(R.id.ll_middle_content);
        iv_left_icon = (ImageView) head_view.findViewById(R.id.iv_left_icon);
        iv_left_cancle = (ImageView) head_view.findViewById(R.id.iv_left_cancle);
        iv_right_icon = (AutoDownloadImageView) head_view.findViewById(R.id.iv_right_icon);
        iv_right_head = (AutoDownloadImageView) head_view.findViewById(R.id.iv_right_head);
        tv_title = (TextView) head_view.findViewById(R.id.tv_title);
        tv_right = (TextView) head_view.findViewById(R.id.tv_right);
        tv_right.setTextColor(getResources().getColor(R.color.white));
        editTextMiddle = (EditText) head_view.findViewById(R.id.middle_edit);
        tv_calendar_title = (TextView) head_view.findViewById(R.id.tv_calendar_title);
        ll_calendar_title = (LinearLayout) head_view.findViewById(R.id.ll_calendar_title);
        iv_arrow_calendar = (ImageView) head_view.findViewById(R.id.iv_arrow_calendar);
        mHealthyContent = (LinearLayout) head_view.findViewById(R.id.ll_healthy_content);
        mHealthyCalendarLeft = (ImageButton) head_view.findViewById(R.id.ll_healthy_calendarLeft);
        mHealthyCalendarRight = (ImageButton) head_view.findViewById(R.id.ll_healthy_calendarRight);
        mHealthyCalendarTv = (TextView) head_view.findViewById(R.id.ll_healthy_calendar);
        mHealthyCalendarLeft.setOnClickListener(healthyCalendarListener);
        mHealthyCalendarRight.setOnClickListener(healthyCalendarListener);

        editTextMiddle.setOnClickListener(editListener);
    }

    public void setHealthyCalendarTv(String title)
    {
        mHealthyCalendarTv.setText(title);
    }

    private OnClickListener healthyCalendarListener = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            if (mCalendar == null)
            {
                mCalendar = Calendar.getInstance();
            }
            switch (v.getId())
            {
                case R.id.ll_healthy_calendarLeft:
                    mCalendar.add(Calendar.DAY_OF_YEAR, -1);
                    mHealthyCalendarTv.setText(mFormat.format(mCalendar.getTime()));
                    mCalendarListener.getCurrentData(mCalendar.getTime());
                    break;
                case R.id.ll_healthy_calendarRight:
                    mCalendar.add(Calendar.DAY_OF_YEAR, 1);
                    mHealthyCalendarTv.setText(mFormat.format(mCalendar.getTime()));
                    mCalendarListener.getCurrentData(mCalendar.getTime());
                    break;
            }
        }
    };

    private OnClickListener editListener = new OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            editTextMiddle.setFocusable(true);
            editTextMiddle.setFocusableInTouchMode(true);
            editTextMiddle.requestFocus();
            editTextMiddle.setText("");
            MySoftInputUtil.showInputMethod(ctx, editTextMiddle);
        }
    };

    public void setIvLeftCancleIcon(int resId)
    {
        iv_left_cancle.setImageResource(resId);
    }

    public void setIvLeftIcon(int resId)
    {
        iv_left_icon.setImageResource(resId);
    }

    public void setIvLeftIconInvisiable() {
        iv_left_icon.setVisibility(View.INVISIBLE);
    }

    public void setIvRightIconInvisiable() {
        iv_right_icon.setVisibility(View.INVISIBLE);

    }

    public void setIvLeftIconPosition()
    {
        android.view.ViewGroup.LayoutParams lp = iv_left_icon.getLayoutParams();
        lp.width = 48;
        lp.height = 48;
        iv_left_icon.setLayoutParams(lp);
    }

    public void setIvRightHead(int resId)
    {
        iv_right_head.setImageResource(resId);
    }

    public void setIvRightHead(Bitmap bm)
    {
        iv_right_head.setImageBitmap(bm);
    }

    public void setIvRightIcon(int resId)
    {
        if (resId == -1)
        {
            iv_right_icon.setBackgroundColor(getResources().getColor(R.color.font_blue));
        }
        else
        {
            iv_right_icon.setImageResource(resId);
        }
    }

    public void setIvRightIcon(Bitmap bm)
    {
        iv_right_icon.setImageBitmap(bm);
    }

    public void setTvCalendarTitleMsg(String s)
    {
        if (!TextUtils.isEmpty(s))
        {
            tv_calendar_title.setText(s);
            tv_calendar_title.setVisibility(View.VISIBLE);
            ll_calendar_title.setVisibility(View.VISIBLE);
        }
    }

    public void setTvTitleMsg(String s)
    {
        if (!TextUtils.isEmpty(s))
        {
            tv_title.setText(s);
            tv_title.setVisibility(View.VISIBLE);
            ll_title.setVisibility(View.VISIBLE);
        }
    }

    public void setTvRightMsg(String s)
    {
        if (!TextUtils.isEmpty(s))
        {
            tv_right.setText(s);
            tv_right.setVisibility(View.VISIBLE);
        }
    }

    public void setIvLeftCancleIconListener(OnClickListener clickListener)
    {
        if (clickListener != null)
        {
            iv_left_cancle.setOnClickListener(clickListener);
            iv_left_cancle.setVisibility(View.VISIBLE);
        }
    }

    public void setIvLeftIconListener(OnClickListener clickListener)
    {
        if (clickListener != null)
        {
            iv_left_icon.setOnClickListener(clickListener);
            iv_left_icon.setVisibility(View.VISIBLE);
        }
    }

    public void setIvRightIconListener(OnClickListener clickListener)
    {
        if (clickListener != null)
        {
            iv_right_icon.setOnClickListener(clickListener);
            iv_right_icon.setVisibility(View.VISIBLE);
        }
    }

    public void setIvRightHeadListener(OnClickListener clickListener)
    {
        if (clickListener != null)
        {
            iv_right_head.setOnClickListener(clickListener);
            iv_right_head.setVisibility(View.VISIBLE);
        }
    }

    public void setIvTvListener(OnClickListener clickListener)
    {
        if (clickListener != null)
        {
            tv_title.setOnClickListener(clickListener);
            tv_title.setVisibility(View.VISIBLE);
        }
    }

    public void setTvRightListener(OnClickListener clickListener)
    {
        if (clickListener != null)
        {
            tv_right.setOnClickListener(clickListener);
            tv_right.setVisibility(View.VISIBLE);
        }
    }

    public View getIvLeftIcon()
    {
        if (iv_left_icon.getVisibility() == View.VISIBLE)
        {
            return iv_left_icon;
        }
        else
        {
            throw new IllegalArgumentException("当前左侧没有显示任何View");
        }
    }

    public AutoDownloadImageView getIvRightHead()
    {
        if (iv_right_head.getVisibility() == View.VISIBLE)
        {
            return iv_right_head;
        }
        else
        {
            throw new IllegalArgumentException("当前右侧没有显示任何View");
        }
    }

    public AutoDownloadImageView getIvRightIcon()
    {
        if (iv_right_icon.getVisibility() == View.VISIBLE)
        {
            return iv_right_icon;
        }
        else
        {
            throw new IllegalArgumentException("当前右侧没有显示任何View");
        }
    }

    public LinearLayout getLLCalendarTitle()
    {
        if (ll_calendar_title.getVisibility() == View.VISIBLE)
        {
            return ll_calendar_title;
        }
        else
        {
            throw new IllegalArgumentException("当前中间没有显示任何View");
        }
    }

    public TextView getTvCalendarTitle()
    {
        if (ll_calendar_title.getVisibility() == View.VISIBLE
                && tv_calendar_title.getVisibility() == View.VISIBLE)
        {
            return tv_calendar_title;
        }
        else
        {
            throw new IllegalArgumentException("当前中间没有显示任何View");
        }
    }

    public ImageView getIvCalendarArrow()
    {
            return iv_arrow_calendar;
    }

    public TextView getTvTitle()
    {
        if (tv_title.getVisibility() == View.VISIBLE)
        {
            return tv_title;
        }
        else
        {
            throw new IllegalArgumentException("当前中间没有显示任何View");
        }
    }

    public TextView getTvRihgt()
    {
        if (tv_right.getVisibility() == View.VISIBLE)
        {
            return tv_right;
        }
        else
        {
            throw new IllegalArgumentException("当前右边没有显示任何View");
        }
    }

    public LinearLayout getLLTitle()
    {
        if (ll_title.getVisibility() == View.VISIBLE)
        {
            return ll_title;
        }
        else
        {
            throw new IllegalArgumentException("当前中间没有显示任何View");
        }
    }

    public void showLLMiddleContent()
    {
        ll_middle_content.setVisibility(View.VISIBLE);
    }

    public LinearLayout getLLMiddleContent()
    {
        if (ll_middle_content.getVisibility() == View.VISIBLE)
        {
            return ll_middle_content;
        }
        else
        {
            throw new IllegalArgumentException("当前中间没有显示任何View");
        }
    }

    public void setSearcheEnable(Boolean enable)
    {
        if (enable)
        {
            ll_middle_content.setVisibility(View.VISIBLE);
            editTextMiddle.setVisibility(View.VISIBLE);
            tv_right.setTextColor(getResources().getColor(R.color.Gray));
            editTextMiddle.setFocusable(false);
            editTextMiddle.setFocusableInTouchMode(false);
            editTextMiddle.requestFocus();
            /*          EditText search_content = new EditText(this.ctx);
                        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, ll_middle_content.getHeight()-20);
                        lp.setMargins(5, -10, 5, -10);
                        search_content.setHeight(ll_middle_content.getHeight()-20);
                        search_content.setLayoutParams(lp);
                        search_content.setBackgroundResource(R.drawable.bg_menu_item_up_normal);
                        ll_middle_content.addView(search_content);*/
        }
        else
        {
            ll_middle_content.setVisibility(View.GONE);
        }
    }

    public String getSearchContent()
    {
        return editTextMiddle.getText().toString();
    }

    public void setHealthyCalendarEnable(Boolean enable)
    {
        if (enable)
        {
            mHealthyContent.setVisibility(View.VISIBLE);
        }
        else
        {
            mHealthyContent.setVisibility(View.GONE);
        }
    }

    public TextView getHealthyContent()
    {
        if (mHealthyContent.getVisibility() == View.VISIBLE)
        {
            return mHealthyCalendarTv;
        }
        else
        {
            throw new IllegalArgumentException("当前中间没有显示任何View");
        }
    }
    
    public interface CalendarListener
    {
        void getCurrentData(Date date);
    }

    private CalendarListener mCalendarListener;

    public void setHealthyCalendarListener(CalendarListener calendarListener)
    {
        this.mCalendarListener = calendarListener;
    }

}
