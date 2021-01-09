package id.exomatik.mushafmuslim.utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface RetrofitApi {
    @Streaming
    @GET
    fun downloadPDF(@Url url: String?): Call<ResponseBody?>?
}