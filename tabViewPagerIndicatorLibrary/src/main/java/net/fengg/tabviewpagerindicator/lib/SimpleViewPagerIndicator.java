package net.fengg.tabviewpagerindicator.lib;

import java.util.ArrayList;
import java.util.List;

import net.fengg.tabviewpagerindicator.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class SimpleViewPagerIndicator extends LinearLayout
{

	private static final int COLOR_INDICATOR_COLOR = Color.GREEN;

	private String[] mTitles;
	private int mIndicatorColor = COLOR_INDICATOR_COLOR;
	private float mTranslationX;
	private Paint mPaint = new Paint();
	private static final int COUNT_DEFAULT_TAB = 3;
	private int mTabVisibleCount = COUNT_DEFAULT_TAB;
	private List<ColorTrackView> mTabs = new ArrayList<ColorTrackView>();  
	
	private int mTextSize = 25;
	private int mTextOriginColor = 0xff000000;
	private int mTextChangeColor = 0xffff0000;
	
	public ViewPager mViewPager;

	public SimpleViewPagerIndicator(Context context)
	{
		this(context, null);
	}

	public SimpleViewPagerIndicator(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicator);
		mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_item_count,
				COUNT_DEFAULT_TAB);
		if (mTabVisibleCount < 0)
			mTabVisibleCount = COUNT_DEFAULT_TAB;
		a.recycle();
		
		mPaint.setColor(mIndicatorColor);
		mPaint.setStrokeWidth(9.0F);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void setTitles(String[] titles)
	{
		mTitles = titles;
		generateTitleView();
	}

	public void setIndicatorColor(int indicatorColor)
	{
		this.mIndicatorColor = indicatorColor;
	}
	
	public void setVisibleTabCount(int count)
	{
		this.mTabVisibleCount = count;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		canvas.save();
		canvas.translate(mTranslationX, getHeight() - 2);
		canvas.drawLine(0, 0, getScreenWidth() / mTabVisibleCount, 0, mPaint);
		canvas.restore();
	}

	public void scroll(int position, float offset)
	{
		/**
		 * <pre>
		 *  0-1:position=0 ;1-0:postion=0;
		 * </pre>
		 */
		mTranslationX = getWidth() / mTabVisibleCount * (position + offset);

		if(offset > 0 && position < (mTabs.size() - 1)) {
			ColorTrackView left = mTabs.get(position);    
            ColorTrackView right = mTabs.get(position + 1);    
              
            left.setDirection(1);  
            right.setDirection(0);  
            left.setProgress( 1-offset);    
            right.setProgress(offset);
		}
		
		if (offset > 0 && position >= (mTabVisibleCount - 2)
				&& getChildCount() > mTabVisibleCount)
		{
			int tabWidth = getScreenWidth() / mTabVisibleCount;
			
			if (mTabVisibleCount != 1)
			{
				this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth
						+ (int) (tabWidth * offset), 0);
			} else
			{
				this.scrollTo(
						position * tabWidth + (int) (tabWidth * offset), 0);
			}
		}

		invalidate();
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		return super.dispatchTouchEvent(ev);
	}

	private void generateTitleView()
	{
		if (getChildCount() > 0)
			this.removeAllViews();
		int count = mTitles.length;

		mTabVisibleCount = Math.min(count, mTabVisibleCount);
		
		for (int i = 0; i < count; i++)
		{
			int progress = 0;
			if(i == 0) {
				progress = 1;
			}
			
			ColorTrackView tv = new ColorTrackView(getContext(), mTitles[i], mTextSize, mTextOriginColor, mTextChangeColor, progress, 0);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lp.width = getScreenWidth() / mTabVisibleCount;
			tv.setLayoutParams(lp);
			
			addView(tv);
			mTabs.add((ColorTrackView) tv);
		}
		setItemClickEvent();
	}

	public interface PageChangeListener
	{
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		public void onPageSelected(int position);

		public void onPageScrollStateChanged(int state);
	}
	
		private PageChangeListener onPageChangeListener;

		public void setOnPageChangeListener(PageChangeListener pageChangeListener)
		{
			this.onPageChangeListener = pageChangeListener;
		}

		public void setViewPager(ViewPager mViewPager, int pos)
		{
			this.mViewPager = mViewPager;

			mViewPager.setOnPageChangeListener(new OnPageChangeListener()
			{
				@Override
				public void onPageSelected(int position)
				{
					if (onPageChangeListener != null)
					{
						onPageChangeListener.onPageSelected(position);
					}
				}

				@Override
				public void onPageScrolled(int position, float positionOffset,
						int positionOffsetPixels)
				{
					scroll(position, positionOffset);

					if (onPageChangeListener != null)
					{
						onPageChangeListener.onPageScrolled(position,
								positionOffset, positionOffsetPixels);
					}

				}

				@Override
				public void onPageScrollStateChanged(int state)
				{
					if (onPageChangeListener != null)
					{
						onPageChangeListener.onPageScrollStateChanged(state);
					}

				}
			});
			mViewPager.setCurrentItem(pos);
		}

		public void setItemClickEvent()
		{
			int cCount = getChildCount();
			for (int i = 0; i < cCount; i++)
			{
				final int j = i;
				View view = getChildAt(i);
				view.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						mViewPager.setCurrentItem(j);
					}
				});
			}
		}
		
		@Override
		protected void onFinishInflate()
		{
			super.onFinishInflate();

			int cCount = getChildCount();

			if (cCount == 0)
				return;

			mTabVisibleCount = Math.min(cCount, mTabVisibleCount);
			
			for (int i = 0; i < cCount; i++)
			{
				View view = getChildAt(i);
				LinearLayout.LayoutParams lp = (LayoutParams) view
						.getLayoutParams();
				lp.width = getScreenWidth() / mTabVisibleCount;
				view.setLayoutParams(lp);
				mTabs.add((ColorTrackView) view);
			}
			setItemClickEvent();

		}
		
		public int getScreenWidth()
		{
			WindowManager wm = (WindowManager) getContext().getSystemService(
					Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);
			return outMetrics.widthPixels;
		}

		public int getTextSize() {
			return mTextSize;
		}

		/**
		 * 需在setTitles之前设置
		 * @param textSize
		 */
		public void setTextSize(int textSize) {
			this.mTextSize = textSize;
		}

		public int getTextOriginColor() {
			return mTextOriginColor;
		}

		public void setTextOriginColor(int textOriginColor) {
			this.mTextOriginColor = textOriginColor;
		}

		public int getTextChangeColor() {
			return mTextChangeColor;
		}

		public void setmTextChangeColor(int textChangeColor) {
			this.mTextChangeColor = textChangeColor;
		}
}
