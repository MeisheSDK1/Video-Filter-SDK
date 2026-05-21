package com.meishe.cutsame.fragment.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.Template;
import com.meishe.base.utils.ImageLoader;
import com.meishe.base.utils.LogUtils;
import com.meishe.http.bean.UserInfo;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;

import java.text.DecimalFormat;


/**
 * author : lhz
 * date   : 2020/11/4
 * desc   :模板列表适配器,注意因为效果图仅仅是图片大小比例不同，所以用了BaseQuickAdapter，没有用
 * BaseMultiItemQuickAdapter
 * Template list adapter, note that since renderings are only image size and scale differences, using the BaseQuickAdapter does not work
 * BaseMultiItemQuickAdapter
 */
public class TemplateAdapter extends BaseQuickAdapter<Template, BaseViewHolder> {
    private int mItemWidth;
    private ImageLoader.Options mCircleOptions;
    private ImageLoader.Options mRoundCornerOptions;

    public TemplateAdapter(int itemWidth) {
        super(R.layout.item_template);
        this.mItemWidth = itemWidth;
        mCircleOptions = new ImageLoader.Options().circleCrop().centerCrop();
        mRoundCornerOptions = new ImageLoader.Options().centerCrop().roundedCorners(15);
    }

    /**
     * Convert.
     * 转换
     *
     * @param helper the helper  helper
     * @param item   the item 条目
     */
    @Override
    protected void convert(@NonNull BaseViewHolder helper, final Template item) {
//            helper.setVisible(R.id.tv_used_num, false);
        if (item.getUseNum() == -1) {
            helper.setVisible(R.id.tv_used_num, false);
        } else {
            helper.setText(R.id.tv_used_num, String.format(mContext.getString(R.string.template_used_num), formatNumber(item.getUseNum())));
        }
        helper.setText(R.id.tv_template_name, item.getDisplayName());
        helper.setText(R.id.tv_description, item.getDescription());
        UserInfo userInfo = item.getUserInfo();
        if (userInfo != null) {
            if (!TextUtils.isEmpty(userInfo.getNickname())) {
                helper.setText(R.id.tv_user_name, userInfo.getNickname());
            } else {
                helper.setText(R.id.tv_user_name, R.string.cut_ms_template);
            }
            if (!TextUtils.isEmpty(userInfo.getIconUrl())) {
                ImageLoader.loadUrl(mContext, Uri.parse(userInfo.getIconUrl()), (ImageView) helper.getView(R.id.iv_portrait), mCircleOptions);
            }
        }else {
            helper.setText(R.id.tv_user_name, R.string.cut_ms_template);
        }
        final ImageView cover = helper.getView(R.id.iv_cover);
        setLayoutParams(cover, item);
        cover.post(new Runnable() {
            @Override
            public void run() {
                String coverUrl = item.getCoverUrl();
                if (!TextUtils.isEmpty(coverUrl)) {
                    ImageLoader.loadUrl(mContext, Uri.parse(coverUrl.startsWith("http") ? coverUrl : "file://" + coverUrl), cover, mRoundCornerOptions);
                } else {
                    LogUtils.e("coverUrl is null !!");
                }
            }
        });
    }

    /**
     * 更改布局参数
     * 注意：由于瀑布流各个item大小可能不同，所以在convert做处理，如果非瀑布流不要在convert做处理。
     * Change layout parameters
     * Note: Since waterfall streams may vary in item size, do this at Convert, but not at Convert if not waterfall streams.
     */
    private void setLayoutParams(View view, Template item) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int height = (int) (mItemWidth * item.getRatio());
        if (params != null && params.height != height) {
            params.width = mItemWidth;
            params.height = height;
            view.setLayoutParams(params);
            RelativeLayout parent = (RelativeLayout) view.getParent();
            ViewGroup.LayoutParams parentParams = parent.getLayoutParams();
            parentParams.height = (int) (params.height + mContext.getResources().getDimension(R.dimen.dp_px_270));
            parent.setLayoutParams(parentParams);
        }
    }

    private DecimalFormat mDecimalFormat;

    private String formatNumber(int number) {
        if (mDecimalFormat == null) {
            mDecimalFormat = new DecimalFormat("0.0");
        }
        if (number < 10000) {
            return number + "";
        }
        return mDecimalFormat.format(number / 10000f) + mContext.getString(R.string.num_unit_w);
    }
}
