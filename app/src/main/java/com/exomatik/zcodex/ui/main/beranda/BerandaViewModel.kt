package com.exomatik.zcodex.ui.main.beranda

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelTransaction
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.exomatik.zcodex.R
import com.exomatik.zcodex.model.ModelNotes
import com.exomatik.zcodex.ui.main.editNotes.EditNotesFragment

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

    @SuppressLint("SimpleDateFormat")
    private val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd-M-yyyy"))
    } else {
        SimpleDateFormat("dd-M-yyyy").format(Date())
    }

    fun initAdapter(){
        adapter = AdapterNotesBeranda(listNotes) { item: ModelNotes -> onClickItem(item) }
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        rcNotes.layoutManager = layoutManager
        rcNotes.adapter = adapter
    }

    fun setAdMob() {
        totalPoint.value = "Total Point = ${savedData?.getDataUser()?.totalPoin.toString()}"
        hargaPoint.value = "Price Point = N/A"

        MobileAds.initialize(activity) {}

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
            }

            override fun onAdFailedToLoad(error : LoadAdError) {
                message.value = error.toString()
            }

            override fun onAdOpened() {
                onClickBanner()
            }

            override fun onAdClicked() {
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }

    fun onClickBanner(){
        val dataUser = savedData?.getDataUser()
        val username = savedData?.getDataUser()?.username

        if (dataUser != null && !username.isNullOrEmpty()){
            isShowLoading.value = true
            val dataTransaction = ModelTransaction("", tglSekarang, username, 1)

            val onCompleteListener = OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    val point = dataUser.totalPoin + 1

                    addTotalPoint(dataUser, username, point)
                } else {
                    isShowLoading.value = false
                    message.value = "Failed to add point"
                }
            }

            val onFailureListener = OnFailureListener { result ->
                isShowLoading.value = false
                message.value = result.message
            }

            FirebaseUtils.setValueUniqueTransaction(
                Constant.referenceTransaction
                , dataTransaction
                , onCompleteListener
                , onFailureListener
            )
        }
        else{
            message.value = "Failed to add point"
        }
    }

    fun onClickAddNotes(){
        navController.navigate(R.id.addNotesFragment)
    }

    @Suppress("DEPRECATION")
    fun onClickInterstisial(){
        MobileAds.initialize(activity,
            "ca-app-pub-3940256099942544~3347511713")

        val mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                } else {
                    message.value = "Ads is not loaded yet"
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                message.value = adError.toString()
            }

            override fun onAdOpened() {
                onClickBanner()
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addTotalPoint(dataUser: ModelUser, username: String, point: Long){
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false
                message.value = "Adding 1 point"
                dataUser.totalPoin = point
                savedData?.setDataObject(dataUser, Constant.referenceUser)
                totalPoint.value = "Total Point = ${savedData?.getDataUser()?.totalPoin.toString()}"

                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            } else {
                isShowLoading.value = false
                message.value = "Failed to add point"
            }
        }

        val onFailureListener = OnFailureListener { result ->
            isShowLoading.value = false
            message.value = result.message
        }

        FirebaseUtils.setValueWith3ChildInt(
            Constant.referenceUser
            , username
            , Constant.referenceTotalPoin
            , point
            , onCompleteListener
            , onFailureListener
        )
    }

    fun getTotalUser() {
        isShowLoading.value = true

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                isShowLoading.value = false
                totalUser.value = "Total User = 0"
            }

            override fun onDataChange(result: DataSnapshot) {
                isShowLoading.value = false
                if (result.exists()) {
                    for ((totalSize) in result.children.withIndex()) {
                        totalUser.value = "Total User = $totalSize"
                    }
                } else {
                    isShowLoading.value = false
                    totalUser.value = "Total User = 0"
                }
            }
        }

        FirebaseUtils.getDataObject(
            Constant.referenceUser
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
}