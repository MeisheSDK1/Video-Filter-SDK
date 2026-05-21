package com.meishe.engine.bean;

import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meicam.sdk.NvsVideoTransition;
import com.meishe.base.constants.BaseConstants;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.constant.NvsConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.meishe.engine.bean.CommonData.MAIN_TRACK_INDEX;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_REDDENING;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_STRENGTH;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_WHITENING;

/**
 * Created by CaoZhiChao on 2020/7/6 18:06
 */
public class TimelineDataUtil {
    private static final String TAG = "TimelineDataUtil";

    public static ClipInfo getTimelineVideoFxData(int trackIndex, long inPoint) {
        List<MeicamTimelineVideoFxTrack> stickerCaptionTracks = TimelineData.getInstance().getMeicamTimelineVideoFxTrackList();
        int trackSize = stickerCaptionTracks.size();
        if (!CommonUtils.isIndexAvailable(trackIndex, stickerCaptionTracks)) {
            LogUtils.e("getStickerCaptionData: trackIndex is illegal. trackIndex = " + trackIndex + "  trackSize = " + trackSize);
            return null;
        }
        MeicamTimelineVideoFxTrack stickerCaptionTrack = stickerCaptionTracks.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = stickerCaptionTrack.getClipInfoList();
        for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() == inPoint) {
                return clipInfo;
            }
        }
        return null;
    }

    public static ClipInfo getStickerCaptionData(int trackIndex, long inPoint) {
        List<MeicamStickerCaptionTrack> stickerCaptionTracks = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        if (CommonUtils.isEmpty(stickerCaptionTracks)) {
            Log.e(TAG, "getStickerCaptionData: trackSize is 0");
            return null;
        }
        int trackSize = stickerCaptionTracks.size();
        if (!CommonUtils.isIndexAvailable(trackIndex, stickerCaptionTracks)) {
            Log.e(TAG, "getStickerCaptionData: trackIndex is bigger than trackSize。 trackIndex = " + trackIndex + "  trackSize = " + trackSize);
            return null;
        }
        MeicamStickerCaptionTrack stickerCaptionTrack = stickerCaptionTracks.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = stickerCaptionTrack.getClipInfoList();
        for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() == inPoint) {
                return clipInfo;
            }
        }
        return null;
    }

    public static boolean removeStickerCaption(int trackIndex, long inPoint) {
        List<MeicamStickerCaptionTrack> stickerCaptionTracks = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        int trackSize = stickerCaptionTracks.size();
        if (trackSize <= trackIndex) {
            Log.e(TAG, "getStickerCaptionData: trackIndex is bigger than trackSize。 trackIndex = " + trackIndex + "  trackSize = " + trackSize);
            return false;
        }
        MeicamStickerCaptionTrack stickerCaptionTrack = stickerCaptionTracks.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = stickerCaptionTrack.getClipInfoList();
        for (int i = clipInfoList.size() - 1; i >= 0; i--) {
            ClipInfo clipInfo = clipInfoList.get(i);
            if (clipInfo.getInPoint() == inPoint) {
                clipInfoList.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 移除某一时间点的字幕
     * Remove caption from a point in time
     */
    public static void removeCaption(long inPoint) {
        List<MeicamStickerCaptionTrack> trackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        if (trackList != null) {
            for (MeicamStickerCaptionTrack track : trackList) {
                List clipList = track.getClipInfoList();
                for (int i = 0; i < clipList.size(); i++) {
                    Object o = clipList.get(i);
                    if (o instanceof MeicamCaptionClip) {
                        MeicamCaptionClip captionClip = (MeicamCaptionClip) o;
                        if (captionClip.getInPoint() == inPoint) {
                            clipList.remove(i);
                            i--;
                        }
                    }
                }
            }
        }
    }

    public static MeicamVideoTrack getMainTrack() {
        List<MeicamVideoTrack> mMeicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (mMeicamVideoTrackList.size() == 0) {
            mMeicamVideoTrackList.add(new MeicamVideoTrack(MAIN_TRACK_INDEX));
        }
        return mMeicamVideoTrackList.get(MAIN_TRACK_INDEX);
    }

    public static void addMainTransition(MeicamTransition meicamTransition) {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<MeicamTransition> transitionList = meicamVideoTrack.getTransitionInfoList();
        boolean hasTransition = false;
        for (int i = 0; i < transitionList.size(); i++) {
            MeicamTransition transition = transitionList.get(i);
            if (transition.getIndex() == meicamTransition.getIndex()) {
                hasTransition = true;
                transitionList.set(i, meicamTransition);
                break;
            }
        }
        if (!hasTransition) {
            transitionList.add(meicamTransition);
        }
    }

    public static void setMainTrackData(ArrayList<MeicamVideoClip> clipInfoArray) {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        clipInfoList.addAll(clipInfoArray);
        initClipInfos(clipInfoList);
    }

    /**
     * 增加素材
     * Add material
     * @param addClipInfoArray
     * @param index
     */
    public static void addMainTrackData(ArrayList<MeicamVideoClip> addClipInfoArray, int index) {
        if (CommonUtils.isEmpty(addClipInfoArray)) {
            return;
        }
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        if (index == 0) {
            initClipInfos(addClipInfoArray);
            MeicamVideoClip meicamVideoClip = addClipInfoArray.get(addClipInfoArray.size() - 1);
            long offset = meicamVideoClip.getOutPoint();
            for (int i = 0; i < clipInfoList.size(); i++) {
                ClipInfo clipInfo = clipInfoList.get(i);
                //将素材统一向后移动 Move the material uniformly backwards
                clipInfo.setInPoint(clipInfo.getInPoint() + offset);
                clipInfo.setOutPoint(clipInfo.getOutPoint() + offset);
            }
        } else {
            if (!CommonUtils.isIndexAvailable(index - 1, clipInfoList)) {
                LogUtils.e("clipInfoList.size===" + clipInfoList.size() + "index==" + (index - 1));
                return;
            }
            ClipInfo clipInfo = clipInfoList.get(index - 1);
            long outPoint = clipInfo.getOutPoint();
            initClipInfos(addClipInfoArray, outPoint);
            long offset = 0;
            for (int i = 0; i < addClipInfoArray.size(); i++) {
                MeicamVideoClip meicamVideoClip = addClipInfoArray.get(i);
                offset += meicamVideoClip.getOrgDuration();
            }
            for (int i = 0; i < clipInfoList.size(); i++) {
                if (i < index) {
                    continue;
                }
                ClipInfo oldClipInfo = clipInfoList.get(i);
                if (oldClipInfo == null) {
                    continue;
                }
                //将素材统一向后移动  Move the material uniformly backwards
                oldClipInfo.setInPoint(oldClipInfo.getInPoint() + offset);
                oldClipInfo.setOutPoint(oldClipInfo.getOutPoint() + offset);
            }
        }
        clipInfoList.addAll(index, addClipInfoArray);
    }


    public static MeicamTransition getMainTrackTransitionByIndex(int index) {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<MeicamTransition> transitionList = meicamVideoTrack.getTransitionInfoList();
        for (MeicamTransition meicamTransition : transitionList) {
            if (meicamTransition.getIndex() == index) {
                return meicamTransition;
            }
        }
        return null;
    }

    private static void initClipInfos(List<? extends ClipInfo<?>> clipInfoList) {
        for (int i = 0; i < clipInfoList.size(); i++) {
            ClipInfo clipInfo = clipInfoList.get(i);
            if (i == 0) {
                if (clipInfo instanceof MeicamVideoClip) {
                    clipInfo.setInPoint(0);
                    (clipInfo).setOutPoint(((MeicamVideoClip) clipInfo).getOrgDuration());
                }
            } else {
                ClipInfo lastClipInfo = clipInfoList.get(i - 1);
                if (clipInfo instanceof MeicamVideoClip) {
                    clipInfo.setInPoint(lastClipInfo.getOutPoint());
                    clipInfo.setOutPoint(clipInfo.getInPoint() + ((MeicamVideoClip) clipInfo).getOrgDuration());
                }
            }
            ((MeicamVideoClip) clipInfo).setTrimIn(0);
            ((MeicamVideoClip) clipInfo).setTrimOut(((MeicamVideoClip) clipInfo).getOrgDuration());
        }
    }


    private static void initClipInfos(List<? extends ClipInfo<?>> clipInfoList, long offset) {
        for (int i = 0; i < clipInfoList.size(); i++) {
            ClipInfo clipInfo = clipInfoList.get(i);
            if (i == 0) {
                if (clipInfo instanceof MeicamVideoClip) {
                    clipInfo.setInPoint(offset);
                    (clipInfo).setOutPoint(offset + ((MeicamVideoClip) clipInfo).getOrgDuration());
                }
            } else {
                ClipInfo lastClipInfo = clipInfoList.get(i - 1);
                if (clipInfo instanceof MeicamVideoClip) {
                    clipInfo.setInPoint(lastClipInfo.getOutPoint());
                    clipInfo.setOutPoint(clipInfo.getInPoint() + ((MeicamVideoClip) clipInfo).getOrgDuration());
                }
            }
            ((MeicamVideoClip) clipInfo).setTrimIn(0);
            ((MeicamVideoClip) clipInfo).setTrimOut(((MeicamVideoClip) clipInfo).getOrgDuration());
        }
    }


    public static void setMainTrackTransitionAll(MeicamTransition meicamTransition) {
        if (meicamTransition == null) {
            LogUtils.e("setMainTrackTransitionAll  meicamTransition==null");
            return;
        }
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<MeicamTransition> transitionList = meicamVideoTrack.getTransitionInfoList();
        NvsVideoTrack videoTrack = meicamVideoTrack.getObject();
        int clipCount = videoTrack.getClipCount();
        for (int i = 0; i < clipCount - 1; i++) {
            MeicamTransition newTransition = (MeicamTransition) meicamTransition.clone();
            newTransition.setIndex(i);
            if (i < transitionList.size()) {
                transitionList.set(i, newTransition);
            } else {
                transitionList.add(meicamTransition);
            }
            newTransition.bindToTimeline(meicamVideoTrack.getObject());
        }
    }


    public static void addStickerCaption(int trackIndex, ClipInfo clipInfo) {
        List<MeicamStickerCaptionTrack> stickerCaptionTracks = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        int trackSize = stickerCaptionTracks.size();
        if (trackIndex > trackSize) {
            Log.e(TAG, "getStickerCaptionData: trackIndex is bigger than trackSize。 trackIndex = " + trackIndex + "  trackSize = " + trackSize);
            return;
        }
        MeicamStickerCaptionTrack meicamStickerCaptionTrack = null;
        if (trackSize == trackIndex) {
            meicamStickerCaptionTrack = new MeicamStickerCaptionTrack(trackIndex);
            stickerCaptionTracks.add(meicamStickerCaptionTrack);
        } else {
            meicamStickerCaptionTrack = stickerCaptionTracks.get(trackIndex);
        }
        meicamStickerCaptionTrack.getClipInfoList().add(clipInfo);
    }

    public static List<ClipInfo<?>> getStickerCaption() {
        List<MeicamStickerCaptionTrack> stickerCaptionTracks = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        if (CommonUtils.isEmpty(stickerCaptionTracks)) {
            return null;
        }
        List<ClipInfo<?>> clipInfos = new ArrayList<>();
        for (int i = 0; i < stickerCaptionTracks.size(); i++) {
            MeicamStickerCaptionTrack meicamStickerCaptionTrack = stickerCaptionTracks.get(i);
            List clipInfoList = meicamStickerCaptionTrack.getClipInfoList();
            clipInfos.addAll(clipInfos.size(), clipInfoList);
        }
        return clipInfos;
    }

    public static void addVideoClip(int trackIndex, ClipInfo clipInfo) {
        List<MeicamVideoTrack> meicamVideoClips = TimelineData.getInstance().getMeicamVideoTrackList();
        int trackSize = meicamVideoClips.size();
        if (trackIndex > trackSize) {
            return;
        }
        MeicamVideoTrack meicamVideoTrack = null;
        if (trackSize == trackIndex) {
            meicamVideoTrack = new MeicamVideoTrack(trackIndex);
            meicamVideoClips.add(meicamVideoTrack);
        } else {
            meicamVideoTrack = meicamVideoClips.get(trackIndex);
        }
        meicamVideoTrack.getClipInfoList().add(clipInfo);
    }

    public static void removeVideoClip(int trackIndex, long inPoint) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        int trackSize = meicamVideoTrackList.size();
        if (trackIndex > trackSize) {
            Log.e(TAG, "removeVideoClip: trackIndex: " + trackIndex + "  trackSize: " + trackSize);
            return;
        }
        MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        int size = clipInfoList.size();
        for (int i = size - 1; i >= 0; i--) {
            ClipInfo clipInfo1 = clipInfoList.get(i);
            if (inPoint == clipInfo1.getInPoint()) {
                clipInfoList.remove(i);
                break;
            }
        }
    }

    public static int getPipClipTrackIndexByInPoint(long inPoint) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (CommonUtils.isEmpty(meicamVideoTrackList)) {
            return 0;
        }
        int trackSize = meicamVideoTrackList.size();
        for (int i = trackSize - 1; i >= 0; i--) {
            MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(i);
            List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
            int size = clipInfoList.size();
            for (int j = size - 1; j >= 0; j--) {
                ClipInfo clipInfo1 = clipInfoList.get(j);
                if (inPoint == clipInfo1.getInPoint()) {
                    return i;
                }
            }
        }
        return 0;
    }

    public static List<ClipInfo<?>> getStickerOrCaptionListByType(String type) {
        List<MeicamStickerCaptionTrack> stickerCaptionTracks = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        List<ClipInfo<?>> needClipList = new ArrayList<>();
        for (MeicamStickerCaptionTrack stickerCaptionTrack : stickerCaptionTracks) {
            List<ClipInfo<?>> clipInfoList = stickerCaptionTrack.getClipInfoList();
            for (int i = clipInfoList.size() - 1; i >= 0; i--) {
                ClipInfo clipInfo = clipInfoList.get(i);
                if (clipInfo.getType().equals(type)) {
                    needClipList.add(clipInfo);
                }
            }
        }
        return needClipList;
    }

    public static List<ClipInfo<?>> getAllPipClipList() {
        List<ClipInfo<?>> clipInfoList = new ArrayList<>();
        List<MeicamVideoTrack> videoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        for (int i = 1; i < videoTrackList.size(); i++) {
            MeicamVideoTrack videoTrack = videoTrackList.get(i);
            clipInfoList.addAll(videoTrack.getClipInfoList());
        }
        return clipInfoList;
    }

    public static List<ClipInfo<?>> getAddCaption() {
        List<ClipInfo<?>> clipInfoList = new ArrayList<>();
        List<MeicamVideoTrack> videoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        for (int i = 1; i < videoTrackList.size(); i++) {
            MeicamVideoTrack videoTrack = videoTrackList.get(i);
            clipInfoList.addAll(videoTrack.getClipInfoList());
        }
        return clipInfoList;
    }


    public static List<ClipInfo<?>> getAllAICaption() {
        List<ClipInfo<?>> clipInfoList = new ArrayList<>();
        List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        if (CommonUtils.isEmpty(meicamStickerCaptionTrackList)) {
            return null;
        }
        for (int i = 0; i < meicamStickerCaptionTrackList.size(); i++) {
            MeicamStickerCaptionTrack meicamStickerCaptionTrack = meicamStickerCaptionTrackList.get(i);
            if (meicamStickerCaptionTrack == null) {
                continue;
            }
            List<ClipInfo<?>> clipInfoList1 = meicamStickerCaptionTrack.getClipInfoList();
            if (CommonUtils.isEmpty(clipInfoList1)) {
                continue;
            }

            for (int j = 0; j < clipInfoList1.size(); j++) {
                ClipInfo<?> clipInfo = clipInfoList1.get(j);
                if (TimelineDataUtil.isAICaption(clipInfo)) {
                    clipInfoList.add(clipInfo);
                }
            }

        }
        return clipInfoList;
    }


    public static List<ClipInfo<?>> getAreaAllAICaption(long inPoint, long outPoint) {
        List<ClipInfo<?>> clipInfoList = new ArrayList<>();
        List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        if (CommonUtils.isEmpty(meicamStickerCaptionTrackList)) {
            return null;
        }
        for (int i = 0; i < meicamStickerCaptionTrackList.size(); i++) {
            MeicamStickerCaptionTrack meicamStickerCaptionTrack = meicamStickerCaptionTrackList.get(i);
            if (meicamStickerCaptionTrack == null) {
                continue;
            }
            List<ClipInfo<?>> clipInfoList1 = meicamStickerCaptionTrack.getClipInfoList();
            if (CommonUtils.isEmpty(clipInfoList1)) {
                continue;
            }

            for (int j = 0; j < clipInfoList1.size(); j++) {
                ClipInfo<?> clipInfo = clipInfoList1.get(j);
                if (clipInfo instanceof MeicamCaptionClip &&
                        ((MeicamCaptionClip) clipInfo).getOperationType() == BaseConstants.TYPE_AI_CAPTION &&
                        clipInfo.getInPoint() >= inPoint &&
                        clipInfo.getOutPoint() <= outPoint) {
                    clipInfoList.add(clipInfo);
                }
            }

        }
        return clipInfoList;
    }


    public static MeicamVideoClip findVideoClipByTrackAndInPoint(int trackIndex, long inPoint) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (CommonUtils.isEmpty(meicamVideoTrackList)) {
            LogUtils.e("clipInfo == null  ");
            return null;
        }
        if (!CommonUtils.isIndexAvailable(trackIndex, meicamVideoTrackList)) {
            LogUtils.e("findVideoClipByTrackAndInPoint: trackIndex is invalid");
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        Collections.sort(clipInfoList);
        LogUtils.e("findVideoClipByTrackAndInPoint: " + trackIndex + "  " + inPoint);
        for (int index = 0; index < clipInfoList.size(); index++) {
            ClipInfo clipInfo = clipInfoList.get(index);
            LogUtils.e("findVideoClipByTrackAndInPoint: " + clipInfo.getInPoint());
            if (clipInfo.getInPoint() == inPoint) {
                return (MeicamVideoClip) clipInfo;
            }
        }
        LogUtils.e("clipInfo == null  ");
        return null;
    }

    public static long getOtherTrackMaxDuration() {
        long max = 0;
        long maxPip = getPipTrackMaxDuration();
        max = Math.max(max, maxPip);
        long maxAudio = getAudioTrackMaxDuration();
        max = Math.max(max, maxAudio);
        long maxStickerAndCaption = getStickerAndCaptionTrackMaxDuration();
        max = Math.max(max, maxStickerAndCaption);
        return max;
    }

    public static long getPipTrackMaxDuration() {
        long max = 0;
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        for (int i = meicamVideoTrackList.size() - 1; i >= 1; i--) {
            MeicamVideoTrack videoTrack = meicamVideoTrackList.get(i);
            NvsVideoTrack nvsVideoTrack = videoTrack.getObject();
            if (nvsVideoTrack != null) {
                long trackDuration = nvsVideoTrack.getDuration();
                max = Math.max(trackDuration, max);
            }
        }
        return max;
    }

    public static long getAudioTrackMaxDuration() {
        long max = 0;
        List<MeicamAudioTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamAudioTrackList();

        for (int i = 0; i < meicamVideoTrackList.size(); i++) {
            MeicamAudioTrack videoTrack = meicamVideoTrackList.get(i);
            NvsAudioTrack nvsVideoTrack = videoTrack.getObject();
            if (nvsVideoTrack != null) {
                long trackDuration = nvsVideoTrack.getDuration();
                max = Math.max(trackDuration, max);
            }
        }
        return max;
    }

    public static long getStickerAndCaptionTrackMaxDuration() {
        long max = 0;
        List<MeicamStickerCaptionTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        for (MeicamStickerCaptionTrack meicamStickerCaptionTrack : meicamVideoTrackList) {
            List<ClipInfo<?>> clipInfoList = meicamStickerCaptionTrack.getClipInfoList();
            if (!clipInfoList.isEmpty()) {
                Collections.sort(clipInfoList);
                ClipInfo clipInfo = clipInfoList.get(clipInfoList.size() - 1);
                if (TimelineDataUtil.isAICaption(clipInfo)) {
                    continue;
                }
                max = Math.max(clipInfo.getOutPoint(), max);
            }
        }
        return max;
    }

    /**
     * is AI caption
     *
     * @param clipInfo
     * @return
     */
    public static boolean isAICaption(ClipInfo clipInfo) {
        if (clipInfo instanceof MeicamCaptionClip && ((MeicamCaptionClip) clipInfo).getOperationType() == BaseConstants.TYPE_AI_CAPTION) {
            return true;
        }
        return false;
    }

    public static void changeMainTrackVideoClipSpeed(int trackIndex, long inPoint, float newSpeed, boolean isChangeVoice) {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        MeicamVideoClip videoClip = findVideoClipByTrackAndInPoint(trackIndex, inPoint);
        if (videoClip == null || videoClip.getObject() == null) {
            return;
        }
        NvsVideoClip nvsVideoClip = videoClip.getObject();
        nvsVideoClip.changeSpeed(newSpeed, isChangeVoice);
        videoClip.setKeepAudioPitch(isChangeVoice);
        videoClip.setSpeed(newSpeed);
        videoClip.setOutPoint(nvsVideoClip.getOutPoint());
        videoClip.setCurveSpeed("");
        videoClip.setCurveSpeedName("");
        for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() > videoClip.getInPoint()) {
                NvsVideoClip nvsVideoClip1 = (NvsVideoClip) clipInfo.getObject();
                if (nvsVideoClip1 == null) {
                    continue;
                }
                clipInfo.setInPoint(nvsVideoClip1.getInPoint());
                clipInfo.setOutPoint(nvsVideoClip1.getOutPoint());
            }
        }
    }

    /**
     * videoclip 曲线变速
     * Change track video clip speed curve.
     *
     * @param trackIndex the track index
     * @param inPoint    the in point
     * @param newSpeed   the new speed
     * @param speedName  the speed name
     */
    public static void changeTrackVideoClipSpeedCurve(int trackIndex, long inPoint, String newSpeed, String speedName) {
        MeicamVideoTrack meicamVideoTrack = TimelineData.getInstance().getMeicamVideoTrackList().get(trackIndex);
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        MeicamVideoClip videoClip = findVideoClipByTrackAndInPoint(trackIndex, inPoint);
        if (videoClip == null) {
            return;
        }
        NvsVideoClip nvsVideoClip = videoClip.getObject();
        double originalDuration = (nvsVideoClip.getOutPoint() - nvsVideoClip.getInPoint()) / videoClip.getSpeed();
        boolean isSuccess = nvsVideoClip.changeCurvesVariableSpeed(newSpeed, true);
        LogUtils.d("曲线变速设置" + (isSuccess ? "成功" : "失败"));
        if (isSuccess) {
            videoClip.setSpeed((nvsVideoClip.getOutPoint() - nvsVideoClip.getInPoint()) / originalDuration);
        }
        videoClip.setOutPoint(nvsVideoClip.getOutPoint());
        videoClip.setCurveSpeed(newSpeed);
        videoClip.setCurveSpeedName(speedName);
        videoClip.setOutPoint(nvsVideoClip.getOutPoint());
        for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() > videoClip.getInPoint()) {
                NvsVideoClip nvsVideoClip1 = (NvsVideoClip) clipInfo.getObject();
                if (nvsVideoClip1 == null) {
                    continue;
                }
                clipInfo.setInPoint(nvsVideoClip1.getInPoint());
                clipInfo.setOutPoint(nvsVideoClip1.getOutPoint());
            }
        }
    }

    public static long getMainTrackDuration() {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        Collections.sort(clipInfoList);
        MeicamVideoClip videoClip = (MeicamVideoClip) clipInfoList.get(clipInfoList.size() - 1);
        return videoClip.getOutPoint();
    }

    public static MeicamVideoClip getMainTrackLastClip() {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        Collections.sort(clipInfoList);
        return (MeicamVideoClip) clipInfoList.get(clipInfoList.size() - 1);
    }


    public static void changePipTrackVideoClipSpeed(int trackIndex, long inPoint, float newSpeed, boolean isChangeVoice) {
        MeicamVideoTrack meicamVideoTrack = TimelineData.getInstance().getMeicamVideoTrackList().get(trackIndex);
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        MeicamVideoClip videoClip = findVideoClipByTrackAndInPoint(trackIndex, inPoint);
        if (videoClip == null) {
            return;
        }
        NvsVideoClip nvsVideoClip = videoClip.getObject();
        nvsVideoClip.changeSpeed(newSpeed, isChangeVoice);
        videoClip.setSpeed(newSpeed);
        videoClip.setOutPoint(nvsVideoClip.getOutPoint());
        videoClip.setKeepAudioPitch(isChangeVoice);
        for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() > videoClip.getInPoint()) {
                NvsVideoClip nvsVideoClip1 = (NvsVideoClip) clipInfo.getObject();
                if (nvsVideoClip1 == null) {
                    continue;
                }
                clipInfo.setInPoint(nvsVideoClip1.getInPoint());
                clipInfo.setOutPoint(nvsVideoClip1.getOutPoint());
            }
        }
    }

    public static MeicamTimelineVideoFxClip findTimelineEffectByTrackAndInPoint(int trackIndex, long inPoint) {
        List<MeicamTimelineVideoFxTrack> videoFxTrackList = TimelineData.getInstance().getMeicamTimelineVideoFxTrackList();
        if (CommonUtils.isEmpty(videoFxTrackList)) {
            return null;
        }
        if (!CommonUtils.isIndexAvailable(trackIndex, videoFxTrackList)) {
            LogUtils.e("findVideoClipByTrackAndInPoint: trackIndex is invalid");
            return null;
        }
        MeicamTimelineVideoFxTrack videoFxTrack = videoFxTrackList.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = videoFxTrack.getClipInfoList();
        for (int index = 0; index < clipInfoList.size(); index++) {
            ClipInfo clipInfo = clipInfoList.get(index);
            if (clipInfo.getInPoint() == inPoint) {
                return (MeicamTimelineVideoFxClip) clipInfo;
            }
        }
        return null;
    }

    /**
     * 新增音乐片段
     * New music clip
     * @param nvsAudioTrack
     * @param meicamAudioClip
     */
    public static void addAudioClipInfoByTrackIndex(NvsAudioTrack nvsAudioTrack, MeicamAudioClip meicamAudioClip) {
        List<MeicamAudioTrack> mMeicamAudioTrackList = TimelineData.getInstance().getMeicamAudioTrackList();
        int trackIndex = nvsAudioTrack.getIndex();
        if (mMeicamAudioTrackList != null) {
            MeicamAudioTrack meicamVideoTrack = null;
            if (trackIndex >= mMeicamAudioTrackList.size()) {
                meicamVideoTrack = new MeicamAudioTrack(trackIndex);
                meicamVideoTrack.setObject(nvsAudioTrack);
                mMeicamAudioTrackList.add(meicamVideoTrack);
            } else {
                meicamVideoTrack = mMeicamAudioTrackList.get(trackIndex);
                meicamVideoTrack.setObject(nvsAudioTrack);
            }
            if (meicamVideoTrack != null) {
                List<ClipInfo<?>> meicamVideoClipList = meicamVideoTrack.getClipInfoList();
                meicamVideoClipList.add(meicamAudioClip);
                Collections.sort(meicamVideoClipList);
            }
        }
    }


    /**
     * 查找音乐片段
     * Find music clip
     * @param trackIndex
     * @param inPoint
     * @return
     */
    public static MeicamAudioClip findAudioClipInfoByTrackAndInpoint(int trackIndex, long inPoint) {
        List<MeicamAudioTrack> mMeicamAudioTrackList = TimelineData.getInstance().getMeicamAudioTrackList();
        MeicamAudioClip meicamClipInfo = null;
        if (mMeicamAudioTrackList != null) {
            if (!CommonUtils.isIndexAvailable(trackIndex, mMeicamAudioTrackList)) {
                LogUtils.e("findAudioClipInfoByTrackAndInpoint: trackIndex is invalid");
                return null;
            }
            MeicamAudioTrack meicamVideoTrack = mMeicamAudioTrackList.get(trackIndex);
            if (meicamVideoTrack != null) {
                List<ClipInfo<?>> meicamVideoClipList = meicamVideoTrack.getClipInfoList();
                Collections.sort(meicamVideoClipList);
                for (ClipInfo clipInfo : meicamVideoClipList) {
                    if (clipInfo != null && clipInfo.getInPoint() == inPoint) {
                        return (MeicamAudioClip) clipInfo;
                    }
                }
            }
        }
        return meicamClipInfo;
    }

    /**
     * 刷新上层数据
     * Refresh upper-layer data
     * @param trackIndex
     * @return
     */
    public static void refreshMeicamAudioClipByTrackIndex(int trackIndex) {
        List<MeicamAudioTrack> meicamAudioTracks = TimelineData.getInstance().getMeicamAudioTrackList();
        if (CommonUtils.isEmpty(meicamAudioTracks)) {
            LogUtils.e("refreshMeicamAudioClipByTrackIndex: meicamAudioTracks is null");
            return;
        }
        if (!CommonUtils.isIndexAvailable(trackIndex, meicamAudioTracks)) {
            LogUtils.e("refreshMeicamAudioClipByTrackIndex: trackIndex is invalid");
        }
        MeicamAudioTrack meicamAudioTrack = meicamAudioTracks.get(trackIndex);
        for (int i = 0; i < meicamAudioTrack.getClipInfoList().size(); i++) {
            MeicamAudioClip meicamAudioClip = (MeicamAudioClip) meicamAudioTrack.getClipInfoList().get(i);
            meicamAudioClip.loadData(meicamAudioClip.getObject());
        }
    }

    public static List<ClipInfo<?>> getMainTrackVideoClip() {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (meicamVideoTrackList == null || meicamVideoTrackList.size() == 0) {
            LogUtils.e("getMainTrackVideoClip  meicamVideoTrackList==null");
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = null;
        if (meicamVideoTrackList != null && meicamVideoTrackList.size() > 0) {
            meicamVideoTrack = meicamVideoTrackList.get(BaseConstants.TRACK_INDEX_MAIN);
        }
        List<ClipInfo<?>> clipInfoList = null;
        if (meicamVideoTrack != null) {
            clipInfoList = meicamVideoTrack.getClipInfoList();
        }
        Collections.sort(clipInfoList);
        return clipInfoList;
    }

    /**
     * 获取包含区间的视频片段
     * get container area video clip
     *
     * @param inPoint
     * @param outPoint
     * @return
     */
    public static ClipInfo getMainTrackVideoClip(long inPoint, long outPoint) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (meicamVideoTrackList == null || meicamVideoTrackList.size() == 0) {
            LogUtils.e("getMainTrackVideoClip  meicamVideoTrackList==null");
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = null;
        if (meicamVideoTrackList != null && meicamVideoTrackList.size() > 0) {
            meicamVideoTrack = meicamVideoTrackList.get(BaseConstants.TRACK_INDEX_MAIN);
        }
        List<ClipInfo<?>> clipInfoList = null;
        if (meicamVideoTrack != null) {
            clipInfoList = meicamVideoTrack.getClipInfoList();
        }
        if (!CommonUtils.isEmpty(clipInfoList)) {
            for (int i = 0; i < clipInfoList.size(); i++) {
                ClipInfo<?> clipInfo = clipInfoList.get(i);
                if (clipInfo == null) {
                    continue;
                }
                long infoInPoint = clipInfo.getInPoint();
                long clipInfoOutPoint = clipInfo.getOutPoint();
                if ((inPoint >= infoInPoint) && (outPoint <= clipInfoOutPoint)) {
                    return clipInfo;
                }
            }
        }
        return null;
    }

    public static double getMainTrackVideoClipSpeedByIndex(int index) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        MeicamVideoTrack meicamVideoTrack = null;
        if (meicamVideoTrackList != null && meicamVideoTrackList.size() > 0) {
            meicamVideoTrack = meicamVideoTrackList.get(BaseConstants.TRACK_INDEX_MAIN);
        }
        List<ClipInfo<?>> clipInfoList = null;
        if (meicamVideoTrack != null) {
            clipInfoList = meicamVideoTrack.getClipInfoList();
        }
        Collections.sort(clipInfoList);
        if (index >= clipInfoList.size()) {
            return 1;
        }
        MeicamVideoClip meicamVideoClip = (MeicamVideoClip) clipInfoList.get(index);
        if (meicamVideoClip != null) {
            return meicamVideoClip.getSpeed();
        }
        return 1;
    }

    public static void setMeicamVideoClips(int trackIndex, List<? extends ClipInfo<?>> clipInfos) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        MeicamVideoTrack meicamVideoTrack = null;
        if (CommonUtils.isIndexAvailable(trackIndex, meicamVideoTrackList)) {
            meicamVideoTrack = meicamVideoTrackList.get(trackIndex);
        }
        meicamVideoTrack.setClipInfoList(clipInfos);
    }

    public static List<ClipInfo<?>> getPipTrackVideoClip() {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (CommonUtils.isEmpty(meicamVideoTrackList)) {
            return null;
        }
        if (meicamVideoTrackList.size() < BaseConstants.TRACK_INDEX_MAIN + 1) {
            return null;
        }
        List<ClipInfo<?>> allList = new ArrayList<>();
        for (int index = BaseConstants.TRACK_INDEX_MAIN + 1; index < meicamVideoTrackList.size(); index++) {
            MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(index);
            List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
            allList.addAll(clipInfoList);
        }
        return allList;
    }

    /**
     * 判断是否有人脸特效
     * Determine if there are facial effects
     * @return
     */
    public static boolean hasFaceEffet() {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (CommonUtils.isEmpty(meicamVideoTrackList)) {
            return false;
        }
        if (meicamVideoTrackList.size() == 0) {
            return false;
        }
        for (int index = BaseConstants.TRACK_INDEX_MAIN; index < meicamVideoTrackList.size(); index++) {
            MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(index);
            List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
            for (int i = 0; i < clipInfoList.size(); i++) {
                MeicamVideoClip clip = (MeicamVideoClip) clipInfoList.get(i);
                Map<String, Float> map = clip.getFaceEffectParameter();
                if (map != null && map.size() != 0) {
                    Set<Map.Entry<String, Float>> set = map.entrySet();
                    for (Map.Entry<String, Float> me : set) {
                        String key = me.getKey();
                        if (key.equals(VIDEO_FX_BEAUTY_STRENGTH) || key.equals(VIDEO_FX_BEAUTY_WHITENING) || key.equals(VIDEO_FX_BEAUTY_REDDENING)) {
                            continue;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void updateData(int trackIndex, String type) {
        TrackInfo trackInfo = getTrackInfo(trackIndex, type);
        if (trackInfo == null) {
            return;
        }
        List<ClipInfo<?>> clipInfoList = trackInfo.getClipInfoList();
        for (ClipInfo clipInfo : clipInfoList) {
            clipInfo.loadData(clipInfo.getObject());
        }
    }

    private static TrackInfo getTrackInfo(int trackIndex, String type) {
        List<TrackInfo> trackInfoList = new ArrayList<>();
        if (CommonData.CLIP_TIMELINE_FX.equals(type)) {
            List<MeicamTimelineVideoFxTrack> videoFxTrackList = TimelineData.getInstance().getMeicamTimelineVideoFxTrackList();
            trackInfoList.addAll(videoFxTrackList);
        } else if (CommonData.CLIP_STICKER.equals(type)
                || CommonData.CLIP_COMPOUND_CAPTION.equals(type)
                || CommonData.CLIP_CAPTION.equals(type)) {
            List<MeicamStickerCaptionTrack> stickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
            trackInfoList.addAll(stickerCaptionTrackList);
        } else if (CommonData.CLIP_VIDEO.equals(type) || CommonData.CLIP_IMAGE.equals(type)) {
            List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
            trackInfoList.addAll(meicamVideoTrackList);
            trackIndex += 1;
        } else if (CommonData.CLIP_AUDIO.equals(type)) {
            List<MeicamAudioTrack> meicamAudioTrackList = TimelineData.getInstance().getMeicamAudioTrackList();
            trackInfoList.addAll(meicamAudioTrackList);
        }
        if (CommonUtils.isEmpty(trackInfoList)) {
            return null;
        }
        if (!CommonUtils.isIndexAvailable(trackIndex, trackInfoList)) {
            LogUtils.e("findVideoClipByTrackAndInPoint: trackIndex is invalid");
            return null;
        }
        return trackInfoList.get(trackIndex);
    }

    public static List<ClipInfo<?>> getAllPipVideoClips() {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (meicamVideoTrackList == null || meicamVideoTrackList.size() == 0) {
            return null;
        }
        List<ClipInfo<?>> clipInfos = new ArrayList<>();
        for (int i = 0; i < meicamVideoTrackList.size(); i++) {
            MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(i);
            if (meicamVideoTrack == null) {
                continue;
            }
            if (meicamVideoTrack.getIndex() == MAIN_TRACK_INDEX) {
                continue;
            }
            if (clipInfos.size() == 0) {
                clipInfos.addAll(meicamVideoTrack.getClipInfoList());
            } else {
                clipInfos.addAll(clipInfos.size(), meicamVideoTrack.getClipInfoList());
            }
        }
        return clipInfos;
    }

    public static List<ClipInfo<?>> getVideoClipsInTrackIndex(int trackIndex) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (meicamVideoTrackList == null || meicamVideoTrackList.size() == 0) {
            return null;
        }
        if (meicamVideoTrackList.size() <= trackIndex) {
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(trackIndex);
        if (meicamVideoTrack == null) {
            return null;
        }
        return meicamVideoTrack.getClipInfoList();
    }

    public static NvsVideoTrack getNvsVideoTrack(int trackIndex) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (meicamVideoTrackList == null || meicamVideoTrackList.size() == 0) {
            return null;
        }
        if (meicamVideoTrackList.size() <= trackIndex) {
            return null;
        }

        MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(trackIndex);
        if (meicamVideoTrack == null) {
            return null;
        }
        return meicamVideoTrack.getObject();
    }

    public static void moveMainTrackClipsFromIndex(int index, long offset) {
        if (index == -1) {
            LogUtils.e("moveMainTrackClipsFromIndex index==-1");
            return;
        }
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        if (meicamVideoTrack == null) {
            return;
        }
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        if (CommonUtils.isEmpty(clipInfoList)) {
            return;
        }
        for (int i = index; i < clipInfoList.size(); i++) {
            ClipInfo clipInfo = clipInfoList.get(i);
            if (clipInfo == null) {
                continue;
            }
            clipInfo.setInPoint(clipInfo.getInPoint() + offset);
            clipInfo.setOutPoint(clipInfo.getOutPoint() + offset);
        }
    }

    public static int getAspectRatio() {
        NvsVideoResolution videoResolution = TimelineData.getInstance().getVideoResolution();
        float ratio = videoResolution.imageWidth * 1.0F / videoResolution.imageHeight;
        return CommonData.AspectRatio.getAspect(ratio);
    }


    /**
     * Gets template aspect ratio.
     * 获取模板近似比例
     *
     * @return the template aspect ratio
     */
    public static int getTemplateAspectRatio() {
        NvsVideoResolution videoResolution = TimelineData.getInstance().getVideoResolution();
        float ratio = videoResolution.imageWidth * 1.0F / videoResolution.imageHeight;
        return CommonData.AspectRatio.getTemplateAspect(ratio);
    }

    /**
     * 获取导出自定义高度
     * Gets the exported custom height
     * @param resolution 分辨率
     * @return
     */
    public static int getCustomHeight(int resolution, int ratio) {
        int height = 0;
        if (ratio == 0) {
            NvsVideoResolution videoResolution = TimelineData.getInstance().getVideoResolution();
            int widthImage = videoResolution.imageWidth;
            int heightImage = videoResolution.imageHeight;
            height = widthImage > heightImage ? resolution : Math.round(resolution * 1.0F / widthImage * heightImage);
            return height;
        }
        if (ratio == BaseInfo.AspectRatio_16v9) { // 16:9
            height = resolution;
        } else if (ratio == BaseInfo.AspectRatio_1v1) { //1:1
            height = resolution;
        } else if (ratio == BaseInfo.AspectRatio_9v16) { //9:16
            height = (int) (resolution / 9.0 * 16);
        } else if (ratio == BaseInfo.AspectRatio_3v4) { // 3:4
            height = (int) (resolution / 3.0 * 4.0);
        } else if (ratio == BaseInfo.AspectRatio_4v3) { //4:3ExportTemplateSettingActivity
            height = resolution;
        } else {
            NvsVideoResolution videoResolution = TimelineData.getInstance().getVideoResolution();
            height = videoResolution.imageWidth > videoResolution.imageHeight ? 720 : 1280;
        }
        return height;
    }


    public void getAudioCount(NvsTimeline nvsTimeline) {
        nvsTimeline.audioTrackCount();

    }

    /**
     * 设置主题静音
     * Mute the topic
     */
    public static void setThemeQuiet(NvsTimeline nvsTimeline) {
        MeicamTheme meicamTheme = TimelineData.getInstance().getMeicamTheme();
        if (meicamTheme != null) {
            String themePackageId = meicamTheme.getThemePackageId();
            if (!TextUtils.isEmpty(themePackageId)) {
                nvsTimeline.setThemeMusicVolumeGain(0f, 0f);
            }
        }
    }

    /**
     * 设置主题静音
     * Mute the topic
     */
    public static void restoreThemeVolume(NvsTimeline nvsTimeline) {
        MeicamTheme meicamTheme = TimelineData.getInstance().getMeicamTheme();
        if (meicamTheme != null) {
            String themePackageId = meicamTheme.getThemePackageId();
            if (!TextUtils.isEmpty(themePackageId)) {
                nvsTimeline.setThemeMusicVolumeGain(1.0f, 1.0f);
            }
        }
    }


    public static ClipInfo getVideoClip(int trackIndex, long inPoint) {
        List<MeicamVideoTrack> meicamVideoTracks = TimelineData.getInstance().getMeicamVideoTrackList();
        int trackSize = meicamVideoTracks.size();
        if (trackSize <= trackIndex) {
            Log.e(TAG, "getVideoClip: trackIndex is bigger than trackSize。 trackIndex = " + trackIndex + "  trackSize = " + trackSize + "  clipInfo == null");
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = meicamVideoTracks.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() == inPoint) {
                return clipInfo;
            }
        }
        Log.e(TAG, "  clipInfo == null");
        return null;
    }


    public static ClipInfo getVideoClip(int trackIndex, int index) {
        List<MeicamVideoTrack> meicamVideoTracks = TimelineData.getInstance().getMeicamVideoTrackList();
        int trackSize = meicamVideoTracks.size();
        if (trackSize <= trackIndex) {
            Log.e(TAG, "getVideoClip: trackIndex is bigger than trackSize。 trackIndex = " + trackIndex + "  trackSize = " + trackSize + "  clipInfo == null");
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = meicamVideoTracks.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        if (CommonUtils.isEmpty(clipInfoList)) {
            return null;
        }
        return clipInfoList.get(index);
    }


    public static List<ClipInfo<?>> getVideoClips(int trackIndex, long inPoint) {
        List<MeicamVideoTrack> meicamVideoTracks = TimelineData.getInstance().getMeicamVideoTrackList();
        int trackSize = meicamVideoTracks.size();
        if (trackSize <= trackIndex) {
            Log.e(TAG, "getVideoClip: trackIndex is bigger than trackSize。 trackIndex = " + trackIndex + "  trackSize = " + trackSize + "  clipInfo == null");
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = meicamVideoTracks.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        List<ClipInfo<?>> newClipInfoLis = new ArrayList<>();
        if (CommonUtils.isEmpty(clipInfoList)) {
            return null;
        }
        for (int i = 0; i < clipInfoList.size(); i++) {
            ClipInfo<?> clipInfo = clipInfoList.get(i);
            if (clipInfo == null) {
                continue;
            }
            if (clipInfo.getInPoint() >= inPoint) {
                newClipInfoLis.add(clipInfo);
            }
        }
        return newClipInfoLis;
    }

    public static List<ClipInfo<?>> getVideoClip(int trackIndex) {
        List<MeicamVideoTrack> meicamVideoTracks = TimelineData.getInstance().getMeicamVideoTrackList();
        int trackSize = meicamVideoTracks.size();
        if (trackSize <= trackIndex) {
            Log.e(TAG, "getVideoClip: trackIndex is bigger than trackSize。 trackIndex = " + trackIndex + "  trackSize = " + trackSize);
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = meicamVideoTracks.get(trackIndex);
        return meicamVideoTrack.getClipInfoList();
    }

    public static void refreshTransitionsAfterSplit(int clipIndex) {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<MeicamTransition> transitionInfoList = meicamVideoTrack.getTransitionInfoList();
        if (transitionInfoList.isEmpty()) {
            return;
        }
        for (int i = 0; i < transitionInfoList.size(); i++) {
            MeicamTransition transition = transitionInfoList.get(i);
            if (transition.getIndex() >= clipIndex) {
                transition.setIndex(transition.getIndex() + 1);
            }
        }
    }

    public static void refreshTransitionsAfterDelete(int clipIndex) {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<MeicamTransition> transitionInfoList = meicamVideoTrack.getTransitionInfoList();
        if (transitionInfoList.isEmpty()) {
            return;
        }
        if (CommonUtils.isIndexAvailable(clipIndex, transitionInfoList)) {
            transitionInfoList.remove(clipIndex);
        }
        for (int i = 0; i < transitionInfoList.size(); i++) {
            MeicamTransition transition = transitionInfoList.get(i);
            if (transition.getIndex() >= clipIndex) {
                transition.setIndex(transition.getIndex() - 1);
            }
        }
    }

    /**
     * 刷新所有子view，主轨裁剪后变化
     * Refresh all subviews, main track after cropping changes
     * @param selectInpoint
     * @param dx
     */
    public static void changeAllBaseItemInpoint(long selectInpoint, long dx) {
        LogUtils.e("dx===" + dx);
        List<ClipInfo<?>> clipInfoPip = getPipTrackVideoClip();
        List<ClipInfo<?>> clipInfoStick = getStickerCaption();
        for (ClipInfo infoPip : clipInfoPip) {
            long inPoint = infoPip.getInPoint();
            long outPoint = infoPip.getOutPoint();
            if (inPoint < selectInpoint) {
                continue;
            }
            inPoint = inPoint - dx;
            outPoint = outPoint - dx;
            infoPip.setInPoint(inPoint);
            infoPip.setOutPoint(outPoint);
            NvsVideoClip nvsVideoClip = (NvsVideoClip) infoPip.getObject();
            nvsVideoClip.changeTrimInPoint(inPoint, false);
            nvsVideoClip.changeTrimOutPoint(outPoint, false);
        }
    }

    /**
     * 检查转场信息 以上层数据为主。
     * 删除removeClip会添加默认的淡入淡出转场，在这里以上层数据为准，删除多余的转场
     * Check the transition information of the upper layer data.
     * Removing removeClip will add the default fade in and fade out transition, where the upper layer data prevails, removing the excess transition
     */
    public static void verifyTransition() {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<MeicamTransition> transitionInfoList = meicamVideoTrack.getTransitionInfoList();
        NvsVideoTrack videoTrack = meicamVideoTrack.getObject();
        int clipCount = videoTrack.getClipCount();
        if (clipCount <= 1) {
            return;
        }
        for (int i = 0; i < clipCount; i++) {
            NvsVideoTransition nvsVideoTransition = videoTrack.getTransitionBySourceClipIndex(i);
            if (nvsVideoTransition != null) {
                MeicamTransition meicamTransition = new MeicamTransition(i, CommonData.TYPE_PACKAGE, "");
                if (!transitionInfoList.contains(meicamTransition)) {
                    videoTrack.setBuiltinTransition(i, "");
                }
            }
        }
    }

    public static MeicamVideoFx findPropertyFx(MeicamVideoClip videoClip) {
        if (videoClip == null) {
            return null;
        }
        List<MeicamVideoFx> list = videoClip.getVideoFxs();
        for (MeicamVideoFx fx : list) {
            if (NvsConstants.PROPERTY_FX.equals(fx.desc)) {
                return fx;
            }
        }
        return null;
    }

    /**
     * 重置属性动画
     * Reset property animation.
     *
     * @param videoClip 资源片段
     */
    public static void resetPropertyAnimation(MeicamVideoClip videoClip) {
        MeicamVideoFx meicamVideoFx = findPropertyFx(videoClip);
        if (meicamVideoFx != null) {
            meicamVideoFx.setStringVal(NvsConstants.POST_PACKAGE_ID, "");
            meicamVideoFx.setBooleanVal(NvsConstants.IS_POST_STORY_BOARD_3D, false);
            meicamVideoFx.setFloatVal(NvsConstants.PACKAGE_EFFECT_IN, 0);
            meicamVideoFx.setFloatVal(NvsConstants.PACKAGE_EFFECT_OUT, 0);
        }
    }

    public static void LogTransitions() {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<MeicamTransition> transitionInfoList = meicamVideoTrack.getTransitionInfoList();
        for (MeicamTransition transition : transitionInfoList) {
            Log.e(TAG, "LogTransitions: getIndex: " + transition.getIndex()
                    + " getType: " + transition.getType() + "  " + transition.getDesc());
        }
        Log.e(TAG, "LogTransitions: -------------------");
        NvsVideoTrack videoTrack = meicamVideoTrack.getObject();
        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            NvsVideoTransition nvsVideoTransition = videoTrack.getTransitionBySourceClipIndex(i);
            if (nvsVideoTransition != null) {
                Log.e(TAG, "LogTransitions: index: " + i + "  " + nvsVideoTransition.getBuiltinVideoTransitionName() + "  " + nvsVideoTransition.getVideoTransitionPackageId() + "  " + nvsVideoTransition.getVideoTransitionDuration());
            }
        }
    }

    public static void LogMainTrack() {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        Collections.sort(clipInfoList);
        for (ClipInfo clipInfo : clipInfoList) {
            MeicamVideoClip videoClip = (MeicamVideoClip) clipInfo;
            Log.e(TAG, "LogMainTrack: getInPoint: " + videoClip.getInPoint() + "  getOutPoint: " + videoClip.getOutPoint() + "  getTrimIn: " + videoClip.getTrimIn()
                    + "  getTrimOut: " + videoClip.getTrimOut() + "  getSpeed: " + videoClip.getSpeed());
            NvsVideoClip videoClip1 = videoClip.getObject();
            Log.e(TAG, "LogMainTrack: getInPoint: " + videoClip1.getInPoint() + "  getOutPoint: " + videoClip1.getOutPoint() + "  getTrimIn: " + videoClip1.getTrimIn()
                    + "  getTrimOut: " + videoClip1.getTrimOut() + "  getSpeed: " + videoClip1.getSpeed());
            Log.e(TAG, "=========================================");
        }
    }

    public static ClipInfo getClipInfoByCaptionInPoint(long inPoint) {
        MeicamVideoTrack meicamVideoTrack = getMainTrack();
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        if (clipInfoList == null) {
            return null;
        }
        for (int i = 0; i < clipInfoList.size(); i++) {
            ClipInfo<?> clipInfo = clipInfoList.get(i);
            if (clipInfo == null) {
                continue;
            }
            long clipInPoint = clipInfo.getInPoint();
            long clipOutPoint = clipInfo.getOutPoint();
            if (inPoint >= clipInPoint && inPoint < clipOutPoint) {
                return clipInfo;
            }
        }
        return null;
    }

    public static void LogPipTrack() {
        List<MeicamVideoTrack> mMeicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        for (int i = 1; i < mMeicamVideoTrackList.size(); i++) {
            MeicamVideoTrack meicamVideoTrack = mMeicamVideoTrackList.get(i);
            List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
            Collections.sort(clipInfoList);
            for (ClipInfo clipInfo : clipInfoList) {
                MeicamVideoClip videoClip = (MeicamVideoClip) clipInfo;
                Log.e(TAG, "LogPipTrack: getInPoint: " + videoClip.getInPoint() + "  getOutPoint: " + videoClip.getOutPoint() + "  getTrimIn: " + videoClip.getTrimIn()
                        + "  getTrimOut: " + videoClip.getTrimOut() + "  getSpeed: " + videoClip.getSpeed());
                NvsVideoClip videoClip1 = videoClip.getObject();
                if (videoClip1 != null) {
                    Log.e(TAG, "LogPipTrack: getInPoint: " + videoClip1.getInPoint() + "  getOutPoint: " + videoClip1.getOutPoint() + "  getTrimIn: " + videoClip1.getTrimIn()
                            + "  getTrimOut: " + videoClip1.getTrimOut() + "  getSpeed: " + videoClip1.getSpeed() + " " + videoClip1.toString());
                }
                Log.e(TAG, "=========================================");
            }
        }

    }

    public static void LogStickerAndCaption() {
        List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        for (int i = 0; i < meicamStickerCaptionTrackList.size(); i++) {
            MeicamStickerCaptionTrack meicamStickerCaptionTrack = meicamStickerCaptionTrackList.get(i);
            List<ClipInfo<?>> clipInfoList = meicamStickerCaptionTrack.getClipInfoList();
            for (int j = 0; j < clipInfoList.size(); j++) {
                ClipInfo clipInfo = clipInfoList.get(j);
                if (clipInfo instanceof MeicamStickerClip) {
                    MeicamStickerClip meicamStickerClip = (MeicamStickerClip) clipInfo;
                    Log.e(TAG, "LogStickerAndCaption:贴纸: trackIndex: " + i + " getInPoint: " + meicamStickerClip.getInPoint() + "  getOutPoint: " + meicamStickerClip.getOutPoint() + " getzValue: " + meicamStickerClip.getzValue());
                    NvsTimelineAnimatedSticker animatedSticker = meicamStickerClip.getObject();
                    Log.e(TAG, "LogStickerAndCaption:贴纸: trackIndex: " + i + " getInPoint: " + animatedSticker.getInPoint() + "  getOutPoint: " + animatedSticker.getOutPoint() + " getzValue: " + animatedSticker.getZValue());
                    Log.e(TAG, "=========================================");
                } else if (clipInfo instanceof MeicamCaptionClip) {
                    MeicamCaptionClip meicamCaptionClip = (MeicamCaptionClip) clipInfo;
                    Log.e(TAG, "LogStickerAndCaption:字幕: trackIndex: " + i + " getInPoint: " + meicamCaptionClip.getInPoint() + "  getOutPoint: " + meicamCaptionClip.getOutPoint() + " getzValue: " + meicamCaptionClip.getzValue());
                    NvsTimelineCaption animatedSticker = meicamCaptionClip.getObject();
                    Log.e(TAG, "LogStickerAndCaption:字幕: trackIndex: " + i + " getInPoint: " + animatedSticker.getInPoint() + "  getOutPoint: " + animatedSticker.getOutPoint() + " getzValue: " + animatedSticker.getZValue());
                    Log.e(TAG, "=========================================");

                } else if (clipInfo instanceof MeicamCompoundCaptionClip) {
                    MeicamCompoundCaptionClip meicamCompoundCaptionClip = (MeicamCompoundCaptionClip) clipInfo;
                    Log.e(TAG, "LogStickerAndCaption:组合字幕: trackIndex: " + i + " getInPoint: " + meicamCompoundCaptionClip.getInPoint() + "  getOutPoint: " + meicamCompoundCaptionClip.getOutPoint() + " getzValue: " + meicamCompoundCaptionClip.getzValue());
                    NvsTimelineCompoundCaption animatedSticker = meicamCompoundCaptionClip.getObject();
                    Log.e(TAG, "LogStickerAndCaption:组合字幕: trackIndex: " + i + " getInPoint: " + animatedSticker.getInPoint() + "  getOutPoint: " + animatedSticker.getOutPoint() + " getzValue: " + animatedSticker.getZValue());
                    Log.e(TAG, "=========================================");
                }
            }
        }
    }
}
