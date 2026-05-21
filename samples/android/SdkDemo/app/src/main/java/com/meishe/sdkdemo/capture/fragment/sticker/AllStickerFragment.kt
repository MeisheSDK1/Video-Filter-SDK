package com.meishe.sdkdemo.capture.fragment.sticker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meishe.sdkdemo.R
import com.meishe.sdkdemo.capture.OnStickerItemClickListener
import com.meishe.sdkdemo.capture.StickerAdapter
import com.meishe.sdkdemo.utils.asset.NvAsset
import com.meishe.sdkdemo.utils.asset.NvAssetManager

class AllStickerFragment : Fragment() {

    private var mAssetManager: NvAssetManager? = null
    private var mStickerAdapter: StickerAdapter? = null
    private var mStickers = mutableListOf<NvAsset>()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mItemClickListener: OnStickerItemClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_sticker, container, false)
        initRecyclerView(view)
        initData();
        return view
    }

    private fun initData() {
        initAssets();
    }

    private fun initAssets() {
        mAssetManager = NvAssetManager.sharedInstance()
        mAssetManager?.searchLocalAssets(NvAsset.ASSET_ANIMATED_STICKER)
        val bundlePath = "sticker"
        val nvAsset = NvAsset()
        mStickers.add(nvAsset)
        mAssetManager?.searchReservedAssets(NvAsset.ASSET_ANIMATED_STICKER, bundlePath)
        val usableAssets: ArrayList<NvAsset> = mAssetManager?.getUsableAssets(
            NvAsset.ASSET_ANIMATED_STICKER,
            NvAsset.AspectRatio_All,
            0
        ) as ArrayList<NvAsset>
        if (usableAssets != null && usableAssets.size > 0) {
            for (asset in usableAssets) {
                if (asset.isReserved()) {
                    var coverPath: String? = "file:///android_asset/sticker/"
                    coverPath += asset.uuid
                    coverPath += ".png"
                    /*
                     * 加载assets/sticker文件夹下的图片
                     * Load images in the assets / sticker folder
                     * */asset.coverUrl = coverPath
                }
            }
            mStickers.addAll(usableAssets)
        }
        mStickerAdapter?.setAllData(mStickers)
    }

    private fun initRecyclerView(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        mRecyclerView.layoutManager =
            GridLayoutManager(context, 4);
        mStickerAdapter = context?.let { StickerAdapter(it) }
        mRecyclerView.adapter = mStickerAdapter
        mStickerAdapter?.setItemClickListener(object : OnStickerItemClickListener {
            override fun onItemClick(nvAsset: NvAsset?) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(nvAsset)
                }
            }

            override fun loadMore() {
                if (mItemClickListener != null) {
                    mItemClickListener.loadMore()
                }
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(): AllStickerFragment {
            return AllStickerFragment()
        }
    }

    fun setItemClickListener(listener: OnStickerItemClickListener): AllStickerFragment {
        mItemClickListener = listener
        return this
    }

    fun initStickerData() {
        mStickers.clear()
        val nvAsset = NvAsset()
        nvAsset.coverUrl = "file:///android_asset/arface/084A6EC1-43AB-40EF-BBD5-D83F692B011B/084A6EC1-43AB-40EF-BBD5-D83F692B011B.png"
        mStickers.add(nvAsset)
        val usableAssets: List<NvAsset>? = mAssetManager!!.getUsableAssets(
            NvAsset.ASSET_ANIMATED_STICKER,
            NvAsset.AspectRatio_All,
            0
        )
        if (usableAssets != null && usableAssets.size > 0) {
            for (asset in usableAssets) {
                if (asset.isReserved()) {
                    var coverPath: String? = "file:///android_asset/sticker/"
                    coverPath += asset.uuid
                    coverPath += ".png"
                    /*
                     * 加载assets/sticker文件夹下的图片
                     * Load images in the assets / sticker folder
                     * */asset.coverUrl = coverPath
                }
            }
            mStickers.addAll(usableAssets)
        }
        mStickerAdapter?.notifyDataSetChanged()
    }

}