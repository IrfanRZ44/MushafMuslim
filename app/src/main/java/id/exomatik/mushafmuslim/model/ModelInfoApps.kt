package id.exomatik.mushafmuslim.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelInfoApps(
    var informasi: String? = "",
    var statusApps: String? = "",
    var versionApps: String? = "",
    var lastUpdated: String? = "",
    var totalRevenue: Long = 0,
    var totalAds: Long = 0,
    var timeMin: Long = 0,
    var timeMax: Long = 0,
    var data_faq: List<ModelFaq> = emptyList(),
    var data_youtube: List<ModelVideoYoutube> = emptyList()
    ) : Parcelable