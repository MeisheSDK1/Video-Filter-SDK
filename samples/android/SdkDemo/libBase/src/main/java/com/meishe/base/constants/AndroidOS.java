package com.meishe.base.constants;

import android.content.Context;
import android.os.Build;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2021/6/9 16:59
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class AndroidOS {
    public static boolean USE_SCOPED_STORAGE;
    public static void initConfig(Context context){
        //android11的适配版本 android11 adaptation
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            USE_SCOPED_STORAGE = true;
        }
    }
}
