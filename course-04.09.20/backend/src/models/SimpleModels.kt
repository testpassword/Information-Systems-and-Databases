package com.testpassword.models

import com.github.javafaker.Faker
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.`java-time`.date
import org.postgresql.util.PGobject
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.random.Random

/*
https://stackoverflow.com/questions/45723803/how-to-use-postgresql-enum-type-via-kotlin-exposed-orm
https://blog.jdriven.com/2019/07/kotlin-exposed-a-lightweight-sql-library/
 */

val F = Faker() // random data generator

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

enum class FORCES { GF, NAVY, AF }

object WeaponsInEquipment: Table("equip_weapon"), Generable {

    val equip_id = reference("equip_id", EquipmentTable.equip_id)
    val weapon_id = reference("weapon_id", WeaponTable.weapon_id)

    override fun generateAndInsert(n: Int) {
        val weaponIds = WeaponTable.selectAll().map { it[WeaponTable.weapon_id] }
        EquipmentTable.selectAll().map { it[EquipmentTable.equip_id] }.forEach { e ->
            WeaponsInEquipment.insert {
                it[equip_id] = e
                it[weapon_id] = weaponIds.random()
            }
        }
    }
}

object TransportOnMissions: Table("missions_transport"), Generable {

    val miss_id = reference("miss_id", MissionTable.miss_id)
    val trans_id = reference("trans_id", TransportTable.trans_id)

    override fun generateAndInsert(n: Int) {
        val transIds = TransportTable.select { TransportTable.status eq "available" }.map { it[TransportTable.trans_id] }
        MissionTable.selectAll().map { it[MissionTable.miss_id] }.forEach { m ->
            if (Random.nextBoolean())
                TransportOnMissions.insert {
                    it[miss_id] = m
                    it[trans_id] = transIds.random()
                }
        }
    }
}

object Inspection: Table("inspection"), Generable {

    val emp_id = reference("emp_id", EmployeeTable.emp_id)
    val trans_id = reference("trans_id", TransportTable.trans_id)
    val service_date = date("service_date").defaultExpression(CurrentDateTime().date())

    @InternalAPI override fun generateAndInsert(n: Int) {
        val transIds = TransportTable.selectAll().map { it[TransportTable.trans_id] }
        EmployeeTable.leftJoin(PositionTable)
            .slice(EmployeeTable.emp_id, PositionTable.name)
            .select { PositionTable.name inList listOf("mechanic", "engineer") }
            .map { it[EmployeeTable.emp_id] }
            .forEach { e ->
                if (Random.nextBoolean())
                    Inspection.insert {
                        it[emp_id] = e
                        it[trans_id] = transIds.random()
                        it[service_date] = F.date().between(
                            Date.from(LocalDate.of(2014, 3, 18).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            Date()
                        ).toLocalDateTime().toLocalDate()
                    }
            }
    }
}

object EmployeeOnMission: Table("missions_emp"), Generable {

    val miss_id = reference("miss_id", MissionTable.miss_id)
    val emp_id = reference("emp_id", EmployeeTable.emp_id)

    override fun generateAndInsert(n: Int) {
        val missIds = MissionTable.selectAll().map { it[MissionTable.miss_id] }
        EmployeeTable
            .leftJoin(PositionTable)
            .select { PositionTable.rank neq null }.map { it[EmployeeTable.emp_id] }
            .forEach { e ->
                EmployeeOnMission.insert {
                    it[miss_id] = missIds.random()
                    it[emp_id] = e
                }
            }
    }
}



/*
https://touk.pl/blog/2019/02/12/how-we-use-kotlin-with-exposed-at-touk/
https://ryanharrison.co.uk/2018/04/14/kotlin-ktor-exposed-starter.html
https://hashrocket.com/blog/posts/faster-json-generation-with-postgresql
https://caelis.medium.com/ktor-send-and-receive-json-6c41c64410af
*/