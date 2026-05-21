package com.meishe.cutsame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishe.cutsame.fragment.iview.CutCompileVpView;
import com.meishe.cutsame.fragment.presenter.CutCompilePresenter;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meishe.base.model.BaseMvpActivity;
import com.meishe.base.utils.MediaScannerUtil;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.view.CompileProgress;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.util.PathUtils;

import java.io.File;
import java.util.Hashtable;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : CaoZhiChao
 * @CreateDate : 2020/11/27 16:29
 * @Description : 剪同款的生成界面  video compile Activity
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class CutCompileActivity extends BaseMvpActivity<CutCompilePresenter> implements CutCompileVpView {
    public static final String TEMPLATE_ID = "templateId";
    public static final String COMPILE_RESOLUTION = "compileResolution";
    private ImageView mCutCompileSource;
    private CompileProgress mCutCompileProgress;
    private Button mCutCompileCancel;
    private Button mCutCompileOk;
    private TextView mCutCompileProgressText;
    private TextView mCutCompileTip;
    private String mCompilePath;
    private String mTemplateId;
    private int mCompileResolution;
    private Point mBeforeChangeVideoPoint;

    @Override
    protected int bindLayout() {
        return R.layout.activity_cut_compile;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mTemplateId = bundle.getString(TEMPLATE_ID);
                mCompileResolution = bundle.getInt(COMPILE_RESOLUTION, NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_720);
            }
        }

        EditorController.getInstance().grabImageFromTimelineAsync(0, new NvsRational(1, 1), 0, new NvsStreamingContext.ImageGrabberCallback() {
            @Override
            public void onImageGrabbedArrived(final Bitmap bitmap, long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCutCompileSource != null) {
                            mCutCompileSource.setImageBitmap(bitmap);
                        }
                        mPresenter.updateTemplateInteraction(mTemplateId);
//                        changeVideoTo4K();
                        compileTimeLine();
                    }
                });
            }
        });
    }

    /**
     * 修改时间线为4K(3840 * 2160)。因为生成的时候，不会大于时间线的大小，所以要先修改画幅
     * Modify the timeline to 4K(3840 * 2160).Since it will not be larger than the timeline when it is generated, you need to modify the frame first
     */
    private void changeVideoTo4K() {
        int k4W = 3840;
        int k4H = 2160;
        mBeforeChangeVideoPoint = EditorController.getInstance().getTimelineWidthAndHeight();
        int width = mBeforeChangeVideoPoint.x;
        int height = mBeforeChangeVideoPoint.y;
        if (width > height) {
            height = k4W * height / width;
            width = k4W;
            if (height > k4H) {
                float scale = 1.0f * k4H / height;
                height = k4H;
                width = (int) (width * scale);
            }
        } else {
            width = width * k4H / height;
            height = k4H;
        }
        width = width - width % 4;
        height = height - height % 2;
        EditorController.getInstance().changeVideoSize(width, height);
    }

    private void compileTimeLine() {
        mCompilePath = PathUtils.getVideoSavePath(PathUtils.getVideoSaveName());
        Hashtable<String, Object> mParamsTable = new Hashtable();
        mParamsTable.put(NvsStreamingContext.COMPILE_FPS, new NvsRational(30, 1));
        mParamsTable.put(NvsStreamingContext.COMPILE_USE_OPERATING_RATE,true);

        //计算新高度 Calculated new height
        int setHeightOfCompile = 0;
        if (mCompileResolution == NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_360) {
            setHeightOfCompile = 360;
        } else if (mCompileResolution == NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_480) {
            setHeightOfCompile = 480;
        } else if (mCompileResolution == NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_720) {
            setHeightOfCompile = 720;
        } else if (mCompileResolution == NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_1080) {
            setHeightOfCompile = 1080;
        }

        setHeightOfCompile = EditorController.getInstance().getCustomHeight(setHeightOfCompile);

        EditorController.getInstance().compileTimeLineCustom(mCompilePath, setHeightOfCompile, mParamsTable,
                new NvsStreamingContext.CompileCallback() {
                    @Override
                    public void onCompileProgress(NvsTimeline nvsTimeline, int i) {
                        setCenterProgress(i);
                    }

                    @Override
                    public void onCompileFinished(NvsTimeline nvsTimeline) {

                    }

                    @Override
                    public void onCompileFailed(NvsTimeline nvsTimeline) {
                        onCompileFiled();
                    }
                }, new NvsStreamingContext.CompileCallback2() {
                    @Override
                    public void onCompileCompleted(NvsTimeline nvsTimeline, boolean b) {
                        if (!b) {
                            mCutCompileProgress.setVisibility(View.GONE);
                            mCutCompileProgressText.setVisibility(View.GONE);
                            mCutCompileCancel.setVisibility(View.GONE);
                            mCutCompileTip.setText(R.string.activity_cut_compile_tip2);
                            mCutCompileOk.setVisibility(View.VISIBLE);
                            File file = new File(mCompilePath);
                            if (file.exists()) {
                                /*
                                 * 加入到媒体库
                                 * Add to media library
                                 * */
                                MediaScannerUtil.scanFile(mCompilePath, "video/mp4");
                            }
                            EditorController.getInstance().clearCompileConfigurations();
                        } else {
                            onCompileFiled();
                        }
                        EditorController.getInstance().changeVideoSize(mBeforeChangeVideoPoint.x, mBeforeChangeVideoPoint.y);
                    }
                });
    }

    private void onCompileFiled() {
        mCutCompileProgress.setVisibility(View.GONE);
        mCutCompileProgressText.setVisibility(View.GONE);
        mCutCompileCancel.setVisibility(View.GONE);
        mCutCompileTip.setText(R.string.activity_cut_compile_tip3);
    }

    private void setCenterProgress(int i) {
        mCutCompileProgress.setProgress(i);
        mCutCompileProgressText.setText(i + "%");
    }

    @Override
    protected void initView() {
        ImageView cutCompileClose = findViewById(R.id.cut_compile_close);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cutCompileClose.getLayoutParams();
        layoutParams.topMargin = (int) (ScreenUtils.getStatusBarHeight() + getResources().getDimension(R.dimen.title_margin_top));
        cutCompileClose.setLayoutParams(layoutParams);
        cutCompileClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditorController.getInstance().stop();
                finish();
            }
        });
        mCutCompileSource = findViewById(R.id.cut_compile_source);
        mCutCompileProgress = findViewById(R.id.cut_compile_progress);
        mCutCompileCancel = findViewById(R.id.cut_compile_cancel);
        mCutCompileOk = findViewById(R.id.cut_compile_ok);
        mCutCompileCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditorController.getInstance().stop();
                finish();
            }
        });
        mCutCompileOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCutCompileProgressText = findViewById(R.id.cut_compile_progress_text);
        mCutCompileTip = findViewById(R.id.cut_compile_tip);
    }
}