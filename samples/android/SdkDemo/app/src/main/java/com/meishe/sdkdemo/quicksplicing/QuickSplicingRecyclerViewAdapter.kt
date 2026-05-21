package com.meishe.sdkdemo.quicksplicing

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.meishe.sdkdemo.R
import com.meishe.base.view.RoundImageView
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo
import java.util.*

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/1/7 15:21
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
class QuickSplicingRecyclerViewAdapter(
    private val mContext: Context,
    val mRecyclerView: RecyclerView
) :
    RecyclerView.Adapter<QuickSplicingRecyclerViewAdapter.ViewHolder>(),
    IItemTouchHelperAdapterCallback {


    private var mClipInfos: ArrayList<ClipInfo>? = null
    private var mSelectPosition = -1
    var mOnItemClickListener: OnItemClickListener? = null
    var isDeleteFinish = false
    fun setClipInfos(clipInfos: ArrayList<ClipInfo>) {
        mClipInfos = clipInfos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val v =
            LayoutInflater.from(mContext).inflate(R.layout.item_quick_splicing, viewGroup, false)
        return ViewHolder(v)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") i: Int) {
        if (mSelectPosition == i) {
            viewHolder.mImageView.visibility = View.VISIBLE
            viewHolder.mAddClipLeft.visibility = View.VISIBLE
            viewHolder.mAddClipRight.visibility = View.VISIBLE
        } else {
            viewHolder.mImageView.visibility = View.GONE
            viewHolder.mAddClipLeft.visibility = View.GONE
            viewHolder.mAddClipRight.visibility = View.GONE
        }
        val clipInfo = mClipInfos?.get(i)
        val requestBuilder: RequestBuilder<*> = Glide.with(mContext.applicationContext).load(
            clipInfo?.filePath
        )
        requestBuilder.into(viewHolder.mRivClip)
        viewHolder.mClipDuration.setText(formatTimeStrWithUs(clipInfo!!.trimOut - clipInfo!!.trimIn))
        viewHolder.mClipIndex.setText("${i + 1}");

        viewHolder.mRivClip.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    if (mOnItemClickListener != null) {
                        isDeleteFinish = false
                        mOnItemClickListener!!.onItemClick(v, i)
                    }
                }
                return false
            }
        })


        viewHolder.mAddClipLeft.setOnClickListener { v ->
            mOnItemClickListener?.onItemActionBarClick(v, i)
        }
        viewHolder.mAddClipRight.setOnClickListener { v ->
            mOnItemClickListener?.onItemActionBarClick(v, i)
        }
    }

    private fun formatTimeStrWithUs(us: Long): String? {
        val second = (us / 1000000.0).toInt()
        val hh = second / 3600
        val mm = second % 3600 / 60
        val ss = second % 60
        return if (hh > 0) String.format(
            "%02d:%02d:%02d",
            hh,
            mm,
            ss
        ) else String.format("%02d:%02d", mm, ss)
    }

    var selectPosition: Int
        get() = mSelectPosition
        set(selectPosition) {
            mSelectPosition = selectPosition
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return mClipInfos?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mAddClipLeft: View
        val mAddClipRight: View
        var mRivClip: RoundImageView
        var mImageView: ImageView
        val mClipDuration: TextView
        val mClipIndex: TextView

        init {
            mRivClip = itemView.findViewById(R.id.riv_clip)
            mImageView = itemView.findViewById(R.id.iv_clip_mask)
            mAddClipLeft = itemView.findViewById(R.id.iv_add_clip_left)
            mAddClipRight = itemView.findViewById(R.id.iv_add_clip_right)
            mClipDuration = itemView.findViewById(R.id.tv_time_duration)
            mClipIndex = itemView.findViewById(R.id.tv_video_index)
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onItemActionBarClick(view: View?, position: Int)
        fun onItemMove(from: Int, to: Int);
        fun onItemDelete(position: Int)
        fun onShowBottomDeleteView(boolean: Boolean)
    }

    @SuppressLint("MissingPermission")
    override fun onItemMove(source: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?) {
        val fromPosition = source!!.adapterPosition
        val toPosition = target!!.adapterPosition

        Collections.swap(mClipInfos, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition);
        mOnItemClickListener?.onItemMove(fromPosition, toPosition)
        mSelectPosition = toPosition
    }

    override fun onSwiped(postion: Int) {
        mClipInfos?.removeAt(postion)
        postion.let {
            mOnItemClickListener?.onItemDelete(it)
            notifyItemRemoved(it)
        }

        mRecyclerView.postDelayed({
            showBottomDeleteView(false)
        }, 20)
    }

    override fun onItemSelect(source: RecyclerView.ViewHolder?) {
    }


    fun getItemInfo(position: Int?): ClipInfo? {
        return position?.let { mClipInfos?.get(it) }
    }

    fun getData(): ArrayList<ClipInfo>? {
        return mClipInfos
    }

    override fun onItemClear(viewHolder: RecyclerView.ViewHolder?) {
        notifyDataSetChanged()
        showBottomDeleteView(false)
    }

    fun showBottomDeleteView(boolean: Boolean) {
        mOnItemClickListener?.onShowBottomDeleteView(boolean)
    }

    fun onDragDelete(viewHolder: RecyclerView.ViewHolder?): Boolean {
        val position: Int? = viewHolder?.adapterPosition
        if (position!! < 0) {
            return false
        }
        position.let {
            mClipInfos?.removeAt(it)
            mOnItemClickListener?.onItemDelete(it)
            notifyItemRemoved(it)
        }
        showBottomDeleteView(false)
        isDeleteFinish = true
        return true
    }

    fun hideBottomDeleteView(){
        showBottomDeleteView(false)
    }


}