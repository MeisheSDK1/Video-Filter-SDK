package com.meishe.sdkdemo.flipcaption;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.base.utils.LogUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BasePermissionActivity;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Util;

import java.util.ArrayList;
import java.util.List;

import static com.meishe.sdkdemo.utils.Constants.CaptionColors;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :翻转字幕 字幕编辑Activity
 * @Description :FlipCaption CaptionEdit Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FlipCaptionEditActivity extends BasePermissionActivity {
    private ImageView mCaptionEditBackBtn;
    private ImageView mColorRectButton;
    private RecyclerView mFlipCaptionContentList;
    private View mUpMoveSpace;
    private ImageView mCaptionEditFinish;
    private RelativeLayout mColorLayoutRect;
    private FlipCaptionContentAdapter mFlipCaptionContentAdapter;
    private FlipCaptionColorList mFlipCaptionColorList;
    private ArrayList<String> mCaptionColorList = new ArrayList<>();
    private ArrayList<FlipCaptionDataInfo> mCaptionDataInfoList = new ArrayList<>();
    private ImageView mCaptionColorFinish;
    private StringBuilder mFlipCaptionText = new StringBuilder();
    private int mSelectColorPos = -1;
    static final int REQUEST_CODE = 110;

    @Override
    protected int initRootView() {
        return R.layout.activity_flip_caption_edit;
    }

    @Override
    protected void initViews() {
        mCaptionEditBackBtn = (ImageView) findViewById(R.id.captionEditBackBtn);
        mColorRectButton = (ImageView) findViewById(R.id.colorRectButton);
        mFlipCaptionContentList = (RecyclerView) findViewById(R.id.flipCaptionContentList);
        mUpMoveSpace = findViewById(R.id.upMoveSpace);
        mCaptionEditFinish = (ImageView) findViewById(R.id.captionEditFinish);
        mColorLayoutRect = (RelativeLayout) findViewById(R.id.colorLayoutRect);
        mFlipCaptionColorList = (FlipCaptionColorList) findViewById(R.id.flipCaptionColorList);
        mCaptionColorFinish = (ImageView) findViewById(R.id.captionColorFinish);
        mFlipCaptionColorList = (FlipCaptionColorList) findViewById(R.id.flipCaptionColorList);
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {
        initCaptionColorList();
        initFlipCaptionContentList();
    }

    @Override
    protected void initListener() {
        mCaptionEditBackBtn.setOnClickListener(this);
        mColorRectButton.setOnClickListener(this);
        mCaptionEditFinish.setOnClickListener(this);
        mCaptionColorFinish.setOnClickListener(this);
        mColorLayoutRect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {

    }

    @Override
    protected void nonePermission() {

    }

    @Override
    protected void noPromptPermission() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.captionEditBackBtn) {
            AppManager.getInstance().finishActivity();
        } else if (id == R.id.colorRectButton) {
            int dataCount = mCaptionDataInfoList.size();
            //如果选中最后两项的其中一项，则需要向上平移mFlipCaptionContentList
            //If either of the last two items is selected, it needs to be shifted up
            if (mCaptionDataInfoList.get(dataCount - 2).isSelectItem()
                    || mCaptionDataInfoList.get(dataCount - 1).isSelectItem()) {
                mUpMoveSpace.setVisibility(View.VISIBLE);
                mFlipCaptionContentList.scrollToPosition(mFlipCaptionContentAdapter.getItemCount() - 1);
            }
            mColorLayoutRect.setVisibility(View.VISIBLE);
        } else if (id == R.id.captionColorFinish) {
            if (mSelectColorPos >= 0) {
                int captionCount = mCaptionDataInfoList.size();
                for (int index = 0; index < captionCount; ++index) {
                    if (mCaptionDataInfoList.get(index).isSelectItem())
                        mCaptionDataInfoList.get(index).setSelectItem(false);
                }
                mFlipCaptionContentAdapter.setCaptionDataInfoList(mCaptionDataInfoList);
                mFlipCaptionContentAdapter.notifyDataSetChanged();
                mSelectColorPos = -1;
                mFlipCaptionColorList.setSelectedPos(-1);
                mFlipCaptionColorList.notifyDataSetChanged();
            }

            mColorLayoutRect.setVisibility(View.GONE);
            mUpMoveSpace.setVisibility(View.GONE);
            setColorRectButtonVisible();
        } else if (id == R.id.captionEditFinish) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            BackupData.instance().setFlipDataInfoList(mCaptionDataInfoList);
            AppManager.getInstance().finishActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getInstance().finishActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasAllPermission()) {
            checkPermissions();
            //AppManager.getInstance().finishActivity();
        }
    }

    private void initCaptionColorList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionColorList.add(CaptionColors[index]);
        }
        mFlipCaptionColorList.setCaptionColorInfolist(mCaptionColorList);
        mFlipCaptionColorList.notifyDataSetChanged();
        mFlipCaptionColorList.setCaptionColorListener(new FlipCaptionColorList.OnFlipCaptionColorListener() {
            @Override
            public void onCaptionColor(int pos) {
                mSelectColorPos = pos;
                String selColorVal = mCaptionColorList.get(pos);
                int captionCount = mCaptionDataInfoList.size();
                for (int index = 0; index < captionCount; ++index) {
                    if (!mCaptionDataInfoList.get(index).isSelectItem())
                        continue;
                    mCaptionDataInfoList.get(index).setCaptionColor(selColorVal);
                }
                mFlipCaptionContentAdapter.setCaptionDataInfoList(mCaptionDataInfoList);
                mFlipCaptionContentAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 初始化字幕内容
     * init the caption context
     */
    private void initFlipCaptionContentList() {
        mCaptionDataInfoList = BackupData.instance().cloneFlipCaptionData();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mFlipCaptionContentList.setLayoutManager(layoutManager);
        mFlipCaptionContentAdapter = new FlipCaptionContentAdapter(this);
        mFlipCaptionContentAdapter.setCaptionDataInfoList(mCaptionDataInfoList);
        mFlipCaptionContentList.setAdapter(mFlipCaptionContentAdapter);
        mFlipCaptionContentAdapter.setFlipCaptionListener(new FlipCaptionContentAdapter.FlipCaptionContentListener() {
            @Override
            public void onCaptionModifyFinished(String newCaptionContent, int position) {
                String originCaptionText = mCaptionDataInfoList.get(position).getCaptionText();
                int index = originCaptionText.indexOf("]");
                String subTimeText = originCaptionText.substring(0, index + 1);
                mFlipCaptionText.setLength(0);
                mFlipCaptionText.append(subTimeText);
                mFlipCaptionText.append(newCaptionContent);
                mCaptionDataInfoList.get(position).setCaptionText(mFlipCaptionText.toString());
                mFlipCaptionContentAdapter.setCaptionDataInfoList(mCaptionDataInfoList);
                mFlipCaptionContentAdapter.notifyItemChanged(position);
                mColorRectButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClickEditCaption(int position) {
                mColorRectButton.setVisibility(View.GONE);
            }

            @Override
            public void onIsSelectedCaption(int position) {
                boolean isSelectItem = mCaptionDataInfoList.get(position).isSelectItem();
                mCaptionDataInfoList.get(position).setSelectItem(!isSelectItem);
                mFlipCaptionContentAdapter.setCaptionDataInfoList(mCaptionDataInfoList);
                mFlipCaptionContentAdapter.notifyItemChanged(position);
                setColorRectButtonVisible();
            }
        });
    }

    private void setColorRectButtonVisible() {
        int selectedCount = 0;
        for (int index = 0; index < mCaptionDataInfoList.size(); ++index) {
            if (mCaptionDataInfoList.get(index).isSelectItem()) {
                ++selectedCount;
            }
        }
        mColorRectButton.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(
                "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode + "===" + data);
        if (requestCode == REQUEST_CODE) {
            if (!hasAllPermission()) {
                AppManager.getInstance().finishActivity();
            }
        }
    }
}
