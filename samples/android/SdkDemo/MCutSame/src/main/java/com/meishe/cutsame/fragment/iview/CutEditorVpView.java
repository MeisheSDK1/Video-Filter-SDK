package com.meishe.cutsame.fragment.iview;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.meishe.engine.bean.template.MeicamNvsTemplateFootageCorrespondingClipInfo;
import com.meishe.engine.bean.template.TemplateCaptionDesc;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.base.model.IBaseView;

import java.util.List;

/**
 * Created by CaoZhiChao on 2020/11/10 20:43
 * 剪切编辑器Vp视图接口
 * Shear editor Vp View interface
 */
public interface CutEditorVpView extends IBaseView {

    void getCaptionData(List<TemplateCaptionDesc> captionClipList);

    void getCaptionBitmap(int index);

    void getVideoData(List<MeicamNvsTemplateFootageCorrespondingClipInfo> templateClipList);

    void needSeekPosition(long position, List<PointF> pointFList);
}
