package com.meishe.cutsame.bean;

import com.meishe.third.adpater.entity.SectionEntity;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/24 15:59
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateSection extends SectionEntity<ExportTemplateClip> {
    private int trackSectionIndex;

    public ExportTemplateSection(ExportTemplateClip exportTemplateClip) {
        super(exportTemplateClip);
    }

    public int getTrackSectionIndex() {
        return trackSectionIndex;
    }

    public void setTrackSectionIndex(int trackSectionIndex) {
        this.trackSectionIndex = trackSectionIndex;
    }
}
