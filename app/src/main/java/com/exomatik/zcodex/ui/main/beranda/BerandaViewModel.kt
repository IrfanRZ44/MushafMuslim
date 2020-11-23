package com.exomatik.zcodex.ui.main.beranda

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelNotes
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.ui.main.editNotes.EditNotesFragment
import com.exomatik.zcodex.ui.rewardBanner.RewardBannerActivity
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BerandaViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val rcNotes: RecyclerView,
    private val btnRewardedAds: AppCompatButton
    ) : BaseViewModel() {
    val totalPoin = MutableLiveData<String>()
    val totalUser = MutableLiveData<String>()
    val hargaPoin = MutableLiveData<String>()
    val userRevenue = MutableLiveData<String>()
    val totalTransaction = MutableLiveData<String>()
    val totalRevenue = MutableLiveData<String>()
    val lastUpdated = MutableLiveData<String>()
    private var listNotes = ArrayList<ModelNotes>()
    private var adapter: AdapterNotesBeranda? = null
    private lateinit var rewardedAd: RewardedAd
    private lateinit var adCallback : RewardedAdCallback

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

        totalRevenue.value = "Saldo Perusahaan = ${format.format(savedData?.getDataApps()?.totalRevenue)}"
        lastUpdated.value = "(Di update pada ${savedData?.getDataApps()?.lastUpdated})"
        totalPoin.value = "Total Poin = ${savedData?.getDataUser()?.totalPoin}"
    }

    @SuppressLint("SimpleDateFormat")
    fun onClickAdmob(){
        val adsTimer = savedData?.getKeyString(Constant.adsTimer)

        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(Constant.timeDateFormat)
        val getCurrentTime = sdf.format(c.time)

        if (adsTimer != null && !adsTimer.isNullOrEmpty()){
            val date = sdf.parse(adsTimer)

            if (comparingTimes(getCurrentTime, adsTimer)){
                if (date != null){
                    val sdfTime = SimpleDateFormat(Constant.timeFormat)
                    val getCurrentTimer2 = sdfTime.format(date)

                    message.value = "Coba lagi pada $getCurrentTimer2"
                }
                else{
                    message.value = "Error, kesalahan format waktu"
                }
            }
            else {
                checkingCondition()
            }
        }
        else{
            checkingCondition()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun comparingTimes(waktuMulai: String, waktuDipilih: String) : Boolean{
        try {
            val time1 = SimpleDateFormat(Constant.timeDateFormat).parse(waktuMulai)
            val d = SimpleDateFormat(Constant.timeDateFormat).parse(waktuDipilih)

            val calendar1 = Calendar.getInstance()
            val calendar3 = Calendar.getInstance()

            return if (time1 != null && d != null){
                calendar1.time = time1
                calendar1.add(Calendar.DATE, 1)
                calendar3.time = d
                calendar3.add(Calendar.DATE, 1)

                val x = calendar3.time
                x.after(calendar1.time)
            } else{
                false
            }
        } catch (e: ParseException) {
            message.value = e.message
            return false
        }
    }

    private fun checkingCondition(){
        val ads = savedData?.getDataUser()?.adsLeft?:0
        val adsAlreadyNote = savedData?.getKeyBoolean(Constant.adsAlreadyNote)?:false
        val adsAlreadyVideo = savedData?.getKeyBoolean(Constant.adsAlreadyVideo)?:false

        if (ads <= 0){
            message.value = "Maaf, batas maksimal poin sudah tercapai hari ini"
        }
        else {
            if (adsAlreadyNote) {
                if (adsAlreadyVideo) {
                    val intent = Intent(activity, RewardBannerActivity::class.java)
                    activity?.startActivity(intent)
                } else {
                    if (rewardedAd.isLoaded) {
                        rewardedAd.show(activity, adCallback)
                    } else {
                        message.value = "Iklan belum tersedia"
                    }
                }
            } else {
                message.value = "Mohon untuk mengubah atau menambah notes terlebih dahulu"
            }
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
            userRevenue.value = "Saldo Anda = ${format.format(userRev)}"
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
                        userRevenue.value = "Saldo Anda = ${format.format(userRev2)}"
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

                        if (data != null && data.username == username){
                            listNotes.add(data)
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
        loadRewardedAd()

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    val data = result.getValue(ModelUser::class.java)

                    if (data?.totalPoin != savedData?.getDataUser()?.totalPoin){
                        savedData?.setDataObject(data, Constant.referenceUser)
                    }

                    totalPoin.value = "Total Poin = ${data?.totalPoin.toString()}"
                    loadRewardedAd()
                }
            }
        }

        FirebaseUtils.refreshDataWith1ChildObject1(
            Constant.referenceUser
            , username
            , valueEventListener
        )
    }

    @SuppressLint("SetTextI18n")
    private fun loadRewardedAd(){
        if (savedData != null){
            isShowLoading.value = true
            rewardedAd = RewardedAd(activity, Constant.defaultRewardedID)

            val adLoadCallback = object: RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    isShowLoading.value = false
                    onClickRewardedAd()
                }
                override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                    isShowLoading.value = false
                    message.value = "Iklan gagal dimuat"
                }
            }
            rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        }
        btnRewardedAds.text = "Rewarded Ads ${savedData?.getDataUser()?.adsLeft}"
    }

    private fun onClickRewardedAd(){
        adCallback = object: RewardedAdCallback() {
            var isRewarded = false
            var amount = 5
            override fun onRewardedAdOpened() {
            }
            override fun onRewardedAdClosed() {
                if (isRewarded){
                    val intent = Intent(activity, RewardBannerActivity::class.java)
                    activity?.startActivity(intent)
                    savedData?.setDataBoolean(true, Constant.adsAlreadyVideo)
                    savedData?.setDataInt(amount, Constant.adsRewardVideo)
                }
            }
            override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                amount = reward.amount
                isRewarded = true
            }
            override fun onRewardedAdFailedToShow(adError: AdError) {
                message.value = "Iklan gagal di tampilkan"
            }
        }
    }
}