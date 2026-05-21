package com.meishe.base.view.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The type Item decoration.
 * item装饰类
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {

    private int mLeft;
    private int mRight;
    private int mTop;
    private int mBottom;

    public ItemDecoration() {
    }

    public ItemDecoration(int left, int right) {
        mLeft = left;
        mRight = right;
    }

    public ItemDecoration(int left, int top, int right, int bottom) {
        mLeft = left;
        mRight = right;
        mTop = top;
        mBottom = bottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mLeft;
        outRect.top = mTop;
        outRect.right = mRight;
        outRect.bottom = mBottom;
    }

    /**
     * Gets left.
     * 左
     * @return the left
     */
    public int getLeft() {
        return mLeft;
    }

    /**
     * Sets left.
     * 设置左
     * @param mLeft the m left
     * @return the left
     */
    public ItemDecoration setLeft(int mLeft) {
        this.mLeft = mLeft;
        return this;
    }

    /**
     * Gets right.
     * 右
     * @return the right
     */
    public int getRight() {
        return mRight;
    }

    /**
     * Sets right.
     * 设置右
     * @param mRight the m right
     * @return the right
     */
    public ItemDecoration setRight(int mRight) {
        this.mRight = mRight;
        return this;
    }

    /**
     * Gets top.
     * 获得顶部
     * @return the top
     */
    public int getTop() {
        return mTop;
    }

    /**
     * Sets top.
     * 设置顶部
     * @param mTop the m top
     * @return the top
     */
    public ItemDecoration setTop(int mTop) {
        this.mTop = mTop;
        return this;
    }

    /**
     * Gets bottom.
     * 获得底部
     * @return the bottom
     */
    public int getBottom() {
        return mBottom;
    }

    /**
     * Sets bottom.
     * 设置底部
     * @param mBottom the m bottom
     * @return the bottom
     */
    public ItemDecoration setBottom(int mBottom) {
        this.mBottom = mBottom;
        return this;
    }
}
