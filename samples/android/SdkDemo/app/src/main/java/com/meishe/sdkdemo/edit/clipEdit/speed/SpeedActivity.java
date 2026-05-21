package com.meishe.sdkdemo.edit.clipEdit.speed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.adapter.CurveSpeedViewAdapter;
import com.meishe.sdkdemo.edit.clipEdit.SingleClipFragment;
import com.meishe.sdkdemo.edit.clipEdit.util.CurveSpeedUtil;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.ChangeSpeedCurveInfo;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.EditChangeSpeedCurveView;
import com.meishe.sdkdemo.edit.view.EditChangeSpeedView;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.List;
/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑-编辑-变速-Activity
 * @Description :VideoEdit-edit-Speed-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class SpeedActivity extends BaseActivity {
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private SingleClipFragment mClipFragment;
    private EditChangeSpeedView mEditChangeSpeedView;
    private RelativeLayout rl_select_mode;
    private ImageView iv_conventional,iv_curve,iv_confirm;
    private TextView tv_conventional,tv_curve;
    private CurveSpeedViewAdapter mAdapter;
    private RecyclerView rv_curve;
    private RelativeLayout rl_curve;
    private TextView tv_reset;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private ArrayList<ClipInfo> mClipArrayList;
    private int mCurClipIndex = 0;
    private static final float floatZero = 0.000001f;
    private List<ChangeSpeedCurveInfo> changeSpeedCurveInfoList;
    private int mCurrentSelectedCurvePosition = 0;
    private EditChangeSpeedCurveView changeSpeedCurveView;
    private ImageView iv_confirm_curve;
    private boolean isEditCurveSpeed = false;
    private boolean hasCurveSpeed = false;

    private float mSpeed = 1.0f;
    private boolean keepAudioPitch  = true;
    private String mCurveSpeed;
    //当前播放状态 Current playback status
    private boolean isPlaying = false;
    //当前播放进度 timeline 时间 Current playback progress
    private long mPlayTimestamp;
    //拖拽曲线变速的时码线，拖拽时码线松手是判断是否播放使用
    //Drag the time code line of the curve speed change, drag the time code line to release it to judge whether to play
    private boolean seekCurveTimeline = false;
    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_speed;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        mBottomLayout = findViewById(R.id.bottomLayout);
        mEditChangeSpeedView = findViewById(R.id.change_speed_view);
        rl_select_mode = findViewById(R.id.rl_select_change_mode);
        iv_conventional = findViewById(R.id.iv_change_speed_conventional);
        iv_curve = findViewById(R.id.iv_change_speed_curve);
        iv_confirm = findViewById(R.id.iv_confirm);
        tv_conventional = findViewById(R.id.tv_change_speed_conventional);
        tv_curve = findViewById(R.id.tv_change_speed_curve);
        rv_curve = findViewById(R.id.rv_curve);
        rl_curve = findViewById(R.id.rl_curve_type);
        changeSpeedCurveView = findViewById(R.id.change_speed_curve_view);
        iv_confirm_curve = findViewById(R.id.iv_confirm_curve);
        tv_reset = findViewById(R.id.tv_reset);
        initCurveView();
    }

    private void initCurveView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rv_curve.setLayoutManager(layoutManager);
        mAdapter = new CurveSpeedViewAdapter(this);
        rv_curve.setAdapter(mAdapter);
        changeSpeedCurveInfoList = CurveSpeedUtil.listSpeedFromJson(this);
        mAdapter.updateData(changeSpeedCurveInfoList);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.speed);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mClipArrayList =  BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        if(mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size())
            return;
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        if(clipInfo == null)
            return;
        mTimeline = TimelineUtil.createSingleClipTimeline(clipInfo,true);
        if(mTimeline == null)
            return;
        initClipFragment();

        updateSpeedSeekBar(clipInfo.getSpeed(),clipInfo.isKeepAudioPitch());
        //获取到当前设置的曲线效果 Gets the curve effect for the current setting
        ChangeSpeedCurveInfo changeSpeedCurveInfo = clipInfo.getmCurveSpeed();
        if(null != changeSpeedCurveInfo){
            int index = changeSpeedCurveInfo.index;
            mCurrentSelectedCurvePosition = index;
            changeSpeedCurveInfoList.get(index).speed = changeSpeedCurveInfo.speed;
            mAdapter.setSelectedPosition(mCurrentSelectedCurvePosition);
        }
    }

    /**
     * 初始化起始变速值
     * Initialize the initial variable speed value
     * @param speedVal
     */
    private void updateSpeedSeekBar(float speedVal,boolean keepAudioPitch){
        this.mSpeed = speedVal;
        this.keepAudioPitch = keepAudioPitch;
        mEditChangeSpeedView.setSpeed(speedVal,!keepAudioPitch);
    }
    @Override
    protected void initListener() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                removeTimeline();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {

            }
        });
        iv_conventional.setOnClickListener(this);
        iv_curve.setOnClickListener(this);
        iv_confirm.setOnClickListener(this);
        mEditChangeSpeedView.setOnFunctionListener(new EditChangeSpeedView.OnFunctionListener() {
            @Override
            public void onConfirm(float speed, boolean changeVoice) {
                //saveChangeSpeedAndQuit();
                //这个返回选择调整的类型 不退出页面
                mEditChangeSpeedView.setVisibility(View.GONE);
                rl_select_mode.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChangeVoice(float speed,boolean changeVoice) {
                changeSpeed(speed,!changeVoice);
            }

            @Override
            public void onSpeedChanged(float speed, boolean changeVoice) {
                if(hasCurveSpeed){

                    //调节常规变速后，曲线变速选择无
                    //After adjusting the normal speed, the curve speed selection is not available
                    mCurrentSelectedCurvePosition = 0;
                    mAdapter.setSelectedPosition(mCurrentSelectedCurvePosition);
                    //曲线变速效果设置为无
                    //Curve speed change effect is set to none
                    ChangeSpeedCurveInfo changeSpeedCurveInfo = changeSpeedCurveInfoList.get(0);
                    changeSpeedForCurve(changeSpeedCurveInfo.speed);
                    hasCurveSpeed = false;
                    mCurveSpeed = "";
                    upDataClipDuration();
                }

                changeSpeed(speed,!changeVoice);
            }
        });
        iv_confirm_curve.setOnClickListener(this);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {

                hasCurveSpeed = pos!=0;
                //选中一种曲线变速的规则
                //Choose a curve speed rule
                ChangeSpeedCurveInfo changeSpeedCurveInfo = changeSpeedCurveInfoList.get(pos);
                //当前已经是选中的，则去编辑
                //It is currently selected, go to edit
                if(mCurrentSelectedCurvePosition == pos && pos!=0){
                    changeSpeedCurveView.setVisibility(View.VISIBLE);
                    tv_reset.setVisibility(View.VISIBLE);
                    rv_curve.setVisibility(View.INVISIBLE);
                    isEditCurveSpeed = true;
                    changeSpeedCurveView.setInfo(changeSpeedCurveInfo);
                    changeSpeedCurveView.setClipDuration(getClipDuration());
                    //如果 手动滑动过播放进度  进入曲线编辑页面暂停 否则从0播放
                    //If you manually slide over the playback progress,
                    // enter the curve editing page to pause, otherwise play from 0
                    if(mPlayTimestamp == 0){
                        playVideo(0);
                    }else{
                        //如果设置自定义变速并且没有调节过 则直接使用seek的时间节点更新
                        //If you set a custom variable speed and have not adjusted it,
                        // use the seek time node to update directly
                        long bzPosition = mPlayTimestamp;
                        if(!TextUtils.isEmpty(mCurveSpeed)){
                            bzPosition = getClipPositionFromTimelinePosition(bzPosition);
                        }
                        //更新基准线的位置 Update the position of the baseline
                        changeSpeedCurveView.upDataPlayProgress(bzPosition);
                        //stopPlayVideo();
                    }
                }else{
                    //第一次选中 从片段开始位置播放
                    //Select for the first time to play from the beginning of the clip
                    mAdapter.setSelectedPosition(pos);
                    mCurrentSelectedCurvePosition = pos;
                    mCurveSpeed = changeSpeedCurveInfo.speed;
                    changeSpeedForCurve(mCurveSpeed);
                    //选择无暂定到当前位置  否则从头开始播放
                    //Choose No Tentatively to the current position, otherwise start playing from the beginning
                    if(pos == 0){
                        stopPlayVideo();
                    }else{
                        //从0开始播放 Start playing from 0
                        playVideo(0);
                        hasCurveSpeed = true;
                    }

                }
            }
        });
        tv_reset.setOnClickListener(this);
        changeSpeedCurveView.setOnFunctionListener(new EditChangeSpeedCurveView.OnFunctionListener() {
            @Override
            public void onChangePoint(boolean addPoint) {
                // stopPlay
                stopPlayVideo();
            }

            @Override
            public void onTimelineMove(long timePoint) {
                seekCurveTimeline = true;
                //实时seek  seekTimeline onTime
                seekTimeline(timePoint);

            }

            @Override
            public void onSpeedChanged(String speed,long timePoint) {
                seekCurveTimeline = false;
                changeSpeedCurveInfoList.get(mCurrentSelectedCurvePosition).speed = speed;
                mCurveSpeed = speed;
                hasCurveSpeed = true;
                changeSpeedForCurve(speed);
                seekTimeline(timePoint);

            }

            @Override
            public void onActionDown() {
                isPlaying = mClipFragment.getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK;
            }

            @Override
            public void onActionUp(long timePoint) {
                if (!isPlaying) {
                    //暂停中 从当前位置播放
                    //Pausing and playing from the current position
                    long timeLineTimePoint = getTimelinePositionFromClipPosition(timePoint);
                    seekTimeline(timeLineTimePoint);
                } else {
                    //如果是拖拽时码线，且原状态是暂停，不播放
                    //If the time code line is dragged, and the original state is paused, no playback
                    if(!seekCurveTimeline){
                        //播放中 从0播放
                        //Playing from 0
                        playVideo(0);
                    }
                }
                seekCurveTimeline = false;
            }

            @Override
            public void onSelectPoint() {
                stopPlayVideo();
            }
        });
        //设置视频播放的回调
        //Set callback for video playback
        mClipFragment.setVideoFragmentCallBack(new SingleClipFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                if(changeSpeedCurveView.getVisibility() ==View.VISIBLE){
                    changeSpeedCurveView.upDataPlayProgress(0);
                }
            }

            @Override
            public void playStopped(NvsTimeline timeline) {
            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {

                if(changeSpeedCurveView.getVisibility() ==View.VISIBLE){
                    //如果设置自定义变速并且没有调节过 则直接使用seek的时间节点更新
                    //If you set a custom variable speed and have not adjusted it, use the seek time node to update directly
                    long bzPosition = stamp;
                    if(!TextUtils.isEmpty(mCurveSpeed)){
                        //timeline时间节点 变化为clip对应的时间点
                        //The timeline time node changes to the time point corresponding to the clip
                        bzPosition = getClipPositionFromTimelinePosition(stamp);
                    }
                    //更新基准线的位置
                    //Update the position of the baseline
                    changeSpeedCurveView.upDataPlayProgress(bzPosition);
                }
            }

            @Override
            public void streamingEngineStateChanged(int state) {

            }
        });
        mClipFragment.setVideoFragmentSeekListener(new SingleClipFragment.VideoFragmentSeekListener() {
            @Override
            public void onSeekBarChanged(long timeStamp) {
                mPlayTimestamp = timeStamp;
                //
                if(changeSpeedCurveView.getVisibility() == View.VISIBLE){
                    //如果设置自定义变速并且没有调节过 则直接使用seek的时间节点更新
                    //If you set a custom variable speed and have not adjusted it, use the seek time node to update directly
                    long bzPosition = timeStamp;
                    if(!TextUtils.isEmpty(mCurveSpeed)){
                        bzPosition = getClipPositionFromTimelinePosition(timeStamp);
                    }
                    //更新基准线的位置
                    //Update the position of the baseline
                    changeSpeedCurveView.upDataPlayProgress(bzPosition);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_confirm) {
            saveChangeSpeedAndQuit();
        } else if (id == R.id.iv_change_speed_conventional) {//                iv_conventional.setImageResource(R.mipmap.change_speed_conventional_selected);
//                iv_curve.setImageResource(R.mipmap.change_speed_curve);
//                tv_conventional.setTextColor(getResources().getColor(R.color.change_speed_selected));
//                tv_curve.setTextColor(getResources().getColor(R.color.ccffffff));
            rl_select_mode.setVisibility(View.INVISIBLE);

            mEditChangeSpeedView.setVisibility(View.VISIBLE);
            rl_curve.setVisibility(View.GONE);
        } else if (id == R.id.iv_change_speed_curve) {//                iv_conventional.setImageResource(R.mipmap.change_speed_conventional);
//                iv_curve.setImageResource(R.mipmap.change_speed_curve_selected);
//                tv_conventional.setTextColor(getResources().getColor(R.color.ccffffff));
//                tv_curve.setTextColor(getResources().getColor(R.color.change_speed_selected));
            rl_select_mode.setVisibility(View.INVISIBLE);

            mEditChangeSpeedView.setVisibility(View.GONE);
            rl_curve.setVisibility(View.VISIBLE);
        } else if (id == R.id.iv_confirm_curve) {//点击曲线变速中的确定按钮
            //Click the OK button in the curve shift
            //如果正在编辑 隐藏编辑页面 ， 显示曲线变速的item列表
            //If you are editing, hide the editing page and display the item list of curve speed change
            if (isEditCurveSpeed) {
                changeSpeedCurveView.setVisibility(View.GONE);
                tv_reset.setVisibility(View.INVISIBLE);
                rv_curve.setVisibility(View.VISIBLE);
                isEditCurveSpeed = false;
                stopPlayVideo();

            } else {
                //如果处在选择曲线变速item的列表位置
                //If you are in the list position of selecting the curve variable speed item
                //关闭列表，返回选择变速类型的页面
                //Close the list and return to the page for selecting the type of shifting
                rl_curve.setVisibility(View.GONE);
                rl_select_mode.setVisibility(View.VISIBLE);
                //恢复其他所有曲线效果为默认值
                //Restore all other curve effects to default values
                resetCurveSpeedValue(mCurrentSelectedCurvePosition);
                stopPlayVideo();
            }
        } else if (id == R.id.tv_reset) {//恢复初始值 Restore initial value
            ChangeSpeedCurveInfo changeSpeedCurveInfo = changeSpeedCurveInfoList.get(mCurrentSelectedCurvePosition);
            changeSpeedCurveInfo.speed = changeSpeedCurveInfo.speedOriginal;
            changeSpeedForCurve(changeSpeedCurveInfo.speed);
            changeSpeedCurveView.setInfo(changeSpeedCurveInfo);
            changeSpeedCurveView.setClipDuration(getClipDuration());
        }

    }

    /**
     * 确认选择一种之后，其他曲线变速值恢复默认值
     * After confirming the selection of one, the other curve speed values will be restored to the default values
     * @param mCurrentSelectedCurvePosition  选择的效果 ，这个值对应的曲线不恢复
     *                                       The selected effect, the curve corresponding to this value is not restored
     */
    private void resetCurveSpeedValue(int mCurrentSelectedCurvePosition) {

        for(int i = 1; i<changeSpeedCurveInfoList.size() ; i++){
            if(mCurrentSelectedCurvePosition == i){
                continue;
            }
            ChangeSpeedCurveInfo changeSpeedCurveInfo = changeSpeedCurveInfoList.get(i);
            changeSpeedCurveInfo.speed = changeSpeedCurveInfo.speedOriginal;
        }
    }

    /**
     * 保存变速数据 并退出当前页面
     * Save variable speed data and exit the current page
     */
    private void saveChangeSpeedAndQuit(){
        mClipArrayList.get(mCurClipIndex).setSpeed(mSpeed);
        mClipArrayList.get(mCurClipIndex).setKeepAudioPitch(keepAudioPitch);
        if(hasCurveSpeed){
            mClipArrayList.get(mCurClipIndex).setmCurveSpeed(changeSpeedCurveInfoList.get(mCurrentSelectedCurvePosition));
        }else{
            mClipArrayList.get(mCurClipIndex).setmCurveSpeed(null);

        }
        BackupData.instance().setClipInfoData(mClipArrayList);
        removeTimeline();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }

    /**
     * 修改变速
     * Modify speed
     * @param speed  变速值
     * @param keepAudioPitch  是否变调
     */
    private void changeSpeed(float speed,boolean keepAudioPitch){
        this.mSpeed = speed;
        this.keepAudioPitch = keepAudioPitch;
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if(videoTrack == null)
            return;
        NvsVideoClip videoClip = videoTrack.getClipByIndex(0);
        if(videoClip == null)
            return;
        videoClip.changeSpeed(speed,keepAudioPitch);
        upDataClipDuration();

    }

    /**
     * 设置曲线变速效果
     * Modify the speed setting curve speed effect
     * @param speed
     */
    private void changeSpeedForCurve(String speed){
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if(videoTrack == null)
            return;
        NvsVideoClip videoClip = videoTrack.getClipByIndex(0);
        if(videoClip == null)
            return;
        boolean isSucess = videoClip.changeCurvesVariableSpeed(speed, true);
        Log.e("changeSpeedForCurve", "曲线变速设置" + (isSucess ? "成功" : "失败"));
        //修改时长 Modification duration
        upDataClipDuration();
    }

    /**
     * 设置变速之后调用更新时长
     * Recall the update duration after setting the speed change
     */
    private void upDataClipDuration(){
        if(null != mClipFragment){

            mClipFragment.updateTotalDuration();
        }
    }
    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline(){
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    private void initClipFragment() {
        mClipFragment = new SingleClipFragment();
        mClipFragment.setFragmentLoadFinisedListener(new SingleClipFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mClipFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            }
        });
        mClipFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight",mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight",mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mClipFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mClipFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mClipFragment);
    }

    /**
     * 获取视频片段的时长
     * Get the duration of the video clip
     * @return
     */
    private long getClipDuration() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if(videoTrack == null)
            return 0;
        NvsVideoClip videoClip = videoTrack.getClipByIndex(0);
        if(videoClip == null)
            return 0;
        return videoClip.getTrimOut() - videoClip.getTrimIn();
    }

    /**
     * 暂停播放
     * stopPlay
     */
    private void stopPlayVideo(){
        if(null != mClipFragment){
            mClipFragment.stopEngine();
        }
    }

    /**
     * 开始播放视频
     * Start playing video
     */
    private void playVideo(long start){
        if(null != mClipFragment){
            NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
            if(videoTrack == null)
                return;
            NvsVideoClip videoClip = videoTrack.getClipByIndex(0);
            if(videoClip == null)
                return ;
            mClipFragment.playVideo(start,videoClip.getTrimOut());
        }
    }

    /**
     * 时码线移动，同步更新播放器画面seek
     * The time code line moves, and the player screen is updated synchronously. Seek
     * @param timestamp  clip上的时间节点 需要转换成timeline的时间
     */
    private void seekTimeline(long timestamp){
        if(null != mClipFragment){
            long timelinePosition =getTimelinePositionFromClipPosition(timestamp);
            mClipFragment.updateCurPlayTime(timelinePosition);
            mClipFragment.seekTimeline(timelinePosition, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        }
    }

    /**
     * 获取当前clip的时间点对应的timeline时间
     * Get the timeline time corresponding to the time point of the current clip
     * @param timestamp
     * @return
     */
    private long getTimelinePositionFromClipPosition(long timestamp){
        //计算对应变速后的位置 Calculate the position after the corresponding shift
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if(videoTrack == null)
            return timestamp;
        NvsVideoClip videoClip = videoTrack.getClipByIndex(0);
        if(videoClip == null)
            return timestamp;
        long timelinePosition = videoClip.GetTimelinePosByClipPosCurvesVariableSpeed(timestamp) + videoClip.getTrimIn();
        return timelinePosition;
    }

    /**
     * 获取当前timeline上的时间节点对应的clip时间
     * Get the clip time corresponding to the time node on the current timeline
     * @param timelinePosition
     * @return
     */
    private long getClipPositionFromTimelinePosition(long timelinePosition){
        //计算对应变速后的位置
        //Calculate the position after the corresponding shift
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if(videoTrack == null)
            return timelinePosition;
        NvsVideoClip videoClip = videoTrack.getClipByIndex(0);
        if(videoClip == null)
            return timelinePosition;
        //timeline时间节点 变化为clip对应的时间点
        //The timeline time node changes to the time point corresponding to the clip
        long bzPosition = videoClip.GetClipPosByTimelinePosCurvesVariableSpeed(timelinePosition) - videoClip.getTrimIn();
        return bzPosition;
    }
}
