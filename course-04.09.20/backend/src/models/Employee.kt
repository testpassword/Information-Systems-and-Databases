package com.testpassword.models

import com.google.gson.*
import com.testpassword.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.`java-time`.year
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.random.Random


object EmployeeTable: Table("employee"), Generable {

    val emp_id = integer("emp_id").autoIncrement().primaryKey()
    val name = text("name")
    val surname = text("surname")
    val date_of_birth = date("date_of_birth").check { it less (CurrentDateTime().year() - 18) }
    val education = text("education").nullable()
    val hiring_date = date("hiring_date").defaultExpression(CurrentDateTime().date())
    val pos_id = reference("pos_id", PositionTable.pos_id, onDelete = ReferenceOption.RESTRICT)
    val is_married = bool("is_married")
    val base_id = reference("base_id", BaseTable.base_id, onDelete = ReferenceOption.SET_NULL).nullable()

    @InternalAPI
    override fun generateAndInsert(n: Int) {
        val posIds = PositionTable.selectAll().limit(n).map { it[PositionTable.pos_id] }
        val baseIds = BaseTable.selectAll().map { it[BaseTable.base_id] }
        (1..n).forEach {
            val newbieId = EmployeeTable.insert {
                it[name] = F.name().firstName()
                it[surname] = F.name().lastName()
                it[date_of_birth] = F.date().birthday(18, 70).toLocalDateTime().toLocalDate()
                it[education] = F.educator().university()
                it[hiring_date] = F.date().between(
                    // здесь и далее - условной день основания компании.
                    Date.from(LocalDate.of(2014, 3, 18).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date()
                ).toLocalDateTime().toLocalDate()
                it[pos_id] = posIds.random()
                it[is_married] = Random.nextBoolean()
                it[base_id] = baseIds.random()
            }[emp_id]
            MedicalCardTable.insert {
                it[emp_id] = newbieId
                it[height_cm] = Random.nextInt(150, 200)
                it[weight_kg] = Random.nextInt(40, 120)
                it[diseases] = generateSequence { F.medical().diseaseName() }
                    .take(Random.nextInt(0, 5))
                    .joinToString(separator = "_")
                it[blood] = F.name().bloodGroup()
                it[gender] = Random.nextBoolean()
            }
        }
    }
}

fun ResultRow.toEmployee() = Employee(
    this[EmployeeTable.emp_id],
    this[EmployeeTable.name],
    this[EmployeeTable.surname],
    this[EmployeeTable.date_of_birth],
    this[EmployeeTable.education],
    this[EmployeeTable.hiring_date],
    this[EmployeeTable.pos_id],
    this[EmployeeTable.is_married],
    this[EmployeeTable.base_id])

data class Employee(val empId: Int?,
                    val name: String,
                    val surname: String,
                    val dateOfBirth: LocalDate,
                    val education: String?,
                    val hiringDate: LocalDate,
                    val posId: Int,
                    val isMarried: Boolean,
                    val baseId: Int?)

fun Route.employee() {

    get {
        val (t, s) = try {
            call.parameters["ids"]!!.let {
                transaction {
                    PARSER.toJson(getRecordsWithIds(it, EmployeeTable).map { it.toEmployee() }) to HttpStatusCode.OK
                }
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("empId", raw)
            transaction {
                EmployeeTable.update({ EmployeeTable.emp_id eq id }) { e ->
                    f["name"]?.let { e[name] = it as String }
                    f["surname"]?.let { e[surname] = it as String }
                    f["dateOfBirth"]?.let { e[date_of_birth] = LocalDate.parse(it as String) }
                    f["education"]?.let { e[education] = it as String }
                    f["hiringDate"]?.let { e[hiring_date] = LocalDate.parse(it as String) }
                    f["posId"]?.let { e[pos_id] = it as Int }
                    f["isMarried"]?.let { e[is_married] = it as Boolean }
                    f["baseId"]?.let { e[base_id] = it as Int }
                }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val e = PARSER.fromJson(raw, Employee::class.java)
            transaction {
                EmployeeTable.insert {
                    it[name] = e.name
                    it[surname] = e.surname
                    it[date_of_birth] = e.dateOfBirth
                    it[education] = e.education
                    it[hiring_date] = e.hiringDate
                    it[pos_id] = e.posId
                    it[is_married] = e.isMarried
                    it[base_id] = e.baseId
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, EmployeeTable) }
            "Employee with ids $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}