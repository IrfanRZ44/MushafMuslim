package id.exomatik.mushafmuslim.ui.main.shirah

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
import id.exomatik.mushafmuslim.model.ModelShirah
import id.exomatik.mushafmuslim.ui.main.detailShirah.DetailShirahFragment
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.DataSave
import id.exomatik.mushafmuslim.utils.FirebaseUtils

class ShirahViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val rcShirah: RecyclerView
) : BaseViewModel() {
    val listShirah = ArrayList<ModelShirah>()
    lateinit var adapter: AdapterShirah

    fun initAdapter() {
        rcShirah.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = AdapterShirah(listShirah,
            { item: ModelShirah -> onClickItem(item) },
            navController)
        rcShirah.adapter = adapter
    }

    fun getDataBookmark() {
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
                        val data = snapshot.getValue(ModelShirah::class.java)

                        data?.let {
                            listShirah.add(it)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                else{
                    message.value = Constant.noDataShirah
                }
            }
        }

        FirebaseUtils.getDataObject(
            Constant.referenceShirah
            , valueEventListener
        )
    }

    private fun onClickItem(item: ModelShirah){
        val bundle = Bundle()
        val cariFragment = DetailShirahFragment()
        bundle.putParcelable(Constant.referenceShirah, item)
        cariFragment.arguments = bundle
        navController.navigate(R.id.detailShirahFragment, bundle)
    }
}