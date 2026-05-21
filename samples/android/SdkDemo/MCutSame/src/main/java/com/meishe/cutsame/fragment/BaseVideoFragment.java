package com.meishe.cutsame.fragment;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.model.BaseFragment;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.cutsame.R;
import com.meishe.cutsame.view.CaptionBox;
import com.meishe.engine.bean.BaseInfo;
import com.meishe.engine.constant.NvsConstants;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.interf.VideoFragmentListenerWithClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/11/3 16:16
 * 基础的视频fragment类
 * The basic video Fragment class
 */
public class BaseVideoFragment extends BaseFragment {
    private static final float RATIO_16_9 = 16f / 9f;// 1.7777
    private static final float RATIO_4_3 = 4f / 3f;//1.33333
    private static final float RATIO_1_1 = 1f / 1f;// 1
    private static final float RATIO_3_4 = 3f / 4f;//0.75
    private static final float RATIO_9_16 = 9f / 16f;// 0.5625
    public static final String SHOW_BUTTON = "showButton";
    private NvsLiveWindow mNvsLiveWindow;
    private CaptionBox mCaptionBox;
    private VideoFragmentListenerWithClick mVideoFragmentListener;
    private ImageView mFragmentBaseButton;
    private boolean mIsShowButton = false;
    private RelativeLayout mPlayerLayout;

    public BaseVideoFragment() {
    }

    public static BaseVideoFragment newInstance(boolean showButton) {
        BaseVideoFragment fragment = new BaseVideoFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOW_BUTTON, showButton);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Bind layout int.
     * 绑定布局
     *
     * @return the int
     */
    @Override
    protected int bindLayout() {
        return R.layout.cut_same_fragment_video;
    }

    /**
     * On lazy load.
     * 延迟加载
     */
    @Override
    protected void onLazyLoad() {

    }

