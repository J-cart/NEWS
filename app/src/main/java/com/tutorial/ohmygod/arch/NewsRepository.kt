package com.tutorial.ohmygod.arch

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.tutorial.ohmygod.arch.paging3.BNRemoteMediator
import com.tutorial.ohmygod.arch.paging3.SearchNewsPagingSource
import com.tutorial.ohmygod.db.AppDatabase
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.NewsApiService
import com.tutorial.ohmygod.db.SavedArticle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepository @Inject constructor(private val newsApi: NewsApiService, val db: AppDatabase) :
    MainNewsRepository {

    override fun getPagingSearchNews(query: String) =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SearchNewsPagingSource(newsApi, query) }
        ).liveData


    @OptIn(ExperimentalPagingApi::class)
    override fun getMediatorPagingNews(): LiveData<PagingData<Article>> {
        return Pager(
            config = PagingConfig(20, enablePlaceholders = false),
            remoteMediator = BNRemoteMediator(newsApi, db),
            pagingSourceFactory = { db.getAppDao().getAllMediatorNews() }
        ).liveData
    }

    override suspend fun getAllItemsCount(): Int = db.getAppDao().getAllItemCount()
    //////////////////////////////////////////////////////////////////////////////////////

    override fun getAllSavedNews(): Flow<List<SavedArticle>> =
        db.getSavedNewsDao().getAllSavedNews()

    override suspend fun deleteSavedArticle(article: SavedArticle) {
        db.getSavedNewsDao().deleteSavedArticle(article)
    }

    override suspend fun saveArticle(article: SavedArticle) {
        db.getSavedNewsDao().insertArticle(article)
    }

    override suspend fun checkIfSavedExist(data: String): Int =
        db.getSavedNewsDao().checkIfExists(data)

    override suspend fun deleteAllSaved() = db.getSavedNewsDao().deleteAll()

}