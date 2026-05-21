package com.meishe.sdkdemo.edit.audio.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.audio.adapter.AudioEqualizerAdapter;
import com.meishe.sdkdemo.edit.data.AudioEqualizerItem;
import com.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2021/6/25 10:40
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class AudioEqualizerAdjustView extends LinearLayout {
    private List<AudioEqualizerItem> mAudioEqualizerList;
    private ConflictRecyclerView mRvAudioEqualizer;
    private  SpaceItemDecoration spaceItemDecoration;
    private AudioEqualizerAdapter mAdapter;
    //设置每页显示最大数量
    //Set the maximum number of displays per page
    private static final int MAX_SHOW_ITEM_COUNT = 9;
    public AudioEqualizerAdjustView(Context context) {
        this(context, null);
    }

    public AudioEqualizerAdjustView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioEqualizerAdjustView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {

        View root = LayoutInflater.from(context).inflate(R.layout.activity_audio_equalizer_adjust_new, this);
        mRvAudioEqualizer = root.findViewById(R.id.recycler_audio_equalizer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRvAudioEqualizer.setLayoutManager(linearLayoutManager);
        mAdapter = new AudioEqualizerAdapter(context);
        mRvAudioEqualizer.setAdapter(mAdapter);
    }

    /**
     * 设置需要展示的值
     *Set values to display
     * @param audioEqualizerList 数据集合
     */
    public void setAudioEqualizerList(List<AudioEqualizerItem> audioEqualizerList) {
        if(null == audioEqualizerList || audioEqualizerList.size() == 0){
            return;
        }
        mAudioEqualizerList = new ArrayList<>();
        mAudioEqualizerList.addAll(audioEqualizerList);
        int size = mAudioEqualizerList.size();
        //最多显示9个item
        //Display up to 9 items
        if(size>MAX_SHOW_ITEM_COUNT){
            size = MAX_SHOW_ITEM_COUNT;
        }
        int itemWidth = ScreenUtils.dip2px(getContext(),25);
        //外部布局的内外边距，使用这个方法为了避免首次设置的时候getWidth返回0
        //The internal and external margins of the external layout. This method is used to avoid that getWidth returns 0 when it is set for the first time
        int marginAndPadding = ScreenUtils.dip2px(getContext(),68);
        int spaceItem = (ScreenUtils.getScreenWidth(getContext())-itemWidth*size-marginAndPadding)/size;
        //先移除上一次设置的Decoration
        //Remove the last set decoration first
        mRvAudioEqualizer.removeItemDecoration(spaceItemDecoration);
        spaceItemDecoration = new SpaceItemDecoration(spaceItem/2, spaceItem/2);
        mRvAudioEqualizer.smoothScrollToPosition(0);
        mRvAudioEqualizer.addItemDecoration(spaceItemDecoration);
        mAdapter.setData(mAudioEqualizerList);
    }


    public void setOnItemProgressChangeListener(AudioEqualizerAdjustItemView.onItemProgressChangeListener itemListener) {
        if (null != mAdapter) {
            mAdapter.setOnAudioItemChangedListener(itemListener);
        }
    }
}