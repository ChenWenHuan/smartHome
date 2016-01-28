package com.smarthome.client2.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.smarthome.client2.R;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.unit.listener.LineActionDownFromMySleepListener;
import com.smarthome.client2.unit.listener.LineActionDownFromMySportsListener;
import com.smarthome.client2.util.ScreenUtils;

/**
 * Created by Dacer on 11/4/13.
 */
public class LineView extends View {
	public static final int FLAG_FROM_MY_SPORTS_VIEW = 0;
	public static final int FLAG_FROM_MY_SLEEP_VIEW = 1;
	
	private int mViewHeight;
	// drawBackground
	private boolean autoSetDataOfGird = true;
	private boolean autoSetGridWidth = true;
	private int dataOfAGird = 10;
	private int bottomTextHeight = 0;
	private ArrayList<String> bottomTextList;
	private ArrayList<Integer> dataList;
	private ArrayList<Integer> xCoordinateList = new ArrayList<Integer>();
	private ArrayList<Integer> yCoordinateList = new ArrayList<Integer>();
	private ArrayList<Dot> drawDotList = new ArrayList<Dot>();
	private Paint bottomTextPaint = new Paint();
	private int bottomTextDescent;

	// popup
	private Paint popupTextPaint = new Paint();
	private final int bottomTriangleHeight = 12;
	private boolean showPopup = false;
	private Dot selectedDot;
	
	public int flag_from_view = 0;

	private Paint text_paint;
	private String s_title = "最近七天";
	private int w_title = 0;
	private int h_title = 0;
	
	public boolean isOnTouch = false;

	private int topLineLength = ScreenUtils.dip2px(getContext(), 12); // | | ←
																		// this
	// -+-+-
	private int sideLineLength = ScreenUtils.dip2px(getContext(), 45) / 3 * 2;// --+--+--+--+--+--+--
	// ↑ this ↑
	// private int backgroundGridWidth = ScreenUtils.dip2px(getContext(),45);
	private int backgroundGridWidth = ScreenUtils.getScreenWidth(getContext()) / 8;
	// Constants
	private final int popupTopPadding = ScreenUtils.dip2px(getContext(), 2);
	private final int popupBottomMargin = ScreenUtils.dip2px(getContext(), 5);
	private final int bottomTextTopMargin = ScreenUtils.sp2px(getContext(), 5);
	private final int bottomLineLength = ScreenUtils.sp2px(getContext(), 22);
	private final int DOT_INNER_CIR_RADIUS = ScreenUtils
			.dip2px(getContext(), 2);
	private final int DOT_OUTER_CIR_RADIUS = ScreenUtils
			.dip2px(getContext(), 5);
	private final int MIN_TOP_LINE_LENGTH = ScreenUtils
			.dip2px(getContext(), 12);
	private final int MIN_VERTICAL_GRID_NUM = 4;
	private final int MIN_HORIZONTAL_GRID_NUM = 1;
	private final int BACKGROUND_LINE_COLOR = Color.parseColor("#EEEEEE");
	private final int BOTTOM_TEXT_COLOR = Color.parseColor("#9B9A9B");

	private Runnable animator = new Runnable() {
		@Override
		public void run() {
			boolean needNewFrame = false;
			for (Dot dot : drawDotList) {
				dot.update();
				if (!dot.isAtRest()) {
					needNewFrame = true;
				}
			}
			if (needNewFrame) {
				postDelayed(this, 20);
			}
			invalidate();
		}
	};

	public LineView(Context context) {
		this(context, null);
	}

	public LineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		// 获得可以认为是滚动的距离
		mTouchSlop = configuration.getScaledTouchSlop();
		
		popupTextPaint.setAntiAlias(true);
		popupTextPaint.setColor(Color.WHITE);
		popupTextPaint.setTextSize(ScreenUtils.sp2px(getContext(), 13));
		popupTextPaint.setStrokeWidth(5);
		popupTextPaint.setTextAlign(Paint.Align.CENTER);

		bottomTextPaint.setAntiAlias(true);
		bottomTextPaint.setTextSize(ScreenUtils.sp2px(getContext(), 12));
		bottomTextPaint.setTextAlign(Paint.Align.CENTER);
		bottomTextPaint.setStyle(Paint.Style.FILL);
		bottomTextPaint.setColor(BOTTOM_TEXT_COLOR);

