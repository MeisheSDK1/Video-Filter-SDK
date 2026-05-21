package com.meishe.third.pop.animator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.enums.PopupAnimation;


/**
 * Description: 平移动画，不带渐变
 * Pan animation without gradient
 * Create by dance, at 2018/12/9
 */
public class TranslateAnimator extends PopupAnimator {
    /*
    * 动画起始坐标
    * Animation start coordinates
    * */
    private float startTranslationX, startTranslationY;
    private int oldWidth, oldHeight;
    private float initTranslationX, initTranslationY;
    private boolean hasInitDefTranslation = false;

    public TranslateAnimator(View target, PopupAnimation popupAnimation) {
        super(target, popupAnimation);
    }

    @Override
    public void initAnimator() {
        if(!hasInitDefTranslation){
            initTranslationX = targetView.getTranslationX();
            initTranslationY = targetView.getTranslationY();
            hasInitDefTranslation = true;
        }
        /*
        * 设置起始坐标
        * Set the starting coordinates
        * */
        applyTranslation();
        startTranslationX = targetView.getTranslationX();
        startTranslationY = targetView.getTranslationY();

        oldWidth = targetView.getMeasuredWidth();
        oldHeight = targetView.getMeasuredHeight();
    }

    private void applyTranslation() {
        if (popupAnimation == PopupAnimation.TranslateFromLeft) {
            targetView.setTranslationX(-targetView.getRight());
        } else if (popupAnimation == PopupAnimation.TranslateFromTop) {
            targetView.setTranslationY(-targetView.getBottom());
        } else if (popupAnimation == PopupAnimation.TranslateFromRight) {
            targetView.setTranslationX(((View) targetView.getParent()).getMeasuredWidth() - targetView.getLeft());
        } else if (popupAnimation == PopupAnimation.TranslateFromBottom) {
            targetView.setTranslationY(((View) targetView.getParent()).getMeasuredHeight() - targetView.getTop());
        }
    }

    @Override
    public void animateShow() {
        targetView.animate().translationX(initTranslationX).translationY(initTranslationY)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(XPopup.getAnimationDuration()).start();
    }

    @Override
    public void animateDismiss() {
        /*
        * 执行消失动画的时候，宽高可能改变了，所以需要修正动画的起始值
        * The width and height may have changed when performing the vanishing animation, so you need to fix the start value of the animation
        * */
        if (popupAnimation == PopupAnimation.TranslateFromLeft) {
            startTranslationX -= targetView.getMeasuredWidth() - oldWidth;
        } else if (popupAnimation == PopupAnimation.TranslateFromTop) {
            startTranslationY -= targetView.getMeasuredHeight() - oldHeight;
        } else if (popupAnimation == PopupAnimation.TranslateFromRight) {
            startTranslationX += targetView.getMeasuredWidth() - oldWidth;
        } else if (popupAnimation == PopupAnimation.TranslateFromBottom) {
            startTranslationY += targetView.getMeasuredHeight() - oldHeight;
        }

        targetView.animate().translationX(startTranslationX).translationY(startTranslationY)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(XPopup.getAnimationDuration()).start();
    }
}
