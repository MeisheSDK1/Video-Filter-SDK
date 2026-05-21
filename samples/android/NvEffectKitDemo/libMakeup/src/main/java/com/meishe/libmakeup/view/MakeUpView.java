package com.meishe.libmakeup.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.meishe.libmakeup.MakeupDataManager;
import com.meishe.libmakeup.MakeupHelper;
import com.meishe.libmakeup.R;
import com.meishe.libmakeup.adapter.MakeupAdapter;
import com.meishe.libmakeup.bean.MakeupListData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: Guijun
 * @CreateDate: 2021/07/06 11:29
 * @Description: 美妆View
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MakeUpView extends RelativeLayout {
    private static final String TAG = "MakeUpView";
    private Context mContext;
    private RecyclerView mMakeupRecyclerView;
    private TabLayout mMakeUpTab;
    private MakeupAdapter mMakeupAdapter;

    private HashMap<String, List<MakeupListData>> mMakeupCategoryList = new HashMap<>();

    private MakeupHelper makeUpHelper;
    private MakeupListData mCurrentSelectedItem;
    private int mTabPosition = -1;
    private HashMap<Integer, MakeupListData> mSelectItemHashMap = new HashMap<>();
    private OnMakeupListener mMakeupListener;

    private int addFilterCount = 0;

    public MakeUpView(Context context) {
        this(context, null);
    }

    public MakeUpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
        initListener();
        initData(context);
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_make_up, this);
        mMakeupRecyclerView = rootView.findViewById(R.id.beauty_makeup_item_list);
        mMakeUpTab = rootView.findViewById(R.id.makeup_tab);

        mMakeupAdapter = new MakeupAdapter(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mMakeupRecyclerView.setLayoutManager(layoutManager);
        mMakeupRecyclerView.setAdapter(mMakeupAdapter);
    }


    private void initListener() {
        mMakeUpTab.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.blue_63));
                }
                tabSelect(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.gray_90));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mMakeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MakeupListData selectItem = mMakeupAdapter.getSelectItem();
                // record current makeup item ,Avoid repeated additions
                //记录当前选中的妆 同一个不重复添加
                if (null != mCurrentSelectedItem && TextUtils.equals(mCurrentSelectedItem.getName(), selectItem.getName())) {
                    return;
                }
                onMakeupDataChanged(mMakeUpTab.getSelectedTabPosition(), position);
            }
        });
    }

    private void initData(Context context) {
        mMakeUpTab.removeAllTabs();
        //Get the built-in single makeup category
        //获取内置单妆分类
        mMakeupCategoryList = MakeupDataManager.getMakeupCategoryList(context, false);
        if (mMakeUpTab.getTabCount() == 0) {
            mMakeUpTab.addTab(mMakeUpTab.newTab().setCustomView(newTabView(context.getString(R.string.makeup_))));
            for (Map.Entry<String, List<MakeupListData>> stringListEntry : mMakeupCategoryList.entrySet()) {
                String key = stringListEntry.getKey();
                if (context.getString(R.string.makeup_).equals(key)) {
                    continue;
                }
                mMakeUpTab.addTab(mMakeUpTab.newTab().setCustomView(newTabView(key)));
            }
        }

        mMakeupAdapter.setDataList(mMakeupCategoryList.get(context.getString(R.string.makeup_)), MakeupAdapter.MAKE_UP_ROUND_ICON_TYPE);
    }

    private TextView newTabView(String key) {
        TextView textView = new TextView(mContext);
        textView.setText(key);
        textView.setTextSize(12);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.gray_90));
        return textView;
    }

    /**
     * apply makeup package
     * 美妆应用
     *
     * @param tabPosition
     * @param position
     */
    private void onMakeupDataChanged(int tabPosition, int position) {
        // select none
        //选择无
        if (position == 0) {
            if (mSelectItemHashMap.containsKey(tabPosition)) {
                MakeupListData nvComposeMakeup = mSelectItemHashMap.get(tabPosition);
                mSelectItemHashMap.remove(tabPosition);
                removeMakeupEffect(nvComposeMakeup);
            }
            if (tabPosition == 0) {
                mSelectItemHashMap.clear();
            }
            mCurrentSelectedItem = null;
            if (null != mMakeupListener) {
                mMakeupListener.onMakeup(tabPosition, position);
            }
            return;
        }
        if (mTabPosition == tabPosition && null != mCurrentSelectedItem) {
            // remove last makeup effect
            //移除上个妆容中的效果
            mSelectItemHashMap.remove(tabPosition);
            removeMakeupEffect(mCurrentSelectedItem);
        }
        mTabPosition = tabPosition;
        MakeupListData selectItem = getSelectItem();
        mCurrentSelectedItem = selectItem;
        mSelectItemHashMap.put(tabPosition, selectItem);
        if (null == makeUpHelper) {
            makeUpHelper = new MakeupHelper();
        }
        makeUpHelper.applyCaptureMakeupFx(mCurrentSelectedItem);
    }

    /**
     * 移除这个妆容的效果
     *
     * @param selectItem
     */
    private void removeMakeupEffect(MakeupListData selectItem) {
        makeUpHelper.resetCaptureMakeupFx(selectItem);
    }


    private void tabSelect(TabLayout.Tab tab) {
        if (mMakeupCategoryList.isEmpty()) {
            return;
        }
        TextView textView = (TextView) tab.getCustomView();
        assert textView != null;
        Log.e(TAG, "tabSelect name: " + textView.getText());
        List<MakeupListData> makeupCategory = mMakeupCategoryList.get(textView.getText());
        parseSubCustom(makeupCategory);
    }

    private void parseSubCustom(List<MakeupListData> nvComposeMakeupData) {
        changeToMakeupSubMenu(nvComposeMakeupData, mContext.getString(R.string.makeup));
    }

    /**
     * swich to sub menu
     * 切换到子菜单
     *
     * @param data 数据 sub list
     */
    private void changeToMakeupSubMenu(final List<MakeupListData> data, String backText) {
        mMakeupAdapter.setDataList(data, MakeupAdapter.MAKE_UP_ROUND_ICON_TYPE);
        mMakeupAdapter.notifyDataSetChanged();
        // custom makeup select position
        String selectName = "";
        if (mSelectItemHashMap.containsKey(mMakeUpTab.getSelectedTabPosition())) {
            MakeupListData makeupListData = mSelectItemHashMap.get(mMakeUpTab.getSelectedTabPosition());
            selectName = makeupListData == null ? "" : makeupListData.getName();
        }
        if (!TextUtils.isEmpty(selectName)) {
            for (int i = 0; i < data.size(); i++) {
                if (selectName.equals(data.get(i).getName())) {
                    mMakeupAdapter.setSelectPos(i);
                    break;
                }
            }
        } else {
            mMakeupAdapter.setSelectPos(-1);
        }
        mMakeupAdapter.setEnable(true);
    }

    public void onResume() {
        if (null != makeUpHelper) {
            makeUpHelper.onResume();
        }
    }

    public MakeupListData getSelectItem() {
        if (mMakeupAdapter != null) {
            return mMakeupAdapter.getSelectItem();
        }
        return null;
    }

    public void setMakeupListener(OnMakeupListener mMakeupListener) {
        this.mMakeupListener = mMakeupListener;
    }

    public interface OnMakeupListener {
        void onMakeup(int tabPosition, int position);
    }
}
