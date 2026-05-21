package com.meishe.sdkdemo.utils.asset;

import com.meishe.http.AssetType;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/23.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class NvAssetUtils {

    public static AssetType getAssetType(int assetType, int categoryId) {
        if (assetType == NvAsset.ASSET_ARSCENE_FACE) {
            return AssetType.AR_SCENE_ALL;
        } else if (assetType == NvAsset.ASSET_FILTER) {
            return AssetType.FILTER_FX_ALL_2;
        } else if (assetType == NvAsset.ASSET_THEME) {
            return AssetType.THEME_ALL;
        } else if (assetType == NvAsset.ASSET_ANIMATED_STICKER) {
            return AssetType.STICKER_NOR;
        } else if (assetType == NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION) {
            return AssetType.STICKER_ANIMAL_IN;
        } else if (assetType == NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION) {
            return AssetType.STICKER_ANIMAL_OUT;
        } else if (assetType == NvAsset.ASSET_ANIMATED_STICKER_ANIMATION) {
            return AssetType.STICKER_ANIMAL_COM;
        } else if (assetType == NvAsset.ASSET_ANIMATION_IN) {
            return AssetType.FILTER_ANIMAL_IN;
        } else if (assetType == NvAsset.ASSET_ANIMATION_OUT) {
            return AssetType.FILTER_ANIMAL_OUT;
        } else if (assetType == NvAsset.ASSET_ANIMATION_COMPANY) {
            return AssetType.FILTER_ANIMAL_COM;
        } else if (assetType == NvAsset.ASSET_FONT) {
            return AssetType.FONT_OTHER;
        } else if (assetType == NvAsset.ASSET_CAPTION_STYLE) {
            return AssetType.CAPTION_TRA_ALL;
        } else if (assetType == NvAsset.ASSET_CAPTION_RICH_WORD) {
            return AssetType.CAPTION_COM_WORD;
        } else if (assetType == NvAsset.ASSET_CAPTION_BUBBLE) {
            return AssetType.CAPTION_COM_POP;
        } else if (assetType == NvAsset.ASSET_CAPTION_IN_ANIMATION) {
            return AssetType.CAPTION_COM_ANIMAL_IN;
        } else if (assetType == NvAsset.ASSET_CAPTION_OUT_ANIMATION) {
            return AssetType.CAPTION_COM_ANIMAL_OUT;
        } else if (assetType == NvAsset.ASSET_CAPTION_ANIMATION) {
            return AssetType.CAPTION_COM_ANIMAL_COM;
        } else if (assetType == NvAsset.ASSET_COMPOUND_CAPTION) {
            return AssetType.COM_CAPTION_ALL;
        } else if (assetType == NvAsset.ASSET_VIDEO_TRANSITION) {
            return AssetType.TRANS_ALL;
        } else if (assetType == NvAsset.ASSET_SUPER_ZOOM) {
            return AssetType.PUSH_MIRROR_ALL;
        } else if (assetType == NvAsset.ASSET_PARTICLE) {
            return AssetType.PARTICLE_ALL;
        } else if (assetType == NvAsset.ASSET_ARSCENE_PARTICLE) {
            return AssetType.AR_SCENE_PARTICLE_ALL;
        } else if (assetType == NvAsset.ASSET_FILTER_DOU) {
            return AssetType.FILTER_FX_SPORT;
        } else if (assetType == NvAsset.ASSET_CUSTOM_ANIMATED_STICKER){
            return AssetType.STICKER_TEMPLATE;
        }
        return null;
    }
}
