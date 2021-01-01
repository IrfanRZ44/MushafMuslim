package id.exomatik.bacashirah.ui.main.detailShirah

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import id.exomatik.bacashirah.base.BaseViewModel
import id.exomatik.bacashirah.model.ModelShirah
import id.exomatik.bacashirah.utils.DataSave

class DetailShirahViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val context: Context?
    ) : BaseViewModel() {
    val dataShirah = MutableLiveData<ModelShirah>()

}