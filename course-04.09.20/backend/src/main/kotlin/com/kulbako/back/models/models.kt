package com.kulbako.back.models

import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.OneToOne
import javax.persistence.Table

@Table(name = "base") data class Base(
        val baseId: Long?,
        val location: String,
        val status: String
)

@Table(name = "mre") data class MealReadyToEat(
        val mreId: Long?,
        val breakfast: String,
        val lunch: String,
        val dinner: String,
        val foodAdditives: String?,
        val kkal: Short,
        val proteins: Short,
        val fats: Short,
        val carbohydrate: Short
) {
    init {
        if (kkal < 1000 || proteins <=0 || fats <= 0 || carbohydrate <= 0) throw IllegalArgumentException()
    }
}

@Table(name = "equipment") data class Equipment(
        val equipId: Long?,
        val camouflage: String?,
        val communication: String?,
        val intelligence: String?,
        @OneToOne val medical: String?,
        val mre: MealReadyToEat,
        val extra: String?
)

enum class Force(val type: String) {
    GROUND_TROOPS("СВ"),
    NAVY("ВМФ"),
    AIR("ВКС")
}

@Table(name = "position") data class Position(
        val posId: Long?,
        val name: String,
        val salary: Double,
        val rank: String,
        val equip: Equipment?,
        val forces: Force
) {
    init {
        if (salary < 12130) throw IllegalArgumentException()
    }
}

@Table(name = "employee") data class Employee(
        val empId: Long?,
        val name: String,
        val surname: String,
        val dateOfBirth: LocalDate,
        val education: String,
        val hiringDate: LocalDate = LocalDate.now(),
        val position: Position,
        val isMarried: Boolean,
        val base: Base
) {
    init {
        if (LocalDate.now().year - dateOfBirth.year <= 18) throw IllegalArgumentException()
    }
}

enum class Blood(val type: String) {
    ZERO_PLUS("0+"),
    ZERO_MINUS("0-"),
    A_PLUS("A+"),
    A_MINUS("A-"),
    B_PLUS("B+"),
    B_MINUS("B-"),
    AB_PLUS("AB+"),
    AB_MINUS("AB-")
}

@Table(name = "medical_card") data class MedicalCard(
        val medId: Long?,
        val employee: Employee,
        val height_cm: Byte,
        val weight_kg: Byte,
        val diseases: String?,
        val blood: Blood,
        val gender: Boolean
)

@Table(name = "weapon") data class Weapon(
        val weaponId: Long?,
        val name: String,
        val type: String,
        val caliber: Double?,
        val rateOfFire: Short?,
        val barrelLength: Short?,
        val sightingRange: Short?
) {
    init {
        sequenceOf<Number?>(caliber, rateOfFire, barrelLength, sightingRange).filterNotNull().forEach {
            if (it.toDouble() <= 0) throw IllegalArgumentException()
        }
    }
}

@Table(name = "campaing") data class Campaing(
        val campId: Long?,
        val name: String,
        val customer: String,
        val earning: Double,
        val spending: Double,
        val executionStatus: String
) {
    init {
        sequenceOf(earning, spending).forEach { if (it < 0) throw IllegalArgumentException() }
    }
}

@Table(name = "mission") data class Mission(
        val missId: Long?,
        val campaing: Campaing,
        val startDateAndTime: LocalDateTime,
        val endDateAndTime: LocalDateTime,
        val legalStatus: String?,
        val departureLocation: String?,
        val arrivalLocation: String?,
        val enemies: String?
)

@Table(name = "transport") data class Transport(
        val transId: Long?,
        val name: String,
        val type: String,
        val status: String?
)

@Table(name = "inspection") data class Inspection(
        val employee: Employee,
        val transport: Transport,
        val serviceDate: LocalDate = LocalDate.now()
)