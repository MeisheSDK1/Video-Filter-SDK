package com.meishe.engine.bean;

import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LClipInfo;

import java.io.Serializable;

import androidx.annotation.Nullable;

/**
 * Created by CaoZhiChao on 2020/7/3 17:39
 */
public class ClipInfo<T> extends NvsObject<T> implements Cloneable, Comparable<ClipInfo> , Serializable, TimelineDataToLocalAdapter<LClipInfo> {
    /**
     * 客户端不关注index，而是根据inpoint查找。
     * Instead of focusing on the index, the client looks up by inpoint.
     */
    private int index = -1;
    protected String type = "base";
    private long inPoint;
    private long outPoint;


    public ClipInfo() {
        super(null);
    }

    public ClipInfo(String type) {
        super(null);
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
    public int compareTo(ClipInfo clipInfo) {
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
        ClipInfo tmp = (ClipInfo) obj;
        return tmp.getInPoint() == this.getInPoint();
    }

   @Override
    public LClipInfo parseToLocalData() {
        return null;
    }

    protected void setCommonData(LClipInfo lClipInfo) {
        lClipInfo.setIndex(getIndex());
        lClipInfo.setInPoint(getInPoint());
        lClipInfo.setOutPoint(getOutPoint());
    }
}
