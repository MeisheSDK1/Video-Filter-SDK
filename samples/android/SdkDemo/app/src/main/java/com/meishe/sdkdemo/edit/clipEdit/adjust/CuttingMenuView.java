package com.meishe.sdkdemo.edit.clipEdit.adjust;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meishe.engine.constant.NvsConstants;
import com.meishe.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The type Cutting menu view.
 * 裁剪视图菜单类
 */
public class CuttingMenuView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = CuttingMenuView.class.getSimpleName();
    private Context mContext;
    private MYSeekBarView mMYSeekBarView;
    //    private TextView mResetTextView;
    private ImageView mConfirmImageView;
    private RecyclerView mRecyclerView;
    private OnRatioSelectListener mOnRatioSelectListener;
    private OnConfirmListener mOnConfrimListener;
    private RationAdapter mRationAdapter;
    private TextView tv_angle;
    /**
     * 震动器
     * vibrator
     */
    private Vibrator mVibrator;

    private int[] ratioString = new int[]{
            R.string.free,
            R.string.nineTSixteen, R.string.threeTFour, R.string.nineTEighteen,
            R.string.nineTTwentyOne, R.string.oneTone, R.string.sixteenTNine,
            R.string.fourTThree, R.string.eighteenTNine, R.string.twentyNine};

    private int[] ratioIcon = new int[]{
            R.drawable.cutting_free_bg,
            R.drawable.cutting_nine_sixteen, R.drawable.cutting_three_four, R.drawable.cutting_nine_eighteen,
            R.drawable.cutting_nine_21, R.drawable.cutting_one_tone, R.drawable.cutting_sixteen_nine,
            R.drawable.cutting_four_three, R.drawable.cutting_eighteen_nine, R.drawable.cutting_21_nine};

    private int[] ratios = new int[]{
            NvsConstants.AspectRatio.AspectRatio_NoFitRatio,
            NvsConstants.AspectRatio.AspectRatio_9v16, NvsConstants.AspectRatio.AspectRatio_3v4, NvsConstants.AspectRatio.AspectRatio_9v18,
            NvsConstants.AspectRatio.AspectRatio_9v21, NvsConstants.AspectRatio.AspectRatio_1v1, NvsConstants.AspectRatio.AspectRatio_16v9,
            NvsConstants.AspectRatio.AspectRatio_4v3, NvsConstants.AspectRatio.AspectRatio_18v9, NvsConstants.AspectRatio.AspectRatio_21v9};
    private RelativeLayout mHorizLayout;
    private RelativeLayout mVerticLayout;
    private RelativeLayout mRotateLayout;
    private RelativeLayout mResetLayout;

    /**
     * Sets on seek bar listener.
     * 设置拖动条监听
     *
     * @param listener the listener
     */
    public void setOnSeekBarListener(MYSeekBarView.OnSeekBarListener listener) {
        if (mMYSeekBarView != null) {
            mMYSeekBarView.setListener(listener);
        }
    }

    /**
     * Sets on ratio select listener.
     * 设置比例选择监听器
     *
     * @param listener the listener
     */
    public void setOnRatioSelectListener(OnRatioSelectListener listener) {
        this.mOnRatioSelectListener = listener;
    }

    /**
     * Sets on confrim listener.
     * 设置确认监听器
     *
     * @param listener the listener
     */
    public void setOnConfrimListener(OnConfirmListener listener) {
        this.mOnConfrimListener = listener;
    }

    public CuttingMenuView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CuttingMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_cutting_menu, this);
        mMYSeekBarView = rootView.findViewById(R.id.view_seek_bar);
        mConfirmImageView = rootView.findViewById(R.id.iv_confirm);
        mHorizLayout = findViewById(R.id.horizLayout);
        mVerticLayout = findViewById(R.id.verticLayout);
        mRotateLayout = findViewById(R.id.rotateLayout);
        mResetLayout = findViewById(R.id.resetLayout);
        mHorizLayout.setOnClickListener(this);
        mVerticLayout.setOnClickListener(this);
        mRotateLayout.setOnClickListener(this);
        mResetLayout.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.ratio_recyclerView);
        mConfirmImageView.setOnClickListener(this);
        mRationAdapter = new RationAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManagerWrapper(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRationAdapter);
        mRationAdapter.setSelection(0);
        tv_angle = rootView.findViewById(R.id.tv_angle);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initData() {
        mMYSeekBarView.setStartValueAndCurrentValue(-45, 45);
        mMYSeekBarView.setInitData(90, 45, true);
        mMYSeekBarView.setmAngleChangedListener(new MYSeekBarView.OnSeekBarListener() {
            @Override
            public void onStopTrackingTouch(int progress, String name) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
                final float degree = (progress - 45) * 1.0f;
                if (degree == 0) {
                    if (mVibrator != null) {
                        mVibrator.vibrate(50);
                    } //震动一下 Shaking the
                }
                tv_angle.setText(degree + "");
                if (!fromUser) {
                    tv_angle.post(new Runnable() {
                        @Override
                        public void run() {
                            transTextView(seekBar);
                        }
                    });
                } else {
                    transTextView(seekBar);
                }
            }

            private void transTextView(SeekBar seekBar) {
                int[] location = new int[2];
                seekBar.getLocationInWindow(location);
                Rect bounds = seekBar.getThumb().getBounds();
                int size = bounds.right - bounds.left;
                int startLeft = location[0] + size + bounds.left;
                int progressForWidth = (int) (startLeft - tv_angle.getWidth() / 2F);
                tv_angle.setX(progressForWidth);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_confirm) {
            if (mOnConfrimListener != null) {
                mOnConfrimListener.onConfirm();
            }
        } else if (id == R.id.resetLayout) {
            mRationAdapter.setSelection(0);
            if (mOnRatioSelectListener != null) {
                mOnRatioSelectListener.onReset();
            }
        } else if (id == R.id.horizLayout) {
            if (mOnRatioSelectListener != null) {
                mOnRatioSelectListener.onHorizontal();
            }
        } else if (id == R.id.verticLayout) {
            if (mOnRatioSelectListener != null) {
                mOnRatioSelectListener.onVertical();
            }
        } else if (id == R.id.rotateLayout) {
            if (mOnRatioSelectListener != null) {
                mOnRatioSelectListener.onRotation();
            }
        }
    }

    /**
     * Sets progress.
     * 设置进度
     *
     * @param degree the degree
     */
    public void setProgress(float degree) {
        mMYSeekBarView.setSeekProgress((int) (degree + 45));
    }

    /**
     * Sets select ratio.
     * 设置选择比
     *
     * @param ratio the ratio
     */
    public void setSelectRatio(int ratio) {
        for (int index = 0; index < ratios.length; index++) {
            if (ratio == ratios[index]) {
                mRationAdapter.setSelection(index);
                mRecyclerView.scrollToPosition(index);
                break;
            }
        }
    }

    /**
     * The type Ration adapter.
     * 配给适配器
     */
    class RationAdapter extends RecyclerView.Adapter {
        private int mSelectPosition = -1;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_cutting_ration, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ((Holder) holder).mImageView.setImageResource(ratioIcon[position]);
            ((Holder) holder).mTextView.setText(ratioString[position]);
            if (mSelectPosition == position) {
                ((Holder) holder).mImageView.setSelected(true);
                ((Holder) holder).mTextView.setTextColor(mContext.getResources().getColor(R.color.ms_blue));
            } else {
                ((Holder) holder).mImageView.setSelected(false);
                ((Holder) holder).mTextView.setTextColor(Color.WHITE);
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRatioSelectListener != null) {
                        mOnRatioSelectListener.onItemClicked(ratios[position]);
                    }
                    mSelectPosition = position;
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return ratioString.length;
        }

        /**
         * Sets selection.
         * 设置选项
         *
         * @param selection the selection
         */
        public void setSelection(int selection) {
            mSelectPosition = selection;
            notifyDataSetChanged();
        }

        class Holder extends RecyclerView.ViewHolder {
            private ImageView mImageView;
            private TextView mTextView;

            public Holder(@NonNull View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.image);
                mTextView = itemView.findViewById(R.id.text_view);
            }
        }
    }

    /**
     * The interface On ratio select listener.
     * 选择比的监听接口
     */
    public interface OnRatioSelectListener {
        /**
         * On item clicked.
         * 点击条目
         *
         * @param ratio the ratio
         */
        void onItemClicked(int ratio);

        /**
         * On reset.
         * 重置
         */
        void onReset();

        void onVertical();

        void onRotation();

        void onHorizontal();
    }

    /**
     * The interface On confirm listener.
     * 确认监听的接口
     */
    public interface OnConfirmListener {
        /**
         * On confirm.
         * 确认
         */
        void onConfirm();
    }
}
