package com.meishe.sdkdemo.capture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.base.utils.ImageLoader;
import com.meishe.base.view.RoundImageView;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;


public class MakeupAdapter extends RecyclerView.Adapter<MakeupAdapter.ViewHolder> {
    private List<Makeup> mDataList;
    private int mSelectedPos = Integer.MAX_VALUE;
    private Context mContext;
    private OnItemClickListener mClickListener;
    private boolean mIsEnable = true;
    private ImageLoader.Options mOptions;

    @SuppressLint("CheckResult")
    public MakeupAdapter(Context context, ArrayList<Makeup> dataList) {
        mContext = context;
        mDataList = dataList;
        mOptions = new ImageLoader.Options().placeholder(R.mipmap.icon_thumbnail_round).error(R.mipmap.icon_thumbnail_round);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_make_up, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        final int position = pos;
        Makeup item = mDataList.get(position);
        String path = item.getCover();
        if (position == 0) {
            ImageLoader.loadUrl(mContext, R.mipmap.icon_bg_none, holder.makeup_imageAsset, mOptions);
        } else {
            ImageLoader.loadUrl(mContext, path, holder.makeup_imageAsset, mOptions);
        }

        holder.makeup_text.setText(item.getName());
        boolean isSelected = ((mSelectedPos == position) && (position != 0));
        holder.makeup_imageMask.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        holder.makeup_item_layout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (!mIsEnable) {
                    return;
                }
                if (mClickListener != null) {
                    mSelectedPos = position;
                    notifyDataSetChanged();
                    mClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(List<Makeup> data) {
        this.mDataList = data;
        notifyDataSetChanged();
    }

    public List<Makeup> getDataList() {
        return mDataList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEnable(boolean enable) {
        mIsEnable = enable;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectPos(int pos) {
        mSelectedPos = pos;
        notifyDataSetChanged();
    }

    public int getSelectPos() {
        return mSelectedPos;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public Makeup getSelectItem() {
        if (mDataList != null && mSelectedPos >= 0 && mSelectedPos < mDataList.size()) {
            return mDataList.get(mSelectedPos);
        }
        return null;
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout makeup_item_layout;
        private RoundImageView makeup_imageAsset;
        private RoundImageView makeup_imageMask;
        private TextView makeup_text;

        public ViewHolder(View view) {
            super(view);
            makeup_item_layout = view.findViewById(R.id.makeup_item_layout);
            makeup_imageAsset = view.findViewById(R.id.makeup_imageAsset);
            makeup_imageMask = view.findViewById(R.id.makeup_icon_mask);
            makeup_text = view.findViewById(R.id.makeup_text);
        }
    }

}
