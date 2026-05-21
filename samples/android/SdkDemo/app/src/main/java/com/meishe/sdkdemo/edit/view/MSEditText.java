package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2022/6/29 下午5:43
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MSEditText extends AppCompatEditText {
    private Context mContext;

    public MSEditText(Context context) {
        this(context, null);
    }

    public MSEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MSEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1) {
            super.onKeyPreIme(keyCode, event);
            onKeyBoardHideListener.onKeyHide(keyCode, event);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    OnKeyBoardHideListener onKeyBoardHideListener;

    public void setOnKeyBoardHideListener(OnKeyBoardHideListener onKeyBoardHideListener) {
        this.onKeyBoardHideListener = onKeyBoardHideListener;
    }

    public interface OnKeyBoardHideListener {
        void onKeyHide(int keyCode, KeyEvent event);
    }
}
