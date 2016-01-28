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

import com.smarthome.client2.common.TLog;
import com.smarthome.client2.util.ScreenUtils;

public class CustomAdView extends ViewGroup {

	/** 向外发送的广播事件 */
	public static final String NowViewChange = "cn.etouch.eCalender.AdView.CustomAdView_NowViewScreenChange";
	public static final String GetNextScreenData = "cn.etouch.eCalender.AdView.CustomAdView_GetNextScreenData";
	// -----------------------------------------
	private Context ctx;
	private boolean mFirstLayout = true;
	public int mCurrentScreen = 0;
	float down_x = 0, up_x = 0;
	float down_y = 0, up_y = 0;
	private int lastScrollByPix = 0;
	private Scroller mScroller;

	/** 可以认为是滚动的最小距离 */
	private int mTouchSlop;
	/** 最后点击的点 */
	private float mLastMotionX,mLastMotionY;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	private boolean isCollectShopRrefresh = true;
	
	public int index_view = 0;

	public CustomAdView(Context context) {
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

	public CustomAdView(Context context, AttributeSet attrs) {
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
			if(i < count-2){
				// 为所有子View设定标准尺寸
				getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			}else{
				View child = getChildAt(i);
				int w = child.getLayoutParams().width;
				int h = child.getLayoutParams().height;
				TLog.Log("zxl---customAdview---onMeasure--->"+w+"--->"+h);
				measureChildWithMargins(child, widthMeasureSpec, w, heightMeasureSpec, h);
			}
			
		}
		// 滚动到第一屏设为默认主屏。
		if (mFirstLayout) {
			scrollTo(mCurrentScreen * width, 0);
			mFirstLayout = false;
		}
	}
	
	

