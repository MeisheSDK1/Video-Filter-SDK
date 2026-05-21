package com.meishe.cutsame.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsVideoClip;
import com.meishe.base.manager.AppManager;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.view.decoration.ItemDecoration;
import com.meishe.cutsame.CutSameEditorActivity;
import com.meishe.cutsame.MaterialSelectActivity;
import com.meishe.cutsame.R;
import com.meishe.cutsame.TailorActivity;
import com.meishe.cutsame.TailorFreeActivity;
import com.meishe.cutsame.bean.Template;
import com.meishe.cutsame.bean.TemplateClip;
import com.meishe.cutsame.fragment.adapter.CutCaptionAdapter;
import com.meishe.cutsame.fragment.adapter.CutVideoAdapter;
import com.meishe.cutsame.fragment.iview.CutEditorVpView;
import com.meishe.cutsame.fragment.presenter.CutEditorVpPresenter;
import com.meishe.cutsame.pop.CutCaptionEditPop;
import com.meishe.cutsame.pop.VideoEditPop;
import com.meishe.cutsame.util.CustomConstants;
import com.meishe.engine.bean.template.MeicamNvsTemplateFootageCorrespondingClipInfo;
import com.meishe.engine.bean.template.TemplateCaptionDesc;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.interf.StreamingConstant;
import com.meishe.third.adpater.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.meishe.cutsame.util.CustomConstants.REQUEST_CODE_1;
import static com.meishe.cutsame.util.CustomConstants.SELECTED_TYPE;
import static com.meishe.cutsame.util.CustomConstants.TEMPLATE_CLIP;
import static com.meishe.cutsame.util.CustomConstants.TYPE_ONE_FINISH;

/**
 * Created by CaoZhiChao on 2020/11/4 18:11
 * 剪切编辑器Vp类
 * Shear editor Vp class
 */
public class CutEditorVpFragment extends BaseMvpFragment<CutEditorVpPresenter> implements CutEditorVpView {
    private static final String TYPE = "type";
    private static final String TEMPLATE_ID = "templateID";
    private static final String TEMPLATE_TYPE = "templateType";
    private RecyclerView mFragmentRecyclerView;
    private CutVideoAdapter mCutVideoAdapter;
    private CutCaptionAdapter mCutCaptionAdapter;
    private List<MeicamNvsTemplateFootageCorrespondingClipInfo> mVideoData = new ArrayList<>();
    private List<TemplateCaptionDesc> mCaptionData = new ArrayList<>();
    private String mType;
    private String mTemplateId;
    private String mTemplateType;
    private SeekPositionListener mSeekPositionListener;
    private MeicamNvsTemplateFootageCorrespondingClipInfo selectedClipInfo;
    private CutCaptionEditPop mCaptionEditPop;
    private List<TemplateClip> mClipList = null;

    public CutEditorVpFragment() {
    }

    @SuppressLint("ValidFragment")
    public CutEditorVpFragment(SeekPositionListener seekPositionListener) {
        mSeekPositionListener = seekPositionListener;
    }

