package com.meishe.sdkdemo.mimodemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.mimodemo.adapter.MiMoListAdapter;
import com.meishe.sdkdemo.mimodemo.bean.MiMoLocalData;
import com.meishe.sdkdemo.mimodemo.common.utils.ScreenUtils;
import com.meishe.sdkdemo.mimodemo.common.view.SpaceItemDecoration;
import com.meishe.sdkdemo.mimodemo.interf.OnMiMoSelectListener;

import java.util.List;
/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : ccg
 * @CreateDate : 2020/6/12.
 * @Description :mimo模板列表。MiMoListFragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MiMoListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<MiMoLocalData> mTemplateList;
    private MiMoListAdapter mAdapter;

    private OnMiMoSelectListener mOnMiMoSelectListener;

    public void setOnTemplateSelectListener(OnMiMoSelectListener onMiMoSelectListener) {
        this.mOnMiMoSelectListener = onMiMoSelectListener;
    }

    public MiMoListFragment() {
    }

    @SuppressLint("ValidFragment")
    public MiMoListFragment(List<MiMoLocalData> value) {
        mTemplateList = value;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mimo_template_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.filter_list);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(this.getContext(), 4),
                ScreenUtils.dip2px(this.getContext(), 4)));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MiMoListAdapter(getContext(), mTemplateList);
        mAdapter.setOnTemplateSelectListener(mOnMiMoSelectListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setSelectPosition(int position) {
        mAdapter.setSelectPosition(position);
    }

    public void setNewDatas(List<MiMoLocalData> mDataListLocals) {
        if (mDataListLocals == null || mAdapter == null) {
            return;
        }
        mAdapter.setNewDatas(mDataListLocals);
    }

    public MiMoLocalData getCurrentData() {
        return mAdapter.getCurrentData();
    }
}
