package com.meishe.sdkdemo.edit.clipEdit.adjust;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meishe.sdkdemo.R;


/**
 * The type My seek bar view.
 *调节滑杆
 * @author liupanfeng
 */
public class MYSeekBarView extends RelativeLayout {

    private OnSeekBarListener mListener;
    private OnSeekBarListener mAngleChangedListener;
    private Context mContext;
    public SeekBar mSeekBar;
    private TextView mStartText;
    private TextView mCurrentText;
    private float mTotalValue = 20f;
    private String mName;
    private boolean mIsEndTextFixed = false;
    public MYSeekBarView(Context context) {
        super(context);
        initView(context);
    }


    public MYSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MYSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_edit_change_apply_time, this);
        mSeekBar = rootView.findViewById(R.id.seek_bar);
        mStartText = rootView.findViewById(R.id.tv_start_text);
        mCurrentText = rootView.findViewById(R.id.tv_current_text);
        initListener();

    }

    public float getMaxProgress() {
        return mSeekBar.getMax();
    }

    public float getMinProgress() {
        return 0;
    }
    public void setTotalValue(float totalValue) {
        this.mTotalValue = totalValue;
    }

    public void setInitData(int max, int current, boolean isEndTextFixed) {
        mIsEndTextFixed = isEndTextFixed;
        if(mSeekBar != null) {
            mSeekBar.setMax(max);
            mSeekBar.setProgress(current);
        }
    }

    private void initListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!mIsEndTextFixed && mCurrentText != null) {
                    mCurrentText.setText(String.format(mContext.getResources().getString(R.string.string_format_one_point),
                            (mTotalValue * seekBar.getProgress() / 100 - 10)));
                }
                if (mListener != null) {
                    mListener.onProgressChanged(seekBar, progress, fromUser);
                }
                if(null != mAngleChangedListener){
                    mAngleChangedListener.onProgressChanged(seekBar, progress, fromUser);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStopTrackingTouch(seekBar.getProgress(), mName);
                }
                if (!mIsEndTextFixed && mCurrentText != null) {
                    mCurrentText.setText(String.format(mContext.getResources().getString(R.string.string_format_one_point),
                            (mTotalValue * seekBar.getProgress() / 100 - 10)));
                }
            }
        });
    }

    public void setListener(OnSeekBarListener listener) {
        this.mListener = listener;
    }

    /**
     * Setm angle changed listener.
     * Setm角度改变监听器
     * @param listener the listener
     */
    public void setmAngleChangedListener(OnSeekBarListener listener){
        this.mAngleChangedListener = listener;
    }

    /**
     * Sets name.
     * 设置名字
     * @param name the name
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * The interface On seek bar listener.
     * 拖动条监听的接口
     */
    public interface OnSeekBarListener {

        /**
         * On stop tracking touch.
         * 停止触摸追踪
         * @param progress the progress 进度
         * @param name     the name 名字
         */
        void onStopTrackingTouch(int progress, String name);

        /**
         * On start tracking touch.
         * 停止开始追踪
         * @param seekBar  the seek bar 拖动条
         */
        void onStartTrackingTouch(SeekBar seekBar);

        /**
         * On progress changed.
         * 改变进度
         * @param seekBar  the seek bar 拖动条
         * @param progress the progress 进度
         * @param fromUser the from user 来自用户
         */
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

    }


    /**
     * Sets seek progress.
     * 设置拖动条进度
     * @param progress the progress 进度
     */
    public void setSeekProgress(int progress) {
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress);
        }
    }

    /**
     * Gets seek progress.
     * 获取拖动条进度
     * @return  progress the progress 进度
     */
    public  int getProgress() {
        if (mSeekBar != null) {
          return  mSeekBar.getProgress();
        }
        return 0;
    }

    /**
     * Sets start text visible.
     * 设置开始文本可见度
     * @param visible the visible
     */
    public void setStartTextVisible(boolean visible) {
        mStartText.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Sets end text visible.
     *  设置结束文本可见度
     * @param visible the visible
     */
    public void setEndTextVisible(boolean visible) {
        mCurrentText.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Sets start value and current value.
     * 设置起始值和当前值
     * @param startValue   the start value 起始值
     * @param currentValue the current value 当前值
     */
    public void setStartValueAndCurrentValue(float startValue, float currentValue) {
        if (mStartText != null) {
            mStartText.setText(startValue + "");
        }
        if (mCurrentText != null) {
            mCurrentText.setText(currentValue + "");
        }
    }

    /**
     * Get progress left dis int.
     * 获得左进度
     * @return the int
     */
    public int getProgressLeftDis (){
        if(null != mSeekBar){
            return mSeekBar.getLeft();
        }
        return 0;
    }

}
