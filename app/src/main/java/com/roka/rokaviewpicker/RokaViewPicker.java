package com.roka.rokaviewpicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by roka on 2016. 12. 21..
 */

public class RokaViewPicker<T extends View> extends ScrollView implements View.OnTouchListener{

    private OnPickerScrollStartListener mOnPickerScrollStartListener;
    private OnPickerScrollEndListener mOnPickerScrollEndListener;
    private OnPickerScrollChangeListener mOnPickerScrollChangeListener;


    private Context mContext;
    private LinearLayout mInsideLinearLayout;

    private View mVirtualStartView;
    private View mVirtualEndView;
    private T mFirstView;
    private int mInsertIndex;
    private int mCurrentIndex = 1;

    private boolean mAutoScroll = true;

    private RokaViewPicker<T> mRootView;

    public RokaViewPicker(Context context) {
        this(context, null);
    }

    public RokaViewPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RokaViewPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initUi();
    }

    @TargetApi(23)
    public RokaViewPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUi();
    }

    private long lastScrollUpdate = -1;


    private void initUi() {
        mRootView = this;
        mRootView.setOnTouchListener(this);
        setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        mInsideLinearLayout = new LinearLayout(mContext);

        mInsideLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mInsideLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.addView(mInsideLinearLayout);
    }

    private ViewTreeObserver.OnGlobalLayoutListener virtualViewObserver = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mFirstView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            mVirtualStartView.setLayoutParams(new LinearLayout.LayoutParams(mFirstView.getWidth(), mFirstView.getHeight()));
            mVirtualEndView.setLayoutParams(new LinearLayout.LayoutParams(mFirstView.getWidth(), mFirstView.getHeight()));
            mRootView.setLayoutParams(new LinearLayout.LayoutParams(mFirstView.getWidth(), mFirstView.getHeight()*3));

        }
    };




    public void addPickerView(T view) {
        int childCount = mInsideLinearLayout.getChildCount();
        if (childCount == 0) {
            mVirtualStartView = new View(mContext);
            mVirtualEndView = new View(mContext);
            mFirstView = view;
            view.getViewTreeObserver().addOnGlobalLayoutListener(virtualViewObserver);
            mInsideLinearLayout.addView(mVirtualStartView);
            mInsideLinearLayout.addView(view);
            mInsideLinearLayout.addView(mVirtualEndView);
            mInsertIndex = 2;
        } else {
            mInsideLinearLayout.addView(view, mInsertIndex);
            mInsertIndex++;
        }

    }

    public void addPickerView(T view, int position) {
        int childCount = mInsideLinearLayout.getChildCount();
        if (childCount == 0) {
            mVirtualStartView = new View(mContext);
            mVirtualEndView = new View(mContext);
            mFirstView = view;
            view.getViewTreeObserver().addOnGlobalLayoutListener(virtualViewObserver);
            mInsideLinearLayout.addView(mVirtualStartView);
            mInsideLinearLayout.addView(view);
            mInsideLinearLayout.addView(mVirtualEndView);
            mInsertIndex = 2;
        } else {
            mInsideLinearLayout.addView(view, position+1);
            mInsertIndex++;
        }
    }

    public void removePickerAllView() {
        mInsideLinearLayout.removeAllViews();
    }

    public void removePickerView(int position) {
        View mChildView = mInsideLinearLayout.getChildAt(position + 1);
        mInsideLinearLayout.removeView(mChildView);
        mInsertIndex--;
    }

    public void removePickerView(View view) {
        mInsideLinearLayout.removeView(view);
        mInsertIndex--;
    }

    public void setOnPickerScrollStartListener(OnPickerScrollStartListener onPickerScrollStartListener) {
        this.mOnPickerScrollStartListener = onPickerScrollStartListener;
    }

    public void setOnPickerScrollEndListener(OnPickerScrollEndListener onPickerScrollEndListener) {
        this.mOnPickerScrollEndListener = onPickerScrollEndListener;
    }

    public void setOnPickerScrollChangeListener(OnPickerScrollChangeListener onPickerScrollChangeListener) {
        this.mOnPickerScrollChangeListener = onPickerScrollChangeListener;
    }

    public void setSelectPicker(final int position) {
        try {
            final int childHeight = mInsideLinearLayout.getChildAt(1).getHeight();
            mRootView.post(new Runnable() {
                @Override
                public void run() {
                    mRootView.scrollTo(0, (position * childHeight));
                }
            });

        } catch (NullPointerException e) {
            Log.e("RokaViewPicker", "setSelectPicker:"+e.toString());
        }
    }

    public int getCurrentPicker() {
        return mCurrentIndex;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // TODO : Don't Override Please....:)
        super.onScrollChanged(l, t, oldl, oldt);
        lastScrollUpdate = System.currentTimeMillis();
        if (mOnPickerScrollChangeListener != null) {
            mOnPickerScrollChangeListener.onPickerScrollChangeListener(getScrollX(), getScrollY());
        }
    }


    private void onScrollStart() {
        try {
            int childHeight = mInsideLinearLayout.getChildAt(1).getHeight();
            int index = this.getScrollY() / childHeight;
            View indexView = mInsideLinearLayout.getChildAt(index);
            if (mAutoScroll) {
                if (mOnPickerScrollStartListener != null) {
                    mOnPickerScrollStartListener.onPickerScrollStartListener(indexView, index);
                }
            }
        } catch (Exception e) {
            Log.e("RokaViewPicker", "onScrollStart:"+e.toString());
        }
    }

    private void onScrollEnd() {
        try {
            int childHeight = mInsideLinearLayout.getChildAt(1).getHeight();
            int index = this.getScrollY() / childHeight;
            View indexView = mInsideLinearLayout.getChildAt(index);
            if (mAutoScroll) {
                mAutoScroll = false;
                if (this.getScrollY() - index * childHeight <= (index + 1) * childHeight - getScrollY()) {
                    smoothScrollTo(0, index * childHeight);
                } else {
                    smoothScrollTo(0, (index + 1) * childHeight);
                    index++;
                }
                if (mOnPickerScrollEndListener != null) {
                    mOnPickerScrollEndListener.onPickerScrollEndListener(indexView, index);
                }
            }
            mCurrentIndex = index;
        } catch (Exception e) {
            Log.e("RokaViewPicker", "onScrollEnd:"+e.toString());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAutoScroll = true;
                onScrollStart();
                ((ScrollView)v).requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                postDelayed(new ScrollStateHandler(), 100);
                ((ScrollView)v).requestDisallowInterceptTouchEvent(false);
                break;
        }
        return false;
    }



    private class ScrollStateHandler implements Runnable {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > 50) {
                lastScrollUpdate = -1;
                onScrollEnd();
            } else {
                postDelayed(this, 100);
            }
        }
    }

}

