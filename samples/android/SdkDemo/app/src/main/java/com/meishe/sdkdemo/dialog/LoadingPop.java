package com.meishe.sdkdemo.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.meishe.sdkdemo.R;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.CenterPopupView;

import org.jetbrains.annotations.NotNull;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/9/13 18:40
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class LoadingPop extends CenterPopupView {

    public static LoadingPop create(Context mContext) {
        return (LoadingPop) new XPopup.Builder(mContext)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(new LoadingPop(mContext));
    }

    public LoadingPop(@NonNull @NotNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_loading;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
