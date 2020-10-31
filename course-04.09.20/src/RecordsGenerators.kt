package com.testpassword

import com.github.javafaker.Faker
import io.ktor.util.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.random.Random

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

fun MRE.generateAndInsert(n: Int) =
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

fun Equipment.generateAndInsert(n: Int) {
    val camouflages = setOf("black", "khaki", "olive", "fleckerteppich", "strichtarn", "cce", "vegetata",
        "flora", "forest", "pixel", "woodland", "accupat", "desert", "city")
    val communications = setOf("radio_set", "map", "mobile_satellite", "signal_flares")
    val intelligences = setOf("binoculars", "drone", "radar", "thermal_visor")
    val medicals = setOf("bandage", "harness", "antibiotics", "alcohol", "scissors", "tweezers", "antiseptic",
        "ammonia", "nitroglycerine", "adrenalin")
    val mreIds = MRE.selectAll().limit(n).map { it[MRE.mreId] }
    (1..n).forEach {
        Equipment.insert {
            it[camouflage] = camouflages.random()
            it[communication] = communications.random()
            it[intelligence] = intelligences.random()
            it[medical] = medicals.random()
            it[mreId] = mreIds.random()
        }
    }
}

fun Position.generateAndInsert(n: Int) {
    val ranks = setOf("student_officer", "private_second_class", "private_first_class", "junior_sergeant",
        "sergeant", "senior_sergeant", "petty_officer", "ensign", "senior_ensign", "junior_lieutenant", "lieutenant",
        "senior_lieutenant", "captain", "major", "lieutenant_colonel", "colonel", "major_general", "lieutenant_general",
        "colonel_general", "army_general", "marshal")
    val armyPositions = setOf("medic", "miner", "scout", "security", "driver", "torpedo_operator", "pilot",
        "mechanic", "engineer", "navigator", "orderly", "duty", "coach", "artilleryman", "gunner", "sniper", "spy")
    val equipIds = Equipment.selectAll().limit(n).map { it[Equipment.equipId] }
    (1..n).forEach {
        Position.insert {
            it[name] = if (Random.nextDouble(1.0, 100.0) >= 65) armyPositions.random() else F.job().position()
            it[salary] = F.number().randomDouble(2, 12130, 2000000).toBigDecimal()
            it[rank] = ranks.random()
            it[equipId] = equipIds.random()
            it[forces] = FORCES.values().random()
        }
    }
}

@InternalAPI
fun Employee.generateAndInsert(n: Int) {
    val posIds = Position.selectAll().limit(n).map { it[Position.posId] }
    val baseIds = Base.selectAll().map { it[Base.baseId] }
    (1..n).forEach {
         val newbieId = Employee.insert {
             it[name] = F.name().firstName()
             it[surname] = F.name().lastName()
             it[dateOfBirth] = F.date().birthday(18, 70).toLocalDateTime().toLocalDate()
             it[education] = F.educator().university()
             it[hiringDate] = F.date().between(
                 Date.from(LocalDate.of(2014, 3, 18).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                 Date()
             ).toLocalDateTime().toLocalDate()
             it[posId] = posIds.random()
             it[isMarried] = Random.nextBoolean()
             it[baseId] = baseIds.random()
         }[empId]
        MedicalCard.insert {
            it[empId] = newbieId
            it[height_cm] = Random.nextInt(150, 200).toByte()
            it[weight_kg] = Random.nextInt(40, 120).toByte()
            it[diseases] = generateSequence { F.medical().diseaseName() }
                .take(Random.nextInt(0, 5))
                .joinToString(separator = " ")
            it[blood] = BLOOD.values().random()
            it[gender] = Random.nextBoolean()
        }
    }
}

fun Weapon.generateAndInsert() {
    val rawData = File("resources/static/guns.json").readLines().joinToString("", "", "")
    val jsonBody = JSONObject(rawData).getJSONArray("weapons")
    jsonBody.forEachIndexed { i, _ ->
        val w = jsonBody.getJSONObject(i)
        Weapon.insert {
            it[name] = w.getString("name")
            it[type] = w.getString("type")
            it[caliber] = w.getFloat("caliber")
            it[rateOfFire] = w.getInt("rateOfFire")
            it[sightingRange_m] = w.getInt("sightingRange_m")
        }
    }
}

fun Campaign.generateAndInsert(n: Int) {
    val statuses = setOf("completed", "in the process", "failed", "canceled")
    (1..n).forEach {
        Campaign.insert {
            it[name] = F.ancient().titan()
            it[customer] = F.name().fullName()
            it[earning] = F.number().randomDouble(2, 500000, 100000000).toBigDecimal()
            it[spending] = F.number().randomDouble(2, 0, 10000000).toBigDecimal()
            it[executionStatus] = if (Random.nextInt(1, 100) >= 70) statuses.random() else statuses.first()
        }
    }
}

@InternalAPI
fun Mission.generateAndInsert(n: Int) {
    val campIds = Campaign.selectAll().map { it[Campaign.campId] }
    (1..n).forEach {
        val st = F.date().between(
            Date.from(LocalDate.of(2014, 3, 18).atStartOfDay(ZoneId.systemDefault()).toInstant()),
            Date()
        )
        val et = F.date().between(st, Date())
        Mission.insert {
            it[campId] = campIds.random()
            it[startDateAndTime] = st.toLocalDateTime()
            it[endDateAndTime] = et.toLocalDateTime()
            it[legalStatus] = Random.nextBoolean()
            it[departureLocation] = "${F.address().latitude()} ${F.address().longitude()}"
            it[arrivalLocation] = "${F.address().latitude()} ${F.address().longitude()}"
            it[enemies] = arrayOf(F.nation().nationality(), F.name().fullName()).random()
        }
    }
}

fun Transport.generateAndInsert() {
    val statuses = setOf("under_repair", "available", "destroyed")
    val rawData = File("resources/static/transport.json").readLines().joinToString("", "", "")
    val jsonBody = JSONObject(rawData).getJSONArray("transport")
    jsonBody.forEachIndexed { i, el ->
        val w = jsonBody.getJSONObject(i)
        Transport.insert {
            it[name] = w.getString("name")
            it[type] = w.getString("type")
            it[status] = statuses.random()
        }
    }
}

fun Inspection.generateAndInsert(n: Int) {
}