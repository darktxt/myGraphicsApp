package com.txt.myopenglesproject;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    private myView view;
    private myRenderer myRenderer;
    private boolean RendererSet = false;
    boolean supportsEs2 = true;
    public static int mode = 0;
    public static int iseye = 0;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    public static Toast toast;
    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float NormalizedX = (detector.getFocusX() / view.getWidth()) * 2 - 1;
            float NormalizedY = 1 - (detector.getFocusY() / view.getHeight()) * 2;
            myRenderer.handleScale(detector.getScaleFactor(), NormalizedX, NormalizedY);
            /*toast.cancel();
            toast = Toast.makeText(MainActivity.this,"SCALE"+" "+detector.getScaleFactor(),Toast.LENGTH_SHORT);
            toast.show();*/
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    };
    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float NormalizedX2 = (e2.getX() / view.getWidth()) * 2 - 1;
            float NormalizedY2 = 1 - (e2.getY() / view.getHeight()) * 2;
            if (e1.getPointerCount() == 1)
                myRenderer.handleDrag(NormalizedX2, NormalizedY2, distanceX / view.getWidth(), distanceY / view.getHeight());
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            float NormalizedX = (e.getX() / view.getWidth()) * 2 - 1;
            float NormalizedY = 1 - (e.getY() / view.getHeight()) * 2;
            myRenderer.handlePress(NormalizedX, NormalizedY);
            return true;
        }
    };

    private class myView extends GLSurfaceView {
        myView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getPointerCount() == 2) {
                return scaleGestureDetector.onTouchEvent(event);
            } else
                return gestureDetector.onTouchEvent(event);
        }

    }

    //    private View.OnClickListener onUpClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v){
//            myRenderer.handleUp(mode);
//        }
//    };
//    private View.OnClickListener onDownClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v){
//            myRenderer.handleDown(mode);
//        }
//    };
//    private View.OnClickListener onLeftClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v){
//            myRenderer.handleLeft(mode);
//        }
//    };
//    private View.OnClickListener onRightClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v){
//            myRenderer.handleRight(mode);
//        }
//    };
//    private View.OnClickListener onFrontClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v){
//            myRenderer.handleFront(mode);
//        }
//    };
//    private View.OnClickListener onBackClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v){
//            myRenderer.handleBack(mode);
//        }
//    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.mainlayout);
//        Button up = (Button)findViewById(R.id.buttonup);
//        up.setOnClickListener(onUpClickListener);
//
//        Button down = (Button)findViewById(R.id.buttondown);
//        down.setOnClickListener(onDownClickListener);
//
//        Button left = (Button)findViewById(R.id.buttonleft);
//        left.setOnClickListener(onLeftClickListener);
//
//        Button right = (Button)findViewById(R.id.buttonright);
//        right.setOnClickListener(onRightClickListener);
//
//        Button front =(Button)findViewById(R.id.buttonfront);
//        front.setOnClickListener(onFrontClickListener);
//
//        Button back = (Button)findViewById(R.id.buttonback);
//        back.setOnClickListener(onBackClickListener);
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleButton.setChecked(isChecked);
                if (isChecked)
                    mode = 1;
                else
                    mode = 0;

            }
        });
        final ToggleButton toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleButton2.setChecked(isChecked);
                if (isChecked)
                    iseye = 1;
                else
                    iseye = 0;

            }
        });
        view = new myView(this);
        myRenderer = new myRenderer(this);
        if (supportsEs2) {
            view.setEGLContextClientVersion(2);
            view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            view.setRenderer(myRenderer);
            RendererSet = true;
            gestureDetector = new GestureDetector(this, onGestureListener);
            scaleGestureDetector = new ScaleGestureDetector(this, onScaleGestureListener);
            toast = Toast.makeText(MainActivity.this, "Hello!", Toast.LENGTH_SHORT);
            toast.show();
            /*View.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event != null)
                    {
                        return  gestureDetector.onTouchEvent(event);
                    }
                    else
                        return false;
                }
            });*/
            mainLayout.addView(view);
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }
//        up.bringToFront();
//        down.bringToFront();
//        left.bringToFront();
//        right.bringToFront();
//        front.bringToFront();
//        back.bringToFront();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (RendererSet)
            view.onPause();
        toast.cancel();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (RendererSet)
            view.onResume();
    }
}

