package com.testpassword.models

import com.beust.klaxon.Klaxon
import com.testpassword.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

val P = Klaxon() // parser

object BaseTable: Table("base"), Generable {

    val base_id = integer("base_id").autoIncrement().primaryKey()
    val location = text("location")
    val status = text("status")

    override fun generateAndInsert(n: Int) {
        val statuses = setOf("working", "closed", "destroyed", "abandoned", "unknown", "captured", "for_sale")
        (1..n).forEach {
            BaseTable.insert {
                it[location] = F.address().city()
                it[status] = statuses.random()
            }
        }
    }
}

fun ResultRow.toBase() = Base(this[BaseTable.base_id], this[BaseTable.location], this[BaseTable.status])

data class Base(val baseId: Int?, val location: String, val status: String)

fun Route.base() {

    get {
        val (t, s) = try {
            val raw = call.receiveText()
            transaction {
                P.toJsonString(getRecordsWithIds(raw, BaseTable).map { it.toBase() }.toList()) to HttpStatusCode.OK
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("baseId", raw)
            transaction {
                BaseTable.update({ BaseTable.base_id eq id }) { b ->
                    f["location"]?.let { b[location] = it }
                    f["status"]?.let { b[status] = it }
                }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val b = P.parse<Base>(raw)!!
            transaction {
                BaseTable.insert {
                    it[location] = b.location
                    it[status] = b.status
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction {
                dropRecordsWithIds(droppedIds, BaseTable)
            }
            "Bases with $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}