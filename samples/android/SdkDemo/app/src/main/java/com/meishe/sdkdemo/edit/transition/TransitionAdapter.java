package com.meishe.sdkdemo.edit.transition;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.FilterItem;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :转场菜单-Adapter
 * @Description :Transition-menu-Adapter
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TransitionAdapter extends RecyclerView.Adapter<TransitionAdapter.ViewHolder> {
    private Context mContext;
    private OnItemClickListener mClickListener;
    private List<FilterItem> mFilterList;
    private int mSelectPos = 0;

    public interface OnItemClickListener {

        /**
         * 条目点击
         * Item click
         *
         * @param view
         * @param position
         */
        void onItemClick(View view, int position);

        /**
         * 条目点击，传递新转场信息，更新转场
         * Click on the item, pass the new transition information, and update the transition
         *
         * @param filterItem
         */
        void onResetTransition(FilterItem filterItem);

        /**
         * 重复点击同一个条目(进行参数编辑)
         * Click the same entry repeatedly (for parameter editing)
         */
        void onSameItemClick(int position);
    }

    public void setSelectPos(int pos) {
        this.mSelectPos = pos;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item_assetLayout;
        private RelativeLayout item_coverLayout;
        private ImageView item_assetImage;
        private SimpleDraweeView item_Background;
        private TextView item_assetName;

        public ViewHolder(View view) {
            super(view);
            item_assetLayout = (RelativeLayout) view.findViewById(R.id.layoutAsset);
            item_coverLayout = (RelativeLayout) view.findViewById(R.id.selected_cover_layout);
            item_assetName = (TextView) view.findViewById(R.id.nameAsset);
            item_assetImage = (ImageView) view.findViewById(R.id.imageAsset);
            item_Background = (SimpleDraweeView) view.findViewById(R.id.imageBackground);
        }
    }

    public TransitionAdapter(Context context) {
        mContext = context;
    }

    public void setFilterList(List<FilterItem> filterList) {
        this.mFilterList = filterList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transition, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int mPosition = position;
        FilterItem itemData = mFilterList.get(mPosition);
        if (itemData == null) {
            return;
        }
        if (mPosition == 0) {
            holder.item_assetName.setText("");
        }
        String name = itemData.getFilterDesc();
        if (!TextUtils.isEmpty(name)) {
            if (mSelectPos == mPosition) {
                holder.item_assetName.setTextColor(Color.parseColor("#4A90E2"));
            } else {
                holder.item_assetName.setTextColor(Color.parseColor("#FFFFFF"));
            }
            holder.item_assetName.setText(name);
        }
        int filterMode = itemData.getFilterMode();
        if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
            int imageId = itemData.getImageId();
            if (imageId != 0) {
                holder.item_assetImage.setBackgroundResource(imageId);
                holder.item_assetImage.setVisibility(View.VISIBLE);
            }
        } else {
            holder.item_assetImage.setVisibility(View.GONE);
        }

        String imageUrl = itemData.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            holder.item_Background.setVisibility(View.VISIBLE);
            if (imageUrl.endsWith(".webp")) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(imageUrl)
                        .setAutoPlayAnimations(true)
                        .setOldController(holder.item_Background.getController())
                        .build();
                holder.item_Background.setController(controller);
            } else if ((filterMode == FilterItem.FILTERMODE_BUNDLE) || (filterMode == FilterItem.FILTERMODE_PACKAGE)) {
                RequestOptions options = new RequestOptions();
                options.diskCacheStrategy(DiskCacheStrategy.NONE);
                options.centerCrop();
                Glide.with(mContext)
                        .asBitmap()
                        .load(imageUrl)
                        .apply(options)
                        .into(holder.item_Background);
            }
        } else {
            holder.item_Background.setVisibility(View.GONE);
        }

        if (mSelectPos == mPosition) {
            holder.item_coverLayout.setVisibility(View.VISIBLE);
            holder.item_assetLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_border_select));
        } else {
            holder.item_coverLayout.setVisibility(View.GONE);
            holder.item_assetLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_border_unselect));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectPos == mPosition) {
                    if (mClickListener != null) {
                        mClickListener.onSameItemClick(mSelectPos);
                    }
                    return;
                }

                notifyItemChanged(mSelectPos);
                mSelectPos = mPosition;
                notifyItemChanged(mSelectPos);

                if (mClickListener != null) {
                    mClickListener.onItemClick(view, mPosition);
                    FilterItem item = mFilterList.get(mPosition);
                    mClickListener.onResetTransition(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }
}
