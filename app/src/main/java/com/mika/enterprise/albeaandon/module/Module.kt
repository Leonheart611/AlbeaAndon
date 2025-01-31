package com.mika.enterprise.albeaandon.module

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.mika.enterprise.albeaandon.core.AppDatabase
import com.mika.enterprise.albeaandon.core.dao.UserDao
import com.mika.enterprise.albeaandon.core.domain.API
import com.mika.enterprise.albeaandon.core.repository.NetworkRepository
import com.mika.enterprise.albeaandon.core.repository.NetworkRepositoryImpl
import com.mika.enterprise.albeaandon.core.repository.UserRepository
import com.mika.enterprise.albeaandon.core.repository.UserRepositoryImpl
import com.mika.enterprise.albeaandon.core.util.AuthInterceptor
import com.mika.enterprise.albeaandon.core.util.Constant.PREFERENCES_FILE_KEY
import com.mika.enterprise.albeaandon.core.util.Constant.PROD_URL_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAuthInterceptor(sharedPreferences: SharedPreferences): AuthInterceptor {
        return AuthInterceptor(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        @ApplicationContext context: Context
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(authInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
            .callTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PROD_URL_ID)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
    }

    @Provides
    @Singleton
    fun providePublicApi(retrofit: Retrofit): API {
        return retrofit.create(API::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(
        api: API,
        sharedPreferences: SharedPreferences,
        userRepository: UserRepository
    ): NetworkRepository {
        return NetworkRepositoryImpl(api, sharedPreferences, userRepository)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImpl(userDao)
    }
}
