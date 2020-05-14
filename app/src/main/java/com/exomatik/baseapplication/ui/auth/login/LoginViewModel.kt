package com.exomatik.baseapplication.ui.auth.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.exomatik.baseapplication.R
import com.exomatik.baseapplication.base.BaseViewModel
import com.exomatik.baseapplication.model.ModelUser
import com.exomatik.baseapplication.ui.main.MainActivity
import com.exomatik.baseapplication.utils.DataSave
import com.exomatik.baseapplication.utils.dismissKeyboard

class LoginViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?) : BaseViewModel() {
    val userName = MutableLiveData<String>()
    val dataUser = MutableLiveData<ModelUser>()

    fun onClickBack(){
        activity?.let { dismissKeyboard(it) }
        navController.navigate(R.id.splashFragment)
    }

    fun onClickRegister(){
        activity?.let { dismissKeyboard(it) }
        navController.navigate(R.id.registerFragment)
    }

    fun onClickLogin(){
        activity?.let { dismissKeyboard(it) }
        val intent = Intent(activity, MainActivity::class.java)
        activity?.startActivity(intent)
        activity?.finish()
    }
}