    public static CutEditorVpFragment newInstance(String type, String templateId, String templateType, List<TemplateClip> mClipList, SeekPositionListener seekPositionListener) {
        CutEditorVpFragment fragment = new CutEditorVpFragment(seekPositionListener);
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        args.putString(TEMPLATE_ID, templateId);
        args.putString(TEMPLATE_TYPE, templateType);
        args.putParcelableArrayList(CustomConstants.TEMPLATE_CLIP_LIST, (ArrayList<? extends Parcelable>) mClipList);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Bind layout int.
     * 绑定布局
     *
     * @return the int
     */
    @Override
    protected int bindLayout() {
        return R.layout.fragment_cut_editor;
    }

    /**
     * On lazy load.
     * 延迟加载
     */
    @Override
    protected void onLazyLoad() {
        if (mCutCaptionAdapter != null && mCutCaptionAdapter.getSelectIndex() > 0) {
            if (CutEditorVpPresenter.CAPTION.equals(mType)) {
                if (getActivity() instanceof CutSameEditorActivity) {
                    if (null != mCutVideoAdapter && null != mCutCaptionAdapter) {
                        ((CutSameEditorActivity) getActivity()).showCaptionBox(mPresenter.getCaptionPointList(mCutCaptionAdapter.getItem(mCutVideoAdapter.getSelectIndex())));
                    }
                }
            }
        }
    }

    /**
     * Init view.
     * 初始化视图
     *
     * @param rootView the root view
     */
    @Override
    protected void initView(View rootView) {
        mFragmentRecyclerView = rootView.findViewById(R.id.fragment_recycler_view);
    }

    /**
     * Init data.
     * 初始化数据
     */
    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getString(TYPE);
            mTemplateId = bundle.getString(TEMPLATE_ID);
            mTemplateType = bundle.getString(TEMPLATE_TYPE);
            mClipList = bundle.getParcelableArrayList(CustomConstants.TEMPLATE_CLIP_LIST);
        }
        initRecycleView();
        mPresenter.onDataReady(mType, mTemplateId, mTemplateType, mClipList);
        EditorController.getInstance().playNow2(0);
    }

    private void initRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mFragmentRecyclerView.setLayoutManager(layoutManager);
        mFragmentRecyclerView.addItemDecoration(new ItemDecoration(18, 18));
    }

    /**
     * Chang clip trim.
     * 修剪
     *
     * @param newTrimIn the new trim in
     */
    public void changClipTrim(long newTrimIn) {
        mPresenter.changClipTrim(newTrimIn, selectedClipInfo);
    }

    /**
     * Chang clip trim.
     * 裁剪或替换素材之后都需要调用这个方法
     * 此方法会重新构建一个date集合，主要原因是因为trimIn trimOut inPoint outPoint ,一个变了都会影响后边其他素材。
     *
     * @param newTrimIn the new trim in
     */
    public void changClipTrimInfo(List<TemplateClip> clipList, long newTrimIn, long newTrimOut) {
        //更新clip上的trim
        mPresenter.changClipTrimIn(newTrimIn, selectedClipInfo);
        mPresenter.changClipTrimOut(newTrimOut, selectedClipInfo);
        //更新数据源中的trim
        if (CommonUtils.isEmpty(mVideoData)) {
            return;
        }
        //这里需要把整个集合的内容都修改，修改trimIn,trimOut inPoint outpoint
        List<MeicamNvsTemplateFootageCorrespondingClipInfo> templateVideoClip = mPresenter.getTemplateVideoClip(clipList);
        this.mVideoData = templateVideoClip;
        if (null != mCutVideoAdapter) {
            mCutVideoAdapter.setNewData(mVideoData);
            selectedClipInfo = mVideoData.get(mCutVideoAdapter.getSelectIndex());
        }
    }

    /**
     * Gets caption data.
     * 获得字幕数据
     *
     * @param captionClipList the caption clip list 字幕列表
     */
    @Override
    public void getCaptionData(List<TemplateCaptionDesc> captionClipList) {
        int groupIndex = -1;
        mCaptionData = captionClipList;
        //不可删除，这里要找到所有的GroupID,设置给TemplateCaptionDesc It cannot be deleted. Here you need to find all the Group IDs and set them to TemplateCaptionDesc
        EditorController.getInstance().getAllCaption(mCaptionData);
        //设置编组信息.说明没有编组，适配AE模板，再计算一下编组信息 If no grouping is configured, the AE template is used. Then calculate the grouping information
        Map<String, List<TemplateCaptionDesc>> map = new HashMap<>();
        String key = "";
        List<TemplateCaptionDesc> tempList = null;
        for (TemplateCaptionDesc templateCaptionDesc : mCaptionData) {
            key = templateCaptionDesc.getGroupID();
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            tempList = map.get(key);
            if (null == tempList) {
                tempList = new ArrayList<>();
                map.put(key, tempList);
            }
            tempList.add(templateCaptionDesc);
        }

        for (List<TemplateCaptionDesc> templateClips : map.values()) {
            if (templateClips.size() > 1) {
                groupIndex += 1;
                for (TemplateCaptionDesc templateClip : templateClips) {
                    templateClip.setGroupIndex(groupIndex);
                }
            }
        }

        mCutCaptionAdapter = new CutCaptionAdapter();
        mCutCaptionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                mCutCaptionAdapter.setSelectIndex(position);
                TemplateCaptionDesc captionDesc = mCaptionData.get(position);
                if (mCutCaptionAdapter.getSelectIndex() == mCutCaptionAdapter.getBeforeSelectIndex()) {
                    showCaptionEditPop(captionDesc, position, 0);
                } else {
                    mPresenter.seekToCaptionStartTime(captionDesc);
                }
                mCutCaptionAdapter.setBeforeSelectIndex(position);
            }
        });
        mFragmentRecyclerView.setAdapter(mCutCaptionAdapter);
        mCutCaptionAdapter.setNewData(mCaptionData);
        mPresenter.getCaptionBitmap(mCaptionData);
    }

    /**
     * 外部更新字幕数据
     * Update subtitle data externally
     *
     * @param captionClipList captionClipList
     */
    public void onCaptionDataUpDate(List<TemplateCaptionDesc> captionClipList) {
        this.mCaptionData = captionClipList;
        if (null != mCutCaptionAdapter) {
            mCutCaptionAdapter.setNewData(mCaptionData);
        }
        mPresenter.getCaptionBitmap(captionClipList);
    }

    /**
     * 显示字幕编辑弹窗
     * Displays a caption edit pop
     */
    private void showCaptionEditPop(final TemplateCaptionDesc captionDesc, final int position, final int index) {
        if (mCaptionEditPop == null) {
            mCaptionEditPop = CutCaptionEditPop.create(getActivity());
        }
        mCaptionEditPop.setEventListener(new CutCaptionEditPop.EventListener() {
            @Override
            public void onConfirm(String text) {
                mPresenter.changeCaptionText(captionDesc, text, mTemplateType);
                mCutCaptionAdapter.refreshTextByIndex(text, position);
                mPresenter.seekToCaptionStartTime(captionDesc);
            }
        });
        mCaptionEditPop.setCaptionText(captionDesc.text);
        mCaptionEditPop.show();
    }

    /**
     * Gets caption bitmap.
     * 获得字幕位图
     */
    @Override
    public void getCaptionBitmap(int index) {
        if (mCutCaptionAdapter != null) {
            mCutCaptionAdapter.notifyItemChanged(index);
        }
    }

    /**
     * Gets video data.
     * 获取视频数据
     *
     * @param templateClipList the template clip list
     */
    @Override
    public void getVideoData(List<MeicamNvsTemplateFootageCorrespondingClipInfo> templateClipList) {
        mVideoData = templateClipList;
        mCutVideoAdapter = new CutVideoAdapter();
        mCutVideoAdapter.setTemplateType(mTemplateType);
        mCutVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, final View view, final int position) {
                mCutVideoAdapter.setSelectIndex(position);
                selectedClipInfo = mVideoData.get(position);
                int beforeSelectIndex = mCutVideoAdapter.getBeforeSelectIndex();
                final NvsVideoClip videoClip = EditorController.getInstance().getVideoClipByTemplateFootageCorrespondingClipInfo(selectedClipInfo);
                if (videoClip == null) {
                    LogUtils.e("videoClip==null");
                    return;
                }
                if (!selectedClipInfo.canReplace) {
                    mCutVideoAdapter.setBeforeSelectIndex(position);
                    EditorController.getInstance().seekTimeline(selectedClipInfo.getRealInpoint());
                    if (mSeekPositionListener != null) {
                        mSeekPositionListener.seekPosition(selectedClipInfo.getRealInpoint());
                    }
                    return;
                }
                EditorController.getInstance().seekTimeline(selectedClipInfo.getRealInpoint());
                if (mSeekPositionListener != null) {
                    mSeekPositionListener.seekPosition(selectedClipInfo.getRealInpoint());
                }
                mFragmentRecyclerView.scrollToPosition(position);
                if (beforeSelectIndex == position) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showVideoEditPop(view, videoClip);
                        }
                    }, 100);
                }
                mCutVideoAdapter.setBeforeSelectIndex(position);
            }
        });
        mFragmentRecyclerView.setAdapter(mCutVideoAdapter);
        mCutVideoAdapter.setNewData(templateClipList);
    }

    private VideoEditPop mVideoEditPop;

    private void showVideoEditPop(View view, final NvsVideoClip videoClip) {
        if (getContext() == null) {
            return;
        }
        mVideoEditPop = VideoEditPop.create(view, getContext());
        mVideoEditPop.setEventListener(new VideoEditPop.EventListener() {
            @Override
            public void onReplace() {
                if (getActivity() instanceof CutSameEditorActivity) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(SELECTED_TYPE, TYPE_ONE_FINISH);
                    bundle.putInt(CustomConstants.TEMPLATE_REPLACE_CLIP_INDEX, selectedClipInfo.clipIndex);
                    bundle.putString(CustomConstants.TEMPLATE_TYPE, mTemplateType);
                    bundle.putBoolean(CustomConstants.TEMPLATE_IS_REPLACE_TYPE, true);
                    TemplateClip templateClip = ((CutSameEditorActivity) getActivity()).getTemplateClip(
                            selectedClipInfo.getClipIndexInTimelineList(), selectedClipInfo.getClipTrackIndexInTimelineList(),
                            selectedClipInfo.trackIndex, selectedClipInfo.inpoint);
                    if (null != templateClip) {

                        templateClip = templateClip.copy();
                        templateClip.setFilePath("");
                        templateClip.setReversePath("");
                        bundle.putParcelable(TEMPLATE_CLIP, templateClip);
                        AppManager.getInstance().jumpActivityForResult(getActivity(), MaterialSelectActivity.class, bundle, REQUEST_CODE_1);
                    }
                }

            }

            @Override
            public void onCut() {
                if (!selectedClipInfo.canReplace) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(TailorFreeActivity.VIDEO_PATH, videoClip.getFilePath());
                int clipType = StreamingConstant.VideoClipType.VIDEO_CLIP_TYPE_AV;
                long trimIn = videoClip.getTrimIn();
                if (videoClip.getVideoType() != 0) {
                    trimIn = 0;
                    clipType = StreamingConstant.VideoClipType.VIDEO_CLIP_TYPE_IMAGE;
                }
                bundle.putInt(TailorFreeActivity.VIDEO_TYPE, clipType);
                bundle.putLong(TailorFreeActivity.START_TRIM, trimIn);
                if (mTemplateType.equals(Template.TYPE_TEMPLATE_STANDER) ||
                        mTemplateType.equals(Template.TYPE_TEMPLATE_AE)) {
                    bundle.putLong(TailorFreeActivity.VIDEO_LIMIT, (selectedClipInfo.outpoint - selectedClipInfo.inpoint));
                    AppManager.getInstance().jumpActivityForResult(getActivity(), TailorActivity.class, bundle, CutSameEditorActivity.INTENT_FLAG);
                } else if (mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)) {
                    bundle.putLong(TailorFreeActivity.END_TRIM, videoClip.getTrimOut());
                    bundle.putInt(TailorFreeActivity.CLIP_INDEX, selectedClipInfo.clipIndex);
                    AppManager.getInstance().jumpActivityForResult(getActivity(), TailorFreeActivity.class, bundle, CutSameEditorActivity.INTENT_FLAG);
                }
            }
        });
        mVideoEditPop.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_1 && CutEditorVpPresenter.VIDEO.equals(mType)) {
            mVideoEditPop.dismiss();
            if (data != null) {
                TemplateClip templateClip = data.getParcelableExtra(TEMPLATE_CLIP);
                mCutVideoAdapter.notifyItemChanged(mCutVideoAdapter.getSelectIndex());
                mPresenter.dealVideoReplace(templateClip);
                return;
               /* for (int i = 0; i < mVideoData.size(); i++) {
                    MeicamNvsTemplateFootageCorrespondingClipInfo clipInfo = mVideoData.get(i);
                    if (templateClip.getFootageId().equals(clipInfo.getFootageID())) {
                        TemplateClip clip = ((CutSameEditorActivity) getActivity()).getTemplateClip(
                                clipInfo.getClipIndexInTimelineList(),
                                clipInfo.getClipTrackIndexInTimelineList(),
                                clipInfo.trackIndex, clipInfo.inpoint);
                        clip.setFilePath(templateClip.getFilePath());
                        mCutVideoAdapter.notifyItemChanged(i);
                        mPresenter.dealVideoReplace(clip);
                    }
                }*/
            }
        }
    }

    /**
     * Need seek position.
     * 需要找位置
     *
     * @param position the position
     */
    @Override
    public void needSeekPosition(long position, List<PointF> pointFList) {
        if (mSeekPositionListener != null) {
            mSeekPositionListener.seekPosition(position);
        }
       /* if (CutEditorVpPresenter.CAPTION.equals(mType)) {
            if (getActivity() instanceof CutSameEditorActivity) {
                ((CutSameEditorActivity) getActivity()).showCaptionBox(pointFList);
            }
        }*/
    }

    /**
     * Clear select on play.
     * 清除播放时的选择
     */
    public void clearSelectOnPlay() {
        if (mCutCaptionAdapter != null) {
            mCutCaptionAdapter.setSelectIndex(-1);
            mCutCaptionAdapter.setBeforeSelectIndex(-2);
        }

        if (mCutVideoAdapter != null) {
            mCutVideoAdapter.setSelectIndex(-1);
            mCutVideoAdapter.setBeforeSelectIndex(-2);
        }
    }

    /**
     * On destroy view.
     * 摧毁视图
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 替换素材之后需要更新素材的入点出点
     * After replacing the material, the in and out points of the material need to be updated
     *
     * @param mTemplateClips mTemplateClips
     */
    public void updateVideoData(List<TemplateClip> mTemplateClips) {
        if (null != mVideoEditPop) {
            mVideoEditPop.dismiss();
        }
        if (CommonUtils.isEmpty(mTemplateClips) || CommonUtils.isEmpty(mVideoData)) {
            return;
        }
        for (int i = 0; i < mVideoData.size(); i++) {
            NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo nvsTemplateFootageCorrespondingClipInfo = mVideoData.get(i);
            nvsTemplateFootageCorrespondingClipInfo.inpoint = mTemplateClips.get(i).getInPoint();
            nvsTemplateFootageCorrespondingClipInfo.outpoint = mTemplateClips.get(i).getOutPoint();
            nvsTemplateFootageCorrespondingClipInfo.trimOut = mTemplateClips.get(i).getTrimOut();
            nvsTemplateFootageCorrespondingClipInfo.trimIn = mTemplateClips.get(i).getTrimIn();
        }
        if (null != mCutVideoAdapter) {
            mCutVideoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * The interface Seek position listener.
     * 接口查找位置侦听器
     */
    public interface SeekPositionListener {
        /**
         * Seek position.
         * 寻找位置
         *
         * @param position the position
         */
        void seekPosition(long position);
    }
}
