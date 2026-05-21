package com.meishe.sdkdemo.quicksplicing

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.meicam.sdk.NvsStreamingContext
import com.meicam.sdk.NvsTimeline
import com.meicam.sdk.NvsVideoTrack
import com.meishe.base.utils.ToastUtils
import com.meishe.engine.util.PathUtils
import com.meishe.sdkdemo.MSApplication
import com.meishe.sdkdemo.R
import com.meishe.sdkdemo.base.BaseActivity
import com.meishe.sdkdemo.edit.VideoFragment
import com.meishe.sdkdemo.edit.data.BackupData
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener
import com.meishe.sdkdemo.selectmedia.SelectMediaActivity
import com.meishe.sdkdemo.utils.AppManager
import com.meishe.sdkdemo.utils.Constants
import com.meishe.sdkdemo.utils.FileUtils
import com.meishe.sdkdemo.utils.MediaConstant
import com.meishe.sdkdemo.utils.MediaScannerUtil
import com.meishe.sdkdemo.utils.TimelineUtil
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo
import com.meishe.sdkdemo.utils.dataInfo.TimelineData
import com.meishe.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_quick_splicing.btn_area_selection
import kotlinx.android.synthetic.main.activity_quick_splicing.llBottomDeleteView
import kotlinx.android.synthetic.main.activity_quick_splicing.mBottomLayout
import kotlinx.android.synthetic.main.activity_quick_splicing.mProgressBar
import kotlinx.android.synthetic.main.activity_quick_splicing.mRecycleView
import kotlinx.android.synthetic.main.activity_quick_splicing.mTitleBar
import kotlinx.android.synthetic.main.activity_quick_splicing.mView


/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/1/07.
 * @Description :快速拼接
 * @Description :quick splicing
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
class QuickSplicingActivity : BaseActivity() {

    companion object {
        private const val TRIM_REQUEST_CODE = 1024
    }

    private var mCurrentClipInfo: ClipInfo? = null
    private val TAG = "QuickSplicingActivity"
    private lateinit var mVideoFragment: VideoFragment
    private lateinit var mTimeline: NvsTimeline
    private lateinit var mQuickSplicingAdapter: QuickSplicingRecyclerViewAdapter
    private var mInsertPosition: Int = 0
    private var mCLickLeftActionBar: Boolean = false
    private lateinit var mClipInfos: ArrayList<ClipInfo>
    private lateinit var mCompileVideoPath: String

    override fun initRootView(): Int {
        return R.layout.activity_quick_splicing
    }

    override fun initViews() {
        mClipInfos = TimelineData.instance().clipInfoData;
        initRecycleView()
    }

    override fun initTitle() {
        mTitleBar.setTextCenter(R.string.quick_splicing)
        mTitleBar.setTextRight(R.string.compile)
        mTitleBar.setTextRightVisible(View.VISIBLE)
    }

    override fun initData() {
        if (createTimeline()) return
        initVideoFragment()
    }

    private fun createTimeline(): Boolean {
        mTimeline = TimelineUtil.newTimeline(TimelineData.instance().videoResolution)
        val videoTrack: NvsVideoTrack = mTimeline.appendVideoTrack()
        for (i in mClipInfos.indices) {
            val clipInfo = mClipInfos[i]
            val filePath = FileUtils.getAbsPathByContentUri(
                MSApplication.getContext(),
                MSApplication.CONTENT_FLAG, clipInfo.filePath
            )
            clipInfo.filePath = filePath
            val avInfoFromFile = NvsStreamingContext.getAVInfoFromFile(filePath, 0) ?: continue
            clipInfo.duration = avInfoFromFile.duration
            clipInfo.changeTrimIn(0)
            clipInfo.changeTrimOut(clipInfo.duration)
            addVideoClip(videoTrack, clipInfo)
        }
        val videoVolume = TimelineData.instance().originVideoVolume
        videoTrack.setVolumeGain(videoVolume, videoVolume)
        return false
    }

    private fun addVideoClip(videoTrack: NvsVideoTrack, clipInfo: ClipInfo) {
        val filePath = FileUtils.getAbsPathByContentUri(
            MSApplication.getContext(),
            MSApplication.CONTENT_FLAG, clipInfo.filePath
        )
        videoTrack.appendClip(filePath, clipInfo.trimIn, clipInfo.trimOut)
    }

