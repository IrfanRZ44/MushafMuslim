package id.exomatik.mushafmuslim.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelPenarikan(
    var idPenarikan: String = "",
    var username: String = "",
    var jumlah: String = "",
    var bank: String = "",
    var rekening: String = "",
    var tanggal: String = "",
    var status: String = ""
    ) : Parcelable