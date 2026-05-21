package com.meishe.base.utils;


import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/12
 *     desc  : utils about image
 * </pre>
 * 图片工具类
 * ImageUtil
 */
public final class ImageUtils {

    private ImageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    public static byte[] bitmap2Bytes(final Bitmap bitmap) {
        return bitmap2Bytes(bitmap, CompressFormat.PNG, 100);
    }


    public static byte[] bitmap2Bytes(final Bitmap bitmap, final CompressFormat format, int quality) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, baos);
        return baos.toByteArray();
    }

    /**
     * Bytes to bitmap.
     * 字节的位图
     *
     * @param bytes The bytes. 字节
     * @return bitmap bitmap 位图
     */
    public static Bitmap bytes2Bitmap(final byte[] bytes) {
        return (bytes == null || bytes.length == 0)
                ? null
                : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Drawable to bitmap.
     * 可拉的位图
     *
     * @param drawable The drawable.
     * @return bitmap bitmap
     */
    public static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static Drawable bitmap2Drawable(final Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(Utils.getApp().getResources(), bitmap);
    }


    public static byte[] drawable2Bytes(final Drawable drawable) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable));
    }


    public static byte[] drawable2Bytes(final Drawable drawable, final CompressFormat format, int quality) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable), format, quality);
    }


    public static Drawable bytes2Drawable(final byte[] bytes) {
        return bitmap2Drawable(bytes2Bitmap(bytes));
    }

    /**
     * View to bitmap.
     * 视图以位图
     *
     * @param view The view. 视图
     * @return bitmap bitmap
     */
    public static Bitmap view2Bitmap(final View view) {
        if (view == null) {
            return null;
        }
        boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
        boolean willNotCacheDrawing = view.willNotCacheDrawing();
        view.setDrawingCacheEnabled(true);
        view.setWillNotCacheDrawing(false);
        Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (null == drawingCache) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            drawingCache = view.getDrawingCache();
            if (drawingCache != null) {
                bitmap = Bitmap.createBitmap(drawingCache);
            } else {
                bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
            }
        } else {
            bitmap = Bitmap.createBitmap(drawingCache);
        }
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCacheDrawing);
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        return bitmap;
    }

    /**
     * Return bitmap.
     * 返回位图
     *
     * @param file The file. 该文件
     * @return bitmap bitmap 位图
     */
    public static Bitmap getBitmap(final File file) {
        if (file == null) {
            return null;
        }
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /**
     * Return bitmap.
     * 返回位图
     *
     * @param file      The file. 该文件
     * @param maxWidth  The maximum width. 最大的宽度
     * @param maxHeight The maximum height. 适用高度
     * @return bitmap bitmap
     */
    public static Bitmap getBitmap(final File file, final int maxWidth, final int maxHeight) {
        if (file == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    /**
     * Return bitmap.
     * 返回位图
     *
     * @param filePath The path of file. 文件的路径
     * @return bitmap bitmap 位图
     */
    public static Bitmap getBitmap(final String filePath) {
        if (StringUtils.isSpace(filePath)) {
            return null;
        }
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * Return bitmap.
     * 返回位图
     *
     * @param filePath  The path of file. 文件路径
     * @param maxWidth  The maximum width. 最大的宽度
     * @param maxHeight The maximum height. 适用高度
     * @return bitmap bitmap
     */
    public static Bitmap getBitmap(final String filePath, final int maxWidth, final int maxHeight) {
        if (StringUtils.isSpace(filePath)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    public static Bitmap getBitmap(final InputStream is) {
        if (is == null) {
            return null;
        }
        return BitmapFactory.decodeStream(is);
    }


    public static Bitmap getBitmap(final InputStream is, final int maxWidth, final int maxHeight) {
        if (is == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, options);
    }


    public static Bitmap getBitmap(final byte[] data, final int offset) {
        if (data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, offset, data.length);
    }


    public static Bitmap getBitmap(final byte[] data,
                                   final int offset,
                                   final int maxWidth,
                                   final int maxHeight) {
        if (data.length == 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, data.length, options);
    }

    public static Bitmap getBitmap(@DrawableRes final int resId) {
        Drawable drawable = ContextCompat.getDrawable(Utils.getApp(), resId);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static Bitmap getBitmap(@DrawableRes final int resId,
                                   final int maxWidth,
                                   final int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        final Resources resources = Utils.getApp().getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }


    public static Bitmap getBitmap(final FileDescriptor fd) {
        if (fd == null) {
            return null;
        }
        return BitmapFactory.decodeFileDescriptor(fd);
    }

    public static Bitmap getBitmap(final FileDescriptor fd,
                                   final int maxWidth,
                                   final int maxHeight) {
        if (fd == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }


    public static Bitmap drawColor(@NonNull final Bitmap src, @ColorInt final int color) {
        return drawColor(src, color, false);
    }


    public static Bitmap drawColor(@NonNull final Bitmap src,
                                   @ColorInt final int color,
                                   final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        Canvas canvas = new Canvas(ret);
        canvas.drawColor(color, PorterDuff.Mode.DARKEN);
        return ret;
    }

    /**
     * Return the scaled bitmap.
     * 返回缩放位图
     *
     * @param src       The source of bitmap. 位图的来源
     * @param newWidth  The new width. 新宽度
     * @param newHeight The new height. 新高度
     * @return the scaled bitmap 按比例缩小的位图
     */
    public static Bitmap scale(final Bitmap src, final int newWidth, final int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }


    public static Bitmap scale(final Bitmap src,
                               final int newWidth,
                               final int newHeight,
                               final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }


    public static Bitmap scale(final Bitmap src, final float scaleWidth, final float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }


    public static Bitmap scale(final Bitmap src,
                               final float scaleWidth,
                               final float scaleHeight,
                               final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }


    public static Bitmap clip(final Bitmap src,
                              final int x,
                              final int y,
                              final int width,
                              final int height) {
        return clip(src, x, y, width, height, false);
    }

    /**
     * Return the clipped bitmap.
     * 返回剪切位图
     *
     * @param src     The source of bitmap. 位图的来源
     * @param x       The x coordinate of the first pixel. 第一个像素的x坐标
     * @param y       The y coordinate of the first pixel. 第一个像素的y坐标
     * @param width   The width. 宽窄
     * @param height  The height. 高度
     * @param recycle True to recycle the source of bitmap, false otherwise. 真回收位图源，假否则回收位图源
     * @return the clipped bitmap 夹位图
     */
    public static Bitmap clip(final Bitmap src,
                              final int x,
                              final int y,
                              final int width,
                              final int height,
                              final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(src, x, y, width, height);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * Return the skewed bitmap.
     * 返回倾斜的位图
     *
     * @param src The source of bitmap. 位图的来源
     * @param kx  The skew factor of x. x的斜度因子
     * @param ky  The skew factor of y. y的斜度因子
     * @return the skewed bitmap 倾斜的位图
     */
    public static Bitmap skew(final Bitmap src, final float kx, final float ky) {
        return skew(src, kx, ky, 0, 0, false);
    }


    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final boolean recycle) {
        return skew(src, kx, ky, 0, 0, recycle);
    }


    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final float px,
                              final float py) {
        return skew(src, kx, ky, px, py, false);
    }


    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final float px,
                              final float py,
                              final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }


    public static Bitmap rotate(final Bitmap src,
                                final int degrees,
                                final float px,
                                final float py) {
        return rotate(src, degrees, px, py, false);
    }

    /**
     * Return the rotated bitmap.
     * 返回旋转后的位图
     *
     * @param src     The source of bitmap. 位图的来源
     * @param degrees The number of degrees. 度数
     * @param px      The x coordinate of the pivot point. 轴心点的x坐标
     * @param py      The y coordinate of the pivot point. 轴心点的y坐标
     * @param recycle True to recycle the source of bitmap, false otherwise. 真回收位图源，假否则回收位图源。
     * @return the rotated bitmap 旋转位图
     */
    public static Bitmap rotate(final Bitmap src,
                                final int degrees,
                                final float px,
                                final float py,
                                final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        if (degrees == 0) {
            return src;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }


    public static int getRotateDegree(final String filePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Return the round bitmap.
     * 返回圆形位图
     *
     * @param src The source of bitmap. 位图的来源
     * @return the round bitmap 圆图
     */
    public static Bitmap toRound(final Bitmap src) {
        return toRound(src, 0, 0, false);
    }


    public static Bitmap toRound(final Bitmap src, final boolean recycle) {
        return toRound(src, 0, 0, recycle);
    }


    public static Bitmap toRound(final Bitmap src,
                                 @IntRange(from = 0) int borderSize,
                                 @ColorInt int borderColor) {
        return toRound(src, borderSize, borderColor, false);
    }


    public static Bitmap toRound(final Bitmap src,
                                 @IntRange(from = 0) int borderSize,
                                 @ColorInt int borderColor,
                                 final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        float center = size / 2f;
        RectF rectF = new RectF(0, 0, width, height);
        rectF.inset((width - size) / 2f, (height - size) / 2f);
        Matrix matrix = new Matrix();
        matrix.setTranslate(rectF.left, rectF.top);
        if (width != height) {
            matrix.preScale((float) size / width, (float) size / height);
        }
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        canvas.drawRoundRect(rectF, center, center, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            float radius = center - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap toRoundCorner(final Bitmap src, final float radius) {
        return toRoundCorner(src, radius, 0, 0, false);
    }

    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       final boolean recycle) {
        return toRoundCorner(src, radius, 0, 0, recycle);
    }

    /**
     * Return the round corner bitmap.
     * 返回圆角位图
     *
     * @param src         The source of bitmap. 位图的来源
     * @param radius      The radius of corner. 转角半径
     * @param borderSize  The size of border. 边框的大小
     * @param borderColor The color of border. 边框的颜色
     * @return the round corner bitmap 圆角位图
     */
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       @IntRange(from = 0) int borderSize,
                                       @ColorInt int borderColor) {
        return toRoundCorner(src, radius, borderSize, borderColor, false);
    }


    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       @IntRange(from = 0) int borderSize,
                                       @ColorInt int borderColor,
                                       final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        RectF rectF = new RectF(0, 0, width, height);
        float halfBorderSize = borderSize / 2f;
        rectF.inset(halfBorderSize, halfBorderSize);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }


    public static Bitmap addCornerBorder(final Bitmap src,
                                         @IntRange(from = 1) final int borderSize,
                                         @ColorInt final int color,
                                         @FloatRange(from = 0) final float cornerRadius) {
        return addBorder(src, borderSize, color, false, cornerRadius, false);
    }


    public static Bitmap addCornerBorder(final Bitmap src,
                                         @IntRange(from = 1) final int borderSize,
                                         @ColorInt final int color,
                                         @FloatRange(from = 0) final float cornerRadius,
                                         final boolean recycle) {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle);
    }


    public static Bitmap addCircleBorder(final Bitmap src,
                                         @IntRange(from = 1) final int borderSize,
                                         @ColorInt final int color) {
        return addBorder(src, borderSize, color, true, 0, false);
    }


    public static Bitmap addCircleBorder(final Bitmap src,
                                         @IntRange(from = 1) final int borderSize,
                                         @ColorInt final int color,
                                         final boolean recycle) {
        return addBorder(src, borderSize, color, true, 0, recycle);
    }


    private static Bitmap addBorder(final Bitmap src,
                                    @IntRange(from = 1) final int borderSize,
                                    @ColorInt final int color,
                                    final boolean isCircle,
                                    final float cornerRadius,
                                    final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        int width = ret.getWidth();
        int height = ret.getHeight();
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderSize);
        if (isCircle) {
            float radius = Math.min(width, height) / 2f - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        } else {
            int halfBorderSize = borderSize >> 1;
            RectF rectF = new RectF(halfBorderSize, halfBorderSize,
                    width - halfBorderSize, height - halfBorderSize);
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        }
        return ret;
    }

    /**
     * Return the bitmap with reflection.
     * 返回带有反射的位图
     *
     * @param src              The source of bitmap. 位图的来源
     * @param reflectionHeight The height of reflection. 反射高度
     * @return the bitmap with reflection 具有反射的位图
     */
    public static Bitmap addReflection(final Bitmap src, final int reflectionHeight) {
        return addReflection(src, reflectionHeight, false);
    }


    public static Bitmap addReflection(final Bitmap src,
                                       final int reflectionHeight,
                                       final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        final int REFLECTION_GAP = 0;
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionBitmap = Bitmap.createBitmap(src, 0, srcHeight - reflectionHeight,
                srcWidth, reflectionHeight, matrix, false);
        Bitmap ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(reflectionBitmap, 0, srcHeight + REFLECTION_GAP, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient shader = new LinearGradient(
                0, srcHeight,
                0, ret.getHeight() + REFLECTION_GAP,
                0x70FFFFFF,
                0x00FFFFFF,
                Shader.TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, srcHeight + REFLECTION_GAP, srcWidth, ret.getHeight(), paint);
        if (!reflectionBitmap.isRecycled()) {
            reflectionBitmap.recycle();
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }


    public static Bitmap addTextWatermark(final Bitmap src,
                                          final String content,
                                          final int textSize,
                                          @ColorInt final int color,
                                          final float x,
                                          final float y) {
        return addTextWatermark(src, content, textSize, color, x, y, false);
    }

    /**
     * Return the bitmap with text watermarking.
     * 返回带有文本水印的位图
     *
     * @param src      The source of bitmap. 位图的来源
     * @param content  The content of text. 文本内容
     * @param textSize The size of text. 文本的大小
     * @param color    The color of text. 文字的颜色
     * @param x        The x coordinate of the first pixel. 第一个像素的x坐标
     * @param y        The y coordinate of the first pixel. 第一个像素的y坐标
     * @param recycle  True to recycle the source of bitmap, false otherwise. 真回收位图源，假否则回收位图源
     * @return the bitmap with text watermarking 带有文本水印的位图
     */
    public static Bitmap addTextWatermark(final Bitmap src,
                                          final String content,
                                          final float textSize,
                                          @ColorInt final int color,
                                          final float x,
                                          final float y,
                                          final boolean recycle) {
        if (isEmptyBitmap(src) || content == null) {
            return null;
        }
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }


    public static Bitmap addImageWatermark(final Bitmap src,
                                           final Bitmap watermark,
                                           final int x, final int y,
                                           final int alpha) {
        return addImageWatermark(src, watermark, x, y, alpha, false);
    }


    public static Bitmap addImageWatermark(final Bitmap src,
                                           final Bitmap watermark,
                                           final int x,
                                           final int y,
                                           final int alpha,
                                           final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = src.copy(src.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Canvas canvas = new Canvas(ret);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * Return the alpha bitmap.
     * 返回alpha位图
     *
     * @param src The source of bitmap. 位图的来源
     * @return the alpha bitmap
     */
    public static Bitmap toAlpha(final Bitmap src) {
        return toAlpha(src, false);
    }


    public static Bitmap toAlpha(final Bitmap src, final Boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = src.extractAlpha();
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * Return the gray bitmap.
     * 返回灰色位图
     *
     * @param src The source of bitmap. 位图的来源
     * @return the gray bitmap 灰色的位图
     */
    public static Bitmap toGray(final Bitmap src) {
        return toGray(src, false);
    }


    public static Bitmap toGray(final Bitmap src, final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(src, 0, 0, paint);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * Return the blur bitmap fast.
     * <p>zoom out, blur, zoom in</p>
     * 快速返回模糊位图
     *
     * @param src    The source of bitmap. 位图的来源
     * @param scale  The scale(0...1). 规模(0。1)
     * @param radius The radius(0...25). 半径(0…25)
     * @return the blur bitmap 模糊的位图
     */
    public static Bitmap fastBlur(final Bitmap src,
                                  @FloatRange(
                                          from = 0, to = 1, fromInclusive = false
                                  ) final float scale,
                                  @FloatRange(
                                          from = 0, to = 25, fromInclusive = false
                                  ) final float radius) {
        return fastBlur(src, scale, radius, false, false);
    }


    public static Bitmap fastBlur(final Bitmap src,
                                  @FloatRange(
                                          from = 0, to = 1, fromInclusive = false
                                  ) final float scale,
                                  @FloatRange(
                                          from = 0, to = 25, fromInclusive = false
                                  ) final float radius,
                                  final boolean recycle) {
        return fastBlur(src, scale, radius, recycle, false);
    }


    public static Bitmap fastBlur(final Bitmap src,
                                  @FloatRange(
                                          from = 0, to = 1, fromInclusive = false
                                  ) final float scale,
                                  @FloatRange(
                                          from = 0, to = 25, fromInclusive = false
                                  ) final float radius,
                                  final boolean recycle,
                                  final boolean isReturnScale) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap scaleBitmap =
                Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas();
        PorterDuffColorFilter filter = new PorterDuffColorFilter(
                Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(filter);
        canvas.scale(scale, scale);
        canvas.drawBitmap(scaleBitmap, 0, 0, paint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            scaleBitmap = renderScriptBlur(scaleBitmap, radius, recycle);
        } else {
            scaleBitmap = stackBlur(scaleBitmap, (int) radius, recycle);
        }
        if (scale == 1 || isReturnScale) {
            if (recycle && !src.isRecycled() && scaleBitmap != src) {
                src.recycle();
            }
            return scaleBitmap;
        }
        Bitmap ret = Bitmap.createScaledBitmap(scaleBitmap, width, height, true);
        if (!scaleBitmap.isRecycled()) {
            scaleBitmap.recycle();
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap renderScriptBlur(final Bitmap src,
                                          @FloatRange(
                                                  from = 0, to = 25, fromInclusive = false
                                          ) final float radius) {
        return renderScriptBlur(src, radius, false);
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap renderScriptBlur(final Bitmap src,
                                          @FloatRange(
                                                  from = 0, to = 25, fromInclusive = false
                                          ) final float radius,
                                          final boolean recycle) {
        RenderScript rs = null;
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        try {
            rs = RenderScript.create(Utils.getApp());
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(rs,
                    ret,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blurScript.setInput(input);
            blurScript.setRadius(radius);
            blurScript.forEach(output);
            output.copyTo(ret);
        } finally {
            if (rs != null) {
                rs.destroy();
            }
        }
        return ret;
    }

    public static Bitmap stackBlur(final Bitmap src, final int radius) {
        return stackBlur(src, radius, false);
    }


    public static Bitmap stackBlur(final Bitmap src, int radius, final boolean recycle) {
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        if (radius < 1) {
            radius = 1;
        }
        int w = ret.getWidth();
        int h = ret.getHeight();

        int[] pix = new int[w * h];
        ret.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        ret.setPixels(pix, 0, w, 0, 0, w, h);
        return ret;
    }

    /**
     * Save the bitmap.
     * 保存位图
     *
     * @param src      The source of bitmap. 位图的来源
     * @param filePath The path of file 文件路径.
     * @param format   The format of the image. 图像的格式
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final String filePath,
                               final CompressFormat format) {
        return save(src, filePath, format, 100, false);
    }


    public static boolean save(final Bitmap src, final File file, final CompressFormat format) {
        return save(src, file, format, 100, false);
    }


    public static boolean save(final Bitmap src,
                               final String filePath,
                               final CompressFormat format,
                               final boolean recycle) {
        return save(src, filePath, format, 100, recycle);
    }


    public static boolean save(final Bitmap src,
                               final File file,
                               final CompressFormat format,
                               final boolean recycle) {
        return save(src, file, format, 100, recycle);
    }


    public static boolean save(final Bitmap src,
                               final String filePath,
                               final CompressFormat format,
                               final int quality) {
        return save(src, FileUtils.getFileByPath(filePath), format, quality, false);
    }

    public static boolean save(final Bitmap src,
                               final File file,
                               final CompressFormat format,
                               final int quality) {
        return save(src, file, format, quality, false);
    }


    public static boolean save(final Bitmap src,
                               final String filePath,
                               final CompressFormat format,
                               final int quality,
                               final boolean recycle) {
        return save(src, FileUtils.getFileByPath(filePath), format, quality, recycle);
    }


    public static boolean save(final Bitmap src,
                               final File file,
                               final CompressFormat format,
                               final int quality,
                               final boolean recycle) {
        if (isEmptyBitmap(src)) {
            Log.e("ImageUtils", "bitmap is empty.");
            return false;
        }
        if (src.isRecycled()) {
            Log.e("ImageUtils", "bitmap is recycled.");
            return false;
        }
        if (!FileUtils.createFileByDeleteOldFile(file)) {
            Log.e("ImageUtils", "create or delete file <" + file + "> failed.");
            return false;
        }
        OutputStream os = null;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, quality, os);
            if (recycle && !src.isRecycled()) {
                src.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }


    @Nullable
    public static File save2Album(final Bitmap src,
                                  final CompressFormat format) {
        return save2Album(src, format, 100, false);
    }


    @Nullable
    public static File save2Album(final Bitmap src,
                                  final CompressFormat format,
                                  final boolean recycle) {
        return save2Album(src, format, 100, recycle);
    }


    @Nullable
    public static File save2Album(final Bitmap src,
                                  final CompressFormat format,
                                  final int quality) {
        return save2Album(src, format, quality, false);
    }

    @Nullable
    public static File save2Album(final Bitmap src,
                                  final CompressFormat format,
                                  final int quality,
                                  final boolean recycle) {
        String suffix = CompressFormat.JPEG.equals(format) ? "JPG" : format.name();
        String fileName = System.currentTimeMillis() + "_" + quality + "." + suffix;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File destFile = new File(picDir, Utils.getApp().getPackageName() + "/" + fileName);
            if (!save(src, destFile, format, quality, recycle)) {
                return null;
            }
            FileUtils.notifySystemToScan(destFile);
            return destFile;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            Uri contentUri;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else {
                contentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            }
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/" + Utils.getApp().getPackageName());
            Uri uri = Utils.getApp().getContentResolver().insert(contentUri, contentValues);
            if (uri == null) {
                return null;
            }
            OutputStream os = null;
            try {
                os = Utils.getApp().getContentResolver().openOutputStream(uri);
                src.compress(format, quality, os);
                return Utils.uri2File(uri, Utils.getApp());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Return whether it is a image according to the file name.
     * 根据文件名返回是否为图像
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isImage(final File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return isImage(file.getPath());
    }


    public static boolean isImage(final String filePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            return options.outWidth > 0 && options.outHeight > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return the type of image.
     * 返回图像的类型
     *
     * @param filePath The path of file. 文件的路径
     * @return the type of image 图像的类型
     */
    public static ImageType getImageType(final String filePath) {
        return getImageType(FileUtils.getFileByPath(filePath));
    }


    public static ImageType getImageType(final File file) {
        if (file == null) {
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            ImageType type = getImageType(is);
            if (type != null) {
                return type;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static ImageType getImageType(final InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[12];
            return is.read(bytes) != -1 ? getImageType(bytes) : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ImageType getImageType(final byte[] bytes) {
        String type = FileUtils.bytes2HexString(bytes).toUpperCase();
        if (type.contains("FFD8FF")) {
            return ImageType.TYPE_JPG;
        } else if (type.contains("89504E47")) {
            return ImageType.TYPE_PNG;
        } else if (type.contains("47494638")) {
            return ImageType.TYPE_GIF;
        } else if (type.contains("49492A00") || type.contains("4D4D002A")) {
            return ImageType.TYPE_TIFF;
        } else if (type.contains("424D")) {
            return ImageType.TYPE_BMP;
        } else if (type.startsWith("52494646") && type.endsWith("57454250")) {//524946461c57000057454250-12个字节
            return ImageType.TYPE_WEBP;
        } else if (type.contains("00000100") || type.contains("00000200")) {
            return ImageType.TYPE_ICO;
        } else {
            return ImageType.TYPE_UNKNOWN;
        }
    }

    private static boolean isJPEG(final byte[] b) {
        return b.length >= 2
                && (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(final byte[] b) {
        return b.length >= 6
                && b[0] == 'G' && b[1] == 'I'
                && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(final byte[] b) {
        return b.length >= 8
                && (b[0] == (byte) 137 && b[1] == (byte) 80
                && b[2] == (byte) 78 && b[3] == (byte) 71
                && b[4] == (byte) 13 && b[5] == (byte) 10
                && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(final byte[] b) {
        return b.length >= 2
                && (b[0] == 0x42) && (b[1] == 0x4d);
    }

    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // about compress  关于压缩
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Return the compressed bitmap using scale.
     * 使用比例返回压缩的位图
     *
     * @param src       The source of bitmap. 位图的来源
     * @param newWidth  The new width. 新宽度
     * @param newHeight The new height. 新高度
     * @return the compressed bitmap 压缩位图
     */
    public static Bitmap compressByScale(final Bitmap src,
                                         final int newWidth,
                                         final int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }


    public static Bitmap compressByScale(final Bitmap src,
                                         final int newWidth,
                                         final int newHeight,
                                         final boolean recycle) {
        return scale(src, newWidth, newHeight, recycle);
    }


    public static Bitmap compressByScale(final Bitmap src,
                                         final float scaleWidth,
                                         final float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }


    public static Bitmap compressByScale(final Bitmap src,
                                         final float scaleWidth,
                                         final float scaleHeight,
                                         final boolean recycle) {
        return scale(src, scaleWidth, scaleHeight, recycle);
    }

    public static byte[] compressByQuality(final Bitmap src,
                                           @IntRange(from = 0, to = 100) final int quality) {
        return compressByQuality(src, quality, false);
    }


    public static byte[] compressByQuality(final Bitmap src,
                                           @IntRange(from = 0, to = 100) final int quality,
                                           final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return bytes;
    }


    public static byte[] compressByQuality(final Bitmap src, final long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    /**
     * Return the compressed data using quality.
     * 使用质量返回压缩的数据
     *
     * @param src         The source of bitmap. 位图的来源
     * @param maxByteSize The maximum size of byte. 字节的最大大小
     * @param recycle     True to recycle the source of bitmap, false otherwise. 真回收位图源，假否则回收位图源
     * @return the compressed data using quality 压缩数据使用质量
     */
    public static byte[] compressByQuality(final Bitmap src,
                                           final long maxByteSize,
                                           final boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) {
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes;
        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray();
            } else {
                /*
                 * find the best quality using binary search
                 * 找到最好的质量使用二分搜索
                 * */
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return bytes;
    }


    public static Bitmap compressBySampleSize(final Bitmap src, final int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }


    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int sampleSize,
                                              final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int maxWidth,
                                              final int maxHeight) {
        return compressBySampleSize(src, maxWidth, maxHeight, false);
    }


    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int maxWidth,
                                              final int maxHeight,
                                              final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    public static int[] getSize(String filePath) {
        return getSize(FileUtils.getFileByPath(filePath));
    }


    public static int[] getSize(File file) {
        if (file == null) {
            return new int[]{0, 0};
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        return new int[]{opts.outWidth, opts.outHeight};
    }

    public static int calculateInSampleSize(final BitmapFactory.Options options,
                                            final int maxWidth,
                                            final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while (height > maxHeight || width > maxWidth) {
            height >>= 1;
            width >>= 1;
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    /**
     * The enum Image type.
     * 枚举映像类型
     */
    public enum ImageType {

        TYPE_JPG("jpg"),

        TYPE_PNG("png"),


        TYPE_GIF("gif"),


        TYPE_TIFF("tiff"),


        TYPE_BMP("bmp"),


        TYPE_WEBP("webp"),


        TYPE_ICO("ico"),

        TYPE_UNKNOWN("unknown");


        String value;

        ImageType(String value) {
            this.value = value;
        }


        public String getValue() {
            return value;
        }
    }
}
