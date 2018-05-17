package com.program;

import android.content.Context;

import com.common.ShaderHelper;

import static android.opengl.GLES20.glUseProgram;
import static com.common.TextResourceReader.readTextFileFromResource;

/**
 * Created by txt on 2017/10/23.
 */

public class ShaderProgram {
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String A_POSITION = "a_Position";
    protected static final String U_COLOR= "u_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    protected static final String A_NORMAL = "a_Normal";

    protected static final String U_MV_MATRIX = "u_MVMatrix";
    protected static final String U_IT_MV_MATRIX = "u_IT_MVMatrix";
    protected static final String U_MVP_MATRIX = "u_MVPMatrix";
    protected static final String U_POINT_LIGHT_POSITIONS = "u_PointLightPositions";
    protected static final String U_POINT_LIGHT_COLORS = "u_PointLightColors";
    protected static final String U_VECTOR_TO_LIGHT = "u_VectorToLight";

    protected final int program;
    protected ShaderProgram(Context context,int vertexShaderResourceId,int fragmentShaderResourceId)
    {
        program = ShaderHelper.buildProgram(readTextFileFromResource(context,vertexShaderResourceId),readTextFileFromResource(context,fragmentShaderResourceId));
    }

    public void useProgram(){
        glUseProgram(program);
    }
}
