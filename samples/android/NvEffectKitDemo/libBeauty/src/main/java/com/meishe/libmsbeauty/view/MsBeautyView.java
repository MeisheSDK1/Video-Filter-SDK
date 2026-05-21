package com.meishe.libmsbeauty.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.libbase.util.CommonUtils;
import com.meishe.libbase.util.FileUtils;
import com.meishe.libbase.util.ScreenUtils;
import com.meishe.libbase.util.ToastUtil;
import com.meishe.libbase.util.Utils;
import com.meishe.libmsbeauty.BeautyDataManager;
import com.meishe.libmsbeauty.R;
import com.meishe.libmsbeauty.adapter.ShapeAdapter;
import com.meishe.libmsbeauty.adapter.SkinAdapter;
import com.meishe.libmsbeauty.adapter.SpaceItemDecoration;
import com.meishe.libmsbeauty.bean.IFxInfo;
import com.meishe.nveffectkit.NveEffectKit;
import com.meishe.nveffectkit.beauty.NveBeauty;
import com.meishe.nveffectkit.beauty.NveBeautyBlurTypeEnum;
import com.meishe.nveffectkit.beauty.NveBeautyParams;
import com.meishe.nveffectkit.beauty.NveBeautyWhiteningTypeEnum;
import com.meishe.nveffectkit.microShape.NveMicroShape;
import com.meishe.nveffectkit.microShape.NveMicroShapeData;
import com.meishe.nveffectkit.shape.NveShape;
import com.meishe.nveffectkit.shape.NveShapeData;
import com.meishe.nveffectkit.shape.NveShapeTypeEnum;
import com.meishe.nveffectkit.utils.NveAssetPackageManagerUtil;

import java.util.HashMap;
import java.util.List;

