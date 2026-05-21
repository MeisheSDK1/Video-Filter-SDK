package com.meishe.engine.asset.bean;

import android.content.Context;
import android.content.res.Resources;

import com.meishe.engine.R;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/3/24 14:08
 * @Description :资源常量 Constants of assets
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AssetsConstants {

    public static final int ASSET_THEME = 1;
    public static final int ASSET_FILTER = 2;
    public static final int ASSET_CAPTION_STYLE = 3;
    public static final int ASSET_ANIMATED_STICKER = 4;
    public static final int ASSET_VIDEO_TRANSITION = 5;
    public static final int ASSET_AR_SCENE_FACE = 14;
    public static final int ASSET_COMPOUND_CAPTION = 15;

    public enum AssetsTypeData {
        CAPTION_FLOWER(3, 2, 1),
        CAPTION_BUBBLE(3, 2, 2),
        CAPTION_ANIMATION_IN(3, 2, 3),
        CAPTION_ANIMATION_OUT(3, 2, 4),
        CAPTION_ANIMATION_COMP(3, 2, 5),

        EFFECT_LIVELY(2, 2, 4),
        EFFECT_SHAKING(2, 2, 3),
        EFFECT_DREAM(2, 2, 6),
        EFFECT_FRAME(2, 2, 9),

        ANIMATION_IN(2, 3, 1),
        ANIMATION_OUT(2, 3, 2),
        ANIMATION_COMP(2, 3, 3),

        TEMPLATE(19, -1, -1),

        CAPTION_COMP(15, -1, -1),

        STICKER(4, 1, -1),
        STICKER_VOICE(4, 2, -1),

        NONE(-1, -1, -1);

        public int type;
        public int category;
        public int kind;

        AssetsTypeData(int type, int category, int kind) {
            this.type = type;
            this.category = category;
            this.kind = kind;
        }
    }

    public static String getAssetsTypeName(Context context, int type) {
        String name = "";
        Resources resources = context.getResources();
        switch (type) {
            case ASSET_THEME:
                name = resources.getString(R.string.assets_type_name_theme);
                break;
            case AssetInfo.ASSET_FILTER:
                name = resources.getString(R.string.assets_type_name_filter);
                break;
            case AssetInfo.ASSET_CAPTION_STYLE:
                name = resources.getString(R.string.assets_type_name_caption);
                break;
            case AssetInfo.ASSET_ANIMATED_STICKER:
                name = resources.getString(R.string.assets_type_name_sticker);
                break;
            case AssetInfo.ASSET_VIDEO_TRANSITION:
            case AssetInfo.ASSET_VIDEO_TRANSITION_EFFECT:
            case AssetInfo.ASSET_VIDEO_TRANSITION_3D:
                name = resources.getString(R.string.assets_type_name_transition);
                break;
            case AssetInfo.ASSET_COMPOUND_CAPTION:
                name = resources.getString(R.string.assets_type_name_comp_caption);
                break;
            case AssetInfo.ASSET_EFFECT_LIVELY:
            case AssetInfo.ASSET_EFFECT_SHAKING:
            case AssetInfo.ASSET_EFFECT_DREAM:
            case AssetInfo.ASSET_EFFECT_FRAME:
            case AssetInfo.ASSET_EFFECT_OTHER:
                name = resources.getString(R.string.assets_type_name_effect);
                break;
            case AssetInfo.ASSET_ANIMATION_IN:
                name = resources.getString(R.string.assets_type_name_anim_in);
                break;
            case AssetInfo.ASSET_ANIMATION_OUT:
                name = resources.getString(R.string.assets_type_name_anim_out);
                break;
            case AssetInfo.ASSET_ANIMATION_GROUP:
                name = resources.getString(R.string.assets_type_name_anim_com);
                break;
            case AssetInfo.ASSET_CUSTOM_CAPTION_FLOWER:
                name = resources.getString(R.string.assets_type_name_flower);
                break;
            case AssetInfo.ASSET_CUSTOM_CAPTION_BUBBLE:
                name = resources.getString(R.string.assets_type_name_bubble);
                break;
            case AssetInfo.ASSET_CUSTOM_CAPTION_ANIMATION_IN:
            case AssetInfo.ASSET_CUSTOM_CAPTION_ANIMATION_OUT:
            case AssetInfo.ASSET_CUSTOM_CAPTION_ANIMATION_COMBINATION:
                name = resources.getString(R.string.assets_type_name_caption_animation);
                break;
            default:
                break;
        }
        return name;
    }
}
