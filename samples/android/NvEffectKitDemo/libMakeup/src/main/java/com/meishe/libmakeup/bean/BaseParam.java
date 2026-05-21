package com.meishe.libmakeup.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/6 10:15
 * @Description :美妆基础参数 makeup base param
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BaseParam implements Serializable {
    //类型
    public String type;
    private int canReplace;
    private List<Param> params;

    public int getCanReplace() {
        return canReplace;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    @SerializedName("uuid")
    private String packageId;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean canReplace() {
        return canReplace > 0;
    }

    public void setCanReplace(int canReplace) {
        this.canReplace = canReplace;
    }


    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    @Override
    public String toString() {
        return "BaseParam{" +
                "type='" + type + '\'' +
                ", canReplace=" + canReplace +
                ", params=" + params +
                ", packageId='" + packageId + '\'' +
                '}';
    }

    public static class Param implements Serializable{
        private String key;
        private Object value;
        // type can be int string float double boolean ,path is dir
        //可以为 int string float double boolean ,path 为路径
        private String type;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Param{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
