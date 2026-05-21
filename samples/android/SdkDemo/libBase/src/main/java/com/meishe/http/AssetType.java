package com.meishe.http;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/17.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public enum AssetType {
    //全部主题 theme
    THEME_ALL("1", null, null),
    //全轨主题 track theme
    THEME_ALL_TRACK("1", "1", null),
    //段落主题 Paragraph theme
    THEME_PARA("1", "2", null),
    //全部滤镜 filter
    FILTER_ALL("2", null, null),
    //滤镜调色Lut lut filter
    FILTER_ADJUST_LUT("2", "1", "1"),
    //滤镜调色普通 filter adjust normal
    FILTER_ADJUST_NOR("2", "1", "2"),
    //滤镜特效全部 filter all
    FILTER_FX_ALL("2", "2", null),
    //滤镜特效全部 filter all
    FILTER_FX_ALL_2("2", null, null, "1,2"),
    //滤镜特效基础 filter base
    FILTER_FX_BASE("2", "2", "1"),
    //滤镜特效氛围 filter atmos
    FILTER_FX_ATMOS("2", "2", "2"),
    //滤镜特效Bling filter bling
    FILTER_FX_BLING("2", "2", "3"),
    //滤镜特效动感 filter sport
    FILTER_FX_SPORT("2", "2", "4"),
    //滤镜特效自然 filter natural
    FILTER_FX_NATURAL("2", "2", "5"),
    //滤镜特效光影 filter light
    FILTER_FX_LIGHT("2", "2", "6"),
    //滤镜特效复古 filter retro
    FILTER_FX_RETRO("2", "2", "7"),
    //滤镜特效分屏 filter screen
    FILTER_FX_SCREEN("2", "2", "8"),
    //滤镜特效边框 filter bound
    FILTER_FX_BOUND("2", "2", "9"),
    //滤镜特效漫画 filter cartoon
    FILTER_FX_COMICS("2", "2", "10"),
    //滤镜特效纹理 filter texture
    FILTER_FX_TEXTURE("2", "2", "11"),
    //滤镜 动画 All filter animal
    FILTER_ANIMAL_ALL("2", "3", null),
    //滤镜 入动画 filter animal in
    FILTER_ANIMAL_IN("2", "3", "1"),
    //滤镜 出动画 filter animal out
    FILTER_ANIMAL_OUT("2", "3", "2"),
    //滤镜 组合动画 filter animal compound
    FILTER_ANIMAL_COM("2", "3", "3"),
    //全部字幕 caption
    CAPTION_ALL("3", null, null),
    //传统字幕 all caption
    CAPTION_TRA_ALL("3", "1", null),
    //传统字幕 普通 caption normal
    CAPTION_TRA_NOR("3", "1", "1"),
    //传统字幕 边框 caption bound
    CAPTION_TRA_BOUND("3", "1", "2"),
    //传统字幕 逐字 caption text
    CAPTION_TRA_TEXT("3", "1", "3"),
    //传统字幕 动态底图 caption background
    CAPTION_TRA_BG("3", "1", "4"),
    //模块字幕 all compound caption
    CAPTION_COM_ALL("3", "2", null),
    //模块字幕 花字 compound caption word
    CAPTION_COM_WORD("3", "2", "1"),
    //模块字幕 气泡底图 compound caption bubble
    CAPTION_COM_POP("3", "2", "2"),
    //模块字幕 入动画 compound caption animation in
    CAPTION_COM_ANIMAL_IN("3", "2", "3"),
    //模块字幕 出动画 compound caption animation out
    CAPTION_COM_ANIMAL_OUT("3", "2", "4"),
    //模块字幕 组合动画 compound caption animation compound
    CAPTION_COM_ANIMAL_COM("3", "2", "5"),
    //贴纸 全部 sticker
    STICKER_ALL("4", null, null),
    //贴纸 普通 sticker normal
    STICKER_NOR("4", "1", null),
    //贴纸 有声 sticker voice
    STICKER_VOICE("4", "2", null),
    //贴纸 模板 sticker animation in
    STICKER_ANIMAL_IN("4", "3", "1"),
    //贴纸 模板 sticker animation out
    STICKER_ANIMAL_OUT("4", "3", "2"),
    //贴纸 模板 sticker animation compound
    STICKER_ANIMAL_COM("4", "3", "3"),
    //贴纸 模板 sticker template
    STICKER_TEMPLATE("4", "20000", null),
    //全部转场 transition
    TRANS_ALL("5", null, null),
    //2D转场  transition 2D
    TRANS_2D("5", "1", null),
    //3D转场 transition 3D
    TRANS_3D("5", "2", null),
    //字体 第三方提供 font
    FONT_OTHER("6", null, null),
    //全部粒子 particle
    PARTICLE_ALL("9", null, null),
    //全屏粒子 particle screen
    PARTICLE_SCREEN("9", "1", null),
    //手绘粒子 particle hand
    PARTICLE_HAND("9", "2", null),
    //触发粒子 全部  particle trigger
    PARTICLE_TRIGGER("9", "3", null),
    //触发粒子 眼睛 particle trigger eye
    PARTICLE_TRIGGER_EYE("9", "3", "1"),
    //触发粒子 手势 particle trigger hand
    PARTICLE_TRIGGER_HAND("9", "3", "2"),
    //触发粒子 嘴部 particle trigger mouth
    PARTICLE_TRIGGER_MOUTH("9", "3", "3"),
    //全部推镜拍摄 mirror
    PUSH_MIRROR_ALL("13", null, null),
    //全部道具 scene
    AR_SCENE_ALL("14", null, null),
    //全部2D道具 scene 2D
    AR_SCENE_2D_ALL("14", "1", null),
    //全部2D道具 眼 scene 2D eye
    AR_SCENE_2D_EYE("14", "1", "1"),
    //全部2D道具 头 scene 2D head
    AR_SCENE_2D_HEAD("14", "1", "2"),
    //全部2D道具 嘴 scene 2D mouth
    AR_SCENE_2D_MOUTH("14", "1", "3"),
    //全部2D道具 手 scene 2D hand
    AR_SCENE_2D_HAND("14", "1", "4"),
    //全部3D道具 scene 3D
    AR_SCENE_3D_ALL("14", "2", null),
    //全部3D道具 眼 scene 3D eye
    AR_SCENE_3D_EYE("14", "2", "1"),
    //全部3D道具 头 scene 3D head
    AR_SCENE_3D_HEAD("14", "2", "2"),
    //全部3D道具 嘴 scene 3D mouth
    AR_SCENE_3D_MOUTH("14", "2", "3"),
    //全部3D道具 手 scene 3D hand
    AR_SCENE_3D_HAND("14", "2", "4"),
    //全部分割道具 scene segment
    AR_SCENE_SEG_ALL("14", "3", null),
    //全部分割道具 眼 scene segment eye
    AR_SCENE_SEG_EYE("14", "3", "1"),
    //全部分割道具 头 scene segment head
    AR_SCENE_SEG_HEAD("14", "3", "2"),
    //全部分割道具 嘴 scene segment mouth
    AR_SCENE_SEG_MOUTH("14", "3", "3"),
    //全部分割道具 手 scene segment hand
    AR_SCENE_SEG_HAND("14", "3", "4"),
    //全部前景道具  scene pre
    AR_SCENE_PRE_ALL("14", "4", null),
    //全部前景道具 眼 scene prospect eye
    AR_SCENE_PRE_EYE("14", "4", "1"),
    //全部前景道具 头 scene prospect head
    AR_SCENE_PRE_HEAD("14", "4", "2"),
    //全部前景道具 嘴 scene prospect mouth
    AR_SCENE_PRE_MOUTH("14", "4", "3"),
    //全部前景道具 手 scene prospect hand
    AR_SCENE_PRE_HAND("14", "4", "4"),
    AR_SCENE_PARTICLE_ALL("14", "5", null),
    AR_SCENE_PARTICLE_EYE("14", "5", "1"),
    AR_SCENE_PARTICLE_HEAD("14", "5", "2"),
    AR_SCENE_PARTICLE_MOUTH("14", "5", "3"),
    AR_SCENE_PARTICLE_HAND("14", "5", "4"),
    AR_SCENE_PARTICLE_NORMAL("14", "5", "5"),
    //复合字幕 compound caption
    COM_CAPTION_ALL("15", null, null),
    //复合字幕 静态  compound caption static
    COM_CAPTION_STATIC("15", "1", null),
    //复合字幕 动态  compound caption live
    COM_CAPTION_LIVE("15", "2", null),
    //影集 photo album
    PHOTO_ALBUM_ALL("16", null, null),
    //MIMO
    MIMO_ALL("17", null, null),
    //拍摄模板 capture template
    CAPTURE_TEMPLATE_ALL("18", null, null),
    //美摄模板 ms template
    MS_TEMPLATE_ALL("19", null, null),
    //美摄模板 通用 template normal
    MS_TEMPLATE_NOR("19", "1", null),
    //美摄模板 拍照 template photo
    MS_TEMPLATE_PHOTO("19", "2", null),
    //美摄模板 视频 template video
    MS_TEMPLATE_VIDEO("19", "3", null),
    //美妆- 整妆106 makeup 106
    MAKEUP_TYPE_106("20", "1", null),
    //美妆- 整妆240 makeup 240
    MAKEUP_TYPE_240("20", "3", null),
    //美妆- 妆容106 makeup 106
    MAKEUP_TYPE_ALL("21", "2", null),
    //美妆- 妆容106 makeup 106
    //MAKEUP_TYPE_ZR_106("21", "1", null),
    //美妆- 妆容240 makeup 240
    //MAKEUP_TYPE_ZR_240("21", "2", null),
    //美妆- 单妆 All makeup
    MAKEUP_TYPE_LIST_ALL("20", "2", null),
    //美颜模板 Beauty Template
    BEAUTY_TEMPLATE("20", "5", null);
    /**
     * 素材一级类型type
     * Material primary type
     */
    private String type;
    /**
     * 素材二级类型category
     * Material secondary type
     */
    private String category;
    /**
     * 素材三级类型kind
     * Three-level material type
     */
    private String kind;
    /**
     * 素材二级类型详细可多个
     * 想要此参数生效必须将category为空 如1,2
     * The material secondary type can be more detailed
     * For this parameter to take effect, category must be null as 1,2
     */
    private String categories;
    /**
     * 0是按照星级排序，不传或者1是按照创建时间排序
     */
    private int sorted = 1;

    AssetType(String type, String category, String kind) {
        this.type = type;
        this.category = category;
        this.kind = kind;
    }

    AssetType(String type, String category, String kind, String cagetories) {
        this.type = type;
        this.category = category;
        this.kind = kind;
        this.categories = cagetories;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getKind() {
        return kind;
    }

    public String getCategories() {
        return categories;
    }

    public int getSorted() {
        return sorted;
    }

    public void setSorted(int sorted) {
        this.sorted = sorted;
    }
}
