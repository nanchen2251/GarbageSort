package com.hongmei.garbagesort.widget.pulltozoom;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.lang.ref.WeakReference;

public abstract class PullToZoomBase {
    private static float FRICTION = 2.0f;

    protected WeakReference<Context> mContext;

    protected int mTouchSlop;
    protected boolean mEnabled = true;
    protected boolean mIsZooming = false;
    protected boolean mIsDragging = false;
    protected float mLastMotionY;
    protected float mLastMotionX;
    protected float mInitialMotionY;
    protected float mInitialMotionX;

    protected OnPullZoomListener mOnPullZoomListener;

    public PullToZoomBase(Context context) {
        mContext = new WeakReference<>(context);

        init();
    }

    protected void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(mContext.get());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public boolean isEnable() {
        return mEnabled;
    }

    public void enable(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isZooming() {
        return mIsZooming;
    }

    public void setOnPullZoomListener(OnPullZoomListener onPullZoomListener) {
        mOnPullZoomListener = onPullZoomListener;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isEnable()) {
            return false;
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsDragging = false;
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (isReadyForPullStart()) {
                    final float y = event.getY(), x = event.getX();
                    final float diff, oppositeDiff, absDiff;

                    // We need to use the correct values, based on scroll
                    // direction
                    diff = y - mLastMotionY;
                    oppositeDiff = x - mLastMotionX;
                    absDiff = Math.abs(diff);

                    if (absDiff > mTouchSlop && absDiff > Math.abs(oppositeDiff)) {
                        if (diff >= 1f && isReadyForPullStart()) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsDragging = true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionY = event.getY();
                mInitialMotionX = event.getX();
                if (isReadyForPullStart()) {
                    mLastMotionY = mInitialMotionY;
                    mLastMotionX = mInitialMotionX;
                    mIsDragging = false;
                }
                break;
            }
            default:
                break;
        }
        return mIsDragging;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnable()) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                if (!mIsDragging) {
                    onInterceptTouchEvent(event);
                }
                if (mIsDragging) {
                    mLastMotionY = event.getY();
                    mLastMotionX = event.getX();
                    smoothPullToZoom();
                    mIsZooming = true;
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPullStart()) {
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = mInitialMotionX = event.getX();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mIsDragging) {
                    mIsDragging = false;
                    // If we're already refreshing, just scroll back to the top
                    if (isZooming()) {
                        smoothScrollToTop();
                        if (mOnPullZoomListener != null) {
                            mOnPullZoomListener.onPullZoomEnd();
                        }
                        mIsZooming = false;
                        return true;
                    }
                    return true;
                }
                break;
            }
            default:
                break;
        }
        return false;
    }

    protected void smoothPullToZoom() {
        final int newScrollValue;
        final float initialMotionValue, lastMotionValue;

        initialMotionValue = mInitialMotionY;
        lastMotionValue = mLastMotionY;

        newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0) / FRICTION);

        pullToZoom(newScrollValue);
        if (mOnPullZoomListener != null) {
            mOnPullZoomListener.onPullZooming(newScrollValue);
        }
    }

    protected abstract boolean isReadyForPullStart();

    protected abstract void smoothScrollToTop();

    protected abstract void pullToZoom(int newScrollValue);

    public interface OnPullZoomListener {
        void onPullZooming(int newScrollValue);

        void onPullZoomEnd();
    }
}
