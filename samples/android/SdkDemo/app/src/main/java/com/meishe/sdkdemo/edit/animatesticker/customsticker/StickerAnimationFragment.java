package com.meishe.sdkdemo.edit.animatesticker.customsticker;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meishe.base.utils.Utils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.List;
/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : unKnow
 * @CreateDate : 2021/4/20.
 * @Description :贴纸动画Fragment
 * @Description :StickerAnimalFragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class StickerAnimationFragment extends Fragment {
    private int[] ANIMATION = new int[]{R.string.march_in_animation, R.string.march_out_animation, R.string.combination_animation};
    private RecyclerView mRvAnimationList;
    private AnimationAdapter mAnimationAdapter;
    private List<AssetItem> mPeriodList = new ArrayList<>();
    private List<AssetItem> mInList = new ArrayList<>();
    private List<AssetItem> mOutList = new ArrayList<>();
    private TabLayout mTabAnimation;
    private ImageView mIvLoadMore;
    private TextView mTvLoadMore;
    private int mSelectedAniPos;
    private int mSelectedInAniPos;
    private int mSelectedOutAniPos;
    private OnStickerAnimationStateListener mStickerAnimationListener;
    private NvAssetManager mAssetManager;
    private final String TAG = "StickerAnimation";
    private final String PATH_OUT_ANIMATION = "stickeranimation/out";
    private final String PATH_IN_ANIMATION = "stickeranimation/in";
    private final String PATH_PERIOD_ANIMATION = "stickeranimation/period";

    public interface OnStickerAnimationStateListener {
        void onFragmentLoadFinished();

        void onLoadMore(int type);

        void onItemClick(int pos, int type, AssetItem selectedItem);
    }

    public void setStickerAnimationListener(OnStickerAnimationStateListener mStickerAnimationListener) {
        this.mStickerAnimationListener = mStickerAnimationListener;
    }

    /**
     * 检测是动画的tab按照要求显示,仅仅第一次初始化会使用
     * Detect that the animated tab is displayed as required. Only the first initialization will use
     */
    public void checkSelectedTab() {
        int selectedTabPosition = mTabAnimation.getSelectedTabPosition();
        if (mSelectedAniPos > 0 && selectedTabPosition != 2) {
            //如果设置了组合动画，但是组合动画的tab没有被选中，则选中
            //If composite animation is set, but the tab of composite animation is not selected, select
            TabLayout.Tab animationTab = mTabAnimation.getTabAt(2);
            if (animationTab != null) {
                animationTab.select();
            }
        } else if (mSelectedOutAniPos > 0 && mSelectedInAniPos <= 0 && selectedTabPosition != 1) {
            //如果仅仅设置了出动画动画，但是出动画的tab没有被选中，则选中
            //If out animation is set, but the animated tab is not selected, select
            TabLayout.Tab animationTab = mTabAnimation.getTabAt(1);
            if (animationTab != null) {
                animationTab.select();
            }
        }
    }

    public void setAssetList(List<AssetItem> combination, List<AssetItem> in, List<AssetItem> out) {
        mPeriodList = combination;
        mInList = in;
        mOutList = out;
        if (mTabAnimation != null) {
            displayTabAnimation(mTabAnimation.getSelectedTabPosition());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayTabAnimation(mTabAnimation.getSelectedTabPosition());
        Log.d("stickerAnimation", "stickerAnimationFragment onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sticker_animal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        initListener();
        if (mStickerAnimationListener != null) {
            mStickerAnimationListener.onFragmentLoadFinished();
        }
    }

    private void initView(View view) {
        mTabAnimation = view.findViewById(R.id.tab_animation);
        mIvLoadMore = view.findViewById(R.id.iv_load_more);
        mTvLoadMore = view.findViewById(R.id.tv_load_more);
        mRvAnimationList = view.findViewById(R.id.rv_list);
    }

    private void initListener() {

        mIvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStickerAnimationListener != null) {
                    mStickerAnimationListener.onLoadMore(getType(mTabAnimation.getSelectedTabPosition()));
                }
            }
        });
        mTvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStickerAnimationListener != null) {
                    mStickerAnimationListener.onLoadMore(getType(mTabAnimation.getSelectedTabPosition()));
                }
            }
        });
        mTabAnimation.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                displayTabAnimation(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mAnimationAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (mStickerAnimationListener != null) {
                    mAnimationAdapter.setSelectedPos(pos);
                    int type;
                    int typePos = mTabAnimation.getSelectedTabPosition();
                    if (typePos == 0) {
                        type = NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION;
                        mSelectedInAniPos = pos;
                        mSelectedAniPos = 0;
                    } else if (typePos == 1) {
                        type = NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION;
                        mSelectedOutAniPos = pos;
                        mSelectedAniPos = 0;
                    } else {
                        type = NvAsset.ASSET_ANIMATED_STICKER_ANIMATION;
                        mSelectedAniPos = pos;
                        mSelectedOutAniPos = 0;
                        mSelectedInAniPos = 0;
                    }
                    mStickerAnimationListener.onItemClick(pos, type, mAnimationAdapter.getSelectedItem());
                }
            }
        });
    }

    private void initData() {
        for (int value : ANIMATION) {
            mTabAnimation.addTab(mTabAnimation.newTab().setText(value));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvAnimationList.setLayoutManager(layoutManager);
        mAnimationAdapter = new AnimationAdapter(getActivity());
        mAnimationAdapter.setAssetList(mInList);
        mRvAnimationList.setAdapter(mAnimationAdapter);
        mRvAnimationList.addItemDecoration(new SpaceItemDecoration(0, 15));
        mAssetManager = NvAssetManager.sharedInstance();
        refreshAnimationData();
        displayTabAnimation(0);
        checkSelectedTab();
    }

    private void refreshAnimationData() {
        mOutList.clear();
        mInList.clear();
        mPeriodList.clear();
        dealAssetData(NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION, PATH_IN_ANIMATION, mInList);
        dealAssetData(NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION, PATH_OUT_ANIMATION, mOutList);
        dealAssetData(NvAsset.ASSET_ANIMATED_STICKER_ANIMATION, PATH_PERIOD_ANIMATION, mPeriodList);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String periodAnimationId = arguments.getString("periodAnimationId");
            String outAnimationId = arguments.getString("outAnimationId");
            String inAnimationId = arguments.getString("inAnimationId");
            if (!TextUtils.isEmpty(periodAnimationId)) {
                mSelectedAniPos = getSelectedPosInList(periodAnimationId, mPeriodList);
            } else {
                if (!TextUtils.isEmpty(inAnimationId)) {
                    mSelectedInAniPos = getSelectedPosInList(inAnimationId, mInList);
                }
                if (!TextUtils.isEmpty(outAnimationId)) {
                    mSelectedOutAniPos = getSelectedPosInList(outAnimationId, mOutList);
                }
            }
        }
    }

    private int getSelectedPosInList(String animationId, List<AssetItem> assetItems) {
        if (TextUtils.isEmpty(animationId) || assetItems == null || assetItems.isEmpty()) return 0;
        for (int i = 0; i < assetItems.size(); i++) {
            if (TextUtils.equals(animationId, assetItems.get(i).getAsset().uuid)) {
                return i;
            }
        }
        return 0;
    }

    /***
     * 展示动画Tab对应的内容
     * Show the content corresponding to the animated tab
     */
    private void displayTabAnimation(int pos) {
        if (mAnimationAdapter == null) {
            return;
        }
        int type;
        int selectPos = 0;
        if (pos == 0) {
            mAnimationAdapter.setAssetList(mInList);
            type = NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION;
            selectPos = mSelectedInAniPos;
        } else if (pos == 1) {
            mAnimationAdapter.setAssetList(mOutList);
            type = NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION;
            selectPos = mSelectedOutAniPos;
        } else {
            mAnimationAdapter.setAssetList(mPeriodList);
            type = NvAsset.ASSET_ANIMATED_STICKER_ANIMATION;
            selectPos = mSelectedAniPos;
        }
        mAnimationAdapter.setSelectedPos(selectPos);
        mAnimationAdapter.notifyDataSetChanged();
        if (mStickerAnimationListener != null) {
            mStickerAnimationListener.onItemClick(selectPos, type, mAnimationAdapter.getSelectedItem());
        }
    }

    /**
     * 获取资源类型
     * Get resource type
     */
    private int getType(int pos) {
        int type;
        if (pos == 0) {
            type = NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION;
        } else if (pos == 1) {
            type = NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION;
        } else {
            type = NvAsset.ASSET_ANIMATED_STICKER_ANIMATION;
        }
        return type;
    }

    static class AnimationAdapter extends RecyclerView.Adapter<AnimationAdapter.ViewHolder> {
        private List<AssetItem> mAssetList = new ArrayList<>();
        private Context mContext;
        private OnItemClickListener mOnItemClickListener = null;
        private int mSelectedPos = 0;

        AnimationAdapter(Context context) {
            mContext = context;
        }

        void setAssetList(List<AssetItem> assetArrayList) {
            this.mAssetList = assetArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_animation_caption, parent, false);
            return new ViewHolder(v);
        }

        public AssetItem getSelectedItem() {
            if (mAssetList == null || mSelectedPos < 0 || mSelectedPos > mAssetList.size())
                return null;
            return mAssetList.get(mSelectedPos);
        }

        public void setSelectedPos(int selectedPos) {
            if (selectedPos == mSelectedPos) {
                return;
            }
            if (selectedPos >= 0 && mAssetList != null && selectedPos < mAssetList.size()) {
                notifyItemChanged(mSelectedPos);
                this.mSelectedPos = selectedPos;
                notifyItemChanged(mSelectedPos);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            AssetItem assetItem = mAssetList.get(position);
            if (assetItem == null)
                return;
            NvAsset asset = assetItem.getAsset();
            if (asset == null)
                return;
            if (assetItem.getAssetMode() == AssetItem.ASSET_NONE) {
                holder.mIvCover.setImageResource(assetItem.getImageRes());
            } else {
                loadWebpImage(holder.mIvCover, asset.coverUrl);
            }

            holder.mTvName.setText(asset.name);
            holder.vSelected.setVisibility(mSelectedPos == position ? View.VISIBLE : View.GONE);
            holder.mTvName.setTextColor(mSelectedPos == position ? mContext.getResources().getColor(R.color.red_ff64)
                    : mContext.getResources().getColor(R.color.white));
            holder.mTvName.setSelected(true);
        }

        public void loadWebpImage(SimpleDraweeView view, String imageUrl) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(imageUrl)
                    .setAutoPlayAnimations(true)
                    .setOldController(view.getController())
                    .build();
            view.setController(controller);
        }

        @Override
        public int getItemCount() {
            return mAssetList == null ? 0 : mAssetList.size();
        }

        public void setOnItemClickListener(OnItemClickListener itemClickListener) {
            this.mOnItemClickListener = itemClickListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            SimpleDraweeView mIvCover;
            View vSelected;
            TextView mTvName;

            ViewHolder(View itemView) {
                super(itemView);
                mIvCover = itemView.findViewById(R.id.iv_cover);
                vSelected = itemView.findViewById(R.id.v_select);
                mTvName = itemView.findViewById(R.id.tv_name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                });
            }
        }
    }

    public void refreshAnimationIn() {
        mInList.clear();
        dealAssetData(NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION, PATH_IN_ANIMATION, mInList);
        displayTabAnimation(0);
    }

    public void refreshAnimationOut() {
        mOutList.clear();
        dealAssetData(NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION, PATH_OUT_ANIMATION, mOutList);
        displayTabAnimation(1);

    }

    public void refreshAnimation() {
        mPeriodList.clear();
        dealAssetData(NvAsset.ASSET_ANIMATED_STICKER_ANIMATION, PATH_PERIOD_ANIMATION, mPeriodList);
        displayTabAnimation(2);

    }

    private void dealAssetData(int assetType, String assetPath, List<AssetItem> assetList) {
        mAssetManager.searchLocalAssets(assetType);
        mAssetManager.searchReservedAssets(assetType, assetPath);
        ArrayList<NvAsset> usableAsset = getAssetsDataList(assetType);
        String jsonBundlePath = assetPath + "/info.json";
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(getActivity(), jsonBundlePath);
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                for (NvAsset asset : usableAsset) {
                    if (asset == null)
                        continue;
                    if (TextUtils.isEmpty(asset.uuid))
                        continue;
                    if (asset.isReserved && asset.uuid.equals(jsonFileInfo.getFxPackageId())) {
                        if (Utils.isZh()) {
                            asset.name = jsonFileInfo.getName_Zh();
                        } else {
                            asset.name = jsonFileInfo.getName();
                        }
                        if (!TextUtils.isEmpty(jsonFileInfo.getFitRatio())) {
                            try {
                                asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                            } catch (Exception e) {
                                Log.e(TAG, "Exception=" + e);
                                return;
                            }
                        }
                        asset.coverUrl = "asset://android_asset/" +
                                assetPath +
                                "/" +
                                asset.uuid +
                                "/" +
                                jsonFileInfo.getImageName();
                    }
                }
            }
        }
        if (assetList == null) {
            return;
        }
        AssetItem firstItem = new AssetItem();
        NvAsset firstAsset = new NvAsset();
        firstAsset.name = getString(R.string.timeline_fx_none);
        firstItem.setImageRes(R.mipmap.square_clear);
        firstItem.setAssetMode(AssetItem.ASSET_NONE);
        firstItem.setAsset(firstAsset);
        assetList.add(firstItem);
        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : usableAsset) {
            if (asset == null)
                continue;
            if (TextUtils.isEmpty(asset.uuid))
                continue;
            if ((ratio & asset.aspectRatio) == 0) {
                /*
                 * 制作比例不适配，不加载
                 * Production proportions do not fit, do not load
                 * */
                continue;
            }
            AssetItem assetItem = new AssetItem();
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            assetList.add(assetItem);
        }
    }

    /*
     * 获取下载到手机路径下的素材，包括assets路径下自带的素材
     * Get the material downloaded to the mobile phone path,
     * including the material that comes with the assets path
     * */
    private ArrayList<NvAsset> getAssetsDataList(int assetType) {
        return mAssetManager.getUsableAssets(assetType, NvAsset.AspectRatio_All, 0);
    }
}
