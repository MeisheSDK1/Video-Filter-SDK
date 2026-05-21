package com.meishe.cutsame.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.NetUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.cutsame.R;
import com.meishe.cutsame.TemplatePreviewActivity;
import com.meishe.cutsame.bean.Template;
import com.meishe.cutsame.bean.TemplateCategory;
import com.meishe.cutsame.fragment.adapter.TemplateAdapter;
import com.meishe.cutsame.fragment.iview.TemplateView;
import com.meishe.cutsame.fragment.presenter.TemplatePresenter;
import com.meishe.engine.asset.bean.AssetInfo;
import com.meishe.engine.util.PathUtils;
import com.meishe.third.adpater.BaseQuickAdapter;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.meishe.cutsame.util.CustomConstants.CATEGORY_ID;
import static com.meishe.cutsame.util.CustomConstants.DATA_TEMPLATE;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lhz
 * @CreateDate : 2020/11/3
 * @Description :模板列表fragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TemplateFragment extends BaseMvpFragment<TemplatePresenter> implements TemplateView {
    private SwipeRefreshLayout mSrlRefresh;
    private RecyclerView mRvTemplateList;
    private TemplateAdapter mAdapter;
    private int mCategoryId;
    private View mTvNoDataView;
    private TextView tvReload;
    private LinearLayout rootNoNet;
    private boolean isShowLocal = false;
    private String mKeyword;

    public TemplateFragment() {
    }

    /**
     * Create template fragment.
     * 创建模板片段
     * 1-通用模板   2-不限时长模板  3-AE模板
     * 1- Universal template 2- Unlimited duration template 3-AE template
     *
     * @param categoryId the category id
     * @return the template fragment
     */
    public static TemplateFragment create(int categoryId) {
        Bundle bundle = new Bundle();
        bundle.putInt(CATEGORY_ID, categoryId);
        TemplateFragment template = new TemplateFragment();
        template.setArguments(bundle);
        return template;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_template;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initView(View rootView) {
        mSrlRefresh = rootView.findViewById(R.id.srl_refresh);
        rootNoNet = rootView.findViewById(R.id.root_no_net);
        tvReload = rootView.findViewById(R.id.tv_reload);
        mRvTemplateList = rootView.findViewById(R.id.rv_list);
        mTvNoDataView = rootView.findViewById(R.id.tv_no_date);
        final int decoration = (int) getResources().getDimension(R.dimen.dp_px_30);
        int screenWidth = ScreenUtils.getScreenWidth();
        if (mRvTemplateList.getLayoutParams() != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mRvTemplateList.getLayoutParams();
            layoutParams.width = screenWidth - 2 * decoration;
            layoutParams.leftMargin = (int) (decoration / 2f);
            mRvTemplateList.setLayoutParams(layoutParams);
        }
        mRvTemplateList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new TemplateAdapter((ScreenUtils.getScreenWidth() - 4 * decoration) / 2);
        initListener();
        if (!NetUtils.isNetworkAvailable(getActivity()) && (!isShowLocal)) {
            rootNoNet.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        mSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mPresenter != null && !isShowLocal) {
                    mPresenter.getTemplateListByKeyword(1, mKeyword, String.valueOf(mCategoryId));
                }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), TemplatePreviewActivity.class);
                    intent.putExtra(DATA_TEMPLATE, mAdapter.getData().get(position));
                    getActivity().startActivity(intent);
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mPresenter != null && !mPresenter.getMoreTemplate(mKeyword, String.valueOf(mCategoryId))) {
                    mAdapter.loadMoreEnd();
                }
            }
        }, mRvTemplateList);

        tvReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null && !isShowLocal) {
                    mPresenter.getTemplateListByKeyword(1, mKeyword, String.valueOf(mCategoryId));
                }
            }
        });
    }

    @Override
    protected void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCategoryId = arguments.getInt(CATEGORY_ID);
        }
        mAdapter.setHasStableIds(true);
        mRvTemplateList.setAdapter(mAdapter);
        //检查安装字体 Check installation font
        String fontPath = PathUtils.getAssetDownloadPath(AssetInfo.ASSET_FONT) + "/font";
        FileUtils.createOrExistsDir(fontPath);
        File fontFileParent = new File(fontPath);
        File[] fileList = fontFileParent.listFiles();
        if (fileList != null && fileList.length > 0) {
            for (File listFile : fileList) {
                String fontPathLocal = listFile.getAbsolutePath();
                NvsStreamingContext.getInstance().registerFontByFilePath(fontPathLocal);
            }
        }
        getTemplateData();
    }


    /**
     * 获取模板数据
     */
    private void getTemplateData() {
        //默认通用模板 Default generic template
        String mDisplayName = getResources().getString(R.string.template_type_stander);
        String mLocalDir = PathUtils.getLocalTemplateDir();
        if (mCategoryId == 2) {
            mDisplayName = getResources().getString(R.string.template_type_free);
            mLocalDir = PathUtils.getLocalTemplateFreeDir();
        } else if (mCategoryId == 3) {
            mDisplayName = getResources().getString(R.string.template_type_ae);
            mLocalDir = PathUtils.getLocalTemplateAEDir();
        }
        FileUtils.createOrExistsDir(mLocalDir);
        List<File> files = FileUtils.listFilesInDir(mLocalDir);
        if (files.size() > 0) {
            //本地模板测试使用
            isShowLocal = true;
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file != null) {
                    Template template = new Template();
                    template.setSupportedAspectRatio(1663);
                    template.setDefaultAspectRatio(4);
                    template.setDisplayName(mDisplayName + i);
                    template.setUseNum(i * 12);
                    template.setDescription(getResources().getString(R.string.template_default_creator));
                    template.setPackageUrl(file.getAbsolutePath());

                    switch (mCategoryId) {
                        case 1:
                            template.setCoverUrl("/android_asset/762F4A2F-1A16-484C-92E0-655D39094A07.jpg");
                            template.setType(Template.TYPE_TEMPLATE_STANDER);
                            break;
                        case 2:
                            template.setCoverUrl("/android_asset/762F4A2F-1A16-484C-92E0-655D39094A07.jpg");
                            template.setType(Template.TYPE_TEMPLATE_FREE);
                            break;
                        case 3:
                            template.setCoverUrl("/android_asset/762F4A2F-1A16-484C-92E0-655D39094A07.jpg");
                            template.setType(Template.TYPE_TEMPLATE_AE);
                            break;
                        default:
                            break;
                    }
                    template.setLocalType(Template.TYPE_TEMPLATE_LOCAL);
                    mAdapter.addData(template);
                }
            }
        } else {
            isShowLocal = false;
            mPresenter.getTemplateListByKeyword(1, mKeyword, String.valueOf(mCategoryId));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getTemplateDataByKeyword(String keyword) {
        mKeyword = keyword;
        mAdapter.getData().clear();
        mAdapter.notifyDataSetChanged();
        mPresenter.getTemplateListByKeyword(1, keyword, String.valueOf(mCategoryId));
    }


    /**
     * On template category back.
     *
     * @param categoryList the category list
     */
    @Override
    public void onTemplateCategoryBack(List<TemplateCategory.Category> categoryList) {

    }

    /**
     * On template list back.
     *
     * @param templateList the template list
     */
    @Override
    public void onTemplateListBack(List<Template> templateList) {
        if (mSrlRefresh.isRefreshing()) {
            mSrlRefresh.setRefreshing(false);
        }
        rootNoNet.setVisibility(View.INVISIBLE);
        mTvNoDataView.setVisibility(View.INVISIBLE);
        if (templateList != null && templateList.size() > 0) {
            mAdapter.setNewData(templateList);
            if (templateList.size() < TemplatePresenter.PAGE_NUM) {
                mAdapter.loadMoreEnd(true);
            }
        } else {
            setEmptyView();
        }
    }

    private void setEmptyView() {
        if (isShowLocal) {
            return;
        }
        if ((mAdapter == null) || (mAdapter.getData().size() == 0)) {
            if (mTvNoDataView != null) {
                mTvNoDataView.setVisibility(View.VISIBLE);
                rootNoNet.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mTvNoDataView != null) {
                mTvNoDataView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * On more template back.
     *
     * @param templateList the template list
     */
    @Override
    public void onMoreTemplateBack(List<Template> templateList) {
        if (templateList == null || templateList.size() == 0) {
            mAdapter.loadMoreEnd();
            setEmptyView();
        } else {
            mAdapter.loadMoreComplete();
            mAdapter.addData(templateList);
        }
    }


    /**
     * On download template success.
     *
     * @param templatePath the template path
     */
    @Override
    public void onDownloadTemplateSuccess(String templatePath, boolean isTemplate) {

    }
}
