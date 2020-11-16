package com.exomatik.zcodex.ui.main.editProfile

import android.app.Activity
import androidx.navigation.NavController
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.utils.DataSave

class EditProfileViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?) : BaseViewModel() {

}