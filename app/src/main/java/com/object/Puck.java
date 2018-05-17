package com.object;

import com.data.VertexArray;
import com.program.ColorShaderProgram;
import com.program.TextureShaderProgram;

import java.util.List;

/**
 * Created by txt on 2017/11/5.
 */

public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;
    private final VertexArray vertexArrayPosition;
    private final VertexArray vertexDataTextureCordinatesLocation;
    private final VertexArray vertexNormal;
    public final float[] vertexNormalArray;
    public final float[] vertexPositionArray;
    private final int offset;
    private final List<ObjectBuilder.DrawCommand> drawCommandList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(new Geometry.Cylinder(new Geometry.Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
        this.radius = radius;
        this.height = height;
        this.offset = generatedData.offsetPuck;
        vertexNormal = new VertexArray(generatedData.vertexNormal);
        vertexArrayPosition = new VertexArray(generatedData.vertexDataPosition);
        vertexDataTextureCordinatesLocation = new VertexArray(generatedData.vertexDataTextureCordinatesLocation);
        drawCommandList = generatedData.drawList;
        vertexNormalArray=generatedData.vertexNormal;
        vertexPositionArray=generatedData.vertexDataPosition;
    }

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArrayPosition.setVertexAttribPointer(offset, textureShaderProgram.getaPositionLocation(), POSITION_COMPONENT_COUNT, 0);
        vertexNormal.setVertexAttribPointer(offset,textureShaderProgram.getNormalAttributeLocation(),3,0);
        vertexDataTextureCordinatesLocation.setVertexAttribPointer(0, textureShaderProgram.getaTextureCoordinatesLocation(), 2, 0);
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArrayPosition.setVertexAttribPointer(0, colorShaderProgram.getaPositionLocation(), POSITION_COMPONENT_COUNT, 0);
        vertexNormal.setVertexAttribPointer(0,colorShaderProgram.getNormalAttributeLocation(),3,0);
    }

    public void draw1() {
        drawCommandList.get(0).draw();
    }

    public void draw2() {
        drawCommandList.get(1).draw();
    }
}
