package com.example.myapplication

import android.R.attr.x
import android.R.attr.y
import android.graphics.Matrix
import android.graphics.PointF
import android.support.v4.view.ViewCompat.getRotation
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class RotationGestureDetector(val mListener: OnRotationGestureListener?, val mView: View) {
    private val INVALID_POINTER_ID = -1
    private val mFPoint = PointF()
    private val mSPoint = PointF()
    private var mPtrID1: Int = INVALID_POINTER_ID
    private var mPtrID2: Int = INVALID_POINTER_ID
    private var mAngle: Float = 0.toFloat()
    private val TAG = "RotationGestureDetector"
    var dX: Float = 0.toFloat()
    var dY: Float = 0.toFloat()
    var  pointerUpViewX = 0
    var pointerUpViewY = 0
    fun getAngle(): Float {
        return mAngle
    }


    fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.actionMasked) {
            MotionEvent.ACTION_OUTSIDE -> Log.d(TAG, "ACTION_OUTSIDE")
            MotionEvent.ACTION_DOWN -> {
                Log.v(TAG, "ACTION_DOWN")
                mPtrID1 = event.getPointerId(event.actionIndex)
                dX = mView.x - event.rawX
                dY = mView.y - event.rawY
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.v(TAG, "ACTION_POINTER_DOWN")
                mPtrID2 = event.getPointerId(event.actionIndex)

                getRawPoint(event, mPtrID1, mSPoint)
                getRawPoint(event, mPtrID2, mFPoint)
            }
            MotionEvent.ACTION_MOVE -> {
                try {
                    var isOneFingerTouch = true

                    if (mPtrID1 != INVALID_POINTER_ID && mPtrID2 != INVALID_POINTER_ID) {
                        isOneFingerTouch = false
                        val nfPoint = PointF()
                        val nsPoint = PointF()

                        getRawPoint(event, mPtrID1, nsPoint)
                        getRawPoint(event, mPtrID2, nfPoint)

                        mAngle = angleBetweenLines(mFPoint, mSPoint, nfPoint, nsPoint)

                        if (Math.abs(mAngle) > 0)
                            mListener?.onRotation(this)
                    } else {
                        val mat =  mView.matrix
                        mat.postRotate(mView.rotation)
                        val point = FloatArray(2)
                        mat.mapPoints(point)
                        val intArr = IntArray(2)
                        mView.getLocationOnScreen(intArr)
                        if(mView.width - event.rawX < 50){

                            val params = mView.layoutParams
                            params.width += 100
                            mView.layoutParams = params


                        }else {
                            if (pointerUpViewX == 0 && pointerUpViewY == 0) {
                                mView.animate()
                                    .x(event.rawX + dX)
                                    .y(event.rawY + dY)
                                    .setDuration(0)
                                    .start()
                            } else {
                                mView.animate()
                                    .x(pointerUpViewX.toFloat())
                                    .y(pointerUpViewY.toFloat())
                                    .setDuration(0)
                                    .start()

                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            MotionEvent.ACTION_UP -> {
                mPtrID1 = INVALID_POINTER_ID
                pointerUpViewX = 0
                pointerUpViewY = 0
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mPtrID2 = INVALID_POINTER_ID
                pointerUpViewX = mView.x.toInt()
                pointerUpViewY = mView.y.toInt()

            }
            MotionEvent.ACTION_CANCEL -> {
                mPtrID1 = INVALID_POINTER_ID
                mPtrID2 = INVALID_POINTER_ID
            }
            else -> {
            }

        }
        return true
    }

    fun getRawPoint(ev: MotionEvent, index: Int, point: PointF) {
        val location = intArrayOf(0, 0)
        mView.getLocationOnScreen(location)

        var x = ev.getX(index)
        var y = ev.getY(index)

        var angle = Math.toDegrees(Math.atan2(y.toDouble(), x.toDouble()))
        angle += mView.getRotation()

        val length = PointF.length(x, y)

        x = (length * Math.cos(Math.toRadians(angle))).toFloat() + location[0]
        y = (length * Math.sin(Math.toRadians(angle))).toFloat() + location[1]

        point.set(x, y)
    }

    private fun angleBetweenLines(fPoint: PointF, sPoint: PointF, nFpoint: PointF, nSpoint: PointF): Float {
        val angle1 = Math.atan2((fPoint.y - sPoint.y).toDouble(), (fPoint.x - sPoint.x).toDouble()).toFloat()
        val angle2 = Math.atan2((nFpoint.y - nSpoint.y).toDouble(), (nFpoint.x - nSpoint.x).toDouble()).toFloat()

        var angle = Math.toDegrees((angle1 - angle2).toDouble()).toFloat() % 360
        if (angle < -180f) angle += 360.0f
        if (angle > 180f) angle -= 360.0f
        return -angle
    }

    interface OnRotationGestureListener {
        fun onRotation(rotationDetector: RotationGestureDetector)
    }
}