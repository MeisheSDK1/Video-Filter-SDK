package com.meishe.sdkdemo.edit.compoundcaption;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.data.CaptionColorInfo;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : Hangzhou
 * @CreateDate : 2020/7/22.
 * @Description :视频编辑-字幕-颜色-Fragment
 * @Description :VideoEdit-Caption-Color-Fragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CompoundCaptionColorFragment extends Fragment {
    private RecyclerView mCaptionColorRecyerView;
    private CompoundCaptionColorRecyclerAdaper mCaptionColorRecycleAdapter;
    private SeekBar mCaptonOpacitySeekBar;
    private TextView mSeekBarOpacityValue;
    private OnCaptionColorListener mCaptionColorListener;
    private ArrayList<CaptionColorInfo> mCaptionColorInfolist = new ArrayList<>();
    public interface OnCaptionColorListener{
        void onFragmentLoadFinished();
        void onCaptionColor(int pos);
        void onCaptionOpacity(int progress);
        void onIsApplyToAll(boolean isApplyToAll);
    }

    public void setCaptionColorListener(OnCaptionColorListener captionColorListener) {
        this.mCaptionColorListener = captionColorListener;
    }
    public void updateCaptionOpacityValue(int progress){
        mSeekBarOpacityValue.setText(String.valueOf(progress));
        mCaptonOpacitySeekBar.setProgress(progress);
    }

    public void setCaptionColorInfolist(ArrayList<CaptionColorInfo> captionColorInfolist) {
        this.mCaptionColorInfolist = captionColorInfolist;
        if(mCaptionColorRecycleAdapter != null)
            mCaptionColorRecycleAdapter.setCaptionColorList(captionColorInfolist);
    }


    public void notifyDataSetChanged(){
        mCaptionColorRecycleAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootParent = inflater.inflate(R.layout.compound_caption_color_fragment, container, false);
        mCaptionColorRecyerView = (RecyclerView)rootParent.findViewById(R.id.captionColorRecyerView);
        mCaptonOpacitySeekBar = (SeekBar)rootParent.findViewById(R.id.captonOpacitySeekBar);
        mCaptonOpacitySeekBar.setMax(100);
        mSeekBarOpacityValue = (TextView) rootParent.findViewById(R.id.seekBarOpacityValue);
        return rootParent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCaptionColorRecycleAdapter();
        initCaptionColorSeekBar();
        if(mCaptionColorListener != null){
            mCaptionColorListener.onFragmentLoadFinished();
        }
    }

    private void initCaptionColorSeekBar() {
        mCaptonOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateCaptionOpacityValue(progress);
                    if(mCaptionColorListener != null){
                        mCaptionColorListener.onCaptionOpacity(progress);
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

    private void initCaptionColorRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCaptionColorRecyerView.setLayoutManager(layoutManager);
        mCaptionColorRecycleAdapter = new CompoundCaptionColorRecyclerAdaper(getActivity());
        mCaptionColorRecycleAdapter.setCaptionColorList(mCaptionColorInfolist);
        mCaptionColorRecyerView.setAdapter(mCaptionColorRecycleAdapter);
        mCaptionColorRecyerView.addItemDecoration(new SpaceItemDecoration(0, ScreenUtils.dip2px(getActivity(),29)));
        mCaptionColorRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if(mCaptionColorListener != null){
                    mCaptionColorListener.onCaptionColor(pos);
                }
            }
        });

    }
}
