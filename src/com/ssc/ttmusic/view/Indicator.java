package com.ssc.ttmusic.view;

import com.ssc.ttmusic.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;


	public class Indicator extends LinearLayout {
		private Paint mPaint; // 画指示符的paint
		
		private int mTop; // 指示符的top
		private int mLeft; // 指示符的left
		private int mWidth; // 指示符的width
		private int mHeight; // 指示符的高度
		private int mColor; // 指示符的颜色
		private int mChildCount; // 子item的个数，用于计算指示符的宽度
		private static final int DEFAULT_HEIGHT=3;
		public Indicator(Context context, AttributeSet attrs) {
			super(context, attrs);
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Indicator, 0, 0);
			mColor = ta.getColor(R.styleable.Indicator_color, Color.WHITE);
			mHeight =  (int)ta.getDimension(R.styleable.Indicator_height, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					DEFAULT_HEIGHT, getResources().getDisplayMetrics()));
			ta.recycle();
			
			// 初始化paint
			mPaint = new Paint();
			mPaint.setColor(mColor);
			mPaint.setAntiAlias(true);
		}
		
		@Override
		protected void onFinishInflate() {
			super.onFinishInflate();
			mChildCount = getChildCount();  // 获取子item的个数
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			mTop = getMeasuredHeight(); // 测量的高度即指示符的顶部位置
			int width = getMeasuredWidth(); // 获取测量的总宽度
			int height = mTop + mHeight; // 重新定义一下测量的高度
			if(mChildCount>0)
			{
				mWidth = width / mChildCount;// 指示符的宽度为总宽度/item的个数
			}
			 
			
			setMeasuredDimension(width, height);
		}
		
		/**
		 * 指示符滚动
		 * @param position 现在的位置
		 * @param offset  偏移量 0 ~ 1
		 */
		public void scroll(int position, float offset) {
			mLeft = (int) ((position + offset) * mWidth);
			invalidate();
		}
		
		@Override
		protected void dispatchDraw(Canvas canvas) {
			// 圈出一个矩形
			Rect rect = new Rect(mLeft, mTop, mLeft + mWidth, mTop + mHeight);
			canvas.drawRect(rect, mPaint); // 绘制该矩形
			
			super.dispatchDraw(canvas);
		}
	}
