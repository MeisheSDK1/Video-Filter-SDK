package com.meishe.http.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/18.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class UserInfo implements Parcelable {
    private String nickname;// 昵称 nickname
    private String iconUrl;// 头像 head portrait

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        nickname = in.readString();
        iconUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nickname);
        dest.writeString(iconUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
