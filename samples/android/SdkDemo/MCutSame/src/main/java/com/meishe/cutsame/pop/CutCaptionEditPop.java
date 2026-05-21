package com.meishe.cutsame.pop;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishe.base.utils.BarUtils;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.cutsame.R;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.BasePopupView;
import com.meishe.third.pop.util.XPopupUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/3/10 18:43
 * @Description :根据软键盘高度而变动的字幕编辑弹窗
 * According to the height of the soft keyboard and change the subtitle editing pop-up window
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CutCaptionEditPop extends BasePopupView {
    private FrameLayout mFlRootContainer;
    private LinearLayout mLlRoot;
    private EditText mEtCaption;
    private TextView mTvCaption;
    private String mCaptionText;
    private ImageView mBtConfirm;
    private EventListener mListener;
    private int keyboardHeight = 0;
    private RecyclerView captionFont;
    private View viewBreak;
    private boolean showNavigation;
    private int narHeight;

    public CutCaptionEditPop(@NonNull Context context) {
        super(context);
        mFlRootContainer = findViewById(R.id.fl_container);
    }

    public static CutCaptionEditPop create(Context context) {
        return (CutCaptionEditPop) new XPopup
                .Builder(context)
                .dismissOnTouchOutside(true)
                .hasShadowBg(true)
                .asCustom(new CutCaptionEditPop(context));
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout.pop_caption_edit_container;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.cut_dialog_caption_edit_head;
    }

    @Override
    protected void initPopupContent() {
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), mFlRootContainer, false);
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        mFlRootContainer.addView(contentView, params);
        getPopupContentView().setTranslationX(popupInfo.offsetX);
        getPopupContentView().setTranslationY(popupInfo.offsetY);
        XPopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight());
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mLlRoot = findViewById(R.id.ll_root_view);
        mEtCaption = findViewById(R.id.cut_et_caption);
        mTvCaption = findViewById(R.id.cut_tv_caption);
        mBtConfirm = findViewById(R.id.cut_commit);
        viewBreak = findViewById(R.id.cut_view_break);

        initListener();
        initData();
    }

    private void initData() {
    }


    private void initListener() {
        mBtConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                KeyboardUtils.hideSoftInput(mEtCaption);
                if (mListener != null) {
                    CharSequence text = mTvCaption.getText();
                    mListener.onConfirm(text == null ? "" : text.toString());
                }
            }
        });
        mEtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    mTvCaption.setText(s.toString());
                }
            }
        });

    }

    @Override
    protected int getMaxWidth() {
        return ScreenUtils.getScreenWidth();
    }

    @Override
    protected int getPopupHeight() {
        return ScreenUtils.getScreenHeight() - ScreenUtils.getStatusBarHeight();
    }

    @Override
    public BasePopupView show() {
        /* 重写，不使用父类方法，下面的处理借鉴于BasePopupView
         * Overridden without using superclass methods, the following processing borrows from BasePopupView
         */
        if (getParent() != null) return this;
        final Activity activity = (Activity) getContext();
        popupInfo.decorView = (ViewGroup) activity.getWindow().getDecorView();
        /*
         * 1. add PopupView to its decorView after measured.
         * 在测量后添加PopupView到它的decorView。
         * */
        popupInfo.decorView.post(new Runnable() {
            @Override
            public void run() {
                if (getParent() != null) {
                    ((ViewGroup) getParent()).removeView(CutCaptionEditPop.this);
                }
                popupInfo.decorView.addView(CutCaptionEditPop.this, new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                init();
            }
        });
        return this;
    }


    /**
     * 设置字幕文字
     * Set caption text
     */
    public void setCaptionText(final String text) {
        if (mEtCaption != null) {
            mEtCaption.setText(text);
            mTvCaption.setText(text);
            mTvCaption.post(new Runnable() {
                @Override
                public void run() {
                    mEtCaption.setSelection(text.length());
                }
            });
        } else {
            mCaptionText = text;
        }
    }

    /**
     * 设置事件监听
     *
     * @param listener the listener
     */
    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    @Override
    protected void doAfterShow() {
        KeyboardUtils.showSoftInput(mEtCaption);
        if (!TextUtils.isEmpty(mCaptionText) && TextUtils.isEmpty(mTvCaption.getText())) {
            setCaptionText(mCaptionText);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        KeyboardUtils.registerSoftInputChangedListener((Activity) getContext(), mSoftInputChangedListener);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyboardUtils.unregisterSoftInputChangedListener(((Activity) getContext()).getWindow());
    }

    KeyboardUtils.OnSoftInputChangedListener mSoftInputChangedListener = new KeyboardUtils.OnSoftInputChangedListener() {
        @Override
        public void onSoftInputChanged(int height) {
            if (height == 0) {
                if (isShow()) {
                    //dismiss();
                }
            } else {
                keyboardHeight = height;
                /* 重新设置布局高度，防止被软键盘遮盖
                 * Reset the layout height to avoid being covered by the soft keyboard
                 */
                setRootHeight(height);
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) captionStyleView.getLayoutParams();
//                if (layoutParams != null) {
//                    layoutParams.height = height;
//                }
//                captionStyleView.setLayoutParams(layoutParams);
                mEtCaption.requestFocus();
            }
        }
    };

    private void setRootHeight(final int height) {
        ViewGroup.LayoutParams rootParams = viewBreak.getLayoutParams();
//        rootParams.height = getPopupHeight() - height - BarUtils.getNavBarHeight(getContext());
        int narBar = (!showNavigation && BarUtils.isAllScreenDevice(getContext())) ?
                40 : BarUtils.getNavBarHeight(getContext());
//        rootParams.height = getPopupHeight() - height - 20 + narBar;//20随便写的 目前有点遮挡布局
        rootParams.height = height + narBar;
        //mLlRoot.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        viewBreak.setLayoutParams(rootParams);
//        ViewGroup.LayoutParams rootContainerParams = getPopupContentView().getLayoutParams();
//        rootContainerParams.height = rootParams.height;
//        getPopupContentView().setLayoutParams(rootContainerParams);
    }

    public void setNarBar(boolean showNavigation, int narHeight) {
        this.showNavigation = showNavigation;
        this.narHeight = narHeight;
    }


    public interface EventListener {
        /**
         * 确定
         * Confirm
         */
        void onConfirm(String text);
    }
}
