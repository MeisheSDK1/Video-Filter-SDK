package com.meishe.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 图片加载工具类，主要第三方图片框架的简单封装
 * Picture loading tool class, the main third party picture frame simple packaging
 */
public class ImageLoader {
    /*
     * 默认drawable类型
     * The default drawable type
     * */
    public static final int TYPE_DEFAULT = 0;
    /*
     * bitmap类型
     * bitmap type
     * */
    public static final int TYPE_BITMAP = 1;
    /*
     * gif类型
     *  gif type
     * */
    public static final int TYPE_GIF = 2;

    /**
     * 加载图片
     * load picture
     *
     * @param context   上下文
     * @param url       网络图片地址
     * @param imageView 图片控件
     */
    public static void loadUrl(Context context, String url, ImageView imageView) {
        loadUrl(context, url, imageView, null, TYPE_DEFAULT);
    }

    /**
     * 加载DrawableRes
     * upload DrawableRes
     *
     * @param context   上下文
     * @param resId     图片本地Drawable
     * @param imageView 图片控件
     */
    public static void loadUrl(Context context, @DrawableRes int resId, ImageView imageView) {
        loadUrl(context, resId, imageView, null, TYPE_DEFAULT);
    }

    /**
     * 加载图片
     * load picture
     *
     * @param context   上下文
     * @param object    图片地址或者DrawableResId
     * @param imageView 图片控件
     * @param options   加载选项(可为空)
     */
    public static void loadUrl(Context context, Object object, ImageView imageView, @Nullable Options options) {
        loadUrl(context, object, imageView, options, TYPE_DEFAULT);
    }

    /**
     * 加载图片
     * load picture
     *
     * @param context   上下文
     * @param object    图片地址或者DrawableResId
     * @param imageView 图片控件
     * @param options   加载选项(可为空)
     * @param type      类型0默认drawable类型，1,是bitmap类型，2是gif类型
     */
    public static void loadUrl(Context context, Object object, ImageView imageView,
                               @Nullable Options options, int type) {
        if (Utils.isActivityAlive((Activity) context)) {
            RequestBuilder<?> requestBuilder;
            if (type == TYPE_BITMAP) {
                requestBuilder = Glide.with(context).asBitmap();
            } else if (type == TYPE_GIF) {
                requestBuilder = Glide.with(context).asGif();
            } else {
                requestBuilder = Glide.with(context).asDrawable();
            }
            if (options != null) {
                requestBuilder.load(object)
                        .apply(options.requestOptions)
                        .into(imageView);
            } else {
                requestBuilder.load(object)
                        .into(imageView);
            }
        }
    }

    /**
     * 唤醒图片请求
     * Wake up picture request进入翻译页面
     *
     * @param context the context 上下文
     */
    public static void resumeRequests(Context context) {
        Glide.with(context).resumeRequests();
    }

    /**
     * 暂停图片请求
     * Pause the image request
     *
     * @param context the context 上下文
     * @param isAll   the is all
     */
    public static void pauseRequests(Context context, boolean isAll) {
        if (!isAll) {
            Glide.with(context).pauseRequests();
        } else {
            Glide.with(context).pauseRequestsRecursive();
        }
    }

