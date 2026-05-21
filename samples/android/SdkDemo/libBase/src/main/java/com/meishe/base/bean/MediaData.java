package com.meishe.base.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * author : lhz
 * date   : 2020/10/20
 * desc   :媒体文件实体类
 * Media file entity class
 */
public class MediaData implements Parcelable {
    /*
    * 图片和视频
    * Pictures and Videos
    * */
    public static final int TYPE_ALL = 0;
    /*
    * 视频
    * video
    * */
    public static final int TYPE_VIDEO = 1;
    /*
    * 图片
    * picture
    * */
    public static final int TYPE_PHOTO = 2;
    private int id;
    private int type;
    private String path = "";
    private String thumbPath = "";
    /*
    * 时长，毫秒，视频类型会有值
    * The length, the milliseconds, the video type will have a value
    * */
    private long duration;
    private long date;
    private String displayName = "";
    private boolean state;
    private int position = -1;
    /**
     * 帮助类，用于存放设置的各种标识
     * Help class to hold the various identifiers for the Settings
     */
    private Object tag;

    public MediaData() {
    }

    protected MediaData(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        path = in.readString();
        thumbPath = in.readString();
        duration = in.readLong();
        date = in.readLong();
        displayName = in.readString();
        state = in.readByte() != 0;
        position = in.readInt();
    }

    public int getId() {
        return id;
    }

    public MediaData setId(int id) {
        this.id = id;
        return this;
    }

    public int getType() {
        return type;
    }

    public MediaData setType(int type) {
        this.type = type;
        return this;
    }

    public String getPath() {
        return path;
    }

    public MediaData setPath(String path) {
        this.path = path;
        return this;
    }

    public String getThumbPath() {
        if (TextUtils.isEmpty(thumbPath)) {
            return path;
        }
        return thumbPath;
    }

    public MediaData setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public MediaData setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getDate() {
        return date;
    }

    public MediaData setDate(long date) {
        this.date = date;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MediaData setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public boolean isState() {
        return state;
    }

    public MediaData setState(boolean state) {
        this.state = state;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public MediaData setPosition(int position) {
        this.position = position;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public MediaData setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public MediaData copy() {
        return new MediaData()
                .setId(id)
                .setPath(path)
                .setState(state)
                .setDate(date)
                .setDisplayName(displayName)
                .setDuration(duration)
                .setPosition(position)
                .setThumbPath(thumbPath)
                .setType(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeString(path);
        dest.writeString(thumbPath);
        dest.writeLong(duration);
        dest.writeLong(date);
        dest.writeString(displayName);
        dest.writeByte((byte) (state ? 1 : 0));
        dest.writeInt(position);
    }

    public static final Creator<MediaData> CREATOR = new Creator<MediaData>() {
        @Override
        public MediaData createFromParcel(Parcel in) {
            return new MediaData(in);
        }

        @Override
        public MediaData[] newArray(int size) {
            return new MediaData[size];
        }
    };

}
