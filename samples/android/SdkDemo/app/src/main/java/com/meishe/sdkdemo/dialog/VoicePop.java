package com.meishe.sdkdemo.dialog;

import android.annotation.SuppressLint;
import android.content.Context;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.bean.voice.ChangeVoiceData;
import com.meishe.sdkdemo.capture.VoiceAdapter;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.BottomPopupView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/27 15:09
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
@SuppressLint("ViewConstructor")
public class VoicePop extends BottomPopupView {
    private final List<ChangeVoiceData> mVoiceList;
    private final OnVoicePopListener mOnVoicePopListener;

    public static VoicePop create(Context context, List<ChangeVoiceData> voiceList, OnVoicePopListener onVoicePopListener) {
        return (VoicePop) new XPopup
                .Builder(context)
                .dismissOnTouchOutside(true)
                .hasShadowBg(false)
                .asCustom(new VoicePop(context, voiceList, onVoicePopListener));
    }

    private VoicePop(@NonNull Context context, List<ChangeVoiceData> voiceList, OnVoicePopListener onVoicePopListener) {
        super(context);
        mVoiceList = voiceList;
        mOnVoicePopListener = onVoicePopListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.change_voice_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        RecyclerView mVoiceRecycler = findViewById(R.id.recycler_voice);
        mVoiceRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        VoiceAdapter adapter = new VoiceAdapter(getContext(), mVoiceList);
        mVoiceRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            ChangeVoiceData voiceData = mVoiceList.get(position);
            if ((null == voiceData) || (null == mOnVoicePopListener)) {
                return;
            }
            mOnVoicePopListener.onAudioNoiseLevel(voiceData);
        });
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        if (null != mOnVoicePopListener) {
            mOnVoicePopListener.onDismiss();
        }
    }

    public interface OnVoicePopListener {
        /**
         * 关闭
         * close
         */
        void onDismiss();

        /**
         * 变声
         * Voice
         *
         * @param voiceData voiceData
         */
        void onAudioNoiseLevel(ChangeVoiceData voiceData);
    }
}
