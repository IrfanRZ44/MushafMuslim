package id.exomatik.mushafmuslim.ui.main.account

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseViewModel
import id.exomatik.mushafmuslim.model.ModelDataAccount
import id.exomatik.mushafmuslim.model.ModelPenarikan
import id.exomatik.mushafmuslim.model.ModelUser
import id.exomatik.mushafmuslim.ui.auth.AuthActivity
import id.exomatik.mushafmuslim.utils.*
import id.exomatik.mushafmuslim.utils.Constant.attention
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AccountViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val context: Context?,
    private val etUsername: TextInputLayout,
    private val btnBoost: AppCompatButton
    ) : BaseViewModel() {
    val usernameReferal = MutableLiveData<String>()
    val textPoin = MutableLiveData<String>()
    val textReferal = MutableLiveData<String>()
    val textHargaPoin = MutableLiveData<String>()
    val textSaldo = MutableLiveData<String>()
    val textUsername = MutableLiveData<String>()
    val textPhoneNumber = MutableLiveData<String>()
    val isValidAccount = MutableLiveData<String>()

    fun setUpData(textValidator: AppCompatTextView){
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        val poin = savedData?.getDataAccount()?.totalPoin?:0
        val hargaPoin = savedData?.getDataApps()?.hargaPoin?:1
        val saldo = poin * hargaPoin

        textPoin.value = poin.toString()
        textHargaPoin.value = "$hargaPoin Rupiah"
        textSaldo.value = format.format(saldo)

        textReferal.value = savedData?.getDataAccount()?.totalReferal.toString()
        textUsername.value = savedData?.getDataUser()?.username
        textPhoneNumber.value = savedData?.getDataUser()?.noHp

        val userReferal = savedData?.getDataAccount()?.usernameReferal
        val ctx = context

        setUpValidator(textValidator, ctx)
        setUpBoostPoin()

        if (!userReferal.isNullOrEmpty()){
            etUsername.isEnabled = false
            usernameReferal.value = userReferal
        }
        else{
            setUpReferal(ctx)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setUpBoostPoin(){
        val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.dateFormat1))
        } else {
            SimpleDateFormat(Constant.dateFormat1).format(Date())
        }
        val tglBoost = savedData?.getKeyString(Constant.reffBoostPoin)

        btnBoost.isEnabled = tglSekarang != tglBoost
    }

    @Suppress("DEPRECATION")
    private fun setUpValidator(textValidator: AppCompatTextView, ctx: Context?){
        val validAccount = savedData?.getDataAccount()?.validAccount

        if (validAccount != null && ctx != null && validAccount){
            textValidator.setTextColor(ctx.resources.getColor(R.color.colorPrimary))
            isValidAccount.value = "Valid Account"
        }
        else{
            textValidator.setTextColor(Color.RED)

            val milliseconds = savedData?.getKeyLong(Constant.reffTimerValid)?:0
            val minutes = milliseconds / 1000 / 60
            val seconds = milliseconds / 1000 % 60
            isValidAccount.value = "Invalid Account ($minutes:$seconds)"
        }
    }

    @Suppress("DEPRECATION")
    private fun setUpReferal(ctx: Context?) {
        val validAccount = savedData?.getDataAccount()?.validAccount

        if (validAccount != null && ctx != null && validAccount){
            etUsername.boxBackgroundColor = ctx.resources.getColor(android.R.color.transparent)
            etUsername.isEnabled = true
        }
        else{
            etUsername.isEnabled = false
        }
    }

    private fun removeToken(username: String) {
        isShowLoading.value = true

        val onCompleteListener = OnCompleteListener<Void> { result ->
            isShowLoading.value = false

            if (result.isSuccessful) {
                Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_LONG).show()

                FirebaseUtils.signOut()
                savedData?.setDataObject(ModelUser(), Constant.referenceUser)
                savedData?.setDataObject(ModelDataAccount(), Constant.referenceDataAccount)

                val intent = Intent(context, AuthActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(context, "Gagal menghapus token", Toast.LENGTH_LONG).show()
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
        }

        FirebaseUtils.deleteValueWith2Child(
            Constant.referenceUser, username,
            Constant.referenceToken, onCompleteListener, onFailureListener
        )
    }

    private fun checkingReferal(referal: String, user: String) {
        isShowLoading.value = true

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                isShowLoading.value = false
                message.value = "Error, ${result.message}"
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    isShowLoading.value = false
                    val data = result.getValue(ModelUser::class.java)

                    if (data?.username == referal && data.active == Constant.active){
                        getDataReferal(referal, user)
                    }
                    else{
                        message.value = "Maaf, Username ini sudah dibekukan"
                    }
                } else {
                    isShowLoading.value = false
                    message.value = "Maaf, Username referal belum terdaftar"
                }
            }
        }

        FirebaseUtils.getData1Child(
            Constant.referenceUser
            , referal
            , valueEventListener
        )
    }

    private fun getDataReferal(referal: String, user: String) {
        isShowLoading.value = true

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                isShowLoading.value = false
                message.value = "Error, ${result.message}"
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    isShowLoading.value = false

                    val data = result.getValue(ModelDataAccount::class.java)

                    val totalReff = data?.totalReferal?:0
                    val addReff = totalReff + 1

                    val allReff = data?.reff?:0
                    val addAllReff = allReff + 1

                    addingTotalReferal(referal, user, addReff, addAllReff)
                } else {
                    isShowLoading.value = false
                    message.value = "Maaf, Username referal belum terdaftar"
                }
            }
        }

        FirebaseUtils.getData1Child(
            Constant.referenceDataAccount
            , referal
            , valueEventListener
        )
    }

    private fun addingTotalReferal(referal: String, user: String, totalReff: Long, allReff: Long){
        isShowLoading.value = true
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false

                addingAllReferal(referal, user, allReff)
            } else {
                isShowLoading.value = false
                message.value = "Gagal memasukkan referal"
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueWith2ChildObject(
            Constant.referenceDataAccount
            , referal
            , Constant.referenceTotalReferal
            , totalReff
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun addingAllReferal(referal: String, user: String, allReff: Long){
        isShowLoading.value = true
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false

                addingReferal(referal, user)
            } else {
                isShowLoading.value = false
                message.value = "Gagal memasukkan referal"
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueWith2ChildObject(
            Constant.referenceDataAccount
            , referal
            , Constant.referenceReff
            , allReff
            , onCompleteListener
            , onFailureListener
        )
    }

    @Suppress("DEPRECATION")
    private fun addingReferal(referal: String, user: String){
        isShowLoading.value = true
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false

                val dataAccount = savedData?.getDataAccount()
                dataAccount?.usernameReferal = referal
                savedData?.setDataObject(dataAccount, Constant.referenceDataAccount)

                val act = activity

                if (act != null){
                    dismissKeyboard(act)
                    etUsername.editText?.clearFocus()
                    etUsername.isEnabled = false
                    etUsername.boxBackgroundColor = act.resources.getColor(R.color.gray12)
                }

                message.value = "Berhasil memasukkan username referal"
            } else {
                isShowLoading.value = false
                message.value = "Gagal memasukkan referal"
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueWith2ChildObject(
            Constant.referenceDataAccount
            , user
            , Constant.referenceUsernameReferal
            , referal
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun firstWithdraw(dataPenarikan: ModelPenarikan){
        isShowLoading.value = true

        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false

                val dataAccount = savedData?.getDataAccount()
                dataAccount?.firstWithdraw = true
                savedData?.setDataObject(dataAccount, Constant.referenceDataAccount)

                addDataPenarikan(dataPenarikan)
            } else {
                isShowLoading.value = false
                message.value = "Gagal melakukan penarikan"
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueWith2ChildObject(
            Constant.referenceDataAccount
            , dataPenarikan.username
            , Constant.referenceFirstWithdraw
            , true
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun addDataPenarikan(dataPenarikan: ModelPenarikan) {
        isShowLoading.value = true

        val onCompleteListener =
            OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    isShowLoading.value = false

                    val totalPoin = savedData?.getDataAccount()?.totalPoin?:0
                    val jumlah = if (dataPenarikan.jumlah == Constant.penarikan10){
                        Constant.saldo10
                    }
                    else{
                        Constant.saldo100
                    }

                    val sisaPoin = totalPoin - jumlah

                    reducePoin(dataPenarikan.username, sisaPoin)
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal melakukan penarikan"
                }
            }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueUniquePenarikan(
            Constant.referencePenarikan
            , dataPenarikan
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun reducePoin(username: String, sisaPoin: Long){
        isShowLoading.value = true

        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false

                val dataAccount = savedData?.getDataAccount()
                dataAccount?.totalPoin = sisaPoin
                savedData?.setDataObject(dataAccount, Constant.referenceDataAccount)

                val localeID = Locale("in", "ID")
                val format = NumberFormat.getCurrencyInstance(localeID)
                val hargaPoin = savedData?.getDataApps()?.hargaPoin?:1
                val saldo = sisaPoin * hargaPoin

                textPoin.value = sisaPoin.toString()
                textSaldo.value = format.format(saldo)

                message.value = "Berhasil melakukan penarikan saldo"
            } else {
                isShowLoading.value = false
                message.value = "Error kesalahan database"
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueWith2ChildObject(
            Constant.referenceDataAccount
            , username
            , Constant.referenceTotalPoin
            , sisaPoin
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun penarikan10(){
        val poin = savedData?.getDataAccount()?.totalPoin?:0
        val hargaPoin = savedData?.getDataApps()?.hargaPoin?:1
        val saldo = poin * hargaPoin

        if (saldo >= Constant.saldo10){
            val firstWD = savedData?.getDataAccount()?.firstWithdraw?:false

            if (!firstWD){
                val dataAccount = savedData?.getDataAccount()
                val dataUser = savedData?.getDataUser()

                @SuppressLint("SimpleDateFormat")
                val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.timeDateFormat))
                } else {
                    SimpleDateFormat(Constant.timeDateFormat).format(Date())
                }

                if (dataAccount != null && dataUser != null){
                    val dataPenarikan = ModelPenarikan("", dataAccount.username,
                        Constant.penarikan10, "Go-Pay", dataUser.noHp, tglSekarang, Constant.wdProses)

                    firstWithdraw(dataPenarikan)
                }
                else{
                    message.value = "Error, terjadi kesalahan database"
                }
            }
            else{
                message.value = "Maaf, Anda sudah pernah melakukan pencairan ini"
            }
        }
        else{
            message.value = "Maaf, saldo Anda tidak mencukupi"
        }
    }

    private fun penarikan100(){
        val poin = savedData?.getDataAccount()?.totalPoin?:0
        val hargaPoin = savedData?.getDataApps()?.hargaPoin?:1
        val saldo = poin * hargaPoin

        if (saldo >= Constant.saldo100){
            val dataAccount = savedData?.getDataAccount()
            @SuppressLint("SimpleDateFormat")
            val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.timeDateFormat))
            } else {
                SimpleDateFormat(Constant.timeDateFormat).format(Date())
            }

            if (dataAccount != null){
                val dataPenarikan = ModelPenarikan("", dataAccount.username,
                    Constant.penarikan100, "Go-Pay", tglSekarang, "Proses")

                addDataPenarikan(dataPenarikan)
            }
            else{
                message.value = "Error, terjadi kesalahan database"
            }
        }
        else{
            message.value = "Maaf, saldo Anda tidak mencukupi"
        }
    }

    private fun showAds(){
        val rewardedAd = RewardedAd(activity, Constant.idRewarded)

        val adLoadCallBack = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                if (rewardedAd.isLoaded) {
                    rewardedAd.show(activity, object: RewardedAdCallback() {
                        override fun onRewardedAdOpened() {
                        }
                        override fun onRewardedAdClosed() {

                        }
                        override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                        }
                        override fun onRewardedAdFailedToShow(adError: AdError) {
                        }
                    })
                }
            }
            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
            }
        }

        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallBack)

    }

    fun onClickRating(){
        activity?.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${activity.packageName}")
            )
        )
    }

    fun onClickTarik(){
        val ctx = context
        val listFaq = savedData?.getDataApps()?.data_faq

        if (ctx != null && listFaq != null){
            val dialog = Dialog(ctx, R.style.CustomDialogTheme)
            dialog.setContentView(R.layout.dialog_penarikan)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)

            val rcFaq = dialog.findViewById<ListView>(R.id.listView)
            val btnTarik10 = dialog.findViewById<AppCompatTextView>(R.id.btnPenarikan10)
            val btnTarik100 = dialog.findViewById<AppCompatTextView>(R.id.btnPenarikan10)

            val adapter = ArrayAdapter(ctx, android.R.layout.simple_list_item_1, listFaq)
            rcFaq?.adapter = adapter

            btnTarik10.setOnClickListener {
                dialog.dismiss()
                penarikan10()
            }

            btnTarik100.setOnClickListener {
                dialog.dismiss()
                penarikan100()
            }

            dialog.show()
        }
        else {
            message.value = "Error, terjadi kesalahan yang tidak diketahui"
        }

