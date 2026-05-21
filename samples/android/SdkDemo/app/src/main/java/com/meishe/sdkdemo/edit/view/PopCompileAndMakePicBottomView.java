package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.meishe.sdkdemo.R;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.AttachPopupView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2022/9/6 下午4:38
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class PopCompileAndMakePicBottomView extends AttachPopupView {


    private OnBottomViewClickListener mOnBottomViewClickListener;


    public static PopCompileAndMakePicBottomView create(@NonNull Context context, View aView, OnBottomViewClickListener onBottomViewClickListener) {
        return (PopCompileAndMakePicBottomView) new XPopup.Builder(context)
                .atView(aView)
                .asCustom(new PopCompileAndMakePicBottomView(context)
                        .setBottomClickListener(onBottomViewClickListener));
    }

    public PopCompileAndMakePicBottomView(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.compile_make_pic_view;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_create_video).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnBottomViewClickListener != null) {
                    mOnBottomViewClickListener.onCreateVideo();
                }
            }
        });
        findViewById(R.id.tv_create_pic).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnBottomViewClickListener != null) {
                    mOnBottomViewClickListener.onCreatePicture();
                }
            }
        });
        findViewById(R.id.tv_create_live_photo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnBottomViewClickListener != null) {
                    mOnBottomViewClickListener.onCreateLivePhoto();
                }
            }
        });

    }


    public interface OnBottomViewClickListener {
        /**
         * 生成图片
         * create video
         */
        void onCreateVideo();

        /**
         * 生成图片
         * create pic
         */
        void onCreatePicture();

        /**
         * 生成实况
         */
        void onCreateLivePhoto();
    }

    public PopCompileAndMakePicBottomView setBottomClickListener(PopCompileAndMakePicBottomView.OnBottomViewClickListener onBottomViewClickListener) {
        this.mOnBottomViewClickListener = onBottomViewClickListener;
        return this;
    }
}
