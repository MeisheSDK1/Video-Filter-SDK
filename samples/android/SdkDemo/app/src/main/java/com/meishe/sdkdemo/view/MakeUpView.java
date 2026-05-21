package com.meishe.sdkdemo.view;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: Guijun
 * @CreateDate: 2021/07/06 11:29
 * @Description: 美妆View
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
@Deprecated
public class MakeUpView {
    /*public static final float DEFAULT_MAKEUP_INTENSITY = 0.6f;
    private static final String TAG = "MakeUpView";
    private static final int TYPE_COMPOSE = 0x100;
    private static final int TYPE_CUSTOM = 0x200;
    private static final int TYPE_MAKEUP = 0x300;
    private boolean isFaceModel106 = (BuildConfig.FACE_MODEL == 106);
    private Context mContext;
    private RecyclerView mMakeupRecyclerView;
    private ImageView mMakeupChangeBtn;
    private TextView mMakeupChangeBtnText, mTvColor, mTvAlpha;
    private View mMakeupColorHinLayout, mMakeupTopLayout;
    private ColorPickerView mColorPickerView;
    private VerticalIndicatorSeekBar mMakeupSeekBar;
    private TabLayout mMakeUpTab;
    private boolean mIsMakeupMainMenu = true;

    private MakeupAdapter mMakeupAdapter;
    private MakeUpEventListener mMakeUpEventListener;
    *//**
     * 整妆
     *//*
    private ArrayList<Makeup2> mMakeupComposeData = new ArrayList<>();
    *//**
     * 单妆分类
     *//*
    private ArrayList<MakeupCategory> mCategoryInfos = new ArrayList<>();
    *//**
     * 单妆，包括妆容
     *//*
    private ArrayList<MakeupCustomModel> mMakeupCustomData = new ArrayList<>();

    private String mCurrentEffectId = null;

    *//**
     * as customCategory--> eyeshadow
     *//*

    private boolean isClearMakeup = false;

    public MakeUpView(Context context) {
        this(context, null);
    }

    public MakeUpView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public MakeUpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initListener();
        initData();
    }


    public void setMakeupCustomData(ArrayList<MakeupCustomModel> mMakeupCustomData) {
        this.mMakeupCustomData = mMakeupCustomData;
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_make_up, this);
        mMakeupRecyclerView = rootView.findViewById(R.id.beauty_makeup_item_list);
        mMakeupTopLayout = rootView.findViewById(R.id.makeup_top_layout);
        mMakeupChangeBtn = rootView.findViewById(R.id.change_btn);
        mMakeupChangeBtnText = rootView.findViewById(R.id.change_btn_text);
        mTvColor = rootView.findViewById(R.id.tv_color);
        mTvAlpha = rootView.findViewById(R.id.tv_alpha);
        mMakeupColorHinLayout = rootView.findViewById(R.id.makeup_color_hint_layout);
        mColorPickerView = rootView.findViewById(R.id.color_picker_view);
        mMakeupSeekBar = rootView.findViewById(R.id.seek_bar);
        mMakeUpTab = rootView.findViewById(R.id.makeup_tab);

        mMakeupAdapter = new MakeupAdapter(mContext, mMakeupComposeData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mMakeupRecyclerView.setLayoutManager(layoutManager);
        mMakeupRecyclerView.setAdapter(mMakeupAdapter);
        mMakeupAdapter.setEnable(true);

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
        mColorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {

            @Override
            public void onColorChanged(ColorData colorData) {
                int color = colorData.color;
                mTvColor.setVisibility(View.VISIBLE);
                mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(color).toUpperCase()));
                MakeupData makeupData = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
                if (makeupData == null) {
                    return;
                }
                makeupData.setColorData(colorData);
                MakeupManager.getInstacne().addMakeupEffect(mCurrentEffectId, makeupData);
                float alphaF = (Color.alpha(color) * 1.0f / 255f);
                float red = (Color.red(color) * 1.0f / 255f);
                float green = (Color.green(color) * 1.0f / 255f);
                float blue = (Color.blue(color) * 1.0f / 255f);
                NvsColor nvsColor = new NvsColor(red, green, blue, alphaF);
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
                MakeupData makeupData = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
                if (makeupData == null) {
                    return;
                }
                makeupData.setIntensity(progress / 100f);
                String alpha = String.format(getResources().getString(R.string.make_up_transparency), progress) + "%";
                mTvAlpha.setText(alpha);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupIntensityChanged(mCurrentEffectId, (progress / 100F));
                }
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {

            }
        });

        mMakeupChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMakeupMainMenu) {
                    if (mCategoryInfos.isEmpty()) {
                        return;
                    }
                    HashMap<String,String> fxMap = MakeupManager.getInstacne().getMapFxMap();
                    Set<String> strings = fxMap.keySet();
                    for (String fxName : strings) {
                        if (mMakeUpEventListener != null) {
                            mMakeUpEventListener.removeVideoFxByName(fxName);
                        }
                    }
                    fxMap.clear();
                    mCurrentEffectId = null;
                    // display custom makeup category
                    mMakeUpTab.setVisibility(View.VISIBLE);
                    if (mMakeUpTab.getTabCount() == 0) {
                        for (int i = 0; i < mCategoryInfos.size(); i++) {
                            MakeupCategory item = mCategoryInfos.get(i);
                            item.setDisplayName(Util.upperCaseName(item.getDisplayName()));
                            TextView textView = new TextView(mContext);
                            textView.setText(item.getDisplayNameZhCn());
                            textView.setTextSize(12);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextColor(getResources().getColor(R.color.gray_90));
                            mMakeUpTab.addTab(mMakeUpTab.newTab().setCustomView(textView));
                        }
                    }
                    mIsMakeupMainMenu = false;
                    TabLayout.Tab tab = mMakeUpTab.getTabAt(0);
                    if (tab == null) {
                        return;
                    }
                    if (!tab.isSelected()) {
                        tab.select();
                    } else {
                        tabSelect(tab);
                    }
                } else {
                    // display compose makeup category
                    mMakeUpTab.setVisibility(View.INVISIBLE);
                    changeToMakeupMainMenu();
                }
            }
        });
    }

    private void initData() {
        //获取整妆数据
        getMaterialListAll(TYPE_COMPOSE, isFaceModel106 ? AssetType.MAKEUP_TYPE_106 : AssetType.MAKEUP_TYPE_240
                , null, null);
        mMakeUpTab.removeAllTabs();
        //获取内置单妆分类
        mCategoryInfos = MakeupManager.getInstacne().getMakeupTab(mContext);
        getCustomTab();
    }

    private void tabSelect(TabLayout.Tab tab) {
        if (mMakeupCustomData.isEmpty()) {
            return;
        }
        mCurrentEffectId = mCategoryInfos.get(tab.getPosition()).getDisplayName();
        MakeupCustomModel customModel = mMakeupCustomData.get(tab.getPosition());
        if (customModel == null) {
            return;
        }
        if (customModel.isRequest()) {
            List<BeautyData> makeupData = customModel.getModelContent();
            parseSubCustom(makeupData);
        } else {
            if (tab.getPosition() == 0) {
                getMaterialListAll(TYPE_MAKEUP, isFaceModel106 ? AssetType.MAKEUP_TYPE_ZR_106 : AssetType.MAKEUP_TYPE_ZR_240
                        , null, customModel);
            } else {
                getMaterialListAll(TYPE_CUSTOM, AssetType.MAKEUP_TYPE_LIST_ALL
                        , String.valueOf(customModel.getId()), customModel);
            }
        }
    }

    *//**
     * 切换到主菜单
     *//*
    private void changeToMakeupMainMenu() {
        MakeupManager.getInstacne().clearMapFxData();
        mMakeupChangeBtnText.setText(mContext.getResources().getString(R.string.make_up_custom));
        mMakeupChangeBtn.setImageResource(R.mipmap.makeup_custom);
        mMakeupAdapter.setDataList(mMakeupComposeData, MakeupAdapter.MAKE_UP_RANDOM_BG_TYPE);
        mMakeupAdapter.notifyDataSetChanged();
        mMakeupAdapter.setEnable(true);
        final int index = MakeupManager.getInstacne().getComposeIndex();
        setColorPickerVisibility(View.INVISIBLE);
        mMakeupAdapter.setSelectPos(index);
        mMakeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != 0) {
                    //这里清除选择的组合美妆
                    MakeupManager.getInstacne().clearCustomData();
                    MakeupManager.getInstacne().setMakeupIndex(0);
                }
                MakeupManager.getInstacne().setComposeIndex(position);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupViewDataChanged(mMakeUpTab.getSelectedTabPosition(), position, isClearMakeup);
                }
            }
        });
        mMakeupRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mMakeupRecyclerView.scrollToPosition(Math.max(index, 0));
            }
        });
        mIsMakeupMainMenu = true;
    }

    *//**
     * 切换到子菜单
     *
     * @param data     数据
     * @param backText 返回显示文本
     *//*
    private void changeToMakeupSubMenu(final List<BeautyData> data, String backText) {
        mMakeupChangeBtnText.setText(backText);
        mMakeupChangeBtn.setImageResource(R.mipmap.beauty_facetype_back);
        mMakeupAdapter.setDataList(data, MakeupAdapter.MAKE_UP_ROUND_ICON_TYPE);
        mMakeupAdapter.notifyDataSetChanged();
        // custom makeup select position
        int index = 0;
        setColorPickerVisibility(View.INVISIBLE);
        if (mMakeUpTab.getSelectedTabPosition() != 0) {
            MakeupData makeupData = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
            if (makeupData != null) {
                String uuid = makeupData.getUuid();
                if (!TextUtils.isEmpty(uuid)) {
                    for (int i = 0; i < data.size(); i++) {
                        Makeup makeup = (Makeup) data.get(i);
                        if (makeup == null) {
                            continue;
                        }
                        MakeupEffectContent content = makeup.getEffectContent();
                        if (content == null) {
                            continue;
                        }
                        List<MakeupArgs> args = content.getMakeupArgs();
                        if (args.isEmpty()) {
                            continue;
                        }
                        if (TextUtils.equals(uuid, args.get(0).getUuid())) {
                            onSubViewSelect(i, data.get(i));
                            index = makeupData.getIndex();
                            break;
                        }
                    }
                }
            }
        } else {
            index = MakeupManager.getInstacne().getMakeupIndex();
        }
        mMakeupAdapter.setSelectPos(index);
        mMakeupAdapter.setEnable(true);
        mMakeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BeautyData selectItem = mMakeupAdapter.getSelectItem();
                if (selectItem instanceof Makeup) {
                    Makeup item = (Makeup) selectItem;
                    if (!item.isIsCompose()) {
                        if (mMakeUpTab.getSelectedTabPosition() == 0) {
                            MakeupManager.getInstacne().setMakeupIndex(position);
                        } else {
                            MakeupManager.getInstacne().removeMakeupEffect(mCurrentEffectId);
                            if (position == 0) {
                                setColorPickerVisibility(View.INVISIBLE);
                            } else {
                                onSubViewSelect(position, selectItem);
                            }
                        }
                    }
                }
                if (mMakeUpEventListener != null) {
                    MakeupManager.getInstacne().setComposeIndex(0);
                    mMakeUpEventListener.onMakeupViewDataChanged(mMakeUpTab.getSelectedTabPosition(), position, false);
                }

            }
        });
        mIsMakeupMainMenu = false;
        final int finalIndex = index;
        mMakeupRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mMakeupRecyclerView.scrollToPosition(finalIndex);
            }
        });
    }

    private void parseSubCustom(List<BeautyData> makeupData) {
        if (makeupData.isEmpty()) {
            return;
        }
        for (BeautyData beautyData : makeupData) {
            if (beautyData instanceof Makeup) {
                Makeup item = (Makeup) beautyData;
                MakeupEffectContent content = item.getEffectContent();
                if (content == null) {
                    continue;
                }
                List<MakeupArgs> makeupArgs = content.getMakeupArgs();
                if (makeupArgs.isEmpty()) {
                    continue;
                }
                for (MakeupArgs args : makeupArgs) {
                    if (args == null) {
                        continue;
                    }
                    if (!TextUtils.isEmpty(args.getMakeupUrl()) && beautyData.isBuildIn()) {
                        installMakeupPkg("assets:/" + item.getFolderPath() + File.separator + args.getMakeupUrl());
                    }
                }
            }
        }
        changeToMakeupSubMenu(makeupData, mContext.getString(R.string.makeup));
    }

    *//**
     * select one type of makeup effect from a custom makeup category
     *//*
    private void onSubViewSelect(int position, BeautyData selectItem) {
        if (!(selectItem instanceof Makeup)) {
            return;
        }
        if (TextUtils.isEmpty(mCurrentEffectId)) {
            return;
        }
        MakeupData makeupEffect = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
        if (makeupEffect == null) {
            makeupEffect = new MakeupData(position, DEFAULT_MAKEUP_INTENSITY, new ColorData());
            Makeup makeup = (Makeup) selectItem;
            MakeupEffectContent content = makeup.getEffectContent();
            if (content != null) {
                List<MakeupArgs> makeupArgs = content.getMakeupArgs();
                if (!makeupArgs.isEmpty()) {
                    makeupEffect.setUuid(makeupArgs.get(0).getUuid());
                }
            }
            MakeupManager.getInstacne().addMakeupEffect(mCurrentEffectId, makeupEffect);
        }
        makeupEffect.setIndex(position);
        //set Intensity
        float intensity = makeupEffect.getIntensity();
        String progress = String.format(getResources().getString(R.string.make_up_transparency), (int) (intensity * 100)) + "%";
        mTvAlpha.setText(progress);
        //set current use color
        ColorData colorData = makeupEffect.getColorData();
        if ((colorData != null)) {
            mTvColor.setVisibility((colorData.color == -1) ? INVISIBLE : VISIBLE);
            mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(colorData.color).toUpperCase()));
        }
        mMakeupSeekBar.setProgress((int) (intensity * 100));

        //set RecommendColor
        Makeup item = (Makeup) selectItem;
        MakeupEffectContent makeupEffectContent = item.getEffectContent();
        if (makeupEffectContent == null) {
            return;
        }
        MakeupArgs makeupArgs = makeupEffectContent.getMakeupArgs().get(0);
        if (makeupArgs == null) {
            return;
        }
        List<MakeupArgs.RecommendColor> recommendColors = makeupArgs.getMakeupRecommendColors();
        if (recommendColors != null && !recommendColors.isEmpty()) {
            int[] colors = new int[recommendColors.size()];
            for (int index = 0; index < recommendColors.size(); index++) {
                colors[index] = splitColor(recommendColors.get(index).getMakeupColor());
            }
            mColorPickerView.setDefaultColor(colors, colorData);
            setColorPickerVisibility(View.VISIBLE);
        }
    }

    public BeautyData getSelectItem() {
        if (mMakeupAdapter != null) {
            return mMakeupAdapter.getSelectItem();
        }
        return null;
    }

    *//**
     * 获取当前应用的单妆
     *
     * @return
     *//*
    public String getSelectMakeupId() {
        int position = mMakeUpTab.getSelectedTabPosition();
        if ((position == 0) || mCategoryInfos.isEmpty()) {
            return "";
        }
        return mCurrentEffectId;
    }

    public ArrayList<CategoryInfo> getAllMakeupId() {
        return mCategoryInfos;
    }

    public void setColorPickerVisibility(int visibility) {
        mColorPickerView.setVisibility(visibility);
        mMakeupColorHinLayout.setVisibility(visibility);
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
        *//**
         * 美妆应用
         *
         * @param tabPosition   tab位置
         * @param position      某一类中的position
         * @param isClearMakeup
         *//*
        void onMakeupViewDataChanged(int tabPosition, int position, boolean isClearMakeup);

        *//**
         * 美妆颜色修改
         *
         * @param makeupId 美妆id
         * @param color    颜色
         *//*
        void onMakeupColorChanged(String makeupId, NvsColor color);

        *//**
         * 美妆强度修改
         *
         * @param makeupId  美妆id
         * @param intensity 强度
         *//*
        void onMakeupIntensityChanged(String makeupId, float intensity);

        *//**
         * 移除美妆中自带的特效
         *
         * @param name 特效名称
         *//*
        void removeVideoFxByName(String name);

        *//**
         * 关闭美妆Dialog
         *//*
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

    *//**
     * 获取单妆分类 Tab
     *//*
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
                        List<CategoryInfo> mTabCategorys = data.get(0).getCategories().get(0).getKinds();
                        if (mTabCategorys.isEmpty()) {
                            return;
                        }
                        if (mCategoryInfos.isEmpty()) {
                            mCategoryInfos.addAll(mTabCategorys);
                        } else {
                            for (CategoryInfo categoryInfo : mTabCategorys) {
                                if (categoryInfo == null) {
                                    return;
                                }
                                boolean isExist = false;
                                for (CategoryInfo info : mCategoryInfos) {
                                    if (categoryInfo.getId() == info.getId()) {
                                        isExist = true;
                                        break;
                                    }
                                }
                                if (!isExist) {
                                    mCategoryInfos.add(categoryInfo);
                                    MakeupCustomModel baseModel = new MakeupCustomModel();
                                    baseModel.setId(categoryInfo.getId());
                                    baseModel.setMakeupId(categoryInfo.getDisplayName());
                                    ArrayList<BeautyData> none = new ArrayList<>();
                                    NoneItem noneItem = new NoneItem();
                                    noneItem.setIsCompose(false);
                                    none.add(noneItem);
                                    baseModel.setModelContent(none);
                                    mMakeupCustomData.add(baseModel);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(BaseResponse<List<MakeupCategoryInfo>> response) {
                        Log.e(TAG, "onError: get http makeup category data error!" + response.toString());
                        //ToastUtils.showShort("get makeup tab error");
                    }
                });
    }


    *//**
     * 获取妆容，单妆素材数据
     *//*
    public void getMaterialListAll(final int type, AssetType assetType, String kind, final MakeupCustomModel model) {
        HttpManager.getMaterialList(null
                , assetType
                , kind
                , 1, NvAsset.AspectRatio_All, ""
                , MSApplication.getSdkVersion()
                , 1, 100
                , new RequestCallback<BaseBean<BaseDataBean<Makeup>>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BaseDataBean<Makeup>>> response) {
                        if ((response.getCode() != 1) || (response.getData() == null)) {
                            //ToastUtils.showShort("get makeup data error");
                            if (model != null) {
                                model.setRequest(true);
                                parseSubCustom(model.getModelContent());
                            }
                            return;
                        }
                        BaseBean<BaseDataBean<Makeup>> dataBean = response.getData();
                        if (dataBean == null) {
                            if (model != null) {
                                model.setRequest(true);
                                parseSubCustom(model.getModelContent());
                            }
                            return;
                        }
                        List<BaseDataBean<Makeup>> dataElements = dataBean.getElements();
                        ArrayList<BeautyData> beautyData = new ArrayList<>();
                        if (!dataElements.isEmpty()) {
                            for (BaseDataBean data : dataElements) {
                                Makeup mMakeup = new Makeup();
                                mMakeup.setCover(data.getCoverUrl());
                                mMakeup.setIsBuildIn(false);
                                mMakeup.setName(data.getDisplayNamezhCN());
                                mMakeup.setIsCompose(type == TYPE_COMPOSE);
                                mMakeup.setFolderPath(PathUtils.getSDCardPathByType(NvAsset.ASSET_MAKEUP));
                                switch (type) {
                                    case TYPE_COMPOSE:
                                        mMakeup.setUuid(data.getId());
                                        String[] packageUrlStr = data.getPackageUrl().split("\\/");
                                        mMakeup.setUrl(packageUrlStr[packageUrlStr.length - 1]);
                                        //下载素材并安装
                                        downloadAndinstallMakeupPkg(mMakeup.getFolderPath(), mMakeup.getUrl(), data.getPackageUrl());
                                        break;
                                    case TYPE_CUSTOM:
                                        Makeup customMakeupInfo = (Makeup) data.getInfoJson();
                                        if (customMakeupInfo == null) {
                                            return;
                                        }
                                        MakeupEffectContent content = new MakeupEffectContent();
                                        List<MakeupArgs> makeupArgs = new ArrayList<>();
                                        MakeupArgs args = new MakeupArgs();
                                        args.setUuid(data.getId());
                                        args.setClassName(customMakeupInfo.getClassName());
                                        args.setType(customMakeupInfo.getMakeupId());
                                        String[] customStr = data.getPackageUrl().split("\\/");
                                        args.setMakeupUrl(mMakeup.getFolderPath() + File.separator + customStr[customStr.length - 1]);
                                        args.setMakeupRecommendColors(customMakeupInfo.getMakeupRecommendColors());
                                        makeupArgs.add(args);
                                        content.setMakeupArgs(makeupArgs);
                                        mMakeup.setEffectContent(content);
                                        //下载素材并安装
                                        downloadAndinstallMakeupPkg(mMakeup.getFolderPath(), args.getMakeupUrl(), data.getPackageUrl());
                                        break;
                                    case TYPE_MAKEUP:
                                        Makeup makeupInfo = (Makeup) data.getInfoJson();
                                        if (makeupInfo == null) {
                                            return;
                                        }
                                        MakeupEffectContent effectContent = makeupInfo.getEffectContent();
                                        if (effectContent == null) {
                                            return;
                                        }
                                        List<MakeupArgs> makeupArgsList = effectContent.getMakeupArgs();
                                        if ((makeupArgsList == null) || (makeupArgsList.isEmpty())) {
                                            return;
                                        }
                                        for (MakeupArgs args1 : makeupArgsList) {
                                            if (args1 == null) {
                                                continue;
                                            }
                                            //下载素材并安装
                                            String packageUrl = args1.getMakeupUrl();
                                            String[] makeupStr = packageUrl.split("\\/");
                                            args1.setMakeupUrl(mMakeup.getFolderPath() + File.separator + makeupStr[makeupStr.length - 1]);
                                            downloadAndinstallMakeupPkg(mMakeup.getFolderPath(), args1.getMakeupUrl(), packageUrl);
                                        }
                                        mMakeup.setEffectContent(makeupInfo.getEffectContent());
                                        break;
                                    default:
                                        break;
                                }
                                beautyData.add(mMakeup);
                            }
                        }

                        switch (type) {
                            case TYPE_COMPOSE:
                                if (!beautyData.isEmpty()) {
                                    //这里清除内置整妆，以服务器上美妆为准
                                    BeautyData none = mMakeupComposeData.get(0);
                                    mMakeupComposeData.clear();
                                    mMakeupComposeData.add(none);
                                    for (BeautyData bData : beautyData) {
                                        mMakeupComposeData.add(bData);
                                    }
                                    mMakeupAdapter.notifyDataSetChanged();
                                }
                                break;
                            case TYPE_CUSTOM:
                            case TYPE_MAKEUP:
                                if (model == null) {
                                    return;
                                }
                                model.setRequest(true);
                                if (!beautyData.isEmpty()) {
                                    //这里清除内置妆容或单妆，以服务器上美妆为准
                                    BeautyData none = model.getModelContent().get(0);
                                    model.getModelContent().clear();
                                    model.getModelContent().add(none);
                                    for (BeautyData bData : beautyData) {
                                        model.getModelContent().add(bData);
                                    }
                                }
                                parseSubCustom(model.getModelContent());
                                break;
                            default:
                                break;
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean<Makeup>>> response) {
                        //ToastUtils.showShort("get makeup data error");
                        if (type == TYPE_COMPOSE) {
                            return;
                        }
                        if (model != null) {
                            model.setRequest(true);
                            parseSubCustom(model.getModelContent());
                        }
                    }
                });
    }

    *//**
     * 下载
     *
     * @param packageUrl 文件路径
     *//*
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
                , MakeUpView.this, NvAsset.ASSET_MAKEUP, localPath);
    }

    *//**
     * 安装
     *
     * @param url 安装路径
     *//*
    private void installMakeupPkg(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        NvAssetManager.sharedInstance().installAssetPackage(
                url,
                NvAsset.ASSET_MAKEUP, true);
    }

*/
}
