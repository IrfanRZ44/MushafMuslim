package com.exomatik.zcodex.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelFaq(
    var question: String? = "",
    var answer: String? = ""
    ) : Parcelable