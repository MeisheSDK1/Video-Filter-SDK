package com.meishe.sdkdemo.edit.audio.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.bean.EqualizerType;

import java.util.List;
import java.util.Locale;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/1/11 14:21
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class AudioEqualizerTypeAdapter extends RecyclerView.Adapter<AudioEqualizerTypeAdapter.ViewHolder> {
    private List<EqualizerType> audioEqualizerItems;
    private Context mContext;
    private OnItemSelected onItemSelectedListener;
    private int selectedPos = -1;
    public AudioEqualizerTypeAdapter(Context context) {
        mContext = context;
    }
    public void setData(List<EqualizerType> audioEqualizerItems){
        this.audioEqualizerItems = audioEqualizerItems;
        notifyDataSetChanged();
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_audio_equalizer_type_adjust, viewGroup, false);
        AudioEqualizerTypeAdapter.ViewHolder viewHolder = new AudioEqualizerTypeAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        EqualizerType equalizerType = audioEqualizerItems.get(i);
        viewHolder.tvType.setText(isZh(mContext)?equalizerType.getZh_name():equalizerType.getName());
        viewHolder.ivSelected.setVisibility(selectedPos ==i?View.VISIBLE:View.INVISIBLE);
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemSelectedListener.onItemSelected(equalizerType,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioEqualizerItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView tvType;
        ImageView ivSelected;
        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            tvType = itemView.findViewById(R.id.tv_type);
            ivSelected = itemView.findViewById(R.id.iv_selected);
        }
    }

    public void setOnItemSelectedListener(OnItemSelected onItemSelectedListener){
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelected{
        void onItemSelected(EqualizerType type,int position);
    }
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
