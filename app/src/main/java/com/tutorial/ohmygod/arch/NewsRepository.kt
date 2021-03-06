package com.tutorial.ohmygod.arch

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.tutorial.ohmygod.arch.paging3.BNRemoteMediator
import com.tutorial.ohmygod.arch.paging3.BreakingNewsPagingSource
import com.tutorial.ohmygod.arch.paging3.SearchNewsPagingSource
import com.tutorial.ohmygod.db.*
import com.tutorial.ohmygod.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepository @Inject constructor(private val newsApi: NewsApiService, val db: AppDatabase) :
    MainNewsRepository {


    //TODO -- GET THE RESULT FROM THE PAGING-SOURCE WITH PAGER() AND RETURN A RESPONSE<T>
    override fun getPagingNews() =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { BreakingNewsPagingSource(newsApi) }
        ).liveData


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

    //////////////////////////////////////////////////////////////////////////////////////////


    var responseStatus: JsonResponse? = null
    override suspend fun getBreakingNews(
        country: String,
        page: Int
    ): Resource<JsonResponse> =
        try {
            val result = newsApi.getBreakingNews(country, page)
            val response = result.body()!!
            this.responseStatus = response
            Resource.Successful(response)
        } catch (e: Exception) {
            Resource.Failure(
                e.message ?: responseStatus?.status
                ?: "An unknown error that couldn't catch occurred"
            )
        }

    override suspend fun getSearchNews(query: String, page: Int): Resource<JsonResponse> =
        try {
            val result = newsApi.getSearchNews(query, page)
            val response = result.body()!!
            this.responseStatus = responseStatus
            Resource.Successful(response)
        } catch (e: Exception) {
            Resource.Failure(
                e.message ?: responseStatus?.status
                ?: "An unknown error that couldn't catch occurred"
            )
        }

}