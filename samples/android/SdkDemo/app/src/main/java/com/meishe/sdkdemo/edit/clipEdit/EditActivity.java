package com.meishe.sdkdemo.edit.clipEdit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAVFileInfo;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.activity.MainActivity;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.adapter.AssetRecyclerViewAdapter;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.clipEdit.adjust.AdjustActivity;
import com.meishe.sdkdemo.edit.clipEdit.animatedSticker.ClipAnimatedStickerActivity;
import com.meishe.sdkdemo.edit.clipEdit.caption.ClipCaptionActivity;
import com.meishe.sdkdemo.edit.clipEdit.correctionColor.CorrectionColorActivity;
import com.meishe.sdkdemo.edit.clipEdit.noise.NoiseSuppressionActivity;
import com.meishe.sdkdemo.edit.clipEdit.photo.DurationActivity;
import com.meishe.sdkdemo.edit.clipEdit.photo.PhotoMovementActivity;
import com.meishe.sdkdemo.edit.clipEdit.speed.SpeedActivity;
import com.meishe.sdkdemo.edit.clipEdit.spilt.SpiltActivity;
import com.meishe.sdkdemo.edit.clipEdit.trim.TrimActivity;
import com.meishe.sdkdemo.edit.clipEdit.volume.VolumeActivity;
import com.meishe.sdkdemo.edit.data.AssetInfoDescription;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.filter.ClipFilterActivity;
import com.meishe.sdkdemo.edit.grallyRecyclerView.GrallyAdapter;
import com.meishe.sdkdemo.edit.grallyRecyclerView.GrallyScaleHelper;
import com.meishe.sdkdemo.edit.interfaces.OnGrallyItemClickListener;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.selectmedia.SelectMediaActivity;
import com.meishe.sdkdemo.urledit.UrlMaterialActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.TransitionInfo;

