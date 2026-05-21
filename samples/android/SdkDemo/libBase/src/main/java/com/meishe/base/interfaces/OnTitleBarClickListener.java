package com.meishe.base.interfaces;

/**
 * The interface On title bar click listener.
 * 标题栏点击监听器接口
 */
public interface OnTitleBarClickListener {
    /**
     * On back image click.
     * 返回图片点击
     */
    void onBackImageClick();

    /**
     * On center text click.
     * 单击中心文本
     */
    void onCenterTextClick();

    /**
     * On right text click.
     * 点击正确的文本
     */
    void onRightTextClick();
}
