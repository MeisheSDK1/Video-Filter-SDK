package com.meishe.sdkdemo.douvideo.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.edit.filter.AssetHelper;
import com.meishe.sdkdemo.edit.view.CircleProgressBar;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.util.ArrayList;

public class FilterFxAdapter extends RecyclerView.Adapter<FilterFxAdapter.ViewHolder> implements View.OnLongClickListener {
    private ArrayList<AssetItem> mAssetDataList = new ArrayList<>();
    private OnItemLongPressListener m_listener;
    private Context mContext;
    RequestOptions options = new RequestOptions();
    private OnFilterItemClickListener mFilterItemClickListener;

    public void setFilterItemClickListener(OnFilterItemClickListener filterItemClickListener) {
        this.mFilterItemClickListener = filterItemClickListener;
    }

    public void setOnItemLongPressListener (OnItemLongPressListener listener){
        m_listener = listener;
    }

    public ArrayList<AssetItem> getAssetDataList() {
        return mAssetDataList;
    }

    public void setAssetDataList(ArrayList<AssetItem> assetDataList) {
        this.mAssetDataList = assetDataList;
        for (int i = 0; i < mAssetDataList.size(); i++) {
            mAssetDataList.get(i).setAssetFilterNameId(AssetHelper.getAssetKeyId(mAssetDataList.get(i).getAsset().name));
        }
    }


    public FilterFxAdapter(Context context) {
        mContext = context;
        options.circleCrop();
        options.placeholder(R.mipmap.default_filter);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.douvideo_filter_fx_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setOnLongClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        AssetItem assetItem = mAssetDataList.get(position);
        if(assetItem == null)
            return;
        final NvAsset asset = assetItem.getAsset();
        if(asset == null)
            return;
        String imageUrl = asset.coverUrl;
        if (!TextUtils.isEmpty(asset.coverUrl) && asset.coverUrl.endsWith(".webp")) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(asset.coverUrl)
                    .setAutoPlayAnimations(true)
                    .setOldController(holder.mImageView.getController())
                    .build();
            holder.mImageView.setController(controller);
        }else if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(imageUrl)
                    .apply(options)
                    .into(holder.mImageView);
        }
        if(asset.isUsable()){
            setViewVisible(holder,View.GONE,View.GONE,View.GONE);
        }else {
            if(asset.downloadStatus == NvAsset.DownloadStatusFinished){
                setViewVisible(holder,View.GONE,View.GONE,View.GONE);
            }else if(asset.downloadStatus == NvAsset.DownloadStatusInProgress){
                setViewVisible(holder,View.VISIBLE,View.GONE,View.VISIBLE);
                holder.mCircleProgressBar.drawProgress(asset.downloadProgress);
            }else {
                setViewVisible(holder,View.VISIBLE,View.VISIBLE,View.GONE);
            }
        }

        String name = asset.name;
        if (name != null) {
            int res = assetItem.getAssetFilterNameId();
            if(res>0){
                holder.mTextView.setText(res);
            }else {
                holder.mTextView.setText(asset.name);
            }
        }
        holder.itemView.setTag(position);
        holder.mDownloadAssetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFilterItemClickListener != null)
                    mFilterItemClickListener.onItemDownload(v, position);
            }
        });

        holder.mDownloadShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!asset.isUsable()){
                    holder.mDownloadAssetButton.callOnClick();
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mAssetDataList.size();
    }

    @Override
    public boolean onLongClick(View view) {
        if(m_listener != null){
            m_listener.onItemLongPress(view, (int) view.getTag());
        }

        return false;
    }

    private void setViewVisible(final ViewHolder holder,
                                int shadowVisibility,
                                int downloadAssetVisibility,
                                int progressBarVisibility){
        holder.mDownloadShadow.setVisibility(shadowVisibility);
        holder.mDownloadAssetButton.setVisibility(downloadAssetVisibility);
        holder.mCircleProgressBar.setVisibility(progressBarVisibility);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        SimpleDraweeView mImageView;
        View mView;
        View mDownloadShadow;
        ImageView mDownloadAssetButton;
        CircleProgressBar mCircleProgressBar;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (SimpleDraweeView) itemView.findViewById(R.id.image_view);
            mTextView = (TextView) itemView.findViewById(R.id.text_view);
            mView = itemView.findViewById(R.id.layer);
            mDownloadShadow = itemView.findViewById(R.id.assetDownloadShadow);
            mDownloadAssetButton = (ImageView) itemView.findViewById(R.id.downloadAssetButton);
            mCircleProgressBar = (CircleProgressBar)itemView.findViewById(R.id.circleProgressBar);
        }
    }

    public interface OnItemLongPressListener{
        void onItemLongPress(View view, int pos);
    }
    public interface OnFilterItemClickListener{
        void onItemDownload(View view,int position);
        //void onItemClick(View view,int position);
    }
}

