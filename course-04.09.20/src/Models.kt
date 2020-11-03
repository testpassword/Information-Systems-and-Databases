package com.testpassword

import io.ktor.util.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.`java-time`.year
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.json.JSONObject
import org.postgresql.util.PGobject
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.random.Random

/*
https://stackoverflow.com/questions/45723803/how-to-use-postgresql-enum-type-via-kotlin-exposed-orm
https://blog.jdriven.com/2019/07/kotlin-exposed-a-lightweight-sql-library/
 */

class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}

inline fun <reified T : Enum<T>> Table.postgresEnumeration(
    columnName: String,
    postgresEnumName: String
) = customEnumeration(columnName, postgresEnumName,
    { value -> enumValueOf<T>(value as String) }, { PGEnum(postgresEnumName, it) })

interface Generable { fun generateAndInsert(n: Int = 0) = Unit }

object Base: Table("base"), Generable {

    val baseId = integer("base_id").autoIncrement().primaryKey()
    val location = text("location")
    val status = text("status")

    override fun generateAndInsert(n: Int) {
        val statuses = setOf("working", "closed", "destroyed", "abandoned", "unknown", "captured", "for_sale")
        (1..n).forEach {
            Base.insert {
                it[location] = F.address().city()!!
                it[status] = statuses.random()
            }
        }
    }
}

object MRE: Table("mre"), Generable {

    val mreId = integer("mre_id").autoIncrement().primaryKey()
    val breakfast = text("breakfast").nullable()
    val lunch = text("lunch").nullable()
    val dinner = text("dinner").nullable()
    val foodAdditives = text("food_additives")
    val kkal = integer("kkal").nullable().check { it greaterEq 1000 }
    val proteins = integer("proteins").nullable().check { it greater 0 }
    val fats = integer("fats").nullable().check { it greater 0 }
    val carbohydrate = integer("carbohydrate").nullable().check { it greater 0 }

    override fun generateAndInsert(n: Int) {
        (1..n).forEach {
            MRE.insert {
                it[breakfast] = F.food().dish()
                it[lunch] = F.food().dish()
                it[dinner] = F.food().dish()
                it[foodAdditives] = generateSequence { F.food().ingredient() }.take(3).joinToString(separator = "_")
                it[kkal] = F.number().numberBetween(1000, 4000)
                it[proteins] = F.number().numberBetween(1, 400)
                it[fats] = F.number().numberBetween(1, 400)
                it[carbohydrate] = F.number().numberBetween(1, 400)
            }
        }
    }
}

object Equipment: Table("equipment"), Generable {

    val equipId = integer("equip_id").autoIncrement().primaryKey()
    val camouflage = text("camouflage")
    val communication = text("communication")
    val intelligence = text("intelligence")
    val medical = text("medical")
    val mreId = reference("mre_id", MRE.mreId)
    val extra = text("extra")

    override fun generateAndInsert(n: Int) {
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
}

enum class FORCES { GF, NAVY, AF }

object Position: Table("Position"), Generable {

    val posId = integer("pos_id").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val salary = decimal("salary", 2, 11).nullable().check { it greater 12130.0 }
    val rank = text("rank")
    val equipId = reference("equip_id", Equipment.equipId)
    val forces = postgresEnumeration<FORCES>("forces", "force")

    override fun generateAndInsert(n: Int) {
        val ranks = setOf("student_officer", "private_second_class", "private_first_class", "junior_sergeant",
            "sergeant", "senior_sergeant", "petty_officer", "ensign", "senior_ensign", "junior_lieutenant", "lieutenant",
            "senior_lieutenant", "captain", "major", "lieutenant_colonel", "colonel") //, "major_general", "lieutenant_general",
        //"colonel_general", "army_general", "marshal") исключим высшие звания из генерации
        val armyPositions = setOf("medic", "miner", "scout", "security", "driver", "torpedo_operator", "pilot",
            "mechanic", "engineer", "navigator", "orderly", "duty", "coach", "artilleryman", "gunner", "sniper", "spy")
        val equipIds = Equipment.selectAll().limit(n).map { it[Equipment.equipId] }
        (1..n).forEach {
            Position.insert {
                val empPos = if (Random.nextDouble(1.0, 100.0) >= 65) armyPositions.random() else F.job().position()
                it[name] = empPos
                it[salary] = F.number().randomDouble(2, 12130, 1000000).toBigDecimal()
                if (empPos in armyPositions) it[rank] = ranks.random()
                it[equipId] = equipIds.random()
                it[forces] = FORCES.values().random()
            }
        }
    }
}

object Employee: Table("employee"), Generable {

    val empId = integer("emp_id").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val surname = text("surname").nullable()
    val dateOfBirth = date("date_of_birth").nullable().check { it less (CurrentDateTime().year() - 18) }
    val education = text("education")
    val hiringDate = date("hiring_date").defaultExpression(CurrentDateTime().date())
    val posId = reference("pos_id", Position.posId)
    val isMarried = bool("is_married").nullable()
    val baseId = reference("base_id", Base.baseId)

    @InternalAPI override fun generateAndInsert(n: Int) {
        val posIds = Position.selectAll().limit(n).map { it[Position.posId] }
        val baseIds = Base.selectAll().map { it[Base.baseId] }
        (1..n).forEach {
            val newbieId = Employee.insert {
                it[name] = F.name().firstName()
                it[surname] = F.name().lastName()
                it[dateOfBirth] = F.date().birthday(18, 70).toLocalDateTime().toLocalDate()
                it[education] = F.educator().university()
                it[hiringDate] = F.date().between(
                    // здесь и далее - условной день основания компании.
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
                    .joinToString(separator = "_")
                it[blood] = F.name().bloodGroup()
                it[gender] = Random.nextBoolean()
            }
        }
    }
}

object MedicalCard: Table("medical_card"), Generable {

