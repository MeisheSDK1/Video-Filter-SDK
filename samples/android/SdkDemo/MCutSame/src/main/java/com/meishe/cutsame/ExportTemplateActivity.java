package com.meishe.cutsame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishe.cutsame.bean.ExportTemplateClip;
import com.meishe.cutsame.bean.ExportTemplateSection;
import com.meishe.cutsame.util.CustomConstants;
import com.meishe.cutsame.util.ExportTemplateUtil;
import com.meishe.cutsame.util.RatioUtil;
import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsMediaFileConvertor;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.model.BaseActivity;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.FileIOUtils;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.ImageUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.MediaScannerUtil;
import com.meishe.base.utils.NetUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.utils.ToastUtils;
import com.meishe.base.utils.Utils;
import com.meishe.base.view.CompileProgress;
import com.meishe.engine.EditorEngine;
import com.meishe.engine.asset.bean.TemplateUploadParam;
import com.meishe.engine.bean.BaseInfo;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.TimelineData;
import com.meishe.engine.bean.TimelineDataUtil;
import com.meishe.engine.util.PathUtils;
import com.meishe.engine.util.WhiteList;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import static com.meishe.engine.bean.TimelineDataUtil.getTemplateAspectRatio;
import static com.meishe.engine.constant.NvsConstants.AspectRatio.AspectRatio_NoFitRatio;

/**
 * @author liupanfeng
 * @CreateDate :2020/12/30 15:14
 * @Description : 导出模板页面 Export Template page
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ExportTemplateActivity extends BaseActivity implements View.OnClickListener {

    public static final int FOOTAGE_ID_BASE_NUMBER = 1000;
    public static int FOOTAGE_ID_AUDIO_BASE_NUMBER = 2000;
    private static final int DEFAULT_COMPILE_RESOLUTION = NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM;
    private static final int TEMPLATE_NAME_MAX_LENGTH = 14;
    private static final int TEMPLATE_DESC_MAX_LENGTH = 60;
    private static final int MAX_TRACK_COUNT = 30;
    /**
     * compile占用上传时间的百分比 The percent of  compiling cast time in uploading total time.
     */
    private static final int PROGRESS_PERCENT_COMPILE = 50;
    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_UPLOAD_TOTAL = 98;

    private static final String FOLDER_ANIMATE_STICKER = "animatedsticker";
    private static final String FOLDER_CAPTION_ANIMATION = "captionanimation";
    private static final String FOLDER_CAPTION_CONTEXT = "captioncontext";
    private static final String FOLDER_CAPTION_OUT_ANIMATION = "captionoutanimation";
    private static final String FOLDER_CAPTION_IN_ANIMATION = "captioninanimation";
    private static final String FOLDER_CAPTION_RENDER_ER = "captionrenderer";
    private static final String FOLDER_CAPTION_STYLE = "captionstyle";
    private static final String FOLDER_COMPOUND_CAPTION = "compoundcaption";
    private static final String FOLDER_VIDEO_FX = "videofx";
    private static final String FOLDER_VIDEO_TRANSITION = "videotransition";
    private static final String FILE_TEMPLATE_INFO_DESC = "info.json";


    private EditText mEtInputTemplateName;
    private EditText mEtInputTemplateDesc;
    private Button mBtnExportTemplate;
    private View mBackView;
    private NvsTimeline mTimeLine;
    private Bitmap mCoverBitmap;
    private NvsStreamingContext mStreamingContext;
    private String mDestTransitionFolderPath;
    /**
     * 字幕组合动画 文件夹
     * caption animation folder
     */
    private String mDestCaptionCombAnimationFolderPath;
    /**
     *
     */
    private String mDestCaptionOutAnimationFolderPath;
    /**
     * 字幕 入场动画
     * caption in animation
     */
    private String mDestCaptionInAnimationFolderPath;

    /**
     * 视频特效 目标文件路径
     * video fx dest path
     */
    private String mDestVideoFxFolderPath;
    /**
     * 字幕 气泡 文件夹
     * caption bubble folder
     */
    private String mDestCaptionBubbleFolderPath;
    /**
     * 贴纸 文件夹
     * sticker folder
     */
    private String mDestStickerFolderDir;
    /**
     * 字幕 花字 文件夹
     * caption flower folder
     */
    private String mDestCaptionFlowerFolderPath;

