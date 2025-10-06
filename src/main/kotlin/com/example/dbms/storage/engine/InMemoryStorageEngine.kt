package com.example.dbms.storage.engine

import com.example.dbms.storage.Row

class InMemoryStorageEngine : StorageEngine {
    private val rows = mutableListOf<Row>()

    override fun insert(row: Row) {
        rows.add(row)
    }

    override fun scan(predicate: ((Row) -> Boolean)?): List<Row> {
        if (predicate == null) {
            return rows.toList()
        }
        return rows.filter(predicate)
    }

    override fun update(predicate: ((Row) -> Boolean)?, transform: (Row) -> Row): Int {
        var updated = 0
        for (index in rows.indices) {
            val current = rows[index]
            if (predicate == null || predicate(current)) {
                rows[index] = transform(current)
                updated++
            }
        }
        return updated
    }

    override fun delete(predicate: ((Row) -> Boolean)?): Int {
        if (predicate == null) {
            val count = rows.size
            rows.clear()
            return count
        }

        var removed = 0
        val iterator = rows.iterator()
        while (iterator.hasNext()) {
            val row = iterator.next()
            if (predicate(row)) {
                iterator.remove()
                removed++
            }
        }
        return removed
    }

    override fun rowCount(): Int = rows.size
}
