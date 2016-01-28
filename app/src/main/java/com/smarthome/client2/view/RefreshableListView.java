package com.smarthome.client2.view;

import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.util.ScreenUtils;

public class RefreshableListView extends ListView implements OnScrollListener {

	/** View 主体部分 */
	private View mHeaderView = null;
	private LinearLayout ll_content = null;
	private ImageView mArrow = null;
	private ProgressBar mProgress = null;
	private TextView mText = null;
	private TextView last_updated_text;
	/** 记录用户按下（开始下拉）的y坐标 */
	private float mY = 0;
	private float mHistoricalY = 0;
	private boolean mFlag = false;
	private boolean mArrowUp = false;
	/** 这个值用来标示下拉刷新中listView 的动作 从手指下拉松开开始 到listView滑动到距离顶部距离为0结束 */
	private boolean mIsRefreshing = false;

	private int mHeaderHeight = 0;
	private OnRefreshListener mListener = null;

	/** 只用来表示是否启动了异步线程加载数据 从下载数据的部分激活到下载数据的部分结束并手动调用complete */
	private boolean isRefreshingAndGetingData = false;

	private int mInitialHeight = 0;

	/** 标示当前状态为正在刷新的状态 刷新状态下 动画底线是head的高度 **/
	private static final int REFRESH = 0;
	/** 标示当前状态是正常状态 正常状态下动画底线是0 */
	private static final int NORMAL = 1;
	private static final int HEADER_HEIGHT_DP = 62;

	/** 标识手指是否处于拉动刷新状态 */
	private boolean isPullToRefrush = false;

	public CustomOnScrollListener customOnScrollListener;

	public boolean IsBusy = false;

	private notifyDataListenner notifyDataListenner;


	private boolean isCanRefresh = true;
	
	private boolean isCanSlidle = false;
	
	private boolean isShowUpdateTime = false;
	
	//下拉刷新
	private String s_update_tittle_down = "下拉刷新";
	
	//松开刷新
	private String s_update_tittle_middle = "松开刷新";
	
	//正在刷新...
	private String s_update_tittle_ing = "正在刷新...";
	
	//刷新完成
	private String s_update_tittle_up = "刷新完成";
	
	/**
	 * ListView的item
	 */
	private View itemView;
	/**
	 * 滑动类
	 */
	private Scroller scroller;
	private static final int SNAP_VELOCITY = 600;
	/**
	 * 速度追踪对象
	 */
	private VelocityTracker velocityTracker;
	/**
	 * 是否响应滑动，默认为不响应
	 */
	private boolean isSlide = false;
	/**
	 * 认为是用户滑动的最小距离
	 */
	private int mTouchSlop;
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	/**
	 *  移除item后的回调接口
	 */
	private RemoveListener mRemoveListener;
	
	private CustomMoveUpListener moveUpListener;
	private CustomMoveDownListener moveDownListener;
	
	private boolean isAdapterUnable = false;

	public RefreshableListView(final Context context) {
		super(context);
		init();
	}