	private int child_up_h = 0;
	private int child_down_h = 0;
	private View child_view_down;
//	private int[] child_view_down_location = new int[2];
	private int child_view_down_postion = 0;
	private int[] location = new int[2];
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childLeft = 0;
		int w_down = 0;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if(i < count-2){
				final View child = getChildAt(i);
				if (child.getVisibility() != View.GONE) {
					final int childWidth = child.getMeasuredWidth();
					child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
					childLeft += childWidth;
					child_up_h = child.getMeasuredHeight();
				}
			}else{
				child_view_down = getChildAt(i);
				int[] custom_ad_view_location = new int[2];
				CustomAdView.this.getLocationInWindow(custom_ad_view_location);
				TLog.Log("zxl---customAdView---onLayout---child_view_down_postion--->"+child_view_down_postion+"--->"+custom_ad_view_location[1]);
				if (child_view_down.getVisibility() != View.GONE) {
					child_down_h = child_view_down.getMeasuredHeight();
					
					w_down += (ScreenUtils.getScreenWidth(ctx)-child_view_down.getMeasuredWidth())/2;
					
					child_view_down.layout(w_down, child_up_h, w_down+child_view_down.getMeasuredWidth(), child_down_h+child_up_h);
					w_down = (i-2+1)*ScreenUtils.getScreenWidth(ctx);
				}
				child_view_down_postion = child_up_h + custom_ad_view_location[1];
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
	
	int last_x_dispatch = 0;
	int now_x_dispatch = 0;
	int last_x_dispatch2 = 0;
	
	int last_y_dispatch = 0;
	int now_y_dispatch = 0;
	int last_y_dispatch2 = 0;
	
	boolean isYmove_dispatch = false;
	boolean isMoveToUp_dispatch = false;
	boolean isThroughMove = false;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			TLog.Log("zxl---customAdview---dispatch---down--->"+isYmove_dispatch);
			last_x_dispatch = (int) ev.getX();
			last_y_dispatch = (int) ev.getY();
			
			last_x_dispatch2 = (int) ev.getX();
			last_y_dispatch2 = (int) ev.getY();
			last_y = (int) ev.getY();
			return super.dispatchTouchEvent(ev);
		case MotionEvent.ACTION_MOVE:
			
			isThroughMove = true;
			
			now_x_dispatch = (int) ev.getX();
			now_y_dispatch = (int) ev.getY();
			int deltaX = Math.abs(now_x_dispatch - last_x_dispatch);
			int deltaY = Math.abs(now_y_dispatch - last_y_dispatch);

			if(deltaX > 0){
				isYmove_dispatch = false;
			}else{
				isYmove_dispatch = deltaY > mTouchSlop && deltaY > deltaX;
			}
			
			
//			last_x_dispatch = now_x_dispatch;
//			last_y_dispatch = now_y_dispatch;
			
			TLog.Log("zxl---customAdview---dispatch---move--->"+isYmove_dispatch+"--->"+deltaX+"--->"+deltaY);
			return super.dispatchTouchEvent(ev);
		case MotionEvent.ACTION_UP:
			now_y_dispatch = (int) ev.getY();
			TLog.Log("zxl---customAdview---dispatch---up--->"+isYmove_dispatch);
			isThroughMove = false;
			isYmove_dispatch = false;
			return super.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			TLog.Log("zxl---customAdview---onInterceptTouchEvent---down--->");
			return super.onInterceptTouchEvent(ev);
		case MotionEvent.ACTION_MOVE:
			now_y_dispatch = (int) ev.getY();
			TLog.Log("zxl---customAdview---onInterceptTouchEvent---move--->"+child_down_h+"--->"+this.clickLineViewListener.isClickLineView()+"--->"+isYmove_dispatch);
			if(isYmove_dispatch){
				return true;
			}
			if (this.clickLineViewListener != null && this.clickLineViewListener.isClickLineView()) {
				if(isYmove_dispatch){
					return true;
				}else{
					isThroughMove = false;
					isYmove_dispatch = false;
					return super.onInterceptTouchEvent(ev);
				}
			}else if(this.clickTimeLooperListener != null && this.clickTimeLooperListener.isClickTimeLooperView()){
				if(isYmove_dispatch){
					return true;
				}else{
					isThroughMove = false;
					isYmove_dispatch = false;
					return super.onInterceptTouchEvent(ev);
				}
			}else if(child_up_h+(location[1]-child_view_down_postion) < now_y_dispatch){
				isThroughMove = false;
				isYmove_dispatch = false;
				return super.onInterceptTouchEvent(ev);
			}
			return true;
		case MotionEvent.ACTION_UP:
			try{
				now_y_dispatch = (int) ev.getY();
				TLog.Log("zxl---customAdview---onInterceptTouchEvent---up--->"+child_up_h+"--->"+(location[1]-child_view_down_postion)+"--->"+now_y_dispatch+"--->"+this.clickLineViewListener.isClickLineView()+"--->"+isYmove_dispatch+"--->"+isThroughMove);
				if (this.clickLineViewListener != null && this.clickLineViewListener.isClickLineView()) {
					if(isThroughMove){
						if(isYmove_dispatch){
							return true;
						}else{
							return super.onInterceptTouchEvent(ev);
						}
					}else{
						return super.onInterceptTouchEvent(ev);
					}
				}else if(this.clickTimeLooperListener != null && this.clickTimeLooperListener.isClickTimeLooperView()){
					if(isThroughMove){
						if(isYmove_dispatch){
							return true;
						}else{
							return super.onInterceptTouchEvent(ev);
						}
					}else{
						return super.onInterceptTouchEvent(ev);
					}
				}else if(child_up_h+(location[1]-child_view_down_postion) < now_y_dispatch){
					return super.onInterceptTouchEvent(ev);
				}
				return true;
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				TLog.Log("zxl---customAdview---onInterceptTouchEvent---up finally--->"+child_up_h+"--->"+(location[1]-child_view_down_postion)+"--->"+now_y_dispatch+"--->"+this.clickLineViewListener.isClickLineView()+"--->"+isYmove_dispatch+"--->"+isThroughMove);
				isThroughMove = false;
				isYmove_dispatch = false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

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
				index_view = msg.arg1;
				
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
	
	
	int last_x = 0;
	int now_x = 0;
	int last_y = 0;
	int now_y = 0;
	boolean isMoveToRight = false;
	boolean isXmove = false;
	
	boolean isYmove = false;
	boolean isMoveToUp = false;
	
	public int index_today_sleep = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				TLog.Log("zxl---customAdView---down--->");
				last_x = (int) event.getX();
				last_y = (int) event.getY();
				
				isMoveToRight = false;
				isXmove = false;
				return true;
			case MotionEvent.ACTION_MOVE:
				now_x = (int) event.getX();
				now_y = (int) event.getY();
				
				int delta_x = now_x - last_x;
				int delta_y = now_y - last_y;
				
				TLog.Log("zxl---customAdView---move--->"+delta_x+"--->"+delta_y+"--->"+event.getY());
				
				if(Math.abs(delta_x) > Math.abs(delta_y)){
					isXmove = true;
					isMoveToRight = delta_x >= 0;
				}else{
					if(Math.abs(delta_y) > Math.abs(delta_x)){
						isYmove = true;
						child_view_down.getLocationInWindow(location);
						
						TLog.Log("zxl---customAdView---ontouch---ymove--->"+delta_y+"--->"+child_down_h);
						if(moveToUpListener != null){
							moveToUpListener.onMoveToUpListener(delta_y);
						}
						if(delta_y<0){
							isMoveToUp = true;
							int else_can_move_y = child_down_h - (child_view_down_postion - location[1]);
							if(else_can_move_y > 0){
								if(Math.abs(delta_y) > else_can_move_y){
									scrollBy(0, else_can_move_y);
								}else{
									scrollBy(0, -delta_y);
								}
							}
							
						}else{
							isMoveToUp = false;
							
							int else_can_move_y = child_view_down_postion - location[1];
							if(else_can_move_y > 0){
								if(delta_y > else_can_move_y){
									scrollBy(0, -else_can_move_y);
								}else{
									scrollBy(0, -delta_y);
								}
							}
						}
					}
				}
				
				last_x = now_x;
				last_y = now_y;
				
				return super.onTouchEvent(event);
			case MotionEvent.ACTION_UP:
				TLog.Log("zxl---customAdView---up---ymove--->"+index_today_sleep+"--->"+isXmove);
				if(isXmove){
					if(isMoveToRight){
						index_today_sleep--;
					}else{
						index_today_sleep++;
					}
					if(1 == index_view){
						if(changShowDetailSleepTimeListener != null){
							index_today_sleep = changShowDetailSleepTimeListener.OnChangShowDetailSleepTimeListener(index_today_sleep);
						}
					}
				}
				
				if(isYmove){
					child_view_down.getLocationInWindow(location);
					TLog.Log("zxl---customAdView---ontouch---yup--->"+location[1]+"--->"+child_view_down_postion+"--->"+child_up_h+"--->"+child_down_h);
					if(isMoveToUp){
						if(child_view_down_postion - location[1] > child_down_h ){
							scrollBy(0, location[1] + child_down_h - child_view_down_postion);
						}
					}else{
						if(location[1] > child_view_down_postion){
							scrollBy(0, location[1] - child_view_down_postion);
						}
					}
				}
				
				isThroughMove = false;
				isYmove_dispatch = false;
				return super.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}
	
	private ChangShowDetailSleepTimeListener changShowDetailSleepTimeListener;
	
	public void setOnChangShowDetailSleepTimeListener(ChangShowDetailSleepTimeListener listener){
		this.changShowDetailSleepTimeListener = listener;
	}
	
	public interface ChangShowDetailSleepTimeListener{
		int OnChangShowDetailSleepTimeListener(int index);
	}
	
	private ClickLineViewListener clickLineViewListener;
	
	public void setClickLineViewListener(ClickLineViewListener clickLineViewListener){
		this.clickLineViewListener = clickLineViewListener;
	}
	
	public interface ClickLineViewListener{
		boolean isClickLineView();
	}
	
	private MoveToUpListener moveToUpListener;
	
	public void setMoveToUpListener(MoveToUpListener moveToUpListener){
		this.moveToUpListener = moveToUpListener;
	}
	
	public interface MoveToUpListener{
		void onMoveToUpListener(int deltaY);
	}
	
	private ClickTimeLooperListener clickTimeLooperListener;
	
	public void setClickTimeLooperListener(ClickTimeLooperListener clickTimeLooperListener){
		this.clickTimeLooperListener = clickTimeLooperListener;
	}
	
	public interface ClickTimeLooperListener{
		boolean isClickTimeLooperView();
	}

}
