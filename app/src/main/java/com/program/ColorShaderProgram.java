package com.program;

import android.content.Context;

import com.txt.myopenglesproject.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by txt on 2017/10/23.
 */

public class ColorShaderProgram extends ShaderProgram{
    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int uColorLocation;
    private final int uMVMatrixLocation;
    private final int uIT_MVMatrixLocation;
    private final int uMVPMatrixLocation;
    private final int uPointLightPositionsLocation;
    private final int uPointLightColorsLocation;
    private final int aNormalLocation;
    private final int uVectorToLight;

    public ColorShaderProgram(Context context)
    {
        super(context, R.raw.vertex_shader,R.raw.fragment_shader);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        uColorLocation = glGetUniformLocation(program,U_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);

        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
        uPointLightPositionsLocation =
                glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS);
        uPointLightColorsLocation =
                glGetUniformLocation(program, U_POINT_LIGHT_COLORS);
        uVectorToLight = glGetUniformLocation(program,U_VECTOR_TO_LIGHT);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);

    }

    public void setUniforms(float[] matrix,float r,float g,float b)
    {
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        glUniform4f(uColorLocation,r,g,b,1f);
    }
    public void setUniforms(float[] mvMatrix,
                            float[] it_mvMatrix,
                            float[] mvpMatrix,
                            float[] pointLightPositions,
                            float[] pointLightColors,
                            float[] vectorToLight) {
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniform4fv(uVectorToLight, 1, vectorToLight, 0);
        glUniform4fv(uPointLightPositionsLocation, 1, pointLightPositions, 0);
        glUniform3fv(uPointLightColorsLocation, 1 ,pointLightColors, 0);
    }
    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }

    public int getaPositionLocation()
    {
        return aPositionLocation;
    }


}
