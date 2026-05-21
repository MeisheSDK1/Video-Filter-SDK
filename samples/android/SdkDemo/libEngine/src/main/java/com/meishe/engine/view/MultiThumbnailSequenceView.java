//================================================================================
//
// (c) Copyright China Digital Video (Beijing) Limited, 2017. All rights reserved.
//
// This code and information is provided "as is" without warranty of any kind,
// either expressed or implied, including but not limited to the implied
// warranties of merchantability and/or fitness for a particular purpose.
//
//--------------------------------------------------------------------------------
//   Birth Date:    Aug 30. 2017
//   Author:        NewAuto video team
//================================================================================
package com.meishe.engine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsIconGenerator;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


/*! \if ENGLISH
 *   \brief Multiple thumbnail sequence
 *
 *   A multi-thumbnail sequence displays a sequence of thumbnails of multiple segments within a timeline. It supports the adjustment of the thumbnail time scale, and supports scrolling when the effective content is too long.
 *   \warning In the NvsMultiThumbnailSequenceView class, all public APIs are used in the UI thread! ! !
 *   \else
 *   \brief 多缩略图序列
 *
 *   多缩略图序列，可以显示一个时间线内多个片段的缩略图序列。支持缩略图时间比例尺的调节，当有效内容超长时支持滚动浏览。
 *   \warning NvsMultiThumbnailSequenceView类中，所有public API都在UI线程使用！！！
 *   \endif
 *   \since 1.10.0
 */
