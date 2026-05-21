package com.meishe.sdkdemo.mimodemo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.CompileVideoFragment;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.mimodemo.base.BaseEditActivity;
import com.meishe.sdkdemo.mimodemo.bean.MiMoLocalData;
import com.meishe.sdkdemo.mimodemo.common.Constants;
import com.meishe.sdkdemo.mimodemo.common.dataInfo.CompoundCaptionInfo;
import com.meishe.sdkdemo.mimodemo.common.dataInfo.TimelineData;
import com.meishe.sdkdemo.mimodemo.common.template.model.ShotInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.ShotVideoInfo;
import com.meishe.sdkdemo.mimodemo.common.template.utils.MiMoFileUtils;
import com.meishe.sdkdemo.mimodemo.common.template.utils.NvMiMoContext;
import com.meishe.sdkdemo.mimodemo.common.utils.AppManager;
import com.meishe.sdkdemo.mimodemo.common.utils.MimoTimelineUtil;
import com.meishe.sdkdemo.mimodemo.mediapaker.adapter.BottomMenuViewHolder;
import com.meishe.sdkdemo.mimodemo.mediapaker.adapter.IAdapterLifeCircle;
import com.meishe.sdkdemo.mimodemo.mediapaker.view.SelectBottomMenu;

import java.util.ArrayList;
import java.util.List;

