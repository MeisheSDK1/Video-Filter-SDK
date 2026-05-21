package com.meishe.cutsame.pop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.base.utils.CommonUtils;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.AttachPopupView;
import com.meishe.third.pop.enums.PopupPosition;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/3/10 10:49
 * @Description :视频编辑依附弹窗 Video editing attache view
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class VideoEditPop extends AttachPopupView {
    private TextView mTvReplace;
    private TextView mTvCut;
    private EventListener mListener;

    public static VideoEditPop create(View attachView, Context context) {
        //int offsetX = (int) ((context.getResources().getDimension(R.dimen.dp_px_360) - attachView.getWidth())/ 2);
        return (VideoEditPop) new XPopup
                .Builder(context)
                // .offsetX(-offsetX)
                .popupPosition(PopupPosition.Top)
                .atView(attachView)
                .hasShadowBg(false)
                .asCustom(new VideoEditPop(context));
    }

    public VideoEditPop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_video_edit;
    }

    @Override
    protected int getPopupHeight() {
        return (int) getResources().getDimension(R.dimen.dp_px_150);
    }

    @Override
    protected int getMaxHeight() {
        return getPopupHeight();
    }

    @Override
    protected Drawable getPopupBackground() {
        return CommonUtils.getRadiusDrawable(15, getResources().getColor(R.color.color_fcffffff));
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mTvReplace = findViewById(R.id.tv_replace);
        mTvCut = findViewById(R.id.tv_cut);
        initListener();
    }

    private void initListener() {
        mTvReplace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onReplace();
                }
                dismiss();
            }
        });
        mTvCut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCut();
                }
                dismiss();
            }
        });
    }

    /**
     * 设置事件监听
     * Set listener
     *
     * @param listener the listener
     **/
    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    public interface EventListener {
        void onReplace();

        void onCut();
    }
}
