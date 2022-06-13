package com.tutorial.ohmygod.arch.paging3

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tutorial.ohmygod.db.NewsApiService
import com.tutorial.ohmygod.db.Article
import retrofit2.HttpException
import java.io.IOException


private const val STARTING_PAGE_INDEX = 1
class BreakingNewsPagingSource (private val api: NewsApiService): PagingSource<Int, Article>() {
   // TODO DO ALL THE TRY CATCH LOGIC AND RETURN THE RESPONSE BODY
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
       //check the paging3 library codelab for more info on this
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key?: STARTING_PAGE_INDEX
        return try {
            val response = api.getBreakingNews(page = position)
            val result = response.body()?.articles

            LoadResult.Page(
                data = result!!,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position -1,
                nextKey = if (result.isEmpty()) null else position + 1
            )
        }catch (e: IOException){
            Log.d("PAGINGSOURCEIO", "$e")
           LoadResult.Error(e)

        }catch (e:HttpException){
            Log.d("PAGINGSOURCEHTTP", "$e")
            LoadResult.Error(e)
        }
    }
}