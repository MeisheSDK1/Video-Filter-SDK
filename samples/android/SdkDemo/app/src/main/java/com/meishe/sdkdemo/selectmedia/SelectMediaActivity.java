package com.meishe.sdkdemo.selectmedia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.base.BaseFragmentPagerAdapter;
import com.meishe.sdkdemo.edit.VideoEditActivity;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.LivePhotoInfo;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.picinpic.PictureInPictureActivity;
import com.meishe.sdkdemo.quicksplicing.QuickSplicingActivity;
import com.meishe.sdkdemo.selectmedia.adapter.AgendaSimpleSectionAdapter;
import com.meishe.sdkdemo.selectmedia.bean.MediaData;
import com.meishe.sdkdemo.selectmedia.fragment.MediaFragment;
import com.meishe.sdkdemo.selectmedia.interfaces.OnTotalNumChangeForActivity;
import com.meishe.sdkdemo.selectmedia.view.CustomPopWindow;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MediaConstant;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import static com.meishe.sdkdemo.utils.MediaConstant.KEY_CLICK_TYPE;
import static com.meishe.sdkdemo.utils.MediaConstant.KEY_FROM_TYPE;
import static com.meishe.sdkdemo.utils.MediaConstant.LIMIT_COUNT_MAX;

public class SelectMediaActivity extends BaseActivity implements OnTotalNumChangeForActivity, CustomPopWindow.OnViewClickListener {
    private String TAG = getClass().getName();
    private CustomTitleBar mTitleBar;
    private TabLayout tlSelectMedia;
    private ViewPager vpSelectMedia;
    private List<Fragment> fragmentLists = new ArrayList<>();
    private List<String> fragmentTabTitles = new ArrayList<>();
    private BaseFragmentPagerAdapter fragmentPagerAdapter;
    private int visitMethod = Constants.FROMMAINACTIVITYTOVISIT;
    private List<MediaData> mMediaDataList = new ArrayList<>();
    private Integer[] fragmentTotalNumber = {0, 0, 0};
    private int nowFragmentPosition = 0;
    private TextView meidaTVOfStart;
    private int mLimiteMediaCount = -1;
    private int mMediaType;

    @Override
    protected int initRootView() {
        return R.layout.activity_select_media;
    }

