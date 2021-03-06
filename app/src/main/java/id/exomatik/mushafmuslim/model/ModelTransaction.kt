package id.exomatik.mushafmuslim.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelTransaction(
    var idTransaction: String = "",
    var date: String = "",
    var username: String = "",
    var poin: Int = 0
    ) : Parcelable