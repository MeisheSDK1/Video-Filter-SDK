package com.meishe.sdkdemo.edit.compoundcaption;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.model.BaseMvpActivity;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.Caption.presenter.CaptionPresenter;
import com.meishe.sdkdemo.edit.Caption.view.CaptionView;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.CaptionColorInfo;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineEditor;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineTimeSpan;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.CustomViewPager;
import com.meishe.sdkdemo.edit.view.InputDialog;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.CompoundCaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.ColorUtil;
import com.meishe.utils.PackageManagerUtil;
import com.meishe.utils.PathNameUtil;
import com.meishe.utils.ToastUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.meishe.sdkdemo.utils.Constants.CaptionColors;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑-组合字幕-Activity
 * @Description :VideoEdit-CompoundCaption-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CompoundCaptionActivity extends BaseMvpActivity<CaptionPresenter> implements View.OnClickListener, CaptionView {
    private static final String TAG = "CaptionActivity";
    private static final int VIDEOPLAYTOEOF = 105;
    private static final int REQUEST_CAPTION_STYLE = 103;
    private static final int COMPOINDCAPTIONREQUESTLIST = 104;
    public static final String select_caption_index = "select_caption_index";
    public static final String select_caption_text = "select_caption_text";
    private CustomTitleBar mTitleBar;
    private TextView mPlayCurTime;
    private RelativeLayout mZoomInBtn;
    private RelativeLayout mZoomOutBtn;
    private Button mModifyButton;
    private NvsTimelineEditor mTimelineEditor;
    private Button mPlayBtn;
    private ImageView mAddCaptionBtn;
    private ImageView mOkBtn;
    private VideoFragment mVideoFragment;
    private RelativeLayout mBottomRelativeLayout;
    private RelativeLayout mPlayBtnLayout;
    private NvsMultiThumbnailSequenceView mMultiSequenceView;

    private RelativeLayout mCompCaptionAssetLayout;
    private ImageView mComCaptionAssetFinish;
    private int mFontCurClickPos = 0;

    private NvsTimeline mTimeline;
    private boolean mIsSeekTimeline = true;
    private NvsTimelineCompoundCaption mCurCaption;
    private NvsTimelineCompoundCaption mAddComCaption;
    private NvsStreamingContext mStreamingContext;
    private List<CaptionTimeSpanInfo> mTimeSpanInfoList = new ArrayList<>();
    private CaptionHandler m_handler = new CaptionHandler(this);
    private ArrayList<CompoundCaptionInfo> mCaptionDataListClone;
    private boolean mIsInnerDrawRect = false;
    private StringBuilder mShowCurrentDuration = new StringBuilder();
    private ArrayList<AssetItem> mComCaptionStyleList = new ArrayList<>();
    private int mCurSelectedPos = -1;
    private boolean isNewCaptionUuidItemClick = false;
    private long mInPoint;
    private long mCaptionDuration;
    private int mCurCaptionZVal;
    private NvAssetManager mAssetManager;
    private InputDialog inputDialog;
    private int mCaptionStyleType = NvAsset.ASSET_COMPOUND_CAPTION;
    /**
     * 当前编辑的组合字幕中子字幕的index,默认编辑全部
     * Currently edit the subtitle index of the combined subtitle, editing all by default
     */
    private int mSelectedSubCaptionIndex = -1;
    /**
     * 组合字幕样式 背景 填充 描边 字体
     * Combine subtitle style background fill Stroke font
     */
    private TabLayout mCaptionStyleTab;
    private CustomViewPager mViewPager;
    private ArrayList<Fragment> mAssetFragmentsArray;
    private ArrayList<CaptionColorInfo> mCaptionColorList;
    private ArrayList<CaptionColorInfo> mCaptionOutlineColorList;
    private ArrayList<CaptionColorInfo> mCaptionBackgroundList;
    private ArrayList<AssetItem> mCaptionFontList;
    private ArrayList<AssetItem> mServerCaptionFontList;
    private CompoundCaptionStyleFragment mCaptionStyleFragment;
    private CompoundCaptionColorFragment mCaptionColorFragment;
    private CompoundCaptionOutlineFragment mCaptionOutlineFragment;
    private CompoundCaptionBackgroundFragment mCaptionBackgroundFragment;
    private CompoundCaptionFontFragment mCaptionFontFragment;
    private boolean m_waitFlag;
    private boolean isCaptionStyleItemClick;
    private int mSelectedStylePos = -1;
    private int mSelectedColorPos = 0;
    private int mSelectedBackgroundColorPos = 0;
    private int mSelectedOutlinePos = 0;
    private int mCaptionColorOpacityValue = 100;
    private int mCaptionOutlineOpacityValue = 100;
    private int mCaptionOutlineWidthValue = 8;
    private int mCaptionBackgroundOpacityValue = 100;

    @Override
    public void getFontsBack(ArrayList<AssetItem> data) {
        mServerCaptionFontList.clear();
        mServerCaptionFontList.addAll(data);
        initCaptionFontInfoList();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void onDownloadProgress(int position) {
        mCaptionFontFragment.notifyDataSetChanged();
    }

    @Override
    public void onDownloadFinish(int pos, NvAsset assetInfo) {
        mCaptionFontFragment.setSelectedPos(pos);
        assetInfo.name = mStreamingContext.registerFontByFilePath(assetInfo.localDirPath);
        applyCaptionFont(assetInfo.localDirPath, assetInfo.name);
    }

    @Override
    public void onDownloadError(int position) {
    }


    static class CaptionHandler extends Handler {
        WeakReference<CompoundCaptionActivity> mWeakReference;

        public CaptionHandler(CompoundCaptionActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CompoundCaptionActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case VIDEOPLAYTOEOF:
                        activity.resetView();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void resetView() {
        updatePlaytimeText(mCompCaptionAssetLayout.getVisibility() == View.VISIBLE ? mInPoint : 0);
        mMultiSequenceView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        seekTimeline(mCompCaptionAssetLayout.getVisibility() == View.VISIBLE ? mInPoint : 0);
        selectCaptionAndTimeSpan();
    }

    private void seekTimeline(long timeStamp, int flag) {
        mVideoFragment.seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER | flag);
    }

    private void seekTimeline(long timeStamp) {
        mVideoFragment.seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }

    @Override
    protected int bindLayout() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        AppManager.getInstance().addActivity(this);
        return R.layout.activity_compound_caption;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mPlayCurTime = (TextView) findViewById(R.id.play_cur_time);
        mZoomInBtn = (RelativeLayout) findViewById(R.id.zoom_in_btn);
        mZoomOutBtn = (RelativeLayout) findViewById(R.id.zoom_out_btn);
        mModifyButton = (Button) findViewById(R.id.modifyButton);
        mTimelineEditor = (NvsTimelineEditor) findViewById(R.id.caption_timeline_editor);
        mPlayBtn = (Button) findViewById(R.id.play_btn);
        mAddCaptionBtn = (ImageView) findViewById(R.id.add_caption_btn);
        mOkBtn = (ImageView) findViewById(R.id.ok_btn);
        mBottomRelativeLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mPlayBtnLayout = (RelativeLayout) findViewById(R.id.play_btn_layout);
        mMultiSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();
        mCompCaptionAssetLayout = (RelativeLayout) findViewById(R.id.compCaptionAsset_layout);
        //mComCaptionAssetDownload = (ImageView) findViewById(R.id.comCaptionAssetDownload);
        //mComCaptionRecycler = (RecyclerView) findViewById(R.id.comCaptionRecycler);
        mCaptionStyleTab = (TabLayout) findViewById(R.id.captionStyleTab);
        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        mViewPager.setPagingEnabled(false);
        mComCaptionAssetFinish = (ImageView) findViewById(R.id.comCaptionAssetFinish);
        initTitle();
    }

    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.compound_caption_title);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void requestData() {
        mStreamingContext = NvsStreamingContext.getInstance();
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null)
            return;
        mCaptionDataListClone = TimelineData.instance().cloneCompoundCaptionData();
        initCompoundStyleAssetData();
        initVideoFragment();
        updatePlaytimeText(0);
        initMultiSequence();
        addAllTimeSpan();
        selectCaption();
        selectTimeSpan();
        initCompoundCaptionStyleList();
        //initCompoundCaptionRecycleAdapter();
//        initRegisterCaptionFontInfoList();
//        initCaptionFontInfoList();
        initTabLayout();
        initListener();
        mPresenter.getFonts();
    }

    private void initCompoundStyleAssetData() {
        mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.searchLocalAssets(mCaptionStyleType);
        String bundlePath = "compoundcaption";
        mAssetManager.searchReservedAssets(mCaptionStyleType, bundlePath);

        mCaptionBackgroundList = new ArrayList<>();
        mAssetFragmentsArray = new ArrayList<>();
        mCaptionColorList = new ArrayList<>();
        mCaptionOutlineColorList = new ArrayList<>();
        mCaptionFontList = new ArrayList<>();
        mServerCaptionFontList = new ArrayList<>();
        initCaptionBackgroundList();
        initCaptionColorList();
        initCaptionOutlineColorList();
    }

