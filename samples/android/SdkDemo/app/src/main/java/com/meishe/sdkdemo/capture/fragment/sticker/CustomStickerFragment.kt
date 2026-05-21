package com.meishe.sdkdemo.capture.fragment.sticker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meishe.base.msbus.MSBus
import com.meishe.sdkdemo.R
import com.meishe.sdkdemo.capture.CaptureCustomStickerAdapter
import com.meishe.sdkdemo.capture.OnStickerItemClickListener
import com.meishe.sdkdemo.utils.Constants
import com.meishe.sdkdemo.utils.asset.NvAsset
import com.meishe.sdkdemo.utils.asset.NvAssetManager

class CustomStickerFragment : Fragment() {

    private lateinit var mCustomStickerAssetList: ArrayList<NvAssetManager.NvCustomStickerInfo>
    private var mStickerAdapter: CaptureCustomStickerAdapter?=null
    private lateinit var mRecyclerView: RecyclerView
    private var mAssetManager: NvAssetManager? = null
    private  var mItemClickListener: OnStickerItemClickListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_custom_sticker, container, false)
        initRecyclerView(view)
        initData();
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(): CustomStickerFragment {
            return CustomStickerFragment()
        }
    }


    private fun initData() {
        initAssets()
        initCustomAssetsDataList()
    }

    override fun onResume() {
        super.onResume()
        initCustomAssetsDataList()
    }


    private fun initRecyclerView(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        mRecyclerView.layoutManager =
            GridLayoutManager(context, 5);
        mStickerAdapter = context?.let { CaptureCustomStickerAdapter(it) }
        mRecyclerView.adapter = mStickerAdapter
        mStickerAdapter?.setItemClickListener(object : OnStickerItemClickListener {

            override fun onCustomItemClick(nvAsset: NvAssetManager.NvCustomStickerInfo?) {
//                mItemClickListener?.onCustomItemClick(nvAsset)
                MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_CUSTOM_STICKER_TYPE,nvAsset)
            }

            override fun onAddCustomSticker() {
//               mItemClickListener?.onAddCustomSticker()
                MSBus.getInstance().post(Constants.SubscribeType.SUB_ADD_CUSTOM_STICKER_TYPE)
            }

        })
    }


    private fun initAssets() {
        mCustomStickerAssetList = ArrayList<NvAssetManager.NvCustomStickerInfo>()
        mAssetManager = NvAssetManager.sharedInstance()
        mAssetManager?.searchLocalAssets(NvAsset.ASSET_CUSTOM_ANIMATED_STICKER)
        val bundlePath2 = "customsticker"
        mAssetManager?.searchReservedAssets(
            NvAsset.ASSET_CUSTOM_ANIMATED_STICKER,
            bundlePath2
        )

        mAssetManager?.initCustomStickerInfoFromSharedPreferences() //查询自定义的贴纸
    }


    /*
     * 获取自定义贴纸列表
     * Get custom sticker list
     * */
    private fun initCustomAssetsDataList() {
        mCustomStickerAssetList?.clear()

        mCustomStickerAssetList = mAssetManager?.usableCustomStickerAssets!!
        val info = NvAssetManager.NvCustomStickerInfo()
        mCustomStickerAssetList?.add(0,info)
        mStickerAdapter?.setAllData(mCustomStickerAssetList)
    }

    fun setItemClickListener(listener: OnStickerItemClickListener): CustomStickerFragment {
        mItemClickListener = listener
        return this
    }
}