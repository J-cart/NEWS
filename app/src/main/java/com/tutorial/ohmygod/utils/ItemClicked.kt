package com.tutorial.ohmygod.utils

import com.tutorial.ohmygod.db.SavedArticle

interface ItemClicked {
    fun itemClicked(data:SavedArticle)
}