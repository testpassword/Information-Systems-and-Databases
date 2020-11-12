package com.testpassword.routes

import com.testpassword.*
import com.testpassword.models.*
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.ul
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.random() {

    get {
        call.respondHtml {
            body {
                h1 { +"TIP" }
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
                        EquipmentTable to 100,
                        PositionTable to 150,
                        EmployeeTable to 2000,
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