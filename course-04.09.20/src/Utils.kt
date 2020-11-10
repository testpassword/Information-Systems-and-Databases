package com.testpassword

import org.jetbrains.exposed.sql.*
import org.json.JSONObject

class ReferenceEntityError(e: Table): Error() {
    override val message = "Reference entity table ${e.tableName} does not contains such records"
}

object RecordsGenerator {

    fun fillDb(sizes: Map<Generable, Int>) = sizes.forEach { it.key.generateAndInsert(it.value) }
}

fun dropEntitesWithIds(s: String, entityTable: Table) =
    JSONObject(s)
        .getString("droppedIds")
        .split(" ")
        .map {
            entityTable.deleteWhere { (entityTable.primaryKey?.columns?.get(0) as Column<Int>) eq it.toInt() }
        }