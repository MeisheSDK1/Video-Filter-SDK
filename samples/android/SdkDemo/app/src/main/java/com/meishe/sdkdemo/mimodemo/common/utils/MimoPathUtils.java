package com.meishe.sdkdemo.mimodemo.common.utils;

import android.os.Environment;

import com.meishe.base.constants.AndroidOS;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.mimodemo.common.utils.asset.NvAsset;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by admin on 2018-6-5.
 */

public class MimoPathUtils {

    private static final String TAG = MimoPathUtils.class.getName();

    private static String SDK_FILE_ROOT_DIRECTORY = "NvStreamingSdk" + File.separator;
    private static String RECORDING_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Record";
    private static String AUDIO_RECORD_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "AudioRecord";
    private static String DOUVIDEO_RECORDING_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "DouVideoRecord";
    private static String DOUVIDEO_CONVERT_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "DouVideoConvert";
    private static String COVER_IMAGE_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Cover";
    private static String PARTICLE_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Particle";
    private static String CREASH_LOG_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Log";
    private static String WATERMARK_CAF_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "WaterMark";
    private static String PICINPIC_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "PicInPic";
    private static String VIDEOCOMPILE_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Compile";
    private static String CAPTURESCENE_RECORDING_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "CaptureScene";
    private static String BOOMRANG_RECORDING_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "BoomRang";
    private static String SUPERZOOM_RECORDING_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "SuperZoom";

    //转码视频目录 Transcoding video directory
    private static String CONVER_VIDEO_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "convertVideo";
    private static String MIMO_TEMPLATE_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "mimoTemplate";

