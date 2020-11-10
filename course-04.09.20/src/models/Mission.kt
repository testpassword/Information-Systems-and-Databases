package com.testpassword.models

import com.testpassword.F
import com.testpassword.Generable
import com.testpassword.dropEntitesWithIds
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.random.Random

object MissionTable: Table("mission"), Generable {

    val miss_id = integer("miss_id").autoIncrement().primaryKey()
    val camp_id = reference("camp_id", CampaignTable.camp_id)
    val start_date_and_time = datetime("start_date_and_time").nullable()
    val end_date_and_time = datetime("end_date_and_time").nullable()
    val legal_status = bool("legal_status").nullable()
    val departure_location = text("departure_location").nullable()
    val arrival_location = text("arrival_location").nullable()
    val enemies = text("enemies").nullable()

    @InternalAPI
    override fun generateAndInsert(n: Int) {
        val campIds = CampaignTable.selectAll().map { it[CampaignTable.camp_id] }
        (1..n).forEach {
            val st = F.date().between(
                Date.from(LocalDate.of(2014, 3, 18).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date()
            )
            val et = F.date().between(st, Date())
            MissionTable.insert {
                it[camp_id] = campIds.random()
                it[start_date_and_time] = st.toLocalDateTime()
                it[end_date_and_time] = et.toLocalDateTime()
                it[legal_status] = Random.nextBoolean()
                it[departure_location] = "${F.address().latitude()} ${F.address().longitude()}"
                it[arrival_location] = "${F.address().latitude()} ${F.address().longitude()}"
                it[enemies] = arrayOf(F.nation().nationality(), F.name().fullName()).random()
            }
        }
    }
}

fun ResultRow.toMission() = Mission(this[MissionTable.miss_id],
    CampaignTable.select { CampaignTable.camp_id eq this@toMission[MissionTable.camp_id] }.map { it.toCampaign() }.first(),
    this[MissionTable.start_date_and_time], this[MissionTable.end_date_and_time], this[MissionTable.legal_status],
    this[MissionTable.departure_location], this[MissionTable.arrival_location], this[MissionTable.enemies])

data class Mission(val missId: Int?, val campaign: Campaign, val startDateAndTime: LocalDateTime?,
                   val endDateAndTime: LocalDateTime?, val legalStatus: Boolean?, val departureLocation: String?,
                   val arrivalLocation: String?, val enemies: String?)

fun Route.mission() {

    get {
        call.respondText {
            transaction {
                P.toJsonString(MissionTable.selectAll().map { it.toMission() }.toList())
            }
        }
    }

    put {  }

    post {  }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropEntitesWithIds(droppedIds, MissionTable)
            }.toString()
        }
    }
}