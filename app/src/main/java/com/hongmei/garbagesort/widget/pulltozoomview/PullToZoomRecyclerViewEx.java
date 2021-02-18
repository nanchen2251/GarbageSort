package com.hongmei.garbagesort.widget.pulltozoomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;


/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2014/11/7  18:01.
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2014/11/7        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public class PullToZoomRecyclerViewEx extends PullToZoomBase<RecyclerView> {
    private static final String TAG = PullToZoomRecyclerViewEx.class.getSimpleName();
    private FrameLayout mHeaderContainer;
    private int mHeaderContainerHeight;
    private int mZoomViewHeight;
    private ScalingRunnable mScalingRunnable;
    private float lastX;
    private float lastY;
    private float xDistance;
    private float yDistance;
    private float minHeaderHeight = 0;

    public FrameLayout getHeaderContainer() {
        return mHeaderContainer;
    }

    private static final Interpolator sSmoothToTopInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };

    public PullToZoomRecyclerViewEx(Context context) {
        this(context, null);
    }

    public PullToZoomRecyclerViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScalingRunnable = new ScalingRunnable();
    }

    public RecyclerView getRecyclerView() {
        return mRootView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if (xDistance * 2.5 > yDistance) {
                    return false;
                }
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 是否显示headerView
     *
     * @param isHideHeader true: show false: hide
     */
    @Override
    public void setHideHeader(boolean isHideHeader) {
        if (isHideHeader != isHideHeader()) {
            super.setHideHeader(isHideHeader);
            if (isHideHeader) {
                removeHeaderView();
            } else {
                updateHeaderView();
            }
        }
    }

    @Override
    public void setHeaderView(View headerView) {
        if (headerView != null) {
            this.mHeaderView = headerView;
            updateHeaderView();
        }
    }

    @Override
    public void setZoomView(View zoomView) {
        if (zoomView != null) {
            this.mZoomView = zoomView;
            updateHeaderView();
        }
    }

    @Override
    public void setZoomViewAndHeaderView(View zoomView, View headerView) {
        if (zoomView != null && headerView != null && mRootView.getAdapter() instanceof BaseQuickAdapter) {
            this.mZoomView = zoomView;
            this.mHeaderView = headerView;
            BaseQuickAdapter adapter = (BaseQuickAdapter) mRootView.getAdapter();
            if (adapter.getHeaderLayout() != null) {
                adapter.removeHeaderView(adapter.getHeaderLayout().getChildAt(0));
            }
            mHeaderContainer.removeAllViews();
            mHeaderContainer.addView(headerView);
            mHeaderContainerHeight = mHeaderContainer.getHeight();
            adapter.addHeaderView(mHeaderContainer);
        }
    }

    /**
     * 移除HeaderView
     * 如果要兼容API 9,需要修改此处逻辑，API 11以下不支持动态添加header
     */
    private void removeHeaderView() {
        if (mHeaderContainer != null && mRootView != null && mRootView.getAdapter() instanceof BaseQuickAdapter) {
            BaseQuickAdapter adapter = (BaseQuickAdapter) mRootView.getAdapter();
            if (adapter.getHeaderLayout() != null) {
                adapter.removeHeaderView(adapter.getHeaderLayout().getChildAt(0));
            }
        }
    }

    /**
     * 更新HeaderView  先移除-->再添加zoomView、HeaderView -->然后添加到listView的head
     * 如果要兼容API 9,需要修改此处逻辑，API 11以下不支持动态添加header
     */
    public void updateHeaderView() {
        if (mHeaderContainer != null && mRootView != null && mRootView.getAdapter() instanceof BaseQuickAdapter) {
            BaseQuickAdapter adapter = (BaseQuickAdapter) mRootView.getAdapter();
            if (adapter.getHeaderLayout() != null) {
                adapter.removeHeaderView(adapter.getHeaderLayout().getChildAt(0));
            }
            mHeaderContainer.removeAllViews();
            if (mZoomView != null) {
                mHeaderContainer.addView(mZoomView);
            }
            if (mHeaderView != null) {
                mHeaderContainer.addView(mHeaderView);
            }
            mHeaderContainerHeight = mHeaderContainer.getHeight();
            adapter.addHeaderView(mHeaderContainer);
        }
    }

    public void setAdapterAndLayoutManager(RecyclerView.Adapter adapter, GridLayoutManager mLayoutManager) {
        mRootView.setLayoutManager(mLayoutManager);
        mRootView.setAdapter(adapter);
        updateHeaderView();
    }

    /**
     * 创建listView 如果要兼容API9,需要修改此处
     *
     * @param context 上下文
     * @param attrs   AttributeSet
     * @return ListView
     */
    @Override
    protected RecyclerView createRootView(Context context, AttributeSet attrs) {
        SlideRecyclerView rv = new SlideRecyclerView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment
        // 不设置，fragment被回收后，会crash!!
        rv.setId(android.R.id.list);
        rv.setOverScrollMode(OVER_SCROLL_NEVER);
        return rv;
    }

    /**
     * 重置动画，自动滑动到顶部
     */
    @Override
    protected void smoothScrollToTop() {
        Log.d(TAG, "smoothScrollToTop --> ");
        mScalingRunnable.startAnimation(100L);
    }

    /**
     * zoomView动画逻辑
     *
     * @param newScrollValue 手指Y轴移动距离值
     */
    @Override
    protected void pullHeaderToZoom(int newScrollValue) {
        Log.d(TAG, "pullHeaderToZoom --> newScrollValue = " + newScrollValue);
        Log.d(TAG, "pullHeaderToZoom --> mHeaderHeight = " + mHeaderContainerHeight);
        if (mScalingRunnable != null && !mScalingRunnable.isFinished()) {
            mScalingRunnable.abortAnimation();
        }

        ViewGroup.LayoutParams localLayoutParams = mHeaderContainer.getLayoutParams();
        localLayoutParams.height = Math.abs(newScrollValue) + mHeaderContainerHeight;
        mHeaderContainer.setLayoutParams(localLayoutParams);

        ViewGroup.LayoutParams zoomViewLayoutParams = mZoomView.getLayoutParams();
        zoomViewLayoutParams.height = mZoomViewHeight + localLayoutParams.height - mHeaderContainerHeight;
        mZoomView.setLayoutParams(zoomViewLayoutParams);
    }

    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    private boolean isFirstItemVisible() {
        if (mRootView != null) {
            final RecyclerView.Adapter adapter = mRootView.getAdapter();
            final LinearLayoutManager layoutManager = (LinearLayoutManager) mRootView.getLayoutManager();
            if (null == adapter || adapter.getItemCount() == 0) {
                return false;
            } else if (layoutManager != null) {
                return layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
            }
        }
        return false;
    }

    @Override
    public void handleStyledAttributes(TypedArray a) {
        mHeaderContainer = new FrameLayout(getContext());
        if (mZoomView != null) {
            mHeaderContainer.addView(mZoomView);
        }
        if (mHeaderView != null) {
            mHeaderContainer.addView(mHeaderView);
        }

        if (mRootView.getAdapter() instanceof BaseQuickAdapter) {
            ((BaseQuickAdapter) mRootView.getAdapter()).addHeaderView(mHeaderContainer);
        }
    }

    public void setHeaderContainerParams(LayoutParams layoutParams) {
        if (mHeaderContainer != null) {
            mHeaderContainer.setLayoutParams(layoutParams);
            mHeaderContainerHeight = layoutParams.height;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout --> ");
        if (mHeaderContainer != null && mHeaderContainerHeight <= 0) {
            mHeaderContainerHeight = mHeaderContainer.getMeasuredHeight();
        }
        if (mZoomView != null && mZoomViewHeight <= 0) {
            mZoomViewHeight = mZoomView.getMeasuredHeight();
        }
    }

    class ScalingRunnable implements Runnable {
        long mDuration;
        boolean mIsFinished = true;
        float mScale;
        long mStartTime;

        ScalingRunnable() {
        }

        void abortAnimation() {
            mIsFinished = true;
        }

        boolean isFinished() {
            return mIsFinished;
        }

        @Override
        public void run() {
            if (mZoomView != null && mHeaderContainer != null && (!mIsFinished) && (mScale > 1.0f)) {
                float zoomBackProgress = (SystemClock.currentThreadTimeMillis() - mStartTime) / (float) mDuration;
                ViewGroup.LayoutParams headerContainerLayoutParams = mHeaderContainer.getLayoutParams();
                ViewGroup.LayoutParams zoomViewLayoutParams = mZoomView.getLayoutParams();
                if (zoomBackProgress > 1.0f) {
                    headerContainerLayoutParams.height = mHeaderContainerHeight;
                    mHeaderContainer.setLayoutParams(headerContainerLayoutParams);

                    zoomViewLayoutParams.height = mZoomViewHeight;
                    mZoomView.setLayoutParams(zoomViewLayoutParams);

                    mIsFinished = true;
                    return;
                }
                float currentScale = mScale - (mScale - 1.0f) * sSmoothToTopInterpolator.getInterpolation(zoomBackProgress);
                headerContainerLayoutParams.height = (int) (currentScale * mHeaderContainerHeight);
                mHeaderContainer.setLayoutParams(headerContainerLayoutParams);
                zoomViewLayoutParams.height = mZoomViewHeight + headerContainerLayoutParams.height - mHeaderContainerHeight;
                mZoomView.setLayoutParams(zoomViewLayoutParams);

                post(this);
            }
        }

        public void startAnimation(long animationDuration) {
            if (mZoomView != null && mHeaderContainer != null) {
                mStartTime = SystemClock.currentThreadTimeMillis();
                mDuration = animationDuration;
                mScale = ((float) mHeaderContainer.getHeight() / mHeaderContainerHeight);
                mIsFinished = false;
                post(this);
            }
        }
    }
}
