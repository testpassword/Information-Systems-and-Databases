package com.testpassword.models

import com.testpassword.F
import com.testpassword.Generable
import com.testpassword.dropEntitesWithIds
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import kotlin.random.Random

object CampaignTable: Table("campaign"), Generable {

    val camp_id = integer("camp_id").autoIncrement().primaryKey()
    val name = text("name")
    val customer = text("customer")
    val earning = decimal("earning", 2, 11).check { it greaterEq 0.0 }
    val spending = decimal("spending", 2, 11).check { it greaterEq 0.0 }
    val execution_status = text("execution_status")

    override fun generateAndInsert(n: Int) {
        val statuses = setOf("completed", "in the process", "failed", "canceled")
        (1..n).forEach {
            CampaignTable.insert {
                it[name] = F.ancient().titan()
                it[customer] = F.name().fullName()
                it[earning] = F.number().randomDouble(2, 500000, 100000000).toBigDecimal()
                it[spending] = F.number().randomDouble(2, 0, 10000000).toBigDecimal()
                it[execution_status] = if (Random.nextInt(1, 100) >= 70) statuses.random() else statuses.first()
            }
        }
    }
}

fun ResultRow.toCampaign() = Campaign(this[CampaignTable.camp_id], this[CampaignTable.name], this[CampaignTable.customer],
    this[CampaignTable.earning], this[CampaignTable.spending]!!, this[CampaignTable.execution_status])

data class Campaign(val campId: Int?, val name: String, val customer: String, val earning: BigDecimal,
                    val spending: BigDecimal, val executionStatus: String?)

fun Route.campaign() {

    get {
        call.respondText {
            transaction {
                P.toJsonString(CampaignTable.selectAll().map { it.toCampaign() }.toList())
            }
        }
    }

    put {  }

    post {  }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropEntitesWithIds(droppedIds, CampaignTable)
            }.toString()
        }
    }
}