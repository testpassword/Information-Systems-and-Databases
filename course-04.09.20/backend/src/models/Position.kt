package com.testpassword.models

import com.testpassword.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import kotlin.random.Random

object PositionTable: Table("Position"), Generable {

    val pos_id = integer("pos_id").autoIncrement().primaryKey()
    val name = text("name")
    val salary = decimal("salary", 2, 11).check { it greater 300.0 }
    val rank = text("rank").nullable()
    val equip_id = reference("equip_id", EquipmentTable.equip_id, onDelete = ReferenceOption.SET_NULL).nullable()
    val forces = postgresEnumeration<FORCES>("forces", "force").nullable()

    override fun generateAndInsert(n: Int) {
        val ranks = setOf("student_officer", "private_second_class", "private_first_class", "junior_sergeant",
            "sergeant", "senior_sergeant", "petty_officer", "ensign", "senior_ensign", "junior_lieutenant", "lieutenant",
            "senior_lieutenant", "captain", "major", "lieutenant_colonel", "colonel") //, "major_general", "lieutenant_general",
        //"colonel_general", "army_general", "marshal") исключим высшие звания из генерации
        val armyPositions = setOf("medic", "miner", "scout", "security", "driver", "torpedo_operator", "pilot",
            "mechanic", "engineer", "navigator", "orderly", "duty", "coach", "artilleryman", "gunner", "sniper", "spy")
        val equipIds = EquipmentTable.selectAll().limit(n).map { it[EquipmentTable.equip_id] }
        (1..n).forEach {
            PositionTable.insert {
                val empPos = if (Random.nextDouble(1.0, 100.0) >= 65) armyPositions.random() else F.job().position()
                it[name] = empPos
                it[salary] = F.number().randomDouble(2, 300, 1000000).toBigDecimal()
                if (empPos in armyPositions) it[rank] = ranks.random()
                it[equip_id] = equipIds.random()
                it[forces] = FORCES.values().random()
            }
        }
    }
}

fun ResultRow.toPosition() = Position(
    this[PositionTable.pos_id],
    this[PositionTable.name],
    this[PositionTable.salary],
    this[PositionTable.rank],
    this[PositionTable.equip_id],
    this[PositionTable.forces])

data class Position(val posId: Int?,
                    val name: String,
                    val salary: BigDecimal,
                    val rank: String?,
                    val equipId: Int?,
                    val forces: FORCES?)

fun Route.position() {

    get {
        val (t, s) = try {
            call.parameters["ids"]!!.let {
                transaction {
                    PARSER.toJson(getRecordsWithIds(it, PositionTable).map { it.toPosition() }) to HttpStatusCode.OK
                }
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("posId", raw)
            PositionTable.update({ PositionTable.pos_id eq id }) { p ->
                f["name"]?.let { p[name] = it as String }
                f["salary"]?.let { p[salary] = (it as Double).toBigDecimal() }
                f["rank"]?.let { p[rank] = it as String }
                f["equipId"]?.let { p[equip_id] = it as Int }
                f["forces"]?.let { p[forces] = FORCES.valueOf(it as String) }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val p = PARSER.fromJson(raw, Position::class.java)
            transaction {
                PositionTable.insert {
                    it[name] = p.name
                    it[salary] = p.salary
                    it[rank] = p.rank
                    it[equip_id] = p.equipId
                    it[forces] = p.forces
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, PositionTable) }
            "Positions with $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}