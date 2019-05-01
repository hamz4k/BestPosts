package com.hamz4k.bestposts.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.annotation.LayoutRes


val Activity.rootView: ViewGroup
    get() = findViewById(android.R.id.content)

fun ViewGroup.inflate(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
): View = LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun ImageView.growShrink() {
    val expansionFactor: Float = 0.2F
    val growX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f + expansionFactor)
    val growY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f + expansionFactor)
    val growAnimation = ObjectAnimator.ofPropertyValuesHolder(this, growX, growY)
    growAnimation.interpolator = OvershootInterpolator()

    val shrinkX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
    val shrinkY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
    val shrinkAnimation = ObjectAnimator.ofPropertyValuesHolder(this, shrinkX, shrinkY)
    shrinkAnimation.interpolator = OvershootInterpolator()

    val animSetXY = AnimatorSet()
    animSetXY.playSequentially(growAnimation, shrinkAnimation)
    animSetXY.start()
}