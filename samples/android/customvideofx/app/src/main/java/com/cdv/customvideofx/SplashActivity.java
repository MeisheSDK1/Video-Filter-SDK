package com.cdv.customvideofx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.meicam.sdk.NvsStreamingContext;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/8/19 13:02
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_MEDIA = 200;

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= 23) {
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    || (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    || (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
                String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"
                        , "android.permission.CAMERA"
                        , "android.permission.RECORD_AUDIO"};
                requestPermissions(perms, 100);
            }
        }
        findViewById(R.id.capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, CaptureActivity.class);
                SplashActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                startActivityForResult(intent, REQUEST_MEDIA);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            if (videoUri != null) {
                String videoPath = getRealPathFromUri(videoUri);
                if (videoPath != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("video_path", videoPath);
                    startActivity(intent);
                }
            }
        }
    }

    /**
     * 根据 Uri 获取真实路径
     */
    private String getRealPathFromUri(Uri uri) {
        String path = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            try (
                    android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow("_data");
                    path = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    @Override
    protected void onDestroy() {
        NvsStreamingContext.close();
        super.onDestroy();
    }

}
