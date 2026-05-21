package com.meishe.sdkdemo.capture.bean;

import android.text.TextUtils;

import com.meishe.http.bean.BaseDataBean;
import com.meishe.makeup.makeup.Makeup;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 17:06
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyTemplateInfo extends BaseDataBean {
    public enum BeautyTemplateType {
        /**
         * 自定义模板
         * Customize the template
         */
        BeautyTemplate_Custom,
        /**
         * 标准模板
         * Standard template
         */
        BeautyTemplate_Standard;
    }


    private boolean isAssets;
    private boolean isSelected;
    private BeautyTemplateType templateType = BeautyTemplateType.BeautyTemplate_Standard;
    private Makeup makeup;

    public boolean isAssets() {
        return isAssets;
    }

    public void setAssets(boolean assets) {
        isAssets = assets;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public BeautyTemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(BeautyTemplateType templateType) {
        this.templateType = templateType;
    }

    public Makeup getMakeup() {
        return makeup;
    }

    public void setMakeup(Makeup makeup) {
        this.makeup = makeup;
    }

    @Override
    public String getCoverUrl() {
        String cover = super.getCoverUrl();
        return TextUtils.isEmpty(cover) ? "https://qasset.meishesdk.com/material/cover/3D011B76-302C-441F-97A3-044D953E045E.png" : cover;
    }
}
