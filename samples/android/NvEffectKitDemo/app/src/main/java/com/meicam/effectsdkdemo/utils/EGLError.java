package com.meicam.effectsdkdemo.utils;

/**
 * Created by meicam-dx on 2018/3/19.
 */

public enum EGLError {

    OK(0,"ok"),
    ConfigErr(101,"config not support");
    int code;
    String msg;
    EGLError(int code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public int value(){
        return code;
    }

    @Override
    public String toString() {
        return msg;
    }
}