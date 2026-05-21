package com.meicam.effectsdkdemo.utils;

/**
 * Created by meicam-dx on 2018/3/19.
 */
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * EGL执行流程
 * a, 选择Display
 * b, 选择Config
 * c, 创建Surface
 * d, 创建Context
 * e, 指定当前的环境为绘制环境
 */

public class EGLHelper {

    public EGL10 mEgl;
    public EGLDisplay mEglDisplay;
    public EGLConfig mEglConfig;
    public EGLSurface mEglSurface;
    public EGLContext mEglContext;
    public GL10 mGL;

    private static final int EGL_CONTEXT_CLIENT_VERSION=0x3098;

    public static final int SURFACE_PBUFFER=1;
    public static final int SURFACE_PIM=2;
    public static final int SURFACE_WINDOW=3;

    private int surfaceType=SURFACE_PBUFFER;
    private Object surface_native_obj;

    private int red=8;
    private int green=8;
    private int blue=8;
    private int alpha=8;
    private int depth=16;
    private int renderType=4;
    private int bufferType=EGL10.EGL_SINGLE_BUFFER;
    private EGLContext shareContext=EGL10.EGL_NO_CONTEXT;


    public void config(int red,int green,int blue,int alpha,int depth,int renderType){
        this.red=red;
        this.green=green;
        this.blue=blue;
        this.alpha=alpha;
        this.depth=depth;
        this.renderType=renderType;
    }

    public void setSurfaceType(int type,Object ... obj){
        this.surfaceType=type;
        if(obj!=null){
            this.surface_native_obj=obj[0];
        }
    }

    public EGLError eglInit(int width,int height){
        int[] attributes = new int[] {
                EGL10.EGL_RED_SIZE, red,  //指定RGB中的R大小（bits）
                EGL10.EGL_GREEN_SIZE, green, //指定G大小
                EGL10.EGL_BLUE_SIZE, blue,  //指定B大小
                EGL10.EGL_ALPHA_SIZE, alpha, //指定Alpha大小，以上四项实际上指定了像素格式
                EGL10.EGL_DEPTH_SIZE, depth, //指定深度缓存(Z Buffer)大小
                EGL10.EGL_RENDERABLE_TYPE, renderType, //指定渲染api版本, EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE };  //总是以EGL10.EGL_NONE结尾

        //获取Display
        mEgl= (EGL10)EGLContext.getEGL();
        mEglDisplay=mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        int[] version=new int[2];    //主版本号和副版本号
        mEgl.eglInitialize(mEglDisplay,version);
        //选择Config
        int[] configNum=new int[1];
        mEgl.eglChooseConfig(mEglDisplay,attributes,null,0,configNum);
        if(configNum[0]==0){
            return EGLError.ConfigErr;
        }
        EGLConfig[] c=new EGLConfig[configNum[0]];
        mEgl.eglChooseConfig(mEglDisplay,attributes,c,configNum[0],configNum);
        mEglConfig=c[0];
        //创建Surface
        int[] surAttr=new int[]{
                EGL10.EGL_WIDTH,width,
                EGL10.EGL_HEIGHT,height,
                EGL10.EGL_NONE
        };
        mEglSurface=createSurface(surAttr);
        //创建Context
        int[] contextAttr=new int[]{
                EGL_CONTEXT_CLIENT_VERSION,2,
                EGL10.EGL_NONE
        };
        mEglContext=mEgl.eglCreateContext(mEglDisplay,mEglConfig,shareContext,contextAttr);
        makeCurrent();
        return EGLError.OK;
    }

    public void makeCurrent(){
        mEgl.eglMakeCurrent(mEglDisplay,mEglSurface,mEglSurface,mEglContext);
        mGL= (GL10)mEglContext.getGL();
    }

    public void destroy(){
        mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
        mEgl.eglDestroyContext(mEglDisplay, mEglContext);
        mEgl.eglTerminate(mEglDisplay);
    }

    private EGLSurface createSurface(int[] attr){
        switch (surfaceType){
            case SURFACE_WINDOW:
                return mEgl.eglCreateWindowSurface(mEglDisplay,mEglConfig,surface_native_obj,attr);
            case SURFACE_PIM:
                return mEgl.eglCreatePixmapSurface(mEglDisplay,mEglConfig,surface_native_obj,attr);
            default:
                return mEgl.eglCreatePbufferSurface(mEglDisplay,mEglConfig,attr);
        }
    }

    public static final float CUBE[] = {
            -1.0f, 1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
    };

    public static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    public static final float TEXTURE_ROTATED_90[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };
    public static final float TEXTURE_ROTATED_180[] = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
    };
    public static final float TEXTURE_ROTATED_270[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private static final String SURFACETEXTURE_INPUT_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	textureCoordinate = inputTextureCoordinate.xy;\n" +
            "	gl_Position = position;\n" +
            "}";

    private static final String SURFACETEXTURE_INPUT_FRAGMENT_SHADER_OES = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "\n" +
            "precision mediump float;\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES inputImageTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    public static final String CAMERA_INPUT_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    public static int loadProgramForSurfaceTexture() {
        return loadProgram(SURFACETEXTURE_INPUT_VERTEX_SHADER, SURFACETEXTURE_INPUT_FRAGMENT_SHADER_OES);
    }

    public static int loadProgramForTexture() {
        return loadProgram(SURFACETEXTURE_INPUT_VERTEX_SHADER, CAMERA_INPUT_FRAGMENT_SHADER);
    }

    public static int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }

        iProgId = GLES20.glCreateProgram();

        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);

        GLES20.glLinkProgram(iProgId);

        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }

    private static int loadShader(final String strSource, final int iType) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    public static void bindFrameBuffer(int textureId, int frameBuffer, int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,textureId, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("SurfaceTest", op + ": glError " + GLUtils.getEGLErrorString(error));
        }
    }

    public static float[] getRotation(final int rotation, final boolean flipHorizontal,
                                      final boolean flipVertical) {
        float[] rotatedTex;
        switch (rotation) {
            case 90:
                rotatedTex = TEXTURE_ROTATED_90;
                break;
            case 180:
                rotatedTex = TEXTURE_ROTATED_180;
                break;
            case 270:
                rotatedTex = TEXTURE_ROTATED_270;
                break;
            case 0:
            case 360:
            default:
                rotatedTex = TEXTURE_NO_ROTATION;
                break;
        }
        if (flipHorizontal) {
            rotatedTex = new float[]{
                    flip(rotatedTex[0]), rotatedTex[1],
                    flip(rotatedTex[2]), rotatedTex[3],
                    flip(rotatedTex[4]), rotatedTex[5],
                    flip(rotatedTex[6]), rotatedTex[7],
            };
        }
        if (flipVertical) {
            rotatedTex = new float[]{
                    rotatedTex[0], flip(rotatedTex[1]),
                    rotatedTex[2], flip(rotatedTex[3]),
                    rotatedTex[4], flip(rotatedTex[5]),
                    rotatedTex[6], flip(rotatedTex[7]),
            };
        }
        return rotatedTex;
    }


    private static float flip(final float i) {
        if (i == 0.0f) {
            return 1.0f;
        }
        return 0.0f;
    }
}