    /**
     * Init view.
     * 初始化视图
     *
     * @param mRootView the m root view
     */
    @Override
    protected void initView(View mRootView) {
        mPlayerLayout = mRootView.findViewById(R.id.fragment_base_parent);
        mNvsLiveWindow = mRootView.findViewById(R.id.fragment_base_live_window);
        mCaptionBox = mRootView.findViewById(R.id.caption_box);
        mNvsLiveWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoFragmentListener != null && mVideoFragmentListener.clickLiveWindowByOthers()) {

                } else {
                    if (!EditorController.getInstance().isPlaying()) {
                        EditorController.getInstance().playNow();
                    } else {
                        EditorController.getInstance().stop();
                    }
                }
            }
        });
        setLiveWindowRatio();
        EditorController.getInstance().connectTimelineWithLiveWindow(mNvsLiveWindow, mVideoFragmentListener);
        EditorController.getInstance().seekTimeline();
        mFragmentBaseButton = mRootView.findViewById(R.id.fragment_base_button);
        mFragmentBaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoFragmentListener != null && mVideoFragmentListener.clickPlayButtonByOthers()) {

                } else {
                    if (!EditorController.getInstance().isPlaying()) {
                        EditorController.getInstance().playNow();
                    } else {
                        EditorController.getInstance().stop();
                    }
                }
            }
        });
    }

    /**
     * Connect timeline with live window.
     * 连接时间轴与活动窗口
     */
    public void connectTimelineWithLiveWindow() {
        EditorController.getInstance().connectTimelineWithLiveWindow(mNvsLiveWindow, mVideoFragmentListener);
        EditorController.getInstance().seekTimeline();
    }

    /**
     * Sets live window ratio.
     * 设置活动窗口比率
     */
    public void setLiveWindowRatio() {
        NvsVideoResolution videoRes = EditorController.getInstance().getNvsTimeline().getVideoRes();
        final int makeRatio = getAspectRatio(videoRes.imageWidth, videoRes.imageHeight);
        final float ratio = 1.0f * videoRes.imageWidth / videoRes.imageHeight;
        if (mPlayerLayout.getWidth() == 0 && mPlayerLayout.getHeight() == 0) {
            mPlayerLayout.post(new Runnable() {
                @Override
                public void run() {
                    setLiveWindowRatio(makeRatio, mPlayerLayout.getHeight(), ratio);
                }
            });
        } else {
            setLiveWindowRatio(makeRatio, mPlayerLayout.getHeight(), ratio);
        }
    }

    private int getAspectRatio(int width, int height) {
        float ratio = 1.0f * width / height;
        if (isFloatEqual(ratio, RATIO_16_9)) {
            return BaseInfo.AspectRatio_16v9;
        } else if (isFloatEqual(ratio, RATIO_4_3)) {
            return BaseInfo.AspectRatio_4v3;
        } else if (isFloatEqual(ratio, RATIO_1_1)) {
            return BaseInfo.AspectRatio_1v1;
        } else if (isFloatEqual(ratio, RATIO_3_4)) {
            return BaseInfo.AspectRatio_3v4;
        } else if (isFloatEqual(ratio, RATIO_9_16)) {
            return BaseInfo.AspectRatio_9v16;
        }
        return BaseInfo.AspectRatio_NoFitRatio;
    }

    private boolean isFloatEqual(float a, float b) {
        return Math.abs(a - b) <= 1e-6;
    }

    /**
     * Sets live window ratio.
     * 设置活动窗口比率
     *
     * @param ratioType the ratio type 比率类型
     * @param newHeight the new height 新高度
     * @param ratio     the ratio 比率
     */
    public void setLiveWindowRatio(int ratioType, int newHeight, float ratio) {
        ViewGroup.LayoutParams layoutParams = mPlayerLayout.getLayoutParams();
        int screenWidth = ScreenUtils.getScreenWidth();
        // 16:9
        if (ratioType == NvsConstants.AspectRatio.AspectRatio_NoFitRatio) {
            if (ratio - 1 > 0) {
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth / ratio);
            } else {
                layoutParams.width = (int) (newHeight * ratio);
                layoutParams.height = newHeight;
            }
        } else if (ratioType == NvsConstants.AspectRatio.AspectRatio_16v9) { // 16:9
            layoutParams.width = screenWidth;
            layoutParams.height = (int) (screenWidth * 9.0 / 16);
        } else if (ratioType == NvsConstants.AspectRatio.AspectRatio_1v1) { //1:1
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth;
            if (newHeight < screenWidth) {
                layoutParams.width = newHeight;
                layoutParams.height = newHeight;
            }
        } else if (ratioType == NvsConstants.AspectRatio.AspectRatio_9v16) { //9:16
            layoutParams.width = (int) (newHeight * 9.0 / 16);
            layoutParams.height = newHeight;
        } else if (ratioType == NvsConstants.AspectRatio.AspectRatio_3v4) { // 3:4
            layoutParams.width = (int) (newHeight * 3.0 / 4);
            layoutParams.height = newHeight;
        } else if (ratioType == NvsConstants.AspectRatio.AspectRatio_4v3) { //4:3
            layoutParams.width = screenWidth;
            layoutParams.height = (int) (screenWidth * 3.0 / 4);
        } else {
            layoutParams.width = screenWidth;
            layoutParams.height = (int) (screenWidth * 9.0 / 16);
        }
        mPlayerLayout.setLayoutParams(layoutParams);
        //mNvsLiveWindow.repaintVideoFrame();

    }

    /**
     * Init data.
     * 初始化数据
     */
    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mIsShowButton = bundle.getBoolean(SHOW_BUTTON);
        }
        changePlayButtonState(true);
        if (mVideoFragmentListener != null) {
            mVideoFragmentListener.connectTimelineWithLiveWindow();
        }
    }

    /**
     * Change play button state.
     * 改变播放按钮状态
     *
     * @param show the show
     */
    public void changePlayButtonState(boolean show) {
        if (mIsShowButton && (mFragmentBaseButton != null)) {
            mFragmentBaseButton.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Sets video fragment listener.
     * 设置视频片段监听器
     *
     * @param videoFragmentListener the video fragment listener 视频片段监听器
     */
    public void setVideoFragmentListener(VideoFragmentListenerWithClick videoFragmentListener) {
        mVideoFragmentListener = videoFragmentListener;
    }

    /**
     * 显示字幕边框
     * Show caption box
     *
     * @param list the pointF list of caption
     */
    public void showCaptionBox(List<PointF> list) {
        mCaptionBox.setVisibility(View.VISIBLE);
        mCaptionBox.setPointFList(transformCoordinates(list));
    }

    /**
     * 显示复合字幕边框
     * Show compound caption box
     *
     * @param list      the pointF list of caption
     * @param childList the child pointF list of caption
     */
    public void showCaptionBox(List<PointF> list, List<List<PointF>> childList) {
        if (CommonUtils.isEmpty(childList) || CommonUtils.isEmpty(list)) {
            return;
        }
        mCaptionBox.setVisibility(View.VISIBLE);
        List<List<PointF>> childListNew = new ArrayList<>();
        for (List<PointF> pointFS : childList) {
            childListNew.add(transformCoordinates(pointFS));
        }
        mCaptionBox.setPointFList(transformCoordinates(list), childListNew);
    }

    /**
     * 隐藏字幕边框
     * Not show caption box
     */
    public void notShowCaptionBox() {
        mCaptionBox.setVisibility(View.INVISIBLE);
    }

    /**
     * 转换坐标
     * transform coordinates
     */
    private List<PointF> transformCoordinates(List<PointF> list) {
        if (list == null || list.size() < 4) {
            return null;
        }
        List<PointF> newList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            PointF pointF = mNvsLiveWindow.mapCanonicalToView(list.get(i));
            newList.add(pointF);
        }
        return newList;
    }

    /**
     * On pause.
     * 暂停
     */
    @Override
    public void onPause() {
        super.onPause();
        EditorController.getInstance().stop();
    }

    /**
     * On destroy view.
     * 销毁视图
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
