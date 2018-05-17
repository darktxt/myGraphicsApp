package com.program;

import android.content.Context;

import com.common.TextResourceReader;
import com.txt.myopenglesproject.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by txt on 2017/10/23.
 */

public class TextureShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int uMVMatrixLocation;
    private final int uIT_MVMatrixLocation;
    private final int uMVPMatrixLocation;
    private final int uPointLightPositionsLocation;
    private final int uPointLightColorsLocation;

    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;
    private final int aNormalLocation;
    private final int uVectorToLight;

    public TextureShaderProgram(Context context)
    {
        super(context, R.raw.texture_vertex_shader,R.raw.texture_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program,U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program,A_TEXTURE_COORDINATES);
        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);

        uPointLightPositionsLocation =
                glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS);
        uPointLightColorsLocation =
                glGetUniformLocation(program, U_POINT_LIGHT_COLORS);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        uVectorToLight = glGetUniformLocation(program,U_VECTOR_TO_LIGHT);
    }

    public void setUniform(float[] matrix,int textureId){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureId);
        glUniform1i(uTextureUnitLocation,0);
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

    public int getaPositionLocation()
    {
        return aPositionLocation;
    }

    public int getaTextureCoordinatesLocation()
    {
        return aTextureCoordinatesLocation;
    }
    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }
}
