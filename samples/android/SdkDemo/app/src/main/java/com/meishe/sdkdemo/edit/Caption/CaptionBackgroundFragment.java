package com.meishe.sdkdemo.edit.Caption;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.data.CaptionColorInfo;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : jml
 * @CreateDate : 2020/6/22.
 * @Description :视频编辑-字幕-背景色-Fragment
 * @Description :VideoEdit-Caption-background-Fragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptionBackgroundFragment extends BaseFragment {
    private RecyclerView mCaptionBackgroundRecyclerView;
    private CaptionBackgroundRecyclerAdaper mCaptionBackgroundRecycleAdapter;
    private SeekBar mCaptonOpacitySeekBar;
    private SeekBar mCaptonCornerSeekBar;
    private SeekBar mCaptionMarginSeekBar;
    private TextView mSeekBarOpacityValue;
    private TextView mSeekBarCornerValue;
    private TextView mSeekBarMarginValue;
    private OnCaptionBackgroundListener captionBackgroundListener;
    private ArrayList<CaptionColorInfo> mCaptionColorInfolist = new ArrayList<>();
    private LinearLayout mApplyToAll;
    private ImageView mApplyToAllImage;
    private TextView mApplyToAllText;
    private boolean mIsApplyToAll = false;
    /*是否是单段编辑
    * Whether it is single-segment editing
    * */
    private boolean mIsClipEdit=false;
    private LinearLayout mLlOpacity;
    private LinearLayout mLlCorner;
    private LinearLayout mLlMargin;

    /**
     * 背景操作回调
     * Background operation callback
     */
    public interface OnCaptionBackgroundListener{
        void onFragmentLoadFinished();
        void onCaptionColor(int pos);
        void onCaptionOpacity(int progress);
        void onCaptionCorner(int progress);
        void onCaptionPadding(int progress);
        void onIsApplyToAll(boolean isApplyToAll);
    }

    public void setCaptionBackgroundListener(OnCaptionBackgroundListener captionBackgroundListener) {
        this.captionBackgroundListener = captionBackgroundListener;
    }
    public void updateCaptionOpacityValue(int progress){
        mSeekBarOpacityValue.setText(String.valueOf(progress));
        mCaptonOpacitySeekBar.setProgress(progress);
    }

    /**
     * 更新设置圆角的进度
     * Update the progress of setting round corners
     * @param progress 获取圆角的值
     *
     */
    public void updateCaptionCornerValue(int progress){

        mSeekBarCornerValue.setText(String.valueOf(progress));
        mCaptonCornerSeekBar.setProgress(progress);
    }

    /**
     * 更新设置边距的进度
     * Update the progress of setting round corners
     * @param progress 获取圆角的值
     *
     */
    public void updateCaptionPaddingValue(int progress){

        mSeekBarMarginValue.setText(String.valueOf(progress/(float)100));
        mCaptionMarginSeekBar.setProgress(progress);
    }

    /**
     * 设置最大进度
     * Set maximum progress
     *  @param max 最大值(原则上是高度的一半)
     *            Maximum value (in principle, half of the height)
     */
    public void initCaptionMaxCorner(int max){
        mCaptonCornerSeekBar.setMax(max);
    }

    public void setCaptionBackgroundInfolist(ArrayList<CaptionColorInfo> captionColorInfolist) {
        this.mCaptionColorInfolist = captionColorInfolist;
        if(mCaptionBackgroundRecycleAdapter != null){
            mCaptionBackgroundRecycleAdapter.setCaptionBackgroundColorList(captionColorInfolist);
        }
    }

    public void applyToAllCaption(boolean isApplyToAll){
        mApplyToAllImage.setImageResource(isApplyToAll ? R.mipmap.applytoall : R.mipmap.unapplytoall);
        mApplyToAllText.setTextColor(isApplyToAll ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ff909293"));
        mIsApplyToAll = isApplyToAll;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyDataSetChanged(int selectPosition){
        if (selectPosition > 0) {
            if (mLlOpacity.getVisibility() != View.VISIBLE) {
                mLlOpacity.setVisibility(View.VISIBLE);
            }
            if (mLlCorner.getVisibility() != View.VISIBLE) {
                mLlCorner.setVisibility(View.VISIBLE);
            }
            if (mLlMargin.getVisibility() != View.VISIBLE) {
                mLlMargin.setVisibility(View.VISIBLE);
            }
        }
        mCaptionBackgroundRecycleAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootParent=null;
        if (mIsClipEdit){
            rootParent = inflater.inflate(R.layout.caption_clip_background_fragment, container, false);
        }else{
             rootParent = inflater.inflate(R.layout.caption_background_fragment, container, false);
        }
        mCaptionBackgroundRecyclerView = (RecyclerView)rootParent.findViewById(R.id.captionColorRecyerView);
        mCaptonOpacitySeekBar = (SeekBar)rootParent.findViewById(R.id.captonOpacitySeekBar);
        mCaptonOpacitySeekBar.setMax(100);
        mCaptonCornerSeekBar = (SeekBar)rootParent.findViewById(R.id.captonCornerSeekBar);
        mCaptonCornerSeekBar.setMax(100);
        mCaptionMarginSeekBar = (SeekBar)rootParent.findViewById(R.id.captionMarginSeekBar);
        mCaptionMarginSeekBar.setMax(100);

        mSeekBarOpacityValue = (TextView) rootParent.findViewById(R.id.seekBarOpacityValue);
        mSeekBarCornerValue = (TextView) rootParent.findViewById(R.id.seekBarCornerValue);
        mSeekBarMarginValue = (TextView) rootParent.findViewById(R.id.seekBarMarginValue);
        mApplyToAll = (LinearLayout)rootParent.findViewById(R.id.applyToAll);
        mLlOpacity = (LinearLayout)rootParent.findViewById(R.id.ll_opacity);
        mLlCorner = (LinearLayout)rootParent.findViewById(R.id.ll_corner);
        mLlMargin = (LinearLayout)rootParent.findViewById(R.id.ll_margin);
        mApplyToAllImage = (ImageView)rootParent.findViewById(R.id.applyToAllImage);
        mApplyToAllText = (TextView)rootParent.findViewById(R.id.applyToAllText);
        return rootParent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCaptionBackgroundRecycleAdapter();
        initCaptionBackgroundSeekBar();
        if(captionBackgroundListener != null){
            captionBackgroundListener.onFragmentLoadFinished();
        }
    }

    private void initCaptionBackgroundSeekBar() {
        mCaptonOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateCaptionOpacityValue(progress);
                    if(captionBackgroundListener != null){
                        captionBackgroundListener.onCaptionOpacity(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mCaptonCornerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateCaptionCornerValue(progress);
                    if(captionBackgroundListener != null){
                        captionBackgroundListener.onCaptionCorner(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCaptionMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateCaptionPaddingValue(progress);
                    if(captionBackgroundListener != null){
                        captionBackgroundListener.onCaptionPadding(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initCaptionBackgroundRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCaptionBackgroundRecyclerView.setLayoutManager(layoutManager);
        mCaptionBackgroundRecycleAdapter = new CaptionBackgroundRecyclerAdaper(getActivity());
        mCaptionBackgroundRecycleAdapter.setCaptionBackgroundColorList(mCaptionColorInfolist);
        mCaptionBackgroundRecyclerView.setAdapter(mCaptionBackgroundRecycleAdapter);
        mCaptionBackgroundRecyclerView.addItemDecoration(new SpaceItemDecoration(0, ScreenUtils.dip2px(getActivity(),16)));
        mCaptionBackgroundRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                mIsApplyToAll=false;
                applyToAllCaption(false);
//                if(captionBackgroundListener != null){
//                    captionBackgroundListener.onIsApplyToAll(mIsApplyToAll);
//                }
                if (pos==0){
                    mLlMargin.setVisibility(View.GONE);
                    mLlCorner.setVisibility(View.GONE);
                    mLlOpacity.setVisibility(View.GONE);
                }else{
                    mLlMargin.setVisibility(View.VISIBLE);
                    mLlCorner.setVisibility(View.VISIBLE);
                    mLlOpacity.setVisibility(View.VISIBLE);
                }
                if(captionBackgroundListener != null){
                    captionBackgroundListener.onCaptionColor(pos);
                }
            }
        });

        mApplyToAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIsPartEdit()){
                    return;
                }
                mIsApplyToAll = !mIsApplyToAll;
                applyToAllCaption(mIsApplyToAll);
                if(captionBackgroundListener != null){
                    captionBackgroundListener.onIsApplyToAll(mIsApplyToAll);
                }
            }
        });
    }

    public void setIsClipEdit(boolean clipEdit) {
        this.mIsClipEdit = clipEdit;
    }
}
