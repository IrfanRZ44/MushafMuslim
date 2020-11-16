package com.exomatik.zcodex.ui.main.addNotes

import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.databinding.FragmentAddNotesBinding

class AddNotesFragment : BaseFragmentBind<FragmentAddNotesBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_add_notes
    lateinit var viewModel: AddNotesViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = AddNotesViewModel(
            activity,
            savedData,
            findNavController()
        )
        bind.viewModel = viewModel
    }
}