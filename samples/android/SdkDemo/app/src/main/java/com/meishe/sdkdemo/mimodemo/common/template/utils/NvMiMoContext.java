package com.meishe.sdkdemo.mimodemo.common.template.utils;

import android.content.Context;

import com.meishe.sdkdemo.mimodemo.bean.MiMoLocalData;
import com.meishe.sdkdemo.mimodemo.common.template.model.MiMoInfo;

import java.util.List;


/**
 * 模板上下文，用于提供模板数据
 * Template context, used to provide template data
 */
public class NvMiMoContext {
    private static volatile NvMiMoContext mInstance;
    private Context mContext;
    private int mSelectListIndex = 0;
    private MiMoLocalData mSelectedMimoData;
    private List<MiMoInfo> mTemplateList;

    private NvMiMoContext(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        if (mInstance == null) {
            synchronized (NvMiMoContext.class) {
                if (mInstance == null) {
                    mInstance = new NvMiMoContext(context);
                }
            }
        }
    }

    public static NvMiMoContext getInstance() {
        return mInstance;
    }

    public void setSelectListIndex(int selectListIndex) {
        this.mSelectListIndex = selectListIndex;
    }

    public void setSelectedMimoData(MiMoLocalData miMoLocalData) {
        this.mSelectedMimoData = miMoLocalData;
    }

    public MiMoLocalData getSelectedMimoData() {
        return mSelectedMimoData;
    }
}
