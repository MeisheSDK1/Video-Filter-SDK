package com.meishe.sdkdemo.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsClipCaption;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoFrameRetriever;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.pop.TipsPop;
import com.meishe.base.utils.ThreadUtils;
import com.meishe.base.utils.Utils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.record.RecordFxListItem;
import com.meishe.sdkdemo.edit.record.SqAreaInfo;
import com.meishe.sdkdemo.edit.view.CommonDialog;
import com.meishe.sdkdemo.interfaces.TipsButtonClickListener;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.CompoundCaptionInfo;
import com.meishe.sdkdemo.utils.permission.OnPermissionsSettingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.meicam.sdk.NvsAVFileInfo.AV_FILE_TYPE_IMAGE;

public class Util {
    private final static String TAG = "Util";

    /**
     * 两次点击间隔不能少于1000ms
     * The interval between two clicks cannot be less than 1000ms
     */
    private static final int MIN_DELAY_TIME = 1000;
    private static long lastClickTime;

    private static final String[] READ_EXTERNAL_STORAGE_33 = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
    };
    private static final String[] READ_EXTERNAL_STORAGE_32 = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    public static final String[] READ_EXTERNAL_STORAGE = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? READ_EXTERNAL_STORAGE_33 : READ_EXTERNAL_STORAGE_32;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
            lastClickTime = currentClickTime;
        }
        return flag;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String processName = getProcessName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return true;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(processName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return false;
            }
        }

        return true;

    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 把String转化为double
     * Convert String to double
     * */
    public static double convertToDouble(String number, double defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    /**
     * 首字母大写
     *
     * @param name
     * @return
     */
    public static String upperCaseName(String name) {
        char[] cs = name.toCharArray();
        if ((97 <= cs[0]) && (cs[0] <= 122)) {
            cs[0] ^= 32;
        }
        return String.valueOf(cs);
    }

    public static boolean getBitmapFromClipInfo(Context context, ClipInfo clipInfo, OnBitmapFromClipInfo bitmapFromClipInfo) {
        Bitmap bitmap = null;
        String clipPath = clipInfo.getFilePath();

        if (TextUtils.isEmpty(clipPath) || (null == bitmapFromClipInfo)) {
            return false;
        }
        NvsStreamingContext streamingContext = NvsStreamingContext.getInstance();
        if (context == null)
            return false;

        NvsAVFileInfo fileInfo = streamingContext.getAVFileInfo(clipPath);
        if (fileInfo == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.edit_clip_default_bg);
            bitmap = Bitmap.createScaledBitmap(bitmap, 540, 960, true);
            bitmapFromClipInfo.onBitmapFromClipInfo(bitmap);
            return true;
        }
        if (fileInfo.getAVFileType() == AV_FILE_TYPE_IMAGE) {
            bitmap = BitmapFactory.decodeFile(clipPath);
            bitmap = centerSquareScaleBitmap(bitmap, 180, 320);
            bitmapFromClipInfo.onBitmapFromClipInfo(bitmap);
            return true;
        }
        ThreadUtils.getIoPool().execute(new Runnable() {
            @Override
            public void run() {
                NvsVideoFrameRetriever videoFrameRetriever = streamingContext.createVideoFrameRetriever(clipPath);
                if (null == videoFrameRetriever) {
                    bitmapFromClipInfo.onBitmapFromClipInfo(null);
                    return;
                }
                long timeStamp = clipInfo.getTrimIn();
                if (timeStamp < 0) {
                    timeStamp = 0;
                }
                Bitmap frameBitmap = videoFrameRetriever.getFrameAtTime(timeStamp, NvsVideoFrameRetriever.VIDEO_FRAME_HEIGHT_GRADE_480);
                frameBitmap = centerSquareScaleBitmap(frameBitmap, 180, 320);
                videoFrameRetriever.release();
                bitmapFromClipInfo.onBitmapFromClipInfo(frameBitmap);
            }
        });
        return true;
    }

    public interface OnBitmapFromClipInfo {
        public void onBitmapFromClipInfo(Bitmap bitmap);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean getBundleFilterInfo(Context context, ArrayList<NvAsset> assetArrayList, String bundlePath) {
        if (context == null)
            return false;

        if (TextUtils.isEmpty(bundlePath))
            return false;

        try {
            InputStream nameListStream = context.getAssets().open(bundlePath);
            BufferedReader nameListBuffer = new BufferedReader(new InputStreamReader(nameListStream, "GBK"));

            String strLine;
            while ((strLine = nameListBuffer.readLine()) != null) {
                String[] strNameArray = strLine.split(",");
                if (strNameArray.length < 3)
                    continue;

                for (int i = 0; i < assetArrayList.size(); ++i) {
                    NvAsset assetItem = assetArrayList.get(i);
                    if (assetItem == null)
                        continue;

                    if (!assetItem.isReserved)
                        continue;

                    String packageId = assetItem.uuid;
                    if (TextUtils.isEmpty(packageId))
                        continue;

                    if (packageId.equals(strNameArray[0])) {
                        assetItem.name = strNameArray[1];
                        assetItem.aspectRatio = Integer.parseInt(strNameArray[2]);
                        break;
                    }

                }
            }
            nameListBuffer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int width, int height) {
        if (null == bitmap || width <= 0 || height <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > width && heightOrg > height) {
            /*
             * 压缩到一个最小长度是edgeLength的bitmap
             * Compressed to a bitmap with a minimum length of edgeLength
             * */
            float wRemainder = widthOrg / (float) width;
            float hRemainder = heightOrg / (float) height;

            int scaledWidth;
            int scaledHeight;
            if (wRemainder > hRemainder) {
                scaledWidth = (int) (widthOrg / hRemainder);
                scaledHeight = (int) (heightOrg / hRemainder);
            } else {
                scaledWidth = (int) (widthOrg / wRemainder);
                scaledHeight = (int) (heightOrg / wRemainder);
            }

            Bitmap scaledBitmap;
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            /*
             * 从图中截取正中间的部分。
             * Take the middle part from the figure.
             * */
            int xTopLeft = (scaledWidth - width) / 2;
            int yTopLeft = (scaledHeight - height) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, width, height);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }

    public static void showDialog(Context context, final String title, final String first_tip, final String second_tip) {
        final CommonDialog dialog = new CommonDialog(context, 1);
        dialog.setOnCreateListener(new CommonDialog.OnCreateListener() {
            @Override
            public void OnCreated() {
                dialog.setTitleTxt(title);
                dialog.setFirstTipsTxt(first_tip);
                dialog.setSecondTipsTxt(second_tip);
            }
        });
        dialog.setOnBtnClickListener(new CommonDialog.OnBtnClickListener() {
            @Override
            public void OnOkBtnClicked(View view) {
                dialog.dismiss();
            }

            @Override
            public void OnCancelBtnClicked(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showDialog(Context context, final String title, final String first_tip) {
        final CommonDialog dialog = new CommonDialog(context, 1);
        dialog.setOnCreateListener(new CommonDialog.OnCreateListener() {
            @Override
            public void OnCreated() {
                dialog.setTitleTxt(title);
                dialog.setFirstTipsTxt(first_tip);
            }
        });
        dialog.setOnBtnClickListener(new CommonDialog.OnBtnClickListener() {
            @Override
            public void OnOkBtnClicked(View view) {
                dialog.dismiss();
            }

            @Override
            public void OnCancelBtnClicked(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showDialog(Context context, final String title, final String first_tip, final TipsButtonClickListener tipsButtonClickListener) {
        TipsPop mPermissionsPop = TipsPop.create(context, true)
                .setDefaultTipsTitle(title)
                .setDefaultTipsContent(first_tip)
                .setTipsDefaultConfirm(context.getResources().getString(R.string.permission_set));
        mPermissionsPop.setOnTipsPopListener(new TipsPop.OnTipsPopListener() {
            @Override
            public void onTipsCancel() {
                AppManager.getInstance().finishActivity();
            }

            @Override
            public void onTipsConfirm() {
                if (context instanceof Activity) {
                    startAppSettings(context);
                    AppManager.getInstance().finishActivity((Activity) context);
                }
                mPermissionsPop.dismiss();
            }
        });
        mPermissionsPop.show();
    }

    /**
     * 是否是华为4A
     *
     * @return
     */
    public static boolean isHUAWEI4A() {
        if (TextUtils.equals(Build.MODEL, "SCL-CL00")) {
            return true;
        }
        return false;
    }

    /**
     * 根据x坐标进行排序
     * Sort by x coordinate
     */
    public static class PointXComparator implements Comparator<PointF> {

        @Override
        public int compare(PointF bean1, PointF bean2) {
            return (int) (bean1.x - bean2.x);
        }
    }

    /**
     * 根据x坐标进行排序
     * Sort by x coordinate
     */
    public static class PointYComparator implements Comparator<PointF> {

        @Override
        public int compare(PointF bean1, PointF bean2) {
            return (int) (bean1.y - bean2.y);
        }
    }

    /**
     * 根据声效rank排序
     * Sort by sound rank
     */
    public static class RecordFxIndexComparator implements Comparator<RecordFxListItem> {

        @Override
        public int compare(RecordFxListItem bean1, RecordFxListItem bean2) {
            return bean1.index - bean2.index;
        }
    }

    /**
     * 根据InPoint排序
     * Sort by InPoint
     */
    public static class AreaInComparator implements Comparator<SqAreaInfo> {

        @Override
        public int compare(SqAreaInfo bean1, SqAreaInfo bean2) {
            return (int) (bean1.getInPoint() - bean2.getInPoint());
        }
    }

    /**
     * 根据OutPoint排序
     * Sort by OutPoint
     */
    public static class AreaOutComparator implements Comparator<SqAreaInfo> {

        @Override
        public int compare(SqAreaInfo bean1, SqAreaInfo bean2) {
            return (int) (bean2.getOutPoint() - bean1.getOutPoint());
        }
    }


    /*
     * 保存数据
     * save data
     * */
    public static CaptionInfo saveCaptionData(NvsCaption caption) {
        if (caption == null)
            return null;
        long inPoint = 0, outPoint = 0;
        if (caption instanceof NvsTimelineCaption) {
            inPoint = ((NvsTimelineCaption) caption).getInPoint();
            outPoint = ((NvsTimelineCaption) caption).getOutPoint();
        } else if (caption instanceof NvsClipCaption) {
            inPoint = ((NvsClipCaption) caption).getInPoint();
            outPoint = ((NvsClipCaption) caption).getOutPoint();
        }
        CaptionInfo captionInfo = new CaptionInfo();
        captionInfo.setInPoint(inPoint);
        captionInfo.setOutPoint(outPoint);
        captionInfo.setText(caption.getText());
        captionInfo.setCaptionZVal((int) caption.getZValue());
        captionInfo.setCaptionSize(caption.getFontSize());
        captionInfo.setCaptionColorAlpha(100);
        return captionInfo;
    }

    /*
     * 保存组合字幕数据
     * Save subtitle data
     * */
    public static CompoundCaptionInfo saveCompoundCaptionData(NvsTimelineCompoundCaption caption) {
        if (caption == null)
            return null;
        CompoundCaptionInfo captionInfo = new CompoundCaptionInfo();
        long inPoint = caption.getInPoint();
        captionInfo.setInPoint(inPoint);
        long outPoint = caption.getOutPoint();
        captionInfo.setOutPoint(outPoint);
        int captionCount = caption.getCaptionCount();
        for (int idx = 0; idx < captionCount; idx++) {
            captionInfo.addCaptionAttributeList(new CompoundCaptionInfo.CompoundCaptionAttr());
        }

        captionInfo.setCaptionZVal((int) caption.getZValue());
        captionInfo.setAnchor(caption.getAnchorPoint());
        PointF pointF = caption.getCaptionTranslation();
        captionInfo.setTranslation(pointF);
        return captionInfo;
    }

    /**
     * 获取json文件记录的录音特效列表
     * <p>
     * Get list of recording effects recorded in json file
     */
    public static List<RecordFxListItem> listRecordFxFromJson(Context context) {
        List<RecordFxListItem> fileList = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open("record/record.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            JSONObject data = new JSONObject(builder.toString());
            JSONArray array = data.getJSONArray("record_fx");
            for (int i = 0; i < array.length(); i++) {
                JSONObject role = array.getJSONObject(i);
                RecordFxListItem audioFxListItem = new RecordFxListItem();
                audioFxListItem.fxName = role.getString("name");
                audioFxListItem.fxID = role.getString("builtin_fx_name");
                audioFxListItem.index = role.getInt("rank");

                switch (i) {
                    case 0:
                        audioFxListItem.image_drawable = ContextCompat.getDrawable(context, R.drawable.record_fx_1);
                        break;
                    case 1:
                        audioFxListItem.image_drawable = ContextCompat.getDrawable(context, R.drawable.record_fx_2);
                        break;
                    case 2:
                        audioFxListItem.image_drawable = ContextCompat.getDrawable(context, R.drawable.record_fx_3);
                        break;
                    case 3:
                        audioFxListItem.image_drawable = ContextCompat.getDrawable(context, R.drawable.record_fx_4);
                        break;
                    case 4:
                        audioFxListItem.image_drawable = ContextCompat.getDrawable(context, R.drawable.record_fx_5);
                        break;
                    case 5:
                        audioFxListItem.image_drawable = ContextCompat.getDrawable(context, R.drawable.record_fx_6);
                        break;
                    case 6:
                        audioFxListItem.image_drawable = ContextCompat.getDrawable(context, R.drawable.record_fx_7);
                        break;
                    case 7:
                        audioFxListItem.image_drawable = ContextCompat.getDrawable(context, R.drawable.record_fx_8);
                        break;
                    default:
                        break;
                }

                fileList.add(audioFxListItem);
            }
            Collections.sort(fileList, new RecordFxIndexComparator()); // 排序

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    /**
     * 清除录音文件
     * <p>
     * Clear recording files
     */
    public static void clearRecordAudioData() {
        String dir = PathUtils.getAudioRecordFilePath();
        if (dir == null) {
            return;
        }
        File record_dir = new File(dir);
        if (!record_dir.exists()) {
            return;
        }
        for (File file : record_dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }


    /**
     * 将bitmap保存到SD卡
     * ca
     * Save bitmap to SD card
     */
    public static boolean saveBitmapToSD(Bitmap bt, String target_path) {
        if (bt == null || target_path == null || target_path.isEmpty()) {
            return false;
        }
        File file = new File(target_path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bt.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static NvsVideoResolution getVideoEditResolution(int ratio) {
        ParameterSettingValues settingValues = ParameterSettingValues.instance();
        int compileRes = settingValues.getCompileVideoRes();
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        Point size = new Point();
        if (ratio == NvAsset.AspectRatio_16v9) {
            size.set(compileRes * 16 / 9, compileRes);
        } else if (ratio == NvAsset.AspectRatio_1v1) {
            size.set(compileRes, compileRes);
        } else if (ratio == NvAsset.AspectRatio_9v16) {
            size.set(compileRes, compileRes * 16 / 9);
        } else if (ratio == NvAsset.AspectRatio_3v4) {
            size.set(compileRes, compileRes * 4 / 3);
        } else if (ratio == NvAsset.AspectRatio_4v3) {
            size.set(compileRes * 4 / 3, compileRes);
        } else if (ratio == NvAsset.AspectRatio_21v9) {
            size.set(compileRes * 21 / 9, compileRes);
        } else if (ratio == NvAsset.AspectRatio_9v21) {
            size.set(compileRes, compileRes * 21 / 9);
        } else if (ratio == NvAsset.AspectRatio_18v9) {
            size.set(compileRes * 18 / 9, compileRes);
        } else if (ratio == NvAsset.AspectRatio_9v18) {
            size.set(compileRes, compileRes * 18 / 9);
        } else if (ratio == NvAsset.AspectRatio_7v6) {
            size.set(compileRes * 7 / 6, compileRes);
        } else if (ratio == NvAsset.AspectRatio_6v7) {
            size.set(compileRes, compileRes * 7 / 6);
        } else {
            size.set(1280, 720);
        }

        videoEditRes.imageWidth = getCustomWidth(size.x, size.y);
        videoEditRes.imageHeight = getCustomHeight(size.x, size.y);

        videoEditRes.bitDepth = settingValues.getBitDepth();
        Logger.d("getVideoEditResolution   ", videoEditRes.imageWidth + "     " + videoEditRes.imageHeight);
        return videoEditRes;
    }


    public static NvsVideoResolution getVideoEditResolution2(int ratio) {
        ParameterSettingValues settingValues = ParameterSettingValues.instance();
        int compileRes = 1080;
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        Point size = new Point();
        if (ratio == NvAsset.AspectRatio_16v9) {
            size.set(compileRes * 16 / 9, compileRes);
        } else if (ratio == NvAsset.AspectRatio_1v1) {
            size.set(compileRes, compileRes);
        } else if (ratio == NvAsset.AspectRatio_9v16) {
            size.set(compileRes, compileRes * 16 / 9);
        } else if (ratio == NvAsset.AspectRatio_3v4) {
            size.set(compileRes, compileRes * 4 / 3);
        } else if (ratio == NvAsset.AspectRatio_4v3) {
            size.set(compileRes * 4 / 3, compileRes);
        } else if (ratio == NvAsset.AspectRatio_21v9) {
            size.set(compileRes * 21 / 9, compileRes);
        } else if (ratio == NvAsset.AspectRatio_9v21) {
            size.set(compileRes, compileRes * 21 / 9);
        } else if (ratio == NvAsset.AspectRatio_18v9) {
            size.set(compileRes * 18 / 9, compileRes);
        } else if (ratio == NvAsset.AspectRatio_9v18) {
            size.set(compileRes, compileRes * 18 / 9);
        } else if (ratio == NvAsset.AspectRatio_7v6) {
            size.set(compileRes * 7 / 6, compileRes);
        } else if (ratio == NvAsset.AspectRatio_6v7) {
            size.set(compileRes, compileRes * 7 / 6);
        } else {
            size.set(1280, 720);
        }
        videoEditRes.imageWidth = size.x;
        videoEditRes.imageHeight = size.y;
        videoEditRes.bitDepth = settingValues.getBitDepth();
        Logger.d("getVideoEditResolution   ", videoEditRes.imageWidth + "     " + videoEditRes.imageHeight);
        return videoEditRes;
    }


    public static int getCustomHeight(int width, int height) {
        ParameterSettingValues settingValues = ParameterSettingValues.instance();
        int compileHeight = settingValues.getCompileVideoRes();
        int resultHeight = 0;
        if (width > height) {
            int result = (int) (compileHeight * 1.0f / height * width);
            if (result > 3840) {
                resultHeight = (int) (3840 * 1.0f / width * height);
            } else {
                resultHeight = compileHeight;
            }
        } else if (width == height) {
            resultHeight = compileHeight;
        } else {
            int result = (int) (compileHeight * 1.0f / width * height);
            if (result > 3840) {
                resultHeight = 3840;
            } else {
                resultHeight = result;
            }
        }
        if (resultHeight % 2 != 0) {
            resultHeight -= 1;
        }
        return resultHeight;
    }


    public static int getCustomWidth(int width, int height) {
        ParameterSettingValues settingValues = ParameterSettingValues.instance();
        int compileHeight = settingValues.getCompileVideoRes();
        int resultWidth = 0;
        if (width > height) {
            int result = (int) (compileHeight * 1.0f / height * width);
            if (result > 3840) {
                resultWidth = 3840;
            } else {
                resultWidth = result;
            }
        } else if (width == height) {
            resultWidth = compileHeight;
        } else {
            int result = (int) (compileHeight * 1.0f / width * height);
            if (result > 3840) {
                resultWidth = (int) (3840 * 1.0f / height * width);
            } else {
                resultWidth = compileHeight;
            }
        }
        if (resultWidth % 2 != 0) {
            resultWidth -= 1;
        }
        return resultWidth;
    }

    public static NvsSize getLiveWindowSize(NvsSize timelineSize, NvsSize parentSize, boolean fullScreen) {
        if (timelineSize.height > timelineSize.width && fullScreen)
            return parentSize;

        NvsSize size = new NvsSize(timelineSize.width, timelineSize.height);

        float scaleWidth = (float) parentSize.width / timelineSize.width;
        float scaleHeight = (float) parentSize.height / timelineSize.height;
        if (scaleWidth < scaleHeight) {
            int width = parentSize.width;
            size.height = width * size.height / size.width;
            size.width = width;
            if (size.height > parentSize.height)
                size.height = parentSize.height;
        } else {
            int height = parentSize.height;
            size.width = height * size.width / size.height;
            size.height = height;

            if (size.width > parentSize.width)
                size.width = parentSize.width;
        }
        return size;
    }

    /**
     * 获取json文件的配置信息更新素材
     * <p>
     * Get the configuration information of the json file to update the material
     */
    public static boolean getBundleFilterInfoFromJson(Context context, List<NvAsset> assetArrayList, String bundlePath) {
        if (context == null) {
            return false;
        }
        if (assetArrayList == null || bundlePath == null || bundlePath.isEmpty()) {
            return false;
        }
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open(bundlePath), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            JSONObject data = new JSONObject(builder.toString());
            JSONArray array = data.getJSONArray("fx");
            for (int i = 0; i < array.length(); i++) {
                JSONObject role = array.getJSONObject(i);
                String id = role.getString("package_id");
                String name = "";
                if (isZh(context)) {
                    name = role.getString("name");
                } else {
                    name = role.getString("en_name");
                }
                int rank = role.getInt("rank");
                int ratio = role.getInt("aspect_ratio");

                for (int j = 0; j < assetArrayList.size(); ++j) {
                    NvAsset assetItem = assetArrayList.get(j);
                    if (assetItem == null) {
                        continue;
                    }
                    if (!assetItem.isReserved) {
                        continue;
                    }
                    if (assetItem.uuid == null || assetItem.uuid.isEmpty()) {
                        continue;
                    }
                    if (assetItem.uuid.contains(id)) {
                        assetItem.name = name;
                        assetItem.aspectRatio = ratio;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            return true;
        } else {
            return false;
        }
    }

    public static List<NvAsset> getBundleFilterInfoFromJsonExt(Context context, List<NvAsset> assetArrayList, String bundlePath) {
        if (context == null || assetArrayList == null || bundlePath == null || bundlePath.isEmpty()) {
            return assetArrayList;
        }

        List<NvAsset> showAssetArrayList = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open(bundlePath), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            JSONObject data = new JSONObject(builder.toString());
            JSONArray array = data.getJSONArray("fx");
            for (int j = 0; j < assetArrayList.size(); ++j) {
                NvAsset assetItem = assetArrayList.get(j);
                if (assetItem == null) {
                    continue;
                }
                if (assetItem.uuid == null || assetItem.uuid.isEmpty()) {
                    continue;
                }
                for (int i = 0; i < array.length(); i++) {
                    JSONObject role = array.getJSONObject(i);
                    String id = role.getString("package_id");
                    String name = role.getString("name");
                    int ratio = role.getInt("aspect_ratio");
                    if (assetItem.uuid.equals(id)) {//只显示json文件里包含的滤镜数据
                        if (assetItem.isReserved) {//bundle Data
                            assetItem.name = name;
                            assetItem.aspectRatio = ratio;
                            String coverPath = com.meishe.utils.PathUtils.getAssetFileBySuffixPic("particle/touch/" + id);
                            ;
                            assetItem.coverUrl = "file:///android_asset/" + coverPath;//加载assets/particle/touch文件夹下的图片
                        }
                        break;
                    }
                }
                showAssetArrayList.add(assetItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return showAssetArrayList;
    }

    /*
     * 获取所有权限列表(相机权限，麦克风权限，存储权限)
     * Get a list of all permissions (camera permissions, microphone permissions, storage permissions)
     * */
    public static List<String> getAllPermissionsList() {
        ArrayList<String> newList = new ArrayList<>();
        newList.addAll(getCameraPermissionsList());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            newList.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
//        }
        newList.addAll(Arrays.asList(READ_EXTERNAL_STORAGE));
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            newList.add(Manifest.permission.ACCESS_MEDIA_LOCATION);
        }*/
        return newList;
    }

    public static List<String> getCameraPermissionsList() {
        ArrayList<String> newList = new ArrayList<>();
        newList.add(Manifest.permission.CAMERA);
        newList.add(Manifest.permission.RECORD_AUDIO);
        return newList;
    }

    public static List<String> getWriteReadPermission() {
        ArrayList<String> newList = new ArrayList<>();
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            newList.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
//        }
        newList.addAll(Arrays.asList(READ_EXTERNAL_STORAGE));
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            newList.add(Manifest.permission.ACCESS_MEDIA_LOCATION);
        }*/
        return newList;
    }

    public static void setPermissionsSetting(Context context, OnPermissionsSettingListener listener) {
        String title = String.format(context.getResources().getString(R.string.permission_title),
                context.getResources().getString(R.string.permission_storage));
        String content = String.format(context.getResources().getString(R.string.permission_message)
                , context.getResources().getString(R.string.permission_storage)
                , context.getResources().getString(R.string.permission_storage));
        TipsPop mPermissionsPop = TipsPop.create(context, true)
                .setDefaultTipsTitle(title)
                .setDefaultTipsContent(content)
                .setTipsDefaultConfirm(context.getResources().getString(R.string.permission_set));
        mPermissionsPop.setOnTipsPopListener(new TipsPop.OnTipsPopListener() {
            @Override
            public void onTipsCancel() {
                if (null != listener) {
                    listener.onSettingCancel();
                }
            }

            @Override
            public void onTipsConfirm() {
                if (null != listener) {
                    listener.onSettingConfirm();
                }
                Util.startAppSettings(context);
                mPermissionsPop.dismiss();
            }
        });
        mPermissionsPop.show();
    }

    /**
     * 读取图片的旋转的角度
     * <p>
     * Read the rotation angle of the picture
     *
     * @param path Picture absolute path
     * @return Picture rotation angle
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            /*
             * 从指定路径下读取图片，并获取其EXIF信息
             * Read the picture from the specified path and get its EXIF information
             * */
            ExifInterface exifInterface = new ExifInterface(path);
            /*
             * 获取图片的旋转信息
             * Get image rotation information
             * */
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static String loadFromSDFile(String fname) {
        fname = "/" + fname;
        String result = null;
        try {
            File f = new File(fname);
            int length = (int) f.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result = new String(buff, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to read file: " + fname);
        }
        return result;
    }

    /**
     * 判断点是否在路境内
     * Determine if the point is within the road
     *
     * @param point
     * @param path
     * @return
     */
    public static boolean isPointInPath(Point point, Path path) {
        Region re = new Region();
        //构造一个区域对象，左闭右开的。 Construct a region object, closed left and open right.
        RectF r = new RectF();
        //计算控制点的边界 Calculate the boundary of control points
        path.computeBounds(r, true);
        //设置区域路径和剪辑描述的区域 Sets the region path and clips the region described
        re.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        //判断触摸点是否在封闭的path内 在返回true 不在返回false Determine if a touchpoint is inside a closed path and return true instead of false

        return re.contains(point.x, point.y);
    }

    /**
     * 判断一次按下抬起手指是否为点击事件
     * Determine whether a press to lift the finger is a click event
     *
     * @param downPointF
     * @param upPointF
     * @param duration
     * @return
     */
    public static boolean isClickTouch(PointF downPointF, PointF upPointF, long duration) {
        if (downPointF == null || upPointF == null || duration > 100) return false;
        if (Math.abs(downPointF.x - upPointF.x) > 5 || Math.abs(downPointF.y - upPointF.y) > 5) {
            return false;
        }
        return true;
    }

    /**
     * 计算两个值的比值（最小值/最大值）
     * Calculate the ratio of two values (minimum/maximum)
     *
     * @param sizeA A
     * @param sizeB B
     * @return float
     */
    public static float getSpecificValue(float sizeA, float sizeB) {
        return (sizeA > sizeB) ? (sizeB / sizeA) : (sizeA / sizeB);
    }

    /**
     * 启动应用的设置
     * Launch app settings
     */
    public static void startAppSettings(Context activity) {
        if (null == activity) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }

    public static String formatTimeStrWithUs(long us) {
        int second = (int) (us / 1000000.0);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        return hh > 0 ? String.format("%02d:%02d:%02d", hh, mm, ss) : String.format("%02d:%02d", mm, ss);
    }

    public static String getLocaleLanguage() {
        Locale locale = Utils.getApp().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        switch (language) {
            case "ar":
            case "de":
            case "el":
            case "es":
            case "fi":
            case "fr":
            case "he":
            case "hi":
            case "id":
            case "it":
            case "ja":
            case "ko":
            case "nl":
            case "pl":
            case "pt":
            case "ru":
            case "sv":
            case "tr":
                break;
            case "zh":
                language = "zh-cn";
                break;
            default:
                language = "en";
                break;
        }
        return language;
    }
}
