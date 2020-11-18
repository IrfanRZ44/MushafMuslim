package com.exomatik.zcodex.ui.main.editProfile

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Build
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import coil.api.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.utils.*
import com.exomatik.zcodex.utils.Constant.referenceUser
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*

class EditProfileViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val spinnerBank: AppCompatSpinner
) : BaseViewModel() {
    val foto = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val namaLengkap = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val nomorRekening = MutableLiveData<String>()
    private val listBank = ArrayList<String>()
    private lateinit var adapterJenis : SpinnerAdapter

    fun setData(){
        username.value = savedData?.getDataUser()?.username
        phoneNumber.value = savedData?.getDataUser()?.noHp
        namaLengkap.value = savedData?.getDataUser()?.nama
        nomorRekening.value = savedData?.getDataUser()?.nomorRekening
        foto.value = savedData?.getDataUser()?.urlFoto

        setSpinnerAdapter(savedData?.getDataUser()?.namaBank)
    }

    private fun setSpinnerAdapter(namaBank: String?) {
        listBank.add(Constant.pilihJenisBank)
        listBank.add(Constant.bankBRI)
        listBank.add(Constant.bankBCA)
        listBank.add(Constant.bankBNI)
        listBank.add(Constant.bankCIMBNiaga)
        listBank.add(Constant.walletOvo)
        listBank.add(Constant.walletDana)
        listBank.add(Constant.walletGopay)

        adapterJenis = SpinnerAdapter(activity, listBank)
        spinnerBank.adapter = adapterJenis

        if (!namaBank.isNullOrEmpty()){
            for (i in listBank.indices){
                if (listBank[i] == namaBank){
                    spinnerBank.setSelection(i)
                }
            }
        }
    }

    fun onClickSave(){
        val user = username.value
        val phone = phoneNumber.value
        val nama = namaLengkap.value
        val rekening = nomorRekening.value
        val bank = listBank[spinnerBank.selectedItemPosition]
        val urlFoto = foto.value

        if (!user.isNullOrEmpty() && !phone.isNullOrEmpty() && !nama.isNullOrEmpty()
            && !rekening.isNullOrEmpty() && spinnerBank.selectedItemPosition != 0
            && !urlFoto.isNullOrEmpty()){
            @SuppressLint("SimpleDateFormat")
            val tglSkrng = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.timeDateFormat))
            } else {
                SimpleDateFormat(Constant.timeDateFormat).format(Date())
            }

            val dataUser = ModelUser(nama, phone, savedData?.getDataUser()?.token?:"",
                "user", user, urlFoto, rekening, bank, tglSkrng, tglSkrng,
                savedData?.getDataUser()?.totalPoin?:0, Constant.active)

            simpanData(dataUser)
        }
    }

    fun saveFoto(image : Uri, username: String){
        isShowLoading.value = true

        val onSuccessListener = OnSuccessListener<UploadTask.TaskSnapshot> {
            getUrlFoto(it, username)
        }

        val onFailureListener = OnFailureListener {
            message.value = it.message
            isShowLoading.value = false
        }

        FirebaseUtils.simpanFoto(Constant.referenceFotoUser, username
            , image, onSuccessListener, onFailureListener)
    }

    private fun getUrlFoto(uploadTask: UploadTask.TaskSnapshot, username: String) {
        val onSuccessListener = OnSuccessListener<Uri?>{
            simpanUrlFoto(it.toString(), username)
        }

        val onFailureListener = OnFailureListener {
            message.value = it.message
            isShowLoading.value = false
        }

        FirebaseUtils.getUrlFoto(uploadTask, onSuccessListener, onFailureListener)
    }

    private fun simpanUrlFoto(urlFoto: String, username: String){
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false
                message.value = "Berhasil menyimpan foto"
                val dataUser = savedData?.getDataUser()

                foto.value = urlFoto
                dataUser?.urlFoto = urlFoto
                savedData?.setDataObject(dataUser, referenceUser)
            } else {
                isShowLoading.value = false
                message.value = "Gagal menyimpan foto"
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueWith2ChildString(
            referenceUser
            , username
            , Constant.referenceFotoUser
            , urlFoto
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun simpanData(dataUser: ModelUser){
        isShowLoading.value = true

        val onCompleteListener =
            OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    message.value = "Berhasil menyimpan data user"
                    navController.navigate(R.id.nav_beranda)
                    savedData?.setDataObject(dataUser, referenceUser)
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal menyimpan data user"
                }
            }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueObject(
            referenceUser
            , dataUser.username
            , dataUser
            , onCompleteListener
            , onFailureListener
        )
    }
}