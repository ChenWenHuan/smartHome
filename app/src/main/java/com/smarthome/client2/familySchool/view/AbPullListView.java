package com.smarthome.client2.familySchool.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;

public class AbPullListView extends ListView implements OnScrollListener
{

    /** The m last y. */
    private float mLastY = -1;

    /** The m scroller. */
    private Scroller mScroller;

    /** The m scroll listener. */
    private OnScrollListener mScrollListener;

    /** The m list view listener. */
    private AbOnListViewListener mListViewListener;

    /** The m header view. */
    private AbListViewHeader mHeaderView;

    /** The m footer view. */
    private AbListViewFooter mFooterView;

    /** The m header view height. */
    private int mHeaderViewHeight;

    /** The m footer view height. */
    private int mFooterViewHeight;

    /** The m enable pull refresh. */
    private boolean mEnablePullRefresh = true;

    /** The m enable pull load. */
    private boolean mEnablePullLoad = true;

    /** The m pull refreshing. */
    private boolean mPullRefreshing = false;

    /** The m pull loading. */
    private boolean mPullLoading;

    /** The m is footer ready. */
    private boolean mIsFooterReady = false;

    // total list items, used to detect is at the bottom of listview.
    /** The m total item count. */
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    /** The m scroll back. */
    private int mScrollBack;

    /** The Constant SCROLLBACK_HEADER. */
    private final static int SCROLLBACK_HEADER = 0;

    /** The Constant SCROLLBACK_FOOTER. */
    private final static int SCROLLBACK_FOOTER = 1;

    /** The Constant SCROLL_DURATION. */
    private final static int SCROLL_DURATION = 200;

    /** The Constant OFFSET_RADIO. */
    private final static float OFFSET_RADIO = 1.8f;

    /** 数据相关. */
    private ListAdapter mAdapter = null;

    public AbPullListView(Context context)
    {
        super(context);
        initWithContext(context);
    }

