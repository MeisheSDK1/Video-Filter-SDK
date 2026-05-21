package com.meishe.third.adpater.loadmore;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import com.meishe.third.adpater.BaseViewHolder;


/**
 * Created by BlingBling on 2016/11/11.
 * 加载更多的视图类
 * Load more view classes
 */
public abstract class LoadMoreView {

    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_END = 4;

    private int mLoadMoreStatus = STATUS_DEFAULT;
    private boolean mLoadMoreEndGone = false;

    /**
     * Sets load more status.
     * 设置加载更多状态
     * @param loadMoreStatus the load more status 加载更多状态
     */
    public void setLoadMoreStatus(int loadMoreStatus) {
        this.mLoadMoreStatus = loadMoreStatus;
    }

    /**
     * Gets load more status.
     * 获得更多的加载状态
     * @return the load more status 加载更多状态
     */
    public int getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    /**
     * Convert.
     * 转换
     * @param holder the holder
     */
    public void convert(BaseViewHolder holder) {
        if (mLoadMoreStatus == STATUS_LOADING) {
            visibleLoading(holder, true);
            visibleLoadFail(holder, false);
            visibleLoadEnd(holder, false);
        } else if (mLoadMoreStatus == STATUS_FAIL) {
            visibleLoading(holder, false);
            visibleLoadFail(holder, true);
            visibleLoadEnd(holder, false);
        } else if (mLoadMoreStatus == STATUS_END) {
            visibleLoading(holder, false);
            visibleLoadFail(holder, false);
            visibleLoadEnd(holder, true);
        } else if (mLoadMoreStatus == STATUS_DEFAULT) {
            visibleLoading(holder, false);
            visibleLoadFail(holder, false);
            visibleLoadEnd(holder, false);
        }
    }

    private void visibleLoading(BaseViewHolder holder, boolean visible) {
        holder.setGone(getLoadingViewId(), visible);
    }

    private void visibleLoadFail(BaseViewHolder holder, boolean visible) {
        holder.setGone(getLoadFailViewId(), visible);
    }

    private void visibleLoadEnd(BaseViewHolder holder, boolean visible) {
        final int loadEndViewId = getLoadEndViewId();
        if (loadEndViewId != 0) {
            holder.setGone(loadEndViewId, visible);
        }
    }

    public final void setLoadMoreEndGone(boolean loadMoreEndGone) {
        this.mLoadMoreEndGone = loadMoreEndGone;
    }

    public final boolean isLoadEndMoreGone() {
        if (getLoadEndViewId() == 0) {
            return true;
        }
        return mLoadMoreEndGone;
    }

    /**
     * No more data is hidden
     * 不再隐藏任何数据
     * @return true for no more data hidden load more true表示没有更多数据隐藏，加载更多数据
     */
    @Deprecated
    public boolean isLoadEndGone() {
        return mLoadMoreEndGone;
    }

    /**
     * load more layout
     * 加载更多的布局
     * @return layout id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * loading view
     * 加载视图
     * @return loading view id 加载视图id
     */
    protected abstract
    @IdRes
    int getLoadingViewId();

    /**
     * load fail view
     * 加载失败的观点
     * @return load fail view id 加载失败视图id
     */
    protected abstract
    @IdRes
    int getLoadFailViewId();

    /**
     * load end view, you can return 0
     * 加载结束视图，你可以返回0
     * @return load end view id 加载端视图id
     */
    protected abstract
    @IdRes
    int getLoadEndViewId();
}
