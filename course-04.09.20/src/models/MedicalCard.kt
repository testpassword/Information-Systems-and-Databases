package com.testpassword.models

import com.testpassword.Generable
import com.testpassword.dropRecordsWithIds
import io.ktor.application.*
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

fun ResultRow.toMedicalCard() = MedicalCard(this[MedicalCardTable.med_id],
    EmployeeTable.select { EmployeeTable.emp_id eq this@toMedicalCard[MedicalCardTable.emp_id] }.map { it.toEmployee() }.first(),
    this[MedicalCardTable.height_cm], this[MedicalCardTable.weight_kg], this[MedicalCardTable.diseases],
    this[MedicalCardTable.blood], this[MedicalCardTable.gender])

data class MedicalCard(val medId: Int?, val employee: Employee, val height_cm: Int, val weight_kg: Int, val diseases: String?,
                       val blood: String, val gender: Boolean)

fun Route.medicalCard() {

    get {
        call.respondText {
            transaction {
                P.toJsonString(MedicalCardTable.selectAll().map { it.toMedicalCard() }.toList())
            }
        }
    }

    put {  }

    post {  }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropRecordsWithIds(droppedIds, MedicalCardTable)
            }.toString()
        }
    }
}