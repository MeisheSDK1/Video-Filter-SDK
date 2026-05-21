package com.meishe.engine.bean;

import android.graphics.PointF;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsLiveWindowExt;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.adapter.parser.IResourceParser;
import com.meishe.engine.local.LMeicamWaterMark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/4 14:56
 */
public class MeicamWaterMark implements Cloneable, Serializable, TimelineDataToLocalAdapter<LMeicamWaterMark>, IResourceParser {
    @SerializedName("watermarkPath")
    private String mWatermarkPath;
    @SerializedName("watermarkX")
    private int mWatermarkX;
    @SerializedName("watermarkY")
    private int mWatermarkY;
    @SerializedName("watermarkW")
    private int mWatermarkW;
    @SerializedName("watermarkH")
    private int mWatermarkH;
    private transient List<PointF> list = new ArrayList<>();
    private float opacity = 1;
    private String resourceId;

    public MeicamWaterMark(String watermarkPath, List<PointF> list) {
        this.mWatermarkPath = watermarkPath;
        this.list = list;
    }

    public String getWatermarkPath() {
        return mWatermarkPath;
    }

    public void setWatermarkPath(String mWatermarkPath) {
        this.mWatermarkPath = mWatermarkPath;
    }

    public int getWatermarkX() {
        return mWatermarkX;
    }

    public void setWatermarkX(int mWatermarkX) {
        this.mWatermarkX = mWatermarkX;
    }

    public int getWatermarkY() {
        return mWatermarkY;
    }

    public void setWatermarkY(int mWatermarkY) {
        this.mWatermarkY = mWatermarkY;
    }

    public int getWatermarkW() {
        return mWatermarkW;
    }

    public void setWatermarkW(int mWatermarkW) {
        this.mWatermarkW = mWatermarkW;
    }

    public int getWatermarkH() {
        return mWatermarkH;
    }

    public void setWatermarkH(int mWatermarkH) {
        this.mWatermarkH = mWatermarkH;
    }

    public List<PointF> getList() {
        return list;
    }

    public void setList(List<PointF> list) {
        this.list = list;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setPointList(NvsLiveWindowExt mLiveWindow, NvsTimeline nvsTimeline) {

        if (nvsTimeline == null) {
            LogUtils.e("MeicamWaterMark setPointList nvsTimeline==null");
            return;
        }

        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        NvsVideoResolution resolution = nvsTimeline.getVideoRes();
        int timeLineWidth = resolution.imageWidth;
        int timeLineHeight = resolution.imageHeight;

        int leftTopPointX = mWatermarkX - timeLineWidth / 2;
        int leftTopPointY = timeLineHeight / 2 - mWatermarkY;

        PointF leftTop = new PointF(leftTopPointX, leftTopPointY);

        PointF rightBottom = new PointF((leftTopPointX + mWatermarkW), (leftTopPointY - mWatermarkH));

        PointF leftBottom = new PointF(leftTop.x, rightBottom.y);
        PointF rightTop = new PointF(rightBottom.x, leftTop.y);


        list.add(mLiveWindow.mapCanonicalToView(leftTop));
        list.add(mLiveWindow.mapCanonicalToView(leftBottom));
        list.add(mLiveWindow.mapCanonicalToView(rightBottom));
        list.add(mLiveWindow.mapCanonicalToView(rightTop));
    }

    @Override
    public LMeicamWaterMark parseToLocalData() {
        parseToResourceId();
        LMeicamWaterMark local = new LMeicamWaterMark(mWatermarkPath);
        local.setWatermarkH(getWatermarkH());
        local.setWatermarkW(getWatermarkW());
        local.setWatermarkX(getWatermarkX());
        local.setWatermarkY(getWatermarkY());
        local.setResourceId(getResourceId());
        return local;
    }

    @Override
    public void parseToResourceId() {
        if (!TextUtils.isEmpty(mWatermarkPath)) {
            MeicamResource resource = new MeicamResource();
            resource.addPathInfo(new MeicamResource.PathInfo("path", mWatermarkPath, false));
            resourceId = TimelineData.getInstance().getPlaceId(resource);
        }
    }
}
