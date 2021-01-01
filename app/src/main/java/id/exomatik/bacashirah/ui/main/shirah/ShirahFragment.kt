package id.exomatik.bacashirah.ui.main.shirah

import androidx.navigation.fragment.findNavController
import id.exomatik.bacashirah.R
import id.exomatik.bacashirah.base.BaseFragmentBind
import id.exomatik.bacashirah.databinding.FragmentShirahBinding

class ShirahFragment : BaseFragmentBind<FragmentShirahBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_shirah
    lateinit var viewModel: ShirahViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = ShirahViewModel(findNavController(), savedData, activity, bind.rcShirah)
        bind.viewModel = viewModel
        viewModel.initAdapter()
        viewModel.getDataBookmark()
    }
}