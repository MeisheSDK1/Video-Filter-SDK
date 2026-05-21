package com.meishe.sdkdemo.edit.Caption;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.meishe.sdkdemo.R;
import com.meishe.utils.ToastUtil;

public class CaptionFontSizeRatioFragment extends BaseFragment  {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView mApplyToAllText;
    private ImageView mApplyToAllImage;
    private boolean mIsApplyToAll = false;

    private OnCaptionFontSizeRationListener mOnCaptionFontSizeRationListener;
    private SeekBar mCaptonOpacitySeekBar;
    private TextView mSeekBarOpacityValue;
    private TextView mTitle;
    private View mLlSeekContainer;
    private boolean mSeekEnable;

    public interface OnCaptionFontSizeRationListener{
        void onFragmentLoadFinished();
        void onChangeFontSizeRation(int progress);
        void onIsApplyToAll(boolean isApplyToAll);
    }

    public void setOnCaptionFontSizeRationListener(OnCaptionFontSizeRationListener mOnCaptionFontSizeRationListener) {
        this.mOnCaptionFontSizeRationListener = mOnCaptionFontSizeRationListener;
    }

    public CaptionFontSizeRatioFragment() {
    }

    public static CaptionFontSizeRatioFragment newInstance(String param1, String param2) {
        CaptionFontSizeRatioFragment fragment = new CaptionFontSizeRatioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootParent = inflater.inflate(R.layout.fragment_caption_font_size_ratio, container, false);
        mApplyToAllImage = (ImageView)rootParent.findViewById(R.id.applyToAllImage);
        mApplyToAllText = (TextView)rootParent.findViewById(R.id.applyToAllText);
        mCaptonOpacitySeekBar = (SeekBar)rootParent.findViewById(R.id.captonOpacitySeekBar);
        mCaptonOpacitySeekBar.setMax(100);
        mSeekBarOpacityValue = (TextView) rootParent.findViewById(R.id.seekBarOpacityValue);
        mTitle = (TextView) rootParent.findViewById(R.id.tv_title);
        mLlSeekContainer =rootParent.findViewById(R.id.ll_seek_container);
        mLlSeekContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSeekEnable){
                    return false;
                }else{
                    ToastUtil.showToast(getContext(),getString(R.string.toast_font_size_ratio));
                    return true;
                }
            }
        });
        updateCaptionOpacityValue(10);
        return rootParent;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        if(mOnCaptionFontSizeRationListener != null){
            mOnCaptionFontSizeRationListener.onFragmentLoadFinished();
        }
    }

    private void initListener() {
        mApplyToAllImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIsPartEdit()){
                    return;
                }
                mIsApplyToAll = !mIsApplyToAll;
                applyToAllCaption(mIsApplyToAll);
                if(mOnCaptionFontSizeRationListener != null){
                    mOnCaptionFontSizeRationListener.onIsApplyToAll(mIsApplyToAll);
                }
            }
        });

        mCaptonOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateCaptionOpacityValue(progress);
                    if(mOnCaptionFontSizeRationListener != null){
                        mOnCaptionFontSizeRationListener.onChangeFontSizeRation(progress);
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

    public void updateCaptionOpacityValue(int progress){
        mSeekBarOpacityValue.setText(String.valueOf(progress));
        mCaptonOpacitySeekBar.setProgress(progress);
    }


    public void applyToAllCaption(boolean isApplyToAll){
        if(isApplyToAll){
            mApplyToAllImage.setImageResource(R.mipmap.applytoall);
            mApplyToAllText.setTextColor(Color.parseColor("#ff4a90e2"));
        }else {
            mApplyToAllImage.setImageResource(R.mipmap.unapplytoall);
            mApplyToAllText.setTextColor(Color.parseColor("#ff909293"));
        }
        mIsApplyToAll = isApplyToAll;
    }

    public void setSeekEnable(boolean enable){
        if (mCaptonOpacitySeekBar==null){
            return;
        }
        mSeekEnable=enable;
        mCaptonOpacitySeekBar.setEnabled(enable);
        if (enable){
            mSeekBarOpacityValue.setTextColor(Color.WHITE);
            mTitle.setTextColor(Color.WHITE);
            mCaptonOpacitySeekBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }else{
            mSeekBarOpacityValue.setTextColor(Color.GRAY);
            mTitle.setTextColor(Color.GRAY);
            mCaptonOpacitySeekBar.getThumb().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        }
    }
}