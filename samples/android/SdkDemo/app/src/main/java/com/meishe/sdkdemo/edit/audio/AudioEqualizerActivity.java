package com.meishe.sdkdemo.edit.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAudioFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.view.CustomPopWindow;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.bean.EqualizerType;
import com.meishe.sdkdemo.edit.CompileVideoFragment;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.audio.adapter.AudioEqualizerTypeAdapter;
import com.meishe.sdkdemo.edit.audio.util.AudioEqualizerDataManager;
import com.meishe.sdkdemo.edit.audio.view.AudioEqualizerAdjustItemView;
import com.meishe.sdkdemo.edit.audio.view.AudioEqualizerAdjustView;
import com.meishe.sdkdemo.edit.data.AudioEqualizerItem;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.BitmapData;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : jml
 * @CreateDate : 2021/6/24.
 * @Description :音频均衡器 Activity
 * @Description :AudioEqualizerActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AudioEqualizerActivity extends BaseActivity {
    private static final String TAG = "AudioEqualizerActivity";

    private VideoFragment mVideoFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private CustomTitleBar mTitleBar;
    private LinearLayout mBottomLayout;
    private RelativeLayout rlLevel;
    private CompileVideoFragment mCompileVideoFragment;
    private RelativeLayout mCompilePage;
    private TextView preSetText;
    private AudioEqualizerAdjustView audioEqualizerAdjustView;
    private Map<String, List<AudioEqualizerItem>> stringListMap;
    private List<EqualizerType> equalizerTypeList;
    private View audioLevel;
    private String audioLevelString;
    private CustomPopWindow customPopWindow;
    private AudioEqualizerTypeAdapter mTypeAdapter;
    private int selectedPos = 0;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_audio_equalizer;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        mBottomLayout = findViewById(R.id.bottomLayout);
        mCompilePage = (RelativeLayout) findViewById(R.id.compilePage);
        audioEqualizerAdjustView = findViewById(R.id.audio_adjust);
        preSetText = findViewById(R.id.equalizerType);
        audioLevel = findViewById(R.id.audio_level);
        rlLevel = findViewById(R.id.audio_item_layout);
        audioLevel.findViewById(R.id.audio_low).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // low低频  Low frequency
                resetLevel();
                audioLevel.findViewById(R.id.audio_low).setBackgroundResource(R.drawable.audio_equalizer_select_shape);
                audioLevelString = getResources().getString(R.string.audio_equalizer_low);
                audioEqualizerAdjustView.setAudioEqualizerList(stringListMap.get(audioLevelString));
            }
        });
        audioLevel.findViewById(R.id.audio_media).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // media中频  Media IF
                resetLevel();
                audioLevel.findViewById(R.id.audio_media).setBackgroundResource(R.drawable.audio_equalizer_select_shape);
                audioLevelString = getResources().getString(R.string.audio_equalizer_medium);
                audioEqualizerAdjustView.setAudioEqualizerList(stringListMap.get(audioLevelString));

            }
        });
        audioLevel.findViewById(R.id.audio_media_high).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 中高频  Medium and high frequency
                resetLevel();
                audioLevel.findViewById(R.id.audio_media_high).setBackgroundResource(R.drawable.audio_equalizer_select_shape);
                audioLevelString = getResources().getString(R.string.audio_equalizer_medium_high);
                audioEqualizerAdjustView.setAudioEqualizerList(stringListMap.get(audioLevelString));
            }
        });
        audioLevel.findViewById(R.id.audio_high).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 高频  high frequency
                resetLevel();
                audioLevel.findViewById(R.id.audio_high).setBackgroundResource(R.drawable.audio_equalizer_select_shape);
                audioLevelString = getResources().getString(R.string.audio_equalizer_high);
                audioEqualizerAdjustView.setAudioEqualizerList(stringListMap.get(audioLevelString));
            }
        });
        preSetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectPreSetWindow();
            }
        });
    }

    private void resetLevel() {
        audioLevel.findViewById(R.id.audio_low).setBackgroundResource(R.color.ff414141);
        audioLevel.findViewById(R.id.audio_media).setBackgroundResource(R.color.ff414141);
        audioLevel.findViewById(R.id.audio_media_high).setBackgroundResource(R.color.ff414141);
        audioLevel.findViewById(R.id.audio_high).setBackgroundResource(R.color.ff414141);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.audio_equalizer);
        mTitleBar.setTextRight(R.string.compile);
        mTitleBar.setTextRightVisible(View.VISIBLE);
    }

    @Override
    protected void initData() {
        initTimeline();
        initVideoFragment();
        initCompileVideoFragment();
        buildAudioEqualizerData();
        initEqualizerPreSetData();

    }


    private void initTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null)
            return;
        NvsVideoTrack mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null)
            return;
        int clipCount = mVideoTrack.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip clip = mVideoTrack.getClipByIndex(i);
        }

    }

    /**
     * 构建30段调频数据 和显示数据
     * Build 30-band FM data and display data
     */
    private void buildAudioEqualizerData() {
        audioLevelString = getResources().getString(R.string.audio_equalizer_low);
        stringListMap = AudioEqualizerDataManager.buildAudioEqualizerData(this);
        refreshStreamAudioEqualizerData(stringListMap);
        audioEqualizerAdjustView.setAudioEqualizerList(stringListMap.get(audioLevelString));
        audioEqualizerAdjustView.setOnItemProgressChangeListener(new AudioEqualizerAdjustItemView.onItemProgressChangeListener() {
            @Override
            public void onItemProgressChange(String audioKey, int audioValue) {
                Log.w(TAG, "audioKey:" + audioKey + " audioValue:" + audioValue);
                //设置生效  Setting takes effect
                setAudioEqualizer(audioKey, audioValue);
                //调节的如果不是自定义或者30段调频，切换至自定义，也就是说默认预设的数据不调节。
                //If the adjustment is not customized or 30-band FM, switch to customized, that is, the default preset data is not adjusted.
                //默认先去获取30段调节的数据
                //By default, the data of 30-segment adjustment is obtained first
                if(selectedPos == equalizerTypeList.size()-1){
                    List<AudioEqualizerItem>audioEqualizerItems = stringListMap.get(audioLevelString);
                    saveAudioEqualizerData(audioEqualizerItems,audioKey, audioValue);
                }else{
                    //如果不是自定义的话去调节自定义
                    //Adjust customization if not
                    if(selectedPos!=0){
                        //调节后的数据赋值给自定义类型
                        //Assign the adjusted data to the user-defined type
                        List<AudioEqualizerItem> currentTypeValues=  equalizerTypeList.get(selectedPos).getValueList();
                        List<AudioEqualizerItem>designSelfValues = equalizerTypeList.get(0).getValueList();
                        copyValue(currentTypeValues,designSelfValues);
                        EqualizerType type = equalizerTypeList.get(0);
                        type.setValueList(designSelfValues);
                        saveAudioEqualizerData(designSelfValues,audioKey, audioValue);
//                        selectedPos = 0;
                        //更新显示选中的类型为自定义
                        //Update to show that the selected type is custom
//                        updateSelectType(type,selectedPos,false);
                    }else{
                        //是自定义直接调节不需要另外操作
                        //It is a user-defined direct adjustment without additional operation
                        List<AudioEqualizerItem>audioEqualizerItems = equalizerTypeList.get(selectedPos).getValueList();
                        saveAudioEqualizerData(audioEqualizerItems,audioKey, audioValue);
                    }
                }

            }
        });
    }

    /**
     * 第一个集合中的item 的 value copy给第二个item中对应的值
     * The value copy of the item in the first set is given to the corresponding value in the second item
     * @param currentTypeValues
     * @param designSelfValues
     */
    private void copyValue(List<AudioEqualizerItem> currentTypeValues, List<AudioEqualizerItem> designSelfValues) {
        if(null == currentTypeValues || null == designSelfValues){
            return;
        }
        if(currentTypeValues.size() != designSelfValues.size()){
            return;
        }
        for(int i = 0; i< currentTypeValues.size() ; i++){
            designSelfValues.get(i).setValue(currentTypeValues.get(i).getValue());
        }
    }


    /**
     * 从json文件中初始化预设的分类列表
     * Initialize the preset classification list from the json file
     */
    private void initEqualizerPreSetData() {
        String filePath = "audio/info.json";
        equalizerTypeList = AudioEqualizerDataManager.buildPresetFromJson(this,filePath);
        if(null != equalizerTypeList && equalizerTypeList.size()>0){
            EqualizerType equalizerType = equalizerTypeList.get(0);
            updateSelectType(equalizerType,selectedPos,false);
        }

    }
    private void refreshStreamAudioEqualizerData(Map<String, List<AudioEqualizerItem>> stringListMap) {
        if (stringListMap != null) {
            Set<String> keys = stringListMap.keySet();
            for (String key : keys) {
                List<AudioEqualizerItem> items = stringListMap.get(key);
                if (items != null) {
                    for (AudioEqualizerItem item : items) {
                        refreshAudioEqualizer(item);
                    }
                }
            }
        }
    }

    /**
     * 刷新指定频率的音频值
     *Refresh the audio value at the specified frequency
     * @param item 正在调节的单项
     */
    private void refreshAudioEqualizer(AudioEqualizerItem item) {
        if (item == null || mTimeline == null) return;
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) return;
        int clipCount = videoTrack.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if(videoClip==null)continue;
            NvsAudioFx audioFx = null;
            int fxCount = videoClip.getAudioFxCount();
            for (int j = 0; j < fxCount; j++) {
                NvsAudioFx fxByIndex = videoClip.getAudioFxByIndex(j);
                if (fxByIndex != null && TextUtils.equals(fxByIndex.getBuiltinAudioFxName(), Constants.AUDIO_EQ)) {
                    audioFx = fxByIndex;
                }
            }
            if (audioFx == null) {
                audioFx = videoClip.appendAudioFx(Constants.AUDIO_EQ);
            }
            if (audioFx != null) {
                int intVal = (int) audioFx.getFloatVal(item.getEffectKey());
                item.setValue(intVal);
                Log.d(TAG, "item key:" + item.getEffectKey() + "voiceValue:" + intVal);
            }
        }

    }

    /**
     * 刷新指定频率的音频值
     *Refresh the audio value at the specified frequency
     * @param audioKey 调节的单项的key
     * @param audioValue  调节的单项的value
     */
    private void setAudioEqualizer(String audioKey, int audioValue) {
        if (TextUtils.isEmpty(audioKey) || mTimeline == null) return;
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) return;
        int clipCount = videoTrack.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (videoClip == null) return;
            NvsAudioFx audioFx = null;
            int fxCount = videoClip.getAudioFxCount();
            for (int j = 0; j < fxCount; j++) {
                NvsAudioFx fxByIndex = videoClip.getAudioFxByIndex(j);
                if (fxByIndex != null && TextUtils.equals(fxByIndex.getBuiltinAudioFxName(), Constants.AUDIO_EQ)) {
                    audioFx = fxByIndex;
                }
            }
            if (audioFx == null) {
                audioFx = videoClip.appendAudioFx(Constants.AUDIO_EQ);
            }
            if (audioFx != null) {
                if (audioFx.getFloatVal(audioKey) != audioValue) {
                    Log.d(TAG, "setAudioFx key:\"" + audioKey + "\" value:" + audioValue);
                    audioFx.setFloatVal(audioKey, audioValue);
                }
            }
        }
    }

    public void saveAudioEqualizerData(List<AudioEqualizerItem>mAudioEqualizerList,String audioKey, int audioValue) {
        if(null == mAudioEqualizerList || mAudioEqualizerList.size() <= 0){
            return;
        }
        for (AudioEqualizerItem item : mAudioEqualizerList) {
            if (TextUtils.equals(audioKey, item.getEffectKey())) {
                item.setValue(audioValue);
            }
        }
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }


    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setAutoPlay(true);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", NvAsset.AspectRatio_9v16);
        bundle.putBoolean("playBarVisible", true);
        bundle.putBoolean("voiceButtonVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                mVideoFragment.playVideo(0, -1);
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {

            }

            @Override
            public void streamingEngineStateChanged(int state) {

            }
        });
    }

    private void initCompileVideoFragment() {
        mCompileVideoFragment = new CompileVideoFragment();
        mCompileVideoFragment.setTimeline(mTimeline);
        getFragmentManager().beginTransaction()
                .add(R.id.compilePage, mCompileVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mCompileVideoFragment);
    }

    private void clearData() {
        TimelineData.instance().clear();
        BackupData.instance().clear();
        BitmapData.instance().clear();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    @Override
    protected void initListener() {


        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                removeTimeline();
                clearData();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                mCompilePage.setVisibility(View.VISIBLE);
                mCompileVideoFragment.compileVideo();
            }
        });

        mCompilePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mCompileVideoFragment.setCompileVideoListener(new CompileVideoFragment.OnCompileVideoListener() {
            @Override
            public void compileProgress(NvsTimeline timeline, int progress) {
            }

            @Override
            public void compileFinished(NvsTimeline timeline) {
                mCompilePage.setVisibility(View.GONE);
            }

            @Override
            public void compileFailed(NvsTimeline timeline) {
                mCompilePage.setVisibility(View.GONE);
            }

            @Override
            public void compileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                mCompilePage.setVisibility(View.GONE);
            }

            @Override
            public void compileVideoCancel() {
                mCompilePage.setVisibility(View.GONE);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeTimeline();
        clearData();
        AppManager.getInstance().finishActivity();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 选择预设分类的列表
     * Select a list of preset categories
     */
    private void showSelectPreSetWindow() {
        if(null == customPopWindow){

            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.layout_equalizer_preset_type_dialog,null);
            customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                    .setView(view)
                    .size(ScreenUtils.dip2px(this,100),ScreenUtils.dip2px(this,250))
                    .create();
            RecyclerView rvType = view.findViewById(R.id.rv_type);
            rvType.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            mTypeAdapter  = new AudioEqualizerTypeAdapter(this);
            rvType.setAdapter(mTypeAdapter);
            mTypeAdapter.setData(equalizerTypeList);
            mTypeAdapter.setOnItemSelectedListener((type, position) -> {
                customPopWindow.dissmiss();
                if(selectedPos == position){
                    return;
                }

                selectedPos = position;
                updateSelectType(type,selectedPos,true);
            });
            mTypeAdapter.setSelectedPos(selectedPos);
        }

        customPopWindow.showAsDropDown(preSetText,0,5);
    }

    /**
     * 根据选择的类型 更新显示内容 最后一个是30段  第一个是自定义
     * Update the displayed content according to the selected type. The last one is 30 paragraphs. The first one is custom
     * @param type item
     * @param selectedPos pos
     * @param needEffect 设置生效
     *
     */
    private void updateSelectType(EqualizerType type,int selectedPos,boolean needEffect) {
        if(null == type){
            return;
        }
        preSetText.setText(isZh(this)?type.getZh_name():type.getName());
        //选择的是全部调节 30段均衡器
        //The choice is to adjust the 30-segment equalizer completely
        if(selectedPos == equalizerTypeList.size()-1){
            rlLevel.setVisibility(View.VISIBLE);
            //根据当前选择正在调解的等级显示内容
            //Display content according to the level currently selected for mediation
            audioEqualizerAdjustView.setAudioEqualizerList(stringListMap.get(audioLevelString));
            if(needEffect){
                effectSelectEqualizerType(stringListMap);
            }
        }else{
            rlLevel.setVisibility(View.INVISIBLE);
            //显示对应类型的数据
            //Display data of corresponding type
            List<AudioEqualizerItem>itemList = type.getValueList();
            audioEqualizerAdjustView.setAudioEqualizerList(itemList);
            if(needEffect) {
                effectSelectEqualizerType(itemList);
            }
        }
        if(null != mTypeAdapter){
            mTypeAdapter.setSelectedPos(selectedPos);
        }
    }

    /**
     * 设置生效
     * Setting takes effect
     * @param stringListMap 设置特技，如果是30段调节，入参是map
     */
    private void effectSelectEqualizerType(Map<String, List<AudioEqualizerItem>> stringListMap){

        if(null != stringListMap && stringListMap.size()>0){
            for(String item: stringListMap.keySet()){
                List<AudioEqualizerItem>itemList = stringListMap.get(item);
                effectSelectEqualizerType(itemList);
            }
        }
    }
    /**
     * 设置生效
     * Setting takes effect
     * @param audioEqualizerItems 设置特技，如果不是30段调节，入参是list
     */
    private void effectSelectEqualizerType(List<AudioEqualizerItem>audioEqualizerItems){

        if(null != audioEqualizerItems && audioEqualizerItems.size()>0){
            for(AudioEqualizerItem item: audioEqualizerItems){
                setAudioEqualizer(item.getEffectKey(),item.getValue());
            }
        }
    }
}
