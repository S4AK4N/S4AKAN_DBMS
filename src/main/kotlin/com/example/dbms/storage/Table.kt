package com.example.dbms.storage

import com.example.dbms.parser.*

/**
 * インメモリテーブル
 * スキーマとデータ行を管理
 */

class Table(statement: CreateTableStatement){
    // テーブル名
    val name: String = statement.tableName

    // カラム定義
    val columns: List<ColumnDefinition> = statement.columns

    // データ行を格納するリスト（Rowオブジェクトで管理）
    private val rows: MutableList<Row> = mutableListOf()

    /**
     * テーブルの基本情報を表示
     */
    override fun toString(): String {
        return "Table(name='$name', columns=$columns, rows=${rows.size}件)"
    }

    // データ行の挿入
    fun insertRow(values: List<String>) {
        // カラム数と値の数が一致するかチェック
        if (values.size != columns.size) {
            throw DatabaseException("カラム数と値の数が一致しません: 期待=${columns.size}, 実際=${values.size}")
        }

        // 値の型チェックと変換
        val convertedValues = mutableListOf<Any>()
        for (i in values.indices) {
            val columnType = columns[i].type
            val value = values[i]
            
            val convertedValue = when (columnType) {
                is DataType.IntType -> {
                    value.toIntOrNull()
                        ?: throw DatabaseException("カラム '${columns[i].name}' はINT型ですが、値 '$value' は不正です")
                }
                is DataType.VarcharType -> {
                    if (value.length > columnType.length) {
                        throw DatabaseException("カラム '${columns[i].name}' はVARCHAR(${columnType.length})型ですが、値 '$value' は長すぎます")
                    }
                    value
                }
            }
            convertedValues.add(convertedValue)
        }

        // Rowオブジェクトとして追加
        val row = Row(convertedValues)
        rows.add(row)
    }
}

/**
 * データ行を表現するクラス
 */
data class Row(val values: List<Any>)