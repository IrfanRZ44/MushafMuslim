package id.exomatik.mushafmuslim.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelInfoApps(
    var informasi: String? = "",
    var statusApps: String? = "",
    var versionApps: String? = "",
    var hargaPoin: Long = 0,
    var data_faq: ArrayList<String>? = ArrayList()
    ) : Parcelable