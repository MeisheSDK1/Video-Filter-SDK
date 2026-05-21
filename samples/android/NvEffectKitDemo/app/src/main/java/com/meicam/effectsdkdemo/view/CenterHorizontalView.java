package com.meicam.effectsdkdemo.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.meishe.libbase.util.ScreenUtils;


public class CenterHorizontalView extends RecyclerView {

    /**
     * 一个屏幕中显示多少个item，必须为奇数
     * How many items are displayed on a screen, must be odd
     */
    private int itemCount = 5;
    /**
     * 当前被选中的位置
     * Currently selected position
     */
    private int selectPos = 0;
    private int oldSelectedPos = 0;

    private int deltaX;

    private OnSelectedPositionChangedListener listener;

    private LinearLayoutManager linearLayoutManager;
    private WrapperAdapter wrapAdapter;
    private Adapter adapter;

    public CenterHorizontalView(@NonNull Context context) {
        super(context);
    }

    public CenterHorizontalView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(final Adapter adapter) {
        this.adapter = adapter;
        this.wrapAdapter = new WrapperAdapter(adapter, getContext(), itemCount);
//        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
//
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                wrapAdapter.notifyDataSetChanged();
//                if (listener != null) {
//                    listener.selectedPositionChanged(selectPos);
//                }
//            }
//
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                wrapAdapter.notifyDataSetChanged();
//                if (positionStart <= selectPos && listener != null) {
//                    listener.selectedPositionChanged(selectPos);
//                }
//            }
//
//            @Override
//            public void onItemRangeRemoved(int positionStart, int itemCount) {
//                wrapAdapter.notifyDataSetChanged();
//                if (positionStart <= selectPos && listener != null) {
//                    correctDeltax(adapter);
//                    listener.selectedPositionChanged(selectPos);
//                } else {
//                    correctDeltax(adapter);
//                }
//            }
//        });
        deltaX = 0;
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(getContext());
        }
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        super.setLayoutManager(linearLayoutManager);
        super.setAdapter(this.wrapAdapter);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof LinearLayoutManager)) {
            throw new IllegalStateException("The LayoutManager here must be LinearLayoutManager!");
        }
        this.linearLayoutManager = (LinearLayoutManager) layout;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        deltaX += dx;
        calculateSelectedPos();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_IDLE) {
            if (wrapAdapter == null) {
                return;
            }
            int itemWidth = wrapAdapter.getItemWidth();
            int headerFooterWidth = wrapAdapter.getHeaderFooterWidth();
            if (itemWidth == 0 || headerFooterWidth == 0) {
                /*
                 * 此时adapter还没有准备好，忽略此次调用
                 * At this point, the adapter is not ready, ignore this call
                 * */
                return;
            }
            /*
             * 超出上个item的位置
             * Beyond the last item
             * */
            int overLastPosOffset = deltaX % itemWidth;
            if (overLastPosOffset == 0) {
                /*
                 * 刚好处于一个item选中位置，无需滑动偏移纠正
                 * Just right in a selected item position, no sliding offset correction required
                 * */
            } else if (Math.abs(overLastPosOffset) <= itemWidth / 2) {
                smoothScrollBy(-overLastPosOffset, 0);
            } else if (overLastPosOffset > 0) {
                smoothScrollBy((itemWidth - overLastPosOffset), 0);
            } else {
                smoothScrollBy(-(itemWidth + overLastPosOffset), 0);
            }
            calculateSelectedPos();
            if(oldSelectedPos == selectPos) {
                return;
            }
            /*
             * 此处通知刷新是为了重新绘制之前被选中的位置以及刚刚被选中的位置
             * The notification refresh here is to redraw the previously selected position and the position just selected.
             * */
            wrapAdapter.notifyItemChanged(oldSelectedPos + 1);
            wrapAdapter.notifyItemChanged(selectPos + 1);
            oldSelectedPos = selectPos;
            if (listener != null) {
                listener.selectedPositionChanged(selectPos);
            }
        }
    }

    /**
     * 删除item后偏移距离可能需要重新计算，从而保证selectPos的正确
     * The offset distance may need to be recalculated after deleting the item to ensure the correct selection of Pos
     *
     * @param adapter
     */
    private void correctDeltax(Adapter adapter) {
        if (adapter.getItemCount() <= selectPos) {
            deltaX -= wrapAdapter.getItemWidth() * (selectPos - adapter.getItemCount() + 1);
        }
        calculateSelectedPos();
    }

    private void calculateSelectedPos() {
        int itemWidth = wrapAdapter.getItemWidth();
        if (deltaX > 0 && itemWidth>0) {
            selectPos = (deltaX + itemWidth / 2) / itemWidth;
        }
        Log.d("mgj", "calculateSelectedPos: deltaX:" + deltaX + " selectPos:" + selectPos);
    }

    class WrapperAdapter extends Adapter {
        private Context context;
        private Adapter adapter;
        private int itemCount;
        private static final int HEADER_FOOTER_TYPE = -1;
        private View itemView;
        /**
         * 头部或尾部的宽度
         * Head or tail width
         */
        private int headerFooterWidth;

        /**
         * 每个item的宽度
         * The width of each item
         */
        private int itemWidth;

        public WrapperAdapter(Adapter adapter, Context context, int itemCount) {
            this.adapter = adapter;
            this.context = context;
            this.itemCount = itemCount;
            if (adapter instanceof IHorizontalView) {
                itemView = ((IHorizontalView) adapter).getItemView();
            } else {
                throw new RuntimeException(adapter.getClass().getSimpleName() + " should implements com.jianglei.view.AutoLocateHorizontalView.IHorizontalView !");
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int screenWidth = ScreenUtils.getScreenWidth(context);
            int width = screenWidth / itemCount;
            if (viewType == HEADER_FOOTER_TYPE) {
                View view = new View(context);
                headerFooterWidth = screenWidth / 2 - (width) / 2;
                LayoutParams params = new LayoutParams(headerFooterWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(params);
                return new HeaderFooterViewHolder(view);
            }
            ViewHolder holder = adapter.onCreateViewHolder(parent, viewType);
            itemView = ((IHorizontalView) adapter).getItemView();

            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (params != null) {
                params.width = width;
                itemWidth = width;
                /*
                 * 滑动项的宽度
                 * The width of the slider
                 * */
//                params.height=width;
                itemView.setLayoutParams(params);
            }
            Log.d("mgj", "getItemCount width: " + width);
            return holder;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (!isHeaderOrFooter(position)) {
                adapter.onBindViewHolder(holder, position - 1);
                if (selectPos == position - 1) {
                    ((IHorizontalView) adapter).onViewSelected(true, position - 1, holder, itemWidth);
                } else {
                    ((IHorizontalView) adapter).onViewSelected(false, position - 1, holder, itemWidth);
                }
            }
        }


        @Override
        public int getItemCount() {
            return adapter.getItemCount() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == getItemCount() - 1) {
                return HEADER_FOOTER_TYPE;
            }
            return adapter.getItemViewType(position - 1);
        }


        private boolean isHeaderOrFooter(int pos) {
            if (pos == 0 || pos == getItemCount() - 1) {
                return true;
            }
            return false;
        }

        public int getHeaderFooterWidth() {
            return headerFooterWidth;
        }

        public int getItemWidth() {
            return itemWidth;
        }

        class HeaderFooterViewHolder extends ViewHolder {
            HeaderFooterViewHolder(View itemView) {
                super(itemView);
            }
        }


    }

    public interface IHorizontalView {
        /**
         * 获取item的根布局
         * Get the root layout of the item
         */
        View getItemView();

        /**
         * 当item被选中时会触发这个回调，可以修改被选中时的样式
         * This callback will be triggered when item is selected, you can modify the style when selected
         *
         * @param isSelected 是否被选中；Whether selected
         * @param pos        当前view的位置；Current view position
         * @param holder
         * @param itemWidth  当前整个item的宽度；The current width of the entire item
         */
        void onViewSelected(boolean isSelected, int pos, ViewHolder holder, int itemWidth);
    }

    /***
     * 选中位置改变时的监听
     * Listen when selected position changes
     */
    public interface OnSelectedPositionChangedListener {
        void selectedPositionChanged(int pos);
    }

    public void setOnSelectedPositionChangedListener(OnSelectedPositionChangedListener listener) {
        this.listener = listener;
    }
}
