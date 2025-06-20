package com.example.cueview.di

import com.example.cueview.data.firebase.FirebaseService
import com.example.cueview.data.firebase.createFirebaseService
import com.example.cueview.data.remote.api.ApiConfig
import com.example.cueview.data.remote.api.TmdbApiService
import com.example.cueview.data.repository.AuthRepositoryImpl
import com.example.cueview.data.repository.TvShowRepositoryImpl
import com.example.cueview.data.repository.UserRepositoryImpl
import com.example.cueview.domain.repository.AuthRepository
import com.example.cueview.domain.repository.TvShowRepository
import com.example.cueview.domain.repository.UserRepository
import com.example.cueview.domain.usecase.*
import com.example.cueview.presentation.viewmodel.AuthViewModel
import com.example.cueview.presentation.viewmodel.DiscoverViewModel
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
    
    single {
        TmdbApiService(
            httpClient = get(),
            apiKey = ApiConfig.TMDB_API_KEY
        )
    }
}

val repositoryModule = module {
    single<FirebaseService> { createFirebaseService() }
    single<TvShowRepository> { TvShowRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}

val useCaseModule = module {
    // Show use cases
    factory { GetTrendingShowsUseCase(get()) }
    factory { GetPopularShowsUseCase(get()) }
    factory { SearchShowsUseCase(get()) }
    factory { GetShowDetailsUseCase(get()) }
    
    // Auth use cases
    factory { SignInUseCase(get()) }
    factory { SignUpUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }
}

val viewModelModule = module {
    factory { DiscoverViewModel(get(), get(), get()) }
    factory { AuthViewModel(get(), get(), get(), get()) }
}

val appModules = listOf(
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
