package com.meishe.cutsame.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.meishe.cutsame.R;
import com.meishe.base.utils.CommonUtils;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2021/1/5 17:26
 * @Description : 带选中态的文案
 *                with select state test
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected){
            Drawable radiusDrawable = CommonUtils.getRadiusDrawable((int) getResources().getDimension(R.dimen.dp_px_6), getResources().getColor(R.color.red_fc2b55));
            setBackground(radiusDrawable);
        }else{
            Drawable radiusDrawable = CommonUtils.getRadiusDrawable((int) getResources().getDimension(R.dimen.dp_px_6), getResources().getColor(R.color.ffefefef));
            setBackground(radiusDrawable);
        }
    }
}
