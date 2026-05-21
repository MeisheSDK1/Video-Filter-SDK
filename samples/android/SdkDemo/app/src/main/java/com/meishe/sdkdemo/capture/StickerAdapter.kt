package com.meishe.sdkdemo.capture

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.meishe.sdkdemo.R
import com.meishe.sdkdemo.utils.asset.NvAsset

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/1/10 16:30
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
class StickerAdapter(context: Context) :
    RecyclerView.Adapter<StickerAdapter.StickerViewHolder?>() {
    private lateinit var itemClickListener: OnStickerItemClickListener
    private val mData = mutableListOf<NvAsset>()
    private val mContext: Context = context
    private var selectId = -1

    private fun resetSelectPos() {
        selectId = -1
        notifyDataSetChanged()
    }

    fun setAllData(data:List<NvAsset>){
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, i: Int): StickerViewHolder {
        val itemView: View = LayoutInflater.from(mContext).inflate(R.layout.item_fx_sticker, null)
        return StickerViewHolder(itemView)
    }

    override fun onBindViewHolder(stickerViewHolder: StickerViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val nvAsset = mData[position]
        if (selectId == position) {
            stickerViewHolder.clickedBg.visibility = View.VISIBLE
        } else {
            stickerViewHolder.clickedBg.visibility = View.GONE
        }
        val assetCoverUrl = nvAsset.coverUrl
        val options = RequestOptions()
        options.centerCrop()
        options.placeholder(R.mipmap.default_sticker)
        val url: Any = if (position == 0) R.mipmap.dia_fx_more else assetCoverUrl
        Glide.with(mContext)
            .asBitmap()
            .load(url)
            .apply(options)
            .into(stickerViewHolder.stickerBg)
        stickerViewHolder.itemView.setOnClickListener {
            if (position == 0) {
                selectId = -1
                if (itemClickListener != null) {
                    itemClickListener.loadMore()
                }
            } else {
                selectId = position
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(mData[position])
                }
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }


    fun setItemClickListener(listener: OnStickerItemClickListener) {
        itemClickListener = listener
    }


    class StickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stickerBg: ImageView = itemView.findViewById(R.id.iv_bg)
        val clickedBg: ImageView = itemView.findViewById(R.id.iv_selected)
    }
}
