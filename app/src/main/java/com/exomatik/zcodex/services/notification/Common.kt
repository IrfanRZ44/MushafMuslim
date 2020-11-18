package com.exomatik.zcodex.services.notification


object Common {
    private val baseUrl = "https://fcm.googleapis.com/"
    val fCMClient: APIService
        get() = RetrofitClient.getClient(baseUrl).create(APIService::class.java)
}