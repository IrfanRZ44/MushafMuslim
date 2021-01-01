package id.exomatik.bacashirah.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelDataAccount(
    var username: String = "",
    var usernameReferal: String = "",
    var lastLogin: String = "",
    var nomorRekening: String = "",
    var namaBank: String = "",
    var firstWithdraw: Boolean = false,
    var validAccount: Boolean = false,
    var totalPoin: Long = 0,
    var totalReferal: Long = 0
) : Parcelable