	public RefreshableListView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RefreshableListView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		scroller = new Scroller(getContext());
		screenWidth = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
	}

	public void initialize() {
		this.setFadingEdgeLength(0);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		/** 整个顶部的xmlView */
		View mHeaderContainer = inflater.inflate(R.layout.refreshable_list_header, null);
		mHeaderView = mHeaderContainer.findViewById(R.id.refreshable_list_header);
		ll_content = (LinearLayout) mHeaderContainer.findViewById(R.id.ll_content);
		ViewGroup.LayoutParams lp = ll_content.getLayoutParams();
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		ll_content.setLayoutParams(lp);
		mArrow = (ImageView) mHeaderContainer.findViewById(R.id.refreshable_list_arrow);
		mProgress = (ProgressBar) mHeaderContainer.findViewById(R.id.refreshable_list_progress);
		mText = (TextView) mHeaderContainer.findViewById(R.id.refreshable_list_text);
		last_updated_text = (TextView) mHeaderContainer.findViewById(R.id.last_updated_text);
		addHeaderView(mHeaderContainer);
		mHeaderHeight = ScreenUtils.dip2px(this.getContext(),HEADER_HEIGHT_DP);
		setHeaderHeight(0);
		this.setOnScrollListener(this);
		final ViewConfiguration configuration = ViewConfiguration.get(this.getContext());
		// 获得可以认为是滚动的距离
		mTouchSlop = configuration.getScaledTouchSlop();

	}

	public void initialize(int height) {
		this.setFadingEdgeLength(0);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		/** 整个顶部的xmlView */
		View mHeaderContainer = inflater.inflate(R.layout.refreshable_list_header, null);
		mHeaderView = mHeaderContainer.findViewById(R.id.refreshable_list_header);
		ll_content = (LinearLayout) mHeaderContainer.findViewById(R.id.ll_content);
		ViewGroup.LayoutParams lp = ll_content.getLayoutParams();
		if (height >= 0) {
			lp.height = height;
		} else {
			lp.height = 0;
		}
		ll_content.setLayoutParams(lp);
		mArrow = (ImageView) mHeaderContainer.findViewById(R.id.refreshable_list_arrow);
		mProgress = (ProgressBar) mHeaderContainer.findViewById(R.id.refreshable_list_progress);
		mText = (TextView) mHeaderContainer.findViewById(R.id.refreshable_list_text);
		last_updated_text = (TextView) mHeaderContainer.findViewById(R.id.last_updated_text);
		addHeaderView(mHeaderContainer);
		mHeaderHeight = ScreenUtils.dip2px(this.getContext(),HEADER_HEIGHT_DP);
		setHeaderHeight(0);
		this.setOnScrollListener(this);
		final ViewConfiguration configuration = ViewConfiguration.get(this.getContext());
		// 获得可以认为是滚动的距离
		mTouchSlop = configuration.getScaledTouchSlop();

	}

	public void initialize(View view) {
		this.setFadingEdgeLength(0);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		/** 整个顶部的xmlView */
		View mHeaderContainer = inflater.inflate(R.layout.refreshable_list_header, null);
		mHeaderView = mHeaderContainer.findViewById(R.id.refreshable_list_header);
		ll_content = (LinearLayout) mHeaderContainer.findViewById(R.id.ll_content);
		ViewGroup.LayoutParams lp = ll_content.getLayoutParams();
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		ll_content.setLayoutParams(lp);
		if (view != null) {
			view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			ll_content.addView(view);
		}
		mArrow = (ImageView) mHeaderContainer.findViewById(R.id.refreshable_list_arrow);
		mProgress = (ProgressBar) mHeaderContainer.findViewById(R.id.refreshable_list_progress);
		mText = (TextView) mHeaderContainer.findViewById(R.id.refreshable_list_text);
		last_updated_text = (TextView) mHeaderContainer.findViewById(R.id.last_updated_text);
		addHeaderView(mHeaderContainer);
		mHeaderHeight = ScreenUtils.dip2px(this.getContext(),HEADER_HEIGHT_DP);
		setHeaderHeight(0);
		this.setOnScrollListener(this);
		final ViewConfiguration configuration = ViewConfiguration.get(this.getContext());
		// 获得可以认为是滚动的距离
		mTouchSlop = configuration.getScaledTouchSlop();

	}
	
	/**
	 * 设置滑动删除的回调接口
	 * @param removeListener
	 */
	public void setRemoveListener(RemoveListener removeListener) {
		this.mRemoveListener = removeListener;
	}
	
	public void SetPullToRefreshEnable(boolean isCanRefresh) {
		this.isCanRefresh = isCanRefresh;
	}

	public void setonRefreshListener(final OnRefreshListener l) {
		mListener = l;
	}
	
	public void setCustomMoveUpListener(CustomMoveUpListener moveUpListener){
		this.moveUpListener = moveUpListener;
	}
	
	public void setCustomMoveDownListener(CustomMoveDownListener moveDownListener){
		this.moveDownListener = moveDownListener;
	}
	
	public void setAdapterIsUnable(){
		isAdapterUnable = false;
	}

	public void onRefreshComplete() {
		/**
		 * 使用两个状态来标识 mIsRefreshing标识调用这个方法的时候 必须是执行过下拉的过程 此方法才有效
		 * isRefreshCompete 方法主要是标识是否已经刷新完成 主要是防止该完成的方法被重复调用的问题
		 * */
		if (isRefreshingAndGetingData && mIsRefreshing) {
			isRefreshingAndGetingData = false;
			mProgress.setVisibility(View.INVISIBLE);
			mArrow.setVisibility(View.VISIBLE);
			if(isAdapterUnable){
				mText.setText(s_update_tittle_up);
			}
			isAdapterUnable = true;
			
			if(isShowUpdateTime){
				last_updated_text.setVisibility(View.VISIBLE);
				last_updated_text.setText("最近更新:" + new Date().toLocaleString());
			}
			mHandler.removeMessages(REFRESH);
			mHandler.removeMessages(NORMAL);
			mHandler.sendMessage(mHandler.obtainMessage(NORMAL, ll_content.getTop(), 0));
			invalidateViews();
		}
	}

	float mLastMotionX = 0;
	float mLastMotionY = 0;
	boolean ishasFoce = false;
	int topmargin = 0;

	/**
	 * 当前滑动的ListView　position
	 */
	private int slidePosition;
	/**
	 * 手指按下X的坐标
	 */
	private int downY;
	/**
	 * 手指按下Y的坐标
	 */
	private int downX;
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			/////////////////计算listview顶部距离//////////////////
			mLastMotionX = event.getX();
			mLastMotionY = event.getY();
			ishasFoce = false;
			int[] location2 = new int[2];
			RefreshableListView.this.getLocationInWindow(location2);
			topmargin = location2[1];
			/////////////////计算listview顶部距离//////////////////
			
			if(isCanSlidle){
				break;
			}
			addVelocityTracker(event);

			// 假如scroller滚动还没有结束，我们直接返回
			if (!scroller.isFinished()) {
				return super.dispatchTouchEvent(event);
			}
			downX = (int) event.getX();
			downY = (int) event.getY();

			slidePosition = pointToPosition(downX, downY);

			// 无效的position, 不做任何处理
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}

			// 获取我们点击的item view
			itemView = getChildAt(slidePosition - getFirstVisiblePosition());
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (isCanSlidle && Math.abs(getScrollVelocity()) > SNAP_VELOCITY
					|| (Math.abs(event.getX() - downX) > mTouchSlop && Math
							.abs(event.getY() - downY) < mTouchSlop)) {
				isSlide = true;
				
			}
			break;
		}
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			break;
		}

		return super.dispatchTouchEvent(event);
	}
	
	/**
	 * 添加用户的速度跟踪器
	 * 
	 * @param event
	 */
	private void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}

		velocityTracker.addMovement(event);
	}

	/**
	 * 移除用户速度跟踪器
	 */
	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	/**
	 * 获取X方向的滑动速度,大于0向右滑动，反之向左
	 * 
	 * @return
	 */
	private int getScrollVelocity() {
		int velocity = 0;
		if (velocityTracker != null) {
			velocityTracker.computeCurrentVelocity(1000);
			velocity = (int) velocityTracker.getXVelocity();
		}
		return velocity;
	}
	
	/**
	 * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
	 */
	/**
	 * 用来指示item滑出屏幕的方向,向左或者向右,用一个枚举值来标记
	 */
	private RemoveDirection removeDirection;
	// 滑动删除方向的枚举值
	public enum RemoveDirection {
		RIGHT, LEFT
	}
	private void scrollRight() {
		removeDirection = RemoveDirection.RIGHT;
		final int delta = (screenWidth + itemView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,Math.abs(delta));
		postInvalidate(); // 刷新itemView
	}

	/**
	 * 向左滑动，根据上面我们知道向左滑动为正值
	 */
	private void scrollLeft() {
		removeDirection = RemoveDirection.LEFT;
		final int delta = (screenWidth - itemView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
				Math.abs(delta));
		postInvalidate(); // 刷新itemView
	}

	/**
	 * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
	 */
	private void scrollByDistanceX() {
		// 如果向左滚动的距离大于屏幕的二分之一，就让其删除
		if (itemView.getScrollX() >= screenWidth / 2) {
			scrollLeft();
		} else if (itemView.getScrollX() <= -screenWidth / 2) {
			scrollRight();
		} else {
			// 滚回到原始位置,为了偷下懒这里是直接调用scrollTo滚动
			itemView.scrollTo(0, 0);
		}
	}
	
	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (scroller.computeScrollOffset()) {
			// 让ListView item根据当前的滚动偏移量进行滚动
			itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
			
			postInvalidate();

			// 滚动动画结束的时候调用回调接口
			if (scroller.isFinished()) {
				if (mRemoveListener == null) {
//					throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
				}else{
					itemView.scrollTo(0, 0);
					mRemoveListener.removeItem(removeDirection, slidePosition-getHeaderViewsCount());
				}
				
			}
		}
	}
	
	private boolean isTouchByFriend = false;
	public boolean unableTouch(boolean touchable){
		this.isTouchByFriend = touchable;
		return touchable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (isCanSlidle && isSlide && slidePosition != AdapterView.INVALID_POSITION) {
			requestDisallowInterceptTouchEvent(true);
			addVelocityTracker(ev);
			final int action = ev.getAction();
			int x = (int) ev.getX();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				
				MotionEvent cancelEvent = MotionEvent.obtain(ev);
	            cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
	                       (ev.getActionIndex()<< MotionEvent.ACTION_POINTER_INDEX_SHIFT));
	            onTouchEvent(cancelEvent);
	            
				int deltaX = downX - x;
				downX = x;

				// 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
				itemView.scrollBy(deltaX, 0);
				
				return true;  //拖动的时候ListView不滚动
			case MotionEvent.ACTION_UP:
				int velocityX = getScrollVelocity();
				if (velocityX > SNAP_VELOCITY) {
					scrollRight();
				} else if (velocityX < -SNAP_VELOCITY) {
					scrollLeft();
				} else {
					scrollByDistanceX();
				}
				
				recycleVelocityTracker();
				// 手指离开的时候就不响应左右滚动
				isSlide = false;
				break;
			}
		}
		
		if (isCanRefresh) {
			int[] location = new int[2];
			ll_content.getLocationInWindow(location);
			TLog.Log("zxl---refresh list---"+ev.getAction()+"--->"+location[1]+"--->"+topmargin+"---"+mIsRefreshing);
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				mLastMotionX = ev.getX();
				mLastMotionY = ev.getY();
				ishasFoce = false;
			}else if (ev.getAction() == MotionEvent.ACTION_MOVE && location[1] >= topmargin && !mIsRefreshing) {
				float x = ev.getX();
				float y = ev.getY();
				final int xDiff = (int) Math.abs(x - mLastMotionX);
				final int yDiff = (int) Math.abs(y - mLastMotionY);
				boolean yMoved = (yDiff > mTouchSlop) && (xDiff < yDiff);
				
				if (yMoved) {
					ishasFoce = true;
					RefreshableListView.this.getParent().requestDisallowInterceptTouchEvent(true);
				}
				
				if(!isTouchByFriend){
					if (ishasFoce) {
						/** 初始化数据 激活下拉刷新 **/
						if (!isPullToRefrush) {
							mHandler.removeMessages(REFRESH);
							mHandler.removeMessages(NORMAL);
							mY = mHistoricalY = ev.getY();
							mInitialHeight = ll_content.getTop();
							isPullToRefrush = true;
						}
						float direction = ev.getY() - mHistoricalY;
						float deltaY = Math.abs(mY - ev.getY());
						ViewConfiguration config = ViewConfiguration.get(getContext());
						/** 出发view滚动的最短距离 **/
						if (deltaY > config.getScaledTouchSlop()) {
							int height = (int) (ev.getY() - mY) / 2 + mInitialHeight;
							if (height < 0) {
								height = 0;
							}
							/** direction用来识别滑动的方向 */
							if (direction > 0) {
								/** 判断是否到达顶端 */
								setHeaderHeight(height);
								/**
								 * 当人为控制顶部的head大小的时候 不希望整个listView接收到滑动时间
								 * 因此在此处使用一个ACTION_CANCEL
								 */
								/** 使用ACTION_CANCEL的好处在于 可以实时的终止事件传递 而不用考虑逻辑问题 */
								ev.setAction(MotionEvent.ACTION_CANCEL);
								mFlag = false;
							} else if (direction < 0) {
								setHeaderHeight(height);
								if (ll_content.getTop() <= 0 && !mFlag) {
									ev.setAction(MotionEvent.ACTION_DOWN);
									mFlag = true;
								}
							}
						}
						mHistoricalY = ev.getY();
					}
				}
			} else if (ev.getAction() == MotionEvent.ACTION_UP) {
				isPullToRefrush = false;
				if (!mIsRefreshing) {
					if (mArrowUp) {
						mArrow.setVisibility(View.INVISIBLE);
						mProgress.setVisibility(View.VISIBLE);
						mText.setText(s_update_tittle_ing);
						mIsRefreshing = true;
						mHandler.sendMessage(mHandler.obtainMessage(REFRESH, ll_content.getTop(), 0));
					} else {
						if (location[1] >= topmargin) {
							mHandler.sendMessage(mHandler.obtainMessage(NORMAL, ll_content.getTop(), 0));
						}
					}
				}
				mFlag = false;
			}
		}
		
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
			float y = ev.getY();
			final int yDiff = (int) Math.abs(y - mLastMotionY);
			TLog.Log("zxl---refresh listview---ontouch--->"+y+"--->"+mLastMotionY);
			if(yDiff > 0){
				if(!mIsRefreshing){
					if(y > mLastMotionY){
						//向下滑动
						if(moveDownListener != null){
							moveDownListener.moveDown();
						}
					}else if(y < mLastMotionY){
						//向上滑动
						if(moveUpListener != null){
							moveUpListener.moveUp();
						}
					}
				}					
			}
		}
		
		try {
			return super.onTouchEvent(ev);
		} catch (Exception e) {
			return false;
		}
	}

	private void setHeaderHeight(final int height) {
		TLog.Log("zxl---setHeaderHeight--->"+height+"--->"+mHeaderHeight);
		IsBusy = false;
		if (height <= 1) {
			mHeaderView.setVisibility(View.GONE);
		} else {
			mHeaderView.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams headerLp = (LinearLayout.LayoutParams) mHeaderView.getLayoutParams();
			if (headerLp == null) {
				headerLp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			}
			headerLp.topMargin = -mHeaderHeight + height;
			mHeaderView.setLayoutParams(headerLp);
		}

		if (!mIsRefreshing) {
			if (height > mHeaderHeight && !mArrowUp) {
				mArrow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
				mText.setText(s_update_tittle_middle);
				rotateArrow();
				mArrowUp = true;
			} else if (height < mHeaderHeight && mArrowUp) {
				mArrow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
				mText.setText(s_update_tittle_down);
				rotateArrow();
				mArrowUp = false;
			}
		}
	}

	private void rotateArrow() {
		Drawable drawable = mArrow.getDrawable();
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.save();
		canvas.rotate(180.0f, canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		canvas.restore();
		mArrow.setImageBitmap(bitmap);
	}

	private void startRefreshing() {
		mArrow.setVisibility(View.INVISIBLE);
		mProgress.setVisibility(View.VISIBLE);
		mText.setText(s_update_tittle_ing);
		mIsRefreshing = true;
		isRefreshingAndGetingData = true;
		if (mListener != null) {
			mListener.onRefresh();
		}
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);

			int limit = 0;
			switch (msg.what) {
			case REFRESH:
				limit = mHeaderHeight;
				if (msg.arg1 <= limit && !isRefreshingAndGetingData) {
					startRefreshing();
					TLog.Log("startRefreshing();");
				}
				break;
			case NORMAL:
				limit = 0;
				break;
			}
			if (msg.arg1 >= limit) {
				if (msg.arg1 == 0) {
					mIsRefreshing = false;
				}
				setHeaderHeight(msg.arg1);
				int displacement = (msg.arg1 - limit) / 10;
				if (displacement == 0) {
					mHandler.sendMessage(mHandler.obtainMessage(msg.what, msg.arg1 - 1, 0));
				} else {
					mHandler.sendMessage(mHandler.obtainMessage(msg.what, msg.arg1 - displacement, 0));
				}
			}
		}
	};


	private boolean canNotify = true;

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		if (this.customOnScrollListener != null) {
			this.customOnScrollListener.CustomonScrollStateChanged(arg0, arg1);
		}
		int[] location = new int[2];
		ll_content.getLocationInWindow(location);
		if (canNotify && (location[1] <= topmargin)) {
			if (arg1 == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				IsBusy = true;
				// TLog.Log("SCROLL_STATE_TOUCH_SCROLL   " + IsBusy);
			} else if (arg1 == OnScrollListener.SCROLL_STATE_FLING) {
				IsBusy = true;
				// TLog.Log("SCROLL_STATE_FLING   " + IsBusy);
			} else if (arg1 == OnScrollListener.SCROLL_STATE_IDLE) { // 判断滚动到底部
				// 在下拉刷新中接受到错误的busy信号
				IsBusy = false;
				// TLog.Log("SCROLL_STATE_IDLE   " + IsBusy);
				if (notifyDataListenner != null) {
					canNotify = false;
					// TLog.Log("不处理刷新");
					notifyDataListenner.notifyDataSetChanged();
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							canNotify = true;
							// TLog.Log("处理刷新");
						}
					}, 100);
				}
			}
		} else {
			// TLog.Log("没有处理刷新的权限");
		}
	}

	public interface OnRefreshListener {
		void onRefresh();
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3) {
		if (this.customOnScrollListener != null) {
			this.customOnScrollListener.CustomonScroll(arg0, firstVisiableItem - 1 >= 0 ? firstVisiableItem - 1 : 0, arg2, arg3);
		}
	}
	
	private int child_0_top_position = 0;
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
//		int[] location = new int[2];
//		if(getChildAt(0) != null)
//		getChildAt(0).getLocationInWindow(location);
//		TLog.Log("zxl---refresh listview--->onscrollchanged--->"+t+"--->"+oldt+"--->"+location[1]);
//		if(!mIsRefreshing){
//			if(t > oldt){
//				//向下滑动
//				if(moveDownListener != null){
//					moveDownListener.moveDown();
//				}
//			}else if(t < oldt){
//				//向上滑动
//				if(moveUpListener != null){
//					moveUpListener.moveUp();
//				}
//			}
//		}
//		child_0_top_position = location[1];
	}

	// 如果你要使用下拉刷新的listView实现onscroll 接口 请务必使用该自定义的 接口
	public void SetOnCustomScrollListener(CustomOnScrollListener customOnScrollListener) {
		this.customOnScrollListener = customOnScrollListener;
	}

	public interface CustomOnScrollListener {
		void CustomonScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3);

		void CustomonScrollStateChanged(AbsListView arg0, int arg1);
	}

	/**
	 * 在下拉刷新的listView 中 如果需要有item点击事件 请务必使用该点击事件
	 * 
	 * @description
	 * @date 2013-4-7
	 * @author chenlong
	 * @param onCustomItemClickListenner
	 */
	public void SetOnCustomItemClickListenner(final OnCustomItemClickListenner onCustomItemClickListenner) {
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				onCustomItemClickListenner.onCustomItemClick(parent, view, position - 1, id);
			}
		});
	}

	public interface OnCustomItemClickListenner {
		void onCustomItemClick(AdapterView<?> parent, View view, int position, long id);
	}

	/**
	 * 此方法用于激活 滑动停止后的刷新
	 * 
	 * @description
	 * @date 2013-4-7
	 * @author chenlong
	 * @param notifyDataListenner
	 */
	public void SetNotifyDataListenner(notifyDataListenner notifyDataListenner) {
		this.notifyDataListenner = notifyDataListenner;
	}

	public interface notifyDataListenner {
		void notifyDataSetChanged();
	}
	
	/**
	 * 
	 * 当ListView item滑出屏幕，回调这个接口
	 * 我们需要在回调方法removeItem()中移除该Item,然后刷新ListView
	 * 
	 * @author xiaanming
	 *
	 */
	public interface RemoveListener {
		void removeItem(RemoveDirection direction, int position);
	}
	
	public interface CustomMoveUpListener{
		void moveUp();
	}
	
	public interface CustomMoveDownListener{
		void moveDown();
	}
	
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		TLog.Log("RefreshableListView onDetachedFromWindow");
	}
	
	

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		TLog.Log("RefreshableListView onAttachedToWindow");
		super.onAttachedToWindow();
		
	}

	public void destory() {
		mHandler.removeMessages(REFRESH);
		mHandler.removeMessages(NORMAL);
	}
	
	public void setIsCanSlidle(boolean isCanSlidle){
		this.isCanSlidle = isCanSlidle;
	}
	
	public void setIsShowUpdateTime(boolean isShowUpdateTime){
		this.isShowUpdateTime = isShowUpdateTime;
	}
	
	public void setUpdateTittleDown(String s_update_tittle_down){
		this.s_update_tittle_down = s_update_tittle_down;
		mText.setText(s_update_tittle_down);
	}
	
	public void setUpdateTittleMiddle(String s_update_tittle_middle){
		this.s_update_tittle_middle = s_update_tittle_middle;
	}
	
	public void setUpdateTittleIng(String s_update_tittle_ing){
		this.s_update_tittle_ing = s_update_tittle_ing;
	}
	
	public void setUpdateTittleUp(String s_update_tittle_up){
		this.s_update_tittle_up = s_update_tittle_up;
	}

}
