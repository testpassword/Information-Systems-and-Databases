package com.testpassword.models

import com.testpassword.Generable
import com.testpassword.dropRecordsWithIds
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONObject
import java.io.File

object WeaponTable: Table("weapon"), Generable {

    val weapon_id = integer("weapon_id").autoIncrement().primaryKey()
    val name = text("name")
    val type = text("type")
    val caliber = float("caliber").check { it greater 0.0 }.nullable()
    val rate_of_fire = integer("rate_of_fire").check { it greater 0 }.nullable()
    val sighting_range_m = integer("sighting_range_m").check { it greater 0 }.nullable()

    override fun generateAndInsert(n: Int) {
        val rawData = File("resources/static/guns.json").readLines().joinToString(separator = "")
        val jsonBody = JSONObject(rawData).getJSONArray("weapons")
        jsonBody.forEachIndexed { i, _ ->
            val w = jsonBody.getJSONObject(i)
            WeaponTable.insert {
                it[name] = w.getString("name")
                it[type] = w.getString("type")
                it[caliber] = w.getFloat("caliber")
                it[rate_of_fire] = w.getInt("rate_of_fire")
                it[sighting_range_m] = w.getInt("sighting_range_m")
            }
        }
    }
}

fun ResultRow.toWeapon() = Weapon(this[WeaponTable.weapon_id], this[WeaponTable.name], this[WeaponTable.type],
    this[WeaponTable.caliber]?.toDouble(), this[WeaponTable.rate_of_fire], this[WeaponTable.sighting_range_m])

data class Weapon(val weaponId: Int?, val name: String, val type: String, val caliber: Double?, val rateOfFire: Int?,
                  val sightingRange_m: Int?)

fun Route.weapon() {

    get {
        call.respondText {
            transaction {
                P.toJsonString(WeaponTable.selectAll().map { it.toWeapon() }.toList())
            }
        }
    }

    put {  }

    post {  }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropRecordsWithIds(droppedIds, WeaponTable)
            }.toString()
        }
    }
}