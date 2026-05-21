package com.meishe.cutsame.pop;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.ExportTemplateClip;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.AttachPopupView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/28 15:29
 * @Description : 依附弹窗
 * attach view
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class PopFootageAttachView extends AttachPopupView {

    private OnAttachListener mAttachListener;
    private String mFootageType;


    public static PopFootageAttachView create(Context context, View aView, OnAttachListener attachListener, String footageType) {
        return (PopFootageAttachView) new XPopup.Builder(context)
                .atView(aView).asCustom(new PopFootageAttachView(context, footageType).
                        setAttachListener(attachListener));
    }

    public PopFootageAttachView(@NonNull Context context, String footageType) {
        super(context);
        this.mFootageType = footageType;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.cut_same_pop_attach_footage_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        final RadioGroup radioGroup = findViewById(R.id.rl_footage_container);
        RadioButton unlimit = findViewById(R.id.rb_footage_unlimited);
        RadioButton onlyImage = findViewById(R.id.rb_footage_image);
        RadioButton onlyVideo = findViewById(R.id.rb_footage_video);

        if (ExportTemplateClip.TYPE_FOOTAGE_IMAGE_AND_VIDEO.equals(mFootageType)) {
            unlimit.setChecked(true);
        } else if (ExportTemplateClip.TYPE_FOOTAGE_VIDEO.equals(mFootageType)) {
            onlyVideo.setChecked(true);
        } else if (ExportTemplateClip.TYPE_FOOTAGE_IMAGE.equals(mFootageType)) {
            onlyImage.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (mAttachListener != null) {
                    mAttachListener.onFootageType(radioGroup.getCheckedRadioButtonId());
                }
            }
        });

        CheckBox checkBox = findViewById(R.id.cb_apply_all);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mAttachListener != null) {
                    mAttachListener.onApplyToAll(radioGroup.getCheckedRadioButtonId());
                }
            }
        });
    }

    public interface OnAttachListener {

        /**
         * 应用到全部
         * apply to all
         *
         * @param selectViewId
         */
        void onApplyToAll(int selectViewId);

        /**
         * 选择镜头类型
         * select footage type
         *
         * @param selectViewId
         */
        void onFootageType(int selectViewId);

    }


    public PopFootageAttachView setAttachListener(OnAttachListener attachListener) {
        mAttachListener = attachListener;
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
