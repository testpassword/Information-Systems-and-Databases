package com.testpassword

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.`java-time`.year
import org.postgresql.util.PGobject
import java.time.LocalDateTime
import java.util.*

//https://stackoverflow.com/questions/45723803/how-to-use-postgresql-enum-type-via-kotlin-exposed-orm
class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}

inline fun <reified T : Enum<T>> Table.postgresEnumeration(
    columnName: String,
    postgresEnumName: String
) = customEnumeration(columnName, postgresEnumName,
    { value -> enumValueOf<T>(value as String) }, { PGEnum(postgresEnumName, it) })

object Base: Table("base") {
    val baseId = integer("baseId").autoIncrement().primaryKey()
    val location = text("location")
    val status = text("status")
}

object MRE: Table("mre") {
    val mreId = integer("mreId").autoIncrement().primaryKey()
    val breakfast = text("breakfast").nullable()
    val lunch = text("lunch").nullable()
    val dinner = text("dinner").nullable()
    val foodAdditives = text("foodAdditives")
    val kkal = integer("kkal").nullable().check { it greaterEq 1000 }
    val proteins = integer("proteins").nullable().check { it greater 0 }
    val fats = integer("fats").nullable().check { it greater 0 }
    val carbohydrate = integer("carbohydrate").nullable().check { it greater 0 }
}

object Equipment: Table("equipment") {
    val equipId = integer("equipId").autoIncrement().primaryKey()
    val camouflage = text("camouflage")
    val communication = text("communication")
    val intelligence = text("intelligence")
    val medical = text("medical")
    val mreId = reference("mreId", MRE.mreId)
    val extra = text("extra")
}

enum class FORCES { GF, NAVY, AF }

object Position: Table("Position") {
    val posId = integer("posId").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val salary = decimal("salary", 2, 11).nullable().check { it greater 12130.0 }
    val rank = text("rank")
    val equipId = reference("equipId", Equipment.equipId)
    val forces = postgresEnumeration<FORCES>("forces", "force")
}

object Employee: Table("employee") {
    val empId = integer("empId").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val surname = text("surname").nullable()
    val dateOfBirth = date("dateOfBirth").nullable().check { it less (CurrentDateTime().year() - 18) }
    val education = text("education")
    val hiringDate = date("hiringDate").defaultExpression(CurrentDateTime().date())
    val posId = reference("posId", Position.posId)
    val isMarried = bool("isMarried").nullable()
    val base = reference("baseId", Base.baseId)
}

enum class BLOOD(val type: String) {
    ZERO_PLUS("0+"),
    ZERO_MINUS("0-"),
    A_PLUS("A+"),
    A_MINUS("A-"),
    B_PLUS("B+"),
    B_MINUS("B-"),
    AB_PLUS("AB+"),
    AB_MINUS("AB-")
}

object MedicalCard: Table("medical_card") {
    val medId = integer("medId").autoIncrement().primaryKey()
    val empId = reference("empId", Employee.empId)
    val height_cm = byte("height_cm").nullable()
    val weight_kg = byte("weight_kg").nullable()
    val diseases = text("diseases")
    val blood = postgresEnumeration<BLOOD>("blood", "blood").nullable()
    val gender = bool("gender").nullable()
}

object Weapon: Table("weapon") {
    val weaponId = integer("weaponId").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val type = text("type").nullable()
    val caliber = float("caliber").check { it greater 0.0 }
    val rateOfFire = float("rateOfFire").check { it greater 0.0 }
    val barrelLength = float("barrelLength").check { it greater 0.0 }
    val sightingRange = float("sightingRange").check { it greater 0.0 }
}

object Campaign: Table("campaign") {
    val campId = integer("campId").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val customer = text("customer").nullable()
    val earning = decimal("earning", 2, 11).nullable().check { it greaterEq 0.0 }
    val spending = decimal("spending", 2, 11).nullable().check { it greaterEq 0.0 }
    val executionStatus = text("executionStatus")
}

object Mission: Table("mission") {
    val missId = integer("missId").autoIncrement().primaryKey()
    val campId = reference("missId", Mission.missId)
    val startDateAndTime = datetime("startDateAndTime")
    val endDateAndTime = datetime("endDateAndTime")
    val legalStatus = text("legalStatus")
    val departureLocation = text("departureLocation")
    val arrivalLocation = text("arrivalLocation")
    val enemies = text("enemies")
}

object Transport: Table("transport") {
    val transId = integer("transId").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val type = text("type").nullable()
    val status = text("status")
}

object Inspection: Table("inspection") {
    val empId = reference("empId", Employee.empId)
    val transId = reference("transId", Transport.transId)
    val serviceDate = date("serviceDate").defaultExpression(CurrentDateTime().date()).nullable()
}