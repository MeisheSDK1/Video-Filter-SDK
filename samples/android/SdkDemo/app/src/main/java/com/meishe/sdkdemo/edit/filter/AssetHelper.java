package com.meishe.sdkdemo.edit.filter;

import android.content.Context;
import android.text.TextUtils;

import com.meishe.sdkdemo.R;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : husheng
 * @CreateDate :2024/4/22 13:22
 * @Description :资源帮助类 asset helper
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AssetHelper {

    /**
     * 获取资源key值
     * Get asset key
     *
     * @param name 名称
     * @return the asset key
     */
    public static int getAssetKeyId(String name) {
        if (TextUtils.isEmpty(name)){
            return -1;
        }
        switch (name){
            case "Fair":
                return R.string.fair;
            case "Ice Cream":
                return R.string.ice_cream;
            case "Morning Sunlight":
                return R.string.morning_sunlight;
            case "Wind Whispers":
                return R.string.wind_whispers;
            case "B&W":
                return R.string.b_and_w;
            case "ins Reyes":
                return R.string.ins_reyes;
            case "Chelsea":
                return R.string.chelsea;
            case "Youth":
                return R.string.youth;
            case "Whitening":
                return R.string.asset_whitening;
            case "ins Jaipur":
                return R.string.ins_jaipur;
            case "Tsukiji":
                return R.string.tsukiji;
            case "缩放":
                return R.string.asset_zoom;
            case "粒子":
                return R.string.point;
            case "闪白":
                return R.string.asset_white;
            case "幻觉":
                return R.string.hallucination;
            case "Male":
                return R.string.male;
            case "Hall":
                return R.string.hall;
            case "Reverb":
                return R.string.reverb;
            case "Female":
                return R.string.female;
            case "Cartoon":
                return R.string.cartoon;
            case "Echo":
                return R.string.echo;
            case "Monster":
                return R.string.monster;
            case "Up and down":
                return R.string.up_and_down;
            case "Or so":
                return R.string.or_so;
            case "Full screen left":
                return R.string.full_screen_left;
            case "Heart beat":
                return R.string.heart_beat;
            case "Orange Box":
                return R.string.orange_box;
            case "Fashion flash":
                return R.string.fashion_flash;
            case "Top and bottom border":
                return R.string.top_and_bottom_border;
            case "little cat":
                return R.string.little_cat;
            case "signature two":
                return R.string.signature_two;
            case "Vertical line poem":
                return R.string.vertical_line_poem;
            case "Pop Under":
                return R.string.pop_under;
            case "Popup":
                return R.string.popup;
            case "Spin out":
                return R.string.spin_out;
            case "Flicker":
                return R.string.flicker;
            case "霓虹灯":
                return R.string.neon_light;
            case "金色闪烁":
                return R.string.golden_flicker;
            case "Jump":
                return R.string.jump;
            case "opening":
                return R.string.opening;
            case "Fade in":
                return R.string.fade_in;
            case "Typewriter1":
                return R.string.typewriter1;
            case "closing":
                return R.string.closing;
            case "Fade Out":
                return R.string.fade_out;
            case "Cat":
                return R.string.cat;
            case "Flower caption1":
                return R.string.flower_caption1;
            case "Effect 01":
                return R.string.effect_01;
            case "Site title":
                return R.string.site_title;
            case "Wonderful Day":
                return R.string.wonderful_day;
            case "Three line love letter":
                return R.string.three_line_love_letter;
            case "Four character title":
                return R.string.four_character_title;
            case "BeiJing.HaiDan":
                return R.string.beijing_haidan;
            case "Film subtitles":
                return R.string.film_subtitles;
            case "Record A Good Life":
                return R.string.theme_title;
            case "GRAINBUDS":
                return R.string.grainbuds;
            case "HOKKAIDO IN SUMMER":
                return R.string.hokkaido_in_summer;
            case "Visit Us":
                return R.string.visit_us;
            case "CHARM OF NATURE":
                return R.string.charm_of_nature;
            case "On the journey":
                return R.string.on_the_journey;
            case "April Vlog":
                return R.string.april_vlog;
            case "Signal interference":
                return R.string.signal_interference;
            case "Fuzzy amplification":
                return R.string.fuzzy_amplification;
            case "Column Sweep":
                return R.string.column_sweep;
            case "Pixelated Wipe":
                return R.string.pixelated_wipe;
            case "BounceOpen":
                return R.string.bounceopen;
            case "Page Flip":
                return R.string.page_flip;
            case "Color Revolve":
                return R.string.color_revolve;
            case "pendulum":
                return R.string.pendulum;
            case "Wiper":
                return R.string.wiper;
            case "Screw in":
                return R.string.screw_in;
            case "Shake":
                return R.string.super_zoom_shake;
            case "zoom in":
            case "Zoom In":
                return R.string.key_frame_zoom_in_text;
            case "zoom out":
            case "Zoom Out":
                return R.string.key_frame_zoom_out_text;
            case "None":
                return R.string.NO_FX;
            case "Spin-fall":
                return R.string.spin_fall;
            case "Puzzle":
                return R.string.puzzle;
            case "Slide left":
                return R.string.slide_left;
            case "Zoom1":
                return R.string.zoom1;
            case "custom":
                return R.string.tv_custom;
            case "montage":
                return R.string.montage;
            case "heroic moment":
                return R.string.heroic_moment;
            case "bullet time":
                return R.string.bullet_time;
            case "jump cut":
                return R.string.jump_cut;
            case "flash in":
                return R.string.flash_in;
            case "flash out":
                return R.string.flash_out;
            default:
                return -1;
        }
    }

    public static String getAssetStringValue(Context context,String key) {
        int res = getAssetKeyId(key);
        if (res > 0){
            return context.getResources().getString(res);
        }else {
            return "";
        }
    }

}
