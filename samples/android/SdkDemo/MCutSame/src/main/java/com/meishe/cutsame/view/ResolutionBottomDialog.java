package com.meishe.cutsame.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishe.cutsame.R;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.view.CustomCompileParamView;
import com.meishe.base.view.bean.CompileParamData;
import com.meishe.engine.editor.EditorController;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : CaoZhiChao
 * @CreateDate : 2020/11/27 16:29
 * @Description : 选择生成时分辨率的底部弹窗。 The bottom dialog to select resolution on compile
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class ResolutionBottomDialog {
    private static final int PADDING = 0;
    private Context mContext;
    private Dialog mDialog;
    private Button mSureButton;
    private TextView mCutDialogResolutionTextSize;
    private ImageView mCutDialogResolutionBack;
    private ResolutionBottomDialogListener mResolutionBottomDialogListener;
    private CustomCompileParamView mCustomCompileParamView;
    private List<CompileParamData> customResolutionList;
    /**
     * 分辨率系数 720p为基准1.0
     * The resolution coefficient of 720p is reference 1.0
     */
    private float baseResolutionValue = 1.0f;
    private int mVideoResolution = NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_720;

    public ResolutionBottomDialog(Context context) {
        mContext = context;
        initDialog();
        initView();
        initWindow();
        initListener();
        initData();
    }

    /**
     * Sets resolution bottom dialog listener.
     * 设置分辨率底部对话框监听器
     * @param resolutionBottomDialogListener the resolution bottom dialog listener
     */
    public void setResolutionBottomDialogListener(ResolutionBottomDialogListener resolutionBottomDialogListener) {
        mResolutionBottomDialogListener = resolutionBottomDialogListener;
    }

    private void initDialog() {
        mDialog = new Dialog(mContext, com.meishe.cafconvertor.R.style.Theme_AppCompat_Dialog);
        mDialog.setCanceledOnTouchOutside(false);
    }


    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.cut_layout_edit_bottom_resolution, null);
        mSureButton = view.findViewById(R.id.cut_dialog_resolution_sure);
        mCustomCompileParamView = view.findViewById(R.id.cut_dialog_resolution_param_view);
        mCutDialogResolutionTextSize = view.findViewById(R.id.cut_dialog_resolution_text_size);
        mCutDialogResolutionBack = view.findViewById(R.id.cut_dialog_resolution_back);
        mDialog.setContentView(view);
    }

    private void initWindow() {
        Window window = mDialog.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.getDecorView().setPadding(PADDING, PADDING, PADDING, PADDING);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_px_975);
            window.setAttributes(layoutParams);
            window.getDecorView().setBackgroundResource(com.meishe.base.R.color.colorTranslucent);
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);
        }
    }


    private void initListener() {
        mSureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (mResolutionBottomDialogListener != null) {
                    mResolutionBottomDialogListener.onDone(mVideoResolution);
                }
            }
        });

        mCustomCompileParamView.setOnFunctionSelectedListener(new CustomCompileParamView.OnFunctionSelectedListener() {
            @Override
            public void onSelected(CompileParamData itemData) {
                mVideoResolution = getVideoResolution(itemData);
                buildSrcSize();
            }

            @Override
            public void onTouched() {
            }

            @Override
            public void onRelease() {
            }
        });
        mCutDialogResolutionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    private void initData() {
        String[] customResolutionArray = mContext.getResources().getStringArray(R.array.cut_custom_resolution);
        buildResolutionData(customResolutionArray);
        mCustomCompileParamView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCustomCompileParamView.setSelectedData(customResolutionList);
                buildSrcSize();
            }
        }, 100);
    }

    private void buildSrcSize() {
        /*
          基于标准的分辨率 帧率下每秒视频的size kb
          Video size kilobytes per second at standard resolution frame rates
         */
        float perSecondSize1 = 6.7f * 1024 / 8;
        float perSecondSize = perSecondSize1 * baseResolutionValue * 30 / 25;
        long duration = EditorController.getInstance().getTimelineDuration();
        float second = duration * 1.0f / 1000 / 1000;
        float size = perSecondSize * second;
        int m = (int) (size / 1024);
        String fileSize;
        String hintText = mContext.getResources().getString(R.string.file_size_about);
        if (m > 0) {
            fileSize = hintText +" " + m + " M";
        } else {
            fileSize = hintText +" "+ size + " Kb";
        }
        mCutDialogResolutionTextSize.setText(fileSize);
    }

    private int getVideoResolution(CompileParamData itemData) {
        if (null == itemData) {
            baseResolutionValue = 1.0f;
            return NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_720;
        }
        if (itemData.getShowData().equals(mContext.getResources().getString(R.string.compile_int360))) {
            baseResolutionValue = 0.5f;
            return NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_360;
        } else if (itemData.getShowData().equals(mContext.getResources().getString(R.string.compile_int480))) {
            baseResolutionValue = 0.67f;
            return NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_480;
        } else if (itemData.getShowData().equals(mContext.getResources().getString(R.string.compile_int720))) {
            baseResolutionValue = 1.0f;
            return NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_720;
        } else if (itemData.getShowData().equals(mContext.getResources().getString(R.string.compile_int1080))) {
            baseResolutionValue = 1.5f;
            return NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_1080;
        } else if (itemData.getShowData().equals(mContext.getResources().getString(R.string.compile_int4K))) {
            baseResolutionValue = 3.0f;
            return NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_2160;
        } else {
            baseResolutionValue = 1.0f;
            return NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_720;
        }
    }

    private void buildResolutionData(String[] customResolutionArray) {
        customResolutionList = new ArrayList<>();
        if (null != customResolutionArray && customResolutionArray.length > 0) {

            for (String showData : customResolutionArray) {
                CompileParamData compileParamData = new CompileParamData();
                compileParamData.setShowData(showData);
                compileParamData.setRecommend("720p".equals(showData));
                customResolutionList.add(compileParamData);
            }
        }
    }

    public void show() {
        mDialog.show();
    }

    public void hide() {
        mDialog.hide();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    /**
     * The interface Resolution bottom dialog listener.
     * 分辨率底对话监听器的接口
     */
    public interface ResolutionBottomDialogListener {
        /**
         * 确认选择的分辨率
         * confirm the resolution select
         *
         * @param resolution 分辨率的值 值来自于{@link #getVideoResolution}
         */
        void onDone(int resolution);
    }
}
