package com.meishe.sdkdemo.mimodemo.common.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionsActivity extends AppCompatActivity {
    public static final int PERMISSIONS_GRANTED = 0;
    public static final int PERMISSIONS_DENIED = 1;
    public static final int PERMISSIONS_No_PROMPT = 2;
    private static final int PERMISSION_REQUEST_CODE = 0;
    public static final String EXTRA_PERMISSIONS =
            "com.meicam.sdkdemo.utils.permission.extra_permission";
    private PermissionsChecker mChecker;


    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            throw new RuntimeException("PermissionsActivity needs to be started using the static startActivityForResult method!");
        }
        mChecker = new PermissionsChecker(this);
        String[] permissions = getPermissions();
        if (mChecker.lacksPermissions(permissions)) {
            requestPermissions(permissions);
        } else {
            allPermissionsGranted();
        }
    }

    /**
     * 返回传递的权限参数
     * Returns the passed permission argument
     *
     * @return
     */
    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    /**
     * 请求权限兼容低版本
     * Request permission compatibility lower version
     *
     * @param permissions
     */
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    /**
     * 全部权限均已获取
     * All permissions have been obtained
     */
    private void allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int mResultCode = -1;
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问 Check whether Disable is selected and do not ask again
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (showRequestPermission) {
                            mResultCode = PERMISSIONS_DENIED;
                        } else { // false 被禁止了，不在访问 It's banned. It's no longer accessible
                            mResultCode = PERMISSIONS_No_PROMPT;
                            break;
                        }
                    }
                }
                if (mResultCode == -1) {
                    mResultCode = PERMISSIONS_GRANTED;
                }
                setActivityResult(mResultCode);
                break;
        }

    }

    private void setActivityResult(int code) {
        setResult(code);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
