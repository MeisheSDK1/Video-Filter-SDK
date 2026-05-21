package com.meishe.sdkdemo.mimodemo.common.template.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.meishe.sdkdemo.mimodemo.common.Constants;
import com.meishe.sdkdemo.mimodemo.common.template.model.MiMoExtraDataInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.MiMoInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.RepeatInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.ShotDataInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.ShotInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.ShotVideoInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.SpeedInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.SubTrackFilterInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.TempJsonInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.TrackClipInfo;
import com.meishe.sdkdemo.mimodemo.common.utils.MimoPathUtils;
import com.meishe.sdkdemo.mimodemo.common.utils.ParseJsonFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MiMoFileUtils {
    public static final String PATH_ASSETS = "assets:/";
    private static final String ASSETS_RECORD_PATH = "mystory/record.json";
    private static final String SUFFIX_JSON_FILE = ".json";
    private static final String PATH_SEPARATOR = "/";
    private static final String FILE_JSON_INFO = "/info.json";
    private static final String PATH_TEMPLATE = "mimo/template";

    /**
     * 表示5毫秒
     * 5 milliseconds
     */
    public static final float DIFF_TIME_VALUE = 5f;

    public static String getTemplateAssetsFilePath(String templateDir, String fileName, boolean isBuildIn) {
        StringBuilder filePath = new StringBuilder();
        if (isBuildIn) {
            filePath.append(PATH_ASSETS).append(templateDir).append(PATH_SEPARATOR).append(fileName);
        } else {
            filePath.append(templateDir).append(PATH_SEPARATOR).append(fileName);
        }
        return filePath.toString();
    }

    public static String getTemplateFilePath(String templateDir, String fileName) {
        StringBuilder filePath = new StringBuilder();
        filePath.append(templateDir).append(PATH_SEPARATOR).append(fileName);
        return filePath.toString();
    }

    /**
     * 从mystory/record.json文件中获取模板文件中的json.info路径
     * Get the json.info path in the template file from the mystory/record.json file
     *
     * @param context
     * @return
     */
    public static List<MiMoInfo> getTemplateListFromRecord(Context context) {
        String jsonInfoFile = ParseJsonFile.readAssetJsonFile(context, ASSETS_RECORD_PATH);
        if (jsonInfoFile == null) {
            return null;
        }
        TempJsonInfo recordInfo = ParseJsonFile.fromJson(jsonInfoFile, TempJsonInfo.class);
        if (recordInfo == null) {
            return null;
        }
        List<TempJsonInfo.JsonInfo> jsonList = recordInfo.getJsonList();
        if (jsonList == null || jsonList.isEmpty()) {
            return null;
        }
        List<MiMoInfo> miMoInfoList = new ArrayList<>();
        for (TempJsonInfo.JsonInfo jsonInfo : jsonList) {
            if (jsonInfo == null) {
                continue;
            }
            String jsonPath = jsonInfo.jsonPath;
            String jsonFile = ParseJsonFile.readAssetJsonFile(context, jsonPath + FILE_JSON_INFO);
            if (jsonFile == null) {
                continue;
            }
            MiMoInfo miMoInfo = ParseJsonFile.fromJson(jsonFile, MiMoInfo.class);
            if (miMoInfo == null) {
                continue;
            }
            //设置时间线扫换默认素材源路径
            //Set the default material source path for time line scanning
            String timeLineTransSource = miMoInfo.getTimeLineTransSource();
            if (!TextUtils.isEmpty(timeLineTransSource)) {
                String transSourcePath = getTemplateAssetsFilePath(jsonPath, timeLineTransSource, true);
                miMoInfo.setTransSourcePath(transSourcePath);
            }
            updateShotClipInfos(miMoInfo);
            MiMoExtraDataInfo extraDataInfo = new MiMoExtraDataInfo();
            extraDataInfo.setTemplateDirectory(jsonPath);
            String previewVideoPath = getTemplateAssetsFilePath(jsonPath, miMoInfo.getPreview(), true);
            extraDataInfo.setPreviewVideoPath(previewVideoPath);
            String coverFilePath = "file:///android_asset/" + MiMoFileUtils.getTemplateFilePath(jsonPath, miMoInfo.getCover());
            extraDataInfo.setCoverFilePath(coverFilePath);
            String musicFilePath = getTemplateAssetsFilePath(jsonPath, miMoInfo.getMusic(), true);
            extraDataInfo.setMusicFilePath(musicFilePath);
            miMoInfo.setExtraDataInfo(extraDataInfo);
            miMoInfoList.add(miMoInfo);
        }
        return miMoInfoList;
    }

    /**
     * 获取手机上的模板
     * Get the template on the phone
     *
     * @return
     */
    public static List<MiMoInfo> getTemplateListFromSdCard() {
        String mimoTemplateDir = MimoPathUtils.getMimoTemolateDirectory();
        if (TextUtils.isEmpty(mimoTemplateDir)) {
            return null;
        }
        File file = new File(mimoTemplateDir);
        String[] list = file.list();
        if (list == null || list.length <= 0) {
            return null;
        }
        String parentPath = file.getAbsolutePath();
        List<MiMoInfo> miMoInfoList = new ArrayList<>();
        for (int index = 0; index < list.length; index++) {
            String childDirectory = parentPath + PATH_SEPARATOR + list[index];
            File childFolder = new File(childDirectory);
            String[] childlist = childFolder.list();
            if (childlist == null || childlist.length <= 0) {
                continue;
            }
            for (int i = 0; i < childlist.length; i++) {
                String childFileName = childlist[i];
                if (childFileName.endsWith(SUFFIX_JSON_FILE)) {
                    String jsonFile = ParseJsonFile.readSdCardJsonFile(childDirectory + PATH_SEPARATOR + childFileName);
                    if (TextUtils.isEmpty(jsonFile)) {
                        continue;
                    }
                    MiMoInfo miMoInfo = ParseJsonFile.fromJson(jsonFile, MiMoInfo.class);
                    if (miMoInfo == null) {
                        continue;
                    }

                    //设置时间线扫换默认素材源路径
                    //Set the default material source path for time line scanning
                    String timeLineTransSource = miMoInfo.getTimeLineTransSource();
                    if (!TextUtils.isEmpty(timeLineTransSource)) {
                        String transSourcePath = getTemplateAssetsFilePath(childDirectory, timeLineTransSource, false);
                        miMoInfo.setTransSourcePath(transSourcePath);
                    }
                    updateShotClipInfos(miMoInfo);

                    MiMoExtraDataInfo extraDataInfo = new MiMoExtraDataInfo();
                    extraDataInfo.setTemplateDirectory(childDirectory);
                    String previewVideoPath = getTemplateAssetsFilePath(childDirectory, miMoInfo.getPreview(), false);
                    extraDataInfo.setPreviewVideoPath(previewVideoPath);
                    String coverFilePath = MiMoFileUtils.getTemplateFilePath(childDirectory, miMoInfo.getCover());
                    extraDataInfo.setCoverFilePath(coverFilePath);
                    String musicFilePath = getTemplateAssetsFilePath(childDirectory, miMoInfo.getMusic(), false);
                    extraDataInfo.setMusicFilePath(musicFilePath);
                    miMoInfo.setExtraDataInfo(extraDataInfo);
                    miMoInfoList.add(miMoInfo);
                    break;
                }
            }
        }

        return miMoInfoList;
    }


    /**
     * 处理速度start值与上一个速度end不衔接的问题
     * Handle the problem that the speed start value does not correspond to the previous speed end value
     *
     * @param shotInfo
     */
    private static void handleSpeedInfoDisconnect(ShotInfo shotInfo) {
        if (shotInfo == null) {
            return;
        }
        List<SpeedInfo> speedInfos = shotInfo.getSpeed();
        if (speedInfos == null || speedInfos.isEmpty()) {
            return;
        }
        List<SpeedInfo> insertSpeedInfos = new ArrayList<>();
        List<Integer> insertIndexList = new ArrayList<>();
        int speedCpunt = speedInfos.size();
        //记录前一个speedInfo 的end值
        //Record the end value of the previous speedInfo
        float prevSpeedEnd = 0f;
        for (int index = 0; index < speedCpunt; index++) {
            SpeedInfo speedInfo = speedInfos.get(index);
            if (speedInfo == null) {
                continue;
            }
            float start = speedInfo.getStart();
            if (index > 0 && prevSpeedEnd > 0f && !isEqualSpeedStartWithEnd(prevSpeedEnd, start)) {
                SpeedInfo newSpeedInfo = new SpeedInfo(prevSpeedEnd, start, 1f, 1f);
                insertSpeedInfos.add(newSpeedInfo);
                insertIndexList.add(index);
            }
            prevSpeedEnd = speedInfo.getEnd();
        }
        int insertCount = insertSpeedInfos.size();
        if (!insertSpeedInfos.isEmpty()) {
            for (int insertIndex = 0; insertIndex < insertCount; insertIndex++) {
                SpeedInfo newSpeedInfo = insertSpeedInfos.get(insertIndex);
                int insertPosition = insertIndexList.get(insertIndex);
                speedInfos.add(insertPosition, newSpeedInfo);
            }
        }
    }

    /**
     * 判断上一个速度信息的end值与下一个速度信息的start值是否相同,返回true,表示衔接，false则相反
     * Check whether the end value of the previous speed information is the same as the start value of the next speed information. Return true, indicating cohesion; false is the opposite
     *
     * @param end
     * @param start
     * @return
     */
    private static boolean isEqualSpeedStartWithEnd(float end, float start) {
        float timeDiff = start - end;
        if (timeDiff >= -DIFF_TIME_VALUE && timeDiff <= DIFF_TIME_VALUE) {
            return true;
        }
        return false;
    }

    private static void updateShotClipInfos(MiMoInfo miMoInfo) {
        if (miMoInfo == null) {
            return;
        }
        List<ShotInfo> shotInfos = miMoInfo.getShotInfos();
        if (shotInfos == null || shotInfos.isEmpty()) {
            return;
        }
        boolean isTimelineTrans = miMoInfo.isTimelineTrans();
        String transSourcePath = miMoInfo.getTransSourcePath();
        //存储当前镜头需要的所有视频片段信息，trackTotalClipInfos.size()表示当前镜头需要的源视频个数
        //trackTotalClipInfos.size() represents the number of source videos required by the current lens
        List<ShotDataInfo> totalShotDataInfos = new ArrayList<>();
        for (ShotInfo shotInfo : shotInfos) {
            if (shotInfo == null) {
                continue;
            }
            String mainTrackFilter = shotInfo.getMainTrackFilter();
            handleSpeedInfoDisconnect(shotInfo);
            ShotDataInfo shotDataInfo = new ShotDataInfo();
            ShotVideoInfo shotVideoInfo = getRepeatClipInfos(shotInfo, mainTrackFilter, 0);
            if (shotVideoInfo != null && !shotVideoInfo.getTrackClipInfos().isEmpty()) {
                //作反复动作 repeat
                shotDataInfo.setMainTrackVideoInfo(shotVideoInfo);
            } else {
                //没有反复动作 No repetition
                ShotVideoInfo mainTrackVideoInfo = appendTrackClipInfos(shotInfo, mainTrackFilter, 0);
                if (mainTrackVideoInfo != null && !mainTrackVideoInfo.getTrackClipInfos().isEmpty()) {
                    shotDataInfo.setMainTrackVideoInfo(mainTrackVideoInfo);
                }
            }

            //子轨道视频列表 Subtrack video list
            List<SubTrackFilterInfo> subTrackFilter = shotInfo.getSubTrackFilter();
            if (subTrackFilter != null && !subTrackFilter.isEmpty()) {
                int subTrackCount = subTrackFilter.size();
                List<ShotVideoInfo> subTrackVideoInfos = new ArrayList<>();
                for (int subTrackIndex = 0; subTrackIndex < subTrackCount; subTrackIndex++) {
                    SubTrackFilterInfo subTrackFilterInfo = subTrackFilter.get(subTrackIndex);
                    if (subTrackFilterInfo == null) {
                        continue;
                    }
                    String subTrackFilterName = subTrackFilterInfo.getFilterName();
                    ShotVideoInfo subTrackVideoInfo = appendTrackClipInfos(shotInfo, subTrackFilterName, subTrackIndex + 1);
                    if (subTrackVideoInfo != null && !subTrackVideoInfo.getTrackClipInfos().isEmpty()) {
                        subTrackVideoInfos.add(subTrackVideoInfo);
                    }
                }
                shotDataInfo.setSubTrackVideoInfos(subTrackVideoInfos);
            } else {
                //没有子轨道，如果是时间线扫换模板，需要添加上默认扫换素材源路径
                //There is no subtrack. If it is a timeline scan template, you need to add the default scan material source path
                if (isTimelineTrans && !TextUtils.isEmpty(transSourcePath)) {
                    ShotVideoInfo subTrackVideoInfo = appendTrackClipInfos(shotInfo, null, 1);
                    subTrackVideoInfo.setVideoClipPath(transSourcePath);
                    List<ShotVideoInfo> subTrackVideoInfos = new ArrayList<>();
                    subTrackVideoInfos.add(subTrackVideoInfo);
                    shotDataInfo.setSubTrackVideoInfos(subTrackVideoInfos);
                }
            }
            totalShotDataInfos.add(shotDataInfo);
        }
        miMoInfo.setShotDataInfos(totalShotDataInfos);
    }

    private static ShotVideoInfo getRepeatClipInfos(ShotInfo shotInfo, String trackFilter, int trackIndex) {
        List<RepeatInfo> repeatInfos = shotInfo.getRepeat();
        if (repeatInfos == null || repeatInfos.isEmpty()) {
            return null;
        }

        int shot = shotInfo.getShot();
        String filter = shotInfo.getFilter();
        String transition = shotInfo.getTrans();
        List<TrackClipInfo> videoClipInfos = new ArrayList<>();
        int repeatCount = repeatInfos.size();

        //处理反复，添加反复片段
        // Handle repetition, add repetition fragments
        long totalRealNeedDuration = 0;
        long trimIn = 0;//源视频裁剪入点 Source video cropping entry point
        float prevEnd = 0f;////前一个反复动作的end值 The end value of the previous repeated action
        for (int repeatIndex = 0; repeatIndex < repeatCount; repeatIndex++) {
            RepeatInfo repeatInfo = repeatInfos.get(repeatIndex);
            if (repeatInfo == null) {
                continue;
            }
            float start = repeatInfo.getStart();
            //当前反复操作跟上一个反复操作之间的时间间距
            //The time interval between the current iteration and the previous iteration
            long startTimeInterval = millisecondToMicrosecond(start - prevEnd);
            long originDuration = millisecondToMicrosecond(repeatInfo.getOriginDuration());
            prevEnd = repeatInfo.getEnd();
            //反复操作重复的次数 The number of times an operation is repeated
            int repeat = repeatInfo.getCount();
            for (int index = 0; index < repeat; index++) {
                //正放视频片段 We're playing video clips now
                long clipRealDuration = originDuration + startTimeInterval;
                TrackClipInfo videoClipInfo = new TrackClipInfo(
                        trackIndex, 1f, 1f, null, filter, trackFilter,
                        false, 1, trimIn, clipRealDuration);
                //倒放视频片段 Play the video clip backwards
                TrackClipInfo oppositeVideoClipInfo = new TrackClipInfo(
                        trackIndex, 1f, 1f, null, filter, trackFilter,
                        true, 2, 0, originDuration);
                videoClipInfos.add(videoClipInfo);
                videoClipInfos.add(oppositeVideoClipInfo);
            }
            //第二个正放视频的裁剪入点 The second is the clip entry point of the video
            long secondClipTrimIn = trimIn + startTimeInterval;
            //正放视频片段 We're playing video clips now
            TrackClipInfo secondVideoClipInfo = new TrackClipInfo(
                    0, 1f, 1f, transition, filter, trackFilter,
                    false, 3, secondClipTrimIn, originDuration);
            videoClipInfos.add(secondVideoClipInfo);
            trimIn += (startTimeInterval + originDuration);
            totalRealNeedDuration = trimIn;
        }

        //根据speedInfo查找片段信息，设置片段速度
        //Find the fragment information according to the speedInfo and set the fragment speed
        List<SpeedInfo> speedInfos = shotInfo.getSpeed();
        if (speedInfos != null && !speedInfos.isEmpty()) {
            int speedCount = speedInfos.size();
            int clipInfoCount = videoClipInfos.size();
            long clipTrimIn = 0, clipTrimOut = 0;
            for (int clipIndex = 0; clipIndex < clipInfoCount; clipIndex++) {
                TrackClipInfo clipInfo = videoClipInfos.get(clipIndex);
                if (clipInfo == null) {
                    continue;
                }
                long clipNeedDuration = clipInfo.getRealNeedDuration();
                clipTrimOut += clipNeedDuration;
                long speedTrimIn = 0, speedTrimOut = 0;
                for (int speedIndex = 0; speedIndex < speedCount; speedIndex++) {
                    SpeedInfo speedInfo = speedInfos.get(speedIndex);
                    if (speedInfo == null) {
                        continue;
                    }
                    //计算所需要的原始视频的时长 Calculate the length of the original video required
                    float speed0 = speedInfo.getSpeed0();
                    float speed1 = speedInfo.getSpeed1();
                    float diffDuration = speedInfo.getEnd() - speedInfo.getStart();
                    float speedValue = (speed0 + speed1) / 2;
                    long needDuration = millisecondToMicrosecond(diffDuration * speedValue);
                    speedTrimOut += needDuration;
                    if (clipTrimIn >= speedTrimIn && clipTrimOut <= speedTrimOut) {
                        //设置速度值 Set speed value
                        clipInfo.setSpeed0(speed0);
                        clipInfo.setSpeed1(speed1);
                    }
                    speedTrimIn += needDuration;
                }
                clipTrimIn += clipNeedDuration;
            }
        }
        long durationBySpeed = millisecondToMicrosecond(shotInfo.getDuration());
        long duration = (long) (shotInfo.getDuration() * 1000);
        ShotVideoInfo shotVideoInfo = new ShotVideoInfo(videoClipInfos,
                totalRealNeedDuration, durationBySpeed, shot, trackIndex, true,
                shotInfo.getFilter(), duration);
        return shotVideoInfo;
    }

    private static ShotVideoInfo appendTrackClipInfos(ShotInfo shotInfo, String trackFilter, int trackIndex) {
        if (shotInfo == null) {
            return null;
        }
        int shot = shotInfo.getShot();
        String filter = shotInfo.getFilter();
        String transition = shotInfo.getTrans();
        boolean isReverse = shotInfo.isReverse();
        List<TrackClipInfo> videoClipInfos = new ArrayList<>();
        List<TrackClipInfo> trackClipInfoBySpeed = getVideoClipInfosBySpeed(shotInfo.getSpeed(),
                trackIndex,
                transition,
                filter,
                trackFilter,
                isReverse);
        if (trackClipInfoBySpeed != null && !trackClipInfoBySpeed.isEmpty()) {
            videoClipInfos.addAll(trackClipInfoBySpeed);
        } else {
            //没有变速列表 No variable speed list
            long realNeedDuration = millisecondToMicrosecond(shotInfo.getDuration());
            TrackClipInfo videoClipInfo = new TrackClipInfo(
                    trackIndex, 1f, 1f,
                    transition, filter, trackFilter, isReverse,
                    0, 0, realNeedDuration);
            videoClipInfos.add(videoClipInfo);
        }
        long realNeedDuration = 0;
        int clipCount = videoClipInfos.size();
        //计算实际需要的视频视频时长 Calculate the required video duration
        for (int clipIndex = 0; clipIndex < clipCount; clipIndex++) {
            TrackClipInfo clipInfo = videoClipInfos.get(clipIndex);
            if (clipInfo == null) {
                continue;
            }
            realNeedDuration += clipInfo.getRealNeedDuration();
        }

        long durationBySpeed = millisecondToMicrosecond(shotInfo.getDuration());
        ShotVideoInfo shotVideoInfo = new ShotVideoInfo(videoClipInfos,
                realNeedDuration, durationBySpeed, shot, trackIndex, isReverse,
                shotInfo.getFilter(), (long) (shotInfo.getDuration() * 1000l));
        return shotVideoInfo;
    }

    /**
     * 获取变速的视频片段信息
     * Get video footage of the speed change
     */
    private static List<TrackClipInfo> getVideoClipInfosBySpeed(List<SpeedInfo> speedInfos,
                                                                int trachIndex,
                                                                String trans,
                                                                String filter,
                                                                String trackFilter,
                                                                boolean isReverse) {
        if (speedInfos == null || speedInfos.isEmpty()) {
            return null;
        }
        long videoTrimIn = 0;
        int speedCount = speedInfos.size();
        List<TrackClipInfo> shotClipInfos = new ArrayList<>();
        for (int speedIndex = 0; speedIndex < speedCount; speedIndex++) {
            SpeedInfo speedInfo = speedInfos.get(speedIndex);
            if (speedInfo == null) {
                continue;
            }
            //计算所需要的原始视频的时长 Calculate the length of the original video required
            float diffDuration = speedInfo.getEnd() - speedInfo.getStart();
            float speedValue = (speedInfo.getSpeed0() + speedInfo.getSpeed1()) / 2;
            long realNeedDuration = millisecondToMicrosecond(diffDuration * speedValue);
            TrackClipInfo videoClipInfo = new TrackClipInfo(
                    trachIndex, speedInfo.getSpeed0(), speedInfo.getSpeed1(),
                    null, filter, trackFilter, isReverse,
                    0, videoTrimIn, realNeedDuration);
            if (speedIndex == speedCount - 1) {
                videoClipInfo.setTrans(trans);
            }
            shotClipInfos.add(videoClipInfo);
            videoTrimIn += realNeedDuration;
        }
        return shotClipInfos;
    }

    /**
     * 毫秒转微秒
     * Milliseconds to microseconds
     */
    public static long millisecondToMicrosecond(float millisecond) {
        return (long) (millisecond * Constants.BASE_MILLISECOND);
    }

    private static String getFontNameFromAssets(Context context, String path) {
        AssetManager assets = context.getAssets();
        try {
            String[] list = assets.list(path);
            if (list == null) {
                return null;
            }
            for (int index = 0; index < list.length; index++) {
                if (list[index].endsWith("ttf")) {
                    return list[index];
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

    private static String getFontNameFromSdCard(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        String[] list = file.list();
        if (list == null) {
            return null;
        }
        for (int index = 0; index < list.length; index++) {
            if (list[index].endsWith("ttf")) {
                return list[index];
            }
        }
        return null;
    }

    public static String getFontPath(String folderPath, String fontName, boolean isBuildInTemp) {
        return getTemplateAssetsFilePath(folderPath, fontName, isBuildInTemp);
    }

}
