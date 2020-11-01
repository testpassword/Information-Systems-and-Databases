package com.testpassword

import com.github.javafaker.Faker
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
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

object RecordsGenerator {

    fun fillDb(sizes: Map<Generable, Int>) = sizes.forEach { it.key.generateAndInsert(it.value) }
}