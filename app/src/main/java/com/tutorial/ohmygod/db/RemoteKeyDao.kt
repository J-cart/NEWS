package com.tutorial.ohmygod.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeys(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM breaking_news_remote_keys WHERE id =:id")
    suspend fun getRemotekeys(id:String):RemoteKey?

    @Query("DELETE FROM breaking_news_remote_keys")
    suspend fun deleteAllKeys()
}