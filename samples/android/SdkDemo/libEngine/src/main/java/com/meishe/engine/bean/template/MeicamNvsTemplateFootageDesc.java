package com.meishe.engine.bean.template;

import com.meicam.sdk.NvsAssetPackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: ChuChenGuang
 * @CreateDate: 2022/5/20 17:33
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MeicamNvsTemplateFootageDesc extends NvsAssetPackageManager.NvsTemplateFootageDesc {
    private final List<Integer> clipTrackIndexInTimelineList = new ArrayList<>();
    private final List<Integer> clipIndexInTimelineList = new ArrayList<>();

    public void addClipTrackIndexInTimeline(int trackIndex) {
        clipTrackIndexInTimelineList.add(trackIndex);
    }

    public void addClipIndexInTimeline(int clipIndex) {
        clipIndexInTimelineList.add(clipIndex);
    }

    public List<Integer> getClipTrackIndexInTimelineList() {
        return clipTrackIndexInTimelineList;
    }

    public void setClipTrackIndexInTimelineList(List<Integer> clipTrackIndexInTimelineList) {
        this.clipTrackIndexInTimelineList.addAll(clipTrackIndexInTimelineList);
    }

    public List<Integer> getClipIndexInTimelineList() {
        return clipIndexInTimelineList;
    }

    public void setClipIndexInTimelineList(List<Integer> clipIndexInTimelineList) {
        this.clipIndexInTimelineList.addAll(clipIndexInTimelineList);
    }

    public void setNvsTemplateFootageDesc(NvsAssetPackageManager.NvsTemplateFootageDesc desc) {
        this.id = desc.id;
        this.type = desc.type;
        this.canReplace = desc.canReplace;
        this.innerAssetFilePath = desc.innerAssetFilePath;
        this.tags = desc.tags;
        this.correspondingClipInfos = desc.correspondingClipInfos;
        this.timelineClipFootages = desc.timelineClipFootages;
    }

    public void copy(MeicamNvsTemplateFootageDesc nvsTemplateFootageDesc) {
        this.id = nvsTemplateFootageDesc.id;
        this.type = nvsTemplateFootageDesc.type;
        this.canReplace = nvsTemplateFootageDesc.canReplace;
        this.innerAssetFilePath = nvsTemplateFootageDesc.innerAssetFilePath;
        this.tags = nvsTemplateFootageDesc.tags;
        this.correspondingClipInfos = nvsTemplateFootageDesc.correspondingClipInfos;
        this.timelineClipFootages = nvsTemplateFootageDesc.timelineClipFootages;
        this.clipTrackIndexInTimelineList.addAll(nvsTemplateFootageDesc.clipTrackIndexInTimelineList);
        this.clipIndexInTimelineList.addAll(nvsTemplateFootageDesc.clipIndexInTimelineList);
    }
}
