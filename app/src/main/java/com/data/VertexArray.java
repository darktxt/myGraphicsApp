package com.data;

import com.object.Mallet;
import com.object.Puck;
import com.object.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static android.opengl.GLES10.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.data.Constant.BYTES_PER_FLOAT;

/**
 * Created by txt on 2017/10/23.
 */

public class VertexArray {
    public static float[] concat(float[] first, float[] second) {
        float[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer.allocateDirect(BYTES_PER_FLOAT * vertexData.length).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexData);
    }

    public VertexArray(float[] a, float[] b, float[] c, float[] d) {

        float[] data1 = concat(a, b);
        float[] data2 = concat(c, d);
        float[] data = concat(data1, data2);
        floatBuffer = ByteBuffer.allocateDirect(BYTES_PER_FLOAT * data.length).order(ByteOrder.nativeOrder()).asFloatBuffer().put(data);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }


}
