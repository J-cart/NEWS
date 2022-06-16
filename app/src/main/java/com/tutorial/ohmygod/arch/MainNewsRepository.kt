package com.tutorial.ohmygod.arch

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.JsonResponse
import com.tutorial.ohmygod.db.SavedArticle
import com.tutorial.ohmygod.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MainNewsRepository {

    suspend fun getBreakingNews(country:String, page:Int):Resource<JsonResponse>
    suspend fun getSearchNews(query:String, page:Int):Resource<JsonResponse>

    //TODO--- add paging 3 GET function
    fun getPagingNews():LiveData<PagingData<Article>>
    fun getPagingSearchNews(query: String):LiveData<PagingData<Article>>
    fun getMediatorPagingNews():LiveData<PagingData<Article>>
    suspend fun getAllItemsCount():Int

    fun getAllSavedNews(): Flow<List<SavedArticle>>
    suspend fun deleteSavedArticle(article: SavedArticle)
    suspend fun saveArticle(article: SavedArticle)
    suspend fun checkIfSavedExist(data:String):Int

}