package com.meishe.cutsame.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.base.view.CompileProgress;
import com.meishe.engine.util.ConvertFileManager;
import com.meishe.third.pop.core.CenterPopupView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : Chuchenguang
 * @CreateDate : 2021/02/01 15:29
 * @Description : 视频倒放进度
 * video reverse
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class VideoReverseDialog extends CenterPopupView {


    private TextView tvProgress;
    private CompileProgress compileProgress;
    private ConvertFileManager mConvertFileManager;
    private OnConvertListener onConvertListener;
    private String mPath;

    public VideoReverseDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_video_recerse;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        initView();
        initListener();
    }

    private void initListener() {
        if (mConvertFileManager == null) {
            mConvertFileManager = ConvertFileManager.getInstance();
            mConvertFileManager.setOnConvertListener(new ConvertFileManager.OnConvertListener() {
                @Override
                public void onConvertProgress(float progress) {
                    String text = (int) progress + "%";
                    tvProgress.setText(text);
                    compileProgress.setProgress((int) progress);
                }

                @Override
                public void onConvertFinish(String path, boolean convertSuccess) {
                    if (onConvertListener != null) {
                        onConvertListener.onConvertFinish(path, convertSuccess);
                        dismiss();
                    }
                }


                @Override
                public void onConvertCancel() {
                    dismiss();
                }
            });
        }
    }

    private void initView() {
        TextView tvCancel = findViewById(R.id.tv_cancel);
        tvProgress = findViewById(R.id.tv_progress);
        compileProgress = findViewById(R.id.cut_compile_progress);
        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mConvertFileManager.cancelConvert();
            }
        });
    }

    public void setOnConvertListener(OnConvertListener onConvertListener) {
        this.onConvertListener = onConvertListener;
    }

    public void setPath(String path) {
        mPath = path;
    }

    @Override
    protected void doAfterShow() {
        super.doAfterShow();
        mConvertFileManager.convertFile(mPath);
    }

    public interface OnConvertListener {

        void onConvertFinish(String path, boolean convertSuccess);
    }
}
