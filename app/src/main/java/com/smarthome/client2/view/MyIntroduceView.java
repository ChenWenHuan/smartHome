package com.smarthome.client2.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyIntroduceView extends ViewGroup {

	/** 向外发送的广播事件 */
	public static final String NowViewChange = "cn.etouch.eCalender.AdView.CustomAdView_NowViewScreenChange";
	public static final String GetNextScreenData = "cn.etouch.eCalender.AdView.CustomAdView_GetNextScreenData";
	// -----------------------------------------
	private Context ctx;
	private boolean mFirstLayout = true;
	public int mCurrentScreen = 1;
	float down_x = 0, up_x = 0;
	private int lastScrollByPix = 0;
	private Scroller mScroller;

	/** 可以认为是滚动的最小距离 */
	private int mTouchSlop;
	/** 最后点击的点 */
	private float mLastMotionX;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	private boolean isCollectShopRrefresh = true;

	public MyIntroduceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.ctx = context;
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		// 获得可以认为是滚动的距离
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	public boolean isCollectShopRrefresh() {
		return isCollectShopRrefresh;
	}

	public void setCollectShopRrefresh(boolean isCollectShopRrefresh) {
		this.isCollectShopRrefresh = isCollectShopRrefresh;
	}

	public MyIntroduceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.ctx = context;
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		// 获得可以认为是滚动的距离
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	/** 添加一个布局View */
	public void addMyView(View v) {
		this.addView(v);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			// 为所有子View设定标准尺寸
		}
		// 滚动到第一屏设为默认主屏。
		if (mFirstLayout) {
			scrollTo(mCurrentScreen * width, 0);
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childLeft = 0;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}// end onLayout

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), 0); // mCurrentScreen*getWidth()
			postInvalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_MOVE:
			final int yDiff = (int) Math.abs(x - mLastMotionX);
			boolean yMoved = yDiff > mTouchSlop;
			// 判断是否是移动
			if (yMoved) {
				mTouchState = TOUCH_STATE_SCROLLING;
				down_x = mLastMotionX;
			}
			break;
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		float now_x = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			down_x = now_x;
			break;
		case MotionEvent.ACTION_MOVE:
			int nowScrollByPix = (int) (down_x - now_x);
			if (getScrollX() >= 0 && getScrollX() <= (getChildCount() - 1) * getWidth()) {
				scrollBy(nowScrollByPix - lastScrollByPix, 0);
				lastScrollByPix = nowScrollByPix;
			}
			break;
		case MotionEvent.ACTION_UP:
			int nextScreen = 0;
			if (getScrollX() <= (getChildCount() - 1) * getWidth() && getScrollX() > (mCurrentScreen * getWidth() + getWidth() / 3)) {
				nextScreen = mCurrentScreen + 1;
				if (isCollectShopRrefresh) {
					isCollectShopRrefresh = false;
					handler.sendEmptyMessage(2);
				}
			} else if (getScrollX() >= 0 && getScrollX() < (mCurrentScreen * getWidth() - getWidth() / 3)) {
				nextScreen = mCurrentScreen - 1;
			} else {
				nextScreen = mCurrentScreen;
			}
			ScrollToScreen(nextScreen);
			mTouchState = TOUCH_STATE_REST;
			if (Math.abs(now_x - down_x) < 5) {
				// 点击事件
			}
			break;
		}
		return true;
	}

	/** 滑动到指定的一屏 */
	public void ScrollToScreen(int nextScreen) {
		nextScreen = Math.max(0, Math.min(nextScreen, getChildCount() - 1));
		final int newX = nextScreen * getWidth();
		final int delta = newX - (mCurrentScreen * getWidth() + lastScrollByPix);
		mScroller.startScroll(mCurrentScreen * getWidth() + lastScrollByPix, 0, delta, 0, (int) (Math.abs(delta) * 1.5f));//
		if (mCurrentScreen != nextScreen) {
			mCurrentScreen = nextScreen;
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = mCurrentScreen;
			handler.sendMessage(msg);
			// 滚动到新的一屏
		}
		lastScrollByPix = 0;
		invalidate();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 滑动到新的一屏
				Intent in = new Intent(NowViewChange);
				in.putExtra("position", msg.arg1);
				ctx.sendBroadcast(in);
				break;
			case 2:
				Intent getNextScreenData = new Intent(GetNextScreenData);
				getNextScreenData.putExtra("position", msg.arg1);
				ctx.sendBroadcast(getNextScreenData);
				break;
			}
		}
	};

}
