package com.meishe.engine.local;

import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamTransition;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by CaoZhiChao on 2020/7/3 20:23
 */
public class LMeicamTransition extends LNvsObject implements Cloneable, Serializable, LocalToTimelineDataAdapter<MeicamTransition> {
    private int index;
    private String type;
    private String desc;
    private String displayName;
    private String displayNamezhCN;
    private String iconPath;
    private int iconResourceId;
    private long duration = CommonData.TIMEBASE;

    private String resourceId;

    public LMeicamTransition() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayNamezhCN() {
        return displayNamezhCN;
    }

    public void setDisplayNamezhCN(String displayNamezhCN) {
        this.displayNamezhCN = displayNamezhCN;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof LMeicamTransition) {
            LMeicamTransition transition = (LMeicamTransition) object;
            return this.getIndex() == (transition.getIndex());
        }
        return super.equals(object);
    }

    @NonNull
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public MeicamTransition parseToTimelineData() {
        MeicamTransition timelineData = new MeicamTransition();
        timelineData.setDesc(getDesc());
        timelineData.setDisplayName(getDisplayName());
        timelineData.setDisplayNamezhCN(getDisplayNamezhCN());
        timelineData.setDuration(getDuration());
        timelineData.setIconPath(getIconPath());
        timelineData.setIconResourceId(getIconResourceId());
        timelineData.setIndex(getIndex());
        timelineData.setType(getType());
        return timelineData;
    }
}
