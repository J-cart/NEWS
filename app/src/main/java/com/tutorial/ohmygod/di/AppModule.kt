package com.tutorial.ohmygod.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tutorial.ohmygod.arch.MainNewsRepository
import com.tutorial.ohmygod.arch.NewsRepository
import com.tutorial.ohmygod.db.AppDao
import com.tutorial.ohmygod.db.AppDatabase
import com.tutorial.ohmygod.db.NewsApiService
import com.tutorial.ohmygod.utils.Constants.NEWS_DATABASE_NAME
import com.tutorial.ohmygod.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            NEWS_DATABASE_NAME
        ).build()//.addMigrations(MIGRATION())


    @Singleton
    @Provides
    fun getMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun getRepository(api: NewsApiService, db: AppDatabase): MainNewsRepository =
        NewsRepository(api, db)

    @Singleton
    @Provides
    fun getDao(db: AppDatabase): AppDao = db.getAppDao()

    @Singleton
    @Provides
    fun getDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }

    @Singleton
    @Provides
    fun getRetrofit(moshi: Moshi): NewsApiService = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl("https://newsapi.org/")
        .build()
        .create(NewsApiService::class.java)


    class MIGRATION : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `breaking_news_remote_keys` (`id` TEXT,`prevKey` INTEGER,`nextKey` INTEGER, PRIMARY KEY(`id`))")
        }
    }

    //region OKHTTP CLIENT
    /*  fun http(){
          val logger = HttpLoggingInterceptor()
          logger.level = HttpLoggingInterceptor.Level.BASIC
          val http = OkHttpClient.Builder().addInterceptor(logger).build()

          Retrofit.Builder()
              .client(http)
              .baseUrl("")
              .addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(""::class.java)
      }*/
    //endregion

}