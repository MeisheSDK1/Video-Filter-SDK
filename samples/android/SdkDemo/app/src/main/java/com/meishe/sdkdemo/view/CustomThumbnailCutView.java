package com.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.cutsame.view.MultiThumbnailSequenceView2;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.view.MultiThumbnailSequenceView;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/11/4 10:25
 * 调整视图的类
 * Adjust the view's classes
 */
public class CustomThumbnailCutView extends RelativeLayout {
    private static final float COVER_WIDTH = 0.7f;
    private static final float COVER_MARGIN = 0.15f;
    public static final int FROM_VIDEO = 0;
    public static final int FROM_USER = 1;
    private MultiThumbnailSequenceView2 mNvsMultiThumbnailSequenceView;
    private View mCutViewCover;
    private View mCutViewScroller;
    private List<ClipInfo> mCutClips;
    private int mCoverWidth;
    private int mCoverMargin;
    private long mVideoLimit = -1;
    private int mState = -1;
    private MultiThumbnailSequenceView2.OnScrollListener mOnScrollListener;

    public CustomThumbnailCutView(Context context) {
        super(context);
        init(context);
    }

    public CustomThumbnailCutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomThumbnailCutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCoverWidth = (int) (ScreenUtils.getScreenWidth() * COVER_WIDTH);
        mCoverMargin = (int) (ScreenUtils.getScreenWidth() * COVER_MARGIN);
        LayoutInflater inflater = LayoutInflater.from(context);
        View parentView = inflater.inflate(R.layout.view_custom_cut_thumbnail, this);
        mNvsMultiThumbnailSequenceView = parentView.findViewById(R.id.cut_view_sequence);
        mCutViewCover = parentView.findViewById(R.id.cut_view_cover);
        mCutViewScroller = parentView.findViewById(R.id.cut_view_scroller);
        LayoutParams layoutParams = (LayoutParams) mCutViewCover.getLayoutParams();
        layoutParams.leftMargin = mCoverMargin;
        layoutParams.rightMargin = mCoverMargin;
        mCutViewCover.setLayoutParams(layoutParams);
        Drawable drawable = CommonUtils.getRadiusDrawable(getResources().getDimensionPixelOffset(R.dimen.dp_px_3),
                getResources().getColor(R.color.white), getResources().getDimensionPixelOffset(R.dimen.dp_px_6), getResources().getColor(R.color.transparent));
        mCutViewCover.setBackground(drawable);
        mNvsMultiThumbnailSequenceView.setStartPadding(mCoverMargin);
        mNvsMultiThumbnailSequenceView.setEndPadding(mCoverMargin);
        mNvsMultiThumbnailSequenceView.setScrollListener(new MultiThumbnailSequenceView2.OnScrollListener() {
            @Override
            public void onScrollChanged(int dx, int oldDx) {
//                 LogUtils.d(("onScrollChanged: " + dx + "  " + oldDx + "  " + EditorController.getInstance().lengthToDuration(dx, mNvsMultiThumbnailSequenceView.getPixelPerMicrosecond())));
//                if (mState != FROM_USER) {
//                    return;
//                }
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollChanged(dx, oldDx);
                }
            }

            @Override
            public void onScrollStopped() {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStopped();
                }
            }

            @Override
            public void onSeekingTimeline() {
                mState = FROM_USER;
                if (mOnScrollListener != null) {
                    mOnScrollListener.onSeekingTimeline();
                }
            }
        });
    }

    private void refreshVideoView() {
        if (CommonUtils.isEmpty(mCutClips) || mVideoLimit <= 0) {
            LogUtils.e("refreshVideoView is null!");
            return;
        }
        double pixelPerMicrosecond = mCoverWidth * 1.0D / mVideoLimit;
        mNvsMultiThumbnailSequenceView.setPixelPerMicrosecond(pixelPerMicrosecond);
        mNvsMultiThumbnailSequenceView.setThumbnailImageFillMode(NvsMultiThumbnailSequenceView.THUMBNAIL_IMAGE_FILLMODE_ASPECTCROP);
        if (mNvsMultiThumbnailSequenceView != null) {
            ArrayList<MultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDescsArray = new ArrayList<>();
            for (ClipInfo clipInfo : mCutClips) {
                MultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new MultiThumbnailSequenceView.ThumbnailSequenceDesc();
                sequenceDescs.mediaFilePath = clipInfo.getFilePath();
                sequenceDescs.trimIn = clipInfo.getTrimIn();
                sequenceDescs.trimOut = clipInfo.getTrimOut();
                sequenceDescs.inPoint = clipInfo.getInPoint();
                sequenceDescs.outPoint = clipInfo.getOutPoint();
                sequenceDescs.stillImageHint = false;
                sequenceDescsArray.add(sequenceDescs);
            }
            mNvsMultiThumbnailSequenceView.setThumbnailSequenceDescArray(sequenceDescsArray);
        }
    }

    /**
     * Seek to position.
     * 查看位置
     *
     * @param position  the position 位置
     * @param state     the state 状态
     * @param startTime the start time 开始时间
     */
    public void seekToPosition(long position, int state, long startTime) {
        mState = state;
        LayoutParams layoutParams = (LayoutParams) mCutViewScroller.getLayoutParams();
        layoutParams.leftMargin = EditorController.getInstance().durationToLength(position - startTime, mNvsMultiThumbnailSequenceView.getPixelPerMicrosecond());
        mCutViewScroller.setLayoutParams(layoutParams);
    }

    /**
     * Seek nvs multi thumbnail sequence view.
     * 查看nvs多缩略图序列视图
     *
     * @param length the length
     */
    public void seekNvsMultiThumbnailSequenceView(final int length) {
        mNvsMultiThumbnailSequenceView.scrollBy(length, 0);
    }


    /**
     * Sets state.
     * 设置状态
     *
     * @param state the state
     */
    public void setState(int state) {
        mState = state;
    }

    public void setClipInfo(long videoLimit, List<ClipInfo> tailorClip) {
        mVideoLimit = videoLimit;
        mCutClips = tailorClip;
        refreshVideoView();
    }

    public void setOnScrollListener(MultiThumbnailSequenceView2.OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public double getPixelPerMicrosecond() {
        if (mNvsMultiThumbnailSequenceView == null) {
            LogUtils.e("getPixelPerMicrosecond: mNvsMultiThumbnailSequenceView is null!");
            return 0;
        }
        return mNvsMultiThumbnailSequenceView.getPixelPerMicrosecond();
    }
}
