package com.ssc.ttmusic.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ssc.ttmusic.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
@SuppressLint("DrawAllocation")
public class LrcView extends View {
	private static final int SCROLL_TIME = 500;
	private static final String DEFAULT_TEXT = "暂无歌词";

	private static ArrayList<LyricSentence> mLyricSentences = new ArrayList<LyricSentence>();

	private final static Pattern mBracketPattern = Pattern
			.compile("(?<=\\[).*?(?=\\])");
	private final static Pattern mTimePattern = Pattern
			.compile("(?<=\\[)(\\d{2}:\\d{2}\\.?\\d{0,3})(?=\\])");

	private long mNextTime = 0l; // 保存下一句开始的时间

	private int mViewWidth; // view的宽度
	private int mLrcHeight; // lrc界面的高度
	private int mRows; // 多少行
	private int mCurrentLine = 0; // 当前行
	private int mOffsetY; // y上的偏移
	private int mMaxScroll; // 最大滑动距离=一行歌词高度+歌词间距

	private float mTextSize; // 字体
	private float mDividerHeight; // 行间距

	private Rect mTextBounds;

	private Paint mNormalPaint; // 常规的字体
	private Paint mCurrentPaint; // 当前歌词的大小

	private Bitmap mBackground;

	private Scroller mScroller;

