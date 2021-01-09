package id.exomatik.mushafmuslim.ui.main.detailShirah

import androidx.navigation.fragment.findNavController
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import id.exomatik.mushafmuslim.databinding.FragmentDetailShirahBinding
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.RetrofitUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailShirahFragment : BaseFragmentBind<FragmentDetailShirahBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_detail_shirah
    private lateinit var viewModel: DetailShirahViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = DetailShirahViewModel(findNavController(), savedData, activity, context)
        bind.viewModel = viewModel

        viewModel.dataShirah.value = this.arguments?.getParcelable(Constant.referenceShirah)

        try {
            RetrofitUtils.downloadPDF(viewModel.dataShirah.value?.shirah?:throw Exception("Error, data tidak ditemukan"),
                object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        viewModel.isShowLoading.value = false

                        bind.pdfView.fromStream(response.body()?.byteStream())
                            .defaultPage(0)
                            .load()
                    }

                    override fun onFailure(
                        call: Call<ResponseBody?>,
                        t: Throwable
                    ) {
                        viewModel.isShowLoading.value = false
                        viewModel.message.value = t.message
                    }
                })
        }catch (e: Exception){
            viewModel.message.value = e.message
        }
    }
}