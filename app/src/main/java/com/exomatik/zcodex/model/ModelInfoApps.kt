package com.exomatik.zcodex.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelInfoApps(
    var informasi: String? = "",
    var statusApps: String? = "",
    var versionApps: String? = "",
    var lastUpdated: String? = "",
    var totalRevenue: Long = 0,
    var totalAds: Long = 0
    ) : Parcelable