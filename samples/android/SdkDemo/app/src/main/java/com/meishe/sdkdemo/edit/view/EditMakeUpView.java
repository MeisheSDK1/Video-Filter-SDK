package com.meishe.sdkdemo.edit.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsColor;
import com.meishe.base.utils.ZipUtils;
import com.meishe.base.view.MagicProgress;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.makeup.makeup.BaseParam;
import com.meishe.makeup.makeup.CategoryContent;
import com.meishe.makeup.makeup.ColorData;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupCategory;
import com.meishe.makeup.makeup.MakeupParam;
import com.meishe.makeup.makeup.MakeupParamContent;
import com.meishe.makeup.net.InfoJson;
import com.meishe.makeup.net.MakeupCategoryInfo;
import com.meishe.makeup.net.RecommendColor;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.EditMakeupAdapter;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.asset.NvHttpRequest;
import com.meishe.sdkdemo.view.ButtonRoundColorView;
import com.meishe.sdkdemo.view.ColorSeekBar;
import com.meishe.utils.ColorUtil;
import com.meishe.utils.PathNameUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2021/11/08.
 * @Description: 美妆View Beauty View
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class EditMakeUpView extends RelativeLayout implements NvHttpRequest.NvHttpRequestListener {
    public static final float DEFAULT_MAKEUP_INTENSITY = 0.6f;
    private static final String TAG = "MakeUpView";
    private static final int TYPE_COMPOSE = 0x100;
    private static final int TYPE_CUSTOM = 0x200;
    private static final int TYPE_MAKEUP = 0x300;
    private Context mContext;
    private RecyclerView mMakeupRecyclerView;
    private TextView mTvColor, mTvAlpha;
    private View mMakeupColorHinLayout, mMakeupTopLayout;
    private MagicProgress mMakeupSeekBar;
    private TabLayout mMakeUpTab;

    private EditMakeupAdapter mMakeupAdapter;
    private MakeUpEventListener mMakeUpEventListener;

    private List<MakeupCategory> mMakeupCategoryList = new ArrayList<>();
    private HashMap<String, Makeup> mSelectedMakeupMap;
    private String mCurrentEffectId = null;

    private ColorSeekBar mColorSeekView;
    private ButtonRoundColorView mCustomButtonView;
    private Makeup mCurrentItem;

    public EditMakeUpView(Context context) {
        this(context, null);
    }

    public EditMakeUpView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public EditMakeUpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initListener();
        initData();
    }

    public void setMakeupCategoryList(List<MakeupCategory> makeupCategoryList, HashMap<String, Makeup> selectedMakeupMap) {
        this.mMakeupCategoryList = makeupCategoryList;
        this.mSelectedMakeupMap = selectedMakeupMap;
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_edit_make_up, this);
        mMakeupRecyclerView = rootView.findViewById(R.id.beauty_makeup_item_list);
        mMakeupTopLayout = rootView.findViewById(R.id.makeup_top_layout);
        mTvColor = rootView.findViewById(R.id.tv_color);
        mTvAlpha = rootView.findViewById(R.id.tv_alpha);
        mMakeupColorHinLayout = rootView.findViewById(R.id.makeup_color_hint_layout);
        mColorSeekView = rootView.findViewById(R.id.color_seekBar);
        mCustomButtonView = rootView.findViewById(R.id.custom_btn);
        mMakeupSeekBar = rootView.findViewById(R.id.seek_bar);
        mMakeupSeekBar.setMax(100);
        mMakeupSeekBar.setPointEnable(false);
        mMakeupSeekBar.setBreakProgress(0);

        mMakeUpTab = rootView.findViewById(R.id.makeup_tab);

        mMakeupAdapter = new EditMakeupAdapter(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mMakeupRecyclerView.setLayoutManager(layoutManager);
        mMakeupRecyclerView.setAdapter(mMakeupAdapter);
        mMakeupAdapter.setEnable(true);
        mColorSeekView.setOnColorChangedListener(new ColorSeekBar.OnColorChangedListener() {
            @Override
            public void onColorChanged(int co) {

            }

            @Override
            public void onColorChanged(int co, float progress) {
                mCustomButtonView.setColor(co);
                ColorData colorData = new ColorData(mColorSeekView.rawX, -1, co);
                int color = colorData.color;
                mTvColor.setVisibility(View.VISIBLE);
                mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(color).toUpperCase()));
                float alphaF = (Color.alpha(color) * 1.0f / 255f);
                float red = (Color.red(color) * 1.0f / 255f);
                float green = (Color.green(color) * 1.0f / 255f);
                float blue = (Color.blue(color) * 1.0f / 255f);
                NvsColor nvsColor = new NvsColor(red, green, blue, alphaF);
                Makeup selectItem = getSelectItem();
                if (selectItem != null) {
                    MakeupParamContent effectContent = selectItem.getEffectContent();
                    if (effectContent != null) {
                        MakeupParam makeupParam = effectContent.getMakeupParam(0);
                        if (makeupParam != null) {
                            makeupParam.setColorData(colorData);
                        }
                    }
                }
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupColorChanged(mCurrentEffectId, nvsColor);
                }
            }
        });

        mCustomButtonView.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));
        mCustomButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mColorSeekView.getVisibility() == View.VISIBLE) {
                    mColorSeekView.setVisibility(INVISIBLE);
                    mCustomButtonView.setSelected(false);
                    mCustomButtonView.setText("");
                    mCustomButtonView.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));
                } else {
                    mColorSeekView.setVisibility(VISIBLE);
                    mCustomButtonView.setSelected(true);
                    int color = 0;

                    if (mCurrentItem != null && mCurrentItem.isSingleMakeup()) {
                        MakeupParamContent effectContent = mCurrentItem.getEffectContent();
                        if (effectContent != null) {
                            List<MakeupParam> makeupParams = effectContent.getMakeupParams();
                            if (makeupParams != null) {
                                MakeupParam makeupParam = makeupParams.get(0);
                                ColorData colorData = makeupParam.getColorData();
                                if (colorData != null) {
                                    color = colorData.color;
                                }
                            }
                        }
                        if (color == 0) {
                            color = Color.WHITE;
                        }
                    } else {
                        color = Color.WHITE;
                    }
                    mCustomButtonView.setColor(color);
                    mCustomButtonView.setBitmap(null);
                }
            }
        });
        mMakeupTopLayout.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMakeupTopLayout.getId() == v.getId() && mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeUpViewDismiss();
                }
                return false;
            }
        });
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

        mMakeupSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {

            @Override
            public void onProgressChange(int progress, boolean fromUser) {
//                if (!fromUser) {
//                    return;
//                }
                if (TextUtils.isEmpty(mCurrentEffectId)) {
                    return;
                }
                Makeup selectItem = getSelectItem();
                if (null == selectItem) {
                    return;
                }
                float value = progress / 100f;
                MakeupParamContent effectContent = selectItem.getEffectContent();
                if (effectContent != null) {
                    MakeupParam makeupParam = effectContent.getMakeupParam(0);
                    if (makeupParam != null) {
//                        makeupParam.setValue(value);
                        List<BaseParam.Param> params = makeupParam.getParams();
                        if (null == params || params.isEmpty()) {
                            return;
                        }
                        for (BaseParam.Param param : params) {
                            if (param.getValue() instanceof Float) {
                                param.setValue(value);
                                break;
                            }
                        }
                    }
                }
                String alpha = String.format(getResources().getString(R.string.make_up_transparency), progress) + "%";
                mTvAlpha.setText(alpha);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupIntensityChanged(mCurrentEffectId, value);
                }
            }
        });
    }

    private void initData() {
        if (mMakeupCategoryList.isEmpty()) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            }, 100);
        } else {
            getMaterialListAll(TYPE_MAKEUP, AssetType.MAKEUP_TYPE_ALL, null, mMakeupCategoryList.get(0).getCategoryContent());
        }
        mMakeUpTab.removeAllTabs();
        //获取内置单妆分类 Get the built-in single makeup assortment
        if (mMakeUpTab.getTabCount() == 0) {
            for (int i = 0; i < mMakeupCategoryList.size(); i++) {
                MakeupCategory item = mMakeupCategoryList.get(i);
                item.setDisplayName(Util.upperCaseName(item.getDisplayName()));
                TextView textView = new TextView(mContext);
                if (MSApplication.isZh()) {
                    textView.setText(item.getDisplayNameZhCn());
                } else {
                    textView.setText(item.getDisplayName());
                }
                textView.setTextSize(12);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.gray_90));
                mMakeUpTab.addTab(mMakeUpTab.newTab().setCustomView(textView));
            }
        }
        getCustomTab();
    }

    private void tabSelect(TabLayout.Tab tab) {
        if (mMakeupCategoryList.isEmpty()) {
            return;
        }
        MakeupCategory makeupCategory = mMakeupCategoryList.get(tab.getPosition());
        mCurrentEffectId = makeupCategory.getDisplayName();
        CategoryContent categoryContent = makeupCategory.getCategoryContent();
        if (categoryContent.isUpdatedFromNet()) {
            parseSubCustom(categoryContent.getAllMakeupList());
        } else {
            if (tab.getPosition() == 0) {
                getMaterialListAll(TYPE_MAKEUP, AssetType.MAKEUP_TYPE_ALL
                        , null, categoryContent);
            } else {
                getMaterialListAll(TYPE_CUSTOM, AssetType.MAKEUP_TYPE_LIST_ALL
                        , String.valueOf(categoryContent.getId()), categoryContent);
            }
        }
    }

    /**
     * 切换到子菜单
     * Switch to the submenu
     *
     * @param data     数据 data
     * @param backText 返回显示文本 Return display text
     */
    @SuppressLint("NotifyDataSetChanged")
    private void changeToMakeupSubMenu(final List<Makeup> data, String backText) {
        mMakeupAdapter.setDataList(data, EditMakeupAdapter.MAKE_UP_ROUND_ICON_TYPE);
        mMakeupAdapter.notifyDataSetChanged();
        // custom makeup select position
        int index = 0;
        mMakeupSeekBar.setVisibility(INVISIBLE);
        setColorPickerVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(mCurrentEffectId)) {
            for (MakeupCategory category : mMakeupCategoryList) {
                if (mCurrentEffectId.equals(category.getDisplayName())) {
                    index = category.getCategoryContent().getSelectedPosition();
                }
            }
        }
        if ((index > 0) && (index <= data.size())) {
            Makeup makeup = data.get(index);
            if ((null != makeup) && makeup.isSingleMakeup()) {
                mMakeupSeekBar.setVisibility(VISIBLE);
                MakeupParam makeupParam = makeup.getEffectContent().getMakeupParam(0);
                if (makeupParam != null) {
                    List<BaseParam.Param> params = makeupParam.getParams();
                    if (null == params || params.size() == 0) {
                        return;
                    }
                    for (BaseParam.Param param : params) {
                        if (param.getValue() instanceof Float) {
                            float value = (float) param.getValue();
                            mMakeupSeekBar.setProgress((int) (value * 100));
                            break;
                        }
                    }
                }
            }
        }
        mMakeupAdapter.setSelectPos(index);
        mMakeupAdapter.setEnable(true);
        mMakeupAdapter.setOnItemClickListener(new EditMakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mCurrentItem = mMakeupAdapter.getSelectItem();
                int tabPos = mMakeUpTab.getSelectedTabPosition();
                MakeupCategory category = mMakeupCategoryList.get(tabPos);
                category.getCategoryContent().setSelectedPosition(position);
                if ((tabPos > 0) && (position == 0)) {
                    mMakeupSeekBar.setVisibility(INVISIBLE);
                    setColorPickerVisibility(View.INVISIBLE);
                }
                if (mCurrentItem.isSingleMakeup()) {
                    if (position == 0) {
                        setColorPickerVisibility(View.INVISIBLE);
                    } else {
                        onSubViewSelect(position, mCurrentItem);
                    }
                }
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupViewDataChanged(mMakeUpTab.getSelectedTabPosition(), position, false);
                }

            }
        });
        final int finalIndex = index;
        mMakeupRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mMakeupRecyclerView.scrollToPosition(finalIndex);
            }
        });
    }

    private void parseSubCustom(List<Makeup> makeupList) {
        if (makeupList.isEmpty()) {
            return;
        }
        changeToMakeupSubMenu(makeupList, mContext.getString(R.string.makeup));
    }


    /**
     * select one type of makeup effect from a custom makeup category
     */
    private void onSubViewSelect(int position, Makeup selectItem) {
        if (selectItem.getEffectContent() == null) {
            return;
        }
        List<MakeupParam> makeupParams = selectItem.getEffectContent().getMakeupParams();
        if (makeupParams == null || makeupParams.size() > 1) {
            return;
        }
        // 取出单妆的参数 Take out the parameters of the single makeup
        MakeupParam makeupParam = makeupParams.get(0);
        List<BaseParam.Param> params = makeupParam.getParams();
        if (null == params || params.size() == 0) {
            return;
        }
        float intensity = DEFAULT_MAKEUP_INTENSITY;
        for (BaseParam.Param param : params) {
            if (param.getValue() instanceof Float) {
                intensity = (float) param.getValue();
                if (intensity == Float.MIN_VALUE) {
                    param.setValue(intensity = DEFAULT_MAKEUP_INTENSITY);
                    break;
                }
            }
        }
//        float intensity = makeupParam.getValue();
//        if (intensity == Float.MIN_VALUE) {
//            makeupParam.setValue(intensity = DEFAULT_MAKEUP_INTENSITY);
//        }
        mMakeupSeekBar.setVisibility(VISIBLE);
        mMakeupSeekBar.setProgress((int) (intensity * 100));

        //set RecommendColor
        List<RecommendColor> recommendColors = makeupParam.getMakeupRecommendColors();
        if (recommendColors != null && !recommendColors.isEmpty()) {
            int[] colors = new int[recommendColors.size()];
            for (int index = 0; index < recommendColors.size(); index++) {
                colors[index] = splitColor(recommendColors.get(index).getMakeupColor());
            }
            //set current use color
            ColorData colorData = makeupParam.getColorData();
            if (colorData == null) {
                colorData = new ColorData();
            }
            String progress = String.format(getResources().getString(R.string.make_up_transparency), (int) (intensity * 100)) + "%";
            mTvAlpha.setText(progress);

            int color = colorData.color;
            mTvColor.setVisibility((color == -1) ? INVISIBLE : VISIBLE);
            if (color >= 0) {
                mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(color).toUpperCase()));
            }
            mCustomButtonView.setVisibility(VISIBLE);
            mMakeupColorHinLayout.setVisibility(VISIBLE);
            mMakeupSeekBar.setVisibility(VISIBLE);
            mMakeupSeekBar.setProgress((int) (intensity * 100));
            mCustomButtonView.setColor(color);
            mColorSeekView.setColors(colorData.colorsProgress);
            float alphaF = (Color.alpha(color) * 1.0f / 255f);
            float red = (Color.red(color) * 1.0f / 255f);
            float green = (Color.green(color) * 1.0f / 255f);
            float blue = (Color.blue(color) * 1.0f / 255f);
            NvsColor nvsColor = new NvsColor(red, green, blue, alphaF);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMakeUpEventListener != null) {
                        mMakeUpEventListener.onMakeupColorChanged(mCurrentEffectId, nvsColor);
                    }
                }
            }, 0);

            mColorSeekView.setVisibility(INVISIBLE);
            mCustomButtonView.setSelected(false);
            mCustomButtonView.setText("");
            mCustomButtonView.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));

        } else {
            setColorPickerVisibility(View.INVISIBLE);
        }
    }

    public Makeup getSelectItem() {
        if (mMakeupAdapter != null) {
            return mMakeupAdapter.getSelectItem();
        }
        return null;
    }

    /**
     * 获取当前应用的单妆
     * Get the currently applied single makeup
     *
     * @return String
     */
    public String getSelectMakeupId() {
        if (mMakeupCategoryList.isEmpty()) {
            return "";
        }
        return mCurrentEffectId;
    }

    public List<MakeupCategory> getMakeupCategoryList() {
        return mMakeupCategoryList;
    }

    public void setColorPickerVisibility(int visibility) {
        mColorSeekView.setVisibility(visibility);
        mCustomButtonView.setVisibility(visibility);
        mMakeupColorHinLayout.setVisibility(visibility);
    }

    public void setOnMakeUpEventListener(MakeUpEventListener makeUpEventListener) {
        this.mMakeUpEventListener = makeUpEventListener;
    }

    @Override
    public void onGetAssetListSuccess(List responseArrayList, int assetType, boolean hasNext, String searchKey) {

    }

    @Override
    public void onGetAssetListFailed(Throwable e, int assetType) {
    }

    @Override
    public void onDonwloadAssetProgress(int progress, int assetType, String downloadId) {
    }

    @Override
    public void onDonwloadAssetSuccess(boolean success, String downloadPath, int assetType, String downloadId) {
        if (success) {
            installMakeupPkg(downloadPath);
        }
    }

    @Override
    public void onDonwloadAssetFailed(Throwable e, int assetType, String downloadId) {
    }

    public interface MakeUpEventListener {
        /**
         * 美妆应用
         * Beauty application
         *
         * @param tabPosition   tab位置 tab position
         * @param position      某一类中的position A position in a class
         * @param isClearMakeup isClearMakeup
         */
        void onMakeupViewDataChanged(int tabPosition, int position, boolean isClearMakeup);

        /**
         * 美妆颜色修改
         * Makeup color modification
         *
         * @param makeupId 美妆id Makeup id
         * @param color    颜色 color
         */
        void onMakeupColorChanged(String makeupId, NvsColor color);

        /**
         * 美妆强度修改
         * Beauty intensity modification
         *
         * @param makeupId  美妆id Makeup id
         * @param intensity 强度 intensity
         */
        void onMakeupIntensityChanged(String makeupId, float intensity);

        /**
         * 移除美妆中自带的特效
         * Remove special effects from your makeup
         *
         * @param name 特效名称 Special effect name
         */
        void removeVideoFxByName(String name);

        /**
         * 关闭美妆Dialog
         * Close the Beauty Dialog
         */
        void onMakeUpViewDismiss();
    }

    public int splitColor(String color) {
        String[] split = color.split(",");
        if (split.length == 4) {
            int red = (int) Math.floor(Float.parseFloat(split[0]) * 255 + 0.5D);
            int green = (int) Math.floor(Float.parseFloat(split[1]) * 255 + 0.5D);
            int blue = (int) Math.floor(Float.parseFloat(split[2]) * 255 + 0.5D);
            int alpha = (int) Math.floor(Float.parseFloat(split[3]) * 255 + 0.5D);
            return Color.argb(alpha, red, green, blue);
        }
        return 0;
    }

    /**
     * 获取单妆分类 Tab
     * Get the Single makeup category Tab
     */
    private void getCustomTab() {
        HttpManager.getMaterialTypeAndCategory(null
                , AssetType.MAKEUP_TYPE_LIST_ALL
                , MSApplication.getSdkVersion()
                , new RequestCallback<List<MakeupCategoryInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<MakeupCategoryInfo>> response) {
                        if ((response.getCode() != 1) || (response.getData() == null)) {
                            return;
                        }
                        List<MakeupCategoryInfo> data = response.getData();
                        if (data.isEmpty()) {
                            return;
                        }
                        List<MakeupCategory> makeupCategoryList = data.get(0).getCategories().get(0).getKinds();
                        if (makeupCategoryList.isEmpty()) {
                            return;
                        }
                        if (mMakeupCategoryList.isEmpty()) {
                            mMakeupCategoryList.addAll(makeupCategoryList);
                        } else {
                            // 以网络分类数据排序，把已有的CategoryContent设置给相应的分类
                            //Sort by network category data, setting an existing category content to the corresponding category
                            for (MakeupCategory makeupCategory : makeupCategoryList) {
                                if (makeupCategory == null) {
                                    return;
                                }
                                for (MakeupCategory info : mMakeupCategoryList) {
                                    if (makeupCategory.getId() == info.getId()) {
                                        makeupCategory.setCategoryContent(info.getCategoryContent());
                                        break;
                                    }
                                }
                            }
                            // 取出妆容 Take out makeup
                            MakeupCategory category = mMakeupCategoryList.get(0);
                            mMakeupCategoryList = makeupCategoryList;
                            makeupCategoryList.add(0, category);
                        }
                    }

                    @Override
                    public void onError(BaseResponse<List<MakeupCategoryInfo>> response) {
                        Log.e(TAG, "onError: get http makeup category data error!" + response.toString());
                    }
                });
    }


    /**
     * 获取妆容，单妆素材数据
     * Get makeup, single makeup material data
     */
    public void getMaterialListAll(final int type, AssetType assetType, String kind, final CategoryContent categoryContent) {
        HttpManager.getMaterialList(null
                , assetType
                , kind
                , 1, NvAsset.AspectRatio_All, ""
                , MSApplication.getSdkVersion()
                , 1, 100
                , new RequestCallback<BaseBean<BaseDataBean<InfoJson>>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BaseDataBean<InfoJson>>> response) {
                        BaseBean<BaseDataBean<InfoJson>> dataBean = response.getData();
                        if (response.getCode() != 1 || dataBean == null) {
                            //ToastUtils.showShort("get makeup data error");
                            if (categoryContent != null) {
                                categoryContent.setUpdatedFromNet(true);
                                parseSubCustom(categoryContent.getAllMakeupList());
                            }
                            return;
                        }
                        List<BaseDataBean<InfoJson>> dataElements = dataBean.getElements();
                        List<Makeup> oldRemoteMakeupList = categoryContent.getRemoteMakeupList();
                        List<Makeup> tempMakeupList = new ArrayList<>();
                        if (!dataElements.isEmpty()) {
                            for (BaseDataBean<InfoJson> data : dataElements) {
                                InfoJson infoJson = data.getInfoJson();
                                if (infoJson == null) {
                                    continue;
                                }
                                Makeup makeup = new Makeup();
                                makeup.setVersion(data.getVersion() + "");
                                makeup.setMinSdkVersion(data.getMinAppVersion());
                                makeup.setSupportedAspectRatio(data.getSupportedAspectRatio() + "");
                                makeup.setCover(data.getCoverUrl());
                                makeup.setUuid(data.getId());
                                if (MSApplication.isZh()) {
                                    makeup.setName(data.getDisplayNamezhCN());
                                } else {
                                    makeup.setName(data.getDisplayName());
                                }
                                makeup.setAssetsDirectory(PathUtils.getSDCardPathByType(NvAsset.ASSET_MAKEUP));
                                if (type == TYPE_CUSTOM) {
                                    // 单妆 single makeup
                                    MakeupParamContent makeupParamContent = new MakeupParamContent();
                                    makeup.setEffectContent(makeupParamContent);
                                    List<MakeupParam> makeupParamList = new ArrayList<>();
                                    makeupParamContent.setMakeupParams(makeupParamList);
                                    MakeupParam makeupParam = new MakeupParam();
                                    makeupParamList.add(makeupParam);
                                    makeupParam.setPackageId(data.getId());
//                                    makeupParam.setParamKey(infoJson.getClassName());
                                    List<BaseParam.Param> paramList = new ArrayList<>();
                                    //添加设置单妆的参数 Add parameters to set single makeup
                                    BaseParam.Param param = new BaseParam.Param();
                                    param.setKey(infoJson.getClassName());
                                    param.setValue(data.getId());
                                    param.setType("string");
                                    paramList.add(param);
                                    //添加设置单妆强度的参数 Add parameters to set the strength of a single makeup
                                    BaseParam.Param paramValue = new BaseParam.Param();
                                    paramValue.setKey("Makeup " + infoJson.getMakeupId() + " Intensity");
                                    paramValue.setValue(DEFAULT_MAKEUP_INTENSITY);
                                    paramValue.setType("float");
                                    paramList.add(paramValue);
                                    makeupParam.setParams(paramList);


                                    makeupParam.setType(infoJson.getMakeupId());
                                    makeupParam.setMakeupRecommendColors(infoJson.getMakeupRecommendColors());
                                    String[] customStr = data.getPackageUrl().split("\\/");
                                    String localPath = makeup.getAssetsDirectory() + File.separator + customStr[customStr.length - 1];
                                    //这里是获取的已经解压的文件夹路径
                                    // Here is the extracted folder path obtained
                                    localPath = PathNameUtil.getOutOfPathSuffixWithOutPoint(localPath);
                                    downloadAndinstallMakeupPkg(makeup.getAssetsDirectory(), localPath, data.getPackageUrl());
                                } else if (type == TYPE_MAKEUP) {
                                    // 妆容 makeup
                                    makeup.setEffectContent(infoJson.getEffectContent());
                                    boolean needDownload = true;
                                    if (oldRemoteMakeupList != null) {
                                        for (Makeup localMakeup : oldRemoteMakeupList) {
                                            if (localMakeup != null) {
                                                if (TextUtils.equals(localMakeup.getUuid(), makeup.getUuid())) {
                                                    needDownload = !TextUtils.equals(makeup.getVersion(), localMakeup.getVersion());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (needDownload) {
                                        downloadMakeupZip(data.getPackageUrl(), makeup.getAssetsDirectory());
                                    }
                                    // 注意要先下载再改变目录 Be sure to download before changing the directory
                                    makeup.setAssetsDirectory(makeup.getAssetsDirectory() + File.separator + makeup.getUuid());
                                } else {
                                    continue;
                                }
                                tempMakeupList.add(makeup);
                            }
                            categoryContent.setUpdatedFromNet(true);
                            if (tempMakeupList.size() > 0) {
                                // 使用网络端的排序以及数据 Use the network side for sorting and data
                                categoryContent.setRemoteMakeupList(tempMakeupList);
                            }
                            List<Makeup> allMakeupList = categoryContent.getAllMakeupList();
                            if (allMakeupList != null) {
                                for (Map.Entry<String, Makeup> entry : mSelectedMakeupMap.entrySet()) {
                                    Makeup value = entry.getValue();
                                    if (categoryContent.getType().equals(entry.getKey())) {
                                        // 设置选中的位置 Sets the selected location
                                        for (int i = 0; i < allMakeupList.size(); i++) {
                                            Makeup makeup = allMakeupList.get(i);
                                            if (TextUtils.equals(value.getUuid(), makeup.getUuid())) {
                                                categoryContent.setSelectedPosition(i);
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                parseSubCustom(allMakeupList);
                            }
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean<InfoJson>>> response) {
                        if (type == TYPE_COMPOSE) {
                            return;
                        }
//                        if (categoryContent != null) {
//                            parseSubCustom(categoryContent.getAllMakeupList());
//                        }
                    }
                });
    }

    /**
     * 下载美妆包
     * Download the Beauty package
     *
     * @param packageUrl url
     * @param folderPath path
     */
    private void downloadMakeupZip(String packageUrl, String folderPath) {
        String[] split = packageUrl.split("/");
        String fileName = split[split.length - 1];
        File file = new File(folderPath + File.separator + fileName.split("\\.")[0]);
        if (file.exists()) {
            return;
        }
        HttpManager.download(packageUrl, packageUrl, folderPath, fileName, new SimpleDownListener(packageUrl) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {

            }

            @Override
            public void onFinish(File file, Progress progress) {
                try {
                    List<File> files = ZipUtils.unzipFile(file, new File(folderPath + File.separator + fileName.split("\\.")[0]));
                    if (files != null && !files.isEmpty()) {
                        FileUtils.deleteFile(file);
                        for (File filePath : files) {
                            installNewMakeup(filePath.getAbsolutePath());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void installNewMakeup(String filePath) {
        if (filePath.endsWith(".makeup")) {
            NvAssetManager.sharedInstance().installAssetPackage(
                    filePath,
                    NvAsset.ASSET_MAKEUP, true);
        } else if (filePath.endsWith(".warp")) {
            NvAssetManager.sharedInstance().installAssetPackage(
                    filePath,
                    NvAsset.ASSET_MAKEUP_WARP, true);
        } else if (filePath.endsWith(".facemesh")) {
            NvAssetManager.sharedInstance().installAssetPackage(
                    filePath,
                    NvAsset.ASSET_MAKEUP_FACE, true);
        } else if (filePath.endsWith(".videofx")) {
            NvAssetManager.sharedInstance().installAssetPackage(
                    filePath,
                    NvAsset.ASSET_FILTER, true);
        }
        Log.d(TAG, "installNewMakeup :" + filePath);
    }

    /**
     * 下载
     * download
     *
     * @param packageUrl 文件路径 File path
     */
    private void downloadAndinstallMakeupPkg(String folderPath, String localPath, String packageUrl) {
        if (TextUtils.isEmpty(folderPath) || TextUtils.isEmpty(localPath) || TextUtils.isEmpty(packageUrl)) {
            return;
        }
        File packageFile = new File(localPath);
        if (packageFile.exists()) {
            installMakeupPkg(packageFile.getAbsolutePath());
            return;
        }
        String assetDownloadDestPath = localPath + ".tmp";
        NvHttpRequest.sharedInstance().downloadAsset(packageUrl
                , folderPath
                , assetDownloadDestPath
                , ".makeup"
                , EditMakeUpView.this, NvAsset.ASSET_MAKEUP, localPath);
    }

    /**
     * 安装
     * install
     *
     * @param url 安装路径 Installation path
     */
    private void installMakeupPkg(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        NvAssetManager.sharedInstance().installAssetPackage(
                url,
                NvAsset.ASSET_MAKEUP, true);
    }


}
