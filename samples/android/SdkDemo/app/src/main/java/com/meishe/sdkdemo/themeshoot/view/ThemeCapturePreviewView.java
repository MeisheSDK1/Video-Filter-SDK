package com.meishe.sdkdemo.themeshoot.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.themeshoot.model.ThemeModel;
import com.meishe.sdkdemo.themeshoot.utlils.ThemeShootUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/8/5.
 * @Description :主题拍摄预览view。ThemeCapturePreviewView
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ThemeCapturePreviewView extends LinearLayout {
    private int ratioType = -1;


    private Context mContext;
    private final String TAG = getClass().getName();
    private ImageView mCloseView;
    private Button mStartView;
    private TextView mThemeName, mThemeClipNum, mThemeDuration;
    private OnThemePreviewOperationListener mOnThemePreviewOperationListener;

    private ThemeModel mThemeModel;
    private ImageView iv16_9;
    private ImageView iv9_16;
    private ThemePreviewLiveWindow mLiveWindow;
    private NvsStreamingContext nvsStreamingContext;
    private NvsTimeline mTimeLine9_16;
    private NvsTimeline mTimeLine16_9;
    private View mHorizontalCaptureLayout;
    private View mVerticalCaptureLayout;
    private TextView tv9_16;
    private TextView tv_16_9;
    private View oneLayout;
    private ImageView iv21_9;
    private TextView tv21_9;
    private View fourLayout;
    private ImageView iv9_21;
    private TextView tv9_21;
    private NvsTimeline mTimeLine9_21;
    private NvsTimeline mTimeLine21_9;
    private String supportRatio;

    public ThemeCapturePreviewView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public ThemeCapturePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void updateThemeModelView(ThemeModel themeModel) {
        this.mThemeModel = themeModel;
        mThemeName.setText(mThemeModel.getName());
        String clipNumTip = String.format(getResources().getString(R.string.theme_clip_num), mThemeModel.getShotsNumber());
        mThemeClipNum.setText(clipNumTip);
        String duration = ThemeShootUtil.formatUsToString(mThemeModel.getMusicDuration() * 1000);
        String durationTip = String.format(getResources().getString(R.string.theme_duration), duration);
        mThemeDuration.setText(durationTip);
        supportRatio = mThemeModel.getSupportedAspectRatio();
        if (!TextUtils.isEmpty(supportRatio)) {
            hideIvAndTv();
            oneLayout.setVisibility(supportRatio.contains("21v9") ? VISIBLE : GONE);
            if (supportRatio.contains("21v9")) {
                if (ratioType == -1) {
                    ratioType = NvAsset.AspectRatio_21v9;
                }
            }
            mHorizontalCaptureLayout.setVisibility(supportRatio.contains("16v9") ? VISIBLE : GONE);
            if (!supportRatio.contains("16v9")) {
                if (ratioType == -1) {
                    ratioType = NvAsset.AspectRatio_16v9;
                }
            }
            mVerticalCaptureLayout.setVisibility(supportRatio.contains("9v16") ? VISIBLE : GONE);
            if (!supportRatio.contains("9v16")) {
                if (ratioType == -1) {
                    ratioType = NvAsset.AspectRatio_9v16;
                }
            }


            fourLayout.setVisibility(supportRatio.contains("9v21") ? VISIBLE : GONE);
            if (supportRatio.contains("9v21")) {
                if (ratioType == -1) {
                    ratioType = NvAsset.AspectRatio_9v21;
                }
            }
            iv16_9.setBackgroundResource(R.drawable.bg_rect_select);
        }
        updateTimeLine(mThemeModel.getPreview());
    }

    private void updateTimeLine(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            Log.e("pathError", "videoPath==null");
            return;
        }
//        String video9Url = ThemeShootUtil.get9V16PathByPath(videoUrl);
//        String video9Url = videoUrl.replace("cover9v16.mp4", "cover.mp4");
        String video9Url = videoUrl;
//        String video16Url = videoUrl.replace("cover16v9.mp4", "cover.mp4");
        String video16Url = videoUrl;
//        String video21Url = videoUrl.replace("cover21v9.mp4", "cover.mp4");
        String video21Url = videoUrl;
//        String video9v21Url = videoUrl.replace("cover9v21.mp4", "cover.mp4");
        String video9v21Url = videoUrl;
        if (supportRatio.contains("9v16")) {
            createTimeLine9(video9Url);
        }
        if (supportRatio.contains("16v9")) {
            createTimeLine16(video16Url);
        }
        if (supportRatio.contains("21v9")) {
            createTimeLine21v9(video21Url);
        }
        if (supportRatio.contains("9v21")) {
            createTimeLine9v21(video9v21Url);
        }
        if (ratioType == NvAsset.AspectRatio_9v16 && mTimeLine9_16 != null) {
            nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine9_16, mLiveWindow);
            playTimeline(mTimeLine9_16, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine9_16), mTimeLine9_16.getDuration());
        } else if (ratioType == NvAsset.AspectRatio_16v9 && mTimeLine16_9 != null) {
            nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine16_9, mLiveWindow);
            playTimeline(mTimeLine16_9, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine16_9), mTimeLine16_9.getDuration());
        } else if (ratioType == NvAsset.AspectRatio_21v9 && mTimeLine21_9 != null) {
            nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine21_9, mLiveWindow);
            playTimeline(mTimeLine21_9, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine21_9), mTimeLine21_9.getDuration());
        } else if (ratioType == NvAsset.AspectRatio_9v21 && mTimeLine9_21 != null) {
            nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine9_21, mLiveWindow);
            playTimeline(mTimeLine9_21, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine9_21), mTimeLine9_21.getDuration());
        }
    }

    private void initView() {
        final View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_theme_preview, this);
        mLiveWindow = rootView.findViewById(R.id.preview_theme_live_window);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);
        mThemeName = rootView.findViewById(R.id.theme_name);
        mThemeClipNum = rootView.findViewById(R.id.theme_clip_num);
        mThemeDuration = rootView.findViewById(R.id.theme_duration);
        mCloseView = rootView.findViewById(R.id.iv_close);
        mStartView = rootView.findViewById(R.id.start_capture);
        iv16_9 = rootView.findViewById(R.id.iv_16_9);
        iv9_16 = rootView.findViewById(R.id.iv_9_16);
        tv9_16 = rootView.findViewById(R.id.tv_9_16);
        tv_16_9 = rootView.findViewById(R.id.tv_16_9);
        mStartView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnThemePreviewOperationListener != null) {
                    mOnThemePreviewOperationListener.onEnterButtonPressed(ratioType);
                }
            }
        });
        mCloseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nvsStreamingContext != null) {
                    nvsStreamingContext.stop();
                }
                if (mOnThemePreviewOperationListener != null) {
                    mOnThemePreviewOperationListener.onPreviewClosed();
                }
                clear();
            }
        });
        mVerticalCaptureLayout = rootView.findViewById(R.id.preview_check_9);
        mVerticalCaptureLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratioType == NvAsset.AspectRatio_9v16) {
                    return;
                }
                ratioType = NvAsset.AspectRatio_9v16;
                iv9_16.setBackgroundResource(R.mipmap.vertical_rect_selected);
                iv16_9.setBackgroundResource(R.mipmap.horizental_rect);
                if (mTimeLine9_16 != null) {
                    String videoUrl = mThemeModel.getPreview();
                    String video9Url = videoUrl.replace("cover.mp4", "cover9v16.mp4");
                    mThemeModel.setPreview(video9Url);
                    nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine9_16, mLiveWindow);
                    playTimeline(mTimeLine9_16, 0, mTimeLine9_16.getDuration());
                }
            }
        });

        fourLayout = rootView.findViewById(R.id.preview_check_9_21);
        iv9_21 = rootView.findViewById(R.id.preview_check_9_21_iv);
        tv9_21 = rootView.findViewById(R.id.tv_9_21);
        fourLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratioType == NvAsset.AspectRatio_9v21) {
                    return;
                }
                ratioType = NvAsset.AspectRatio_9v21;
                hideIvAndTv();
                iv9_21.setBackgroundResource(R.drawable.bg_rect_select);
                if (mTimeLine9_21 != null) {
                    String videoUrl = mThemeModel.getPreview();
                    String video9_21Url = videoUrl.replace("cover9v21.mp4", "cover.mp4");
                    mThemeModel.setPreview(video9_21Url);
                    nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine9_21, mLiveWindow);
                    playTimeline(mTimeLine9_21, 0, mTimeLine9_21.getDuration());
                }
            }
        });
        oneLayout = rootView.findViewById(R.id.preview_check_21);
        iv21_9 = rootView.findViewById(R.id.iv_21_9);
        tv21_9 = rootView.findViewById(R.id.tv_21_9);
        oneLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratioType == NvAsset.AspectRatio_21v9) {
                    return;
                }
                ratioType = NvAsset.AspectRatio_21v9;
                hideIvAndTv();
                iv21_9.setBackgroundResource(R.drawable.bg_rect_select);
                if (mTimeLine21_9 != null) {
                    String videoUrl = mThemeModel.getPreview();
                    String video16Url = videoUrl.replace("cover21v9.mp4", "cover.mp4");
                    mThemeModel.setPreview(video16Url);
                    nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine21_9, mLiveWindow);
                    playTimeline(mTimeLine21_9, 0, mTimeLine21_9.getDuration());
                }
            }
        });
        mHorizontalCaptureLayout = rootView.findViewById(R.id.preview_check_16);
        mHorizontalCaptureLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratioType == NvAsset.AspectRatio_16v9) {
                    return;
                }
                ratioType = NvAsset.AspectRatio_16v9;
                hideIvAndTv();
                iv16_9.setBackgroundResource(R.drawable.bg_rect_select);
                if (mTimeLine16_9 != null) {
                    String videoUrl = mThemeModel.getPreview();
                    String video16Url = videoUrl.replace("cover9v16.mp4", "cover.mp4");
                    mThemeModel.setPreview(video16Url);
                    nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine16_9, mLiveWindow);
                    playTimeline(mTimeLine16_9, 0, mTimeLine16_9.getDuration());
                }
            }
        });
        rootView.findViewById(R.id.preview_check_16).callOnClick();
        nvsStreamingContext = NvsStreamingContext.getInstance();
        nvsStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                playTimeline(nvsTimeline, 0, nvsTimeline.getDuration());
            }
        });
    }

    private void hideIvAndTv() {
        iv9_21.setBackgroundResource(R.drawable.bg_rect);
        iv9_16.setBackgroundResource(R.drawable.bg_rect);
        iv16_9.setBackgroundResource(R.drawable.bg_rect);
        iv21_9.setBackgroundResource(R.drawable.bg_rect);
    }

    private void seekTimeline(NvsTimeline mTimeLine, long timestamp, int seekShowMode) {
        nvsStreamingContext.seekTimeline(mTimeLine, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
                seekShowMode | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);

    }


    private void createTimeLine9(String videoUrl) {
        NvsAVFileInfo fileInfo = nvsStreamingContext.getAVFileInfo(videoUrl);
        if (fileInfo == null) {
            return;
        }
        int videoStreamRotation = fileInfo.getVideoStreamRotation(0);
        int videoWidth = fileInfo.getVideoStreamDimension(0).width;
        int videoHeight = fileInfo.getVideoStreamDimension(0).height;
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        //先根据高判断 According to high judgment first
        int width = (int) (videoHeight / 16f * 9f);
        int height = videoHeight;
        if (width > videoWidth) {
            //根据宽判断 Judge by width
            width = videoWidth;
            height = (int) (videoHeight * 16f / 9f);
        }
        videoEditRes.imageWidth = width - width % 4;
        videoEditRes.imageHeight = height - height % 2;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;
        mTimeLine9_16 = nvsStreamingContext.createTimeline(videoEditRes, videoFps, audioEditRes);
        if (mTimeLine9_16 == null) {
            Log.e(TAG, "failed to create timeline");
            return;
        }
        NvsVideoTrack mVideoTrack = mTimeLine9_16.appendVideoTrack();
        mVideoTrack.appendClip(videoUrl);
        if (mVideoTrack == null) {
            Log.e(TAG, "videoTrack is null");
            return;
        }
        for (int i = 0; i < mVideoTrack.getClipCount(); i++) {
            mVideoTrack.getClipByIndex(i).setPanAndScan(0, 1);
        }
    }

    private void createTimeLine9v21(String videoUrl) {
        NvsAVFileInfo fileInfo = nvsStreamingContext.getAVFileInfo(videoUrl);
        if (fileInfo == null) {
            return;
        }
        int videoStreamRotation = fileInfo.getVideoStreamRotation(0);
        int videoWidth = fileInfo.getVideoStreamDimension(0).width;
        int videoHeight = fileInfo.getVideoStreamDimension(0).height;
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        //先根据高判断
        int width = (int) (videoWidth / 21f * 9f);
        int height = videoHeight;
        if (width > videoWidth) {
            //根据宽判断
            width = videoWidth;
            height = (int) (videoHeight * 21f / 9f);
        }
        videoEditRes.imageWidth = width - width % 4;
        videoEditRes.imageHeight = height - height % 2;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;
        mTimeLine9_21 = nvsStreamingContext.createTimeline(videoEditRes, videoFps, audioEditRes);
        if (mTimeLine9_21 == null) {
            Log.e(TAG, "failed to create timeline");
            return;
        }
        NvsVideoTrack mVideoTrack = mTimeLine9_21.appendVideoTrack();
        mVideoTrack.appendClip(videoUrl);
        if (mVideoTrack == null) {
            Log.e(TAG, "videoTrack is null");
            return;
        }
        for (int i = 0; i < mVideoTrack.getClipCount(); i++) {
            mVideoTrack.getClipByIndex(i).setPanAndScan(0, 1);
        }
    }

    private void createTimeLine21v9(String videoUrl) {
        NvsAVFileInfo fileInfo = nvsStreamingContext.getAVFileInfo(videoUrl);
        if (fileInfo == null) {
            return;
        }
        int videoStreamRotation = fileInfo.getVideoStreamRotation(0);
        int videoWidth = fileInfo.getVideoStreamDimension(0).width;
        int videoHeight = fileInfo.getVideoStreamDimension(0).height;
        int rotation = fileInfo.getVideoStreamRotation(0);
        Log.d(TAG, "createTimeLine9: videoWidth:" + videoWidth + " videoHeight:" + videoHeight + "  rotation:" + rotation);
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        //先根据宽判断
        int height = (int) (videoWidth / 21f * 9f);
        int width = videoWidth;
        if (height > videoHeight) {
            //根据高判断
            height = videoHeight;
            width = (int) (videoHeight * 21f / 9f);
        }
        videoEditRes.imageWidth = width - width % 4;
        videoEditRes.imageHeight = height - height % 2;
        Log.d(TAG, "initTimeline16 video: timeline16 size: " + width + " * " + height);
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;
        mTimeLine21_9 = nvsStreamingContext.createTimeline(videoEditRes, videoFps, audioEditRes);
        if (mTimeLine21_9 == null) {
            Log.e(TAG, "failed to create timeline");
            return;
        }
        NvsVideoTrack mVideoTrack = mTimeLine21_9.appendVideoTrack();
        mVideoTrack.appendClip(videoUrl);
        if (mVideoTrack == null) {
            Log.e(TAG, "videoTrack is null");
            return;
        }
        for (int i = 0; i < mVideoTrack.getClipCount(); i++) {
            mVideoTrack.getClipByIndex(i).setPanAndScan(0, 1);
        }
    }

    private void createTimeLine16(String videoUrl) {
        NvsAVFileInfo fileInfo = nvsStreamingContext.getAVFileInfo(videoUrl);
        if (fileInfo == null) {
            return;
        }
        int videoStreamRotation = fileInfo.getVideoStreamRotation(0);
        int videoWidth = fileInfo.getVideoStreamDimension(0).width;
        int videoHeight = fileInfo.getVideoStreamDimension(0).height;
        int rotation = fileInfo.getVideoStreamRotation(0);
        Log.d(TAG, "createTimeLine9: videoWidth:" + videoWidth + " videoHeight:" + videoHeight + "  rotation:" + rotation);
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        //先根据宽判断
        int height = (int) (videoWidth / 16f * 9f);
        int width = videoWidth;
        if (height > videoHeight) {
            //根据高判断
            height = videoHeight;
            width = (int) (videoHeight * 16f / 9f);
        }
        videoEditRes.imageWidth = width - width % 4;
        videoEditRes.imageHeight = height - height % 2;
        Log.d(TAG, "initTimeline16 video: timeline16 size: " + width + " * " + height);
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;
        mTimeLine16_9 = nvsStreamingContext.createTimeline(videoEditRes, videoFps, audioEditRes);
        if (mTimeLine16_9 == null) {
            Log.e(TAG, "failed to create timeline");
            return;
        }
        NvsVideoTrack mVideoTrack = mTimeLine16_9.appendVideoTrack();
        mVideoTrack.appendClip(videoUrl);
        if (mVideoTrack == null) {
            Log.e(TAG, "videoTrack is null");
            return;
        }
        for (int i = 0; i < mVideoTrack.getClipCount(); i++) {
            mVideoTrack.getClipByIndex(i).setPanAndScan(0, 1);
        }
    }

    public void playTimeline(NvsTimeline mTimeLine, long startTime, long endTime) {
        nvsStreamingContext.playbackTimeline(mTimeLine, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true,
                NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME | NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_LOW_PIPELINE_SIZE);

    }

    public void setOnThemePreviewOperationListener(OnThemePreviewOperationListener onThemePreviewOperationListener) {
        this.mOnThemePreviewOperationListener = onThemePreviewOperationListener;
    }

    public void onResume() {
        if (mTimeLine9_16 != null && mTimeLine16_9 != null) {
            if (ratioType == NvAsset.AspectRatio_16v9) {
                playTimeline(mTimeLine16_9, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine16_9), mTimeLine16_9.getDuration());
            } else if (ratioType == NvAsset.AspectRatio_9v16) {
                playTimeline(mTimeLine9_16, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine9_16), mTimeLine9_16.getDuration());
            }
        }
    }

    public interface OnThemePreviewOperationListener {
        void onPreviewClosed();

        void onEnterButtonPressed(int ratio_type);
    }

    public void clear() {
        if (nvsStreamingContext != null) {
            nvsStreamingContext.stop();
        }
        if (mTimeLine9_16 != null) {
            nvsStreamingContext.removeTimeline(mTimeLine9_16);
        }
        if (mTimeLine16_9 != null) {
            nvsStreamingContext.removeTimeline(mTimeLine16_9);
        }
        mTimeLine16_9 = null;
        mTimeLine9_16 = null;
        getRootView().findViewById(R.id.preview_check_16).callOnClick();
    }
}
