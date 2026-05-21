package com.meishe.sdkdemo.edit.Caption;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.data.CaptionColorInfo;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;

public class CaptionShadowFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView mApplyToAllText;
    private ImageView mApplyToAllImage;
    private boolean mIsApplyToAll = false;

    private OnCaptionShadowListener mOnCaptionShadowListener;
    private SeekBar mCaptonOpacitySeekBar;
    private TextView mSeekBarOpacityValue;
    private RecyclerView mCaptionBackgroundRecyclerView;
    private CaptionBackgroundRecyclerAdaper mCaptionBackgroundRecycleAdapter;
    private ArrayList<CaptionColorInfo> mCaptionColorInfolist = new ArrayList<>();
    private View mLlCaptionSeekViewContainer;

    public void notifyDataSetChanged() {
        mCaptionBackgroundRecycleAdapter.notifyDataSetChanged();
    }

    public interface OnCaptionShadowListener {
        void onFragmentLoadFinished();
        void onChangeShadow(int progress);
        void onIsApplyToAll(boolean isApplyToAll);
        void onCaptionShadowColor(int position);
    }


    public void setOnCaptionFontSizeRationListener(OnCaptionShadowListener mOnCaptionShadowListener) {
        this.mOnCaptionShadowListener = mOnCaptionShadowListener;
    }

    public void setCaptionBackgroundInfolist(ArrayList<CaptionColorInfo> captionColorInfolist) {
        this.mCaptionColorInfolist = captionColorInfolist;
        if(mCaptionBackgroundRecycleAdapter != null){
            mCaptionBackgroundRecycleAdapter.setCaptionBackgroundColorList(captionColorInfolist);
        }
    }

    public CaptionShadowFragment() {
    }

    public static CaptionShadowFragment newInstance(String param1, String param2) {
        CaptionShadowFragment fragment = new CaptionShadowFragment();
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
        View rootParent = inflater.inflate(R.layout.fragment_caption_shadow, container, false);
        mApplyToAllImage = (ImageView)rootParent.findViewById(R.id.applyToAllImage);
        mCaptionBackgroundRecyclerView = (RecyclerView)rootParent.findViewById(R.id.captionColorRecyerView);
        mApplyToAllText = (TextView)rootParent.findViewById(R.id.applyToAllText);
        mCaptonOpacitySeekBar = (SeekBar)rootParent.findViewById(R.id.captonOpacitySeekBar);
        mCaptonOpacitySeekBar.setMax(100);
        mSeekBarOpacityValue = (TextView) rootParent.findViewById(R.id.seekBarOpacityValue);
        mLlCaptionSeekViewContainer =  rootParent.findViewById(R.id.ll_caption_sub);
        updateCaptionOpacityValue(50);
        return rootParent;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        initCaptionBackgroundRecycleAdapter();
        if(mOnCaptionShadowListener != null){
            mOnCaptionShadowListener.onFragmentLoadFinished();
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
                if(mOnCaptionShadowListener != null){
                    mOnCaptionShadowListener.onIsApplyToAll(mIsApplyToAll);
                }
            }
        });

        mCaptonOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateCaptionOpacityValue(progress);
                    if(mOnCaptionShadowListener != null){
                        mOnCaptionShadowListener.onChangeShadow(progress);
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
//                if(mOnCaptionShadowListener != null){
//                    mOnCaptionShadowListener.onIsApplyToAll(mIsApplyToAll);
//                }
                if(mOnCaptionShadowListener != null){
                    mOnCaptionShadowListener.onCaptionShadowColor(pos);
                }
            }
        });

    }

    public void showSeekContainerVisible(int visible){
        if (mLlCaptionSeekViewContainer!=null){
            mLlCaptionSeekViewContainer.setVisibility(visible);
        }
    }

}