package com.exomatik.zcodex.ui.auth.verifyLogin

import android.app.Activity
import android.os.CountDownTimer
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.services.timer.TListener
import com.exomatik.zcodex.services.timer.TimeFormatEnum
import com.exomatik.zcodex.services.timer.TimerView
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.Constant.phone
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
import com.google.firebase.iid.InstanceIdResult
import java.util.concurrent.TimeUnit

class VerifyLoginViewModel(
    private val dataSave: DataSave,
    private val activity: Activity?,
    private val progressTimer: TimerView,
    private val etText1 : AppCompatEditText,
    private val etText2 : AppCompatEditText,
    private val etText3 : AppCompatEditText,
    private val etText4 : AppCompatEditText,
    private val etText5 : AppCompatEditText,
    private val etText6 : AppCompatEditText,
    private val navController: NavController
) : BaseViewModel() {
    val phoneCode = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    var unverify = true
    var verifyId = ""
    lateinit var dataUser : ModelUser

    fun onClick(requestEvent: Int) {
        activity?.let { dismissKeyboard(it) }

        when (requestEvent) {
            2 -> {
                isShowLoading.value = true
                verifyUser()
            }
            3 -> {
                isShowLoading.value = true
                sendCode()
            }
        }
    }

    private fun verifyUser() {
        try {
            val credential = PhoneAuthProvider.getCredential(
                verifyId
                , phoneCode.value ?: throw Exception("Error, kode verifikasi tidak boleh kosong")
            )

            val onCoCompleteListener =
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        isShowLoading.value = false
                        getUserToken()
                    } else {
                        message.value = "Error, kode verifikasi salah"
                        isShowLoading.value = false
                        setEmptyEditText()
                    }
                }

            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(onCoCompleteListener)
        } catch (e: java.lang.Exception) {
            message.value = e.message
            isShowLoading.value = false
            setEmptyEditText()
        }
    }

    private fun setEmptyEditText() {
        etText6.setText("")
        etText5.setText("")
        etText4.setText("")
        etText3.setText("")
        etText2.setText("")
        etText1.setText("")
        etText6.clearFocus()
        etText1.findFocus()
        etText1.requestFocus()
    }

    private fun getUserToken() {
        val onCompleteListener = OnCompleteListener<InstanceIdResult> { result ->
                if (result.isSuccessful) {
                    dataUser.token = result.result?.token?:""

                    saveToken(
                        dataUser.token,
                        dataUser.username
                    )
                    dataSave.setDataObject(dataUser, referenceUser)
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal mendapatkan token"
                    setEmptyEditText()
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
                    message.value = "Berhasil menyimpan data user"

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
            , Constant.referenceToken
            , value
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun sendCode() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                unverify = false
                message.value =
                    "Berhasil memverifikasi nomor " + dataUser.noHp
                isShowLoading.value = false
                loading.value = true

                val body = HashMap<String, String?>()
                body[phone] = dataUser.noHp
                body[username] = dataUser.username

                getUserToken()
                isShowLoading.value = false
            }

            override fun onVerificationFailed(e: FirebaseException) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        message.value = "Error, Nomor Handphone tidak Valid"
                        setEmptyEditText()
                    }
                    is FirebaseTooManyRequestsException -> {
                        message.value = "Error, Anda sudah terlalu banyak mengirimkan permintaan"
                        setEmptyEditText()
                    }
                    else -> {
                        message.value = e.message
                        setEmptyEditText()
                    }
                }
                isShowLoading.value = false
                loading.value = true
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                object : CountDownTimer(2000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        if (unverify) {
                            isShowLoading.value = false
                            loading.value = true

                            message.value =
                                "Kami sudah mengirimkan kode verifikasi ke nomor ${dataUser.noHp}"
                            unverify = false
                            setProgress()

                            try {
                                verifyId = verificationId
                            } catch (e: Exception) {
                                message.value = e.message
                            }
                        }
                    }
                }.start()
            }
        }

        try {
            FirebaseUtils.registerUser(
                dataUser.noHp
                , callbacks, activity ?: throw Exception("Error, Mohon mulai ulang aplikasi")
            )
        } catch (e: Exception) {
            message.value = e.message
            isShowLoading.value = false
        }
    }

    private fun setProgress() {
        progressTimer.setCircularTimerListener(object :
            TListener {
            override fun updateDataOnTick(remainingTimeInMs: Long): String {
                // long seconds = (milliseconds / 1000);
                val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMs)
                progressTimer.prefix = ""
                progressTimer.suffix = " detik"
                return seconds.toString()
            }

            override fun onTimerFinished() {
                progressTimer.prefix = ""
                progressTimer.suffix = ""
                progressTimer.text = "Kirim Ulang?"
                isShowLoading.value = false
                loading.value = false
            }
        }, 60, TimeFormatEnum.SECONDS, 1)

        progressTimer.progress = 0F
        progressTimer.startTimer()
    }
}