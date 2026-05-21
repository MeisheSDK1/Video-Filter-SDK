//================================================================================
//
// (c) Copyright China Digital Video (Beijing) Limited, 2017. All rights reserved.
//
// This code and information is provided "as is" without warranty of any kind,
// either expressed or implied, including but not limited to the implied
// warranties of merchantability and/or fitness for a particular purpose.
//
//--------------------------------------------------------------------------------
//   Birth Date:    Jul 24. 2017
//   Author:        NewAuto video team
//================================================================================
package com.cdv.customvideofx;

import com.meicam.sdk.NvsCustomVideoFx;

import java.nio.ByteBuffer;


public class EditCustomVideoFx implements NvsCustomVideoFx.RendererExt {

    /*
     *  美摄SDK对自定义视频特效调用此方法以便让用户初始化一些资源
     *  这个方法在自定义视频特效的生命周期里最多只会被调用一次。如果该特效从未被真正使用过，则这个方法将不会被调用。
     *  这个方法是在美摄SDK引擎的特效渲染线程里调用，并且当前线程已经绑定了一个EGL Context。
     */
    @Override
    public void onInit() {
    }

    /*
     *  美摄SDK对自定义视频特效调用此方法以便让用户清理资源
     *  这个方法在自定义视频特效的生命周期里最多只会被调用一次，而且一定会在onInit之后调用，如果onInit没有被调用则也不会调用该方法。
     *  这个方法是在美摄SDK引擎的特效渲染线程里调用，并且当前线程已经绑定了一个EGL Context。
     */
    @Override
    public void onCleanup() {

    }

    /*
     *  美摄SDK对自定义视频特效调用此方法以便让进行一些资源预处理
     *  这个方法在自定义视频特效的生命周期里会被多次调用，而且一定会在onInit之后调用，一般来讲是在每次播放时间线之前调用。
     *  一般来讲用户需要在此函数里面进行诸如构建shader program的工作。
     *  这个方法是在美摄SDK引擎的特效渲染线程里调用，并且当前线程已经绑定了一个EGL Context。
     */
    @Override
    public void onPreloadResources() {
        // 通过在预取资源过程中构建shader program可以避免卡顿
        // 但是本示例程序展示的是一个采集自定义视频特效，因此没有预取的过程。
        // 如果将其应用在时间线相关的自定义视频特效则可充分利用预取来构建shader program
    }

    @Override
    public void onRender(NvsCustomVideoFx.RenderContext renderContext) {
        NvsCustomVideoFx.RenderHelper renderHelper = renderContext.helper;
        if (null != renderHelper && renderContext.hasBuddyVideoFrame) {
            int outputTexId = renderContext.outputVideoFrame.texId;
            //这里SDK返回的ByteBuffer是上下颠倒的，所以这里做了上下镜像
            ByteBuffer newByteBuffer = EditUtil.flipI420Vertical(renderContext.inputBuddyVideoFramebuffer, renderContext.inputBuddyVideoFrameInfo.frameWidth, renderContext.inputBuddyVideoFrameInfo.frameHeight);
            renderHelper.uploadHostBufferToOpenGLTexture(newByteBuffer, renderContext.inputBuddyVideoFrameInfo, outputTexId);
        }

    }


    @Override
    public void onClearCacheResources() {

    }

    @Override
    public NvsCustomVideoFx.FxRequirement onCollectionReq() {
        NvsCustomVideoFx.FxRequirement fxRequirement = new NvsCustomVideoFx.FxRequirement();
        fxRequirement.needBuddyFrame = true;
        return fxRequirement;
    }
}
