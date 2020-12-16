package com.testpassword.models

import com.testpassword.*
import io.ktor.application.*
import io.ktor.http.*
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
    val execution_status = text("execution_status").nullable()

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

fun ResultRow.toCampaign() = Campaign(
    this[CampaignTable.camp_id],
    this[CampaignTable.name],
    this[CampaignTable.customer],
    this[CampaignTable.earning].toDouble(),
    this[CampaignTable.spending].toDouble(),
    this[CampaignTable.execution_status])

data class Campaign(val campId: Int?,
                    val name: String,
                    val customer: String,
                    val earning: Double,
                    val spending: Double,
                    val executionStatus: String?)

fun Route.campaign() {

    get {
        val (t, s) = try {
            call.parameters["ids"]!!.let {
                transaction {
                    PARSER.toJson(getRecordsWithIds(it, CampaignTable).map { it.toCampaign() }) to HttpStatusCode.OK
                }
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("campId", raw)
            CampaignTable.update({ CampaignTable.camp_id eq id }) { c ->
                f["name"]?.let { c[name] = it as String }
                f["customer"]?.let { c[customer] = it as String }
                f["earning"]?.let { c[earning] = (it as Double).toBigDecimal() }
                f["spending"]?.let { c[spending] = (it as Double).toBigDecimal() }
                f["executionStatus"]?.let { c[execution_status] = it as String }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val b = PARSER.fromJson(raw, Campaign::class.java)
            transaction {
                CampaignTable.insert {
                    it[name] = b.name
                    it[customer] = b.customer
                    it[earning] = b.earning.toBigDecimal()
                    it[spending] = b.spending.toBigDecimal()
                    it[execution_status] = b.executionStatus
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, CampaignTable) }
            "Campaigns with ids $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}