package com.meishe.sdkdemo.superzoom;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meishe.effect.NvSuperZoom;
import com.meishe.engine.util.PathUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.boomrang.LiveWindow;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.MediaScannerUtil;
import com.meishe.sdkdemo.utils.VideoCompileUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.threadpools.AllThreadPools;

import java.util.concurrent.TimeUnit;

public class SuperZoomPreviewActivity extends BaseActivity {

    private static final String TAG = "SuperZoomPreviewActivit";

    private LiveWindow mLiveWindow;
    private ImageView superZoomPreviewCloseButton;
    private ImageView superZoomPreviewCreate;
    private LinearLayout mCompilePage;
    private LinearLayout superZoom_preview_ing;
    private TextView superZoom_preview_completed;

    private NvsTimeline mTimeline;
    private String mRecordVideoPath;
    private String mCompileVideoPath;
    private String mZoomFx;
    private NvSuperZoom nvSuperZoom;
//    private NvsTimelineAnimatedSticker logo;


    @Override
    protected int initRootView() {
        return R.layout.activity_super_zoom_preview;
    }

    @Override
    protected void initViews() {
        mLiveWindow = (LiveWindow) findViewById(R.id.super_zoom_liveWindow);
        superZoomPreviewCloseButton = (ImageView) findViewById(R.id.super_zoom_preview_closeButton);
        superZoomPreviewCreate = (ImageView) findViewById(R.id.super_zoom_preview_create);
        mCompilePage = (LinearLayout) findViewById(R.id.compilePage);
        superZoom_preview_ing = (LinearLayout) findViewById(R.id.super_zoom_preview_ing);
        superZoom_preview_completed = (TextView) findViewById(R.id.super_zoom_preview_completed);
    }

    @Override
    protected void initTitle() {
    }

    @Override
    protected void initData() {
        nvSuperZoom = new NvSuperZoom(SuperZoomPreviewActivity.this);
        Intent intent = getIntent();
        if (intent != null) {
            mRecordVideoPath = intent.getStringExtra("video_path");
            if (mRecordVideoPath == null || mRecordVideoPath.isEmpty()) {
                return;
            }
            mZoomFx = intent.getStringExtra("zoomFx");
            if (mZoomFx == null || mZoomFx.isEmpty()) {
                return;
            }
        }
//        if (TextUtils.equals("dramatization", mZoomFx) || TextUtils.equals("spring", mZoomFx)) {
            nvSuperZoom.setAssetsExternalPath(null);
//        } else {
//            nvSuperZoom.setAssetsExternalPath(PathUtils.getSDCardPathByType(NvAsset.ASSET_SUPER_ZOOM));
//        }
        mLiveWindow.init();
        initTimeline();
    }

    private void initTimeline() {
        mTimeline = nvSuperZoom.createTimeline(mRecordVideoPath, mZoomFx);
        if (mTimeline == null) {
            Log.e(TAG, "initTimeline： timeline buildFx failed");
            return;
        }
        mLiveWindow.playVideo(mTimeline);
        Log.d(TAG, "initTimeline： timeline duration: " + mTimeline.getDuration());
    }

    @Override
    protected void initListener() {
        superZoomPreviewCloseButton.setOnClickListener(this);
        superZoomPreviewCreate.setOnClickListener(this);
        mStreamingContext.setCompileCallback(new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {

            }

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
                setCover(1);
                AllThreadPools.scheduleThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setCover(2);
                            }
                        });
                    }
                }, 1, TimeUnit.SECONDS);
                MediaScannerUtil.scanFile(mCompileVideoPath, "video/mp4");
                mLiveWindow.playVideo(mTimeline);
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {
                removeLogo();
            }
        });

        mCompilePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.super_zoom_preview_closeButton) {
            if (isFinishing()) {
                return;
            }
            superZoomPreviewCloseButton.setClickable(false);
            AppManager.getInstance().finishActivity();
        } else if (id == R.id.super_zoom_preview_create) {
            mStreamingContext.setCompileConfigurations(null);
            setCover(0);
            String fileName = "superZoom" + System.currentTimeMillis() + ".mp4";
            mCompileVideoPath = PathUtils.getVideoSavePath(fileName);
            int compile_height = 1280;
            NvsAVFileInfo fileInfo = mStreamingContext.getAVFileInfo(mRecordVideoPath);
            if (fileInfo != null) {
                NvsSize size = fileInfo.getVideoStreamDimension(0);
                if (size != null) {
                    compile_height = size.height;
                }
            }

            VideoCompileUtil.compileVideo(mStreamingContext, mTimeline, mCompileVideoPath, 0, mTimeline.getDuration(),
                    NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM, compile_height);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mStreamingContext.stop();
    }
    /**
     * 控制生成中蒙层的显示
     * Controlling the display of masks in generation
     *
     * @param state
     */
    private void setCover(int state) {
        if (state == 0) {
            mCompilePage.setVisibility(View.VISIBLE);
            superZoom_preview_ing.setVisibility(View.VISIBLE);
            superZoom_preview_completed.setVisibility(View.GONE);
        } else if (state == 1) {
            superZoom_preview_ing.setVisibility(View.GONE);
            superZoom_preview_completed.setVisibility(View.VISIBLE);
            removeLogo();
        } else if (state == 2) {
            mCompilePage.setVisibility(View.GONE);
        }
    }

    private void removeLogo() {
//        assert logo != null;
//        removeLogoSticker(logo, mTimeline);
        mLiveWindow.playVideo(mTimeline);
    }

    @Override
    public void onBackPressed() {
        if (superZoomPreviewCloseButton.isClickable()) {
            superZoomPreviewCloseButton.callOnClick();
            superZoomPreviewCloseButton.setClickable(false);
        }
    }

    @Override
    protected void onResume() {
        if (mTimeline != null && mLiveWindow != null) {
            mLiveWindow.playVideo(mTimeline);
        } else {
            finish();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (nvSuperZoom != null) {
            nvSuperZoom.stop();
            nvSuperZoom.releaseResources();
            nvSuperZoom = null;
        }
        if (mTimeline != null) {
            if (mStreamingContext != null) {
                mStreamingContext.removeTimeline(mTimeline);
                mStreamingContext.clearCachedResources(true);
            }
            mTimeline = null;
        }
        super.onDestroy();
    }
}
