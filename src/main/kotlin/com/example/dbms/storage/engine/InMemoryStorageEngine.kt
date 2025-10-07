package com.example.dbms.storage.engine

import com.example.dbms.storage.Row

class InMemoryStorageEngine : StorageEngine {
    private val rows = mutableListOf<Row>()

    override fun insert(row: Row) {
        rows.add(row)
    }

    override fun scanRows(filter: RowFilter): List<Row> {
        if (filter === RowFilter.All) {
            return rows.toList()
        }
        return rows.filter { row -> filter.matches(row) }
    }

    override fun update(filter: RowFilter, updater: RowUpdater): Int {
        var updated = 0
        for (index in rows.indices) {
            val current = rows[index]
            if (filter === RowFilter.All || filter.matches(current)) {
                rows[index] = updater.apply(current)
                updated++
            }
        }
        return updated
    }

    override fun delete(filter: RowFilter): Int {
        if (filter === RowFilter.All) {
            val count = rows.size
            rows.clear()
            return count
        }

        var removed = 0
        val iterator = rows.iterator()
        while (iterator.hasNext()) {
            val row = iterator.next()
            if (filter.matches(row)) {
                iterator.remove()
                removed++
            }
        }
        return removed
    }

    override fun rowCount(): Int = rows.size
}
