package com.meishe.engine.bean.template;

import android.graphics.Bitmap;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsCompoundCaption;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by CaoZhiChao on 2020/11/18 17:45
 * 模板标题Desc类
 * Template title Desc class
 */
public class TemplateCaptionDesc extends NvsAssetPackageManager.NvsTemplateCaptionDesc implements Comparable<TemplateCaptionDesc> {

    public TemplateCaptionDesc() {
    }

    private Bitmap mBitmap;
    private long inPoint;
    /**
     * 不限长模板使用到这个字段保存字幕对象
     * Unlimited length templates are used to hold subtitle objects in this field
     */
    private NvsCaption nvsCaption;
    private NvsCompoundCaption nvsCompoundCaption;
    private int captionType;
    /**
     * 如果是组合字幕，拆分成多个子字幕对象，保存子字幕的index
     * If it is a combined subtitle, split it into multiple subtitle objects and save the index of the subtitle
     */
    private int captionIndex;
    public String replaceId;
    public String text;
    public String originalText;
    public List<TemplateCaptionDesc> subCaptions = new ArrayList<>();
    public int clipIndex = -1;
    public int trackIndex = -1;
    /**
     * 编组ID
     * Marshalling ID
     */
    private String groupID;
    private int groupIndex;
    public List<TemplateCompoundCaptionItemDesc> itemList;
    public List<TemplateCompoundCaptionItemDesc> originalItemList;
    private List<Integer> clipTrackIndexInTimelineList = new ArrayList<>();
    private List<Integer> clipIndexInTimelineList = new ArrayList<>();

    public long getInPoint() {
        return inPoint;
    }

