package com.hongmei.garbagesort.widget.pulltorefreshview;

import android.view.animation.Interpolator;

/**
 * Date: 2021-02-18
 * Desc:
 */
class SpringInterpolator implements Interpolator {

    private float mFactor;

    public SpringInterpolator() {
        this.mFactor = 1f;
    }

    public SpringInterpolator(float mFactor) {
        this.mFactor = mFactor;
    }

    @Override
    public float getInterpolation(float input) {
        float result = (float) (Math.pow(2, -10 * input) * Math.sin((input - mFactor / 4) * (2 * Math.PI) / mFactor) + 1);
        return result;
    }

}
