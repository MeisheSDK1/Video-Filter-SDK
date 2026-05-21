package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.base.utils.CommonUtils;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/4 13:49
 */
public class LMeicamResource implements Cloneable, LocalToTimelineDataAdapter<MeicamResource> {
    @SerializedName("resourceId")
    private String id;

    private List<LPathInfo> pathList = new ArrayList<>();

    public LMeicamResource(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LPathInfo> getPathList() {
        return pathList;
    }

    public void setPathList(List<LPathInfo> pathList) {
        this.pathList = pathList;
    }

    @Override
    public MeicamResource parseToTimelineData() {
        MeicamResource local = null;
        if (!CommonUtils.isEmpty(pathList)) {
            local = new MeicamResource(id);
            for (LPathInfo lPathInfo : pathList) {
                local.addPathInfo(new MeicamResource.PathInfo(lPathInfo.getType(), lPathInfo.getPath(), lPathInfo.isNeedTranscode()));
            }
        }
        return local;
    }

    public void addPathInfo(LPathInfo pathInfo) {
        pathList.add(pathInfo);
    }
}
