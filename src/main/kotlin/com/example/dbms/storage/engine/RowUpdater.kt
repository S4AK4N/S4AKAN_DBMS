package com.example.dbms.storage.engine

import com.example.dbms.storage.Row

/** 行の更新処理を表現するインターフェース。 */
interface RowUpdater {
    fun apply(row: Row): Row

    object Identity : RowUpdater {
        override fun apply(row: Row): Row = row
    }
}
