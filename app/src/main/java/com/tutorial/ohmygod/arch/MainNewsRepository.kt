package com.tutorial.ohmygod.arch

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.SavedArticle
import kotlinx.coroutines.flow.Flow

interface MainNewsRepository {

    fun getPagingSearchNews(query: String):LiveData<PagingData<Article>>
    fun getMediatorPagingNews():LiveData<PagingData<Article>>
    suspend fun getAllItemsCount():Int

    fun getAllSavedNews(): Flow<List<SavedArticle>>
    suspend fun deleteSavedArticle(article: SavedArticle)
    suspend fun saveArticle(article: SavedArticle)
    suspend fun checkIfSavedExist(data:String):Int
    suspend fun deleteAllSaved()

}