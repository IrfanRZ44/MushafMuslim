package id.exomatik.mushafmuslim.ui.auth.register

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseViewModel
import id.exomatik.mushafmuslim.model.ModelUser
import id.exomatik.mushafmuslim.ui.auth.verifyRegister.VerifyRegisterFragment
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.Constant.active
import id.exomatik.mushafmuslim.utils.Constant.phone
import id.exomatik.mushafmuslim.utils.Constant.referenceUser
import id.exomatik.mushafmuslim.utils.Constant.username
import id.exomatik.mushafmuslim.utils.DataSave
import id.exomatik.mushafmuslim.utils.FirebaseUtils
import id.exomatik.mushafmuslim.utils.dismissKeyboard
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.InstanceIdResult
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RegisterViewModel(
    private val activity: Activity?,
    private val dataSave: DataSave?,
    private val navController: NavController
) : BaseViewModel() {
    val userName = MutableLiveData<String>()
    val noHp = MutableLiveData<String>()
    var unverify = true

    @SuppressLint("SimpleDateFormat")
    private val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.timeDateFormat))
    } else {
        SimpleDateFormat(Constant.timeDateFormat).format(Date())
    }

    fun onClickBack(){
        activity?.let { dismissKeyboard(it) }
        navController.navigate(R.id.splashFragment)
    }

    fun onClickLogin(){
        activity?.let { dismissKeyboard(it) }
        navController.navigate(R.id.loginFragment)
    }

    fun cekUserName() {
        activity?.let { dismissKeyboard(it) }
        isShowLoading.value = true
        try {
            val user = userName.value ?: throw Exception("Error, Username tidak boleh kosong")
            val hp = noHp.value?.replaceFirst("0", "+62")?: throw Exception("Error, nomor telepon tidak boleh kosong")
            val valueEventListener = object : ValueEventListener {
                override fun onCancelled(result: DatabaseError) {
                    isShowLoading.value = false
                    message.value = "Error, ${result.message}"
                }

                override fun onDataChange(result: DataSnapshot) {
                    if (result.exists()) {
                        isShowLoading.value = false
                        message.value = "Gagal, Username sudah digunakan"
                    } else {
                        cekHandphone(user, hp)
                    }
                }
            }
            FirebaseUtils.searchDataWith1ChildObject(
                referenceUser, username
                , user
                , valueEventListener
            )
        } catch (e: Exception) {
            isShowLoading.value = false
            message.value = e.message
        }
    }

    private fun cekHandphone(user: String, hp: String) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                isShowLoading.value = false
                message.value = "Error, ${result.message}"
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    isShowLoading.value = false
                    message.value = "Gagal, No Handphone sudah terdaftar"
                } else {
                    signUp(user, hp)
                }
            }
        }

        FirebaseUtils.searchDataWith1ChildObject(
            referenceUser, phone
            , hp
            , valueEventListener
        )
    }

    private fun signUp(user: String, hp: String) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (unverify) {
                    signIn(credential, user, hp)
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

            @Suppress("DEPRECATION")
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                object : CountDownTimer(8000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        if (unverify) {
                            isShowLoading.value = false
                            val dataUser = ModelUser(
                                hp, user, "user"
                                , "", tglSekarang, active
                            )

                            val bundle = Bundle()
                            val fragmentTujuan = VerifyRegisterFragment()
                            bundle.putString("verifyId", verificationId)
                            bundle.putBoolean("auth", true)
                            bundle.putParcelable("dataUser", dataUser)
                            fragmentTujuan.arguments = bundle
                            navController.navigate(R.id.verifyRegisterFragment, bundle)
                            unverify = false
                        }
                    }
                }.start()
            }
        }

        try {
            FirebaseUtils.registerUser(hp, callbacks, activity ?: throw Exception("Mohon mulai ulang aplikasi"))
        } catch (e: Exception) {
            message.value = e.message
            isShowLoading.value = false
        }
    }

    fun signIn(credential: AuthCredential, user: String, hp: String) {
        isShowLoading.value = true

        val onCoCompleteListener =
            OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {

                    val dataUser = ModelUser(
                        hp, user, "user", "", tglSekarang, active
                    )

                    getUserToken(dataUser, user)
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal masuk ke Akun Anda"
                }
            }

        FirebaseUtils.signIn(credential, onCoCompleteListener)
    }

    private fun getUserToken(dataUser: ModelUser, userName: String) {
        val onCompleteListener =
            OnCompleteListener<InstanceIdResult> { result ->
                if (result.isSuccessful) {
                    message.value = "Berhasil mendapatkan token"

                    dataUser.token = result.result?.token ?: ""
                    addUserToFirebase(dataUser, userName)
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal mendapatkan token"
                }
            }

        FirebaseUtils.getUserToken(
            onCompleteListener
        )
    }

    private fun addUserToFirebase(dataUser: ModelUser, userName: String) {
        val onCompleteListener =
            OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    isShowLoading.value = false
                    message.value = "Berhasil menyimpan user"

                    dataSave?.setDataObject(
                        dataUser, referenceUser
                    )

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
        FirebaseUtils.setValueObject(
            referenceUser
            , userName
            , dataUser
            , onCompleteListener
            , onFailureListener
        )
    }
}