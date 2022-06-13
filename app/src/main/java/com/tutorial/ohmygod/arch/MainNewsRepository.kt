package com.tutorial.ohmygod.arch

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.JsonResponse
import com.tutorial.ohmygod.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MainNewsRepository {

    suspend fun getBreakingNews(country:String, page:Int):Resource<JsonResponse>
    suspend fun getSearchNews(query:String, page:Int):Resource<JsonResponse>

    //TODO--- add paging 3 GET function
    fun getPagingNews():LiveData<PagingData<Article>>
    fun getPagingSearchNews(query: String):LiveData<PagingData<Article>>
//    fun getMediatorPagingNews():LiveData<PagingData<Article>>

    fun getAllNews(): Flow<List<Article>>
    suspend fun deleteArticle(article: Article)
    suspend fun insertArticle(article: Article)
    suspend fun checkIfExist(data:String):Int

}