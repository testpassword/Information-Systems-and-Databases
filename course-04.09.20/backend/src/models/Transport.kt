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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONObject
import java.io.File

object TransportTable: Table("transport"), Generable {

    val trans_id = integer("trans_id").autoIncrement().primaryKey()
    val name = text("name")
    val type = text("type")
    val status = text("status")

    override fun generateAndInsert(n: Int) {
        val statuses = setOf("available", "under_repair", "destroyed", "broken")
        val rawData = File("resources/static/transports.json").readLines().joinToString(separator = "")
        val jsonBody = JSONObject(rawData).getJSONArray("transport")
        jsonBody.forEachIndexed { i, el ->
            val w = jsonBody.getJSONObject(i)
            TransportTable.insert {
                it[name] = w.getString("name")
                it[type] = w.getString("type")
                it[status] = statuses.random()
            }
        }
    }
}

fun ResultRow.toTransport() = Transport(this[TransportTable.trans_id], this[TransportTable.name],
    this[TransportTable.type], this[TransportTable.status])

data class Transport(val transId: Int?, val name: String, val type: String, val status: String)

fun Route.transport() {

    get {
        val (t, s) = try {
            val raw = call.receiveText()
            transaction {
                P.toJsonString(getRecordsWithIds(raw, TransportTable).map { it.toTransport() }) to HttpStatusCode.OK
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("transId", raw)
            TransportTable.update({ TransportTable.trans_id eq id }) { t ->
                f["name"]?.let { t[name] = it }
                f["type"]?.let { t[type] = it }
                f["status"]?.let { t[status] = it }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val t = P.parse<Transport>(raw)!!
            transaction {
                TransportTable.insert {
                    it[name] = t.name
                    it[type] = t.type
                    it[status] = t.status
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, TransportTable) }
            "Transports with ids $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}