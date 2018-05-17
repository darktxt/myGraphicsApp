package com.object;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES10.glDrawArrays;
import static javax.microedition.khronos.opengles.GL10.GL_LINE_STRIP;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_FAN;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_STRIP;

/**
 * Created by txt on 2017/11/5.
 */

public class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexDataPosition;
    private final float[] vertexDataTextureCordinatesLocation;
    private final float[] vertexNormal;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;
    private int offsetTexture = 0;
    private final boolean hasTexture;
    private final int offsetPuck;

    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexDataPosition;
        final float[] vertexNormal;
        final float[] vertexDataTextureCordinatesLocation;
        final int offsetPuck;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexDataPosition, float[] vertexDataTextureCordinatesLocation,float[] vertexNormal, int offsetPuck, List<DrawCommand> drawList) {
            this.drawList = drawList;
            this.vertexDataPosition = vertexDataPosition;
            this.vertexDataTextureCordinatesLocation = vertexDataTextureCordinatesLocation;
            this.offsetPuck = offsetPuck;
            this.vertexNormal = vertexNormal;
        }
    }

    private GeneratedData build() {
        return new GeneratedData(vertexDataPosition, vertexDataTextureCordinatesLocation, vertexNormal,offsetPuck, drawList);
    }

    private ObjectBuilder(int sizeInVertices, boolean hasTexture, int offsetPuck) {
        vertexDataPosition = new float[sizeInVertices * FLOATS_PER_VERTEX];
        this.offsetPuck = offsetPuck;
        this.hasTexture = hasTexture;
        this.vertexNormal = new float[sizeInVertices * FLOATS_PER_VERTEX];
        if (hasTexture)
            vertexDataTextureCordinatesLocation = new float[sizeInVertices * 2];
        else
            vertexDataTextureCordinatesLocation = null;
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        return 2 + numPoints;
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (1 + numPoints) * 2;
    }

    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size, true, sizeOfOpenCylinderInVertices(numPoints) * 3);
        Geometry.Circle puckTop = new Geometry.Circle(puck.center.TranslateY(puck.height / 2f), puck.radius);
        builder.appendOpenCylinder(puck, numPoints);
        builder.appendCircle(puckTop, numPoints);
        return builder.build();
    }

    static GeneratedData createMallet(Geometry.Point center, float radius, float height, int numPoints) {
        int size = (sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)) * 2;
        ObjectBuilder builder = new ObjectBuilder(size, false, 0);
        float baseHeight = height * 0.25f;
        Geometry.Circle baseCircle = new Geometry.Circle(center.TranslateY(-baseHeight), radius);
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(baseCircle.center.TranslateY(-baseHeight / 2f), radius, baseHeight);
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;
        Geometry.Circle handleCircle = new Geometry.Circle(center.TranslateY(height * 0.5f), handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(handleCircle.center.TranslateY(-handleHeight / 2f), handleRadius, handleHeight);
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);
        return builder.build();
    }

    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        vertexNormal[offset]=0f;
        vertexDataPosition[offset++] = circle.center.x;
        vertexNormal[offset]=1f;
        vertexDataPosition[offset++] = circle.center.y;
        vertexNormal[offset]=0f;
        vertexDataPosition[offset++] = circle.center.z;

        if (hasTexture) {
            vertexDataTextureCordinatesLocation[offsetTexture++] = 0.5f;
            vertexDataTextureCordinatesLocation[offsetTexture++] = 0.5f;
        }
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            vertexNormal[offset]=0f;
            vertexDataPosition[offset++] = circle.center.x + circle.radius * (float) Math.cos(angleInRadians);
            vertexNormal[offset]=1f;
            vertexDataPosition[offset++] = circle.center.y;
            vertexNormal[offset]=0f;
            vertexDataPosition[offset++] = circle.center.z + circle.radius * (float) Math.sin(angleInRadians);

            if (hasTexture) {
                vertexDataTextureCordinatesLocation[offsetTexture++] = 0.5f + 0.5f * (float) Math.cos(angleInRadians);
                vertexDataTextureCordinatesLocation[offsetTexture++] = 0.5f + 0.5f * (float) Math.sin(angleInRadians);
            }
        }
        if (!hasTexture) {
            drawList.add(new DrawCommand() {
                @Override
                public void draw() {

                    glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
                }
            });
        } else
            drawList.add(new DrawCommand() {
                @Override
                public void draw() {

                    glDrawArrays(GL_TRIANGLE_FAN, 0, numVertices);
                }
            });
    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - cylinder.height / 2f;
        final float yEnd = cylinder.center.y + cylinder.height / 2f;
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            float xPosition = cylinder.center.x + cylinder.radius * (float) Math.cos(angleInRadians);
            float zPosition = cylinder.center.z + cylinder.radius * (float) Math.sin(angleInRadians);
            vertexNormal[offset]=(float) Math.cos(angleInRadians);
            vertexDataPosition[offset++] = xPosition;
            vertexNormal[offset]=0f;
            vertexDataPosition[offset++] = yStart;
            vertexNormal[offset]=(float) Math.sin(angleInRadians);
            vertexDataPosition[offset++] = zPosition;
            vertexNormal[offset]=(float) Math.cos(angleInRadians);
            vertexDataPosition[offset++] = xPosition;
            vertexNormal[offset]=0f;
            vertexDataPosition[offset++] = yEnd;
            vertexNormal[offset]=(float) Math.sin(angleInRadians);
            vertexDataPosition[offset++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });

    }
}
