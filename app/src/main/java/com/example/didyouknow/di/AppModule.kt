package com.example.didyouknow.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.didyouknow.R
import com.example.didyouknow.data.remote.BlogsDatabase
import com.example.didyouknow.datasource.FirebaseBlogsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesGlideInstance(
        @ApplicationContext
        context:Context
    ):RequestManager{
        return Glide.with(context).setDefaultRequestOptions(
                RequestOptions()
                    .error(R.drawable.ic_img_error)
                    .placeholder(R.drawable.ic_placeholder)
            )

    }

    @Provides
    @Singleton
    fun provideBogDatabase() = BlogsDatabase()


}