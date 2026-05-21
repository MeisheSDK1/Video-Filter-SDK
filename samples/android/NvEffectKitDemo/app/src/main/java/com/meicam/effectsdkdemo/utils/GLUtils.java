package com.meicam.effectsdkdemo.utils;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLException;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glGenTextures;

/**
 * @author ms
 */
public class GLUtils {

    public static int createGlTexture(int width, int height) {
        int[] tex = new int[1];
        glGenTextures(1, tex, 0);
        EGLHelper.checkGlError("Texture generate");
        return tex[0];
    }

    public static void destroyGlTexture(int texId) {
        if (texId <= 0) {
            return;
        }
        int[] tex = new int[1];
        tex[0] = texId;
        GLES20.glDeleteTextures(1, tex, 0);
    }

    public static Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) throws OutOfMemoryError {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    intBuffer);
            int offset1, offset2;

            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }


}