//    /**
//     * 音频 文件夹
//     * audio folder
//     */
//    private String mDestAudioFolderPath;
//
//    /**
//     * 水印 文件夹
//     * water maker folder
//     */
//    private String mDestWaterMarkerFolderPath;

    private String mRootResourceFilePath;
    private int mFootageIdNumber;
    private LinearLayout mLlGenerateTemplate;
    private RelativeLayout mRlGenerateTemplateProgress;
    private TextView mTvGenerateResult;
    private CompileProgress mGenerateTemplateProgress;
    /**
     * 显示进度
     * show progress
     */
    private TextView mTvGenerateTemplateProgress;


    private Button mBtnGenerateTemplateCancel;
    private List<ExportTemplateSection> mData;
    private List<ExportTemplateClip> mAllExportTemplateClipList = new ArrayList<>();
    private List<ExportTemplateClip> mLockData = new ArrayList<>();
    private String mVideoSavePath;
    private ImageView mIvHome;
    private TextView mTvNameNumber;
    private TextView mTvDescNumber;
    private ImageView mIvCover;
    private String mGenerateTemplateFileFolder;
    private String mDestCompoundCaptionFolderPath;
    private NvsMediaFileConvertor mMediaFileConverter;
    private String mUuid;
    private int mRatio;
    private boolean mNeedUpload = false;
    private int mCurrentProgress = PROGRESS_PERCENT_COMPILE;

    @Override
    protected int bindLayout() {
        return R.layout.activity_export_template;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            mData = (List<ExportTemplateSection>) intent.getSerializableExtra(CustomConstants.TEMPLATE_CLIP_DATA);
        }
        if (CommonUtils.isEmpty(mData)) {
            return;
        }

        EditorEngine mEditorEngine = EditorEngine.getInstance();
        if (mEditorEngine == null) {
            return;
        }
        mTimeLine = EditorEngine.getInstance().getCurrentTimeline();
        if (mTimeLine == null) {
            LogUtils.e("timeline is null !!!");
            return;
        }
        mStreamingContext = mEditorEngine.getStreamingContext();
        mRatio = TimelineData.getInstance().getMakeRatio();
        if (mRatio == AspectRatio_NoFitRatio) {
            mRatio = getTemplateAspectRatio();
        }
        mCoverBitmap = mStreamingContext.grabImageFromTimeline(mTimeLine, 0, null);
        initSectionData();
        initVideoClip();
        initCaptionData();
        if (mMediaFileConverter == null) {
            mMediaFileConverter = new NvsMediaFileConvertor();
        }
        mUuid = UUID.randomUUID().toString().toUpperCase();
        initRootResourcePath();
        saveNeedVideoClip();
    }

    /**
     * 初始化字幕数据
     * Initializes the title data
     */
    private void initCaptionData() {
        NvsTimelineCaption firstCaption = mTimeLine.getFirstCaption();
        int index = 0;
        while (firstCaption != null) {
            index++;
            firstCaption.setTemplateAttachment(NvsVideoClip.TEMPLATE_KEY_REPLACE_ID, String.valueOf(index));
            firstCaption = mTimeLine.getNextCaption(firstCaption);
        }
    }


    @Override
    protected void initView() {
        mBackView = findViewById(R.id.iv_back);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mBackView.getLayoutParams();
        layoutParams.topMargin = (int) (ScreenUtils.getStatusBarHeight() + getResources().getDimension(R.dimen.title_margin_top));
        mBackView.setLayoutParams(layoutParams);
        mIvHome = findViewById(R.id.iv_compile_home);
        FrameLayout.LayoutParams layoutParamsHome = (FrameLayout.LayoutParams) mIvHome.getLayoutParams();
        layoutParamsHome.topMargin = (int) (ScreenUtils.getStatusBarHeight() + getResources().getDimension(R.dimen.title_margin_top));
        mIvHome.setLayoutParams(layoutParamsHome);
        mIvCover = findViewById(R.id.iv_cover);
        mLlGenerateTemplate = findViewById(R.id.ll_generate_template);
        mRlGenerateTemplateProgress = findViewById(R.id.rl_generate_template_progress);
        mTvGenerateResult = findViewById(R.id.tv_generate_result);
        mGenerateTemplateProgress = findViewById(R.id.generate_template_progress);
        mTvGenerateTemplateProgress = findViewById(R.id.tv_generate_template_progress);
        mBtnGenerateTemplateCancel = findViewById(R.id.btn_generate_template_cancel);
        mEtInputTemplateName = findViewById(R.id.et_input_template_name);
        mEtInputTemplateDesc = findViewById(R.id.et_input_template_desc);
        mBtnExportTemplate = findViewById(R.id.btn_export_template);
        View uploadingHint = findViewById(R.id.tv_uploading_hint);
        mBtnExportTemplate.setText(getResources().getString(R.string.activity_cut_export_template_confirm_generate_template));
        uploadingHint.setVisibility(View.GONE);
        mTvNameNumber = findViewById(R.id.tv_name_number);
        mTvDescNumber = findViewById(R.id.tv_desc_number);
        mLlGenerateTemplate.setVisibility(View.VISIBLE);

        mTvNameNumber.setText(String.valueOf(TEMPLATE_NAME_MAX_LENGTH));
        mTvDescNumber.setText(String.valueOf(TEMPLATE_DESC_MAX_LENGTH));
        initListener();
        mIvCover.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIvCover.setImageBitmap(mCoverBitmap);
            }
        }, 100);
    }

    private void initListener() {
        mBtnExportTemplate.setOnClickListener(this);
        mBackView.setOnClickListener(this);
        mBtnGenerateTemplateCancel.setOnClickListener(this);
        mIvHome.setOnClickListener(this);

        mStreamingContext.setCompileCallback(new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {
                if (!mNeedUpload) {
                    compileProgress(i);
                }
            }

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
                File file = new File(mVideoSavePath);
                if (file.exists()) {
                    /*
                     * 加入到媒体库
                     * Add to media library
                     * */
                    MediaScannerUtil.scanFile(mVideoSavePath, "video/mp4");
                }
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {
                FileUtils.deleteAllInDir(mGenerateTemplateFileFolder);
            }
        });

        mStreamingContext.setCompileCallback2(new NvsStreamingContext.CompileCallback2() {
            @Override
            public void onCompileCompleted(NvsTimeline nvsTimeline, boolean b) {
                if (!b) {
                    //jumpToCompile();
                    mStreamingContext.setCompileConfigurations(null);
                } else {
                    if (!mNeedUpload) {
                        ToastUtils.showShort(getString(R.string.activity_cut_export_template_cancel));
                    }
                }
                onCompileEnd(!b);
            }
        });

        mEtInputTemplateName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                String str = s.toString();
                if (length > TEMPLATE_NAME_MAX_LENGTH) {
                    mEtInputTemplateName.setText(str.substring(0, TEMPLATE_NAME_MAX_LENGTH));
                    mEtInputTemplateName.requestFocus();
                    mEtInputTemplateName.setSelection(mEtInputTemplateName.getText().length());
                } else {
                    int i = TEMPLATE_NAME_MAX_LENGTH - length;
                    mTvNameNumber.setText(String.valueOf(i));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mEtInputTemplateDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                String str = s.toString();
                if (length > TEMPLATE_DESC_MAX_LENGTH) {
                    mEtInputTemplateDesc.setText(str.substring(0, TEMPLATE_DESC_MAX_LENGTH));
                    mEtInputTemplateDesc.requestFocus();
                    mEtInputTemplateDesc.setSelection(mEtInputTemplateDesc.getText().length());
                } else {
                    int i = TEMPLATE_DESC_MAX_LENGTH - length;
                    mTvDescNumber.setText(String.valueOf(i));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 对数据进行处理，去掉header部分
     * remove header part
     */
    private void initSectionData() {
        for (int i = 0; i < mData.size(); i++) {
            ExportTemplateSection exportTemplateSection = mData.get(i);
            if (exportTemplateSection == null) {
                continue;
            }
            if (exportTemplateSection.isHeader) {
                continue;
            }
            ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
            if (exportTemplateClip == null) {
                continue;
            }
            mAllExportTemplateClipList.add(exportTemplateClip);
        }
    }

    /**
     * 对视频片段进行footage设置
     * set footage for video clip
     */
    private void initVideoClip() {
        mLockData.clear();
        mFootageIdNumber = 0;
        for (int m = 0; m < MAX_TRACK_COUNT; m++) {
            NvsVideoTrack videoTrackByIndex = mTimeLine.getVideoTrackByIndex(m);
            if (videoTrackByIndex != null) {
                int clipCount = videoTrackByIndex.getClipCount();
                for (int i = 0; i < clipCount; i++) {
                    NvsVideoClip videoClip = videoTrackByIndex.getClipByIndex(i);
                    if (videoClip == null) {
                        continue;
                    }
                    ExportTemplateClip exportTemplateClip = findExportTemplateClip(m, videoClip.getInPoint());
                    if (exportTemplateClip == null) {
                        continue;
                    }
                    if (exportTemplateClip.isLock()) {
                        mLockData.add(exportTemplateClip);
                        String imagePath = exportTemplateClip.getImagePath();
                        File file = new File(imagePath);
                        if (file.exists()) {
                            videoClip.setTemplateAttachment(NvsVideoClip.TEMPLATE_KEY_FOOTAGE_NAME, file.getName());
                        }
                    }
                    videoClip.setTemplateAttachment(NvsVideoClip.TEMPLATE_KEY_FOOTAGE_ID, "footage" + (exportTemplateClip.getFootageGroupsId() == 0 ?
                            exportTemplateClip.getFootageId() : exportTemplateClip.getFootageGroupsId() * FOOTAGE_ID_BASE_NUMBER));
                    videoClip.setTemplateAttachment(NvsVideoClip.TEMPLATE_KEY_FOOTAGE_TYPE, exportTemplateClip.getFootageType());
                    videoClip.setTemplateAttachment(NvsVideoClip.TEMPLATE_KEY_FOOTAGE_CAN_REPLACE, exportTemplateClip.isLock() ? "false" : "true");
                    MeicamVideoClip meicamVideoClip = (MeicamVideoClip) TimelineDataUtil.getVideoClip(videoTrackByIndex.getIndex(), videoClip.getInPoint());
                    if (meicamVideoClip != null) {
                        videoClip.setTemplateAttachment(NvsAudioClip.TEMPLATE_KEY_FOOTAGE_NEED_REVERSE, meicamVideoClip.getVideoReverse() + "");
                    }
                    mFootageIdNumber++;
                }
            }
            NvsAudioTrack nvsAudioTrack = mTimeLine.getAudioTrackByIndex(m);
            if (nvsAudioTrack != null) {
                for (int i = 0; i < nvsAudioTrack.getClipCount(); i++) {
                    NvsAudioClip nvsAudioClip = nvsAudioTrack.getClipByIndex(i);
                    if (nvsAudioClip == null) {
                        continue;
                    }
                    String imagePath = nvsAudioClip.getFilePath();
                    File file = new File(imagePath);
                    if (file.exists()) {
                        nvsAudioClip.setTemplateAttachment(NvsAudioClip.TEMPLATE_KEY_FOOTAGE_NAME, file.getName());
                    }
                    nvsAudioClip.setTemplateAttachment(NvsAudioClip.TEMPLATE_KEY_FOOTAGE_TYPE, "audio");
                    nvsAudioClip.setTemplateAttachment(NvsAudioClip.TEMPLATE_KEY_FOOTAGE_NEED_REVERSE, "false");
                    nvsAudioClip.setTemplateAttachment(NvsAudioClip.TEMPLATE_KEY_FOOTAGE_ID, "footage" + (FOOTAGE_ID_AUDIO_BASE_NUMBER++));
                    nvsAudioClip.setTemplateAttachment(NvsAudioClip.TEMPLATE_KEY_FOOTAGE_TAGS, "音频");
                }
            }
        }

    }


    /**
     * 找到对应的上层数据
     * Find the corresponding upper-layer data
     *
     * @param trackIndex
     * @param inPoint
     * @return
     */
    private ExportTemplateClip findExportTemplateClip(int trackIndex, long inPoint) {
        if (CommonUtils.isEmpty(mAllExportTemplateClipList)) {
            return null;
        }
        for (int i = 0; i < mAllExportTemplateClipList.size(); i++) {
            ExportTemplateClip exportTemplateClip = mAllExportTemplateClipList.get(i);
            if (exportTemplateClip == null) {
                continue;
            }
            if (exportTemplateClip.getInPoint() == inPoint
                    && exportTemplateClip.getTrackIndex() == trackIndex) {
                return exportTemplateClip;
            }
        }
        return null;
    }


    /**
     * 保存需要的资源文件
     * save need resource
     */
    private void scanAndSaveNeedResource() {
        //保存转场资源 Save transition resources
        ExportTemplateUtil.scanAndSaveTransitionResource(this, mDestTransitionFolderPath);
        //保存特效资源 Save effects resources
        ExportTemplateUtil.scanAndSaveVideoFx(this, mDestVideoFxFolderPath);
        //保存贴纸资源 Save sticker resources
        ExportTemplateUtil.scanAndSaveAnimateSticker(this, mDestStickerFolderDir);
        //保存组合字幕资源 Save the composite title resource
        ExportTemplateUtil.scanAndSaveCompoundCaption(this, mDestCompoundCaptionFolderPath);
        //保存字幕相关的资源 Save resources associated with subtitles
        ExportTemplateUtil.scanAndSaveCaptionAnimation(this, mDestCaptionCombAnimationFolderPath
                , mDestCaptionInAnimationFolderPath, mDestCaptionOutAnimationFolderPath
                , mDestCaptionBubbleFolderPath, mDestCaptionFlowerFolderPath);
        //保存音频相关的资源 Save audio related resources
        ExportTemplateUtil.scanAndSaveAudio(this, mRootResourceFilePath);
        //保存水印相关的资源 Saves watermarking related resources
        ExportTemplateUtil.scanAndSaveWaterMaker(this, mRootResourceFilePath);
    }

    /**
     * 保存需要的视频资源
     * save need video
     */
    private void saveNeedVideoClip() {
        if (CommonUtils.isEmpty(mLockData)) {
            return;
        }
        for (int i = 0; i < mLockData.size(); i++) {
            ExportTemplateClip exportTemplateClip = mLockData.get(i);
            if (exportTemplateClip == null) {
                continue;
            }
            String imagePath = exportTemplateClip.getImagePath();
            if (TextUtils.isEmpty(imagePath)) {
                continue;
            }
            File file = new File(imagePath);
            if (!file.exists()) {
                continue;
            }
            String fileName = file.getName();
            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") ||
                    fileName.endsWith(".jpeg") || fileName.endsWith(".gif")) {
                String destFilePath = mRootResourceFilePath + File.separator + fileName;
                FileUtils.createOrExistsFile(destFilePath);
                ExportTemplateUtil.copySdcardFile(imagePath, destFilePath);
            } else {
                long trimIn = exportTemplateClip.getTrimIn();
                long trimOut = exportTemplateClip.getTrimOut();
                if (trimOut < trimIn) {
                    continue;
                }
                String destFilePath = mRootResourceFilePath + File.separator + fileName;
                if (WhiteList.isCovert4KFileWhiteList(imagePath)) {
                    Hashtable<String, Object> hashtable = new Hashtable<>();
                    hashtable.put(NvsMediaFileConvertor.CONVERTOR_CUSTOM_VIDEO_HEIGHT, 1080);
                    mMediaFileConverter.convertMeidaFile(imagePath, destFilePath, exportTemplateClip.isVideoReverse(), trimIn, trimOut, hashtable);
                } else {
                    mMediaFileConverter.convertMeidaFile(imagePath, destFilePath, exportTemplateClip.isVideoReverse(), trimIn, trimOut, null);
                }
            }
        }
    }


    public void initRootResourcePath() {
        String templateFileFolder = PathUtils.getTemplateFileFolder();
        mRootResourceFilePath = templateFileFolder + File.separator + mUuid;
        FileUtils.createOrExistsDir(mRootResourceFilePath);
    }

    /**
     * 初始化生成模板的文件夹
     * init template folder
     */
    private void initTemplateFolder(String templateName, String templateDesc, String coverPath) {

        mDestStickerFolderDir = mRootResourceFilePath + File.separator + FOLDER_ANIMATE_STICKER;
        FileUtils.createOrExistsDir(mDestStickerFolderDir);

        mDestCaptionCombAnimationFolderPath = mRootResourceFilePath + File.separator + FOLDER_CAPTION_ANIMATION;
        FileUtils.createOrExistsDir(mDestTransitionFolderPath);

        mDestCaptionBubbleFolderPath = mRootResourceFilePath + File.separator + FOLDER_CAPTION_CONTEXT;
        FileUtils.createOrExistsDir(mDestCaptionBubbleFolderPath);

        mDestCaptionOutAnimationFolderPath = mRootResourceFilePath + File.separator + FOLDER_CAPTION_OUT_ANIMATION;
        FileUtils.createOrExistsDir(mDestCaptionOutAnimationFolderPath);

        mDestCaptionInAnimationFolderPath = mRootResourceFilePath + File.separator + FOLDER_CAPTION_IN_ANIMATION;
        FileUtils.createOrExistsDir(mDestCaptionInAnimationFolderPath);

        mDestCaptionFlowerFolderPath = mRootResourceFilePath + File.separator + FOLDER_CAPTION_RENDER_ER;
        FileUtils.createOrExistsDir(mDestCaptionFlowerFolderPath);
        mDestCompoundCaptionFolderPath = mRootResourceFilePath + File.separator + FOLDER_COMPOUND_CAPTION;
        FileUtils.createOrExistsDir(mDestCompoundCaptionFolderPath);
        mDestVideoFxFolderPath = mRootResourceFilePath + File.separator + FOLDER_VIDEO_FX;
        FileUtils.createOrExistsDir(mDestVideoFxFolderPath);
        mDestTransitionFolderPath = mRootResourceFilePath + File.separator + FOLDER_VIDEO_TRANSITION;
        FileUtils.createOrExistsDir(mDestTransitionFolderPath);

        FileUtils.createOrExistsFile(mRootResourceFilePath + File.separator + FILE_TEMPLATE_INFO_DESC);

        File file = new File(mRootResourceFilePath + File.separator + FILE_TEMPLATE_INFO_DESC);
        if (!file.exists()) {
            return;
        }
        FileIOUtils.writeFileFromString(file
                , ExportTemplateUtil.getInfoDesc(getResources().getString(R.string.template_default_creator), mUuid
                        , RatioUtil.getAspectRatioStr(mRatio), mTimeLine.getDuration()
                        , mFootageIdNumber, templateName, templateDesc, coverPath));
    }

    TemplateUploadParam param;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        } else if (view.getId() == R.id.btn_export_template) {
            if (mNeedUpload && !NetUtils.isNetworkAvailable(this)) {
                ToastUtils.showShort(Utils.getApp().getResources().getString(com.meishe.engine.R.string.user_hint_assets_net_error));
                return;
            }
            String templateName = mEtInputTemplateName.getText().toString();
            if (TextUtils.isEmpty(templateName)) {
                ToastUtils.showShort(R.string.activity_cut_export_template_please_input_template_name);
                return;
            }

            String templateDesc = mEtInputTemplateDesc.getText().toString();
            if (TextUtils.isEmpty(templateDesc)) {
                ToastUtils.showShort(R.string.activity_cut_export_template_please_input_template_desc);
                return;
            }
            mGenerateTemplateFileFolder = PathUtils.getGenerateTemplateFileFolder(mUuid);
            String imageCoverPath = mGenerateTemplateFileFolder + File.separator + mUuid + ".png";
            FileUtils.createOrExistsFile(imageCoverPath);
            //保存封面图 Save cover image
            File imageFile = new File(imageCoverPath);
            if (!imageFile.exists()) {
                return;
            }
            Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
            ImageUtils.save(mCoverBitmap, imageFile, format);

            initTemplateFolder(templateName, templateDesc, imageCoverPath);

            scanAndSaveNeedResource();

            boolean exportTemplateInfo = mStreamingContext.exportTemplateInfo(mUuid, mTimeLine, mRatio);
            if (!exportTemplateInfo) {
                return;
            }

            boolean generateTemplatePackage = mStreamingContext.generateTemplatePackage(mUuid, mRootResourceFilePath, mGenerateTemplateFileFolder);
            if (!generateTemplatePackage) {
                return;
            }
            //将描述文件输出到指定的目录 Outputs the description file to the specified directory
            String templateDescFilePath = mGenerateTemplateFileFolder + File.separator + FILE_TEMPLATE_INFO_DESC;
            FileUtils.createOrExistsFile(templateDescFilePath);
            File file = new File(templateDescFilePath);
            if (!file.exists()) {
                return;
            }
            String templatePath = mGenerateTemplateFileFolder + File.separator + mUuid + ".template";
            mVideoSavePath = mGenerateTemplateFileFolder + File.separator + PathUtils.getTemplateVideoSaveName(mUuid);
            FileIOUtils.writeFileFromString(file, ExportTemplateUtil.getSimpleInfoDesc(mUuid,
                    RatioUtil.getAspectRatioStr(mRatio),
                    mTimeLine.getDuration(),
                    mFootageIdNumber, templateName, templateDesc,
                    imageCoverPath, templatePath, mVideoSavePath));

            //保存视频 Save video
            mVideoSavePath = PathUtils.getVideoSavePath(PathUtils.getTemplateVideoSaveName(mUuid));
            int state = mStreamingContext.getStreamingEngineState();
            if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_STOPPED || state == NvsStreamingContext.STREAMING_ENGINE_STATE_SEEKING) {
                int ratio = TimelineData.getInstance().getMakeRatio();
                if (ratio == BaseInfo.AspectRatio_NoFitRatio) {
                    mStreamingContext.setCustomCompileVideoHeight(TimelineDataUtil.getCustomHeight(720, mRatio));
                }
                if (mStreamingContext.compileTimeline(mTimeLine, 0, mTimeLine.getDuration(),
                        mVideoSavePath, DEFAULT_COMPILE_RESOLUTION,
                        NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH, NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_HOST_VIDEO_FRAME
                                | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_ORIGIN_VIDEO_FRAME)) {
                    onStartGenerateTemplate();
                }
            }

            param = new TemplateUploadParam();
            param.materialFile = new File(templatePath);
            param.coverFile = imageFile;
            param.previewVideoFile = new File(mVideoSavePath);
            if (Utils.isZh()) {
                param.descriptinZhCn = templateDesc;
            } else {
                param.description = templateDesc;
            }
            param.customDisplayName = templateName;

        } else if (view.getId() == R.id.btn_generate_template_cancel) {
            mStreamingContext.stop();
            mStreamingContext.setCompileConfigurations(null);
        }
    }


    public void onStartGenerateTemplate() {
        mRlGenerateTemplateProgress.setFocusable(true);
        mRlGenerateTemplateProgress.requestFocus();
        mRlGenerateTemplateProgress.setVisibility(View.VISIBLE);
        mBtnGenerateTemplateCancel.setVisibility(View.VISIBLE);
        mLlGenerateTemplate.setVisibility(View.GONE);
    }

    public void compileProgress(int progress) {
        mGenerateTemplateProgress.setProgress(progress);
        String text = progress + "%";
        mTvGenerateTemplateProgress.setText(text);
    }

    public void onCompileEnd(boolean complete) {
        if (complete) {
            if (!mNeedUpload) {
                onExportSuccess();
            }

        } else {
            onExportFailed();
        }
    }

    /**
     * 导出视频未完成
     * The exported video is not complete
     */
    private void onExportFailed() {
        mRlGenerateTemplateProgress.setVisibility(View.GONE);
        mBtnGenerateTemplateCancel.setVisibility(View.GONE);
        mRlGenerateTemplateProgress.setFocusable(false);
        mGenerateTemplateProgress.setProgress(0);
        mTvGenerateTemplateProgress.setText("0%");
        mLlGenerateTemplate.setVisibility(View.VISIBLE);
        FileUtils.deleteAllInDir(mGenerateTemplateFileFolder);
    }

    /**
     * 导出视频完成
     * Export video completion
     */
    private void onExportSuccess() {
        mTvGenerateResult.setVisibility(View.VISIBLE);
        mIvHome.setVisibility(View.VISIBLE);
        mRlGenerateTemplateProgress.setVisibility(View.GONE);
        mBtnGenerateTemplateCancel.setVisibility(View.GONE);
        mIvCover.setVisibility(View.GONE);
    }

}