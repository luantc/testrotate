package com.example.myapplication

import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RotationGestureDetector.OnRotationGestureListener, View.OnTouchListener {
    var rotationGestureDetector: RotationGestureDetector? = null
    var dX: Float = 0.toFloat()
    var dY:Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myView.setOnTouchListener(this)
        rotationGestureDetector = RotationGestureDetector(this, myView)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

           return rotationGestureDetector?.onTouchEvent(event) ?: true
    }



    override fun onRotation(rotationDetector: RotationGestureDetector) {
        myView.rotation = rotationDetector.getAngle()
    }
}
