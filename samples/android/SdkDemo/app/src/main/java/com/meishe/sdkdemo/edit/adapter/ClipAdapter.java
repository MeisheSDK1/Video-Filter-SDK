package com.meishe.sdkdemo.edit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.anim.AnimationClipAdapter;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;

import java.util.ArrayList;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/3/5.
 * @Description :说明。English class
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ClipAdapter extends AnimationClipAdapter {
    public ClipAdapter(Context context) {
        super(context);
    }

    @Override
    public AnimationClipAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mask_clip, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


}
