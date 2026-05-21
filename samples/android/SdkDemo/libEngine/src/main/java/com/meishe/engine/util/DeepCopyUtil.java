package com.meishe.engine.util;

import com.meishe.base.utils.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2020/7/6 21:15
 * 对象的深拷贝工具类
 * Object deep copy utility class
 */
public class DeepCopyUtil {
    public static Object deepClone(Serializable obj) {
        Object anotherObj = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] bytes = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            anotherObj = ois.readObject();
        } catch (Exception ex) {
            LogUtils.e(ex);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException localIOException3) {
                    LogUtils.e("deepClone error:"+localIOException3.getMessage());
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException localIOException4) {
                    LogUtils.e("deepClone error:"+localIOException4.getMessage());
                }
            }
        }
        return anotherObj;
    }
}
