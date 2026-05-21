package com.meishe.sdkdemo.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RadioGroup
import com.meishe.sdkdemo.R
import com.meishe.third.pop.XPopup
import com.meishe.third.pop.core.BottomPopupView
import com.meishe.third.pop.interfaces.SimpleCallback
import kotlinx.android.synthetic.main.pop_audio_noise.view.*

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/12/28 15:02
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
@SuppressLint("ViewConstructor")
class AudioNoisePop(context: Context, var onAudioNoisePopListener: OnAudioNoisePopListener) :
    BottomPopupView(context) {

    companion object {
        @JvmStatic
        fun create(
            context: Context,
            onAudioNoisePopListener: OnAudioNoisePopListener
        ): AudioNoisePop {
            return XPopup.Builder(context)
                .hasShadowBg(false)
                .setPopupCallback(object : SimpleCallback() {
                    override fun onDismiss() {
                        super.onDismiss()
                        onAudioNoisePopListener.onDismiss()
                    }
                })
                .asCustom(AudioNoisePop(context, onAudioNoisePopListener))
                    as AudioNoisePop
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_audio_noise
    }

    override fun onCreate() {
        super.onCreate()
        rb_audio_noise_no.isChecked = true
        rg_audio_noise.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_audio_noise_no -> onAudioNoisePopListener.onAudioNoiseLevel(0)
                R.id.rb_audio_noise_one -> onAudioNoisePopListener.onAudioNoiseLevel(1)
                R.id.rb_audio_noise_two -> onAudioNoisePopListener.onAudioNoiseLevel(2)
                R.id.rb_audio_noise_three -> onAudioNoisePopListener.onAudioNoiseLevel(3)
                R.id.rb_audio_noise_four -> onAudioNoisePopListener.onAudioNoiseLevel(4)
            }
        }
    }

    interface OnAudioNoisePopListener {
        fun onDismiss()

        /**
         * 降噪等级
         * Noise reduction level
         */
        fun onAudioNoiseLevel(level: Int)
    }
}