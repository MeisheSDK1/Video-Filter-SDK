package com.meishe.sdkdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsColor;
import com.meishe.base.utils.ToastUtils;
import com.meishe.base.utils.ZipUtils;
import com.meishe.base.view.MagicProgress;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.makeup.makeup.BaseParam;
import com.meishe.makeup.makeup.CategoryContent;
import com.meishe.makeup.makeup.ColorData;
import com.meishe.makeup.makeup.FilterParam;
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
import com.meishe.sdkdemo.capture.MakeupAdapter;
import com.meishe.sdkdemo.edit.view.VerticalSeekBar;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.asset.NvHttpRequest;
import com.meishe.utils.ColorUtil;
import com.meishe.utils.PathNameUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 * 移植于MakeUpView
 *
 * @Author: Guijun
 * @CreateDate: 2021/07/06 11:29
 * @Description: 美妆View Makeup view
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MakeUpSingleView extends RelativeLayout implements NvHttpRequest.NvHttpRequestListener {
    public static final float DEFAULT_MAKEUP_INTENSITY = 0.6f;
    private static final int MAKEUP_ADJUST_MAKEUP = 0X100;
    private static final int MAKEUP_ADJUST_FILTER = 0X200;
    private static final String TAG = "MakeUpView";
    private static final int TYPE_CUSTOM = 0x200;
    private static final int TYPE_MAKEUP = 0x300;
    private Context mContext;
    private RecyclerView mMakeupRecyclerView;
    private TextView mTvColor, mTvAlpha;
    private View mMakeupColorHinLayout, mMakeupTopLayout;
    private ColorPickerView mColorPickerView;
    private VerticalIndicatorSeekBar mMakeupSeekBar;
    private TabLayout mMakeUpTab;
    private MakeupAdapter mMakeupAdapter;
    private LinearLayout mMakeupStrengthLayout;
    private RadioGroup mRgMakeup;
    private RadioButton mRbMakeup;
    private RadioButton mRbFilter;
    private MagicProgress mMpMakeup;
    private MakeUpEventListener mMakeUpEventListener;


    private List<MakeupCategory> mMakeupCategoryList = new ArrayList<>();

    private String mCurrentEffectId = null;

    private Makeup mPreMakeUpItem;
    /**
     * 当时使用的妆容
     * The makeup used at the time
     */
    private Makeup mComposeMakeup;
    private int mComposeMakeupIndesity = 100;
    private int mComposeFilterIndesity = 100;
    private int mMakeupAdjustType = MAKEUP_ADJUST_MAKEUP;
    private int mSelectPosition = -1;

    public MakeUpSingleView(Context context) {
        this(context, null);
    }

    public MakeUpSingleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public MakeUpSingleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initListener();
        initData();
    }

    public void setMakeupCategoryList(List<MakeupCategory> makeupCategoryList) {
        this.mMakeupCategoryList = makeupCategoryList;
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_make_up, this);
        mMakeupRecyclerView = rootView.findViewById(R.id.beauty_makeup_item_list);
        mMakeupTopLayout = rootView.findViewById(R.id.makeup_top_layout);
        mTvColor = rootView.findViewById(R.id.tv_color);
        mTvAlpha = rootView.findViewById(R.id.tv_alpha);
        mMakeupColorHinLayout = rootView.findViewById(R.id.makeup_color_hint_layout);
        mColorPickerView = rootView.findViewById(R.id.color_picker_view);
        mMakeupSeekBar = rootView.findViewById(R.id.seek_bar);
        mMakeUpTab = rootView.findViewById(R.id.makeup_tab);

        mMakeupAdapter = new MakeupAdapter(mContext, new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mMakeupRecyclerView.setLayoutManager(layoutManager);
        mMakeupRecyclerView.setAdapter(mMakeupAdapter);
        mMakeupAdapter.setEnable(true);
        mMakeupStrengthLayout = findViewById(R.id.makeup_strength_layout);
        mRgMakeup = findViewById(R.id.rg_makeup);
        mRbMakeup = findViewById(R.id.rb_makeup);
        mRbFilter = findViewById(R.id.rb_filter);
        mMpMakeup = findViewById(R.id.mp_makeup);
        mMpMakeup.setPointEnable(true);
        mMpMakeup.setBreakProgress(0);
        mMpMakeup.setShowBreak(false);
        findViewById(R.id.change_layout).setVisibility(GONE);
        findViewById(R.id.line).setVisibility(GONE);
        mColorPickerView.setOnColorSeekBarStateChangeListener(new ColorPickerView.OnColorSeekBarStateChangeListener() {
            @Override
            public void onColorSeekBarStateChanged(boolean show) {
                mMakeupColorHinLayout.setVisibility(show ? View.VISIBLE : View.GONE);
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
        mRgMakeup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_makeup) {
                    mRbMakeup.setChecked(true);
                    mMakeupAdjustType = MAKEUP_ADJUST_MAKEUP;
                    mMpMakeup.setProgress(mComposeMakeupIndesity);
                } else if (checkedId == R.id.rb_filter) {
                    mRbFilter.setChecked(true);
                    mMakeupAdjustType = MAKEUP_ADJUST_FILTER;
                    mMpMakeup.setProgress(mComposeFilterIndesity);
                }
            }
        });
        mMpMakeup.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (fromUser || (null == mComposeMakeup)) {
                    return;
                }
                float intensityValue = progress * 1.0F / 100;
                MakeupParamContent paramContent = mComposeMakeup.getEffectContent();
                if (null == paramContent) {
                    return;
                }
                if (mMakeupAdjustType == MAKEUP_ADJUST_MAKEUP) {
                    mComposeMakeupIndesity = progress;
                    onMakeupIntensity(paramContent, intensityValue);
                    return;
                }
                mComposeFilterIndesity = progress;
                onMakeupFilterIntensity(paramContent, intensityValue);


            }
        });
        mMakeUpTab.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //判断应用整妆之后，修改单妆，当前切换的单妆 在整妆里是否支持替换
                //After applying the makeup, modify the makeup. Whether the current makeup can be replaced in the makeup
                int position = tab.getPosition();
                if (null != mPreMakeUpItem && !mPreMakeUpItem.isSingleMakeup() && position != 0) {
                    //取出对应的单妆的id Take out the id of the corresponding single makeup
                    String tabMakeupId = mMakeupCategoryList.get(position).getDisplayName();
                    //判断当前要切换的是否支持替换  Check whether the current switch supports replacement
                    MakeupParamContent effectContent = mPreMakeUpItem.getEffectContent();
                    if (effectContent != null) {
                        List<MakeupParam> makeupParamList = effectContent.getMakeupParams();
                        if (null != makeupParamList && makeupParamList.size() > 0) {
                            for (MakeupParam makeupParam : makeupParamList) {
                                if (TextUtils.equals(makeupParam.getType(), tabMakeupId)) {
                                    if (!makeupParam.canReplace()) {
                                        ToastUtils.showShort(R.string.makeup_not_allow_change_this);
                                        mMakeUpTab.getTabAt(0).select();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.black));
                }
                tabSelect(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(com.meishe.base.R.color.color_888888));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mColorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {

            @Override
            public void onColorChanged(ColorData colorData) {
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
        mMakeupSeekBar.setOnSeekBarChangedListener(new VerticalIndicatorSeekBar.OnSeekBarChangedListener() {

            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                if (TextUtils.isEmpty(mCurrentEffectId)) {
                    return;
                }
                Makeup selectItem = getSelectItem();
                float value = progress / 100f;
                MakeupParamContent effectContent = selectItem.getEffectContent();
                if (effectContent != null) {
                    MakeupParam makeupParam = effectContent.getMakeupParam(0);
//                    if (makeupParam != null) {
//                        makeupParam.setValue(value);
//                    }
                    List<BaseParam.Param> params = makeupParam.getParams();
                    if (null == params || params.size() == 0) {
                        return;
                    }
                    for (BaseParam.Param param : params) {
                        if (param.getValue() instanceof Float) {
                            param.setValue(value);
                            break;
                        }
                    }
                }
                String alpha = String.format(getResources().getString(R.string.make_up_transparency), progress) + "%";
                mTvAlpha.setText(alpha);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupIntensityChanged(mCurrentEffectId, value);
                }
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {

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

        if (mMakeUpTab.getTabCount() == 0) {
            for (int i = 0; i < mMakeupCategoryList.size(); i++) {
                MakeupCategory makeupCategory = mMakeupCategoryList.get(i);
                makeupCategory.setDisplayName(Util.upperCaseName(makeupCategory.getDisplayName()));
                TextView textView = new TextView(mContext);
                if (MSApplication.isZh()) {
                    textView.setText(makeupCategory.getDisplayNameZhCn());
                } else {
                    textView.setText(makeupCategory.getDisplayName());
                }
                textView.setTextSize(12);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(com.meishe.base.R.color.color_888888));
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
        if (null == categoryContent) {
            return;
        }
        mMakeupAdapter.setDataList(null);
        HttpManager.cancelAll();
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
     * @param data     数据
     * @param backText 返回显示文本
     */
    private void changeToMakeupSubMenu(final List<Makeup> data, String backText) {
        mMakeupAdapter.setDataList(data);
        // custom makeup select position
        int index = 0;
        setColorPickerVisibility(View.INVISIBLE, null);
        if (!TextUtils.isEmpty(mCurrentEffectId)) {
            for (MakeupCategory category : mMakeupCategoryList) {
                if (mCurrentEffectId.equals(category.getDisplayName())) {
                    index = category.getCategoryContent().getSelectedPosition();
                }
            }
        }
        int tabPos = mMakeUpTab.getSelectedTabPosition();
        updateMakeupIntensityView((tabPos == 0) && (null != mComposeMakeup));
        if (tabPos > 0 && index > 0) {
            Makeup makeup = data.get(index);
            if (null != makeup) {
                setColorPickerVisibility(View.VISIBLE, makeup);
                MakeupParamContent effectContent = makeup.getEffectContent();
                if (effectContent != null) {
                    MakeupParam makeupParam = effectContent.getMakeupParam(0);
                    List<BaseParam.Param> params = makeupParam.getParams();
                    if (null == params || params.isEmpty()) {
                        return;
                    }
                    for (BaseParam.Param param : params) {
                        if (param.getValue() instanceof Float) {
                            int progress = (int) (((float) param.getValue()) * 100);
                            mMakeupSeekBar.setProgress(progress);
                            break;
                        }
                    }
                }

            }
        }
        mMakeupAdapter.setSelectPos(index);
        mSelectPosition = index;
        mMakeupAdapter.setEnable(true);
        mMakeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mSelectPosition == position) {
                    return;
                }
                mSelectPosition = position;
                Makeup selectItem = mMakeupAdapter.getSelectItem();
                int tabPos = mMakeUpTab.getSelectedTabPosition();
                MakeupCategory category = mMakeupCategoryList.get(tabPos);
                category.getCategoryContent().setSelectedPosition(position);
                setColorPickerVisibility(View.INVISIBLE, selectItem);
                if (position != 0) {
                    onSubViewSelect(position, selectItem);
                }
                //记录上一次选择的妆
                //Keep track of your last makeup selection
                mPreMakeUpItem = selectItem;
                if (position == 0) {
                    mPreMakeUpItem = null;
                }
                if ((tabPos == 0) && (position == 0)) {
                    mComposeMakeup = null;
                }
                if (tabPos == 0) {
                    mComposeMakeupIndesity = 100;
                    mComposeFilterIndesity = 100;
                    mRbMakeup.setChecked(true);
                    mRbFilter.setChecked(false);
                }
                updateMakeupIntensityView((tabPos == 0) && (position > 0));
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupViewDataChanged(tabPos, position, false);
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
        if (makeupList == null || makeupList.isEmpty()) {
            return;
        }
        changeToMakeupSubMenu(makeupList, mContext.getString(R.string.makeup));
    }

    private void updateMakeupIntensityView(boolean isShow) {
        mMakeupStrengthLayout.setVisibility(isShow ? VISIBLE : INVISIBLE);
        mMpMakeup.setProgress((mMakeupAdjustType == MAKEUP_ADJUST_MAKEUP) ? mComposeMakeupIndesity : mComposeFilterIndesity);
    }

    /**
     * 安装自定义美妆内的文件
     * Install the file inside custom makeup
     *
     * @param folderPath
     */
    private void installCustomAsset(String folderPath) {
        if (folderPath.startsWith("/storage/")) {
            File file = new File(folderPath);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }
            for (File listFile : file.listFiles()) {
                installNewMakeup(listFile.getAbsolutePath());
            }
        } else {
            try {
                for (String filePath : mContext.getAssets().list(folderPath)) {
                    installNewMakeup("assets:/" + folderPath + File.separator + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

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
            mColorPickerView.setDefaultColor(colors, colorData);
//            setColorPickerVisibility(View.VISIBLE, item);
        }
        setColorPickerVisibility(View.VISIBLE, selectItem);
    }

    /**
     * 美妆强度调节
     * Beauty intensity adjustment
     *
     * @param paramContent   content
     * @param intensityValue value
     */
    private void onMakeupIntensity(MakeupParamContent paramContent, float intensityValue) {
        List<MakeupParam> makeups = paramContent.getMakeupParams();
        for (MakeupParam makeup : makeups) {
            if (null == makeup) {
                continue;
            }
            /*
             *判断是否需要修改单妆强度。如果选择过某一个单妆，则在调节妆容的时候不调节该单妆强度
             *Determine whether the strength of single makeup needs to be modified.
             *If a single makeup is selected, do not adjust the intensity of the single makeup when adjusting makeup
             */
            boolean isChange = true;
            for (MakeupCategory category : mMakeupCategoryList) {
                if (null == category) {
                    continue;
                }
                String disPlayName = category.getDisplayName();
                if (TextUtils.equals(makeup.getType(), disPlayName)) {
                    int position = category.getCategoryContent().getSelectedPosition();
                    if (position > 0) {
                        isChange = false;
                        break;
                    }
                }
            }
            if (!isChange) {
                continue;
            }

            List<BaseParam.Param> params = makeup.getParams();
            if ((null == params) || params.isEmpty()) {
                continue;
            }
            for (BaseParam.Param param : params) {
                if (null == param) {
                    continue;
                }
                String key = param.getKey();
                String type = param.getType();
                if (TextUtils.equals(type, "float") && key.endsWith("Intensity")) {
                    double defValue = (double) param.getValue();
                    float intensity = (float) (intensityValue * defValue);
                    if (mMakeUpEventListener != null) {
                        mMakeUpEventListener.onMakeupIntensityChanged(makeup.getType(), intensity);
                    }
                    break;
                }

            }
        }
    }

    /**
     * 美妆-滤镜强度调节
     * Beauty - Filter intensity adjustment
     *
     * @param paramContent   content
     * @param intensityValue value
     */
    private void onMakeupFilterIntensity(MakeupParamContent paramContent, float intensityValue) {
        List<FilterParam> filters = paramContent.getFilterParams();
        if (null == filters) {
            return;
        }
        for (FilterParam filter : filters) {
            if (null == filter) {
                continue;
            }
            String packageId = filter.getPackageId();
            if (TextUtils.isEmpty(packageId)) {
                continue;
            }
            float defValue = filter.getValue();
            float intensity = (float) (intensityValue * defValue);
            if (mMakeUpEventListener != null) {
                mMakeUpEventListener.onMakeupFilterIntensityChanged(packageId, intensity);
            }
        }

    }

    public Makeup getSelectItem() {
        if (mMakeupAdapter != null) {
            return mMakeupAdapter.getSelectItem();
        }
        return null;
    }

    public int getTabPosition() {
        if (null == mMakeUpTab) {
            return -1;
        }
        return mMakeUpTab.getSelectedTabPosition();
    }

    /**
     * 获取当前应用的单妆
     * Get the currently applied single makeup
     *
     * @return
     */
    public String getSelectMakeupId() {
        int position = mMakeUpTab.getSelectedTabPosition();
        if ((position == 0) || mMakeupCategoryList.isEmpty()) {
            return "";
        }
        return mCurrentEffectId;
    }

    public void updateSelectMakeup(Makeup makeup) {
        if ((null == makeup)) {
            return;
        }
        mComposeMakeup = makeup;
    }

    public Makeup getComposeMakeup() {
        return mComposeMakeup;
    }

    public List<MakeupCategory> getMakeupCategory() {
        return mMakeupCategoryList;
    }

    public void setColorPickerVisibility(int visibility, Makeup makeup) {
        boolean colorFlag = false;
        if (makeup != null) {
            MakeupParamContent effectContent = makeup.getEffectContent();
            if (effectContent != null) {
                List<MakeupParam> makeupParams = effectContent.getMakeupParams();
                if (makeupParams != null && makeupParams.size() == 1) {
                    MakeupParam makeupParam = makeupParams.get(0);
                    colorFlag = makeupParam != null && makeupParam.getMakeupRecommendColors() != null
                            && makeupParam.getMakeupRecommendColors().size() > 0;
                }
            }
        }
        mColorPickerView.setVisibility(colorFlag ? visibility : INVISIBLE);
        mMakeupColorHinLayout.setVisibility(colorFlag ? visibility : INVISIBLE);
        mMakeupSeekBar.setVisibility(visibility);
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
         * @param isClearMakeup
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
         * 美妆-单妆强度修改
         * Beauty intensity modification
         *
         * @param makeupId  美妆id Makeup id
         * @param intensity 强度 intensity
         */
        void onMakeupIntensityChanged(String makeupId, float intensity);

        /**
         * 美妆-滤镜强度修改
         * Beauty - Filter strength modification
         *
         * @param filterId  filter Id
         * @param intensity intensity
         */
        void onMakeupFilterIntensityChanged(String filterId, float intensity);

        /**
         * 移除美妆中自带的特效
         * Remove special effects from your makeup
         *
         * @param name 特效名称 name
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
                            //ToastUtils.showShort("get makeup tab error");
                            return;
                        }
                        List<MakeupCategoryInfo> data = response.getData();
                        if (data.isEmpty()) {
                            return;
                        }
                        List<MakeupCategory> makeupCategoryList = data.get(0).getCategories().get(0).getKinds();
                        if (makeupCategoryList == null || makeupCategoryList.isEmpty()) {
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
                        //ToastUtils.showShort("get makeup tab error");
                    }
                });
    }


    /**
     * 获取妆容，单妆素材数据
     * Get makeup, single makeup material data
     */
    public void getMaterialListAll(final int type, AssetType assetType, String kind, final CategoryContent categoryContent) {
        assetType.setSorted(0);
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
                        if ((response.getCode() != 1) || (dataBean == null)) {
                            //ToastUtils.showShort("get makeup data error");
                            if (categoryContent != null) {
                                categoryContent.setUpdatedFromNet(true);
                                parseSubCustom(categoryContent.getAllMakeupList());
                            }
                            return;
                        }
                        String name = Looper.myLooper().getThread().getName();
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
                                if (MSApplication.isZh()) {
                                    makeup.setName(data.getDisplayNamezhCN());
                                } else {
                                    makeup.setName(data.getDisplayName());
                                }
                                makeup.setAssetsDirectory(PathUtils.getSDCardPathByType(NvAsset.ASSET_MAKEUP));
                                if (type == TYPE_CUSTOM) {
                                    //单妆 single makeup
                                    MakeupParamContent makeupParamContent = new MakeupParamContent();
                                    makeup.setEffectContent(makeupParamContent);
                                    List<MakeupParam> makeupParamList = new ArrayList<>();
                                    makeupParamContent.setMakeupParams(makeupParamList);
                                    MakeupParam makeupParam = new MakeupParam();
                                    makeupParamList.add(makeupParam);
                                    makeupParam.setPackageId(data.getId());
//                                  //makeupParam.setParamKey(infoJson.getClassName());
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
                                    // 妆容  makeup
                                    makeup.setUuid(data.getId());
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
                                        downloadMakeupZip(data.getZipUrl(), makeup.getAssetsDirectory(), data.getId());
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
                        }
                        parseSubCustom(categoryContent.getAllMakeupList());
                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean<InfoJson>>> response) {
                        //ToastUtils.showShort("get makeup data error");
                        if (categoryContent != null) {
                            //  categoryContent.setUpdatedFromNet(true);
                            parseSubCustom(categoryContent.getAllMakeupList());
                        }
                    }
                });
    }

    /**
     * 下载美妆包
     * Download the Beauty Kit
     *
     * @param packageUrl
     * @param folderPath
     */
    private void downloadMakeupZip(String packageUrl, String folderPath, String id) {
        String[] split = packageUrl.split("/");
        String fileName = split[split.length - 1];
        HttpManager.download(packageUrl, packageUrl, folderPath, fileName, new SimpleDownListener(packageUrl) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {

            }

            @Override
            public void onFinish(File file, Progress progress) {
                // 解压 以及安装 Unzip and install
//                try {
//                    List<File> files = ZipUtils.unzipFile(file, new File(folderPath + File.separator + fileName.split("\\.")[0]));
//                    if (files != null && !files.isEmpty()) {
//                        FileUtils.deleteFile(file);
//                        for (File filePath : files) {
//                            installNewMakeup(filePath.getAbsolutePath());
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
                unZipFile(file, folderPath, id);
            }
        });
    }

    private void unZipFile(File file, String folderPath, String fileName) {
        try {
            List<File> files = ZipUtils.unzipFile(file, new File(folderPath + File.separator + fileName.split("\\.")[0]));
            if (files != null && !files.isEmpty()) {
                FileUtils.deleteFile(file);
                String subZipPath = "";
                for (File filePath : files) {
                    String absolutePath = filePath.getAbsolutePath();
                    if (absolutePath.endsWith(".zip")) {
                        subZipPath = absolutePath;
                        break;
                    }
                    installNewMakeup(filePath.getAbsolutePath());
                }
                if (!TextUtils.isEmpty(subZipPath)) {
                    String zipName = folderPath + File.separator + fileName.split("\\.")[0];
                    unZipFile(new File(subZipPath), zipName, fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

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
                , MakeUpSingleView.this, NvAsset.ASSET_MAKEUP, localPath);
    }

    private void installMakeupPkg(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        NvAssetManager.sharedInstance().installAssetPackage(
                url,
                NvAsset.ASSET_MAKEUP, true);
    }


}