    @Override
    protected void initViews() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                visitMethod = bundle.getInt("visitMethod", Constants.FROMMAINACTIVITYTOVISIT);
                mLimiteMediaCount = bundle.getInt("limitMediaCount", -1);
                mMediaType = bundle.getInt(MediaConstant.MEDIA_TYPE, MediaConstant.ALL_MEDIA);
            }
        }

        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        tlSelectMedia = (TabLayout) findViewById(R.id.tl_select_media);
        vpSelectMedia = (ViewPager) findViewById(R.id.vp_select_media);
        meidaTVOfStart = (TextView) findViewById(R.id.media_tv_startEdit);
        meidaTVOfStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visitMethod == Constants.FROMMAINACTIVITYTOVISIT | visitMethod == Constants.FROM_QUICK_SPLICING_ACTIVITY) {
                    new CustomPopWindow.PopupWindowBuilder(SelectMediaActivity.this)
                            .setView(R.layout.pop_select_makesize)
//                            .enableBackgroundDark(true)
//                            .setBgDarkAlpha(alphaOnPop)
                            .setViewClickListener(SelectMediaActivity.this)
                            .create()
                            .showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
                } else if (visitMethod == Constants.FROMCLIPEDITACTIVITYTOVISIT) {
                    ArrayList<ClipInfo> clipInfos = getClipInfoList();
                    BackupData.instance().setAddClipInfoList(clipInfos);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    AppManager.getInstance().finishActivity();
                } else if (visitMethod == Constants.FROMPICINPICACTIVITYTOVISIT) {
                    ArrayList<String> picInpicArray = getPicInPicVideoList();
                    if (picInpicArray.size() <= 1) {
                        String[] selectVideoTips = getResources().getStringArray(R.array.select_video_tips);
                        Util.showDialog(SelectMediaActivity.this, selectVideoTips[0], selectVideoTips[1]);
                        return;
                    }
                    BackupData.instance().setPicInPicVideoArray(picInpicArray);
                    AppManager.getInstance().jumpActivity(SelectMediaActivity.this, PictureInPictureActivity.class, null);
                    AppManager.getInstance().finishActivity();
                } else if (visitMethod == Constants.FROM_QUICK_SPLICING_ONLY_ONE_ACTIVITY) {
                    ArrayList<String> picInpicArray = getPicInPicVideoList();
//                    if (picInpicArray.size() <= 1) {
//                        String[] selectVideoTips = getResources().getStringArray(R.array.select_video_tips);
//                        Util.showDialog(SelectMediaActivity.this, selectVideoTips[0], selectVideoTips[1]);
//                        return;
//                    }
                    BackupData.instance().setQuickSplicingVideoArray(picInpicArray);

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    AppManager.getInstance().finishActivity();
                }
            }
        });

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.selectMedia);
    }

    public void setTitleText(int count) {
        if (count > 0) {
            String txt = getResources().getString(R.string.setSelectMedia);
            @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String txtWidthCount = String.format(txt, count);
            mTitleBar.setTextCenter(txtWidthCount);
        } else {
            mTitleBar.setTextCenter(R.string.selectMedia);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initData() {

        if (visitMethod == Constants.FROMPICINPICACTIVITYTOVISIT) {
            meidaTVOfStart.setVisibility(View.VISIBLE);
            meidaTVOfStart.setText(R.string.select_two_video);
            meidaTVOfStart.setTextColor(Color.parseColor("#ffa3a3a3"));
        }
        fragmentLists.clear();
        fragmentTabTitles.clear();
        if (mMediaType == MediaConstant.ALL_MEDIA) {
            String[] tabList = getResources().getStringArray(R.array.select_media);
            checkDataCountAndTypeCount(tabList, MediaConstant.MEDIATYPECOUNT);
            for (int i = 0; i < tabList.length; i++) {
                MediaFragment mediaFragment = new MediaFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.MEDIATYPECOUNT[i]);
                bundle.putInt(LIMIT_COUNT_MAX, mLimiteMediaCount);
                bundle.putInt(KEY_CLICK_TYPE, MediaConstant.TYPE_ITEMCLICK_MULTIPLE);
                bundle.putInt(KEY_FROM_TYPE, visitMethod);
                mediaFragment.setArguments(bundle);
                fragmentLists.add(mediaFragment);
            }

            for (int i = 0; i < tabList.length; i++) {
                fragmentTabTitles.add(tabList[i]);
            }
        } else if (mMediaType == MediaConstant.VIDEO | mMediaType == MediaConstant.IMAGE) {
            fragmentLists.clear();
            MediaFragment mediaFragment = new MediaFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(MediaConstant.MEDIA_TYPE, mMediaType);
            bundle.putInt(LIMIT_COUNT_MAX, mLimiteMediaCount);
            bundle.putInt(KEY_CLICK_TYPE, MediaConstant.TYPE_ITEMCLICK_MULTIPLE);
            bundle.putInt(KEY_FROM_TYPE, visitMethod);
            mediaFragment.setArguments(bundle);
            fragmentLists.add(mediaFragment);

            if (mMediaType == MediaConstant.VIDEO) {
                fragmentTabTitles.add("视频");
            } else {
                fragmentTabTitles.add("图片");
            }
            tlSelectMedia.setVisibility(View.GONE);
        }

        /*
         * 禁止预加载
         * Disable preload
         * */
        vpSelectMedia.setOffscreenPageLimit(3);
        fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragmentLists, fragmentTabTitles);
        vpSelectMedia.setAdapter(fragmentPagerAdapter);
        vpSelectMedia.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowFragmentPosition = position;
                for (int i = 0; i < fragmentLists.size(); i++) {
                    MediaFragment mediaFragment = (MediaFragment) fragmentLists.get(i);
                    List<Integer> list = Arrays.asList(fragmentTotalNumber);
                    if (!list.isEmpty()) {
                        mediaFragment.setTotalSize(Collections.max(list));
                    }
                }
                notifyFragmentDataSetChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tlSelectMedia.setupWithViewPager(vpSelectMedia);
    }

    /**
     * 校验一次数据，使得item标注的数据统一
     * <p>
     * Check the data once to make the data marked by the item uniform
     *
     * @param position 碎片索引值0，1，2；Shard index value 0.1.2
     */
    private void notifyFragmentDataSetChanged(int position) {
        MediaFragment fragment = (MediaFragment) fragmentLists.get(position);
        if (fragment == null || fragment.getAdapter() == null) return;
        List<MediaData> currentFragmentList = checkoutSelectList(fragment);
        fragment.getAdapter().setSelectList(currentFragmentList);
        setTitleText(fragment.getAdapter().getSelectList().size());
        Logger.e(TAG, "onPageSelected: " + fragment.getAdapter().getSelectList().size());
    }

    private List<MediaData> checkoutSelectList(MediaFragment fragment) {
        if (fragment == null || fragment.getAdapter() == null) return new ArrayList<>();
        List<MediaData> currentFragmentList = fragment.getAdapter().getSelectList();
        List<MediaData> totalSelectList = getMediaDataList();
        for (MediaData mediaData : currentFragmentList) {
            for (MediaData data : totalSelectList) {
                if (data.getPath().equals(mediaData.getPath()) && data.isState() == mediaData.isState()) {
                    mediaData.setPosition(data.getPosition());
                }
            }
        }
        return currentFragmentList;
    }

    private void checkDataCountAndTypeCount(String[] tabList, int[] mediaTypeCount) {
        if (tabList.length != mediaTypeCount.length) {
            return;
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < fragmentLists.size(); i++) {
            fragmentLists.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onTotalNumChangeForActivity(List selectList, Object tag) {
        int index = (int) tag;
        fragmentTotalNumber[index] = selectList.size();
        if (visitMethod == Constants.FROMPICINPICACTIVITYTOVISIT) {
            List<MediaData> mediaDataList = getMediaDataList();
            int count = mediaDataList.size();
            if (count == 1) {
                meidaTVOfStart.setText(R.string.select_the_second_video);
            } else if (count == 2) {
                meidaTVOfStart.setText(R.string.startMaking);
            } else {
                meidaTVOfStart.setText(R.string.select_two_video);
            }
            meidaTVOfStart.setTextColor(count == mLimiteMediaCount ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ffa3a3a3"));
        } else {
            meidaTVOfStart.setVisibility((Collections.max(Arrays.asList(fragmentTotalNumber))) > 0 ? View.VISIBLE : View.GONE);
        }

        for (int i = 0; i < fragmentLists.size(); i++) {
            if (i != index) {
                MediaFragment fragment = (MediaFragment) fragmentLists.get(i);
                fragment.refreshSelect(selectList, index);
            }
        }

        if (index == nowFragmentPosition) {
            setTitleText(selectList.size());
        }
    }


    public List<MediaData> getMediaDataList() {

        if (mMediaDataList == null) {
            return new ArrayList<>();
        }
        return getMediaDataList(0);
    }

    public List<MediaData> getMediaDataList(int position) {
        if (fragmentLists == null || position < 0 || position >= fragmentLists.size()) {
            return new ArrayList<>();
        }
        MediaFragment fragment = (MediaFragment) fragmentLists.get(position);
        if (fragment != null) {
            AgendaSimpleSectionAdapter adapter = fragment.getAdapter();
            if (adapter != null) {
                List<MediaData> mediaDataList = adapter.getSelectList();
                return mediaDataList;
            }
        }
        return new ArrayList<>();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        setTotal(0);
        nowFragmentPosition = 0;
        super.onDestroy();
    }

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

    private void selectCreateRatio(int makeRatio) {
        ArrayList<ClipInfo> pathList = getClipInfoList();
        if (pathList.size() == 1) {
            ClipInfo clipInfo = pathList.get(0);
            String filePath = clipInfo.getFilePath();
            NvsAVFileInfo avFileInfo = NvsStreamingContext.getAVInfoFromFile(filePath, 0);
            if (null == avFileInfo) {
                String[] versionName = getResources().getStringArray(R.array.clip_load_failed_tips);
                ToastUtil.showToast(SelectMediaActivity.this, versionName[1]);
                return;
            }
        }

        for (ClipInfo clipInfo : pathList) {
            String filePath = clipInfo.getFilePath();
            if (TextUtils.isEmpty(filePath)) {
                continue;
            }
            NvsAVFileInfo avFileInfo = NvsStreamingContext.getAVInfoFromFile(filePath, 0);
            if (null == avFileInfo) {
                continue;
            }
            int flag = avFileInfo.getFlags();
            if (flag != NvsAVFileInfo.AV_FILE_INFO_FLAGS_HAS_VIDEO_PART_IN_IMAGE) {
                continue;
            }
            NvsAVFileInfo.ExtendsDataInfo extendsDataInfo = avFileInfo.getExtendsDataInfo();
            if (extendsDataInfo == null) {
                continue;
            }
            LivePhotoInfo livePhotoInfo = new LivePhotoInfo();
            livePhotoInfo.setUrl(filePath);
            livePhotoInfo.setOffset(extendsDataInfo.offset);
            livePhotoInfo.setLength(extendsDataInfo.length);
            clipInfo.setLivePhotoInfo(livePhotoInfo);
        }
        NvsVideoResolution videoEditResolution = Util.getVideoEditResolution(makeRatio);
        Log.d(TAG, "videoEditResolution.imageWidth==" + videoEditResolution.imageWidth + " videoEditResolution.imageHeight==" + videoEditResolution.imageHeight);
        if (videoEditResolution != null && videoEditResolution.imageWidth * videoEditResolution.imageHeight > 2160 * 3840) {
            ToastUtil.showToast(this, getResources().getString(R.string.timeline_size_to_large));
            return;
        }
        TimelineData.instance().setVideoResolution(videoEditResolution);
        TimelineData.instance().setClipInfoData(pathList);
        TimelineData.instance().setMakeRatio(makeRatio);
        if (visitMethod == Constants.FROMMAINACTIVITYTOVISIT) {
            AppManager.getInstance().jumpActivity(SelectMediaActivity.this, VideoEditActivity.class, null);
        } else if (visitMethod == Constants.FROM_QUICK_SPLICING_ACTIVITY) {
            AppManager.getInstance().jumpActivity(SelectMediaActivity.this, QuickSplicingActivity.class, null);
        }
        AppManager.getInstance().finishActivity();
    }

    private ArrayList<ClipInfo> getClipInfoList() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (MediaData mediaData : getMediaDataList()) {
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setFilePath(mediaData.getPath());
            pathList.add(clipInfo);
        }
        return pathList;
    }

    private ArrayList<String> getPicInPicVideoList() {
        ArrayList<String> pathList = new ArrayList<>();
        for (MediaData mediaData : getMediaDataList()) {
            pathList.add(mediaData.getPath());
        }
        return pathList;
    }
}
