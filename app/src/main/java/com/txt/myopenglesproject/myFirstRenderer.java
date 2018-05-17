package com.txt.myopenglesproject;

/**
 * Created by txt on 2017/10/10.NO APPLIED
 */

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.common.LoggerConfig;
import com.common.MatrixHelper;
import com.common.TextResourceReader;
import com.common.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class myFirstRenderer implements Renderer
{
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = 20;
    private final FloatBuffer vertexData;
    private final Context context;
    private int program;
    private static final String A_COLOR = "a_Color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_Matrix";
    private int aColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;
    private final float[] Matrix = new float[16];
    private final float[] ModelMatrix = new float[16];
    public myFirstRenderer(Context context)
    {
        float table[] =
                {     //x,y,r,g,b
                        0f,0f,1f,1f,1f,
                        -0.5f,-0.5f,0.7f,0.7f,0.7f,
                        0.5f,-0.5f,0.7f,0.7f,0.7f,
                        0.5f,0.5f,0.7f,0.7f,0.7f,
                        -0.5f,0.5f,0.7f,0.7f,0.7f,
                        -0.5f,-0.5f,0.7f,0.7f,0.7f,

                        -0.5f,0f,1f,0f,0f,
                        0.5f,0f,1f,0f,0f,

                        0f,-0.25f,1f,0f,0f,
                        0f,0.25f,1f,0f,0f
                };
        this.context = context;
        vertexData = ByteBuffer.allocateDirect(table.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(table);
    }
    @Override
    public void onSurfaceChanged(GL10 glUnused,int width,int height)
    {
        glViewport(0,0,width,height);
        MatrixHelper.perspectiveM(Matrix,45,(float)width/(float) height,0f,10f);
        setIdentityM(ModelMatrix,0);
        translateM(ModelMatrix,0,0f,0f,-2.5f);
        rotateM(ModelMatrix,0,-60,1f,0f,0f);
        rotateM(ModelMatrix,0,0f,0f,1f,0f);
        final float[] temp = new float[16];
        multiplyMM(temp,0,Matrix,0,ModelMatrix,0);
        System.arraycopy(temp,0,Matrix,0,temp.length);
    }
    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniformMatrix4fv(uMatrixLocation,1,false,Matrix,0);
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
        glDrawArrays(GL_LINES,6,2);
        glDrawArrays(GL_POINTS,8,2);
    }
    @Override
    public void onSurfaceCreated(GL10 glUnused,EGLConfig config)
    {
        glClearColor(0f,0f,0f,0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.fragment_shader);
        int vertexShader = ShaderHelper.compileShader(GL_VERTEX_SHADER,vertexShaderSource);
        int fragmentShader = ShaderHelper.compileShader(GL_FRAGMENT_SHADER,fragmentShaderSource);
        program = ShaderHelper.createAndLinkProgram(vertexShader,fragmentShader,null);
        if(LoggerConfig.ON)
            ShaderHelper.validateProgram(program);
        glUseProgram(program);
        aColorLocation = glGetAttribLocation(program,A_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GL_FLOAT,false,STRIDE,vertexData);
        glEnableVertexAttribArray(aPositionLocation);
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation,COLOR_COMPONENT_COUNT,GL_FLOAT,false,STRIDE,vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    public void rotate_x(float degree)
    {
        setIdentityM(ModelMatrix,0);
        rotateM(ModelMatrix,0,degree,1f,0f,0f);
        final float[] temp = new float[16];
        multiplyMM(temp,0,Matrix,0,ModelMatrix,0);
        System.arraycopy(temp,0,Matrix,0,temp.length);
    }
    public void rotate_y(float degree)
    {
        setIdentityM(ModelMatrix,0);
        rotateM(ModelMatrix,0,degree,0f,1f,0f);
        final float[] temp = new float[16];
        multiplyMM(temp,0,Matrix,0,ModelMatrix,0);
        System.arraycopy(temp,0,Matrix,0,temp.length);
    }
    public void scale(float ratio)
    {
        setIdentityM(ModelMatrix,0);
        scaleM(ModelMatrix,0,ratio,ratio,ratio);
        final float[] temp = new float[16];
        multiplyMM(temp,0,Matrix,0,ModelMatrix,0);
        System.arraycopy(temp,0,Matrix,0,temp.length);
    }
    public void shift_x(float distance)
    {
        setIdentityM(ModelMatrix,0);
        translateM(ModelMatrix,0,distance,0f,0f);
        final float[] temp = new float[16];
        multiplyMM(temp,0,Matrix,0,ModelMatrix,0);
        System.arraycopy(temp,0,Matrix,0,temp.length);
    }
    public void shift_y(float distance)
    {
        setIdentityM(ModelMatrix,0);
        translateM(ModelMatrix,0,0f,distance,0f);
        final float[] temp = new float[16];
        multiplyMM(temp,0,Matrix,0,ModelMatrix,0);
        System.arraycopy(temp,0,Matrix,0,temp.length);
    }
}
