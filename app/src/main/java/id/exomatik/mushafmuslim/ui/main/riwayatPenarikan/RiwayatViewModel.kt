package id.exomatik.mushafmuslim.ui.main.riwayatPenarikan

import android.app.Activity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseViewModel
import id.exomatik.mushafmuslim.model.ModelPenarikan
import id.exomatik.mushafmuslim.ui.main.detailShirah.DetailShirahFragment
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.DataSave
import id.exomatik.mushafmuslim.utils.FirebaseUtils

class RiwayatViewModel(
    private val navController: NavController,
    private val activity: Activity?,
    private val rcPenarikan: RecyclerView
) : BaseViewModel() {
    val listPenarikan = ArrayList<ModelPenarikan>()
    lateinit var adapter: AdapterRiwayat

    fun initAdapter() {
        rcPenarikan.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = AdapterRiwayat(listPenarikan, activity)
        rcPenarikan.adapter = adapter
    }

    fun getDataPenarikan(username: String) {
        isShowLoading.value = true

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                message.value = result.message
                isShowLoading.value = false
            }

            override fun onDataChange(result: DataSnapshot) {
                isShowLoading.value = false
                if (result.exists()) {
                    for (snapshot in result.children) {
                        val data = snapshot.getValue(ModelPenarikan::class.java)

                        data?.let {
                            listPenarikan.add(it)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                else{
                    message.value = Constant.noDataShirah
                }
            }
        }

        FirebaseUtils.searchDataWith1ChildObject(
            Constant.referencePenarikan
            , Constant.username
            , username
            , valueEventListener
        )
    }

    private fun onClickItem(item: ModelPenarikan){
        val bundle = Bundle()
        val cariFragment = DetailShirahFragment()
        bundle.putParcelable(Constant.referenceShirah, item)
        cariFragment.arguments = bundle
        navController.navigate(R.id.detailShirahFragment, bundle)
    }
}