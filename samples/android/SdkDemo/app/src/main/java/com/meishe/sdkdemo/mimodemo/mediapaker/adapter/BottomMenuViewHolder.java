package com.meishe.sdkdemo.mimodemo.mediapaker.adapter;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.mimodemo.common.template.model.ShotVideoInfo;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.GlidUtils;
import com.meishe.sdkdemo.utils.ScreenUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.annotation.NonNull;

public class BottomMenuViewHolder extends BaseRecyclerHolder<ShotVideoInfo> {
    private final ImageView mCoverImage;
    public final TextView mDurationText;
    private final ImageView mMaskImage;

    public BottomMenuViewHolder(@NonNull View itemView) {
        super(itemView);
        mDurationText = (TextView) itemView.findViewById(R.id.text_duration);
        mCoverImage = (ImageView) itemView.findViewById(R.id.image_cover);
        mMaskImage = (ImageView) itemView.findViewById(R.id.image_mask);
    }

    @Override
    public void bind(ShotVideoInfo shotVideoInfo, boolean isSelected) {
        if (shotVideoInfo == null) {
            return;
        }
        long realNeedDuration = shotVideoInfo.getRealNeedDuration();
        mDurationText.setText(getFormatDuration(realNeedDuration));
        mMaskImage.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        mCoverImage.setImageDrawable(null);
        String videoFilePath = shotVideoInfo.getVideoClipPath();
        if (!TextUtils.isEmpty(videoFilePath)) {

            //这里需要改为加载trimin的点对应的第一帧图
            //Here we need to change to the first frame corresponding to the point of loading trimin
            long trimIn = shotVideoInfo.getTrimIn();
            if (trimIn == 0 || isImage(videoFilePath)) {
                GlidUtils.setImageByPathAndWidth(mCoverImage, videoFilePath, ScreenUtils.dip2px(mCoverImage.getContext(), 45));
            } else {
                if (videoFilePath.startsWith("content:")) {
                    videoFilePath = FileUtils.contentPath2AbsPath(MSApplication.getContext().getContentResolver(), videoFilePath);
                }
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(new File(videoFilePath).getAbsolutePath());
                    mmr.setDataSource(inputStream.getFD());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
//                mmr.setDataSource(MSApplication.getmContext(), Uri.parse(videoFilePath));
                Bitmap bitmap = mmr.getFrameAtTime(trimIn, MediaMetadataRetriever.OPTION_CLOSEST);
                mCoverImage.setImageBitmap(bitmap);
            }


            mDurationText.setTextColor(mDurationText.getResources().getColor(R.color.white));
            if (shotVideoInfo.getFileDuration() >= realNeedDuration) {
                mDurationText.setTextColor(mDurationText.getResources().getColor(R.color.white));
            } else {
                mDurationText.setTextColor(mDurationText.getResources().getColor(R.color.msffd0021b));
            }
        } else {
            mCoverImage.setImageDrawable(mCoverImage.getContext().getResources().getDrawable(R.drawable.image_cover_default_drawable));//占位图
            mDurationText.setTextColor(mDurationText.getResources().getColor(R.color.white));
        }
    }

    private boolean isImage(String videoFilePath) {
        if (!TextUtils.isEmpty(videoFilePath)) {
            NvsAVFileInfo avInfoFromFile = NvsStreamingContext.getAVInfoFromFile(videoFilePath, 0);
            if (null == avInfoFromFile) {
                return false;
            }
            int type = avInfoFromFile.getAVFileType();
            if (type == NvsAVFileInfo.AV_FILE_TYPE_IMAGE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canClickable() {
        return mCoverImage.getDrawable() != null;
    }
}
