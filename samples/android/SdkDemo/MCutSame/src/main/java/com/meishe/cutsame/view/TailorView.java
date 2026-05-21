package com.meishe.cutsame.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.TailorClip;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.view.MultiThumbnailSequenceView;

import java.util.ArrayList;

/**
 * Created by CaoZhiChao on 2020/11/4 10:25
 * 调整视图的类
 * Adjust the view's classes
 */
public class TailorView extends RelativeLayout {
    private static final float COVER_WIDTH = 0.7f;
    private static final float COVER_MARGIN = 0.15f;
    public static final int FROM_VIDEO = 0;
    public static final int FROM_USER = 1;
    private MultiThumbnailSequenceView2 mNvsMultiThumbnailSequenceView;
    private View mTailorViewCover;
    private View mTailorViewScroller;
    private TailorClip mTailorClip;
    private int mCoverWidth;
    private int mCoverMargin;
    private int mState = -1;
    private MultiThumbnailSequenceView2.OnScrollListener mOnScrollListener;

    public TailorView(Context context) {
        super(context);
        init(context);
    }

    public TailorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TailorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCoverWidth = (int) (ScreenUtils.getScreenWidth() * COVER_WIDTH);
        mCoverMargin = (int) (ScreenUtils.getScreenWidth() * COVER_MARGIN);
        LayoutInflater inflater = LayoutInflater.from(context);
        View parentView = inflater.inflate(R.layout.tailor_view, this);
        mNvsMultiThumbnailSequenceView = parentView.findViewById(R.id.tailor_view_sequence);
        mTailorViewCover = parentView.findViewById(R.id.tailor_view_cover);
        mTailorViewScroller = parentView.findViewById(R.id.tailor_view_scroller);
        LayoutParams layoutParams = (LayoutParams) mTailorViewCover.getLayoutParams();
        layoutParams.leftMargin = mCoverMargin;
        layoutParams.rightMargin = mCoverMargin;
        mTailorViewCover.setLayoutParams(layoutParams);
        Drawable drawable = CommonUtils.getRadiusDrawable(getResources().getDimensionPixelOffset(R.dimen.dp_px_3),
                getResources().getColor(R.color.white), getResources().getDimensionPixelOffset(R.dimen.dp_px_6), getResources().getColor(R.color.transparent));
        mTailorViewCover.setBackground(drawable);
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
        if (mTailorClip == null) {
            LogUtils.e("refreshVideoView is null!");
            return;
        }
        double pixelPerMicrosecond = mCoverWidth * 1.0D / mTailorClip.getLimitLength();
        mNvsMultiThumbnailSequenceView.setPixelPerMicrosecond(pixelPerMicrosecond);
        mNvsMultiThumbnailSequenceView.setThumbnailImageFillMode(NvsMultiThumbnailSequenceView.THUMBNAIL_IMAGE_FILLMODE_ASPECTCROP);
        if (mNvsMultiThumbnailSequenceView != null) {
            ArrayList<MultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDescsArray = new ArrayList<>();
            MultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new MultiThumbnailSequenceView.ThumbnailSequenceDesc();
            sequenceDescs.mediaFilePath = mTailorClip.getFilePath();
            sequenceDescs.trimIn = mTailorClip.getTrimIn();
            sequenceDescs.trimOut = mTailorClip.getTrimOut();
            sequenceDescs.inPoint = 0;
            sequenceDescs.stillImageHint = false;
            sequenceDescs.onlyDecodeKeyFrame = true;
            sequenceDescs.outPoint = ((mTailorClip.getTrimOut() - mTailorClip.getTrimIn()));
            sequenceDescsArray.add(sequenceDescs);
            mNvsMultiThumbnailSequenceView.setThumbnailSequenceDescArray(sequenceDescsArray);
        }
    }

    /**
     * Seek to position.
     * 查看位置
     * @param position  the position 位置
     * @param state     the state 状态
     * @param startTime the start time 开始时间
     */
    public void seekToPosition(long position, int state, long startTime) {
        mState = state;
        LayoutParams layoutParams = (LayoutParams) mTailorViewScroller.getLayoutParams();
        layoutParams.leftMargin = EditorController.getInstance().durationToLength(position - startTime, mNvsMultiThumbnailSequenceView.getPixelPerMicrosecond());
        mTailorViewScroller.setLayoutParams(layoutParams);
    }

    /**
     * Seek nvs multi thumbnail sequence view.
     * 查看nvs多缩略图序列视图
     * @param length the length
     */
    public void seekNvsMultiThumbnailSequenceView(final int length) {
        mNvsMultiThumbnailSequenceView.scrollBy(length, 0);
    }


    /**
     * Sets state.
     *  设置状态
     * @param state the state
     */
    public void setState(int state) {
        mState = state;
    }

    public void setTailorClip(TailorClip tailorClip) {
        mTailorClip = tailorClip;
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