public class MultiThumbnailSequenceView extends HorizontalScrollView
        implements NvsIconGenerator.IconCallback {
    private static final String TAG = "Meicam";
    private NvsIconGenerator m_iconGenerator = null;
    private boolean m_scrollEnabled = true;

    /*! \anchor THUMBNAIL_IMAGE_FILLMODE */
    /*!
     *  \if ENGLISH
     *   @name image fill mode
     *  \else
     *   @name 图片填充模式
     *  \endif
     */
    /*! @name 图片填充模式 */
    //!@{

    public static final int THUMBNAIL_IMAGE_FILLMODE_STRETCH = 0;    //!< \if ENGLISH Image zoom to fill the full window without maintaining the original scale (default mode) \else 图片缩放来填充满窗口，不保持原比例 (默认模式) \endif
    public static final int THUMBNAIL_IMAGE_FILLMODE_ASPECTCROP = 1; //!< \if ENGLISH The image fills the full window evenly and scales if necessary \else 图片按比例均匀填充满窗口，必要时进行裁剪 \endif

    // These two flags control the cached keyframe only mode and whether it is still valid
    private static final int THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY = 1;
    private static final int THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY_VALID = 2;

    /*! \if ENGLISH
     *   \brief Interface for monitoring horizontal scrolling.
     *   \else
     *   \brief 用于监听水平滚动的接口
     *   \endif
     */
    public interface OnScrollChangeListener {
        void onScrollChanged(MultiThumbnailSequenceView view, int x, int oldx);
    }

    private OnScrollChangeListener m_scrollChangeListener;

    private boolean mIsTriming = false;

    /*! \if ENGLISH
     *   \brief Multi-thumbnail sequence information description
     *   \else
     *   \brief 多缩略图序列信息描述
     *   \endif
     *   \since 1.10.0
     */
    public static class ThumbnailSequenceDesc {
        public String mediaFilePath;        //!< \if ENGLISH Video file path \else 视频文件路径 \endif
        public long inPoint;                //!< \if ENGLISH Timeline in point (in microseconds) \else 时间线上入点(单位微秒) \endif
        public long outPoint;               //!< \if ENGLISH Timeline out point (in microseconds) \else 时间线上出点(单位微秒) \endif
        public long trimIn;                 //!< \if ENGLISH Trim in point (in microseconds) \else 裁剪入点(单位微秒) \endif
        public long trimOut;                //!< \if ENGLISH Trim out point (in microseconds) \else 裁剪出点(单位微秒) \endif
        public boolean stillImageHint;      //!< \if ENGLISH Whether it is a static picture \else 是否是静态图片 \endif
        public boolean onlyDecodeKeyFrame;  //!< \if ENGLISH Whether decode only key frames \else 是否是只解码关键帧 \endif
        public float thumbnailAspectRatio;  //!< \if ENGLISH Thumbnail's aspect ratio of this sequence, 0 means comply with the thumbnail's aspect ratio of the view \else 当前序列的缩略图横纵比，为0表示使用控件的缩略图横纵比 \endif

        public ThumbnailSequenceDesc() {
            inPoint = 0;
            outPoint = 4000000;
            trimIn = 0;
            trimOut = 4000000;
            stillImageHint = false;
            onlyDecodeKeyFrame = false;
            thumbnailAspectRatio = 0;
        }
    }

    private ArrayList<ThumbnailSequenceDesc> m_descArray;
    private float m_thumbnailAspectRatio = 9.0f / 16;
    private double m_pixelPerMicrosecond = 1080.0 / 15000000;
    private int m_startPadding = 0;
    private int m_endPadding = 0;
    private int m_thumbnailImageFillMode = THUMBNAIL_IMAGE_FILLMODE_STRETCH;
    private long m_maxTimelinePosToScroll = 0;

    private static class ThumbnailSequence {
        int m_index;
        String m_mediaFilePath;
        long m_inPoint;
        long m_outPoint;
        long m_trimIn;
        long m_trimDuration;
        boolean m_stillImageHint;
        boolean m_onlyDecodeKeyFrame;
        public float m_thumbnailAspectRatio;

        int m_flags;

        int m_x; // Relative to content view
        int m_width;
        int m_thumbnailWidth;

        public ThumbnailSequence() {
            m_index = 0;
            m_inPoint = 0;
            m_outPoint = 0;
            m_trimIn = 0;
            m_trimDuration = 0;
            m_stillImageHint = false;
            m_onlyDecodeKeyFrame = false;
            m_thumbnailAspectRatio = 0;
            m_flags = 0;
            m_x = 0;
            m_width = 0;
            m_thumbnailWidth = 0;
        }

        public long calcTimestampFromX(int x, long duratinPerThumbnail) {
            long timestamp = m_trimIn + (long) ((double) (x - m_x) / m_width * m_trimDuration + 0.5);
            long timestamp1 = (long) (((double) timestamp / duratinPerThumbnail)) * duratinPerThumbnail;
            return timestamp1;
        }

        public long calcTimestampFromX(int x) {
            long timestamp = m_trimIn + (long) (Math.floor(x - m_x) / m_width * m_trimDuration + 0.5);
            return timestamp;
        }
    }

    private ArrayList<ThumbnailSequence> m_thumbnailSequenceArray = new ArrayList<ThumbnailSequence>();
    private TreeMap<Integer, ThumbnailSequence> m_thumbnailSequenceMap = new TreeMap<Integer, ThumbnailSequence>();
    private int m_contentWidth = 0;

    private static class ThumbnailId implements Comparable<ThumbnailId> {
        public int m_seqIndex;
        public long m_timestamp;

        public ThumbnailId(int seqIndex, long timestamp) {
            m_seqIndex = seqIndex;
            m_timestamp = timestamp;
        }

        @Override
        public int compareTo(ThumbnailId o) {
            if (m_seqIndex < o.m_seqIndex) {
                return -1;
            } else if (m_seqIndex > o.m_seqIndex) {
                return 1;
            } else {
                if (m_timestamp < o.m_timestamp)
                    return -1;
                else if (m_timestamp > o.m_timestamp)
                    return 1;
                else
                    return 0;
            }
        }
    }

    private static class Thumbnail {
        ThumbnailSequence m_owner;
        long m_timestamp;
        ImageView m_imageView;
        long m_iconTaskId;
        boolean m_imageViewUpToDate;
        boolean m_touched;

        public Thumbnail() {
            m_timestamp = 0;
            m_iconTaskId = 0;
            m_imageViewUpToDate = false;
            m_touched = false;
        }
    }

    private TreeMap<ThumbnailId, Thumbnail> m_thumbnailMap = new TreeMap<ThumbnailId, Thumbnail>();
    Bitmap m_placeholderBitmap;
    private int m_maxThumbnailWidth = 0;
    private boolean m_updatingThumbnail = false;

    private class ContentView extends ViewGroup {
        public ContentView(Context context) {
            super(context);
        }

        /**
         * Any layout manager that doesn't scroll will want this.
         */
        @Override
        public boolean shouldDelayChildPressedState() {
            return false;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // NOTE: At the time our size is being measured
            // The content width may not be ready!
            int w = m_contentWidth, h;

            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST)
                h = heightSize;
            else
                h = MultiThumbnailSequenceView.this.getHeight(); // Shouldn't reach here

            // Check against our minimum height and width
            w = Math.max(w, getSuggestedMinimumWidth());
            h = Math.max(h, getSuggestedMinimumHeight());

            w = resolveSizeAndState(w, widthMeasureSpec, 0);
            h = resolveSizeAndState(h, heightMeasureSpec, 0);

            setMeasuredDimension(w, h);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            updateThumbnails();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            if (h != oldh)
                requestUpdateThumbnailSequenceGeometry();

            super.onSizeChanged(w, h, oldw, oldh);
        }
    }

    private ContentView m_contentView;

    public MultiThumbnailSequenceView(Context context) {
        super(context);
        NvsUtils.checkFunctionInMainThread();
        init(context);
    }

    public MultiThumbnailSequenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        NvsUtils.checkFunctionInMainThread();
        init(context);
    }

    public MultiThumbnailSequenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        NvsUtils.checkFunctionInMainThread();
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultiThumbnailSequenceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        NvsUtils.checkFunctionInMainThread();
        init(context);
    }

    /*! \if ENGLISH
     *   \brief Sets the thumbnail sequence description array
     *   \param descArray The thumbnail sequence describes the array. Note: Once it is set, modifying the contents of the array will not work unless thumbnail sequence description is set array again.
     *   \else
     *   \brief 设置缩略图序列描述数组
     *   \param descArray 缩略图序列描述数组。注意：一旦设置，再修改数组里面的内容是不起作用的，除非再次设置缩略图序列描述数组
     *   \endif
     *   \sa getThumbnailSequenceDescArray
     */
    public void setThumbnailSequenceDescArray(ArrayList<ThumbnailSequenceDesc> descArray) {
        NvsUtils.checkFunctionInMainThread();
        if (descArray == m_descArray)
            return;

//        clearThumbnailSequences();
        m_thumbnailSequenceArray.clear();
        m_placeholderBitmap = null;

        m_descArray = descArray;
        if (descArray != null) {
            int index = 0;
            long lastOutPoint = 0;
            for (ThumbnailSequenceDesc desc : descArray) {
                if (desc.mediaFilePath == null ||
                        desc.inPoint < lastOutPoint || desc.outPoint <= desc.inPoint ||
                        desc.trimIn < 0 || desc.trimOut <= desc.trimIn) {
                    Log.e(TAG, "Invalid ThumbnailSequenceDesc!");
                    continue;
                }

                ThumbnailSequence thumbnailSequence = new ThumbnailSequence();
                thumbnailSequence.m_index = index++;
                thumbnailSequence.m_mediaFilePath = desc.mediaFilePath;
                thumbnailSequence.m_inPoint = desc.inPoint;
                thumbnailSequence.m_outPoint = desc.outPoint;
                thumbnailSequence.m_trimIn = desc.trimIn;
                thumbnailSequence.m_trimDuration = desc.trimOut - desc.trimIn;
                thumbnailSequence.m_stillImageHint = desc.stillImageHint;
                thumbnailSequence.m_onlyDecodeKeyFrame = desc.onlyDecodeKeyFrame;
                thumbnailSequence.m_thumbnailAspectRatio = desc.thumbnailAspectRatio;

                m_thumbnailSequenceArray.add(thumbnailSequence);

                lastOutPoint = desc.outPoint;
            }
        }

        updateThumbnailSequenceGeometry();
    }

    /*! \if ENGLISH
     *   \brief Gets the thumbnail sequence description array
     *   \return Returns the obtained thumbnail sequence description array.
     *   \else
     *   \brief 获取缩略图序列描述数组
     *   \return 返回获取的缩略图序列描述数组
     *   \endif
     *   \sa setThumbnailSequenceDescArray
     */
    public ArrayList<ThumbnailSequenceDesc> getThumbnailSequenceDescArray() {
        return m_descArray;
    }


    /*! \if ENGLISH
     *   \brief Sets the image fill mode of the thumbnail
     *   \param fillMode [image fill mode] (@ref THUMBNAIL_IMAGE_FILLMODE)
     *   \else
     *   \brief 设置缩略图的图片填充模式
     *   \param fillMode [图片填充模式] (@ref THUMBNAIL_IMAGE_FILLMODE)
     *   \endif
     *   \sa getThumbnailImageFillMode
     */
    public void setThumbnailImageFillMode(int fillMode) {
        NvsUtils.checkFunctionInMainThread();
        if (m_thumbnailImageFillMode != THUMBNAIL_IMAGE_FILLMODE_ASPECTCROP &&
                m_thumbnailImageFillMode != THUMBNAIL_IMAGE_FILLMODE_STRETCH) {
            m_thumbnailImageFillMode = THUMBNAIL_IMAGE_FILLMODE_STRETCH;
        }

        if (m_thumbnailImageFillMode == fillMode)
            return;

        m_thumbnailImageFillMode = fillMode;
        updateThumbnailSequenceGeometry();
    }

    /*! \if ENGLISH
     *   \brief Gets the image fill mode of the thumbnail
     *   \return Returns the obtained image fill mode of the thumbnail
     *   \else
     *   \brief 获取缩略图的图片填充模式
     *   \return 返回获取的缩略图的图片填充模式
     *   \endif
     *   \sa setThumbnailImageFillMode
     */
    public int getThumbnailImageFillMode() {
        return m_thumbnailImageFillMode;
    }

    /*! \if ENGLISH
     *   \brief Sets thumbnail aspect ratio.
     *   \param thumbnailAspectRatio aspect ratio
     *   \else
     *   \brief 设置缩略图横纵比
     *   \param thumbnailAspectRatio 横纵比
     *   \endif
     *   \sa getThumbnailAspectRatio
     */
    public void setThumbnailAspectRatio(float thumbnailAspectRatio) {
        NvsUtils.checkFunctionInMainThread();
        if (thumbnailAspectRatio < 0.1f)
            thumbnailAspectRatio = 0.1f;
        else if (thumbnailAspectRatio > 10)
            thumbnailAspectRatio = 10;

        if (Math.abs(m_thumbnailAspectRatio - thumbnailAspectRatio) < 0.001f)
            return;

        m_thumbnailAspectRatio = thumbnailAspectRatio;
        updateThumbnailSequenceGeometry();
    }

    /*! \if ENGLISH
     *   \brief Gets thumbnail aspect ratio.
     *   \return Returns the thumbnail aspect ratio.
     *   \else
     *   \brief 获取缩略图横纵比
     *   \return 返回缩略图横纵比值
     *   \endif
     *   \sa setThumbnailAspectRatio
     */
    public float getThumbnailAspectRatio() {
        return m_thumbnailAspectRatio;
    }

    /*! \if ENGLISH
     *   \brief Sets the scale.
     *   \param pixelPerMicrosecond The number of pixels per subtle
     *   \else
     *   \brief 设置比例尺
     *   \param pixelPerMicrosecond 每微妙所占用的像素数
     *   \endif
     *   \sa getPixelPerMicrosecond
     */
    public void setPixelPerMicrosecond(double pixelPerMicrosecond) {
        NvsUtils.checkFunctionInMainThread();
        if (pixelPerMicrosecond <= 0 || pixelPerMicrosecond == m_pixelPerMicrosecond)
            return;

        m_pixelPerMicrosecond = pixelPerMicrosecond;
        updateThumbnailSequenceGeometry();
    }

    /*! \if ENGLISH
     *   \brief Gets the current scale.
     *   \return Returns the number of pixels per subtle.
     *   \else
     *   \brief 获取当前比例尺
     *   \return 返回每微妙所占用的像素数
     *   \endif
     *   \sa setPixelPerMicrosecond
     */
    public double getPixelPerMicrosecond() {
        return m_pixelPerMicrosecond;
    }

    /*! \if ENGLISH
     *   \brief Sets the starting padding.
     *   \param startPadding Starting padding(in pixels)
     *   \else
     *   \brief 设置起始边距
     *   \param startPadding 起始边距（单位是像素）
     *   \endif
     *   \sa getStartPadding
     */
    public void setStartPadding(int startPadding) {
        NvsUtils.checkFunctionInMainThread();
        if (startPadding < 0 || startPadding == m_startPadding)
            return;

        m_startPadding = startPadding;
        updateThumbnailSequenceGeometry();
    }

    /*! \if ENGLISH
     *   \brief Gets the current starting padding.
     *   \return Returns the starting padding(in pixels).
     *   \else
     *   \brief 获取当前起始边距
     *   \return 返回起始边距（单位是像素）
     *   \endif
     *   \sa setStartPadding
     */
    public int getStartPadding() {
        return m_startPadding;
    }

    /*! \if ENGLISH
     *   \brief Sets end padding.
     *   \param endPadding Ends padding(in pixels)
     *   \else
     *   \brief 设置结束边距。
     *   \param endPadding 结束边距（单位为像素）
     *   \endif
     *   \sa getEndPadding
     */
    public void setEndPadding(int endPadding) {
        NvsUtils.checkFunctionInMainThread();
        if (endPadding < 0 || endPadding == m_endPadding)
            return;

        m_endPadding = endPadding;
        updateThumbnailSequenceGeometry();
    }

    /*! \if ENGLISH
     *   \brief Gets the current ending padding.
     *   \return Returns the ending padding(in pixels)
     *   \else
     *   \brief 获取当前结束边距。
     *   \return 返回结束边距，单位为像素
     *   \endif
     *   \sa setEndPadding
     */
    public int getEndPadding() {
        return m_endPadding;
    }

    /*! \if ENGLISH
     *   \brief Sets the maximum timeline position that allows scrolling.
     *   \param maxTimelinePosToScroll The maximum timeline position that is allowed to scroll(in microseconds).
     *   \else
     *   \brief 设置允许滚动的最大时间线位置
     *   \param maxTimelinePosToScroll 允许滚动的最大时间线位置，单位为微秒
     *   \endif
     *   \sa getMaxTimelinePosToScroll
     *   \since 1.17.0
     */
    public void setMaxTimelinePosToScroll(int maxTimelinePosToScroll) {
        NvsUtils.checkFunctionInMainThread();
        maxTimelinePosToScroll = Math.max(maxTimelinePosToScroll, 0);
        if (maxTimelinePosToScroll == m_maxTimelinePosToScroll)
            return;

        m_maxTimelinePosToScroll = maxTimelinePosToScroll;

        updateThumbnailSequenceGeometry();
    }

    /*! \if ENGLISH
     *   \brief Gets the maximum timeline position that allows scrolling.
     *   \return Returns the maximum timeline position that is allowed to scroll(in microseconds).
     *   \else
     *   \brief 获取允许滚动的最大时间线位置
     *   \return 返回允许滚动的最大时间线位置，单位为微秒
     *   \endif
     *   \sa setMaxTimelinePosToScroll
     *   \since 1.17.0
     */
    public long getMaxTimelinePosToScroll() {
        return m_maxTimelinePosToScroll;
    }

    /*! \if ENGLISH
     *   \brief Maps the X coordinate of the control to the timeline position.
     *   \param x The X coordinate of the control(in pixels)
     *   \return Returns the timeline position of the map(in microseconds).
     *   \else
     *   \brief 将控件的X坐标映射到时间线位置
     *   \param x 控件的X坐标（单位为像素）
     *   \return 返回映射的时间线位置（单位为微秒）
     *   \endif
     *   \sa mapXFromTimelinePos
     */
    public long mapTimelinePosFromX(int x) {
        NvsUtils.checkFunctionInMainThread();
        final int scrollX = getScrollX();
        x = x + scrollX - m_startPadding;
        final long timelinePos = (long) Math.floor(x / m_pixelPerMicrosecond + 0.5);
        return timelinePos;
    }

    /*! \if ENGLISH
     *   \brief Maps the timeline position to the X coordinate of the control.
     *   \param timelinePos Timeline position(in microseconds)
     *   \return Returns the X coordinate of the mapped control(in pixels).
     *   \else
     *   \brief 将时间线位置映射到控件的X坐标
     *   \param timelinePos 时间线位置（单位为微秒）
     *   \return 返回映射的控件的X坐标（单位为像素）
     *   \endif
     *   \sa mapTimelinePosFromX
     */
    public int mapXFromTimelinePos(long timelinePos) {
        NvsUtils.checkFunctionInMainThread();
        int x = (int) Math.floor(timelinePos * m_pixelPerMicrosecond + 0.5);
        final int scrollX = getScrollX();
        return x + m_startPadding - scrollX;
    }

    /*! \if ENGLISH
     *   \brief Zooms the current scale.
     *   \param scaleFactor Scale ratio
     *   \param anchorX Scaled anchor X coordinate(in pixels).
     *   \else
     *   \brief 缩放当前比例尺
     *   \param scaleFactor 缩放的比例
     *   \param anchorX 缩放的锚点X坐标（单位为像素）
     *   \endif
     */
    public void scaleWithAnchor(double scaleFactor, int anchorX) {
        NvsUtils.checkFunctionInMainThread();
        if (scaleFactor <= 0)
            return;

        final long anchorTimelinePos = mapTimelinePosFromX(anchorX);
        m_pixelPerMicrosecond *= scaleFactor;

        updateThumbnailSequenceGeometry();
        final int newAnchorX = mapXFromTimelinePos(anchorTimelinePos);
        final int scrollX = getScrollX() + newAnchorX - anchorX;
        // According to android developer document, this version of scrollTo()
        // also clamps the scrolling to the bounds of our child.
        scrollTo(scrollX, 0);
    }

    /*! \if ENGLISH
     *   \brief Sets the scroll listener interface.
     *   \param listener Rolling monitor interface
     *   \else
     *   \brief 设置滚动监听接口
     *   \param listener 滚动监听接口
     *   \endif
     *   \sa getOnScrollChangeListenser
     */
    public void setOnScrollChangeListenser(OnScrollChangeListener listener) {
        NvsUtils.checkFunctionInMainThread();
        m_scrollChangeListener = listener;
    }

    /*! \if ENGLISH
     *   \brief Gets the current scrolling listener interface.
     *   \return Returns the current scrolling listener interface.
     *   \else
     *   \brief 获取当前滚动监听接口
     *   \return 返回当前滚动监听接口
     *   \endif
     *   \sa setOnScrollChangeListenser
     */
    public OnScrollChangeListener getOnScrollChangeListenser() {
        NvsUtils.checkFunctionInMainThread();
        return m_scrollChangeListener;
    }

    /*! \if ENGLISH
     *   \brief Sets whether to start scroll preview.
     *   \param enable Whether to start scroll preview.
     *   \else
     *   \brief 设置是否开启滚动预览
     *   \param enable 是否开启滚动预览
     *   \endif
     *   \sa getScrollEnabled
     *   \since 1.11.0
     */
    public void setScrollEnabled(boolean enable) {
        m_scrollEnabled = enable;
    }

    /*! \if ENGLISH
     *   \brief Gets whether scroll preview has started.
     *   \return Returns whether scroll preview has started.
     *   \else
     *   \brief 获取当前是否开启了滚动预览
     *   \return 返回当前是否开启了滚动预览
     *   \endif
     *   \sa setScrollEnabled
     *   \since 1.11.0
     */
    public boolean getScrollEnabled() {
        return m_scrollEnabled;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!isInEditMode()) {
            m_iconGenerator = new NvsIconGenerator();
            m_iconGenerator.setIconCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelIconTask();

        m_scrollChangeListener = null;

        if (m_iconGenerator != null) {
            m_iconGenerator.release();
            m_iconGenerator = null;
        }

        super.onDetachedFromWindow();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (m_scrollChangeListener != null)
            m_scrollChangeListener.onScrollChanged(this, l, oldl);
        updateThumbnails();
    }

    /*! \cond */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (m_scrollEnabled)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (m_scrollEnabled)
            return super.onTouchEvent(ev);
        else
            return false;
    }
    /*! \endcond */

    private void init(Context context) {
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

        // Create the internal content view
        m_contentView = new ContentView(context);
        addView(m_contentView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    private void requestUpdateThumbnailSequenceGeometry() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                updateThumbnailSequenceGeometry();
            }
        });
    }

    private void updateThumbnailSequenceGeometry() {
        cancelIconTask();

        // Clear thumbnails since their geometry is subject to change
        clearThumbnails();

        // Calculate thumbnail width in pixel
        final int h = getHeight();
        if (h == 0)
            return;

        m_thumbnailSequenceMap.clear();

        int lastX = m_startPadding;
        m_maxThumbnailWidth = 0;
        for (ThumbnailSequence thumbnailSequence : m_thumbnailSequenceArray) {
            // Mark cached keyframe only mode as invalid
            thumbnailSequence.m_flags &= ~THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY_VALID;

            final int x = (int) Math.floor(thumbnailSequence.m_inPoint * m_pixelPerMicrosecond + 0.5) + m_startPadding;
            final int x2 = (int) Math.floor(thumbnailSequence.m_outPoint * m_pixelPerMicrosecond + 0.5) + m_startPadding;
            if (x2 <= x) {
                // For current scale ratio, this thumbnail sequence can't be represented, just ignore it
                continue;
            }

            thumbnailSequence.m_x = x;
            thumbnailSequence.m_width = x2 - x;

            // Calculate thumbnail width in pixel
            final float thumbnailAspectRatio = thumbnailSequence.m_thumbnailAspectRatio > 0 ?
                    thumbnailSequence.m_thumbnailAspectRatio : m_thumbnailAspectRatio;
            thumbnailSequence.m_thumbnailWidth = (int) Math.floor(h * thumbnailAspectRatio + 0.5);
            thumbnailSequence.m_thumbnailWidth = Math.max(thumbnailSequence.m_thumbnailWidth, 1);
            m_maxThumbnailWidth = Math.max(thumbnailSequence.m_thumbnailWidth, m_maxThumbnailWidth);

            m_thumbnailSequenceMap.put(x, thumbnailSequence);

            lastX = x2;
        }

        // Update desired content (view) width
        int contentWidth = lastX;
        if (m_maxTimelinePosToScroll <= 0) {
            contentWidth += m_endPadding;
        } else {
            int len = (int) Math.floor(m_startPadding + m_maxTimelinePosToScroll * m_pixelPerMicrosecond + 0.5f);
            if (len < contentWidth)
                contentWidth = len;
        }
        m_contentWidth = contentWidth;

//        m_contentView.layout(0, 0, m_contentWidth, getHeight());
        m_contentView.requestLayout(); // updateThumbnails() will be called during layout

        if (getWidth() + getScrollX() > m_contentWidth) {
            final int newScrollX = Math.max(getScrollX() - (getWidth() + getScrollX() - m_contentWidth), 0);
            if (newScrollX != getScrollX())
                scrollTo(newScrollX, 0);
        }
    }

    private static class ClipImageView extends ImageView {
        private int m_clipWidth;

        ClipImageView(Context ctx, int clipWidth) {
            super(ctx);
            m_clipWidth = clipWidth;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.clipRect(new Rect(0, 0, m_clipWidth, getHeight()));
            super.onDraw(canvas);
        }
    }

    private void updateThumbnails() {
        if (m_iconGenerator == null)
            return;

        if (m_thumbnailSequenceMap.isEmpty()) {
            clearThumbnails();
            return;
        }

        final int guardLength = m_maxThumbnailWidth;
        final int scrollX = getScrollX();
        final int width = getWidth();
        final int visibleLeftBound = Math.max(scrollX - guardLength, m_startPadding);
        final int visibleRightBound = visibleLeftBound + width + guardLength;
        if (visibleRightBound <= visibleLeftBound) {
            clearThumbnails();
            return;
        }

        Integer startKey = m_thumbnailSequenceMap.floorKey(visibleLeftBound);
        if (startKey == null)
            startKey = m_thumbnailSequenceMap.firstKey();

        SortedMap<Integer, ThumbnailSequence> sortedMap = m_thumbnailSequenceMap.tailMap(startKey);
        for (Map.Entry<Integer, ThumbnailSequence> entry : sortedMap.entrySet()) {
            ThumbnailSequence seq = entry.getValue();
            if (seq.m_x + seq.m_width < visibleLeftBound)
                continue;
            if (seq.m_x >= visibleRightBound)
                break;

            int thumbnailX;
            if (seq.m_x < visibleLeftBound)
                thumbnailX = seq.m_x + (visibleLeftBound - seq.m_x) / seq.m_thumbnailWidth * seq.m_thumbnailWidth;
            else
                thumbnailX = seq.m_x;

            boolean outOfBound = false;
            final int seqEndX = seq.m_x + seq.m_width;
//            final long timeStep = Math.max((long) ((double) seq.m_thumbnailWidth / seq.m_width * seq.m_trimDuration), 1);
//            long seqPointsPerMicrosecond =seq.m_width/seq.m_trimDuration;
            double seqPointsPerMicrosecond = divide(seq.m_width, seq.m_trimDuration, 10);
            int thumbnailItemTime = (int) (seq.m_thumbnailWidth / seqPointsPerMicrosecond);
            long firstImageWidth = 0;
            if (mIsTriming) {
                firstImageWidth = (long) ((thumbnailItemTime - seq.m_trimIn % thumbnailItemTime) * seqPointsPerMicrosecond);
            }
            while (thumbnailX < seqEndX) {
                if (thumbnailX >= visibleRightBound) {
                    outOfBound = true;
                    break;
                }

                int thumbnailWidth = seq.m_thumbnailWidth;

                // Calculate timestamp of this thumbnail
                long timestamp = seq.calcTimestampFromX(thumbnailX);
                if (firstImageWidth > 0) {
                    timestamp = seq.calcTimestampFromX((int) (thumbnailX - (thumbnailWidth - firstImageWidth)));
                    if (timestamp < 0) {
                        timestamp = 0;
                    }
                    thumbnailWidth = (int) firstImageWidth;
                } else if (thumbnailX + thumbnailWidth > seqEndX) {
                    thumbnailWidth = seqEndX - thumbnailX;
                }

                if (thumbnailItemTime > (seq.m_outPoint - seq.m_inPoint)) {
                    thumbnailWidth = (int) ((seq.m_outPoint - seq.m_inPoint) * seqPointsPerMicrosecond);
                }
                // Find the thumbnail from the current thumbnail map first
                ThumbnailId tid = new ThumbnailId(seq.m_index, timestamp);
                Thumbnail thumbnail = m_thumbnailMap.get(tid);
                if (thumbnail == null) {
                    // Create a new thumbnail
                    thumbnail = new Thumbnail();
                    thumbnail.m_owner = seq;
                    thumbnail.m_timestamp = timestamp;
                    thumbnail.m_imageViewUpToDate = false;
                    thumbnail.m_touched = true;

                    m_thumbnailMap.put(tid, thumbnail);

                    if (thumbnailWidth == seq.m_thumbnailWidth)
                        thumbnail.m_imageView = new ImageView(this.getContext());
                    else
                        thumbnail.m_imageView = new ClipImageView(this.getContext(), thumbnailWidth);

                    if (m_thumbnailImageFillMode == THUMBNAIL_IMAGE_FILLMODE_STRETCH)
                        thumbnail.m_imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    else if (m_thumbnailImageFillMode == THUMBNAIL_IMAGE_FILLMODE_ASPECTCROP)
                        thumbnail.m_imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    m_contentView.addView(thumbnail.m_imageView);
                    thumbnail.m_imageView.layout(thumbnailX, 0, thumbnailX + seq.m_thumbnailWidth, m_contentView.getHeight());
                } else {
                    thumbnail.m_touched = true;
                }

                if (firstImageWidth > 0) {
                    firstImageWidth = -1;
                }

                thumbnailX += thumbnailWidth;
            }

            if (outOfBound)
                break;
        }

        //
        // Remove untouched thumbnail objects and collect icons from cache
        //
        m_updatingThumbnail = true;

        boolean hasDirtyThumbnail = false;
        TreeMap<ThumbnailId, Bitmap> iconMap = new TreeMap<ThumbnailId, Bitmap>();
        Set<Map.Entry<ThumbnailId, Thumbnail>> thumbnailSet = m_thumbnailMap.entrySet();
        Iterator<Map.Entry<ThumbnailId, Thumbnail>> itrThumbnail = thumbnailSet.iterator();
        while (itrThumbnail.hasNext()) {
            Map.Entry<ThumbnailId, Thumbnail> entry = itrThumbnail.next();
            Thumbnail thumbnail = entry.getValue();

            // Update placeholder bitmap
            if (thumbnail.m_imageView != null) {
                Drawable drawable = thumbnail.m_imageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    if (bitmap != null)
                        m_placeholderBitmap = bitmap;
                }
            }

            if (!thumbnail.m_touched) {
                // These thumbnail hasn't been touched, remove it
                if (thumbnail.m_iconTaskId != 0)
                    m_iconGenerator.cancelTask(thumbnail.m_iconTaskId);

                m_contentView.removeView(thumbnail.m_imageView);
                itrThumbnail.remove();
                continue;
            }

            // Reset touched flag for later use
            thumbnail.m_touched = false;

            if (thumbnail.m_imageViewUpToDate) {
                Bitmap bitmap = ((BitmapDrawable) thumbnail.m_imageView.getDrawable()).getBitmap();
                iconMap.put(entry.getKey(), bitmap);
            } else {
                final long realTimestamp = thumbnail.m_owner.m_stillImageHint ? 0 : thumbnail.m_timestamp;
                updateKeyframeOnlyModeForThumbnailSequence(thumbnail.m_owner);
                final int flags = (thumbnail.m_owner.m_flags & THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY) != 0 ? 1 : 0;
                Bitmap bitmap = m_iconGenerator.getIconFromCache(thumbnail.m_owner.m_mediaFilePath, realTimestamp, flags);
                if (bitmap != null) {
                    iconMap.put(entry.getKey(), bitmap);
                    if (setBitmapToThumbnail(bitmap, thumbnail)) {
                        thumbnail.m_imageViewUpToDate = true;
                        thumbnail.m_iconTaskId = 0;
                    }
                } else {
                    hasDirtyThumbnail = true;
                    thumbnail.m_iconTaskId = m_iconGenerator.getIcon(thumbnail.m_owner.m_mediaFilePath, realTimestamp, flags);
                }
            }
        }

        m_updatingThumbnail = false;

        if (!hasDirtyThumbnail)
            return;

        if (iconMap.isEmpty()) {
            // Now we set placeholder image to thumbnail whose ImageView was not up to date yet
            if (m_placeholderBitmap != null) {
                for (Map.Entry<ThumbnailId, Thumbnail> entry : m_thumbnailMap.entrySet()) {
                    Thumbnail thumbnail = entry.getValue();
                    if (!thumbnail.m_imageViewUpToDate)
                        setBitmapToThumbnail(m_placeholderBitmap, thumbnail);
                }
            }

            return;
        }

        // Now we set image to thumbnail whose ImageView was not up to date yet
        for (Map.Entry<ThumbnailId, Thumbnail> entry : m_thumbnailMap.entrySet()) {
            Thumbnail thumbnail = entry.getValue();
            if (thumbnail.m_imageViewUpToDate)
                continue;

            // We fail to find an image with the given timestamp value,
            // To make thumbnail sequence looks better we use an image whose
            // timestamp is close to the given timestamp
            Map.Entry<ThumbnailId, Bitmap> ceilingEntry = iconMap.ceilingEntry(entry.getKey());

            if (ceilingEntry != null)
                setBitmapToThumbnail(ceilingEntry.getValue(), thumbnail);
            else
                setBitmapToThumbnail(iconMap.lastEntry().getValue(), thumbnail);
        }
    }

    public double divide(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void updateKeyframeOnlyModeForThumbnailSequence(ThumbnailSequence seq) {
        if ((seq.m_flags & THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY_VALID) != 0)
            return;

        if (seq.m_onlyDecodeKeyFrame) {
            // We always respect the user's option
            seq.m_flags |= THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY |
                    THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY_VALID;
            return;
        }

        final long timeStep = Math.max((long) (seq.m_thumbnailWidth / m_pixelPerMicrosecond + 0.5), 1);
        final boolean keyFrameOnly = shouldDecodecKeyFrameOnly(seq.m_mediaFilePath, timeStep);
        if (keyFrameOnly)
            seq.m_flags |= THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY;
        else
            seq.m_flags &= ~THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY;
        seq.m_flags |= THUMBNAIL_SEQUENCE_FLAGS_CACHED_KEYFRAME_ONLY_VALID;
    }

    private boolean shouldDecodecKeyFrameOnly(String filePath, long timeStep) {
        NvsStreamingContext streamingContext = NvsStreamingContext.getInstance();
        if (streamingContext == null)
            return false;

        NvsAVFileInfo fileInfo = streamingContext.getAVFileInfo(filePath);
        if (fileInfo == null)
            return false;

        if (fileInfo.getVideoStreamCount() < 1)
            return false;

        NvsRational fps = fileInfo.getVideoStreamFrameRate(0);
        if (fps == null)
            return false;

        if (fps.den <= 0 || fps.num <= 0)
            return false;

        long videoDuration = fileInfo.getVideoStreamDuration(0);
        if (videoDuration < timeStep)
            return false;

        int keyframeInterval = streamingContext.detectVideoFileKeyframeInterval(filePath);
        if (keyframeInterval == 0)
            keyframeInterval = 30; // We can't detect its GOP size, just guess it
        else if (keyframeInterval == 1)
            return false; // Keyframe only, no need to use keyframe only mode

        int keyframeIntervalTime = (int) (keyframeInterval * ((double) fps.den / fps.num) * 1000000);
        if (keyframeInterval <= 30) {
            if (timeStep > keyframeIntervalTime * 0.9)
                return true;
        } else if (keyframeInterval <= 60) {
            if (timeStep > keyframeIntervalTime * 0.8)
                return true;
        } else if (keyframeInterval <= 100) {
            if (timeStep > keyframeIntervalTime * 0.7)
                return true;
        } else if (keyframeInterval <= 150) {
            if (timeStep > keyframeIntervalTime * 0.5)
                return true;
        } else if (keyframeInterval <= 250) {
            if (timeStep > keyframeIntervalTime * 0.3)
                return true;
        } else {
            if (timeStep > keyframeIntervalTime * 0.2)
                return true;
        }

        return false;
    }

    public boolean isIsTriming() {
        return mIsTriming;
    }

    public void setIsTriming(boolean mIsTriming) {
        this.mIsTriming = mIsTriming;
    }

    private boolean setBitmapToThumbnail(Bitmap bitmap, Thumbnail thumbnail) {
        if (bitmap == null || thumbnail.m_imageView == null)
            return false;

        thumbnail.m_imageView.setImageBitmap(bitmap);
        return true;
    }

    private void clearThumbnailSequences() {
        cancelIconTask();
        clearThumbnails();

        m_thumbnailSequenceArray.clear();
        m_thumbnailSequenceMap.clear();
        m_contentWidth = 0;
    }

    private void clearThumbnails() {
        for (Map.Entry<ThumbnailId, Thumbnail> entry : m_thumbnailMap.entrySet())
            m_contentView.removeView(entry.getValue().m_imageView);

        m_thumbnailMap.clear();
    }

    private void cancelIconTask() {
        if (m_iconGenerator != null)
            m_iconGenerator.cancelTask(0);
    }


    @Override
    public void onIconReady(final Bitmap icon, long timestamp, final long taskId) {
        post(new Runnable() {
            @Override
            public void run() {
                // updateThumbnails();
                updateOne(icon, taskId);
            }
        });
    }

    private void updateOne(Bitmap bitmap, long taskId) {
        for (Map.Entry<ThumbnailId, Thumbnail> entry : m_thumbnailMap.entrySet()) {
            Thumbnail thumbnail = entry.getValue();
            if (thumbnail.m_iconTaskId == taskId) {
                // LogUtils.d("updateOne,find,taskId="+taskId+",timestamp="+thumbnail.m_timestamp);
                setBitmapToThumbnail(bitmap, thumbnail);
                break;
            }

        }
    }
}