	public LrcView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mScroller = new Scroller(context, new LinearInterpolator());
		inflateAttributes(attrs);
	}

	// 初始化操作
	private void inflateAttributes(AttributeSet attrs) {
		// <begin>
		// 解析自定义属性
		TypedArray ta = getContext().obtainStyledAttributes(attrs,
				R.styleable.Lrc);
		mTextSize = ta.getDimension(R.styleable.Lrc_textSize, 50.0f);
		mRows = ta.getInteger(R.styleable.Lrc_rows, 5);
		mDividerHeight = ta.getDimension(R.styleable.Lrc_dividerHeight, 0.0f);

		int normalTextColor = ta.getColor(R.styleable.Lrc_normalTextColor,
				0xffffffff);
		int currentTextColor = ta.getColor(R.styleable.Lrc_currentTextColor,
				0xff00ffde);
		ta.recycle();
		// </end>

		// 计算lrc面板的高度
		mLrcHeight = (int) (mTextSize + mDividerHeight) * mRows + 5;

		mNormalPaint = new Paint();
		mCurrentPaint = new Paint();

		// 初始化paint
		mNormalPaint.setTextSize(mTextSize);
		mNormalPaint.setColor(normalTextColor);
		mNormalPaint.setAntiAlias(true);
		mCurrentPaint.setTextSize(mTextSize);
		mCurrentPaint.setColor(currentTextColor);
		mCurrentPaint.setAntiAlias(true);

		mTextBounds = new Rect();
		mCurrentPaint.getTextBounds(DEFAULT_TEXT, 0, DEFAULT_TEXT.length(),
				mTextBounds);
		mMaxScroll = (int) (mTextBounds.height() + mDividerHeight);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 重新设置view的高度
		int measuredHeightSpec = MeasureSpec.makeMeasureSpec(mLrcHeight,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, measuredHeightSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 获取view宽度
		mViewWidth = getMeasuredWidth();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBackground != null) {
			canvas.drawBitmap(Bitmap.createScaledBitmap(mBackground,
					mViewWidth, mLrcHeight, true), new Matrix(), null);
		}

		// float centerY = (getMeasuredHeight() + mTextBounds.height() -
		// mDividerHeight) / 2;
		float centerY = (getMeasuredHeight() + mTextBounds.height()) / 2;
		if (mLyricSentences.size()<=0) {
			canvas.drawText(DEFAULT_TEXT,
					(mViewWidth - mCurrentPaint.measureText(DEFAULT_TEXT)) / 2,
					centerY, mCurrentPaint);
			return;
		}

		float offsetY = mTextBounds.height() + mDividerHeight;
		String currentLrc = mLyricSentences.get(mCurrentLine).getContentText();
		float currentX = (mViewWidth - mCurrentPaint.measureText(currentLrc)) / 2;
		// 画当前行
		canvas.drawText(currentLrc, currentX, centerY - mOffsetY, mCurrentPaint);

		int firstLine = mCurrentLine - mRows / 2;
		firstLine = firstLine <= 0 ? 0 : firstLine;
		int lastLine = mCurrentLine + mRows / 2 + 2;
		lastLine = lastLine >= mLyricSentences.size() - 1 ? mLyricSentences.size() - 1 : lastLine;

		// 画当前行上面的
		for (int i = mCurrentLine - 1, j = 1; i >= firstLine; i--, j++) {
			String lrc = mLyricSentences.get(i).getContentText();
			float x = (mViewWidth - mNormalPaint.measureText(lrc)) / 2;
			canvas.drawText(lrc, x, centerY - j * offsetY - mOffsetY,
					mNormalPaint);
		}

		// 画当前行下面的
		for (int i = mCurrentLine + 1, j = 1; i <= lastLine; i++, j++) {
			String lrc = mLyricSentences.get(i).getContentText();
			float x = (mViewWidth - mNormalPaint.measureText(lrc)) / 2;
			canvas.drawText(lrc, x, centerY + j * offsetY - mOffsetY,
					mNormalPaint);
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			mOffsetY = mScroller.getCurrY();
			if (mScroller.isFinished()) {
				int cur = mScroller.getCurrX();
				mCurrentLine = cur <= 1 ? 0 : cur - 1;
				mOffsetY = 0;
			}

			postInvalidate();
		}
	}
	private static String regTo(String content)
	{
		Pattern pattern=Pattern.compile("<.+?>");
		Matcher matcher=pattern.matcher(content);
		return matcher.replaceAll(" ");
	}
	public synchronized void changeCurrent(long time) {
		// 如果当前时间小于下一句开始的时间
		// 直接return
		if (mNextTime > time) {
			return;
		}

		// 每次进来都遍历存放的时间
		int timeSize = mLyricSentences.size();
		for (int i = 0; i < timeSize; i++) {

			// 解决最后一行歌词不能高亮的问题
			if (mNextTime == mLyricSentences.get(timeSize - 1).getStartTime()) {
				mNextTime += 60 * 1000;
				mScroller.abortAnimation();
				mScroller.startScroll(timeSize, 0, 0, mMaxScroll, SCROLL_TIME);
				// mNextTime = mTimes.get(i);
				// mCurrentLine = i <= 1 ? 0 : i - 1;
				postInvalidate();
				return;
			}

			if (mLyricSentences.get(i).getStartTime() >= time) {
				mNextTime = mLyricSentences.get(i).getStartTime();
				mScroller.abortAnimation();
				mScroller.startScroll(i, 0, 0, mMaxScroll, SCROLL_TIME);
				postInvalidate();
				return;
			}
		}
	}

	// 外部提供方法
	// 拖动进度条时
	public void onDrag(int progress) {
		for (int i = 0; i < mLyricSentences.size(); i++) {
			if (mLyricSentences.get(i).getStartTime() > progress) {
				mNextTime = i == 0 ? 0 : mLyricSentences.get(i - 1)
						.getStartTime();
				return;
			}
		}
	}

	// 外部提供方法
	// 设置lrc的路径
	public void setLrcPath(String path) {
		reset();
		String data = "";
		File file = new File(path);
		if (!file.exists()) {
			postInvalidate();
			return;
		}
		try {
			FileInputStream stream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					stream, "utf-8"));

			while ((data = br.readLine()) != null) {
				parseLine(data);

			}

			stream.close();

		} catch (Exception e) {

		}

		Collections.sort(mLyricSentences, new Comparator<LyricSentence>() {
			public int compare(LyricSentence object1, LyricSentence object2) {
				if (object1.getStartTime() > object2.getStartTime()) {
					return 1;
				} else if (object1.getStartTime() < object2.getStartTime()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
	}

	private static void parseLine(String line) {
		if (line.equals("")) {
			return;
		}
		String content = null;
		int timeLength = 0;
		int index = 0;
		Matcher matcher = mTimePattern.matcher(line);
		int lastIndex = -1;
		int lastLength = -1;

		List<String> times = new ArrayList<String>();

		while (matcher.find()) {
			String s = matcher.group();
			index = line.indexOf("[" + s + "]");
			if (lastIndex != -1 && index - lastIndex > lastLength + 2) {
				content = trimBracket(line.substring(
						lastIndex + lastLength + 2, index));
				for (String string : times) {
					long t = parseTime(string);
					if (t != -1) {

						mLyricSentences.add(new LyricSentence(t, regTo(content)));
					}
				}
				times.clear();
			}
			times.add(s);
			lastIndex = index;
			lastLength = s.length();
		}
		if (times.isEmpty()) {
			return;
		}

		timeLength = lastLength + 2 + lastIndex;
		if (timeLength > line.length()) {
			content = trimBracket(line.substring(line.length()));
		} else {
			content = trimBracket(line.substring(timeLength));
		}
		for (String s : times) {
			long t = parseTime(s);
			if (t != -1) {
				mLyricSentences.add(new LyricSentence(t, regTo(content)));
			}
		}

	}

	@SuppressLint("DefaultLocale")
	private static long parseTime(String strTime) {
		String beforeDot = new String("00:00:00");
		String afterDot = new String("0");

		int dotIndex = strTime.indexOf(".");
		if (dotIndex < 0) {
			beforeDot = strTime;
		} else if (dotIndex == 0) {
			afterDot = strTime.substring(1);
		} else {
			beforeDot = strTime.substring(0, dotIndex);
			afterDot = strTime.substring(dotIndex + 1);
		}

		long intSeconds = 0;
		int counter = 0;
		while (beforeDot.length() > 0) {
			int colonPos = beforeDot.indexOf(":");
			try {
				if (colonPos > 0) {
					intSeconds *= 60;
					intSeconds += Integer.valueOf(beforeDot.substring(0,
							colonPos));
					beforeDot = beforeDot.substring(colonPos + 1);
				} else if (colonPos < 0) {
					intSeconds *= 60;
					intSeconds += Integer.valueOf(beforeDot);
					beforeDot = "";
				} else {
					return -1;
				}
			} catch (NumberFormatException e) {
				return -1;
			}
			++counter;
			if (counter > 3) {
				return -1;
			}
		}

		String totalTime = String.format("%d.%s", intSeconds, afterDot);
		Double doubleSeconds = Double.valueOf(totalTime);
		return (long) (doubleSeconds * 1000);
	}

	private static String trimBracket(String content) {
		String s = null;
		String result = content;
		Matcher matcher = mBracketPattern.matcher(content);
		while (matcher.find()) {
			s = matcher.group();
			result = result.replace("[" + s + "]", "");
		}
		return result;
	}

	private void reset() {
		mLyricSentences.clear();
		mCurrentLine = 0;
		mNextTime = 0l;
	}

	// 是否设置歌词
	public boolean hasLrc() {
		return mLyricSentences.size() > 0;
	}
	public void setBackground(Bitmap bmp) {
		mBackground = bmp;
	}
}
