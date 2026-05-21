package com.meishe.cutsame.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.meishe.base.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * author : lhz
 * date   : 2020/11/4
 * desc   :模板中的片段实体类
 * A fragment entity class in a template
 */
public class TemplateClip implements Parcelable, Comparable<TemplateClip> {

    public static final int TYPE_CAN_REPLACE = 0;
    public static final int TYPE_CAN_NOT_REPLACE = 1;

    /**
     * 模板片段所需文件路径
     * The file path required for the template fragment
     */
    private String filePath;
    /**
     * 模板片段时长
     * Template fragment length
     */
    private long duration;
    /**
     * 模板片段的trim 时长
     * the template clip trim duration
     */
    private long trimDuration;
    /**
     * 模板片段类型
     * Template fragment type
     */
    private int type;
    /**
     * 辅助类/属性
     * Helper classes/attributes
     */
    private Object tag;

    private String mFootageId;

    private int groupIndex;

    private long inPoint;

    private long outPoint;
    private long trimIn;
    private long trimOut;


    private int trackIndex;

    /**
     * 是否有编组 1代表有编组，0代表无编组
     * is or not group
     */
    private int isHasGroup;
    /**
     * 是否需要倒放 1需要，0代表不需要
     * is or not Reverse
     */
    private int needReverse;

    private String reversePath;
    /**
     * 填入素材类型
     * media type
     */
    private int mediaType;

    /**
     * 模板类别
     */
    private String templateType = Template.TYPE_TEMPLATE_STANDER;

    /**
     * 片段是否可替换
     * 因为 readBoolean 有sdk版本限制 所以使用int类型
     * 0：表示可替换 1表示不可替换
     */
    public int replaceMode = TYPE_CAN_REPLACE;

    /**
     * clip序列嵌套相关
     */
    private int clipIndex;
    private List<Integer> clipTrackIndexInTimelineList = new ArrayList<>();
    private List<Integer> clipIndexInTimelineList = new ArrayList<>();


    public TemplateClip() {
    }

    public String getFootageId() {
        return mFootageId;
    }

    public TemplateClip setFootageId(String footageId) {
        this.mFootageId = footageId;
        return this;
    }

    public int getType() {
        return type;
    }

    public TemplateClip setType(int type) {
        this.type = type;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public TemplateClip setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public TemplateClip setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getTrimDuration() {
        return trimDuration;
    }

    public TemplateClip setTrimDuration(long trimDuration) {
        this.trimDuration = trimDuration;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public TemplateClip setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public TemplateClip setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
        return this;
    }

    public long getInPoint() {
        return inPoint;
    }

    public TemplateClip setInPoint(long inPoint) {
        this.inPoint = inPoint;
        return this;
    }

    public long getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(long outPoint) {
        this.outPoint = outPoint;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(long trimIn) {
        this.trimIn = trimIn;
    }

    public long getTrimOut() {
        return trimOut;
    }

    public void setTrimOut(long trimOut) {
        this.trimOut = trimOut;
    }

    public int getTrackIndex() {
        return trackIndex;
    }


    public int getReplaceMode() {
        return replaceMode;
    }

    public void setReplaceMode(int replaceMode) {
        this.replaceMode = replaceMode;
    }

    public TemplateClip setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
        return this;
    }

    public boolean isHasGroup() {
        return isHasGroup == 1;
    }

    public TemplateClip setHasGroup(boolean hasGroup) {
        this.isHasGroup = hasGroup ? 1 : 0;
        return this;
    }

    public boolean getNeedReverse() {
        return needReverse == 1;
    }

    public TemplateClip setNeedReverse(boolean needReverse) {
        this.needReverse = needReverse ? 1 : 0;
        return this;
    }

    public String getReversePath() {
        return reversePath;
    }

    public void setReversePath(String reversePath) {
        this.reversePath = reversePath;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public int getClipIndex() {
        return clipIndex;
    }

    public TemplateClip setClipIndex(int clipIndex) {
        this.clipIndex = clipIndex;
        return this;
    }

    public List<Integer> getClipTrackIndexInTimelineList() {
        return clipTrackIndexInTimelineList;
    }

    public TemplateClip setClipTrackIndexInTimelineList(List<Integer> clipTrackIndexInTimelineList) {
        this.clipTrackIndexInTimelineList = clipTrackIndexInTimelineList;
        return this;
    }

    public List<Integer> getClipIndexInTimelineList() {
        return clipIndexInTimelineList;
    }

    public TemplateClip setClipIndexInTimelineList(List<Integer> clipIndexInTimelineList) {
        this.clipIndexInTimelineList = clipIndexInTimelineList;
        return this;
    }

    protected TemplateClip(Parcel in) {
        filePath = in.readString();
        duration = in.readLong();
        trimDuration = in.readLong();
        type = in.readInt();
        mFootageId = in.readString();
        groupIndex = in.readInt();
        inPoint = in.readLong();
        outPoint = in.readLong();
        trimIn = in.readLong();
        trimOut = in.readLong();
        trackIndex = in.readInt();
        isHasGroup = in.readInt();
        needReverse = in.readInt();
        reversePath = in.readString();
        templateType = in.readString();
        replaceMode = in.readInt();
        clipIndex = in.readInt();
        in.readList(clipTrackIndexInTimelineList, Integer.class.getClassLoader());
        in.readList(clipIndexInTimelineList, Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeLong(duration);
        dest.writeLong(trimDuration);
        dest.writeInt(type);
        dest.writeString(mFootageId);
        dest.writeInt(groupIndex);
        dest.writeLong(inPoint);
        dest.writeLong(outPoint);
        dest.writeLong(trimIn);
        dest.writeLong(trimOut);
        dest.writeInt(trackIndex);
        dest.writeInt(isHasGroup);
        dest.writeInt(needReverse);
        dest.writeString(reversePath);
        dest.writeString(templateType);
        dest.writeInt(replaceMode);
        dest.writeInt(clipIndex);
        dest.writeList(clipTrackIndexInTimelineList);
        dest.writeList(clipIndexInTimelineList);
    }

    public TemplateClip copy() {
        return GsonUtils.fromJson(GsonUtils.toJson(this), getClass());
    }

    public void update(TemplateClip clip) {
        setFilePath(clip.getFilePath());
        setDuration(clip.getDuration());
        setTrimDuration(clip.getTrimDuration());
        setType(clip.getType());
        setGroupIndex(clip.getGroupIndex());
        setFootageId(clip.getFootageId());
        setInPoint(clip.getInPoint());
        setOutPoint(clip.getOutPoint());
        setTrimIn(clip.getTrimIn());
        setTrimIn(clip.getTrimOut());
        setTrackIndex(clip.getTrackIndex());
        setHasGroup(clip.isHasGroup());
        setNeedReverse(clip.getNeedReverse());
        setReversePath(clip.getReversePath());
        setTemplateType(clip.getTemplateType());
        setReplaceMode(clip.getReplaceMode());
        setClipIndex(clip.getClipIndex());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TemplateClip> CREATOR = new Creator<TemplateClip>() {
        @Override
        public TemplateClip createFromParcel(Parcel in) {
            return new TemplateClip(in);
        }

        @Override
        public TemplateClip[] newArray(int size) {
            return new TemplateClip[size];
        }
    };

    @Override
    public int compareTo(TemplateClip o) {
        //先按照Inpoint排序  First sort by Inpoint
        int i = (int) (this.getInPoint() - o.getInPoint());
        if (i == 0) {
            //如果Inpoint相同，按照trackIndex排序 If Inpoint is the same, sort by track Index
            return this.getTrackIndex() - o.getTrackIndex();
        }
        return i;
    }
}
