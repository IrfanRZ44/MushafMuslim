@file:Suppress("DEPRECATION")

package com.exomatik.zcodex.ui.main.beranda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelNotes
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.ui.main.editNotes.EditNotesFragment
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.exomatik.zcodex.utils.MyService
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class BerandaViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val adView: AdView,
    private val rcNotes: RecyclerView
    ) : BaseViewModel() {
    val totalPoint = MutableLiveData<String>()
    val totalUser = MutableLiveData<String>()
    val hargaPoint = MutableLiveData<String>()
    private var listNotes = ArrayList<ModelNotes>()
    private var adapter: AdapterNotesBeranda? = null

    fun initAdapter(){
        adapter = AdapterNotesBeranda(listNotes) { item: ModelNotes -> onClickItem(item) }
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        rcNotes.layoutManager = layoutManager
        rcNotes.adapter = adapter
    }

    fun setAdMobBanner() {
        totalPoint.value = "Total Point = ${savedData?.getDataUser()?.totalPoin.toString()}"

        MobileAds.initialize(activity) {}
        adView.loadAd(AdRequest.Builder().build())
    }

    fun onClickAdmob(){
        val intent = Intent(activity, MyService::class.java)
        intent.putExtra(Constant.referenceToken, savedData?.getDataUser()?.token)
        activity?.startService(intent)
    }

    fun onClickAddNotes(){
        navController.navigate(R.id.addNotesFragment)
    }

    fun getTotalUser() {
        totalUser.value = "Jumlah User = ${savedData?.getKeyString(Constant.totalUser)?:0}"

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    var size = 0

                    for ((totalSize) in result.children.withIndex()) {
                        size = totalSize
                    }

                    if (size.toString() != savedData?.getKeyString(Constant.totalUser)){
                        savedData?.setDataString(size.toString(), Constant.totalUser)
                        totalUser.value = "Jumlah User = $size"
                    }
                }
            }
        }

        FirebaseUtils.getDataObject(
            Constant.referenceUser
            , valueEventListener
        )
    }

    fun getPricePoint() {
        hargaPoint.value = "Harga Point = ${savedData?.getKeyString(Constant.pricePoint)?:0} rupiah"

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    val data = result.getValue(Int::class.java)

                    if (data.toString() != savedData?.getKeyString(Constant.pricePoint)){
                        savedData?.setDataString(data.toString(), Constant.pricePoint)
                        hargaPoint.value = "Harga Point = ${savedData?.getKeyString(Constant.pricePoint)?:0} rupiah"
                    }
                }
            }
        }

        FirebaseUtils.getDataObject(
            Constant.pricePoint
            , valueEventListener
        )
    }

    fun getListNotes(username: String) {
        isShowLoading.value = true

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                isShowLoading.value = false
                if (listNotes.size == 0){
                    message.value = "Nothing notes to show"
                }
                else{
                    message.value = ""
                }
            }

            override fun onDataChange(result: DataSnapshot) {
                isShowLoading.value = false
                if (result.exists()) {
                    for (snapshot in result.children) {
                        val data = snapshot.getValue(ModelNotes::class.java)
                        data?.let {
                            listNotes.add(it)
                            adapter?.notifyDataSetChanged()
                        }
                    }
                } else {
                    if (listNotes.size == 0){
                        message.value = "Nothing notes to show"
                    }
                    else{
                        message.value = ""
                    }
                }
            }
        }

        FirebaseUtils.getData1Child(
            Constant.referenceNotes
            , username
            , valueEventListener
        )
    }

    private fun onClickItem(item: ModelNotes){
        val bundle = Bundle()
        val cariFragment = EditNotesFragment()
        bundle.putParcelable("dataNotes", item)
        cariFragment.arguments = bundle
        navController.navigate(R.id.editNotesFragment, bundle)
    }

    fun getTotalPoint(username: String) {
        totalPoint.value = "Total Point = ${savedData?.getDataUser()?.totalPoin.toString()}"

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    val data = result.getValue(ModelUser::class.java)

                    if (data?.totalPoin != savedData?.getDataUser()?.totalPoin){
                        savedData?.setDataObject(data, Constant.referenceUser)
                        totalPoint.value = "Total Point = ${data?.totalPoin.toString()}"
                    }
                }
            }
        }

        FirebaseUtils.refreshDataWith1ChildObject1(
            Constant.referenceUser
            , username
            , valueEventListener
        )
    }
}