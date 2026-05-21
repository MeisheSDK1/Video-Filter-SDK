package com.meicam.effectsdkdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.adapter.FilterItemAdapter;
import com.meicam.effectsdkdemo.adapter.SpaceItemDecoration;
import com.meicam.effectsdkdemo.data.AssetInfoData;
import com.meicam.effectsdkdemo.data.NvAsset;
import com.meicam.effectsdkdemo.data.NvAssetManager;
import com.meicam.effectsdkdemo.utils.ParseJsonFile;
import com.meishe.libbase.util.ScreenUtils;
import com.meishe.nveffectkit.NveEffectKit;
import com.meishe.nveffectkit.filter.NveFilter;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author cdv
 */
public class BeautyFilterView extends RelativeLayout {
    private SeekBar mFilterSeekBar;
    private RecyclerView mFilterRecyclerList;
    private FilterItemAdapter mFilterAdapter;
    private ArrayList<AssetInfoData> mFilterDataInfos = new ArrayList<>();

    /**
     * filter
     */
    public static final String FILTER_BUNDLE_PATH = "beauty/filter";
    public static final String FILTER_COVER_DIRECTORY = "file:///android_asset/beauty/filter/";
    public static final String FILTER_JSON_DIRECTORY = "beauty/filter/info.json";

    private NveFilter mNvFilter;
    private float mCurrentIntensity;

    public BeautyFilterView(Context context) {
        this(context, null);
    }

    public BeautyFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initData(context);
        initListener();
    }

    private void initListener() {
        mFilterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentIntensity = progress * 1.0f / 100;
                    mNvFilter.setFilterIntensity(mCurrentIntensity);
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


    private void initData(Context context) {
        initFilterData(context);
        initFilterRecyclerView(context);
    }

    private void initFilterData(Context context) {
        NvAssetManager.init(context);
        NvAssetManager mAssetManager = NvAssetManager.getInstance();
        mAssetManager.searchReservedAssets(NvAsset.ASSET_FILTER, FILTER_BUNDLE_PATH);
        mFilterDataInfos = ParseJsonFile.getFilterJsonFileList(context, FILTER_JSON_DIRECTORY, FILTER_COVER_DIRECTORY);
        AssetInfoData noFilter = new AssetInfoData();
        noFilter.setName(getResources().getString(R.string.no_use));
        noFilter.setCoverImageId(R.mipmap.none);
        mFilterDataInfos.add(0, noFilter);
    }


    private void initFilterRecyclerView(Context context) {
        mFilterAdapter = new FilterItemAdapter(context);
        mFilterAdapter.setDataList(mFilterDataInfos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mFilterRecyclerList.setLayoutManager(linearLayoutManager);
        mFilterRecyclerList.setAdapter(mFilterAdapter);
        mFilterRecyclerList.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(context, 0), ScreenUtils.dip2px(context, 12)));
        mFilterAdapter.setOnItemClickListener(new FilterItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AssetInfoData assetInfoData = mFilterDataInfos.get(position);
                if (assetInfoData == null) {
                    Log.e("TAG", "onItemClick: mFilterAdapter position is unUsed!");
                    return;
                }
                //这里滤镜只能添加一个,需要检查删除
                if (mNvFilter != null) {
                    NveEffectKit.getInstance().removeFilter(mNvFilter);
                    mNvFilter = null;
                }
                if (position == 0) {
                    mFilterSeekBar.setVisibility(INVISIBLE);
                    return;
                }
                //这里使用的都是包特技
                mFilterSeekBar.setVisibility(VISIBLE);
                String effectId = assetInfoData.getEffectId();
                Log.e("TAG", "onItemClick: " + position + "  " + effectId);
                mNvFilter = new NveFilter(effectId);
                NveEffectKit.getInstance().addFilter(mNvFilter);
                mNvFilter.setFilterIntensity(0.5f);
                Log.e("TAG", "onItemClick: " + mNvFilter.getFilterIntensity());
                mFilterSeekBar.setProgress(50);
            }
        });
    }

    private void init(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.filter_view, this);
        mFilterRecyclerList = rootView.findViewById(R.id.beauty_shape_item_list);
        mFilterSeekBar = rootView.findViewById(R.id.filter_sb);
    }

    public void onResume() {
        if (null != mNvFilter) {
            NveEffectKit.getInstance().addFilter(mNvFilter);
            mNvFilter.setFilterIntensity(mCurrentIntensity);
        }
    }

}
