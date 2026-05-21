package com.meishe.sdkdemo.utils;

import android.content.Context;
import android.text.TextUtils;

import com.meishe.arscene.BeautyDataManager;
import com.meishe.arscene.bean.BeautyFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.makeup.makeup.BaseParam;
import com.meishe.makeup.makeup.BeautyParam;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupParamContent;
import com.meishe.sdkdemo.R;

import java.util.HashSet;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/12/8 15:18
 * @Description: 数据转换工具类
 * Data conversion tool class
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class DataConvertUtils {
    /**
     * 美妆转美颜
     * Make up for beauty
     * @param makeupInfo 美妆信息 Beauty information
     * @param beautyInfo 美颜信息 Beauty information
     */
    public static BeautyFxInfo makeupToBeauty(Context context, Makeup makeupInfo, BeautyFxInfo beautyInfo) {
        if (null == makeupInfo) {
            return null;
        }
        MakeupParamContent content = makeupInfo.getEffectContent();
        if (null == content) {
            return null;
        }
        if (null == beautyInfo) {
            beautyInfo = new BeautyFxInfo();
            beautyInfo.setBeautys(new HashSet<>());
        }
        HashSet<IFxInfo> resultFx = beautyInfo.getBeautys();
        List<BeautyParam> beautyParams = content.getBeautyParams();
        //美肤 Beauty of skin
        if ((null != beautyParams) && !beautyParams.isEmpty()) {
            //获取美肤数据 Get beauty data
            List<IFxInfo> beautys = BeautyDataManager.getBeautyList(context, true);
            //获取磨皮数据 Obtain the peeling data
            List<IFxInfo> beautySkins = BeautyDataManager.getBuffingSkin(context);
            for (BeautyParam beautyParam : beautyParams) {
                if ((null == beautyParam) || !beautyParam.canReplace()) {
                    continue;
                }
                //校色 Color correction
                String type = beautyParam.getType();
                if (TextUtils.equals(type, "ColorCorrect")) {
                    for (IFxInfo info : beautys) {
                        if (TextUtils.equals(info.getName(), context.getResources().getString(R.string.correctionColor))) {
                            info.setCanReplace(beautyParam.canReplace());
                            info.setStrength(beautyParam.getValue());
                            resultFx.add(info);
                            beautyInfo.setOpenSkin(true);
                            beautyInfo.setOpenAdjustColor(true);
                            break;
                        }
                    }
                    continue;
                }
                List<BaseParam.Param> params = beautyParam.getParams();
                if ((null == params) || params.isEmpty()) {
                    continue;
                }
                for (BaseParam.Param param : params) {
                    String key = param.getKey();
                    //磨皮 skinning
                    //美白 whitening
                    IFxInfo info = null;
                    if (TextUtils.equals(key, FxParams.WHITENING_LUT_FILE)) {
                        String value = (String) param.getValue();
                        info = (TextUtils.isEmpty(value))
                                ? BeautyDataManager.getWhiteningA(context)
                                : BeautyDataManager.getWhiteningB(context);
                        beautyInfo.setBeautyType((TextUtils.isEmpty(value)) ? FxParams.BEAUTY_A : FxParams.BEAUTY_B);
                    }
                    if ((null != info)
                            && TextUtils.equals(info.getFxName(), FxParams.BEAUTY_WHITENING)
                            && TextUtils.equals(key, FxParams.BEAUTY_WHITENING)) {
                        info.setCanReplace(beautyParam.canReplace());
                        info.setStrength((Double) param.getValue());
                        resultFx.add(info);
                        beautyInfo.setOpenSkin(true);
                        break;
                    }
                    //红润 ruddy
                    if (TextUtils.equals(key, FxParams.WHITENING_REDDENING)) {
                        for (IFxInfo info1 : beautys) {
                            if (TextUtils.equals(info1.getFxName(), FxParams.WHITENING_REDDENING)) {
                                info1.setCanReplace(beautyParam.canReplace());
                                info1.setStrength((Double) param.getValue());
                                resultFx.add(info1);
                                beautyInfo.setOpenSkin(true);
                                break;
                            }
                        }
                        break;
                    }
                    //锐度 sharpness
                    if (TextUtils.equals(key, FxParams.DEFAULT_SHARPEN_ENABLE)) {
                        for (IFxInfo info1 : beautys) {
                            if (TextUtils.equals(info1.getFxName(), FxParams.DEFAULT_SHARPEN_ENABLE)) {
                                info1.setCanReplace(beautyParam.canReplace());
                                resultFx.add(info1);
                                beautyInfo.setOpenSkin(true);
                                beautyInfo.setOpenSharpen((Boolean) param.getValue());
                                break;
                            }
                        }
                        break;
                    }


                }

            }
        }
        //美型 Beauty type
        List<BaseParam> shapeParams = content.getShapeParams();
        List<IFxInfo> shapes = BeautyDataManager.getBeautyShapeList(context);
        packageShapeData(FxParams.BEAUTY_FACE, shapeParams, shapes, beautyInfo, resultFx);
        //微整形 microshaping
        List<BaseParam> microShapeParams = content.getMicroShapeParams();
        List<IFxInfo> microShapes = BeautyDataManager.getMicroPlasticList(context);
        packageShapeData(FxParams.BEAUTY_SMALL, microShapeParams, microShapes, beautyInfo, resultFx);
        return beautyInfo;
    }

    /**
     * 组装美型，微整形数据
     * Assemble beauty type, micro shaping data
     * @param fxType      type
     * @param shapeParams 美妆中美型，微整形数据 Beauty beauty type, micro plastic data
     * @param shapes      美型，微整形数据 Beauty type, micro plastic data
     * @param beautyInfo  beautyInfo
     * @param resultFx    resultFx
     */
    private static void packageShapeData(int fxType, List<BaseParam> shapeParams, List<IFxInfo> shapes, BeautyFxInfo beautyInfo, HashSet<IFxInfo> resultFx) {
        if ((null == shapeParams) || shapeParams.isEmpty()) {
            return;
        }
        for (BaseParam shapeParam : shapeParams) {
            String type = shapeParam.getType();
            if (TextUtils.isEmpty(type)) {
                continue;
            }
            for (IFxInfo info : shapes) {
                String fxName = info.getFxName();
                if (TextUtils.isEmpty(fxName)) {
                    continue;
                }
                if (fxName.contains(type)) {
                    //获取strength Gain strength
                    List<BaseParam.Param> params = shapeParam.getParams();
                    if ((null != params) && !params.isEmpty()) {
                        for (BaseParam.Param param : params) {
                            if (null == param) {
                                continue;
                            }
                            if (TextUtils.equals(param.getKey(), fxName) && TextUtils.equals(param.getType(), "float")) {
                                double value = (double) param.getValue();
                                info.setStrength(value);
                                break;
                            }
                        }
                    }
                    if (fxType == FxParams.BEAUTY_FACE) {
                        beautyInfo.setOpenFace(true);
                    }
                    if (fxType == FxParams.BEAUTY_SMALL) {
                        beautyInfo.setOpenSmall(true);
                    }
                    info.setCanReplace(shapeParam.canReplace());
                    resultFx.add(info);
                    break;
                }
            }
        }
    }

}
