package id.exomatik.mushafmuslim.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelShirah(
    var idShirah: String = "",
    var urlFoto: String = "",
    var title: String = "",
    var shirah: String = "",
    var dateCreated: String = ""
    ) : Parcelable