package com.meishe.cutsame.fragment.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.cutsame.CutSameEditorActivity;
import com.meishe.cutsame.CutSameNetApi;
import com.meishe.cutsame.bean.RatioInfo;
import com.meishe.cutsame.bean.Template;
import com.meishe.cutsame.bean.TemplateCategory;
import com.meishe.cutsame.bean.TemplateClip;
import com.meishe.cutsame.fragment.iview.TemplateView;
import com.meishe.cutsame.util.CustomConstants;
import com.meishe.cutsame.util.RatioUtil;
import com.meishe.cutsame.util.TemplateUtils;
import com.meishe.cutsame.view.SelectRatioDialog;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.util.PathUtils;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.third.pop.XPopup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lhz
 * @CreateDate : 2020/11/3
 * @Description :模板逻辑处理类
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TemplatePresenter extends Presenter<TemplateView> {
    private static final String TEMPLATE_POSTFIX = ".template";
    private static final String VIDEO_POSTFIX = ".mp4";
    private static final String INFO_POSTFIX = ".json";
    public static final int PAGE_NUM = 12;
    private boolean hasNext;
    private int mPage = 1;
    private boolean canLoadMore = true;

    @Override
    public void attachView(TemplateView templateView) {
        super.attachView(templateView);
    }

    /**
     * 获取模板分类
     * Get template classification
     */
    public void getTemplateCategory() {
        CutSameNetApi.getTemplateCategory(this, new RequestCallback<List<TemplateCategory.CategoryList>>() {
            @Override
            public void onSuccess(BaseResponse<List<TemplateCategory.CategoryList>> response) {
                if (response != null && response.getData() != null) {
                    List<TemplateCategory.CategoryList> data = response.getData();
                    if (data!= null && getView() != null) {
                        for (TemplateCategory.CategoryList category : data) {
                            if (category.getId() == 19) {
                                getView().onTemplateCategoryBack(category.categories);
                                break;
                            }
                        }

                    }
                }
            }

            @Override
            public void onError(BaseResponse<List<TemplateCategory.CategoryList>> response) {
                getView().onTemplateCategoryBack(null);
            }
        });
    }

    /**
     * 获取模板对应分类的列表
     * <p></p>
     * Gets the list of categories corresponding to the template
     *
     * @param page       int 请求页数
     * @param categoryId String 模板分类id
     */
    public void getTemplateList(final int page, final String categoryId) {
        getTemplateListByKeyword(page, null, categoryId);
    }

    /**
     * 通过关键字获取模板对应分类的列表
     * Obtain a list of categories corresponding to the template by keyword
     *
     * @param page       请求页数
     * @param keyword    关键字
     * @param categoryId 模板分类id
     */
    public void getTemplateListByKeyword(final int page, String keyword, final String categoryId) {
        String sdkVersion = "";
        NvsStreamingContext instance = NvsStreamingContext.getInstance();
        if (instance != null && instance.getSdkVersion() != null) {
            NvsStreamingContext.SdkVersion version = instance.getSdkVersion();
            sdkVersion = version.majorVersion + "." + version.minorVersion + "." + version.revisionNumber;
        }
        AssetType assetType = null;
        if ("1".equals(categoryId)) {
            assetType = AssetType.MS_TEMPLATE_NOR;
        } else if ("2".equals(categoryId)) {
            assetType = AssetType.MS_TEMPLATE_PHOTO;
        } else if ("3".equals(categoryId)) {
            assetType = AssetType.MS_TEMPLATE_VIDEO;
        }
        int ratio = 1 | 2 | 4 | 8 | 16 | 32 | 64 | 512 | 1024;
        CutSameNetApi.getMaterialList(null, assetType, 1, ratio,
                keyword, sdkVersion, page, PAGE_NUM, new RequestCallback<BaseBean<BaseDataBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BaseDataBean>> response) {
                        if (response != null && response.getData() != null) {
                            if (response.getData().getElements() != null && getView() != null) {
                                List<Template> templates = TemplateUtils.getTemplate(response.getData().getElements(), categoryId);
                                canLoadMore = response.getData().getTotal() > page * PAGE_NUM;
                                if (page == 1) {
                                    mPage = 1;
                                    getView().onTemplateListBack(templates);
                                } else {
                                    mPage++;
                                    getView().onMoreTemplateBack(templates);
                                }
                                hasNext = true;
                            } else {
                                hasNext = false;
                            }
                        } else {
                            hasNext = false;
                        }
                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean>> response) {
                        getView().onTemplateListBack(null);

                    }
                });
    }

    /**
     * 加载更多模板
     * Loading more templates
     *
     * @param categoryId the category id 类别id
     * @return the more template 更多的模板
     */
    public boolean getMoreTemplate(String categoryId) {
        if (hasNext && canLoadMore) {
            getTemplateList((mPage + 1), categoryId);
            canLoadMore = false;
        }
        return hasNext;
    }

    /**
     * 加载更多模板
     * Loading more templates
     *
     * @param keyword    keyword
     * @param categoryId the category id 类别id
     * @return the more template 更多的模板
     */
    public boolean getMoreTemplate(String keyword, String categoryId) {
        if (hasNext && canLoadMore) {
            getTemplateListByKeyword((mPage + 1), keyword, categoryId);
            canLoadMore = false;
        }
        return hasNext;
    }

    /**
     * 下载对应的模板资源
     * download template resource
     *
     * @param url        下载链接 download link
     * @param downDir    文件所在目录 The directory where the file is located
     * @param fileName   文件名称 file name
     * @param isTemplate true则是下载模板文件，false则不然。 true is to download the template file, false is not.
     */
    public void download(String url, String downDir, final String fileName, final boolean isTemplate) {
        CutSameNetApi.download(fileName, url, downDir, fileName, new SimpleDownListener(fileName) {
            @Override
            public void onFinish(final File file, Progress progress) {
                if (getView() != null) {
                    getView().onDownloadTemplateSuccess(file.getAbsolutePath(), isTemplate);
                }
            }

            @Override
            public void onError(Progress progress) {
                FileUtils.delete(PathUtils.getTemplateDir() + File.separator + fileName);
            }
        });
    }

    /**
     * 检查模板是否需要更新
     * Check if the template needs to be updated
     *
     * @param template the template 模板
     * @return the boolean
     */
    public boolean checkTemplateUpdate(Template template) {
        File file = getTemplatePath(template.getId());
        try {
            if (file != null) {
                String templateName = file.getName();
                String[] split = templateName.split("\\.");
                if (split.length == 3 && Integer.valueOf(split[1]) >= template.getVersion()
                        || split.length == 2 && template.getVersion() <= 0) {
                    /*
                     * 不用更新
                     * Don't need to update
                     * */
                    if (getView() != null) {
                        getView().onDownloadTemplateSuccess(file.getAbsolutePath(), true);
                    }
                    return false;
                } else {
                    /*
                     * 需要更新，则删除文件夹中的内容
                     * If an update is needed, the contents of the folder are deleted
                     * */
                    FileUtils.deleteFilesInDir(file.getParentFile());
                }
            } /*else {
                //第一次需要创建文件夹  The first time you need to create a folder
                FileUtils.createOrExistsDir(PathUtils.getTemplateDir() + File.separator + template.getId());
            }*/
            String fileName = template.getPackageUrl();
            String fileDir = PathUtils.getTemplateDir() + File.separator + template.getId();
            try {
                /*
                 * 截取文件名
                 * Intercept file name
                 * */
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            } catch (Exception e) {
                LogUtils.e(e);
                fileName = template.getId() + "." + template.getVersion() + TEMPLATE_POSTFIX;
            }
            download(template.getPackageUrl(), fileDir, fileName, true);
            fileName = template.getPreviewVideoUrl();
            if (!TextUtils.isEmpty(fileName)) {
                try {
                    /*
                     * 截取文件名
                     * Intercept file name
                     * */
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                } catch (Exception e) {
                    LogUtils.e(e);
                    fileName = template.getId() + "." + template.getVersion() + VIDEO_POSTFIX;
                }
                download(template.getPreviewVideoUrl(), fileDir, fileName, false);
            }

            fileName = template.getInfoUrl();
            if (!TextUtils.isEmpty(fileName)) {
                try {
                    /*
                     * 截取文件名
                     * Intercept file name
                     * */
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                } catch (Exception e) {
                    LogUtils.e(e);
                    fileName = template.getId() + "." + template.getVersion() + INFO_POSTFIX;
                }
                download(template.getInfoUrl(), fileDir, fileName, false);
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return true;
    }

    /**
     * 获取下载后的模板预览视频路径
     * <p></p>
     * Get the template preview video path after downloading
     *
     * @param templateId the template id 模板编号
     * @return the video path 视频路径
     */
    public String getVideoPath(String templateId) {
        return getFilePath(templateId, VIDEO_POSTFIX);
    }

    /**
     * 获取信息文件路径
     * <p></p>
     * Get the information file path after downloading
     *
     * @param templateId the template id 模板编号
     * @return the information file path 视频路径
     */
    public String getInfoPath(String templateId) {
        return getFilePath(templateId, INFO_POSTFIX);
    }

    /**
     * 根据文件后缀获取文件路径
     * <p></p>
     * Get the file path by postfix
     *
     * @param templateId the template id 模板编号
     * @param postfix    the file postfix 文件后缀
     * @return the file path 文件路径
     */
    private String getFilePath(String templateId, String postfix) {
        if (TextUtils.isEmpty(postfix)) {
            return null;
        }
        String templatePath = PathUtils.getTemplateDir() + "/" + templateId;
        if (FileUtils.isDir(templatePath)) {
            List<File> files = FileUtils.listFilesInDir(templatePath);
            if (files != null && files.size() > 0) {
                for (File file : files) {
                    if (file.getName().toLowerCase().endsWith(postfix.toLowerCase())) {
                        return file.getAbsolutePath();
                    }
                }
            }

        }
        return null;
    }


    /**
     * 获取下载后的模板文件
     * Get the downloaded template file
     */
    private File getTemplatePath(String templateId) {
        String templatePath = PathUtils.getTemplateDir() + "/" + templateId;
        if (FileUtils.isDir(templatePath)) {
            List<File> files = FileUtils.listFilesInDir(templatePath);
            if (files.size() > 0) {
                for (File file : files) {
                    if (file.getName().endsWith(TEMPLATE_POSTFIX)) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

    public void showSelectRatioView(final Activity activity, final String mTemplateId, final Template mTemplate, final List<TemplateClip> mClipList) {
        if ((null == activity) || TextUtils.isEmpty(mTemplateId) || (null == mTemplate)) {
            return;
        }
        final int ration = EditorController.getInstance().getAssetPackageSupportedAspectRatio(mTemplateId, EditorController.ASSET_PACKAGE_TYPE_TEMPLATE);
        int defaultAspectRatio = mTemplate.getDefaultAspectRatio();
        if (defaultAspectRatio == ration) {
            toCutSameEditor(activity, mTemplateId, ration, mTemplate, mClipList);
            return;
        }
        List<RatioInfo> ratioInfos = RatioUtil.getSupportedAspectRatios(ration, mTemplate.getSupportedAspectRatio());
        SelectRatioDialog selectRatioDialog = (SelectRatioDialog) new XPopup.Builder(activity)
                .asCustom(new SelectRatioDialog(activity, ration, ratioInfos, new SelectRatioDialog.OnSelectRatioListener() {
                    @Override
                    public void onSelectRatio(int tag) {
                        toCutSameEditor(activity, mTemplateId, ration, mTemplate, mClipList);
                    }
                }));
        selectRatioDialog.show();
    }

    private void toCutSameEditor(Activity activity, String mTemplateId, int tag, Template mTemplate, List<TemplateClip> mClipList) {
        EditorController.getInstance().changeTemplateAspectRatio(mTemplateId, tag);
        Intent it = new Intent(activity, CutSameEditorActivity.class);
        it.putParcelableArrayListExtra(CustomConstants.TEMPLATE_CLIP_LIST, (ArrayList<? extends Parcelable>) mClipList);
        it.putExtra(CustomConstants.TEMPLATE_ID, mTemplateId);
        it.putExtra(CustomConstants.TEMPLATE_TYPE, mTemplate.getType());
        it.putExtra(CustomConstants.TEMPLATE_RATIO, tag);
        activity.startActivity(it);
    }
}
