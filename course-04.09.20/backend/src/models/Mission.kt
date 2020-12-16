package com.testpassword.models

import com.testpassword.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.random.Random

object MissionTable: Table("mission"), Generable {

    val miss_id = integer("miss_id").autoIncrement().primaryKey()
    val camp_id = reference("camp_id", CampaignTable.camp_id, onDelete = ReferenceOption.CASCADE)
    val start_date_and_time = datetime("start_date_and_time").nullable()
    val end_date_and_time = datetime("end_date_and_time").nullable()
    val legal_status = bool("legal_status")
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

fun ResultRow.toMission() = Mission(
    this[MissionTable.miss_id],
    this[MissionTable.camp_id],
    this[MissionTable.start_date_and_time],
    this[MissionTable.end_date_and_time],
    this[MissionTable.legal_status],
    this[MissionTable.departure_location],
    this[MissionTable.arrival_location],
    this[MissionTable.enemies])

data class Mission(val missId: Int?,
                   val campId: Int,
                   val startDateAndTime: LocalDateTime?,
                   val endDateAndTime: LocalDateTime?,
                   val legalStatus: Boolean,
                   val departureLocation: String?,
                   val arrivalLocation: String?,
                   val enemies: String?)

fun Route.mission() {

    get {
        val (t, s) = try {
            call.parameters["ids"]!!.let {
                transaction {
                    PARSER.toJson(getRecordsWithIds(it, MissionTable).map { it.toMission() }) to HttpStatusCode.OK
                }
            }
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    put {
        val (t, s) = try {
            val raw = call.receiveText()
            val (id, f) = explodeJsonForModel("missId", raw)
            transaction {
                MissionTable.update({ MissionTable.miss_id eq id }) { m ->
                    f["camp_id"]?.let { m[camp_id] = it as Int }
                    f["start_date_and_time"]?.let { m[start_date_and_time] = LocalDateTime.parse(it as String) }
                    f["end_date_and_time"]?.let { m[end_date_and_time] = LocalDateTime.parse(it as String) }
                    f["legal_status"]?.let { m[legal_status] = it as Boolean }
                    f["departure_location"]?.let { m[departure_location] = it as String }
                    f["arrival_location"]?.let { m[arrival_location] = it as String }
                    f["enemies"]?.let { m[enemies] = it as String }
                }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val m = PARSER.fromJson(raw, Mission::class.java)
            transaction {
                MissionTable.insert {
                    it[camp_id] = m.campId
                    it[start_date_and_time] = m.startDateAndTime
                    it[end_date_and_time] = m.endDateAndTime
                    it[legal_status] = m.legalStatus
                    it[departure_location] = m.departureLocation
                    it[arrival_location] = m.arrivalLocation
                    it[enemies] = m.enemies
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, MissionTable) }
            "Missions with $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}