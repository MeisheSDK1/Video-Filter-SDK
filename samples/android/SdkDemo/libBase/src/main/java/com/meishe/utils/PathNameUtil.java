package com.meishe.utils;

/**
 * Created by CaoZhiChao on 2018/12/12 11:56
 */
public class PathNameUtil {
    /**
     * 获取文件后缀名
     * <p>
     * Get file suffix name
     *
     * @param path
     */
    public static String getPathSuffix(String path) {
        if (!path.isEmpty()) {
            return path.substring(path.lastIndexOf(".") + 1);
        }
        return "";
    }

    /**
     * 获取文件除后缀名之外的全部
     * Get all files except suffix
     * 例如：/storage/emulated/0/Android/data/com.meishe.ms240sdkdemo/files/NvStreamingSdk/Asset/ArScene/7832FAC6-A188-43FF-AA70-A8FD1B433060.1.arscene
     * 最后剩下：/storage/emulated/0/Android/data/com.meishe.ms240sdkdemo/files/NvStreamingSdk/Asset/ArScene/7832FAC6-A188-43FF-AA70-A8FD1B433060.1
     *
     * @param path
     */
    public static String getOutOfPathSuffix(String path) {
        if (!path.isEmpty()) {
            return path.substring(0, path.lastIndexOf("."));
        }
        return "";
    }

    /**
     * getOutOfPathSuffix方法对带版本号的路径有.1的后缀，这里把版本号的后缀也删除了
     * 例如：/storage/emulated/0/Android/data/com.meishe.ms240sdkdemo/files/NvStreamingSdk/Asset/ArScene/7832FAC6-A188-43FF-AA70-A8FD1B433060.1.arscene
     * 最后剩下：/storage/emulated/0/Android/data/com.meishe.ms240sdkdemo/files/NvStreamingSdk/Asset/ArScene/7832FAC6-A188-43FF-AA70-A8FD1B433060
     */
    public static String getOutOfPathSuffixWithOutPoint(String path) {
        if (!path.isEmpty()) {
            String name = getPathNameNoSuffix(path);
            String basePath = path.substring(0, path.lastIndexOf("/") + 1);
            if (name.contains(".")) {
                //路径有可能最后是点
                name = name.substring(0, name.lastIndexOf("."));
            }
            return basePath + name;
        }
        return "";
    }

    /**
     * 获取文件名，不带后缀
     * <p>
     * Get file name without suffix
     *
     * @param path
     */
    public static String getPathNameNoSuffix(String path) {
        if (!path.isEmpty()) {
            return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        }
        return "";
    }

    /**
     * 获取文件名，带后缀
     * <p>
     * Get file name with suffix
     *
     * @param path
     */
    public static String getPathNameWithSuffix(String path) {
        if (!path.isEmpty()) {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        return "";
    }
    /**
     * 获取文件名，带后缀的完整路径
     * <p>
     * Get file name with suffix
     *
     * @param path
     */
    public static String getAllPathNameWithSuffix(String path) {
        if (!path.isEmpty()) {
            return  path.substring(0, path.lastIndexOf("/"));
        }
        return "";
    }
}
