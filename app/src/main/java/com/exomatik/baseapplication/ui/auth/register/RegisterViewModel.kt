package com.exomatik.baseapplication.ui.auth.register

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.exomatik.baseapplication.R
import com.exomatik.baseapplication.base.BaseViewModel
import com.exomatik.baseapplication.ui.main.MainActivity
import com.exomatik.baseapplication.utils.DataSave
import com.exomatik.baseapplication.utils.dismissKeyboard

class RegisterViewModel(
    private val activity: Activity?,
    private val dataSave: DataSave?,
    private val navController: NavController
) : BaseViewModel() {
    val userName = MutableLiveData<String>()
    val noHp = MutableLiveData<String>()

    fun onClickBack(){
        activity?.let { dismissKeyboard(it) }
        navController.navigate(R.id.splashFragment)
    }

    fun onClickLogin(){
        activity?.let { dismissKeyboard(it) }
        navController.navigate(R.id.loginFragment)
    }

    fun onClickRegister(){
        activity?.let { dismissKeyboard(it) }
        val intent = Intent(activity, MainActivity::class.java)
        activity?.startActivity(intent)
        activity?.finish()
    }
}