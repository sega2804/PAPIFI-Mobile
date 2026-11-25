package com.papifi.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.IOException

object NetworkModule {
    fun provideHttpClient(authTokenProvider: (() -> String?)? = null): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                // Configure OkHttp client if needed
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(Logging) {
                level = LogLevel.BODY
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, _ ->
                    when (cause) {
                        is IOException -> throw NetworkException("Network connection error: ${cause.message}", cause)
                        else -> throw cause
                    }
                }
            }
        }.apply {
            if (authTokenProvider != null) {
                plugin(HttpSend).intercept { request ->
                    authTokenProvider()?.let { token ->
                        request.headers.set("Authorization", "Bearer $token")
                    }
                    execute(request)
                }
            }
        }
    }
}

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
