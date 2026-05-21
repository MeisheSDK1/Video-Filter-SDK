package com.meicam.effectsdkdemo.utils;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper
{
	private static final String TAG = "ShaderHelper";
	
	/** 
	 * Helper function to compile a shader.
	 * 
	 * @param shaderType The shader type.
	 * @param shaderSource The shader source code.
	 * @return An OpenGL handle to the shader.
	 */
	public static int compileShader(final int shaderType, final String shaderSource) 
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) 
		{
			// Pass in the shader source.
			//传入着色器源代码。
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			//编译着色器。
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			//获取编译状态。
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			//如果编译失败，删除着色器。
			if (compileStatus[0] == 0) 
			{
				Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
		{			
			throw new RuntimeException("Error creating shader.");
		}
		
		return shaderHandle;
	}
	
	/**
	 * Helper function to compile and link a program.
	 * 编译和链接程序的辅助函数。
	 * 
	 * @return An OpenGL handle to the program.程序的OpenGL句柄。
	 */
	public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) 
	{
		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0) 
		{
			// Bind the vertex shader to the program.
			//将顶点着色器绑定到程序。
			GLES20.glAttachShader(programHandle, vertexShaderHandle);			

			// Bind the fragment shader to the program.
			//将片段着色器绑定到程序。
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			// Bind attributes
			// 绑定属性
			if (attributes != null)
			{
				final int size = attributes.length;
				for (int i = 0; i < size; i++)
				{
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}						
			}
			
			// Link the two shaders together into a program.
			// 将两个着色器链接到一个程序中。
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			//获取链路状态
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			//如果链接失败，请删除该程序。
			if (linkStatus[0] == 0) 
			{				
				Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		
		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}
}
