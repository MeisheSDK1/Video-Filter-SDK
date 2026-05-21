package com.meishe.cutsame.pop;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.BottomPopupView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/29 14:39
 * @Description : 底部弹窗
 *              bottom pop window view
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class PopFootageBottomView extends BottomPopupView {

    private OnBottomViewClickListener mOnBottomViewClickListener;

    public static PopFootageBottomView create(Context context, OnBottomViewClickListener onBottomViewClickListener) {
        return (PopFootageBottomView) new XPopup.Builder(context).asCustom(new PopFootageBottomView(context).
                setBottomClickListener(onBottomViewClickListener));
    }

    public PopFootageBottomView(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.cut_export_template_clip_bottom_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnBottomViewClickListener != null) {
                    mOnBottomViewClickListener.onBottomCancelClick();
                }
            }
        });
        findViewById(R.id.tv_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnBottomViewClickListener != null) {
                    mOnBottomViewClickListener.onBottomConfirmClick();
                }
            }
        });

    }

    public interface OnBottomViewClickListener {
        /**
         * 点击取消
         * click cancel
         */
        void onBottomCancelClick();

        /**
         * 点击确认
         * click confirm
         */
        void onBottomConfirmClick();

    }

    public PopFootageBottomView setBottomClickListener(OnBottomViewClickListener onBottomViewClickListener) {
        this.mOnBottomViewClickListener = onBottomViewClickListener;
        return this;
    }

}
