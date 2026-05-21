package com.meishe.cutsame.util;

import com.meishe.cutsame.bean.Template;
import com.meishe.base.utils.Utils;
import com.meishe.http.bean.BaseDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/24.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TemplateUtils {
    public static List<Template> getTemplate(List<BaseDataBean> elements, String categoryId) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        List<Template> data = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            BaseDataBean dataBean = elements.get(i);
            if (dataBean != null) {
                Template template = new Template();
                template.setCoverUrl(dataBean.getCoverUrl());
                template.setDisplayName(Utils.isZh() ? dataBean.getDisplayNamezhCN() : dataBean.getDisplayName());
                template.setDescription(Utils.isZh() ? dataBean.getDescriptionZhCn() : dataBean.getDescription());
                template.setDuration(dataBean.getDuration());
                template.setId(dataBean.getId());
                template.setPackageUrl(dataBean.getPackageUrl());
                if(dataBean.getQueryInteractiveResultDto()!=null){
                    template.setUseNum(dataBean.getQueryInteractiveResultDto().getUseNum());
                }
                template.setPreviewVideoUrl(dataBean.getPreviewVideoUrl());
                template.setInfoUrl(dataBean.getInfoUrl());
                template.setShotsNumber(dataBean.getShotsNumber());
                template.setSupportedAspectRatio(dataBean.getSupportedAspectRatio());
                template.setDefaultAspectRatio(dataBean.getDefaultAspectRatio());
                template.setUserInfo(dataBean.getUserInfo());
                template.setKind(dataBean.getKind());
                if ("2".equals(categoryId)) {
                    template.setType(Template.TYPE_TEMPLATE_FREE);
                }
                if ("3".equals(categoryId)) {
                    template.setType(Template.TYPE_TEMPLATE_AE);
                }
                data.add(template);
            }
        }
        return data;
    }
}
