package com.meishe.sdkdemo.bean;

import com.meishe.sdkdemo.edit.data.AudioEqualizerItem;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/1/11 18:06
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class EqualizerType {
    private String name;
    private String zh_name;
    private List<AudioEqualizerItem> valueList;

    public void setName(String name) {
        this.name = name;
    }

    public void setValueList(List<AudioEqualizerItem> valueList) {
        this.valueList = valueList;
    }

    public void setZh_name(String zh_name) {
        this.zh_name = zh_name;
    }

    public List<AudioEqualizerItem> getValueList() {
        return valueList;
    }

    public String getName() {
        return name;
    }

    public String getZh_name() {
        return zh_name;
    }

    public static class EqualizerTypeItemValue{
        public String key;
        public int value;
        public String effectKey;

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }

        public void setEffectKey(String effectKey) {
            this.effectKey = effectKey;
        }

        public String getEffectKey() {
            return effectKey;
        }
    }
}
