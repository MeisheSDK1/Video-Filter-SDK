package com.meishe.sdkdemo.capture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.meishe.arscene.bean.BaseFxInfo;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;


public class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.ViewHolder> {

    private List<IFxInfo> mDataList;
    private int mSelectedPos = Integer.MAX_VALUE;
    private Context mContext;
    private OnItemClickListener mClickListener;
    private boolean mIsEnable = true;
    private boolean isBeautyShape = false;

    public static final int POS_BEAUTY_STRENGTH_0 = 0;
//    public static final int POS_BEAUTY_WHITING = 3;
    public static final int POS_BEAUTY_STRENGTH = 1;
    public static final int POS_BEAUTY_REDDING_2 = 2;
//    public static final int POS_BEAUTY_ADJUST_COLOR = 5;
    public static final int POS_BEAUTY_SHARPEN = 6;
    private boolean needFirstBack = false;

    private Switch mSwitchView;
    /**
     * 两种类型
     * 1.美型的类型，点击返回选择类型的列表
     * 2.美型的item操作，点击设置值
     *
     * Two types
     * 1. The type of American type, click to return to the list of selected types
     *2. For the item operation of American type, click the setting value
     */
    public static final int TYPE_KIND_BACK = 1;
    public static final int TYPE_KIND_ITEM = 2;

    public static final int TYPE_POINT = 3;

    private BeautyShapeDataKindItem shapeDataKindItem;

    public ShapeAdapter(Context context, List<IFxInfo> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    public void setIsBeautyShape(boolean isBeautyShape) {
        this.isBeautyShape = isBeautyShape;
    }

    public void setDataList(List<IFxInfo> data) {
        this.mDataList = data;
        notifyDataSetChanged();
    }

    public void setEnable(boolean enable) {
        mIsEnable = enable;
        notifyDataSetChanged();
    }


    public void setSwitch(Switch smallShapeSwitch) {
        mSwitchView = smallShapeSwitch;
    }

    public void setSelectPos(int pos) {
        mSelectedPos = pos;
        Log.e("tell", "setSelectPos position = " + pos);
        notifyDataSetChanged();
    }

    public int getSelectPos() {
        return mSelectedPos;
    }

    public IFxInfo getSelectItem() {
        if (mDataList != null && mSelectedPos >= 0 && mSelectedPos < mDataList.size()) {
            return mDataList.get(mSelectedPos);
        }
        return null;
    }

    public IFxInfo getItem(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }
        if (mDataList == null) {
            return null;
        }
        for (int i = 0; i < mDataList.size(); i++) {
            IFxInfo beautyShapeDataItem = mDataList.get(i);
            if (beautyShapeDataItem == null) {
                continue;
            }
            String name = beautyShapeDataItem.getName();
            if (tag.equals(name)) {
                return beautyShapeDataItem;
            }
        }
        return null;
    }

    public List<IFxInfo> getItems() {
        return mDataList;
    }

    /**
     * 设置选中的美型类型
     *Set the selected beauty type
     * @param shapeDataKindItem
     */
    public void setSelectedKind(BeautyShapeDataKindItem shapeDataKindItem) {
        this.shapeDataKindItem = shapeDataKindItem;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && null != shapeDataKindItem) {
            return TYPE_KIND_BACK;
        } else {
            IFxInfo info = mDataList.get(position);
            if (TextUtils.equals(info.getType(), BaseFxInfo.TYPE_PLACE_HOLDER)) {
                return TYPE_POINT;
            }
            return TYPE_KIND_ITEM;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void updateDataList(ArrayList dataList) {
        mDataList.clear();
        mDataList.addAll(dataList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        View view;
        switch (viewType) {
            case TYPE_POINT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beauty_shape_point_item, parent, false);
                holder = new ViewHolder(view);
                break;
            case TYPE_KIND_BACK:
            case TYPE_KIND_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beauty_shape_item, parent, false);
                holder = new ViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final int itemType = getItemViewType(position);
        if (itemType == TYPE_KIND_BACK) {
            if (null != shapeDataKindItem) {
                int padding = ScreenUtils.dip2px(mContext, 10);
//                holder.shape_icon.setPadding(padding, padding, padding, padding);
                holder.shape_icon.setImageResource(com.meishe.base.R.mipmap.beauty_back);
                holder.shape_name.setText(shapeDataKindItem.getName());
//                holder.shape_name.setTextColor(Color.BLACK);
//                holder.shape_icon_layout.setBackgroundResource(R.drawable.shape_beauty_menu_select);
                holder.shape_icon_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mClickListener) {
                            mClickListener.onItemClick(v, position, shapeDataKindItem.getName());
                        }
                    }
                });
            }
        } else if (itemType == TYPE_POINT) {
            if (mIsEnable) {
                holder.itemView.setAlpha(1f);
            } else {
                holder.itemView.setAlpha(0.5f);
            }
        } else {
            final IFxInfo item = mDataList.get(position);
            if (item.getStrength() != 0) {
                holder.blue_point.setVisibility(View.VISIBLE);
            } else {
                holder.blue_point.setVisibility(View.GONE);
            }
            holder.shape_icon.setImageResource(item.getResourceId());
            holder.shape_name.setText(item.getName());
            if (mIsEnable) {
                holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.black_alfph));
            } else {
                holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.ms_disable_color));
            }
            if (mIsEnable && mSelectedPos == position) {
                holder.shape_icon.setSelected(true);
                holder.shape_name.setTextColor(Color.parseColor("#CC4A90E2"));
                holder.shape_icon_layout.setAlpha(1.0f);
                holder.shape_name.setAlpha(1.0f);
            } else {
                holder.shape_icon.setSelected(false);
                if (mIsEnable && mSelectedPos != position) {
                    if (isBeautyShape) {
                        holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.black_alfph));
                    } else {
                        holder.shape_name.setTextColor(Color.BLACK);
                    }
                    holder.shape_icon_layout.setAlpha(1.0f);
                    holder.shape_name.setAlpha(0.8f);

                } else if (!mIsEnable) {
                    holder.shape_name.setTextColor(Color.BLACK);
                    holder.shape_icon_layout.setAlpha(0.5f);
                    holder.shape_name.setAlpha(0.5f);
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mSwitchView.isChecked()) {
                        return;
                    }
                    if (!mIsEnable) {
                        return;
                    }
                    if (mClickListener != null) {
                        notifyItemChanged(mSelectedPos);
                        Log.e("tell", "onClick position = " + position);
                        mSelectedPos = position;
                        notifyItemChanged(mSelectedPos);
                        mClickListener.onItemClick(v, position, item.getName());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String name);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout shape_icon_layout;
        private ImageView shape_icon;
        private TextView shape_name;
        private View blue_point;

        public ViewHolder(View view) {
            super(view);
            shape_icon_layout = (RelativeLayout) view.findViewById(R.id.shape_icon_layout);
            shape_icon = (ImageView) view.findViewById(R.id.shape_icon);
            shape_name = (TextView) view.findViewById(R.id.shape_txt);
            blue_point = (View) view.findViewById(R.id.blue_point);
        }
    }

}
