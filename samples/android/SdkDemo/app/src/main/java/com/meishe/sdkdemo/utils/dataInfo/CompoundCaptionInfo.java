package com.meishe.sdkdemo.utils.dataInfo;

import static com.meishe.sdkdemo.utils.dataInfo.CaptionInfo.ATTRIBUTE_UNUSED_FLAG;

import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by chengzhonglin on 2019/06/11.
 */

public class CompoundCaptionInfo {
    /*
     * X缩放因子
     * X scaling factor
     * */
    private float m_scaleFactorX;
    /*
     * Y缩放因子
     * Y scaling factor
     * */
    private float m_scaleFactorY;
    /*
     * 锚点
     * Anchor
     * */
    private PointF m_anchor;
    /*
     * 字幕偏移量
     * Subtitle offset
     * */
    private PointF m_translation;
    /*
     * 旋转角度
     * Rotation angle
     * */
    private float m_rotation;

    /*
     * 字幕入点
     * Subtitle entry point
     * */
    private long m_inPoint;
    /*
     * 字幕出点
     * Subtitle Out
     * */
    private long m_outPoint;
    /*
     * 字幕样式Uuid
     * Caption style Uuid
     * */
    private String m_captionStyleUuid;
    /*
     * 字幕Z值
     * Subtitle Z value
     * */
    private int m_captionZVal;
    /*
    * 字幕属性列表
    * Subtitle attribute list
    * */
    private ArrayList<CompoundCaptionAttr> m_captionAttributeList;

    /*
     * 是否是斜体
     * Whether it is italic
     * */
    private boolean misItalic;

    public int getCaptionZVal() {
        return m_captionZVal;
    }

    public void setCaptionZVal(int m_captionZVal) {
        this.m_captionZVal = m_captionZVal;
    }

    public long getInPoint() {
        return m_inPoint;
    }

    public void setInPoint(long inPoint) {
        this.m_inPoint = inPoint;
    }
    public long getOutPoint() {
        return m_outPoint;
    }

    public void setOutPoint(long outPoint) {
        this.m_outPoint = outPoint;
    }
    public String getCaptionStyleUuid() {
        return m_captionStyleUuid;
    }

    public void setCaptionStyleUuid(String captionStyleUuid) {
        this.m_captionStyleUuid = captionStyleUuid;
    }

    public ArrayList<CompoundCaptionAttr> getCaptionAttributeList() {
        return m_captionAttributeList;
    }
    public void addCaptionAttributeList(CompoundCaptionAttr captionAttribute) {
        this.m_captionAttributeList.add(captionAttribute);
    }

    public CompoundCaptionInfo() {
        m_scaleFactorX = 1.0f;
        m_scaleFactorY = 1.0f;
        m_anchor = null;
        m_rotation = 0;
        m_translation = null;
        m_inPoint = 0;
        m_outPoint = 0;
        m_captionStyleUuid = "";
        m_captionZVal = 0;
        m_captionAttributeList = new ArrayList<>();
    }

    public void setScaleFactorX(float value) {
        m_scaleFactorX = value;
    }

    public float getScaleFactorX() {
        return m_scaleFactorX;
    }
    public void setScaleFactorY(float value) {
        m_scaleFactorY = value;
    }

    public float getScaleFactorY() {
        return m_scaleFactorY;
    }

    public void setAnchor(PointF point) {
        m_anchor = point;
    }

    public PointF getAnchor() {
        return m_anchor;
    }

    public void setRotation(float value) {
        m_rotation = value;
    }

    public float getRotation() {
        return m_rotation;
    }

    public void setTranslation(PointF point) {
        m_translation = point;
    }

    public PointF getTranslation() {
        return m_translation;
    }

    public boolean isMisItalic() {
        return misItalic;
    }

    public void setMisItalic(boolean misItalic) {
        this.misItalic = misItalic;
    }

    public CompoundCaptionInfo clone(){
        CompoundCaptionInfo newCaptionInfo = new CompoundCaptionInfo();
        newCaptionInfo.setAnchor(this.getAnchor());
        newCaptionInfo.setRotation(this.getRotation());
        newCaptionInfo.setScaleFactorX(this.getScaleFactorX());
        newCaptionInfo.setScaleFactorY(this.getScaleFactorY());
        newCaptionInfo.setTranslation(this.getTranslation());

        //copy data
        newCaptionInfo.setInPoint(this.getInPoint());
        newCaptionInfo.setOutPoint(this.getOutPoint());
        newCaptionInfo.setCaptionZVal(this.getCaptionZVal());
        newCaptionInfo.setCaptionStyleUuid(this.getCaptionStyleUuid());
        newCaptionInfo.setMisItalic(this.isMisItalic());

        ArrayList<CompoundCaptionAttr> captionAttrList = this.getCaptionAttributeList();
        if(captionAttrList != null){
            int compoundAttrCount = captionAttrList.size();
            for (int index = 0;index < compoundAttrCount;++index){
                newCaptionInfo.addCaptionAttributeList(captionAttrList.get(index));
            }
        }

        return newCaptionInfo;
    }

