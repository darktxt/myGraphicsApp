package com.txt.myopenglesproject;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.common.TextureHelper;
import com.data.VertexArray;
import com.object.Geometry;
import com.object.Geometry.Point;
import com.object.Mallet;
import com.object.Puck;
import com.object.Skybox;
import com.object.Table;
import com.program.ColorShaderProgram;
import com.program.LightProgram;
import com.program.SkyboxShaderProgram;
import com.program.TextureShaderProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_ALWAYS;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.transposeM;
import static com.object.Geometry.intersectionPoint;
import static com.object.Geometry.intersects;
import static com.object.Geometry.reflex;
import static com.object.Geometry.vectorBetween;
import static com.txt.myopenglesproject.MainActivity.iseye;
import static com.txt.myopenglesproject.MainActivity.mode;

/**
 * Created by txt on 2017/10/23.
 */

public class myRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] it_modelViewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] invertViewProjectionMatrix = new float[16];
    private Table table;
    private Puck puck;
    private Mallet mallet;
    private Skybox skybox;
    private SkyboxShaderProgram skyboxProgram;
    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;
    //private LightProgram lightProgram;
    private int skyboxTexture;
    private int texture;
    private int texturePuck;
    private boolean bluemalletPressed = false;
    private boolean whitemalletPressed = false;
    private Point blueMalletPosition;
    private Point previousBlueMalletPosition;
    private Point previousWhiteMalletPosition;
    private Point whiteMalletPosition;
    private Geometry.Vector2 puckRotateVector;
    private float puckRotateValue = 0f;
    private float puckRotateChange = 30f;
    private final float leftbound = -0.5f;
    private final float rightbound = 0.5f;
    private final float farbound = -0.8f;
    private final float nearbound = 0.8f;
    private Point puckPosition;
    private Geometry.Vector puckVector;
    private float[] eye = {0f, 1f, 2f};
    private float[] center = {0f, 0f, 0f};
    private float[] pointLightPositions = {0.01f, 2f, 0f, 1f};
    private float[] pointPositionsInEyeSpace = new float[4];
    private float[] pointLightColors = {0.3f, 0.3f, 0.3f};
    private float light = 1f;
    private float angleX = 3f * (float) Math.PI / 2f;
    private float angleY = (float) Math.atan(2);
    private float distance;
    private final float[] vectorToLightBlue = new float[4];
    private final float[] vectorToLightWhite = new float[4];
    private final float[] vectorToLightTable = new float[4];
    private final float[] vectorToLightPuck = new float[4];
    private final float[] vectorToLightBlueView = new float[4];
    private final float[] vectorToLightWhiteView = new float[4];
    private final float[] vectorToLightTableView = new float[4];
    private final float[] vectorToLightPuckView = new float[4];

    public myRenderer(Context context) {
        this.context = context;
    }

    private void positionTableInScene() {
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        updateMvpMatrix();
    }

    private void positionObjectInScene(float x, float y, float z, float rotate) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        if (rotate != 0f)
            rotateM(modelMatrix, 0, rotate, 0f, 1f, 0f);
        updateMvpMatrix();
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        skybox = new Skybox();
        table = new Table();
        puck = new Puck(0.06f, 0.03f, 32);
        mallet = new Mallet(0.06f, 0.12f, 32);
        skyboxProgram = new SkyboxShaderProgram(context);
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        //lightProgram = new LightProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        texturePuck = TextureHelper.loadTexture(context, R.drawable.puck);
        skyboxTexture = TextureHelper.loadCubeMap(context,
                new int[]{R.drawable.left, R.drawable.right,
                        R.drawable.bottom, R.drawable.top,
                        R.drawable.front, R.drawable.back});
        blueMalletPosition = new Point(0f, mallet.height / 2f, 0.4f);
        whiteMalletPosition = new Point(0f, mallet.height / 2f, -0.4f);
        puckPosition = new Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);
        puckRotateVector = new Geometry.Vector2(0f, 0f);
        distance = getDistance();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0);
        invertM(invertViewProjectionMatrix, 0, viewProjectionMatrix, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glDepthFunc(GL_ALWAYS);
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix, pointPositionsInEyeSpace, pointLightColors);
        skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
        glDepthFunc(GL_LESS);

        vectorToLightTable[0]=-pointLightPositions[0];
        vectorToLightTable[1]=-pointLightPositions[1];
        vectorToLightTable[2]=-pointLightPositions[2];
        multiplyMV(vectorToLightTableView,0,viewMatrix,0,vectorToLightTable,0);
        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(modelViewProjectionMatrix, texture);
        textureShaderProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix, pointPositionsInEyeSpace, pointLightColors,vectorToLightTableView);
        table.bindData(textureShaderProgram);
        table.draw();

        vectorToLightWhite[0]=whiteMalletPosition.x-pointLightPositions[0];
        vectorToLightWhite[1]=whiteMalletPosition.y-pointLightPositions[1];
        vectorToLightWhite[2]=whiteMalletPosition.z-pointLightPositions[2];
        vectorToLightWhite[3]=0f;
        multiplyMV(vectorToLightWhiteView,0,viewMatrix,0,vectorToLightWhite,0);
        positionObjectInScene(whiteMalletPosition.x, whiteMalletPosition.y, whiteMalletPosition.z, 0f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 1f, 1f);
        colorShaderProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix, pointPositionsInEyeSpace, pointLightColors,vectorToLightWhiteView);
        mallet.bendData(colorShaderProgram);
        mallet.draw();

        vectorToLightBlue[0]=blueMalletPosition.x-pointLightPositions[0];
        vectorToLightBlue[1]=blueMalletPosition.y-pointLightPositions[1];
        vectorToLightBlue[2]=blueMalletPosition.z-pointLightPositions[2];
        vectorToLightBlue[3]=0f;
        multiplyMV(vectorToLightBlueView,0,viewMatrix,0,vectorToLightBlue,0);
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z, 0f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        colorShaderProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix, pointPositionsInEyeSpace, pointLightColors,vectorToLightBlueView);
        mallet.bendData(colorShaderProgram);
        mallet.draw();


        Geometry.Vector temp;
        if (puckPosition.x < leftbound + puck.radius
                || puckPosition.x > rightbound - puck.radius) {
            temp = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            if (temp.dotProduct(puckVector) < 0)
                puckRotateChange = -puckVector.add(temp.scale(-1)).length() * 200;
            else
                puckRotateChange = puckVector.add(temp.scale(-1)).length() * 200;
            puckVector = temp;
            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z < farbound + puck.radius
                || puckPosition.z > nearbound - puck.radius) {
            temp = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            if (temp.dotProduct(puckVector) < 0)
                puckRotateChange = -puckVector.add(temp.scale(-1)).length() * 200;
            else
                puckRotateChange = puckVector.add(temp.scale(-1)).length() * 200;
            puckVector = temp;
            puckVector = puckVector.scale(0.9f);
        }
        puckPosition = new Point(
                clamp(puckPosition.x, leftbound + puck.radius, rightbound - puck.radius),
                puck.height / 2f,
                clamp(puckPosition.z, farbound + puck.radius, nearbound - puck.radius)
        );
        Point judgePosition = new Point(blueMalletPosition.x, puck.height / 2f, blueMalletPosition.z);
        Geometry.Vector line = vectorBetween(puckPosition, judgePosition);
        if (line.length() < (puck.radius + mallet.radius)) {
            while (line.length() < (puck.radius + mallet.radius)) {
                puckPosition = puckPosition.translate(puckVector.scale(-1));
                line = vectorBetween(puckPosition, judgePosition);
            }
            temp = reflex(puckVector, line);
            if (temp.dotProduct(puckVector) < 0)
                puckRotateChange = -puckVector.add(temp.scale(-1)).length() * 200;
            else
                puckRotateChange = puckVector.add(temp.scale(-1)).length() * 200;
            puckVector = temp;
        }
        judgePosition = new Point(whiteMalletPosition.x, puck.height / 2f, whiteMalletPosition.z);
        line = vectorBetween(puckPosition, judgePosition);
        if (line.length() < (puck.radius + mallet.radius)) {
            while (line.length() < (puck.radius + mallet.radius)) {
                puckPosition = puckPosition.translate(puckVector.scale(-1));
                line = vectorBetween(puckPosition, judgePosition);
            }
            temp = reflex(puckVector, line);
            if (temp.dotProduct(puckVector) < 0)
                puckRotateChange = -puckVector.add(temp.scale(-1)).length() * 200;
            else
                puckRotateChange = puckVector.add(temp.scale(-1)).length() * 200;
            puckVector = temp;
        }


        vectorToLightPuck[0]=puckPosition.x-pointLightPositions[0];
        vectorToLightPuck[1]=puckPosition.y-pointLightPositions[1];
        vectorToLightPuck[2]=puckPosition.z-pointLightPositions[2];
        vectorToLightPuck[3]=0f;
        multiplyMV(vectorToLightPuckView,0,viewMatrix,0,vectorToLightPuck,0);
        puckRotateValue += puckRotateChange;
        puckRotateChange *= 0.99f;
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z, puckRotateValue);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 1f, 1f);
        colorShaderProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix, pointPositionsInEyeSpace, pointLightColors,vectorToLightPuckView);
        puck.bindData(colorShaderProgram);
        puck.draw1();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(modelViewProjectionMatrix, texturePuck);
        textureShaderProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix, pointPositionsInEyeSpace, pointLightColors,vectorToLightPuckView);
        puck.bindData(textureShaderProgram);
        puck.draw2();
        puckPosition = puckPosition.translate(puckVector);
        puckVector.scale(0.9f);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        glViewport(0, 0, width, height);
        perspectiveM(projectionMatrix, 0, 45, (float) width / (float) height, 0.1f, 100f);
        //setIdentityM(projectionMatrix,0);
        setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }


    private void divideByW(float[] v) {
        v[0] /= v[3];
        v[1] /= v[3];
        v[2] /= v[3];
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {

        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(nearPointWorld, 0, invertViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertViewProjectionMatrix, 0, farPointNdc, 0);
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);

        Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, vectorBetween(nearPointRay, farPointRay));
    }

    public void handlePress(float normalizedX, float normalizedY) {

        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), mallet.height);
        bluemalletPressed = Geometry.intersects(malletBoundingSphere, ray);
        malletBoundingSphere = new Geometry.Sphere(new Point(whiteMalletPosition.x, whiteMalletPosition.y, whiteMalletPosition.z), mallet.height);
        whitemalletPressed = intersects(malletBoundingSphere, ray);

    }

    public void handleDrag(float normalizedX, float normalizedY, float distanceX, float distanceY) {
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        Geometry.Plane plane = new Geometry.Plane(new Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
        Point touchedPoint = intersectionPoint(ray, plane);
        Point touchedCorrectedPoint = new Point(clamp(touchedPoint.x, leftbound + mallet.radius, rightbound - mallet.radius), mallet.height / 2f, clamp(touchedPoint.z, 0f + mallet.radius, nearbound - mallet.radius));
        if (bluemalletPressed) {
            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = touchedCorrectedPoint;
            Point judgePosition = new Point(blueMalletPosition.x, puck.height / 2f, blueMalletPosition.z);
            Geometry.Vector v = vectorBetween(judgePosition, puckPosition);
            float distance = v.length();
            if (distance < (puck.radius + mallet.radius)) {
                puckVector = vectorBetween(previousBlueMalletPosition, blueMalletPosition);
                if (v.dotProduct(puckVector) < 0)
                    puckVector = puckVector.scale(-1);
            }

        } else if (whitemalletPressed) {
            previousWhiteMalletPosition = whiteMalletPosition;
            whiteMalletPosition = new Point(clamp(touchedPoint.x, leftbound + mallet.radius, rightbound - mallet.radius), mallet.height / 2f, clamp(touchedPoint.z, farbound + mallet.radius, 0f - mallet.radius));
            Point judgePosition = new Point(whiteMalletPosition.x, puck.height / 2f, whiteMalletPosition.z);
            Geometry.Vector v = vectorBetween(judgePosition, puckPosition);
            float distance = v.length();
            if (distance < (puck.radius + mallet.radius)) {
                puckVector = vectorBetween(previousWhiteMalletPosition, whiteMalletPosition);
                if (v.dotProduct(puckVector) < 0)
                    puckVector = puckVector.scale(-1);
            }

        } else {
            if (mode == 0) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    if (iseye == 0)
                        angleX -= distanceX;
                    else if (iseye == 1)
                        angleX -= distanceX * 2;
                    while (angleX >= 2 * Math.PI)
                        angleX -= 2 * Math.PI;
                    while (angleX < 0)
                        angleX += 2 * Math.PI;
                } else {
                    if (iseye == 0)
                        angleY += distanceY;
                    else if (iseye == 1)
                        angleY += distanceY * 2;
                    while (angleY >= 2 * Math.PI)
                        angleY -= 2 * Math.PI;
                    while (angleY < 0)
                        angleY += 2 * Math.PI;
                }
                if (iseye == 0) {
                    center[0] = eye[0] + (float) Math.cos(angleX) * (float) Math.sin(angleY) * distance;
                    center[2] = eye[2] + (float) Math.sin(angleX) * (float) Math.sin(angleY) * distance;
                    center[1] = eye[1] - (float) Math.cos(angleY) * distance;
                } else if (iseye == 1) {
                    Geometry.Vector v = vectorBetween(new Point(eye[0], eye[1], eye[2]), new Point(center[0], center[1], center[2]));
                    Geometry.Ray r = new Geometry.Ray(new Point(eye[0], eye[1], eye[2]), v);
                    Point newCenter = intersectionPoint(r, new Geometry.Plane(new Point(0f, 0f, 0f), new Geometry.Vector(0f, 1f, 0f)));
                    center[0] = newCenter.x;
                    center[1] = newCenter.y;
                    center[2] = newCenter.z;
                    distance = getDistance();
                    eye[0] = center[0] - (float) Math.cos(angleX) * (float) Math.sin(angleY) * distance;
                    eye[2] = center[2] - (float) Math.sin(angleX) * (float) Math.sin(angleY) * distance;
                    eye[1] = center[1] + (float) Math.cos(angleY) * distance;
                }

                setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
                multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            } else if (mode == 1) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    if (angleX > Math.PI)
                        pointLightPositions[0] -= (distanceX);
                    else
                        pointLightPositions[0] += (distanceX);
                    // pointLightPositions[1] += ray.vector.y * 0.01 * (distanceX);
                    // pointLightPositions[2] +=  (distanceX);
                } else {
                    // pointLightPositions[0] += ray.vector.x  * (distanceY);
                    // pointLightPositions[1] += ray.vector.y * 0.01 * (distanceY);
                    if (angleX > Math.PI)
                        pointLightPositions[2] -= (distanceY);
                    else
                        pointLightPositions[2] += (distanceY);
                }
            }
        }
    }

    public void handleScale(float scaleI, float x, float y) {
        if (mode == 0) {
            Geometry.Ray ray = convertNormalized2DPointToRay(x, y);
            eye[0] += ray.vector.x * 0.01 * (scaleI - 1);
            eye[1] += ray.vector.y * 0.01 * (scaleI - 1);
            eye[2] += ray.vector.z * 0.01 * (scaleI - 1);
            distance = getDistance();
            center[0] = eye[0] + (float) Math.cos(angleX) * (float) Math.sin(angleY) * distance;
            center[2] = eye[2] + (float) Math.sin(angleX) * (float) Math.sin(angleY) * distance;
            center[1] = eye[1] - (float) Math.cos(angleY) * distance;
            setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
            multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        } else if (mode == 1) {
            light *= scaleI;
            pointLightColors[0] = light * 0.5f;
            pointLightColors[1] = light * 0.5f;
            pointLightColors[2] = light * 0.5f;
        }
    }


    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

