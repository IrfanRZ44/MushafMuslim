package com.exomatik.zcodex.ui.main.editNotes

import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.databinding.FragmentEditNotesBinding
import com.exomatik.zcodex.model.ModelNotes

class EditNotesFragment : BaseFragmentBind<FragmentEditNotesBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_edit_notes
    lateinit var viewModel: EditNotesViewModel

    override fun myCodeHere() {
        val dataNotes = this.arguments?.getParcelable<ModelNotes>("dataNotes")
        bind.lifecycleOwner = this

        viewModel = EditNotesViewModel(
            activity,
            savedData,
            findNavController(),
            dataNotes
        )
        bind.viewModel = viewModel

        dataNotes?.let { viewModel.setData(it) }
    }
}