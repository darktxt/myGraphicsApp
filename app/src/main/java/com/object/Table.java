package com.object;

import com.data.VertexArray;
import com.program.LightProgram;
import com.program.TextureShaderProgram;

import static android.opengl.GLES10.GL_POLYGON_SMOOTH_HINT;
import static android.opengl.GLES10.GL_QUADRATIC_ATTENUATION;
import static android.opengl.GLES10.GL_TRIANGLE_FAN;
import static android.opengl.GLES10.glDrawArrays;
import static android.opengl.GLES32.GL_QUADS;
import static com.data.Constant.BYTES_PER_FLOAT;

/**
 * Created by txt on 2017/10/23.
 */

public class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COINT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT+TEXTURE_COORDINATES_COMPONENT_COINT) * BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
             0f,    0f,   0.5f, 0.5f,
            -0.5f, -0.8f, 0f,   0.9f,
             0.5f, -0.8f, 1f,   0.9f,
             0.5f,  0.8f, 1f,   0.1f,
            -0.5f,  0.8f, 0f,   0.1f,
            -0.5f, -0.8f, 0f,   0.9f
             /*-0.5f,-0.8f, 0.1f, 0.9f,
              0.5f,-0.8f, 0.9f, 0.9f,
              0.5f, 0.8f, 0.9f, 0.1f,
             -0.5f, 0.8f, 0.1f, 0.1f*/
    };
    private static final float[] normal = {0f,0f,1f,0f,0f,1f,0f,0f,1f,0f,0f,1f,0f,0f,1f,0f,0f,1f};
    private final VertexArray vertexArray;
    private final VertexArray normalArray;
    public final float[] vertexNormalArray = normal;
    public final float[] vertexPositionArray = {0f,0f,0f,
                                                -0.5f,-0.8f,0f,
                                                0.5f,-0.8f,0f,
                                                0.5f,0.8f,0f,
                                                -0.5f,0.8f,0f,
                                                -0.5f,-0.8f,0f};
    public Table()
    {
        vertexArray = new VertexArray(VERTEX_DATA);
        normalArray = new VertexArray(normal);
    }
    public void bindData(TextureShaderProgram textureShaderProgram)
    {
        normalArray.setVertexAttribPointer(0,textureShaderProgram.getNormalAttributeLocation(),3,0);
        vertexArray.setVertexAttribPointer(0,textureShaderProgram.getaPositionLocation(),POSITION_COMPONENT_COUNT,STRIDE);
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,textureShaderProgram.getaTextureCoordinatesLocation(),TEXTURE_COORDINATES_COMPONENT_COINT,STRIDE);
    }
    public void bindData(LightProgram lightProgram){
        vertexArray.setVertexAttribPointer(0,lightProgram.getaPositionLocation(),POSITION_COMPONENT_COUNT,STRIDE);
    }
    public void draw()
    {
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
    }
}
