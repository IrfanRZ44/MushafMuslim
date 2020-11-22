package com.exomatik.zcodex.ui.main.beranda

import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import com.exomatik.zcodex.databinding.FragmentBerandaBinding
import com.exomatik.zcodex.utils.FirebaseUtils

class BerandaFragment : BaseFragmentBind<FragmentBerandaBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_beranda
    lateinit var viewModel: BerandaViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = BerandaViewModel(findNavController(), savedData, activity, bind.rcNotes, bind.btnRewardedAds)
        bind.viewModel = viewModel

        viewModel.initAdapter()
        viewModel.setAdMobBanner()
        viewModel.getTotalUser()
        val revenue = savedData.getDataApps()?.totalRevenue
        if (revenue != null && revenue > 1000){
            viewModel.getTotalTransaction(revenue)
        }
        else{
            viewModel.message.value = "Mohon mulai ulang aplikasi"
        }

        savedData.getDataUser()?.username?.let {
            viewModel.getListNotes(it)
            viewModel.getTotalPoin(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseUtils.stopRefresh()
    }
}