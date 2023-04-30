package com.example.geotrackfamily

import com.example.geotrackfamily.datasources.FriendRemoteDataSource
import com.example.geotrackfamily.datasources.UserRemoteDataSource
import com.example.geotrackfamily.interfaces.FriendServiceRemote
import com.example.geotrackfamily.interfaces.UserServiceRemote
import com.example.geotrackfamily.repository.FriendRepository
import com.example.geotrackfamily.repository.UserRepository
import com.example.geotrackfamily.utility.Utils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    @Named("api")
    fun provideRetrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl(Utils.domainApi)
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

    ///////
 /*   @Provides
    fun provideFriendService(@Named("api") retrofit: Retrofit): FriendServiceRemote = retrofit.create(FriendServiceRemote::class.java)

    @Singleton
    @Provides
    fun provideFriendRemoteDataSource(friendServiceRemote: FriendServiceRemote) = FriendRemoteDataSource(friendServiceRemote)

    @Singleton
    @Provides
    fun provideFriendRepositoru(friendRemoteDataSource: FriendRemoteDataSource) = FriendRepository(friendRemoteDataSource)

*/


}