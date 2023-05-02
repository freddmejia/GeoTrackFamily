package com.example.geotrackfamily

import android.content.Context
import android.content.SharedPreferences
import com.example.geotrackfamily.datasources.FriendRemoteDataSource
import com.example.geotrackfamily.datasources.UserRemoteDataSource
import com.example.geotrackfamily.interfaces.FriendServiceRemote
import com.example.geotrackfamily.interfaces.UserServiceRemote
import com.example.geotrackfamily.models.AuthInterceptor
import com.example.geotrackfamily.models.TokenManager
import com.example.geotrackfamily.repository.FriendRepository
import com.example.geotrackfamily.repository.UserRepository
import com.example.geotrackfamily.utility.Utils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    fun provideTokenManager(sharedPreferences: SharedPreferences):
            TokenManager = TokenManager(sharedPreferences)

    @Provides
    fun provideAuthInterceptor(tokenManager: TokenManager):
            AuthInterceptor = AuthInterceptor(tokenManager)

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Singleton
    @Provides
    @Named("api")
    fun provideRetrofit(gson: Gson,okHttpClient: OkHttpClient) : Retrofit = Retrofit.Builder()
        .baseUrl(Utils.domainApi)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()


    @Provides
    fun provideUserService(@Named("api") retrofit: Retrofit): UserServiceRemote = retrofit.create(UserServiceRemote::class.java)

    @Singleton
    @Provides
    fun provideUserRemoteDataSource(userServiceRemote: UserServiceRemote) = UserRemoteDataSource(userServiceRemote)

    @Singleton
    @Provides
    fun provideUserRepository(userDataSourceRemote: UserRemoteDataSource) = UserRepository(userDataSourceRemote)


    @Singleton
    @Provides
    fun providerSharedPreferences(@ApplicationContext appContext: Context) : SharedPreferences =
        appContext.applicationContext.getSharedPreferences(appContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE)



    ///////
    @Provides
    fun provideFriendService(@Named("api") retrofit: Retrofit): FriendServiceRemote = retrofit.create(FriendServiceRemote::class.java)

    @Singleton
    @Provides
    fun provideFriendRemoteDataSource(friendServiceRemote: FriendServiceRemote) = FriendRemoteDataSource(friendServiceRemote)

    @Singleton
    @Provides
    fun provideFriendRepositoru(friendRemoteDataSource: FriendRemoteDataSource) = FriendRepository(friendRemoteDataSource)



}