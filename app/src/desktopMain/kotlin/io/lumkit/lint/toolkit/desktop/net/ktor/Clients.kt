package io.lumkit.lint.toolkit.desktop.net.ktor

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.gson.*
import io.ktor.utils.io.charsets.Charsets

val ktorClient by lazy {
    HttpClient(Java) {
        engine {
            pipelining = true
            protocolVersion = java.net.http.HttpClient.Version.HTTP_2
        }
        install(ContentNegotiation) {
            gson()
        }
        install(HttpCookies)
        install(ContentEncoding) {
            deflate(1.0F)
            gzip(0.9F)
        }
//        install(HttpCache)
        install(HttpTimeout) {
            connectTimeoutMillis = 60000
        }
        Charsets {
            // Allow using `UTF_8`.
            register(Charsets.UTF_8)
        }
    }
}

