package id.exomatik.mushafmuslim.ui.main.detailShirah

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseViewModel
import id.exomatik.mushafmuslim.model.ModelShirah
import id.exomatik.mushafmuslim.model.ModelTransaction
import id.exomatik.mushafmuslim.utils.*
import io.github.krtkush.lineartimer.LinearTimer
import io.github.krtkush.lineartimer.LinearTimerStates
import io.github.krtkush.lineartimer.LinearTimerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class DetailShirahViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val context: Context?,
    private val pdfView: PDFView,
    private val progressTimer: LinearTimerView,
    private val textVerify: AppCompatTextView
    ) : BaseViewModel() {
    val dataShirah = MutableLiveData<ModelShirah>()
    private lateinit var linearTimer: LinearTimer
    var timerActivate : CountDownTimer? = null

    fun setUpPdfView(){
        try {
            isShowLoading.value = true
            RetrofitUtils.downloadPDF(dataShirah.value?.shirah?:throw Exception("Error, data tidak ditemukan"),
                object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        isShowLoading.value = false

                        pdfView.fromStream(response.body()?.byteStream())
                            .defaultPage(0)
                            .onPageScroll { _, _ ->
                                resumeTimer()
                            }
                            .load()
                        setProgress()
                        val isValidAccount = savedData?.getDataAccount()?.validAccount

                        if (isValidAccount != null && !isValidAccount){
                            val timeLeft = savedData?.getKeyLong(Constant.reffTimerValid)?:Constant.timerValid

                            timerValidation(timeLeft)
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseBody?>,
                        t: Throwable
                    ) {
                        isShowLoading.value = false
                        message.value = t.message
                    }
                })
        }catch (e: Exception){
            isShowLoading.value = false
            message.value = e.message
        }
    }

    private fun setProgress() {
        linearTimer = LinearTimer.Builder()
            .linearTimerView(progressTimer)
            .timerListener(object : LinearTimer.TimerListener{
                override fun onTimerReset() {
                    textVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }

                override fun animationComplete() {
                    if (linearTimer.state == LinearTimerStates.FINISHED){
                        textVerify.text = ""
                        textVerify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coin, 0, 0, 0)
                    }
                }

                override fun timerTick(tickUpdateInMillis: Long) {
                    if (linearTimer.state == LinearTimerStates.ACTIVE){
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(tickUpdateInMillis)
                        textVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        textVerify.text = seconds.toString()

                        if (seconds.toInt() == 5 || seconds.toInt() == 10 || seconds.toInt() == 15 ||
                            seconds.toInt() == 20 || seconds.toInt() == 25){
                            linearTimer.pauseTimer()
                        }
                    }
                }
            })
            .duration(30 * 1000.toLong())
            .build()

        if (linearTimer.state == LinearTimerStates.INITIALIZED){
            linearTimer.startTimer()
        }
    }

    private fun resumeTimer(){
        if (linearTimer.state == LinearTimerStates.PAUSED){
            linearTimer.resumeTimer()
        }
    }

    fun onClickPoin(){
        val userName = savedData?.getDataUser()?.username
        if (linearTimer.state == LinearTimerStates.FINISHED && !userName.isNullOrEmpty()){
            @SuppressLint("SimpleDateFormat")
            val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.dateFormat1))
            } else {
                SimpleDateFormat(Constant.dateFormat1).format(Date())
            }
            val tglBoostPoin = savedData?.getKeyString(Constant.reffBoostPoin)

            val poinAdded = if (tglBoostPoin == tglSekarang){
                Constant.boostPoin2
            }
            else{
                Constant.boostPoin1
            }

            val poin = savedData?.getDataAccount()?.totalPoin?:0
            val resultPoin = poin + poinAdded
            isShowLoading.value = true
            addPoin(userName, resultPoin, poinAdded)
        }
        else{
            message.value = "Belum waktunya mengambil poin"
        }
    }

    private fun addPoin(userName: String, poin: Long, poinAdded: Int){
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false
                message.value = "Poin bertambah $poinAdded"

                val dataAccount = savedData?.getDataAccount()
                dataAccount?.totalPoin = poin
                savedData?.setDataObject(
                    dataAccount, Constant.referenceDataAccount
                )
                linearTimer.restartTimer()
                addTransaction(userName, poinAdded)
            } else {
                isShowLoading.value = false
                message.value = "Gagal"
                linearTimer.restartTimer()
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
            linearTimer.restartTimer()
        }

        FirebaseUtils.setValueWith2ChildObject(
            Constant.referenceDataAccount
            , userName
            , Constant.referenceTotalPoin
            , poin
            , onCompleteListener
            , onFailureListener
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun addTransaction(userName: String, poin: Int){
        val dateTimeNow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.timeDateFormat))
        } else {
            SimpleDateFormat(Constant.timeDateFormat).format(Date())
        }

        val dateNow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.dateFormat1))
        } else {
            SimpleDateFormat(Constant.dateFormat1).format(Date())
        }

        val dataTransaction = ModelTransaction("", dateTimeNow, userName, poin)

        val onCompleteListener = OnCompleteListener<Void> {}

        val onFailureListener = OnFailureListener {}

        FirebaseUtils.setValueUniqueTransaction(
            Constant.referenceTransaction
            , dateNow
            , dataTransaction
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun timerValidation(timeLeft: Long){
        timerActivate = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                savedData?.setDataLong(millisUntilFinished, Constant.reffTimerValid)
            }

            override fun onFinish() {
                val user = savedData?.getDataUser()?.username

                if (!user.isNullOrEmpty()) {
                    validatingAccount(user)
                }
                else{
                    message.value = "Error, terjadi kesalahan database"
                }
            }
        }.start()
    }

    private fun validatingAccount(userName: String){
        isShowLoading.value = true
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false

                val dataAccount = savedData?.getDataAccount()
                dataAccount?.validAccount = true
                savedData?.setDataObject(
                    dataAccount, Constant.referenceDataAccount
                )
                message.value = "Akun Anda sudah tervalidasi"
            } else {
                isShowLoading.value = false
                message.value = "Gagal memvalidasi akun"
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueWith2ChildObject(
            Constant.referenceDataAccount
            , userName
            , Constant.referenceValidAccount
            , true
            , onCompleteListener
            , onFailureListener
        )
    }
}