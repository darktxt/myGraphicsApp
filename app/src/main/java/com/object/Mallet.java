package com.object;

import com.data.VertexArray;
import com.program.ColorShaderProgram;

import java.util.List;

import static android.opengl.GLES10.GL_POINTS;
import static android.opengl.GLES10.glDrawArrays;
import static com.data.Constant.BYTES_PER_FLOAT;

/**
 * Created by txt on 2017/10/24.
 */

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius;
    public final float height;
    public final float[] vertexNormalArray;
    public final float[] vertexPositionArray;
    private final VertexArray vertexArray;
    private final VertexArray normalArray;
    private final List<ObjectBuilder.DrawCommand>drawCommandList;
    public Mallet(float radius,float height,int numPointsAroundMallet){
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(new Geometry.Point(0f,0f,0f),radius,height,numPointsAroundMallet);
        this.radius=radius;
        this.height=height;
        vertexArray=new VertexArray(generatedData.vertexDataPosition);
        normalArray=new VertexArray(generatedData.vertexNormal);
        drawCommandList=generatedData.drawList;
        vertexNormalArray=generatedData.vertexNormal;
        vertexPositionArray=generatedData.vertexDataPosition;
    }
    public void bendData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(0,colorShaderProgram.getaPositionLocation(),POSITION_COMPONENT_COUNT,0);
        normalArray.setVertexAttribPointer(0,colorShaderProgram.getNormalAttributeLocation(),3,0);
    }
    public void draw(){
        for(ObjectBuilder.DrawCommand drawCommand:drawCommandList){
            drawCommand.draw();
        }
    }
}