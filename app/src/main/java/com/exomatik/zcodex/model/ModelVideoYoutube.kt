package com.exomatik.zcodex.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelVideoYoutube(
    var urlVideo: String? = "",
    var title: String? = ""
    ) : Parcelable