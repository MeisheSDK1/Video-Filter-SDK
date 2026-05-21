package com.meishe.base.utils;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/05/07
 *     desc  : utils about resource
 * </pre>
 * 资源工具类
 * Resource tool class
 */
public final class ResourceUtils {

    private static final int BUFFER_SIZE = 8192;

    private ResourceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return the drawable by identifier.
     * 返回drawable by标识符
     *
     * @param id The identifier. 标识符
     * @return the drawable by identifier drawable by标识符
     */
    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(Utils.getApp(), id);
    }

    /**
     * Return the id identifier by name.
     * 按名称返回id标识符
     *
     * @param name The name of id. id的名称
     * @return the id identifier by name 按名称的id标识符
     */
    public static int getIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "id", Utils.getApp().getPackageName());
    }

    /**
     * Return the string identifier by name.
     * 按名称返回字符串标识符
     *
     * @param name The name of string. 字符串的名称
     * @return the string identifier by name 按名称的字符串标识符
     */
    public static int getStringIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "string", Utils.getApp().getPackageName());
    }

    /**
     * Return the color identifier by name.
     * 按名称返回颜色标识符
     *
     * @param name The name of color. 颜色的名称
     * @return the color identifier by name 按名称的颜色标识符
     */
    public static int getColorIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "color", Utils.getApp().getPackageName());
    }

    /**
     * Return the dimen identifier by name.
     * 按名称返回dimen标识符
     *
     * @param name The name of dimen. dimen的名字
     * @return the dimen identifier by name 按名称排列的dimen标识符
     */
    public static int getDimenIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "dimen", Utils.getApp().getPackageName());
    }

    /**
     * Return the drawable identifier by name.
     * 按名称返回可绘制标识符
     *
     * @param name The name of drawable. drawable的名字
     * @return the drawable identifier by name 按名称绘制的标识符
     */
    public static int getDrawableIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "drawable", Utils.getApp().getPackageName());
    }

    /**
     * Return the mipmap identifier by name.
     * 按名称返回mipmap标识符
     *
     * @param name The name of mipmap. mipmap的名称
     * @return the mipmap identifier by name mipmap标识符的名称
     */
    public static int getMipmapIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "mipmap", Utils.getApp().getPackageName());
    }

    /**
     * Return the mipmap identifier by name.
     * 按名称返回mipmap标识符
     *
     * @param resourceId The id of mipmap. mipmap中图片id
     * @return the uri path of mipmap . mipmap中的图片uri路径
     */
    public static String getMipmapToUri(int resourceId) {
        Resources r = Utils.getApp().getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(resourceId) + "/"
                + r.getResourceTypeName(resourceId) + "/"
                + r.getResourceEntryName(resourceId));
        return uri.toString();
    }

    /**
     * Return the layout identifier by name.
     * 按名称返回布局标识符
     *
     * @param name The name of layout. 布局名称
     * @return the layout identifier by name 布局标识符的名称
     */
    public static int getLayoutIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "layout", Utils.getApp().getPackageName());
    }

    /**
     * Return the style identifier by name.
     * 按名称返回样式标识符
     *
     * @param name The name of style. 风格的名称
     * @return the style identifier by name 按名称命名的样式标识符
     */
    public static int getStyleIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "style", Utils.getApp().getPackageName());
    }

    /**
     * Return the anim identifier by name.
     * 按名称返回动画标识符
     *
     * @param name The name of anim. 动画的名字
     * @return the anim identifier by name 动画标识符的名称
     */
    public static int getAnimIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "anim", Utils.getApp().getPackageName());
    }

    /**
     * Return the menu identifier by name.
     * 按名称返回菜单标识符
     *
     * @param name The name of menu. 菜单名称
     * @return the menu identifier by name 菜单标识符的名称
     */
    public static int getMenuIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "menu", Utils.getApp().getPackageName());
    }

    /**
     * Copy the file from assets.
     * 从资产中复制文件
     *
     * @param assetsFilePath The path of file in assets. 资产中的文件路径
     * @param destFilePath   The path of destination file. 目标文件的路径
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyFileFromAssets(final String assetsFilePath, final String destFilePath) {
        boolean res = true;
        try {
            String[] assets = Utils.getApp().getAssets().list(assetsFilePath);
            if (assets != null && assets.length > 0) {
                for (String asset : assets) {
                    res &= copyFileFromAssets(assetsFilePath + "/" + asset, destFilePath + "/" + asset);
                }
            } else {
                res = FileIOUtils.writeFileFromIS(
                        destFilePath,
                        Utils.getApp().getAssets().open(assetsFilePath)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

    /**
     * Return the content of assets.
     * 返回资产的内容
     *
     * @param assetsFilePath The path of file in assets. 资产中的文件路径
     * @return the content of assets 资产内容
     */
    public static String readAssets2String(final String assetsFilePath) {
        return readAssets2String(assetsFilePath, null);
    }

    public static String readAssets2String(final String assetsFilePath, final String charsetName) {
        try {
            InputStream is = Utils.getApp().getAssets().open(assetsFilePath);
            byte[] bytes = FileIOUtils.input2OutputStream(is).toByteArray();
            if (bytes == null) {
                return "";
            }
            if (StringUtils.isSpace(charsetName)) {
                return new String(bytes);
            } else {
                try {
                    return new String(bytes, charsetName);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Return the content of file in assets.
     * 返回资产中的文件内容
     *
     * @param assetsPath The path of file in assets. 资产中的文件路径
     * @return the content of file in assets 资产中的文件内容
     */
    public static List<String> readAssets2List(final String assetsPath) {
        return readAssets2List(assetsPath, "");
    }

    public static List<String> readAssets2List(final String assetsPath,
                                               final String charsetName) {
        try {
            return FileIOUtils.inputStream2Lines(Utils.getApp().getResources().getAssets().open(assetsPath), charsetName);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    /**
     * Copy the file from raw.
     * 从原始文件复制文件
     *
     * @param resId        The resource id. 资源id
     * @param destFilePath The path of destination file. 目标文件的路径
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyFileFromRaw(@RawRes final int resId, final String destFilePath) {
        return FileIOUtils.writeFileFromIS(
                destFilePath,
                Utils.getApp().getResources().openRawResource(resId)
        );
    }

    /**
     * Return the content of resource in raw.
     * 返回资源的原始内容
     *
     * @param resId The resource id. 资源id
     * @return the content of resource in raw
     */
    public static String readRaw2String(@RawRes final int resId) {
        return readRaw2String(resId, null);
    }

    /**
     * Return the content of resource in raw.
     * 返回资源的原始内容
     *
     * @param resId       The resource id. 资源id
     * @param charsetName The name of charset. 字符集的名称
     * @return the content of resource in raw 原始资源的内容
     */
    public static String readRaw2String(@RawRes final int resId, final String charsetName) {
        InputStream is = Utils.getApp().getResources().openRawResource(resId);
        byte[] bytes = FileIOUtils.inputStream2Bytes(is);
        if (bytes == null) {
            return null;
        }
        if (StringUtils.isSpace(charsetName)) {
            return new String(bytes);
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * Return the content of resource in raw.
     * 返回资源的原始内容
     *
     * @param resId The resource id. 资源id
     * @return the content of file in assets 资产中的文件内容
     */
    public static List<String> readRaw2List(@RawRes final int resId) {
        return readRaw2List(resId, "");
    }

    public static List<String> readRaw2List(@RawRes final int resId,
                                            final String charsetName) {
        return FileIOUtils.inputStream2Lines(Utils.getApp().getResources().openRawResource(resId), charsetName);
    }
}
