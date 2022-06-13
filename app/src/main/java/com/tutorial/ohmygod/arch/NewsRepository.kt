package com.tutorial.ohmygod.arch

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.tutorial.ohmygod.arch.paging3.BreakingNewsPagingSource
import com.tutorial.ohmygod.arch.paging3.SearchNewsPagingSource
import com.tutorial.ohmygod.db.AppDatabase
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.JsonResponse
import com.tutorial.ohmygod.db.NewsApiService
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




    /*@OptIn(ExperimentalPagingApi::class)
    override fun getMediatorPagingNews(): LiveData<PagingData<Article>> {
        return Pager(
            config = PagingConfig(20, enablePlaceholders = false),
            remoteMediator =  BNRemoteMediator(newsApi, db) ,
            pagingSourceFactory = { SearchNewsPagingSource(newsApi, "") }//db.getBNMediatorDao().getAll()
        ).liveData

    }*/


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

    override fun getAllNews(): Flow<List<Article>> = db.getAppDao().getAllNews()

    override suspend fun checkIfExist(data: String): Int = db.getAppDao().checkIfExists(data)

    override suspend fun deleteArticle(article: Article) = db.getAppDao().deleteArticle(article)

    override suspend fun insertArticle(article: Article) = db.getAppDao().insertArticle(article)


}