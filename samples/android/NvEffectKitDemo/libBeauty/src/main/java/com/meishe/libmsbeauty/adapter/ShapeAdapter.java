package com.meishe.libmsbeauty.adapter;

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

import com.meishe.libmsbeauty.R;
import com.meishe.libmsbeauty.bean.BaseFxInfo;
import com.meishe.libmsbeauty.bean.IFxInfo;

import java.util.ArrayList;
import java.util.List;


public class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.ViewHolder> {
    private static final String TAG = "ShapeAdapter";
    private List<IFxInfo> mDataList;
    private int mSelectedPos = Integer.MAX_VALUE;
    private Context mContext;
    private OnItemClickListener mClickListener;
    private boolean mIsEnable = true;
    // if is beauty shape
    //判断是否是美型
    private boolean isBeautyShape = false;

    private Switch mSwitchView;
    /**
     * two types
     * 两种类型
     * 1.是类型选项，点击返回选择类型的列表
     *   is type class ,click back to type item list
     * 2.美型的item操作，点击设置值
     *   is type item ,clic to apply value
     */
    public static final int TYPE_KIND_BACK = 1;
    public static final int TYPE_KIND_ITEM = 2;

    public static final int TYPE_POINT = 3;


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

    public List<IFxInfo> getItems() {
        return mDataList;
    }

    @Override
    public int getItemViewType(int position) {

            IFxInfo info = mDataList.get(position);
            if (TextUtils.equals(info.getType(), BaseFxInfo.TYPE_PLACE_HOLDER)) {
                return TYPE_POINT;
            }
            return TYPE_KIND_ITEM;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
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
       if (itemType == TYPE_POINT) {
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
                        Log.e(TAG, "onClick position = " + position);
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
