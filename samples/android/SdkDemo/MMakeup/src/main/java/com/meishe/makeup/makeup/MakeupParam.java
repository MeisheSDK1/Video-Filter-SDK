package com.meishe.makeup.makeup;


import com.meishe.makeup.net.RecommendColor;

import java.util.List;


/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/26 13:41
 * @Description :美妆参数 Makeup param
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MakeupParam extends BaseParam {
    /**
     * 推荐的美妆颜色信息，一般是自定义美妆需要，
     * the recommend makeup color
     */
    private List<RecommendColor> makeupRecommendColors;

    private ColorData colorData;

    public List<RecommendColor> getMakeupRecommendColors() {
        return makeupRecommendColors;
    }

    public void setMakeupRecommendColors(List<RecommendColor> makeupRecommendColors) {
        this.makeupRecommendColors = makeupRecommendColors;
    }

    public ColorData getColorData() {
        return colorData;
    }

    public void setColorData(ColorData colorData) {
        this.colorData = colorData;
    }

}
