package com.meishe.base.utils;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2017/12/29
 *     desc  : constants of permission
 * </pre>
 * 权限常量类
 * Permission constant class
 */
@SuppressLint("InlinedApi")
public final class PermissionConstants {

    public static final String CALENDAR = "CALENDAR";

    public static final String CAMERA = "CAMERA";

    public static final String CONTACTS = "CONTACTS";

    public static final String LOCATION = "LOCATION";

    public static final String MICROPHONE = "MICROPHONE";

    public static final String PHONE = "PHONE";

    public static final String SENSORS = "SENSORS";

    public static final String SMS = "SMS";

    public static final String STORAGE = "STORAGE";

    private static final String[] GROUP_CALENDAR = {
            permission.READ_CALENDAR, permission.WRITE_CALENDAR
    };
    private static final String[] GROUP_CAMERA = {
            permission.CAMERA
    };
    private static final String[] GROUP_CONTACTS = {
            permission.READ_CONTACTS, permission.WRITE_CONTACTS, permission.GET_ACCOUNTS
    };
    private static final String[] GROUP_LOCATION = {
            permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION
    };
    private static final String[] GROUP_MICROPHONE = {
            permission.RECORD_AUDIO
    };
    private static final String[] GROUP_PHONE = {
            permission.READ_PHONE_STATE, permission.READ_PHONE_NUMBERS, permission.CALL_PHONE,
            permission.READ_CALL_LOG, permission.WRITE_CALL_LOG, permission.ADD_VOICEMAIL,
            permission.USE_SIP, permission.PROCESS_OUTGOING_CALLS, permission.ANSWER_PHONE_CALLS
    };
    private static final String[] GROUP_PHONE_BELOW_O = {
            permission.READ_PHONE_STATE, permission.READ_PHONE_NUMBERS, permission.CALL_PHONE,
            permission.READ_CALL_LOG, permission.WRITE_CALL_LOG, permission.ADD_VOICEMAIL,
            permission.USE_SIP, permission.PROCESS_OUTGOING_CALLS
    };
    private static final String[] GROUP_SENSORS = {
            permission.BODY_SENSORS
    };
    private static final String[] GROUP_SMS = {
            permission.SEND_SMS, permission.RECEIVE_SMS, permission.READ_SMS,
            permission.RECEIVE_WAP_PUSH, permission.RECEIVE_MMS,
    };
    //    private static final String[] READ_EXTERNAL_STORAGE_34 = new String[]{
//            Manifest.permission.READ_MEDIA_IMAGES,
//            Manifest.permission.READ_MEDIA_VIDEO,
//            Manifest.permission.READ_MEDIA_AUDIO,
//            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
//    };
    private static final String[] READ_EXTERNAL_STORAGE_33 = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
    };
    private static final String[] READ_EXTERNAL_STORAGE_32 = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String[] GROUP_STORAGE = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? READ_EXTERNAL_STORAGE_33 : READ_EXTERNAL_STORAGE_32;
//    private static final String[] GROUP_STORAGE_34 = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ? READ_EXTERNAL_STORAGE_34 : READ_EXTERNAL_STORAGE_32;

    /**
     * The interface Permission.
     * 权限接口
     */
    @StringDef({CALENDAR, CAMERA, CONTACTS, LOCATION, MICROPHONE, PHONE, SENSORS, SMS, STORAGE,})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Permission {
    }

    /**
     * Get permissions string [ ].
     * 获取权限字符串[]
     *
     * @param permission the permission 许可
     * @return the string [ ] 字符串[]
     */
    public static String[] getPermissions(@Permission final String permission) {
        if (permission == null) {
            return new String[0];
        }
        if (CALENDAR.equals(permission)) {
            return GROUP_CALENDAR;
        } else if (CAMERA.equals(permission)) {
            return GROUP_CAMERA;
        } else if (CONTACTS.equals(permission)) {
            return GROUP_CONTACTS;
        } else if (LOCATION.equals(permission)) {
            return GROUP_LOCATION;
        } else if (MICROPHONE.equals(permission)) {
            return GROUP_MICROPHONE;
        } else if (PHONE.equals(permission)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return GROUP_PHONE_BELOW_O;
            } else {
                return GROUP_PHONE;
            }
        } else if (SENSORS.equals(permission)) {
            return GROUP_SENSORS;
        } else if (SMS.equals(permission)) {
            return GROUP_SMS;
        } else if (STORAGE.equals(permission)) {
            //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            return GROUP_STORAGE_34;
//        }else{
            return GROUP_STORAGE;
//        }
        }
        return new String[]{permission};
    }
}
