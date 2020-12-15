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

object EquipmentTable: Table("equipment"), Generable {

    val equip_id = integer("equip_id").autoIncrement().primaryKey()
    val camouflage = text("camouflage").nullable()
    val communication = text("communication").nullable()
    val intelligence = text("intelligence").nullable()
    val medical = text("medical").nullable()
    val mre_id = reference("mre_id", MRETable.mre_id, onDelete = ReferenceOption.RESTRICT)
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
    this[EquipmentTable.mre_id], this[EquipmentTable.extra])

data class Equipment(val equipId: Int?, val camouflage: String?, val communication: String?, val intelligence: String?,
                     val medical: String?, val mreId: Int, val extra: String?)

fun Route.equipment() {

    get {
        val (t, s) = try {
            call.parameters["ids"]!!.let {
                transaction {
                    PARSER.toJson(getRecordsWithIds(it, EquipmentTable).map { it.toEquipment() }) to HttpStatusCode.OK
                }
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("equipId", raw)
            transaction {
                EquipmentTable.update({ EquipmentTable.equip_id eq id }) { e ->
                    f["camouflage"]?.let { e[camouflage] = it }
                    f["communication"]?.let { e[communication] = it }
                    f["intelligence"]?.let { e[intelligence] = it }
                    f["medical"]?.let { e[medical] = it }
                    f["mreId"]?.let { e[mre_id] = it.toInt() }
                    f["extra"]?.let { e[extra] = it }
                }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val e = PARSER.fromJson(raw, Equipment::class.java)
            transaction {
                EquipmentTable.insert {
                    it[camouflage] = e.camouflage
                    it[communication] = e.communication
                    it[intelligence] = e.intelligence
                    it[medical] = e.medical
                    it[mre_id] = e.mreId
                    it[extra] = e.extra
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, EquipmentTable) }
            "Equipments with $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}