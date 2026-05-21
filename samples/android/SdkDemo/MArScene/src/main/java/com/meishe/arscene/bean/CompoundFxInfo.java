package com.meishe.arscene.bean;

import android.text.TextUtils;

import com.meishe.arscene.inter.IFxInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/17 18:15
 * @Description :复合型特效 数据类 compound fx data
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CompoundFxInfo extends BaseFxInfo {
    private List<FxParams> paramList;

    public static CompoundFxInfo create() {
        return new CompoundFxInfo();
    }

    public List<FxParams> getParamList() {
        return paramList;
    }

    public CompoundFxInfo setParamList(List<FxParams> paramList) {
        this.paramList = paramList;
        return this;
    }

    public CompoundFxInfo addParam(FxParams param) {
        if (param == null) {
            return this;
        }
        if (paramList == null) {
            paramList = new ArrayList<>(6);
        }
        paramList.add(param);
        return this;
    }

    public FxParams findParam(String key) {
        if (paramList != null && !TextUtils.isEmpty(key)) {
            for (FxParams param : paramList) {
                if (key.equals(param.key)) {
                    return param;
                }
            }
        }
        return null;
    }

    @Override
    public BaseFxInfo copy(IFxInfo fxInfo) {
        super.copy(fxInfo);
        if (fxInfo instanceof CompoundFxInfo) {
            if (paramList != null) {
                paramList.clear();
            }
            List<FxParams> paramList = ((CompoundFxInfo) fxInfo).paramList;
            if (paramList != null) {
                for (FxParams param : paramList) {
                    addParam(new FxParams(param));
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "CompoundFxInfo{" +
                "name='" + getName() + '\'' +
                ", type='" + getType() + '\'' +
                ", fxName='" + getFxName() + '\'' +
                ", strength=" + getStrength() +
                ", defaultStrength=" + getDefaultStrength() +
                ", packageId='" + getPackageId() + '\'' +
                ", assetPackagePath='" + getAssetPackagePath() + '\'' +
                "paramList=" + paramList
                + '}';
    }
}
