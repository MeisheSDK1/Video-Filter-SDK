package com.meishe.cutsame.fragment.adapter;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.ExportTemplateTrackMenu;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiuPanFeng
 * @CreateDate: 2020/12/24 15:25
 * @Description: 轨道菜单 适配器
 *              track menu adapter
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateTrackMenuAdapter extends BaseQuickAdapter<ExportTemplateTrackMenu, BaseViewHolder> {

    public ExportTemplateTrackMenuAdapter() {
        super(R.layout.item_export_template_track_menu);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ExportTemplateTrackMenu item) {
        helper.setText(R.id.tv_track_name,item.getTrackName());
    }
}