    public AbPullListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initWithContext(context);
    }

    private void initWithContext(Context context)
    {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);
        mHeaderView = new AbListViewHeader(context);
        mHeaderViewHeight = mHeaderView.getHeaderHeight();
        mHeaderView.setGravity(Gravity.BOTTOM);
        addHeaderView(mHeaderView);
        mFooterView = new AbListViewFooter(context);
        mFooterViewHeight = mFooterView.getFooterHeight();
        // 默认是打开刷新与更多
        setPullRefreshEnable(true);
        setPullLoadEnable(true);
    }

    /**
     * @see android.widget.ListView#setAdapter(android.widget.ListAdapter)
     * @author: zhaoqp
     * @date：2013-9-4 下午4:06:32
     * @version v1.0
     */
    @Override
    public void setAdapter(ListAdapter adapter)
    {
        mAdapter = adapter;
        // make sure XListViewFooter is the last footer view, and only add once.
        if (mIsFooterReady == false)
        {
            mIsFooterReady = true;
            mFooterView.setGravity(Gravity.TOP);
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * 打开或者关闭下拉刷新功能.
     * @param enable
     *            开关标记
     */
    public void setPullRefreshEnable(boolean enable)
    {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh)
        {
            mHeaderView.setVisibility(View.INVISIBLE);
        }
        else
        {
            mHeaderView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 打开或者关闭加载更多功能.
     * @param enable
     *            开关标记
     */
    public void setPullLoadEnable(boolean enable)
    {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad)
        {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        }
        else
        {
            mPullLoading = false;
            mFooterView.setState(AbListViewFooter.STATE_READY);
            // load more点击事件.
            mFooterView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!mAdapter.isEmpty())
                    {
                        startLoadMore();
                    }
                }
            });
        }
    }

    /**
     * 停止刷新，隐藏headerView
     */
    public void stopRefresh()
    {
        if (mPullRefreshing == true)
        {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * 停止加载更多
     */
    public void stopLoadMore()
    {
        if (mPullLoading == true)
        {
            mPullLoading = false;
            mFooterView.setState(AbListViewFooter.STATE_READY);
        }
    }

    /**
     * Update header height.
     * @param delta
     *            the delta
     */
    private void updateHeaderHeight(float delta)
    {
        int newHeight = (int) delta + mHeaderView.getVisiableHeight();
        mHeaderView.setVisiableHeight(newHeight);
        if (mEnablePullRefresh && !mPullRefreshing)
        {
            if (mHeaderView.getVisiableHeight() >= mHeaderViewHeight)
            {
                mHeaderView.setState(AbListViewHeader.STATE_READY);
            }
            else
            {
                mHeaderView.setState(AbListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0);
    }

    /**
     * 根据状态设置Header的位置
     */
    private void resetHeaderHeight()
    {
        // 当前下拉到的高度
        int height = mHeaderView.getVisiableHeight();
        if (height < mHeaderViewHeight || !mPullRefreshing)
        {
            // 距离短隐藏
            mScrollBack = SCROLLBACK_HEADER;
            mScroller.startScroll(0, height, 0, -1 * height, SCROLL_DURATION);
        }
        else if (height > mHeaderViewHeight || !mPullRefreshing)
        {
            // 距离多的 弹回到mHeaderViewHeight
            mScrollBack = SCROLLBACK_HEADER;
            mScroller.startScroll(0,
                    height,
                    0,
                    -(height - mHeaderViewHeight),
                    SCROLL_DURATION);
        }
        invalidate();
    }

    /**
     * 更新 footer的显示.
     * @param delta
     *            增加值
     */
    private void updateFooterHeight(float delta)
    {
        if (mAdapter.isEmpty())
        {
            return;
        }
        int newHeight = mFooterView.getVisiableHeight() + (int) delta;
        if (newHeight >= mFooterViewHeight * 2)
        {
            newHeight = mFooterViewHeight * 2;
        }
        if (newHeight <= mFooterViewHeight)
        {
            newHeight = mFooterViewHeight;
        }
        mFooterView.setVisiableHeight(newHeight);
        if (mEnablePullLoad && !mPullLoading)
        {
            if (newHeight >= mFooterViewHeight)
            {
                mFooterView.setState(AbListViewFooter.STATE_PULLING);
            }
        }
    }

    /**
     * Start load more.
     */
    private void startLoadMore()
    {
        if (mAdapter.isEmpty())
        {
            return;
        }
        mPullLoading = true;
        mFooterView.setState(AbListViewFooter.STATE_LOADING);
        if (mListViewListener != null)
        {
            // 开始下载数据
            mListViewListener.onLoadMore();
        }
    }

    /**
     * 描述：onTouchEvent
     * @see android.widget.ListView#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if (mLastY == -1)
        {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (mEnablePullRefresh && getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0))
                {
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                }
                else if (mEnablePullLoad
                        && getLastVisiblePosition() == mTotalItemCount - 1)
                {
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastY = -1;
                if (getFirstVisiblePosition() == 0)
                {
                    // 需要刷新的条件
                    if (mEnablePullRefresh
                            && !mPullRefreshing
                            && !mPullLoading
                            && mHeaderView.getVisiableHeight() >= mHeaderViewHeight)
                    {
                        mPullRefreshing = true;
                        mHeaderView.setState(AbListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null)
                        {
                            // 刷新
                            mListViewListener.onRefresh();
                        }
                    }

                    if (mEnablePullRefresh)
                    {
                        // 弹回
                        resetHeaderHeight();
                    }
                }
                // 在到底部就加载下一页
                if (getLastVisiblePosition() == mTotalItemCount - 1)
                {
                    // 上拉高度超过初始值100后才加载更多
                    if (mEnablePullLoad
                            && !mPullLoading
                            && !mPullRefreshing
                            && mFooterView.getVisiableHeight() >= mFooterViewHeight * 2 - 10)
                    {
                        startLoadMore();
                    }
                    // 恢复到初始高度
                    else
                    {
                        mFooterView.setVisiableHeight(mFooterViewHeight);
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * @see android.view.View#computeScroll()
     */
    @Override
    public void computeScroll()
    {
        if (mScroller.computeScrollOffset())
        {
            if (mScrollBack == SCROLLBACK_HEADER)
            {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 描述：设置ListView的监听器.
     * @param listViewListener
     */
    public void setAbOnListViewListener(AbOnListViewListener listViewListener)
    {
        mListViewListener = listViewListener;
    }

    /**
     * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView,
     *      int)
     * @author: zhaoqp
     * @date：2013-9-4 下午4:06:32
     * @version v1.0
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if (mScrollListener != null)
        {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView,
     *      int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount)
    {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null)
        {
            mScrollListener.onScroll(view,
                    firstVisibleItem,
                    visibleItemCount,
                    totalItemCount);
        }
    }

    /**
     * 描述：获取Header View
     * @return
     * @throws
     */
    public AbListViewHeader getHeaderView()
    {
        return mHeaderView;
    }

    /**
     * 描述：获取Footer View
     * @return
     * @throws
     */
    public AbListViewFooter getFooterView()
    {
        return mFooterView;
    }

    /**
     * 描述：获取Header ProgressBar，用于设置自定义样式
     * @return
     * @throws
     */
    public ProgressBar getHeaderProgressBar()
    {
        return mHeaderView.getHeaderProgressBar();
    }

    /**
     * 描述：获取Footer ProgressBar，用于设置自定义样式
     */
    public ProgressBar getFooterProgressBar()
    {
        return mFooterView.getFooterProgressBar();
    }

    public boolean isRefreshing()
    {
        return mPullRefreshing;
    }

    public boolean isLoading()
    {
        return mPullLoading;
    }

    /**
     * 模拟下拉刷新动作
     */
    public void performRefresh()
    {
        updateHeaderHeight(mHeaderViewHeight + 10);
        mPullRefreshing = true;
        mHeaderView.setState(AbListViewHeader.STATE_REFRESHING);
        if (mListViewListener != null)
        {
            mListViewListener.onRefresh();
        }
        if (mEnablePullRefresh)
        {
            resetHeaderHeight();
        }
    }

}
