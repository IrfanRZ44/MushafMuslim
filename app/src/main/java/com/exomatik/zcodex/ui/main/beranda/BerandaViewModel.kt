@file:Suppress("DEPRECATION")

package com.exomatik.zcodex.ui.main.beranda

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelNotes
import com.exomatik.zcodex.model.ModelTransaction
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.ui.main.editNotes.EditNotesFragment
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.Constant.defaultRewardedID
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    private lateinit var mRewardedVideoAd : RewardedVideoAd

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

    fun setAdMobBanner() {
        totalPoint.value = "Total Point = ${savedData?.getDataUser()?.totalPoin.toString()}"
        hargaPoint.value = "Price Point = N/A"

        MobileAds.initialize(activity) {}
        adView.loadAd(AdRequest.Builder().build())
    }

    @Suppress("DEPRECATION")
    fun setUpRewardedAds(){
        MobileAds.initialize(activity) {}
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)

        mRewardedVideoAd.loadAd(defaultRewardedID,
            AdRequest.Builder().build())

        mRewardedVideoAd.rewardedVideoAdListener = object: RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {
            }

            override fun onRewardedVideoAdLeftApplication() {
            }

            override fun onRewardedVideoAdLoaded() {
                message.value = "Ads Reward is ready"
            }

            override fun onRewardedVideoAdOpened() {
            }

            override fun onRewardedVideoCompleted() {
            }

            override fun onRewarded(reward: RewardItem?) {
                saveRewarded()
            }

            override fun onRewardedVideoStarted() {
            }

            override fun onRewardedVideoAdFailedToLoad(error: Int) {
                message.value = "Failed to load ads"
            }

        }
    }

    fun onClickAdmob(){
        if (mRewardedVideoAd.isLoaded) {
            mRewardedVideoAd.show()
        }
        else {
            message.value = "Ads it's not loaded yet"
        }
    }

    private fun saveRewarded(){
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

    @SuppressLint("SetTextI18n")
    private fun addTotalPoint(dataUser: ModelUser, username: String, point: Long){
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false
                message.value = "Adding 1 point"
                dataUser.totalPoin = point
                savedData?.setDataObject(dataUser, Constant.referenceUser)
                totalPoint.value = "Total Point = ${savedData?.getDataUser()?.totalPoin.toString()}"
                mRewardedVideoAd.loadAd(defaultRewardedID, AdRequest.Builder().build())
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

    fun onClickAddNotes(){
        navController.navigate(R.id.addNotesFragment)
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