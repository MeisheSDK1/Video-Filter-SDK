package com.meishe.sdkdemo.edit.data;

import androidx.annotation.NonNull;

import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.utils.FileUtils;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2025/11/12 17:11
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class LivePhotoInfo {
    private String url;
    private long offset;
    private long length;

    public String getUrl() {
        return url;
    }

    public String getLivePhotoUrl() {
        String filePath = FileUtils.getAbsPathByContentUri(MSApplication.getContext(),
                MSApplication.CONTENT_FLAG, getUrl());
        return "meicam://url=" + filePath + "?offset=" + offset + "&length=" + length;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    @NonNull
    @Override
    public LivePhotoInfo clone() {
        try {
            LivePhotoInfo photoInfo = new LivePhotoInfo();
            photoInfo.setUrl(this.getUrl());
            photoInfo.setOffset(this.getOffset());
            photoInfo.setLength(this.getLength());
            return photoInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
