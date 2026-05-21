package com.meishe.engine.bean;

import android.text.TextUtils;

import com.meishe.base.utils.CommonUtils;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LMeicamResource;
import com.meishe.engine.local.LPathInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/4 13:49
 */
public class MeicamResource implements Cloneable, TimelineDataToLocalAdapter<LMeicamResource> {
    /**
     * 和需要上传资源的clip中id对应
     * It is the same as the id in the clip of the resource to be uploaded
     */
    private String id;
    private List<PathInfo> pathList = new ArrayList<>();

    public MeicamResource(String id) {
        this.id = id;
    }

    public MeicamResource() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addPathInfo(PathInfo pathInfo) {
        pathList.add(pathInfo);
    }

    public List<PathInfo> getPathList() {
        return pathList;
    }

    public void setPathList(List<PathInfo> pathList) {
        this.pathList = pathList;
    }

    @Override
    public LMeicamResource parseToLocalData() {
        LMeicamResource local = null;
        if (!CommonUtils.isEmpty(pathList)) {
            local = new LMeicamResource(id);
            for (PathInfo pathInfo : pathList) {
                local.addPathInfo(new LPathInfo(pathInfo.getType(), pathInfo.getPath(), pathInfo.isNeedTranscode()));
            }
        }
        return local;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        MeicamResource other = (MeicamResource) obj;
        return isSame(pathList, other.pathList);
    }

    /**
     * 判断两个List内的元素是否相同
     * Determines whether the elements in two lists are the same
     *
     * @param list1
     * @param list2
     * @return
     */
    private boolean isSame(List<PathInfo> list1, List<PathInfo> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (PathInfo pathInfo : list1) {
            if (!list2.contains(pathInfo)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static class PathInfo {
        private String type;
        private String path;
        /**
         * 是否需要转码
         * Whether to transcode
         */
        private boolean needTranscode = false;

        public PathInfo(String type, String path, boolean needTranscode) {
            this.type = type;
            this.path = path;
            this.needTranscode = needTranscode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isNeedTranscode() {
            return needTranscode;
        }

        public void setNeedTranscode(boolean needTranscode) {
            this.needTranscode = needTranscode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            PathInfo other = (PathInfo) obj;

            return TextUtils.equals(path, other.path)
                    && TextUtils.equals(type, other.type)
                    && TextUtils.equals(String.valueOf(needTranscode), String.valueOf(other.needTranscode));
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
