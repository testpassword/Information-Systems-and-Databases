package com.testpassword.models

import com.testpassword.Generable
import com.testpassword.dropEntitesWithIds
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object EquipmentTable: Table("equipment"), Generable {

    val equip_id = integer("equip_id").autoIncrement().primaryKey()
    val camouflage = text("camouflage").nullable()
    val communication = text("communication").nullable()
    val intelligence = text("intelligence").nullable()
    val medical = text("medical").nullable()
    val mre_id = reference("mre_id", MRETable.mre_id)
    val extra = text("extra").nullable()

    override fun generateAndInsert(n: Int) {
        val camouflages = setOf("black", "khaki", "olive", "fleckerteppich", "strichtarn", "cce", "vegetata",
            "flora", "forest", "pixel", "woodland", "accupat", "desert", "city")
        val communications = setOf("radio_set", "map", "mobile_satellite", "signal_flares")
        val intelligences = setOf("binoculars", "drone", "radar", "thermal_visor")
        val medicals = setOf("bandage", "harness", "antibiotics", "alcohol", "scissors", "tweezers", "antiseptic",
            "ammonia", "nitroglycerine", "adrenalin")
        val mreIds = MRETable.selectAll().limit(n).map { it[MRETable.mre_id] }
        (1..n).forEach {
            EquipmentTable.insert {
                it[camouflage] = camouflages.random()
                it[communication] = communications.random()
                it[intelligence] = intelligences.random()
                it[medical] = medicals.random()
                it[mre_id] = mreIds.random()
            }
        }
    }
}

fun ResultRow.toEquipment() = Equipment(this[EquipmentTable.equip_id], this[EquipmentTable.camouflage],
    this[EquipmentTable.communication], this[EquipmentTable.intelligence], this[EquipmentTable.medical],
    MRETable.select { MRETable.mre_id eq this@toEquipment[EquipmentTable.mre_id] }.map { it.toMRE() }.first(),
    this[EquipmentTable.extra])

data class Equipment(val equipId: Int?, val camouflage: String?, val communication: String?, val intelligence: String?,
                     val medical: String?, val mre: MRE, val extra: String?)

fun Route.equipment() {

    get {
        call.respondText {
            transaction {
                P.toJsonString(EquipmentTable.selectAll().map { it.toEquipment() }.toList())
            }
        }
    }

    put {  }

    post {  }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropEntitesWithIds(droppedIds, BaseTable)
            }.toString()
        }
    }
}