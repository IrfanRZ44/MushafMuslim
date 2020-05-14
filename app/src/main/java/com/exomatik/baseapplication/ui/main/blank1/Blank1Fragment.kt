package com.exomatik.baseapplication.ui.main.blank1

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.exomatik.baseapplication.R
import com.exomatik.baseapplication.base.BaseFragmentBind
import com.exomatik.baseapplication.databinding.FragmentBlank1Binding

class Blank1Fragment : BaseFragmentBind<FragmentBlank1Binding>() {
    private lateinit var viewModel: Blank1ViewModel

    override fun getLayoutResource(): Int = R.layout.fragment_blank1

    override fun myCodeHere() {
        setHasOptionsMenu(true)
        init()
    }

    private fun init() {
        bind.lifecycleOwner = this
        viewModel = Blank1ViewModel()
        bind.viewModel = viewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_pesan_and_kontak, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionPesan ->{
                viewModel.message.value = "Pesan"
            }
            R.id.actionKontak->{
                viewModel.message.value = "Kontak"
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