    private fun initRecycleView() {
        val layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        mRecycleView.setLayoutManager(layoutManager)
        mQuickSplicingAdapter = QuickSplicingRecyclerViewAdapter(this, mRecycleView)
        mRecycleView.adapter = mQuickSplicingAdapter
        mQuickSplicingAdapter.setClipInfos(mClipInfos)
        val callBack = RecyclerViewTouchCallBack(mQuickSplicingAdapter)
        val touchHelper = ItemTouchHelper(callBack)
        touchHelper.attachToRecyclerView(mRecycleView)

        mQuickSplicingAdapter.mOnItemClickListener =
            object : QuickSplicingRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    mQuickSplicingAdapter.selectPosition = position
                    val streamingEngineState = mStreamingContext.streamingEngineState
                    if (streamingEngineState == NvsStreamingContext.STREAMING_ENGINE_STATE_STOPPED) {
                        mVideoFragment.playVideoButtonClick(
                            mStreamingContext.getTimelineCurrentPosition(
                                mTimeline
                            ), mTimeline.duration
                        )
                    }
                }

                override fun onItemActionBarClick(view: View?, position: Int) {
                    when (view?.id) {
                        R.id.iv_add_clip_left -> {
                            mCLickLeftActionBar = true
                            mInsertPosition = position
                            startToSelectMedia()
                        }

                        R.id.iv_add_clip_right -> {
                            mInsertPosition = position + 1;
                            mCLickLeftActionBar = false
                            startToSelectMedia()
                        }
                    }
                }


                override fun onItemMove(from: Int, to: Int) {
                    mClipInfos = mQuickSplicingAdapter.getData()!!
                    doWidthTrack()
                }

                override fun onItemDelete(position: Int) {
                    mClipInfos = mQuickSplicingAdapter.getData()!!
                    doWidthTrack()
                }

