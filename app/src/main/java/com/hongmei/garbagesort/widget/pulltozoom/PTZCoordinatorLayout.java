package com.hongmei.garbagesort.widget.pulltozoom;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.hongmei.garbagesort.R;
import com.hongmei.garbagesort.ext.ScreenUtilKt;

import org.jetbrains.annotations.NotNull;

/**
 * 支持 pull to zoom 的 CoordinatorLayout
 */
public class PTZCoordinatorLayout extends CoordinatorLayout {
    private int mZoomId;
    private View mZoomView;
    private int mZoomViewHeight;
    private float mMaxScale;
    private boolean mIsMove = false;

    private PTZCoordinatorLayoutHelper mPTZLayoutHelper;

    public PTZCoordinatorLayout(Context context) {
        this(context, null);
    }

    public PTZCoordinatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PTZCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs, defStyleAttr);
    }

    public void performTouch(){
        long downTime = SystemClock.uptimeMillis();
        int x = ScreenUtilKt.getScreenWidth() / 2;
        int y = ScreenUtilKt.getScreenHeight() / 2;
        MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        dispatchTouchEvent(downEvent);
        downEvent.recycle();
    }

    public boolean isMove() {
        return mIsMove;
    }

    protected void parseAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullToZoomLayout, defStyleAttr, 0);
        mZoomId = typedArray.getResourceId(R.styleable.PullToZoomLayout_pull_to_zoom_id, 0);
        mMaxScale = typedArray.getFloat(R.styleable.PullToZoomLayout_pull_to_zoom_max_scale, 0);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mZoomId > 0) {
            mZoomView = findViewById(mZoomId);
            init(getContext());
        }
    }

    public void setZoomView(@NotNull View view){
        mZoomView = view;
        mZoomViewHeight = mZoomView.getHeight();
        init(getContext());
    }

    protected void init(Context context) {
        mPTZLayoutHelper = new PTZCoordinatorLayoutHelper(context, this, mZoomView);
        mPTZLayoutHelper.setZoomViewHeight(mZoomViewHeight);
        mPTZLayoutHelper.setMaxScale(mMaxScale);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mPTZLayoutHelper != null) {
            mPTZLayoutHelper.onScrollChange(l, t, oldl, oldt);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            mIsMove = true;
        }else {
            mIsMove = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = mPTZLayoutHelper.onInterceptTouchEvent(ev);
        if (!result) {
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = mPTZLayoutHelper.onTouchEvent(ev);
        if (!result) {
            return super.onTouchEvent(ev);
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mZoomViewHeight == 0 && mZoomView != null) {
            mZoomViewHeight = mZoomView.getHeight();
            if (mPTZLayoutHelper != null) {
                mPTZLayoutHelper.setZoomViewHeight(mZoomViewHeight);
                mPTZLayoutHelper.setMaxScale(mMaxScale);
            }
        }
    }
}
