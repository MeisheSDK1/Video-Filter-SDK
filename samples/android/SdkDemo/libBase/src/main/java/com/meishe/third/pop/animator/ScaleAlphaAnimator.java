package com.meishe.third.pop.animator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.enums.PopupAnimation;


/**
 * Description: 缩放透明
 * Scaling of transparent
 * Create by dance, at 2018/12/9
 */
public class ScaleAlphaAnimator extends PopupAnimator {
    public ScaleAlphaAnimator(View target, PopupAnimation popupAnimation) {
        super(target, popupAnimation);
    }

    @Override
    public void initAnimator() {
        targetView.setScaleX(0f);
        targetView.setScaleY(0f);
        targetView.setAlpha(0);

        /*
        * 设置动画参考点
        * Set the animation reference point
        * */
        targetView.post(new Runnable() {
            @Override
            public void run() {
                applyPivot();
            }
        });
    }

    /**
     * 根据不同的PopupAnimation来设定对应的pivot
     * The corresponding pivot is set according to different PopupAnimation
     */
    private void applyPivot() {
        if (popupAnimation == PopupAnimation.ScaleAlphaFromCenter) {
            targetView.setPivotX(targetView.getMeasuredWidth() / 2);
            targetView.setPivotY(targetView.getMeasuredHeight() / 2);
        } else if (popupAnimation == PopupAnimation.ScaleAlphaFromLeftTop) {
            targetView.setPivotX(0);
            targetView.setPivotY(0);
        } else if (popupAnimation == PopupAnimation.ScaleAlphaFromRightTop) {
            targetView.setPivotX(targetView.getMeasuredWidth());
            targetView.setPivotY(0f);
        } else if (popupAnimation == PopupAnimation.ScaleAlphaFromLeftBottom) {
            targetView.setPivotX(0f);
            targetView.setPivotY(targetView.getMeasuredHeight());
        } else if (popupAnimation == PopupAnimation.ScaleAlphaFromRightBottom) {
            targetView.setPivotX(targetView.getMeasuredWidth());
            targetView.setPivotY(targetView.getMeasuredHeight());
        }

    }

    @Override
    public void animateShow() {
        targetView.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(XPopup.getAnimationDuration())
                .setInterpolator(new OvershootInterpolator(1f))
                .start();
    }

    @Override
    public void animateDismiss() {
        targetView.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(XPopup.getAnimationDuration())
                .setInterpolator(new FastOutSlowInInterpolator()).start();
    }

}
