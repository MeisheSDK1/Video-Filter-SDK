package com.meishe.cutsame.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.RatioInfo;
import com.meishe.cutsame.view.CustomTextView;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/1/13 17:07
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class RatioAdapter extends RecyclerView.Adapter<RatioAdapter.RatioViewHolder> {

    private Context mContext;
    private List<RatioInfo> mRatioInfos;
    private OnItemClickListener mOnItemClickListener;
    private int mDefaultRatio;

    public RatioAdapter(Context context, int defaultRatio, List<RatioInfo> ratioInfos) {
        mContext = context;
        mDefaultRatio = defaultRatio;
        mRatioInfos = ratioInfos;
    }

    @NonNull
    @Override
    public RatioAdapter.RatioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomTextView view = new CustomTextView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.dp_px_360)
                , mContext.getResources().getDimensionPixelSize(R.dimen.dp_px_81));
        view.setBackgroundResource(R.color.ffefefef);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(13);
        view.setLayoutParams(params);
        view.setSelected(false);
        return new RatioViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final RatioAdapter.RatioViewHolder holder, int position) {
        final RatioInfo ratioInfo = mRatioInfos.get(position);
        final CustomTextView ratioName = (CustomTextView) holder.itemView;
        ratioName.setText(ratioInfo.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    ratioName.setSelected(true);
                    mOnItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mRatioInfos == null) ? 0 : mRatioInfos.size();
    }

    static class RatioViewHolder extends RecyclerView.ViewHolder {

        public RatioViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
