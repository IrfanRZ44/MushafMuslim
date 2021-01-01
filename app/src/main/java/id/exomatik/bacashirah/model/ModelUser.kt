package id.exomatik.bacashirah.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelUser(
    var noHp: String = "",
    var username: String = "",
    var typeAccount: String = "",
    var token: String = "",
    var dateCreated: String = "",
    var active: String = ""
) : Parcelable