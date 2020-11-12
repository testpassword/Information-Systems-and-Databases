package com.testpassword.models

import com.testpassword.Generable
import com.testpassword.dropRecordsWithIds
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
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
        call.respondText {
            transaction {
                P.toJsonString(TransportTable.selectAll().map { it.toTransport() }.toList())
            }
        }
    }

    put {  }

    post {  }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropRecordsWithIds(droppedIds, TransportTable)
            }.toString()
        }
    }
}