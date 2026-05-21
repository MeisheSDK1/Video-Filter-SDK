package com.meicam.effectsdkdemo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.meicam.effectsdkdemo.data.AssetInfoData;
import com.meicam.effectsdkdemo.data.FilterJsonParseData;
import com.meicam.effectsdkdemo.data.StickerJsonParseData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author admin
 * @date 2018/11/28
 */
public class ParseJsonFile {
    private static final String TAG = "ParseJsonFile";

    /**
     * Json转Java对象
     * Json to Java Object
     */
    public static <T> T fromJson(String json, Class<T> clz) {
        return new Gson( ).fromJson(json, clz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return new Gson( ).fromJson(json, typeOfT);
    }

    // read asset info form json file
    // 读取素材包信息
    public static String readAssetJsonFile(Context context, String jsonFilePath) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder( );
        try {
            InputStream inputStream = context.getAssets( ).open(jsonFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine( )) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.d(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close( );
                }
            } catch (Exception e) {
                Log.d(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString( );
    }

    public static ArrayList<AssetInfoData> getFilterJsonFileList(Context context, String jsonFilePath, String coverDirectory) {
        String result = readAssetJsonFile(context, jsonFilePath);
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        FilterJsonParseData resultInfo = fromJson(result, FilterJsonParseData.class);
        ArrayList<FilterJsonParseData.FilterJsonFileInfo> infoLists = resultInfo.getInfoList( );
        if (infoLists == null || infoLists.isEmpty( )) {
            return null;
        }
        StringBuilder coverFilePath = new StringBuilder( );
        int dataCount = infoLists.size( );
        ArrayList<AssetInfoData> assetInfoDatas = new ArrayList<>( );
        for (int index = 0; index < dataCount; index++) {
            coverFilePath.setLength(0);
            FilterJsonParseData.FilterJsonFileInfo jsonFileInfo = infoLists.get(index);
            if (jsonFileInfo == null) {
                continue;
            }
            String defaultCoverName = jsonFileInfo.getImageName( );
            String name = jsonFileInfo.getName( );
            String effectId = jsonFileInfo.getFxPackageId( );
            AssetInfoData assetInfoData = new AssetInfoData( );
            assetInfoData.setEffectId(effectId);
            assetInfoData.setName(name);

            coverFilePath.append(coverDirectory);
            coverFilePath.append(defaultCoverName);
            assetInfoData.setDefaultCoverPath(coverFilePath.toString( ));
            assetInfoDatas.add(assetInfoData);
        }
        return assetInfoDatas;
    }
    public static ArrayList<StickerJsonParseData.StickerJsonFileInfo> getStickerJsonFileList(Context context, String jsonFilePath) {
        String result = readAssetJsonFile(context, jsonFilePath);
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        StickerJsonParseData resultInfo = fromJson(result, StickerJsonParseData.class);
        ArrayList<StickerJsonParseData.StickerJsonFileInfo> infoLists = resultInfo.getInfoList( );
        if (infoLists == null || infoLists.isEmpty( )) {
            return null;
        }
        return infoLists;
    }
    public static String readSDJsonFile(String jsonFilePath) {
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder( );
        try {
            FileInputStream inputStream = new FileInputStream(jsonFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine( )) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.d(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close( );
                }
            } catch (Exception e) {
                Log.d(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString( );
    }
}
