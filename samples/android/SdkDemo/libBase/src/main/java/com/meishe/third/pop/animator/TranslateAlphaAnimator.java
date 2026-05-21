package com.meishe.third.pop.animator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.enums.PopupAnimation;
import com.meishe.third.pop.util.XPopupUtils;


/**
 * Description: 平移动画
 * TranslateAnimation
 * Create by dance, at 2018/12/9
 */
public class TranslateAlphaAnimator extends PopupAnimator {
    /*
    * 动画起始坐标
    * Animation start coordinates
    * */
    private float startTranslationX, startTranslationY;
    private float defTranslationX, defTranslationY;

    public TranslateAlphaAnimator(View target, PopupAnimation popupAnimation) {
        super(target, popupAnimation);
    }

    @Override
    public void initAnimator() {
        defTranslationX = targetView.getTranslationX();
        defTranslationY = targetView.getTranslationY();

        targetView.setAlpha(0);
        /*
        * 设置移动坐标
        *Set moving coordinates
        * */
        applyTranslation();
        startTranslationX = targetView.getTranslationX();
        startTranslationY = targetView.getTranslationY();
    }

    private void applyTranslation() {
        int halfWidthOffset = XPopupUtils.getWindowWidth(targetView.getContext())/2 - targetView.getMeasuredWidth()/2;
        int halfHeightOffset = XPopupUtils.getWindowHeight(targetView.getContext())/2 - targetView.getMeasuredHeight()/2;
        if (popupAnimation == PopupAnimation.TranslateAlphaFromLeft) {
            targetView.setTranslationX(-(targetView.getMeasuredWidth()/* + halfWidthOffset*/));
        } else if (popupAnimation == PopupAnimation.TranslateAlphaFromTop) {
            targetView.setTranslationY(-(targetView.getMeasuredHeight() /*+ halfHeightOffset*/));
        } else if (popupAnimation == PopupAnimation.TranslateAlphaFromRight) {
            targetView.setTranslationX(targetView.getMeasuredWidth() /*+ halfWidthOffset*/);
        } else if (popupAnimation == PopupAnimation.TranslateAlphaFromBottom) {
            targetView.setTranslationY(targetView.getMeasuredHeight() /*+ halfHeightOffset*/);
        }
    }

    @Override
    public void animateShow() {
        targetView.animate().translationX(defTranslationX).translationY(defTranslationY).alpha(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(XPopup.getAnimationDuration()).start();
    }

    @Override
    public void animateDismiss() {
        targetView.animate().translationX(startTranslationX).translationY(startTranslationY).alpha(0f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(XPopup.getAnimationDuration()).start();
    }
}
