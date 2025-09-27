package com.example.dbms.storage

import com.example.dbms.parser.*

/**
 * テーブルスキーマ（テーブル定義情報）
 */
data class TableSchema(
    val tableName: String,
    val columns: List<ColumnDefinition>
) {
    /**
     * 指定されたカラム名のインデックスを取得
     */
    fun getColumnIndex(columnName: String): Int {
        return columns.indexOfFirst { it.name.equals(columnName, ignoreCase = true) }
    }
    
    /**
     * カラムが存在するかチェック
     */
    fun hasColumn(columnName: String): Boolean {
        return getColumnIndex(columnName) != -1
    }
}

/**
 * インメモリテーブル
 */
class Table(statement: CreateTableStatement) {
    // テーブルスキーマ（構造情報）
    val schema: TableSchema = TableSchema(statement.tableName, statement.columns)
    
    // データ行を格納するリスト（Rowオブジェクトで管理）
    private val _rows: MutableList<Row> = mutableListOf()
    
    // 外部からアクセス可能なrows（読み取り専用）
    val rows: List<Row> get() = _rows.toList()

    /**
     * テーブルの基本情報を表示
     */
    override fun toString(): String {
        return "Table(name='${schema.tableName}', columns=${schema.columns.size}, rows=${_rows.size}件)"
    }

    // データ行の挿入
    fun insertRow(values: List<String>) {
        // カラム数と値の数が一致するかチェック
        if (values.size != schema.columns.size) {
            throw DatabaseException("カラム数と値の数が一致しません: 期待=${schema.columns.size}, 実際=${values.size}")
        }

        // 値の型チェックと変換
        val convertedValues = mutableListOf<Any>()
        for (i in values.indices) {
            val columnType = schema.columns[i].type
            val value = values[i]
            
            val convertedValue = when (columnType) {
                is DataType.IntType -> {
                    value.toIntOrNull()
                        ?: throw DatabaseException("カラム '${schema.columns[i].name}' はINT型ですが、値 '$value' は不正です")
                }
                is DataType.VarcharType -> {
                    if (value.length > columnType.length) {
                        throw DatabaseException("カラム '${schema.columns[i].name}' はVARCHAR(${columnType.length})型ですが、値 '$value' は長すぎます")
                    }
                    value
                }
            }
            convertedValues.add(convertedValue)
        }

        // Rowオブジェクトとして追加
        val row = Row(convertedValues)
        _rows.add(row)
    }
    
    /**
     * データを検索
     * @param selectedColumns 選択するカラム名のリスト（"*"なら全カラム）
     * @return 検索結果のRowリスト
     */
    fun select(selectedColumns: List<String>): List<Row> {
        return _rows // 現在はすべての行を返す（簡易実装）
    }
    
    /**
     * テーブルの行数を取得
     */
    fun getRowCount(): Int = _rows.size
}

/**
 * データ行を表現するクラス
 */
data class Row(val values: List<Any>)