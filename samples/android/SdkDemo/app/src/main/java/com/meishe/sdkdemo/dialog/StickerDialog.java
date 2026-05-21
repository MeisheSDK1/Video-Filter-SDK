package com.meishe.sdkdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;

import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/29.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class StickerDialog extends Dialog {

    private RecyclerView stickerRecycler;
    private StickerItemClickListener itemClickListener;
    private NvAssetManager mAssetManager;
    private List<NvAsset> stickers = new ArrayList<>();
    private StickerAdapter stickerAdapter;

    public void setItemClickListener(StickerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public StickerDialog(@NonNull Context context) {
        super(context);
    }

    public StickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fx_sticker);
        stickerRecycler = findViewById(R.id.sticker_recycler);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.gravity = Gravity.BOTTOM;
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(p);
        stickerRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        initAssets();

    }

    private void initAssets() {
        mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.searchLocalAssets(NvAsset.ASSET_ANIMATED_STICKER);
        String bundlePath = "sticker";
        NvAsset nvAsset = new NvAsset();
        nvAsset.coverUrl = "file:///android_asset/arface/084A6EC1-43AB-40EF-BBD5-D83F692B011B/084A6EC1-43AB-40EF-BBD5-D83F692B011B.png";
        stickers.add(nvAsset);
        mAssetManager.searchReservedAssets(NvAsset.ASSET_ANIMATED_STICKER, bundlePath);
        ArrayList<NvAsset> usableAssets = mAssetManager.getUsableAssets(NvAsset.ASSET_ANIMATED_STICKER, NvAsset.AspectRatio_All, 0);
        if (usableAssets != null && usableAssets.size() > 0) {
            for (NvAsset asset : usableAssets) {
                if (asset.isReserved()) {
                    String coverPath = "file:///android_asset/sticker/";
                    coverPath += asset.uuid;
                    coverPath += ".png";
                    /*
                     * 加载assets/sticker文件夹下的图片
                     * Load images in the assets / sticker folder
                     * */
                    asset.coverUrl = coverPath;
                }
            }
            stickers.addAll(usableAssets);
        }
        stickerAdapter = new StickerAdapter(getContext(), stickers);
        stickerRecycler.setAdapter(stickerAdapter);

    }

    public void initStickerData() {
        stickers.clear();
        NvAsset nvAsset = new NvAsset();
        nvAsset.coverUrl = "file:///android_asset/arface/084A6EC1-43AB-40EF-BBD5-D83F692B011B/084A6EC1-43AB-40EF-BBD5-D83F692B011B.png";
        stickers.add(nvAsset);
        List<NvAsset> usableAssets = mAssetManager.getUsableAssets(NvAsset.ASSET_ANIMATED_STICKER, NvAsset.AspectRatio_All, 0);
        if (usableAssets != null && usableAssets.size() > 0) {
            for (NvAsset asset : usableAssets) {
                if (asset.isReserved()) {
                    String coverPath = "file:///android_asset/sticker/";
                    coverPath += asset.uuid;
                    coverPath += ".png";
                    /*
                     * 加载assets/sticker文件夹下的图片
                     * Load images in the assets / sticker folder
                     * */
                    asset.coverUrl = coverPath;
                }
            }
            stickers.addAll(usableAssets);
        }
        stickerAdapter.notifyDataSetChanged();
    }

    public interface StickerItemClickListener {
        //ItemClickListener
        void onItemClick(NvAsset nvAsset);

        void loadMore();

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerViewHolder> {
        private List<NvAsset> data = new ArrayList<>();
        private Context context;
        private int selectId = -1;

        public StickerAdapter(Context context, List<NvAsset> data) {
            this.data = data;
            this.context = context;
        }

        @NonNull
        @Override
        public StickerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_fx_sticker, null);
            return new StickerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(StickerDialog.StickerViewHolder stickerViewHolder, int position) {
            NvAsset nvAsset = data.get(position);
            final int curPosition = position;
            if (selectId == position) {
                stickerViewHolder.clickedBg.setVisibility(View.VISIBLE);
            } else {
                stickerViewHolder.clickedBg.setVisibility(View.GONE);
            }
            String assetCoverUrl = nvAsset.coverUrl;
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.placeholder(R.mipmap.default_sticker);
            Glide.with(context)
                    .asBitmap()
                    .load(assetCoverUrl)
                    .apply(options)
                    .into(stickerViewHolder.stickerBg);
            stickerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (curPosition == 0) {
                        selectId = -1;
                        if (itemClickListener != null) {
                            itemClickListener.loadMore();
                        }
                    } else {
                        selectId = curPosition;
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(data.get(curPosition));
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }
    }

    public class StickerViewHolder extends RecyclerView.ViewHolder {
        private ImageView stickerBg;
        private ImageView clickedBg;

        public StickerViewHolder(View itemView) {
            super(itemView);
            stickerBg = itemView.findViewById(R.id.iv_bg);
            clickedBg = itemView.findViewById(R.id.iv_selected);
        }
    }
}
