package id.exomatik.mushafmuslim.ui.main.shirah

import androidx.navigation.fragment.findNavController
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import id.exomatik.mushafmuslim.databinding.FragmentShirahBinding

class ShirahFragment : BaseFragmentBind<FragmentShirahBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_shirah
    lateinit var viewModel: ShirahViewModel

    override fun myCodeHere() {
        supportActionBar?.hide()
        bind.lifecycleOwner = this
        viewModel = ShirahViewModel(findNavController(), savedData, activity, bind.rcShirah)
        bind.viewModel = viewModel
        viewModel.initAdapter()
        viewModel.getDataBookmark()
    }
}