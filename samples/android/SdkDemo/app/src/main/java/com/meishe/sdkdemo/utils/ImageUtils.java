package com.meishe.sdkdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static void setImageByPath(ImageView imageView, String path) {

        RequestOptions options = new RequestOptions().centerCrop()
                .placeholder(R.mipmap.icon_feed_back_pic)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(MSApplication.getContext())
                .asBitmap()
                .load(path)
                .apply(options)
                .into(imageView);
    }


    public static void setImageByPathAndWidth(ImageView imageView, String path, int width) {
        RequestOptions options = new RequestOptions().centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(width, width);
        Glide.with(MSApplication.getContext())
                .asBitmap()
                .load(path)
                .apply(options)
                .into(imageView);
    }

    /**
     * 图片转化成base64字符串,将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     * The image is converted into a base64 string, the image file is converted into a byte array string, and the Base64 encoding process is carried out
     * @param imgFile
     * @return
     */
    public static String getImageBase64Str(String imgFile) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组 Read the picture byte array
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码 Encodes the byte array Base64
        return zipBase64(Base64.encodeToString(data, Base64.NO_CLOSE));
    }


    /**
     * 字符串的压缩
     * String compression
     * @param base64 待压缩的字符串 The string to be compressed
     * @return 返回压缩后的字符串 Returns the compressed string
     * @throws IOException
     */
    public static String zipBase64(String base64) {
        if (null == base64 || base64.length() <= 0) {
            return base64;
        }
        // 创建一个新的 byte 数组输出流 Creates a new byte array output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流 Create a new output stream with the default buffer size
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            // 将 b.length 个字节写入此输出流 Write b.ls bytes to this output stream
            gzip.write(base64.getBytes());
            gzip.close();
            // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
            //Converts the contents of the buffer to a string by decoding the bytes, using the specified charsetName
            return out.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getColorImageByColor(Context context, String color) {
        String colorFolder = PathUtils.getColorPath(context);
        String colorFilePath = colorFolder + File.separator + color + ".png";
        File file = new File(colorFilePath);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    public static String parseViewToBitmap(Context context, View view, String color) {
        String colorFolder = PathUtils.getColorPath(context);
        String colorFilePath = colorFolder + File.separator + color + ".png";
        File file = new File(colorFilePath);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        view.draw(c);
        saveBitmap(bmp, colorFilePath);
        return colorFilePath;
    }

    public static void saveBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream fos;
        try {
            File file = new File(filePath);
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(Bitmap bitmap, String filePath, boolean needClear) {
        FileOutputStream fos;
        try {
            File file = new File(filePath);
            if (!file.exists() && !file.mkdirs()) {
                return;
            }
            if (needClear) {
                clearDir(file.getParentFile(), ".png");
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Logger.e(TAG, "saveBitmap -> error: " + e.fillInStackTrace());
        }
    }

    private static void clearDir(File file, String suffix) {
        if (!file.isDirectory()) {
            return;
        }
        File list[] = file.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.getName().contains(suffix)) {
                    Logger.d(TAG, "clearDir: name = " + f.getName());
                    f.delete();
                }
            }
        }
    }

    public static void loadWebpUrl(Context context, Object imgUrl, Transformation<Bitmap> transformation
            , RequestOptions options, ImageView imageView) {
        if (imgUrl != null && imgUrl instanceof String && !TextUtils.isEmpty((String) imgUrl) && ((String) imgUrl).endsWith(".webp")) {
            WebpDrawableTransformation webpDrawableTransformation = new WebpDrawableTransformation(transformation);
            Glide.with(context)
                    .load(imgUrl)
                    .apply(options)
                    .optionalTransform(WebpDrawable.class, webpDrawableTransformation)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .apply(options)
                    .into(imageView);
        }
    }

    /**
     * 通知系统刷新相册
     * Notifies the system to refresh the album
     * @param context
     * @param fileAbsolutePath
     */
    public static void refreshAlbum(Context context, String fileAbsolutePath) {
        if (context == null || TextUtils.isEmpty(fileAbsolutePath)) return;
        File imgFile = new File(fileAbsolutePath);
        Uri uri = Uri.fromFile(imgFile);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
