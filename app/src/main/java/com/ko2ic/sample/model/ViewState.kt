package com.ko2ic.sample.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ViewState(
    val page: Int = 0,
) : Parcelable
