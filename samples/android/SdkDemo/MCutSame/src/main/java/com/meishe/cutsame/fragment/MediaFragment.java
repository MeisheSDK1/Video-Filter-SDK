package com.meishe.cutsame.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.meishe.base.bean.MediaData;
import com.meishe.base.bean.MediaSection;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.utils.SizeUtils;
import com.meishe.cutsame.R;
import com.meishe.cutsame.fragment.adapter.MediaSelectAdapter;
import com.meishe.cutsame.fragment.iview.MediaView;
import com.meishe.cutsame.fragment.presenter.MediaPresenter;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.decoration.GridSectionAverageGapItemDecoration;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.meishe.cutsame.util.CustomConstants.MEDIA_TYPE;


/**
 * author : lhz
 * date   : 2020/10/20
 * desc   :媒体列表页面
 * Media List page
 */
public class MediaFragment extends BaseMvpFragment<MediaPresenter> implements MediaView {
    private RecyclerView mRvMediaList;
    private TextView mTvNothing;

    private MediaSelectAdapter mAdapter;
    private MediaChangeListener mListener;
    private boolean isNeedHideTag;

    public MediaFragment() {
    }

    public MediaFragment setNeedHideTag(boolean needHideTag) {
        isNeedHideTag = needHideTag;
        return this;
    }

    /**
     * Create media fragment.
     * 创建媒体片段
     *
     * @param type     the type 类型
     * @param listener the listener 监听
     * @return the media fragment 媒体片段
     */
    public static MediaFragment create(int type, MediaChangeListener listener) {
        MediaFragment fragment = new MediaFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MEDIA_TYPE, type);
        fragment.setArguments(bundle);
        fragment.setOnMediaChangeListener(listener);
        return fragment;
    }

    private void setOnMediaChangeListener(MediaChangeListener listener) {
        mListener = listener;
    }

    /**
     * Bind layout int.
     * 绑定布局
     *
     * @return the int
     */
    @Override
    protected int bindLayout() {
        return R.layout.fragment_template_media;
    }

    /**
     * On lazy load.
     * 延迟加载
     */
    @Override
    protected void onLazyLoad() {
    }

    /**
     * Init view.
     * 初始化布局
     *
     * @param view the view
     */
    @Override
    protected void initView(View view) {
        mRvMediaList = view.findViewById(R.id.rv_media_list);
        mTvNothing = view.findViewById(R.id.tv_nothing);
        int w = SizeUtils.dp2px(6);
        FrameLayout.LayoutParams layoutParams = null;
        if (mRvMediaList.getLayoutParams() != null) {
            layoutParams = (FrameLayout.LayoutParams) mRvMediaList.getLayoutParams();
        }
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        /*
         * 两边是两倍的item间距
         * It's going to be twice the item spacing on both sides
         * */
        layoutParams.width = ScreenUtils.getScreenWidth() - 4 * w;
        layoutParams.leftMargin = 2 * w;
        layoutParams.rightMargin = 2 * w;
        int SPAN_COUNT = 4;
        int itemSize = (int) ((layoutParams.width - (SPAN_COUNT - 1) * w) / SPAN_COUNT * 1f);
        mRvMediaList.setLayoutParams(layoutParams);
        mRvMediaList.addItemDecoration(new GridSectionAverageGapItemDecoration(6, 6, 6));
        mRvMediaList.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
        mAdapter = new MediaSelectAdapter(itemSize);
        mAdapter.setNeedHideTag(isNeedHideTag);
        mRvMediaList.setAdapter(mAdapter);
        initListener();
    }

    /**
     * Init data.
     * 初始化数据
     */
    @Override
    protected void initData() {
        int type = MediaData.TYPE_VIDEO;
        if (getArguments() != null) {
            type = getArguments().getInt(MEDIA_TYPE);
        }
        mPresenter.getMediaList(type);
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dealSelectedState(position, true);
            }
        });
    }

    /**
     * 处理选中状态，内部不允许取消选中，外部可以取消选中
     * Handle the checked status. Internal deselect is not allowed. External deselect is allowed
     *
     * @param position    the position 位置
     * @param mediaChange the media change 媒介变化
     */
    public void dealSelectedState(int position, boolean mediaChange) {
        //MediaData item = mAdapter.getItem(position);
        MediaSection section = mAdapter.getItem(position);
        if (section != null && !section.isHeader) {
            if (mediaChange && section.t.isState()) {
                /*
                 * 不允许再次点击取消选中，只能由外部（activity）取消
                 * You are not allowed to click deselect again, only the external activity can deselect
                 * */
                if (mListener != null) {
                    mListener.onMediaChange(section.t);
                }
                return;
            }
            if (mediaChange && mListener != null && mListener.onMediaChange(section.t)) {
                mPresenter.dealSelected(section.t, position);
            } else if (!mediaChange) {
                mPresenter.dealSelected(section.t, position);
            }
        }
    }

    /**
     * 处理选中，已选中的不做处理
     * Process the selected, and do not process the selected
     *
     * @param mediaPath the media path 媒体路径
     * @return the int
     */
    public int dealSelected(String mediaPath) {
        if (!TextUtils.isEmpty(mediaPath)) {
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                MediaSection section = mAdapter.getItem(i);
                if (section != null && !section.isHeader && mediaPath.equals(section.t.getThumbPath())) {
                    if (!section.t.isState()) {
                        mPresenter.dealSelected(section.t, i);
                    }
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * On media back.
     * 媒体
     *
     * @param mediaData the media data
     */
    @Override
    public void onMediaBack(List<MediaSection> mediaData) {
        if (mediaData.size() > 0) {
            mAdapter.setNewData(null);
        }
        mAdapter.setNewData(mediaData);
        if (mAdapter.getData().size() > 0) {
            mTvNothing.setVisibility(View.INVISIBLE);
            mRvMediaList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemChange(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * The interface Media change listener.
     * 媒体更改监听器接口
     */
    public interface MediaChangeListener {
        /**
         * On media change boolean.
         * 媒体的变化
         *
         * @param mediaData the media data 媒体数据
         * @return the boolean
         */
        boolean onMediaChange(MediaData mediaData);
    }

}
