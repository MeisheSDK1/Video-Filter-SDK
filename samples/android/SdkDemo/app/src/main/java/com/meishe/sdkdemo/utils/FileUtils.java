package com.meishe.sdkdemo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sensetime on 16-11-16.
 */

public class FileUtils {
    public static boolean copyFileIfNeed(Context context, String fileName, String className) {
        String path = getFilePath(context, className + File.separator + fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                /*
                 * 模型文件不存在
                 * The model file does not exist
                 * */
                try {
                    String folderpath = null;
                    File dataDir = context.getApplicationContext().getExternalFilesDir(null);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        dataDir = context.getApplicationContext().getFilesDir();
                    }

                    if (dataDir != null) {
                        folderpath = dataDir.getAbsolutePath() + File.separator + className;
                    }
                    File folder = new File(folderpath);

                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    if (file.exists())
                        file.delete();

                    file.createNewFile();

                    InputStream in = context.getAssets().open(className + File.separator + fileName);
                    if (in == null) {
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }


    public static void copyAssetFile(Context context, String fileName, String className, String destFileDirPath) {
        /*
         * 模型文件不存在
         * The model file does not exist
         * */
        try {
            File folder = new File(destFileDirPath);

            if (!folder.exists()) {
                folder.mkdirs();
            }
            String destFilePath = destFileDirPath + File.separator + fileName;
            File file = new File(destFilePath);
            if (file.exists()) {
                return;
            }
            InputStream in = context.getAssets().open(className + File.separator + fileName);
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dataDir = context.getApplicationContext().getFilesDir();
        }
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }
        return path;
    }

    public static String getExternalFilePath(Context context, String fileName) {
        File compileDir = new File(Environment.getExternalStorageDirectory(), fileName);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            compileDir = context.getApplicationContext().getExternalFilesDir(fileName);
        }
        if (!compileDir.exists() && !compileDir.mkdirs()) {
            Log.d("TAG", "Failed to make Compile directory");
            return null;
        }
        return compileDir.getAbsolutePath();
    }

    public static List<String> copyStickerZipFiles(Context context, String className) {
        String files[] = null;
        ArrayList<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if (str.indexOf(".zip") != -1) {
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                /*
                 * 判断是否为model结尾
                 * Determine if it is the end of the model
                 * */
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static List<String> getStickerZipFilesFromSd(Context context, String className) {
        ArrayList<String> modelFiles = new ArrayList<String>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                /*
                 * 判断是否为model结尾
                 * Determine if it is the end of the model
                 * */
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static String getStickerZipFileFromSd(Context context, String pathName) {
        ArrayList<String> modelFiles = new ArrayList<String>();

        String stickerpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            stickerpath = dataDir.getAbsolutePath() + File.separator + pathName;

            File zipFile = new File(stickerpath);

            if (!zipFile.exists()) {
                return null;
            }
        }
        return stickerpath;
    }

    public static Map<String, Bitmap> copyStickerIconFiles(Context context, String className) {
        String files[] = null;
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if (str.indexOf(".png") != -1) {
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                /*
                 * 判断是否为png结尾
                 * Determine if it is the end of png
                 * */
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static Map<String, Bitmap> getStickerIconFilesFromSd(Context context, String className) {
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                /*
                 * 判断是否为png结尾
                 * Determine if it is the end of png
                 * */
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static List<String> getStickerNames(Context context, String className) {
        ArrayList<String> modelNames = new ArrayList<String>();
        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;
            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                /*
                 * 判断是否为model结尾
                 * Determine if it is the end of the model
                 * */
                if (filename.trim().toLowerCase().endsWith(".zip") && filename.indexOf("filter") == -1) {
                    String name = subFile[i].getName();
                    modelNames.add(getFileNameNoEx(name));
                }
            }
        }

        return modelNames;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            String[] split = filename.split("/");
            String s = split[split.length - 1];
            int dot = filename.lastIndexOf('/');
            int length = dot + s.indexOf(".") + 1;
            if ((length > -1) && (length < (filename.length()))) {
                return filename.substring(0, length);
            }
        }
        return filename;
    }

    /**
     * content路径转绝对路径
     * content path to absolute path
     * @param contentResolver
     * @param path
     * @return
     */
    public static String contentPath2AbsPath(ContentResolver contentResolver, String path) {
        Uri uri = Uri.parse(path);
        return getFilePathFromContentUri(uri, contentResolver);
    }


    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;

    }

    public static boolean isContent(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("content://");
    }


    public static void JPG2PNG(String path) {
        Bitmap bitmap = openImage(path);
        savePNG_After(bitmap, path);
    }

    /**
     * 将本地图片转成Bitmap
     * Convert the local image to a Bitmap
     * @param path path
     * @return
     */
    private static Bitmap openImage(String path) {
        Bitmap bitmap = null;
        try {
            //1.无压缩 uncompressed
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);

            //2.压缩
//            BitmapFactory.Options options = new BitmapFactory.Options();
//
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//
//            options.inSampleSize = 2;
//
//            bitmap = BitmapFactory.decodeFile(path,options);

            bis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    private static void savePNG_After(Bitmap bitmap, String name) {
        name = name.replace("jpg", "png");
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            //质量压缩，不改变bitmap大小，只牵涉到磁盘文件大小。100表示不压缩
            //Quality compression does not change the bitmap size, only the disk file size is involved. 100 indicates no compression
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将file重命名为zip文件
     * Rename file to a zip file
     * @param otherFile
     * @return
     */
    public static String renameFileZip(String otherFile) {
        File file = new File(otherFile);
        if (file != null && file.isFile()) {
            File newFile = new File(file.getParent() + File.separator + file.getName().split("\\.")[0] + ".zip");
            file.renameTo(newFile);
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * ContentRui转AbsPath
     * ContentRui turns to AbsPath
     * @param context
     * @param filePath
     * @return
     */
    public static String getAbsPathByContentUri(Context context, String filePath) {
        String absPath = filePath;
        if (FileUtils.isContent(filePath) && context != null) {
            absPath = FileUtils.contentPath2AbsPath(context.getContentResolver()
                    , filePath);
        }
        return absPath;
    }

    /**
     * ContentRui转AbsPath 带是否生效控制
     * The control of ContentRui to AbsPath belt is in effect
     * @param context
     * @param applyFlag
     * @param filePath
     * @return
     */
    public static String getAbsPathByContentUri(Context context, boolean applyFlag, String filePath) {
        if (!applyFlag || context == null) return filePath;
        return getAbsPathByContentUri(context, filePath);
    }

    public static boolean deleteFile(File file) {
        if (file != null && file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static String readFile(Context context, String filePath) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder();
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine()) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Logger.e("FileUtils", "fail to read json" + filePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Logger.e("FileUtils", "fail to close bufferedReader", e);
            }
        }
        return retsult.toString();
    }
}
