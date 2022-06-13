package com.tutorial.ohmygod.db

data class JsonResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)