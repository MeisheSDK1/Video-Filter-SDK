package com.meishe.sdkdemo.urledit.adapter;

import android.net.Uri;
import android.widget.ImageView;

import com.meishe.base.utils.ImageLoader;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;
import com.meishe.third.adpater.BaseViewHolder;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/6 15:27
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class UrlMaterialVideoAdapter extends UrlMaterialAdapter {

    public UrlMaterialVideoAdapter(int itemWidth) {
        super(itemWidth, R.layout.item_url_material_video);
    }

    @Override
    protected void convertHolder(BaseViewHolder helper, UrlMaterialInfo item) {
        final ImageView cover = helper.getView(R.id.iv_url_video_cover);
        helper.setChecked(R.id.cb_url_video_select, item.isSelected());
        helper.setVisible(R.id.cb_url_video_select, isShowSelected);
        helper.addOnClickListener(R.id.iv_url_video_copy);
        setLayoutParams(cover, item);
        cover.post(new Runnable() {
            @Override
            public void run() {
                String coverUrl = item.getCoverUrl();
                ImageLoader.loadUrl(mContext, Uri.parse(coverUrl.startsWith("http") ? coverUrl : "file://" + coverUrl), cover, mRoundCornerOptions);
            }
        });
    }

}
