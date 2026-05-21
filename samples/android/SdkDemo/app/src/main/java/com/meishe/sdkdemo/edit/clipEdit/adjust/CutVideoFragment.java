package com.meishe.sdkdemo.edit.clipEdit.adjust;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsLiveWindowExt;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.utils.FormatUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.bean.CutData;
import com.meishe.engine.constant.NvsConstants;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.clipEdit.view.CutRectLayout;
import com.meishe.sdkdemo.edit.clipEdit.view.TestRectView;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_EXTRA_ROTATION_Z;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_EXTRA_SCALE_X;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_EXTRA_SCALE_Y;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_ROTATION_Z;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_SCALE_X;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_SCALE_Y;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_TRANS_X;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_TRANS_Y;

public class CutVideoFragment extends Fragment implements ICutRegionFragment {
    private static final String TAG = "CutVideoFragment";
    private static final String PARAM_MAX_DURATION = "max_duration";
    private static final int MESSAGE_RESET_PLAY_BACK_STATE = 100;
    private static final boolean IS_DEBUG = false;
    private NvsLiveWindowExt mLiveWindow;
    private NvsTimeline mTimeline;
    private NvsStreamingContext mStreamingContext = NvsStreamingContext.getInstance();
    private RelativeLayout mPlayerLayout;
    private long mStartTime;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MESSAGE_RESET_PLAY_BACK_STATE) {
                playVideo(mStartTime, mStartTime + getDuration());
            }
            return false;
        }
    });
    private long mMaxDuration;
    private OnPlayProgressChangeListener mOnPlayProgessChangeListener;
    private OnFragmentLoadFinisedListener mFragmentLoadFinisedListener;
    private VideoFragmentListener mVideoFragmentCallBack;
    private CutRectLayout mCutView;
    private OnCutRectChangedListener mOnCutRectChangedListener;
    private float mMinLiveWindowScale = 1.0F;
    private Point mOriginalSize;
    private View mPlayButton;
    private TextView mCurrentPlaytimeView;
    private SeekBar mSeekBar;
    private ImageView mPlayButtonImage;
    /**
     * 震动器
     * Vibrator
     */
    private Vibrator mVibrator;
    boolean hasVibrator = false;
    private FloatPoint mCenterPoint = new FloatPoint();
    private TextView mTotalDurationView;
    /**
     * 当前编辑的临时缩放值。用于旋转时，能恢复到这个值
     * The temporary scaling value of the current edit.
     * When used for rotation, it can be restored to this value.
     */
    private float mTempScale = 1.0F;
    private TestRectView mTestView;

    @Override
    public void setOnCutRectChangeListener(OnCutRectChangedListener listener) {
        this.mOnCutRectChangedListener = listener;
    }

    @Override
    public void rotateClip(float degree) {
        mTransformData.put(STORYBOARD_KEY_EXTRA_ROTATION_Z, degree);
        if (mLiveWindow != null) {
            rotateVideo(0);
        }
    }

    public void setOnPlayProgressChangeListener(OnPlayProgressChangeListener onPlayProgessChangeListener) {
        this.mOnPlayProgessChangeListener = onPlayProgessChangeListener;
    }

    public CutVideoFragment() {
        mTransformData.put(STORYBOARD_KEY_SCALE_X, 1.0F);
        mTransformData.put(STORYBOARD_KEY_SCALE_Y, 1.0F);
        mTransformData.put(STORYBOARD_KEY_ROTATION_Z, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_X, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, 0F);
        mTransformData.put(STORYBOARD_KEY_EXTRA_ROTATION_Z, 0F);
        mTransformData.put(STORYBOARD_KEY_EXTRA_SCALE_X, 1F);
        mTransformData.put(STORYBOARD_KEY_EXTRA_SCALE_Y, 1F);
    }

    public static CutVideoFragment newInstance(long maxDuration) {
        CutVideoFragment fragment = new CutVideoFragment();
        Bundle args = new Bundle();
        args.putLong(PARAM_MAX_DURATION, maxDuration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mMaxDuration = getArguments().getLong(PARAM_MAX_DURATION);
        }
        super.onCreate(savedInstanceState);

        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playStopped(nvsTimeline);
                }
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                mHandler.sendEmptyMessage(MESSAGE_RESET_PLAY_BACK_STATE);
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playBackEOF(nvsTimeline);
                }
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                updatePlayProgress(cur_position);
            }
        });
        mStreamingContext.setSeekingCallback(new NvsStreamingContext.SeekingCallback() {
            @Override
            public void onSeekingTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                updatePlayProgress(cur_position);
            }
        });
        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mPlayButtonImage.setBackgroundResource(R.mipmap.icon_pause);
                } else {
                    mPlayButtonImage.setBackgroundResource(R.mipmap.icon_play);
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cut_video, container, false);
        mLiveWindow = rootView.findViewById(R.id.liveWindow);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTCROP);
        mPlayerLayout = rootView.findViewById(R.id.playerLayout);
        mCutView = rootView.findViewById(R.id.cut_view);
        mPlayButton = rootView.findViewById(R.id.playLayout);
        mCurrentPlaytimeView = rootView.findViewById(R.id.currentPlaytime);
        mSeekBar = rootView.findViewById(R.id.playSeekBar);
        mPlayButtonImage = rootView.findViewById(R.id.playImage);
        mTotalDurationView = rootView.findViewById(R.id.totalDuration);
        mTestView = rootView.findViewById(R.id.testView);
        if (IS_DEBUG) {
            mTestView.setVisibility(View.VISIBLE);
        }
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        initListener();
        return rootView;
    }

    private void initListener() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    stopEngine();
                } else {
                    if (mTimeline == null) {
                        return;
                    }
                    long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    //已经在指定片段上播放的时长
                    //The amount of time that has been played on the specified segment
                    long alreadyPlayDuration = startTime - mStartTime;
                    long endTime = startTime + getDuration() - (alreadyPlayDuration);
                    playVideo(startTime, endTime);
                    if (mOnPlayProgessChangeListener != null) {
                        mOnPlayProgessChangeListener.onPlayStateChanged(true);
                    }
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private long currentTime = 0L;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime = getDuration() * progress / 100 + mStartTime;
                    seekTimeline(currentTime, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
                    updateCurPlayTime(currentTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                NvsStreamingContext.getInstance().setTimelineScaleForSeek(mTimeline, mTimeline.getDuration() / 1000000D / seekBar.getWidth());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playVideo(currentTime, currentTime + getDuration());
                seekTimeline(currentTime, 0);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mFragmentLoadFinisedListener != null) {
            mFragmentLoadFinisedListener.onLoadFinished();
        }
        if (mTimeline != null) {
            mTotalDurationView.setText(FormatUtils.microsecond2Time(mTimeline.getDuration()));
        }
    }

    @Override
    public void initData() {
        final boolean[] updateView = {false};
        mCutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!updateView[0]) {
                    Point size = getOriginalLiveWindowLayoutParam();
                    setCutRectViewSize(size);
                    setLiveWindowSize(size);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            initLiveWindowCenterPoint();
                            Point size = changeCutRectViewNoScale(mRatio);
                            mMinLiveWindowScale = getSuitLiveWindowScale(size);
                            mTransformData = parseToViewTransData(mTransformData);
                            mLiveWindow.setRotation(mTransformData.get(STORYBOARD_KEY_ROTATION_Z)
                                    + mTransformData.get(STORYBOARD_KEY_EXTRA_ROTATION_Z));
                            scaleLiveWindow(mTransformData.get(STORYBOARD_KEY_SCALE_X));
                            mLiveWindow.setTranslationX(mTransformData.get(STORYBOARD_KEY_TRANS_X));
                            mLiveWindow.setTranslationY(mTransformData.get(STORYBOARD_KEY_TRANS_Y));

                            FloatPoint pointLT = new FloatPoint();
                            FloatPoint pointRB = new FloatPoint();
                            float halfWidth = mLiveWindow.getWidth() * 1.0F / 2;
                            float halfHeight = mLiveWindow.getHeight() * 1.0F / 2;
                            pointLT.x = mCenterPoint.x - halfWidth;
                            pointLT.y = mCenterPoint.y - halfHeight;

                            pointRB.x = mCenterPoint.x + halfWidth;
                            pointRB.y = mCenterPoint.y + halfHeight;

                            float oldScale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
                            float degree = mTransformData.get(STORYBOARD_KEY_ROTATION_Z);
                            pointLT = transformData(pointLT, mCenterPoint, oldScale, degree,
                                    mTransformData.get(STORYBOARD_KEY_TRANS_X), mTransformData.get(STORYBOARD_KEY_TRANS_Y));
                            pointRB = transformData(pointRB, mCenterPoint, oldScale, degree,
                                    mTransformData.get(STORYBOARD_KEY_TRANS_X), mTransformData.get(STORYBOARD_KEY_TRANS_Y));

                            mCenterPoint.x = (pointLT.x + pointRB.x) / 2;
                            mCenterPoint.y = (pointLT.y + pointRB.y) / 2;
                            mRegionData = updateRegionData();

                            Rect clipRect = getClipRect(0, 0, 0);
                            mCutView.setLimitRect(clipRect);
                        }
                    });
                    updateView[0] = true;
                }
            }
        });
        connectTimelineWithLiveWindow();
        mCutView.setOnTransformListener(new CutRectLayout.OnTransformListener() {
            @Override
            public void onTrans(float deltaX, float deltaY) {
                if (deltaX == 0 || deltaY == 0) {
                    return;
                }
                float oldTransX = mTransformData.get(STORYBOARD_KEY_TRANS_X);
                float oldTransY = mTransformData.get(STORYBOARD_KEY_TRANS_Y);
                float newTransX = oldTransX;
                float newTransY = oldTransY;
                if (canTrans(deltaX, 0, 0F)) {
                    newTransX = oldTransX - deltaX;
                    mCenterPoint.x -= deltaX;
                }
                if (canTrans(0, deltaY, 0F)) {
                    newTransY = oldTransY - deltaY;
                    mCenterPoint.y -= deltaY;
                }
                mLiveWindow.setTranslationX(newTransX);
                mLiveWindow.setTranslationY(newTransY);
                mTransformData.put(STORYBOARD_KEY_TRANS_X, newTransX);
                mTransformData.put(STORYBOARD_KEY_TRANS_Y, newTransY);
                Rect clipRect = getClipRect(0, 0, 0);
                mCutView.setLimitRect(clipRect);
            }

            @Override
            public void onTransUp() {

            }

            @Override
            public void onScaleAndRotate(float scale, float degree) {
                if (scale < 1.0F && !canTrans(0, 0, -degree)) {
                    return;
                }

                float newDegree = mTransformData.get(STORYBOARD_KEY_ROTATION_Z) - degree;
                if (newDegree > 45 && degree < 0) {
                    return;
                }
                if (newDegree < -45 && degree > 0) {
                    return;
                }
                newDegree = (int) newDegree;
                if (!hasVibrator && newDegree == 0) {
                    if (mVibrator != null) {
                        mVibrator.vibrate(50);
                    } //震动一下 Give a jolt
                    hasVibrator = true;
                }
                if (newDegree != 0) {
                    hasVibrator = false;
                }
                mLiveWindow.setRotation(newDegree + mTransformData.get(STORYBOARD_KEY_EXTRA_ROTATION_Z));
                mTransformData.put(STORYBOARD_KEY_ROTATION_Z, newDegree);

                double scaleValue = computeScale(newDegree, mTransformData.get(STORYBOARD_KEY_SCALE_X) * scale);
                double newScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) * scale;
                if (newScale < scaleValue && scaleValue > 1.0F) {
                    newScale = (float) scaleValue;
                }

                if (scaleValue != 1.0F && scale < 1.0F) {
                    return;
                }

                if (newScale < mMinLiveWindowScale) {
                    newScale = mMinLiveWindowScale;
                }
                scaleLiveWindow((float) newScale);
                mTransformData.put(STORYBOARD_KEY_SCALE_X, (float) newScale);
                mTransformData.put(STORYBOARD_KEY_SCALE_Y, (float) newScale);
                if (mOnCutRectChangedListener != null) {
                    mOnCutRectChangedListener.onScaleAndRotate((float) newScale, newDegree);
                }
                Rect clipRect = getClipRect(0, 0, 0);
                mCutView.setLimitRect(clipRect);
                mTempScale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
            }

            @Override
            public void onTransEnd(float scale, float[] size) {
                if (scale < 0) {
                    rotateVideo(mTransformData.get(STORYBOARD_KEY_ROTATION_Z));
                } else {
                    float newScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) * scale;
                    scaleLiveWindow(newScale);
                    mTransformData.put(STORYBOARD_KEY_SCALE_X, newScale);
                    mTransformData.put(STORYBOARD_KEY_SCALE_Y, newScale);
                }
                Rect clipRect = getClipRect(0, 0, 0);
                mCutView.setLimitRect(clipRect);
            }

            @Override
            public void onRectMoved(float scale, Point distance, Point anchor) {
                if (mOriginalSize == null) {
                    mOriginalSize = new Point();
                }
                mOriginalSize.x = mCutView.getRectWidth();
                mOriginalSize.y = mCutView.getRectHeight();
                mMinLiveWindowScale = getSuitLiveWindowScale(mOriginalSize);

                float newScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) * scale;
                //double minScale = computeScale(mTransformData.get(STORYBOARD_KEY_ROTATION_Z), scale);
               /* if (newScale < minScale) {
                    newScale = (float) minScale;
                    scale = newScale / mTransformData.get(STORYBOARD_KEY_SCALE_X);
                }*/
                scaleLiveWindow(newScale);

                FloatPoint pointAfter = new FloatPoint();
                pointAfter.x = anchor.x;
                pointAfter.y = anchor.y;
                transformData(pointAfter, mCenterPoint, scale, 0);

                float deltaX = anchor.x - pointAfter.x + distance.x;
                float deltaY = anchor.y - pointAfter.y + distance.y;
                translateLiveWindow(deltaX, deltaY);
                mRegionData = updateRegionData();

                mRatioValue = mCutView.getRectWidth() * 1F / mCutView.getRectHeight();

                Rect clipRect = getClipRect(0, 0, 0);
                mCutView.setLimitRect(clipRect);
                mTempScale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
            }

            @Override
            public boolean canMove(Point anchor) {
                if (mTransformData.get(STORYBOARD_KEY_ROTATION_Z) == 0) {
                    return true;
                }
                return canTrans(anchor);
            }
        });
    }

    private void updateCurPlayTime(long currentTime) {
        mCurrentPlaytimeView.setText(FormatUtils.microsecond2Time(currentTime - mStartTime));
    }

    private void updatePlayProgress(long curPosition) {
        float progress = ((float) (curPosition - mStartTime) / (float) getDuration());
        mSeekBar.setProgress((int) (progress * 100));
        updateCurPlayTime(curPosition);
    }

    private void translateLiveWindow(float deltaX, float deltaY) {
        float newTransX = mTransformData.get(STORYBOARD_KEY_TRANS_X) + deltaX;
        float newTransY = mTransformData.get(STORYBOARD_KEY_TRANS_Y) + deltaY;
        mLiveWindow.setTranslationX(newTransX);
        mLiveWindow.setTranslationY(newTransY);
        mCenterPoint.x += deltaX;
        mCenterPoint.y += deltaY;
        mTransformData.put(STORYBOARD_KEY_TRANS_X, newTransX);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, newTransY);
    }

    private Point getFreeCutRectSize(float[] size) {
        float ratio = size[0] / size[1];
        int width = mCutView.getWidth();
        int height = mCutView.getHeight();
        float layoutRatio = width * 1.0F / height;
        Point rectSize = new Point();
        if (ratio > layoutRatio) { //宽对齐 Wide alignment
            rectSize.x = width;
            rectSize.y = (int) (width * 1.0F / ratio);
        } else {//高对齐 High alignment
            rectSize.y = height;
            rectSize.x = (int) (height * ratio);
        }
        return rectSize;
    }


    @Override
    public void setCutData(CutData cutData) {
        if (cutData == null) {
            return;
        }
        mRatio = cutData.getRatio();
        mRatioValue = cutData.getRatioValue();
        Map<String, Float> transformData = cutData.getTransformData();
        Set<String> keySet = transformData.keySet();
        for (String key : keySet) {
            Float aFloat = transformData.get(key);
            if (aFloat != null) {
                mTransformData.put(key, aFloat);
            }
        }
    }

    private boolean canTrans(float deltaX, float deltaY, float deltaDegree) {
        //获取4个角的顶点数据，并进行坐标转换
        //Obtain the vertex data of 4 angles and carry out coordinate transformation
        FloatPoint pointLT = new FloatPoint();
        FloatPoint pointLB = new FloatPoint();
        FloatPoint pointRT = new FloatPoint();
        FloatPoint pointRB = new FloatPoint();

        float halfWidth = mLiveWindow.getWidth() * 1.0F / 2;
        float halfHeight = mLiveWindow.getHeight() * 1.0F / 2;

        Float extraRotation = mTransformData.get(STORYBOARD_KEY_EXTRA_ROTATION_Z);
        if (extraRotation == 90 || extraRotation == 270) {
            halfHeight = mLiveWindow.getWidth() * 1.0F / 2;
            halfWidth = mLiveWindow.getHeight() * 1.0F / 2;
        }

        FloatPoint centerPoint = new FloatPoint();
        centerPoint.x = mCenterPoint.x - deltaX;
        centerPoint.y = mCenterPoint.y - deltaY;

        pointLT.x = centerPoint.x - halfWidth;
        pointLT.y = centerPoint.y - halfHeight;

        pointLB.x = centerPoint.x - halfWidth;
        pointLB.y = centerPoint.y + halfHeight;

        pointRT.x = centerPoint.x + halfWidth;
        pointRT.y = centerPoint.y - halfHeight;

        pointRB.x = centerPoint.x + halfWidth;
        pointRB.y = centerPoint.y + halfHeight;

        float degree = mTransformData.get(STORYBOARD_KEY_ROTATION_Z) + deltaDegree;
        float scale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
        pointLT = transformData(pointLT, centerPoint, scale, degree);
        pointLB = transformData(pointLB, centerPoint, scale, degree);
        pointRT = transformData(pointRT, centerPoint, scale, degree);
        pointRB = transformData(pointRB, centerPoint, scale, degree);


        if (IS_DEBUG) {
            List<FloatPoint> data = new ArrayList<>();
            data.add(pointLT);
            data.add(pointLB);
            data.add(pointRB);
            data.add(pointRT);
            mTestView.setRectPoint(data);
        }

        //判断四个顶点是否超出LiveWindow坐标范围外
        //Determine whether the four vertices are outside the LiveWindow coordinate range
        int[] location = new int[2];
        mCutView.getLocationOnScreen(location);
        int rectLeft = location[0] + mCutView.getDrawRectViewLeft();
        int rectTop = location[1] + mCutView.getDrawRectViewTop();
        int rectRight = rectLeft + mCutView.getRectWidth();
        int rectBottom = rectTop + mCutView.getRectHeight();
        //左上角 Upper left corner
        FloatPoint point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectTop;
        boolean inRectLT = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        //右上角 Upper right corner
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectTop;
        boolean inRectRT = isInRect(pointLT, pointRT, pointRB, pointLB, point);

        //右下角 Lower right corner
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectBottom;
        boolean inRectRB = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        //左下角 Lower left corner
        point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectBottom;
        boolean inRectLB = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        return (inRectLT && inRectLB && inRectRT && inRectRB);
    }

    private boolean canTrans(Point size) {
        //获取4个角的顶点数据，并进行坐标转换
        //Obtain the vertex data of 4 angles and carry out coordinate transformation
        FloatPoint pointLT = new FloatPoint();
        FloatPoint pointLB = new FloatPoint();
        FloatPoint pointRT = new FloatPoint();
        FloatPoint pointRB = new FloatPoint();

        float halfWidth = mLiveWindow.getWidth() * 1.0F / 2;
        float halfHeight = mLiveWindow.getHeight() * 1.0F / 2;

        FloatPoint centerPoint = new FloatPoint();
        centerPoint.x = mCenterPoint.x;
        centerPoint.y = mCenterPoint.y;

        pointLT.x = centerPoint.x - halfWidth;
        pointLT.y = centerPoint.y - halfHeight;

        pointLB.x = centerPoint.x - halfWidth;
        pointLB.y = centerPoint.y + halfHeight;

        pointRT.x = centerPoint.x + halfWidth;
        pointRT.y = centerPoint.y - halfHeight;

        pointRB.x = centerPoint.x + halfWidth;
        pointRB.y = centerPoint.y + halfHeight;

        float degree = mTransformData.get(STORYBOARD_KEY_ROTATION_Z);
        float scale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
        pointLT = transformData(pointLT, centerPoint, scale, degree);
        pointLB = transformData(pointLB, centerPoint, scale, degree);
        pointRT = transformData(pointRT, centerPoint, scale, degree);
        pointRB = transformData(pointRB, centerPoint, scale, degree);

        //判断四个顶点是否超出LiveWindow坐标范围外
        //Determine whether the four vertices are outside the LiveWindow coordinate range
        int[] location = new int[2];
        mCutView.getLocationOnScreen(location);
        int rectLeft = location[0] + mCutView.getDrawRectViewLeft();
        int rectTop = location[1] + mCutView.getDrawRectViewTop();
        int rectRight = rectLeft + size.x;
        int rectBottom = rectTop + size.y;
        //左上角 Upper left corner
        FloatPoint point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectTop;
        boolean inRectLT = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        //右上角 Upper right corner
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectTop;
        boolean inRectRT = isInRect(pointLT, pointRT, pointRB, pointLB, point);

        //右下角 Lower right corner
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectBottom;
        boolean inRectRB = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        //左下角 Lower left corner
        point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectBottom;
        boolean inRectLB = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        return (inRectLT && inRectLB && inRectRT && inRectRB);
    }

    private Rect getClipRect(float deltaX, float deltaY, float deltaDegree) {
        float degree = mTransformData.get(STORYBOARD_KEY_ROTATION_Z) + deltaDegree;
        if (degree == 0) {
            //获取4个角的顶点数据，并进行坐标转换
            //Obtain the vertex data of 4 angles and carry out coordinate transformation
            FloatPoint pointLT = new FloatPoint();
            FloatPoint pointRB = new FloatPoint();

            float halfWidth = mLiveWindow.getWidth() * 1.0F / 2;
            float halfHeight = mLiveWindow.getHeight() * 1.0F / 2;

            FloatPoint centerPoint = new FloatPoint();
            centerPoint.x = mCenterPoint.x - deltaX;
            centerPoint.y = mCenterPoint.y - deltaY;

            pointLT.x = centerPoint.x - halfWidth;
            pointLT.y = centerPoint.y - halfHeight;

            pointRB.x = centerPoint.x + halfWidth;
            pointRB.y = centerPoint.y + halfHeight;

            float scale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
            pointLT = transformData(pointLT, centerPoint, scale, degree);
            pointRB = transformData(pointRB, centerPoint, scale, degree);

            Rect rect = new Rect();
            rect.left = (int) pointLT.x;
            rect.top = (int) pointLT.y;
            rect.bottom = (int) pointRB.y;
            rect.right = (int) pointRB.x;
            return rect;
        }
        return null;
    }

    /**
     * 计算缩放值
     * Calculated scale value
     *
     * @param newDegree 最新的角度值  The latest Angle value
     * @param newScale  最新的缩放值  The latest scale value
     * @return 缩放值，这个值是相对于原始值的缩放值 Scale value, which is the scale value relative to the original value
     */
    private double computeScale(float newDegree, float newScale) {
        //获取4个角的顶点数据， 并进行坐标转换
        //Obtain the vertex data of 4 angles and carry out coordinate transformation
        FloatPoint pointLT = new FloatPoint();
        FloatPoint pointLB = new FloatPoint();
        FloatPoint pointRT = new FloatPoint();
        FloatPoint pointRB = new FloatPoint();

        float halfWidth = mLiveWindow.getWidth() * 1.0F / 2;
        float halfHeight = mLiveWindow.getHeight() * 1.0F / 2;
        Float extraRotation = mTransformData.get(STORYBOARD_KEY_EXTRA_ROTATION_Z);
        if (extraRotation == 90 || extraRotation == 270) {
            halfHeight = mLiveWindow.getWidth() * 1.0F / 2;
            halfWidth = mLiveWindow.getHeight() * 1.0F / 2;
        }

        pointLT.x = mCenterPoint.x - halfWidth;
        pointLT.y = mCenterPoint.y - halfHeight;

        pointLB.x = mCenterPoint.x - halfWidth;
        pointLB.y = mCenterPoint.y + halfHeight;

        pointRT.x = mCenterPoint.x + halfWidth;
        pointRT.y = mCenterPoint.y - halfHeight;

        pointRB.x = mCenterPoint.x + halfWidth;
        pointRB.y = mCenterPoint.y + halfHeight;

        pointLT = transformData(pointLT, mCenterPoint, newScale, newDegree);
        pointLB = transformData(pointLB, mCenterPoint, newScale, newDegree);
        pointRT = transformData(pointRT, mCenterPoint, newScale, newDegree);
        pointRB = transformData(pointRB, mCenterPoint, newScale, newDegree);

        if (IS_DEBUG) {
            List<FloatPoint> data = new ArrayList<>();
            data.add(pointLT);
            data.add(pointLB);
            data.add(pointRB);
            data.add(pointRT);
            mTestView.setRectPoint(data);
        }

        int[] location = new int[2];
        mCutView.getLocationOnScreen(location);
        int rectLeft = location[0] + mCutView.getDrawRectViewLeft();
        int rectTop = location[1] + mCutView.getDrawRectViewTop();
        int rectRight = rectLeft + mCutView.getRectWidth();
        int rectBottom = rectTop + mCutView.getRectHeight();


        //左上角 Upper left corner
        FloatPoint point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectTop;

        boolean inRect = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        double scale = 1.0d;
        if (!inRect) {
            //计算左上角的点到矩形上边的距离 Calculates the distance from the point in the upper left corner to the upper edge of the rectangle
            double cos = Math.cos(Math.toRadians(90 - angle(mCenterPoint, point, pointLT, pointRT)));
            float centerToPoint = lineSpace(mCenterPoint.x, mCenterPoint.y, point.x, point.y);
            double pointToLineLR = centerToPoint * cos;

            //计算左上角的点到矩形左边的距离 Calculate the distance from the point in the upper left corner to the left of the rectangle
            cos = Math.cos(Math.toRadians(90 - angle(mCenterPoint, point, pointLT, pointLB)));
            double pointToLineTB = centerToPoint * cos;

            double scaleX = pointToLineTB / halfWidth;
            double scaleY = pointToLineLR / halfHeight;
            scale = Math.max(scaleX, scaleY);
            if (Double.isNaN(scale)) {
                scale = 1.0d;
            }
        }
        double scaleTemp = scale;

        //右上角 Upper right corner
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectTop;

        inRect = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        if (!inRect) {
            //计算右上角的点到矩形右边的距离 Calculate the distance from the point in the upper right corner to the right of the rectangle
            double cos = Math.cos(Math.toRadians(90 - angle(mCenterPoint, point, pointRT, pointRB)));
            float centerToPoint = lineSpace(mCenterPoint.x, mCenterPoint.y, point.x, point.y);
            double pointToLineTB = centerToPoint * cos;
            //计算右上角的点到矩形上边的距离 Calculate the distance from the point in the upper right corner to the upper edge of the rectangle
            cos = Math.cos(Math.toRadians(90 - angle(mCenterPoint, point, pointRT, pointLT)));
            double pointToLineRL = centerToPoint * cos;

            double scaleX = pointToLineRL / halfHeight;
            double scaleY = pointToLineTB / halfWidth;
            scaleTemp = Math.max(scaleX, scaleY);
            if (Double.isNaN(scaleTemp)) {
                scaleTemp = 1.0d;
            }
        }
        scale = Math.max(scale, scaleTemp);

        //右下角 Lower right corner
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectBottom;

        inRect = isInRect(pointLT, pointRT, pointRB, pointLB, point);

        if (!inRect) {
            //计算右下角的点到矩形下边的距离 Calculate the distance from the point in the lower right corner to the bottom of the rectangle
            double cos = Math.cos(Math.toRadians(90 - angle(mCenterPoint, point, pointRB, pointLB)));
            float centerToPoint = lineSpace(mCenterPoint.x, mCenterPoint.y, point.x, point.y);
            double pointToLineRL = centerToPoint * cos;
            //计算右下角的点到矩形右边的距离 Calculate the distance from the point in the lower right corner to the right of the rectangle
            cos = Math.cos(Math.toRadians(90 - angle(mCenterPoint, point, pointRB, pointRT)));
            double pointToLineTB = centerToPoint * cos;

            double scaleX = pointToLineRL / halfHeight;
            double scaleY = pointToLineTB / halfWidth;
            scaleTemp = Math.max(scaleX, scaleY);
            if (Double.isNaN(scaleTemp)) {
                scaleTemp = 1.0d;
            }
        }
        scale = Math.max(scale, scaleTemp);

        //左下角 Lower left corner
        point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectBottom;

        inRect = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        if (!inRect) {
            //计算左下角的点到矩形下边的距离 Calculate the distance from the point in the lower left corner to the bottom of the rectangle
            double cos = Math.cos(Math.toRadians(90 - angle(mCenterPoint, point, pointLB, pointRB)));
            float centerToPoint = lineSpace(mCenterPoint.x, mCenterPoint.y, point.x, point.y);
            double pointToLineLR = centerToPoint * cos;
            //计算左下角的点到矩形左边的距离 Calculate the distance from the point in the lower left corner to the left side of the rectangle
            cos = Math.cos(Math.toRadians(90 - angle(mCenterPoint, point, pointLT, pointLB)));
            double pointToLineTB = centerToPoint * cos;

            double scaleX = pointToLineLR / halfHeight;
            double scaleY = pointToLineTB / halfWidth;
            scaleTemp = Math.max(scaleX, scaleY);
            if (Double.isNaN(scaleTemp)) {
                scaleTemp = 1.0d;
            }
        }
        scale = Math.max(scale, scaleTemp);
        return scale;
    }


    /**
     * 判断点p是否在p1p2p3p4的矩形内
     * Determine whether point p is in the rectangle of p1p2p3p4
     */
    private boolean isInRect(FloatPoint p1, FloatPoint p2, FloatPoint p3, FloatPoint p4, FloatPoint p) {
        return getCross(p1, p2, p) * getCross(p3, p4, p) >= 0 && getCross(p2, p3, p) * getCross(p4, p1, p) >= 0;
    }

    /**
     * 计算 |p1 p2| X |p1 p|
     * calculation
     */
    private float getCross(FloatPoint p1, FloatPoint p2, FloatPoint p) {
        return (p2.x - p1.x) * (p.y - p1.y) - (p.x - p1.x) * (p2.y - p1.y);
    }

    private FloatPoint transformData(FloatPoint point, FloatPoint centerPoint, float scale, float degree) {
        float[] src = new float[]{point.x, point.y};
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        matrix.setScale(scale, scale, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        point.x = Math.round(src[0]);
        point.y = Math.round(src[1]);
        return point;
    }

    private FloatPoint transformData(FloatPoint point, FloatPoint centerPoint, float scale, float degree, float transX, float transY) {
        float[] src = new float[]{point.x, point.y};
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        matrix.setScale(scale, scale, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        matrix.setTranslate(transX, transY);
        matrix.mapPoints(src);
        point.x = src[0];
        point.y = src[1];
        return point;
    }

    /**
     * 计算向量夹角，此计算值为锐角
     * Calculate the Angle between vectors. The calculated value is an acute Angle
     *
     * @param pointA1 点A1
     * @param pointA2 点A2
     * @param pointB1 点B1
     * @param pointB2 点B2
     * @return the angle
     */
    private static double angle(FloatPoint pointA1, FloatPoint pointA2, FloatPoint pointB1, FloatPoint pointB2) {
        float x1 = pointA1.x - pointA2.x;
        float y1 = pointA1.y - pointA2.y;
        float x2 = pointB1.x - pointB2.x;
        float y2 = pointB1.y - pointB2.y;
        double value = (x1 * x2 + y1 * y2) / (Math.sqrt(x1 * x1 + y1 * y1) * Math.sqrt(x2 * x2 + y2 * y2)); // 余弦值 cosine
        return Math.toDegrees(Math.acos(Math.abs(value)));// 角度 degree
    }

    /**
     * 点到直线的最短距离的判断 点（x0,y0） 到由两点组成的线段（x1,y1） ,( x2,y2 )
     * The shortest distance from a point to a line (x0,y0) to a line segment consisting of two points (x1,y1),(x2,y2).
     *
     * @param x1 点的X坐标 Point X coordinate
     * @param y1 点的Y坐标 Point Y coordinate
     * @param x2 直线上点0的X坐标 The x-coordinate of point 0 on the line
     * @param y2 直线上点0的Y坐标 The Y-coordinate of point 0 on the line
     * @param x0 直线上点1的X坐标 The x-coordinate of point 1 on the line
     * @param y0 直线上点1的Y坐标 The Y-coordinate of point 1 on the line
     * @return 点到直线的距离 The distance from the point to the line
     */
    private double pointToLine(float x1, float y1, float x2, float y2, float x0,
                               float y0) {
        double space;
        float a, b, c;
        a = lineSpace(x1, y1, x2, y2);// 线段的长度 The length of a line segment
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离 x1,y1 is the distance from the point
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离 x2,y2 is the distance from the point
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长 Half circumference
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积 Helen's formula for area
        if (a > 0) {
            space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高） Return the distance from the point to the line (using the triangle area formula for height)
        } else {
            space = 0;
        }
        return space;
    }


    /**
     * 计算两点之间的距离
     * Calculate the distance between two points
     */
    private float lineSpace(float x1, float y1, float x2, float y2) {
        float lineLength = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2));
        return lineLength;
    }

    /**
     * 点到直线的距离
     * The distance from the point to the line
     *
     * @param anchor 点的坐标 Point coordinates
     * @param pointA 直线上的点A Point A on the line
     * @param pointB 直线上的点B Point B on the line
     */
    private double getPointToLine(FloatPoint anchor, FloatPoint pointA, FloatPoint pointB) {
        return pointToLine(pointA.x, pointA.y, pointB.x, pointB.y, anchor.x, anchor.y);
    }

    /**
     * 点的X坐标到直线的距离
     * The distance from the x-coordinate of the point to the line
     *
     * @param anchor 点的坐标 Point coordinates
     * @param pointA 直线上的点A Point A on the line
     * @param pointB 直线上的点B Point B on the line
     */
    private float getPointToLineX(FloatPoint anchor, FloatPoint pointA, FloatPoint pointB) {
        if (pointA.y - pointB.y <= 0.000001) {
            return 0;
        }
        float deltaX = (pointA.x - pointB.x) / (pointA.y - pointB.y) * (pointA.y - anchor.y);
        return Math.abs(deltaX);

    }

    /**
     * 点的Y坐标到直线的距离
     * The distance from the Y-coordinate of the point to the line
     *
     * @param anchor 点的坐标 Point coordinates
     * @param pointA 直线上的点A Point A on the line
     * @param pointB 直线上的点B Point B on the line
     */
    private double getPointToLineY(FloatPoint anchor, FloatPoint pointA, FloatPoint pointB) {
        if (pointA.x - pointB.x <= 0.000001) {
            return 0;
        }
        float deltaY = (pointA.x - anchor.x) * (pointA.y - pointB.y) / (pointA.x - pointB.x);
        return Math.abs(deltaY);
    }


    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    public long getDuration() {
        if (mMaxDuration > 0L) {
            return mMaxDuration;
        }
        if (mTimeline == null) {
            return 0L;
        }
        return mTimeline.getDuration();
    }

    private void setLiveWindowRatio(int ratio) {
        if (null == mTimeline) {
            return;
        }
        Point size;
        if (ratio == NvsConstants.AspectRatio.AspectRatio_NoFitRatio) {
            size = getOriginalLiveWindowLayoutParam();
        } else {
            size = getLiveWindowSizeByRatio(ratio);
        }
        setLiveWindowSize(size);
        setCutRectViewSize(size);
        if (ratio == NvsConstants.AspectRatio.AspectRatio_NoFitRatio) {
            mCutView.setWidthHeightRatio(-1);
        } else {
            mCutView.setWidthHeightRatio(size.x * 1.0F / size.y);
        }
    }

    private void setLiveWindowSize(Point size) {
        ViewGroup.LayoutParams layoutParams = mLiveWindow.getLayoutParams();
        layoutParams.width = size.x;
        layoutParams.height = size.y;
        mLiveWindow.setLayoutParams(layoutParams);
    }

    private void setCutRectViewSize(Point size) {
        mCutView.setDrawRectSize(size.x, size.y);
    }

    private void connectTimelineWithLiveWindow() {
        if (mStreamingContext == null || mTimeline == null || mLiveWindow == null) {
            return;
        }
        mStreamingContext.connectTimelineWithLiveWindowExt(mTimeline, mLiveWindow);
        mCutView.setDrawRectSize(mLiveWindow.getWidth(), mLiveWindow.getHeight());
    }

    private Point getOriginalLiveWindowLayoutParam() {
        if (mTimeline == null) {
            throw new NullPointerException("mTimeline == null");
        }
        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        Point size = new Point();
        int screenWidth = mPlayerLayout.getWidth();
        int newHeight = mPlayerLayout.getHeight();
        int imageWidth = videoRes.imageWidth;
        int imageHeight = videoRes.imageHeight;
        float viewRatio = screenWidth * 1.0F / newHeight;
        float timelineRation = imageWidth * 1.0F / imageHeight;
        float ratio = 1.0F;
        if (timelineRation > viewRatio) {
            size.x = screenWidth;
            ratio = screenWidth * 1.0F / imageWidth;
            size.y = (int) (ratio * imageHeight);
        } else {
            size.y = newHeight;
            ratio = newHeight * 1.0F / imageHeight;
            size.x = (int) (ratio * imageWidth);
        }
        return size;
    }

    private Point getLiveWindowSizeByRatio(int ratio) {
        int screenWidth = mPlayerLayout.getWidth();
        int newHeight = mPlayerLayout.getHeight();
        Point size = new Point();

        // 16:9
        if (ratio == NvAsset.AspectRatio_9v16) { //9:16
            size.x = (int) (newHeight * 9.0 / 16);
            size.y = newHeight;
        } else if (ratio == NvAsset.AspectRatio_3v4) { // 3:4
            size.x = screenWidth;
            size.y = (int) (screenWidth * 4.0 / 3);
            if (size.y > newHeight) {
                size.y = newHeight;
                size.x = newHeight * 3 / 4;
            }
        } else if (ratio == NvAsset.AspectRatio_9v18) { //9:16
            size.x = (int) (newHeight * 9.0 / 18);
            size.y = newHeight;
        } else if (ratio == NvAsset.AspectRatio_9v21) { //9:16
            size.x = (int) (newHeight * 9.0 / 21);
            size.y = newHeight;
        } else if (ratio == NvAsset.AspectRatio_1v1) { //1:1
            size.x = screenWidth;
            size.y = screenWidth;
            if (newHeight < screenWidth) {
                size.x = newHeight;
                size.y = newHeight;
            }
        } else if (ratio == NvAsset.AspectRatio_16v9) { // 16:9
            size.x = screenWidth;
            size.y = (int) (screenWidth * 9.0 / 16);
        } else if (ratio == NvAsset.AspectRatio_4v3) { //4:3
            size.x = screenWidth;
            size.y = (int) (screenWidth * 3.0 / 4);
            if (size.y > newHeight) {
                size.x = newHeight * 4 / 3;
                size.y = newHeight;
            }
        } else if (ratio == NvAsset.AspectRatio_18v9) {
            size.x = screenWidth;
            size.y = screenWidth * 9 / 18;
            if (size.y > newHeight) {
                size.x = newHeight * 18 / 9;
                size.y = newHeight;
            }
        } else if (ratio == NvAsset.AspectRatio_21v9) {
            size.x = screenWidth;
            size.y = screenWidth * 9 / 21;
            if (size.y > newHeight) {
                size.x = newHeight * 21 / 9;
                size.y = newHeight;
            }
        } else {
            size.x = screenWidth;
            size.y = newHeight;
        }
        return size;
    }

    private Point getLiveWindowSizeByRatio(float ratio) {
        int screenWidth = mPlayerLayout.getWidth();
        int newHeight = mPlayerLayout.getHeight();
        float viewRatio = screenWidth * 1.0F / newHeight;
        Point size = new Point();
        if (ratio >= viewRatio) {
            size.x = screenWidth;
            size.y = (int) (screenWidth / ratio);
        } else {
            size.x = (int) (newHeight * ratio);
            size.y = newHeight;
        }
        return size;
    }

    @Override
    public void rotateVideo(float degree) {
        mLiveWindow.setRotation(degree + mTransformData.get(STORYBOARD_KEY_EXTRA_ROTATION_Z));
        mTransformData.put(STORYBOARD_KEY_ROTATION_Z, degree);
        float scaleValue = (float) computeScale(degree, mTempScale);
        //不能小于当前缩放的值 Cannot be less than the current zoom value.
        if (scaleValue < mTempScale) {
            scaleValue = mTempScale;
        }
        scaleLiveWindow(scaleValue);
        if (degree != 0) {
            mCutView.setLimitRect(null);
        }
    }

    private void scaleLiveWindow(float scaleValue) {
        if (Float.isNaN(scaleValue) || Float.isInfinite(scaleValue)) {
            LogUtils.e("scaleValue is NaN or Infinite!");
            scaleValue = mMinLiveWindowScale;
        }
        mLiveWindow.setScaleX(scaleValue);
        mLiveWindow.setScaleY(scaleValue);
        mTransformData.put(STORYBOARD_KEY_SCALE_X, scaleValue);
        mTransformData.put(STORYBOARD_KEY_SCALE_Y, scaleValue);
    }


    @Override
    public void reset() {
        mLiveWindow.setTranslationX(0);
        mLiveWindow.setTranslationY(0);
        mLiveWindow.setRotation(0);
        scaleLiveWindow(1.0F);
        setLiveWindowRatio(NvsConstants.AspectRatio.AspectRatio_NoFitRatio);
        mOriginalSize = null;
        mTransformData.put(STORYBOARD_KEY_ROTATION_Z, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_X, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, 0F);
        mTransformData.put(STORYBOARD_KEY_EXTRA_SCALE_X, 1F);
        mTransformData.put(STORYBOARD_KEY_EXTRA_SCALE_Y, 1F);
        mTransformData.put(STORYBOARD_KEY_EXTRA_ROTATION_Z, 0F);
        initLiveWindowCenterPoint();
        mRatio = NvsConstants.AspectRatio.AspectRatio_NoFitRatio;
        mMinLiveWindowScale = 1.0F;
        mRegionData = updateRegionData();
        Rect clipRect = getClipRect(0, 0, 0);
        mCutView.setLimitRect(clipRect);
        mTempScale = 1.0F;
        mRatioValue = mCutView.getRectWidth() * 1F / mCutView.getRectHeight();
    }

    private int mRatio = NvsConstants.AspectRatio.AspectRatio_NoFitRatio;
    private float mRatioValue = -1F;

    @Override
    public void changeCutRectView(int ratio) {
        //LiveWindow和 CutRectView需要分别设置，
        // LiveWindow适配CutRectView大小，需要和CutRectView宽和高对齐，
        // 如果LiveWindow宽或高有一边没有对齐，LiveWindow需要进行缩放
        //LiveWindow and CutRectView need to be set separately.
        //LiveWindow fits the CutRectView size. It should be aligned with the CutRectView width and height.
        //If the LiveWindow width or height side is not aligned, the LiveWindow needs to be scaled
        Point size;
        if (ratio == NvsConstants.AspectRatio.AspectRatio_NoFitRatio) {
            if (mOriginalSize == null) {
                size = getOriginalLiveWindowLayoutParam();
                mOriginalSize = size;
            } else {
                size = mOriginalSize;
            }
        } else {
            size = getLiveWindowSizeByRatio(ratio);
        }
        mRatioValue = size.x * 1F / size.y;
        setCutRectViewSize(size);
        if (ratio == NvsConstants.AspectRatio.AspectRatio_NoFitRatio) {
            mCutView.setWidthHeightRatio(-1);
        } else {
            mCutView.setWidthHeightRatio(size.x * 1.0F / size.y);
        }
        mMinLiveWindowScale = getSuitLiveWindowScale(size);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRegionData = updateRegionData();
                float rotation = mTransformData.get(STORYBOARD_KEY_ROTATION_Z);
                if (rotation == 0) {
                    float oldScale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
                    if (mMinLiveWindowScale > oldScale) {
                        scaleLiveWindow(mMinLiveWindowScale);
                    } else {
                        rotateVideo(rotation);
                    }
                } else {
                    rotateVideo(rotation);
                }
                Rect clipRect = getClipRect(0, 0, 0);
                mCutView.setLimitRect(clipRect);
                mTempScale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
            }
        });
        mRatio = ratio;
    }

    private RectF mRegionData;

    private RectF updateRegionData() {
        return getRectEx(mCutView.getRectWidth(), mCutView.getRectHeight(), mLiveWindow.getWidth(), mLiveWindow.getHeight());
    }

    private Point changeCutRectViewNoScale(int ratio) {
        //LiveWindow和 CutRectView需要分别设置，
        // LiveWindow适配CutRectView大小，需要和CutRectView宽和高对齐，
        // 如果LiveWindow宽或高有一边没有对齐，LiveWindow需要进行缩放
        //LiveWindow and CutRectView need to be set separately.
        //LiveWindow fits the CutRectView size. It should be aligned with the CutRectView width and height.
        //If the LiveWindow width or height side is not aligned, the LiveWindow needs to be scaled
        Point size;
        if (mRatioValue > 0) {
            size = getLiveWindowSizeByRatio(mRatioValue);
        } else {
            if (ratio == NvsConstants.AspectRatio.AspectRatio_NoFitRatio) {
                size = getOriginalLiveWindowLayoutParam();
            } else {
                size = getLiveWindowSizeByRatio(ratio);
            }
        }
        mRatioValue = size.x * 1F / size.y;
        setCutRectViewSize(size);
        if (ratio == NvsConstants.AspectRatio.AspectRatio_NoFitRatio) {
            mCutView.setWidthHeightRatio(-1);
        } else {
            mCutView.setWidthHeightRatio(size.x * 1.0F / size.y);
        }
        mRatio = ratio;
        if (mOnCutRectChangedListener != null) {
            mOnCutRectChangedListener.onSizeChanged(size);
        }
        return size;
    }

    private void initLiveWindowCenterPoint() {
        int[] location = new int[2];
        mLiveWindow.getLocationOnScreen(location);
        int locationX = location[0];
        int locationY = location[1];
        mCenterPoint.x = locationX + mLiveWindow.getWidth() * 1.0F / 2;
        mCenterPoint.y = locationY + mLiveWindow.getHeight() * 1.0F / 2;
    }

    private float getSuitLiveWindowScale(Point rectSize) {
        int liveWindowWidth = mLiveWindow.getWidth();
        int liveWindowHeight = mLiveWindow.getHeight();
        float widthScale = rectSize.x * 1.0F / liveWindowWidth;
        float heightScale = rectSize.y * 1.0F / liveWindowHeight;
        float scale = widthScale;
        if (scale < heightScale) {
            scale = heightScale;
        }
        if (scale < 1) {//只放大，不缩小 Zoom in, not out
            return -1;
        }
        return scale;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void setTimeLine(NvsTimeline timeline) {
        mTimeline = timeline;
    }

    public void playVideoButtonClick() {
        if (mTimeline == null) {
            return;
        }
        long endTime = getDuration();
        playVideoButtonClick(0, endTime);
    }


    public void playVideoButtonClick(long inPoint, long outPoint) {
        playVideo(inPoint, outPoint);
    }

    public void playVideo(long startTime, long endTime) {
        mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    public void playVideoFromStartPosition() {
        mStreamingContext.playbackTimeline(mTimeline, mStartTime, mStartTime + getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    @Override
    public void seekTimeline(long timestamp, int seekShowMode) {
        mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, seekShowMode);
    }

    public int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    private void stopEngine() {
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
    }


    public NvsTimeline getTimeLine() {
        return mTimeline;
    }

    @Override
    public float[] getRegionData(float[] size) {
        if (mRegionData == null) {
            return null;
        }
        RectF rectF = new RectF();
        rectF.top = mRegionData.top * size[1];
        rectF.bottom = mRegionData.bottom * size[1];
        rectF.left = mRegionData.left * size[0];
        rectF.right = mRegionData.right * size[0];
        float[] regionData;
        if (mRatio == NvsConstants.AspectRatio.AspectRatio_NoFitRatio) {
            regionData = new float[10];
            regionData[0] = rectF.left;
            regionData[1] = rectF.top;
            regionData[2] = rectF.right;
            regionData[3] = rectF.top;
            regionData[4] = rectF.right;
            regionData[5] = rectF.bottom;
            regionData[6] = rectF.left;
            regionData[7] = rectF.bottom;
            regionData[8] = rectF.left;
            regionData[9] = 0;
        } else {
            regionData = new float[8];
            regionData[0] = rectF.left;
            regionData[1] = rectF.top;
            regionData[2] = rectF.right;
            regionData[3] = rectF.top;
            regionData[4] = rectF.right;
            regionData[5] = rectF.bottom;
            regionData[6] = rectF.left;
            regionData[7] = rectF.bottom;
        }
        return regionData;
    }

    @Override
    public int getRatio() {
        return mRatio;
    }

    @Override
    public float getRatioValue() {
        return mRatioValue;
    }

    private RectF getRect(int timelineWidth, int timelineHeight, int imageWidth, int imageHeight, float[] size) {
        if (size == null) {
            size = new float[2];
            size[0] = 1.0F;
            size[1] = 1.0F;
        }
        RectF rectF = new RectF();
        float imageRatio = imageWidth * 1.0F / imageHeight;
        float timelineRatio = timelineWidth * 1.0F / timelineHeight;
        if (imageRatio > timelineRatio) {
            float scale = timelineWidth * 1.0F / imageWidth;
            float timelineImageHeight = imageHeight * scale;
            rectF.top = timelineImageHeight / timelineHeight * size[1];
            rectF.bottom = -rectF.top;
            rectF.right = 1 * size[0];
            rectF.left = -rectF.right;
        } else {
            float scale = timelineHeight * 1.0F / imageHeight;
            float timelineImageWidth = imageWidth * scale;

            rectF.top = 1.0F * size[1];
            rectF.bottom = -rectF.top;
            rectF.right = timelineImageWidth / timelineWidth * size[0];
            rectF.left = -rectF.right;
        }
        return rectF;
    }

    private RectF getRectEx(int rectWidth, int rectHeight, int imageWidth, int imageHeight) {
        imageWidth = (int) (imageWidth * mMinLiveWindowScale);
        imageHeight = (int) (imageHeight * mMinLiveWindowScale);
        RectF rectF = new RectF();
        float imageRatio = imageWidth * 1.0F / imageHeight;
        float rectRatio = rectWidth * 1.0F / rectHeight;
        if (rectRatio > imageRatio) {
            rectF.right = rectWidth * 1.0F / imageWidth;
            rectF.left = -rectF.right;

            float scale = rectWidth * 1.0F / imageWidth;
            float timelineImageHeight = imageHeight * scale;

            rectF.top = rectHeight * 1.0F / timelineImageHeight;
            rectF.bottom = -rectF.top;

        } else {
            rectF.top = rectHeight * 1.0F / imageHeight;
            rectF.bottom = -rectF.top;

            float scale = rectHeight * 1.0F / imageHeight;
            float timelineImageWidth = imageWidth * scale;
            rectF.right = rectWidth * 1.0F / timelineImageWidth;
            rectF.left = -rectF.right;
        }
        return rectF;
    }


    private Map<String, Float> mTransformData = new HashMap<>();


    @Override
    public Map<String, Float> getTransFromData(int originalTimelineWidth, int originalTimelineHeight) {
        return parseToTimelineTransData(originalTimelineWidth, originalTimelineHeight);
    }

    public int[] getSize() {
        int[] size = new int[2];
        size[0] = (int) (mLiveWindow.getWidth() * mMinLiveWindowScale);
        size[1] = (int) (mLiveWindow.getHeight() * mMinLiveWindowScale);
        return size;
    }

    @Override
    public int[] getRectViewSize() {
        return new int[]{mCutView.getRectWidth(), mCutView.getRectHeight()};
    }

    private Map<String, Float> parseToTimelineTransData(int originalTimelineWidth, int originalTimelineHeight) {
        Map<String, Float> result = new HashMap<>();
        float realScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) / mMinLiveWindowScale;
        float realTransX = mTransformData.get(STORYBOARD_KEY_TRANS_X);
        float realTransY = mTransformData.get(STORYBOARD_KEY_TRANS_Y);
        result.put(STORYBOARD_KEY_SCALE_X, realScale);
        result.put(STORYBOARD_KEY_SCALE_Y, realScale);
        result.put(STORYBOARD_KEY_TRANS_X, realTransX);
        result.put(STORYBOARD_KEY_TRANS_Y, realTransY);
        result.put(STORYBOARD_KEY_ROTATION_Z, mTransformData.get(STORYBOARD_KEY_ROTATION_Z));
        result.put(STORYBOARD_KEY_EXTRA_ROTATION_Z, mTransformData.get(STORYBOARD_KEY_EXTRA_ROTATION_Z));
        result.put(STORYBOARD_KEY_EXTRA_SCALE_X, mTransformData.get(STORYBOARD_KEY_EXTRA_SCALE_X));
        result.put(STORYBOARD_KEY_EXTRA_SCALE_Y, mTransformData.get(STORYBOARD_KEY_EXTRA_SCALE_Y));
        return result;
    }

    private Map<String, Float> parseToViewTransData(Map<String, Float> originalData) {
        Map<String, Float> result = new HashMap<>();
        float viewScale = originalData.get(STORYBOARD_KEY_SCALE_X) * mMinLiveWindowScale;
        float viewTransX = originalData.get(STORYBOARD_KEY_TRANS_X);
        float viewTransY = originalData.get(STORYBOARD_KEY_TRANS_Y);
        result.put(STORYBOARD_KEY_SCALE_X, viewScale);
        result.put(STORYBOARD_KEY_SCALE_Y, viewScale);
        result.put(STORYBOARD_KEY_TRANS_X, viewTransX);
        result.put(STORYBOARD_KEY_TRANS_Y, -viewTransY);
        result.put(STORYBOARD_KEY_ROTATION_Z, originalData.get(STORYBOARD_KEY_ROTATION_Z));
        result.put(STORYBOARD_KEY_EXTRA_ROTATION_Z, originalData.get(STORYBOARD_KEY_EXTRA_ROTATION_Z));
        result.put(STORYBOARD_KEY_EXTRA_SCALE_X, originalData.get(STORYBOARD_KEY_EXTRA_SCALE_X));
        result.put(STORYBOARD_KEY_EXTRA_SCALE_Y, originalData.get(STORYBOARD_KEY_EXTRA_SCALE_Y));
        return result;
    }

    public void changeHorizontal(float scaleX) {
        mTransformData.put(STORYBOARD_KEY_EXTRA_SCALE_X, scaleX);
    }

    public void changeVertical(float scaleY) {
        mTransformData.put(STORYBOARD_KEY_EXTRA_SCALE_Y, scaleY);
    }

    public interface OnSaveOperationListener {
        void onSaveCurrentTimeline();
    }

    public interface OnLiveWindowClickListener {
        void onLiveWindowClick();
    }

    public interface OnFragmentLoadFinisedListener {
        void onLoadFinished();
    }

    public void setFragmentLoadFinisedListener(OnFragmentLoadFinisedListener fragmentLoadFinisedListener) {
        this.mFragmentLoadFinisedListener = fragmentLoadFinisedListener;
    }

    public void setVideoFragmentCallBack(VideoFragmentListener videoFragmentCallBack) {
        this.mVideoFragmentCallBack = videoFragmentCallBack;
    }

    public interface OnPlayProgressChangeListener {
        void onPlayProgressChanged(long curTime);

        void onPlayStateChanged(boolean isPlaying);
    }

    public interface VideoFragmentListener {

        void playBackEOF(NvsTimeline timeline);

        void playStopped(NvsTimeline timeline);

        void playbackTimelinePosition(NvsTimeline timeline, long stamp);

        void streamingEngineStateChanged(int state);
    }
}
