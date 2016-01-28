package com.smarthome.client2.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.common.GloableData;
import com.smarthome.client2.common.SysParamers;

public class EActivityView extends LinearLayout{
	/**
	 * 滚动显示和隐藏menu时，手指滑动需要达到的速度。
	 */
	public static final int SNAP_VELOCITY = 200;

	/**
	 * 屏幕宽度值。
	 */
	private int screenWidth;

	/**
	 * menu最多可以滑动到的左边缘。值由menu布局的宽度来定，marginLeft到达此值之后，不能再减少。
	 */
	private int leftEdge;

	/**
	 * menu最多可以滑动到的右边缘。值恒为0，即marginLeft到达0之后，不能增加。
	 */
	private int rightEdge = 0;

	/**
	 * menu完全显示时，留给content的宽度值。
	 */
	private int menuPadding = 80;

	/**
	 * 主内容的布局。
	 */
	private LinearLayout content;

	/**
	 * menu的布局。
	 */
	private View menu;

	/**
	 * menu布局的参数，通过此参数来更改leftMargin的值。
	 */
	private LinearLayout.LayoutParams menuParams;

	/**
	 * 记录手指按下时的横坐标。
	 */
	private float xDown,yDown;

	/**
	 * 记录手指移动时的横坐标。
	 */
	private float xMove,yMove;

	/**
	 * 记录手机抬起时的横坐标。
	 */
	private float xUp;

	/**
	 * menu当前是显示还是隐藏。只有完全显示或隐藏menu时才会更改此值，滑动过程中此值无效。
	 */
	private boolean isMenuVisible;

	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;
	
	private View e_activity_view;
	public View content_view;
	
	public boolean enableScroll = false;//是否可以滑动拉出菜单
	
	public boolean isNeedResetLayoutParameter = false;//是否需要重新设置添加的contentview的布局
	

	public EActivityView(Context context) {
		super(context);
	}
	
	public EActivityView(Context context,View view) {
		super(context);
		setCustomContentView(view);
	}
	
	public EActivityView(Context context,int layoutResID) {
		super(context);
		setCustomContentView(layoutResID);
	}

	public void setCustomContentView(View view) {
		LayoutInflater lf = LayoutInflater.from(getContext());
		e_activity_view = lf.inflate(R.layout.e_activity, this);
		initValues();
		content_view = view;
		
		if(isNeedResetLayoutParameter){
			LayoutParams lp = new LayoutParams(GloableData.getScreenW(getContext()),GloableData.getScreenH(getContext()));
			content_view.setLayoutParams(lp);
		}else{
			LayoutParams lp = new LayoutParams(GloableData.getScreenW(getContext()),LayoutParams.FILL_PARENT);
			content_view.setLayoutParams(lp);
		}
		
		content.addView(content_view);
	}
	
	public void setCustomContentView(int layoutResID) {
		LayoutInflater lf = LayoutInflater.from(getContext());
		e_activity_view = lf.inflate(R.layout.e_activity, this);
		initValues();
		content_view = lf.inflate(layoutResID, null);
		
		if(isNeedResetLayoutParameter){
			LayoutParams lp = new LayoutParams(GloableData.getScreenW(getContext()),GloableData.getScreenH(getContext()));
			content_view.setLayoutParams(lp);
		}else{
			LayoutParams lp = new LayoutParams(GloableData.getScreenW(getContext()),LayoutParams.FILL_PARENT);
			content_view.setLayoutParams(lp);
		}
		content.addView(content_view);
	}
	
	
	

	public EActivityView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 初始化一些关键性数据。包括获取屏幕的宽度，给content布局重新设置宽度，给menu布局重新设置宽度和偏移距离等。
	 */
	private void initValues() {
		WindowManager window = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		screenWidth = window.getDefaultDisplay().getWidth();
		content = (LinearLayout) e_activity_view.findViewById(R.id.e_content);
		menu = e_activity_view.findViewById(R.id.e_menu);
		menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();
		// 将menu的宽度设置为屏幕宽度减去menuPadding
//		menuParams.width = screenWidth - menuPadding;
		menuParams.width = 4*screenWidth/5;
		// 左边缘的值赋值为menu宽度的负数
		leftEdge = -menuParams.width;
		// menu的leftMargin设置为左边缘的值，这样初始化时menu就变为不可见
		menuParams.leftMargin = leftEdge;
		// 将content的宽度设置为屏幕宽度
		content.getLayoutParams().width = screenWidth;
		
	}
	
	
	public void meanuOnClick(){
		if(isMenuVisible){
			scrollToContent();
		}else{
			scrollToMenu();
		}
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时，记录按下时的横坐标
			xDown = ev.getRawX();
			yDown = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			// 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整menu的leftMargin值，从而显示和隐藏menu
			xMove = ev.getRawX();
			yMove = ev.getRawY();
			int distanceX = (int) (xMove - xDown);
			int distanceY = (int) (yMove - yDown);
			
			ViewConfiguration configuration = ViewConfiguration.get(getContext());
			// 获得可以认为是滚动的距离
			int mTouchSlop = configuration.getScaledTouchSlop();
			
			
		}
		
