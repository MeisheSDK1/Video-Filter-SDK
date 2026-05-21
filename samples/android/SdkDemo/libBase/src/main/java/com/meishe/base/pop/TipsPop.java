package com.meishe.base.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.meishe.base.R;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.CenterPopupView;
import com.meishe.utils.DrawableUitls;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/20 20:07
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class TipsPop extends CenterPopupView implements View.OnClickListener {
    private final Context mContext;
    private TextView mTipsTitle;
    private TextView mTipsContent;
    private Button mTipsCancel;
    private Button mTipsConfirm;
    private OnTipsPopListener mOnTipsPopListener;
    private String mTitle;
    private String mContent;
    private String mConfirmTitle;
    private String mCancelTitle;

    public static TipsPop create(Context context, boolean hasShadowBg) {
        return (TipsPop) new XPopup.Builder(context)
                .hasShadowBg(hasShadowBg)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(new TipsPop(context));
    }

    public TipsPop(@NonNull @NotNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_tips;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        int roundRadius = mContext.getResources().getDimensionPixelSize(R.dimen.dp_px_24);
        Drawable leftDrawable = DrawableUitls.getRadiiDrawable(0, 0, 0, roundRadius, Color.WHITE);
        Drawable rightDrawable = DrawableUitls.getRadiiDrawable(0, 0, roundRadius, 0, Color.WHITE);
        mTipsTitle = findViewById(R.id.pop_tips_title);
        mTipsContent = findViewById(R.id.pop_tips_content);
        mTipsConfirm = findViewById(R.id.pop_tips_confirm);
        mTipsCancel = findViewById(R.id.pop_tips_cancel);
        mTipsTitle.setVisibility(!TextUtils.isEmpty(mTitle) ? VISIBLE : GONE);
        mTipsTitle.setText(TextUtils.isEmpty(mTitle) ? mContext.getResources().getString(R.string.app_name) : mTitle);
        mTipsContent.setText(TextUtils.isEmpty(mContent) ? mContext.getResources().getString(R.string.app_name) : mContent);
        mTipsConfirm.setText(TextUtils.isEmpty(mConfirmTitle) ? mContext.getResources().getString(R.string.confirm) : mConfirmTitle);
        mTipsConfirm.setBackground(rightDrawable);
        mTipsCancel.setText(TextUtils.isEmpty(mCancelTitle) ? mContext.getResources().getString(R.string.cancel) : mCancelTitle);
        mTipsCancel.setBackground(leftDrawable);
        mTipsConfirm.setOnClickListener(this);
        mTipsCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (null == mOnTipsPopListener) {
            return;
        }
        if (v.getId() == R.id.pop_tips_confirm) {
            mOnTipsPopListener.onTipsConfirm();
            return;
        }
        if (v.getId() == R.id.pop_tips_cancel) {
            mOnTipsPopListener.onTipsCancel();
            dismiss();
        }
    }

    public void setTipsTitle(String title) {
        if (null == mTipsTitle) {
            return;
        }
        if (mTipsTitle.getVisibility() != VISIBLE) {
            mTipsTitle.setVisibility(VISIBLE);
        }
        mTipsTitle.setText(title);
    }

    public void setTipsContent(String content) {
        if (null == mTipsContent) {
            return;
        }
        mTipsContent.setText(content);
    }

    public void setTipsConfirm(String confirm) {
        if (null == mTipsConfirm) {
            return;
        }
        mTipsConfirm.setText(confirm);
    }

    public void setTipsCancel(String cancel) {
        if (null == mTipsCancel) {
            return;
        }
        mTipsCancel.setText(cancel);
    }

    public TipsPop setDefaultTipsTitle(String title) {
        mTitle = title;
        return this;
    }

    public TipsPop setDefaultTipsContent(String content) {
        mContent = content;
        return this;
    }

    public TipsPop setTipsDefaultConfirm(String confirm) {
        mConfirmTitle = confirm;
        return this;
    }

    public TipsPop setTipsDefaultCancel(String cancel) {
        mCancelTitle = cancel;
        return this;
    }

    public TipsPop setOnTipsPopListener(OnTipsPopListener onTipsPopListener) {
        mOnTipsPopListener = onTipsPopListener;
        return this;
    }

    public interface OnTipsPopListener {
        /**
         * 取消
         * Cancel
         */
        void onTipsCancel();

        /**
         * 确定
         * Confirm
         */
        void onTipsConfirm();
    }

}
