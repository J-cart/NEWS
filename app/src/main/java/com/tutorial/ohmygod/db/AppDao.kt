package com.tutorial.ohmygod.db

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface AppDao {

//PAGING
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    @Query("SELECT * FROM news_article")
    fun getAllMediatorNews(): PagingSource<Int, Article>

    @Query("DELETE FROM news_article")
    suspend fun deleteAll()




}