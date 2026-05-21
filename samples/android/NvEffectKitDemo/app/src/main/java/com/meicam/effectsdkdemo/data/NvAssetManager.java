package com.meicam.effectsdkdemo.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.effect.sdk.NvsEffectSdkContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author cdv
 */
public class NvAssetManager implements NvsAssetPackageManager.AssetPackageManagerCallback {
    private static final String TAG = "NvAssetManager";

    private static volatile NvAssetManager mInstance;

    private NvsAssetPackageManager mPackageManager;
    private NvsEffectSdkContext mEffectSdkContext;
    private HashMap<String, ArrayList<NvAsset>> mAssetDict;
    private Context mContext;

    private NvAssetManager(Context context) {
        mContext = context;
        mEffectSdkContext = NvsEffectSdkContext.getInstance( );
        mAssetDict = new HashMap<>( );
        mPackageManager = mEffectSdkContext.getAssetPackageManager( );
        mPackageManager.setCallbackInterface(this);
    }

    public static NvAssetManager init(Context context) {
        if (mInstance == null) {
            synchronized (NvAssetManager.class) {
                if (mInstance == null) {
                    mInstance = new NvAssetManager(context);
                }
            }
        }
        return mInstance;
    }

    public static NvAssetManager getInstance() {
        return mInstance;
    }

    public void searchReservedAssets(int assetType, String dirPath) {
        String assetSuffix = getAssetSuffix(assetType);
        ArrayList<NvAsset> assets = mAssetDict.get(String.valueOf(assetType));
        if (assets == null) {
            assets = new ArrayList<>( );
            mAssetDict.put(String.valueOf(assetType), assets);
        }
        try {
            String[] fileList = mContext.getAssets( ).list(dirPath);
            if (fileList == null) {
                return;
            }
            for (int index = 0; index < fileList.length; ++index) {
                String filePath = fileList[index];
                if (TextUtils.isEmpty(filePath)) {
                    continue;
                }

                if (filePath.endsWith(assetSuffix)) {
                    String packagePath = "assets:/" + dirPath + File.separator + filePath;
                    NvAsset asset = installAssetPackage(packagePath, assetType, true);
                    if (asset == null) {
                        continue;
                    }

                    asset.isReserved = true;
                    asset.assetType = assetType;
                    asset.bundledLocalDirPath = packagePath;

                    NvAsset assetInfo = findAsset(assetType, asset.uuid);
                    if (assetInfo == null) {
                        assets.add(asset);
                    } else {
                        if (assetInfo.version <= asset.version) {
                            assetInfo.copyAsset(asset);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace( );
        }
        ArrayList<NvAsset> assetddd = mAssetDict.get(String.valueOf(assetType));
        for (NvAsset nvAsset : assetddd) {
            if (assetType == NvAsset.ASSET_ARSCENE_FACE) {
            }
        }
    }

    public NvAsset installAssetPackage(String filePath, int assetType, boolean isReserved) {
        NvAsset asset = new NvAsset( );
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        asset.assetType = assetType;
        asset.uuid = fileName.split("\\.")[0];
        if (TextUtils.isEmpty(asset.uuid)) {
            return null;
        }

        StringBuilder packageId = new StringBuilder( );
        //Synchronous installation
        // 同步安装
        int error = mPackageManager.installAssetPackage(filePath, null, asset.getPackageType( ), true, packageId);
        if (assetType == NvAsset.ASSET_ARSCENE_FACE) {
            Log.d(TAG, "installAssetPackage: ARSCENE_FACE error=" + error);
        }
        if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR) {
            asset.version = mPackageManager.getAssetPackageVersion(asset.uuid, asset.getPackageType( ));
            asset.aspectRatio = mPackageManager.getAssetPackageSupportedAspectRatio(asset.uuid, asset.getPackageType( ));
        } else if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            asset.version = mPackageManager.getAssetPackageVersion(asset.uuid, asset.getPackageType( ));
            asset.aspectRatio = mPackageManager.getAssetPackageSupportedAspectRatio(asset.uuid, asset.getPackageType( ));
            int version = mPackageManager.getAssetPackageVersionFromAssetPackageFilePath(filePath);
            if (version > asset.version) {
                error = mPackageManager.upgradeAssetPackage(filePath, null, asset.getPackageType( ), false, packageId);
                if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR) {
                    asset.version = version;
                }
            }
        }

        asset.name = "";
        asset.categoryId = NvAsset.NV_CATEGORY_ID_ALL;
        asset.aspectRatio = NvAsset.AspectRatio_All;
        return asset;
    }

    private NvAsset findAsset(int assetType, String uuid) {
        String key = String.valueOf(assetType);
        ArrayList<NvAsset> assetList = mAssetDict.get(key);
        for (int i = 0; i < assetList.size( ); i++) {
            NvAsset asset = assetList.get(i);
            if (asset.uuid.equals(uuid)) {
                return asset;
            }
        }
        return null;
    }

    // FIXME: 2020/4/1 need to optimization
    private NvAsset findAsset(String uuid) {
        for (String key : mAssetDict.keySet( )) {
            ArrayList<NvAsset> assetList = mAssetDict.get(key);
            for (int i = 0; i < assetList.size( ); i++) {
                NvAsset asset = assetList.get(i);
                if (asset.uuid.equals(uuid)) {
                    return asset;
                }
            }
        }
        return null;
    }

    private String getAssetSuffix(int assetType) {
        String assetSuffix;
        switch (assetType) {
            case NvAsset.ASSET_FILTER:
                assetSuffix = ".videofx";
                break;
            case NvAsset.ASSET_CAPTION_STYLE:
                assetSuffix = ".captionstyle";
                break;
            case NvAsset.ASSET_ANIMATED_STICKER:
                assetSuffix = ".animatedsticker";
                break;
            case NvAsset.ASSET_FONT:
                assetSuffix = ".ttf";
                break;
            case NvAsset.ASSET_CUSTOM_ANIMATED_STICKER:
                assetSuffix = ".animatedsticker";
                break;
            case NvAsset.ASSET_FACE_BUNDLE_STICKER:
                assetSuffix = ".bundle";
                break;
            case NvAsset.ASSET_ARSCENE_FACE:
                assetSuffix = ".arscene";
                break;
            case NvAsset.ASSET_COMPOUND_CAPTION:
                assetSuffix = ".compoundcaption";
                break;
            default:
                assetSuffix = ".videofx";
                break;
        }
        return assetSuffix;
    }

    @Override
    public void onFinishAssetPackageInstallation(String assetPackageId, String assetPackageFilePath, int assetPackageType, int error) {
        if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR || error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            NvAsset asset = findAsset(assetPackageId);
            asset.version = mPackageManager.getAssetPackageVersion(assetPackageId, assetPackageType);
            asset.aspectRatio = mPackageManager.getAssetPackageSupportedAspectRatio(asset.uuid, asset.getPackageType( ));
        }
    }

    @Override
    public void onFinishAssetPackageUpgrading(String s, String s1, int i, int i1) {
    }
}
