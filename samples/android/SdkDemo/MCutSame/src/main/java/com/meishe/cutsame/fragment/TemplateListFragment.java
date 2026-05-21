package com.meishe.cutsame.fragment;

import android.animation.ValueAnimator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishe.base.adapter.CommonFragmentAdapter;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.base.utils.ToastUtils;
import com.meishe.base.utils.Utils;
import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.Template;
import com.meishe.cutsame.bean.TemplateCategory;
import com.meishe.cutsame.fragment.iview.TemplateView;
import com.meishe.cutsame.fragment.presenter.TemplatePresenter;
import com.meishe.cutsame.inter.OnTemplateListener;
import com.meishe.third.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lhz
 * @CreateDate : 2020/11/3
 * @Description :模板列表fragment Template list fragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TemplateListFragment extends BaseMvpFragment<TemplatePresenter> implements TemplateView {
    public static String cut_model;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTabTitleList = new ArrayList<>();
    private ImageView mTemplateBack;
    private EditText mTemplateSearch;
    private ImageView mTemplateClear;
    private TextView mTemplateCancel;
    private SlidingTabLayout mTlTemplate;
    private ViewPager mViewPager;
    private ValueAnimator mCancelAnimation;
    private OnTemplateListener mOnTemplateListener;

    public TemplateListFragment() {
    }

    public static TemplateListFragment create(String cutModel) {
        TemplateListFragment.cut_model = cutModel;
        return new TemplateListFragment();
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_template_list;
    }

    @Override
    public void onLazyLoad() {
        if (mTabTitleList.size() <= 0) {
            mPresenter.getTemplateCategory();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mViewPager != null) {
                if (mFragmentList.size() == 0) {
                    return;
                }
                int position = mTlTemplate.getCurrentTab();
                if (!CommonUtils.isIndexAvailable(position, mFragmentList)) {
                    return;
                }
                Fragment mCurrentFragment = mFragmentList.get(mTlTemplate.getCurrentTab());
                if (mCurrentFragment instanceof TemplateMineFragmentNew) {
                    ((TemplateMineFragmentNew) mCurrentFragment).onLazyLoad();
                }
            }
        }
    }

    @Override
    protected void initView(View view) {
        mTemplateBack = view.findViewById(R.id.template_list_back);
        mTemplateSearch = view.findViewById(R.id.template_search);
        mTemplateClear = view.findViewById(R.id.template_list_clear);
        mTemplateCancel = view.findViewById(R.id.template_list_cancel);
        mTlTemplate = view.findViewById(R.id.tl_template_title);
        mViewPager = view.findViewById(R.id.vp_pager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new CommonFragmentAdapter(getChildFragmentManager(), mFragmentList, mTabTitleList));
        mTlTemplate.setViewPager(mViewPager);
        initListener();
    }

    @Override
    protected void initData() {
        mPresenter.getTemplateCategory();
    }

    private void initListener() {
        mTemplateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnTemplateListener) {
                    mOnTemplateListener.onBack();
                }
            }
        });
        mTemplateClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTemplateSearch.getText().clear();
            }
        });
        mTemplateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTemplateSearch.getText().clear();
                KeyboardUtils.hideSoftInput(getActivity());
                mTemplateSearch.setFocusable(false);
                getTemplateListByKeyword("");
            }
        });
        mTemplateSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strResult = s.toString();
                mTemplateClear.setVisibility(strResult.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        mTemplateSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyWord = mTemplateSearch.getText().toString().trim();
                    if (TextUtils.isEmpty(keyWord)) {
                        ToastUtils.showShort(R.string.template_search_null);
                        return true;
                    }
                    KeyboardUtils.hideSoftInput(getActivity());
                    getTemplateListByKeyword(keyWord);
                }
                return true;
            }
        });
        mTemplateSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showCancelLayout(hasFocus);
                if (!hasFocus) {
                    mTemplateSearch.setFocusable(true);
                    mTemplateSearch.setFocusableInTouchMode(true);
                }
            }
        });
    }

    /**
     * 模板搜索
     * Template search
     *
     * @param keyWord keyWord
     */
    private void getTemplateListByKeyword(String keyWord) {
        if (mFragmentList.isEmpty()) {
            return;
        }
        for (Fragment fragment : mFragmentList) {
            if (fragment instanceof TemplateFragment) {
                ((TemplateFragment) fragment).getTemplateDataByKeyword(keyWord);
            }
        }
    }


    @Override
    public void onTemplateCategoryBack(List<TemplateCategory.Category> categoryList) {
        mTabTitleList.clear();
        mFragmentList.clear();
        if (categoryList != null && categoryList.size() > 0) {
            for (TemplateCategory.Category category : categoryList) {
                mTabTitleList.add(Utils.isZh() ? category.getDisplayNameZhCn() : category.getDisplayName());
                mFragmentList.add(TemplateFragment.create(category.getId()));
            }
           /* mTabTitleList.add(getString(R.string.template_not_limit_time));
            mFragmentList.add(TemplateFragment.create(2));

            mTabTitleList.add(getString(R.string.template_ae));
            mFragmentList.add(TemplateFragment.create(3));*/

            mTlTemplate.updateTitles(mTabTitleList);
        }
    }

    @Override
    public void onTemplateListBack(List<Template> templateList) {

    }

    @Override
    public void onMoreTemplateBack(List<Template> templateList) {

    }

    @Override
    public void onDownloadTemplateSuccess(String templatePath, boolean isTemplate) {

    }

    public void setOnTemplateListener(OnTemplateListener onTemplateListener) {
        mOnTemplateListener = onTemplateListener;
    }

    private void showCancelLayout(final boolean show) {
        if (show) {
            if (mTemplateCancel.getVisibility() == View.VISIBLE) {
                return;
            }
        } else {
            if (mTemplateCancel.getVisibility() != View.VISIBLE) {
                return;
            }
        }
        mCancelAnimation = ValueAnimator.ofInt(0, 0);
        mCancelAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTemplateSearch.getLayoutParams();
                params.width = 0;
                mTemplateSearch.setLayoutParams(params);
                mTemplateCancel.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        mCancelAnimation.setDuration(300);
        mCancelAnimation.start();
    }
}
