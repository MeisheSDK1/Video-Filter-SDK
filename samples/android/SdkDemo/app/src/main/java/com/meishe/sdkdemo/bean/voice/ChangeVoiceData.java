package com.meishe.sdkdemo.bean.voice;

import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;

/**
 * @author zcy
 * @Destription:
 * @Emial: zcywell@163.com
 * @CreateDate: 2021/3/3.
 */
public class ChangeVoiceData {
    //背景颜色字符串 #ffffff
//    Background color string
    private String bgColor = "#627883";
    private int bgRes;
    //背景图
//    Background img url
    private String bgUrl;
    //变声Id
    //Change voice Id
    private String voiceId;
    //显示名称
    //show namw
    private String name;


    /**
     * 创建无
     * create none data
     * @return
     */
    public static ChangeVoiceData noneData() {
        ChangeVoiceData voiceData = new ChangeVoiceData();
        voiceData.setBgRes(R.mipmap.none);
        voiceData.setName(MSApplication.getContext().getResources().getString(R.string.timeline_fx_none));
        return voiceData;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBgRes() {
        return bgRes;
    }

    public void setBgRes(int bgRes) {
        this.bgRes = bgRes;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }
}
