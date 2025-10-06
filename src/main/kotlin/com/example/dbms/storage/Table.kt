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
     * データを検索（WHERE句なし）
     * @param selectedColumns 選択するカラム名のリスト（"*"なら全カラム）
     * @return 検索結果のRowリスト
     */
    fun select(@Suppress("UNUSED_PARAMETER") selectedColumns: List<String>): List<Row> {
        return _rows
    }

    /**
     * データを検索（WHERE句あり）
     * @param selectedColumns 選択するカラム名のリスト（"*"なら全カラム）
     * @param whereClause WHERE句による条件フィルタ
     * @return 検索結果のRowリスト
     */
    fun select(@Suppress("UNUSED_PARAMETER") selectedColumns: List<String>, whereClause: WhereClause): List<Row> {
        return _rows.filter { row -> evaluateWhereClause(row, whereClause) }
    }

    /**
     * WHERE句の条件を評価
     * @param row 評価対象の行
     * @param whereClause WHERE句
     * @return 条件に一致するかどうか
     */
    private fun evaluateWhereClause(row: Row, whereClause: WhereClause): Boolean {
        return when (val condition = whereClause.condition) {
            is Condition.ComparisonCondition -> {
                val columnIndex = schema.getColumnIndex(condition.left)
                if (columnIndex == -1) {
                    throw DatabaseException("カラム '${condition.left}' が見つかりません")
                }
                
                val columnValue = row.values[columnIndex]
                val targetValue = condition.right
                
                when (condition.operator) {
                    ComparisonOperator.EQUALS -> compareValues(columnValue, targetValue) == 0
                    ComparisonOperator.NOT_EQUALS -> compareValues(columnValue, targetValue) != 0
                    ComparisonOperator.GREATER_THAN -> compareValues(columnValue, targetValue) > 0
                    ComparisonOperator.LESS_THAN -> compareValues(columnValue, targetValue) < 0
                    ComparisonOperator.GREATER_THAN_OR_EQUAL -> compareValues(columnValue, targetValue) >= 0
                    ComparisonOperator.LESS_THAN_OR_EQUAL -> compareValues(columnValue, targetValue) <= 0
                }
            }
        }
    }

    /**
     * 値を比較する（型を考慮）
     * @param value1 カラムの値
     * @param value2 比較対象の値（文字列）
     * @return 比較結果 (-1: value1 < value2, 0: 等しい, 1: value1 > value2)
     */
    private fun compareValues(value1: Any, value2: String): Int {
        return when (value1) {
            is Int -> {
                val num2 = value2.toIntOrNull()
                    ?: throw DatabaseException("Cannot compare as a number: '$value2'")
                value1.compareTo(num2)
            }
            is String -> {
                value1.compareTo(value2)
            }
            else -> throw DatabaseException("比較できない型です: ${value1::class}")
        }
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