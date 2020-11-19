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
import java.text.NumberFormat
import java.util.*

class BerandaViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val adView: AdView,
    private val rcNotes: RecyclerView
    ) : BaseViewModel() {
    val totalPoin = MutableLiveData<String>()
    val totalUser = MutableLiveData<String>()
    val hargaPoin = MutableLiveData<String>()
    val userRevenue = MutableLiveData<String>()
    val totalTransaction = MutableLiveData<String>()
    val totalRevenue = MutableLiveData<String>()
    val lastUpdated = MutableLiveData<String>()
    val btnRewardedAds = MutableLiveData<String>()
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
        val format = NumberFormat.getCurrencyInstance()

        totalRevenue.value = "Total Pendapatan = ${format.format(savedData?.getDataApps()?.totalRevenue)}"
        lastUpdated.value = "(Di update pada ${savedData?.getDataApps()?.lastUpdated})"
        totalPoin.value = "Total Poin = ${savedData?.getDataUser()?.totalPoin}"
        btnRewardedAds.value = "Rewarded Ads ${savedData?.getKeyInt(Constant.adsLeft)}"

        MobileAds.initialize(activity) {}
        adView.loadAd(AdRequest.Builder().build())
    }

    fun onClickAdmob(){
        var ads = savedData?.getKeyInt(Constant.adsLeft)?:0

        if (ads <= 0){
            message.value = "Maaf, batas maksimal poin sudah tercapai hari ini"
        }
        else{
            ads -= 1
            btnRewardedAds.value = "Rewarded Ads $ads"
            savedData?.setDataInt(ads, Constant.adsLeft)
            val intent = Intent(activity, MyService::class.java)
            intent.putExtra(Constant.referenceToken, savedData?.getDataUser()?.token)
            activity?.startService(intent)
        }
    }

    fun onClickAddNotes(){
        navController.navigate(R.id.addNotesFragment)
    }

    fun getTotalUser() {
        totalUser.value = "Jumlah User = ${savedData?.getKeyLong(Constant.totalUser)?:0}"

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    val size = result.childrenCount

                    if (size != savedData?.getKeyLong(Constant.totalUser)){
                        savedData?.setDataLong(size, Constant.totalUser)
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

    fun getTotalTransaction(revenue: Long) {
        val format = NumberFormat.getCurrencyInstance()
        val transaction = savedData?.getKeyLong(Constant.totalTransaksiIklan)
        totalTransaction.value = "Total Transaksi Klik Iklan = $transaction"

        if (transaction != null && transaction > 1){
            val harga =  (revenue / transaction)
            val userRev = savedData?.getDataUser()?.totalPoin?.times(harga)

            hargaPoin.value = "Harga Poin = Rp$harga"
            userRevenue.value = "Penghasilan User = ${format.format(userRev)}"
        }

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    val transactionUpdate = result.childrenCount

                    if (transactionUpdate != savedData?.getKeyLong(Constant.totalTransaksiIklan)){
                        savedData?.setDataLong(transactionUpdate, Constant.totalTransaksiIklan)
                        totalTransaction.value = "Total Transaksi Klik Iklan = $transactionUpdate"

                        val revenue2 = savedData?.getDataApps()?.totalRevenue?:0
                        val harga2 =  (revenue2 / transactionUpdate)
                        val userRev2 = savedData?.getDataUser()?.totalPoin?.times(harga2)

                        hargaPoin.value = "Harga Poin = Rp$harga2"
                        userRevenue.value = "Penghasilan User = ${format.format(userRev2)}"
                    }
                }
            }
        }

        FirebaseUtils.getDataObject(
            Constant.referenceTransaction
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

    fun getTotalPoin(username: String) {
        totalPoin.value = "Total Poin = ${savedData?.getDataUser()?.totalPoin.toString()}"

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    val data = result.getValue(ModelUser::class.java)

                    if (data?.totalPoin != savedData?.getDataUser()?.totalPoin){
                        savedData?.setDataObject(data, Constant.referenceUser)
                        totalPoin.value = "Total Poin = ${data?.totalPoin.toString()}"
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