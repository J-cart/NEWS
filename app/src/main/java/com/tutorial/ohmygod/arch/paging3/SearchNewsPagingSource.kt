package com.tutorial.ohmygod.arch.paging3

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.NewsApiService
import retrofit2.HttpException
import java.io.IOException


private const val STARTING_PAGE_INDEX = 1
class SearchNewsPagingSource(
   private val api:NewsApiService,
    private val query:String
):PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        //check the paging3 library codelab for more info on this
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key?: STARTING_PAGE_INDEX
        return try {
            val response = api.getSearchNews(query = query, page = position)
            val result = response.body()?.articles
            var realResult = emptyList<Article>()

            result?.let {
                realResult = it
            }

            Log.d("SEARCHPAGINGSOURCEIO","Data retrieved page $position")
            LoadResult.Page(
                data = realResult,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position -1,
                nextKey = if (realResult.isEmpty()) null else position + 1
            )
        }catch (e: IOException){
            Log.d("SEARCHPAGINGSOURCEIO", "$e")
            LoadResult.Error(e)

        }catch (e: HttpException){
            Log.d("SEARCHPAGINGSOURCEHTTP", "$e")
            LoadResult.Error(e)
        }
    }
}