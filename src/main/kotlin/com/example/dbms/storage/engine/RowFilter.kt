package com.example.dbms.storage.engine

import com.example.dbms.storage.Row

/** 行に対する選択条件を表すインターフェース。 ストレージ層ではSQL構文に依存しない形で利用する。 */
interface RowFilter {
    fun matches(row: Row): Boolean

    object All : RowFilter {
        override fun matches(row: Row): Boolean = true
    }
}
