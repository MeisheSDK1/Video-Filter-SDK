package com.meishe.engine.local;

import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.ClipInfo;

import java.io.Serializable;

import androidx.annotation.Nullable;

/**
 * Created by CaoZhiChao on 2020/7/3 17:39
 */
public class LClipInfo extends LNvsObject implements Cloneable, Comparable<LClipInfo> , Serializable, LocalToTimelineDataAdapter<ClipInfo> {
    private int index;
    protected String type = "base";
    private long inPoint;
    private long outPoint;


    public LClipInfo() {
    }

    public LClipInfo(String type) {
        this.type = type;
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

    public long getInPoint() {
        return inPoint;
    }

    public void setInPoint(long inPoint) {
        this.inPoint = inPoint;
    }

    public long getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(long outPoint) {
        this.outPoint = outPoint;
    }


    @Override
    public int compareTo(LClipInfo clipInfo) {
        if (inPoint < clipInfo.getInPoint()) {
            return -1;
        } else if (inPoint > clipInfo.getInPoint()) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        LClipInfo tmp = (LClipInfo) obj;
        return tmp.getInPoint() == this.getInPoint();
    }

    protected void setCommonData(ClipInfo clipInfo) {
        clipInfo.setIndex(getIndex());
        clipInfo.setInPoint(getInPoint());
        clipInfo.setOutPoint(getOutPoint());
    }

    @Override
    public ClipInfo parseToTimelineData() {
        return null;
    }
}
