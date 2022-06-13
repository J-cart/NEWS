package com.tutorial.ohmygod.db

import com.tutorial.ohmygod.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") country:String = "us",
        @Query("page") page:Int ,//TODO -- FIX THIS BRUH
        @Query("apiKey") apiKey:String =API_KEY
    ):Response<JsonResponse>


    @GET("v2/everything")
    suspend fun getSearchNews(
        @Query("q") query:String,
        @Query("page") page:Int ,
        @Query("apiKey") apiKey:String =API_KEY
    ):Response<JsonResponse>
}