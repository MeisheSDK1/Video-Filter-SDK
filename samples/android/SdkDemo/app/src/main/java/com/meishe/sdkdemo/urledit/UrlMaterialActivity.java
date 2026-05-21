package com.meishe.sdkdemo.urledit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.adapter.CommonFragmentAdapter;
import com.meishe.base.model.BaseActivity;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.base.utils.ThreadUtils;
import com.meishe.base.utils.ToastUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capturescene.httputils.NetWorkUtil;
import com.meishe.sdkdemo.dialog.LoadingPop;
import com.meishe.sdkdemo.edit.VideoEditActivity;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.music.AudioPlayer;
import com.meishe.sdkdemo.edit.music.CustomViewPager;
import com.meishe.sdkdemo.selectmedia.view.CustomPopWindow;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;
import com.meishe.sdkdemo.urledit.fragment.UrlExternalFragment;
import com.meishe.sdkdemo.urledit.fragment.UrlMaterialFragment;
import com.meishe.sdkdemo.urledit.inter.OnUrlChangeListener;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.view.TrimMusicView;
import com.meishe.third.tablayout.SlidingTabLayout;
import com.meishe.third.tablayout.listener.OnTabSelectListener;
import com.meishe.utils.DrawableUitls;
import com.meishe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.meishe.sdkdemo.utils.Constants.NS_TIME_BASE;
import static com.meishe.sdkdemo.utils.Constants.POINT16V9;
import static com.meishe.sdkdemo.utils.Constants.POINT18V9;
import static com.meishe.sdkdemo.utils.Constants.POINT1V1;
import static com.meishe.sdkdemo.utils.Constants.POINT21V9;
import static com.meishe.sdkdemo.utils.Constants.POINT3V4;
import static com.meishe.sdkdemo.utils.Constants.POINT4V3;
import static com.meishe.sdkdemo.utils.Constants.POINT6V7;
import static com.meishe.sdkdemo.utils.Constants.POINT7V6;
import static com.meishe.sdkdemo.utils.Constants.POINT9V16;
import static com.meishe.sdkdemo.utils.Constants.POINT9V18;
import static com.meishe.sdkdemo.utils.Constants.POINT9V21;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/2 13:21
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class UrlMaterialActivity extends BaseActivity {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTabTitleList = new ArrayList<>();
    private final List<UrlMaterialInfo> mUrlDatas = new ArrayList<>();
    private ImageView mUrlClose;
    private Button mUrlEdit;
    private SlidingTabLayout mUrlTab;
    private CustomViewPager mUrlPager;
    private TrimMusicView mTrimMusicView;
    private UrlExternalFragment mUrlExternalFragment;
    private UrlMaterialFragment mUrlMaterialFragment;
    private CustomPopWindow mSizePop;
    private LoadingPop mLoadingPop;
    private boolean isGetInfo = false;
    private boolean mIsForResult;
    private int mUrlType = Constants.URL_MATERIAL_VIDEO;
    private boolean mIsMulti = true;
    private UrlMaterialInfo mMusicUrlMaterialInfo;

    public static void nativeUrlMaterial(Activity activity, int requestCode, boolean isForResult, int urlType, boolean isMulti) {
        Bundle urlBundle = new Bundle();
        urlBundle.putBoolean(Constants.URL_MATERIAL_KEY_FOR_RESULT, isForResult);
        urlBundle.putInt(Constants.URL_MATERIAL_TYPE, urlType);
        urlBundle.putBoolean(Constants.URL_MATERIAL_MULTI, isMulti);
        AppManager.getInstance().jumpActivityForResult(activity,
                UrlMaterialActivity.class,
                urlBundle,
                requestCode);
    }


    @Override
    protected int bindLayout() {
        return R.layout.activity_url_material;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mIsForResult = bundle.getBoolean(Constants.URL_MATERIAL_KEY_FOR_RESULT, false);
                mUrlType = bundle.getInt(Constants.URL_MATERIAL_TYPE);
                mIsMulti = bundle.getBoolean(Constants.URL_MATERIAL_MULTI, true);
            }
        }
        mUrlExternalFragment = UrlExternalFragment.create(mUrlType);
        mUrlExternalFragment.setSelectMulti(mIsMulti);
        mUrlMaterialFragment = UrlMaterialFragment.create(mUrlType);
        mUrlMaterialFragment.setSelectMulti(mIsMulti);
        mFragmentList.add(mUrlExternalFragment);
        mFragmentList.add(mUrlMaterialFragment);
        String[] fragmentItems = getResources().getStringArray(R.array.main_url_edit);
        mTabTitleList.addAll(Arrays.asList(fragmentItems));
    }

    @Override
    protected void initView() {
        mUrlClose = findViewById(R.id.url_close);
        mUrlTab = findViewById(R.id.url_tab_layout);
        mUrlPager = findViewById(R.id.url_pager);
        mUrlPager.setScanScroll(false);
        mTrimMusicView = findViewById(R.id.select_url_music_trim);
        mUrlEdit = findViewById(R.id.url_edit);
        Drawable bgDrawable = DrawableUitls.getRadiusDrawable(getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_150)
                , getResources().getColor(R.color.ms_blue));
        mUrlEdit.setBackground(bgDrawable);
        initListener();
        mUrlPager.setOffscreenPageLimit(4);
        mUrlPager.setAdapter(new CommonFragmentAdapter(getSupportFragmentManager(), mFragmentList, mTabTitleList));
        mUrlTab.setViewPager(mUrlPager);
        initPop();
    }

    private void initPop() {
        mSizePop = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(R.layout.pop_select_makesize)
                .setViewClickListener(new CustomPopWindow.OnViewClickListener() {
                    @Override
                    public void onViewClick(CustomPopWindow popWindow, View view) {
                        int id = view.getId();
                        if (id == R.id.button16v9) {
                            selectCreateRatio(POINT16V9);
                        } else if (id == R.id.button1v1) {
                            selectCreateRatio(POINT1V1);
                        } else if (id == R.id.button9v16) {
                            selectCreateRatio(POINT9V16);
                        } else if (id == R.id.button3v4) {
                            selectCreateRatio(POINT3V4);
                        } else if (id == R.id.button4v3) {
                            selectCreateRatio(POINT4V3);
                        } else if (id == R.id.button21v9) {
                            selectCreateRatio(POINT21V9);
                        } else if (id == R.id.button9v21) {
                            selectCreateRatio(POINT9V21);
                        } else if (id == R.id.button18v9) {
                            selectCreateRatio(POINT18V9);
                        } else if (id == R.id.button9v18) {
                            selectCreateRatio(POINT9V18);
                        } else if (id == R.id.button7v6) {
                            selectCreateRatio(POINT7V6);
                        } else if (id == R.id.button6v7) {
                            selectCreateRatio(POINT6V7);
                        }

                    }
                }).create();

        mLoadingPop = LoadingPop.create(this);
    }

    private void initListener() {
        mUrlTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                stopMusicPlay();
                hideTrimMusicView();
                KeyboardUtils.hideSoftInput(UrlMaterialActivity.this);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        mUrlClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mUrlEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUrlEdit();
            }
        });
        if (null != mUrlExternalFragment && null != mUrlMaterialFragment) {
            mUrlExternalFragment.setOnUrlChangeListener(new OnUrlChangeListener() {
                @Override
                public void onUrlChange() {
                    mUrlMaterialFragment.resetDataBySelected();
                    updateSelectedSize();
                }

                @Override
                public void onEditVisibility(boolean isShow) {
                    mUrlEdit.setVisibility(isShow ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onUrlInfo(UrlMaterialInfo info) {
                    goTrimMusic(info);
                }

            });
            mUrlMaterialFragment.setOnUrlChangeListener(new OnUrlChangeListener() {
                @Override
                public void onUrlChange() {
                    mUrlExternalFragment.resetDataBySelected();
                    updateSelectedSize();
                }

                @Override
                public void onEditVisibility(boolean isShow) {

                }

                @Override
                public void onUrlInfo(UrlMaterialInfo info) {
                    goTrimMusic(info);
                }
            });
        }
        mTrimMusicView.setOnTrimMusicListener(new TrimMusicView.OnTrimMusicListener() {
            @Override
            public void seekPosition(long value) {
                AudioPlayer.getInstance(UrlMaterialActivity.this).seekPosition(value);
            }

            @Override
            public void onTrimMusicPlayState(MusicInfo musicInfo) {
                boolean isPlaying = AudioPlayer.getInstance(UrlMaterialActivity.this).isPlaying();
                mTrimMusicView.updatePlayView(isPlaying);
                if (isPlaying) {
                    AudioPlayer.getInstance(UrlMaterialActivity.this).pause();
                    return;
                }
                AudioPlayer.getInstance(UrlMaterialActivity.this).play();
            }

            @Override
            public void onTrimMusicUse() {
                goUrlEdit();
                stopMusicPlay();
            }
        });
        if (mUrlType == Constants.URL_MATERIAL_MUSIC) {
            AudioPlayer.getInstance(UrlMaterialActivity.this).setPlayListener(new AudioPlayer.OnPlayListener() {
                @Override
                public void onMusicPlay() {

                }

                @Override
                public void onMusicStop() {
                }

                @Override
                public void onGetCurrentPos(int curPos) {
                    if (null == mTrimMusicView) {
                        return;
                    }
                    mTrimMusicView.updateProgress(curPos);
                }
            });
        }
    }

    private void goUrlEdit() {
        if (mUrlType == Constants.URL_MATERIAL_MUSIC) {
            if (mMusicUrlMaterialInfo == null || TextUtils.isEmpty(mMusicUrlMaterialInfo.getUrl())) {
                ToastUtil.showToast(this, R.string.url_empty_tips);
                return;
            }
            MusicInfo musicInfo = mTrimMusicView.getMusicInfo();
            if (null == musicInfo || TextUtils.isEmpty(musicInfo.getFilePath())) {
                ToastUtil.showToast(this, R.string.url_empty_tips);
                return;
            }
            mMusicUrlMaterialInfo.setTrimIn(musicInfo.getTrimIn());
            mMusicUrlMaterialInfo.setTrimOut(musicInfo.getTrimOut());
            mUrlDatas.clear();
            mUrlDatas.add(mMusicUrlMaterialInfo);
        } else {
            List<UrlMaterialInfo> urlExternalData = mUrlExternalFragment.getUrlData();
            List<UrlMaterialInfo> urlMaterialData = mUrlMaterialFragment.getUrlData();
            if (urlExternalData.isEmpty() && urlMaterialData.isEmpty()) {
                ToastUtils.showShort(R.string.url_empty_tips);
                return;
            }
            mUrlDatas.clear();
            mUrlDatas.addAll(urlExternalData);
            mUrlDatas.addAll(urlMaterialData);
        }
        if (mIsForResult) {
            goBack();
            return;
        }
        mSizePop.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    private void selectCreateRatio(int makeRatio) {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            ToastUtil.showToast(this, R.string.network_not_available);
            return;
        }
        ArrayList<ClipInfo> pathList = getClipInfoList();
        mLoadingPop.show();
        isGetInfo = true;
        ThreadUtils.getSinglePool().execute(new Runnable() {
            @Override
            public void run() {
                //这里先调用getAvInfo
                //TODO 需要判断码率提示
                for (ClipInfo clipInfo : pathList) {
                    String filePath = clipInfo.getFilePath();
                    NvsAVFileInfo avFileInfo = NvsStreamingContext.getAVInfoFromFile(filePath, 0);
                    if (null == avFileInfo) {
                        isGetInfo = false;
                        break;
                    }
                    Log.e("meicam", ">>>>>>path:" + filePath + "    rate:" + avFileInfo.getDataRate());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingPop.dismiss();
                        if (!isGetInfo) {
                            String[] versionName = getResources().getStringArray(R.array.clip_load_failed_tips);
                            ToastUtils.showShort(versionName[1]);
                            return;
                        }
                        NvsVideoResolution videoEditResolution = Util.getVideoEditResolution(makeRatio);
                        if (videoEditResolution.imageWidth * videoEditResolution.imageHeight > 2160 * 3840) {
                            ToastUtil.showToast(UrlMaterialActivity.this, getResources().getString(R.string.timeline_size_to_large));
                            return;
                        }
                        TimelineData.instance().setVideoResolution(videoEditResolution);
                        TimelineData.instance().setClipInfoData(pathList);
                        TimelineData.instance().setMakeRatio(makeRatio);
                        AppManager.getInstance().jumpActivity(UrlMaterialActivity.this, VideoEditActivity.class, null);
                    }
                });

            }
        });
    }

    private void goBack() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            ToastUtil.showToast(this, R.string.network_not_available);
            return;
        }
        ArrayList<ClipInfo> pathList = getClipInfoList();
        mLoadingPop.show();
        isGetInfo = true;
        ThreadUtils.getSinglePool().execute(new Runnable() {
            @Override
            public void run() {
                //这里先调用getAvInfo
                MusicInfo musicInfo = new MusicInfo();
                for (ClipInfo clipInfo : pathList) {
                    String filePath = clipInfo.getFilePath();
                    NvsAVFileInfo avFileInfo = NvsStreamingContext.getAVInfoFromFile(filePath, 0);
                    if (mUrlType == Constants.URL_MATERIAL_MUSIC) {
                        if (null == avFileInfo) {
                            isGetInfo = false;
                            break;
                        }
                        musicInfo.setFilePath(filePath);
                        musicInfo.setDuration(avFileInfo.getDuration());
                        musicInfo.setInPoint(0);
                        musicInfo.setOutPoint(avFileInfo.getDuration());
                        musicInfo.setTrimIn(clipInfo.getTrimIn());
                        musicInfo.setTrimOut(clipInfo.getTrimOut());
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingPop.dismiss();
                        if (mUrlType == Constants.URL_MATERIAL_MUSIC) {
                            if (!isGetInfo) {
                                String[] versionName = getResources().getStringArray(R.array.clip_load_failed_tips);
                                ToastUtils.showShort(versionName[1]);
                                return;
                            }
                            Intent intent = new Intent();
                            intent.putExtra("select_music", musicInfo);
                            setResult(RESULT_OK, intent);
                            finish();
                            return;
                        }
                        BackupData.instance().setAddClipInfoList(pathList);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

            }
        });
    }

    private ArrayList<ClipInfo> getClipInfoList() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (UrlMaterialInfo mediaData : mUrlDatas) {
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setFilePath(mediaData.getUrl());
            clipInfo.setCoverPath(mediaData.getCoverUrl());
            clipInfo.changeTrimIn(mediaData.getTrimIn());
            long trimOut = mediaData.getTrimOut();
            if (trimOut > 0) {
                clipInfo.changeTrimOut(trimOut);
            }
            pathList.add(clipInfo);
        }
        return pathList;
    }

    @SuppressLint("StringFormatMatches")
    private void updateSelectedSize() {
        if (null == mUrlExternalFragment || null == mUrlMaterialFragment) {
            return;
        }
        int externalSize = mUrlExternalFragment.getUrlData().size();
        int materialSize = mUrlMaterialFragment.getUrlData().size();
        int totalSize = externalSize + materialSize;
        if (mUrlType == Constants.URL_MATERIAL_VIDEO) {
            mUrlEdit.setVisibility(totalSize > 0 ? View.VISIBLE : View.GONE);
            mUrlEdit.setText(String.format(getResources().getString(R.string.url_edit), totalSize));
        }
    }

    private void goTrimMusic(UrlMaterialInfo urlMaterialInfo) {
        mMusicUrlMaterialInfo = urlMaterialInfo;
        if (mUrlType != Constants.URL_MATERIAL_MUSIC) {
            return;
        }
        if (null == urlMaterialInfo) {
            stopMusicPlay();
            hideTrimMusicView();
            return;
        }
        String path = urlMaterialInfo.getUrl();
        if (TextUtils.isEmpty(path)) {
            stopMusicPlay();
            hideTrimMusicView();
            return;
        }
        MusicInfo mCurrentMusicInfo = AudioPlayer.getInstance(this).getCurrentMusic();
        if (AudioPlayer.getInstance(this).isPlaying()
                && (null != mCurrentMusicInfo)
                && TextUtils.equals(path, mCurrentMusicInfo.getFilePath())) {
            stopMusicPlay();
            hideTrimMusicView();
            return;
        }
        long duration = urlMaterialInfo.getDuration();
        NvsAVFileInfo avFileInfo = NvsStreamingContext.getAVInfoFromFile(path, 0);
        if (null == avFileInfo || avFileInfo.getDuration() <= 0) {
            return;
        }
        if (duration <= 0) {
            duration = avFileInfo.getDuration() / NS_TIME_BASE;
        }
        mTrimMusicView.setVisibility(View.VISIBLE);
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setFilePath(urlMaterialInfo.getUrl());
        musicInfo.setImagePath(urlMaterialInfo.getCoverUrl());
        musicInfo.setTitle(urlMaterialInfo.getDisplayName());
        musicInfo.setDuration(duration * TimelineUtil.TIME_BASE);
        musicInfo.setTrimIn(0);
        musicInfo.setTrimOut(musicInfo.getDuration());
        mTrimMusicView.initMusicInfo(musicInfo);
        startMusicPlay(musicInfo);
    }

    public void hideTrimMusicView() {
        if (null == mTrimMusicView) {
            return;
        }
        if (mTrimMusicView.getVisibility() == View.VISIBLE) {
            mTrimMusicView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUrlType == Constants.URL_MATERIAL_MUSIC) {
            AudioPlayer.getInstance(this).pause();
            mTrimMusicView.updatePlayView(false);
        }
    }

    public void startMusicPlay(MusicInfo musicInfo) {
        if (null == musicInfo || mUrlType != Constants.URL_MATERIAL_MUSIC) {
            return;
        }
        AudioPlayer.getInstance(this).setCurrentMusic(musicInfo, true);
        AudioPlayer.getInstance(this).startPlay();
    }

    public void stopMusicPlay() {
        if (mUrlType != Constants.URL_MATERIAL_MUSIC) {
            return;
        }
        AudioPlayer.getInstance(UrlMaterialActivity.this).stopPlay();
    }

    public void destroyMusicPlayer() {
        if (mUrlType != Constants.URL_MATERIAL_MUSIC) {
            return;
        }
        AudioPlayer.getInstance(UrlMaterialActivity.this).destroyPlayer();
    }

    @Override
    protected void onDestroy() {
        destroyMusicPlayer();
        super.onDestroy();
    }
}
