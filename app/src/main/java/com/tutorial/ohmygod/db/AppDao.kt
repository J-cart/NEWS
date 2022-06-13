package com.tutorial.ohmygod.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface AppDao {

    @Query("SELECT * FROM news_article")
    fun getAllNews(): Flow<List<Article>>

    @Query("SELECT COUNT() FROM news_article WHERE url =:data")
    suspend fun checkIfExists(data:String):Int

    @Delete
    suspend fun deleteArticle(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article)

    @Query("DELETE FROM news_article")
    suspend fun deleteAll()







}