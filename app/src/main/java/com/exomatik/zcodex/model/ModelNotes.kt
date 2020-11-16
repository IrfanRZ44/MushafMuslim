package com.exomatik.zcodex.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelNotes(
    var idNotes: String = "",
    var title: String = "",
    var notes: String = "",
    var tglDibuat: String = "",
    var tglDiedit: String = "",
    var username: String = ""
    ) : Parcelable