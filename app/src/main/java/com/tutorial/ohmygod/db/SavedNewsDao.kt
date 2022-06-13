package com.tutorial.ohmygod.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedNewsDao {


    @Query("SELECT * FROM saved_article")
    fun getAllSavedNews(): Flow<List<SavedArticle>>

    @Query("SELECT COUNT() FROM saved_article WHERE url =:data")
    suspend fun checkIfExists(data:String):Int

    @Delete
    suspend fun deleteSavedArticle(article: SavedArticle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: SavedArticle)

    @Query("DELETE FROM saved_article")
    suspend fun deleteAll()
}