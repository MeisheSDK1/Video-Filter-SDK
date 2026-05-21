package com.meishe.libmakeup.bean;

import com.meishe.nveffectkit.makeup.NveMakeup;
import com.meishe.nveffectkit.makeup.NveMakeupTypeEnum;

public class MakeupListData {
    private String name;
    private String cover;
    private NveMakeup mNvMakeup;
    private NveMakeupTypeEnum type;

    public NveMakeupTypeEnum getType() {
        return type;
    }

    public void setType(NveMakeupTypeEnum type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public NveMakeup getNvMakeup() {
        return mNvMakeup;
    }

    public void setNvMakeup(NveMakeup nvMakeup) {
        mNvMakeup = nvMakeup;
    }
}
