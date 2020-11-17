package com.exomatik.zcodex.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelUser(
    var nama: String = "",
    var noHp: String = "",
    var token: String = "",
    var jenisAkun: String = "",
    var username: String = "",
    var urlFoto: String = "",
    var lastSignIn: String = "",
    var totalPoin: Long = 0,
    var active: String = ""
    ) : Parcelable