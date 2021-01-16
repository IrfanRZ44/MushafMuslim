package id.exomatik.mushafmuslim.ui.main.riwayatPenarikan

import androidx.navigation.fragment.findNavController
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.databinding.FragmentRiwayatBinding

class RiwayatFragment : BaseFragmentBind<FragmentRiwayatBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_riwayat
    lateinit var viewModel: RiwayatViewModel

    override fun myCodeHere() {
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = Constant.riwayatPenarikan

        bind.lifecycleOwner = this
        viewModel = RiwayatViewModel(findNavController(), activity, bind.rcPenarikan)
        bind.viewModel = viewModel
        viewModel.initAdapter()

        val username = savedData.getDataUser()?.username

        if (!username.isNullOrEmpty()){
            viewModel.getDataPenarikan(username)
        }
        else{
            viewModel.message.value = "Error, terjadi kesalahan database"
        }
    }
}