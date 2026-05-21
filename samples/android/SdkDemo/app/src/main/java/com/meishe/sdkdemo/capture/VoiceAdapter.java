package com.meishe.sdkdemo.capture;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.bean.voice.ChangeVoiceData;
import com.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;


public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.ViewHolder> {


    private List<ChangeVoiceData> mDataList;
    private int mSelectedPos = 0;
    private Context mContext;
    private OnItemClickListener mClickListener;

    public VoiceAdapter(Context context, List dataList) {
        mContext = context;
        mDataList = dataList;
    }

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

    public ChangeVoiceData getSelectItem() {
        if (mDataList != null && mSelectedPos >= 0 && mSelectedPos < mDataList.size()) {
            return mDataList.get(mSelectedPos);
        }
        return null;
    }

    public void updateDataList(ArrayList dataList) {
        mDataList.clear();
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        final int position = pos;
        ChangeVoiceData item = mDataList.get(position);
        holder.tvName.setText(item.getName());
        String bgColor = item.getBgColor();
        if (TextUtils.isEmpty(bgColor)) {
            String path = item.getBgUrl();
            if (!TextUtils.isEmpty(path)) {
                path = "file:///android_asset/voice/" + path;
                RequestBuilder requestBuilder = Glide.with(mContext.getApplicationContext()).load(path);
                requestBuilder.into(holder.bgItem);
            } else {
                Glide.with(mContext).load(item.getBgRes()).into(holder.bgItem);
            }
        } else {
            holder.bgItem.setBackground(getBackgroundDrawable(bgColor, 5));
            holder.bgItemFrame.setBackground(getBoundDrawable(bgColor, 5));
        }
        if (mSelectedPos == position) {
            holder.bgItemFrame.setVisibility(View.VISIBLE);
        } else {
            holder.bgItemFrame.setVisibility(View.GONE);
        }

        holder.itemMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mSelectedPos = position;
                    notifyDataSetChanged();
                    mClickListener.onItemClick(v, position);
                }
            }
        });
    }

    /**
     * 根据string获取一个自定义的边框背景图
     *Get a custom border background image based on string
     * @param bgColor
     * @return
     */
    private Drawable getBoundDrawable(String bgColor, float cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(cornerRadius);
        drawable.setStroke(ScreenUtils.dip2px(mContext, 1.5f), Color.parseColor(bgColor));
        return drawable;
    }

    private Drawable getBackgroundDrawable(String bgColor, float cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(cornerRadius);
        drawable.setColor(Color.parseColor(bgColor));
        return drawable;
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView bgItemFrame;
        private ImageView bgItem;
        private TextView tvName;
        private View itemMain;

        public ViewHolder(View view) {
            super(view);
            bgItem = view.findViewById(R.id.item_bg_voice);
            bgItemFrame = view.findViewById(R.id.item_bg_voice_frame);
            tvName = view.findViewById(R.id.item_name_voice);
            itemMain = view.findViewById(R.id.item_main);
        }
    }

}
