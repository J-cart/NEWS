package com.tutorial.ohmygod.utils

import androidx.room.TypeConverter
import com.tutorial.ohmygod.db.Source

class SourceConverter {

    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name
    }

    @TypeConverter
    fun toSource(name:String):Source{
        return Source(name,name)
    }
}