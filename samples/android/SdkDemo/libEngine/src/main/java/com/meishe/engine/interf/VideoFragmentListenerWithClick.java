package com.meishe.engine.interf;

/**
 * Created by CaoZhiChao on 2020/11/3 17:08
 */
public interface VideoFragmentListenerWithClick extends VideoFragmentListener {

    /**
     * VideoFragment有自己的点击逻辑控制播放等，但是有的点击逻辑需要由外界控制
     * <p></p>
     * VideoFragment has its own click logic control playback, but some click logic needs to be controlled by the outside world
     *
     * @return 根据返回值判断点击逻辑是否由外界执行。
     * Determines whether the click logic is executed by the outside world based on the return value
     */
    boolean clickPlayButtonByOthers();

    /**
     * LiveWindow的点击逻辑是否由外界的实现控制。默认是点击触发播放和暂停。
     * <p></p>
     * Whether the click logic of LiveWindow is controlled by an external implementation. The default is to click to trigger play and pause.
     *
     * @return true代表由外界控制。
     * if true will control by others
     */
    boolean clickLiveWindowByOthers();

    /**
     * 时间线链接到livewindow后的回调
     * <p></p>
     * timeline connect to livewindow
     */
    void connectTimelineWithLiveWindow();
}
