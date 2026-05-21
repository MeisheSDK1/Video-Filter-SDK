package com.meishe.sdkdemo.dialog

import android.content.Context
import android.text.TextUtils
import com.meishe.base.utils.ScreenUtils
import com.meishe.sdkdemo.R
import com.meishe.sdkdemo.bean.DOPermission
import com.meishe.third.pop.XPopup
import com.meishe.third.pop.core.PositionPopupView
import com.meishe.third.pop.enums.PopupAnimation
import kotlinx.android.synthetic.main.layout_permission_tips.view.permission_tips_desc
import kotlinx.android.synthetic.main.layout_permission_tips.view.permission_tips_main
import kotlinx.android.synthetic.main.layout_permission_tips.view.permission_tips_title

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/04/08 15:12
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
class PermissionTipsPop(context: Context) : PositionPopupView(context) {
    companion object {
        fun create(context: Context): PermissionTipsPop {
            return XPopup.Builder(context)
                .hasShadowBg(false)
                .popupAnimation(PopupAnimation.TranslateFromTop)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .isCenterHorizontal(true)
                .offsetY(context.resources.getDimensionPixelOffset(R.dimen.dp_px_120))
                .asCustom(PermissionTipsPop(context))
                    as PermissionTipsPop
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_permission_tips
    }

    fun show(permissionTip: DOPermission) {
        permission_tips_main.text = permissionTip.header
        permission_tips_title.text = permissionTip.title
        permission_tips_desc.text = permissionTip.desc
        if (TextUtils.isEmpty(permissionTip.title)) {
            permission_tips_title.visibility = GONE
        }
        show()
    }

    override fun getPopupWidth(): Int {
        return ScreenUtils.getScreenWidth() - context.resources.getDimensionPixelSize(R.dimen.dp_px_90)
    }

}