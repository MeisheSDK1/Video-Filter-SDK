package com.meishe.engine.bean.background;

import com.meishe.engine.local.background.LMeicamBackgroundStory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * author：yangtailin on 2020/7/10 20:38
 */
public class MeicamBackgroundStory extends MeicamStoryboardInfo {
    private int backgroundType;

    public MeicamBackgroundStory() {
        classType = "BackgroundStory";
        subType = MeicamStoryboardInfo.SUB_TYPE_BACKGROUND;
    }
    
    public int getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(int backgroundType) {
        this.backgroundType = backgroundType;
    }

    @Override
    public LMeicamBackgroundStory parseToLocalData() {
        LMeicamBackgroundStory local = new LMeicamBackgroundStory();
        setCommonData(local);
        local.setStoryDesc(getStoryDesc());
        local.setSource(getSource());
        local.setSourceDir(getSourceDir());
        Map<String, Float> localClipTrans = new HashMap<>();
        Map<String, Float> clipTrans = getClipTrans();
        Set<String> keySet = clipTrans.keySet();
        for (String key : keySet) {
            localClipTrans.put(key, clipTrans.get(key));
        }
        local.setClipTrans(localClipTrans);
        local.setBackgroundType(getBackgroundType());
        return local;
    }
}
