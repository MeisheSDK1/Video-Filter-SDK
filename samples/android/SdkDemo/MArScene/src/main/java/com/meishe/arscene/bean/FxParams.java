package com.meishe.arscene.bean;


import java.io.Serializable;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/17 18:17
 * @Description :ar 的一些参数 AR Params
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FxParams implements Serializable {
    /**
     * 美颜类型：美肤，美型，微整形
     * Beauty type: Beauty, beauty, micro plastic surgery
     */
    public static final int BEAUTY_SKIN = 0X11;
    public static final int BEAUTY_FACE = 0X22;
    public static final int BEAUTY_SMALL = 0X33;
    public static final int BEAUTY_ADJUST = 0X44;
    public static final int BEAUTY_CONTOURING = 0X55;

    public static final String BEAUTY_A = "A";
    public static final String BEAUTY_B = "B";

    /**
     * 美白相关 about beauty whitening kin
     */
    public static final String BEAUTY_WHITENING = "Beauty Whitening";
    public static final String DEFAULT_BEAUTY_LUT_FILE = "Default Beauty Lut File";
    public static final String DEFAULT_BEAUTY_ENABLE = "Default Beauty Enabled";
    public static final String WHITENING_LUT_FILE = "Whitening Lut File";
    public static final String WHITENING_LUT_ENABLE = "Whitening Lut Enabled";

    /**
     * 肤色
     * skin color
     */
    public static final String SKIN_COLOUR = "SkinColour";
    public static final String SKIN_COLOUR_TYPE = "Skin Colour Type";

    /**
     * 红润 Beauty Reddening
     */
    public static final String WHITENING_REDDENING = "Beauty Reddening";

    /**
     * 油光 about Shiny skin
     */
    public static final String ADVANCED_BEAUTY_MATTE_INTENSITY = "Advanced Beauty Matte Intensity";
    public static final String ADVANCED_BEAUTY_MATTE_FILL_RADIUS = "Advanced Beauty Matte Fill Radius";

    /**
     * 校色
     * Color Correct
     */
    public static final String COLOR_CORRECT = "ColorCorrect";
    /**
     * 锐度 sharpness
     */
    public static final String DEFAULT_SHARPEN_ENABLE = "Default Sharpen Enabled";
    public static final String SHARPEN = "Sharpen";
    public static final String SHARPEN_AMOUNT = "Amount";

    /**
     * 清晰度 Definition
     */
    public static final String DEFINITION = "Definition";
    public static final String DEFINITION_INTENSITY = "Intensity";

    /**
     * 磨皮相关 about buffing skin
     **/
    public static final String SKINNING = "skinning";
    public static final String BEAUTY_STRENGTH = "Beauty Strength";
    public static final String ADVANCED_BEAUTY_ENABLE = "Advanced Beauty Enable";
    public static final String ADVANCED_BEAUTY_INTENSITY = "Advanced Beauty Intensity";
    public static final String ADVANCED_BEAUTY_TYPE = "Advanced Beauty Type";

    /**
     * 修容
     * Contouring
     */
    public static final String CONTOURING = "Contouring";
    /* ******************************美型相关***************************************************************/
    /**
     * 窄脸 face width
     */
    public static final String FACE_WIDTH_DEGREE = "Face Mesh Face Width Degree";
    public static final String FACE_WIDTH_PACKAGE_ID = "Face Mesh Face Width Custom Package Id";
    /**
     * 小脸 face length
     */
    public static final String FACE_LENGTH_DEGREE = "Face Mesh Face Length Degree";
    public static final String FACE_LENGTH_PACKAGE_ID = "Face Mesh Face Length Custom Package Id";
    /**
     * 瘦脸 face size
     */
    public static final String FACE_SIZE_DEGREE = "Face Mesh Face Size Degree";
    public static final String FACE_SIZE_PACKAGE_ID = "Face Mesh Face Size Custom Package Id";
    /**
     * 额头 forehead height
     */
    public static final String FOREHEAD_HEIGHT_DEGREE = "Face Mesh Forehead Height Degree";
    public static final String FOREHEAD_HEIGHT_PACKAGE_ID = "Face Mesh Forehead Height Custom Package Id";
    // public static final String FOREHEAD_HEIGHT_DEGREE = "Forehead Height Warp Degree";
    // public static final String FOREHEAD_HEIGHT_PACKAGE_ID = "Warp Forehead Height Custom Package Id";
    /**
     * 下巴 chin length
     */
    public static final String FACE_CHIN_LENGTH_DEGREE = "Face Mesh Chin Length Degree";
    public static final String FACE_CHIN_LENGTH_PACKAGE_ID = "Face Mesh Chin Length Custom Package Id";

    /**
     * 大眼 eye size
     */
    public static final String EYE_SIZE_DEGREE = "Face Mesh Eye Size Degree";
    public static final String EYE_SIZE_PACKAGE_ID = "Face Mesh Eye Size Custom Package Id";

    /**
     * 眼角 eye corner
     */
    public static final String EYE_CORNER_DEGREE = "Face Mesh Eye Corner Stretch Degree";
    public static final String EYE_CORNER_PACKAGE_ID = "Face Mesh Eye Corner Stretch Custom Package Id";

    /**
     * 瘦鼻 nose width
     */
    public static final String NOSE_WIDTH_DEGREE = "Face Mesh Nose Width Degree";
    public static final String NOSE_WIDTH_PACKAGE_ID = "Face Mesh Nose Width Custom Package Id";

    /**
     * 长鼻 nose length
     */
    public static final String NOSE_LENGTH_DEGREE = "Face Mesh Nose Length Degree";
    public static final String NOSE_LENGTH_PACKAGE_ID = "Face Mesh Nose Length Custom Package Id";

    /**
     * 嘴型 mouth size
     */
    public static final String MOUTH_SIZE_DEGREE = "Face Mesh Mouth Size Degree";
    public static final String MOUTH_SIZE_PACKAGE_ID = "Face Mesh Mouth Size Custom Package Id";

    /**
     * 嘴角 mouth corner
     */
    public static final String MOUTH_CORNER_DEGREE = "Face Mesh Mouth Corner Lift Degree";
    public static final String MOUTH_CORNER_PACKAGE_ID = "Face Mesh Mouth Corner Lift Custom Package Id";

    /**
     * 缩头 head size
     */
//    public static final String HEAD_SIZE_DEGREE = "Face Mesh Head Size Degree";
    public static final String HEAD_SIZE_DEGREE = "Head Size Warp Degree";
    //    public static final String HEAD_SIZE_PACKAGE_ID = "Face Mesh Head Size Custom Package Id";
    public static final String HEAD_SIZE_PACKAGE_ID = "Warp Head Size Custom Package Id";

    /**
     * 颧骨宽 cheekbone width
     */
    public static final String CHEEKBONE_WIDTH_DEGREE = "Face Mesh Malar Width Degree";
    public static final String CHEEKBONE_WIDTH_PACKAGE_ID = "Face Mesh Malar Width Custom Package Id";

    /**
     * 下颌宽 Jaw Width
     */
    public static final String JAW_WIDTH_DEGREE = "Face Mesh Jaw Width Degree";
    public static final String JAW_WIDTH_PACKAGE_ID = "Face Mesh Jaw Width Custom Package Id";

    /**
     * 太阳穴宽 temple width
     */
    public static final String TEMPLE_WIDTH_DEGREE = "Face Mesh Temple Width Degree";
    public static final String TEMPLE_WIDTH_PACKAGE_ID = "Face Mesh Temple Width Custom Package Id";
    /**
     * 法令纹  nasolabial folds
     */
    public static final String NASOLABIAL_FOLDS = "Advanced Beauty Remove Nasolabial Folds Intensity";
    /**
     * 黑眼圈 black eye
     */
    public static final String DARK_CIRCLES = "Advanced Beauty Remove Dark Circles Intensity";
    /**
     * 亮眼 Brighten Eyes
     */
    public static final String BRIGHTEN_EYES = "Advanced Beauty Brighten Eyes Intensity";

    /**
     * 美牙 Whiten Teeth
     */
    public static final String WHITEN_TEETH = "Advanced Beauty Whiten Teeth Intensity";

    /**
     * 眼角度 Eye Angle
     */
    public static final String EYE_ANGLE_DEGREE = "Face Mesh Eye Angle Degree";
    public static final String EYE_ANGLE_PACKAGE_ID = "Face Mesh Eye Angle Custom Package Id";
    /**
     * 眼离 eye distance
     */
    public static final String EYE_DISTANCE_DEGREE = "Face Mesh Eye Distance Degree";
    public static final String EYE_DISTANCE_PACKAGE_ID = "Face Mesh Eye Distance Custom Package Id";

    /**
     * 眼高度 Eye Height
     */
    public static final String EYE_HEIGHT_DEGREE = "Face Mesh Eye Height Degree";
    public static final String EYE_HEIGHT_PACKAGE_ID = "Face Mesh Eye Height Custom Package Id";

    /**
     * 眼宽度 Eye Width
     */
    public static final String EYE_WIDTH_DEGREE = "Face Mesh Eye Width Degree";
    public static final String EYE_WIDTH_PACKAGE_ID = "Face Mesh Eye Width Custom Package Id";

    /**
     * 眼弧度 Eye Arc
     */
    public static final String EYE_ARC_DEGREE = "Face Mesh Eye Arc Degree";
    public static final String EYE_ARC_PACKAGE_ID = "Face Mesh Eye Arc Custom Package Id";

    /**
     * 眼上下 Eye Y Offset
     */
    public static final String EYE_Y_OFFSET_DEGREE = "Face Mesh Eye Y Offset Degree";
    public static final String EYE_Y_OFFSET_PACKAGE_ID = "Face Mesh Eye Y Offset Custom Package Id";

    /**
     * 人中 philtrum
     */
    public static final String PHILTRUM_LENGTH_DEGREE = "Face Mesh Philtrum Length Degree";
    public static final String PHILTRUM_LENGTH_PACKAGE_ID = "Face Mesh Philtrum Length Custom Package Id";

    /**
     * 鼻梁宽度 Nose Bridge Width
     */
    public static final String NOSE_BRIDGE_WIDTH_DEGREE = "Face Mesh Nose Bridge Width Degree";
    public static final String NOSE_BRIDGE_WIDTH_PACKAGE_ID = "Face Mesh Nose Bridge Width Custom Package Id";

    /**
     * 鼻头 Nose Head Width
     */
    public static final String NOSE_HEAD_WIDTH_DEGREE = "Face Mesh Nose Head Width Degree";
    public static final String NOSE_HEAD_WIDTH_PACKAGE_ID = "Face Mesh Nose Head Width Custom Package Id";

    /**
     * 眉毛粗细 Eyebrow Thickness
     */
    public static final String EYEBROW_THICKNESS_DEGREE = "Face Mesh Eyebrow Thickness Degree";
    public static final String EYEBROW_THICKNESS_PACKAGE_ID = "Face Mesh Eyebrow Thickness Custom Package Id";

    /**
     * 眉角度 Eyebrow Angle
     */
    public static final String EYEBROW_ANGLE_DEGREE = "Face Mesh Eyebrow Angle Degree";
    public static final String EYEBROW_ANGLE_PACKAGE_ID = "Face Mesh Eyebrow Angle Custom Package Id";

    /**
     * 眉上下 Eyebrow Y Offset
     */
    public static final String EYEBROW_Y_OFFSET_DEGREE = "Face Mesh Eyebrow Y Offset Degree";
    public static final String EYEBROW_Y_OFFSET_PACKAGE_ID = "Face Mesh Eyebrow Y Offset Custom Package Id";

    /**
     * 眉间距 Eyebrow X Offset
     */
    public static final String EYEBROW_X_OFFSET_DEGREE = "Face Mesh Eyebrow X Offset Degree";
    public static final String EYEBROW_X_OFFSET_PACKAGE_ID = "Face Mesh Eyebrow X Offset Custom Package Id";

    /**
     * 获取美颜类型
     * Get beauty types
     *
     * @return int[]
     */
    public static int[] getBeautyType() {
        return new int[]{BEAUTY_SKIN, BEAUTY_FACE, BEAUTY_SMALL};
    }

    public static int[] getBeautyTemplateType() {
        return new int[]{BEAUTY_SKIN, BEAUTY_FACE, BEAUTY_SMALL, BEAUTY_ADJUST, BEAUTY_CONTOURING};
    }

    public FxParams(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public FxParams(FxParams params) {
        this.key = params.key;
        this.value = params.value;
        this.defaultValue = params.defaultValue;
        this.type = params.type;
    }

    /**
     * 扩展类型，暂时没有用到，比如value是String类型，但是该value对应的是sdk fx 某一个特殊的方法，而非fx的setStringVal，就可以定义一个该类型的
     * An extension type that is not currently used, such as a String value that corresponds to a special sdk fx method rather than the setStringVal of fx, can be defined
     */
    public int type;
    public String key;
    public Object value;
    public Object defaultValue;

    public Object getDefaultValue() {
        return defaultValue;
    }

    public FxParams setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public FxParams setValue(Object value) {
        this.value = value;
        return this;
    }

    public String getStringValue() {
        if (value instanceof String) {
            return (String) value;
        }
        return "";
    }

    public double getDoubleValue() {
        if (value instanceof Double) {
            return (Double) value;
        }
        return 0;
    }

    public int getIntValue() {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return 0;
    }

    public Double getDefaultDoubleValue() {
        if (defaultValue instanceof Double) {
            return (Double) defaultValue;
        }
        return 0D;
    }

    public void copy(FxParams params) {
        this.key = params.key;
        this.value = params.value;
        this.defaultValue = params.defaultValue;
        this.type = params.type;
    }

    @Override
    public String toString() {
        return "FxParams{" +
                "type=" + type +
                ", key='" + key + '\'' +
                ", value=" + value +
                ", defaultValue=" + defaultValue +
                '}';
    }
}
