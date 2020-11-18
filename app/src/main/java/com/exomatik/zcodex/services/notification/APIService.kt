package com.exomatik.zcodex.services.notification

import com.exomatik.zcodex.services.notification.model.MyResponse
import com.exomatik.zcodex.services.notification.model.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers("Content-Type:application/json", "Authorization:key=AAAA4M-K5aI:APA91bEGj9MvMfafDazs_soZd8dmL3Uwus1f2f43DLYRMBNLIMPruBRCIrb4MjdzpBntV5ZMVbk5hxL-7ExstFhGmOZ50WLtBtq7_BGbfE5lAliYSCZgDw4vd4oNHDSDoy-vou5mgnY8")
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender?): Call<MyResponse?>?
}