package com.meishe.sdkdemo.capture;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.meishe.sdkdemo.BR;

import java.util.List;

/**
 * @Class: com.meishe.sdkdemo.capture.BeautyShapeDataItem.java
 * @Time: 2022/3/22 22:14:18
 * @author: lpf
 * @Description: 拍摄页面Style;Style data class for capturing video
 */
public class BeautyStyleInfo extends BaseObservable {
    private int resId;
    private String name;
    private String type;
    private boolean visible;
    private String textDefaultColor="#ff707070";
    private String textSelectColor="#ff63ABFF";

    private List<BeautyShapeDataItem> dataItems;


    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Bindable
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        notifyPropertyChanged(BR.visible);
    }

    public String getTextDefaultColor() {
        return textDefaultColor;
    }

    public String getTextSelectColor() {
        return textSelectColor;
    }

    public void setTextDefaultColor(String textDefaultColor) {
        this.textDefaultColor = textDefaultColor;
    }

    public void setTextSelectColor(String textSelectColor) {
        this.textSelectColor = textSelectColor;
    }

    public List<BeautyShapeDataItem> getDataItems() {
        return dataItems;
    }

    public void setDataItems(List<BeautyShapeDataItem> dataItems) {
        this.dataItems = dataItems;
    }
}
