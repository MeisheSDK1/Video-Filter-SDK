package com.meicam.effectsdkdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.data.StickerJsonParseData;

import java.util.ArrayList;

/**
 * @author cdv
 */
public class StickerItemAdapter extends RecyclerView.Adapter<StickerItemAdapter.ViewHolder> {
    private ArrayList<StickerJsonParseData.StickerJsonFileInfo> mDataList;
    private int mSelectedPos = 0;
    private Context mContext;
    private OnItemClickListener mClickListener;
    RequestOptions mOptions = new RequestOptions( );

    public StickerItemAdapter(Context context) {
        mContext = context;
        mOptions.fitCenter( );
        mOptions.skipMemoryCache(false);
        mOptions.placeholder(R.mipmap.none);
    }

    public void setDataList(ArrayList<StickerJsonParseData.StickerJsonFileInfo> dataList) {
        this.mDataList = dataList;
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectedPos = selectPosition;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_filter_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (mDataList == null || mDataList.isEmpty( )) {
            return;
        }
        int count = mDataList.size( );
        if (position < 0 || position >= count) {
            return;
        }
        StickerJsonParseData.StickerJsonFileInfo assetInfoData = mDataList.get(position);
        if (assetInfoData == null) {
            return;
        }
        String coverPath = assetInfoData.getDefaultCoverName( );
        if (mSelectedPos == position) {
            holder.selectedRect.setVisibility(View.VISIBLE);
            holder.selectedRect.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_shape_select));
            int selectNameColor = mContext.getResources( ).getColor(R.color.beautyTabSelected);
            holder.nameText.setTextColor(selectNameColor);
        } else {
            holder.selectedRect.setVisibility(View.GONE);
            int unselectNameColor = mContext.getResources( ).getColor(R.color.beautyTabUnSelected);
            holder.nameText.setTextColor(unselectNameColor);
        }
        int coverImageId = assetInfoData.getCoverImageId( );
        if (coverImageId > 0) {
            holder.coverImage.setImageResource(coverImageId);
        } else {
            Glide.with(mContext)
                    .asBitmap( )
                    .load(coverPath)
                    .apply(mOptions)
                    .into(holder.coverImage);
        }

        String name = assetInfoData.getName( );
        holder.nameText.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (mSelectedPos == position) {
                    return;
                }
                notifyItemChanged(mSelectedPos);
                mSelectedPos = position;
                notifyItemChanged(mSelectedPos);
                if (mClickListener != null) {
                    mClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDataList == null || mDataList.isEmpty( )) {
            return 0;
        }
        return mDataList.size( );
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View selectedRect;
        private ImageView coverImage;
        private TextView nameText;

        public ViewHolder(View view) {
            super(view);
            selectedRect = view.findViewById(R.id.selectedRect);
            coverImage = (ImageView) view.findViewById(R.id.coverImage);
            nameText = (TextView) view.findViewById(R.id.nameText);
        }
    }
}
