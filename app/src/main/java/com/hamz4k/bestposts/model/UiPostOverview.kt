package com.hamz4k.bestposts.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UiPostOverview(
    val userId: Int,
    val id: Int,
    val avatarUrl: String,
    val title: String,
    val body: String
) : Parcelable
