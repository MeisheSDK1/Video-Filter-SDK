package com.meishe.engine.interf;

import com.meicam.sdk.NvsVideoClip;

public interface StreamingConstant {
    /**
     * 视频片段类型的枚举类。分别代表视频和图片
     * <p></p>
     * Enumeration class for the video clip type. Represent video and picture respectively
     */
    class VideoClipType {
        public static final int VIDEO_CLIP_TYPE_AV = NvsVideoClip.VIDEO_CLIP_TYPE_AV;
        public static final int VIDEO_CLIP_TYPE_IMAGE = NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE;
    }
}