		text_paint = new Paint();
		text_paint.setAntiAlias(true);
		text_paint.setStyle(Paint.Style.FILL);
		text_paint.setTextSize(ScreenUtils.sp2px(getContext(), 13));
		text_paint.setColor(Color.BLACK);
		Rect r = new Rect();
		text_paint.getTextBounds(s_title, 0, s_title.length(), r);
		w_title = r.right - r.left;
		h_title = r.bottom - r.top;
	}

	/**
	 * dataList will be reset when called is method.
	 * 
	 * @param bottomTextList
	 *            The String ArrayList in the bottom.
	 */
	public void setBottomTextList(ArrayList<String> bottomTextList) {
		this.dataList = null;
		this.bottomTextList = bottomTextList;
		
		Rect r = new Rect();
		int longestWidth = 0;
		String longestStr = "";
		bottomTextDescent = 0;
		for (String s : bottomTextList) {
			bottomTextPaint.getTextBounds(s, 0, s.length(), r);
			if (bottomTextHeight < r.height()) {
				bottomTextHeight = r.height();
			}
			if (autoSetGridWidth && (longestWidth < r.width())) {
				longestWidth = r.width();
				longestStr = s;
			}
			if (bottomTextDescent < (Math.abs(r.bottom))) {
				bottomTextDescent = Math.abs(r.bottom);
			}
		}

		if (autoSetGridWidth) {
			if (backgroundGridWidth < longestWidth) {
				backgroundGridWidth = longestWidth
						+ (int) bottomTextPaint.measureText(longestStr, 0, 1);
			}
			if (sideLineLength < longestWidth / 2) {
				sideLineLength = longestWidth / 2;
			}
		}

		refreshXCoordinateList(getHorizontalGridNum());
	}

	/**
	 * 
	 * @param dataList
	 *            The Integer ArrayList for showing, dataList.size() must <
	 *            bottomTextList.size()
	 */
	public void setDataList(ArrayList<Integer> dataList) {
		if (null == this.dataList) {
			this.dataList = new ArrayList<Integer>();
		}
		this.dataList.clear();
		for (int i = dataList.size() - 1; i > -1; i--) {
			this.dataList.add(dataList.get(i));
		}
		// this.dataList = dataList;
		if (dataList.size() > bottomTextList.size()) {
			throw new RuntimeException("dacer.LineView error:"
					+ " dataList.size() > bottomTextList.size() !!!");
		}
		if (autoSetDataOfGird) {
			int biggestData = 0;
			for (Integer i : dataList) {
				if (biggestData < i) {
					biggestData = i;
				}
			}
			dataOfAGird = 1;
			while (biggestData / 10 > dataOfAGird) {
				dataOfAGird *= 10;
			}
		}
		refreshAfterDataChanged();
		showPopup = false;
		setMinimumWidth(0); // It can help the LineView reset the Width,
							// I don't know the better way..
		postInvalidate();
	}

	private void refreshAfterDataChanged() {
		int verticalGridNum = getVerticalGridlNum();
		refreshTopLineLength(verticalGridNum);
		refreshYCoordinateList(verticalGridNum);
		refreshDrawDotList(verticalGridNum);
	}

	private int getVerticalGridlNum() {
		int verticalGridNum = MIN_VERTICAL_GRID_NUM;
		if (dataList != null && !dataList.isEmpty()) {
			for (Integer integer : dataList) {
				if (verticalGridNum < (integer + 1)) {
					verticalGridNum = integer + 1;
				}
			}
		}
		return verticalGridNum;
	}

	private int getHorizontalGridNum() {
		int horizontalGridNum = bottomTextList.size() - 1;
		if (horizontalGridNum < MIN_HORIZONTAL_GRID_NUM) {
			horizontalGridNum = MIN_HORIZONTAL_GRID_NUM;
		}
		return horizontalGridNum;
	}

	private void refreshXCoordinateList(int horizontalGridNum) {
		xCoordinateList.clear();
		for (int i = 0; i < (horizontalGridNum + 1); i++) {
			xCoordinateList.add(backgroundGridWidth + backgroundGridWidth * i);
		}

	}

	private void refreshYCoordinateList(int verticalGridNum) {
		yCoordinateList.clear();
		for (int i = 0; i < (verticalGridNum + 1); i++) {
			yCoordinateList
					.add(topLineLength
							+ h_title
							+ ((mViewHeight - h_title - topLineLength
									- bottomTextHeight - bottomTextTopMargin - bottomTextDescent)
									* i / (verticalGridNum)));
		}
	}

	private void refreshDrawDotList(int verticalGridNum) {
		if (dataList != null && !dataList.isEmpty()) {
			int drawDotSize = drawDotList.isEmpty() ? 0 : drawDotList.size();
			for (int i = 0; i < dataList.size(); i++) {
				int x = xCoordinateList.get(i);
				int y = yCoordinateList.get(verticalGridNum - dataList.get(i));
				if (i > drawDotSize - 1) {
					drawDotList.add(new Dot(x, 0, x, y, dataList.get(i)));
				} else {
					drawDotList.set(
							i,
							drawDotList.get(i).setTargetData(x, y,
									dataList.get(i)));
				}
			}
			int temp = drawDotList.size() - dataList.size();
			for (int i = 0; i < temp; i++) {
				drawDotList.remove(drawDotList.size() - 1);
			}
		}
		removeCallbacks(animator);
		post(animator);
	}

	private void refreshTopLineLength(int verticalGridNum) {
		// For prevent popup can't be completely showed when
		// backgroundGridHeight is too small.
		// But this code not so good.
		if ((mViewHeight - topLineLength - bottomTextHeight - bottomTextTopMargin)
				/ (verticalGridNum + 2) < getPopupHeight()) {
			topLineLength = getPopupHeight() + DOT_OUTER_CIR_RADIUS
					+ DOT_INNER_CIR_RADIUS + 2;
		} else {
			topLineLength = MIN_TOP_LINE_LENGTH;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawBackgroundLines(canvas);
		drawLines(canvas);
		drawDots(canvas);
		if (showPopup && selectedDot != null) {
			drawPopup(canvas, String.valueOf(selectedDot.data),
					selectedDot.getPoint());
		}
		drawWeekOval(canvas);
	}

	/**
	 * 
	 * @param canvas
	 *            The canvas you need to draw on.
	 * @param point
	 *            The Point consists of the x y coordinates from left bottom to
	 *            right top. Like is ↓ 3 2 1 0 1 2 3 4 5
	 */
	private void drawPopup(Canvas canvas, String num, Point point) {
		boolean singularNum = (num.length() == 1);
		int sidePadding = ScreenUtils.dip2px(getContext(), singularNum ? 8 : 5);
		int x = point.x;
		int y = point.y - ScreenUtils.dip2px(getContext(), 5);
		Rect popupTextRect = new Rect();
		popupTextPaint.getTextBounds(num, 0, num.length(), popupTextRect);
		Rect r = new Rect(x - popupTextRect.width() / 2 - sidePadding, y
				- popupTextRect.height() - bottomTriangleHeight
				- popupTopPadding * 2 - popupBottomMargin, x
				+ popupTextRect.width() / 2 + sidePadding, y + popupTopPadding
				- popupBottomMargin);

		NinePatchDrawable popup = (NinePatchDrawable) getResources()
				.getDrawable(R.drawable.popup_red);
		popup.setBounds(r);
		popup.draw(canvas);
		canvas.drawText(num, x, y - bottomTriangleHeight - popupBottomMargin,
				popupTextPaint);
	}

	private int getPopupHeight() {
		Rect popupTextRect = new Rect();
		popupTextPaint.getTextBounds("9", 0, 1, popupTextRect);
		Rect r = new Rect(-popupTextRect.width() / 2, -popupTextRect.height()
				- bottomTriangleHeight - popupTopPadding * 2
				- popupBottomMargin, +popupTextRect.width() / 2,
				+popupTopPadding - popupBottomMargin);
		return r.height();
	}

	private void drawDots(Canvas canvas) {
		Paint bigCirPaint = new Paint();
		bigCirPaint.setAntiAlias(true);
		bigCirPaint.setColor(Color.parseColor(dotColor));
		Paint smallCirPaint = new Paint(bigCirPaint);
		smallCirPaint.setColor(Color.parseColor("#FFFFFF"));
		if (drawDotList != null && !drawDotList.isEmpty()) {
			for (Dot dot : drawDotList) {
				canvas.drawCircle(dot.x, dot.y, DOT_OUTER_CIR_RADIUS,
						bigCirPaint);
				canvas.drawCircle(dot.x, dot.y, DOT_INNER_CIR_RADIUS,
						smallCirPaint);
			}
		}
//////////////////////////滑动绘制竖线/////////////////////////////////		
		Paint paint = new Paint();
		paint.setColor(Color.parseColor(lineColor));
		paint.setAntiAlias(true);
		if(0 == x_need_draw_by_move && drawDotList != null && drawDotList.size() > 0){
			x_need_draw_by_move = drawDotList.get(drawDotList.size()-1).x;
		}
		canvas.drawLine(x_need_draw_by_move, h_title, x_need_draw_by_move, mViewHeight - bottomTextTopMargin
				- bottomTextHeight - bottomTextDescent, paint);
//////////////////////////滑动绘制竖线/////////////////////////////////
	}

	private String lineColor = red;
	private String dotColor = red;

	public static String red = "#FF0033";
	public static String blue = "#25a9e0";

	public void setLineColor(String color) {
		this.lineColor = color;
		this.dotColor = color;
		postInvalidate();
	}

	private void drawLines(Canvas canvas) {
		Paint linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setColor(Color.parseColor(lineColor));
		linePaint.setStrokeWidth(ScreenUtils.dip2px(getContext(), 2));
		for (int i = 0; i < drawDotList.size() - 1; i++) {
			canvas.drawLine(drawDotList.get(i).x, drawDotList.get(i).y,
					drawDotList.get(i + 1).x, drawDotList.get(i + 1).y,
					linePaint);
		}
	}

	private void drawBackgroundLines(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(ScreenUtils.dip2px(getContext(), 1f));
		paint.setColor(BACKGROUND_LINE_COLOR);
		PathEffect effects = new DashPathEffect(new float[] { 10, 5, 10, 5 }, 1);

		// draw vertical lines
		for (int i = 0; i < xCoordinateList.size(); i++) {
			canvas.drawLine(xCoordinateList.get(i), h_title,
					xCoordinateList.get(i), mViewHeight - bottomTextTopMargin
							- bottomTextHeight - bottomTextDescent, paint);
		}

		// draw dotted lines
		// paint.setPathEffect(effects);
		// Path dottedPath = new Path();
		canvas.drawLine(0, h_title, getWidth(), h_title, paint);
		// for(int i=0;i<yCoordinateList.size();i++){
		// if((yCoordinateList.size()-1-i)%dataOfAGird == 0){
		// // dottedPath.moveTo(0, yCoordinateList.get(i));
		// // dottedPath.lineTo(getWidth(), yCoordinateList.get(i));
		// canvas.drawLine(0,yCoordinateList.get(i),getWidth(),yCoordinateList.get(i),paint);
		// // canvas.drawPath(dottedPath, paint);
		// }
		// }
		canvas.drawLine(0, mViewHeight - bottomTextTopMargin - bottomTextHeight
				- bottomTextDescent, getWidth(), mViewHeight
				- bottomTextTopMargin - bottomTextHeight - bottomTextDescent,
				paint);
	}

	private void drawWeekOval(Canvas canvas) {
		Paint week_oval_paint = new Paint();
		week_oval_paint.setColor(BACKGROUND_LINE_COLOR);//设置画笔颜色
		RectF r_f_week = new RectF();
		r_f_week.left = getWidth()/2-w_title/2-w_title/4;
		r_f_week.top = 0;
		r_f_week.right = getWidth()/2+w_title/2+w_title/4;
		r_f_week.bottom = h_title*2;
		
		canvas.drawOval(r_f_week, week_oval_paint); 
		
		week_oval_paint.setStrokeWidth(4); //
		week_oval_paint.setStyle(Style.STROKE);//设置填充类型
		canvas.drawOval(r_f_week, week_oval_paint); 
		
		canvas.drawText(s_title, getWidth() / 2 - (w_title) / 2, h_title/2*3, text_paint);
		
		// draw bottom text
		if (bottomTextList != null) {
			for (int i = 0; i < bottomTextList.size(); i++) {
				canvas.drawText(bottomTextList.get(i), backgroundGridWidth
						+ backgroundGridWidth * i, mViewHeight
						- bottomTextDescent, bottomTextPaint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mViewWidth = measureWidth(widthMeasureSpec);
		mViewHeight = measureHeight(heightMeasureSpec);
		refreshAfterDataChanged();
		setMeasuredDimension(mViewWidth, mViewHeight);
	}

	private int measureWidth(int measureSpec) {
		int horizontalGridNum = getHorizontalGridNum();
		int preferred = backgroundGridWidth * horizontalGridNum
				+ sideLineLength * 2;
		return getMeasurement(measureSpec, preferred);
	}

	private int measureHeight(int measureSpec) {
		int preferred = 0;
		return getMeasurement(measureSpec, preferred);
	}

	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement;
		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.EXACTLY:
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}
		return measurement;
	}
	
	int last_x_dispatch = 0;
	int now_x_dispatch = 0;
	int last_y_dispatch = 0;
	int now_y_dispatch = 0;
	
	boolean isYmove_dispatch = false;
	boolean isMoveToUp_dispatch = false;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			TLog.Log("zxl---line---dispatch---down--->"+isYmove_dispatch);
			isYmove_dispatch = false;
			last_x_dispatch = (int) ev.getX();
			last_y_dispatch = (int) ev.getY();
			return super.dispatchTouchEvent(ev);
		case MotionEvent.ACTION_MOVE:
			now_x_dispatch = (int) ev.getX();
			now_y_dispatch = (int) ev.getY();
			int deltaX = Math.abs(now_x_dispatch - last_x_dispatch);
			int deltaY = Math.abs(now_y_dispatch - last_y_dispatch);
			if(deltaY > deltaX){
				isYmove_dispatch = true;
			}
			TLog.Log("zxl---line---dispatch---move--->"+isYmove_dispatch);
			return super.dispatchTouchEvent(ev);
		case MotionEvent.ACTION_UP:
			TLog.Log("zxl---line---dispatch---up--->"+isYmove_dispatch);
			return super.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	/** 可以认为是滚动的最小距离 */
	private int mTouchSlop;

	
	int last_x = 0;
	int now_x = 0;
	int last_y = 0;
	int now_y = 0;
	boolean isXmove = false;
	int x_need_draw_by_move = 0;
	int index_by_move = -1;
	int index_last_by_move = -2;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		Point point = new Point();
		point.x = (int) event.getX();
		point.y = (int) event.getY();
		Region r = new Region();
		int width = backgroundGridWidth / 2;
		if (drawDotList != null || !drawDotList.isEmpty()) {
			/*
			 * for(Dot dot : drawDotList){
			 * r.set(dot.x-width,dot.y-width,dot.x+width,dot.y+width); if
			 * (r.contains(point.x,point.y) && event.getAction() ==
			 * MotionEvent.ACTION_DOWN){ selectedDot = dot; }else if
			 * (event.getAction() == MotionEvent.ACTION_UP){ if
			 * (r.contains(point.x,point.y)){ showPopup = true;
			 * actionDownListener.setOnActionDownListener(dot.x, dot.y); } } }
			 */

			for (int i = 0; i < drawDotList.size(); i++) {
				Dot dot = drawDotList.get(i);
				r.set(dot.x - width, dot.y - width, dot.x + width, dot.y
						+ width);
				if (r.contains(point.x, point.y)
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					selectedDot = dot;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if (r.contains(point.x, point.y)) {
						showPopup = false;
						if(flag_from_view == FLAG_FROM_MY_SPORTS_VIEW){
							lineActionDownFromMySportsListener.setOnLineActionDownFromMySportsListener(i);
						}else if(flag_from_view == FLAG_FROM_MY_SLEEP_VIEW){
							lineActionDownFromMySleepListener.setOnLineActionDownFromMySleepListener(i);
						}
					}
				}
			}
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_UP) {
			postInvalidate();
		}
		
////////////////////////////滑动绘制竖线/////////////////////////////////
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			isOnTouch = true;
			
			TLog.Log("zxl---line---oninter---down");
			last_x = (int) event.getX();
			last_y = (int) event.getY();
			isXmove = false;
			
			now_x = (int) event.getX();
			int x_min_down = backgroundGridWidth;
			index_by_move = -1;
			for (Dot dot : drawDotList) {
				if(Math.abs(now_x-dot.x)<=x_min_down){
					x_min_down = Math.abs(now_x-dot.x);
					x_need_draw_by_move = dot.x;
					index_by_move = drawDotList.indexOf(dot);
				}
			}
			
			postInvalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			TLog.Log("zxl---line---oninter---move");
			isOnTouch = true;
			now_x = (int) event.getX();
			now_y = (int) event.getY();
			int delta_x = now_x - last_x;
			int delta_y = now_y - last_y;
			
			if(Math.abs(delta_x) > 0){
				isXmove = true;
				int x_min = backgroundGridWidth;
				index_by_move = -1;
				for (Dot dot : drawDotList) {
					if(Math.abs(now_x-dot.x)<=x_min){
						x_min = Math.abs(now_x-dot.x);
//						x_need_draw_by_move = dot.x;
						index_by_move = drawDotList.indexOf(dot);
					}
				}
				x_need_draw_by_move = now_x;
			}
			postInvalidate();
			break;
		case MotionEvent.ACTION_UP:
			TLog.Log("zxl---line---oninter---up");
			now_x = (int) event.getX();
			int x_min = backgroundGridWidth;
			index_by_move = -1;
			for (Dot dot : drawDotList) {
				if(Math.abs(now_x-dot.x)<=x_min){
					x_min = Math.abs(now_x-dot.x);
					x_need_draw_by_move = dot.x;
					index_by_move = drawDotList.indexOf(dot);
				}
			}
			
			postInvalidate();
			isOnTouch = false;
			return true;
		case MotionEvent.ACTION_CANCEL:
			isOnTouch = false;
			break;
		}
		if(index_by_move > -1 && index_by_move != index_last_by_move){
			index_last_by_move = index_by_move;
			if(flag_from_view == FLAG_FROM_MY_SPORTS_VIEW){
				lineActionDownFromMySportsListener.setOnLineActionDownFromMySportsListener(index_by_move);
			}else if(flag_from_view == FLAG_FROM_MY_SLEEP_VIEW){
				lineActionDownFromMySleepListener.setOnLineActionDownFromMySleepListener(index_by_move);
			}
		}
//////////////////////////滑动绘制竖线/////////////////////////////////
		return true;
	}

	private LineActionDownFromMySportsListener lineActionDownFromMySportsListener;
	private LineActionDownFromMySleepListener lineActionDownFromMySleepListener;

	public void setLineActionDownFromMySportsListener(
			LineActionDownFromMySportsListener lineActionDownFromMySportsListener) {
		this.lineActionDownFromMySportsListener = lineActionDownFromMySportsListener;
	}
	
	public void setLineActionDownFromMySleepListener(LineActionDownFromMySleepListener lineActionDownFromMySleepListener) {
		this.lineActionDownFromMySleepListener = lineActionDownFromMySleepListener;
	}

	private int updateSelf(int origin, int target, int velocity) {
		if (origin < target) {
			origin += velocity;
		} else if (origin > target) {
			origin -= velocity;
		}
		if (Math.abs(target - origin) < velocity) {
			origin = target;
		}
		return origin;
	}

	class Dot {
		int x;
		int y;
		int data;
		int targetX;
		int targetY;
		int velocity = ScreenUtils.dip2px(getContext(), 10);

		Dot(int x, int y, int targetX, int targetY, Integer data) {
			this.x = x;
			this.y = y;
			setTargetData(targetX, targetY, data);
		}

		Point getPoint() {
			return new Point(x, y);
		}

		Dot setTargetData(int targetX, int targetY, Integer data) {
			this.targetX = targetX;
			this.targetY = targetY;
			this.data = data;
			return this;
		}

		boolean isAtRest() {
			return (x == targetX) && (y == targetY);
		}

		void update() {
			x = updateSelf(x, targetX, velocity);
			y = updateSelf(y, targetY, velocity);
		}
	}
}
