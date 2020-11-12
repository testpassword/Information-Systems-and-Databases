package com.testpassword

import org.jetbrains.exposed.sql.*
import org.json.JSONObject

class ReferenceEntityError(e: Table): Error() {
    override val message = "Reference entity table ${e.tableName} does not contains such records"
}

object RecordsGenerator {

    fun fillDb(sizes: Map<Generable, Int>) = sizes.forEach { it.key.generateAndInsert(it.value) }
}

fun dropRecordsWithIds(s: String, entityTable: Table) {
    JSONObject(s).getString("droppedIds").split(" ").map {
        entityTable.deleteWhere { (entityTable.primaryKey?.columns?.get(0) as Column<Int>) eq it.toInt() }
    }
}

fun getRecordsWithIds(s: String, entityTable: Table): Query {
    val raw = JSONObject(s).getString("selectedIds")
    return if (raw.isNullOrBlank()) entityTable.selectAll() else {
        val ids = raw.split(" ").map { it.toInt() }
        entityTable.select {
            entityTable.primaryKey?.columns?.get(0) as Column<Int> inList ids
        }
    }
}

fun explodeJsonForModel(modelIdField: String, jsonStr: String): Pair<Int, Map<String, String>> {
    val s = JSONObject(jsonStr)
    val fields = mutableMapOf<String, String>()
    s.keys().forEach { fields[it] = s.getString(it) }
    return s.getInt(modelIdField) to fields
}