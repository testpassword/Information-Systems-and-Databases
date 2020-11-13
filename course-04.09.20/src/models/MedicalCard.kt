package com.testpassword.models

import com.testpassword.Generable
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

object MedicalCardTable: Table("medical_card"), Generable {

    val med_id = integer("med_id").autoIncrement().primaryKey()
    val emp_id = reference("emp_id", EmployeeTable.emp_id, onDelete = ReferenceOption.CASCADE)
    val height_cm = integer("height_cm")
    val weight_kg = integer("weight_kg")
    val diseases = text("diseases").nullable()
    val blood = text("blood")
    val gender = bool("gender")
}

fun ResultRow.toMedicalCard() = MedicalCard(this[MedicalCardTable.med_id], this[MedicalCardTable.emp_id],
    this[MedicalCardTable.height_cm], this[MedicalCardTable.weight_kg], this[MedicalCardTable.diseases],
    this[MedicalCardTable.blood], this[MedicalCardTable.gender])

data class MedicalCard(val medId: Int?, val empId: Int, val heightCm: Int, val weightKg: Int, val diseases: String?,
                       val blood: String, val gender: Boolean)

fun Route.medicalCard() {

    get {
        val (t, s) = try {
            val raw = call.receiveText()
            transaction {
                P.toJsonString(getRecordsWithIds(raw, MedicalCardTable).map { it.toMedicalCard() }) to HttpStatusCode.OK
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("medId", raw)
            transaction {
                MedicalCardTable.update({ MedicalCardTable.med_id eq id }) { m ->
                    f["emp_id"]?.let { m[emp_id] = it.toInt() }
                    f["heightCm"]?.let { m[height_cm] = it.toInt() }
                    f["weightKg"]?.let { m[weight_kg] = it.toInt() }
                    f["diseases"]?.let { m[diseases] = it }
                    f["blood"]?.let { m[blood] = it }
                    f["gender"]?.let { m[gender] = it.toBoolean() }
                }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val m = P.parse<MedicalCard>(raw)!!
            transaction {
                MedicalCardTable.insert {
                    it[emp_id] = m.empId
                    it[height_cm] = m.heightCm
                    it[weight_kg] = m.weightKg
                    it[diseases] = m.diseases
                    it[blood] = m.blood
                    it[gender] = m.gender
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, MedicalCardTable) }
            "Cards with $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}