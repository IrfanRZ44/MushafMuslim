package id.exomatik.mushafmuslim.ui.main.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.exomatik.mushafmuslim.BuildConfig
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseViewModel
import id.exomatik.mushafmuslim.model.ModelDataAccount
import id.exomatik.mushafmuslim.model.ModelUser
import id.exomatik.mushafmuslim.ui.auth.AuthActivity
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.Constant.attention
import id.exomatik.mushafmuslim.utils.DataSave
import id.exomatik.mushafmuslim.utils.FirebaseUtils
import id.exomatik.mushafmuslim.utils.dismissKeyboard
import java.text.NumberFormat

class AccountViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val context: Context?,
    private val etUsername: TextInputLayout
    ) : BaseViewModel() {
    val usernameReferal = MutableLiveData<String>()
    val textPoin = MutableLiveData<String>()
    val textHargaPoin = MutableLiveData<String>()
    val textSaldo = MutableLiveData<String>()
    val versiAplikasi = MutableLiveData<String>()
    val textUsername = MutableLiveData<String>()
    val textPhoneNumber = MutableLiveData<String>()
    val isValidAccount = MutableLiveData<String>()

    fun setUpData(textValidator: AppCompatTextView){
        versiAplikasi.value = "Versi Aplikasi ${BuildConfig.VERSION_NAME}"

        val format = NumberFormat.getCurrencyInstance()
        val poin = savedData?.getDataAccount()?.totalPoin?:0
        val hargaPoin = savedData?.getDataApps()?.hargaPoin?:1
        val saldo = poin * hargaPoin

        textPoin.value = poin.toString()
        textHargaPoin.value = "$hargaPoin Rupiah"
        textSaldo.value = format.format(saldo)

        textUsername.value = savedData?.getDataUser()?.username
        textPhoneNumber.value = savedData?.getDataUser()?.noHp

        val userReferal = savedData?.getDataAccount()?.usernameReferal
        val ctx = context

        setUpValidator(textValidator, ctx)

        if (!userReferal.isNullOrEmpty()){
            etUsername.isEnabled = false
            usernameReferal.value = userReferal
        }
        else{
            setUpReferal(ctx)
        }
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

    fun onClickRating(){
        activity?.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${activity.packageName}")
            )
        )
    }

    fun onClickTarik(){
        message.value = "Tarik"
//        activity?.let { dismissKeyboard(it) }
//        navController.navigate(R.id.loginFragment)
    }

    fun onClickRiwayat(){
        message.value = "Riwayat"
//        activity?.let { dismissKeyboard(it) }
//        navController.navigate(R.id.loginFragment)
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