package com.exomatik.zcodex.ui.auth.login

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.ui.auth.verify.VerifyFragment
import com.exomatik.zcodex.utils.Constant.noHp
import com.exomatik.zcodex.utils.Constant.referenceUser
import com.exomatik.zcodex.utils.Constant.username
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.exomatik.zcodex.utils.dismissKeyboard
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.InstanceIdResult

class LoginViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?) : BaseViewModel() {
    val userName = MutableLiveData<String>()
    val dataUser = MutableLiveData<ModelUser>()
    var unverify = true

    fun onClickRegister(){
        activity?.let { dismissKeyboard(it) }
        navController.navigate(R.id.registerFragment)
    }

    fun onClickLogin(){
        activity?.let { dismissKeyboard(it) }
        try {
            val dataInput = userName.value ?: throw Exception("Error, input tidak boleh kosong")
            val char1 = dataInput.substring(0, 1)
            when {
                char1 == "0" -> {
                    val dataGanti = dataInput.replaceFirst("0", "+62")
                    cekNomorTelepon(dataGanti)
                }
                dataInput.contains("+62") -> {
                    cekNomorTelepon(dataInput)
                }
                else -> {
                    cekUserName(dataInput)
                }
            }
        } catch (e: Exception) {
            message.value = e.message
        }
    }

    private fun cekNomorTelepon(dataInput: String) {
        isShowLoading.value = true

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                isShowLoading.value = false
                message.value = "Error, ${result.message}"
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    for (snapshot in result.children) {
                        val data = snapshot.getValue(ModelUser::class.java)
                        dataUser.value = data

                        requestCode()
                    }
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal, Username atau No. Telepon belum terdaftar"
                }
            }
        }

        FirebaseUtils.searchDataWith1ChildObject(
            referenceUser, noHp
            , dataInput
            , valueEventListener
        )
    }

    private fun cekUserName(dataInput: String) {
        isShowLoading.value = true

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                isShowLoading.value = false
                message.value = "Error, ${result.message}"
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    for (snapshot in result.children) {
                        val data = snapshot.getValue(ModelUser::class.java)
                        dataUser.value = data

                        requestCode()
                    }
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal, Username atau No. Telepon belum terdaftar"
                }
            }
        }

        FirebaseUtils.searchDataWith1ChildObject(
            referenceUser, username
            , dataInput
            , valueEventListener
        )
    }

    private fun requestCode() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (unverify) {
                    signIn(credential)
                }
                unverify = false
            }

            override fun onVerificationFailed(e: FirebaseException) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        message.value = "Error, Nomor Handphone tidak Valid"
                    }
                    is FirebaseTooManyRequestsException -> {
                        message.value = "Error, Anda sudah terlalu banyak mengirimkan permintaan"
                    }
                    else -> {
                        message.value = e.message
                    }
                }
                isShowLoading.value = false
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Handler().postDelayed({
                    if (unverify) {
                        isShowLoading.value = false
                        unverify = false
                        val bundle = Bundle()
                        val fragmentTujuan = VerifyFragment()
                        bundle.putString("verifyId", verificationId)
                        bundle.putBoolean("auth", true)
                        bundle.putParcelable("dataUser", dataUser.value)
                        fragmentTujuan.arguments = bundle
                        navController.navigate(R.id.verifyFragment, bundle)
                    }
                }, 5000L)

            }
        }

        try {
            FirebaseUtils.registerUser(
                dataUser.value?.noHp ?: throw Exception("Error, No Handphone tidak boleh kosong")
                , callbacks, activity ?: throw Exception("Mohon mulai ulang aplikasi")
            )
        } catch (e: Exception) {
            message.value = e.message
            isShowLoading.value = false
        }
    }

    private fun signIn(credential: AuthCredential) {
        isShowLoading.value = true

        val onCoCompleteListener =
            OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {

                    try {
                        getUserToken()
                    } catch (e: Exception) {
                        isShowLoading.value = false
                        message.value = e.message
                    }
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal masuk ke Akun Anda"
                }
            }

        FirebaseUtils.signIn(credential, onCoCompleteListener)
    }

    private fun getUserToken() {
        val onCompleteListener =
            OnCompleteListener<InstanceIdResult> { result ->
                if (result.isSuccessful) {
                    try {
                        val tkn = result.result?.token ?: throw Exception("Error, kesalahan saat menyimpan token")
                        val user = dataUser.value?.username?:throw Exception("Error, mohon login ulang")
                        dataUser.value?.token = tkn
                        savedData?.setDataObject(dataUser.value, referenceUser)
                        saveToken(tkn, user)

                    } catch (e: Exception) {
                        isShowLoading.value = false
                        message.value = e.message
                    }
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal mendapatkan token"
                }
            }

        FirebaseUtils.getUserToken(
            onCompleteListener
        )
    }

    private fun saveToken(value: String, userName: String) {
        val onCompleteListener =
            OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    isShowLoading.value = false
                    message.value = "Berhasil masuk ke Akun Anda"

                    navController.navigate(R.id.splashFragment)
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal menyimpan data user"
                }
            }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }
        FirebaseUtils.setValueWith2ChildString(
            referenceUser
            , userName
            , "token"
            , value
            , onCompleteListener
            , onFailureListener
        )
    }
}