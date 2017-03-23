package net.fengg.tabviewpagerindicator.lib;

import net.fengg.tabviewpagerindicator.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 
 *
 */
public class ColorTrackView extends View
{

	private int mTextStartX;

	private int mDirection = DIRECTION_LEFT; 
	
	private static final int  DIRECTION_LEFT = 0 ; 
	
	public void setDirection(int direction)
	{
		mDirection = direction;
	}
	
	private String mText;
	private Paint mPaint;
	private int mTextSize = sp2px(30);

	private int mTextOriginColor = 0xff000000;
	private int mTextChangeColor = 0xffff0000;

	private Rect mTextBound = new Rect();
	private int mTextWidth;

	private int mRealWidth;

	private float mProgress;

	public ColorTrackView(Context context, 
			String mText, int mTextSize, 
			int mTextOriginColor, int mTextChangeColor, float mProgress, int mDirection)
	{
		super(context, null);
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mText = mText;
		this.mTextSize = mTextSize;
		this.mTextOriginColor = mTextOriginColor;
		this.mTextChangeColor = mTextChangeColor;
		this.mProgress = mProgress;
		this.mDirection = mDirection;
		
		mPaint.setTextSize(mTextSize);
		measureText();
	}

	public ColorTrackView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.ColorTrackView);
		mText = ta.getString(R.styleable.ColorTrackView_text);
		mTextSize = ta.getDimensionPixelSize(
				R.styleable.ColorTrackView_text_size, mTextSize);
		mTextOriginColor = ta.getColor(
				R.styleable.ColorTrackView_text_origin_color,
				mTextOriginColor);
		mTextChangeColor = ta.getColor(
				R.styleable.ColorTrackView_text_change_color,
				mTextChangeColor);
		mProgress = ta.getFloat(R.styleable.ColorTrackView_progress, 0);
		
		mDirection = ta.getInt(R.styleable.ColorTrackView_direction, mDirection);
		
		ta.recycle();

		mPaint.setTextSize(mTextSize);
		measureText();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);

		mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		mTextStartX = mRealWidth / 2 - mTextWidth / 2;

	}

	private int measureHeight(int measureSpec)
	{
		int mode = MeasureSpec.getMode(measureSpec);
		int val = MeasureSpec.getSize(measureSpec);
		int result = 0;
		switch (mode)
		{
		case MeasureSpec.EXACTLY:
			result = val;
			break;
		case MeasureSpec.AT_MOST:
		case MeasureSpec.UNSPECIFIED:
			result = mTextBound.height();
			break;
		}
		result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
		return result + getPaddingTop() + getPaddingBottom();
	}

	private int measureWidth(int measureSpec)
	{
		int mode = MeasureSpec.getMode(measureSpec);
		int val = MeasureSpec.getSize(measureSpec);
		int result = 0;
		switch (mode)
		{
		case MeasureSpec.EXACTLY:
			result = val;
			break;
		case MeasureSpec.AT_MOST:
		case MeasureSpec.UNSPECIFIED:
			result = mTextWidth;
			break;
		}
		result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
		return result + getPaddingLeft() + getPaddingRight();
	}

	private void measureText()
	{
		mTextWidth = (int) mPaint.measureText(mText);
		mPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
	}
	
	public void reverseColor()
	{
		int tmp = mTextOriginColor ; 
		mTextOriginColor = mTextChangeColor ; 
		mTextChangeColor = tmp ;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int r = (int) (mProgress* mTextWidth +mTextStartX );
		
		if(mDirection == DIRECTION_LEFT)
		{
			drawChangeLeft(canvas, r);
			drawOriginLeft(canvas, r);
		}else
		{
			 drawOriginRight(canvas, r);
			 drawChangeRight(canvas, r);
		}
		

	}
	
	private void drawText(Canvas canvas , int color , int startX , int endX)
	{
		mPaint.setColor(color);
		canvas.save(Canvas.CLIP_SAVE_FLAG);
		canvas.clipRect(startX, 0, endX, getMeasuredHeight());
		canvas.drawText(mText, mTextStartX, getMeasuredHeight() / 2
				+ mTextBound.height() / 2, mPaint);
		canvas.restore();
	}
	
	private void drawChangeRight(Canvas canvas, int r)
	{
		drawText(canvas, mTextChangeColor, (int) (mTextStartX +(1-mProgress)*mTextWidth), mTextStartX+mTextWidth );
	}
	private void drawOriginRight(Canvas canvas, int r)
	{
		drawText(canvas, mTextOriginColor, mTextStartX, (int) (mTextStartX +(1-mProgress)*mTextWidth) );
	}

	private void drawChangeLeft(Canvas canvas, int r)
	{
		drawText(canvas, mTextChangeColor, mTextStartX, (int) (mTextStartX + mProgress * mTextWidth) );
	}

	private void drawOriginLeft(Canvas canvas, int r)
	{
		drawText(canvas, mTextOriginColor, (int) (mTextStartX + mProgress * mTextWidth), mTextStartX +mTextWidth );
	}

	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		this.mText = text;
	}
	
	public float getProgress()
	{
		return mProgress;
	}

	public void setProgress(float progress)
	{
		this.mProgress = progress;
		invalidate();
	}

	public int getTextSize()
	{
		return mTextSize;
	}

	public void setTextSize(int mTextSize)
	{
		this.mTextSize = mTextSize;
		requestLayout();
		invalidate();
	}

	public int getTextOriginColor()
	{
		return mTextOriginColor;
	}

	public void setTextOriginColor(int mTextOriginColor)
	{
		this.mTextOriginColor = mTextOriginColor;
		invalidate();
	}

	public int getTextChangeColor()
	{
		return mTextChangeColor;
	}

	public void setTextChangeColor(int mTextChangeColor)
	{
		this.mTextChangeColor = mTextChangeColor;
		invalidate();
	}

	private int sp2px(float dpVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				dpVal, getResources().getDisplayMetrics());
	}
}