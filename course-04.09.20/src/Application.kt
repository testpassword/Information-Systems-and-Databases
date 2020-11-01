package com.testpassword

import com.github.javafaker.Faker
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
                RecordsGenerator.fillDb(
                    mapOf(
                        Base to 50,
                        MRE to 30,
                        Equipment to 100,
                        Position to 150,
                        Employee to 4000,
                        MedicalCard to 0,
                        Weapon to 0,
                        Campaign to 40,
                        Mission to 110,
                        Transport to 0,
                        WeaponsInEquipment to 0,
                        TransportOnMissions to 0,
                        Inspection to 0,
                        EmployeeOnMission to 0
                    ))
            }
        }
        get("/testing") {
            val l = List<String>(10) { Faker.instance().job().position() }
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (q in l) {
                            li { +q }
                        }
                    }
                }
            }
        }
    }
}