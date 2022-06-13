package com.tutorial.ohmygod.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tutorial.ohmygod.utils.SourceConverter

@Database(
    entities = [Article::class],//,RemoteKey::class
    version = 1
)
@TypeConverters(SourceConverter::class)
abstract class AppDatabase:RoomDatabase() {
    abstract fun getAppDao(): AppDao
   // abstract fun getRemoteKeysDao():RemoteKeyDao
    abstract fun getBNMediatorDao():BNMediatorDao
}