package com.hamz4k.bestposts.utils

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar


val Activity.rootView: ViewGroup
    get() = findViewById(android.R.id.content)

fun ViewGroup.inflate(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
): View = LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)


fun Activity.makeSnackBar(
    @StringRes msg: Int,
    view: View = rootView,
    duration: Int = Snackbar.LENGTH_LONG
) = Snackbar.make(view, msg, duration)
    .show()

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}