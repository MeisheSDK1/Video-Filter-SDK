//================================================================================
//
// (c) Copyright China Digital Video (Beijing) Limited, 2017. All rights reserved.
//
// This code and information is provided "as is" without warranty of any kind,
// either expressed or implied, including but not limited to the implied
// warranties of merchantability and/or fitness for a particular purpose.
//
//--------------------------------------------------------------------------------
//   Birth Date:    Jul 24. 2017
//   Author:        NewAuto video team
//================================================================================
package com.cdv.customvideofx;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;

import java.util.Locale;


public class CaptureActivity extends AppCompatActivity {
    private static final String TAG = "Meicam";

    private NvsStreamingContext m_streamingContext;
    private CaptureCustomVideoFx m_myCustomVideoFx;

    private SeekBar m_saturationGainSeekBar;
    private TextView m_saturationGainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_streamingContext = NvsStreamingContext.init(this, null);

        setContentView(R.layout.activity_capture);

        m_saturationGainSeekBar = (SeekBar)findViewById(R.id.saturationGainSeekBar);
        m_saturationGainSeekBar.setMax(2000);
        m_saturationGainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (!fromUser)
                    return;

                final float p = (float)progress / m_saturationGainSeekBar.getMax();
                final float minSatGain = m_myCustomVideoFx.getMinSaturationGain();
                final float maxSatGain = m_myCustomVideoFx.getMaxSaturationGain();
                final float saturationGain = minSatGain + (maxSatGain - minSatGain) * p;
                m_myCustomVideoFx.setSaturationGain(saturationGain);
                updateSaturationGainText(saturationGain);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        m_saturationGainText = (TextView)findViewById(R.id.saturationGainText);

        NvsLiveWindow liveWindow = (NvsLiveWindow)findViewById(R.id.liveWindow);
        m_streamingContext.connectCapturePreviewWithLiveWindow(liveWindow);

        // 添加自定义的采集视频特效
        m_myCustomVideoFx = new CaptureCustomVideoFx();
        m_streamingContext.appendCustomCaptureVideoFx(m_myCustomVideoFx);
        updateSaturationGainSeekBar(m_myCustomVideoFx.getSaturationGain());
        updateSaturationGainText(m_myCustomVideoFx.getSaturationGain());
    }

    @Override
    protected void onDestroy()
    {
        NvsStreamingContext.close();

        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        if (m_streamingContext != null)
            m_streamingContext.stop();

        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        startCapturePreview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults)
    {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    doStartCapturePreview();

                break;
            }
        }
    }

    private void startCapturePreview()
    {
        if (m_streamingContext == null)
            return;

        if (m_streamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW)
            return;

        // 检查采集相关的权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
                return;
            }
        }

        doStartCapturePreview();
    }

    private void doStartCapturePreview()
    {
        m_streamingContext.startCapturePreview(0, NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_HIGH, 0, null);
    }

    private void updateSaturationGainSeekBar(float saturationGain)
    {
        final float minSatGain = m_myCustomVideoFx.getMinSaturationGain();
        final float maxSatGain = m_myCustomVideoFx.getMaxSaturationGain();
        final float progress = (saturationGain - minSatGain) / (maxSatGain - minSatGain);
        m_saturationGainSeekBar.setProgress((int)(progress * m_saturationGainSeekBar.getMax() + 0.5f));
    }

    private void updateSaturationGainText(float saturationGain)
    {
        String t = String.format(Locale.getDefault(), "%1$.3f", saturationGain);
        m_saturationGainText.setText(t);
    }
}
