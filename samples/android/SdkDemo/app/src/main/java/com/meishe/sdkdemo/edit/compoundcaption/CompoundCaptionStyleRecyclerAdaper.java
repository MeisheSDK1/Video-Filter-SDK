package com.meishe.sdkdemo.edit.compoundcaption;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meishe.base.utils.ImageLoader;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by admin on 2018/5/25.
 */

public class CompoundCaptionStyleRecyclerAdaper extends RecyclerView.Adapter<CompoundCaptionStyleRecyclerAdaper.ViewHolder> {
    private ArrayList<AssetItem> mAssetList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;
    private int mSelectedPos = 0;

    public CompoundCaptionStyleRecyclerAdaper(Context context) {
        mContext = context;
    }

    public void setAssetList(ArrayList<AssetItem> assetArrayList) {
        this.mAssetList = assetArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_asset_caption_style, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void setSelectedPos(int selectedPos) {
        this.mSelectedPos = selectedPos;
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
            holder.mCaptionAssetCover.setImageResource(assetItem.getImageRes());
        } else {
            ImageLoader.Options options = new ImageLoader.Options();
            options.roundedCorners(mContext.getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_18))
                    .placeholder(R.mipmap.default_caption);
            if (!TextUtils.isEmpty(asset.coverUrl) && asset.coverUrl.endsWith(".webp")) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(asset.coverUrl)
                        .setAutoPlayAnimations(true)
                        .setOldController(holder.mCaptionAssetCover.getController())
                        .build();
                holder.mCaptionAssetCover.setController(controller);
            } else {
                ImageLoader.loadUrl(mContext, asset.coverUrl, holder.mCaptionAssetCover, options);
            }
        }
        holder.mCaptionStyleName.setText(asset.name);
        holder.mSelecteItem.setVisibility(mSelectedPos == position ? View.VISIBLE : View.GONE);
        holder.mCaptionStyleName.setTextColor(mSelectedPos == position ? Color.parseColor("#ff4a90e2")
                : Color.parseColor("#ffffffff"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(v, position);
                if (mSelectedPos == position)
                    return;
                notifyItemChanged(mSelectedPos);
                mSelectedPos = position;
                notifyItemChanged(mSelectedPos);
            }
        });
        holder.mCaptionStyleName.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return mAssetList.size();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mCaptionAssetCover;
        View mSelecteItem;
        TextView mCaptionStyleName;

        public ViewHolder(View itemView) {
            super(itemView);
            mCaptionAssetCover = itemView.findViewById(R.id.captionStyleAssetCover);
            mSelecteItem = itemView.findViewById(R.id.selectedItem);
            mCaptionStyleName = (TextView) itemView.findViewById(R.id.captionStyleName);
        }
    }
}
