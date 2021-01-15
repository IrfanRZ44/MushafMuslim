package id.exomatik.mushafmuslim.ui.main.detailShirah

import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.databinding.FragmentDetailShirahBinding

class DetailShirahFragment : BaseFragmentBind<FragmentDetailShirahBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_detail_shirah
    private lateinit var viewModel: DetailShirahViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = DetailShirahViewModel(findNavController(), savedData, activity, context,
            bind.pdfView, bind.linearTimer, bind.textVerify)
        bind.viewModel = viewModel

        viewModel.dataShirah.value = this.arguments?.getParcelable(Constant.referenceShirah)
        viewModel.setUpPdfView()
        setUpAdmob()
    }

    private fun setUpAdmob(){
        MobileAds.initialize(context) {}
        val adRequest = AdRequest.Builder().build()
        bind.adView.loadAd(adRequest)
    }

    override fun onPause() {
        super.onPause()
        viewModel.timerActivate?.cancel()
    }
}