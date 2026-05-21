package com.meicam.effectsdkdemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.meicam.effectsdkdemo.R;
import com.meishe.libbase.util.ScreenUtils;

public class SidebarView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private OnSidebarListener mOnSidebarListener;
    private TextView mBarFlash;

    public SidebarView(Context context) {
        super(context);
        initView(context);
    }

    public SidebarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SidebarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View mBeautyView = LayoutInflater.from(context).inflate(R.layout.sidebar_layout, this);
        TextView mBarSwitch = mBeautyView.findViewById(R.id.bar_switch);
        mBarSwitch.setOnClickListener(this);
        changeIconSize(mBarSwitch);
        mBarFlash = mBeautyView.findViewById(R.id.bar_flash);
        mBarFlash.setOnClickListener(this);
        updateFlashView(false);
       // changeIconSize(mBarFlash);
        TextView mBarZoom = mBeautyView.findViewById(R.id.bar_zoom);
        mBarZoom.setOnClickListener(this);
        changeIconSize(mBarZoom);
        TextView mBarExpose = mBeautyView.findViewById(R.id.bar_expose);
        mBarExpose.setOnClickListener(this);
        changeIconSize(mBarExpose);
        TextView mBarBeauty = mBeautyView.findViewById(R.id.bar_beauty);
        mBarBeauty.setOnClickListener(this);
        changeIconSize(mBarBeauty);
        TextView mBarProp = mBeautyView.findViewById(R.id.bar_prop);
        mBarProp.setOnClickListener(this);
        changeIconSize(mBarProp);
        TextView mBarFilter = mBeautyView.findViewById(R.id.bar_filter);
        mBarFilter.setOnClickListener(this);
        changeIconSize(mBarFilter);
        TextView mBarMarkup = mBeautyView.findViewById(R.id.bar_makeup);
        mBarMarkup.setOnClickListener(this);
        changeIconSize(mBarMarkup);
        TextView mBarSeg = mBeautyView.findViewById(R.id.bar_seg);
        mBarSeg.setOnClickListener(this);
        changeIconSize(mBarSeg);
        TextView mBarAdjust = mBeautyView.findViewById(R.id.bar_adjust);
        mBarAdjust.setOnClickListener(this);
        changeIconSize(mBarAdjust);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (null == mOnSidebarListener) {
            return;
        }
        switch (v.getId()) {
            case R.id.bar_switch:
                mOnSidebarListener.onBarSwitch();
                break;
            case R.id.bar_flash:
                mOnSidebarListener.onBarFlash();
                break;
            case R.id.bar_zoom:
                mOnSidebarListener.onBarZoom();
                break;
            case R.id.bar_expose:
                mOnSidebarListener.onBarExpose();
                break;
            case R.id.bar_beauty:
                mOnSidebarListener.onHideView();
                mOnSidebarListener.onBarBeauty();
                break;
            case R.id.bar_prop:
                mOnSidebarListener.onHideView();
                mOnSidebarListener.onBarProp();
                break;
            case R.id.bar_filter:
                mOnSidebarListener.onHideView();
                mOnSidebarListener.onBarFilter();
                break;
            case R.id.bar_makeup:
                mOnSidebarListener.onHideView();
                mOnSidebarListener.onBarMakeup();
                break;
            case R.id.bar_seg:
                mOnSidebarListener.onBarSeg();
                break;
            case R.id.bar_adjust:
                mOnSidebarListener.onBarAdjust();
                break;
            default:
                break;
        }
    }

    private void changeIconSize(TextView view) {
        if (null == view) {
            return;
        }
        Drawable originalDrawable = view.getCompoundDrawables()[1];
        if (null == originalDrawable) {
            return;
        }
        originalDrawable.setBounds(0, 0, ScreenUtils.dip2px(getContext(), 25), ScreenUtils.dip2px(getContext(), 25));
        view.setCompoundDrawables(null, originalDrawable, null, null);
    }

    public void updateFlashView(boolean isOn) {
        Drawable drawable = ContextCompat.getDrawable(mContext, isOn ? R.mipmap.icon_flash_on : R.mipmap.icon_flash_off);
        mBarFlash.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
    }

    public void setOnSidebarListener(OnSidebarListener onSidebarListener) {
        mOnSidebarListener = onSidebarListener;
    }

    public interface OnSidebarListener {
        void onHideView();

        void onBarSwitch();

        void onBarFlash();

        void onBarZoom();

        void onBarExpose();

        void onBarBeauty();

        void onBarProp();

        void onBarFilter();

        void onBarMakeup();

        void onBarSeg();

        void onBarAdjust();
    }
}
