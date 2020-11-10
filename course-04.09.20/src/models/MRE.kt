package com.testpassword.models

import com.testpassword.F
import com.testpassword.Generable
import com.testpassword.dropEntitesWithIds
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object MRETable: Table("mre"), Generable {

    val mre_id = integer("mre_id").autoIncrement().primaryKey()
    val breakfast = text("breakfast")
    val lunch = text("lunch")
    val dinner = text("dinner")
    val food_additives = text("food_additives").nullable()
    val kkal = integer("kkal").check { it greaterEq 1000 }
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
        call.respondText {
            transaction {
                P.toJsonString(MRETable.selectAll().map { it.toMRE() }.toList())
            }
        }
    }

    put {  }

    post {  }

    delete {
        val droppedIds = call.receiveText()
        call.respondText {
            transaction {
                dropEntitesWithIds(droppedIds, MRETable)
            }.toString()
        }
    }
}
