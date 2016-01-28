package com.smarthome.client2.util;

import android.content.Context;
import android.view.View.OnClickListener;

import com.smarthome.client2.view.CustomActionBar;

public class TopBarUtils
{

    /**
     *
     * @param context
     * @param left_iv_resId 左边图片
     * @param left_iv_ClickListener 左边imageview点击事件
     * @param middle_title 中间文字信息
     * @param right_iv_resId 右边图片
     * @param right_iv_ClickListener 右边imageview
     * @return
     */
    public static CustomActionBar createCustomActionBar(Context context,
            int left_iv_resId, OnClickListener left_iv_ClickListener,
            String middle_title, int right_iv_resId,
            OnClickListener right_iv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setTvTitleMsg(middle_title);
        actionBar.setIvRightIcon(right_iv_resId);
        actionBar.setIvRightIconListener(right_iv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    public static CustomActionBar createCustomActionBarInvisiableRightImage(Context context,
                  int left_iv_resId, OnClickListener left_iv_ClickListener,
                  String middle_title, int right_iv_resId,
                  OnClickListener right_iv_ClickListener) {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setTvTitleMsg(middle_title);
        actionBar.setIvRightIcon(right_iv_resId);
        actionBar.setIvRightIconInvisiable();
        //actionBar.setIvRightIconListener(right_iv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    public static CustomActionBar createCustomActionBarInvisiableLeftImage(Context context,
            int left_iv_resId, OnClickListener left_iv_ClickListener,
            String middle_title, int right_iv_resId,
            OnClickListener right_iv_ClickListener) {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setIvLeftIconInvisiable();
        actionBar.setTvTitleMsg(middle_title);
        actionBar.setIvRightIcon(right_iv_resId);
        actionBar.setIvRightIconListener(right_iv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    /**
     *
     * @param context
     * @param left_iv_resId 左边图片
     * @param left_iv_ClickListener 左边imageview点击事件
     * @param middle_title 中间文字信息
     * @param right_msg 右边文字信息
     * @param right_tv_ClickListener 右边textview点击事件
     * @return
     */
    public static CustomActionBar createCustomActionBar(Context context,
            int left_iv_resId, OnClickListener left_iv_ClickListener,
            String middle_title, String right_msg,
            OnClickListener right_tv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setTvTitleMsg(middle_title);
        actionBar.setTvRightMsg(right_msg);
        actionBar.setTvRightListener(right_tv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    public static CustomActionBar createCustomActionBarCancle(Context context,
            int left_iv_resId, OnClickListener left_iv_ClickListener,
            String middle_title, String right_msg,
            OnClickListener right_tv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftCancleIcon(left_iv_resId);
        actionBar.setIvLeftCancleIconListener(left_iv_ClickListener);
        actionBar.setTvTitleMsg(middle_title);
        actionBar.setTvRightMsg(right_msg);
        actionBar.setTvRightListener(right_tv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    /**
     *
     * @param context
     * @param left_iv_resId 左边图片
     * @param left_iv_ClickListener 左边imageview点击事件
     * @param middle_title 中间文字信息
     * @param tv_iv_ClickListener 中间textview点击事件
     * @param right_iv_resId 右边图片
     * @param right_iv_ClickListener 右边imageview
     * @return
     */
    public static CustomActionBar createCustomMiddleActionBar(Context context,
            int left_iv_resId, OnClickListener left_iv_ClickListener,
            String middle_title, OnClickListener tv_iv_ClickListener,
            int right_iv_resId, OnClickListener right_iv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setTvTitleMsg(middle_title);
        actionBar.setIvTvListener(tv_iv_ClickListener);
        actionBar.setIvRightIcon(right_iv_resId);
        actionBar.setIvRightIconListener(right_iv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    public static CustomActionBar createCustomActionBarRightHead(
            Context context, int left_iv_resId,
            OnClickListener left_iv_ClickListener, String middle_title,
            int right_iv_resId, OnClickListener right_iv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setTvTitleMsg(middle_title);
        actionBar.setIvRightHead(right_iv_resId);
        actionBar.setIvRightHeadListener(right_iv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    public static CustomActionBar createCustomActionBarCalendarRightHead(
            Context context, int left_iv_resId,
            OnClickListener left_iv_ClickListener, String middle_title,
            int right_iv_resId, OnClickListener right_iv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setTvCalendarTitleMsg(middle_title);
        actionBar.setIvRightHead(right_iv_resId);
        actionBar.setIvRightHeadListener(right_iv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    public static CustomActionBar createCustomActionBarCalendar(
            Context context, int left_iv_resId,
            OnClickListener left_iv_ClickListener, String middle_title,
            String right_msg, OnClickListener right_tv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setTvCalendarTitleMsg(middle_title);
        actionBar.setTvRightMsg(right_msg);
        actionBar.setTvRightListener(right_tv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    public static CustomActionBar createCustomActionBarCancleCalendar(
            Context context, int left_iv_resId,
            OnClickListener left_iv_ClickListener, String middle_title,
            String right_msg, OnClickListener right_tv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftCancleIcon(left_iv_resId);
        actionBar.setIvLeftCancleIconListener(left_iv_ClickListener);
        actionBar.setTvCalendarTitleMsg(middle_title);
        actionBar.setTvRightMsg(right_msg);
        actionBar.setTvRightListener(right_tv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }


    public static CustomActionBar createCustomActionBarImgCalendar(
            Context context, int left_iv_resId,
            OnClickListener left_iv_ClickListener, String middle_title,
            int right_icon, OnClickListener right_tv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setTvCalendarTitleMsg(middle_title);
        actionBar.setIvRightIcon(right_icon);
        actionBar.setIvRightIconListener(right_tv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(false);

        return actionBar;
    }

    /**
     *
     * @param context
     * @param left_iv_resId 左边图片
     * @param left_iv_ClickListener 左边imageview点击事件
     * @param tv_iv_ClickListener 中间textview点击事件
     * @param right_iv_resId 右边图片
     * @param right_iv_ClickListener 右边imageview
     * @return
     */
    public static CustomActionBar createCustomSearchActionBar(Context context,
            int left_iv_resId, OnClickListener left_iv_ClickListener,
            int right_iv_resId, OnClickListener right_iv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setSearcheEnable(true);
        actionBar.setHealthyCalendarEnable(false);
        actionBar.setIvRightIcon(right_iv_resId);
        actionBar.setIvRightIconListener(right_iv_ClickListener);

        return actionBar;
    }

    /**
     *
     * @param context
     * @param left_iv_resId 左边图片
     * @param left_iv_ClickListener 左边imageview点击事件
     * @param tv_iv_ClickListener 中间textview
     * @param right_iv_resId 右边图片
     * @param right_iv_ClickListener 右边imageview
     * @return
     */
    public static CustomActionBar createCustomHealthyCalendarActionBar(
            Context context, int left_iv_resId,
            OnClickListener left_iv_ClickListener, String content,
            int right_iv_resId, OnClickListener right_iv_ClickListener)
    {

        CustomActionBar actionBar = new CustomActionBar(context);
        actionBar.setIvLeftIcon(left_iv_resId);
        actionBar.setIvLeftIconListener(left_iv_ClickListener);
        actionBar.setSearcheEnable(false);
        actionBar.setHealthyCalendarEnable(true);
        actionBar.setIvRightHead(right_iv_resId);
        actionBar.setIvRightHeadListener(right_iv_ClickListener);
        actionBar.setHealthyCalendarTv(content);

        return actionBar;
    }

}
