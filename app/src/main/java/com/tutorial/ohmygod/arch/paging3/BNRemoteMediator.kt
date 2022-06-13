
package com.tutorial.ohmygod.arch.paging3

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.tutorial.ohmygod.db.AppDatabase
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.NewsApiService
import com.tutorial.ohmygod.db.RemoteKey
import retrofit2.HttpException
import java.io.IOException


private const val STARTING_PAGE_INDEX = 1


@OptIn(ExperimentalPagingApi::class)
class BNRemoteMediator(
    private val api: NewsApiService,
    private val db: AppDatabase
) : RemoteMediator<Int, Article>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {

        val page =
            when (val pageKeyData = getKeyPageData(loadType, state)) {
                is MediatorResult.Success -> return pageKeyData
                else -> pageKeyData as Int
            }
        return try {
            val response = api.getBreakingNews(page = page)
            val result = response.body()?.articles
            val isEndOfList = result!!.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.getBNMediatorDao().deleteAll()
                    db.getRemoteKeysDao().deleteAllKeys()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

                val keys = result.map {
                    RemoteKey(id = it.title!!, prevKey = prevKey, nextKey = nextKey)
                }

                db.getRemoteKeysDao().insertAllKeys(keys)
                db.getBNMediatorDao().insertAll(result)

            }
            MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, Article>): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosest(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey =
                    remoteKeys?.prevKey ?: MediatorResult.Success(endOfPaginationReached = false)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey =
                    remoteKeys?.nextKey ?: MediatorResult.Success(endOfPaginationReached = false)
                nextKey
            }
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, Article>): RemoteKey? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { article ->
                article.title?.let {
                    db.getRemoteKeysDao().getRemotekeys(it)
                }
            }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, Article>): RemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNullOrEmpty() }
            ?.data?.lastOrNull()
            ?.let { article ->
                article.title?.let { it -> db.getRemoteKeysDao().getRemotekeys(it) }
            }
    }

    private suspend fun getRemoteKeyClosest(state: PagingState<Int, Article>): RemoteKey? {
        return state.anchorPosition
            ?.let { position ->
                state.closestItemToPosition(position)?.title
                    ?.let {
                        db.getRemoteKeysDao().getRemotekeys(it)
                    }
            }
    }


}

