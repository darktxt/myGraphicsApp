package com.program;

import android.content.Context;

import com.txt.myopenglesproject.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by txt on 2017/11/18.
 */

public class LightProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int uColorLocation;
    public LightProgram(Context context){
        super(context, R.raw.light_vertex_shader,R.raw.light_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        uColorLocation = glGetUniformLocation(program,U_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);

    }
    public void setUniforms(float[] mvpmatrix,float[] color)
    {
        glUniformMatrix4fv(uMatrixLocation,1,false,mvpmatrix,0);
        glUniform3fv(uColorLocation,1,color,0);
    }
    public int getaPositionLocation()
    {
        return aPositionLocation;
    }
}
