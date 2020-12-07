package com.testpassword.routes

import com.testpassword.*
import com.testpassword.models.*
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.god() {

    val GOD_URL = "localhost:9090/god"

    get {
        call.respondHtml {
            body {
                h1 { +"GOD-MODE" }
                ul {
                    li {
                        form(action = GOD_URL, method = FormMethod.put) {
                            button(type = ButtonType.submit) {
                                +"PUT to create tables before fill"
                            }
                        }
                    }
                    li {
                        form(action = GOD_URL, method = FormMethod.post) {
                            button(type=ButtonType.submit) {
                                + "POST to fill tables"
                            }
                        }
                    }
                    li {
                        form(action = GOD_URL, method = FormMethod.delete) {
                            button(type = ButtonType.submit) {
                                + "DELETE to drop table and all data"
                            }
                        }
                    }
                }
            }
        }
    }

    put {
        transaction {
            SchemaUtils.create(
                BaseTable, MRETable, EquipmentTable, PositionTable, EmployeeTable, MedicalCardTable, WeaponTable,
                CampaignTable, MissionTable, TransportTable, WeaponsInEquipment, TransportOnMissions, Inspection,
                EmployeeOnMission
            )
        }
    }

    post {
        val (t, s) = try {
            transaction {
                RecordsGenerator.fillDb(
                    mapOf(
                        BaseTable to 50,
                        MRETable to 30,
                        EquipmentTable to 75,
                        PositionTable to 50,
                        EmployeeTable to 1000,
                        MedicalCardTable to 0,
                        WeaponTable to 0,
                        CampaignTable to 40,
                        MissionTable to 110,
                        TransportTable to 0,
                        WeaponsInEquipment to 0,
                        TransportOnMissions to 0,
                        Inspection to 0,
                        EmployeeOnMission to 0
                    )
                )
            }
            "Records generated" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        transaction {
            SchemaUtils.drop(
                BaseTable, MRETable, EquipmentTable, PositionTable, EmployeeTable, MedicalCardTable, WeaponTable,
                CampaignTable, MissionTable, TransportTable, WeaponsInEquipment, TransportOnMissions, Inspection,
                EmployeeOnMission
            )
        }
    }
}