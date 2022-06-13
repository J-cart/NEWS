package com.tutorial.ohmygod.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "saved_article")
@Parcelize
data class SavedArticle(
@PrimaryKey(autoGenerate = true)
val id:Int?,
val author: String?,
val content: String?,
val description: String?,
val publishedAt: String?,
val source:Source?,
val title: String?,
val url: String?,
val urlToImage: String?
):Parcelable

