package com.testpassword.models

import com.testpassword.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object MRETable: Table("mre"), Generable {

    val mre_id = integer("mre_id").autoIncrement().primaryKey()
    val breakfast = text("breakfast")
    val lunch = text("lunch")
    val dinner = text("dinner")
    val food_additives = text("food_additives").nullable()
    val kkal = integer("kkal").check { it greaterEq 3000 }
    val proteins = integer("proteins").check { it greater 0 }
    val fats = integer("fats").check { it greater 0 }
    val carbohydrate = integer("carbohydrate").check { it greater 0 }

    override fun generateAndInsert(n: Int) {
        (1..n).forEach {
            MRETable.insert {
                it[breakfast] = F.food().dish()
                it[lunch] = F.food().dish()
                it[dinner] = F.food().dish()
                it[food_additives] = generateSequence { F.food().ingredient() }.take(3).joinToString(separator = "_")
                it[kkal] = F.number().numberBetween(1000, 4000)
                it[proteins] = F.number().numberBetween(1, 400)
                it[fats] = F.number().numberBetween(1, 400)
                it[carbohydrate] = F.number().numberBetween(1, 400)
            }
        }
    }
}

fun ResultRow.toMRE() = MRE(this[MRETable.mre_id], this[MRETable.breakfast], this[MRETable.lunch],
    this[MRETable.dinner], this[MRETable.food_additives], this[MRETable.kkal], this[MRETable.proteins],
    this[MRETable.fats], this[MRETable.carbohydrate])

data class MRE(val mreId: Int?, val breakfast: String, val lunch: String, val dinner: String, val foodAdditives: String?,
               val kkal: Int, val proteins: Int, val fats: Int, val carbohydrate: Int)

fun Route.mre() {

    get {
        val (t, s) = try {
            call.parameters["ids"]!!.let {
                transaction {
                    P.toJsonString(getRecordsWithIds(it, MRETable).map { it.toMRE() }) to HttpStatusCode.OK
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
                MRETable.update({ MRETable.mre_id eq id }) { m ->
                    f["breakfast"]?.let { m[breakfast] = it }
                    f["lunch"]?.let { m[lunch] = it }
                    f["dinner"]?.let { m[dinner] = it }
                    f["foodAdditives"]?.let { m[food_additives] = it }
                    f["kkal"]?.let { m[kkal] = it.toInt() }
                    f["proteins"]?.let { m[proteins] = it.toInt() }
                    f["fats"]?.let { m[fats] = it.toInt() }
                    f["carbohydrate"]?.let { m[carbohydrate] = it.toInt() }
                }
            }
            "$raw updated)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    post {
        val (t, s) = try {
            val raw = call.receiveText()
            val m = P.parse<MRE>(raw)!!
            transaction {
                MRETable.insert {
                    it[breakfast] = m.breakfast
                    it[lunch] = m.lunch
                    it[dinner] = m.dinner
                    it[food_additives] = m.foodAdditives
                    it[kkal] = m.kkal
                    it[proteins] = m.proteins
                    it[fats] = m.fats
                    it[carbohydrate] = m.carbohydrate
                }
            }
            "$raw added)" to HttpStatusCode.Created
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }

    delete {
        val (t, s) = try {
            val droppedIds = call.receiveText()
            transaction { dropRecordsWithIds(droppedIds, MRETable) }
            "MREs with $droppedIds deleted)" to HttpStatusCode.OK
        } catch (e: Exception) { e.toString() to HttpStatusCode.BadRequest }
        call.respondText(text = t, status = s)
    }
}
