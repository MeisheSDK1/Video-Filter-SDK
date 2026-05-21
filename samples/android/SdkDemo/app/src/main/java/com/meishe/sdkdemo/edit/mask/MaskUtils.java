package com.meishe.sdkdemo.edit.mask;

import android.content.Context;
import android.content.res.TypedArray;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.mask.MaskBean;

import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/3/5.
 * @Description :主页标签实体类。Home tag entity class
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MaskUtils {

    /**
     * 获取蒙版信息
     * getMaskView Info
     *
     * @param context
     * @return
     */
    public static List<MaskBean> getMaskMenus(Context context) {
        TypedArray editIconArray = context.getResources().obtainTypedArray(R.array.sub_edit_mask_menu_icon);
        String[] nameArray = context.getResources().getStringArray(R.array.sub_edit_mask_menu_name);
        List<MaskBean> maskInfoList = new ArrayList<>();

        for (int i = 0; i < nameArray.length; i++) {
            MaskBean maskBean = new MaskBean();
            maskBean.setCoverId(editIconArray.getResourceId(i, -1));
            maskBean.setName(nameArray[i]);
            //这里设置类型与设置的maskType 是对应关系 所以直接设置i
            // The set type here corresponds to the maskType you set so just set i
            maskBean.setMaskType(i);
            maskInfoList.add(maskBean);
        }
        editIconArray.recycle();
        return maskInfoList;

    }
}