import static com.meishe.sdkdemo.mimodemo.mediapaker.SelectMediaActivity.REQUEST_CODE_EDIT_CLIP;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : ccg
 * @CreateDate : 2020/6/12.
 * @Description :mimo编辑页。MiMo Edit activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MimoEditActivity extends BaseEditActivity {
    private static final String TAG = "MimoEditActivity";
    private static final int REQUEST_CODE_CAPTION_EDIT = 101;
    private static final int REQUEST_CODE_VIDEO_TRIM = 102;
    private final String FragmentTag = "CompileVideoFragment";
    private SelectBottomMenu mBottomMenu;
    private RelativeLayout mLayoutWaterFilter;
    private CustomTitleBar mTitle;
    private TextView mSwitchHint;
    private Switch mSwitch;
    private CompileVideoFragment mCompileVideoFragment;
    private ArrayList<CompoundCaptionInfo> mCaptionDataListClone;
    private NvsTimelineCompoundCaption mCurCaption;
    private NvsTimelineCompoundCaption mAddComCaption;
    private boolean mIsForResult = false;
    //跳转到trim编辑视频的页面选择的position
    //Jump to the index of trim editing video page selection
    private int selectedToTrimPosition = -1;
    //避免重复点击编辑，创建多个编辑页面
    //Avoid repeated clicks to edit, create multiple edit pages
    private long lastClickTime;
    private String endingFilter;
    private long endingFilterLen;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        MiMoLocalData mimoData = NvMiMoContext.getInstance().getSelectedMimoData();
        if (mimoData != null) {
            endingFilter = mimoData.getEndingWatermark();
            endingFilterLen = MiMoFileUtils.millisecondToMicrosecond(mimoData.getEndingFilterLen());
            if (!TextUtils.isEmpty(endingFilter) && (null != mLayoutWaterFilter)) {
                mLayoutWaterFilter.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MimoTimelineUtil.rebuildTimelineByTemplate(mTimeline);
        if (mMimoVideoFragment != null) {
            mMimoVideoFragment.initData();
            mBottomMenu.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMimoVideoFragment.playVideo(0, mTimeline.getDuration());
                }
            }, 100);
        }
    }

    @Override
    protected int initRootView() {
        return R.layout.mimo_activity_edit;
    }

    @Override
    protected void initViews() {
        mLayoutWaterFilter = findViewById(R.id.layout_water_filter);
        mBottomMenu = (SelectBottomMenu) findViewById(R.id.bottom_menu);
        mTitle = (CustomTitleBar) findViewById(R.id.title);
        mSwitchHint = (TextView) findViewById(R.id.tv_switch_water_hint);
        mSwitch = (Switch) findViewById(R.id.switch_water_filter);
        mSwitch.setChecked(true);
        mBottomMenu.setIsEditable(false);
        mBottomMenu.addAdapterLifeCircle(new IAdapterLifeCircle() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
                View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mimo_select_menu_item_big_layout, viewGroup, false);
                return new BottomMenuViewHolder(rootView);
            }
        });
        initCompileVideoFragment();
    }

    @Override
    protected void initTitle() {
        mTitle.setTextRight(getResources().getString(R.string.generate));
        mTitle.setTextRightVisible(View.VISIBLE);
        mTitle.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                showFragment();
                mCompileVideoFragment.setTimeline(mTimeline);
                mCompileVideoFragment.compileVideo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTION_EDIT) {
            mCaptionDataListClone = TimelineData.instance().getCompoundCaptionArray();
            MimoTimelineUtil.setCompoundCaption(mTimeline, mCaptionDataListClone);
            long curSeekPos = TimelineData.instance().getCurSeekTimelinePos();
            seekTimeline(curSeekPos);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    selectCaptionAndTimeSpan();
                    //重新绘制，解决不能点击的问题
                    //Redraw to solve the problem of not clicking
                    mMimoVideoFragment.setDrawRectVisible(true);
                }
            });
        } else if (requestCode == REQUEST_CODE_EDIT_CLIP) {
            if (data == null) {
                return;
            }
        } else if (REQUEST_CODE_VIDEO_TRIM == requestCode) {
            mBottomMenu.upDataForPosition(selectedToTrimPosition);
        }
    }

    @Override
    protected void initEditData() {

    }

    @Override
    protected void initListener() {
        mBottomMenu.setOnItemClickListener(new SelectBottomMenu.OnItemClickListener() {
            @Override
            public void OnItemClicked(int index) {
                Long currentTime = System.currentTimeMillis();
                long duration = currentTime - lastClickTime;
                lastClickTime = currentTime;
                if (duration <= 200) {
                    return;
                }
                MiMoLocalData templateInfo = NvMiMoContext.getInstance().getSelectedMimoData();
                if (templateInfo == null) {
                    return;
                }
                List<ShotVideoInfo> shotVideoInfos = templateInfo.getTotalShotVideoInfos();
                if (shotVideoInfos.isEmpty() && shotVideoInfos.size() <= index) {
                    return;
                }
                ShotVideoInfo shotVideoInfo = shotVideoInfos.get(index);
                String videoClipPath = shotVideoInfo.getVideoClipPath();
                NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(videoClipPath);
                if (avFileInfo != null && avFileInfo.getAVFileType() == NvsAVFileInfo.AV_FILE_TYPE_IMAGE) {
                    return;
                }
                selectedToTrimPosition = index;
                mMimoVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.IntentKey.INTENT_KEY_SHOT_ID, index);
                bundle.putBoolean(TrimEditActivity.INTENT_KEY_FROM_WHAT, true);
                AppManager.getInstance().jumpActivityForResult(MimoEditActivity.this, TrimEditActivity.class, bundle, REQUEST_CODE_VIDEO_TRIM);

            }
        });
        setOnPlayProgressChangeListener();
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mMimoVideoFragment.stopEngine();
                String hint = isChecked ? MimoEditActivity.this.getResources().getString(R.string.hint_open_last_water) :
                        MimoEditActivity.this.getResources().getString(R.string.hint_close_last_water);
                mSwitchHint.setText(hint);
                // 开启关闭结尾水印
                //Turn on and off the end watermark
                if (mTimeline != null) {
                    if (!isChecked) {
                        NvsTimelineAnimatedSticker sticker = mTimeline.getFirstAnimatedSticker();
                        while (sticker != null) {
                            if (TextUtils.equals(endingFilter, sticker.getAnimatedStickerPackageId())) {
                                sticker = mTimeline.removeAnimatedSticker(sticker);
                            } else {
                                sticker = mTimeline.getNextAnimatedSticker(sticker);
                            }
                        }
                    } else {
                        mTimeline.addAnimatedSticker(mTimeline.getDuration() - endingFilterLen, mTimeline.getDuration(), endingFilter);
                    }
                }
            }
        });
    }


    private void setOnPlayProgressChangeListener() {
        mMimoVideoFragment.setOnPlayProgressChangeListener(new MimoVideoFragment.OnPlayProgressChangeListener() {
            @Override
            public void onPlayProgressChanged(long curTime) {
                updatePlayState(curTime / Constants.US_TIME_BASE);
            }

            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                if (mMimoVideoFragment.isDrawRectVisible() && isPlaying) {
                    mMimoVideoFragment.setDrawRectVisible(false);
                }
            }
        });
    }

    private void seekTimeline(long timeStamp) {
        mMimoVideoFragment.seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }

    private void updatePlayState(long curTime) {
        mBottomMenu.updatePosition(curTime);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected NvsTimeline initTimeLine() {
        NvsVideoResolution videoResolution = TimelineData.instance().getVideoResolution();
        if (videoResolution == null) {
            return null;
        }
        NvsTimeline timeline = MimoTimelineUtil.newTimeline(videoResolution);
        if (timeline == null) {
            return null;
        }
        getCurCaption(timeline);
        return timeline;
    }

    @Override
    protected long getVideoDuration() {
        return 0L;
    }

    private void initCompileVideoFragment() {
        mCompileVideoFragment = new CompileVideoFragment();
        mCompileVideoFragment.setTimeline(mTimeline);
        mCompileVideoFragment.setCompileVideoListener(new CompileVideoFragment.OnCompileVideoListener() {
            @Override
            public void compileProgress(NvsTimeline timeline, int progress) {
            }

            @Override
            public void compileFinished(NvsTimeline timeline) {
                hideFragment();
            }

            @Override
            public void compileFailed(NvsTimeline timeline) {
                hideFragment();
            }

            @Override
            public void compileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                hideFragment();
            }

            @Override
            public void compileVideoCancel() {
                hideFragment();
            }
        });
        getFragmentManager().beginTransaction().add(R.id.compilePage, mCompileVideoFragment, FragmentTag).commit();
        hideFragment();
    }

    private void showFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FragmentTag);
        if (fragment == null) {
            fragmentManager.beginTransaction().add(R.id.compilePage, mCompileVideoFragment, FragmentTag).commit();
        } else {
            fragmentManager.beginTransaction().show(fragment).commit();
        }
    }

    private void hideFragment() {
        getFragmentManager().beginTransaction().hide(mCompileVideoFragment).commit();
    }

    private void selectCaptionAndTimeSpan() {
        selectCaption();
        updateComCaptionBoundingRect();
    }

    private void selectCaption() {
        if (mTimeline == null) {
            return;
        }
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineCompoundCaption> captionList = mTimeline.getCompoundCaptionsByTimelinePosition(curPos);
        int captionCount = captionList.size();
        if (captionCount > 0) {
            float zVal = captionList.get(0).getZValue();
            int index = 0;
            for (int i = 0; i < captionCount; i++) {
                float tmpZVal = captionList.get(i).getZValue();
                if (tmpZVal > zVal) {
                    zVal = tmpZVal;
                    index = i;
                }
            }
            mCurCaption = captionList.get(index);
        } else {
            mCurCaption = null;
        }
    }

    private void updateComCaptionBoundingRect() {
        mMimoVideoFragment.setCurCompoundCaption(mCurCaption);
        mMimoVideoFragment.updateCompoundCaptionCoordinate(mCurCaption);
        if (mAddComCaption == null) {
            mMimoVideoFragment.setDrawRectVisible(View.GONE);
        } else {
            mMimoVideoFragment.changeCompoundCaptionRectVisible();
        }
    }

    private void getCurCaption(NvsTimeline timeline) {
        mAddComCaption = timeline.getFirstCompoundCaption();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if (context == null) {
            return;
        }

        context.clearCachedResources(false);

        clearSelectData();
    }

    private void clearSelectData() {
        MiMoLocalData template = NvMiMoContext.getInstance().getSelectedMimoData();
        if (template == null) {
            return;
        }
        List<ShotInfo> shotInfos = template.getShotInfos();
        for (int index = 0; index < shotInfos.size(); index++) {
            ShotInfo shotInfo = shotInfos.get(index);
            if (!shotInfo.isCanPlaced()) {
                continue;
            }
            shotInfo.setSource(null);
            shotInfo.setTrimIn(0);
        }
        template.setShotInfos(shotInfos);
    }

}
