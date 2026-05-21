package com.meishe.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.app.BaseApp;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.ZipUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PackageManagerUtil {
    private static final String TAG = "PackageManagerUtil";

    //是否同步安装
    private static String installAssetPackage(String packagePath, String licPath, int packageType, boolean synchronous, StringBuilder asynchronousStringBuilder) {
        boolean noLic= SpUtil.getInstance(BaseApp.getContext()).getBoolean("NO_LIC", false);
        if (ZipUtils.isZipFile(packagePath)) {
            String parentPath = PathNameUtil.getOutOfPathSuffixWithOutPoint(packagePath);
            Log.e(TAG, "onFinish parentPath: " + parentPath);
            try {
                List<File> files = ZipUtils.unzipFile(packagePath, parentPath);
                StringBuilder lic = new StringBuilder();
                StringBuilder packageB = new StringBuilder();
                getPackAndLicPathFromFileDirectory(parentPath, lic, packageB);
                licPath = lic.toString();
                packagePath = packageB.toString();
//                String jsonPath = "";
//                for (File file1 : files) {
//                    if (file1.getName().equals("info.json")) {
//                        jsonPath = file1.getAbsolutePath();
//                        break;
//                    }
//                }
//                if (!jsonPath.isEmpty()) {
//                    JSONObject jsonObject = readJsonFromFile(jsonPath);
//                    packagePath = parentPath + File.separator + jsonObject.getString("packageFileName");
//                    if (!new File(packagePath).exists()) {
//                        Log.e(TAG, "installAssetPackage local packagePath: " + packagePath
//                                + "  is not exists", new Exception("packagePath is not exists"));
//                    }
//                    if (jsonObject.has("uuid")) {
//                        licPath = parentPath + File.separator + jsonObject.getString("uuid") + ".lic";
//                    } else {
//                        licPath = parentPath + File.separator + PathNameUtil.getPathNameWithSuffix(parentPath) + ".lic";
//                    }
//                    if (!new File(licPath).exists()) {
//                        Log.e(TAG, "installAssetPackage local licPath: " + licPath
//                                + "  is not exists");
//                        licPath = "";
//                    }
//
//                }
            } catch (IOException e) {
                Log.e(TAG, "onFinish unzipFile error! IOException: " + e);
            }

        } else if (packagePath.contains("assets:/")) {
            Log.e(TAG, "installAssetPackage assets: " + packagePath);
            //assets路径下去主动寻找lic
            licPath = PathNameUtil.getOutOfPathSuffixWithOutPoint(packagePath) + ".lic";
            //检查文件是否真实存在
            boolean result = isAssertFileExists(BaseApp.getContext(), licPath);
            if (!result) {
                licPath = "";
            }
        } else if (new File(packagePath).exists()) {
            File packageFile = new File(packagePath);
            if (packageFile.isDirectory()) {
                //如果是文件夹 去文件夹里找
                StringBuilder lic = new StringBuilder();
                StringBuilder packageB = new StringBuilder();
                getPackAndLicPathFromFileDirectory(packagePath, lic, packageB);
                licPath = lic.toString();
                packagePath = packageB.toString();
            } else if (packageFile.isFile()) {
                //如果是文件 根据名字拼lic去找
                String parentPath = PathNameUtil.getOutOfPathSuffixWithOutPoint(packagePath);
                licPath = parentPath + ".lic";
                if (!new File(licPath).exists()) {
                    Log.e(TAG, "installAssetPackage local licPath: " + licPath
                            + "  is not exists");
                    licPath = "";
                }
            }
        }
//        if (!noLic && (licPath == null || licPath.isEmpty())) {
//            Log.e(TAG, "installAssetPackage licPath is null! packagePath: " + packagePath
//                    + " licPath: " + licPath, new Exception(""));
//        }
        if (noLic){
            licPath = "";
        }
        NvsAssetPackageManager packageManager = NvsStreamingContext.getInstance().getAssetPackageManager();
        int code;
        StringBuilder stringBuilder = new StringBuilder();
        if (synchronous) {
            code = packageManager.installAssetPackage(packagePath, licPath, packageType, true, stringBuilder);
        } else {
            code = packageManager.installAssetPackage(packagePath, licPath, packageType, false, asynchronousStringBuilder);
        }
//        Log.d("====","====installAssetPackage=="+code+"=="+packageType+"=="+packagePath);
        if (code == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR || code == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            if (code == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
                int version = packageManager.getAssetPackageVersion(FileUtils.getFileName(packagePath), packageType);
                int newVersion = packageManager.getAssetPackageVersionFromAssetPackageFilePath(packagePath);
                if (newVersion > version) {
                    packageManager.upgradeAssetPackage(packagePath, licPath, packageType, true, new StringBuilder());
                }
            }
            return stringBuilder.toString();
        } else {
            if (synchronous) {
                Log.e(TAG, "error synchronous installAssetPackage packagePath: " + packagePath + " packageType: " + packageType + " errorCode: " + code);
            } else {
                if (code != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_WORKING_INPROGRESS) {
                    Log.e(TAG, "error asynchronous installAssetPackage packagePath: " + packagePath + " packageType: " + packageType + " errorCode: " + code);
                }
            }
            return "";
        }
    }

    public static void getPackAndLicPathFromFileDirectory(String parentPath, StringBuilder licBuilder, StringBuilder packageBuilder) {
        try {
            String jsonPath = "";
            String licPath = "";
            String packagePath = "";
            File file = new File(parentPath);
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                if (file1.getName().equals("info.json")) {
                    jsonPath = file1.getAbsolutePath();
                    break;
                }
            }
            if (!jsonPath.isEmpty()) {
                JSONObject jsonObject = readJsonFromFile(jsonPath);
                packagePath = parentPath + File.separator + jsonObject.getString("packageFileName");
//                if (!new File(parentPath).exists()) {
//                    Log.e(TAG, "installAssetPackage local packagePath: " + parentPath
//                            + "  is not exists", new Exception("packagePath is not exists"));
//                }
                if (jsonObject.has("uuid")) {
                    licPath = parentPath + File.separator + jsonObject.getString("uuid") + ".lic";
                } else {
                    licPath = PathNameUtil.getOutOfPathSuffixWithOutPoint(packagePath) + ".lic";
                }
                if (!new File(licPath).exists()) {
                    Log.e(TAG, "installAssetPackage local licPath: " + licPath
                            + "  is not exists");
                    licPath = "";
                }
                licBuilder.append(licPath);
                packageBuilder.append(packagePath);
            }
        } catch (JSONException e) {
            Log.e(TAG, "onFinish unzipFile error! JSONException: " + e);
        }
    }

    public static String installAssetPackage(String packagePath, String licPath, int packageType) {
        return installAssetPackage(packagePath, licPath, packageType, true, null);
    }

    public static String installAssetPackageSynchronous(String packagePath, String licPath, int packageType) {
        return installAssetPackage(packagePath, licPath, packageType, true, null);
    }

    public static void installAssetPackageAsynchronous(String packagePath, String licPath, int packageType, StringBuilder stringBuilder, NvsAssetPackageManager.AssetPackageManagerCallback callback) {
        NvsStreamingContext.getInstance().getAssetPackageManager().setCallbackInterface(callback);
        installAssetPackage(packagePath, licPath, packageType, false, stringBuilder);
    }

    /**
     * 从指定路径读取JSON文件内容并返回JSONObject
     *
     * @param filePath JSON文件的完整路径
     * @return 解析后的JSONObject，如果读取或解析失败则返回null
     */
    public static JSONObject readJsonFromFile(String filePath) {
        JSONObject jsonObject = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();
            jsonObject = new JSONObject(jsonString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // 处理异常，可以根据需要添加错误日志或返回错误信息
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    /*
    获取ARScene资源包的提示信息
     */
    public static void showPropsToast(Context context, String sceneId) {
        NvsAssetPackageManager manager = NvsStreamingContext.getInstance().getAssetPackageManager();
        if (manager == null) {
            return;
        }
        String packagePrompt = manager.getARSceneAssetPackagePrompt(sceneId);
        if (!TextUtils.isEmpty(packagePrompt)) {
            ToastUtil.showToastCenter(context, packagePrompt);
        }
    }

    //assets:/captionstyle/DBCFD1EE-5E24-464C-ADC3-5342DA99C459.captionstyle
    private static boolean isAssertFileExists(Context context, String filePath) {
        String fileName = PathNameUtil.getOutOfPathSuffix(filePath);
        String name = PathNameUtil.getPathNameWithSuffix(filePath);
        String dir = "";
        String cha = "assets:/";
        int start = fileName.indexOf(cha);
        if (start >= 0) {
            dir = fileName.substring(start + cha.length());
        }
        if (dir.contains("/")) {
            //在子目录下，寻找多级目录
            dir = dir.substring(0, dir.lastIndexOf("/"));
        }
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list(dir);
            if (files != null) {
                for (String file : files) {
                    if (file.equals(name)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
