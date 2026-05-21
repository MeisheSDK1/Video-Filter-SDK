package com.meishe.sdkdemo.capture.fragment.beauty;

import android.content.Context;
import android.text.TextUtils;

import com.meicam.sdk.NvsFx;
import com.meishe.arscene.BeautyDataManager;
import com.meishe.arscene.BeautyHelper;
import com.meishe.arscene.CustomBeautyHelper;
import com.meishe.arscene.bean.BaseFxInfo;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.base.constants.BaseConstants;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.Utils;
import com.meishe.makeup.MakeupDataManager;
import com.meishe.makeup.MakeupHelper;
import com.meishe.makeup.makeup.BaseParam;
import com.meishe.makeup.makeup.BeautyParam;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupCategory;
import com.meishe.makeup.makeup.MakeupParam;
import com.meishe.makeup.makeup.MakeupParamContent;
import com.meishe.makeup.net.Translation;
import com.meishe.makeup.utils.ParseJsonFile;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capture.bean.BeautyTemplateInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/14 20:51
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyCapturePresenter extends Presenter<BeautyCaptureView> {
    /**
     * 从assets获取模板数据
     * Get template data from assets
     *
     * @param beautyTemplates beautyTemplates
     */
    public void getBeautyTemplateFromAssets(Context mContext, List<BeautyTemplateInfo> beautyTemplates) {
        try {
            //assets资源
            String assets = "beauty/template";
            String[] fileList = mContext.getAssets().list(assets);
            if (fileList.length > 0) {
                for (String str : fileList) {
                    String assetsPath = "assets:/" + assets + File.separator + str;
                    BeautyTemplateInfo templateInfo = new BeautyTemplateInfo();
                    templateInfo.setAssets(true);
                    templateInfo.setDownloadStatus(BaseConstants.RES_STATUS_DOWNLOAD_ALREADY);
                    String fileJson = assets + File.separator + str + File.separator + MakeupDataManager.INFO_NEW_JSON;
                    String readInfo = ParseJsonFile.readAssetJsonFile(mContext, fileJson);
                    if (TextUtils.isEmpty(readInfo)) {
                        continue;
                    }
                    Makeup makeup = ParseJsonFile.fromJson(readInfo, Makeup.class);
                    makeup.setAssetsDirectory(assetsPath);

                    List<Translation> translations = makeup.getTranslation();
                    if (!translations.isEmpty()) {
                        Translation translation = translations.get(0);
                        templateInfo.setDisplayName(translation.getOriginalText());
                        templateInfo.setDisplayNameZhCn(translation.getTargetText());
                    }
                    templateInfo.setMakeup(makeup);
                    beautyTemplates.add(templateInfo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重置美颜模板
     * Reset the beauty template
     *
     * @param mapDatas      data
     * @param mMakeupHelper Makeup Helper
     * @param mBeautyHelper Beauty Helper
     * @param isClear       isClear
     */
    public void resetBeautyTemplate(Context context, HashMap<Integer, List<IFxInfo>> mapDatas
            , MakeupHelper mMakeupHelper, BeautyHelper mBeautyHelper
            , boolean isClear) {
        if ((null == mBeautyHelper) || (null == mMakeupHelper)) {
            return;
        }
        /*
         * 移除肤色滤镜
         * Remove the skin tone filter
         */
        removaAllSkinColour(context, mBeautyHelper);
        /*
         * 重置磨皮
         * Reset dermabrasion
         */
        resetSkinning(mBeautyHelper);
        /*
         * 移除锐度
         * Remove sharpness
         */
        mBeautyHelper.deleteCaptureSharpen();
        /*
         * 移除清晰度
         * Remove definition
         */
        mBeautyHelper.deleteCaptureDefinition();
        if ((null == mapDatas) || mapDatas.isEmpty()) {
            return;
        }
        /*
         * 重置"美白"效果
         * Reset the "Whitening" effect
         */
        resetWhiteningLut(mBeautyHelper, isClear);

        for (Map.Entry<Integer, List<IFxInfo>> entry : mapDatas.entrySet()) {
            int key = entry.getKey();
            List<IFxInfo> value = entry.getValue();
            if ((null == value) || value.isEmpty()) {
                continue;
            }
            /*
             * 重置调整
             * reset adjustment
             */
            if (key == FxParams.BEAUTY_ADJUST) {
                for (IFxInfo info : value) {
                    double tempStrength = info.getStrength();
                    info.setStrength(isClear ? 0 : info.getStrength());
                    String type = info.getType();
                    switch (type) {
                        case FxParams.SHARPEN:
                            mBeautyHelper.applyCaptureSharpenFx(info);
                            break;
                        case FxParams.DEFINITION:
                            mBeautyHelper.applyCaptureDefinitionFx(info);
                            break;
                        default:
                            mBeautyHelper.applyCaptureBeautyFx(info);
                            break;
                    }
                    info.setStrength(tempStrength);
                }
                continue;
            }
            /*
             * 重置修容（单妆）
             * Reset Contouring (Single Makeup)
             */
            if (key == FxParams.BEAUTY_CONTOURING) {
                for (IFxInfo info : value) {
                    double tempStrength = info.getStrength();
                    info.setStrength(isClear ? 0 : info.getStrength());
                    String fxName = info.getFxName();
                    mMakeupHelper.setCaptureMakeupPackageId(fxName, isClear ? "" : info.getPackageId());
                    mMakeupHelper.setCaptureMakeupIntensity(fxName, (float) info.getStrength());
                    info.setStrength(tempStrength);
                }
                continue;
            }
            for (IFxInfo info : value) {
                List<IFxInfo> nodeFxInfos = info.getFxNodes();
                if ((null != nodeFxInfos) && !nodeFxInfos.isEmpty()) {
                    for (IFxInfo nodeInfo : nodeFxInfos) {
                        if ((null == nodeInfo) || !nodeInfo.isSelected()) {
                            continue;
                        }
                        double tempStrength = nodeInfo.getStrength();
                        nodeInfo.setStrength(isClear ? 0 : nodeInfo.getStrength());
                        mBeautyHelper.applyCaptureBeautyFx(nodeInfo);
                        nodeInfo.setStrength(tempStrength);
                    }
                    continue;
                }
                double tempStrength = info.getStrength();
                info.setStrength(isClear ? 0 : info.getStrength());
                mBeautyHelper.applyCaptureBeautyFx(info);
                info.setStrength(tempStrength);
            }
        }
    }

    /**
     * 应用美颜模板
     * 注：每次应用模板时
     * 1.重置上一个模板相关数据（见resetBeautyTemplate方法）
     * 2.安装模板中的相关素材
     * 3.通过MakeupHelper安装效果
     * 4.应用美肤：组装美肤数据，只有肤色项需要应用效果（BeautyHelper）
     * 5.应用美型：组装美型数据
     * 6.应用微整形：组装微整形数据
     * 7.应用调节：组装调节数据，需要应用效果（BeautyHelper）
     * 8.应用单妆：组装单妆数据
     * Apply the beauty template
     * Note: Every time the template is applied
     * 1. Reset the previous template (see the resetBeautyTemplate method)
     * 2. Install materials in the template
     * 3. Install effects via MakeupHelper
     * 4. Apply beauty: Assemble beauty data, only the skin color item needs to apply effect (BeautyHelper)
     * 5. Application of beauty: Assemble beauty data
     * 6. Apply micro-shaping: Assemble micro-shaping data
     * 7. Apply adjustment: Assemble adjustment data, need application effect (BeautyHelper)
     * 8. Apply single makeup: Assemble single makeup data
     *
     * @param mContext           context
     * @param mMakeupHelper      Makeup Helper
     * @param mBeautyHelper      Beauty Helper
     * @param beautyTemplateInfo BeautyTemplateInfo
     */
    public void applyBeautyTemplate(Context mContext, MakeupHelper mMakeupHelper, BeautyHelper mBeautyHelper, BeautyTemplateInfo beautyTemplateInfo) {
        if ((null == mMakeupHelper) || (null == mBeautyHelper) || (null == beautyTemplateInfo)) {
            return;
        }
        Makeup makeup = beautyTemplateInfo.getMakeup();
        if (null == makeup) {
            return;
        }
        boolean isCustomTemplate = beautyTemplateInfo.getTemplateType() == BeautyTemplateInfo.BeautyTemplateType.BeautyTemplate_Custom;
        if (!isCustomTemplate) {
            String makeupPath = makeup.getAssetsDirectory();
            mMakeupHelper.installFxPackage(new File(makeupPath));
            mMakeupHelper.applyCaptureMakeupFx(makeup);
        }
        MakeupParamContent paramContent = makeup.getEffectContent();
        if (null == paramContent) {
            return;
        }

        HashMap<Integer, List<IFxInfo>> mapData = new HashMap<>();
        //美肤 Beauty of skin
        mapData.put(FxParams.BEAUTY_SKIN, applyBeauty(mContext, isCustomTemplate, mBeautyHelper, makeup.getAssetsDirectory(), paramContent.getBeautyParams()));
        //美型 Beauty type
        mapData.put(FxParams.BEAUTY_FACE, applyShape(mContext, mBeautyHelper, makeup.getAssetsDirectory()));
        //微整形 Microshaping
        mapData.put(FxParams.BEAUTY_SMALL, applyMicroShape(mContext, mBeautyHelper, makeup.getAssetsDirectory()));
        //调节 Adjust
        mapData.put(FxParams.BEAUTY_ADJUST, applyAdjust(mContext, mBeautyHelper, makeup.getAssetsDirectory(), paramContent.getAdjustParams()));
        //美妆 Makeup
        mapData.put(FxParams.BEAUTY_CONTOURING, applyMakeup(mContext, mMakeupHelper, makeup.getAssetsDirectory(), paramContent.getMakeupParams()));

        getView().getBeautyData(mapData, isCustomTemplate);
    }

    public void applyBeautyTemplate(BeautyHelper mBeautyHelper) {
        if ((null == mBeautyHelper)) {
            return;
        }

        Map<String, Map<String, FxParams>> beautyFxParams = CustomBeautyHelper.get().getBeautyFxParams();
        Set<Map.Entry<String, Map<String, FxParams>>> entries = beautyFxParams.entrySet();
        for (Map.Entry<String, Map<String, FxParams>> entry : entries) {
            String key = entry.getKey();
            Map<String, FxParams> value = entry.getValue();
            if (value != null) {
                mBeautyHelper.applyBuildInCaptureFx(key, value);
            }
        }
        Map<String, Map<String, FxParams>> packageFxParams = CustomBeautyHelper.get().getPackageFxParams();
        entries = packageFxParams.entrySet();
        for (Map.Entry<String, Map<String, FxParams>> entry : entries) {
            String key = entry.getKey();
            Map<String, FxParams> value = entry.getValue();
            if (value != null) {
                mBeautyHelper.applyPackageCaptureFx(key, value);
            }
        }
    }

    private void resetWhiteningLut(BeautyHelper mBeautyHelper, boolean isClear) {
        NvsFx nvsFx = mBeautyHelper.getCaptureBeautyFx();
        if (null != nvsFx) {
            boolean lutEnable = (boolean) nvsFx.getAttachment(FxParams.WHITENING_LUT_ENABLE);
            String lutFile = (String) nvsFx.getAttachment(FxParams.WHITENING_LUT_FILE);
            double defaultStrength = (double) nvsFx.getAttachment(FxParams.BEAUTY_WHITENING);
            nvsFx.setBooleanVal("Whitening Lut Enabled", isClear ? false : lutEnable);
            nvsFx.setStringVal("Whitening Lut File", isClear ? "" : lutFile);
            nvsFx.setFloatVal("Beauty Whitening", isClear ? 0 : defaultStrength);
        }
    }

    /**
     * 获取美肤数据
     * Get beauty data
     *
     * @param mContext        context
     * @param mBeautyHelper   BeautyHelper
     * @param assetsDirectory Directory
     * @param beautyParams    params
     */
    private List<IFxInfo> applyBeauty(Context mContext, boolean isCustomTemplate, BeautyHelper mBeautyHelper, String assetsDirectory, List<BeautyParam> beautyParams) {
        NvsFx mArScene = mBeautyHelper.getCaptureBeautyFx();
        List<IFxInfo> mBeautyList = new ArrayList<>();

        List<IFxInfo> mSkins = BeautyDataManager.getBeautyList(mContext, true);
        for (IFxInfo info : mSkins) {
            String name = info.getName();
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            if (TextUtils.equals(name, mContext.getResources().getString(R.string.correctionColor))
                    || TextUtils.equals(name, mContext.getResources().getString(R.string.sharpness))
                    || TextUtils.equals(name, mContext.getResources().getString(R.string.whitening_B))) {
                continue;
            }
            mBeautyList.add(info);
            if (null == mArScene) {
                continue;
            }
            //设置应用的去油光数据 Set the degreasing data for the application
            if (TextUtils.equals(info.getFxName(), FxParams.ADVANCED_BEAUTY_MATTE_INTENSITY)) {
                if (info instanceof CompoundFxInfo) {
                    //强度 strength
                    double strength = mArScene.getFloatVal(FxParams.ADVANCED_BEAUTY_MATTE_INTENSITY);
                    info.setStrength(strength);
                    info.setDefaultStrength(strength);
                    CompoundFxInfo compoundFxInfo = (CompoundFxInfo) info;
                    //半径 radius
                    double matteRadius = mArScene.getFloatVal(FxParams.ADVANCED_BEAUTY_MATTE_FILL_RADIUS);
                    FxParams fxParams = compoundFxInfo.findParam(FxParams.ADVANCED_BEAUTY_MATTE_FILL_RADIUS);
                    if (null != fxParams) {
                        fxParams.setValue(matteRadius);
                        fxParams.setDefaultValue(matteRadius);
                    }
                }
                continue;
            }
            //红润 ruddy
            if (TextUtils.equals(info.getFxName(), FxParams.WHITENING_REDDENING)) {
                double strength = mArScene.getFloatVal(FxParams.WHITENING_REDDENING);
                info.setStrength(strength);
                info.setDefaultStrength(strength);
            }
        }
        //设置磨皮数据 Set the peeling data
        if (!mBeautyList.isEmpty()) {
            IFxInfo mBuffingSkinData = mBeautyList.get(0);
            if (null != mBuffingSkinData) {
                mBuffingSkinData.setFxNodes(getBuffingSkinData(mArScene, mContext, beautyParams));
            }
        }
        //设置肤色数据 Set skin color data
        if (!mBeautyList.isEmpty()) {
            BaseFxInfo beautySkin = new BaseFxInfo();
            beautySkin.setName(mContext.getResources().getString(com.meishe.arscene.R.string.skin));
            beautySkin.setResourceId(com.meishe.arscene.R.mipmap.icon_beauty_skin);
            beautySkin.setFxNodes(getSkinData(mContext, isCustomTemplate, mBeautyHelper, assetsDirectory, beautyParams));
            mBeautyList.add(1, beautySkin);
        }

        //设置美白数据 Set whitening data
        if (!mBeautyList.isEmpty()) {
            BaseFxInfo beautySkin = new BaseFxInfo();
            beautySkin.setName(mContext.getResources().getString(R.string.whitening));
            beautySkin.setResourceId(com.meishe.arscene.R.mipmap.ic_strength);
            beautySkin.setFxNodes(getWhiteningSkinData(mArScene, mContext, beautyParams));
            mBeautyList.add(beautySkin);
        }

        if (null != mArScene) {
            mArScene.setAttachment(FxParams.WHITENING_LUT_ENABLE, mArScene.getBooleanVal(FxParams.WHITENING_LUT_ENABLE));
            mArScene.setAttachment(FxParams.WHITENING_LUT_FILE, mArScene.getStringVal(FxParams.WHITENING_LUT_FILE));
            mArScene.setAttachment(FxParams.BEAUTY_WHITENING, mArScene.getFloatVal(FxParams.BEAUTY_WHITENING));
        }
        return mBeautyList;
    }

    /**
     * 获取美肤-磨皮数据
     * Get beauty - dermabrasion data
     *
     * @param mContext Context
     */
    public List<IFxInfo> getBuffingSkinData(NvsFx mArScene, Context mContext, List<BeautyParam> beautyParams) {
        List<IFxInfo> data = BeautyDataManager.getBuffingSkin(mContext);
        data.remove(data.size() - 1);
        if (null == mArScene) {
            return data;
        }
        double type = 0;
        double beautyStrengthValue = 0F;
        double intensityValue = 0F;
        if (null == beautyParams) {
            return data;
        }
        for (BeautyParam beautyParam : beautyParams) {
            if (null == beautyParam) {
                continue;
            }
            List<BaseParam.Param> paramList = beautyParam.getParams();
            if (paramList.isEmpty()) {
                continue;
            }
            BaseParam.Param typeParam = beautyParam.findParam(FxParams.ADVANCED_BEAUTY_TYPE);
            if (null != typeParam) {
                type = (double) typeParam.getValue();
                BaseParam.Param strengthParam = beautyParam.findParam(FxParams.BEAUTY_STRENGTH);
                BaseParam.Param intensityParam = beautyParam.findParam(FxParams.ADVANCED_BEAUTY_INTENSITY);
                if (null != strengthParam) {
                    beautyStrengthValue = (double) strengthParam.getValue();
                }
                if (null != intensityParam) {
                    intensityValue = (double) intensityParam.getValue();
                }
                break;
            }
        }
        for (IFxInfo info : data) {
            if (TextUtils.equals(info.getFxName(), FxParams.BEAUTY_STRENGTH)) {
                if ((beautyStrengthValue > 0) && (type == 0)) {
                    info.setSelected(true);
                    info.setDefaultFx(true);
                    info.setStrength(beautyStrengthValue);
                    info.setDefaultStrength(beautyStrengthValue);
                    break;
                }
                continue;
            }
            CompoundFxInfo compoundFxInfo = (CompoundFxInfo) info;
            FxParams fxParams = compoundFxInfo.findParam(FxParams.ADVANCED_BEAUTY_TYPE);
            if (null == fxParams) {
                continue;
            }
            int value = fxParams.getIntValue();
            if (type == value) {
                info.setSelected(true);
                info.setDefaultFx(true);
                info.setStrength(intensityValue);
                info.setDefaultStrength(intensityValue);
                break;
            }
        }
        return data;
    }


    /**
     * 获取美肤-美白数据
     * Get beauty - whitening data
     *
     * @param mContext Context
     */
    public List<IFxInfo> getWhiteningSkinData(NvsFx mArScene, Context mContext, List<BeautyParam> beautyParams) {
        List<IFxInfo> data = BeautyDataManager.getWhiteningSkin(mContext);
        if (null == mArScene) {
            return data;
        }
        if (null == beautyParams) {
            return data;
        }
        for (BeautyParam beautyParam : beautyParams) {
            if (null == beautyParam) {
                continue;
            }
            List<BaseParam.Param> paramList = beautyParam.getParams();
            if (paramList.isEmpty()) {
                continue;
            }
            BaseParam.Param typeParam = beautyParam.findParam(FxParams.WHITENING_LUT_ENABLE);
            if (null != typeParam) {
                boolean isWhiteningB = false;
                Object value = typeParam.getValue();
                if (value instanceof Boolean) {
                    isWhiteningB = (boolean) value;
                }
                if (isWhiteningB) {
                    BaseParam.Param param = beautyParam.findParam(FxParams.WHITENING_LUT_FILE);
                    CompoundFxInfo iFxInfo = (CompoundFxInfo) data.get(1);
                    List<FxParams> whitenParamList = iFxInfo.getParamList();
                    for (FxParams fxParams : whitenParamList) {
                        if (FxParams.WHITENING_LUT_FILE.equals(fxParams.key)) {
                            fxParams.value = param.getValue();
                        }
                    }
                    BaseParam.Param intensityParam = beautyParam.findParam(FxParams.BEAUTY_WHITENING);
                    if (intensityParam != null) {
                        double strength = (double) intensityParam.getValue();
                        iFxInfo.setStrength(strength);
                        iFxInfo.setDefaultStrength(strength);
                    }
                    iFxInfo.setSelected(true);
                } else {
                    CompoundFxInfo iFxInfo = (CompoundFxInfo) data.get(0);
                    BaseParam.Param intensityParam = beautyParam.findParam(FxParams.BEAUTY_WHITENING);
                    if (intensityParam != null) {
                        double strength = (double) intensityParam.getValue();
                        iFxInfo.setStrength(strength);
                        iFxInfo.setDefaultStrength(strength);
                    }
                    iFxInfo.setSelected(true);
                }
            }
        }
        return data;
    }

    /**
     * 获取美肤-肤色数据
     * Get Beauty - Skin tone data
     *
     * @param mContext context
     * @return list
     */
    public List<IFxInfo> getSkinData(Context mContext, boolean isCustomTemplate, BeautyHelper mBeautyHelper, String assetsDirectory, List<BeautyParam> beautyParams) {
        List<IFxInfo> data = BeautyDataManager.getSkin(mContext);
        if (!isCustomTemplate) {
            for (IFxInfo info : data) {
                info.setStrength(0.6F);
                info.setDefaultStrength(0.6F);
            }
        }
        removaAllSkinColour(mContext, mBeautyHelper);
        int canReplace = 1;
        String packageId = "";
        double skinTypeIndex = -1;
        float value = 0;
        if (null == beautyParams) {
            return data;
        }
        for (BeautyParam beautyParam : beautyParams) {
            String type = beautyParam.getType();
            if (TextUtils.equals(type, FxParams.SKIN_COLOUR)) {
                canReplace = beautyParam.getCanReplace();
                packageId = beautyParam.getPackageId();
                value = beautyParam.getValue();
                List<BaseParam.Param> params = beautyParam.getParams();
                for (BaseParam.Param param : params) {
                    if (TextUtils.equals(param.getKey(), FxParams.SKIN_COLOUR_TYPE)) {
                        skinTypeIndex = (double) param.getValue();
                    }
                }
                break;
            }
        }
        if ((skinTypeIndex < 0) || (skinTypeIndex > data.size())) {
            return data;
        }
        IFxInfo info = data.get((int) skinTypeIndex);
        if (null == info) {
            return data;
        }
        info.setCanReplace(canReplace > 0);
        info.setPackageId(packageId);
        info.setStrength(value);
        info.setSelected(true);
        info.setDefaultStrength(value);
        info.setAssetPackagePath(getPackagePath(assetsDirectory, packageId));
        if (null != mBeautyHelper) {
            mBeautyHelper.applyCaptureBeautyFx(info);
        }
        return data;
    }

    public void removaAllSkinColour(Context mContext, BeautyHelper mBeautyHelper) {
        List<IFxInfo> data = BeautyDataManager.getSkin(mContext);
        for (IFxInfo info : data) {
            mBeautyHelper.removeCapturePackageFx(info.getPackageId());
        }
    }

    public void resetSkinning(BeautyHelper mBeautyHelper) {
        NvsFx nvsFx = mBeautyHelper.getCaptureBeautyFx();
        if (null == nvsFx) {
            return;
        }
        nvsFx.setFloatVal(FxParams.BEAUTY_STRENGTH, 0);
        nvsFx.setFloatVal(FxParams.ADVANCED_BEAUTY_INTENSITY, 0);
    }

    /**
     * 获取美型数据
     * Get beauty data
     *
     * @param context         context
     * @param mBeautyHelper   helper
     * @param assetsDirectory directory
     */
    private List<IFxInfo> applyShape(Context context, BeautyHelper mBeautyHelper, String assetsDirectory) {
        List<IFxInfo> shapeList = BeautyDataManager.getBeautyShapeList(context);
        if (null == mBeautyHelper) {
            return shapeList;
        }
        NvsFx mArScene = mBeautyHelper.getCaptureBeautyFx();
        if (null == mArScene) {
            return shapeList;
        }
        for (IFxInfo info : shapeList) {
            String fxName = info.getFxName();
            if (TextUtils.isEmpty(fxName)) {
                continue;
            }
            double degree = mArScene.getFloatVal(fxName);
            info.setStrength(degree);
            info.setDefaultStrength(degree);
            String newPackageId = "";
            if (info instanceof CompoundFxInfo) {
                CompoundFxInfo fxInfo = (CompoundFxInfo) info;
                List<FxParams> fxParams = fxInfo.getParamList();
                if ((null == fxParams) || fxParams.isEmpty()) {
                    continue;
                }
                for (FxParams params : fxParams) {
                    String key = params.key;
                    String uuid = mArScene.getStringVal(key);
                    if (TextUtils.equals(uuid, params.getStringValue())) {
                        continue;
                    }
                    newPackageId = uuid;
                    if (!TextUtils.isEmpty(newPackageId)) {
                        params.setValue(newPackageId);
                    }
                }
                if (!TextUtils.isEmpty(newPackageId)) {
                    info.setPackageId(newPackageId);
                    info.setAssetPackagePath(getPackagePath(assetsDirectory, newPackageId));
                }
            }
        }
        return shapeList;
    }

    /**
     * 获取微整形数据
     * Obtain microshaping data
     *
     * @param context         context
     * @param mBeautyHelper   helper
     * @param assetsDirectory directory
     */
    private List<IFxInfo> applyMicroShape(Context context, BeautyHelper mBeautyHelper, String assetsDirectory) {
        List<IFxInfo> microShapeList = BeautyDataManager.getMicroPlasticList(context);
        if (null == mBeautyHelper) {
            return microShapeList;
        }
        NvsFx mArScene = mBeautyHelper.getCaptureBeautyFx();
        if (null == mArScene) {
            return microShapeList;
        }
        for (IFxInfo info : microShapeList) {
            String fxName = info.getFxName();
            if (TextUtils.isEmpty(fxName)) {
                continue;
            }
            double degree = mArScene.getFloatVal(fxName);
            info.setStrength(degree);
            info.setDefaultStrength(degree);
            String newPackageId = "";
            if (info instanceof CompoundFxInfo) {
                CompoundFxInfo fxInfo = (CompoundFxInfo) info;
                List<FxParams> fxParams = fxInfo.getParamList();
                if ((null == fxParams) || fxParams.isEmpty()) {
                    continue;
                }
                for (FxParams params : fxParams) {
                    String key = params.key;
                    String uuid = mArScene.getStringVal(key);
                    if (TextUtils.equals(uuid, params.getStringValue())) {
                        continue;
                    }
                    newPackageId = uuid;
                    if (!TextUtils.isEmpty(newPackageId)) {
                        params.setValue(newPackageId);
                    }
                }
                if (!TextUtils.isEmpty(newPackageId)) {
                    info.setPackageId(newPackageId);
                    info.setAssetPackagePath(getPackagePath(assetsDirectory, newPackageId));
                }
            }
        }
        return microShapeList;
    }

    /**
     * 获取调整数据
     * Get adjustment data
     *
     * @param context         context
     * @param mBeautyHelper   helper
     * @param assetsDirectory directory
     * @param adjustParams    params
     */
    private List<IFxInfo> applyAdjust(Context context, BeautyHelper mBeautyHelper, String assetsDirectory, List<BeautyParam> adjustParams) {
        List<IFxInfo> adjustList = BeautyDataManager.getAdjustList(context);
        if ((null == mBeautyHelper) || (null == adjustParams) || adjustParams.isEmpty()) {
            return adjustList;
        }
        for (BeautyParam baseParam : adjustParams) {
            String type = baseParam.getType();
            if (TextUtils.isEmpty(type)) {
                continue;
            }
            for (IFxInfo info : adjustList) {
                if (!TextUtils.equals(type, info.getType())) {
                    continue;
                }
                if (TextUtils.equals(type, FxParams.SHARPEN)
                        || TextUtils.equals(type, FxParams.DEFINITION)) {
                    List<BaseParam.Param> params = baseParam.getParams();
                    if ((null == params) || params.isEmpty()) {
                        break;
                    }
                    for (BaseParam.Param param : params) {
                        double value = (double) param.getValue();
                        info.setCanReplace(baseParam.canReplace());
                        info.setStrength(value);
                        info.setDefaultStrength(value);
                    }
                    if (TextUtils.equals(type, FxParams.SHARPEN)) {
                        mBeautyHelper.applyCaptureSharpenFx(info);
                    } else {
                        mBeautyHelper.applyCaptureDefinitionFx(info);
                    }
                    break;
                }
                info.setCanReplace(baseParam.canReplace());
                info.setStrength(baseParam.getValue());
                info.setDefaultStrength(baseParam.getValue());
                if (!TextUtils.equals(baseParam.getPackageId(), info.getPackageId())) {
                    info.setPackageId(baseParam.getPackageId());
                    info.setAssetPackagePath(getPackagePath(assetsDirectory, baseParam.getPackageId()));
                }
                mBeautyHelper.applyCaptureBeautyFx(info);
            }
        }
        return adjustList;
    }

    /**
     * 获取单妆数据
     * Get single makeup data
     *
     * @param context         context
     * @param mMakeupHelper   Makeup Helper
     * @param assetsDirectory Assets Directory
     * @param makeupParams    params
     * @return List
     */
    private List<IFxInfo> applyMakeup(Context context, MakeupHelper mMakeupHelper, String assetsDirectory, List<MakeupParam> makeupParams) {
        if ((null == makeupParams) || makeupParams.isEmpty()) {
            return null;
        }
        NvsFx mArScene = mMakeupHelper.getCaptureVideoFx();
        if (null == mArScene) {
            return null;
        }
        List<MakeupCategory> makeupCategories = MakeupDataManager.getMakeupCategory(context);
        List<IFxInfo> makeupList = new ArrayList<>();
        for (MakeupParam makeupParam : makeupParams) {
            String type = makeupParam.getType();
            if (TextUtils.isEmpty(type)) {
                continue;
            }
            String name = "None";
            int icon = R.mipmap.icon_none;
            if ((null != makeupCategories) && !makeupCategories.isEmpty()) {
                for (MakeupCategory category : makeupCategories) {
                    if (TextUtils.equals(category.getType(), type)) {
                        name = Utils.isZh() ? category.getDisplayNameZhCn() : category.getDisplayName();
                        icon = category.getCover();
                        break;
                    }
                }
            }
            BaseFxInfo compoundFxInfo = CompoundFxInfo.create()
                    .setType(FxParams.CONTOURING)
                    .setName(name)
                    .setResourceId(icon)
                    .setFxName(type);
            String packageId = mArScene.getStringVal(mMakeupHelper.getMakeupPackageKey(type));
            compoundFxInfo.setPackageId(packageId);
            compoundFxInfo.setAssetPackagePath(getPackagePath(assetsDirectory, packageId));
            double degree = mArScene.getFloatVal(mMakeupHelper.getMakeupIntensityKey(type));
            compoundFxInfo.setStrength(degree);
            compoundFxInfo.setDefaultStrength(degree);
            makeupList.add(compoundFxInfo);
        }
        return makeupList;
    }

    /**
     * 用户是否修改自定义模板项
     * Whether the user modifies the custom template item
     *
     * @param mapBeautyDatas mapBeautyDatas
     * @return boolean
     */
    public boolean getUserChangeTemplateStrength(HashMap<Integer, List<IFxInfo>> mapBeautyDatas) {
        if ((null == mapBeautyDatas) || mapBeautyDatas.isEmpty()) {
            return false;
        }
        for (Map.Entry<Integer, List<IFxInfo>> entry : mapBeautyDatas.entrySet()) {
            List<IFxInfo> iFxInfos = entry.getValue();
            if ((null == iFxInfos) || iFxInfos.isEmpty()) {
                continue;
            }
            for (IFxInfo info : iFxInfos) {
                if (null == info) {
                    continue;
                }
                List<IFxInfo> nodeInfos = info.getFxNodes();
                if ((null != nodeInfos) && !nodeInfos.isEmpty()) {
                    for (IFxInfo nodeInfo : nodeInfos) {
                        if (null == nodeInfo) {
                            continue;
                        }
                        if (nodeInfo.getStrength() != 0) {
                            return true;
                        }
                    }
                    continue;
                }

                if (info.getStrength() != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getPackagePath(String assetsDirectory, String packageId) {
        if (TextUtils.isEmpty(assetsDirectory) || TextUtils.isEmpty(packageId)) {
            return "";
        }
        List<File> files = FileUtils.listFilesInDir(assetsDirectory);
        for (File file : files) {
            String name = file.getName();
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            if (name.contains(packageId)) {
                return assetsDirectory + File.separator + name;
            }
        }
        return "";
    }
}
