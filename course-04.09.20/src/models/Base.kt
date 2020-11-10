package com.testpassword.models

import com.beust.klaxon.Klaxon
import com.testpassword.F
import com.testpassword.Generable
import com.testpassword.dropEntitesWithIds
import io.ktor.application.*
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
        call.respondText {
            transaction {
                P.toJsonString(BaseTable.selectAll().map { it.toBase() }.toList())
            }
        }
    }

    put {
    }

    post {
        val b = P.parse<Base>(call.receiveText())!!
        call.respondText {
            transaction {
                BaseTable.insert {
                    it[location] = b.location
                    it[status] = b.status
                }.resultedValues!!.joinToString()
            }
        }
    }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropEntitesWithIds(droppedIds, BaseTable)
            }.toString()
        }
    }
}

/*
https://touk.pl/blog/2019/02/12/how-we-use-kotlin-with-exposed-at-touk/
https://ryanharrison.co.uk/2018/04/14/kotlin-ktor-exposed-starter.html
https://hashrocket.com/blog/posts/faster-json-generation-with-postgresql
https://caelis.medium.com/ktor-send-and-receive-json-6c41c64410af
*/