		return super.onInterceptTouchEvent(ev);
	}
	
	
	int distanceX = 0;
	ViewConfiguration configuration = ViewConfiguration.get(getContext());
	int mTouchSlop = configuration.getScaledTouchSlop();
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(!enableScroll){
			return super.onTouchEvent(event);
		}
		
		createVelocityTracker(event);
		

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时，记录按下时的横坐标
			distanceX = 0;
			xDown = event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			// 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整menu的leftMargin值，从而显示和隐藏menu
			xMove = event.getRawX();
			distanceX = (int) (xMove - xDown);
			
			// 获得可以认为是滚动的距离
			if (isMenuVisible) {
				menuParams.leftMargin = distanceX;
			} else {
				menuParams.leftMargin = leftEdge + distanceX;
			}
			if (menuParams.leftMargin < leftEdge) {
				menuParams.leftMargin = leftEdge;
			} else if (menuParams.leftMargin > rightEdge) {
				menuParams.leftMargin = rightEdge;
			}
			menu.setLayoutParams(menuParams);
			
			break;
		case MotionEvent.ACTION_UP:
			// 手指抬起时，进行判断当前手势的意图，从而决定是滚动到menu界面，还是滚动到content界面
			xUp = event.getRawX();
			if (wantToShowMenu()) {
				if (shouldScrollToMenu()) {
					scrollToMenu();
				} else {
					scrollToContent();
				}
			} else if (wantToShowContent()) {
				if (shouldScrollToContent()) {
					scrollToContent();
				} else {
					scrollToMenu();
				}
			}
			recycleVelocityTracker();			
			break;
		}
		return true;
	}

	/**
	 * 判断当前手势的意图是不是想显示content。如果手指移动的距离是负数，且当前menu是可见的，则认为当前手势是想要显示content。
	 * 
	 * @return 当前手势想显示content返回true，否则返回false。
	 */
	private boolean wantToShowContent() {
		return xUp - xDown < 0 && isMenuVisible;
	}

	/**
	 * 判断当前手势的意图是不是想显示menu。如果手指移动的距离是正数，且当前menu是不可见的，则认为当前手势是想要显示menu。
	 * 
	 * @return 当前手势想显示menu返回true，否则返回false。
	 */
	private boolean wantToShowMenu() {
		return xUp - xDown > 0 && !isMenuVisible;
	}

	/**
	 * 判断是否应该滚动将menu展示出来。如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY，
	 * 就认为应该滚动将menu展示出来。
	 * 
	 * @return 如果应该滚动将menu展示出来返回true，否则返回false。
	 */
	private boolean shouldScrollToMenu() {
		return xUp - xDown > menuParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}

	/**
	 * 判断是否应该滚动将content展示出来。如果手指移动距离加上menuPadding大于屏幕的1/2，
	 * 或者手指移动速度大于SNAP_VELOCITY， 就认为应该滚动将content展示出来。
	 * 
	 * @return 如果应该滚动将content展示出来返回true，否则返回false。
	 */
	private boolean shouldScrollToContent() {
		return xDown - xUp + menuPadding > menuParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}

	/**
	 * 将屏幕滚动到menu界面，滚动速度设定为30.
	 */
	private void scrollToMenu() {
		new ScrollTask().execute(30);
	}

	/**
	 * 将屏幕滚动到content界面，滚动速度设定为-30.
	 */
	private void scrollToContent() {
		new ScrollTask().execute(-30);
	}

	/**
	 * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
	 * 
	 * @param event
	 *            content界面的滑动事件
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 获取手指在content界面滑动的速度。
	 * 
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... speed) {
			int leftMargin = menuParams.leftMargin;
			// 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。
			while (true) {
				leftMargin = leftMargin + speed[0];
				if (leftMargin > rightEdge) {
					leftMargin = rightEdge;
					break;
				}
				if (leftMargin < leftEdge) {
					leftMargin = leftEdge;
					break;
				}
				publishProgress(leftMargin);
				// 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画。
				sleep(20);
			}
			isMenuVisible = speed[0] > 0;
			return leftMargin;
		}

		@Override
		protected void onProgressUpdate(Integer... leftMargin) {
			menuParams.leftMargin = leftMargin[0];
			menu.setLayoutParams(menuParams);
		}

		@Override
		protected void onPostExecute(Integer leftMargin) {
			menuParams.leftMargin = leftMargin;
			menu.setLayoutParams(menuParams);
		}
	}

	/**
	 * 使当前线程睡眠指定的毫秒数。
	 * 
	 * @param millis
	 *            指定当前线程睡眠多久，以毫秒为单位
	 */
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
