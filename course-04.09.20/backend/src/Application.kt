package com.testpassword

import com.testpassword.models.*
import com.testpassword.routes.god
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.initDB() =
    Database.connect(HikariDataSource(HikariConfig(environment.config.property("ktor.hikariconfig").getString())))

fun Application.allowCORS() {
    install(CORS) {
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
        methods.addAll(HttpMethod.DefaultMethods)
    }
}

//https://stefangaller.at/app-development/kotlin/ktor-rest-api-exposed/
fun Application.module() {
    this.initDB()
    this.allowCORS()
    routing {
        route("god") { god() }
        route("base") { base() }
        route("campaign") { campaign() }
        route("employee") { employee() }
        route("equipment") { equipment() }
        route("medicalCard") { medicalCard() }
        route("mission") { mission() }
        route("mre") { mre() }
        route("position") { position() }
        route("transport") { transport() }
        route("weapon") { weapon() }
    }
}