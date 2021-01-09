package id.exomatik.mushafmuslim.ui.main.detailShirah

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import id.exomatik.mushafmuslim.base.BaseViewModel
import id.exomatik.mushafmuslim.model.ModelShirah
import id.exomatik.mushafmuslim.utils.DataSave

class DetailShirahViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val context: Context?
    ) : BaseViewModel() {
    val dataShirah = MutableLiveData<ModelShirah>()

}