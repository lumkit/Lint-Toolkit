package io.lumkit.lint.toolkit.desktop.net.ktor

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.charsets.*

val ktorClient by lazy {
    HttpClient(OkHttp) {
        engine {
            pipelining = true
        }
        install(ContentNegotiation) {
            json()
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

