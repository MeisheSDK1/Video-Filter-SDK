package com.meishe.sdkdemo.urledit.adapter;

import android.net.Uri;
import android.widget.ImageView;

import com.meishe.base.utils.ImageLoader;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.third.adpater.BaseViewHolder;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/6 15:27
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class UrlMaterialMusicAdapter extends UrlMaterialAdapter {

    public UrlMaterialMusicAdapter(int itemWidth) {
        super(itemWidth, R.layout.item_url_material_music);
    }

    @Override
    protected void convertHolder(BaseViewHolder helper, UrlMaterialInfo item) {
        final ImageView cover = helper.getView(R.id.iv_url_music_cover);
        helper.setText(R.id.tv_url_music_name, item.getDisplayName());
        helper.setText(R.id.tv_url_music_duration, Util.formatTimeStrWithUs(item.getDuration() * 1000000));
        helper.addOnClickListener(R.id.iv_url_music_copy);
        String coverUrl = item.getCoverUrl();
        ImageLoader.loadUrl(mContext, Uri.parse(coverUrl.startsWith("http") ? coverUrl : "file://" + coverUrl), cover, mRoundCornerOptions);
    }


}
