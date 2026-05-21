package com.meishe.engine.bean;

import com.meicam.sdk.NvsVideoTrack;
import com.meicam.sdk.NvsVideoTransition;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.adapter.parser.IResourceParser;
import com.meishe.engine.local.LMeicamTransition;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.meishe.engine.bean.CommonData.EFFECT_BUILTIN;
import static com.meishe.engine.bean.CommonData.TYPE_BUILD_IN;
import static com.meishe.engine.bean.CommonData.TYPE_PACKAGE;


/**
 * Created by CaoZhiChao on 2020/7/3 20:23
 */
public class MeicamTransition extends NvsObject<NvsVideoTransition> implements Cloneable, Serializable,
        TimelineDataToLocalAdapter<LMeicamTransition>, IResourceParser {
    private int index;

    private String type;
    /**
     * 内置转场为名字 包专场为id。
     * Built-in transition for name package special field for id.
     */
    private String desc;
    private String displayName;
    private String displayNamezhCN;
    private String iconPath;
    private int iconResourceId;
    private long duration = CommonData.TIMEBASE;
    private String resourceId;

    public MeicamTransition() {
        super(null);
    }

    public MeicamTransition(int index, String type, String desc) {
        super(null);
        this.index = index;
        this.type = type;
        this.desc = desc;
    }

    public MeicamTransition(int index, String type, String desc, String iconPath) {
        super(null);
        this.index = index;
        this.type = type;
        this.desc = desc;
        this.iconPath = iconPath;
    }

    public MeicamTransition(int index, String type, String desc, int iconResourceId) {
        super(null);
        this.index = index;
        this.type = type;
        this.desc = desc;
        this.iconResourceId = iconResourceId;
    }


    public NvsVideoTransition bindToTimeline(NvsVideoTrack videoTrack) {
        NvsVideoTransition videoTransition;
        if (TYPE_BUILD_IN.equals(type)) {
            videoTransition = videoTrack.setBuiltinTransition(index, desc);
        } else {
            videoTransition = videoTrack.setPackagedTransition(index, desc);
        }
        setObject(videoTransition);
        if (videoTransition != null) {
            videoTransition.setVideoTransitionDuration(getDuration(),
                    NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_NONE);
        }
        return videoTransition;
    }

    @Override
    public void loadData(NvsVideoTransition videoTransition) {
        setObject(videoTransition);
        if (videoTransition.getVideoTransitionType() == EFFECT_BUILTIN) {
            type = TYPE_BUILD_IN;
            desc = videoTransition.getBuiltinVideoTransitionName();
        } else {
            type = TYPE_PACKAGE;
            desc = videoTransition.getVideoTransitionPackageId();
        }
        duration = videoTransition.getVideoTransitionDuration();
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
        NvsVideoTransition object = getObject();
        if (object != null) {
            object.setVideoTransitionDuration(duration, NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_NONE);
        }
        this.duration = duration;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof MeicamTransition) {
            MeicamTransition transition = (MeicamTransition) object;
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
    public LMeicamTransition parseToLocalData() {
        parseToResourceId();
        LMeicamTransition local = new LMeicamTransition();
        local.setDesc(getDesc());
        local.setDisplayName(getDisplayName());
        local.setDisplayNamezhCN(getDisplayNamezhCN());
        local.setDuration(getDuration());
        local.setIconPath(getIconPath());
        local.setIconResourceId(getIconResourceId());
        local.setIndex(getIndex());
        local.setType(getType());
        local.setResourceId(resourceId);
        return local;
    }

    @Override
    public void parseToResourceId() {
     /*   MeicamResource resource = null;
        if (!TextUtils.isEmpty(iconPath)) {
            resource = new MeicamResource();
            resource.addPathInfo(new MeicamResource.PathInfo("iconPath", iconPath,false));
        }

        if (PACKAGE.equals(type)) {
            if (!TextUtils.isEmpty(desc)) {
                if (resource == null) {
                    resource = new MeicamResource();
                    resource.addPathInfo(new MeicamResource.PathInfo("path", desc,false));
                }
            }
        }
        if (resource != null) {
            resourceId = TimelineData.getInstance().getPlaceId(resource);
        }*/
    }
}
