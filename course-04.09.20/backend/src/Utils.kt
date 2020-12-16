package com.testpassword

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.testpassword.models.Generable
import org.jetbrains.exposed.sql.*
import org.json.JSONObject
import java.time.LocalDate

val PARSER = GsonBuilder()
    .registerTypeAdapter(
        LocalDate::class.java,
        JsonSerializer<LocalDate> { src, _, _ -> if (src == null) null else JsonPrimitive(src.toString()) })
    .registerTypeAdapter(
        LocalDate::class.java,
        JsonDeserializer<LocalDate> { json, _, _ -> if (json == null) null else LocalDate.parse(json.asString.split("T").first()) })
    .create()

class ReferenceEntityError(e: Table): Error() {
    override val message = "Reference entity table ${e.tableName} does not contains such records"
}

fun fillDb(sizes: Map<Generable, Int>) = sizes.forEach { it.key.generateAndInsert(it.value) }

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

fun explodeJsonForModel(modelIdField: String, jsonStr: String): Pair<Int, Map<String, Any>> {
    val s = JSONObject(jsonStr)
    val id = s.getInt(modelIdField)
    s.remove(modelIdField)
    val fields = mutableMapOf<String, Any>()
    s.keys().forEach { fields[it] = s.get(it) }
    return id to fields
}