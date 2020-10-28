package com.testpassword

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = true) {
    Database.connect("jdbc:postgresql://localhost:5432/pmc", "org.postgresql.Driver", "postgres", "root")
    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
        get("/random") {
            //TODO: адреса бд брать из conf
            transaction {
                Base.generateAndInsert(10)
            }
            /*call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (q in bases!!.iterator()) {
                            li { +"${q[Base.location]} - ${q[Base.status]}" }
                        }
                    }
                }
            }*/
        }
    }
}