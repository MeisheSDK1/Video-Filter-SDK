package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meishe.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;


/**
 * 变速
 * Speed change
 */
public class EditChangeSpeedView extends LinearLayout {
    private ImageView mIvConfirm;
    private RelativeLayout mLlSpeedContainerView;
    private CheckBox mCheckBox;

    private EditChangeSpeedScrollView speedScrollView;
    private List<EditChangeSpeedScrollView.SpeedParam> speedParams;
    private float mSpeed;
    private OnFunctionListener onFunctionListener;

    public EditChangeSpeedView(Context context) {
        this(context, null);

    }

    public EditChangeSpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initData();
        initListener();
    }
   /* public EditChangeSpeedView(Context context, String type) {
        super(context);
        this.mContext = context;
        this.mClipType = type;
        initView(context);
    }*/

    protected void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_edit_chang_speed, this);
        mLlSpeedContainerView = rootView.findViewById(R.id.ll_speed_container);
        mIvConfirm = rootView.findViewById(R.id.speedFinish);
        mCheckBox = rootView.findViewById(R.id.ck_change_voice);
        speedScrollView = rootView.findViewById(R.id.speed_view);
    }

    protected void initData() {
        speedParams = new ArrayList<>();
        EditChangeSpeedScrollView.SpeedParam param0 = new EditChangeSpeedScrollView.SpeedParam(0.1f);
        EditChangeSpeedScrollView.SpeedParam param1 = new EditChangeSpeedScrollView.SpeedParam(1f);
        EditChangeSpeedScrollView.SpeedParam param2 = new EditChangeSpeedScrollView.SpeedParam(2f);
        EditChangeSpeedScrollView.SpeedParam param3 = new EditChangeSpeedScrollView.SpeedParam(5f);
        EditChangeSpeedScrollView.SpeedParam param4 = new EditChangeSpeedScrollView.SpeedParam(10f);
        EditChangeSpeedScrollView.SpeedParam param5 = new EditChangeSpeedScrollView.SpeedParam(100f);
        speedParams.add(param0);
        speedParams.add(param1);
        speedParams.add(param2);
        speedParams.add(param3);
        speedParams.add(param4);
        speedParams.add(param5);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                speedScrollView.setSelectedData(speedParams);
            }
        }, 200);
    }


    public void setSpeed(float speed, boolean isChangeVoice) {
        mSpeed = speed;
        mCheckBox.setChecked(isChangeVoice);

        speedScrollView.setCurrentSpeed(speed);
    }

    protected void initListener() {
        mIvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onFunctionListener) {
                    onFunctionListener.onConfirm(mSpeed, mCheckBox.isChecked());
                }
            }
        });
        /*
         * 变调
         * Tone variation
         */
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null != onFunctionListener) {
                    onFunctionListener.onChangeVoice(mSpeed, isChecked);
                }
            }
        });

        mLlSpeedContainerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        speedScrollView.setOnSpeedChangedListener(new EditChangeSpeedScrollView.OnSpeedChangedListener() {
            @Override
            public void onSpeedChanged(float speed) {
                mSpeed = speed;
                if (null != onFunctionListener) {
                    onFunctionListener.onSpeedChanged(speed, mCheckBox.isChecked());
                }
            }
        });
    }

    public void setOnFunctionListener(OnFunctionListener callBack) {
        this.onFunctionListener = callBack;
    }

    public interface OnFunctionListener {
        void onConfirm(float speed, boolean changeVoice);

        void onChangeVoice(float speed, boolean changeVoice);

        void onSpeedChanged(float speed, boolean changeVoice);
    }

}
