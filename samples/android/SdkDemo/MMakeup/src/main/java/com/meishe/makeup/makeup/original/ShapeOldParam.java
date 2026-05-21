package com.meishe.makeup.makeup.original;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/9 17:34
 * @Description :美型参数 The beauty shape param
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ShapeOldParam extends BaseOldParam {

    private String degreeName;
    /**
     * 资源文件的路径
     * the package file path
     */
    private String assetsPath;


    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public String getAssetsPath() {
        return assetsPath;
    }

    public void setAssetsPath(String assetsPath) {
        this.assetsPath = assetsPath;
    }

    @Override
    public String toString() {
        return "ShapeParam{" +
                "degreeName='" + degreeName + '\'' +
                ", assetsPath='" + assetsPath + '\'' +
                "type='" + type + '\'' +
                ", canReplace=" + canReplace() +
                ", paramKey='" + getParamKey() + '\'' +
                ", packageId='" + getPackageId() + '\'' +
                ", value=" + getValue() +
                '}';
    }
}
