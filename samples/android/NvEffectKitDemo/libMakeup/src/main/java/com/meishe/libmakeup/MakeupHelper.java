package com.meishe.libmakeup;

import com.meishe.libmakeup.bean.MakeupListData;
import com.meishe.nveffectkit.NveEffectKit;
import com.meishe.nveffectkit.makeup.NveComposeMakeup;
import com.meishe.nveffectkit.makeup.NveMakeup;
import com.meishe.nveffectkit.makeup.NveSingleMakeUp;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/23 16:29
 * @Description :美妆帮助类 Beauty makeup helper
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MakeupHelper {
    private static final String TAG = "MakeupHelper";
    private NveMakeup mNveMakeupController;

    public MakeupHelper() {
        mNveMakeupController = new NveMakeup();
        mNveMakeupController.setEnable(true);
        NveEffectKit.getInstance().setMakeup(mNveMakeupController);
    }

    /**
     * 应用美妆
     * Apply the beauty makeup about ar scene
     *
     * @param NveComposeMakeup the makeup info
     */
    public void applyCaptureMakeupFx(MakeupListData NveComposeMakeup) {
        applyMakeupFx(NveComposeMakeup, false);
    }

    /**
     * 重置拍摄的美妆,实际就是取反或者置空
     * Reset the capture beauty makeup
     *
     * @param NveComposeMakeup the makeup info
     */
    public void resetCaptureMakeupFx(MakeupListData NveComposeMakeup) {
        applyMakeupFx(NveComposeMakeup, true);
    }

    /**
     * 应用美妆
     * Apply the beauty makeup
     *
     * @param makeupListData the makeup info 美妆参数信
     * @param reset          reset fx param  是否重置，重置是取反或者置空对应的美妆参数信息
     */
    public void applyMakeupFx(MakeupListData makeupListData, boolean reset) {
        if (makeupListData == null) {
            return;
        }
        NveSingleMakeUp singleMakeUp = makeupListData.getNvMakeup().getSingleMakeUp(makeupListData.getType());
        NveComposeMakeup composeMakeup = makeupListData.getNvMakeup().getComposeMakeup();
        if (singleMakeUp != null) {
            if (reset) {
                mNveMakeupController.removeSingleMakeUp(makeupListData.getType());
            } else {
                mNveMakeupController.addSingleMakeUp(makeupListData.getType(), singleMakeUp);
            }
        }
        if (composeMakeup != null) {
            if (reset) {
                mNveMakeupController.removeComposeMakeUp();
            } else {
                mNveMakeupController.useComposeMakeup(composeMakeup);
            }
        }
    }

    public void onResume() {
        if (null != mNveMakeupController) {
            NveEffectKit.getInstance().setMakeup(mNveMakeupController);
        }
    }
}
