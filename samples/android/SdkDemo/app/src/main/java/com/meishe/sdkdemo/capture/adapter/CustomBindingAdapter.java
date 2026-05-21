package com.meishe.sdkdemo.capture.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/24 下午4:28
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CustomBindingAdapter {

    @BindingAdapter("url")
    public static void setImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asDrawable()
                .load(url)
                .into(imageView);
    }

    @BindingAdapter("res")
    public static void setImageRes(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

    @BindingAdapter("color")
    public static void setColor(TextView textView, String color) {
        textView.setTextColor(Color.parseColor(color));
    }

    @BindingAdapter("visible")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
