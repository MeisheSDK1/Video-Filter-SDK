package com.meishe.sdkdemo.capturescene;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capturescene.view.CaptureSceneEffectView;
import com.meishe.sdkdemo.utils.Constants;

public class CaptureSceneFragment extends Fragment {
    private static final String ARG_TYPE = "Type";
    private int mType;
    private CaptureSceneEffectView mCaptureSceneEffectView;
    private NvsStreamingContext mStreamingContext;
    private FrameLayout mRootContainer;

    public CaptureSceneFragment() {
        // Required empty public constructor
    }

    public static CaptureSceneFragment newInstance(int type) {
        CaptureSceneFragment fragment = new CaptureSceneFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MSBus.getInstance().register(this);
        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture_scene, container, false);
        mRootContainer = view.findViewById(R.id.fl_root);
        mStreamingContext = NvsStreamingContext.getInstance();
        if (mCaptureSceneEffectView == null) {
            mCaptureSceneEffectView = new CaptureSceneEffectView(getContext());
            mRootContainer.addView(mCaptureSceneEffectView);
        }
        mCaptureSceneEffectView.setStreamingContext(mStreamingContext, getActivity(), mType);
        return view;
    }

    public void clearCaptureScene() {
        mCaptureSceneEffectView.clearCaptureScene();
    }

    @MSSubscribe(Constants.SubscribeType.SUB_CAPTURE_SCENE_PACKAGE)
    public void refreshCaptureSceneItem(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        if (null != mCaptureSceneEffectView) {
            mCaptureSceneEffectView.selectPosition(filePath);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MSBus.getInstance().unregister(this);
        mCaptureSceneEffectView.onDestroy();
    }

    public void addLocalResource(String filePath) {
        if (mCaptureSceneEffectView != null) {
            mCaptureSceneEffectView.addLocalResource(filePath);
        }
    }
}