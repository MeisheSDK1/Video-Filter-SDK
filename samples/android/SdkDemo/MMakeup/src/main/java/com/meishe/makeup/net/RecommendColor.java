package com.meishe.makeup.net;

import java.io.Serializable;

/**
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2022/5/18.
 */
public class RecommendColor implements Serializable {
    private String makeupColor;

    public String getMakeupColor() {
        return makeupColor;
    }

    public void setMakeupColor(String makeupColor) {
        this.makeupColor = makeupColor;
    }
}
