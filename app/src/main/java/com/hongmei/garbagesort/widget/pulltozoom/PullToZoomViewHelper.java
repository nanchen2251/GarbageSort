package com.hongmei.garbagesort.widget.pulltozoom;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

public abstract class PullToZoomViewHelper extends PullToZoomBase {
    protected ScalingRunnable mScalingRunnable;

    protected View mZoomView;
    protected View mRootView;
    protected int mZoomViewHeight;
    protected int mMaxZoomHeight = 0;
    protected long mAnimationDuration = 100L;

    protected PullToZoomViewHelper(Context context, View rootView, View zoomView) {
        super(context);
        mRootView = rootView;
        mZoomView = zoomView;
    }

    protected Interpolator mInterpolator = new Interpolator() {
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };

    @Override
    protected void init() {
        super.init();
        mScalingRunnable = new ScalingRunnable();
    }

    @Override
    protected void smoothScrollToTop() {
        mScalingRunnable.startAnimation(mAnimationDuration);
    }

    protected void setZoomViewHeight(int zoomViewHeight) {
        mZoomViewHeight = zoomViewHeight;
    }

    protected void setMaxScale(float maxScale) {
        if (maxScale == 0) {
            maxScale = 1;
        }
        mMaxZoomHeight = (int) (mZoomViewHeight * maxScale);
    }

    protected void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    protected void setInterpolator(Interpolator interpolator) {
        if (interpolator != null) {
            mInterpolator = interpolator;
        }
    }

    protected void setMaxZoomHeight(int maxZoomHeight) {
        mMaxZoomHeight = maxZoomHeight;
    }

    @Override
    protected void pullToZoom(int newScrollValue) {
        if (mScalingRunnable != null && !mScalingRunnable.isFinished()) {
            mScalingRunnable.abortAnimation();
        }

        int newHeight = Math.abs(newScrollValue) + mZoomViewHeight;
        if (mMaxZoomHeight != 0 && newHeight > mMaxZoomHeight) {
            return;
        }
        ViewGroup.LayoutParams zoomLayoutParams = mZoomView.getLayoutParams();
        zoomLayoutParams.height = newHeight;
        mZoomView.setLayoutParams(zoomLayoutParams);
    }

    class ScalingRunnable implements Runnable {
        protected long mDuration;
        protected boolean mIsFinished = true;
        protected float mScale;
        protected long mStartTime;

        ScalingRunnable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        @Override
        public void run() {
            if (mZoomView != null) {
                float f2;
                if ((!mIsFinished) && (mScale > 1.0D)) {
                    float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime) / (float) mDuration;
                    f2 = mScale - (mScale - 1.0F) * mInterpolator.getInterpolation(f1);
                    ViewGroup.LayoutParams zoomLayoutParams = mZoomView.getLayoutParams();
                    if (f2 > 1.0F) {
                        zoomLayoutParams.height = ((int) (f2 * mZoomViewHeight));
                        mZoomView.setLayoutParams(zoomLayoutParams);
                        mRootView.post(this);
                        return;
                    } else {
                        zoomLayoutParams.height = mZoomViewHeight;
                        mZoomView.setLayoutParams(zoomLayoutParams);
                    }
                    mIsFinished = true;
                }
            }
        }

        public void startAnimation(long paramLong) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis();
                mDuration = paramLong;
                mScale = mZoomViewHeight != 0 ? ((float) (mZoomView.getBottom()) / mZoomViewHeight) : 1f;
                mIsFinished = false;
                mRootView.post(this);
            }
        }
    }
}
