package com.meishe.engine.local.background;
import com.meishe.engine.bean.background.MeicamBackgroundStory;
import com.meishe.engine.bean.background.MeicamStoryboardInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * author：yangtailin on 2020/8/11 15:56
 */
public class LMeicamBackgroundStory extends LMeicamStoryboardInfo {

    private int backgroundType;

    public LMeicamBackgroundStory() {
        classType = "BackgroundStory";
        subType = MeicamStoryboardInfo.SUB_TYPE_BACKGROUND;
        classType = "BackgroundStory";
    }

    public int getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(int backgroundType) {
        this.backgroundType = backgroundType;
    }

    @Override
    public MeicamBackgroundStory parseToTimelineData() {
        MeicamBackgroundStory timelineData = new MeicamBackgroundStory();
        setCommonData(timelineData);
        timelineData.setStoryDesc(getStoryDesc());
        timelineData.setSource(getSource());
        timelineData.setSourceDir(getSourceDir());
        Map<String, Float> localClipTrans = new HashMap<>();
        Map<String, Float> clipTrans = getClipTrans();
        Set<String> keySet = clipTrans.keySet();
        for (String key : keySet) {
            localClipTrans.put(key, clipTrans.get(key));
        }
        timelineData.setClipTrans(localClipTrans);
        timelineData.setBackgroundType(getBackgroundType());
        return timelineData;
    }
}