import static com.meishe.libmsbeauty.bean.BaseFxInfo.TYPE_BEAUTY_SHAPE;
import static com.meishe.libmsbeauty.bean.BaseFxInfo.TYPE_PLACE_HOLDER;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/8/22 16:15
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class MsBeautyView extends RelativeLayout {
    //Turn on all the switches and see the effect directly
    //打开所有开关,需要直接查看效果时
    private static final boolean OPEN_ALL_SWITCH = true;
    private static final String TAG = "MsBeautyView";
    private Context mContext;
    private TextView mBeautyTabButton;
    private View mVSkinBeautyLine, mVShapeBeautyLine, mVSmallBeautyLine;
    private TextView mShapeTabButton;
    private TextView mSmallTabButton;
    private RelativeLayout mBeautySelectRelativeLayout;
    private RelativeLayout mShapeSelectRelativeLayout;
    private RelativeLayout mSmallSelectRelativeLayout;
    private HashMap<String, Double> mBuffingSkinTempData = new HashMap<>();
    private HashMap<String, Float> mShapeTempData = new HashMap<>();

    private MagicProgress mShapeSeekBar;
    /**
     * 美颜
     * Beauty
     */
    private Switch mBeautySwitch;
    private TextView mBeauty_switch_text;
    private RecyclerView mBeautyRecyclerView;
    /**
     * beauty skin
     * 美肤
     */
    private SkinAdapter mBeautyAdapter;
    /**
     * microshaping
     * 微整形
     */
    private ShapeAdapter mSmallShapeAdapter;

    private LinearLayout mLLBeautySeek;
    private MagicProgress mBeautySeekBar;

    /**
     * Beauty reset function
     * 美颜的重置功能
     */
    private LinearLayout mBeautyResetLayout;

    /**
     * beauty shape
     * 美型
     */
    private Switch mBeautyShapeSwitch;
    private TextView mBeauty_shape_switch_text;
    /**
     * reset beauty shape
     * 美型重置
     */
    private LinearLayout mBeautyShapeResetLayout;
    private ImageView mBeautyShapeResetIcon;
    private TextView mBeautyShapeResetTxt;
    private RecyclerView mShapeRecyclerView;
    private ShapeAdapter mShapeAdapter;

    /**
     * microshaping
     * 微整形
     */
    private Switch mSmallShapeSwitch;
    private TextView mSmallShapeSwitchText;
    /**
     * reset microshaping
     * 微整形重置
     */
    private LinearLayout mSmallShapeResetLayout;
    private ImageView mSmallShapeResetIcon;
    private TextView mSmallShapeResetTxt;
    private RecyclerView mSmallShapeRecyclerView;

    private MagicProgress mSmallSeekBar;
    private LinearLayout mRlSmallSeekRootView;
    private TextView mTvBeautyA;
    private TextView mTvBeautyB;

    private boolean selectMarkUp;

    /**
     * beauty data
     * 美颜数据
     */
    private List<IFxInfo> mBeautyList;
    /**
     * strength data
     * 磨皮数据
     */
    private List<IFxInfo> mBuffingSkinList;
    /**
     * beauty shape data
     * 美型数据
     */
    private List<IFxInfo> mShapeDataList;
    /**
     * microshaping data
     * 微整型数据
     */
    private List<IFxInfo> mSmallShapeDataList;


    private MagicProgress mBeautySubSeekBar;
    private View mLlSubSeekContainer;
    private boolean supportQuyouguang = true;
    //美颜管理类
    private NveBeauty mBeautyController;
    //美型管理类
    private NveShape mShapeController;
    //微整形管理类
    private NveMicroShape mMicroShapeController;

    public MsBeautyView(Context context) {
        this(context, null);
    }

    public MsBeautyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsBeautyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initData(getContext());
        mBeautyController = new NveBeauty();
        mShapeController = new NveShape();
        mMicroShapeController = new NveMicroShape();
    }


    private void initData(Context context) {
        mBeautyList = BeautyDataManager.getBeautyList(mContext, supportQuyouguang);
        mBuffingSkinList = BeautyDataManager.getBuffingSkin(mContext);
        mShapeDataList = BeautyDataManager.getBeautyShapeList(mContext);
        mSmallShapeDataList = BeautyDataManager.getMicroPlasticList(mContext);
        initShapeRecyclerView(context);
        initBeautyRecyclerView(context);
        initSmallRecyclerView(context);
    }

    private void initView(Context context) {
        View mBeautyView = LayoutInflater.from(context).inflate(R.layout.beauty_view, this);
        mContext = context;

        mBeautyTabButton = mBeautyView.findViewById(R.id.beauty_tab_btn);
        mVSkinBeautyLine = mBeautyView.findViewById(R.id.v_skin_beauty_line);
        mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
        mBeautyResetLayout = mBeautyView.findViewById(R.id.beauty_reset_layout);

        mShapeTabButton = mBeautyView.findViewById(R.id.shape_tab_btn);
        mSmallTabButton = mBeautyView.findViewById(R.id.small_tab_btn);
        mVShapeBeautyLine = mBeautyView.findViewById(R.id.v_shape_beauty_line);
        mVSmallBeautyLine = mBeautyView.findViewById(R.id.v_small_change_line);

        mBeautySelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.beauty_select_rl);
        mShapeSelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.shape_select_rl);
        mSmallSelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.small_select_rl);
        mTvBeautyA = mBeautyView.findViewById(R.id.tv_beauty_a);
        mTvBeautyB = mBeautyView.findViewById(R.id.tv_beauty_b);
        mLLBeautySeek = mBeautyView.findViewById(R.id.ll_beauty_seek);
        mBeautySeekBar = mBeautyView.findViewById(R.id.beauty_sb);
        mBeautySubSeekBar = mBeautyView.findViewById(R.id.beauty_sub_sb);
        mLlSubSeekContainer = mBeautyView.findViewById(R.id.ll_sub_seek_container);
        mBeautySeekBar.setMax(100);
        mBeautySeekBar.setPointEnable(true);
        mBeautySeekBar.setBreakProgress(0);

        mBeautySubSeekBar.setMax(100);
        mBeautySubSeekBar.setMin(0);
        mBeautySubSeekBar.setPointEnable(true);
        mBeautySubSeekBar.setBreakProgress(0);
        mShapeSeekBar = (MagicProgress) mBeautyView.findViewById(R.id.shape_sb);

        mShapeSeekBar.setMax(200);
        mShapeSeekBar.setPointEnable(true);

        mBeautySwitch = (Switch) mBeautyView.findViewById(R.id.beauty_switch);
        mBeauty_switch_text = (TextView) mBeautyView.findViewById(R.id.beauty_switch_text);
        mBeautyRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.beauty_list_rv);

        mBeautyShapeSwitch = (Switch) mBeautyView.findViewById(R.id.beauty_shape_switch);
        mBeauty_shape_switch_text = (TextView) mBeautyView.findViewById(R.id.beauty_shape_switch_text);
        mBeautyShapeResetLayout = (LinearLayout) mBeautyView.findViewById(R.id.beauty_shape_reset_layout);
        mBeautyShapeResetIcon = (ImageView) mBeautyView.findViewById(R.id.beauty_shape_reset_icon);
        mBeautyShapeResetTxt = (TextView) mBeautyView.findViewById(R.id.beauty_shape_reset_txt);
        mShapeRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.beauty_shape_item_list);


        mSmallShapeSwitch = (Switch) mBeautyView.findViewById(R.id.small_shape_switch);
        mSmallShapeSwitchText = (TextView) mBeautyView.findViewById(R.id.small_shape_switch_text);
        mSmallShapeResetLayout = (LinearLayout) mBeautyView.findViewById(R.id.small_shape_reset_layout);
        mSmallShapeResetIcon = (ImageView) mBeautyView.findViewById(R.id.small_shape_reset_icon);
        mSmallShapeResetTxt = (TextView) mBeautyView.findViewById(R.id.small_shape_reset_txt);
        mSmallShapeRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.small_shape_item_list);

        mRlSmallSeekRootView = (LinearLayout) mBeautyView.findViewById(R.id.rl_small_seek_root_view);
        mSmallSeekBar = (MagicProgress) mBeautyView.findViewById(R.id.small_seek);

        initBeautyClickListener();
    }

    /**
     * 美颜dialog 动作监听
     * Beauty dialog action monitoring
     */
    private void initBeautyClickListener() {
        /*
         *美颜控制开关
         *Beauty control switch
         */
        mBeautyTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mBeautyTabButton.setSelected(true);
                mShapeTabButton.setSelected(false);
                mSmallTabButton.setSelected(false);
                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVSmallBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mBeautySelectRelativeLayout.setVisibility(View.VISIBLE);
                mShapeSelectRelativeLayout.setVisibility(View.GONE);
                mSmallSelectRelativeLayout.setVisibility(View.GONE);
                mRlSmallSeekRootView.setVisibility(View.GONE);
                mSmallSeekBar.setVisibility(View.GONE);
                mBeautyAdapter.setSelectPos(Integer.MAX_VALUE);
            }
        });

        //美颜模式A
        //Beauty mode A
        mTvBeautyA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBeautyAdapter == null) {
                    return;
                }
                changeWhiteningMode(true);
            }
        });
        //美颜模式B
        //Beauty mode B
        mTvBeautyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBeautyAdapter == null) {
                    return;
                }
                changeWhiteningMode(false);
            }
        });

        mShapeTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBeautySeekViewVisible(View.INVISIBLE);
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mBeautySeekBar.setVisibility(View.GONE);
                mLlSubSeekContainer.setVisibility(View.INVISIBLE);
                mTvBeautyA.setVisibility(View.GONE);
                mTvBeautyB.setVisibility(View.GONE);
                mBeautyTabButton.setSelected(false);
                mSmallTabButton.setSelected(false);
                mShapeTabButton.setSelected(true);
                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVSmallBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
                mBeautySelectRelativeLayout.setVisibility(View.GONE);
                mSmallSelectRelativeLayout.setVisibility(View.GONE);
                mRlSmallSeekRootView.setVisibility(View.GONE);
                mSmallSeekBar.setVisibility(View.GONE);
                mShapeSelectRelativeLayout.setVisibility(View.VISIBLE);

                if (selectMarkUp) {
                    selectMarkUp = false;
                }

                mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
            }
        });

        mSmallTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeBeautySeekViewVisible(View.INVISIBLE);
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mBeautySeekBar.setVisibility(View.GONE);
                mLlSubSeekContainer.setVisibility(View.INVISIBLE);
                mTvBeautyA.setVisibility(View.GONE);
                mTvBeautyB.setVisibility(View.GONE);
                mBeautyTabButton.setSelected(false);
                mShapeTabButton.setSelected(false);

                mSmallTabButton.setSelected(true);
                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVSmallBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));

                mBeautySelectRelativeLayout.setVisibility(View.GONE);
                mShapeSelectRelativeLayout.setVisibility(View.GONE);
                mSmallSelectRelativeLayout.setVisibility(View.VISIBLE);
                mRlSmallSeekRootView.setVisibility(View.VISIBLE);

                mSmallShapeAdapter.setSelectPos(Integer.MAX_VALUE);
            }
        });

        mBeautySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBeautySwitchChecked(isChecked);
            }
        });

        mBeautyShapeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setBeautyShapeSwitchChecked(isChecked);
            }
        });

        mSmallShapeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSmallBeautyShapeSwitchChecked(isChecked);
                if (!mBeautySwitch.isChecked()) {
                    NveEffectKit.getInstance().setBeauty(mBeautyController);
                    mBeautyController.setBlurIntensity(0);
                }
            }
        });

        mShapeSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (mShapeAdapter.getSelectPos() >= 0 && mShapeAdapter.getSelectPos() <= mShapeAdapter.getItemCount()) {
                    IFxInfo selectItem = mShapeAdapter.getSelectItem();
                    float strength = (float) (progress - 100) / 100;
                    selectItem.setStrength(strength);
                    mShapeController.setDegreeByType(NveShapeTypeEnum.valueOf(selectItem.getFxName()), strength);
                    mShapeTempData.put(selectItem.getFxName(), strength);
                }
            }
        });

        mSmallSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                IFxInfo selectItem = mSmallShapeAdapter.getSelectItem();
                if (TextUtils.isEmpty(selectItem.getFxName())) {
                    return;
                }
                boolean isShape = TYPE_BEAUTY_SHAPE.equals(selectItem.getType());
                float degree = isShape ? ((float) (progress - 100) / 100) : (float) (progress * 1.0 / 100);
                selectItem.setStrength(degree);
                selectItem.setDefaultStrength(degree);
                mMicroShapeController.setDegreeByType(BeautyDataManager.getNveMicroShapeTypeEnumByName(selectItem.getFxName()), degree);
            }
        });
        mBeautySeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                IFxInfo selectItem = mBeautyAdapter.getSelectItem();
                if (selectItem == null) {
                    Log.e(TAG, "onProgressChange selectItem is null");
                    return;
                }
                double strength = progress * 1.0 / 100;
                selectItem.setStrength(strength);
                selectItem.setDefaultStrength(strength);
                Log.e(TAG, "mBeautySeekBar onProgressChange: " + selectItem.getFxName() + "  " + strength);
                String name = selectItem.getName();
                if (mBuffingSkinTempData.containsKey(name)) {
                    mBuffingSkinTempData.put(name, strength);
                }
                setBeautyStrengthByName(selectItem.getFxName(), (float) strength);
            }
        });

        mBeautySubSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (fromUser) {
                    return;
                }
                IFxInfo selectItem = mBeautyAdapter.getSelectItem();
                if (selectItem == null) {
                    Log.e(TAG, "onProgressChange selectItem is null");
                    return;
                }
                if (TextUtils.equals(selectItem.getFxName(), NveBeautyParams.ADVANCED_BEAUTY_MATTE_INTENSITY)) {
                    double strength = progress * 0.27 + 3;
                    mBeautyController.setMatteFillRadius((float) strength);
                }
            }
        });
        //reset beauty skin
        //美肤重置
        mBeautyResetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBeautySwitch.isChecked()) {
                    return;
                }
                mBuffingSkinTempData.clear();
                mBeautySeekBar.setVisibility(View.INVISIBLE);
                mLlSubSeekContainer.setVisibility(View.INVISIBLE);
                mBeautyAdapter.setSelectPos(Integer.MAX_VALUE);
                for (IFxInfo beautyInfo : mBeautyList) {
                    beautyInfo.setStrength(0);
                    beautyInfo.setDefaultStrength(0);
                    setBeautyStrengthByName(beautyInfo.getFxName(), (float) beautyInfo.getDefaultStrength());
                }
                List<IFxInfo> iFxInfos = BeautyDataManager.getBeautyList(getContext(), true);
                List<IFxInfo> buffingSkinInfos = BeautyDataManager.getBuffingSkin(getContext());
                iFxInfos.addAll(buffingSkinInfos);
                if (CommonUtils.isEmpty(iFxInfos)) {
                    return;
                }
                for (IFxInfo beautyInfo : mBeautyList) {
                    for (IFxInfo fxInfo : iFxInfos) {
                        if (TextUtils.equals(fxInfo.getFxName(), beautyInfo.getFxName())
                                && fxInfo.getDefaultStrength() > 0) {
                            if (TextUtils.equals(NveBeautyParams.ADVANCED_BEAUTY_MATTE_INTENSITY, fxInfo.getFxName())) {
                                mBeautyController.setMatteFillRadius(15);
                            }
                            beautyInfo.setStrength(fxInfo.getDefaultStrength());
                            beautyInfo.setDefaultStrength(fxInfo.getDefaultStrength());
                            setBeautyStrengthByName(beautyInfo.getFxName(), (float) beautyInfo.getDefaultStrength());
                            break;
                        }
                    }
                }
            }
        });
        // beauty shape reset
        //美型重置
        mBeautyShapeResetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBeautyShapeSwitch.isChecked()) {
                    return;
                }
                mShapeTempData.clear();
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
                for (IFxInfo shapeInfo : mShapeDataList) {
                    shapeInfo.setStrength(shapeInfo.getDefaultStrength());
                    if (TYPE_PLACE_HOLDER.equals(shapeInfo.getType())) {
                        continue;
                    }
                    mShapeController.setDegreeByType(NveShapeTypeEnum.valueOf(shapeInfo.getFxName()),
                            (float) shapeInfo.getDefaultStrength());
                }
            }
        });
        //Microinteger reset
        //微整型重置
        mSmallShapeResetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSmallShapeSwitch.isChecked()) {
                    return;
                }
                mSmallSeekBar.setVisibility(View.INVISIBLE);
                mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
                for (IFxInfo shapeInfo : mSmallShapeDataList) {
                    shapeInfo.setStrength(0);
                    shapeInfo.setDefaultStrength(0);
                    if (shapeInfo.getFxName() == null || TextUtils.isEmpty(shapeInfo.getFxName())) {
                        continue;
                    }
                    mMicroShapeController.setDegreeByType(BeautyDataManager.getNveMicroShapeTypeEnumByName(shapeInfo.getFxName()),
                            0);
                }
            }
        });
    }

    private void initBeautyRecyclerView(Context context) {
        mBeautyAdapter = new SkinAdapter(context, mBuffingSkinList, mBeautyList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mBeautyRecyclerView.setLayoutManager(layoutManager);
        mBeautyRecyclerView.setAdapter(mBeautyAdapter);
        mBeautyAdapter.setEnable(OPEN_ALL_SWITCH);
        mBeautyAdapter.setSwitch(mBeautySwitch);
        mBeautyAdapter.setOnItemClickListener(new SkinAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name) {
                mLlSubSeekContainer.setVisibility(View.INVISIBLE);
                if (position == 0) {
                    mTvBeautyA.setVisibility(View.GONE);
                    mTvBeautyB.setVisibility(View.GONE);
                    changeBeautySeekViewVisible(View.INVISIBLE);
                    return;
                }
                IFxInfo selectItem = mBeautyAdapter.getSelectItem();
                if (selectItem == null) {
                    Log.e(TAG, "mBeautyAdapter selectItem is null");
                    return;
                }
                double defaultLevel = selectItem.getDefaultStrength();
                double level = defaultLevel;
                if (selectItem.getName().startsWith("磨皮")) {
                    String nameType = selectItem.getName();
                    if (!mBuffingSkinTempData.containsKey(nameType)) {
                        level = 0;
                        mBuffingSkinTempData.put(nameType, level);
                    } else {
                        level = mBuffingSkinTempData.get(nameType);
                    }
                }
                Log.e(TAG, "mBeautyAdapter onItemClick: " + selectItem.getFxName() + "  " + level);
                if (TextUtils.equals(selectItem.getFxName(), NveBeautyParams.ADVANCED_BEAUTY_MATTE_INTENSITY)) {
                    if (!NveEffectKit.getInstance().isSupportMatte()) {
                        ToastUtil.showToastCenterWithBg(mContext, getResources().getString(R.string.toast_matte_support_tip), "#CCFFFFFF", R.color.colorTranslucent);
                        return;
                    }
                    double subLevel = mBeautyController.getMatteFillRadius();
                    mBeautySubSeekBar.setPointProgress((int) (((subLevel - 3)) / 0.27));
                    mBeautySubSeekBar.setProgress((int) (((subLevel - 3)) / 0.27));
                    mBeautySeekBar.setPointProgress((int) (defaultLevel * 100));
                    mBeautySeekBar.setProgress((int) (defaultLevel * 100));
                } else {
                    mBeautySeekBar.setPointProgress((int) (defaultLevel * 100));
                    mBeautySeekBar.setProgress((int) (defaultLevel * 100));
                }
                if (getResources().getString(R.string.quyouguang).equals(name)) {
                    changeBeautySeekViewVisible(View.VISIBLE);
                    mLlSubSeekContainer.setVisibility(View.VISIBLE);
                } else {
                    changeBeautySeekViewVisible(View.VISIBLE);
                }
                mTvBeautyA.setVisibility(View.GONE);
                mTvBeautyB.setVisibility(View.GONE);
                if (name.startsWith(getResources().getString(R.string.whitening))) {
                    mTvBeautyA.setVisibility(View.VISIBLE);
                    mTvBeautyB.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initSmallRecyclerView(Context context) {
        mSmallShapeAdapter = new ShapeAdapter(context,
                mSmallShapeDataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mSmallShapeRecyclerView.setLayoutManager(layoutManager);
        mSmallShapeRecyclerView.setAdapter(mSmallShapeAdapter);
        mSmallShapeAdapter.setEnable(OPEN_ALL_SWITCH);
        mSmallShapeAdapter.setSwitch(mSmallShapeSwitch);
        mSmallShapeAdapter.setOnItemClickListener(new ShapeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name) {
                IFxInfo selectItem = mSmallShapeAdapter.getSelectItem();
                Log.e(TAG, "mSmallShapeAdapter onItemClick: " + selectItem.getFxName()
                        + "  " + selectItem.getType());
                double floatVal = mSmallShapeAdapter.getSelectItem().getStrength();

                if (TYPE_BEAUTY_SHAPE.equals(selectItem.getType())) {
                    //beauty shape
                    //美型
                    mSmallSeekBar.setMax(200);
                    mSmallSeekBar.setPointEnable(true);
                    mSmallSeekBar.setBreakProgress(100);
                    mSmallSeekBar.setVisibility(View.VISIBLE);
                    double defaultLevel = mSmallShapeAdapter.getSelectItem().getDefaultStrength();
                    mSmallSeekBar.setPointProgress((int) (defaultLevel * 100 + 100));
                    mSmallSeekBar.setProgress((int) (floatVal * 100 + 100));
                } else {
                    //beauty
                    //美颜
                    mSmallSeekBar.setMax(100);
                    mSmallSeekBar.setPointEnable(true);
                    mSmallSeekBar.setVisibility(View.VISIBLE);
                    mSmallSeekBar.setBreakProgress(0);

                    double defaultLevel = mSmallShapeAdapter.getSelectItem().getDefaultStrength();
                    mSmallSeekBar.setPointProgress((int) (defaultLevel * 100));
                    mSmallSeekBar.setProgress((int) (floatVal * 100));
                }
                checkIsPackageInstall(selectItem.getAssetPackagePath());
                Log.e(TAG, "onItemClick: " + selectItem.getFxName() + "  " + BeautyDataManager.getNveMicroShapeTypeEnumByName(selectItem.getFxName()));
                mMicroShapeController.setMicroShapeData(BeautyDataManager.getNveMicroShapeTypeEnumByName(selectItem.getFxName()), new NveMicroShapeData((float) floatVal, selectItem.getPackageId()));

            }
        });
    }

    /**
     * 初始化美型的各个特效的列表
     * Initializes the list of various effects for the beauty type
     */
    private void initShapeRecyclerView(Context context) {
        mShapeAdapter = new ShapeAdapter(context, mShapeDataList);
        mShapeAdapter.setIsBeautyShape(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mShapeRecyclerView.setLayoutManager(linearLayoutManager);
        mShapeRecyclerView.setAdapter(mShapeAdapter);
        mShapeAdapter.setSwitch(mBeautyShapeSwitch);
        int space = ScreenUtils.dip2px(context, 8);
        mShapeRecyclerView.addItemDecoration(new SpaceItemDecoration(space, 0));
        mShapeAdapter.setOnItemClickListener(new ShapeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name) {
                if (position < 0 || position >= mShapeAdapter.getItemCount()) {
                    return;
                }
                mShapeSeekBar.setVisibility(View.VISIBLE);
                /*
                 * 美型程度
                 * Beauty degree
                 */
                double level = 0.0;
                IFxInfo selectItem = mShapeAdapter.getSelectItem();
                if (selectItem == null) {
                    return;
                }
                Log.e(TAG, "mShapeAdapter onItemClick: " + name + "  " +
                        selectItem.getPackageId() + "  " + selectItem.getFxName());
                checkIsPackageInstall(selectItem.getAssetPackagePath());
                mShapeSeekBar.setMax(200);
                mShapeSeekBar.setPointEnable(true);
                mShapeSeekBar.setBreakProgress(100);


                double floatVal = selectItem.getStrength();
                if (floatVal >= 0) {
                    level = (Math.round(floatVal * 100)) * 0.01;
                } else {
                    level = -Math.round((Math.abs(floatVal) * 100)) * 0.01;
                }
                /*
                 * 美型特效值的范围[-1,1]
                 * Range of American special effects [-1,1]
                 */
                mShapeSeekBar.setProgress((int) (level * 100 + 100));

                mShapeController.setShapeData(NveShapeTypeEnum.valueOf(selectItem.getFxName()), new NveShapeData((float) floatVal, selectItem.getPackageId()));
                //设置默认值
                //set default value
                double defaultLevel = selectItem.getDefaultStrength();
                mShapeSeekBar.setPointProgress((int) (defaultLevel * 100 + 100));
            }
        });
    }

    public void onResume() {
        if (null != mBeautyController) {
            NveEffectKit.getInstance().setBeauty(mBeautyController);
        }
        if (null != mShapeController) {
            NveEffectKit.getInstance().setShape(mShapeController);
        }
        if (null != mMicroShapeController) {
            NveEffectKit.getInstance().setMicroShape(mMicroShapeController);
        }
    }

    private void changeBeautySeekViewVisible(int visible) {
        mLLBeautySeek.setVisibility(visible);
        mBeautySeekBar.setVisibility(visible);
    }

    private void changeWhiteningMode(boolean showWhiteningA) {
        IFxInfo selectItem = mBeautyAdapter.getSelectItem();
        if (selectItem == null) {
            Log.e(TAG, "selectItem is null");
            return;
        }
        if (showWhiteningA) {
            IFxInfo whiteningA = BeautyDataManager.getWhiteningA(mContext);
            whiteningA.setStrength(selectItem.getStrength());
            mBeautyAdapter.updateItem(mBeautyAdapter.getSelectPos(), BeautyDataManager.getWhiteningA(mContext));
            mBeautyController.setWhiteningType(NveBeautyWhiteningTypeEnum.WhiteningA);
            mBeautyController.setWhiteningIntensity((float) whiteningA.getStrength());
            mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_blue63);
            mTvBeautyA.setTextColor(getResources().getColor(R.color.white));
            mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_white);
            mTvBeautyB.setTextColor(getResources().getColor(R.color.blue_63));
            ToastUtil.showToastCenterWithBg(mContext, getResources().getString(R.string.whiteningA), "#CCFFFFFF", R.color.colorTranslucent);
        } else {
            IFxInfo whiteningB = BeautyDataManager.getWhiteningB(mContext);
            whiteningB.setStrength(selectItem.getStrength());
            mBeautyAdapter.updateItem(mBeautyAdapter.getSelectPos(), whiteningB);
            mBeautyController.setWhiteningType(NveBeautyWhiteningTypeEnum.WhiteningB);
            mBeautyController.setWhiteningIntensity((float) whiteningB.getStrength());
            mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_white);
            mTvBeautyA.setTextColor(getResources().getColor(R.color.blue_63));
            mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_blue63);
            mTvBeautyB.setTextColor(getResources().getColor(R.color.white));
            ToastUtil.showToastCenterWithBg(mContext, getResources().getString(R.string.whiteningB), "#CCFFFFFF", R.color.colorTranslucent);
        }
    }

    /**
     * 切换微整形开关
     * Toggle the micro shaping switch
     *
     * @param isChecked
     */
    private void setSmallBeautyShapeSwitchChecked(boolean isChecked) {
        mMicroShapeController.setEnable(isChecked);
        NveEffectKit.getInstance().setMicroShape(mMicroShapeController);
        List<IFxInfo> items = mSmallShapeAdapter.getItems();
        if (isChecked) {
            mSmallShapeSwitchText.setText(R.string.small_shape_close);
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    IFxInfo beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (TextUtils.isEmpty(beautyShapeDataItem.getName())) {
                        continue;
                    }
                    beautyShapeDataItem.setStrength(beautyShapeDataItem.getDefaultStrength());
                    if (TYPE_PLACE_HOLDER.equals(beautyShapeDataItem.getType())) {
                        continue;
                    }
                    Log.e(TAG, "setSmallBeautyShapeSwitchChecked 11: " + beautyShapeDataItem.getType()
                            + "  " + BeautyDataManager.getNveMicroShapeTypeEnumByName(beautyShapeDataItem.getFxName()) + "  " + beautyShapeDataItem.getFxName());

                    mMicroShapeController.setDegreeByType(BeautyDataManager.getNveMicroShapeTypeEnumByName(beautyShapeDataItem.getFxName()),
                            (float) beautyShapeDataItem.getDefaultStrength());
                }
            }
        } else {
            mSmallShapeSwitchText.setText(R.string.small_shape_open);
            mSmallShapeAdapter.setSelectPos(Integer.MAX_VALUE);
            mSmallSeekBar.setVisibility(View.INVISIBLE);
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    IFxInfo beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    beautyShapeDataItem.setStrength(0);
                    if (TYPE_PLACE_HOLDER.equals(beautyShapeDataItem.getType())) {
                        continue;
                    }
                    mMicroShapeController.setDegreeByType(BeautyDataManager.getNveMicroShapeTypeEnumByName(beautyShapeDataItem.getFxName()),
                            (float) beautyShapeDataItem.getStrength());
                }
            }
        }
        mSmallShapeAdapter.setDataList(items);
        mSmallShapeSwitch.setChecked(isChecked);
        mSmallShapeAdapter.setEnable(isChecked);
    }

    private void setBeautyShapeSwitchChecked(boolean isChecked) {
        if (mShapeController == null) {
            return;
        }
        mShapeController.setEnable(isChecked);
        NveEffectKit.getInstance().setShape(mShapeController);
        List<IFxInfo> items = mShapeAdapter.getItems();
        if (isChecked) {
            mBeauty_shape_switch_text.setText(R.string.beauty_shape_close);
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    IFxInfo beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (TextUtils.isEmpty(beautyShapeDataItem.getFxName())) {
                        continue;
                    }
                    double strength = beautyShapeDataItem.getDefaultStrength();
                    if (mShapeTempData.containsKey(beautyShapeDataItem.getFxName())) {
                        strength = mShapeTempData.get(beautyShapeDataItem.getFxName());
                    }

                    beautyShapeDataItem.setStrength(strength);
                    mShapeController.setDegreeByType(NveShapeTypeEnum.valueOf(beautyShapeDataItem.getFxName()),
                            (float) beautyShapeDataItem.getStrength());
                }
            }
        } else {
            mBeauty_shape_switch_text.setText(R.string.beauty_shape_open);
            mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
            mShapeSeekBar.setVisibility(View.INVISIBLE);
            //Effects are turned off by parameters
            //特效通过参数关闭
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    IFxInfo beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    beautyShapeDataItem.setStrength(0);
                    if (TYPE_PLACE_HOLDER.equals(beautyShapeDataItem.getType())) {
                        continue;
                    }
                    mShapeController.setDegreeByType(NveShapeTypeEnum.valueOf(beautyShapeDataItem.getFxName()),
                            (float) beautyShapeDataItem.getStrength());
                }
            }


        }
        mShapeAdapter.setDataList(items);
        mBeautyShapeSwitch.setChecked(isChecked);
        shapeLayoutEnabled(isChecked);
    }

    private void shapeLayoutEnabled(Boolean isEnabled) {
        mBeautyShapeResetLayout.setEnabled(isEnabled);
        mBeautyShapeResetLayout.setClickable(isEnabled);
        mShapeAdapter.setEnable(isEnabled);
        if (isEnabled) {
            mBeautyShapeResetIcon.setAlpha(1f);
            mBeautyShapeResetTxt.setTextColor(Color.BLACK);
        } else {
            mBeautyShapeResetIcon.setAlpha(0.5f);
            mBeautyShapeResetTxt.setTextColor(getResources().getColor(R.color.ms_disable_color));
        }
    }

    /**
     * Toggle beauty switch
     * 切换美颜开关
     *
     * @param isChecked
     */
    private void setBeautySwitchChecked(boolean isChecked) {
        mBeautyController.setEnable(isChecked);
        useBeautyDefault();
        if (isChecked) {
            List<IFxInfo> items = mBeautyAdapter.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (i == 0) {
                    //Only one peeling effect can be used at the same time, the selected peeling effect
                    //同时只能使用同一种磨皮效果，选中的磨皮效果
                    IFxInfo selectedBeautyTempData = mBeautyAdapter.getSelectedBeautyTempData();
                    if (selectedBeautyTempData == null) {
                        continue;
                    }
                    setBeautyStrengthByName(selectedBeautyTempData.getFxName(), (float) selectedBeautyTempData.getDefaultStrength());
                } else {
                    IFxInfo beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (TextUtils.equals(NveBeautyParams.ADVANCED_BEAUTY_MATTE_INTENSITY, beautyShapeDataItem.getFxName())) {
                        mBeautyController.setMatteFillRadius(15);
                    }
                    String beautyShapeId = beautyShapeDataItem.getFxName();
                    if (TextUtils.isEmpty(beautyShapeId)) {
                        continue;
                    }
                    String beautyName = beautyShapeDataItem.getName();
                    //除了磨皮，其他情况
                    if (beautyName.equals(mContext.getResources().getString(R.string.strength_1))
                            || beautyName.equals(mContext.getResources().getString(R.string.advanced_strength_1))
                            || beautyName.equals(mContext.getResources().getString(R.string.advanced_strength_2))) {
                        continue;//其他的磨皮直接循环下一个
                    } else if (mContext.getResources().getString(R.string.whitening_A).equals(beautyName)) {
                        changeBeautyWhiteMode(true, true);
                    } else if (mContext.getResources().getString(R.string.whitening_B).equals(beautyName)) {
                        changeBeautyWhiteMode(false, true);
                    }
                    setBeautyStrengthByName(beautyShapeDataItem.getFxName(), (float) beautyShapeDataItem.getDefaultStrength());
                }
            }
            mBeauty_switch_text.setText(R.string.beauty_close);
            mBeauty_switch_text.setTextColor(getResources().getColor(R.color.black));

        } else {
            List<IFxInfo> items = mBeautyAdapter.getItems();
            for (int i = 0; i < items.size(); i++) {
                IFxInfo beautyShapeDataItem = items.get(i);
                if (beautyShapeDataItem == null) {
                    continue;
                }
                setBeautyStrengthByName(beautyShapeDataItem.getFxName(), 0);
            }
            mBeauty_switch_text.setText(R.string.beauty_open);
            mBeauty_switch_text.setTextColor(getResources().getColor(R.color.ms_disable_color));
        }
        mBeautyAdapter.setEnable(isChecked);
        mBeautySwitch.setChecked(isChecked);
    }

    private void useBeautyDefault() {
        NveEffectKit.getInstance().setBeauty(mBeautyController);
        mBeautyController.setBlurType(NveBeautyBlurTypeEnum.BuffingSkin);
        mBeautyController.setWhiteningType(NveBeautyWhiteningTypeEnum.WhiteningA);
    }

    private void changeBeautyWhiteMode(boolean isOpen,
                                       boolean isExchange) {
        if (isExchange) {
            if (isOpen) {
                ToastUtil.showToastCenterWithBg(mContext, getResources().getString(R.string.beauty_open), "#CCFFFFFF", R.color.colorTranslucent);
//                mBeautyAdapter.setWittenName(getResources().getString(R.string.whitening_A));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_blue63);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.white));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_white);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.blue_63));
            } else {

                ToastUtil.showToastCenterWithBg(mContext, getResources().getString(R.string.whiteningB), "#CCFFFFFF", R.color.colorTranslucent);
//                mBeautyAdapter.setWittenName(getResources().getString(R.string.whitening_B));

                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_white);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.blue_63));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_blue63);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            if (isOpen) {
//                mBeautyAdapter.setWittenName(getResources().getString(R.string.whitening_B));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_white);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.blue_63));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_blue63);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.white));
            } else {
//                mBeautyAdapter.setWittenName(getResources().getString(R.string.whitening_A));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_blue63);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.white));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_white);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.blue_63));
            }
        }
    }


    private double getBeautyStrengthByName(String name) {
        String mopi = NveBeautyBlurTypeEnum.BuffingSkin.toString();
        String mopi1 = NveBeautyBlurTypeEnum.AdvancedBuffingSkin1.toString();
        String mopi2 = NveBeautyBlurTypeEnum.AdvancedBuffingSkin2.toString();
        String mopi3 = NveBeautyBlurTypeEnum.AdvancedBuffingSkin3.toString();
        String whiteningA = NveBeautyWhiteningTypeEnum.WhiteningA.toString();
        String whiteningB = NveBeautyWhiteningTypeEnum.WhiteningB.toString();
        if (name.equals(mopi) || (name.equals(mopi1)) || (name.equals(mopi2)) || (name.equals(mopi3))) {
            return mBeautyController.getBlurIntensity();
        } else if (name.equals(whiteningA) || (name.equals(whiteningB))) {
            return mBeautyController.getWhiteningIntensity();
        } else {
            switch (name) {
                case NveBeautyParams.BEAUTY_WHITENING:
                    return mBeautyController.getWhiteningIntensity();
                case NveBeautyParams.ADVANCED_BEAUTY_MATTE_INTENSITY:
                    return mBeautyController.getMatteIntensity();
                case NveBeautyParams.WHITENING_REDDENING:
                    return mBeautyController.getReddeningIntensity();
                case NveBeautyParams.FX_DEFINITION:
                    return mBeautyController.getDefinitionIntensity();
                case NveBeautyParams.SHARPEN:
                    return mBeautyController.getSharpnessIntensity();
            }
        }
        return 0;
    }

    private void setBeautyStrengthByName(String name, float strength) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        String mopi = NveBeautyBlurTypeEnum.BuffingSkin.toString();
        String mopi1 = NveBeautyBlurTypeEnum.AdvancedBuffingSkin1.toString();
        String mopi2 = NveBeautyBlurTypeEnum.AdvancedBuffingSkin2.toString();
        String mopi3 = NveBeautyBlurTypeEnum.AdvancedBuffingSkin3.toString();
        String mopi4 = NveBeautyBlurTypeEnum.AdvancedBuffingSkin4.toString();
        String whiteningA = NveBeautyWhiteningTypeEnum.WhiteningA.toString();
        String whiteningB = NveBeautyWhiteningTypeEnum.WhiteningB.toString();
        if (name.equals(mopi)) {
            Log.e(TAG, "setBeautyStrengthByName: 调整普通磨皮");
            mBeautyController.setBlurType(NveBeautyBlurTypeEnum.BuffingSkin);
            mBeautyController.setBlurIntensity(strength);
        } else if (name.equals(mopi1)) {
            Log.e(TAG, "setBeautyStrengthByName: 调整高级磨皮1");
            mBeautyController.setBlurType(NveBeautyBlurTypeEnum.AdvancedBuffingSkin1);
            mBeautyController.setBlurIntensity(strength);
        } else if (name.equals(mopi2)) {
            Log.e(TAG, "setBeautyStrengthByName: 调整高级磨皮2");
            mBeautyController.setBlurType(NveBeautyBlurTypeEnum.AdvancedBuffingSkin2);
            mBeautyController.setBlurIntensity(strength);
        } else if (name.equals(mopi3)) {
            Log.e(TAG, "setBeautyStrengthByName: 调整高级磨皮3");
            mBeautyController.setBlurType(NveBeautyBlurTypeEnum.AdvancedBuffingSkin3);
            mBeautyController.setBlurIntensity(strength);
        } else if (name.equals(mopi4)) {
            Log.e(TAG, "setBeautyStrengthByName: 调整高级磨皮4");
            mBeautyController.setBlurType(NveBeautyBlurTypeEnum.AdvancedBuffingSkin4);
            mBeautyController.setBlurIntensity(strength);
        } else if (name.equals(whiteningA)) {
            Log.e(TAG, "setBeautyStrengthByName: 调整美白a");
            mBeautyController.setWhiteningType(NveBeautyWhiteningTypeEnum.WhiteningA);
            mBeautyController.setWhiteningIntensity(strength);
        } else if (name.equals(whiteningB)) {
            Log.e(TAG, "setBeautyStrengthByName: 调整美白B");
            mBeautyController.setWhiteningType(NveBeautyWhiteningTypeEnum.WhiteningB);
            mBeautyController.setWhiteningIntensity(strength);
        } else {
            switch (name) {
                case NveBeautyParams.BEAUTY_WHITENING:
                    mBeautyController.setWhiteningIntensity(strength);
                    break;
                case NveBeautyParams.ADVANCED_BEAUTY_MATTE_INTENSITY:
                    mBeautyController.setMatteIntensity(strength);
                    break;
                case NveBeautyParams.WHITENING_REDDENING:
                    mBeautyController.setReddeningIntensity(strength);
                    break;
                case NveBeautyParams.FX_DEFINITION:
                    mBeautyController.setDefinitionIntensity(strength);
                    break;
                case NveBeautyParams.SHARPEN:
                    mBeautyController.setSharpnessIntensity(strength);
                    break;
            }
        }
    }

    private void checkIsPackageInstall(String path) {
        if (path == null || TextUtils.isEmpty(path)) {
            return;
        }
        int packageType = Utils.getPackageType(FileUtils.getFileExtension(path));
        NveAssetPackageManagerUtil.installFxPackage(path, "", packageType);
    }

    public void close() {
        mLlSubSeekContainer.setVisibility(GONE);
        mBeautySeekBar.setVisibility(GONE);
        mShapeSeekBar.setVisibility(GONE);
        mSmallSeekBar.setVisibility(GONE);
    }

    public void resetApply() {
        NveEffectKit.getInstance().setBeauty(mBeautyController);
    }

}
