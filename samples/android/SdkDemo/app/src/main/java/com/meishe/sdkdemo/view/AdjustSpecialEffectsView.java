package com.meishe.sdkdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsExpressionParam;
import com.meishe.base.view.MagicProgress;
import com.meishe.makeup.makeup.ColorData;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.bean.AdjustSpecialEffectsInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2022/8/16 上午11:23
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class AdjustSpecialEffectsView extends LinearLayout {

    private AdjustAdapter mAdjustAdapter;

    public AdjustSpecialEffectsView(Context context) {
        this(context, null);
    }

    public AdjustSpecialEffectsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdjustSpecialEffectsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View inflate = layoutInflater.inflate(R.layout.view_adjust_special_effects, this);
        RecyclerView recyclerView = inflate.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                //处理RecyclerView的item中嵌套可消费滑动事件的view时的滑动冲突问题
                View childViewUnder = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childViewUnder != null) {
                    AdjustHolder baseViewHolder = (AdjustHolder) rv.getChildViewHolder(childViewUnder);
                    recyclerView.requestDisallowInterceptTouchEvent(baseViewHolder.isTouchInWipeSpace(e.getRawX(), e.getRawY()));
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        mAdjustAdapter = new AdjustAdapter();
        recyclerView.setAdapter(mAdjustAdapter);
    }


    public void setData(List<AdjustSpecialEffectsInfo> mData) {
        if (mAdjustAdapter != null) {
            mAdjustAdapter.setData(mData);
            mAdjustAdapter.notifyDataSetChanged();
        }
    }

    public List<AdjustSpecialEffectsInfo> getData() {
        if (null == mAdjustAdapter) {
            return null;
        }
        return mAdjustAdapter.getData();
    }


    class AdjustAdapter extends RecyclerView.Adapter<AdjustHolder> {
        private List<AdjustSpecialEffectsInfo> mData;

        public void setData(List<AdjustSpecialEffectsInfo> mData) {
            this.mData = mData;
        }

        public List<AdjustSpecialEffectsInfo> getData() {
            return mData;
        }

        @NonNull
        @Override
        public AdjustHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_adjust_effects, parent, false);
            return new AdjustHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdjustHolder holder, int position) {
            if (null == mData) {
                return;
            }
            AdjustSpecialEffectsInfo adjustSpecialEffectsInfo = mData.get(position);
            if (null == adjustSpecialEffectsInfo) {
                return;
            }
            holder.mMagicProgress.setShowTextEnable(true);
            holder.mTypeName.setText(adjustSpecialEffectsInfo.getAdjustmentCategoryName());
            if (adjustSpecialEffectsInfo.getType() == NvsExpressionParam.TYPE_COLOR) {
                holder.mMagicProgress.setVisibility(GONE);
                holder.mCbbAdjust.setVisibility(VISIBLE);
                holder.mCbbAdjust.setColors(Integer.MIN_VALUE);
                holder.mCbbAdjust.setOnColorChangedListener(new ColorSeekBar.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        if (onItemProgressChangeListener != null) {
                            ColorData colorData = new ColorData(holder.mCbbAdjust.rawX, -1, color);
                            onItemProgressChangeListener.onColorChange(adjustSpecialEffectsInfo, colorData.color);
                        }
                    }

                    @Override
                    public void onColorChanged(int color, float progress) {

                    }
                });

            } else {
                holder.mMagicProgress.setVisibility(VISIBLE);
                holder.mCbbAdjust.setVisibility(GONE);
                holder.mMagicProgress.setMax(100);
                holder.mMagicProgress.setMin(0);
                holder.mMagicProgress.setPointEnable(true);
                holder.mMagicProgress.setShowBreak(true);
                holder.mMagicProgress.setBreakProgress(0);

                float maxVal = adjustSpecialEffectsInfo.getMaxVal();
                float minVal = adjustSpecialEffectsInfo.getMinVal();
                float defVal = adjustSpecialEffectsInfo.getDefVal();
                float strength = adjustSpecialEffectsInfo.getStrength();

                float diff = maxVal - minVal;
                float v = defVal - minVal;
                float defaultProgress = v / diff * 100;
                float currProgress = (strength - minVal) / diff * 100;

                holder.mMagicProgress.setPointProgress((int) defaultProgress);
                holder.mMagicProgress.setProgress((int) currProgress);
                holder.mTypeName.setText(adjustSpecialEffectsInfo.getAdjustmentCategoryName());

                holder.mMagicProgress.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
                    @Override
                    public void onProgressChange(int progress, boolean fromUser) {
                        if (onItemProgressChangeListener != null) {
                            onItemProgressChangeListener.onProgressChange(adjustSpecialEffectsInfo, progress, fromUser);
                        }
                    }

                    @Override
                    public void onProgressChangeStarted(int progress, boolean fromUser) {
                        if (onItemProgressChangeListener != null) {
                            onItemProgressChangeListener.onProgressChangeStarted(progress, fromUser);
                        }
                    }

                    @Override
                    public void onProgressChangeFinish(int progress, boolean fromUser) {
                        if (onItemProgressChangeListener != null) {
                            onItemProgressChangeListener.onProgressChangeFinish(progress, fromUser);
                        }
                    }
                });

                holder.mMagicProgress.setOnTouchStateChangeListener(new MagicProgress.OnTouchStateChangeListener() {
                    @Override
                    public void onTouchUp(boolean touchUpFlag) {
                        if (onItemProgressChangeListener != null) {
                            onItemProgressChangeListener.onTouchUp(touchUpFlag);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    private static class AdjustHolder extends RecyclerView.ViewHolder {
        private final TextView mTypeName;
        private final MagicProgress mMagicProgress;
        private final ColorSeekBar mCbbAdjust;

        public AdjustHolder(@NonNull View itemView) {
            super(itemView);
            mTypeName = itemView.findViewById(R.id.type_name);
            mMagicProgress = itemView.findViewById(R.id.mp_filter);
            mCbbAdjust = itemView.findViewById(R.id.cbb_adjust);
        }

        public boolean isTouchInWipeSpace(float x, float y) {
            int[] wh = new int[2];
            itemView.getLocationOnScreen(wh);
            int width = itemView.getMeasuredWidth();
            int height = itemView.getMeasuredHeight();
            return x >= wh[0] && x <= wh[0] + width && y >= wh[1] && y <= wh[1] + height;
        }
    }

    private OnItemProgressChangeListener onItemProgressChangeListener;

    public void setOnItemProgressChangeListener(OnItemProgressChangeListener onItemProgressChangeListener) {
        this.onItemProgressChangeListener = onItemProgressChangeListener;
    }

    public interface OnItemProgressChangeListener {

        void onProgressChange(AdjustSpecialEffectsInfo adjustSpecialEffectsInfo, int progress, boolean fromUser);

        default void onProgressChangeStarted(int progress, boolean fromUser){}
        default void onProgressChangeFinish(int progress, boolean fromUser){}

        default void onTouchUp(boolean touchUpFlag) {

        }

        void onColorChange(AdjustSpecialEffectsInfo effectsInfo, int color);
    }

}
