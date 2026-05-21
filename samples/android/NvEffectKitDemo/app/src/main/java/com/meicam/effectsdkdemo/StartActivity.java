package com.meicam.effectsdkdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.meishe.libbase.util.StatusBarUtils;

import java.util.Map;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "StartActivity";
    private boolean permission = false;
    ActivityResultLauncher<String[]> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            boolean success = true;
            for (Map.Entry<String, Boolean> stringBooleanEntry : result.entrySet()) {
                if (!stringBooleanEntry.getValue()) {
                    success = false;
                    break;
                }
            }
            permission = success;
            Log.e(TAG, "onActivityResult: " + success);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        setContentView(R.layout.activity_start);
        requestPermission();
        findViewById(R.id.start_preview).setOnClickListener(this);
    }

    private void requestPermission() {
        requestPermission.launch(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (!permission) {
            requestPermission();
            return;
        }
        switch (v.getId()) {
            case R.id.start_preview:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}