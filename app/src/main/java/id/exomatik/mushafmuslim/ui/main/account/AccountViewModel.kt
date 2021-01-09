package id.exomatik.mushafmuslim.ui.main.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import id.exomatik.mushafmuslim.base.BaseViewModel
import id.exomatik.mushafmuslim.model.ModelDataAccount
import id.exomatik.mushafmuslim.model.ModelUser
import id.exomatik.mushafmuslim.ui.auth.AuthActivity
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.Constant.attention
import id.exomatik.mushafmuslim.utils.DataSave
import id.exomatik.mushafmuslim.utils.FirebaseUtils

class AccountViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val context: Context?
    ) : BaseViewModel() {
    val usernameReferal = MutableLiveData<String>()
    val textPoin = MutableLiveData<String>()
    val textHargaPoin = MutableLiveData<String>()
    val textRupiah = MutableLiveData<String>()
    val versiAplikasi = MutableLiveData<String>()

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

    private fun removeToken(username: String) {
        isShowLoading.value = true

        val onCompleteListener = OnCompleteListener<Void> { result ->
            isShowLoading.value = false

            if (result.isSuccessful) {
                FirebaseUtils.stopRefresh()

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

    fun onClickRating(){
        activity?.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${activity.packageName}")
            )
        )
    }
}