package com.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.meishe.base.utils.ImageLoader;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.music.CutMusicView;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.meishe.utils.DrawableUitls;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/16 13:20
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class TrimMusicView extends ConstraintLayout {
    private ImageView mMusicIcon;
    private ImageView mMusicPlayState;
    private TextView mMusicName;
    private TextView mMusicTime;
    private TextView mMusicImport;
    private CutMusicView mMusicCutView;
    private MusicInfo mMusicInfo;
    private OnTrimMusicListener mOnTrimMusicListener;
    private final ImageLoader.Options mRoundCornerOptions;

    public TrimMusicView(Context context) {
        this(context, null);
    }

    public TrimMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrimMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        mRoundCornerOptions = new ImageLoader.Options().centerCrop().roundedCorners(15);
        initListener();
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View inflate = layoutInflater.inflate(R.layout.view_trim_music, this);
        mMusicIcon = inflate.findViewById(R.id.trim_music_image);
        mMusicPlayState = inflate.findViewById(R.id.trim_music_play_state);
        mMusicImport = inflate.findViewById(R.id.trim_music_import);
        Drawable bgDrawable = DrawableUitls.getRadiusDrawable(getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_150)
                , getResources().getColor(R.color.ms_blue));
        mMusicImport.setBackground(bgDrawable);
        mMusicName = inflate.findViewById(R.id.trim_music_name);
        mMusicTime = inflate.findViewById(R.id.trim_music_time);
        mMusicCutView = inflate.findViewById(R.id.trim_music_cut_view);
    }

    public void initMusicInfo(MusicInfo musicInfo) {
        if (null == musicInfo) {
            return;
        }
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
        mMusicInfo = musicInfo;
        String imagePath = musicInfo.getImagePath();
        if (!TextUtils.isEmpty(imagePath)) {
            ImageLoader.loadUrl(getContext(), Uri.parse(imagePath), mMusicIcon, mRoundCornerOptions);
        }
        mMusicName.setText(musicInfo.getTitle());
        updateTimeView(0, mMusicInfo.getTrimOut());
        mMusicPlayState.setBackgroundResource(R.mipmap.icon_edit_pause);
        mMusicCutView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMusicCutView.setRightHandleVisiable(true);
                mMusicCutView.setMinDuration(Constants.MUSIC_MIN_DURATION);
                mMusicCutView.setCutLayoutWidth(mMusicCutView.getWidth());
                mMusicCutView.setCanTouchCenterMove(false);
                mMusicCutView.setMaxDuration(musicInfo.getDuration());
                mMusicCutView.setInPoint(0);
                mMusicCutView.setOutPoint(musicInfo.getDuration());
                mMusicCutView.reLayout();
            }
        }, 100);
    }

    private void initListener() {
        mMusicIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnTrimMusicListener) {
                    mOnTrimMusicListener.onTrimMusicPlayState(mMusicInfo);
                }
            }
        });
        mMusicImport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnTrimMusicListener) {
                    mOnTrimMusicListener.onTrimMusicUse();
                }
            }
        });
        mMusicCutView.setOnSeekBarChangedListener(new CutMusicView.OnSeekBarChanged() {
            @Override
            public void onLeftValueChange(long var) {
                if (mMusicInfo != null) {
                    updateTimeView(var, mMusicInfo.getTrimOut());
                }
            }

            @Override
            public void onRightValueChange(long var) {
                if (mMusicInfo != null) {
                    updateTimeView(mMusicInfo.getTrimIn(), var);
                }
            }

            @Override
            public void onCenterTouched(long left, long right) {

            }

            @Override
            public void onUpTouched(boolean touch_left, long left, long right) {
                if (mMusicInfo != null) {
                    mMusicInfo.setTrimIn(left);
                    mMusicInfo.setTrimOut(right);
                }
                if (touch_left) {
                    if (mMusicInfo != null && (null != mOnTrimMusicListener)) {
                        mOnTrimMusicListener.seekPosition(left);
                    }
                    mMusicCutView.setIndicator(left);
                }
            }
        });
    }

    public void updateProgress(int curPos) {
        if (null == mMusicCutView
                || null == mMusicTime
                || null == mMusicInfo) {
            return;
        }
        mMusicCutView.setIndicator(curPos);
        updateTimeView(curPos, mMusicInfo.getTrimOut());
    }

    public void setOnTrimMusicListener(OnTrimMusicListener mOnTrimMusicListener) {
        this.mOnTrimMusicListener = mOnTrimMusicListener;
    }

    public interface OnTrimMusicListener {
        void seekPosition(long value);

        void onTrimMusicPlayState(MusicInfo musicInfo);

        void onTrimMusicUse();
    }

    public MusicInfo getMusicInfo() {
        return mMusicInfo;
    }

    private void updateTimeView(long current, long total) {
        mMusicTime.setText(String.format("%s/%s", TimeFormatUtil.formatUsToString2(current), TimeFormatUtil.formatUsToString2(total)));
    }

    public void updatePlayView(boolean isPlaying) {
        if (null == mMusicPlayState) {
            return;
        }
        mMusicPlayState.setBackgroundResource(isPlaying ? R.mipmap.icon_edit_play : R.mipmap.icon_edit_pause);
    }
}