    public static class CompoundCaptionAttr{
        public String getCaptionFontName() {
            return captionFontName;
        }

        public void setCaptionFontName(String captionFontName) {
            this.captionFontName = captionFontName;
        }

        public String getCaptionColor() {
            return captionColor;
        }

        public void setCaptionColor(String captionColor) {
            this.captionColor = captionColor;
        }

        public String getCaptionText() {
            return captionText;
        }

        public void setCaptionText(String captionText) {

            this.captionText = captionText;
        }

        public String getCaptionStrokeColor() {
            return captionStrokeColor;
        }

        public void setCaptionStrokeColor(String captionStrokeColor) {
            this.captionStrokeColor = captionStrokeColor;
        }

        public int getCaptionStrokeWidth() {
            return captionStrokeWidth;
        }

        public void setCaptionStrokeWidth(int captionStrokeWidth) {
            this.captionStrokeWidth = captionStrokeWidth;
        }

        public String getCaptionBackgroundColor() {
            return captionBackgroundColor;
        }

        public void setCaptionBackgroundColor(String captionBackgroundColor) {
            this.captionBackgroundColor = captionBackgroundColor;
        }

        public int getM_usedColorFlag() {
            return m_usedColorFlag;
        }

        public void setM_usedColorFlag(int m_usedColorFlag) {
            this.m_usedColorFlag = m_usedColorFlag;
        }

        public int getM_usedBackgroundFlag() {
            return m_usedBackgroundFlag;
        }

        public void setM_usedBackgroundFlag(int m_usedBackgroundFlag) {
            this.m_usedBackgroundFlag = m_usedBackgroundFlag;
        }

        public int getM_usedOutlineFlag() {
            return m_usedOutlineFlag;
        }

        public void setM_usedOutlineFlag(int m_usedOutlineFlag) {
            this.m_usedOutlineFlag = m_usedOutlineFlag;
        }

        public int getCaptionColorAlpha() {
            return captionColorAlpha;
        }

        public void setCaptionColorAlpha(int captionColorAlpha) {
            this.captionColorAlpha = captionColorAlpha;
        }

        public int getCaptionStrokeColorAlpha() {
            return captionStrokeColorAlpha;
        }

        public void setCaptionStrokeColorAlpha(int captionStrokeColorAlpha) {
            this.captionStrokeColorAlpha = captionStrokeColorAlpha;
        }

        public int getCaptionBackgroundColorAlpha() {
            return captionBackgroundColorAlpha;
        }

        public void setCaptionBackgroundColorAlpha(int captionBackgroundColorAlpha) {
            this.captionBackgroundColorAlpha = captionBackgroundColorAlpha;
        }

        public boolean isMisItalic() {
            return misItalic;
        }

        public void setMisItalic(boolean misItalic) {
            this.misItalic = misItalic;
        }

        public CompoundCaptionAttr clone(){
            CompoundCaptionAttr newACaptionAttr = new CompoundCaptionAttr();
            newACaptionAttr.setCaptionColor(this.getCaptionColor());
            newACaptionAttr.setCaptionFontName(this.getCaptionFontName());
            newACaptionAttr.setCaptionText(this.getCaptionText());
            newACaptionAttr.setCaptionBackgroundColor(this.getCaptionBackgroundColor());
            newACaptionAttr.setCaptionStrokeColor(this.getCaptionStrokeColor());
            newACaptionAttr.setCaptionStrokeWidth(this.getCaptionStrokeWidth());
            newACaptionAttr.setM_usedBackgroundFlag(this.getM_usedBackgroundFlag());
            newACaptionAttr.setM_usedColorFlag(this.getM_usedColorFlag());
            newACaptionAttr.setM_usedOutlineFlag(this.getM_usedOutlineFlag());
            newACaptionAttr.setCaptionColorAlpha(this.getCaptionColorAlpha());
            newACaptionAttr.setCaptionStrokeColorAlpha(this.getCaptionStrokeColorAlpha());
            newACaptionAttr.setCaptionBackgroundColorAlpha(this.getCaptionBackgroundColorAlpha());
            newACaptionAttr.setMisItalic(this.isMisItalic());
            return newACaptionAttr;
        }
        private String captionFontName;
        private String captionColor;
        private int captionColorAlpha;
        private String captionText;
        private String captionStrokeColor;
        private int captionStrokeColorAlpha;
        private int captionStrokeWidth;
        private String captionBackgroundColor;
        private int captionBackgroundColorAlpha;
        /*
         * 使用颜色标识
         * Use color identification
         * */
        private int m_usedColorFlag = ATTRIBUTE_UNUSED_FLAG;
        /*
         * 使用背景色标识
         * Use background identification
         * */
        private int m_usedBackgroundFlag = ATTRIBUTE_UNUSED_FLAG;
        /*
         * 使用描边标识
         * Use stroke logo
         * */
        private int m_usedOutlineFlag = ATTRIBUTE_UNUSED_FLAG;

        /*
         * 是否是斜体
         * Whether it is italic
         * */
        private boolean misItalic;

    }
}
