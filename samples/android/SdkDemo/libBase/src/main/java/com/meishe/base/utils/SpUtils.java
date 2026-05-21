package com.meishe.base.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2020/12/21 13:42
 * @Description :本地存储工具类 SharedPreferences utils
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class SpUtils {
    private SharedPreferences mSharedPreferences;

    public SpUtils(Context context, String spName) {
        mSharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    /**
     * 清除所有存储的值
     * Clears all stored values
     *
     * @return the boolean
     */
    public boolean clear() {
        if (mSharedPreferences != null) {
            return mSharedPreferences.edit().clear().commit();
        } else {
            return true;
        }
    }

    /**
     * 保存浮点类型的值
     * Save floating-point values
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value float 需要保存的值 The value you need to save
     */
    public boolean putFloat(String key, float value) {
        return mSharedPreferences != null && mSharedPreferences.edit().putFloat(key, value).commit();
    }

    /**
     * 获取对应浮点类型的值
     * Gets the value of the corresponding floating point type
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue float 默认的值 the default value
     * @return float 所需的value
     */
    public float getFloat(String key, float defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getFloat(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    /**
     * 保存long类型的值
     * Save the value of type LONG
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value long 需要保存的值 The value you need to save
     */
    public boolean putLong(String key, long value) {
        return mSharedPreferences != null && mSharedPreferences.edit().putLong(key, value).commit();
    }

    /**
     * 获取long类型的值
     * Gets the value of type LONG
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue float 默认的值 The default value you need
     * @return long 所需的value
     */
    public Long getLong(String key, long defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getLong(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取所需浮点类型的值
     * Save the value of type float
     *
     * @param key String 保存的key值 Saved key value
     */
    public float getFloat(String key) {
        return getFloat(key, 1f);
    }

    /**
     * 保存String类型的值
     * Save the value of type String
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value String 需要保存的值 The value you need to save
     */
    public boolean putString(String key, String value) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.edit().putString(key, value).commit();
        } else {
            return false;
        }
    }

    /**
     * 获取所需类型的值
     * Gets the value of type String
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue String 默认的值 The default value
     * @return String 所需的value
     */
    public String getString(String key, String defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getString(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取对应String类型的值
     * Get the value of type String
     *
     * @param key String 保存的key值 Saved key value
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * 保存json对象
     * Save the value of type object
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value Object 需要保存的值 The value you need to save
     */
    public boolean putJson(String key, Object value) {
        return putString(key, GsonUtils.toJson(value));
    }

    /**
     * 获取所需类型的值
     * Gets the value of type json
     *
     * @param key String 已保存的key值 Saved key value
     * @return String 所需的value
     */
    public <T> T getJson(String key, Class<T> type) {
        return GsonUtils.fromJson(getString(key), type);
    }

    /**
     * 保存int类型的值
     * Save the value of type int
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value int 需要保存的值 The value you need to save
     */
    public boolean putInt(String key, int value) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.edit().putInt(key, value).commit();
        } else {
            return false;
        }
    }

    /**
     * 获取所需对应类型的值
     * Gets the value of type int
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue int 需要保存的值 The default value
     */
    public int getInt(String key, int defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getInt(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取所需对应类型的值
     * Gets the value of type int
     *
     * @param key String 保存的key值
     */
    public int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * 保存boolean类型的值
     * Save the value of type boolean
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value boolean 需要保存的值 The key value you need to save
     */
    public boolean putBoolean(String key, boolean value) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.edit().putBoolean(key, value).commit();
        } else {
            return false;
        }
    }

    /**
     * 获取所需对应类型的值
     * Gets the value of type boolean
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue boolean 需要保存的值 The default value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getBoolean(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取所需对应类型的值
     * Gets the value of type boolean
     *
     * @param key String 已保存的key值 Saved key value
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }
}

