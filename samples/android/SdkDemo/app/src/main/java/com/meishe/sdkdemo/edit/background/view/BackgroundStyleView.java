package com.meishe.sdkdemo.edit.background.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.ThreadUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.background.BackgroundStyleInfo;
import com.meishe.sdkdemo.edit.background.SpaceItemDecoration;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author liupanfeng
 * @desc 样式背景 view
 * @date 2020/10/21 10:45
 */
public class BackgroundStyleView extends LinearLayout {

    private final static String IMAGE_ASSETS_PATH = "background/image";

    private Context mContext;
    private RecyclerView mRecyclerView;
    private BackgroundStyleAdapter mBackgroundStyleAdapter;
    private List<BackgroundStyleInfo> mData;

    private int mOnSelectPosition;
    private OnBackgroundStyleItemClickListener mOnBackgroundStyleItemClickListener;


    public BackgroundStyleView(Context context) {
        super(context);
        init(context);
    }

    public BackgroundStyleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BackgroundStyleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_background_style_view, this);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        initData();
        initRecyclerView();
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBackgroundStyleAdapter = new BackgroundStyleAdapter();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mBackgroundStyleAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dp2px(mContext, 3), ScreenUtils.dp2px(getContext(), 12)));
    }

    private void initData() {
        mData = new ArrayList<>();
        mData = getBackgroundImageList();
    }

    public void setSelectPosition(int position) {
        if (position == mOnSelectPosition) {
            return;
        }
        int oldPosition = mOnSelectPosition;
        mOnSelectPosition = position;
        mBackgroundStyleAdapter.notifyItemChanged(oldPosition);
        mBackgroundStyleAdapter.notifyItemChanged(mOnSelectPosition);
    }


    private class BackgroundStyleAdapter extends RecyclerView.Adapter<StyleHolder> {

        @Override
        public StyleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = null;
            view = LayoutInflater.from(mContext).inflate(R.layout.item_background_style, null);
            return new StyleHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StyleHolder styleHolder, int i) {
            final int position = i;
            final BackgroundStyleInfo backgroundStyleInfo = mData.get(i);
            if (backgroundStyleInfo == null) {
                return;
            }
            String filePath = backgroundStyleInfo.getFilePath();
            Glide.with(mContext.getApplicationContext())
                    .asBitmap()
                    .load(TextUtils.isEmpty(filePath)
                            ? backgroundStyleInfo.getIconRcsId()
                            : (backgroundStyleInfo.isAssets() ? "file:///android_asset/background/image/" + filePath : filePath))
                    .apply(styleHolder.mOptions)
                    .into(styleHolder.mIcon);

            styleHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnBackgroundStyleItemClickListener != null) {
                        mOnBackgroundStyleItemClickListener.onStyleItemClick(view, position, backgroundStyleInfo);
                    }
                    if (mOnSelectPosition == position) {
                        return;
                    }
                    notifyItemChanged(mOnSelectPosition);
                    mOnSelectPosition = position;
                    notifyItemChanged(mOnSelectPosition);
                }
            });

            if (position == 0) {
                styleHolder.mMark.setVisibility(View.GONE);
            } else {
                styleHolder.mMark.setVisibility(mOnSelectPosition == position ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

    }


    private class StyleHolder extends RecyclerView.ViewHolder {

        private RequestOptions mOptions = new RequestOptions();
        private ImageView mIcon;
        private View mMark;

        public StyleHolder(@NonNull View itemView) {
            super(itemView);
            mOptions.skipMemoryCache(false);
            mIcon = itemView.findViewById(R.id.iv_pic);
            mMark = itemView.findViewById(R.id.iv_mark);
        }

    }


    public List<BackgroundStyleInfo> getBackgroundImageList() {
        try {
            List<BackgroundStyleInfo> result = new ArrayList<>();
            BackgroundStyleInfo more = new BackgroundStyleInfo();
            more.setIconRcsId(R.mipmap.icon_background_style_more);
            result.add(more);
            BackgroundStyleInfo nullInfo = new BackgroundStyleInfo();
            nullInfo.setIconRcsId(R.mipmap.icon_background_style_no);
            result.add(nullInfo);

            String rootDirPath = PathUtils.getFolderDirPath(PathUtils.BACKGROUND_IMAGE_STYLE);
            if (!TextUtils.isEmpty(rootDirPath)) {
                File backgroundFile = new File(rootDirPath);
                File[] files = backgroundFile.listFiles();
                if ((null != files) && (files.length > 0)) {
                    for (File file : files) {
                        BackgroundStyleInfo info = new BackgroundStyleInfo();
                        info.setAssets(false);
                        info.setFilePath(file.getAbsolutePath());
                        result.add(info);
                    }
                }
            }
            AssetManager assets = mContext.getAssets();
            String[] list = assets.list(IMAGE_ASSETS_PATH);
            if ((list == null) || (list.length <= 0)) {
                return null;
            }
            for (int index = 0; index < list.length; index++) {
                BackgroundStyleInfo canvasStyleInfo = new BackgroundStyleInfo();
                canvasStyleInfo.setFilePath(list[index]);
                result.add(canvasStyleInfo);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BackgroundStyleInfo> getData() {
        return mData;
    }

    public BackgroundStyleInfo getSelectData() {
        if (mOnSelectPosition <= 1) {
            return null;
        }
        return mData.get(mOnSelectPosition);

    }

    /**
     * 添加用户选择的背景素材
     *Add user-selected background material
     * @param filePath filepath
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addBackgroundImage(String filePath) {
        if (TextUtils.isEmpty(filePath) || (null == mData)) {
            return;
        }
        String fileName = FileUtils.getFileName(filePath);
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        UUID uuid = UUID.nameUUIDFromBytes(fileName.getBytes());
        String imageName = uuid.toString().toUpperCase();
        boolean isHas = false;
        for (int i = 0; i < mData.size(); i++) {
            BackgroundStyleInfo info = mData.get(i);
            if (null == info) {
                continue;
            }
            String path = info.getFilePath();
            if (TextUtils.isEmpty(path)) {
                continue;
            }
            if (path.contains(imageName)) {
                isHas = true;
                setSelectPosition(i);
                break;
            }
        }
        if (isHas) {
            return;
        }
        BackgroundStyleInfo backgroundInfo = new BackgroundStyleInfo();
        backgroundInfo.setAssets(false);
        backgroundInfo.setFilePath(filePath);
        mData.add(2, backgroundInfo);
        setSelectPosition(2);
        mBackgroundStyleAdapter.notifyDataSetChanged();

        String rootDirPath = PathUtils.getFolderDirPath(PathUtils.BACKGROUND_IMAGE_STYLE);
        String backgroundImagePath = rootDirPath + File.separator + imageName + "." + FileUtils.getFileExtension(filePath);
        ThreadUtils.getSinglePool().execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.copy(filePath, backgroundImagePath);
            }
        });
    }


    public void setOnBackgroundStyleItemClickListener(OnBackgroundStyleItemClickListener onBackgroundStyleItemClickListener) {
        this.mOnBackgroundStyleItemClickListener = onBackgroundStyleItemClickListener;
    }

    public interface OnBackgroundStyleItemClickListener {

        /**
         * 样式背景点击回调
         *Style background click callback
         * @param view
         * @param backgroundStyleInfo
         */
        void onStyleItemClick(View view, int position, BackgroundStyleInfo backgroundStyleInfo);

    }

}
