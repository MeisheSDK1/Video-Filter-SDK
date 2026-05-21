package com.meishe.sdkdemo.edit.audio.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.audio.view.AudioEqualizerAdjustItemView;
import com.meishe.sdkdemo.edit.data.AudioEqualizerItem;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/1/11 14:21
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class AudioEqualizerAdapter  extends RecyclerView.Adapter<AudioEqualizerAdapter.ViewHolder> {
    private List<AudioEqualizerItem> audioEqualizerItems;
    private Context mContext;
    private AudioEqualizerAdjustItemView.onItemProgressChangeListener itemChangedListener;
    public AudioEqualizerAdapter(Context context) {
        mContext = context;
    }
    public void setData(List<AudioEqualizerItem> audioEqualizerItems){
        this.audioEqualizerItems = audioEqualizerItems;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_audio_equalizer_adjust, viewGroup, false);
        AudioEqualizerAdapter.ViewHolder viewHolder = new AudioEqualizerAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.audioEqualizerAdjustItemView.setAudioEqualizerItem(audioEqualizerItems.get(i));
        viewHolder.audioEqualizerAdjustItemView.setOnItemProgressChangeListener(itemChangedListener);
    }

    @Override
    public int getItemCount() {
        return audioEqualizerItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AudioEqualizerAdjustItemView audioEqualizerAdjustItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            audioEqualizerAdjustItemView = (AudioEqualizerAdjustItemView)itemView.findViewById(R.id.audio_adjust_item);
        }
    }

    public void setOnAudioItemChangedListener(AudioEqualizerAdjustItemView.onItemProgressChangeListener itemChangedListener){
        this.itemChangedListener = itemChangedListener;
    }
}
