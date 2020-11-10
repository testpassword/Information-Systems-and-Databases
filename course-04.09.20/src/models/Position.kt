package com.testpassword.models

import com.testpassword.*
import io.ktor.application.*
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
    val salary = decimal("salary", 2, 11).check { it greater 12130.0 }
    val rank = text("rank").nullable()
    val equip_id = reference("equip_id", EquipmentTable.equip_id).nullable()
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
                it[salary] = F.number().randomDouble(2, 12130, 1000000).toBigDecimal()
                if (empPos in armyPositions) it[rank] = ranks.random()
                it[equip_id] = equipIds.random()
                it[forces] = FORCES.values().random()
            }
        }
    }
}

fun ResultRow.toPosition() = Position(this[PositionTable.pos_id], this[PositionTable.name],
    this[PositionTable.salary], this[PositionTable.rank],
    this@toPosition[PositionTable.equip_id]?.let {
        EquipmentTable.select { EquipmentTable.equip_id eq it }.map { it.toEquipment() }.first()
    },
    this[PositionTable.forces])

data class Position(val posId: Int?, val name: String, val salary: BigDecimal, val rank: String?, val equip: Equipment?,
                    val forces: FORCES?)

fun Route.position() {

    get {
        call.respondText {
            transaction {
                P.toJsonString(PositionTable.selectAll().map { it.toPosition() }.toList())
            }
        }
    }

    put {  }

    post {  }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropEntitesWithIds(droppedIds, PositionTable)
            }.toString()
        }
    }
}