//
//    public void handleUp(int mode) {
//        if (mode == 0) {
//            eye[1] += 0.3f;
//            center[1] += 0.3f;
//            center[0] = eye[0] + (float) Math.cos(angleX) * (float) Math.sin(angleY);
//            center[2] = eye[2] + (float) Math.sin(angleX) * (float) Math.sin(angleY);
//            center[1] = eye[1] - (float) Math.cos(angleY);
//            setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
//            multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//        } else
//            pointLightPositions[1] += 0.3f;
//    }
//
//    public void handleDown(int mode) {
//        if (mode == 0) {
//
//            eye[1] -= 0.3f;
//            center[1] -= 0.3f;
//            center[0] = eye[0] + (float) Math.cos(angleX) * (float) Math.sin(angleY);
//            center[2] = eye[2] + (float) Math.sin(angleX) * (float) Math.sin(angleY);
//            center[1] = eye[1] - (float) Math.cos(angleY);
//            setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
//            multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//        } else
//            pointLightPositions[1] -= 0.3f;
//    }
//
//    public void handleLeft(int mode) {
//        if (mode == 0) {
//            eye[0] -= 0.3f;
//            center[0] -= 0.3f;
//            center[0] = eye[0] + (float) Math.cos(angleX) * (float) Math.sin(angleY);
//            center[2] = eye[2] + (float) Math.sin(angleX) * (float) Math.sin(angleY);
//            center[1] = eye[1] - (float) Math.cos(angleY);
//            setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
//            multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//        } else
//            pointLightPositions[0] -= 0.3f;
//    }
//
//
//    public void handleRight(int mode) {
//        if (mode == 0) {
//            eye[0] += 0.3f;
//            center[0] += 0.3f;
//            center[0] = eye[0] + (float) Math.cos(angleX) * (float) Math.sin(angleY);
//            center[2] = eye[2] + (float) Math.sin(angleX) * (float) Math.sin(angleY);
//            center[1] = eye[1] - (float) Math.cos(angleY);
//            setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
//            multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//        } else
//            pointLightPositions[0] += 0.3f;
//    }
//
//    public void handleFront(int mode) {
//        if (mode == 0) {
//            eye[2] -= 0.3f;
//            center[2] -= 0.3f;
//            center[0] = eye[0] + (float) Math.cos(angleX) * (float) Math.sin(angleY);
//            center[2] = eye[2] + (float) Math.sin(angleX) * (float) Math.sin(angleY);
//            center[1] = eye[1] - (float) Math.cos(angleY);
//            setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
//            multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//        } else
//            pointLightPositions[2] -= 0.3f;
//    }
//
//    public void handleBack(int mode) {
//        if (mode == 0) {
//            eye[2] += 0.3f;
//            center[2] += 0.3f;
//            center[0] = eye[0] + (float) Math.cos(angleX) * (float) Math.sin(angleY);
//            center[2] = eye[2] + (float) Math.sin(angleX) * (float) Math.sin(angleY);
//            center[1] = eye[1] - (float) Math.cos(angleY);
//            setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], 0f, 1f, 0f);
//            multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//        } else if (mode == 1)
//
//            pointLightPositions[2] += 0.3f;
//
//    }

    private void updateMvpMatrix() {
        float[] tempMatrix = new float[16];
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
        multiplyMM(
                modelViewProjectionMatrix, 0,
                projectionMatrix, 0,
                modelViewMatrix, 0);
    }

    private void updateVectorToLight() {

        vectorToLightBlue[0]=blueMalletPosition.x-pointLightPositions[0];
        vectorToLightBlue[1]=blueMalletPosition.y-pointLightPositions[1];
        vectorToLightBlue[2]=blueMalletPosition.z-pointLightPositions[2];
        vectorToLightBlue[3]=0f;
        multiplyMV(vectorToLightBlueView,0,viewMatrix,0,vectorToLightBlue,0);
        vectorToLightWhite[0]=whiteMalletPosition.x-pointLightPositions[0];
        vectorToLightWhite[1]=whiteMalletPosition.y-pointLightPositions[1];
        vectorToLightWhite[2]=whiteMalletPosition.z-pointLightPositions[2];
        vectorToLightWhite[3]=0f;
        multiplyMV(vectorToLightWhiteView,0,viewMatrix,0,vectorToLightWhite,0);
        vectorToLightPuck[0]=puckPosition.x-pointLightPositions[0];
        vectorToLightPuck[1]=puckPosition.y-pointLightPositions[1];
        vectorToLightPuck[2]=puckPosition.z-pointLightPositions[2];
        vectorToLightPuck[3]=0f;
        multiplyMV(vectorToLightPuckView,0,viewMatrix,0,vectorToLightPuck,0);

    }

    private float getDistance() {
        float sum = 0;
        for (int i = 0; i < 3; i++) {
            sum += Math.pow((eye[i] - center[i]), 2);
        }
        return (float) Math.sqrt(sum);
    }
}
