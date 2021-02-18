package com.hongmei.garbagesort.widget.pulltozoom;

import android.content.Context;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.math.MathUtils;

import com.google.android.material.appbar.AppBarLayout;

public class PTZCoordinatorLayoutHelper extends PullToZoomViewHelper {
    private CoordinatorLayout mCoordinatorLayout;

    protected PTZCoordinatorLayoutHelper(Context context, CoordinatorLayout coordinatorLayout, View zoomView) {
        super(context, coordinatorLayout, zoomView);
        mCoordinatorLayout = coordinatorLayout;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mCoordinatorLayout != null && (mCoordinatorLayout.getScaleY() == 0 || getAppBarOffset() == 0);
    }

    /**
     * 参考AppBarLayout的源码，获取appbar滑动的offset，来判断是否可以start pull to zoom
     *
     * @return
     */
    private int getAppBarOffset() {
        final View child = mCoordinatorLayout.getChildAt(0);
        if (child instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            CoordinatorLayout.Behavior b = lp.getBehavior();
            if (b instanceof AppBarLayout.Behavior) {
                return MathUtils.clamp(((AppBarLayout.Behavior) b).getTopAndBottomOffset(),
                        -((AppBarLayout) child).getTotalScrollRange(), 0);
            }
        }
        return -1;
    }

    public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //暂时先不做处理
    }
}
