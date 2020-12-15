package com.testpassword.models

import com.testpassword.PARSER
import com.testpassword.dropRecordsWithIds
import com.testpassword.explodeJsonForModel
import com.testpassword.getRecordsWithIds
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
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
                  val sightingRangeM: Int?)

fun Route.weapon() {

    get {
        val (t, s) = try {
            call.parameters["ids"]!!.let {
                transaction {
                    PARSER.toJson(getRecordsWithIds(it, WeaponTable).map { it.toWeapon() }) to HttpStatusCode.OK
                }
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("weaponId", raw)
            WeaponTable.update({ WeaponTable.weapon_id eq id }) { w ->
                f["name"]?.let { w[name] = it }
                f["type"]?.let { w[type] = it }
                f["caliber"]?.let { w[caliber] = it.toFloat() }
                f["rateOfFire"]?.let { w[rate_of_fire] = it.toInt() }
                f["sighting_rangeM"]?.let { w[sighting_range_m] = it.toInt() }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val w = PARSER.fromJson(raw, Weapon::class.java)
            transaction {
                WeaponTable.insert {
                    it[name] = w.name
                    it[type] = w.type
                    it[caliber] = w.caliber?.toFloat()
                    it[rate_of_fire] = w.rateOfFire
                    it[sighting_range_m] = w.sightingRangeM
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, WeaponTable) }
            "Weapons with ids $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}