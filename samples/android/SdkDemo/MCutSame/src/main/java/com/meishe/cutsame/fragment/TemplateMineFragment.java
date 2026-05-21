package com.meishe.cutsame.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.meishe.cutsame.R;
import com.meishe.cutsame.TemplatePreviewActivity;
import com.meishe.cutsame.bean.ExportTemplateDescInfo;
import com.meishe.cutsame.bean.Template;
import com.meishe.cutsame.fragment.adapter.TemplateAdapter;
import com.meishe.cutsame.util.ConfigUtil;
import com.meishe.cutsame.util.RatioUtil;
import com.meishe.base.model.BaseFragment;
import com.meishe.base.utils.FileIOUtils;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.GsonUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.engine.util.PathUtils;
import com.meishe.http.bean.UserInfo;
import com.meishe.third.adpater.BaseQuickAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.meishe.cutsame.util.CustomConstants.DATA_TEMPLATE;
import static com.meishe.cutsame.util.CustomConstants.TEMPLATE_IS_FROM_MINE;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2021/1/5 15:24
 * @Description : 剪同款我的
 * template mine
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class TemplateMineFragment extends BaseFragment {
    private TemplateAdapter mAdapter;
    private View mTvNoDataView;
    private RecyclerView mRecyclerView;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_cut_same_template_mine;
    }

    @Override
    protected void initView(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.rv_list);
        mTvNoDataView = rootView.findViewById(R.id.tv_no_date);

        final int decoration = (int) getResources().getDimension(R.dimen.dp_px_30);
        int screenWidth = ScreenUtils.getScreenWidth();
        if (mRecyclerView.getLayoutParams() != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mRecyclerView.getLayoutParams();
            layoutParams.width = screenWidth - 2 * decoration;
            layoutParams.leftMargin = (int) (decoration / 2f);
            mRecyclerView.setLayoutParams(layoutParams);
        }
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new TemplateAdapter((ScreenUtils.getScreenWidth() - 4 * decoration) / 2);
        mRecyclerView.setAdapter(mAdapter);
        initListener();
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), TemplatePreviewActivity.class);
                    intent.putExtra(DATA_TEMPLATE, mAdapter.getData().get(position));
                    intent.putExtra(TEMPLATE_IS_FROM_MINE, true);
                    getActivity().startActivity(intent);
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mRecyclerView);
    }

    private void loadMore() {

    }

    @Override
    protected void onLazyLoad() {
        initTemplateData();
    }

    @Override
    protected void initData() {
        initTemplateData();
    }

    private void initTemplateData() {
        if (mAdapter == null) {
            showNoDataView(View.VISIBLE);
            return;
        }
        if (ConfigUtil.isToC()) {
            getDataFromLocal();
        }
    }

    private void getDataFromLocal() {
        String templateFileFolder = PathUtils.getGenerateTemplateFileFolder();
        List<File> files = FileUtils.listFilesInDir(templateFileFolder);
        if (files.isEmpty()) {
            showNoDataView(View.VISIBLE);
            return;
        }
        if ((files.size() == mAdapter.getData().size())) {
            showNoDataView(View.GONE);
            return;
        }
        if (files.size() > 0) {
            showNoDataView(View.GONE);
            final List<Template> list = new ArrayList<>();
            for (int i = files.size() - 1; i >= 0; i--) {
                File file = files.get(i);
                List<File> templateFiles = FileUtils.listFilesInDir(file);
                if (templateFiles.size() > 0) {
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
                            String jsonStr = FileIOUtils.readFile2String(fileAbsolutePath);
                            ExportTemplateDescInfo exportTemplateDescInfo = GsonUtils.fromJson(jsonStr, ExportTemplateDescInfo.class);
                            if (exportTemplateDescInfo == null) {
                                continue;
                            }
                            Template template = new Template();
                            template.setId(exportTemplateDescInfo.getUuid());
                            int ratio = RatioUtil.getAspectRatio(exportTemplateDescInfo.getSupportedAspectRatio());
                            template.setSupportedAspectRatio(ratio);
                            template.setDefaultAspectRatio(ratio);
                            template.setDisplayName(exportTemplateDescInfo.getName());
                            template.setUseNum(-1);
                            template.setDescription(exportTemplateDescInfo.getDescription());
                            template.setCoverUrl(exportTemplateDescInfo.getCover());
                            template.setPackageUrl(exportTemplateDescInfo.getTemplatePath());
                            template.setPreviewVideoUrl(exportTemplateDescInfo.getTemplateVideoPath());
                            template.setDuration(exportTemplateDescInfo.getDuration() / 1000);
                            template.setCreateTime(exportTemplateDescInfo.getCreateTime());
                            UserInfo userInfo=new UserInfo();
                            userInfo.setNickname(getString(R.string.template_default_creator));
                            userInfo.setIconUrl("https://qasset.meishesdk.com/my/default_icon.png");
                            template.setUserInfo(userInfo);
                            list.add(template);
                        }
                    }
                }
            }
            Collections.sort(list);
            mAdapter.setNewData(list);
        } else {
            showNoDataView(View.VISIBLE);
        }
    }


    private void showNoDataView(int visible) {
        if (mTvNoDataView != null) {
            mTvNoDataView.setVisibility(visible);
        }
    }
}
