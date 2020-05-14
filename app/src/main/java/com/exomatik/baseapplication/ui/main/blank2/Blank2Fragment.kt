package com.exomatik.baseapplication.ui.main.blank2

import com.exomatik.baseapplication.R
import com.exomatik.baseapplication.base.BaseFragmentBind
import com.exomatik.baseapplication.databinding.FragmentBlank2Binding

class Blank2Fragment : BaseFragmentBind<FragmentBlank2Binding>() {
    private lateinit var viewModel: Blank2ViewModel

    override fun getLayoutResource(): Int = R.layout.fragment_blank2

    override fun myCodeHere() {
        init()
    }

    private fun init() {
        bind.lifecycleOwner = this
        viewModel = Blank2ViewModel()
        bind.viewModel = viewModel
    }

}