                override fun onShowBottomDeleteView(boolean: Boolean) {
                    if (boolean) {
                        if (llBottomDeleteView.visibility != View.VISIBLE) {
                            llBottomDeleteView.visibility = View.VISIBLE
                        }
                    } else {
                        if (llBottomDeleteView.visibility != View.GONE) {
                            llBottomDeleteView.visibility = View.GONE
                        }
                    }
                }
            }

        mRecycleView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, p1: MotionEvent?): Boolean {
                val action = p1?.action
                var currentY: Float? = 0f
                if (action == MotionEvent.ACTION_DOWN) {
                    currentY = view?.y
                } else if (action == MotionEvent.ACTION_MOVE) {
                    val fl = view?.y?.minus(currentY!!)?.let { Math.abs(it) }
                    if (fl != null) {
                        if (fl > 20) {
                            if (mClipInfos.size <= 1) {
                                ToastUtils.showShort(resources.getString(R.string.reserve_one_videoclip))
                                return true
                            }
                        }
                    }
                }
                return false
            }

        })
    }

    private fun doWidthTrack() {
        resetVideoTrack()
        mVideoFragment.updateTotalDurationText()
    }

    private fun resetVideoTrack() {
        val videoTrack = mTimeline.getVideoTrackByIndex(0)
        videoTrack.removeAllClips()
        for (i in mClipInfos.indices) {
            val clipInfo = mClipInfos[i]
            addVideoClip(videoTrack, clipInfo)
        }
        val videoVolume = TimelineData.instance().originVideoVolume
        videoTrack.setVolumeGain(videoVolume, videoVolume)

        mVideoFragment.setTimeline(mTimeline)
        mVideoFragment.playVideo(0, mTimeline.duration)
    }


    private fun startToSelectMedia() {
        val bundle = Bundle()
        bundle.putInt("visitMethod", Constants.FROM_QUICK_SPLICING_ONLY_ONE_ACTIVITY)
        bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO)
        AppManager.getInstance().jumpActivityForResult(
            this,
            SelectMediaActivity::class.java, bundle, Constants.FROM_QUICK_SPLICING_ONLY_ONE_ACTIVITY
        )
    }

    private fun initVideoFragment() {
        mVideoFragment = VideoFragment()
        mVideoFragment.setFragmentLoadFinisedListener(VideoFragment.OnFragmentLoadFinisedListener {
            mVideoFragment.seekTimeline(
                mStreamingContext.getTimelineCurrentPosition(mTimeline),
                0
            )
        })
        mVideoFragment.setTimeline(mTimeline)
        mVideoFragment.setAutoPlay(true)
        val bundle = Bundle()
        bundle.putInt("titleHeight", mTitleBar.layoutParams.height)
        bundle.putInt("bottomHeight", mBottomLayout.layoutParams.height)
        bundle.putInt("ratio", TimelineData.instance().makeRatio)
        bundle.putBoolean("playBarVisible", true)
        bundle.putBoolean("voiceButtonVisible", true)
        mVideoFragment.arguments = bundle
        fragmentManager.beginTransaction().add(R.id.video_layout, mVideoFragment).commit()
        fragmentManager.beginTransaction().show(mVideoFragment)
        mVideoFragment.setPlayFlag(NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME)
        mVideoFragment.setVideoFragmentCallBack(object : VideoFragment.VideoFragmentListener {
            override fun playBackEOF(timeline: NvsTimeline) {
                if (timeline.duration - mStreamingContext.getTimelineCurrentPosition(mTimeline) <= 40000) {
                    mVideoFragment.updateCurPlayTime(0)
                    mVideoFragment.seekTimeline(0, 0)
                }
            }

            override fun playStopped(timeline: NvsTimeline) {}
            override fun playbackTimelinePosition(timeline: NvsTimeline, stamp: Long) {}
            override fun streamingEngineStateChanged(state: Int) {}
        })
    }

    override fun initListener() {
        mTitleBar.onTitleBarClickListener = object : OnTitleBarClickListener {
            override fun OnBackImageClick() {
                splicingFinish()
            }

            override fun OnCenterTextClick() {

            }

            override fun OnRightTextClick() {
                val fileName = "quick_splicing_" + System.currentTimeMillis() + ".mp4"
                mCompileVideoPath = PathUtils.getQuickSplicingVideoSavePath(fileName)
                mStreamingContext.stop()
                val compilePassthroughTimeline = mStreamingContext.compilePassthroughTimeline(
                    mTimeline, mCompileVideoPath, null, 0
                )
                if (compilePassthroughTimeline) {
                    changeProgressBarVisible(View.VISIBLE)
                }
            }
        }

        mStreamingContext.setCompileCallback(object : NvsStreamingContext.CompileCallback {
            override fun onCompileProgress(p0: NvsTimeline?, p1: Int) {
            }

            override fun onCompileFinished(p0: NvsTimeline?) {
                runOnUiThread {
                    changeProgressBarVisible(View.GONE)
                    ToastUtil.showToast(
                        this@QuickSplicingActivity,
                        resources.getString(R.string.compile_video_success, mCompileVideoPath)
                    );
                    mStreamingContext.compileConfigurations = null
                    MediaScannerUtil.scanFile(mCompileVideoPath, "video/mp4")
                }
            }

            override fun onCompileFailed(p0: NvsTimeline?) {
                changeProgressBarVisible(View.GONE)
            }
        })

        mView.setOnClickListener {
            mQuickSplicingAdapter.selectPosition = -1
        }

        btn_area_selection.setOnClickListener {
            val selectPosition = mQuickSplicingAdapter.selectPosition
            val size = mClipInfos.size
            if (selectPosition in 0 until size) {
                mCurrentClipInfo = mClipInfos[selectPosition]
                BackupData.instance().clipInfo = mCurrentClipInfo
                AppManager.getInstance().jumpActivityForResult(
                    this,
                    FileQuickSplicingActivity::class.java, null, TRIM_REQUEST_CODE
                );
            } else {
                mCurrentClipInfo = null
                ToastUtils.showShort(resources.getString(R.string.select_one_clip))
            }
        }

    }

    private fun changeProgressBarVisible(visible: Int) {
        mProgressBar.visibility = visible
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.FROM_QUICK_SPLICING_ONLY_ONE_ACTIVITY) {
            val quickSplicingVideoArray = BackupData.instance().quickSplicingVideoArray
            val clipInfoList = ArrayList<ClipInfo>()
            if (quickSplicingVideoArray != null) {
                for (str: String in quickSplicingVideoArray) {
                    val clipInfo = ClipInfo()

                    val filePath = FileUtils.getAbsPathByContentUri(
                        MSApplication.getContext(),
                        MSApplication.CONTENT_FLAG, str
                    )
                    clipInfo.filePath = filePath
                    val avInfoFromFile =
                        NvsStreamingContext.getAVInfoFromFile(filePath, 0) ?: continue;
                    clipInfo.duration = avInfoFromFile.duration
                    clipInfo.changeTrimIn(0);
                    clipInfo.changeTrimOut(avInfoFromFile.duration)
                    clipInfoList.add(clipInfo)
                }
            }
            if (quickSplicingVideoArray != null) {
                mClipInfos.addAll(mInsertPosition, clipInfoList)
                mQuickSplicingAdapter.notifyDataSetChanged()
                if (mCLickLeftActionBar) {
                    val selectPosition = mQuickSplicingAdapter.selectPosition
                    mQuickSplicingAdapter.selectPosition = selectPosition + clipInfoList.size
                }
                resetVideoTrack()
                mVideoFragment.updateTotalDurationText()
            }
            BackupData.instance().quickSplicingVideoArray = null
        } else if (requestCode == TRIM_REQUEST_CODE) {
            val clipInfo = BackupData.instance().clipInfo
            mClipInfos.set(mClipInfos.indexOf(mCurrentClipInfo), clipInfo)
            resetVideoTrack()
            mVideoFragment.updateTotalDurationText()
            mQuickSplicingAdapter.notifyItemChanged(mClipInfos.indexOf(mCurrentClipInfo))
        }
    }


    override fun onClick(v: View?) {

    }

    override fun onBackPressed() {
        splicingFinish()
        super.onBackPressed()
    }

    private fun splicingFinish() {
        mVideoFragment.stopEngine()
        removeTimeline()
        finish()
    }

    private fun removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline)
    }

}