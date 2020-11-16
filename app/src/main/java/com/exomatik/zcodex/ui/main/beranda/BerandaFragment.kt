package com.exomatik.zcodex.ui.main.beranda

import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import com.exomatik.zcodex.databinding.FragmentBerandaBinding

class BerandaFragment : BaseFragmentBind<FragmentBerandaBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_beranda
    lateinit var viewModel: BerandaViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = BerandaViewModel(findNavController(), savedData, activity, bind.adView, bind.rcNotes)
        bind.viewModel = viewModel

        viewModel.initAdapter()
        viewModel.setAdMob()
        viewModel.getTotalUser()
        savedData.getDataUser()?.username?.let { viewModel.getListNotes(it) }
        onClick()
    }

    private fun onClick() {

    }
}