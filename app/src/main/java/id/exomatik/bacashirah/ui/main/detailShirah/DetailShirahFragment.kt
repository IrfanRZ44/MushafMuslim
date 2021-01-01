package id.exomatik.bacashirah.ui.main.detailShirah

import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import es.voghdev.pdfviewpager.library.RemotePDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import es.voghdev.pdfviewpager.library.util.FileUtil
import id.exomatik.bacashirah.R
import id.exomatik.bacashirah.base.BaseFragmentBind
import id.exomatik.bacashirah.databinding.FragmentDetailShirahBinding
import id.exomatik.bacashirah.utils.Constant
import id.exomatik.bacashirah.utils.showLog
import java.lang.Exception

class DetailShirahFragment : BaseFragmentBind<FragmentDetailShirahBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_detail_shirah
    lateinit var viewModel: DetailShirahViewModel
    private lateinit var adapter : PDFPagerAdapter
    private lateinit var remotePDFViewPager: RemotePDFViewPager

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = DetailShirahViewModel(findNavController(), savedData, activity, context)
        bind.viewModel = viewModel

        viewModel.dataShirah.value = this.arguments?.getParcelable(Constant.referenceShirah)

        viewModel.isShowLoading.value = true

        remotePDFViewPager = RemotePDFViewPager(context, "https://firebasestorage.googleapis.com/v0/b/bacashirah-c9c9e.appspot.com/o/Docs%2FLembar%20Nilai%20Kompren%20(Irfan%20rosal).pdf?alt=media&token=e22c91f6-9e07-47b4-accb-65e7d36e362e",
            object : DownloadFile.Listener{
                override fun onSuccess(url: String?, destinationPath: String?) {
                    showLog("Succes")
                    adapter = PDFPagerAdapter(context, FileUtil.extractFileNameFromURL(url))
                    remotePDFViewPager.adapter = adapter
                    bind.shirahDocs.removeAllViewsInLayout()
                    bind.shirahDocs.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    viewModel.isShowLoading.value = false
                }

                override fun onFailure(e: Exception?) {
                    viewModel.message.value = "Gagal Membuka File"
                    viewModel.isShowLoading.value = false
                }

                override fun onProgressUpdate(progress: Int, total: Int) {
                    viewModel.isShowLoading.value = true
                }

            }
        )
    }
}