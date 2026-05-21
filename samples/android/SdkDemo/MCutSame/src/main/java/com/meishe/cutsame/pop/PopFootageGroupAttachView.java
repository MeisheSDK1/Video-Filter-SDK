package com.meishe.cutsame.pop;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.AttachPopupView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/29 17:53
 * @Description : 依附弹窗
 *                attach view
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class PopFootageGroupAttachView extends AttachPopupView {

    private OnCancelCroupClickListener mOnCancelCroupClickListener;

    public static PopFootageGroupAttachView create(Context context,View view,OnCancelCroupClickListener onCancelCroupClickListener){
        return (PopFootageGroupAttachView) new XPopup.Builder(context).atView(view).asCustom(new
                PopFootageGroupAttachView(context).setCancelGroupClickListener(onCancelCroupClickListener));
    }

    public PopFootageGroupAttachView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.cut_export_template_cancel_group_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.ll_root_view).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnCancelCroupClickListener!=null){
                    mOnCancelCroupClickListener.onCancelClick();
                }
                PopFootageGroupAttachView.this.dismiss();
            }
        });
    }

    public PopFootageGroupAttachView setCancelGroupClickListener(OnCancelCroupClickListener onCancelCroupClickListener){
        this.mOnCancelCroupClickListener=onCancelCroupClickListener;
        return this;
    }

    public interface OnCancelCroupClickListener{

        void onCancelClick();
    }
}
