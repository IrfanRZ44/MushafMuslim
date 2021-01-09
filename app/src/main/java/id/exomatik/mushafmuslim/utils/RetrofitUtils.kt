package id.exomatik.mushafmuslim.utils

import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtils {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constant.defaultBasePdf)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api: RetrofitApi = retrofit.create(RetrofitApi::class.java)

    fun downloadPDF(url: String,
                 callback: Callback<ResponseBody?>){
        val call = api.downloadPDF(url)
        call?.enqueue(callback)
    }
}