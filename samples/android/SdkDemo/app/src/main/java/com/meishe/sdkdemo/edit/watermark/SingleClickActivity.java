package com.meishe.sdkdemo.edit.watermark;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.cafconvertor.NvCafCreator;
import com.meishe.cafconvertor.NvRational;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.douvideo.DouVideoTrimActivity;
import com.meishe.sdkdemo.edit.animatesticker.customsticker.CustomAnimateStickerActivity;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.makecover.MakeCoverActivity;
import com.meishe.sdkdemo.selectmedia.bean.MediaData;
import com.meishe.sdkdemo.selectmedia.fragment.MediaFragment;
import com.meishe.sdkdemo.selectmedia.interfaces.OnTotalNumChangeForActivity;
import com.meishe.sdkdemo.selectmedia.view.CustomPopWindow;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MediaConstant;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.PackageManagerUtil;
import com.meishe.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_CANCEL;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_CONFIRM;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_TYPE;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_PATH;

public class SingleClickActivity extends BaseActivity implements OnTotalNumChangeForActivity, CustomPopWindow.OnViewClickListener, PopupWindow.OnDismissListener {
    private final String TAG = "SingleClickActivity";
    private static final int GIFTOCAFCONVERTFINISH = 101;
    private CustomTitleBar m_titleBar;
    private TextView sigleTvStartEdit;
    private TextView sigleTvCancel;
    private LinearLayout mGifToCafLayout;
    private List<MediaData> mediaDataList;
    private int fromWhat = Constants.SELECT_IMAGE_FROM_WATER_MARK;

    /**
     * 画中画专用
     * Special for picture in picture
     */
    private int mPicInPicVideoIndex = -1;

    private SingleClickActivity.SingleClickHandler m_handler = new SingleClickActivity.SingleClickHandler(this);

    static class SingleClickHandler extends Handler {
        WeakReference<SingleClickActivity> mWeakReference;

        public SingleClickHandler(SingleClickActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SingleClickActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case GIFTOCAFCONVERTFINISH:
                        Bundle bundleData = msg.getData();
                        if (bundleData == null) {
                            return;
                        }
                        String imageSrcFilePath = bundleData.getString("imageSrcFilePath");
                        String targetCafPath = bundleData.getString("targetCafPath");
                        boolean retResult = bundleData.getBoolean("retResult");
                        if (retResult) {
                            activity.addCafStickerToInfoList(imageSrcFilePath, targetCafPath);
                        }
                        activity.activityFinish();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected int initRootView() {
        return R.layout.activity_single_click;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initViews() {
        m_titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        sigleTvStartEdit = (TextView) findViewById(R.id.sigle_tv_startEdit);
        sigleTvCancel = (TextView) findViewById(R.id.sigle_tv_cancel);
        sigleTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromWhat == Constants.SELECT_PICTURE_FROM_BACKGROUND_SEG) {
                    Intent intent = new Intent();
                    intent.putExtra(SINGLE_PICTURE_CLICK_TYPE, SINGLE_PICTURE_CLICK_CANCEL);
                    setResult(RESULT_OK, intent);
                    AppManager.getInstance().finishActivity();
                }
            }
        });

        sigleTvStartEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sigleTvStartEdit.setClickable(false);
                if (fromWhat == Constants.SELECT_IMAGE_FROM_WATER_MARK) {
                    Intent intent = new Intent();
                    intent.putExtra(SINGLE_PICTURE_PATH, mediaDataList.get(0).getPath());
                    setResult(RESULT_OK, intent);
                    AppManager.getInstance().finishActivity();
                } else if (fromWhat == Constants.SELECT_PICTURE_FROM_BACKGROUND_SEG) {
                    if (mediaDataList == null || mediaDataList.size() == 0) {
                        ToastUtil.showToast(mContext, getResources().getString(R.string.please_select_one_material));
                        sigleTvStartEdit.setClickable(true);
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(SINGLE_PICTURE_PATH, mediaDataList.get(0).getPath());
                    intent.putExtra(SINGLE_PICTURE_CLICK_TYPE, SINGLE_PICTURE_CLICK_CONFIRM);
                    setResult(RESULT_OK, intent);
                    AppManager.getInstance().finishActivity();
                } else if (fromWhat == Constants.SELECT_IMAGE_FROM_MAKE_COVER) {
                    new CustomPopWindow.PopupWindowBuilder(SingleClickActivity.this)
                            .setView(R.layout.pop_select_makesize)
                            .setViewClickListener(SingleClickActivity.this)
                            .setOnDissmissListener(SingleClickActivity.this)
                            .create()
                            .showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
                } else if (fromWhat == Constants.SELECT_IMAGE_FROM_CUSTOM_STICKER) {
                    final String imageSrcFilePath = mediaDataList.get(0).getThumbPath();
                    int lastPointPos = imageSrcFilePath.lastIndexOf(".");
                    String fileSuffixName = imageSrcFilePath.substring(lastPointPos).toLowerCase();
                    if (fileSuffixName.equals(".gif")) {
                        /*
                         * 转圈，等待gif转caf
                         * Turn, wait for GIF to turn caf
                         * */
                        mGifToCafLayout.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                gifToCafConvert(imageSrcFilePath);
                            }
                        }).start();
                    } else {//Image
                        Bundle bundle = new Bundle();
                        bundle.putString("imageSrcFilePath", imageSrcFilePath);
                        AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), CustomAnimateStickerActivity.class, bundle);
                    }
                } else if (fromWhat == Constants.SELECT_VIDEO_FROM_DOUVIDEOCAPTURE) {
                    long duration = 1000 * mediaDataList.get(0).getDuration();
                    long minDuration = 3 * Constants.NS_TIME_BASE;
                    if (duration < minDuration) {
                        String[] selectTips = getResources().getStringArray(R.array.select_video_min_duration_tips);
                        Util.showDialog(SingleClickActivity.this, selectTips[0], selectTips[1]);
                        sigleTvStartEdit.setClickable(true);
                        return;
                    }
                    int makeRatio = NvAsset.AspectRatio_9v16;
                    TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(makeRatio));
                    TimelineData.instance().setMakeRatio(makeRatio);
                    Bundle bundle = new Bundle();
                    bundle.putString("videoFilePath", mediaDataList.get(0).getPath());
                    AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), DouVideoTrimActivity.class, bundle);
                    AppManager.getInstance().finishActivity();
                } else if (fromWhat == Constants.FROMPICINPICACTIVITYTOVISIT) {
                    if (mPicInPicVideoIndex >= 0) {
                        /*
                         * 画中画页面，替换视频
                         * Picture-in-picture activity, replace video
                         * */
                        BackupData.instance().getPicInPicVideoArray().remove(mPicInPicVideoIndex);
                        BackupData.instance().getPicInPicVideoArray().add(mPicInPicVideoIndex, mediaDataList.get(0).getPath());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        AppManager.getInstance().finishActivity();
                    }
                } else if (fromWhat == Constants.SELECT_PICTURE_FROM_BACKGROUND) {

                    Intent intent = new Intent();
                    intent.putExtra(SINGLE_PICTURE_PATH, mediaDataList.get(0).getThumbPath());
                    setResult(RESULT_OK, intent);
                    AppManager.getInstance().finishActivity();
                } else if (fromWhat == Constants.SELECT_PICTURE_FROM_CAPTURE_SCENE) {

                    Intent intent = new Intent();
                    intent.putExtra(SINGLE_PICTURE_PATH, mediaDataList.get(0).getThumbPath());
                    setResult(RESULT_OK, intent);
                    AppManager.getInstance().finishActivity();
                }
            }
        });
        mGifToCafLayout = (LinearLayout) findViewById(R.id.gifToCafLayout);
        mGifToCafLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void initTitle() {
        m_titleBar.setTextCenter(R.string.selectMedia);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                fromWhat = bundle.getInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_IMAGE_FROM_WATER_MARK);
                mPicInPicVideoIndex = bundle.getInt("picInPicVideoIndex", -1);
                switch (fromWhat) {
                    /*
                     * 短视频拍摄页面相册入口
                     * Video capture page album entry
                     * */
                    case Constants.SELECT_VIDEO_FROM_DOUVIDEOCAPTURE:
                    case Constants.FROMPICINPICACTIVITYTOVISIT:
                        m_titleBar.setTextCenter(R.string.select_video);
                        break;
                    /*
                     *抠像背景
                     * segment background
                     * */
                    case Constants.SELECT_PICTURE_FROM_BACKGROUND_SEG:
                        sigleTvCancel.setVisibility(View.VISIBLE);
                        sigleTvCancel.setText(getResources().getString(R.string.cancel_background));
                        sigleTvStartEdit.setVisibility(View.VISIBLE);
                        sigleTvStartEdit.setText(getResources().getString(R.string.set_background));
                        break;
                    default:
                        /*
                         * 封面, 水印,自定义贴纸
                         * Cover, watermark, custom sticker
                         * */
                        m_titleBar.setTextCenter(R.string.single_select_picture);
                        break;
                }
            }
        }
        initVideoFragment(R.id.single_contain);
    }

    @SuppressLint("NonConstantResourceId")
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

            selectCreateRatio(POINT7V6);
        } else if (id == R.id.button7v6) {
            selectCreateRatio(POINT7V6);
        } else if (id == R.id.button6v7) {
            selectCreateRatio(POINT6V7);
        }
    }

    @Override
    public void onDismiss() {
        sigleTvStartEdit.setClickable(true);
    }

    private void gifToCafConvert(final String imageSrcFilePath) {
        String gifName = imageSrcFilePath.substring(imageSrcFilePath.lastIndexOf("/"));
        String converName = gifName.substring(0, gifName.lastIndexOf("."));
        String gifContverDir = PathUtils.getGifConvertDir();
        final String targetCafPath = gifContverDir + converName + ".caf";
        NvRational frameRate = new NvRational(20, 1);
        NvRational pixelAsprectRatio = new NvRational(1, 1);
        NvCafCreator gifToCafCreator = new NvCafCreator(SingleClickActivity.this,
                imageSrcFilePath, targetCafPath,
                300, 300,
                NvCafCreator.OUTPUT_FORMAT_PNG,
                frameRate, pixelAsprectRatio,
                NvCafCreator.OUTPUT_CAF_LOOP_MODE_REPEAT);
        gifToCafCreator.setOnConvertListener(new NvCafCreator.OnConvertListener() {
            @Override
            public void convertBitmap(Bitmap bitmap) {
                Logger.e(TAG, "cafConvert");
            }

            @Override
            public void convertFinished(boolean ret) {
                Message message = m_handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("imageSrcFilePath", imageSrcFilePath);
                bundle.putString("targetCafPath", targetCafPath);
                bundle.putBoolean("retResult", ret);
                message.setData(bundle);
                message.what = GIFTOCAFCONVERTFINISH;
                m_handler.sendMessage(message);
            }
        });
        gifToCafCreator.start();
    }

    private void selectCreateRatio(int makeRatio) {
        ArrayList<ClipInfo> pathList = getClipInfoList();
        TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(makeRatio));
        TimelineData.instance().setClipInfoData(pathList);
        TimelineData.instance().setMakeRatio(makeRatio);
        AppManager.getInstance().jumpActivity(SingleClickActivity.this, MakeCoverActivity.class, null);
    }

    private ArrayList<ClipInfo> getClipInfoList() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        if (mediaDataList != null) {
            for (MediaData mediaData : mediaDataList) {
                ClipInfo clipInfo = new ClipInfo();
                clipInfo.setImgDispalyMode(Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY);
                clipInfo.setOpenPhotoMove(false);
                clipInfo.setFilePath(mediaData.getPath());
                pathList.add(clipInfo);
            }
        }
        return pathList;
    }

    private void initVideoFragment(int layoutId) {
        MediaFragment mediaFragment = new MediaFragment();
        Bundle bundle = new Bundle();
        switch (fromWhat) {
            /*
             * 短视频拍摄页面相册入口或者画中画替换视频入口
             * Shake video capture page album entry or picture-in-picture replacement video entry
             * */
            case Constants.SELECT_VIDEO_FROM_DOUVIDEOCAPTURE:
            case Constants.FROMPICINPICACTIVITYTOVISIT:
                bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                break;
            case Constants.SELECT_PICTURE_FROM_BACKGROUND_SEG:
                bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.ALL_MEDIA);
                break;
            default:
                /*
                 * 封面, 水印,自定义贴纸
                 * Cover, watermark, custom sticker
                 * */
                bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.IMAGE);
                break;
        }
        bundle.putInt(KEY_CLICK_TYPE, MediaConstant.TYPE_ITEMCLICK_SINGLE);
        mediaFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(layoutId, mediaFragment)
                .commit();
        getSupportFragmentManager().beginTransaction().show(mediaFragment);
    }

    @Override
    protected void initListener() {
        m_titleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                removeHandleMsg();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTotalNumChangeForActivity(List selectList, Object tag) {
        mediaDataList = selectList;
        sigleTvStartEdit.setVisibility(selectList.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sigleTvStartEdit.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activityFinish();
    }

    private void addCafStickerToInfoList(String imageSrcFilePath, String targetCafPath) {
        String stickerTemplatePath = "assets:/E14FEE65-71A0-4717-9D66-3397B6C11223/E14FEE65-71A0-4717-9D66-3397B6C11223.5.animatedsticker";
        String packageId = PackageManagerUtil.installAssetPackage(stickerTemplatePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER);
        if (!packageId.isEmpty()) {
            NvAssetManager.NvCustomStickerInfo customStickerInfo = new NvAssetManager.NvCustomStickerInfo();
            /*
             * 自定义贴纸的Uuid值
             * Uuid value of custom sticker
             * */
            customStickerInfo.uuid = UUID.randomUUID().toString();
            customStickerInfo.imagePath = imageSrcFilePath;
            customStickerInfo.targetImagePath = targetCafPath;
            customStickerInfo.templateUuid = packageId.toString();
            customStickerInfo.order = NvAssetManager.sharedInstance().getUsableCustomStickerAssets().size();
            NvAssetManager.sharedInstance().appendCustomStickerInfoData(customStickerInfo);
            /*
             * 存储自定义贴纸
             * Store custom stickers
             * */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NvAssetManager.sharedInstance().setCustomStickerInfoToSharedPreferences();
                }
            }).start();
        }
    }

    private void activityFinish() {
        removeHandleMsg();
        mGifToCafLayout.setVisibility(View.GONE);
        AppManager.getInstance().finishActivity();
    }

    private void removeHandleMsg() {
        m_handler.removeCallbacksAndMessages(null);
    }
}