//    private void initRegisterCaptionFontInfoList() {
//        String fontJsonPath = "font/info.json";
//        String fontJsonText = ParseJsonFile.readAssetJsonFile(this, fontJsonPath);
//        if (TextUtils.isEmpty(fontJsonText)) {
//            return;
//        }
//        ArrayList<FontInfo> fontInfoList = ParseJsonFile.fromJson(fontJsonText, new TypeToken<List<FontInfo>>() {
//        }.getType());
//        if (fontInfoList == null) {
//            return;
//        }
//        int fontCount = fontInfoList.size();
//        for (int idx = 0; idx < fontCount; idx++) {
//            FontInfo fontInfo = fontInfoList.get(idx);
//            if (fontInfo == null) {
//                continue;
//            }
//            String fontAssetPath = "assets:/font/" + fontInfo.getFontFileName();
//            mStreamingContext.registerFontByFilePath(fontAssetPath);
//        }
//
////        String fontJsonPathSD = "storage/NvStreamingSdk/Asset/Font/info.json";
//        String fontJsonPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font/info.json";
//        if (AndroidOS.USE_SCOPED_STORAGE) {
//            fontJsonPathSD = getExternalFilesDir("") + "/NvStreamingSdk/Asset/Font/info.json";
//        }
//        String fontJsonTextSD = ParseJsonFile.readSDJsonFile(this, fontJsonPathSD);
//        if (TextUtils.isEmpty(fontJsonTextSD)) {
//            return;
//        }
//        ArrayList<FontInfo> fontInfoListSD = ParseJsonFile.fromJson(fontJsonTextSD, new TypeToken<List<FontInfo>>() {
//        }.getType());
//        if (fontInfoListSD == null) {
//            return;
//        }
//        int fontCountSD = fontInfoListSD.size();
//        for (int idx = 0; idx < fontCountSD; idx++) {
//            FontInfo fontInfo = fontInfoListSD.get(idx);
//            if (fontInfo == null) {
//                continue;
//            }
//
//            String fontAssetPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font/" + fontInfo.getFontFileName();
//            if (AndroidOS.USE_SCOPED_STORAGE) {
//                fontAssetPathSD = getExternalFilesDir("") + "/NvStreamingSdk/Asset/Font/" + fontInfo.getFontFileName();
//            }
//            mStreamingContext.registerFontByFilePath(fontAssetPathSD);
//        }
//    }

    private void initCaptionFontInfoList() {
//        String fontJsonPath = "font/info.json";
//        String fontJsonText = ParseJsonFile.readAssetJsonFile(this, fontJsonPath);
//        if (TextUtils.isEmpty(fontJsonText)) {
//            return;
//        }
//        List<FontInfo> infoList = ParseJsonFile.fromJson(fontJsonText, new TypeToken<List<FontInfo>>() {
//        }.getType());
//        if (infoList == null) {
//            return;
//        }
//        int fontCount = infoList.size();
//        for (int idx = 0; idx < fontCount; idx++) {
//            FontInfo fontInfo = infoList.get(idx);
//            if (fontInfo == null) {
//                continue;
//            }
//            String fontAssetPath = "assets:/font/" + fontInfo.getFontFileName();
//            String fontName = mStreamingContext.registerFontByFilePath(fontAssetPath);
//            AssetItem assetItem = new AssetItem();
//            NvAsset asset = new NvAsset();
//            String fontCoverPath = "file:///android_asset/font/" + fontInfo.getImageName();
//            asset.coverUrl = fontCoverPath;
//            asset.isReserved = true;
//            asset.bundledLocalDirPath = fontAssetPath;
//            asset.name = fontName;
//            assetItem.setAsset(asset);
//            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
//            mCaptionFontList.add(assetItem);
//        }
        AssetItem assetItem = new AssetItem();
        NvAsset asset = new NvAsset();
        assetItem.setImageRes(R.mipmap.comp_caption_default);
        assetItem.setAssetMode(AssetItem.ASSET_NONE);
        assetItem.setAsset(asset);
        mCaptionFontList.add(0, assetItem);
        mCaptionFontList.addAll(mServerCaptionFontList);

//        //从SDK卡读取字体文件  Read the font file from the SDK card
//        String fontJsonPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font";
//        if (AndroidOS.USE_SCOPED_STORAGE) {
//            fontJsonPathSD = getExternalFilesDir("") + "/NvStreamingSdk/Asset/Font";
//        }
//        File fontInfoListSDFile = new File(fontJsonPathSD);
//        if (null != fontInfoListSDFile && fontInfoListSDFile.isDirectory()) {
//            File[] files = fontInfoListSDFile.listFiles();
//            if (null != files && files.length > 0) {
//                for (File file : files) {
//                    if (file == null) {
//                        continue;
//                    }
//                    String fontAssetPathSD = file.getAbsolutePath();
//                    String filePath = mStreamingContext.registerFontByFilePath(fontAssetPathSD);
//                    assetItem = new AssetItem();
//                    asset = new NvAsset();
//                    asset.bundledLocalDirPath = fontAssetPathSD;
//                    assetItem.setImageRes(R.mipmap.comp_caption_default);
//                    asset.name = filePath;
//                    assetItem.setAsset(asset);
//                    assetItem.setAssetMode(AssetItem.ASSET_NONE);
//                    mCaptionFontList.add(assetItem);
//                }
//            }
//        }
    }

    private void initTabLayout() {
        String[] assetName = getResources().getStringArray(R.array.compoundCaptionEdit);
        mCaptionStyleTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue_4a));
        for (int i = 0; i < assetName.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(assetName[i]);
            textView.setTextSize(12);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.gray_90));
            mCaptionStyleTab.addTab(mCaptionStyleTab.newTab().setCustomView(textView));
        }
        initCaptionTabFragment();
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mAssetFragmentsArray.get(position);
            }

            @Override
            public int getCount() {
                return mAssetFragmentsArray.size();
            }
        });
        /*
         * 添加tab切换的监听事件
         * Add a tab switch to listen for events
         * */
        mCaptionStyleTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mAddComCaption == null) {
                    if (tab.getPosition() != 0) {
                        ToastUtil.showToast(CompoundCaptionActivity.this, getResources().getString(R.string.no_edit_caption));
                        mCaptionStyleTab.getTabAt(0).select();
                    }

                    return;
                }
                /*
                 * 当前选中的tab的位置，切换到相应的fragment
                 * Position of the currently selected tab, switch to the corresponding fragment
                 * */
                int nowPosition = tab.getPosition();
                TextView textView = (TextView) tab.getCustomView();
                textView.setTextColor(getResources().getColor(R.color.blue_4a));
                mViewPager.setCurrentItem(nowPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.gray_90));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initCaptionColorList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionColorList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
    }

    private void initCaptionOutlineColorList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionOutlineColorList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
        mCaptionOutlineColorList.add(0, new CaptionColorInfo("", false));
    }

    private void initCaptionBackgroundList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionBackgroundList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
        mCaptionBackgroundList.add(0, new CaptionColorInfo("", false));
    }

    private void initCaptionTabFragment() {
        mCaptionStyleFragment = initCaptionStyleFragment();//字幕样式 Subtitle style
        mAssetFragmentsArray.add(mCaptionStyleFragment);
        mCaptionColorFragment = initCaptionColorFragment();//填充 fill
        mAssetFragmentsArray.add(mCaptionColorFragment);
        mCaptionOutlineFragment = initCaptionOutlineFragment();//描边 stroke
        mAssetFragmentsArray.add(mCaptionOutlineFragment);
        mCaptionBackgroundFragment = initCaptionBackgroundFragment();//背景 background
        mAssetFragmentsArray.add(mCaptionBackgroundFragment);

        mCaptionFontFragment = initCaptionFontFragment();//字体 font
        mAssetFragmentsArray.add(mCaptionFontFragment);
    }

    private CompoundCaptionStyleFragment initCaptionStyleFragment() {
        CompoundCaptionStyleFragment captionStyleFragment = new CompoundCaptionStyleFragment();
        captionStyleFragment.setAssetInfolist(mComCaptionStyleList);
        captionStyleFragment.setCaptionStyleListener(new CompoundCaptionStyleFragment.OnCaptionStyleListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionStyleFragment.setSelectedPos(mSelectedStylePos);
                mCaptionStyleFragment.notifyDataSetChanged();
            }

            @Override
            public void OnDownloadCaptionStyle() {
                if (m_waitFlag)
                    return;
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreCompoundCaptionStyle);
                bundle.putInt("assetType", NvAsset.ASSET_COMPOUND_CAPTION);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, COMPOINDCAPTIONREQUESTLIST);

                m_waitFlag = true;
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mComCaptionStyleList.size())
                    return;
                if (mCurSelectedPos == pos) {
                    if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                        if (mAddComCaption != null) {
                            long startTime = mAddComCaption.getInPoint();
                            long endTime = mAddComCaption.getOutPoint();
                            mVideoFragment.playVideo(startTime, endTime);
                            mVideoFragment.setDrawRectVisible(View.GONE);
                            isNewCaptionUuidItemClick = false;
                        }
                    } else {
                        mVideoFragment.stopEngine();
                    }
                    return;
                }
                mVideoFragment.setDrawRectVisible(View.GONE);
                NvAsset asset = mComCaptionStyleList.get(pos).getAsset();
                if (asset == null)
                    return;
                mCurSelectedPos = pos;
                String captionStyleUuid = asset.uuid;
                addCaption(captionStyleUuid);

            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
            }
        });
        return captionStyleFragment;
    }

    private CompoundCaptionColorFragment initCaptionColorFragment() {
        CompoundCaptionColorFragment captionColorFragment = new CompoundCaptionColorFragment();
        captionColorFragment.setCaptionColorInfolist(mCaptionColorList);
        captionColorFragment.setCaptionColorListener(new CompoundCaptionColorFragment.OnCaptionColorListener() {
            @Override
            public void onFragmentLoadFinished() {
                //mSelectedColorPos = getCaptionColorSelectedIndex();
                if (mSelectedColorPos >= 0) {
                    mCaptionColorList.get(mSelectedColorPos).mSelected = true;
                    mCaptionColorFragment.setCaptionColorInfolist(mCaptionColorList);
                    mCaptionColorFragment.notifyDataSetChanged();
                }
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    //  mCaptionColorOpacityValue = mCaptionDataListClone.get(index).getCaptionColorAlpha();
                    //  mCaptionColorFragment.updateCaptionOpacityValue(mCaptionColorOpacityValue);
                }
            }

            @Override
            public void onCaptionColor(int pos) {
                if (pos < 0 || pos > mCaptionColorList.size())
                    return;
                if (mCurCaption == null)
                    return;
                if (mSelectedColorPos >= 0)
                    mCaptionColorList.get(mSelectedColorPos).mSelected = false;
                mCaptionColorList.get(pos).mSelected = true;
                mSelectedColorPos = pos;
                mCaptionColorFragment.notifyDataSetChanged();
                mCaptionColorOpacityValue = 100;
                NvsColor color = ColorUtil.colorStringtoNvsColor(mCaptionColorList.get(pos).mColorValue);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (mSelectedSubCaptionIndex < 0) {
                    //循环设置所有字幕子item的颜色 Loop to set the color of all subitems of the subtitles
                    int count = mCurCaption.getCaptionCount();
                    for (int i = 0; i < count; i++) {
                        mCurCaption.setTextColor(i, color);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setM_usedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionColor(mCaptionColorList.get(pos).mColorValue);
                        }
                    }
                } else {
                    mCurCaption.setTextColor(mSelectedSubCaptionIndex, color);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setM_usedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionColor(mCaptionColorList.get(pos).mColorValue);
                    }
                }
                mCaptionColorFragment.updateCaptionOpacityValue(mCaptionColorOpacityValue);
                playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());
            }

            @Override
            public void onCaptionOpacity(int progress) {
                if (mCurCaption == null)
                    return;
                /*
                 * 设置字体的不透明度
                 * Set the opacity of the font
                 * */
                //默认编辑全部，如果有字幕的子字幕被选中编辑这个目标字幕 Edit all by default, if subsubtitles with subtitles are selected edit this target subtitles
                int editIndex = 0;
                if (mSelectedSubCaptionIndex >= 0) {
                    editIndex = mSelectedSubCaptionIndex;
                }
                NvsColor curColor = mCurCaption.getTextColor(editIndex);
                curColor.a = progress / 100.0f;
                String strColor = ColorUtil.nvsColorToHexString(curColor);
                mCaptionColorOpacityValue = progress;

                int index = getCaptionIndex(mCurCaptionZVal);
                if (mSelectedSubCaptionIndex < 0) {
                    //循环设置所有字幕子item的颜色 Loop to set the color of all subitems of the subtitles
                    int count = mCurCaption.getCaptionCount();
                    for (int i = 0; i < count; i++) {
                        mCurCaption.setTextColor(i, curColor);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setM_usedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionColor(strColor);
                        }
                    }
                } else {
                    mCurCaption.setTextColor(mSelectedSubCaptionIndex, curColor);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setM_usedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionColor(strColor);
                    }
                }

                playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
            }
        });
        return captionColorFragment;
    }

    private CompoundCaptionOutlineFragment initCaptionOutlineFragment() {
        CompoundCaptionOutlineFragment captionOutlineFragment = new CompoundCaptionOutlineFragment();
        captionOutlineFragment.setCaptionOutlineInfolist(mCaptionOutlineColorList);
        captionOutlineFragment.setCaptionOutlineListener(new CompoundCaptionOutlineFragment.OnCaptionOutlineListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionOutlineColorList.get(mSelectedOutlinePos).mSelected = true;
                mCaptionOutlineFragment.setCaptionOutlineInfolist(mCaptionOutlineColorList);
                mCaptionOutlineFragment.notifyDataSetChanged();
                int index = getCaptionIndex(mCurCaptionZVal);
               /* if (index >= 0) {
                    boolean isDrawOutline = mCaptionDataListClone.get(index).isHasOutline();
                    if (isDrawOutline) {
                        mCaptionOutlineWidthValue = (int) mCaptionDataListClone.get(index).getOutlineWidth();
                        mCaptionOutlineOpacityValue = mCaptionDataListClone.get(index).getOutlineColorAlpha();
                    }
                    mCaptionOutlineFragment.updateCaptionOutlineWidthValue(mCaptionOutlineWidthValue);
                    mCaptionOutlineFragment.updateCaptionOutlineOpacityValue(mCaptionOutlineOpacityValue);
                }*/
            }

            @Override
            public void onCaptionOutlineColor(int pos) {
                if (pos < 0 || pos > mCaptionOutlineColorList.size())
                    return;
                if (mCurCaption == null)
                    return;
                mCaptionOutlineColorList.get(mSelectedOutlinePos).mSelected = false;
                mCaptionOutlineColorList.get(pos).mSelected = true;
                mCaptionOutlineFragment.notifyDataSetChanged();
                mSelectedOutlinePos = pos;

                mCaptionOutlineOpacityValue = 100;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (pos == 0) {
                    if (mSelectedSubCaptionIndex < 0) {
                        int count = mCurCaption.getCaptionCount();
                        for (int i = 0; i < count; i++) {
                            mCurCaption.setDrawOutline(false, i);
                            if (index >= 0) {
                                mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setM_usedOutlineFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                            }
                        }
                    } else {
                        mCurCaption.setDrawOutline(false, mSelectedSubCaptionIndex);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setM_usedOutlineFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                        }
                    }

                } else {
                    mCaptionOutlineWidthValue = 8;

                    NvsColor color = ColorUtil.colorStringtoNvsColor(mCaptionOutlineColorList.get(pos).mColorValue);

                    if (mSelectedSubCaptionIndex < 0) {
                        int count = mCurCaption.getCaptionCount();
                        for (int i = 0; i < count; i++) {
                            /*
                             * 设置字幕描边标识
                             * Set caption stroke flag
                             * */
                            mCurCaption.setDrawOutline(true, i);
                            /*
                             * 设置描边颜色
                             * Set outline color
                             * */
                            mCurCaption.setOutlineColor(color, i);
                            /*
                             * 字幕描边宽度
                             * Caption stroke width
                             * */

                            mCurCaption.setOutlineWidth(mCaptionOutlineWidthValue, i);
                            if (index >= 0) {
                                mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setM_usedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionStrokeColor(mCaptionOutlineColorList.get(pos).mColorValue);
                                mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionStrokeWidth(mCaptionOutlineWidthValue);
                                mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionStrokeColorAlpha(mCaptionOutlineOpacityValue);


                            }
                        }
                    } else {
                        /*
                         * 设置字幕描边标识
                         * Set caption stroke flag
                         * */
                        mCurCaption.setDrawOutline(true, mSelectedSubCaptionIndex);
                        /*
                         * 设置描边颜色
                         * Set outline color
                         * */
                        mCurCaption.setOutlineColor(color, mSelectedSubCaptionIndex);
                        /*
                         * 字幕描边宽度
                         * Caption stroke width
                         * */
                        mCurCaption.setOutlineWidth(mCaptionOutlineWidthValue, mSelectedSubCaptionIndex);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setM_usedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionStrokeColor(mCaptionOutlineColorList.get(pos).mColorValue);
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionStrokeWidth(mCaptionOutlineWidthValue);
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionStrokeColorAlpha(mCaptionOutlineOpacityValue);
                        }
                    }
                }
                mCaptionOutlineFragment.updateCaptionOutlineWidthValue(mCaptionOutlineWidthValue);
                mCaptionOutlineFragment.updateCaptionOutlineOpacityValue(mCaptionOutlineOpacityValue);
                playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());
            }

            @Override
            public void onCaptionOutlineWidth(int width) {
                if (mCurCaption == null)
                    return;
                if (mSelectedOutlinePos == 0)
                    return;
                int index = getCaptionIndex(mCurCaptionZVal);
                /*
                 * 字幕描边宽度
                 * Caption stroke width
                 * */
                if (mSelectedSubCaptionIndex < 0) {
                    int count = mCurCaption.getCaptionCount();
                    for (int i = 0; i < count; i++) {
                        mCurCaption.setOutlineWidth(width, i);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionStrokeWidth(width);
                        }
                    }
                } else {
                    mCurCaption.setOutlineWidth(width, mSelectedSubCaptionIndex);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionStrokeWidth(width);
                    }
                }
                mCaptionOutlineWidthValue = width;

                playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());
            }

            @Override
            public void onCaptionOutlineOpacity(int opacity) {
                if (mCurCaption == null)
                    return;
                if (mSelectedOutlinePos == 0)
                    return;
                int index = getCaptionIndex(mCurCaptionZVal);
                /*
                 * 设置字幕描边的不透明度
                 * Set the opacity of the caption stroke
                 * */
                int editIndex = 0;
                if (mSelectedSubCaptionIndex >= 0) {
                    editIndex = mSelectedSubCaptionIndex;
                }
                NvsColor curColor = mCurCaption.getOutlineColor(editIndex);
                curColor.a = opacity / 100.0f;
                String strColor = ColorUtil.nvsColorToHexString(curColor);
                if (mSelectedSubCaptionIndex < 0) {
                    int count = mCurCaption.getCaptionCount();
                    for (int i = 0; i < count; i++) {
                        mCurCaption.setOutlineColor(curColor, i);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionStrokeColor(strColor);
                        }
                    }
                } else {
                    mCurCaption.setOutlineColor(curColor, mSelectedSubCaptionIndex);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionStrokeColor(strColor);
                    }
                }
                mCaptionOutlineOpacityValue = opacity;
                playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
            }
        });
        return captionOutlineFragment;
    }

    private CompoundCaptionBackgroundFragment initCaptionBackgroundFragment() {
        CompoundCaptionBackgroundFragment captionBackgroundFragment = new CompoundCaptionBackgroundFragment();
        captionBackgroundFragment.setCaptionBackgroundInfolist(mCaptionBackgroundList);
        captionBackgroundFragment.setCaptionBackgroundListener(new CompoundCaptionBackgroundFragment.OnCaptionBackgroundListener() {
            @Override
            public void onFragmentLoadFinished() {

                int index = getCaptionIndex(mCurCaptionZVal);
               /* if (index >= 0) {
                    mCaptionBackgroundOpacityValue = mCaptionDataListClone.get(index).getCaptionBackgroundAlpha();
                    mCaptionBackgroundFragment.updateCaptionOpacityValue(mCaptionBackgroundOpacityValue);
                }*/
                //mSelectedBackgroundPos = getBackgroundSelectedIndex();
                /*if (mSelectedBackgroundPos >= 0) {
                    mCaptionBackgroundList.get(mSelectedBackgroundPos).mSelected = true;
                    NvsColor curColor = mCurAddCaption.getBackgroundColor();
                    String strColor = ColorUtil.nvsColorToHexString(curColor);
                    mCaptionBackgroundList.get(mSelectedBackgroundPos).mColorValue = strColor;
                    mCaptionBackgroundFragment.setCaptionBackgroundInfolist(mCaptionBackgroundList);
                    mCaptionBackgroundFragment.notifyDataSetChanged();
                }*/

            }

            @Override
            public void onCaptionColor(int pos) {
                if (pos < 0 || pos > mCaptionBackgroundList.size())
                    return;
                if (mCurCaption == null)
                    return;
                mCaptionBackgroundList.get(pos).mSelected = true;
                mCaptionBackgroundList.get(mSelectedBackgroundColorPos).mSelected = false;
                mSelectedBackgroundColorPos = pos;
                mCaptionBackgroundFragment.notifyDataSetChanged();
                int index = getCaptionIndex(mCurCaptionZVal);
                String noColor = "#00000000";
                NvsColor color = ColorUtil.colorStringtoNvsColor(noColor);
                if (pos != 0) {
                    //背景色透明度 Background color transparency
                    mCaptionBackgroundOpacityValue = 100;
                    color = ColorUtil.colorStringtoNvsColor(mCaptionBackgroundList.get(pos).mColorValue);
                    noColor = ColorUtil.nvsColorToHexString(color);
                }
                if (mSelectedSubCaptionIndex < 0) {
                    int count = mCurCaption.getCaptionCount();
                    for (int i = 0; i < count; i++) {
                        mCurCaption.setBackgroundColor(color, i);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setM_usedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionBackgroundColor(noColor);
                        }
                    }
                } else {
                    mCurCaption.setBackgroundColor(color, mSelectedSubCaptionIndex);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setM_usedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionBackgroundColor(noColor);
                    }
                }

                mCaptionBackgroundFragment.updateCaptionOpacityValue(mCaptionBackgroundOpacityValue);
                playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());

            }

            @Override
            public void onCaptionOpacity(int progress) {
                if (mCurCaption == null)
                    return;
                int index = getCaptionIndex(mCurCaptionZVal);
                /*
                 * 设置背景的不透明度
                 * Set the opacity of the font
                 * */
                int editIndex = 0;
                if (mSelectedSubCaptionIndex >= 0) {
                    editIndex = mSelectedSubCaptionIndex;
                }
                NvsColor curColor = mCurCaption.getBackgroundColor(editIndex);
                curColor.a = progress / 100.0f;
                String strColor = ColorUtil.nvsColorToHexString(curColor);
                if (mSelectedSubCaptionIndex < 0) {
                    int count = mCurCaption.getCaptionCount();
                    for (int i = 0; i < count; i++) {
                        mCurCaption.setBackgroundColor(curColor, i);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setM_usedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionBackgroundColor(strColor);
                            mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionBackgroundColorAlpha(progress);
                        }
                    }
                } else {
                    mCurCaption.setBackgroundColor(curColor, mSelectedSubCaptionIndex);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setM_usedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionBackgroundColor(strColor);
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionBackgroundColorAlpha(progress);
                    }
                }
                mCaptionBackgroundOpacityValue = progress;
                playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());
            }

            @Override
            public void onCaptionCorner(int progress) {
            }

            @Override
            public void onCaptionPadding(int progress) {
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
            }
        });
        return captionBackgroundFragment;
    }

    private CompoundCaptionFontFragment initCaptionFontFragment() {
        CompoundCaptionFontFragment captionFontFragment = new CompoundCaptionFontFragment();
        captionFontFragment.setFontInfolist(mCaptionFontList);
        captionFontFragment.setCaptionFontListener(new CompoundCaptionFontFragment.OnCaptionFontListener() {
            @Override
            public void onFragmentLoadFinished() {
                int index = getCaptionIndex(mCurCaptionZVal);
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mCaptionFontList.size())
                    return;
                if (null == mCurCaption) {
                    return;
                }
                NvAsset asset = mCaptionFontList.get(pos).getAsset();
                if (asset == null)
                    return;
                if (!TextUtils.isEmpty(asset.localDirPath)) {
                    applyCaptionFont(asset.localDirPath, asset.name);
                } else if (!TextUtils.isEmpty(asset.bundledLocalDirPath)) {
                    applyCaptionFont(asset.bundledLocalDirPath, asset.name);
                } else if (pos == 0) {
                    applyCaptionFont("", "");
                }
            }

            @Override
            public void onBold(boolean mIsBold) {
            }

            @Override
            public void onItalic(boolean isItalic) {
                if (null == mCurCaption) {
                    return;
                }
                applyCaptionItalic(isItalic);
            }

            @Override
            public void onShadow() {
            }

            @Override
            public void onUnderline() {
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
            }

            @Override
            public void onFontDownload(int pos) {
                int count = mCaptionFontList.size();
                if (pos <= 0 || pos >= count)
                    return;
                if (mFontCurClickPos == pos) {
                    /*
                     * 重复点击，不作处理；防止素材多次下载
                     * Double click without processing; prevent material from downloading multiple times
                     * */
                    return;
                }
                mFontCurClickPos = pos;
                mPresenter.downloadAsset(mCaptionFontList.get(pos).getAsset(), mFontCurClickPos);
            }
        });
        return captionFontFragment;
    }

    private void applyCaptionFont(String bundledLocalDirPath, String assetsName) {
        if (mCurCaption != null) {
            int index = getCaptionIndex(mCurCaptionZVal);

            if (mSelectedSubCaptionIndex < 0) {
                int count = mCurCaption.getCaptionCount();
                for (int i = 0; i < count; i++) {
                    mCurCaption.setFontFamily(i, assetsName);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setCaptionFontName(assetsName);
                    }
                }
            } else {
                mCurCaption.setFontFamily(mSelectedSubCaptionIndex, assetsName);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setCaptionFontName(assetsName);
                }
            }
            playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());
        }
    }

    private void applyCaptionItalic(boolean italic) {
        if (mCurCaption != null) {
            int index = getCaptionIndex(mCurCaptionZVal);

            if (mSelectedSubCaptionIndex < 0) {
                int count = mCurCaption.getCaptionCount();
                for (int i = 0; i < count; i++) {
                    mCurCaption.setItalic(i, italic);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(i).setMisItalic(italic);
                    }
                }
            } else {
                mCurCaption.setItalic(mSelectedSubCaptionIndex, italic);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).getCaptionAttributeList().get(mSelectedSubCaptionIndex).setMisItalic(italic);
                }
            }
            playVideo(mCurCaption.getInPoint(), mCurCaption.getOutPoint());
        }
    }

    private void playVideo(long mInPoint, long outPoint) {
        /*
         * 播放视频
         * Play video
         * */
        mVideoFragment.playVideo(mInPoint, outPoint);
        //mVideoFragment.setDrawRectVisible(View.GONE);
    }

    protected void initListener() {
        mZoomInBtn.setOnClickListener(this);
        mZoomOutBtn.setOnClickListener(this);
        mModifyButton.setOnClickListener(this);
        mAddCaptionBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        //mComCaptionAssetDownload.setOnClickListener(this);
        mComCaptionAssetFinish.setOnClickListener(this);
        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                if (!mIsSeekTimeline)
                    return;
                if (mTimeline != null) {
                    updatePlaytimeText(timeStamp);
                    selectCaptionAndTimeSpan();
                    seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
                }
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mTimelineEditor.setTimelineScaleForSeek(mTimeline, mTimelineEditor.getDurationWidth());
                } else if (action == MotionEvent.ACTION_UP | action == MotionEvent.ACTION_CANCEL) {
                    seekTimeline(NvsStreamingContext.getInstance().getTimelineCurrentPosition(mTimeline));
                }
                mIsSeekTimeline = true;
                return false;
            }
        });
        if (mVideoFragment != null) {
            mVideoFragment.setLiveWindowClickListener(new VideoFragment.OnLiveWindowClickListener() {
                @Override
                public void onLiveWindowClick() {
                    isNewCaptionUuidItemClick = false;
                }
            });
        }

        if (mVideoFragment != null) {
            mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
                @Override
                public void playBackEOF(NvsTimeline timeline) {
                    m_handler.sendEmptyMessage(VIDEOPLAYTOEOF);
                }

                @Override
                public void playStopped(NvsTimeline timeline) {
                    if (isNewCaptionUuidItemClick)
                        return;
                    selectCaptionAndTimeSpan();
                }

                @Override
                public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                    updatePlaytimeText(stamp);
                    mVideoFragment.setDrawRectVisible(View.GONE);
                    mTimelineEditor.unSelectAllTimeSpan();
                    selectCaption();
                    multiThumbnailSequenceViewSmooth(stamp);
                }

                @Override
                public void streamingEngineStateChanged(int state) {
                    if (NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK == state) {
                        mIsSeekTimeline = false;
                        mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_pause);
                    } else {
                        mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_play);
                        mIsSeekTimeline = true;
                    }
                }
            });
            mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
                @Override
                public void onAssetDelete() {
                    removeCaption(mCurCaption);
                    mCurCaption = null;
                    mAddCaptionBtn = null;
                    selectCaptionAndTimeSpan();
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                    /*
                     * 取消所有Tab页贴纸选中的状态
                     * Deselect all tab page stickers
                     * */
                    notifyDataSetChanged(-1);
                }

                @Override
                public void onAssetSelected(PointF curPoint) {
                    if (mCompCaptionAssetLayout.getVisibility() == View.VISIBLE) {
                        /*
                         * 组合字幕素材列表显示，则不允许在liveWindow 来回切换选择组合字幕，只选择当前添加的组合字幕
                         * The combined subtitle material list is displayed. It is not allowed to switch between the combined subtitles in liveWindow.
                         * Only the currently added combined subtitles are selected.
                         * */
                        return;
                    }
                    /*
                     * 判断若没有选中当前字幕框则选中，选中则不处理
                     * Judge if the current subtitle box is not selected, select it, do not process
                     * */
                    mIsInnerDrawRect = mVideoFragment.curPointIsInnerDrawRect((int) curPoint.x, (int) curPoint.y);
                    if (!mIsInnerDrawRect) {
                        mVideoFragment.selectCompoundCaptionByHandClick(curPoint);
                        mCurCaption = mVideoFragment.getCurrCompoundCaption();
                        selectTimeSpan();
                    }
                }

                @Override
                public void onAssetTranslation() {
                    if (mCurCaption == null)
                        return;
                    PointF pointF = mCurCaption.getCaptionTranslation();
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setTranslation(pointF);
                    }
                }

                @Override
                public void onAssetScale() {
                    if (mCurCaption == null)
                        return;
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setScaleFactorX(mCurCaption.getScaleX());
                        mCaptionDataListClone.get(index).setScaleFactorY(mCurCaption.getScaleY());
                        mCaptionDataListClone.get(index).setAnchor(mCurCaption.getAnchorPoint());
                        mCaptionDataListClone.get(index).setRotation(mCurCaption.getRotationZ());
                        PointF pointF = mCurCaption.getCaptionTranslation();
                        mCaptionDataListClone.get(index).setTranslation(pointF);
                    }
                }

                @Override
                public void onAssetAlign(int alignVal) {
                }

                @Override
                public void onAssetHorizFlip(boolean isHorizFlip) {

                }
            });
            mVideoFragment.setCompoundCaptionListener(new VideoFragment.OnCompoundCaptionListener() {
                @Override
                public void onCaptionIndex(int captionIndex) {
                    if (mCurCaption == null) {
                        return;
                    }
                    mSelectedSubCaptionIndex = captionIndex;
                    int captionCount = mCurCaption.getCaptionCount();
                    if (captionIndex < 0 || captionIndex >= captionCount) {
                        return;
                    }

                   /* mIsInnerDrawRect = false;
                    int zVal = (int) mCurCaption.getZValue();
                    BackupData.instance().setCaptionZVal(zVal);
                    BackupData.instance().setCompoundCaptionList(mCaptionDataListClone);
                    BackupData.instance().setCurSeekTimelinePos(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                    String captionText = mCurCaption.getText(captionIndex);
                    Bundle bundle = new Bundle();
                    bundle.putInt(select_caption_index, captionIndex);
                    bundle.putString(select_caption_text, captionText);
                    AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), CompoundCaptionStyleActivity.class, bundle, REQUEST_CAPTION_STYLE);
                    */
                }

                @Override
                public void onCaptionDoubleClick(int captionIndex) {
                    if (null == mCurCaption) {
                        return;
                    }
                    if (captionIndex < 0) {
                        return;
                    }
                    if (null != inputDialog && inputDialog.isShowing()) {
                        return;
                    }
                    String captionText = mCurCaption.getText(captionIndex);
                    inputDialog = new InputDialog(CompoundCaptionActivity.this, R.style.dialog, new InputDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean ok) {
                            if (ok) {
                                InputDialog d = (InputDialog) dialog;
                                String userInputText = d.getUserInputText();
                                if (null != mCurCaption) {
                                    mCurCaption.setText(captionIndex, userInputText);
                                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                                    mVideoFragment.updateCompoundCaptionCoordinate(mCurCaption);
                                    mVideoFragment.changeCaptionRectVisible();
                                    int zVal = (int) mCurCaption.getZValue();
                                    int index = getCaptionIndex(zVal);
                                    Log.d(TAG, "index==" + index + "  captionIndex===" + captionIndex);
                                    if (index >= 0) {
                                        mCaptionDataListClone.get(index).getCaptionAttributeList().get(captionIndex).setCaptionText(userInputText);
                                    }
                                }

                            }
                        }
                    });
                    inputDialog.setUserInputText(captionText);
                    inputDialog.show();
                }
            });
        }

        mCompCaptionAssetLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.zoom_in_btn) {
            mIsSeekTimeline = false;
            mTimelineEditor.ZoomInSequence();
        } else if (id == R.id.zoom_out_btn) {
            mIsSeekTimeline = false;
            mTimelineEditor.ZoomOutSequence();
        } else if (id == R.id.modifyButton) {
            mModifyButton.setClickable(false);
            if (mCurCaption != null) {
                mCurCaptionZVal = (int) mCurCaption.getZValue();
            }
            int index = getCaptionIndex(mCurCaptionZVal);
            if (index < 0) {
                return;
            }
            CompoundCaptionInfo captionInfo = mCaptionDataListClone.get(index);
            if (captionInfo == null) {
                return;
            }
            String captionPackageUuid = captionInfo.getCaptionStyleUuid();
            int selectPos = getCaptionSelectPos(captionPackageUuid);
            if (selectPos >= 0) {
                //notifyDataSetChanged(selectPos);
                mCompCaptionAssetLayout.setVisibility(View.VISIBLE);
                mAddComCaption = mCurCaption;
                mInPoint = mCurCaption.getInPoint();
                mCaptionDuration = mCurCaption.getOutPoint() - mInPoint;
            }
        } else if (id == R.id.add_caption_btn) {
            mVideoFragment.stopEngine();
            mInPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            mCaptionDuration = 4 * Constants.NS_TIME_BASE;
            long duration = mTimeline.getDuration();
            long outPoint = mInPoint + mCaptionDuration;
            if (outPoint > duration) {
                mCaptionDuration = duration - mInPoint;
                if (mCaptionDuration <= Constants.NS_TIME_BASE) {
                    mCaptionDuration = Constants.NS_TIME_BASE;
                    mInPoint = duration - mCaptionDuration;
                    if (duration <= Constants.NS_TIME_BASE) {
                        mCaptionDuration = duration;
                        mInPoint = 0;
                    }
                }
            }
            if (mCurCaption != null) {
                mCurCaptionZVal = (int) mCurCaption.getZValue();
            }
            mVideoFragment.setDrawRectVisible(View.GONE);
            mCompCaptionAssetLayout.setVisibility(View.VISIBLE);
            //mViewPager
            //mCaptionStyleTab.setScrollPosition(0,0,true);
            mCaptionStyleTab.getTabAt(0).select();
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.ok_btn) {
            mStreamingContext.stop();
            removeTimeline();
            TimelineData.instance().setCompoundCaptionArray(mCaptionDataListClone);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            AppManager.getInstance().finishActivity();
        } else if (id == R.id.play_btn) {
            playVideo();
                /*  case R.id.comCaptionAssetDownload:
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreCompoundCaptionStyle);
                bundle.putInt("assetType", NvAsset.ASSET_COMPOUND_CAPTION);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, COMPOINDCAPTIONREQUESTLIST);
                break;*/
        } else if (id == R.id.comCaptionAssetFinish) {
            multiThumbnailSequenceViewSmooth(mInPoint);
            mCompCaptionAssetLayout.setVisibility(View.GONE);
            seekTimeline(mInPoint);
            if (mAddComCaption != null) {
                selectCaptionAndTimeSpan();
            } else {
                selectCaptionAndTimeSpanByZVal();
            }
            /*
             * 添加组合字幕对象置空，否则再次进入组合字幕列表会造成误删
             * The added subtitle object is left blank, otherwise entering the subtitle list again will cause deletion by mistake
             * */
            mAddComCaption = null;
            mCurCaptionZVal = 0;
            mCurSelectedPos = -1;
            /*
             * 取消列表字幕的选中状态
             * Uncheck list subtitles
             * */

            //notifyDataSetChanged(mCurSelectedPos);
            mModifyButton.setClickable(true);
            isNewCaptionUuidItemClick = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (data == null)
            return;
        switch (requestCode) {
            case REQUEST_CAPTION_STYLE:
                mCaptionDataListClone = BackupData.instance().getCompoundCaptionList();
                TimelineUtil.setCompoundCaption(mTimeline, mCaptionDataListClone);
                mTimelineEditor.deleteAllTimeSpan();
                mTimeSpanInfoList.clear();
                mCurCaption = null;
                addAllTimeSpan();
                long curSeekPos = BackupData.instance().getCurSeekTimelinePos();
                seekTimeline(curSeekPos);
                mCurCaptionZVal = BackupData.instance().getCaptionZVal();
                selectCaptionAndTimeSpanByZVal();
                break;
            case COMPOINDCAPTIONREQUESTLIST:
                initCompoundCaptionStyleList();
                mCaptionStyleFragment.setAssetInfolist(mComCaptionStyleList);
                mCaptionStyleFragment.setSelectedPos(mSelectedStylePos);
                mCaptionStyleFragment.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }


    /**
     * 获取指定captionId的选中位置
     * Get the selected position of the specified captionId
     *
     * @param captionUuid
     * @return
     */
    private int getCaptionSelectPos(String captionUuid) {
        if (TextUtils.isEmpty(captionUuid)) {
            return -1;
        }
        int capStyleCount = mComCaptionStyleList.size();
        for (int idx = 0; idx < capStyleCount; idx++) {
            AssetItem assetItem = mComCaptionStyleList.get(idx);
            if (assetItem == null) {
                continue;
            }
            NvAsset asset = assetItem.getAsset();
            if (asset == null) {
                continue;
            }
            if (!TextUtils.isEmpty(asset.uuid) && asset.uuid.equals(captionUuid)) {
                return idx;
            }
        }
        return -1;
    }


    private void selectCaptionAndTimeSpan() {
        selectCaption();
        updateComCaptionBoundingRect();
        if (mCurCaption != null) {
            selectTimeSpan();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
        }
    }

    private void selectCaptionAndTimeSpanByZVal() {
        selectCaptionByZVal(mCurCaptionZVal);
        updateComCaptionBoundingRect();
        if (mCurCaption != null) {
            selectTimeSpan();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
        }
    }

    /**
     * 更新组合字幕边框
     * Update the position of the combined subtitle border
     */
    private void updateComCaptionBoundingRect() {
        mVideoFragment.setCurCompoundCaption(mCurCaption);
        mVideoFragment.updateCompoundCaptionCoordinate(mCurCaption);
        if (mAddComCaption == null
                && mCompCaptionAssetLayout.getVisibility() == View.VISIBLE) {
            mVideoFragment.setDrawRectVisible(View.GONE);
        } else {
            mVideoFragment.changeCompoundCaptionRectVisible();
        }
    }

    /**
     * 平滑滑动缩略图到指定位置
     * Smoothly slide the thumbnail to the specified position
     *
     * @param stamp
     */
    private void multiThumbnailSequenceViewSmooth(long stamp) {
        if (mMultiSequenceView != null) {
            int x = Math.round((stamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
            mMultiSequenceView.smoothScrollTo(x, 0);
        }
    }

    private void selectCaptionByZVal(int curZVal) {
        if (mTimeline == null) {
            return;
        }
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineCompoundCaption> captionList = mTimeline.getCompoundCaptionsByTimelinePosition(curPos);
        int captionCount = captionList.size();
        if (captionCount > 0) {
            for (int i = 0; i < captionCount; i++) {
                int zVal = (int) captionList.get(i).getZValue();
                if (curZVal == zVal) {
                    mCurCaption = captionList.get(i);
                    break;
                }
            }
            if (mCurCaption != null) {
                mModifyButton.setVisibility(View.VISIBLE);
            }
        } else {
            mCurCaption = null;
            mModifyButton.setVisibility(View.GONE);
        }
    }

    private void notifyDataSetChanged(int position) {
        if (null != mCaptionStyleFragment) {
            mCurSelectedPos = position;
            mCaptionStyleFragment.setSelectedPos(position);
            mCaptionStyleFragment.notifyDataSetChanged();
        }
    }

    private void playVideo() {
        if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long endTime = mTimeline.getDuration();
            mVideoFragment.playVideo(startTime, endTime);
        } else {
            mVideoFragment.stopEngine();
        }
    }

    /**
     * 更新播放展示进度文本
     * Update the playback display progress text
     *
     * @param playTime
     */
    private void updatePlaytimeText(long playTime) {
        if (mTimeline != null) {
            long totalDuaration = mTimeline.getDuration();
            String strTotalDuration = TimeFormatUtil.formatUsToString1(totalDuaration);
            String strCurrentDuration = TimeFormatUtil.formatUsToString1(playTime);
            mShowCurrentDuration.setLength(0);
            mShowCurrentDuration.append(strCurrentDuration);
            mShowCurrentDuration.append("/");
            mShowCurrentDuration.append(strTotalDuration);
            mPlayCurTime.setText(mShowCurrentDuration.toString());
        }
    }

    private float getCurCaptionZVal() {
        float zVal = 0.0f;
        NvsTimelineCompoundCaption caption = mTimeline.getFirstCompoundCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal > zVal)
                zVal = tmpZVal;
            caption = mTimeline.getNextCaption(caption);
        }
        zVal += 1.0;
        return zVal;
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mOkBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                        selectCaptionAndTimeSpan();
                    }
                }, 100);
            }
        });
        /*
         * 设置组合字幕模式
         * Set the combined subtitle mode
         * */
        mVideoFragment.setEditMode(Constants.EDIT_MODE_COMPOUND_CAPTION);
        mVideoFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomRelativeLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    private void initMultiSequence() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null)
            return;
        int clipCount = videoTrack.getClipCount();
        ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDescsArray = new ArrayList<>();
        for (int index = 0; index < clipCount; ++index) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
            if (videoClip == null)
                continue;

            NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
            sequenceDescs.mediaFilePath = videoClip.getFilePath();
            sequenceDescs.trimIn = videoClip.getTrimIn();
            sequenceDescs.trimOut = videoClip.getTrimOut();
            sequenceDescs.inPoint = videoClip.getInPoint();
            sequenceDescs.outPoint = videoClip.getOutPoint();
            sequenceDescs.stillImageHint = false;
            sequenceDescsArray.add(sequenceDescs);
        }

        long duration = mTimeline.getDuration();
        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayBtnLayout.getLayoutParams();
        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        mTimelineEditor.setSequencLeftPadding(sequenceLeftPadding);
        mTimelineEditor.setSequencRightPadding(halfScreenWidth);
        mTimelineEditor.setTimeSpanLeftPadding(sequenceLeftPadding);
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration);
    }

    /*
     * 获取下载到手机路径下的素材，包括assets路径下自带的素材
     * Get the material downloaded to the mobile phone path, including the material that comes with the assets path
     * */
    private ArrayList<NvAsset> getAssetsDataList(int assetType) {
        return mAssetManager.getUsableAssets(assetType, NvAsset.AspectRatio_All, 0);
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    private void initCompoundCaptionStyleList() {
        ArrayList<NvAsset> usableAsset = getAssetsDataList(mCaptionStyleType);
        mComCaptionStyleList.clear();
        String jsonBundlePath = "compoundcaption/info.json";
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(this, jsonBundlePath);
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                for (NvAsset asset : usableAsset) {
                    if (asset == null || TextUtils.isEmpty(asset.uuid)) {
                        continue;
                    }
                    /*
                     * assets路径下的字幕样式包
                     * subtitle style package under assets path
                     * */
                    if (asset.isReserved && asset.uuid.equals(jsonFileInfo.getFxPackageId())) {
                        asset.name = isZh(this) ? jsonFileInfo.getName_Zh() : jsonFileInfo.getName();
                        asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                        StringBuilder coverPath = new StringBuilder("file:///android_asset/compoundcaption/");
                        if (jsonFileInfo.getImageName().endsWith("webp")) {
                            coverPath = new StringBuilder("asset://android_asset/compoundcaption/");
                        }
                        coverPath.append(jsonFileInfo.getFxPackageId());
                        coverPath.append(File.separator);
                        coverPath.append(jsonFileInfo.getImageName());
                        asset.coverUrl = coverPath.toString();
                        asset.fxFileName = jsonFileInfo.getFxFileName();
                    }
                }
            }
        }
        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : usableAsset) {
            if (asset == null || TextUtils.isEmpty(asset.uuid)) {
                /*
                 * 制作比例不适配，不加载
                 * Production proportions do not fit, do not load
                 * */
                continue;
            }
            if (asset.fxFileName == null) {
                asset.fxFileName = PathNameUtil.getPathNameWithSuffix(asset.localDirPath);
            }
            String assetPackageFilePath = asset.localDirPath;
            if (TextUtils.isEmpty(asset.localDirPath)) {
                assetPackageFilePath = "assets:/compoundcaption/" + asset.fxFileName;
            }
            String packageID = installCaptionPackage(assetPackageFilePath);
            if (packageID.isEmpty()) {
                continue;
            }
            asset.uuid = packageID;
            AssetItem assetItem = new AssetItem();
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            mComCaptionStyleList.add(assetItem);
        }
    }

    private String installCaptionPackage(String assetPackageFilePath) {
        return PackageManagerUtil.installAssetPackage(assetPackageFilePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION);
    }

    /*
     * 添加字幕
     * Add subtitles
     * */
    private void addCaption(String captionUuid) {
        isNewCaptionUuidItemClick = true;
        if (mAddComCaption != null) {
            removeCaption(mAddComCaption);
            mAddComCaption = null;
        }
        mAddComCaption = mTimeline.addCompoundCaption(mInPoint, mCaptionDuration, captionUuid);
        if (mAddComCaption == null) {
            Logger.e(TAG, "addCaption is failed");
            return;
        }
        float zVal = getCurCaptionZVal();
        mAddComCaption.setZValue(zVal);
        long outPoint = mInPoint + mCaptionDuration;
        NvsTimelineTimeSpan timeSpan = addTimeSpan(mInPoint, outPoint);
        if (timeSpan == null) {
            Logger.e(TAG, "addCaption time span is failed ");
            return;
        }
        mTimeSpanInfoList.add(new CaptionTimeSpanInfo(mAddComCaption, timeSpan));
        mModifyButton.setVisibility(View.VISIBLE);
        CompoundCaptionInfo captionInfo = Util.saveCompoundCaptionData(mAddComCaption);
        if (captionInfo != null) {
            captionInfo.setCaptionStyleUuid(captionUuid);
            mCaptionDataListClone.add(captionInfo);
        }
        /*
         * 播放视频
         * Play video
         * */
        mCurCaptionZVal = (int) mAddComCaption.getZValue();
        mVideoFragment.playVideo(mInPoint, outPoint);
        mVideoFragment.setDrawRectVisible(View.GONE);
    }

    private void removeCaption(NvsTimelineCompoundCaption delCaption) {
        deleteCurCaptionTimeSpan(delCaption);
        int zVal = (int) delCaption.getZValue();
        int index = getCaptionIndex(zVal);
        if (index >= 0) {
            mCaptionDataListClone.remove(index);
        }
        mTimeline.removeCompoundCaption(delCaption);
    }

    private NvsTimelineTimeSpan addTimeSpan(long inPoint, long outPoint) {
        /*
         * warning: 使用addTimeSpanExt()之前必须设置setTimeSpanType()
         * warning: setTimeSpanType () must be set before using addTimeSpanExt ()
         * */
        mTimelineEditor.setTimeSpanType("NvsTimelineTimeSpan");
        NvsTimelineTimeSpan timelineTimeSpan = mTimelineEditor.addTimeSpan(inPoint, outPoint);
        if (timelineTimeSpan == null) {
            Log.e(TAG, "addTimeSpan is failed");
            return null;
        }
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimInChangeListener() {
            @Override
            public void onTrimInChange(long timeStamp, boolean isDragEnd) {
                seekTimeline(timeStamp);
                updatePlaytimeText(timeStamp);
                mVideoFragment.changeCaptionRectVisible();
                if (isDragEnd && mCurCaption != null) {
                    mCurCaption.changeInPoint(timeStamp);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0)
                        mCaptionDataListClone.get(index).setInPoint(timeStamp);

                    seekMultiThumbnailSequenceView();
                }
            }
        });
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimOutChangeListener() {
            @Override
            public void onTrimOutChange(long timeStamp, boolean isDragEnd) {
                /*
                 * outPoint是开区间，seekTimeline时，需要往前平移一帧即0.04秒，转换成微秒即40000微秒
                 * outPoint is an open interval. In seekTimeline, you need to pan one frame, that is, 0.04 seconds, and convert it to microseconds, that is, 40,000 microseconds.
                 * */
                seekTimeline(timeStamp - 40000);
                updatePlaytimeText(timeStamp);
                mVideoFragment.changeCaptionRectVisible();
                if (isDragEnd && mCurCaption != null) {
                    Logger.e(TAG, "TrimInChange5454->" + timeStamp);
                    mCurCaption.changeOutPoint(timeStamp);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setOutPoint(timeStamp);
                    }

                    seekMultiThumbnailSequenceView();
                }
            }
        });

        return timelineTimeSpan;
    }

    private void seekMultiThumbnailSequenceView() {
        if (mMultiSequenceView != null) {
            long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long duration = mTimeline.getDuration();
            mMultiSequenceView.scrollTo(Math.round(((float) curPos) / (float) duration * mTimelineEditor.getSequenceWidth()), 0);
        }
    }

    private void addAllTimeSpan() {
        NvsTimelineCompoundCaption caption = mTimeline.getFirstCompoundCaption();
        while (caption != null) {
            long inPoint = caption.getInPoint();
            long outPoint = caption.getOutPoint();
            NvsTimelineTimeSpan timeSpan = addTimeSpan(inPoint, outPoint);
            if (timeSpan != null) {
                CaptionTimeSpanInfo timeSpanInfo = new CaptionTimeSpanInfo(caption, timeSpan);
                mTimeSpanInfoList.add(timeSpanInfo);
            }
            caption = mTimeline.getNextCaption(caption);
        }
    }

    private void selectCaption() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineCompoundCaption> captionList = mTimeline.getCompoundCaptionsByTimelinePosition(curPos);
        Logger.e(TAG, "captionList => " + captionList.size());
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
            mModifyButton.setVisibility(View.VISIBLE);
        } else {
            mCurCaption = null;
            mModifyButton.setVisibility(View.GONE);
        }
    }

    private void selectTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mCurCaption != null &&
                    mTimeSpanInfoList.get(i).mCaption == mCurCaption) {
                mTimelineEditor.selectTimeSpan(mTimeSpanInfoList.get(i).mTimeSpan);
                break;
            }
        }
    }

    private void deleteCurCaptionTimeSpan(NvsTimelineCompoundCaption delCaption) {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mTimeSpanInfoList.get(i).mCaption == delCaption) {
                mTimelineEditor.deleteSelectedTimeSpan(mTimeSpanInfoList.get(i).mTimeSpan);
                mTimeSpanInfoList.remove(i);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        mStreamingContext.stop();
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
        m_handler.removeCallbacksAndMessages(null);
    }

    private class CaptionTimeSpanInfo {
        public NvsTimelineCompoundCaption mCaption;
        public NvsTimelineTimeSpan mTimeSpan;

        public CaptionTimeSpanInfo(NvsTimelineCompoundCaption caption, NvsTimelineTimeSpan timeSpan) {
            this.mCaption = caption;
            this.mTimeSpan = timeSpan;
        }
    }

    private int getCaptionIndex(int curZValue) {
        int index = -1;
        int count = mCaptionDataListClone.size();
        for (int i = 0; i < count; ++i) {
            int zVal = mCaptionDataListClone.get(i).getCaptionZVal();
            if (curZValue == zVal) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mModifyButton.setClickable(true);
    }
}
