package com.domelabs.scanapp.di

import com.domelabs.scanapp.data.InMemoryMuseumStorage
import com.domelabs.scanapp.data.KtorMuseumApi
import com.domelabs.scanapp.data.MuseumApi
import com.domelabs.scanapp.data.MuseumRepository
import com.domelabs.scanapp.data.MuseumStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module

val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Any)
            }
        }
    }

    single<MuseumApi> { KtorMuseumApi(get()) }
    single<MuseumStorage> { InMemoryMuseumStorage() }
    single {
        MuseumRepository(get(), get()).apply {
            initialize()
        }
    }
}

fun initKoin() {
    startKoin {
        modules(dataModule)
    }
}
