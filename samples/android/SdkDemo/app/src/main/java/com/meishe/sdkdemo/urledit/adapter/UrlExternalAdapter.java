package com.meishe.sdkdemo.urledit.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.meishe.base.utils.ImageLoader;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.base.utils.RegexUtils;
import com.meishe.base.utils.ToastUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/6 15:27
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class UrlExternalAdapter extends BaseQuickAdapter<UrlMaterialInfo, BaseViewHolder> {
    private OnUrlTextChangedListener mOnUrlTextChangedListener;
    protected final ImageLoader.Options mRoundCornerOptions;
    private int mUrlType = Constants.URL_MATERIAL_VIDEO;

    public UrlExternalAdapter(Context context) {
        super(R.layout.item_url_external);
        mRoundCornerOptions = new ImageLoader.Options().placeholder(R.mipmap.icon_feed_back_pic).centerCrop().cacheAll(true).roundedCorners(15);
    }

    public void setOnUrlTextChangedListener(OnUrlTextChangedListener mOnUrlTextChangedListener) {
        this.mOnUrlTextChangedListener = mOnUrlTextChangedListener;
    }

    public void setUrlType(int mUrlType) {
        this.mUrlType = mUrlType;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, UrlMaterialInfo item) {
        ImageView urlImage = holder.getView(R.id.url_external_image);
        urlImage.setVisibility(!TextUtils.isEmpty(item.getUrl()) ? View.VISIBLE : View.GONE);
        showUrlImage(item.getUrl(), urlImage);
        EditText editText = holder.getView(R.id.url_link_input);
        editText.setText(item.getUrl());
        holder.addOnClickListener(R.id.url_input_add_or_del, R.id.url_input_clear);
        if (editText.getTag() instanceof TextWatcher) {
            editText.removeTextChangedListener((TextWatcher) editText.getTag());
        }
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                KeyboardUtils.showSoftInput(editText);
                if (null != mOnUrlTextChangedListener) {
                    mOnUrlTextChangedListener.onUrlTextChange();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null == editable) {
                    return;
                }
                String strResult = editable.toString();
                holder.setVisible(R.id.url_input_clear, !strResult.isEmpty());
                String content = editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                item.setUrl(content);
                if (null != mOnUrlTextChangedListener) {
                    mOnUrlTextChangedListener.onUrlTextChange();
                }
            }
        };
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String content = editText.getText().toString().trim();
                    if (!RegexUtils.isURL(content) && !TextUtils.isEmpty(content)) {
                        editText.setText("");
                        item.setUrl("");
                        ToastUtils.showShort(R.string.url_invalid);
                    }
                    String url = item.getUrl();
                    if (!TextUtils.isEmpty(url) && mUrlType == Constants.URL_MATERIAL_VIDEO) {
                        urlImage.setVisibility(!TextUtils.isEmpty(url) ? View.VISIBLE : View.GONE);
                        urlImage.setBackgroundResource(R.mipmap.icon_feed_back_pic);
                        showUrlImage(url, urlImage);
                    }
                    editText.clearFocus();
                    KeyboardUtils.hideSoftInput(editText);
                    return true;
                }
                return false;
            }
        });
        editText.addTextChangedListener(textWatcher);
        editText.setTag(textWatcher);
    }

    public interface OnUrlTextChangedListener {
        void onUrlTextChange();
    }

    private void showUrlImage(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(mContext)
                .asBitmap()
                .override(200, 200)
                .load(Uri.parse(url))
                .apply(new RequestOptions().frame(500000))
                .into(imageView);
    }

}
