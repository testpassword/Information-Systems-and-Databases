package com.testpassword

import com.github.javafaker.Faker
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

val F = Faker()

class ReferenceEntityError(e: Table): Error() {
    override val message = "Reference entity table ${e.tableName} does not contains such records"
}

fun Base.generateAndInsert(n: Int) {
    val statuses = setOf("working", "closed", "destroyed", "abandoned", "unknown", "captured", "for_sale")
    (1..n).forEach {
        Base.insert {
            it[location] = F.address().city()!!
            it[status] = statuses.random()
        }
    }
}

fun MRE.generateAndInsert(n: Int) {
    (1..n).forEach {
        MRE.insert {
            it[breakfast] = F.food().dish()
            it[lunch] = F.food().dish()
            it[dinner] = F.food().dish()
            it[foodAdditives] = generateSequence { F.food().ingredient() }.take(5).joinToString(separator = " ")
            it[kkal] = F.number().numberBetween(1000, 4000)
            it[proteins] = F.number().numberBetween(1, 400)
            it[fats] = F.number().numberBetween(1, 400)
            it[carbohydrate] = F.number().numberBetween(1, 400)
        }
    }
}

fun Equipment.generateAndInsert(n: Int) {
    val camouflages = setOf("black", "khaki", "olive", "fleckerteppich", "strichtarn", "cce", "vegetata",
        "flora", "forest", "pixel", "woodland", "accupat", "desert", "city")
    val communications = setOf("radio_set", "map", "mobile_satellite", "signal_flares")
    val intelligences = setOf("binoculars", "drone", "radar", "thermal_visor")
    val medicals = setOf("bandage", "harness", "antibiotics", "alcohol", "scissors", "tweezers", "antiseptic",
        "ammonia", "nitroglycerine", "adrenalin")
    /*val mreIds = MRE.slice(MRE.mreId).selectAll().limit(10)
    if (mreIds.size < n) throw ReferenceEntityError(MRE)
    else mreIds.forEach { mreId ->
        Equipment.insert {
            it[camouflage] = camouflages.random()
            it[communication] = communications.random()
            it[intelligence] = intelligences.random()
            it[medical] = medicals.random()
            it[mreId] = mreId
        }
    }*/
}