//        showAds()
    }

    fun onClickRiwayat(){
        navController.navigate(R.id.riwayatFragment)
    }

    fun onClickBoost(){
        val totalReff = savedData?.getDataAccount()?.totalReferal?:0
        val username = savedData?.getDataUser()?.username
        @SuppressLint("SimpleDateFormat")
        val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.dateFormat1))
        } else {
            SimpleDateFormat(Constant.dateFormat1).format(Date())
        }

       if (totalReff >= 1 && !username.isNullOrEmpty()){
            val sisaReferal = totalReff - 1
            isShowLoading.value = true

            val onCompleteListener = OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    isShowLoading.value = false

                    val dataAccount = savedData?.getDataAccount()
                    dataAccount?.totalReferal = sisaReferal
                    savedData?.setDataObject(dataAccount, Constant.referenceDataAccount)
                    savedData?.setDataString(tglSekarang, Constant.reffBoostPoin)
                    textReferal.value = sisaReferal.toString()
                    btnBoost.isEnabled = false

                    message.value = "Berhasil melakukan Boost Poin"
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal melakukan Boost Poin"
                }
            }

            val onFailureListener = OnFailureListener { result ->
                isShowLoading.value = false
                message.value = result.message
            }

            FirebaseUtils.setValueWith2ChildObject(
                Constant.referenceDataAccount
                , username
                , Constant.referenceTotalReferal
                , sisaReferal
                , onCompleteListener
                , onFailureListener
            )
        }
        else{
            message.value = "Boost Poin membutuhkan 1 Referal"
        }
    }

    fun onClickLogout(){
        val ctx = context

        if (ctx != null){
            val alert = AlertDialog.Builder(ctx)
            alert.setTitle(attention)
            alert.setMessage(Constant.alertLogout)
            alert.setPositiveButton(
                Constant.iya
            ) { _, _ ->
                val username = savedData?.getDataUser()?.username
                if (!username.isNullOrEmpty()){
                    FirebaseUtils.stopRefresh()
                    FirebaseUtils.stopRefresh2()

                    removeToken(username)
                }
            }
            alert.setNegativeButton(
                Constant.tidak
            ) { dialog, _ -> dialog.dismiss() }

            alert.show()
        }
        else {
            message.value = "Error, terjadi kesalahan yang tidak diketahui"
        }
    }

    fun onClickReferal(){
        val referal = usernameReferal.value
        val user = savedData?.getDataUser()?.username
        if (!referal.isNullOrEmpty() && !user.isNullOrEmpty()){
            if (referal == user){
                message.value = "Referal harus berisi Username Pengundang Anda"
            }
            else{
                checkingReferal(referal, user)
            }
        }
        else{
            message.value = "Mohon masukkan username referal Anda"
        }
    }
}