    /**
     * 清理图片内存缓存
     * Clean the image memory cache
     *
     * @param context the context 上下文
     */
    public static void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    /**
     * 清理图片磁盘缓存
     * Clean the image disk cache
     *
     * @param context the context 上下文
     */
    public static void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }


    /**
     * The type Options.
     * 类型的选择
     */
    public static class Options {
        RequestOptions requestOptions;

        public Options() {
            requestOptions = new RequestOptions();
        }

        /**
         * Error options.
         * 错误选项
         *
         * @param errorId the error id 错误id
         * @return the options
         */
        public Options error(int errorId) {
            requestOptions.error(errorId);
            return this;
        }

        public Options error(Drawable drawable) {
            requestOptions.error(drawable);
            return this;
        }

        /**
         * Placeholder options.
         * 占位符选项
         *
         * @param resourceId the resource id 资源标识
         * @return the  options
         */
        public Options placeholder(int resourceId) {
            requestOptions.placeholder(resourceId);
            return this;
        }

        /**
         * Placeholder options.
         * 占位符选项
         *
         * @param drawable the drawable 绘图
         * @return the options 选项
         */
        public Options placeholder(Drawable drawable) {
            requestOptions.placeholder(drawable);
            return this;
        }

        /**
         * Cache all options.
         * 缓存所有选项
         *
         * @param cacheAll the cache all 缓存所有
         * @return the options 选项
         */
        public Options cacheAll(boolean cacheAll) {
            requestOptions.diskCacheStrategy(cacheAll ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE);
            return this;
        }

        /**
         * Override options.
         * 覆盖选项
         *
         * @param size the size 大小
         * @return the options 选项
         */
        public Options override(int size) {
            requestOptions.override(size);
            return this;
        }

        /**
         * Override options.
         * 覆盖选项
         *
         * @param width  the width 宽
         * @param height the height 高
         * @return the options 选项
         */
        public Options override(int width, int height) {
            requestOptions.override(width, height);
            return this;
        }

        /**
         * Center crop options.
         * 中心作物的选择
         *
         * @return the options 选项
         */
        public Options centerCrop() {
            requestOptions.centerCrop();
            return this;
        }

        /**
         * 圆形图片
         * Circular images
         *
         * @return the options 选项
         */
        public Options circleCrop() {
            requestOptions.circleCrop();
            return this;
        }

        /**
         * 圆角图片，注意：如果是圆角图片，最好设置所需的Crop，防止模糊拉伸
         * Note: If it is a picture with rounded corners, it is better to set the required Crop to prevent fuzzy stretching
         *
         * @param roundingRadius the rounding radius 圆角半径
         * @return the options 选项
         */
        public Options roundedCorners(int roundingRadius) {
            requestOptions.transform(new RoundedCorners(roundingRadius));
            return this;
        }

        /**
         * 圆角图片
         * ImageView小，图片大的情况下使用,Glide的transform调用只有一次机会，这里可以根据需要是否先进行中心剪裁
         *
         * @param roundingRadius the rounding radius 圆角半径
         * @param centerCrop     true do center crop first先进行中心剪裁，false not不是
         */
        public Options roundedCornersSmall(int roundingRadius, boolean centerCrop) {
            requestOptions.transform(new RoundedCornersSmall(roundingRadius, centerCrop));
            return this;
        }


        /**
         * Fit center options.
         * 健康中心的选择
         *
         * @return the options 选项
         */
        public Options fitCenter() {
            requestOptions.fitCenter();
            return this;
        }

        /**
         * Center inside options.
         * 中心内的选项
         *
         * @return the options 选项
         */
        public Options centerInside() {
            requestOptions.centerInside();
            return this;
        }

        /**
         * webp format conversion options
         * webp格式转换选项
         *
         * @return the options 选项
         */
        @SuppressLint("CheckResult")
        public Options webpTransformation() {
            Transformation<Bitmap> transformation = new CenterCrop();
            requestOptions.optionalTransform(transformation)
                    .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(transformation));
            return this;
        }

        /**
         * Dont animate options.
         * 不要动画选项
         *
         * @return the options
         */
        public Options dontAnimate() {
            requestOptions.dontAnimate();
            return this;
        }

        /**
         * Skip memory cache options.
         * 跳过内存缓存选项
         *
         * @param skip the skip
         * @return the options
         */
        public Options skipMemoryCache(boolean skip) {
            requestOptions.skipMemoryCache(skip);
            return this;
        }
    }

    public static class RoundedCornersSmall extends BitmapTransformation {
        private int radius;
        private boolean centerCrop;

        public RoundedCornersSmall(int roundingRadius, boolean centerCrop) {
            radius = roundingRadius;
            this.centerCrop = centerCrop;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform, outWidth, outHeight);
            /*return TransformationUtils.roundedCorners(pool, toTransform, outWidth, outHeight,
                    radius);*/
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
            if (source == null) {
                return null;
            }
            if (centerCrop) {
                /*中心裁剪*/
                source = TransformationUtils.centerCrop(pool, source, outWidth, outHeight);
            }
            /*圆角剪裁*/
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawRoundRect(rectF, radius, radius, paint);
            canvas.setBitmap(null);
            return result;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }
    }
}
