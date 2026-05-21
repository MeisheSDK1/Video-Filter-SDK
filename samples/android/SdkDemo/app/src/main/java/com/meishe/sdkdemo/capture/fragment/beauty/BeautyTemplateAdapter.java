package com.meishe.sdkdemo.capture.fragment.beauty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.meishe.base.constants.BaseConstants;
import com.meishe.base.utils.ImageLoader;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capture.bean.BeautyTemplateInfo;
import com.meishe.sdkdemo.capturescene.view.CircleBarView;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;
import com.meishe.utils.DrawableUitls;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 15:43
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyTemplateAdapter extends BaseQuickAdapter<BeautyTemplateInfo, BaseViewHolder> {
    private final ImageLoader.Options mRoundCornerOptions;
    private final Drawable mSelectBg;
    protected int mSelectPosition = -1;

    public BeautyTemplateAdapter(Context context) {
        super(R.layout.item_beauty_template);
        int roundRadius = context.getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_24);
        mRoundCornerOptions = new ImageLoader.Options().centerCrop().roundedCorners(roundRadius);
        mSelectBg = DrawableUitls.getRadiusDrawable(roundRadius
                , context.getResources().getColor(com.meishe.base.R.color.color_D9A5CFFF));
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BeautyTemplateInfo item) {
        int position = helper.getAdapterPosition();
        ImageView icon = helper.getView(R.id.bt_icon);
        ImageLoader.loadUrl(mContext, item.getCoverUrl(), icon, mRoundCornerOptions);
        helper.setBackgroundDrawable(R.id.bt_selected, mSelectBg);
        helper.setText(R.id.bt_title, item.getDisplayName());
        helper.setVisible(R.id.bt_selected, (position == mSelectPosition));

        CircleBarView mBtDownload = helper.getView(R.id.bt_download);
        mBtDownload.setVisibility((item.getDownloadStatus() != BaseConstants.RES_STATUS_DOWNLOAD_ALREADY) ? View.VISIBLE : View.GONE);
        mBtDownload.setCBProgress(item.getDownloadProgress());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectPosition(int position) {
        if (position > getData().size()) {
            return;
        }
        mSelectPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }
}
