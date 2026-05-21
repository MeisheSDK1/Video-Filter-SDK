package com.meishe.third.pop.animator;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.enums.PopupAnimation;

/**
 * Description: 像系统的PopupMenu那样的动画
 * Animation like the system's PopupMenu
 * Create by lxj, at 2018/12/12
 */
public class ScrollScaleAnimator extends PopupAnimator {

    private FloatEvaluator floatEvaluator = new FloatEvaluator();
    private IntEvaluator intEvaluator = new IntEvaluator();
    private int startScrollX, startScrollY;
    private float startAlpha = .2f;
    private float startScale = 0f;

    public boolean isOnlyScaleX = false;

    public ScrollScaleAnimator(View target, PopupAnimation popupAnimation) {
        super(target, popupAnimation);
    }

    @Override
    public void initAnimator() {
        targetView.setAlpha(startAlpha);
        targetView.setScaleX(startScale);
        if(!isOnlyScaleX){
            targetView.setScaleY(startScale);
        }

        targetView.post(new Runnable() {
            @Override
            public void run() {
                /*
                * 设置参考点
                * reference pixel
                * */
                applyPivot();
                targetView.scrollTo(startScrollX, startScrollY);
                if(targetView.getBackground()!=null)targetView.getBackground().setAlpha(0);
            }
        });
    }

    private void applyPivot(){
        if (popupAnimation == PopupAnimation.ScrollAlphaFromLeft) {
            targetView.setPivotX(0f);
            targetView.setPivotY(targetView.getMeasuredHeight() / 2);

            startScrollX = targetView.getMeasuredWidth();
            startScrollY = 0;
        } else if (popupAnimation == PopupAnimation.ScrollAlphaFromLeftTop) {
            targetView.setPivotX(0f);
            targetView.setPivotY(0f);
            startScrollX = targetView.getMeasuredWidth();
            startScrollY = targetView.getMeasuredHeight();
        } else if (popupAnimation == PopupAnimation.ScrollAlphaFromTop) {
            targetView.setPivotX(targetView.getMeasuredWidth() / 2);
            targetView.setPivotY(0f);

            startScrollY = targetView.getMeasuredHeight();
        } else if (popupAnimation == PopupAnimation.ScrollAlphaFromRightTop) {
            targetView.setPivotX(targetView.getMeasuredWidth());
            targetView.setPivotY(0f);
            startScrollX = -targetView.getMeasuredWidth();
            startScrollY = targetView.getMeasuredHeight();
        } else if (popupAnimation == PopupAnimation.ScrollAlphaFromRight) {
            targetView.setPivotX(targetView.getMeasuredWidth());
            targetView.setPivotY(targetView.getMeasuredHeight() / 2);

            startScrollX = -targetView.getMeasuredWidth();
        } else if (popupAnimation == PopupAnimation.ScrollAlphaFromRightBottom) {
            targetView.setPivotX(targetView.getMeasuredWidth());
            targetView.setPivotY(targetView.getMeasuredHeight());

            startScrollX = -targetView.getMeasuredWidth();
            startScrollY = -targetView.getMeasuredHeight();
        } else if (popupAnimation == PopupAnimation.ScrollAlphaFromBottom) {
            targetView.setPivotX(targetView.getMeasuredWidth() / 2);
            targetView.setPivotY(targetView.getMeasuredHeight());

            startScrollY = -targetView.getMeasuredHeight();
        } else if (popupAnimation == PopupAnimation.ScrollAlphaFromLeftBottom) {
            targetView.setPivotX(0);
            targetView.setPivotY(targetView.getMeasuredHeight());

            startScrollX = targetView.getMeasuredWidth();
            startScrollY = -targetView.getMeasuredHeight();
        }
    }

    @Override
    public void animateShow() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                targetView.setAlpha(floatEvaluator.evaluate(fraction, startAlpha, 1f));
                targetView.scrollTo(intEvaluator.evaluate(fraction, startScrollX, 0),
                        intEvaluator.evaluate(fraction, startScrollY, 0));
                float scale = floatEvaluator.evaluate(fraction, startScale, 1f);
                targetView.setScaleX(scale);
                if(!isOnlyScaleX)targetView.setScaleY(scale);
                if(fraction>=.9f && targetView.getBackground()!=null) {
                    float alphaFraction = (fraction - .9f) / .1f;
                    targetView.getBackground().setAlpha((int) (alphaFraction*255));
                }
            }
        });
        animator.setDuration(XPopup.getAnimationDuration()).setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    @Override
    public void animateDismiss() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                targetView.setAlpha(floatEvaluator.evaluate(fraction, 1f, startAlpha));
                targetView.scrollTo(intEvaluator.evaluate(fraction, 0, startScrollX),
                        intEvaluator.evaluate(fraction, 0, startScrollY));
                float scale = floatEvaluator.evaluate(fraction, 1f, startScale);
                targetView.setScaleX(scale);
                if(!isOnlyScaleX)targetView.setScaleY(scale);
                if(targetView.getBackground()!=null)targetView.getBackground().setAlpha((int) (fraction*255));
            }
        });
        animator.setDuration(XPopup.getAnimationDuration())
                .setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

}
