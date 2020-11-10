package com.testpassword

import com.testpassword.models.*
import com.testpassword.routes.random
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.initDB() =
    Database.connect(HikariDataSource(HikariConfig(environment.config.property("ktor.hikariconfig").getString())))

//https://stefangaller.at/app-development/kotlin/ktor-rest-api-exposed/
fun Application.module() {
    this.initDB()
    routing {
        route("random") { random() }
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