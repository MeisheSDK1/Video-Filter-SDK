package com.meishe.sdkdemo.edit.theme;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.FilterItem;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yyj on 2017/12/19 0019.
 */

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {
    private Context mContext;
    private OnItemClickListener mClickListener;
    private ArrayList<FilterItem> mThemeDataList = new ArrayList<>();
    private int mSelectPos = 0;
    RequestOptions mOptions = new RequestOptions();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public ThemeAdapter(Context context) {
        mContext = context;
        mOptions.centerCrop();
        mOptions.skipMemoryCache(false);
        mOptions.placeholder(R.mipmap.default_filter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setThemeDataList(ArrayList<FilterItem> themeDataList) {
        this.mThemeDataList = themeDataList;
    }

    public void setSelectPos(int pos) {
        this.mSelectPos = pos;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item_assetLayout;
        private ImageView item_assetImage;
        private TextView item_assetName;
        private View item_selected;

        public ViewHolder(View view) {
            super(view);
            item_assetLayout = (RelativeLayout) view.findViewById(R.id.layoutAsset);
            item_assetName = (TextView) view.findViewById(R.id.nameAsset);
            item_assetImage = (ImageView) view.findViewById(R.id.imageAsset);
            item_selected = view.findViewById(R.id.imageAsset_selected);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int mPosition = position;
        FilterItem itemData = mThemeDataList.get(mPosition);
        holder.item_assetName.setText(itemData.getFilterName());
        int filterMode = itemData.getFilterMode();
        if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
            int imageId = itemData.getImageId();
            if (imageId != 0)
                holder.item_assetImage.setImageResource(imageId);
        } else {
            String imageUrl = itemData.getImageUrl();
            if (imageUrl != null) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(imageUrl)
                        .apply(mOptions)
                        .into(holder.item_assetImage);
            }
        }

        if (mSelectPos == mPosition) {
            holder.item_selected.setVisibility(View.VISIBLE);
            holder.item_assetName.setTextColor(Color.parseColor("#994a90e2"));
        } else {
            holder.item_selected.setVisibility(View.GONE);
            holder.item_assetName.setTextColor(Color.parseColor("#CCffffff"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, mPosition);
                }
                if (mSelectPos == mPosition)
                    return;
                notifyItemChanged(mSelectPos);
                mSelectPos = mPosition;
                notifyItemChanged(mSelectPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mThemeDataList.size();
    }
}