import java.util.ArrayList;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author :
 * @CreateDate : 2019/6/28.
 * @Description :编辑Activity
 * @Description :VideoEditActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class EditActivity extends BaseActivity {
    /**
     * 裁剪
     * Clip crop
     */
    public static final int CLIPTRIM_REQUESTCODE = 101;
    /**
     * 分割
     * Clip spilt
     */
    public static final int CLIPSPILTPOINT_REQUESTCODE = 102;
    /**
     * 校色
     * Color correction
     */
    public static final int CLIPCORRECTIONCOLOR_REQUESTCODE = 103;
    /**
     * 调整
     * Adjustment
     */
    public static final int CLIPADJUST_REQUESTCODE = 104;
    /**
     * 速度
     * speed
     */
    public static final int CLIPSPEED_REQUESTCODE = 105;
    /**
     * 音量
     * volume
     */
    public static final int CLIPVOLUME_REQUESTCODE = 106;
    /**
     * 添加视频
     * Add video
     */
    public static final int ADDVIDEO_REQUESTCODE = 107;
    /**
     * 图片时长
     * Picture duration
     */
    public static final int PHOTODURATION_REQUESTCODE = 108;
    /**
     * 图片运动
     * Picture movement
     */
    public static final int PHOTOMOVE_REQUESTCODE = 109;
    /**
     * 贴纸
     * AnimatedSticker
     */
    public static final int CLIP_EDIT_ANIMATED_STICKER = 110;
    /**
     * 字幕
     * caption
     */
    public static final int CLIP_EDIT_CAPTION = 111;
    /**
     * 降噪 音频特技
     * Noise Suppression
     */
    public static final int CLIP_EDIT_NOISE = 112;

    private final int[] mImageIdVideo = {R.drawable.capture, R.drawable.division, R.drawable.amend, R.drawable.filter,
            R.drawable.clip_animatedsticker, R.drawable.clip_caption, R.drawable.adjust, R.drawable.speed,
            R.drawable.volume, R.drawable.activity_edit_noise, R.drawable.copy, R.drawable.delete};
    private final int[] mImageIdImage = {R.drawable.speed, R.drawable.adjust, R.drawable.amend, R.drawable.filter,
            R.drawable.clip_animatedsticker, R.drawable.clip_caption, R.drawable.copy, R.drawable.delete};
    private final ArrayList<AssetInfoDescription> mArrayAssetInfoVideo = new ArrayList<>();
    private final ArrayList<AssetInfoDescription> mArrayAssetInfoImage = new ArrayList<>();
    private CustomTitleBar mEditCustomTitleBar;
    private RecyclerView mGrallyRecyclerView;
    private RecyclerView mEffectRecyclerView;
    private AssetRecyclerViewAdapter mAssetRecycleAdapter;
    private GrallyAdapter mGrallyAdapter;
    private ArrayList<ClipInfo> mClipInfoArray = new ArrayList<>();
    private GrallyScaleHelper mGrallyScaleHelper;
    private int mCurrentPos = 0;
    private ImageView mEditCommitButton;
    private boolean mWaitFlag;
    private int mAddVideoPostion = 0;
    private boolean mIsImage = false;
    private ArrayList<TransitionInfo> mTransitionInfoArray;

    /**
     * clip 对应的动画集合 当前添加或删除 等操作 需要同步操作这个集合 以保证数据的同步
     * Operations such as adding or deleting the animation set corresponding to clip need to synchronize this set to ensure data synchronization
     */
//    private Map<Integer, AnimationInfo> mVideoClipFxMap = new TreeMap<>();
    @Override
    protected int initRootView() {
        return R.layout.activity_edit;
    }

    @Override
    protected void initViews() {
        mEditCustomTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mGrallyRecyclerView = (RecyclerView) findViewById(R.id.editClipRecyclerView);
        mEffectRecyclerView = (RecyclerView) findViewById(R.id.effectRecyclerView);
        mEditCommitButton = (ImageView) findViewById(R.id.edit_commitButton);
    }

    @Override
    protected void initTitle() {
        mEditCustomTitleBar.setTextCenter(getResources().getString(R.string.edit));
        mEditCustomTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mTransitionInfoArray = TimelineData.instance().cloneTransitionsData();
        mClipInfoArray = TimelineData.instance().cloneClipInfoData();
        BackupData.instance().setClipIndex(0);
        BackupData.instance().setClipInfoData(mClipInfoArray);
        String[] assetNameVideo = getResources().getStringArray(R.array.effectNamesVideo);
        for (int i = 0; i < assetNameVideo.length; i++) {
            mArrayAssetInfoVideo.add(new AssetInfoDescription(assetNameVideo[i], mImageIdVideo[i]));
        }
        String[] assetNameImage = getResources().getStringArray(R.array.effectNamesImage);
        for (int i = 0; i < assetNameImage.length; i++) {
            mArrayAssetInfoImage.add(new AssetInfoDescription(assetNameImage[i], mImageIdImage[i]));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGrallyRecyclerView.setLayoutManager(linearLayoutManager);
        mGrallyAdapter = new GrallyAdapter(getApplicationContext());
        mGrallyAdapter.setClipInfoArray(mClipInfoArray);
        mGrallyRecyclerView.setAdapter(mGrallyAdapter);

        ItemTouchHelper.Callback callback = new com.meishe.sdkdemo.edit.grallyRecyclerView.ItemTouchHelper(mGrallyAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mGrallyRecyclerView);
        mGrallyScaleHelper = new GrallyScaleHelper();
        mGrallyScaleHelper.attachToRecyclerView(mGrallyRecyclerView);
        /*
         * 效果列表
         * Effect list
         * */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mEffectRecyclerView.setLayoutManager(layoutManager);
        mAssetRecycleAdapter = new AssetRecyclerViewAdapter(this);
        mEffectRecyclerView.setAdapter(mAssetRecycleAdapter);
        mEffectRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(this, 6), ScreenUtils.dip2px(this, 8)));
        if (mClipInfoArray.size() > 0) {
            ClipInfo clipInfo = mClipInfoArray.get(0);
            updateOperateMenu(clipInfo);
        }
        //clip 对应的动画集合 The corresponding set of animations
//        ArrayList<ClipInfo> clipInfoData = BackupData.instance().getClipInfoData();
//        for (int i = 0; i < clipInfoData.size(); i++) {
//            mVideoClipFxMap.put(i, clipInfoData.get(i).getAnimationInfo());
//        }
    }

    @Override
    protected void initListener() {
        mAssetRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (mWaitFlag) {
                    return;
                }

                if (!mIsImage) {
                    switch (pos) {
                        case 0://trim
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    TrimActivity.class, null, EditActivity.CLIPTRIM_REQUESTCODE);
                            break;
                        case 1: // spilt
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    SpiltActivity.class, null, EditActivity.CLIPSPILTPOINT_REQUESTCODE);
                            break;
                        case 2: // Color correction
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    CorrectionColorActivity.class, null, EditActivity.CLIPCORRECTIONCOLOR_REQUESTCODE);
                            break;
                        case 3: // filter
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    ClipFilterActivity.class, null, EditActivity.CLIPCORRECTIONCOLOR_REQUESTCODE);
                            break;
                        case 4: // animatedSticker
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    ClipAnimatedStickerActivity.class, null, EditActivity.CLIP_EDIT_ANIMATED_STICKER);
                            break;
                        case 5: // caption
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    ClipCaptionActivity.class, null, EditActivity.CLIP_EDIT_CAPTION);
                            break;
                        case 6: // Adjustment
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    AdjustActivity.class, null, EditActivity.CLIPADJUST_REQUESTCODE);
                            break;
                        case 7:// speed
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    SpeedActivity.class, null, EditActivity.CLIPADJUST_REQUESTCODE);
                            break;
                        case 8: // volume
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    VolumeActivity.class, null, EditActivity.CLIPSPEED_REQUESTCODE);
                            break;
                        case 9: // noise
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    NoiseSuppressionActivity.class, null, EditActivity.CLIP_EDIT_NOISE);
                            break;
                        case 10: // copy
                            copyMediaAsset();
                            break;
                        case 11: // delete
                            deleteMediaAsset();
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (pos) {
                        case 0: // Duration
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    DurationActivity.class, null, EditActivity.PHOTODURATION_REQUESTCODE);
                            break;
                        case 1: // Movement
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    PhotoMovementActivity.class, null, EditActivity.PHOTOMOVE_REQUESTCODE);
                            break;
                        case 2: // Color correction
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    CorrectionColorActivity.class, null, EditActivity.CLIPCORRECTIONCOLOR_REQUESTCODE);
                            break;
                        case 3: // Filter
                            mWaitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    ClipFilterActivity.class, null, EditActivity.CLIPCORRECTIONCOLOR_REQUESTCODE);
                            break;
                        case 4: // animatedSticker
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    ClipAnimatedStickerActivity.class, null, EditActivity.CLIP_EDIT_ANIMATED_STICKER);

                            break;
                        case 5: // caption
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    ClipCaptionActivity.class, null, EditActivity.CLIP_EDIT_CAPTION);

                            break;
                        case 6: // copy
                            copyMediaAsset();
                            break;
                        case 7: // delete
                            deleteMediaAsset();
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        mGrallyAdapter.setOnItemSelectedListener(new OnGrallyItemClickListener() {
            @Override
            public void onLeftItemClick(View view, int pos) {
                reAddMediaAsset(pos);
            }

            @Override
            public void onRightItemClick(View view, int pos) {
                reAddMediaAsset(pos);
            }

            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                // Collections.swap(mClipInfoArray,fromPosition,toPosition);
            }

            @Override
            public void onItemDismiss(int position) {
                mClipInfoArray.remove(position);
            }

            @Override
            public void removeall() {
                mClipInfoArray.clear();
            }
        });

        mGrallyScaleHelper.setOnItemSelectedListener(new GrallyScaleHelper.OnGrallyItemSelectListener() {
            @Override
            public void onItemSelect(int pos) {
                if (pos < 0 || pos >= mClipInfoArray.size()) {
                    return;
                }
                mCurrentPos = pos;
                BackupData.instance().setClipIndex(mCurrentPos);
                ClipInfo clipInfo = mClipInfoArray.get(pos);
                updateOperateMenu(clipInfo);
            }
        });

        mEditCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimelineData.instance().setClipInfoData(mClipInfoArray);
                TimelineData.instance().setTransitionInfoArray(mTransitionInfoArray);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || (data == null)) {
            return;
        }
        ArrayList<ClipInfo> addCipInfoList = BackupData.instance().getAddClipInfoList();
        switch (requestCode) {
            case CLIPSPILTPOINT_REQUESTCODE:
                int spiltPosition = data.getIntExtra("spiltPosition", -1);
                if (spiltPosition != -1) {
                    if ((mTransitionInfoArray != null) && (!mTransitionInfoArray.isEmpty())) {
                        if (spiltPosition <= mTransitionInfoArray.size()) {
                            mTransitionInfoArray.add(spiltPosition, new TransitionInfo());
                        }
                    }
                }
                break;
            case CLIPADJUST_REQUESTCODE:
                break;
            default:
                break;
        }

        mClipInfoArray = BackupData.instance().getClipInfoData();
        if (addCipInfoList.size() > 0) {
            mClipInfoArray.addAll(mAddVideoPostion, addCipInfoList);
            BackupData.instance().setClipInfoData(mClipInfoArray);
            BackupData.instance().clearAddClipInfoList();
            /*
             * 为新增的素材添加默认转场
             * Add default transitions for new material
             * */
            if (mTransitionInfoArray != null) {
                ArrayList<TransitionInfo> temp = new ArrayList<>();
                int maxTransitionCount = mClipInfoArray.size() - mTransitionInfoArray.size() - 1;
                for (int i = 0; i < maxTransitionCount; i++) {
                    TransitionInfo transitionInfo = new TransitionInfo();
                    temp.add(transitionInfo);
                }
                if (mAddVideoPostion <= mTransitionInfoArray.size()) {
                    mTransitionInfoArray.addAll(mAddVideoPostion, temp);
                }
            }
        }
        mGrallyAdapter.setClipInfoArray(mClipInfoArray);
        mGrallyAdapter.notifyDataSetChanged();
        ClipInfo clipInfo = mClipInfoArray.get(mCurrentPos);
        updateOperateMenu(clipInfo);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaitFlag = false;
    }

    private void reAddMediaAsset(int pos) {
        mAddVideoPostion = pos;
        if (MainActivity.isUrlResource) {
            UrlMaterialActivity.nativeUrlMaterial(EditActivity.this,
                    ADDVIDEO_REQUESTCODE,
                    true, Constants.URL_MATERIAL_VIDEO,true);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("visitMethod", Constants.FROMCLIPEDITACTIVITYTOVISIT);
        BackupData.instance().clearAddClipInfoList();
        AppManager.getInstance().jumpActivityForResult(EditActivity.this, SelectMediaActivity.class, bundle, ADDVIDEO_REQUESTCODE);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void copyMediaAsset() {
        if (mClipInfoArray.size() == 0) {
            return;
        }
        int count = mClipInfoArray.size();
        if (mCurrentPos < 0 || mCurrentPos > count) {
            return;
        }
        mClipInfoArray.add(mCurrentPos, mClipInfoArray.get(mCurrentPos).clone());

        /*
         * 添加转场
         * Add transition
         * */
        if (mTransitionInfoArray != null && mTransitionInfoArray.size() >= mCurrentPos) {
            mTransitionInfoArray.add(mCurrentPos, new TransitionInfo());
        }
        mGrallyAdapter.setSelectPos(mCurrentPos);
        mGrallyAdapter.setClipInfoArray(mClipInfoArray);
        mGrallyAdapter.notifyDataSetChanged();
        /*
         * 复制移动到下一个位置
         * Copy move to next position
         * */
        mGrallyRecyclerView.smoothScrollBy(mGrallyScaleHelper.getmOnePageWidth(), 0);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteMediaAsset() {
        if (mClipInfoArray.size() == 0) {
            return;
        }

        if (mClipInfoArray.size() == 1) {
            String[] deleteVideoTips = getResources().getStringArray(R.array.video_delete_tips);
            Util.showDialog(EditActivity.this, deleteVideoTips[0], deleteVideoTips[1]);
            return;
        }
        int clipCount = mClipInfoArray.size();
        if (mCurrentPos < 0 || mCurrentPos >= clipCount) {
            return;
        }
        /*
         * 删除素材和转场
         * delete material and transition
         * */
        mClipInfoArray.remove(mCurrentPos);

        if ((mTransitionInfoArray != null) && !mTransitionInfoArray.isEmpty() && mTransitionInfoArray.size() > mCurrentPos) {
            mTransitionInfoArray.remove(Math.max(mCurrentPos - 1, 0));
        }
        if (mCurrentPos == clipCount - 1) {
            mCurrentPos--;
        }
        mGrallyAdapter.setClipInfoArray(mClipInfoArray);
        mGrallyAdapter.setSelectPos(mCurrentPos);
        mGrallyAdapter.notifyDataSetChanged();
        mGrallyScaleHelper.resetCurrentOffset(mCurrentPos);
        BackupData.instance().setClipIndex(mCurrentPos);
        /*
         * 更新操作菜单
         * Update operation menu
         * */
        ClipInfo clipInfo = mClipInfoArray.get(mCurrentPos);
        updateOperateMenu(clipInfo);
    }

    /**
     * 判断是否是图片
     * Determine whether it is a picture
     *
     * @param clipInfo clipinfo
     * @return boolean
     */
    private boolean isImage(ClipInfo clipInfo) {
        if (clipInfo != null) {
            NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(clipInfo.getFilePath());
            if (avFileInfo != null) {
                return avFileInfo.getAVFileType() == NvsAVFileInfo.AV_FILE_TYPE_IMAGE;
            }
        }
        return false;
    }

    private void updateOperateMenu(ClipInfo clipInfo) {
        if (mStreamingContext != null && clipInfo != null) {
            mIsImage = isImage(clipInfo);
            if (mAssetRecycleAdapter != null) {
                mAssetRecycleAdapter.updateData(mIsImage ? mArrayAssetInfoImage : mArrayAssetInfoVideo);
            }
        }
    }

}
