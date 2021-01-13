package id.exomatik.mushafmuslim.ui.main

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseActivity
import id.exomatik.mushafmuslim.model.ModelDataAccount
import id.exomatik.mushafmuslim.model.ModelUser
import id.exomatik.mushafmuslim.ui.auth.AuthActivity
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.FirebaseUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun getLayoutResource(): Int = R.layout.activity_main

    @Suppress("DEPRECATION")
    override fun myCodeHere() {
        NavHostFragment.create(R.navigation.main_nav)
        viewParent.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        val username = savedData.getDataUser()?.username
        if (!username.isNullOrEmpty()){
            getDataUser(username)
        }
    }

    private fun getDataUser(username: String) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    val data = result.getValue(ModelUser::class.java)

                    when {
                        data?.token != savedData.getDataUser()?.token -> {
                            if (FirebaseUtils.stopRefresh()){
                                logout("Maaf, akun Anda sedang masuk dari perangkat lain")
                            }
                        }
                        data?.active != Constant.active -> {
                            if (FirebaseUtils.stopRefresh()){
                                logout("Maaf, akun Anda dibekukan")
                            }
                        }
                        else -> {
                            savedData.setDataObject(data, Constant.referenceUser)
                        }
                    }
                }
            }
        }

        FirebaseUtils.refreshDataWith1ChildObject2(
            Constant.referenceUser
            , username
            , valueEventListener
        )
    }

    private fun logout(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        FirebaseUtils.signOut()
        savedData.setDataObject(ModelUser(), Constant.referenceUser)
        savedData.setDataObject(ModelDataAccount(), Constant.referenceDataAccount)
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}