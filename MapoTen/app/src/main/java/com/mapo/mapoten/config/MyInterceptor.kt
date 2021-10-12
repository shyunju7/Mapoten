package com.mapo.mapoten.config

import com.mapo.mapoten.ui.activity.App
import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor() :Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
       val request = chain.request()
           .newBuilder()
           .addHeader("Authorization", App.prefs.token ?: "")
           .build()
        return chain.proceed(request)
    }

}