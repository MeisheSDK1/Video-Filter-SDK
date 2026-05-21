package com.meishe.sdkdemo.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class AutoFocusTextView extends androidx.appcompat.widget.AppCompatTextView {
    public AutoFocusTextView(Context context) {
        super(context);
    }

    public AutoFocusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFocusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean isFocused() {
        //textView 在recyclerview中实现滚动效果，需要获取焦点，
        //textView is scrolling in recyclerview, it needs to get focus,
        return true;
    }

}
