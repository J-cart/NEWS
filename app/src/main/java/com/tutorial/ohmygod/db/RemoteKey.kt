package com.tutorial.ohmygod.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breaking_news_remote_keys")
data class RemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id:String,
    val prevKey:Int?,
    val nextKey:Int?
) {
}