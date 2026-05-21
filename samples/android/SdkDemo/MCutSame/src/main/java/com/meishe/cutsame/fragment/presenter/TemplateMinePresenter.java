package com.meishe.cutsame.fragment.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.ExportTemplateDescInfo;
import com.meishe.cutsame.bean.Template;
import com.meishe.cutsame.fragment.iview.TemplateView;
import com.meishe.cutsame.util.ConfigUtil;
import com.meishe.cutsame.util.RatioUtil;
import com.google.gson.Gson;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.FileIOUtils;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.Utils;
import com.meishe.engine.util.PathUtils;
import com.meishe.http.bean.UserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/3
 * desc   :模板逻辑处理类
 * Template logic handles classes
 */
public class TemplateMinePresenter extends Presenter<TemplateView> {
    private boolean hasNext;
    private int currentTotal;
    private int mPage;

    @Override
    public void attachView(TemplateView templateView) {
        super.attachView(templateView);
    }


    /**
     * 获取模板对应分类的列表
     * <p></p>
     * Gets the list of categories corresponding to the template
     *
     * @param page int 请求页数
     */
    public void getTemplateList(Context context, final int page, boolean isRefresh) {
        getDataFromLocal(context);
    }

    public boolean isFromNet() {
        if (ConfigUtil.isToC()) {
            return false;
        }
        return true;
    }

    /**
     * 加载更多模板
     * <p></p>
     * Loading more templates
     *
     * @return the more template 更多的模板
     */
    public boolean getMoreTemplate(Context context) {
        if (hasNext) {
            getTemplateList(context, (mPage + 1), true);
        }
        return hasNext;
    }


    private void getDataFromLocal(Context context) {
        String templateFileFolder = PathUtils.getGenerateTemplateFileFolder();
        List<File> files = FileUtils.listFilesInDir(templateFileFolder);
        if (files.isEmpty()) {
            getView().onTemplateListBack(null);
            return;
        }
        final List<Template> list = new ArrayList<>();
        for (int i = files.size() - 1; i >= 0; i--) {
            File file = files.get(i);
            List<File> templateFiles = FileUtils.listFilesInDir(file);
            if (templateFiles.size() > 0) {
                String templateDescPath = "";
                for (int j = 0; j < templateFiles.size(); j++) {
                    File templateFile = templateFiles.get(j);
                    if (templateFile == null) {
                        continue;
                    }
                    String fileAbsolutePath = templateFile.getAbsolutePath();
                    if (TextUtils.isEmpty(fileAbsolutePath)) {
                        continue;
                    }
                    if (fileAbsolutePath.endsWith(".json")) {
                        templateDescPath = fileAbsolutePath;
                    }
                }
                String jsonStr = FileIOUtils.readFile2String(templateDescPath);
                Gson gson = new Gson();
                ExportTemplateDescInfo exportTemplateDescInfo = gson.fromJson(jsonStr, ExportTemplateDescInfo.class);
                if (exportTemplateDescInfo == null) {
                    continue;
                }

                Template template = new Template();
                boolean isZh = Utils.isZh();
                template.setId(exportTemplateDescInfo.getUuid());
                int ratio = RatioUtil.getAspectRatio(exportTemplateDescInfo.getSupportedAspectRatio());
                template.setSupportedAspectRatio(ratio);
                template.setDefaultAspectRatio(ratio);
                template.setDisplayName(exportTemplateDescInfo.getName());
                template.setUseNum(-1);
                template.setDescription(isZh?exportTemplateDescInfo.getDescription():exportTemplateDescInfo.getDescription());
                template.setCoverUrl(exportTemplateDescInfo.getCover());
                template.setPackageUrl(exportTemplateDescInfo.getTemplatePath());
                template.setPreviewVideoUrl(exportTemplateDescInfo.getTemplateVideoPath());
                template.setDuration(exportTemplateDescInfo.getDuration() / 1000);
                template.setCreateTime(exportTemplateDescInfo.getCreateTime());
                UserInfo userInfo = new UserInfo();
                userInfo.setNickname(context.getString(R.string.template_default_creator));
                userInfo.setIconUrl("https://qasset.meishesdk.com/my/default_icon.png");
                template.setUserInfo(userInfo);
                list.add(template);
            }
        }
        Collections.sort(list);
        getView().onTemplateListBack(list);
    }

}
