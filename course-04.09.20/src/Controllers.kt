package com.testpassword

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.routing.*
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.ul
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.random() {
    route("/random") {
        delete {
            transaction {
                SchemaUtils.drop(Base, MRE, Equipment, Position, Employee, MedicalCard, Weapon, Campaign, Mission,
                    Transport, WeaponsInEquipment, TransportOnMissions, Inspection, EmployeeOnMission)
            }
        }

        get {
            call.respondHtml {
                body {
                    h1 { +"ERROR" }
                    ul {
                        li { +"Use DELETE method to drop tables" }
                        li { +"POST to fill tables" }
                        li { +"PUT to create tables before fill" }
                    }
                }
            }
        }

        put {
            transaction {
                SchemaUtils.create(Base, MRE, Equipment, Position, Employee, MedicalCard, Weapon, Campaign, Mission,
                    Transport, WeaponsInEquipment, TransportOnMissions, Inspection, EmployeeOnMission)
            }
        }

        post {
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
    }
}