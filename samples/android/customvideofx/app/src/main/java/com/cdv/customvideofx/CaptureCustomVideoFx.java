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

import android.opengl.GLES20;
import android.util.Log;

import com.meicam.sdk.NvsCustomVideoFx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class CaptureCustomVideoFx implements NvsCustomVideoFx.Renderer
{
    private static final String TAG = "Meicam";

    private int m_shaderProgram = 0;
    private int m_program_attrLoc_pos = -1;
    private int m_program_attrLoc_texCoord = -1;
    private int m_program_uniformLoc_saturation = -1;

    private static final String VERTEX_SHADER =
            "attribute highp vec2 posAttr;\n" +
            "attribute highp vec2 texCoordAttr;\n" +
            "varying highp vec2 texCoord;\n" +
            "void main()\n" +
            "{\n" +
            "    texCoord = texCoordAttr;\n" +
            "    gl_Position = vec4(posAttr, 0, 1);\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
            "uniform sampler2D sampler;\n" +
            "uniform lowp float saturation;\n" +
            "varying highp vec2 texCoord;\n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 color = texture2D(sampler, texCoord);\n" +
            "    lowp float minRGB = min(color.r, min(color.g, color.b));\n" +
            "    lowp float maxRGB = max(color.r, max(color.g, color.b));\n" +
            "    lowp vec3 lightness = vec3((minRGB + maxRGB) / 2.0);\n" +
            "    gl_FragColor = vec4(mix(lightness, color.rgb, saturation), color.a);\n" +
            "}\n";

    private FloatBuffer m_verticesBuffer;
    private float[] m_verticesData = new float[4 * 4];

    private float m_saturationGain = 0.0f;

    public void setSaturationGain(float saturationGain)
    {
        // 注意：JAVA语言可以保证对float类型成员变量的原子性，因此在这里设置m_saturationGain无需任何同步机制
        m_saturationGain = Math.max(Math.min(saturationGain, getMaxSaturationGain()), getMinSaturationGain());
    }

    public float getSaturationGain()
    {
        return m_saturationGain;
    }

    public float getMinSaturationGain()
    {
        return 0.0f;
    }

    public float getMaxSaturationGain()
    {
        return 2.0f;
    }

    /*
     *  美摄SDK对自定义视频特效调用此方法以便让用户初始化一些资源
     *  这个方法在自定义视频特效的生命周期里最多只会被调用一次。如果该特效从未被真正使用过，则这个方法将不会被调用。
     *  这个方法是在美摄SDK引擎的特效渲染线程里调用，并且当前线程已经绑定了一个EGL Context。
     */
    @Override
    public void onInit()
    {
        // 初始化全局资源
        m_verticesBuffer = ByteBuffer.allocateDirect(4 * 4 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    /*
     *  美摄SDK对自定义视频特效调用此方法以便让用户清理资源
     *  这个方法在自定义视频特效的生命周期里最多只会被调用一次，而且一定会在onInit之后调用，如果onInit没有被调用则也不会调用该方法。
     *  这个方法是在美摄SDK引擎的特效渲染线程里调用，并且当前线程已经绑定了一个EGL Context。
     */
    @Override
    public void onCleanup()
    {
        // 清理全局资源
        if (m_shaderProgram != 0) {
            GLES20.glDeleteProgram(m_shaderProgram);
            m_shaderProgram = 0;
        }

        m_verticesBuffer = null;
    }

    /*
     *  美摄SDK对自定义视频特效调用此方法以便让进行一些资源预处理
     *  这个方法在自定义视频特效的生命周期里会被多次调用，而且一定会在onInit之后调用，一般来讲是在每次播放时间线之前调用。
     *  一般来讲用户需要在此函数里面进行诸如构建shader program的工作。
     *  这个方法是在美摄SDK引擎的特效渲染线程里调用，并且当前线程已经绑定了一个EGL Context。
     */
    @Override
    public void onPreloadResources()
    {
        // 通过在预取资源过程中构建shader program可以避免卡顿
        // 但是本示例程序展示的是一个采集自定义视频特效，因此没有预取的过程。
        // 如果将其应用在时间线相关的自定义视频特效则可充分利用预取来构建shader program
        prepareShaderProgram();
    }

    /*!
     *  美摄SDK对自定义视频特效调用此方法以便对输入视频帧进行特效处理
     *  用户实现这个方法对输入视频帧进行处理并将结果写入到输出视频帧中去以便完成特效渲染。
     *  这个方法是在美摄SDK引擎的特效渲染线程里调用，并且当前线程已经绑定了一个EGL Context。
     *  当前线程已经绑定了一个FBO，用户只需在相应的attachment point上面绑定color buffer, depth buffer...即可
     *  注意：请务必在渲染完成后，将OpenGL ES context的状态复位到默认状态，比如用户渲染过程中调用了glEnable(GL_BLEND),
     *  则渲染完成后一定要调用glDisable(GL_BLEND),因为默认状态下blend是关闭的。关于OpenGL ES context的默认状态
     *  请参考https://www.khronos.org/opengles/
     *  警告：如果渲染完成后，没有将OpenGL ES context的状态复位到默认状态，则可能导致后续特效渲染发生错误！
     */
    @Override
    public void onRender(NvsCustomVideoFx.RenderContext renderContext)
    {
        if (!prepareShaderProgram())
            return;

        // 将输出视频帧纹理绑定到FBO的color attachment 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderContext.outputVideoFrame.texId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderContext.outputVideoFrame.texId, 0);

        GLES20.glViewport(0, 0, renderContext.outputVideoFrame.width, renderContext.outputVideoFrame.height);

        // NOTE: 将color frame buffer清空，这一步对于渲染结果并无意义，因为后面的渲染过程会将目标纹理整个重写。
        // 但这个操作还是有必要的，因为mobile的GPU一般都是tile based架构，这个操作对于OpenGL driver是一个hinting，
        // 可以告诉OpenGL driver不必理会color frame buffer原来的内容，减少了memory copy，提高性能，降低功耗
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(m_shaderProgram);
        // 设定饱和度的增益
        // 注意：JAVA语言可以保证对float类型成员变量的原子性，因此在这里读取m_saturationGain无需任何同步机制
        GLES20.glUniform1f(m_program_uniformLoc_saturation, m_saturationGain);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderContext.inputVideoFrame.texId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // 填写顶点数据
        final boolean isUpsideDownTexture = renderContext.inputVideoFrame.isUpsideDownTexture;
        m_verticesData[0] = -1;
        m_verticesData[1] = 1;
        m_verticesData[2] = 0;
        m_verticesData[3] = isUpsideDownTexture ? 0 : 1;
        m_verticesData[4] = -1;
        m_verticesData[5] = -1;
        m_verticesData[6] = 0;
        m_verticesData[7] = isUpsideDownTexture ? 1 : 0;
        m_verticesData[8] = 1;
        m_verticesData[9] = 1;
        m_verticesData[10] = 1;
        m_verticesData[11] = isUpsideDownTexture ? 0 : 1;
        m_verticesData[12] = 1;
        m_verticesData[13] = -1;
        m_verticesData[14] = 1;
        m_verticesData[15] = isUpsideDownTexture ? 1 : 0;
        m_verticesBuffer.position(0);
        m_verticesBuffer.put(m_verticesData);

        m_verticesBuffer.position(0);
        GLES20.glVertexAttribPointer(m_program_attrLoc_pos, 2, GLES20.GL_FLOAT, false, 4 * 4, m_verticesBuffer);
        m_verticesBuffer.position(2);
        GLES20.glVertexAttribPointer(m_program_attrLoc_texCoord, 2, GLES20.GL_FLOAT, false, 4 * 4, m_verticesBuffer);

        GLES20.glEnableVertexAttribArray(m_program_attrLoc_pos);
        GLES20.glEnableVertexAttribArray(m_program_attrLoc_texCoord);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(m_program_attrLoc_pos);
        GLES20.glDisableVertexAttribArray(m_program_attrLoc_texCoord);

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, 0, 0);
    }

    private boolean prepareShaderProgram()
    {
        if (m_shaderProgram != 0)
            return true;

        m_shaderProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (m_shaderProgram == 0)
            return false;

        m_program_attrLoc_pos = GLES20.glGetAttribLocation(m_shaderProgram, "posAttr");
        m_program_attrLoc_texCoord = GLES20.glGetAttribLocation(m_shaderProgram, "texCoordAttr");
        m_program_uniformLoc_saturation = GLES20.glGetUniformLocation(m_shaderProgram, "saturation");

        int samplerUnifromLoc = GLES20.glGetUniformLocation(m_shaderProgram, "sampler");
        GLES20.glUseProgram(m_shaderProgram);
        GLES20.glUniform1i(samplerUnifromLoc, 0);
        return true;
    }

    private int loadShader(int shaderType, String source)
    {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource)
    {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int fragShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, fragShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragShader);
        return program;
    }

    public void checkGlError(String op)
    {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
