package com.meishe.engine.bean.template;

import com.meicam.sdk.NvsAssetPackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: ChuChenGuang
 * @CreateDate: 2022/5/24 15:01
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MeicamNvsTemplateFootageCorrespondingClipInfo extends NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo {


    private List<Integer> clipTrackIndexInTimelineList = new ArrayList<>();
    private List<Integer> clipIndexInTimelineList = new ArrayList<>();

    private long realInpoint;

    private int groupIndex = -1;

    private String footageID;


    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getFootageID() {
        return footageID;
    }

    public void setFootageID(String footageID) {
        this.footageID = footageID;
    }

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
        this.clipTrackIndexInTimelineList = clipTrackIndexInTimelineList;
    }

    public List<Integer> getClipIndexInTimelineList() {
        return clipIndexInTimelineList;
    }

    public void setClipIndexInTimelineList(List<Integer> clipIndexInTimelineList) {
        this.clipIndexInTimelineList = clipIndexInTimelineList;
    }

    public long getRealInpoint() {
        return realInpoint;
    }

    public void setRealInpoint(long realInpoint) {
        this.realInpoint = realInpoint;
    }

    public void setNvsTemplateFootageCorrespondingClipInfo(NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo correspondingClipInfo) {
        this.inpoint = correspondingClipInfo.inpoint;
        this.clipIndex = correspondingClipInfo.clipIndex;
        this.trackIndex = correspondingClipInfo.trackIndex;
        this.outpoint = correspondingClipInfo.outpoint;
        this.trimIn = correspondingClipInfo.trimIn;
        this.trimOut = correspondingClipInfo.trimOut;
        this.needReverse = correspondingClipInfo.needReverse;
        this.canReplace = correspondingClipInfo.canReplace;
    }
}