    private static String ASSET_DOWNLOAD_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Asset";
    private static String ASSET_DOWNLOAD_DIRECTORY_FILTER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Filter";
    private static String ASSET_DOWNLOAD_DIRECTORY_THEME = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Theme";
    private static String ASSET_DOWNLOAD_DIRECTORY_CAPTION = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Caption";
    private static String ASSET_DOWNLOAD_DIRECTORY_ANIMATEDSTICKER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "AnimatedSticker";
    private static String ASSET_DOWNLOAD_DIRECTORY_TRANSITION = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Transition";
    private static String ASSET_DOWNLOAD_DIRECTORY_FONT = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Font";
    private static String ASSET_DOWNLOAD_DIRECTORY_CAPTURE_SCENE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "CaptureScene";
    private static String ASSET_DOWNLOAD_DIRECTORY_PARTICLE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Particle";
    private static String ASSET_DOWNLOAD_DIRECTORY_FACE_STICKER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "FaceSticker";
    private static String ASSET_DOWNLOAD_DIRECTORY_CUSTOM_ANIMATED_STICKER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "CustomAnimatedSticker";
    private static String ASSET_DOWNLOAD_DIRECTORY_FACE1_STICKER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Face1Sticker";
    private static String ASSET_DOWNLOAD_DIRECTORY_SUPER_ZOOM = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Meicam";
    private static String ASSET_DOWNLOAD_DIRECTORY_ARSCENE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "ArScene";
    private static String ASSET_DOWNLOAD_DIRECTORY_GIFCONVERT = ASSET_DOWNLOAD_DIRECTORY + File.separator + "GifConvert";
    private static String ASSET_DOWNLOAD_DIRECTORY_COMPOUND_CAPTION = ASSET_DOWNLOAD_DIRECTORY + File.separator + "CompoundCaption";

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    deleteDirectoryFile(f);
                }
            }
            //如要保留文件夹，只删除文件，请注释这行
            // To keep the folder and delete only the files, comment this line
            file.delete();
        } else if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteDirectoryFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    deleteDirectoryFile(f);
                }
            }
            //file.delete();
        } else if (file.exists()) {
            file.delete();
        }
    }

    public static String getDouVideoRecordDir() {
        return getFolderDirPath(DOUVIDEO_RECORDING_DIRECTORY);
    }

    public static String getGifConvertDir() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_GIFCONVERT);
    }

    public static String getDouVideoRecordVideoPath() {
        String dstDirPath = getDouVideoRecordDir();
        if (dstDirPath == null)
            return null;
        String fileName = getCharacterAndNumber() + ".mp4";
        return getFileDirPath(dstDirPath, fileName);
    }

    public static String getDouVideoConvertDir() {
        return getFolderDirPath(DOUVIDEO_CONVERT_DIRECTORY);
    }

    public static String getLogDir() {
        return getFolderDirPath(CREASH_LOG_DIRECTORY);
    }

    public static String getWatermarkCafDirectoryDir() {
        return getFolderDirPath(WATERMARK_CAF_DIRECTORY);
    }


    //获取视频生成目录
    //Gets the video generation directory
    public static String getVideoCompileDirPath() {
        return getFolderDirPath(VIDEOCOMPILE_DIRECTORY);
    }

    //获取转码视频目录
    //Gets the transcoding video directory
    public static String getVideoConvertDirPath() {
        return getFolderDirPath(CONVER_VIDEO_DIRECTORY);
    }

    //获取mimo 模板路径
    //Obtain the path of the mimo template
    public static String getMimoTemolateDirectory() {
        return getFolderDirPath(MIMO_TEMPLATE_DIRECTORY);
    }

    public static String getRecordVideoPath() {
        String dstDirPath = getFolderDirPath(RECORDING_DIRECTORY);
        if (dstDirPath == null)
            return null;
        String fileName = getCharacterAndNumber() + ".mp4";
        return getFileDirPath(dstDirPath, fileName);
    }

    public static String getCaptureSceneRecordVideoPath() {
        String dstDirPath = getFolderDirPath(CAPTURESCENE_RECORDING_DIRECTORY);
        if (dstDirPath == null)
            return null;
        String fileName = getCharacterAndNumber() + ".mp4";
        return getFileDirPath(dstDirPath, fileName);
    }


    public static String getRecordPicturePath() {
        String dstDirPath = getFolderDirPath(RECORDING_DIRECTORY);
        if (dstDirPath == null)
            return null;
        String fileName = getCharacterAndNumber() + ".jpg";
        return getFileDirPath(dstDirPath, fileName);
    }

    public static String getCoverImagePath() {
        String dstDirPath = getFolderDirPath(COVER_IMAGE_DIRECTORY);
        if (dstDirPath == null)
            return null;
        String fileName = getCharacterAndNumber() + ".jpg";
        return getFileDirPath(dstDirPath, fileName);
    }

    public static String getParticleRecordPath() {
        String dstDirPath = getFolderDirPath(PARTICLE_DIRECTORY);
        if (dstDirPath == null)
            return null;
        String fileName = getCharacterAndNumber() + ".mp4";
        return getFileDirPath(dstDirPath, fileName);
    }

    public static String getAudioRecordFilePath() {
        return getFolderDirPath(AUDIO_RECORD_DIRECTORY);
    }

    public static String getCharacterAndNumber() {
        return String.valueOf(System.nanoTime());
    }

    public static String getAssetDownloadPath(int assetType) {
        String assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY);
        if (assetDownloadDir == null)
            return null;
        switch (assetType) {
            case NvAsset.ASSET_THEME: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_THEME);
            }
            case NvAsset.ASSET_FILTER: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_FILTER);
            }
            case NvAsset.ASSET_CAPTION_STYLE: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_CAPTION);
            }
            case NvAsset.ASSET_ANIMATED_STICKER: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_ANIMATEDSTICKER);
            }
            case NvAsset.ASSET_VIDEO_TRANSITION: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_TRANSITION);
            }
            case NvAsset.ASSET_FONT: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_FONT);
            }
            case NvAsset.ASSET_CAPTURE_SCENE: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_CAPTURE_SCENE);
            }
            case NvAsset.ASSET_PARTICLE: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_PARTICLE);
            }
            case NvAsset.ASSET_FACE_STICKER: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_FACE_STICKER);
            }
            case NvAsset.ASSET_CUSTOM_ANIMATED_STICKER: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_CUSTOM_ANIMATED_STICKER);
            }
            case NvAsset.ASSET_FACE1_STICKER: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_FACE1_STICKER);
            }
            case NvAsset.ASSET_SUPER_ZOOM: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_SUPER_ZOOM);
            }
            case NvAsset.ASSET_ARSCENE_FACE: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_ARSCENE);
            }
            case NvAsset.ASSET_COMPOUND_CAPTION: {
                return getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_COMPOUND_CAPTION);
            }
        }
        return assetDownloadDir;
    }

    private static String getAssetDownloadDirPath(String assetDirPathToCreate) {
        return getFolderDirPath(assetDirPathToCreate);
    }

    //获取文件生成目录
    //Gets the file generation directory
    public static String getFileDirPath(String dstDirPathToCreate, String fileName) {
        File file = new File(dstDirPathToCreate, fileName);
        if (file.exists()) {
            file.delete();
        }

        return file.getAbsolutePath();
    }

    //获取文件夹生成目录
    //Gets the folder generation directory
    public static String getFolderDirPath(String dstDirPathToCreate) {
        File dstFileDir = new File(Environment.getExternalStorageDirectory(), dstDirPathToCreate);
        if (AndroidOS.USE_SCOPED_STORAGE) {
            dstFileDir = new File(MSApplication.getContext().getExternalFilesDir(""), dstDirPathToCreate);
        }
        if (!dstFileDir.exists() && !dstFileDir.mkdirs()) {
            Logger.e(TAG, "Failed to create file dir path--->" + dstDirPathToCreate);
            return null;
        }
        return dstFileDir.getAbsolutePath();
    }

    public static boolean unZipFile(String zipFile, String folderPath) {
        ZipFile zfile = null;
        try {
            // 转码为GBK格式，支持中文
            // The transcoding is in GBK format and supports Chinese characters
            zfile = new ZipFile(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            // 列举的压缩文件里面的各个文件，判断是否为目录
            //Check whether each file in the compressed file is a directory
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                dirstr.trim();
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = null;
            FileOutputStream fos = null;
            File realFile = getRealFileName(folderPath, ze.getName());
            try {
                fos = new FileOutputStream(realFile);
            } catch (FileNotFoundException e) {
                return false;
            }
            os = new BufferedOutputStream(fos);
            InputStream is = null;
            try {
                is = new BufferedInputStream(zfile.getInputStream(ze));
            } catch (IOException e) {
                return false;
            }
            int readLen = 0;
            try {
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
            } catch (IOException e) {
                return false;
            }
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            zfile.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static File getRealFileName(String baseDir, String absFileName) {
        absFileName = absFileName.replace("\\", "/");
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                ret = new File(ret, substr);
            }

            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            ret = new File(ret, substr);
            return ret;
        } else {
            ret = new File(ret, absFileName);
        }
        return ret;
    }

    public static String getFileNameNoExt(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.indexOf('.');
            int lastSeparator = filename.lastIndexOf('/');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(lastSeparator + 1, dot);
            }
        }
        return filename;
    }

    public static String getFileName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.indexOf('.');
            int lastSeparator = filename.lastIndexOf('/');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(lastSeparator + 1);
            }
        }
        return filename;
    }

    public static int getAssetVersionWithPath(String path) {
        String[] strings = path.split("/");
        if (strings.length > 0) {
            String filename = strings[strings.length - 1];
            String[] parts = filename.split(".");
            if (parts.length == 3) {
                return Integer.parseInt(parts[1]);
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    public static long getFileModifiedTime(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return 0;
        } else {
            return file.lastModified();
        }
    }

}
