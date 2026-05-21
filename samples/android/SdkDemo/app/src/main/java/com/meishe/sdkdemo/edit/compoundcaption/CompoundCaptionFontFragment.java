package com.meishe.sdkdemo.edit.compoundcaption;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.data.AssetItem;

import java.util.ArrayList;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/22.
 * @Description :视频编辑-字幕-字体-Fragment
 * @Description :VideoEdit-Caption-Font-Fragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CompoundCaptionFontFragment extends Fragment {
    private RecyclerView mCaptionFontRecycleView;
    private CompoundCaptionFontRecyclerAdaper mCaptionFontRecycleAdapter;
    private ArrayList<AssetItem> mCaptionFontInfolist = new ArrayList<>();
    //private RelativeLayout mDownloadMoreCapionSytle;
    private OnCaptionFontListener mCaptionFontListener;
    private Button mItalicButton;
    private boolean mIsItalic = false;

    public interface OnCaptionFontListener{
        void onFragmentLoadFinished();
        void onItemClick(int pos);
        void onBold(boolean mIsBold);
        void onItalic(boolean isItalic);
        void onShadow();
        void onUnderline();
        void onIsApplyToAll(boolean isApplyToAll);
        void onFontDownload(int pos);

    }
    public void setCaptionFontListener(OnCaptionFontListener captionFontListener) {
        this.mCaptionFontListener = captionFontListener;
    }

    public void setSelectedPos(int selectedPos){
        if (mCaptionFontRecycleAdapter != null)
            mCaptionFontRecycleAdapter.setSelectedPos(selectedPos);
    }
    public void setFontInfolist(ArrayList<AssetItem> fontInfolist) {
        mCaptionFontInfolist = fontInfolist;
        if (mCaptionFontRecycleAdapter != null)
            mCaptionFontRecycleAdapter.setAssetInfoList(fontInfolist);
    }


    public void notifyDataSetChanged(){
        if(mCaptionFontRecycleAdapter != null)
            mCaptionFontRecycleAdapter.notifyDataSetChanged();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootParent = inflater.inflate(R.layout.compound_caption_font_list_fragment, container, false);
        mItalicButton = (Button) rootParent.findViewById(R.id.italicButton);
        mCaptionFontRecycleView = (RecyclerView) rootParent.findViewById(R.id.captionFontRecycleView);
        return rootParent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAssetRecycleAdapter();
        if(mCaptionFontListener != null){
            mCaptionFontListener.onFragmentLoadFinished();
        }
    }

    private void initAssetRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCaptionFontRecycleView.setLayoutManager(layoutManager);
        mCaptionFontRecycleAdapter = new CompoundCaptionFontRecyclerAdaper(getActivity());
        mCaptionFontRecycleAdapter.setAssetInfoList(mCaptionFontInfolist);
        mCaptionFontRecycleView.setAdapter(mCaptionFontRecycleAdapter);
        mCaptionFontRecycleView.addItemDecoration(new SpaceItemDecoration(0, 8));
        mCaptionFontRecycleAdapter.setOnItemClickListener(new CompoundCaptionFontRecyclerAdaper.OnFontItemClickListener() {
            @Override
            public void onItemDownload(View view, int position) {
                if(mCaptionFontListener != null){
                    mCaptionFontListener.onFontDownload(position);
                }
            }

            @Override
            public void onItemClick(View view, int pos) {
                if(mCaptionFontListener != null){
                    mCaptionFontListener.onItemClick(pos);
                }
            }
        });

        mItalicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIsItalic = !mIsItalic;
                updateItalicButton(mIsItalic);
                if(mCaptionFontListener != null){
                    mCaptionFontListener.onItalic(mIsItalic);
                }
            }
        });
    }


    public void updateItalicButton(boolean isItalic){
        mItalicButton.setBackgroundResource(isItalic ? R.drawable.shape_caption_font_corner_button_selected : R.drawable.shape_caption_font_corner_button);
        mItalicButton.setTextColor(isItalic ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ffffffff"));
        mIsItalic = isItalic;
    }




}
