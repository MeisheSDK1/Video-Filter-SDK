package com.meicam.effectsdkdemo.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.adapter.SpaceItemDecoration;
import com.meicam.effectsdkdemo.adapter.StickerItemAdapter;
import com.meicam.effectsdkdemo.data.StickerJsonParseData;
import com.meicam.effectsdkdemo.utils.ParseJsonFile;
import com.meishe.libbase.util.ScreenUtils;
import com.meishe.nveffectkit.NveEffectKit;
import com.meishe.nveffectkit.prop.NveFaceProp;
import com.meishe.nveffectkit.utils.NveAssetPackageManagerUtil;

import java.util.ArrayList;


/**
 * @author cdv
 */
public class MsPropView extends RelativeLayout {
    public static final String ARSCENE_JSON_DIRECTORY = "arscene/info.json";
    private final Context mContext;
    private ArrayList<StickerJsonParseData.StickerJsonFileInfo> mArSceneDataInfos;
    private NveFaceProp mFaceProp;

    public MsPropView(Context context) {
        this(context, null);
    }

    public MsPropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_prop, this);
        initData();
        initView(context, rootView);
    }

    private void initData() {
        mArSceneDataInfos = ParseJsonFile.getStickerJsonFileList(mContext, ARSCENE_JSON_DIRECTORY);
        if (null != mArSceneDataInfos && mArSceneDataInfos.size() > 0) {
            for (int i = 0; i < mArSceneDataInfos.size(); i++) {
                StickerJsonParseData.StickerJsonFileInfo item = mArSceneDataInfos.get(i);
                NveAssetPackageManagerUtil.installFxPackage(item.getStringFilePath(), "", NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE);
            }
        }
        StickerJsonParseData.StickerJsonFileInfo noProps = new StickerJsonParseData.StickerJsonFileInfo();
        noProps.setCoverImageId(R.mipmap.none);
        mArSceneDataInfos.add(0, noProps);
    }


    public void initView(Context context, View rootView) {
        RecyclerView mRecyclerView = rootView.findViewById(R.id.display_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration(ScreenUtils.dip2px(context, 0), ScreenUtils.dip2px(context, 12));
        mRecyclerView.addItemDecoration(spaceItemDecoration);
        StickerItemAdapter mPropAdapter = new StickerItemAdapter(context);
        mPropAdapter.setDataList(mArSceneDataInfos);
        mPropAdapter.setOnItemClickListener((view, position) -> {
            StickerJsonParseData.StickerJsonFileInfo assetInfoData = mArSceneDataInfos.get(position);
            String mCurrentPropId = assetInfoData.getEffectId();
            if (null == mFaceProp) {
                mFaceProp = new NveFaceProp();
                mFaceProp.setEnable(!TextUtils.isEmpty(mCurrentPropId));
                NveEffectKit.getInstance().setFaceProp(mFaceProp);
            }
            mFaceProp.setPropId(mCurrentPropId);
        });
        mRecyclerView.setAdapter(mPropAdapter);
    }

    public void onResume() {
        if (null != mFaceProp) {
            NveEffectKit.getInstance().setFaceProp(mFaceProp);
        }
    }
}