    val medId = integer("med_id").autoIncrement().primaryKey()
    val empId = reference("emp_id", Employee.empId)
    val height_cm = byte("height_cm").nullable()
    val weight_kg = byte("weight_kg").nullable()
    val diseases = text("diseases")
    val blood = text("blood").nullable()
    val gender = bool("gender").nullable()
}

object Weapon: Table("weapon"), Generable {

    val weaponId = integer("weapon_id").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val type = text("type").nullable()
    val caliber = float("caliber").check { it greater 0.0 }
    val rateOfFire = integer("rate_of_fire").check { it greater 0 }
    val sightingRange_m = integer("sighting_range_m").check { it greater 0 }

    override fun generateAndInsert(n: Int) {
        val rawData = File("resources/static/guns.json").readLines().joinToString(separator = "")
        val jsonBody = JSONObject(rawData).getJSONArray("weapons")
        jsonBody.forEachIndexed { i, _ ->
            val w = jsonBody.getJSONObject(i)
            Weapon.insert {
                it[name] = w.getString("name")
                it[type] = w.getString("type")
                it[caliber] = w.getFloat("caliber")
                it[rateOfFire] = w.getInt("rate_of_fire")
                it[sightingRange_m] = w.getInt("sighting_range_m")
            }
        }
    }
}

object Campaign: Table("campaign"), Generable {

    val campId = integer("camp_id").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val customer = text("customer").nullable()
    val earning = decimal("earning", 2, 11).nullable().check { it greaterEq 0.0 }
    val spending = decimal("spending", 2, 11).nullable().check { it greaterEq 0.0 }
    val executionStatus = text("execution_status")

    override fun generateAndInsert(n: Int) {
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
}

object Mission: Table("mission"), Generable {

    val missId = integer("miss_id").autoIncrement().primaryKey()
    val campId = reference("camp_id", Campaign.campId)
    val startDateAndTime = datetime("start_date_and_time")
    val endDateAndTime = datetime("end_date_and_time")
    val legalStatus = bool("legal_status")
    val departureLocation = text("departure_location")
    val arrivalLocation = text("arrival_location")
    val enemies = text("enemies")

    @InternalAPI override fun generateAndInsert(n: Int) {
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
}

object Transport: Table("transport"), Generable {

    val transId = integer("trans_id").autoIncrement().primaryKey()
    val name = text("name").nullable()
    val type = text("type").nullable()
    val status = text("status")

    override fun generateAndInsert(n: Int) {
        val statuses = setOf("available", "under_repair", "destroyed", "broken")
        val rawData = File("resources/static/transports.json").readLines().joinToString(separator = "")
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
}

object WeaponsInEquipment: Table("equip_weapon"), Generable {

    val equipId = reference("equip_id", Equipment.equipId).nullable()
    val weaponId = reference("weapon_id", Weapon.weaponId).nullable()

    override fun generateAndInsert(n: Int) {
        val weaponIds = Weapon.selectAll().map { it[Weapon.weaponId] }
        Equipment.selectAll().map { it[Equipment.equipId] }.forEach { e ->
            WeaponsInEquipment.insert {
                it[equipId] = e
                it[weaponId] = weaponIds.random()
            }
        }
    }
}

object TransportOnMissions: Table("missions_transport"), Generable {

    val missId = reference("miss_id", Mission.missId).nullable()
    val transId = reference("trans_id", Transport.transId).nullable()

    override fun generateAndInsert(n: Int) {
        val transIds = Transport.selectAll().map { it[Transport.transId] }
        Mission.selectAll().map { it[Mission.missId] }.forEach { m ->
            if (Random.nextBoolean())
                TransportOnMissions.insert {
                    it[missId] = m
                    it[transId] = transIds.random()
                }
        }
    }
}

object Inspection: Table("inspection"), Generable {

    val empId = reference("emp_id", Employee.empId)
    val transId = reference("trans_id", Transport.transId)
    val serviceDate = date("service_date").defaultExpression(CurrentDateTime().date()).nullable()

    @InternalAPI override fun generateAndInsert(n: Int) {
        val transIds = Transport.selectAll().map { it[Transport.transId] }
        Employee.leftJoin(Position)
            .slice(Employee.empId, Position.name)
            .select { Position.name inList listOf("mechanic", "engineer") }
            .map { it[Employee.empId] }
            .forEach { e ->
                if (Random.nextBoolean())
                    Inspection.insert {
                        it[empId] = e
                        it[transId] = transIds.random()
                        it[serviceDate] = F.date().between(
                            Date.from(LocalDate.of(2014, 3, 18).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            Date()
                        ).toLocalDateTime().toLocalDate()
                    }
            }
    }
}

object EmployeeOnMission: Table("missions_emp"), Generable {

    val missId = reference("miss_id", Mission.missId).nullable()
    val empId = reference("emp_id", Employee.empId).nullable()

    override fun generateAndInsert(n: Int) {
        val missIds = Mission.selectAll().map { it[Mission.missId] }
        Employee.leftJoin(Position).select { Position.rank neq "" }.map { it[Employee.empId] }.forEach { e ->
            EmployeeOnMission.insert {
                it[missId] = missIds.random()
                it[empId] = e
            }
        }
    }
}