    public void setInPoint(long inPoint) {
        this.inPoint = inPoint;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setCaptionType(int captionType) {
        this.captionType = captionType;
    }

    public int getCaptionType() {
        return captionType;
    }

    public void setNvsCaption(NvsCaption nvsCaption) {
        this.nvsCaption = nvsCaption;
    }

    public NvsCaption getNvsCaption() {
        return nvsCaption;
    }

    public void setNvsCompoundCaption(NvsCompoundCaption nvsCompoundCaption) {
        this.nvsCompoundCaption = nvsCompoundCaption;
    }

    public NvsCompoundCaption getNvsCompoundCaption() {
        return nvsCompoundCaption;
    }

    public void setCaptionIndex(int captionIndex) {
        this.captionIndex = captionIndex;
    }

    public int getCaptionIndex() {
        return captionIndex;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
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
        this.clipTrackIndexInTimelineList.addAll(clipTrackIndexInTimelineList);
    }

    public List<Integer> getClipIndexInTimelineList() {
        return clipIndexInTimelineList;
    }

    public void setClipIndexInTimelineList(List<Integer> clipIndexInTimelineList) {
        this.clipIndexInTimelineList.addAll(clipIndexInTimelineList);
    }

    /**
     * 由复合字幕描述信息映射过来的创建方法
     * The creator from the NvsTemplateCompoundCaptionDesc
     *
     * @param nvsTemplateCaptionDesc 复合字幕描述数据 Composite subtitles describe data
     * @return the TemplateCaptionDesc
     */
    public static TemplateCaptionDesc create(NvsAssetPackageManager.NvsTemplateCompoundCaptionDesc nvsTemplateCaptionDesc) {
        TemplateCaptionDesc templateCaptionDesc = new TemplateCaptionDesc();
        templateCaptionDesc.captionType = TemplateCaptionType.CLIP_COMPOUND_CAPTION;
        if (!CommonUtils.isEmpty(nvsTemplateCaptionDesc.itemList)) {
            List<TemplateCaptionDesc.TemplateCompoundCaptionItemDesc> itemDescs = new ArrayList<>();
            for (NvsAssetPackageManager.NvsTemplateCompoundCaptionItemDesc nvsTemplateCompoundCaptionItemDesc
                    : nvsTemplateCaptionDesc.itemList) {
                itemDescs.add(TemplateCaptionDesc.TemplateCompoundCaptionItemDesc.create(nvsTemplateCompoundCaptionItemDesc));
            }
            templateCaptionDesc.itemList = itemDescs;
            ArrayList<TemplateCompoundCaptionItemDesc> cloneData = new ArrayList<>();
            for (TemplateCompoundCaptionItemDesc itemDesc : itemDescs) {
                try {
                    cloneData.add(itemDesc.clone());
                    templateCaptionDesc.text = itemDesc.text;
                } catch (CloneNotSupportedException e) {
                    LogUtils.e(e);
                }
            }
            templateCaptionDesc.originalItemList = cloneData;
        }
        templateCaptionDesc.replaceId = nvsTemplateCaptionDesc.replaceId;
        templateCaptionDesc.trackIndex = nvsTemplateCaptionDesc.trackIndex;
        templateCaptionDesc.clipIndex = nvsTemplateCaptionDesc.clipIndex;
        for (NvsAssetPackageManager.NvsTemplateCompoundCaptionDesc nvsTemplateCompoundCaptionDesc
                : nvsTemplateCaptionDesc.subCaptions) {
            templateCaptionDesc.subCaptions.add(TemplateCaptionDesc.create(nvsTemplateCompoundCaptionDesc));
        }

        return templateCaptionDesc;
    }

    /**
     * 由普通字幕描述信息映射过来的创建方法
     * The creator from the NvsTemplateCaptionDesc
     *
     * @param nvsTemplateCaptionDesc 普通字幕描述数据 Composite subtitles describe data
     * @return the TemplateCaptionDesc
     */
    public static TemplateCaptionDesc create(NvsAssetPackageManager.NvsTemplateCaptionDesc nvsTemplateCaptionDesc) {
        TemplateCaptionDesc templateCaptionDesc = new TemplateCaptionDesc();
        templateCaptionDesc.captionType = TemplateCaptionType.CLIP_CAPTION;
        templateCaptionDesc.text = nvsTemplateCaptionDesc.text;
        templateCaptionDesc.originalText = nvsTemplateCaptionDesc.text;
        templateCaptionDesc.replaceId = nvsTemplateCaptionDesc.replaceId;
        templateCaptionDesc.clipIndex = nvsTemplateCaptionDesc.clipIndex;
        templateCaptionDesc.trackIndex = nvsTemplateCaptionDesc.trackIndex;
        for (NvsAssetPackageManager.NvsTemplateCaptionDesc nvsTemplateCaption : nvsTemplateCaptionDesc.subCaptions) {
            templateCaptionDesc.subCaptions.add(TemplateCaptionDesc.create(nvsTemplateCaption));
        }
        return templateCaptionDesc;
    }

    /**
     * Is caption boolean.
     * 是否是普通字幕
     *
     * @return the boolean
     */
    public boolean isCaption() {
        return captionType == 1 || captionType == 3;
    }

    /**
     * Is compound caption boolean.
     * 是否是组合字幕
     *
     * @return the boolean
     */
    public boolean isCompoundCaption() {
        return captionType == 2 || captionType == 4;
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public void setText(int index, String text) {
        if (CommonUtils.isIndexAvailable(index, itemList)) {
            itemList.get(index).text = text;
        }
    }

    public String getText(int index) {
        if (CommonUtils.isIndexAvailable(index, itemList)) {
            return itemList.get(index).text;
        }
        return "";
    }

    public String getOriginalText(int index) {
        if (CommonUtils.isIndexAvailable(index, originalItemList)) {
            return originalItemList.get(index).text;
        }
        return "";
    }

    @Override
    public int compareTo(TemplateCaptionDesc o) {
        /*
         * 先按照创建时间排序 time sort first
         */
        if (o == null) {
            return 0;
        }
        return (int) (this.getInPoint() - o.getInPoint());
    }

    public static class TemplateCaptionType {
        /**
         * 时间线普通字幕
         * Timeline common subtitles
         */
        public static final int TIMELINE_CAPTION = 1;
        /**
         * 时间线组合字幕
         * The timeline combines subtitles
         */
        public static final int TIMELINE_COMPOUND_CAPTION = 2;
        /**
         * clip字幕
         * clip subtitle
         */
        public static final int CLIP_CAPTION = 3;
        /**
         * clip组合字幕
         * clip combined subtitles
         */
        public static final int CLIP_COMPOUND_CAPTION = 4;
    }

    /**
     * 复合字幕子字幕类
     * The template compound caption item desc
     */
    public static class TemplateCompoundCaptionItemDesc implements Cloneable {
        public int index;
        public String text;


        public TemplateCompoundCaptionItemDesc(int index, String text) {
            this.index = index;
            this.text = text;
        }

        @NonNull
        @Override
        protected TemplateCompoundCaptionItemDesc clone() throws CloneNotSupportedException {
            return (TemplateCompoundCaptionItemDesc) super.clone();
        }

        public static TemplateCompoundCaptionItemDesc create(NvsAssetPackageManager.NvsTemplateCompoundCaptionItemDesc nvsTemplateCompoundCaptionItemDesc) {
            return new TemplateCompoundCaptionItemDesc(nvsTemplateCompoundCaptionItemDesc.index, nvsTemplateCompoundCaptionItemDesc.text);
        }
    }
}
