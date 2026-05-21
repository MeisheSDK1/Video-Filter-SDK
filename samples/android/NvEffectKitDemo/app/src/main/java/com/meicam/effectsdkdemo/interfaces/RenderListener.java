package com.meicam.effectsdkdemo.interfaces;

public interface RenderListener {
    void onSurfaceChanged();

    void onDrawFrame();

    void updateFlashView(boolean toggle);
}
