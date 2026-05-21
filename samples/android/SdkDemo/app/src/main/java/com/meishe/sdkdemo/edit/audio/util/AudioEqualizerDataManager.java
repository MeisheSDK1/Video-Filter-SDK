package com.meishe.sdkdemo.edit.audio.util;

import android.content.Context;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.bean.Equalizer;
import com.meishe.sdkdemo.bean.EqualizerType;
import com.meishe.sdkdemo.edit.data.AudioEqualizerItem;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2021/6/25 14:18
 * @des : 构建 音频均衡器数据
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class AudioEqualizerDataManager {
    public static Map<String, List<AudioEqualizerItem>> buildAudioEqualizerData(Context context) {
        Map<String, List<AudioEqualizerItem>> audioEqualizerMap = new HashMap<>();
        String AUDIO_EQUALIZER_ADJUST_LOW = context.getResources().getString(R.string.audio_equalizer_low);
        String AUDIO_EQUALIZER_ADJUST_MEDIUM = context.getResources().getString(R.string.audio_equalizer_medium);
        String AUDIO_EQUALIZER_ADJUST_MEDIUM_HIGH = context.getResources().getString(R.string.audio_equalizer_medium_high);
        String AUDIO_EQUALIZER_ADJUST_HIGH = context.getResources().getString(R.string.audio_equalizer_high);
        List<AudioEqualizerItem> lowAudioEqualizerItems = new ArrayList<>();
        int pos = 1;
        lowAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "31", 0, 20, -20));
        lowAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "40", 20, 20, -20));
        lowAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "50", 10, 20, -20));
        lowAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "63", 20, 20, -20));
        lowAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "80", 10, 20, -20));
        lowAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "100", 0, 20, -20));
        lowAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "125", 0, 20, -20));
        audioEqualizerMap.put(AUDIO_EQUALIZER_ADJUST_LOW, lowAudioEqualizerItems);

        List<AudioEqualizerItem> mediumAudioEqualizerItems = new ArrayList<>();
        mediumAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "160", 0, 20, -20));
        mediumAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "200", 20, 20, -20));
        mediumAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "250", 10, 20, -20));
        mediumAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "315", 20, 20, -20));
        mediumAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "400", 10, 20, -20));
        mediumAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "500", 0, 20, -20));
        audioEqualizerMap.put(AUDIO_EQUALIZER_ADJUST_MEDIUM, mediumAudioEqualizerItems);

        List<AudioEqualizerItem> mediumHighAudioEqualizerItems = new ArrayList<>();
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "630", 0, 20, -20));
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "800", 20, 20, -20));
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "1000", 10, 20, -20));
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "1250", 20, 20, -20));
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "1600", 10, 20, -20));
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "2000", 0, 20, -20));
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "2500", 0, 20, -20));
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "3200", 0, 20, -20));
        mediumHighAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "4000", 0, 20, -20));
        audioEqualizerMap.put(AUDIO_EQUALIZER_ADJUST_MEDIUM_HIGH, mediumHighAudioEqualizerItems);


        List<AudioEqualizerItem> highAudioEqualizerItems = new ArrayList<>();
        highAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "5000", 0, 20, -20));
        highAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "6300", 0, 20, -20));
        highAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "8000", 0, 20, -20));
        highAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "10K", 0, 20, -20));
        highAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "12.5K", 0, 20, -20));
        highAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "16K", 0, 20, -20));
        highAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "20K", 0, 20, -20));
        highAudioEqualizerItems.add(new AudioEqualizerItem((pos++) + " Band Gain", "25K", 0, 20, -20));
        audioEqualizerMap.put(AUDIO_EQUALIZER_ADJUST_HIGH, highAudioEqualizerItems);
        return audioEqualizerMap;
    }

    /**
     * 从json文件中读取配置好的预设参数类型列表和值的集合
     * @param context 上下文
     * @param filePath json文件路径
     * @return
     */
    public static List<EqualizerType> buildPresetFromJson(Context context,String filePath) {
        Equalizer equalizer = ParseJsonFile.fromJson(ParseJsonFile.readAssetJsonFile(context, filePath), Equalizer.class);
        String[] equalizerPresetType = context.getResources().getStringArray(R.array.equalizer_preset_type);
        List<EqualizerType>equalizerTypeList =equalizer.getList();
        if(null != equalizerTypeList && equalizerTypeList.size()>0){
            for (int i = 0; i < equalizerTypeList.size(); i++) {
                EqualizerType type = equalizerTypeList.get(i);
                type.setName(equalizerPresetType[i]);
                List<AudioEqualizerItem> valueList = type.getValueList();
                if(null != valueList && valueList.size()>0){
                    for(AudioEqualizerItem item : valueList){
                        item.setMaxVoice(20);
                        item.setMinVoice(-20);
                    }
                }
            }
        }
        return equalizerTypeList;
    }

}
