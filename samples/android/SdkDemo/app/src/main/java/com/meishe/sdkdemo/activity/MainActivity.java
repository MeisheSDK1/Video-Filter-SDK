package com.meishe.sdkdemo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.model.BaseMvpActivity;
import com.meishe.base.pop.TipsPop;
import com.meishe.base.utils.BarUtils;
import com.meishe.base.view.MSLiveWindow;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.MeicamContextWrap;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.activity.view.MainView;
import com.meishe.sdkdemo.base.BaseFragmentPagerAdapter;
import com.meishe.sdkdemo.boomrang.BoomRangActivity;
import com.meishe.sdkdemo.capture.CaptureActivity;
import com.meishe.sdkdemo.capturescene.CaptureSceneActivity;
import com.meishe.sdkdemo.douvideo.DouVideoCaptureActivity;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.watermark.SingleClickActivity;
import com.meishe.sdkdemo.feedback.FeedBackActivity;
import com.meishe.sdkdemo.glitter.GlitterEffectActivity;
import com.meishe.sdkdemo.main.MainWebViewActivity;
import com.meishe.sdkdemo.main.OnItemClickListener;
import com.meishe.sdkdemo.main.SpannerViewpagerAdapter;
import com.meishe.sdkdemo.main.bean.AdBeansFormUrl;
import com.meishe.sdkdemo.mimodemo.MimoActivity;
import com.meishe.sdkdemo.monitor.LogPrinterListener;
import com.meishe.sdkdemo.monitor.MSPrinter;
import com.meishe.sdkdemo.musicLyrics.MultiVideoSelectActivity;
import com.meishe.sdkdemo.particle.ParticleCaptureActivity;
import com.meishe.sdkdemo.photoalbum.PhotoAlbumActivity;
import com.meishe.sdkdemo.presenter.MainPresenter;
import com.meishe.sdkdemo.selectmedia.SelectMediaActivity;
import com.meishe.sdkdemo.superzoom.SuperZoomActivity;
import com.meishe.sdkdemo.themeshoot.ThemeSelectActivity;
import com.meishe.sdkdemo.urledit.UrlMaterialActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MediaConstant;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.permission.PermissionsActivity;
import com.meishe.sdkdemo.utils.permission.PermissionsChecker;
import com.meishe.utils.SpUtil;
import com.meishe.utils.StatusBarUtils;
import com.meishe.utils.SystemUtils;
import com.meishe.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_MS;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : gexinyu
 * @CreateDate : 2018-05-24 下午7:17
 * @Description :
 * 安装特效包会进行美摄授权检测，一个特效包会对应一个授权lic文件，美摄sdk demo 使用的是全量授权，所以安装特效包的时候，传递特效包授权路径的参数缺省了
 * The installation of special effects package will be subject to the authorization detection of American photography.
 * A special effects package will correspond to an authorized LIC file. The American Photography SDK demo uses full authorization. Therefore, when installing special effects package,
 * the parameters of passing the authorization path of special effects package are default
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MainActivity extends BaseMvpActivity<MainPresenter> implements View.OnClickListener
        , OnItemClickListener, LogPrinterListener, MainView {
    private static final String TAG = "MainActivity";
    public static boolean isUrlResource = false;
    public static final int REQUEST_CAMERA_PERMISSION_CODE = 200;
    public static final int INIT_ARSCENE_COMPLETE_CODE = 201;
    public static final int INIT_ARSCENE_FAILURE_CODE = 202;
    public static final int AD_SPANNER_CHANGE_CODE = 203;
    private static final int PERMISSIONS_REQUEST_CODE = 0x100;
    private ImageView mIvSetting;
    private ImageView mIvFeedBack;
    private RelativeLayout layoutVideoCapture;
    private RelativeLayout layoutVideoEdit;
    private ViewPager mainViewPager;
    private RadioGroup radioGroup;
    private ViewPager mBannerViewPager;
    private SpannerViewpagerAdapter mSpannerViewpagerAdapter;
    private View clickedView = null;
    /**
     * 人脸初始化完成的标识
     * Face initialization completed logo
     */
    private boolean arSceneFinished = false;
    /**
     * 记录人脸模块正在初始化
     * Recording face module is initializing
     */
    private boolean initARSceneing = true;
    /**
     * 防止页面重复点击标识
     * Prevent pages from repeatedly clicking on logos
     */
    private boolean isClickRepeat = false;

    private final MainActivityHandler mHandler = new MainActivityHandler(this);
    private SpUtil spUtil;
    private Runnable mAdRunnable;
    private List<AdBeansFormUrl.AdInfo> mAdInfos = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();

    /**
     * 权限相关
     * Permissions are related
     */
    private PermissionsChecker mPermissionsChecker;

    @SuppressLint("HandlerLeak")
    class MainActivityHandler extends Handler {
        WeakReference<MainActivity> mWeakReference;

        public MainActivityHandler(MainActivity mainActivityContext) {
            mWeakReference = new WeakReference<>(mainActivityContext);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            final MainActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case INIT_ARSCENE_COMPLETE_CODE:
                        /*
                         *  初始化ARScene 完成
                         * Initialization of ARScene completed
                         * */
                        arSceneFinished = true;
                        initARSceneing = false;
                        break;
                    case INIT_ARSCENE_FAILURE_CODE:
                        /*
                         *  初始化ARScene 失败
                         * Initializing ARScene failed
                         * */
                        arSceneFinished = false;
                        initARSceneing = false;
                        break;
                    case AD_SPANNER_CHANGE_CODE:
                        /*
                         * 广告切换
                         * Advertising switch
                         * */
                        if ((mBannerViewPager != null) && (mAdInfos != null) && (mAdInfos.size() > 1)) {
                            mBannerViewPager.setCurrentItem(mBannerViewPager.getCurrentItem() + 1);
                        }
                        break;
                    default:
                        break;

                }
            }
        }
    }


    @Override
    protected int bindLayout() {
        StatusBarUtils.setTransparent(this);
        StatusBarUtils.setTextDark(this, true);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return R.layout.activity_main;
        }
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        AppManager.getInstance().addActivity(this);
        spUtil = SpUtil.getInstance(this);
        mPermissionsChecker = new PermissionsChecker(this);
    }

    private int count = 0;

    @Override
    protected void initView() {
        ImageView mMainLogo = findViewById(R.id.main_logo);
        mIvSetting = findViewById(R.id.iv_main_setting);
        mIvFeedBack = findViewById(R.id.iv_main_feedback);
        BarUtils.addMarginTopEqualStatusBarHeight(mMainLogo);
        BarUtils.addMarginTopEqualStatusBarHeight(mIvSetting);
        BarUtils.addMarginTopEqualStatusBarHeight(mIvFeedBack);
        layoutVideoCapture = findViewById(R.id.layout_video_capture);
        layoutVideoEdit = findViewById(R.id.layout_video_edit);
        mainViewPager = findViewById(R.id.main_viewPager);
        radioGroup = findViewById(R.id.main_radioGroup);
        TextView mainVersionNumber = findViewById(R.id.main_versionNumber);
        mBannerViewPager = findViewById(R.id.banner_viewpager);
        mainVersionNumber.setText(String.format(getResources().getString(R.string.versionNumber), MeicamContextWrap.getSdkVersion()));
        initSpannerView();
        initListener();
        mainVersionNumber.setOnClickListener(v -> {
            count++;
            if (count == 10) {
                count = 0;
                boolean noLic = spUtil.getBoolean("NO_LIC", false);
                if (!noLic) {
                    spUtil.putBoolean("NO_LIC", true);
                    ToastUtil.showToast(getApplicationContext(), "开启不检查授权模式");
                } else {
                    spUtil.putBoolean("NO_LIC", false);
                    ToastUtil.showToast(getApplicationContext(), "开启需要检查授权模式");
                }
            }
        });
    }

    @Override
    protected void requestData() {
        super.requestData();
        MeicamContextWrap.getInstance().setContext(this.getApplicationContext());
        mPresenter.initParameterSetting();
        mPresenter.packageFragmentAndView(this);
        boolean isAgreePrivacy = spUtil.getBoolean(Constants.KEY_AGREE_PRIVACY, false);
        if (!isAgreePrivacy) {
            mPresenter.showPrivacyDialog(this);
            return;
        }
        mPresenter.setDeveloperOptions(this);
        initAuxiliaryData();
    }

    /**
     * 初始化轮播图
     * Initialize the carousel map
     */
    private void initSpannerView() {
        SystemUtils.isZh(this);
        mSpannerViewpagerAdapter = new SpannerViewpagerAdapter(this, mAdInfos);
        mBannerViewPager.setAdapter(mSpannerViewpagerAdapter);
        int item = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % (mAdInfos.size() == 0 ? 1 : mAdInfos.size()));
        mBannerViewPager.setCurrentItem(item);
        mAdRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(AD_SPANNER_CHANGE_CODE);
                mHandler.postDelayed(this, 5000);
            }
        };
    }

    /**
     * 初始化首页功能视图碎片
     * Initialize the Home Features View fragment
     */
    private void initFragmentAndView() {
        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, null);
        mainViewPager.setAdapter(fragmentPagerAdapter);
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setRadioButtonState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initListener() {
        setSpannerListener();
        mIvSetting.setOnClickListener(this);
        mIvFeedBack.setOnClickListener(this);
        layoutVideoCapture.setOnClickListener(this);
        layoutVideoEdit.setOnClickListener(this);
    }

    private void initAuxiliaryData() {
        //获取轮播图数据
//        Get carousel data
        mPresenter.getSpannerViewData(this);
        if (!hasAllPermission()) {
            return;
        }
        mPresenter.copyResourceToSdCard(this);
        mPresenter.checkAuthorization(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClickRepeat = false;
        ParameterSettingValues parameterValues = ParameterSettingValues.instance();
        if (parameterValues != null) {
            //!!!统一处理liveWindow的显示模式
//            Unified handling of liveWindow display modes
            MSLiveWindow.setLiveModel(parameterValues.getLiveWindowModel());
            NvsStreamingContext mStreamingContext = NvsStreamingContext.getInstance();
            if (mStreamingContext != null) {
                mStreamingContext.setColorGainForSDRToHDR(parameterValues.getColorGain());
            }
        }
    }

    @Override
    public void onInitArSceneEffect() {
        initArSceneEffect();
    }

    /**
     * 初始化人脸相关
     * Initialize face correlation
     */
    private void initArSceneEffect() {
        MeicamContextWrap.getInstance().initMeicamModel(this
                , arSceneFinished, initSuccess -> {
                    if (initSuccess) {
                        mHandler.sendEmptyMessage(INIT_ARSCENE_COMPLETE_CODE);
                    } else {
                        mHandler.sendEmptyMessage(INIT_ARSCENE_FAILURE_CODE);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (isClickRepeat) {
            return;
        }
        isClickRepeat = true;
        int id = view.getId();
        /*
         * 设置
         * Set up
         * */
        if (id == R.id.iv_main_setting) {
            AppManager.getInstance().jumpActivity(this, SettingActivity.class, null);
            return;
            /*
             * 反馈
             * Feedback
             * */
        } else if (id == R.id.iv_main_feedback) {
            AppManager.getInstance().jumpActivity(this, FeedBackActivity.class, null);
            return;
        }
        /*
         * 没有权限，则请求权限
         * No permission, request permission
         * */
        if (!hasAllPermission()) {
            clickedView = view;
            checkPermissions();
        } else {
            doClick(view);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void doClick(View view) {
        if (view == null) {
            return;
        }
        if (!arSceneFinished && initARSceneing) {
            mPresenter.checkAuthorization(this);
            isClickRepeat = false;
            ToastUtil.showToast(MainActivity.this, R.string.initArsence);
            return;
        }
        isUrlResource = false;
        int id = view.getId();//Shoot
        if (id == R.id.layout_video_capture) {
            if (!initARSceneing) {
                Bundle captureBundle = new Bundle();
                captureBundle.putBoolean("initArScene", arSceneFinished);
                AppManager.getInstance().jumpActivity(this, CaptureActivity.class, captureBundle);
                return;
            }
            isClickRepeat = false;
            ToastUtil.showToast(MainActivity.this, R.string.initArsence);
            //Edit
        } else if (id == R.id.layout_video_edit) {
            Bundle editBundle = new Bundle();
            editBundle.putInt("visitMethod", Constants.FROMMAINACTIVITYTOVISIT);
            editBundle.putInt("limitMediaCount", -1);
            AppManager.getInstance().jumpActivity(this, SelectMediaActivity.class, editBundle);
        } else {
            Bundle editBundle;
            String tag = (String) view.getTag();
            if (tag.equals(getResources().getString(R.string.douVideoEffects))) {
                if (!initARSceneing) {
                    Bundle douVideoBundle = new Bundle();
                    douVideoBundle.putBoolean("initArScene", arSceneFinished);
                    douVideoBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_STRENGTH, 75);
                    if (arSceneFinished) {
                        douVideoBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_CHEEK, 150);
                        douVideoBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_EYE, 150);
                    }
                    AppManager.getInstance().jumpActivity(this, DouVideoCaptureActivity.class, douVideoBundle);
                } else {
                    isClickRepeat = false;
                    ToastUtil.showToast(MainActivity.this, R.string.initArsence);
                }
            } else if (tag.equals(getResources().getString(R.string.particleEffects))) {
                AppManager.getInstance().jumpActivity(this, ParticleCaptureActivity.class, null);
            } else if (tag.equals(getResources().getString(R.string.captureScene))) {
                AppManager.getInstance().jumpActivity(this, CaptureSceneActivity.class, null);
            } else if (tag.equals(getResources().getString(R.string.picInPic))) {
                Bundle pipBundle = new Bundle();
                pipBundle.putInt("visitMethod", Constants.FROMPICINPICACTIVITYTOVISIT);
                /*
                 * 2表示选择两个素材
                 * 2 means select two materials
                 * */
                pipBundle.putInt("limitMediaCount", 2);
                AppManager.getInstance().jumpActivity(this, SelectMediaActivity.class, pipBundle);
            } else if (tag.equals(getResources().getString(R.string.makingCover))) {
                Bundle makeCoverBundle = new Bundle();
                makeCoverBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_IMAGE_FROM_MAKE_COVER);
                AppManager.getInstance().jumpActivity(this, SingleClickActivity.class, makeCoverBundle);
            } else if (tag.equals(getResources().getString(R.string.flipSubtitles))) {
                Bundle flipBundle = new Bundle();
                flipBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_FLIP_CAPTION);
                flipBundle.putInt("limitMediaCount", -1);
                flipBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, flipBundle);
            } else if (tag.equals(getResources().getString(R.string.urlEditing))) {
                isUrlResource = true;
                UrlMaterialActivity.nativeUrlMaterial(this, -1, false, Constants.URL_MATERIAL_VIDEO, true);
            } else if (tag.equals(getResources().getString(R.string.musicLyrics))) {
                Bundle musicBundle = new Bundle();
                musicBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_MUSIC_LYRICS);
                musicBundle.putInt("limitMediaCount", -1);
                musicBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, musicBundle);
            } else if (tag.equals(getResources().getString(R.string.boomRang))) {
                AppManager.getInstance().jumpActivity(this, BoomRangActivity.class);
            } else if (tag.equals(getResources().getString(R.string.pushMirrorFilm))) {
                if (!initARSceneing) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        AppManager.getInstance().jumpActivity(this, SuperZoomActivity.class);
                    } else {
                        String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                        Util.showDialog(MainActivity.this, tipsInfo[0], getString(R.string.versionBelowTip));
                    }
                } else {
                    isClickRepeat = false;
                    ToastUtil.showToast(MainActivity.this, R.string.initArsence);
                }
            } else if (tag.equals(getResources().getString(R.string.photosAlbum))) {
                AppManager.getInstance().jumpActivity(this, PhotoAlbumActivity.class, null);
            } else if (tag.equals(getResources().getString(R.string.flashEffect))) {
                AppManager.getInstance().jumpActivity(this, GlitterEffectActivity.class, null);
            } else if (tag.equals(getResources().getString(R.string.memo))) {
                AppManager.getInstance().jumpActivity(this, MimoActivity.class, null);
            } else if (tag.equals(getResources().getString(R.string.theme_shoot))) {
                AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), ThemeSelectActivity.class, null);
            } else if (tag.equals(getResources().getString(R.string.cutSame))) {
                AppManager.getInstance().jumpActivity(this, CutSameActivity.class);
            } else if (tag.equals(getResources().getString(R.string.sequence_nesting))) {
                //序列嵌套 Sequence nesting
                Bundle sequenceBundle = new Bundle();
                sequenceBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_SEQUENCE_NESTING);
                sequenceBundle.putInt("limitMediaCount", -1);
                sequenceBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, sequenceBundle);
            } else if (tag.equals(getResources().getString(R.string.audio_equalizer))) {
                //音频均衡器 Audio equalizer
                Bundle audioBundle = new Bundle();
                audioBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.FROM_MAIN_ACTIVITY_TO_AUDIO_EQUALIZER);
                audioBundle.putInt("limitMediaCount", -1);
                audioBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, audioBundle);
            } else if (tag.equals(getResources().getString(R.string.quick_splicing))) {
                //快速拼接 Fast stitching
                editBundle = new Bundle();
                editBundle.putInt("visitMethod", Constants.FROM_QUICK_SPLICING_ACTIVITY);
                editBundle.putInt("limitMediaCount", -1);
                editBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                AppManager.getInstance().jumpActivity(this, SelectMediaActivity.class, editBundle);
            } else {
                String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                Util.showDialog(MainActivity.this, tipsInfo[0], tipsInfo[1], tipsInfo[2]);
            }
        }
    }


    @Override
    public void onPrivacyPolicyAgree(boolean isAgree) {
        spUtil.putBoolean(Constants.KEY_AGREE_PRIVACY, isAgree);
        if (!isAgree) {
            AppManager.getInstance().finishActivity();
        } else {
            MSApplication.getInstance().init();
            initAuxiliaryData();
        }
    }

    @Override
    public void onPrivacyPolicyWeb(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("URL", url);
        AppManager.getInstance().jumpActivity(this, MainWebViewActivity.class, bundle);
    }

    @Override
    public void getSpannerViewData(List<AdBeansFormUrl.AdInfo> adInfos) {
        runOnUiThread(() -> {
            mAdInfos = adInfos;
            if (mSpannerViewpagerAdapter != null) {
                mSpannerViewpagerAdapter.setAdapterData(mAdInfos);
                if ((mAdRunnable != null) && (mAdInfos.size() > 1)) {
                    mHandler.removeCallbacks(mAdRunnable);
                    mHandler.postDelayed(mAdRunnable, 5000);
                }
            }
        });
    }

    @Override
    public void onPackageFragmentView(List<Fragment> mFragmentList, Map<Integer, List<String>> listMap) {
        this.mFragmentList = mFragmentList;
        initFragmentAndView();
        for (int i = 0; i < listMap.size(); i++) {
            addRadioButton(i);
        }
    }

    /**
     * 设置轮播图监听
     * Set up carousel monitoring
     */
    private void setSpannerListener() {
        if (mSpannerViewpagerAdapter != null) {
            mSpannerViewpagerAdapter.setSpannerClickCallback(new SpannerViewpagerAdapter.SpannerClickCallback() {
                @Override
                public void spannerClick(int position, AdBeansFormUrl.AdInfo adInfo) {
                    if (Util.isFastClick()) {
                        return;
                    }
                    boolean notNull = (adInfo != null) && (!TextUtils.isEmpty(adInfo.getAdvertisementUrl())) && (!TextUtils.isEmpty(adInfo.getAdvertisementUrlEn()));
                    if (notNull) {
                        Bundle bundle = new Bundle();
                        bundle.putString("URL", SystemUtils.isZh(getApplicationContext()) ? adInfo.getAdvertisementUrl() : adInfo.getAdvertisementUrlEn());
                        Intent intent = new Intent(MainActivity.this, MainWebViewActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent, bundle);
                    }
                }
            });
        }
    }

    private void addRadioButton(int i) {
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ScreenUtils.dip2px(this, 5), ScreenUtils.dip2px(this, 5));
        lp.setMargins(0, 0, ScreenUtils.dip2px(this, 5), 0);
        radioGroup.addView(initRadioButton(i), lp);
    }

    private RadioButton initRadioButton(int i) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setId(getResources().getIdentifier("main_radioButton" + i, "id", getPackageName()));
        radioButton.setBackground(getResources().getDrawable(R.drawable.activity_main_checkbox_background));
        radioButton.setButtonDrawable(null);
        radioButton.setChecked(i == 0);
        return radioButton;
    }

    private void setRadioButtonState(int position) {
        RadioButton radioButton = findViewById(getResources().getIdentifier("main_radioButton" + position, "id", getPackageName()));
        radioButton.setChecked(true);
    }

    @Override
    public void startMonitor() {
        Looper mainLooper = getMainLooper();
        mainLooper.setMessageLogging(new MSPrinter(this));
    }

    @Override
    public void stopMonitor() {
        Looper mainLooper = getMainLooper();
        mainLooper.setMessageLogging(null);
    }

    @Override
    public void onEndLoop(String info, int level) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hasAllPermission()) {
            Util.clearRecordAudioData();
        }
        MeicamContextWrap.getInstance().destory();
        // 退出清理 Exit Cleanup
        if (NvsStreamingContext.hasARModule() == HUMAN_AI_TYPE_MS) {
            NvsStreamingContext.closeHumanDetection();
        }
        TimelineData.instance().clear();
        BackupData.instance().clear();
        stopMonitor();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        MobclickAgent.onKillProcess(this);
    }

    /**
     * 判断是否已有相关权限
     *
     * @return boolean
     */
    public boolean hasAllPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mPermissionsChecker == null) {
            mPermissionsChecker = new PermissionsChecker(this);
        }
        return !mPermissionsChecker.lacksPermissions(Util.getWriteReadPermission());
    }

    /**
     * 申请相关权限
     * Request permissions
     */
    public void checkPermissions() {
        if (mPermissionsChecker == null) {
            mPermissionsChecker = new PermissionsChecker(this);
        }
        List<String> permissionList = Util.getWriteReadPermission();
        mPermissionsChecker.checkPermission(permissionList);
        String[] permissions = new String[permissionList.size()];
        permissionList.toArray(permissions);
        if (!permissionList.isEmpty()) {
            PermissionsActivity.startActivityForResult(this, PERMISSIONS_REQUEST_CODE, permissions);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            switch (resultCode) {
                case PermissionsActivity.PERMISSIONS_DENIED:
                    break;
                case PermissionsActivity.PERMISSIONS_GRANTED:
                    Logger.e(TAG, "hasPermission: 所有权限都有了");
                    mPresenter.checkAuthorization(this);
                    break;
                case PermissionsActivity.PERMISSIONS_No_PROMPT:
                    setPermissionsSetting();
                    break;
                default:
                    break;
            }
        }
    }

    private void setPermissionsSetting() {
        if (null == mPermissionsChecker) {
            return;
        }
        if (!mPermissionsChecker.lacksPermissions(Util.getWriteReadPermission())) {
            return;
        }
        String title = String.format(getResources().getString(R.string.permission_title), getResources().getString(R.string.permission_storage));
        String content = String.format(getResources().getString(R.string.permission_message)
                , getResources().getString(R.string.permission_storage)
                , getResources().getString(R.string.permission_storage));
        TipsPop mPermissionsPop = TipsPop.create(this, true)
                .setDefaultTipsTitle(title)
                .setDefaultTipsContent(content)
                .setTipsDefaultConfirm(getResources().getString(R.string.permission_set));
        mPermissionsPop.setOnTipsPopListener(new TipsPop.OnTipsPopListener() {
            @Override
            public void onTipsCancel() {

            }

            @Override
            public void onTipsConfirm() {
                Util.startAppSettings(MainActivity.this);
                mPermissionsPop.dismiss();
            }
        });
        mPermissionsPop.show();
    }

}
