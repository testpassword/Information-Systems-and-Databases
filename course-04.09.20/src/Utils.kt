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
    val ids = JSONObject(s).getJSONArray("droppedIds")
    ids.forEachIndexed { i, _ ->
        entityTable.deleteWhere { (entityTable.primaryKey?.columns?.get(0) as Column<Int>) eq ids.getInt(i) }
    }
}

fun getRecordsWithIds(s: String, entityTable: Table): Query {
    val raw = JSONObject(s).getJSONArray("selectedIds")
    val ids = mutableListOf<Int>()
    raw.forEachIndexed { i, _ -> ids.add(raw.getInt(i)) }
    return if (ids.isEmpty()) entityTable.selectAll() else
        entityTable.select {
            entityTable.primaryKey?.columns?.get(0) as Column<Int> inList ids
        }
}

fun explodeJsonForModel(modelIdField: String, jsonStr: String): Pair<Int, Map<String, String>> {
    val s = JSONObject(jsonStr)
    val id = s.getInt(modelIdField)
    s.remove(modelIdField)
    val fields = mutableMapOf<String, String>()
    s.keys().forEach { fields[it] = s.getString(it) }
    return id to fields
}