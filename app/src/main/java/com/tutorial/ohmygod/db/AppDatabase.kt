package com.tutorial.ohmygod.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tutorial.ohmygod.utils.SourceConverter

@Database(
    entities = [Article::class,RemoteKey::class,SavedArticle::class],
    version = 3
)
@TypeConverters(SourceConverter::class)
abstract class AppDatabase:RoomDatabase() {
    abstract fun getAppDao(): AppDao
    abstract fun getRemoteKeysDao():RemoteKeyDao
    abstract fun getSavedNewsDao():SavedNewsDao
}